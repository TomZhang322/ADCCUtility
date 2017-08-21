package com.adcc.utility.mq.entity.ibm;

import com.ibm.mq.constants.CMQXC;

/**
 * 通道实体
 */
public class Channel {

    /**
     * 发送方通道
     */
    public static final int CONSTANT_SENDER = CMQXC.MQCHT_SENDER;

    /**
     * 服务器方通道
     */
    public static final int CONSTANT_SERVER = CMQXC.MQCHT_SERVER;

    /**
     * 接收方通道
     */
    public static final int CONSTANT_RECEIVER = CMQXC.MQCHT_RECEIVER;

    /**
     * 请求方通道
     */
    public static final int CONSTANT_REQUESTER = CMQXC.MQCHT_REQUESTER;

    /**
     * 默认通道
     */
    public static final int CONSTANT_ALL = CMQXC.MQCHT_ALL;

    /**
     * 客户端连接通道
     */
    public static final int CONSTANT_CLINTCONN = CMQXC.MQCHT_CLNTCONN;

    /**
     * 服务器连接通道
     */
    public static final int CONSTANT_SVRCONN = CMQXC.MQCHT_SVRCONN;

    /**
     * 集群发送方通道
     */
    public static final int CONSTANT_CLUSRCVR = CMQXC.MQCHT_CLUSRCVR;

    /**
     * 集群接收方通道
     */
    public static final int CONSTANT_CLUSSDR = CMQXC.MQCHT_CLUSSDR;

    // 名称
    private String name;

    // 类型
    private int type = CONSTANT_SVRCONN;

    // 通道用户
    private String user;

    /**
     * 构造函数
     */
    public Channel() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "IBMChannel{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", user='" + user + '\'' +
                '}';
    }
}
