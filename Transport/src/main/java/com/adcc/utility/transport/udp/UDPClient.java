package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.TranClient;
import com.adcc.utility.transport.DataPacket;
import com.adcc.utility.transport.TranState;
import com.adcc.utility.transport.StateListener;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.concurrent.TimeUnit;

/**
 * UDP Client类
 */
public class UDPClient extends TranClient {

    // 事件循环群组
    private EventLoopGroup elg;

    // 目标地址
    private InetSocketAddress targetAddress;

    // 传输方式
    private Transport transport = Transport.UNICAST;

    // 组播报文跳跃数
    private int multicastTTL = 255;

    // ChannelFuture
    private ChannelFuture cf;

    /**
     * 构造函数
     * @param host
     * @param port
     */
    public UDPClient(String host, int port) {
        super(host, port);
    }

    /**
     * 构造函数
     * @param host
     * @param localHost
     * @param port
     */
    public UDPClient(String host, String localHost, int port) {
        super(host,localHost,port);
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Transport getTransport(){
        return this.transport;
    }

    public void setMulticastTTL(int multicastTTL){
        this.multicastTTL = multicastTTL;
    }

    public int getMulticastTTL(){
        return this.multicastTTL;
    }

    /**
     * 通知变更状态
     * @param state
     */
    private void notifyState(TranState state){
        if(this.state != state){
            this.state = state;
            if(stateListener != null){
                stateListener.onState(state);
            }
        }
    }

    /**
     * 创建单播
     * @param bootstrap
     * @throws Exception
     */
    private void buildUncast(Bootstrap bootstrap) throws Exception{
        try{
            if(!Strings.isNullOrEmpty(localHost)){
                cf = bootstrap.bind(localHost,0).sync();
                cf.channel().closeFuture().await();
            }else{
                cf = bootstrap.bind(0).sync();
                cf.channel().closeFuture().await();
            }
        }catch (Exception ex){
            Log.error(UDPClient.class,"buildUncast() error",ex);
            throw ex;
        }
    }

    /**
     * 创建组播
     * @param bootstrap
     * @throws Exception
     */
    private void buildMulticast(Bootstrap bootstrap) throws Exception{
        try{
            InetAddress localAddress = null;
            if(!Strings.isNullOrEmpty(localHost)){
                localAddress = InetAddress.getByName(localHost);
            }
            if(localAddress == null){
                localAddress = InetAddress.getLocalHost();
            }
            NetworkInterface ni = NetworkInterface.getByInetAddress(localAddress);
            cf = bootstrap.option(ChannelOption.IP_MULTICAST_IF, ni)
                    .option(ChannelOption.IP_MULTICAST_TTL, multicastTTL)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .localAddress(localAddress,port)
                    .bind().sync();
            cf.channel().closeFuture().await();
        }catch (Exception ex){
            Log.error(UDPClient.class,"buildMulticast() error",ex);
            throw ex;
        }
    }

    /**
     * 创建广播
     * @param bootstrap
     * @throws Exception
     */
    private void buildBroadcast(Bootstrap bootstrap) throws Exception{
        try{
            if(!Strings.isNullOrEmpty(localHost)){
                cf = bootstrap.option(ChannelOption.SO_BROADCAST,true)
                        .bind(localHost, 0).sync();
                cf.channel().closeFuture().await();
            }else{
                cf = bootstrap.option(ChannelOption.SO_BROADCAST,true)
                        .bind(0).sync();
                cf.channel().closeFuture().await();
            }
        }catch (Exception ex){
            Log.error(UDPClient.class,"buildBroadcast() error",ex);
            throw ex;
        }
    }

    @Override
    public void run() {
        try{
            Optional<Bootstrap> optional = init();
            if(optional.isPresent()){
                if(transport == Transport.MULTICAST){
                    buildMulticast(optional.get());
                }else if(transport == Transport.BROADCAST){
                    buildBroadcast(optional.get());
                }else{
                    buildUncast(optional.get());
                }
            }else {
                Log.error(UDPServer.class, "initialize UDP client failed");
            }
        }catch (Exception e){
            Log.error(UDPClient.class, "run()", e);
        }finally {
            if(elg != null){
                elg.shutdownGracefully();
            }
        }
    }

    @Override
    protected Optional init() {
        try{
            targetAddress = new InetSocketAddress(host,port);
            elg = group > 0 ? new NioEventLoopGroup(group) : new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(elg)
                    .channelFactory(new ChannelFactory<NioDatagramChannel>() {
                        @Override
                        public NioDatagramChannel newChannel() {
                            return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                        }
                    })
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel channel) throws Exception {
                            if (channelHandlers != null && channelHandlers.length > 0) {
                                channel.pipeline().addLast(channelHandlers);
                            }
                            if(configuration.getAllIdleTime() > 0 || configuration.getReadIdleTime() > 0 || configuration.getWriteIdleTime() > 0){
                                channel.pipeline().addLast(new IdleStateHandler(configuration.getReadIdleTime(),configuration.getWriteIdleTime(),configuration.getAllIdleTime(), TimeUnit.SECONDS));
                            }
                            channel.pipeline().addLast(new HeartbeatHandler(new StateListener() {
                                @Override
                                public void onState(TranState state) {
                                    notifyState(state);
                                }

                                @Override
                                public void onState(String channelID, TranState state) {
                                }

                                @Override
                                public void onState(String hostName, String channelID, TranState state) {
                                }
                            }));
                        }
                    });
            if(configuration.getParameters().size() > 0){
                for(ChannelOption option : configuration.getParameters().keySet()){
                    bootstrap.option(option,configuration.getParameters().get(option));
                }
            }
            return Optional.of(bootstrap);
        }catch (Exception ex){
            Log.error(UDPClient.class,"init() error",ex);
            return Optional.absent();
        }
    }

    /**
     * 发送数据
     * @param packet
     */
    public void send(DataPacket packet){
        try{
            if(cf != null && cf.channel().isOpen()){
                DatagramPacket dp = new DatagramPacket(Unpooled.copiedBuffer(packet.getData()),targetAddress);
                cf.channel().writeAndFlush(dp);
            }
            if(state == TranState.IDLE){
                notifyState(TranState.CONNECTED);
            }
        }catch (Exception ex){
            Log.error(UDPClient.class, "send()", ex);
        }
    }

    @Override
    public void start() {
        try{
            new Thread(this,UDPClient.class.getSimpleName()).start();
        }catch (Exception ex){
            Log.error(UDPServer.class,"start() error",ex);
        }
    }

    @Override
    public void stop() {
        try {
            if (cf != null && cf.channel().isOpen()) {
                cf.channel().close().sync();
            }
        } catch (Exception ex) {
            Log.error(UDPServer.class.getName(), "stop() error", ex);
        } finally {
            if (elg != null) {
                elg.shutdownGracefully();
            }
        }
    }
}
