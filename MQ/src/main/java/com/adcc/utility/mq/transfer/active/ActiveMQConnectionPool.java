package com.adcc.utility.mq.transfer.active;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.active.ActiveMQConfiguration;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;

/**
 * ActiveMQ连接池
 */
public class ActiveMQConnectionPool implements MQConnectionPool {

    // 最大连接数
    private int maxConnections = 300;

    // 最大使用连接数
    private int maxActive = 100;

    // 空闲超时时间
    private int idleTimeout = 2000;

    // 过期超时时间
    private int expiryTimeout = 2000;

    // ActiveMQ连接池工厂
    private PooledConnectionFactory pcf;

    /**
     * 构造函数
     */
    public ActiveMQConnectionPool(){

    }

    public void setMaxConnections(int maxConnections){
        this.maxConnections = maxConnections;
    }

    public void setMaxActive(int maxActive){
        this.maxActive = maxActive;
    }

    public void setIdleTimeout(int idleTimeout){
        this.idleTimeout = idleTimeout;
    }

    public void setExpiryTimeout(int expiryTimeout){
        this.expiryTimeout = expiryTimeout;
    }

    @Override
    public void init(MQConfiguration configuration){
        try{
            ActiveMQConnectionFactory amf = null;
            if(configuration == null){
                new NullPointerException("configuration is null");
            }
            ActiveMQConfiguration amc = (ActiveMQConfiguration)configuration;
            if(!Strings.isNullOrEmpty(amc.getUserName()) && !Strings.isNullOrEmpty(amc.getPassword())){
                amf = new ActiveMQConnectionFactory(amc.getUserName(),amc.getPassword(),amc.getUrl());
            }else{
                amf = new ActiveMQConnectionFactory(amc.getUrl());
            }
            pcf = new PooledConnectionFactory(amf);
            pcf.setMaxConnections(maxConnections);
            pcf.setMaximumActiveSessionPerConnection(maxActive);
            pcf.setIdleTimeout(idleTimeout);
            pcf.setExpiryTimeout(expiryTimeout);
            pcf.setCreateConnectionOnStartup(true);
            if(pcf.isCreateConnectionOnStartup()){
                pcf.start();
            }
        }catch (Exception ex){
            Log.error(ActiveMQConnectionPool.class.getName(),"init() error",ex);
        }
    }

    @Override
    public Optional<Connection> getConnection() {
        try{
            ActiveMQConnectionFactory acf = (ActiveMQConnectionFactory)pcf.getConnectionFactory();
            Connection connection = acf.createConnection();
            return Optional.of(connection);
        }catch (Exception ex){
            Log.error(ActiveMQConnectionPool.class.getName(),"getConnection() error",ex);
            return Optional.absent();
        }
    }

    @Override
    public void returnConnection(Optional<?> optional) {
        try{
            if(optional.isPresent()){
                Connection connection = (ActiveMQConnection)optional.get();
                connection.close();
            }
        }catch (Exception ex){
            Log.error(ActiveMQConnectionPool.class.getName(),"returnConnection（）error",ex);
        }
    }

    @Override
    public void dispose(){
        try{
            if(pcf != null){
                pcf.stop();
            }
        }catch (Exception ex){
            Log.error(ActiveMQConnectionPool.class.getName(),"dispose() error",ex);
        }
    }
}
