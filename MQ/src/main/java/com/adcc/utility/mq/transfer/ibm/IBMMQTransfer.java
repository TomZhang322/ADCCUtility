package com.adcc.utility.mq.transfer.ibm;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.ibm.IBMMQConfiguration;
import com.adcc.utility.mq.entity.MQState;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.transfer.*;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.mq.*;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IBMMQ通信类
 */
public class IBMMQTransfer implements MQTransfer, AsyncMsgListener {

    // 连接配置
    private IBMMQConfiguration configuration;

    // 连接池
    private IBMMQConnectionPool pool;

    // MQ长连接
    private MQQueueManager queueManager;

    // MQ状态
    private MQState state = MQState.DISCONNECTED;

    // 队列异步接收队列
    private List<AsyncReceiver> asyncQReceivers = Lists.newArrayListWithExpectedSize(500);

    // 会话异步接收队列
    private List<AsyncReceiver> asyncTReceivers = Lists.newArrayListWithExpectedSize(500);

    // 队列接收监听器
    private QueueMsgListener queueMsgListener;

    // 主题接收监听器
    private TopicMsgListener topicMsgListener;

    // MQ状态监听器
    private MQStateListener stateListener;

    // 线程池
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 构造函数
     */
    public IBMMQTransfer() {
    }

    /**
     * 构造函数
     * @param configuration
     */
    public IBMMQTransfer(MQConfiguration configuration) throws Exception {
        this.configuration = (IBMMQConfiguration)configuration;
    }

    /**
     * 构造函数
     * @param configuration
     * @param pool
     * @throws Exception
     */
    public IBMMQTransfer(MQConfiguration configuration, MQConnectionPool pool) throws Exception {
        this.configuration = (IBMMQConfiguration)configuration;
        this.pool = (IBMMQConnectionPool)pool;
    }

