package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.entity.Message;

/**
 * 异步消息接收监听器
 */
public interface AsyncMsgListener {

    /**
     * 消息响应接收事件
     * @param name
     * @param message
     * @param type
     */
    public void onMessage(String name,Message message,int type);
}
