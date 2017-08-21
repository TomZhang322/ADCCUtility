package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.DataListener;
import com.adcc.utility.transport.DataPacket;
import com.adcc.utility.transport.TranState;
import com.adcc.utility.log.Log;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zf on 2017/7/25.
 */
public class TCPClientChannelHandler extends ByteToMessageDecoder{

    private TCPClient tcpClient;
    private int overTimeCounts = 0;//心跳包连续丢失次数
    private int MAX_OVERTIME = 3;//最大心跳包连续丢失数

    public TCPClientChannelHandler(TCPClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        overTimeCounts = 0;
        tcpClient.setAttempts(0);
        Log.info(TCPClientChannelHandler.class.getName(),"连接激活");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.info(TCPClientChannelHandler.class.getName(),"客户端与服务器(" + ctx.channel().remoteAddress() + ")断开连接");
        tcpClient.notifyState(TranState.DISCONNECTED);
        if(tcpClient.getAttempts() < tcpClient.getMaxAttempts()){
            tcpClient.attemptsaIncr();
            ctx.channel().eventLoop().schedule(new Runnable() {
                @Override
                public void run() {
                    Log.info(TCPClient.class.getName(),"开始第" + tcpClient.getAttempts() + "次重连");
                    tcpClient.doConnect();
                }
            },tcpClient.getReconnectTime(),TimeUnit.MILLISECONDS);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        DataListener dataListener = tcpClient.getDataListener();
        if(dataListener != null){
            byte[] msgByte = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(msgByte);
            DataPacket packet = new DataPacket(ctx.channel().id().toString(),msgByte);
            packet.setLocalAddress(ctx.channel().localAddress().toString());
            packet.setRemoteAddress(ctx.channel().remoteAddress().toString());
            dataListener.onData(packet);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("aaaaaaaaaaaaaa");
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.READER_IDLE || state == IdleState.ALL_IDLE){
                if(overTimeCounts < MAX_OVERTIME){
                    tcpClient.notifyState(TranState.IDLE);
                    Log.info(TCPClientChannelHandler.class.getName(),"向服务器(" + ctx.channel().remoteAddress() + ")发送心跳");
                    if(tcpClient.getHearBeatListener() != null && tcpClient.getHearBeatListener().ping()
                            && tcpClient.getHearBeatListener().sendHearBeatMsg()){
                        overTimeCounts = 0;
                    }else{
                        overTimeCounts++;
                    }
                }else{
                    ctx.channel().pipeline().fireChannelInactive();
                }
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        tcpClient.notifyState(TranState.CONNECTING);
        Log.info(TCPClientChannelHandler.class.getName(),"正在连接服务器(" + ctx.channel().remoteAddress() + ")");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

        tcpClient.notifyState(TranState.CONNECTED);
        super.channelReadComplete(ctx);
    }
}
