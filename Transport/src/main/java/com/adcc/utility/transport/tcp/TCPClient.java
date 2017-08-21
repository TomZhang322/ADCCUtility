package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.*;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by zf on 2017/7/25.
 */
public class TCPClient extends TranClient {

    //拆包粘包边界符
    private String separator;

    //连续重连次数
    private int attempts;

    //最大连续重连数
    private int maxAttempts = 6;

    //连续重连间隔时间(单位：毫秒)
    private long reconnectTime = 5000;

    //群组
    private EventLoopGroup workGroup;
    private Bootstrap bootstrap;
    private ChannelFuture channelFuture;
    private Channel channel;
    private TranState state = TranState.DISCONNECTED;

    public TCPClient(String host, int port) {
        super(host, port);
    }

    public TCPClient(String host, String localHost, int port) {
        super(host, localHost, port);
    }

    public TCPClient(String host, String localHost, int port, int group) {
        super(host, localHost, port, group);
    }

    @Override
    protected Optional init() {

        final TCPClient t = this;
        workGroup = new NioEventLoopGroup(group);
        bootstrap = new Bootstrap();
        bootstrap.group(workGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        if(channelHandlers != null){
                            sc.pipeline().addLast(channelHandlers);
                        }
                        if(configuration.getReadIdleTime() > 0 || configuration.getWriteIdleTime() > 0 || configuration.getAllIdleTime() > 0){
                            sc.pipeline().addLast(new IdleStateHandler(configuration.getReadIdleTime(), configuration.getWriteIdleTime(), configuration.getAllIdleTime(), TimeUnit.SECONDS));
                        }
                        if(!Strings.isNullOrEmpty(separator)){
                            sc.pipeline().addLast(new BinaryToHexDecode());
                            sc.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.copiedBuffer(separator.getBytes())));
                            sc.pipeline().addLast(new HexToBinaryEncode(separator));
                        }
                        sc.pipeline().addLast(new TCPClientChannelHandler(t));
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
        init();
        doConnect();
    }

    public void doConnect(){
        try{
            channelFuture = bootstrap.connect(host, port);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture cf) throws Exception {
                    if (!cf.isSuccess()) {
                        notifyState(TranState.DISCONNECTED);
                        Log.info(TCPClient.class.getName(),"连接失败");
                        if(attempts < maxAttempts){
                            attempts++;
                            cf.channel().eventLoop().schedule(new Runnable() {
                                @Override
                                public void run() {
                                    Log.info(TCPClient.class.getName(),"开始第" + attempts + "次重连");
                                    doConnect();
                                }
                            },reconnectTime,TimeUnit.MILLISECONDS);
                        }else{
                            Log.info(TCPClient.class.getName(),"服务器（" + host + "）拒绝连接，客户端关闭");
                            stop();
                        }
                    }else{
                        notifyState(TranState.CONNECTED);
                        Log.info(TCPClient.class.getName(),"连接成功");
                    }
                }
            });
            channel = channelFuture.sync().channel();
            channel.closeFuture().sync();
        }catch (Exception e){
            Log.error(TCPClient.class.getName(),"doConnect()", e);
            stop();
        }
    }

    @Override
    public void start() {
        new Thread(this, TCPClient.class.getSimpleName()).start();
    }

    @Override
    public void stop(){
        try{
            if(channelFuture != null && !channelFuture.isDone() && !channelFuture.isCancelled()){
                channelFuture.channel().closeFuture().sync();
            }
            if(workGroup != null && !workGroup.isShutdown()){
                workGroup.shutdownGracefully();
            }
        }catch (Exception e){
            Log.error("stop()", e);
        }
    }

    public void send(DataPacket dataPacket){
        channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(dataPacket.getData()));
    }

    public int getAttempts() {
        return attempts;
    }

    public void attemptsaIncr() {
        this.attempts++;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getReconnectTime() {
        return reconnectTime;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public void setReconnectTime(long reconnectTime) {
        this.reconnectTime = reconnectTime;
    }

    /**
     * 通知变更状态
     * @param state
     */
    public void notifyState(TranState state){
        if(this.state != state){
            this.state = state;
            if(stateListener != null){
                stateListener.onState(state);
            }
        }
    }

}
