package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.TranState;
import com.adcc.utility.transport.StateListener;
import com.adcc.utility.log.Log;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * 心跳接收Handler
 */
public class HeartbeatHandler extends ChannelInboundHandlerAdapter {

    // Channel ID
    private String channelID;

    // 连接状态
    private TranState state = TranState.DISCONNECTED;

    // StateListener
    private StateListener listener;

    /**
     * 构造函数
     */
    public HeartbeatHandler(StateListener listener){
        this.listener = listener;
    }

    /**
     * 通知连接状态
     * @param state
     */
    private void notifyState(TranState state){
        if(this.state != state){
            this.state = state;
            if(listener != null){
                listener.onState(state);
            }
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
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        try{
            super.channelActive(ctx);
            Log.info("active channel:" + channelID);
            notifyState(TranState.CONNECTED);
        }catch (Exception ex){
            Log.error(ChannelHandlerContext.class,"channelActive() error",ex);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try{
            super.channelInactive(ctx);
            Log.info("inactive channel:" + channelID);
            notifyState(TranState.DISCONNECTED);
        }catch (Exception ex) {
            Log.error(ChannelHandlerContext.class,"channelInactive() error",ex);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
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
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            super.exceptionCaught(ctx, cause);
            Log.error(DecodeHandler.class,cause.fillInStackTrace().toString());
        }catch (Exception ex){
            Log.error(DecodeHandler.class,"exceptionCaught() error",ex);
        }
    }
}
