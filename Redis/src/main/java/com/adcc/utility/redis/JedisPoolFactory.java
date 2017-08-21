package com.adcc.utility.redis;

import com.google.common.base.Optional;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by ZHANG on 2016/8/31.
 */
public class JedisPoolFactory {

    // 单例对象
    private static JedisPoolFactory instance;

    // 主机地址
    private String host;

    // 端口
    private int port;

    // 超时时间
    private int timeout;

    // 最大分配对象
    private int maxActive;

    // 最大能够保持空闲状态的对象数
    private int maxIdle;

    // 当池内没有返回对象时,最大等待时间
    private long maxWait;

    // 当调用borrow方法时,是否进行有效性检查
    private boolean testOnBorrow;

    // 当调用return方法时,是否进行有效性检查
    private boolean testOnReturn;

    // 当使用空闲的对象时,是否进行有效性检查
    private boolean testWhileIdle;

    /**
     * 构造函数
     */
    private JedisPoolFactory(){

    }

    /**
     * 单例方法
     * @return
     */
    public static JedisPoolFactory getInstance(){
        if(instance == null){
            instance = new JedisPoolFactory();
        }
        return instance;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    /**
     * 创建连接池
     * @return
     */
    public Optional<JedisPool> createRedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(this.maxActive);
        jedisPoolConfig.setMaxWaitMillis(this.maxWait);
        jedisPoolConfig.setTestOnBorrow(this.testOnBorrow);
        jedisPoolConfig.setTestOnReturn(this.testOnReturn);
        jedisPoolConfig.setTestWhileIdle(this.testWhileIdle);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig,host,port,timeout);
        return Optional.of(jedisPool);
    }

    /**
     * 销毁连接池
     * @param jedisPool
     */
    public void destroyRedisPool(JedisPool jedisPool){
        jedisPool.close();
        jedisPool.destroy();
    }
}
