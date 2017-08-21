package com.adcc.utility.transport;

import com.google.common.base.Optional;
import io.netty.channel.ChannelHandler;

/**
 * 基类Server
 */
public abstract class TranClient implements Runnable{

    // 主机名称
    protected String host;

    // 本地主机名称
    protected String localHost;

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

    protected HeartBeatListener hearBeatListener;

    protected StateListener stateListener;

    protected ChannelHandler[] channelHandlers;

    /**
     * 构造函数
     */
    public TranClient(){

    }

    /**
     * 构造函数
     */
    public TranClient(int port){
        this.port = port;
    }

    /**
     * 构造函数
     * @param host
     * @param port
     */
    public TranClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 构造函数
     * @param host
     * @param localHost
     * @param port
     */
    public TranClient(String host, String localHost, int port) {
        this.host = host;
        this.localHost = localHost;
        this.port = port;
    }

    /**
     * 构造函数
     * @param host
     * @param localHost
     * @param port
     * @param group
     */
    public TranClient(String host, String localHost, int port, int group){
        this.host = host;
        this.localHost = localHost;
        this.port = port;
        this.group = group;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public String getLocalHost() {
        return localHost;
    }

    public void setLocalHost(String localHost){
        this.localHost = localHost;
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

    public TranConfiguration getConfiguration() {
        return configuration;
    }

    public void setChannelHandlers(ChannelHandler[] channelHandlers) {
        this.channelHandlers = channelHandlers;
    }

    public DataListener getDataListener() {
        return dataListener;
    }

    public void setDataListener(DataListener dataListener) {
        this.dataListener = dataListener;
    }

    public HeartBeatListener getHearBeatListener() {
        return hearBeatListener;
    }

    public void setHearBeatListener(HeartBeatListener hearBeatListener) {
        this.hearBeatListener = hearBeatListener;
    }

    public StateListener getStateListener() {
        return stateListener;
    }

    public void setStateListener(StateListener stateListener) {
        this.stateListener = stateListener;
    }

    /**
     * 启动Server
     */
    public abstract void start();

    /**
     * 关闭Server
     */
    public abstract void stop();

    protected abstract Optional init();
}
