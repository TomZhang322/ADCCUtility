package com.adcc.utility.mq.entity;

/**
 * 消息队列状态(已连接,未连接,连接中)
 */
public enum MQState {
    CONNECTED,
    DISCONNECTED,
    CONNECTING,
}
