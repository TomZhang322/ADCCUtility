package com.adcc.utility.mq.transfer.active;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.active.ActiveMQConfiguration;
import com.adcc.utility.mq.entity.MQState;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.entity.active.AckMode;
import com.adcc.utility.mq.transfer.*;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.transport.TransportListener;

import javax.jms.*;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Active MQ通信类
 */
public class ActiveMQTransfer implements MQTransfer,TransportListener,AsyncMsgListener{

    // failover标识
    private static final String STRING_FAILOVER_FLAG = "failover:";

    // 连接配置
    private ActiveMQConfiguration configuration;

    // 连接池
    private ActiveMQConnectionPool pool;

    // 应答模式
    private AckMode ackMode = AckMode.AUTO_ACKNOWLEDGE;

    // MQ状态
    private MQState state = MQState.DISCONNECTED;

    // Failover标识
    private boolean failoverFlag = false;

    // MQ长连接
    private ActiveMQConnection connection;

    // 队列异步接收队列
    protected List<AsyncReceiver> asyncQReceivers = Lists.newArrayListWithExpectedSize(500);

    // 会话异步接收队列
    protected List<AsyncReceiver> asyncTReceivers = Lists.newArrayListWithExpectedSize(500);

    // 队列接收监听器
    private QueueMsgListener queueMsgListener;

    // 主题接收监听器
    private TopicMsgListener topicMsgListener;

    // MQ状态监听器
    private MQStateListener stateListener;

    /**
     * 构造函数
     */
    public ActiveMQTransfer(){

    }

    /**
     * 构造函数
     * @param configuration
     */
    public ActiveMQTransfer(MQConfiguration configuration) throws Exception{
        this.configuration = (ActiveMQConfiguration)configuration;
        if(this.configuration.getUrl().contains(STRING_FAILOVER_FLAG)){
            failoverFlag = true;
        }
    }

    /**
     * 构造函数
     * @param configuration
     * @param pool
     * @throws Exception
     */
    public ActiveMQTransfer(MQConfiguration configuration,MQConnectionPool pool) throws Exception{
        this.configuration = (ActiveMQConfiguration)configuration;
        if(this.configuration.getUrl().contains(STRING_FAILOVER_FLAG)){
            failoverFlag = true;
        }
        this.pool = (ActiveMQConnectionPool)pool;
    }

    /**
     * 设置应答模式
     * @param ackMode
     */
    public void setAckMode(AckMode ackMode){
        this.ackMode = ackMode;
    }

    /**
     * 取得MQ信息
     * @return
     */
    private Map<String,String> getMQInfo(){
        Map<String,String> info = Maps.newHashMapWithExpectedSize(2);
        info.put("url",configuration.getUrl());
        if(connection != null){
            info.put("broker", Strings.nullToEmpty(connection.getBrokerName()));
        }else{
            info.put("broker", Strings.nullToEmpty(""));
        }
        return info;
    }

    /**
     * 通知MQ连接状态
     * @param state
     */
    private void notifyState(MQState state) {
        this.state = state;
        if(stateListener != null){
            stateListener.onState(state, getMQInfo());
        }
    }

    /**
     * 取得连接池
     * @return
     * @throws Exception
     */
    private Connection getConnection() throws Exception{
        Optional<Connection> optional = pool.getConnection();
        if(optional.isPresent()){
            Connection connection = optional.get();
            connection.start();
            return connection;
        }else{
            throw new NullPointerException("get connection is null");
        }
    }

    /**
     * 返回连接池
     * @param connection
     */
    private void retrunConnection(Connection connection){
        pool.returnConnection(Optional.of(connection));
    }

