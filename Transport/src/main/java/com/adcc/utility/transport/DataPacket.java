package com.adcc.utility.transport;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * Transport数据包
 */
public class DataPacket implements Serializable{

    // Channel ID
    private String channelID;

    // 本地地址
    private String localAddress;

    // 远程地址
    private String remoteAddress;

    // 接收/发送数据
    private byte[] data;

    /**
     * 构造函数
     */
    public DataPacket(){

    }

    /**
     * 构造函数
     * @param data
     */
    public DataPacket(byte[] data){
        this.data = data;
    }

    /**
     * 构造函数
     * @param channelID
     * @param data
     */
    public DataPacket(String channelID,byte[] data){
        this.channelID = channelID;
        this.data = data;
    }

    public String getChannelID() {
        return channelID;
    }

    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }

    public String getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(String localAddress) {
        this.localAddress = localAddress;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public void setData(byte[] data){
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public String toString() {
        if(data != null && data.length > 0){
            return new String(data,0,data.length, Charset.forName("utf-8"));
        }else{
            return "";
        }
    }
}
