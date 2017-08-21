package com.adcc.utility.mq.configuration;

import com.adcc.utility.mq.configuration.active.ActiveMQConfiguration;
import com.adcc.utility.mq.configuration.ibm.IBMMQConfiguration;

/**
 * 配置工厂
 */
public class MQConfigurationFactory extends ConfigurationFactory{

    // 单例方法
    private static MQConfigurationFactory instance = null;

    /**
     * 构造函数
     */
    private MQConfigurationFactory(){

    }

    /**
     * 单例方法
     * @return
     */
    public synchronized static ConfigurationFactory getInstance(){
        if(instance == null){
            instance = new MQConfigurationFactory();
        }
        return instance;
    }

    @Override
    public ActiveMQConfiguration createActiveMQConfiguration(String url) {
        ActiveMQConfiguration configuration = new ActiveMQConfiguration(url);
        return configuration;
    }

    @Override
    public ActiveMQConfiguration createActiveMQConfiguration(String userName, String password, String url) {
        ActiveMQConfiguration configuration = new ActiveMQConfiguration(userName,password,url);
        return configuration;
    }

    @Override
    public IBMMQConfiguration createIBMMQConfiguration(String host, int port,String queueManager,String channel) {
        IBMMQConfiguration configuration = new IBMMQConfiguration(host,port,queueManager,channel);
        return configuration;
    }
}
