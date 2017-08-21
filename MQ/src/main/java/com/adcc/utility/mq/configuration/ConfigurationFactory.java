package com.adcc.utility.mq.configuration;

/**
 * 抽象配置工厂
 */
public abstract class ConfigurationFactory<T extends MQConfiguration> {

    /**
     * 创建ActiveMQ连接配置
     * @return
     */
    public abstract T createActiveMQConfiguration(String url);

    /**
     * 创建ActiveMQ连接配置
     * @param userName
     * @param password
     * @param url
     * @return
     */
    public abstract T createActiveMQConfiguration(String userName,String password,String url);

    /**
     * 创建IBMMQ连接配置
     * @param host
     * @param port
     * @param channel
     * @return
     */
    public abstract T createIBMMQConfiguration(String host,int port,String queueManager,String channel);
}
