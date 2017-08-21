package com.adcc.utility.mq.manager;

import com.adcc.utility.mq.entity.Queue;
import com.adcc.utility.mq.entity.Topic;

import java.util.List;

/**
 * MQ管理接口
 */
public interface MQManager<T1 extends Queue,T2 extends Topic> {

    /**
     * 创建队列
     * @param queue
     * @throws Exception
     */
    public abstract void createQueue(T1 queue) throws Exception;

    /**
     * 删除队列
     * @param queue
     */
    public abstract void removeQueue(T1 queue) throws Exception;

    /**
     * 更新队列
     * @param queue
     * @throws Exception
     */
    public abstract void updateQueue(T1 queue) throws Exception;

    /**
     * 查询队列
     * @return
     * @throws Exception
     */
    public abstract List<T1> findQueue(String name) throws Exception;

    /**
     * 查询所有队列
     * @throws Exception
     */
    public abstract List<T1> findAllQueue() throws Exception;

    /**
     * 清空队列
     * @throws Exception
     */
    public abstract void clearQueue(String name) throws Exception;

    /**
     * 清空队列
     * @throws Exception
     */
    public abstract void clearAllQueue() throws Exception;

    /**
     * 创建主题
     * @param topic
     * @throws Exception
     */
    public abstract void createTopic(T2 topic) throws Exception;

    /**
     * 删除主题
     * @param topic
     * @throws Exception
     */
    public abstract void removeTopic(T2 topic) throws Exception;

    /**
     * 更新主题
     * @param topic
     * @throws Exception
     */
    public abstract void updateTopic(T2 topic) throws Exception;

    /**
     * 查询主题
     * @param name
     * @return
     * @throws Exception
     */
    public abstract List<T2> findTopic(String name) throws Exception;

    /**
     * 查询所有主题
     * @return
     * @throws Exception
     */
    public abstract List<T2> findAllTopic() throws Exception;
}
