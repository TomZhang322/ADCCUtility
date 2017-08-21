package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.*;
import com.adcc.utility.log.Log;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 数据接收Handler
 */
public class DecodeHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    // Channel ID
    private String channelID;

    // 连接状态
    private TranState state = TranState.DISCONNECTED;

    // DataListener
    private DataListener dataListener;

    // StateListener
    private StateListener stateListener;

    /**
     * 构造函数
     * @param dataListener
     * @param stateListener
     */
    public DecodeHandler(DataListener dataListener, StateListener stateListener){
        this.dataListener = dataListener;
        this.stateListener = stateListener;
    }

    /**
     * 通知连接状态
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
     * 通知接收报文
     * @param buffer
     */
    private void notifyData(byte[] buffer){
        if(dataListener != null){
            DataPacket dataPacket = new DataPacket(channelID,buffer);
            dataListener.onData(dataPacket);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        try{
            super.channelRegistered(ctx);
            channelID = ctx.channel().id().asLongText();
            Log.info("register channel:" + channelID);
            notifyState(TranState.CONNECTING);
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"channelRegistered() error",ex);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        try{
            super.channelUnregistered(ctx);
            Log.info("unregister channel:" + channelID);
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"channelUnregistered() error",ex);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        try{
            super.channelActive(ctx);
            Log.info("active channel:" + channelID);
            notifyState(TranState.CONNECTED);
        }catch (Exception ex){
            Log.error(ChannelHandlerContext.class,"channelActive() error",ex);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        try{
            super.channelInactive(ctx);
            Log.info("inactive channel:" + channelID);
            notifyState(TranState.DISCONNECTED);
        }catch (Exception ex) {
            Log.error(ChannelHandlerContext.class,"channelInactive() error",ex);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket datagramPacket) {
        try{
            ByteBuf bb = datagramPacket.content();
            if(bb.readableBytes() > 0){
                byte[] buffer = new byte[bb.readableBytes()];
                bb.readBytes(buffer);
                notifyData(buffer);
            }
            datagramPacket.retain();
            if(state == TranState.IDLE){
                notifyState(TranState.CONNECTED);
            }
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"channelRead0() error",ex);
        }finally {
            datagramPacket.release();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        try{
            if(evt instanceof IdleStateEvent){
                IdleStateEvent idle = (IdleStateEvent)evt;
                if(idle.state() == IdleState.ALL_IDLE){
                    Log.info("channel:" + channelID + " idle");
                    notifyState(TranState.IDLE);
                }else if(idle.state() == IdleState.READER_IDLE){
                    Log.info("channel:" + channelID + " read idle");
                    notifyState(TranState.IDLE);
                }else if(idle.state() == IdleState.WRITER_IDLE){
                    Log.info("channel:" + channelID + " write idle");
                    notifyState(TranState.IDLE);
                }
            }else{
                super.userEventTriggered(ctx, evt);
            }
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"userEventTriggered() error",ex);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        try {
            super.exceptionCaught(ctx, cause);
            Log.error(DecodeHandler.class,cause.fillInStackTrace().toString());
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"exceptionCaught() error",ex);
        }
    }
}