    /**
     * 取得MQ信息
     * @return
     */
    private Map<String,String> getMQInfo() {
        try {
            Map<String,String> info = Maps.newHashMapWithExpectedSize(4);
            info.put("url", configuration.getHost());
            info.put("port", String.valueOf(configuration.getPort()));
            info.put("channel", configuration.getChannel());
            info.put("qm", configuration.getQueueManager());
            return info;
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "getMQInfo() error", ex);
            return null;
        }
    }

    /**
     * 通知MQ连接状态
     * @param state
     */
    private void notifyState(MQState state) {
        this.state = state;
        if (stateListener != null) {
            stateListener.onState(state, getMQInfo());
        }
    }

    /**
     * 取得MQQueueManager
     * @return
     */
    private MQQueueManager getQueueManager() {
        if(pool != null){

            // 连接池方式获取QueueManager
            Optional<MQQueueManager> optional = pool.getConnection();
            if (optional.isPresent()) {
                MQQueueManager queueManager = optional.get();
                return queueManager;
            } else {
                return null;
            }
        }else{

            // 非连接池方式获取QueueManager
            try{
                if(state != MQState.CONNECTED){
                    queueManager = new MQQueueManager(configuration.getQueueManager(),configuration.getParams());
                }
                return queueManager;
            }catch (Exception ex){
                Log.error(IBMMQTransfer.class.getName(),"initialize QueueManager error",ex);
                return null;
            }
        }
    }

    /**
     * 返回MQQueueManager
     * @param queueManager
     * @param flag
     */
    private void returnQueueManager(MQQueueManager queueManager,boolean flag) {
        try {
            if (queueManager != null) {
                if (pool != null) {
                    pool.returnConnection(Optional.of(queueManager));
                }else{
                    if(flag){
                        if(queueManager != null){
                            queueManager.close();
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "returnQueueManager() error", ex);
        }
    }

    /**
     * 是否存在异步接收
     * @param name
     * @param receiverList
     * @return
     * @throws Exception
     */
    private boolean isExistAsyncReceiver(String name,List<AsyncReceiver> receiverList) {
        if(Strings.isNullOrEmpty(name)) {
            return true;
        }
        if (receiverList.size() > 0) {
            for (AsyncReceiver receiver : receiverList) {
                if (name.equals(receiver.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 启动异步接收处理
     */
    private void startReceivers(MQQueueManager queueManager) {
        try {
            if (asyncQReceivers.size() > 0) {
                for (AsyncReceiver receiver : asyncQReceivers) {
                    ((AsyncQueueReceiver)receiver).setQueueManager(queueManager);
                    receiver.start();
                }
            }
            if (asyncTReceivers.size() > 0) {
                for (AsyncReceiver receiver : asyncTReceivers) {
                    ((AsyncTopicReceiver)receiver).setQueueManager(queueManager);
                    receiver.start();
                }
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "startReceivers() error", ex);
        }
    }

    /**
     * 关闭异步接收处理
     */
    private void stopReceivers() {
        try {
            if (asyncQReceivers.size() > 0) {
                for (AsyncReceiver receiver : asyncQReceivers) {
                    receiver.stop();
                }
            }
            if (asyncTReceivers.size() > 0) {
                for(AsyncReceiver receiver : asyncTReceivers) {
                    receiver.stop();
                }
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "stopReceivers() error", ex);
        }
    }

    /**
     * 发送队列消息
     * @param queue
     * @param message
     * @throws Exception
     */
    public void sendQueueWithIdentity(String queue, Message message, int expiredTime) throws Exception {
        MQQueueManager queueManager = null;
        MQQueue mqQueue = null;
        MQMessage mqMessage = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQOO_OUTPUT | CMQC.MQOO_SET_IDENTITY_CONTEXT;
            mqQueue = queueManager.accessQueue(queue, options);
            if (mqQueue.isOpen()) {
                MQPutMessageOptions pmo = new MQPutMessageOptions();
                pmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING | CMQC.MQPMO_ASYNC_RESPONSE;
                mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.expiry = expiredTime;
                mqMessage.write(message.getContent(),0,message.getContent().length);
                if (message.getHead() != null && message.getHead().size() > 0) {
                    for (Map.Entry<String, Object> entry : message.getHead().entrySet()) {
                        mqMessage.setObjectProperty(entry.getKey(), entry.getValue());
                    }
                }
                mqQueue.put(mqMessage,pmo);
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "send() error", ex);
            throw ex;
        } finally {
            try {
                if(mqMessage != null){
                    mqMessage.clearMessage();
                }
                if (mqQueue != null) {
                    mqQueue.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close queue error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    /**
     * 发送队列消息
     * @param queue
     * @param message
     * @param expiredTime
     * @throws Exception
     */
    public void sendQueue(String queue, Message message, int expiredTime) throws Exception {
        MQQueueManager queueManager = null;
        MQQueue mqQueue = null;
        MQMessage mqMessage = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQOO_OUTPUT;
            mqQueue = queueManager.accessQueue(queue, options);
            if (mqQueue.isOpen()) {
                MQPutMessageOptions pmo = new MQPutMessageOptions();
                pmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING | CMQC.MQPMO_ASYNC_RESPONSE;
                mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.expiry = expiredTime;
                mqMessage.write(message.getContent(), 0, message.getContent().length);
                if (message.getHead() != null && message.getHead().size() > 0) {
                    for (Map.Entry<String, Object> entry : message.getHead().entrySet()) {
                        mqMessage.setObjectProperty(entry.getKey(), entry.getValue());
                    }
                }
                mqQueue.put(mqMessage, pmo);
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "send() error", ex);
            throw ex;
        } finally {
            try {
                if(mqMessage != null){
                    mqMessage.clearMessage();
                }
                if (mqQueue != null) {
                    mqQueue.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close queue error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    /**
     * 预览消息
     * @param queue
     * @return
     * @throws Exception
     */
    public List<Message> browsingQueue(String queue) throws Exception {
        MQQueueManager queueManager = null;
        MQQueue mqQueue = null;
        try {
            List<Message> list = Lists.newArrayListWithExpectedSize(10000);
            queueManager = getQueueManager();
            int options = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_BROWSE | CMQC.MQOO_INQUIRE;
            mqQueue = queueManager.accessQueue(queue,options);
            if (mqQueue.isOpen()) {
                int intCurrentDepth = mqQueue.getCurrentDepth();
                if(intCurrentDepth > 0){
                    MQGetMessageOptions gmo = new MQGetMessageOptions();
                    gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_BROWSE_NEXT | CMQC.MQGMO_CONVERT | CMQC.MQGMO_FAIL_IF_QUIESCING;
                    for(int i = 0;i < intCurrentDepth;i++){
                        MQMessage mqMessage = new MQMessage();
                        mqMessage.characterSet = 1208;
                        mqQueue.get(mqMessage, gmo);
                        Message message = new Message();
                        Enumeration<String> propertyNames = mqMessage.getPropertyNames("%");
                        if (propertyNames!= null) {
                            while(propertyNames.hasMoreElements()) {
                                String propertyName = propertyNames.nextElement();
                                message.getHead().put(propertyName, mqMessage.getObjectProperty(propertyName));
                            }
                        }
                        byte[] buffer = new byte[mqMessage.getMessageLength()];
                        mqMessage.readFully(buffer, 0, buffer.length);
                        message.setContent(buffer);
                        mqMessage.clearMessage();
                        list.add(message);
                    }
                }
                return list;
            } else {
                return null;
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(),"browsingQueue() error",ex);
            throw ex;
        } finally {
            try {
                if (mqQueue != null) {
                    mqQueue.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close queue error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    /**
     * 发送会话消息
     * @param topic
     * @param message
     * @param expiredTime
     * @throws Exception
     */
    public void sendTopic(String topic, Message message, int expiredTime) throws Exception {
        MQQueueManager queueManager = null;
        MQTopic mqTopic = null;
        MQMessage mqMessage = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQOO_OUTPUT | CMQC.MQOO_FAIL_IF_QUIESCING;
            mqTopic = queueManager.accessTopic(topic, topic, CMQC.MQTOPIC_OPEN_AS_PUBLICATION, options);
            if (mqTopic.isOpen()) {
                MQPutMessageOptions gmo = new MQPutMessageOptions();
                gmo.options = CMQC.MQPMO_FAIL_IF_QUIESCING;
                mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqMessage.expiry = expiredTime;
                mqMessage.write(message.getContent(), 0, message.getContent().length);
                mqTopic.put(mqMessage, gmo);
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "sendToTopic() error", ex);
            throw ex;
        } finally {
            try {
                if(mqMessage != null) {
                    mqMessage.clearMessage();
                }
                if (mqTopic != null) {
                    mqTopic.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close topic error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    /**
     * 取得队列当前深度
     * @param queue
     * @return
     * @throws Exception
     */
    public int getDepth(String queue) throws Exception{
        MQQueueManager queueManager = null;
        MQQueue mqQueue = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQOO_INQUIRE;
            mqQueue = queueManager.accessQueue(queue,options);
            return mqQueue.getCurrentDepth();
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(),"getDepth() error",ex);
            throw ex;
        } finally {
            try {
                if (mqQueue != null) {
                    mqQueue.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close queue error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    /**
     * 设置队列消息接收暂停
     */
    public void suspendQueue(String name, boolean flag){
        if(asyncQReceivers.size() > 0){
            for(AsyncReceiver ar : asyncQReceivers){
                if(ar.getName().equals(name)){
                    ((AsyncQueueReceiver)ar).suspend(flag);
                    return;
                }
            }
        }
    }

    /**
     * 设置主题消息接收暂停
     */
    public void suspendTopic(String name, boolean flag){
        if(asyncTReceivers.size() > 0){
            for(AsyncReceiver ar : asyncTReceivers){
                if(ar.getName().equals(name)){
                    ((AsyncTopicReceiver)ar).suspend(flag);
                    return;
                }
            }
        }
    }

    @Override
    public void setConfiguration(MQConfiguration configuration) throws Exception {
        this.configuration = (IBMMQConfiguration)configuration;
    }

    @Override
    public void setConnectionPool(MQConnectionPool pool) throws Exception {
        this.pool = (IBMMQConnectionPool)pool;
    }

    @Override
    public void setQueueListener(QueueMsgListener listener) {
        try {
            if (asyncQReceivers.size() > 0) {
                for(AsyncReceiver receiver : asyncQReceivers){
                    receiver.setMsgListener(this);
                }
            }
            queueMsgListener = listener;
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "setQueueListener() error", ex);
        }
    }

    @Override
    public void setQueueListener(QueueMsgListener listener, String... queue) {
        try {
            if (queue != null && queue.length > 0) {
                for (String q : queue) {
                    if (!isExistAsyncReceiver(q, asyncQReceivers)) {
                        AsyncReceiver receiver = new AsyncQueueReceiver(q);
                        receiver.setMsgListener(this);
                        asyncQReceivers.add(receiver);
                    }
                }
            }
            queueMsgListener = listener;
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "setQueueListener() error", ex);
        }
    }

    @Override
    public void setTopicListener(TopicMsgListener listener) {
        try {
            if (asyncTReceivers.size() > 0) {
                for(AsyncReceiver receiver : asyncTReceivers){
                    receiver.setMsgListener(this);
                }
            }
            topicMsgListener = listener;
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "setTopicListener() error", ex);
        }
    }

    @Override
    public void setTopicListener(TopicMsgListener listener, String... topic) {
        try {
            if (topic != null && topic.length > 0) {
                for (String t : topic) {
                    if (!isExistAsyncReceiver(t, asyncTReceivers)) {
                        AsyncReceiver receiver = new AsyncTopicReceiver(t);
                        receiver.setMsgListener(this);
                        asyncTReceivers.add(receiver);
                    }
                }
            }
            topicMsgListener = listener;
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "setTopicListener() error", ex);
        }
    }

    @Override
    public void setMQStateListener(MQStateListener listener) {
        this.stateListener = listener;
    }

    @Override
    public boolean isConnected() throws Exception {
        MQQueueManager queueManager = null;
        try {
            queueManager = getQueueManager();
            if (queueManager.isConnected() && queueManager.isOpen()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "isConnected() error", ex);
            return false;
        } finally {
            returnQueueManager(queueManager,false);
        }
    }

    @Override
    public List<AsyncReceiver> getAsyncQReceivers() {
        return asyncQReceivers;
    }

    @Override
    public List<AsyncReceiver> getAsyncTReceivers() {
        return asyncTReceivers;
    }

    @Override
    public void sendQueue(String queue, Message message) throws Exception {
        sendQueue(queue, message, -1);
    }

    @Override
    public Message receiveQueue(String queue) throws Exception {
        MQQueueManager queueManager = null;
        MQQueue mqQueue = null;
        MQMessage mqMessage = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_INQUIRE;
            mqQueue = queueManager.accessQueue(queue,options);

            if (mqQueue.isOpen() && mqQueue.getCurrentDepth() > 0) {
                MQGetMessageOptions gmo = new MQGetMessageOptions();
                gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_CONVERT | CMQC.MQGMO_FAIL_IF_QUIESCING;

                mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqQueue.get(mqMessage, gmo);

                Message message = new Message();
                Enumeration<String> propertyNames = mqMessage.getPropertyNames("%");
                if (propertyNames!= null) {
                    while(propertyNames.hasMoreElements()) {
                        String propertyName = propertyNames.nextElement();
                        message.getHead().put(propertyName, mqMessage.getObjectProperty(propertyName));
                    }
                }
                byte[] buffer = new byte[mqMessage.getMessageLength()];
                mqMessage.readFully(buffer, 0, buffer.length);
                message.setContent(buffer);
                return message;
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (ex instanceof  MQException) {
                MQException e = (MQException)ex;
                if (e.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE || e.getReason() == CMQC.MQRC_OPTIONS_CHANGED) {
                    return null;
                }
            }
            Log.error(IBMMQTransfer.class.getName(),"receiveQueue() error",ex);
            throw ex;
        } finally {
            try {
                if(mqMessage != null){
                    mqMessage.clearMessage();
                }
                if (mqQueue != null) {
                    mqQueue.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close queue error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    @Override
    public void sendTopic(String topic, Message message) throws Exception {
        sendTopic(topic, message,-1);
    }

    @Override
    public Message receiveTopic(String topic) throws Exception {
        MQQueueManager queueManager = null;
        MQTopic mqTopic = null;
        MQMessage mqMessage = null;
        try {
            queueManager = getQueueManager();
            int options = CMQC.MQSO_CREATE | CMQC.MQOO_FAIL_IF_QUIESCING;
            mqTopic = queueManager.accessTopic(topic,topic,CMQC.MQTOPIC_OPEN_AS_SUBSCRIPTION,options);

            if (mqTopic.isOpen()) {
                MQGetMessageOptions gmo = new MQGetMessageOptions();
                gmo.options =  CMQC.MQGMO_NO_WAIT | CMQC.MQOO_FAIL_IF_QUIESCING | CMQC.MQGMO_CONVERT | CMQC.MQGMO_NO_PROPERTIES;
                mqMessage = new MQMessage();
                mqMessage.characterSet = 1208;
                mqTopic.get(mqMessage, gmo);
                Message message = new Message();
                byte[] buffer = new byte[mqMessage.getMessageLength()];
                mqMessage.readFully(buffer, 0, buffer.length);
                message.setContent(buffer);
                return message;
            } else {
                return null;
            }
        } catch (Exception ex) {
            if (ex instanceof  MQException) {
                MQException e = (MQException)ex;
                if (e.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE || e.getReason() == CMQC.MQRC_OPTIONS_CHANGED) {
                    return null;
                }
            }
            Log.error(IBMMQTransfer.class.getName(),"receiveTopic() error",ex);
            throw ex;
        } finally {
            try {
                if(mqMessage != null){
                    mqMessage.clearMessage();
                }
                if (mqTopic != null) {
                    mqTopic.close();
                }
            } catch (Exception ex) {
                Log.error(IBMMQTransfer.class.getName(), "close topic error", ex);
            }
            returnQueueManager(queueManager,false);
        }
    }

    @Override
    public void startAsync() {
        try {

            // 获取队列管理器
            queueManager = getQueueManager();

            // 启动监控线程
            executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new MonitorRunnable(queueManager));
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "startAsync() error", ex);
        }
    }

    @Override
    public void stopAsync() {
        try {

            // 关闭监控线程
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdownNow();
            }

            // 关闭异步接收
            stopReceivers();

            // 回收队列管理器
            returnQueueManager(queueManager,true);
            notifyState(MQState.DISCONNECTED);
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "stopAsync() error", ex);
        }
    }

    @Override
    public void onMessage(String name, Message message, int type) {
        try {
            if (type == 0) {
                if (queueMsgListener != null) {
                    queueMsgListener.onQueueMsg(name,message,getMQInfo());
                }
            } else {
                if (topicMsgListener != null) {
                    topicMsgListener.onTopicMsg(name,message,getMQInfo());
                }
            }
        } catch (Exception ex) {
            Log.error(IBMMQTransfer.class.getName(), "onMessage() error", ex);
        }
    }

    /**
     * IBMMQ监控线程
     */
    private class MonitorRunnable implements Runnable {

        // MQ长连接
        private MQQueueManager queueManager;

        /**
         * 构造函数
         * @param queueManager
         */
        public MonitorRunnable(MQQueueManager queueManager){
            this.queueManager = queueManager;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        MQAsyncStatus status = queueManager.getAsyncStatus();
                        if (status.completionCode == 0) {
                            if (status.reasonCode == CMQC.MQCC_OK || status.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE || status.reasonCode == CMQC.MQRC_OPTIONS_CHANGED){
                                if (state != MQState.CONNECTED) {
                                    startReceivers(queueManager);
                                    notifyState(MQState.CONNECTED);
                                }
                            } else {
                                Log.error(MonitorRunnable.class.getName(), "connect qm " + configuration.getQueueManager() + " error reasonCode:" + status.reasonCode);
                                if (state != MQState.CONNECTING) {
                                    notifyState(MQState.CONNECTING);
                                }

                                // 关闭异步接收
                                stopReceivers();

                                // 重新获取QueueManager
                                returnQueueManager(queueManager,true);
                                try {
                                    queueManager = getQueueManager();
                                } catch (Exception e) {
                                    Log.error(MonitorRunnable.class.getName(), "get queueManager instance error",e);
                                }
                            }
                        } else {
                            Log.error(MonitorRunnable.class.getName(), "connect qm " + configuration.getQueueManager() + " error reasonCode:" + status.reasonCode);
                            if (state != MQState.CONNECTING) {
                                notifyState(MQState.CONNECTING);
                            }

                            // 关闭异步接收
                            stopReceivers();

                            // 重新获取QueueManager
                            returnQueueManager(queueManager,true);
                            try {
                                queueManager = getQueueManager();
                            } catch (Exception e) {
                                Log.error(MonitorRunnable.class.getName(), "get queueManager instance error", e);
                            }
                        }
                    } catch (Exception ex) {
                        Log.error(MonitorRunnable.class.getName(), "connect qm " + configuration.getQueueManager() + " error", ex);
                        if (state != MQState.CONNECTING) {
                            notifyState(MQState.CONNECTING);
                        }

                        // 关闭异步接收
                        stopReceivers();

                        // 重新获取QueueManager
                        returnQueueManager(queueManager,true);
                        try {
                            queueManager = getQueueManager();
                        } catch (Exception e) {
                            Log.error(MonitorRunnable.class.getName(), "get queueManager instance error", e);
                        }
                    }
                    Thread.sleep(2000);
                }
            } catch (Exception ex) {
                Log.error(MonitorRunnable.class.getName(), "run () error", ex);
            }
        }
    }
}
