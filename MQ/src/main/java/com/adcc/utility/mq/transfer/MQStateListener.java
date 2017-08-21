package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.entity.MQState;

import java.util.Map;

/**
 * MQ状态监听器
 */
public interface MQStateListener {

    /**
     * 消息接收响应事件
     * @param state
     */
    public void onState(MQState state,Map<String,String> info);
}
