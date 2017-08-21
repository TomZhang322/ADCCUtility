package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.entity.Message;

import java.util.Map;

/**
 * 队列消息接收监听器
 */
public interface QueueMsgListener {

    /**
     * 队列接收消息监听事件
     * @param name
     * @param message
     * @param info
     */
    public void onQueueMsg(String name,Message message,Map<String,String> info);
}
