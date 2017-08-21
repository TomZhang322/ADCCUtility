package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.configuration.MQConfiguration;

import javax.security.auth.login.Configuration;

/**
 * 连接池抽象工厂
 */
public abstract class ConnectionPoolFactory<T extends MQConnectionPool> {

    /**
     * 创建ActiveMQ连接池
     * @return
     */
    public abstract T createActiveMQConnectionPool();

    /**
     * 创建ActiveMQ连接池
     * @param configuration
     * @return
     */
    public abstract T createActiveMQConnectionPool(MQConfiguration configuration);

    /**
     * 创建IBMMQ连接池
     * @return
     */
    public abstract T createIBMMQConnectionPool();

    /**
     * 创建IBMMQ连接池
     * @param configuration
     * @return
     */
    public abstract T createIBMMQConnectionPool(MQConfiguration configuration);
}
