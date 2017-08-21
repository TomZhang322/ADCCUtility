package com.adcc.utility.transport;

import com.google.common.base.Optional;
import io.netty.channel.ChannelHandler;

/**
 * Server基类
 */
public abstract class TranServer implements Runnable{

    // 主机名称
    protected String host;

    // 端口
    protected int port;

    // 群组数
    protected int group;

    // 状态
    protected TranState state = TranState.DISCONNECTED;

    // 网络参数配置
    protected TranConfiguration configuration = new TranConfiguration();

    // DataListener
    protected DataListener dataListener;

    // StateListener
    protected StateListener stateListener;

    protected HeartBeatListener hearBeatListener;

    protected ChannelHandler[] channelHandlers;

    /**
     * 构造函数
     */
    public TranServer(){

    }

    /**
     * 构造函数
     */
    public TranServer(int port) {
        this.port = port;
    }

    /**
     * 构造函数
     * @param host
     * @param port
     */
    public TranServer(String host, int port){
        this.host = host;
        this.port = port;
    }

    public TranServer(int port, int group) {
        this.port = port;
        this.group = group;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public int getGroup(){
        return group;
    }

    public void setGroup(int group){
        this.group = group;
    }

    public TranState getState() {
        return state;
    }

    public TranConfiguration getConfiguration(){
        return configuration;
    }

    public void setDataListener(DataListener listener){
        this.dataListener = listener;
    }

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setHearBeatListener(HeartBeatListener hearBeatListener) {
        this.hearBeatListener = hearBeatListener;
    }

    public HeartBeatListener getHearBeatListener() {
        return hearBeatListener;
    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    public StateListener getStateListener() {
        return stateListener;
    }

    public void setChannelHandlers(ChannelHandler[] channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    protected abstract Optional init();

    public abstract void start();

    public abstract void stop();
}
