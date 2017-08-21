package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.transfer.active.ActiveMQConnectionPool;
import com.adcc.utility.mq.transfer.ibm.IBMMQConnectionPool;

/**
 * MQ连接池工厂
 */
public class MQConnectionPoolFactory extends ConnectionPoolFactory{

    // 单例方法
    private static MQConnectionPoolFactory instance = null;

    /**
     * 构造函数
     */
    private MQConnectionPoolFactory(){

    }

    /**
     * 单例方法
     * @return
     */
    public synchronized static MQConnectionPoolFactory getInstance(){
        if(instance == null){
            instance = new MQConnectionPoolFactory();
        }
        return instance;
    }

    @Override
    public ActiveMQConnectionPool createActiveMQConnectionPool() {
        ActiveMQConnectionPool connectionPool = new ActiveMQConnectionPool();
        return connectionPool;
    }

    @Override
    public MQConnectionPool createActiveMQConnectionPool(MQConfiguration configuration) {
        ActiveMQConnectionPool connectionPool = new ActiveMQConnectionPool();
        connectionPool.init(configuration);
        return connectionPool;
    }

    @Override
    public IBMMQConnectionPool createIBMMQConnectionPool() {
        IBMMQConnectionPool connectionPool = new IBMMQConnectionPool();
        return connectionPool;
    }

    @Override
    public MQConnectionPool createIBMMQConnectionPool(MQConfiguration configuration) {
        IBMMQConnectionPool connectionPool = new IBMMQConnectionPool();
        connectionPool.init(configuration);
        return connectionPool;
    }
}
