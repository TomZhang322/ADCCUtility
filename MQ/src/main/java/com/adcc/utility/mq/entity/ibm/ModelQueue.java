package com.adcc.utility.mq.entity.ibm;

/**
 * 模型队列
 */
public class ModelQueue extends IBMQueue{

    /**
     * 构造函数
     */
    public ModelQueue(){
        type = IBMQueue.CONSTANT_MODEL;
    }
}
