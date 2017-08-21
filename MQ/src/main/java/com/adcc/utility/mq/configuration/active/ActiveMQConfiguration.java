package com.adcc.utility.mq.configuration.active;

import com.adcc.utility.mq.configuration.MQConfiguration;

/**
 * Active MQ配置类
 */
public class ActiveMQConfiguration implements MQConfiguration{

    // 用户名
    private String userName;

    // 密码
    private String password;

    // URL
    private String url;

    /**
     * 构造函数
     */
    public ActiveMQConfiguration(){

    }

    /**
     * 构造函数
     * @param url
     */
    public ActiveMQConfiguration(String url){
        this.url = url;
    }

    /**
     * 构造函数
     * @param hostName
     * @param password
     * @param url
     */
    public ActiveMQConfiguration(String hostName,String password,String url){
        this.userName = hostName;
        this.password = password;
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
