package com.adcc.utility.mq.manager.ibm;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.ibm.IBMMQConfiguration;
import com.adcc.utility.mq.entity.Queue;
import com.adcc.utility.mq.entity.Topic;
import com.adcc.utility.mq.entity.ibm.*;
import com.adcc.utility.mq.manager.MQManager;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.adcc.utility.mq.transfer.ibm.IBMMQConnectionPool;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.CMQCFC;
import com.ibm.mq.pcf.PCFMessage;
import com.ibm.mq.pcf.PCFMessageAgent;

import java.util.List;

/**
* IBM MQ管理类
*/
public class IBMMQManager<T1 extends Queue, T2 extends Topic> implements MQManager<T1, T2> {

    // 连接配置
    private IBMMQConfiguration configuration;

    // 连接池
    private IBMMQConnectionPool pool;

    /**
     * 构造函数
     */
    public IBMMQManager(MQConfiguration configuration) {
        this.configuration = (IBMMQConfiguration)configuration;
    }

    /**
     * 构造函数
     * @param configuration
     * @param pool
     */
    public IBMMQManager(MQConfiguration configuration, MQConnectionPool pool) {
        this.configuration = (IBMMQConfiguration)configuration;
        this.pool = (IBMMQConnectionPool)pool;
    }

    /**
     * 取得连接池
     * @return
     * @throws Exception
     */
    private MQQueueManager getQueueManager() throws Exception {
        if (pool == null) {
            return new MQQueueManager(configuration.getQueueManager(), configuration.getParams());
        } else {
            Optional<MQQueueManager> optional = pool.getConnection();
            if (optional.isPresent()) {
                MQQueueManager queueManager = optional.get();
                return queueManager;
            } else {
                throw new NullPointerException("get queueManager is null");
            }
        }
    }

    /**
     * 返还连接池
     * @param queueManager
     */
    private void returnQueueManager(MQQueueManager queueManager) {
        try {
            if (queueManager != null) {
                if (pool == null) {
                    if (queueManager.isConnected()) {
                        queueManager.disconnect();
                    }
                    if (queueManager.isOpen()) {
                        queueManager.close();
                    }
                } else {
                    pool.returnConnection(Optional.of(queueManager));
                }
            }
        } catch(Exception ex) {
            Log.error(IBMMQManager.class.getName(), "returnQueueManager() error", ex);
        }
    }

