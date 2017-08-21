package com.adcc.utility.mq.entity.ibm;

/**
 * 远程队列
 */
public class RemoteQueue extends IBMQueue{

    /**
     * 构造函数
     */
    public RemoteQueue(){
        type = IBMQueue.CONSTANT_REMOTE;
    }
}
