package com.adcc.utility.mq.entity.ibm;

import com.adcc.utility.mq.entity.Topic;
import com.ibm.mq.constants.CMQC;

/**
 * 会话实体
 */
public class IBMTopic extends Topic {

    /**
     * 本地主题
     */
    public static final int CONSTANT_LOCAL = CMQC.MQTOPT_LOCAL;

    /**
     * 所有主题
     */
    public static final int CONSTANT_ALL = CMQC.MQTOPT_ALL;

    /**
     * 集群主题
     */
    public static final int CONSTANT_CLUSTER = CMQC.MQTOPT_CLUSTER;

    // 类型
    private int type = CONSTANT_LOCAL;

    // 优先级
    private int priority = CMQC.MQPRI_PRIORITY_AS_PARENT;

    // 持久化
    private boolean persistent = false;

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

    @Override
    public String toString() {
        return "IBMTopic{" +
                "name=" + name +
                ", type=" + type +
                ", enqueued=" + enqueued +
                ", dequeued=" + dequeued +
                ", producers=" + producers +
                ", consumers=" + consumers +
                ", priority=" + priority +
                ", persistent=" + persistent +
                '}';
    }
}
