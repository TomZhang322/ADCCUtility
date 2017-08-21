package com.adcc.utility.transport;

/**
 * 数据接收监听器
 */
public interface DataListener {

    /**
     * 数据接收事件
     * @param dataPacket
     */
    public void onData(DataPacket dataPacket);
}
