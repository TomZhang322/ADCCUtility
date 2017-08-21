package com.adcc.utility.mq.entity.ibm;

import com.adcc.utility.mq.entity.Queue;
import com.ibm.mq.constants.CMQC;

/**
 * 队列实体
 */
public abstract class IBMQueue extends Queue {

    /**
     * 本地队列
     */
    public static final int CONSTANT_LOCAL = CMQC.MQQT_LOCAL;

    /**
     * 别名队列
     */
    public static final int CONSTANT_ALIAS = CMQC.MQQT_ALIAS;

    /**
     * 模型队列
     */
    public static final int CONSTANT_MODEL = CMQC.MQQT_MODEL;

    /**
     * 远程队列
     */
    public static final int CONSTANT_REMOTE = CMQC.MQQT_REMOTE;

    // 类型
    protected int type = -1;

    // 优先级
    protected int priority = 0;

    // 持久化
    protected boolean persistent = false;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }
}
