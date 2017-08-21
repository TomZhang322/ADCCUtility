package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.*;
import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UDP Server类
 */
public class UDPServer extends TranServer {

    // 事件循环群组
    private EventLoopGroup elg;

    // 传输方式
    private Transport transport = Transport.UNICAST;

    // 组播地址
    private List<String> multicastAddress = Lists.newArrayListWithExpectedSize(10);

    // 禁止组播数据回送
    private boolean disabledMulticastLoop = true;

    private ChannelFuture cf;

    /**
     * 构造函数
     */
    public UDPServer() {
        super();
    }

    /**
     * 构造函数
     * @param port
     */
    public UDPServer(int port) {
        super(port);
    }

    /**
     * 构造函数
     * @param host
     * @param port
     */
    public UDPServer(String host, int port) {
        super(host, port);
    }

    public void setTransport(Transport transport){
        this.transport = transport;
    }

    public Transport getTransport(){
        return transport;
    }

    public List<String> getMulticastAddress(){
        return multicastAddress;
    }

    public boolean isDisabledMulticastLoop(){
        return disabledMulticastLoop;
    }

    public void setDisabledMulticastLoop(boolean disabledMulticastLoop){
        this.disabledMulticastLoop = disabledMulticastLoop;
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
     * 通知接收数据
     * @param packet
     */
    private void notifyData(DataPacket packet){
        if(dataListener != null){
            dataListener.onData(packet);
        }
    }

    /**
     * 创建单播
     */
    private void buildUncast(Bootstrap bootstrap) throws Exception{
        try{
            if(!Strings.isNullOrEmpty(host)){
                cf = bootstrap.bind(host, port).sync();
                cf.channel().closeFuture().await();
            }else{
                cf = bootstrap.bind(port).sync();
                cf.channel().closeFuture().await();
            }
        }catch (Exception ex){
            Log.error(UDPServer.class,"buildUncast() error",ex);
            throw ex;
        }
    }

    /**
     * 创建组播
     */
    private void buildMulticast(Bootstrap bootstrap) throws Exception{
        try{
            InetAddress localAddress = null;
            if(!Strings.isNullOrEmpty(host)){
                localAddress = InetAddress.getByName(host);
            }
            if(localAddress == null){
                localAddress = InetAddress.getLocalHost();
            }
            InetSocketAddress[] multiAddress = new InetSocketAddress[this.multicastAddress.size()];
            for(int i = 0; i < this.multicastAddress.size(); i++){
                multiAddress[i] = new InetSocketAddress(this.multicastAddress.get(i),port);
            }
            NetworkInterface ni = NetworkInterface.getByInetAddress(localAddress);
            bootstrap.option(ChannelOption.IP_MULTICAST_IF,ni)
                    .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, disabledMulticastLoop)
                    .option(ChannelOption.SO_REUSEADDR, true);
            DatagramChannel dc = (DatagramChannel) bootstrap.bind(localAddress,port).sync().channel();
            for(InetSocketAddress address : multiAddress){
                cf = dc.joinGroup(address,ni).sync();
                cf.channel().closeFuture().await();
            }
        }catch (Exception ex){
            Log.error(UDPServer.class,"buildMulticast() error",ex);
            throw ex;
        }
    }

    /**
     * 创建广播
     */
    private void buildBroadcast(Bootstrap bootstrap) throws Exception{
        try{
            if(!Strings.isNullOrEmpty(host)){
                cf = bootstrap.option(ChannelOption.SO_BROADCAST,true)
                        .bind(host,port).sync();
                cf.channel().closeFuture().await();
            }else{
                cf = bootstrap.option(ChannelOption.SO_BROADCAST,true)
                        .bind(port).sync();
                cf.channel().closeFuture().await();
            }
        }catch (Exception ex){
            Log.error(UDPServer.class,"buildBroadcast() error",ex);
            throw ex;
        }
    }

    @Override
    public void run() {
        try {
            Optional<Bootstrap> optional = init();
            if(optional.isPresent()){
                if(transport == Transport.MULTICAST){
                    buildMulticast(optional.get());
                }else if(transport == Transport.BROADCAST){
                    buildBroadcast(optional.get());
                }else{
                    buildUncast(optional.get());
                }
            }else{
                Log.error(UDPServer.class,"initialize UDP server failed");
                notifyState(TranState.CONNECTING);
            }
        } catch (Exception ex) {
            Log.error(UDPServer.class, "run() error", ex);
            notifyState(TranState.CONNECTING);
        }finally {
            if(elg != null){
                elg.shutdownGracefully();
            }
        }
    }

    @Override
    protected Optional<Bootstrap> init() {
        try{
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
                                    if (configuration.getAllIdleTime() > 0 || configuration.getReadIdleTime() > 0 || configuration.getWriteIdleTime() > 0){
                                        channel.pipeline().addLast(new IdleStateHandler(configuration.getReadIdleTime(),configuration.getWriteIdleTime(),configuration.getAllIdleTime(), TimeUnit.SECONDS));
                                    }
                                    channel.pipeline().addLast(new DecodeHandler(new DataListener() {
                                        @Override
                                        public void onData(DataPacket dataPacket) {
                                            notifyData(dataPacket);
                                        }
                                    }, new StateListener() {
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
            Log.error(UDPServer.class,"init() error",ex);
            return Optional.absent();
        }
    }

    @Override
    public void start() {
        try{
            new Thread(this,UDPServer.class.getSimpleName()).start();
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
