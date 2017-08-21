package com.adcc.utility.mq.entity.ibm;

/**
 * 本地队列
 */
public class LocalQueue extends IBMQueue{

    // 最大队列深度
    private int maxDepth = 5000;

    // 最大消息长度
    private int maxMsgLength = 4194304;

    // 保留时间间隔
    private int retainInterval = 999999999;

    /**
     * 构造函数
     */
    public LocalQueue(){
        super.type = IBMQueue.CONSTANT_LOCAL;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMaxMsgLength() {
        return maxMsgLength;
    }

    public void setMaxMsgLength(int maxMsgLength) {
        this.maxMsgLength = maxMsgLength;
    }

    public int getRetainInterval() {
        return retainInterval;
    }

    public void setRetainInterval(int retainInterval) {
        this.retainInterval = retainInterval;
    }
}