    public List<QueueManager> findQueueManager() throws Exception {
        MQQueueManager queueManager = null;
        try{
            List<QueueManager> result = Lists.newArrayListWithExpectedSize(100);
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q_MGR_STATUS);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if(messages != null && messages.length > 0){
                for(PCFMessage pm : messages){
                    QueueManager qmgr = new QueueManager();
                    qmgr.setName(pm.getStringParameterValue(CMQC.MQCA_Q_MGR_NAME));
                    qmgr.setIp(configuration.getHost());
                    qmgr.setPort(configuration.getPort());
                    String strDate = pm.getStringParameterValue(CMQCFC.MQCACF_Q_MGR_START_DATE);
                    String strTime = pm.getStringParameterValue(CMQCFC.MQCACF_Q_MGR_START_TIME);
                    String strStarTime = strDate.trim() + " " + strTime.trim().replace(".",":");
                    qmgr.setStartTime(strStarTime);
                    qmgr.setConnectCount(pm.getIntParameterValue(CMQCFC.MQIACF_CONNECTION_COUNT));
                    int intStatus = pm.getIntParameterValue(CMQCFC.MQIACF_Q_MGR_STATUS);
                    if(intStatus == 2){
                        qmgr.setStatus(true);
                    }else{
                        qmgr.setStatus(false);
                    }
                    result.add(qmgr);
                }
            }
            return result;
        }catch (Exception ex){
            Log.error(IBMMQManager.class.getName(), "findQueueManager() error", ex);
            throw ex;
        }finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 创建通道
     * @param channel
     * @throws Exception
     * */
    public void createChannel(Channel channel) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (channel == null) {
                throw new NullPointerException("channel is null");
            }
            if (Strings.isNullOrEmpty(channel.getName())) {
                throw new NullPointerException("channel name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CREATE_CHANNEL);
            pcfMessage.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channel.getName());
            pcfMessage.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, channel.getType());
            pcfMessage.addParameter(CMQCFC.MQCACH_MCA_USER_ID, channel.getUser());
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "createChannel() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 删除通道
     * @param channel
     * @throws Exception
     * */
    public void removeChannel(Channel channel) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (channel == null) {
                throw new NullPointerException("channel is null");
            }
            if (Strings.isNullOrEmpty(channel.getName())) {
                throw new NullPointerException("channel name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_DELETE_CHANNEL);
            pcfMessage.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channel.getName());
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "removeChannel() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 更新通道
     * @param channel
     * @throws Exception
     * */
    public void updateChannel(Channel channel) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (channel == null) {
                throw new NullPointerException("channel is null");
            }
            if (Strings.isNullOrEmpty(channel.getName())) {
                throw new NullPointerException("channel name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CHANGE_CHANNEL);
            pcfMessage.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, channel.getName());
            pcfMessage.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, channel.getType());
            pcfMessage.addParameter(CMQCFC.MQCACH_MCA_USER_ID, channel.getUser());
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "updateChannel() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 查询通道
     * @param name
     * @throws Exception
     * */
    public List<Channel> findChannel(String name) throws Exception {
        MQQueueManager queueManager = null;
        List<Channel> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("channel name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
            pcfMessage.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, name);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for(PCFMessage pcf : messages) {
                    if(pcf != null) {
                        Channel channel = new Channel();
                        channel.setName(pcf.getParameterValue(CMQCFC.MQCACH_CHANNEL_NAME).toString().trim());
                        channel.setType(pcf.getIntParameterValue(CMQCFC.MQIACH_CHANNEL_TYPE));
                        channel.setUser(String.valueOf(pcf.getParameterValue(CMQCFC.MQCACH_MCA_USER_ID)).trim());
                        result.add(channel);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "findChannel() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 查询通道
     * @param name
     * @param type
     * @throws Exception
     * */
    public List<Channel> findChannel(String name, int type) throws Exception {
        MQQueueManager queueManager = null;
        List<Channel> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("channel name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_CHANNEL);
            pcfMessage.addParameter(CMQCFC.MQCACH_CHANNEL_NAME, name);
            pcfMessage.addParameter(CMQCFC.MQIACH_CHANNEL_TYPE, type);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for(PCFMessage pcf : messages) {
                    if(pcf != null) {
                        Channel channel = new Channel();
                        channel.setName(pcf.getParameterValue(CMQCFC.MQCACH_CHANNEL_NAME).toString().trim());
                        channel.setType(type);
                        channel.setUser(pcf.getParameterValue(CMQCFC.MQCACH_MCA_USER_ID).toString().trim());
                        result.add(channel);
                    }
                }
            }
            return result;
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "findChannel() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 查询所有通道
     * @throws Exception
     * */
    public List<Channel> findAllChannel() throws Exception {
        return findChannel("*");
    }

    /**
     * 查询主题
     * @param name
     * @param type
     * @throws Exception
     * */
    public List<T2> findTopic(String name, int type) throws Exception {
        MQQueueManager queueManager = null;
        List<IBMTopic> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("topic name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC);
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_NAME, name);
            pcfMessage.addParameter(CMQC.MQIA_TOPIC_TYPE, type);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for (PCFMessage pcf : messages){
                    if (pcf != null) {
                        IBMTopic topic = new IBMTopic();
                        topic.setName(pcf.getParameterValue(CMQC.MQCA_TOPIC_NAME).toString().trim());
                        topic.setType(type);
                        topic.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                        topic.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_TOPIC_DEF_PERSISTENCE) == 1) ? true : false);
                        result.add(topic);
                    }
                }
            }
            return ((List<T2>)result);
        } catch (Exception ex) {
            Log.error(MQManager.class.getName(), "findTopic() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    /**
     * 查询队列
     * @param name
     * @param type
     * @throws Exception
     * */
    public List<T1> findQueue(String name, int type) throws Exception {
        MQQueueManager queueManager = null;
        List<IBMQueue> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, name);
            pcfMessage.addParameter(CMQC.MQIA_Q_TYPE, type);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for (PCFMessage pcf : messages) {
                    if (pcf != null) {
                        if(type == IBMQueue.CONSTANT_LOCAL){
                            LocalQueue queue = new LocalQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            queue.setMaxDepth(pcf.getIntParameterValue(CMQC.MQIA_MAX_Q_DEPTH));
                            queue.setMaxMsgLength(pcf.getIntParameterValue(CMQC.MQIA_MAX_MSG_LENGTH));
                            queue.setRetainInterval(pcf.getIntParameterValue(CMQC.MQIA_RETENTION_INTERVAL));
                            queue.setDepth(pcf.getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH));
                            queue.setProducers(pcf.getIntParameterValue(CMQC.MQIA_OPEN_OUTPUT_COUNT));
                            queue.setConsumers(pcf.getIntParameterValue(CMQC.MQIA_OPEN_INPUT_COUNT));
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_ALIAS){
                            AliasQueue queue = new AliasQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_MODEL){
                            ModelQueue queue = new ModelQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_REMOTE){
                            RemoteQueue queue = new RemoteQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }
                    }
                }
            }
            return (List<T1>) result;
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "findQueue() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void createQueue(T1 queue) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (queue == null) {
                throw new NullPointerException("queue is null");
            }
            if (Strings.isNullOrEmpty(queue.getName())) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CREATE_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, queue.getName());
            pcfMessage.addParameter(CMQC.MQIA_Q_TYPE, ((IBMQueue)queue).getType());
            pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, ((IBMQueue)queue).getPriority());
            if (((IBMQueue)queue).isPersistent()) {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PERSISTENCE, CMQC.MQPER_PERSISTENT);
            } else {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PERSISTENCE, CMQC.MQPER_NOT_PERSISTENT);
            }
            if (((IBMQueue)queue).getType() == IBMQueue.CONSTANT_LOCAL) {
                pcfMessage.addParameter(CMQC.MQIA_MAX_Q_DEPTH, ((LocalQueue) queue).getMaxDepth());
                pcfMessage.addParameter(CMQC.MQIA_MAX_MSG_LENGTH, ((LocalQueue)queue).getMaxMsgLength());
                pcfMessage.addParameter(CMQC.MQIA_RETENTION_INTERVAL, ((LocalQueue)queue).getRetainInterval());
            }
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "createQueue() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void removeQueue(T1 queue) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if(queue == null) {
                throw new NullPointerException("queue is null or empty");
            }
            if (Strings.isNullOrEmpty(queue.getName())) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_DELETE_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, queue.getName());
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(MQManager.class.getName(), "removeQueue() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void updateQueue(T1 queue) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if(queue == null) {
                throw new NullPointerException("queue is null or empty");
            }
            if (Strings.isNullOrEmpty(queue.getName())) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CHANGE_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, queue.getName());
            pcfMessage.addParameter(CMQC.MQIA_Q_TYPE, ((IBMQueue)queue).getType());
            pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, ((IBMQueue)queue).getPriority());
            if (((IBMQueue)queue).isPersistent()) {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PERSISTENCE, CMQC.MQPER_PERSISTENT);
            } else {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PERSISTENCE, CMQC.MQPER_NOT_PERSISTENT);
            }
            if (((IBMQueue)queue).getType() == IBMQueue.CONSTANT_LOCAL) {
                pcfMessage.addParameter(CMQC.MQIA_MAX_Q_DEPTH, ((LocalQueue) queue).getMaxDepth());
                pcfMessage.addParameter(CMQC.MQIA_MAX_MSG_LENGTH, ((LocalQueue)queue).getMaxMsgLength());
                pcfMessage.addParameter(CMQC.MQIA_RETENTION_INTERVAL, ((LocalQueue)queue).getRetainInterval());
            }
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "updateQueue() error",ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public List<T1> findQueue(String name) throws Exception {
        MQQueueManager queueManager = null;
        List<IBMQueue> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, name);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for (PCFMessage pcf : messages) {
                    if (pcf != null) {
                        int type = pcf.getIntParameterValue(CMQC.MQIA_Q_TYPE);
                        if(type == IBMQueue.CONSTANT_LOCAL){
                            LocalQueue queue = new LocalQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            queue.setMaxDepth(pcf.getIntParameterValue(CMQC.MQIA_MAX_Q_DEPTH));
                            queue.setMaxMsgLength(pcf.getIntParameterValue(CMQC.MQIA_MAX_MSG_LENGTH));
                            queue.setRetainInterval(pcf.getIntParameterValue(CMQC.MQIA_RETENTION_INTERVAL));
                            queue.setDepth(pcf.getIntParameterValue(CMQC.MQIA_CURRENT_Q_DEPTH));
                            queue.setProducers(pcf.getIntParameterValue(CMQC.MQIA_OPEN_OUTPUT_COUNT));
                            queue.setConsumers(pcf.getIntParameterValue(CMQC.MQIA_OPEN_INPUT_COUNT));
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_ALIAS){
                            AliasQueue queue = new AliasQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_MODEL){
                            ModelQueue queue = new ModelQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }else if(type == IBMQueue.CONSTANT_REMOTE){
                            RemoteQueue queue = new RemoteQueue();
                            queue.setName(pcf.getParameterValue(CMQC.MQCA_Q_NAME).toString().trim());
                            queue.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                            queue.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_DEF_PERSISTENCE) == 1) ? true : false);
                            result.add(queue);
                        }
                    }
                }
            }
            return (List<T1>) result;
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "findQueue() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public List<T1> findAllQueue() throws Exception {
        return findQueue("*");
    }

    @Override
    public void clearQueue(String name) throws Exception {
        MQQueueManager queueManager = null;
        try{
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("queue name is null or empty");
            }
            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CLEAR_Q);
            pcfMessage.addParameter(CMQC.MQCA_Q_NAME, name);
            pcfMessageAgent.send(pcfMessage);
        }catch (Exception ex){
            Log.error(IBMMQManager.class.getName(),"clearQueue() error",ex);
            throw ex;
        }finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void clearAllQueue() throws Exception {
        clearQueue("*");
    }

    @Override
    public void createTopic(T2 topic) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (topic == null) {
                throw new NullPointerException("topic is null");
            }
            if (Strings.isNullOrEmpty(topic.getName())) {
                throw new NullPointerException("topic name is null or empty");
            }

            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CREATE_TOPIC);
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_NAME, topic.getName());
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_STRING, topic.getName());
            pcfMessage.addParameter(CMQC.MQIA_TOPIC_TYPE, ((IBMTopic) topic).getType());
            if (((IBMTopic)topic).getPriority() >= 0) {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, ((IBMTopic)topic).getPriority());
            } else {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, CMQC.MQPRI_PRIORITY_AS_PARENT);
            }
            if (((IBMTopic)topic).isPersistent()) {
                pcfMessage.addParameter(CMQC.MQIA_TOPIC_DEF_PERSISTENCE, CMQC.MQPER_PERSISTENT);
            } else {
                pcfMessage.addParameter(CMQC.MQIA_TOPIC_DEF_PERSISTENCE, CMQC.MQPER_NOT_PERSISTENT);
            }
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(IBMMQManager.class.getName(), "createTopic() error",ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void removeTopic(T2 topic) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (topic == null) {
                throw new NullPointerException("topic is null");
            }
            if (Strings.isNullOrEmpty(topic.getName())) {
                throw new NullPointerException("topic name is null or empty");
            }

            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_DELETE_TOPIC);
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_NAME, topic.getName());
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(MQManager.class.getName(), "removeTopic() error",ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public void updateTopic(T2 topic) throws Exception {
        MQQueueManager queueManager = null;
        try {
            if (topic == null) {
                throw new NullPointerException("topic is null");
            }
            if (Strings.isNullOrEmpty(topic.getName())) {
                throw new NullPointerException("topic name is null or empty");
            }

            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_CHANGE_TOPIC);
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_NAME, topic.getName());
            if (((IBMTopic)topic).getPriority() >= 0) {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, ((IBMTopic)topic).getPriority());
            } else {
                pcfMessage.addParameter(CMQC.MQIA_DEF_PRIORITY, CMQC.MQPRI_PRIORITY_AS_PARENT);
            }
            if (((IBMTopic)topic).isPersistent()) {
                pcfMessage.addParameter(CMQC.MQIA_TOPIC_DEF_PERSISTENCE, CMQC.MQPER_PERSISTENT);
            } else {
                pcfMessage.addParameter(CMQC.MQIA_TOPIC_DEF_PERSISTENCE, CMQC.MQPER_NOT_PERSISTENT);
            }
            pcfMessageAgent.send(pcfMessage);
        } catch (Exception ex) {
            Log.error(MQManager.class.getName(), "updateTopic() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public List<T2> findTopic(String name) throws Exception {
        MQQueueManager queueManager = null;
        List<IBMTopic> result = Lists.newArrayListWithExpectedSize(100);
        try {
            if (Strings.isNullOrEmpty(name)) {
                throw new NullPointerException("topic name is null or empty");
            }

            queueManager = getQueueManager();
            PCFMessageAgent pcfMessageAgent = new PCFMessageAgent(queueManager);
            PCFMessage pcfMessage = new PCFMessage(CMQCFC.MQCMD_INQUIRE_TOPIC);
            pcfMessage.addParameter(CMQC.MQCA_TOPIC_NAME, name);
            PCFMessage[] messages = pcfMessageAgent.send(pcfMessage);
            if (messages != null && messages.length > 0) {
                for (PCFMessage pcf : messages){
                    if (pcf != null) {
                        IBMTopic topic = new IBMTopic();
                        topic.setName(pcf.getParameterValue(CMQC.MQCA_TOPIC_NAME).toString().trim());
                        topic.setType(pcf.getIntParameterValue(CMQC.MQIA_TOPIC_TYPE));
                        topic.setPriority(pcf.getIntParameterValue(CMQC.MQIA_DEF_PRIORITY));
                        topic.setPersistent((pcf.getIntParameterValue(CMQC.MQIA_TOPIC_DEF_PERSISTENCE) == 1) ? true : false);
                        result.add(topic);
                    }
                }
            }
            return ((List<T2>)result);
        } catch (Exception ex) {
            Log.error(MQManager.class.getName(), "findTopic() error", ex);
            throw ex;
        } finally {
            returnQueueManager(queueManager);
        }
    }

    @Override
    public List<T2> findAllTopic() throws Exception {
        return findTopic("*");
    }
}
