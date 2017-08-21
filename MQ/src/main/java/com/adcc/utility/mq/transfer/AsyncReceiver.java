package com.adcc.utility.mq.transfer;

/**
 * MQ异步接收
 */
public interface AsyncReceiver {

    /**
     * 设置异步消息接收监听器
     * @param listener
     */
    public abstract void setMsgListener(AsyncMsgListener listener);

    /**
     * 取得MQ异步接收名称
     * @return
     */
    public abstract String getName();

    /**
     * 启动异步接收
     */
    public abstract void start();

    /**
     * 停止异步接收
     */
    public abstract void stop();
}
