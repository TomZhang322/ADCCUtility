package com.adcc.utility.transport;

import com.google.common.collect.Maps;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;

import java.util.Map;

/**
 * 网络参数配置类
 */
public class TranConfiguration {

    // ByteBuf缓冲区
    private ByteBufAllocator allocator = ByteBufAllocator.DEFAULT;

    // 发送缓冲区
    private Integer sndBuf = 1024;

    // 接收缓冲区
    private Integer rcvBuf = 1024;

    //读空闲
    private long readIdleTime
            ;
    //写空闲
    private long writeIdleTime;

    //读写空闲
    private long allIdleTime;

    // 配置参数
    private Map<ChannelOption,Object> parameters = Maps.newHashMapWithExpectedSize(28);

    /**
     * 构造函数
     */
    public TranConfiguration(){
        parameters.put(ChannelOption.ALLOCATOR,allocator);
//        parameters.put(ChannelOption.SO_SNDBUF, sndBuf);
        parameters.put(ChannelOption.SO_RCVBUF,rcvBuf);
    }

    public void setAllocator(ByteBufAllocator allocator){
        this.allocator = allocator;
        parameters.put(ChannelOption.ALLOCATOR,allocator);
    }

    public void setSndBuf(Integer sndBuf){
        this.sndBuf = sndBuf;
        parameters.put(ChannelOption.SO_SNDBUF,sndBuf);
    }

    public void setRcvBuf(Integer rcvBuf){
        this.rcvBuf = rcvBuf;
        parameters.put(ChannelOption.SO_RCVBUF,rcvBuf);
    }

    public long getReadIdleTime() {
        return readIdleTime;
    }

    public void setReadIdleTime(long readIdleTime) {
        this.readIdleTime = readIdleTime;
    }

    public long getWriteIdleTime() {
        return writeIdleTime;
    }

    public void setWriteIdleTime(long writeIdleTime) {
        this.writeIdleTime = writeIdleTime;
    }

    public long getAllIdleTime() {
        return allIdleTime;
    }

    public void setAllIdleTime(long allIdleTime) {
        this.allIdleTime = allIdleTime;
    }

    public final Map<ChannelOption, Object> getParameters() {
        return parameters;
    }

    /**
     * 添加参数
     * @param option
     * @param obj
     */
    public void addParameter(ChannelOption option,Object obj){
        parameters.put(option,obj);
    }
}
