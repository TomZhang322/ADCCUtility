package com.adcc.utility.mq.entity.ibm;

/**
 * 别名队列
 */
public class AliasQueue extends IBMQueue{

    /**
     * 构造函数
     */
    public AliasQueue(){
        type = IBMQueue.CONSTANT_ALIAS;
    }
}
