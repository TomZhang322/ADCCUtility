package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.google.common.base.Optional;

/**
 * 连接池接口
 */
public interface MQConnectionPool {

    /**
     * 初始化MQ连接池
     */
    public void init(MQConfiguration configuration);

    /**
     * 获取MQ连接
     * @return
     */
    public Optional<?> getConnection();

    /**
     * 归还MQ连接
     */
    public void returnConnection(Optional<?> optional);

    /**
     * 销毁MQ连接池
     */
    public void dispose();
}
