package com.adcc.utility.mq.entity;

/**
 * 队列实体
 */
public abstract class Queue {

    // 队列名称
    protected String name;

    // 队列深度
    protected long depth;

    // 入队列消息数
    protected long enqueued;

    // 出队列消息数
    protected long dequeued;

    // 生产者数
    protected long producers;

    // 消费者数
    protected long consumers;

    /**
     * 构造函数
     */
    public Queue(){

    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public long getDepth(){
        return depth;
    }

    public void setDepth(long depth){
        this.depth = depth;
    }

    public long getEnqueued(){
        return enqueued;
    }

    public void setEnqueued(long enqueued){
        this.enqueued = enqueued;
    }

    public long getDequeued(){
        return dequeued;
    }

    public void setDequeued(long dequeued){
        this.dequeued = dequeued;
    }

    public long getProducers(){
        return producers;
    }

    public void setProducers(long producers){
        this.producers = producers;
    }

    public long getConsumers(){
        return consumers;
    }

    public void setConsumers(long consumers){
        this.consumers = consumers;
    }
}
