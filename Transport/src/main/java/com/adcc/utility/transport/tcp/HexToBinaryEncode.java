package com.adcc.utility.transport.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Administrator on 2017/8/6.
 */
public class HexToBinaryEncode extends ByteToMessageDecoder{

    private String separator;

    public HexToBinaryEncode(String separator){
        this.separator = separator;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf bf, List<Object> list) throws Exception {
        while(bf.isReadable()){
            byte[] b = new byte[bf.readableBytes()];
            bf.readBytes(b);
            list.add(Unpooled.copiedBuffer(hexStringToBinary(separator + new String(b,0,b.length))));
        }
    }

    private static byte[] hexStringToBinary(String src){
        int l = src.length() / 2;
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            ret[i] = (byte) Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();
        }
        return ret;
    }
}