    /**
     * 是否存在异步接收
     * @param name
     * @param receiverList
     * @return
     * @throws Exception
     */
    private boolean isExistAsyncReceiver(String name,List<AsyncReceiver> receiverList){
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
    private void startReceivers(){
        if(asyncQReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncQReceivers){
                ((AsyncQueueReceiver)receiver).setConnection(connection);
                receiver.start();
            }
        }
        if(asyncTReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncTReceivers){
                ((AsyncTopicReceiver)receiver).setConnection(connection);
                receiver.start();
            }
        }
    }

    /**
     * 停止异步接收处理
     */
    private void stopReceivers(){
        if(asyncQReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncQReceivers){
                receiver.stop();
            }
        }
        if(asyncTReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncTReceivers){
                receiver.stop();
            }
        }
    }

    /**
     * 格式化消息
     * @param msg
     * @return
     */
    private Optional<Message> formatMsg(javax.jms.Message msg){
        try{
            Message message = null;
            if(msg == null){
                return Optional.absent();
            }else{
                if(msg instanceof TextMessage){
                    TextMessage tm = (TextMessage)msg;
                    if(tm != null && tm.getText().length() > 0){
                        message = new Message(tm.getText().getBytes());

                        // 获取消息头
                        Enumeration<String> enumeration = tm.getPropertyNames();
                        while(enumeration.hasMoreElements()){
                            String key = enumeration.nextElement();
                            Object value = tm.getObjectProperty(key);
                            message.getHead().put(key,value);
                        }
                    }
                }else if(msg instanceof BytesMessage){
                    BytesMessage bm = (BytesMessage)msg;
                    if(bm != null && bm.getBodyLength() > 0){
                        byte[] buffer = new byte[(int)bm.getBodyLength()];
                        bm.readBytes(buffer);
                        message = new Message(buffer);

                        // 获取消息头
                        Enumeration<String> enumeration = bm.getPropertyNames();
                        while(enumeration.hasMoreElements()){
                            String key = enumeration.nextElement();
                            Object value = bm.getObjectProperty(key);
                            message.getHead().put(key,value);
                        }
                    }
                }else{
                    return Optional.absent();
                }
            }
            return Optional.fromNullable(message);
        }catch (Exception ex){
            Log.error("formatMsg() error",ex);
            return Optional.absent();
        }
    }

    public void sendQueue(String queue, Message message,long expiredTime) throws Exception{
        Session session = null;
        MessageProducer producer = null;
        Connection connection = getConnection();
        try{
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session = connection.createSession(true, ackMode.ordinal());
            }else{
                session = connection.createSession(false, ackMode.ordinal());
            }
            Destination destination = session.createQueue(queue);
            producer = session.createProducer(destination);
            BytesMessage bm = session.createBytesMessage();
            if(message.getHead().size() > 0){
                for(Iterator<Map.Entry<String,Object>> iterator = message.getHead().entrySet().iterator();iterator.hasNext();){
                    Map.Entry<String,Object> entry = iterator.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    bm.setObjectProperty(key,value);
                }
            }
            bm.writeBytes(message.getContent());
            producer.setTimeToLive(expiredTime);
            producer.send(bm);
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session.commit();
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"sendQueue() error",ex);
            if(session != null){
                session.rollback();
            }
            throw ex;
        }finally {
            if(producer != null){
                producer.close();
            }
            if(session != null){
                session.close();
            }
            retrunConnection(connection);
        }
    }

    public Message receiveQueue(String queue,long expiredTime) throws Exception {
        Session session = null;
        MessageConsumer consumer = null;
        Connection connection = getConnection();
        try{
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session = connection.createSession(true, ackMode.ordinal());
            }else{
                session = connection.createSession(false, ackMode.ordinal());
            }
            Destination destination = session.createQueue(queue);
            consumer = session.createConsumer(destination);
            javax.jms.Message msg = null;
            if(expiredTime == 0){
                msg = consumer.receiveNoWait();
            }else{
                msg = consumer.receive(expiredTime);
            }
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session.commit();
            }
            Optional<Message> optional = formatMsg(msg);
            if(optional.isPresent()){
                return optional.get();
            }else{
                return null;
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"receiveQueue() error",ex);
            if(session != null){
                session.rollback();
            }
            throw ex;
        }finally {
            if(consumer != null){
                consumer.close();
            }
            if(session != null){
                session.close();
            }
            retrunConnection(connection);
        }
    }

    public void sendTopic(String topic,Message message,long expiredTime) throws Exception{
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        try{
            connection = getConnection();
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session = connection.createSession(true, ackMode.ordinal());
            }else{
                session = connection.createSession(false, ackMode.ordinal());
            }
            Destination destination = session.createTopic(topic);
            producer = session.createProducer(destination);
            BytesMessage bm = session.createBytesMessage();
            if(message.getHead().size() > 0){
                for(Iterator<Map.Entry<String,Object>> iterator = message.getHead().entrySet().iterator();iterator.hasNext();){
                    Map.Entry<String,Object> entry = iterator.next();
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    bm.setObjectProperty(key,value);
                }
            }
            bm.writeBytes(message.getContent());
            producer.setTimeToLive(expiredTime);
            producer.send(bm);
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session.commit();
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"sendTopic() error",ex);
            if(session != null){
                session.rollback();
            }
            throw ex;
        }finally {
            if(producer != null){
                producer.close();
            }
            if(session != null){
                session.close();
            }
            retrunConnection(connection);
        }
    }

    public Message receiveTopic(String queue,long expiredTime) throws Exception {
        Session session = null;
        MessageConsumer consumer = null;
        Connection connection = getConnection();
        try{
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session = connection.createSession(true, ackMode.ordinal());
            }else{
                session = connection.createSession(false, ackMode.ordinal());
            }
            Destination destination = session.createTopic(queue);
            consumer = session.createConsumer(destination);
            javax.jms.Message msg = null;
            if(expiredTime == 0){
                msg = consumer.receiveNoWait();
            }else{
                msg = consumer.receive(expiredTime);
            }
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session.commit();
            }
            Optional<Message> optional = formatMsg(msg);
            if(optional.isPresent()){
                return optional.get();
            }else{
                return null;
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"receiveTopic() error",ex);
            if(session != null){
                session.rollback();
            }
            throw ex;
        }finally {
            if(consumer != null){
                consumer.close();
            }
            if(session != null){
                session.close();
            }
            retrunConnection(connection);
        }
    }

    @Override
    public void setConfiguration(MQConfiguration configuration) throws Exception{
        if(configuration == null){
            throw new NullPointerException("configuration is null");
        }
        this.configuration = (ActiveMQConfiguration)configuration;
        if(this.configuration.getUrl().contains(STRING_FAILOVER_FLAG)){
            failoverFlag = true;
        }
    }

    @Override
    public void setConnectionPool(MQConnectionPool pool) throws Exception {
        if(pool == null){
            throw new NullPointerException("pool is null");
        }
        this.pool = (ActiveMQConnectionPool)pool;
    }

    @Override
    public void setQueueListener(QueueMsgListener listener) {
        if(asyncQReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncQReceivers){
                receiver.setMsgListener(this);
            }
        }
        this.queueMsgListener = listener;
    }

    @Override
    public void setQueueListener(QueueMsgListener listener, String... queue) {
        if(queue != null && queue.length > 0){
            for(String t : queue){
                if(!isExistAsyncReceiver(t,asyncQReceivers)){
                    AsyncReceiver receiver = new AsyncQueueReceiver(t,ackMode);
                    receiver.setMsgListener(this);
                    asyncQReceivers.add(receiver);
                }
            }
        }
        this.queueMsgListener = listener;
    }

    @Override
    public void setTopicListener(TopicMsgListener listener) {
        if(asyncTReceivers.size() > 0){
            for(AsyncReceiver receiver : asyncTReceivers){
                receiver.setMsgListener(this);
            }
        }
        this.topicMsgListener = listener;
    }

    @Override
    public void setTopicListener(TopicMsgListener listener, String... topic) {
        if(topic != null && topic.length > 0){
            for(String t : topic){
                if(!isExistAsyncReceiver(t,asyncTReceivers)){
                    AsyncReceiver receiver = new AsyncTopicReceiver(t,ackMode);
                    receiver.setMsgListener(this);
                    asyncTReceivers.add(receiver);
                }
            }
        }
        this.topicMsgListener = listener;
    }

    @Override
    public void setMQStateListener(MQStateListener listener) {
        this.stateListener = listener;
    }

    @Override
    public boolean isConnected() throws Exception {
        ActiveMQConnection connection = (ActiveMQConnection)getConnection();
        try{
            if(connection.isStarted()){
                return true;
            }else{
                return false;
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"isConnected() error",ex);
            return false;
        }finally {
            retrunConnection(connection);
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
        sendQueue(queue,message,javax.jms.Message.DEFAULT_TIME_TO_LIVE);
    }

    @Override
    public Message receiveQueue(String queue) throws Exception {
        return receiveQueue(queue, 0);
    }

    @Override
    public void sendTopic(String topic, Message message) throws Exception {
        sendTopic(topic,message,0);
    }

    @Override
    public Message receiveTopic(String topic) throws Exception {
        return receiveTopic(topic,0);
    }

    @Override
    public void startAsync() {
        try{
            connection = (ActiveMQConnection)getConnection();
            connection.addTransportListener(this);
            connection.start();
            if(connection.isStarted()){
                startReceivers();
                notifyState(MQState.CONNECTED);
            }else{
                notifyState(MQState.CONNECTING);
            }
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"startAsync() error",ex);
            notifyState(MQState.CONNECTING);
            if(!failoverFlag){
                try {
                    Thread.sleep(2000);
                    startAsync();
                } catch (Exception e) {
                    Log.error("restart startAsync error",e);
                }
            }
        }
    }

    @Override
    public void stopAsync() {
        try{
            stopReceivers();
            retrunConnection(connection);
            notifyState(MQState.DISCONNECTED);
        }catch (Exception ex){
            Log.error(ActiveMQTransfer.class.getName(),"stopAsync() error",ex);
        }
    }

    @Override
    public void onCommand(Object o) {
        Log.debug(ActiveMQTransfer.class.getName(), "connect ActiveMQ command:" + o.toString());
    }

    @Override
    public void onException(IOException e) {
        Log.error(ActiveMQTransfer.class.getName(),"connect ActiveMQ error",e);
        if(state != MQState.CONNECTING){
            notifyState(MQState.CONNECTING);
            if(!failoverFlag){
                startAsync();
            }
        }
    }

    @Override
    public void transportInterupted() {
        Log.error(ActiveMQTransfer.class.getName(),"connect ActiveMQ interrupted");
        if(state != MQState.CONNECTING){
            notifyState(MQState.CONNECTING);
            if(!failoverFlag){
                startAsync();
            }
        }
    }

    @Override
    public void transportResumed() {
        Log.info(ActiveMQTransfer.class.getName(),"connect ActiveMQ resumed");
        if(state != MQState.CONNECTED){
            notifyState(MQState.CONNECTED);
        }
    }

    @Override
    public void onMessage(String name, Message message, int type) {
        if(type == 0){
            if(queueMsgListener != null){
                queueMsgListener.onQueueMsg(name,message,getMQInfo());
            }
        }else{
            if(topicMsgListener != null){
                topicMsgListener.onTopicMsg(name,message,getMQInfo());
            }
        }
    }
}
