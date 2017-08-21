package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.*;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zf on 2017/7/25.
 */
public class TCPServer extends TranServer {

    //服务端接收连接队列长度
    private int backLog;

    //连接保活
    private boolean keepAlive;

    //是否立即发送数据
    private boolean noDelay;

    private ChannelManager channelManager = new ChannelManager();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;
    private ChannelFuture channelFuture;

    public TCPServer(int port) {
        super(port);
    }

    public TCPServer(int port, int group) {
        super(port, group);
    }

    @Override
    protected Optional init() {

        final TCPServer t = this;
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup(group);
        workGroup = new NioEventLoopGroup(group);
        bootstrap.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_BACKLOG,backLog)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childOption(ChannelOption.TCP_NODELAY,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        if(channelHandlers != null){
                            sc.pipeline().addLast(channelHandlers);
                        }
                        if(configuration.getReadIdleTime() > 0 || configuration.getWriteIdleTime() > 0 || configuration.getAllIdleTime() > 0){
                            sc.pipeline().addLast(new IdleStateHandler(configuration.getReadIdleTime(), configuration.getWriteIdleTime(), configuration.getAllIdleTime(), TimeUnit.SECONDS));
                        }
                        sc.pipeline().addLast(new TCPServerChannelHandler(t));
                    }
                });
        if(configuration.getParameters().size() > 0){
            for(ChannelOption option : configuration.getParameters().keySet()){
                bootstrap.option(option,configuration.getParameters().get(option));
            }
        }
        return Optional.of(bootstrap);
    }

    @Override
    public void run(){
        try{
            Optional<ServerBootstrap> optional = init();
            if(optional.isPresent()){
                channelFuture = optional.get().bind(port).sync();
                if(stateListener != null){
                    stateListener.onState(TranState.CONNECTED);
                }
                channelFuture.channel().closeFuture().sync();
            }
        }catch (Exception e){
            Log.error(TCPServer.class.getName(),"start()", e);
            if(stateListener != null) {
                stateListener.onState(TranState.DISCONNECTED);
            }
        }
    }

    @Override
    public void start() {
        new Thread(this, TCPServer.class.getSimpleName()).start();
    }

    @Override
    public void stop(){
        try{
            if(bossGroup != null && !bossGroup.isShutdown()){
                bossGroup.shutdownGracefully();
            }
            if(workGroup != null && !workGroup.isShutdown()){
                workGroup.shutdownGracefully();
            }
        }catch (Exception e){
            Log.error(TCPServer.class.getName(),"stop()", e);
        }
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public void setNoDelay(boolean noDelay) {
        this.noDelay = noDelay;
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    /**
     * 发送信息
     * @param channelId
     * @param dataPacket
     */
    public void send(String channelId,DataPacket dataPacket){

        try{
            Channel channel = channelManager.getChannel(channelId);
            channel.writeAndFlush(Unpooled.copiedBuffer(dataPacket.getData()));
        }catch (Exception e){
            Log.error(TCPServer.class.getName(),"send()",e);
        }
    }

    /**
     * 向所有客户端发送信息
     * @param
     * @param dataPacket
     */
    public void send(DataPacket dataPacket){

        try{
            HashMap<String,Channel> map = channelManager.getChannels();
            for(Map.Entry<String,Channel> entry: map.entrySet()){
                entry.getValue().writeAndFlush(Unpooled.copiedBuffer(dataPacket.getData()));
            }
        }catch (Exception e){
            Log.error(TCPServer.class.getName(),"send()",e);
        }
    }
}
