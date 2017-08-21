package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.entity.MQState;
import com.adcc.utility.mq.entity.Message;

import java.util.List;

/**
 * MQ通信接口
 */
public interface MQTransfer{

    /**
     * 设置连接配置
     * @param configuration
     */
    public void setConfiguration(MQConfiguration configuration) throws Exception;

    /**
     * 设置连接池
     * @param pool
     * @throws Exception
     */
    public void setConnectionPool(MQConnectionPool pool) throws Exception;

    /**
     * 设置队列接收监听器
     * @param listener
     */
    public void setQueueListener(QueueMsgListener listener);

    /**
     * 设置队列接收监听器
     * @param listener
     * @param queue
     */
    public void setQueueListener(QueueMsgListener listener,String... queue);

    /**
     * 设置主题接收监听器
     * @param listener
     */
    public void setTopicListener(TopicMsgListener listener);

    /**
     * 设置主题接收监听器
     * @param listener
     * @param topic
     */
    public void setTopicListener(TopicMsgListener listener,String... topic);

    /**
     * 设置MQ状态监听器
     * @param listener
     */
    public void setMQStateListener(MQStateListener listener);

    /**
     * 是否连接MQ
     * @return
     */
    public boolean isConnected() throws Exception;

    /**
     * 获取队列异步接收队列
     * */
    public List<AsyncReceiver> getAsyncQReceivers();

    /**
     * 获取会话异步接收队列
     * */
    public List<AsyncReceiver> getAsyncTReceivers();

    /**
     * 发送队列消息
     * @param queue
     * @param message
     * @throws Exception
     */
    public void sendQueue(String queue,Message message) throws Exception;

    /**
     * 接收消息队列
     * @param queue
     * @return
     * @throws Exception
     */
    public Message receiveQueue(String queue) throws Exception;

    /**
     * 发送主题消息
     * @param topic
     * @param message
     * throws Exception
     * */
    public void sendTopic(String topic,Message message) throws Exception;

    /**
     * 接收主题消息
     * @param topic
     * @throws Exception
     */
    public Message receiveTopic(String topic) throws Exception;

    /**
     * 启动异步处理
     */
    public abstract void startAsync();

    /**
     * 关闭异步处理
     */
    public abstract void stopAsync();
}
