package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.DataListener;
import com.adcc.utility.transport.DataPacket;
import com.adcc.utility.log.Log;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.List;

/**
 * Created by zf on 2017/7/25.
 */
public class TCPServerChannelHandler extends ByteToMessageDecoder {

    private TCPServer tcpServer;
    private int overTimeCounts = 0;//心跳包连续丢失次数
    private int MAX_OVERTIME = 3;//最大心跳包连续丢失数

    public TCPServerChannelHandler(TCPServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        DataListener dataListener = tcpServer.getDataListener();
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
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
            IdleState state = ((IdleStateEvent) evt).state();
            if(state == IdleState.ALL_IDLE){
                if(overTimeCounts < MAX_OVERTIME){
                    Log.info(TCPClientChannelHandler.class.getName(),"向客户端(" + ctx.channel().remoteAddress() + ")发送心跳");
                    if(tcpServer.getHearBeatListener() != null && tcpServer.getHearBeatListener().ping()
                            && tcpServer.getHearBeatListener().sendHearBeatMsg()){
                        overTimeCounts = 0;
                    }else{
                        overTimeCounts++;
                    }
                }else{
                    Log.info(TCPClientChannelHandler.class.getName(),"向客户端（" + ctx.channel().remoteAddress() + "）发送心跳无反应；关闭客户端连接");
                    ctx.close();
                }
            }
        }else{
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        tcpServer.getChannelManager().addChannel(ctx.channel().id().toString(),ctx.channel());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        tcpServer.getChannelManager().removeChannel(ctx.channel().id().toString());
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
