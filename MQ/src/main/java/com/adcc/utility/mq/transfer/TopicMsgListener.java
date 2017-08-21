package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.entity.Message;

import java.util.Map;

/**
 * 队列消息接收监听器
 */
public interface TopicMsgListener {

    /**
     * 主题接收消息监听事件
     * @param name
     * @param message
     * @param info
     */
    public void onTopicMsg(String name,Message message,Map<String,String> info);
}
