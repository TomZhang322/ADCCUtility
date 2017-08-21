package com.adcc.utility.redis;

/**
 * Created by ZHANG on 2016/8/31.
 */
public class JedisConfiguration {
    // 主机名称
    private String host;

    // 端口
    private int port;

    // 超时时间
    private int timeout;

    // 最大分配的对象数
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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }
}
