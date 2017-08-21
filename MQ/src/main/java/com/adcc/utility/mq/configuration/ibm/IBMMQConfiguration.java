package com.adcc.utility.mq.configuration.ibm;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.google.common.base.Strings;
import com.ibm.mq.MQC;

import java.util.Hashtable;

/**
 * IBM MQ配置类
 */
public class IBMMQConfiguration implements MQConfiguration{

    // 主机名称
    private String host;

    // 端口
    private int port;

    // 队列管理器
    private String queueManager;

    // 通道
    private String channel;

    // MQ配置信息
    private Hashtable<Object,Object> params = new Hashtable<Object, Object>();

    /**
     * 构造函数
     */
    public IBMMQConfiguration(){

    }

    /**
     * 构造函数
     * @param host
     * @param port
     * @param queueManager
     * @param channel
     */
    public IBMMQConfiguration(String host, int port, String queueManager, String channel){
        this.host = host;
        if(!Strings.isNullOrEmpty(host)){
            params.put("hostname",host);
        }
        this.port = port;
        if(port > 0 && port < 65535){
            params.put("port",port);
        }
        this.queueManager = queueManager;
        this.channel = channel;
        if(!Strings.isNullOrEmpty(channel)){
            params.put("channel",channel);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        if(!Strings.isNullOrEmpty(host)){
            params.put("hostname",host);
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        if(port > 0 && port < 65535){
            params.put("port",port);
        }
    }

    public String getQueueManager() {
        return queueManager;
    }

    public void setQueueManager(String queueManager) {
        this.queueManager = queueManager;

    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
        if(!Strings.isNullOrEmpty(channel)){
            params.put("channel",channel);
        }
    }

    public Hashtable<Object, Object> getParams() {
        return params;
    }
}
