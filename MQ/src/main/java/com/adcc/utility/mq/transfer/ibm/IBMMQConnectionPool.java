package com.adcc.utility.mq.transfer.ibm;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.ibm.IBMMQConfiguration;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.google.common.base.Optional;
import com.ibm.mq.*;

/**
 * IBMMQ连接池
 */
public class IBMMQConnectionPool implements MQConnectionPool{

    // 池模式
    private int activeMode = MQSimpleConnectionManager.MODE_ACTIVE;

    // 超时时间
    private long timeout = 3000;

    // 最大连接数
    private int maxConnections = 1024;

    // 空闲连接数
    private int maxIdelConnections = 500;

    // ConnectionManager
    private MQSimpleConnectionManager connectionManager;

    // IBMMQConfiguration
    private IBMMQConfiguration configuration;

    public void setActiveMode(int activeMode) {
        this.activeMode = activeMode;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public void setMaxIdelConnections(int maxIdelConnections) {
        this.maxIdelConnections = maxIdelConnections;
    }

    @Override
    public void init(MQConfiguration configuration) {
        try {
            if (configuration == null) {
                throw new NullPointerException("configuration is null");
            }
            this.configuration = (IBMMQConfiguration)configuration;

            connectionManager = new MQSimpleConnectionManager();
            if(activeMode == MQSimpleConnectionManager.MODE_AUTO){
                connectionManager.setActive(MQSimpleConnectionManager.MODE_AUTO);
            } else if(activeMode == MQSimpleConnectionManager.MODE_ACTIVE) {
                connectionManager.setActive(MQSimpleConnectionManager.MODE_ACTIVE);
            } else if(activeMode == MQSimpleConnectionManager.MODE_INACTIVE) {
                connectionManager.setActive(MQSimpleConnectionManager.MODE_INACTIVE);
            } else{
                connectionManager.setActive(MQSimpleConnectionManager.MODE_ACTIVE);
            }
            connectionManager.setTimeout(timeout);
            connectionManager.setMaxConnections(maxConnections);
            connectionManager.setMaxUnusedConnections(maxIdelConnections);
            MQEnvironment.setDefaultConnectionManager(connectionManager);

        } catch (Exception ex) {
            Log.error(IBMMQConnectionPool.class.getName(), "init() error", ex);
        }
    }

    @Override
    public Optional<MQQueueManager> getConnection() {
        try {
            if (configuration == null) {
                throw new NullPointerException("configuration is null");
            }
            MQQueueManager mqm = new MQQueueManager(configuration.getQueueManager(), configuration.getParams(), connectionManager);
            return Optional.of(mqm);
        } catch (Exception ex) {
            Log.error(IBMMQConnectionPool.class.getName(), "getConnection() error", ex);
            return Optional.absent();
        }
    }

    @Override
    public void returnConnection(Optional<?> optional) {
        try {
            if (optional.isPresent()) {
                MQQueueManager mqm = (MQQueueManager) optional.get();
                if (mqm.isConnected()) {
                    mqm.disconnect();
                }
                if (mqm.isOpen()) {
                    mqm.close();
                }
            }
        } catch (Exception ex) {
            Log.error(IBMMQConnectionPool.class.getName(), "returnConnection() error", ex);
        }
    }

    @Override
    public void dispose() {
    }
}
