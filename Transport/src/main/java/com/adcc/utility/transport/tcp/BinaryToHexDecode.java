package com.adcc.utility.transport.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Administrator on 2017/8/6.
 */
public class BinaryToHexDecode extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf bf, List<Object> list) throws Exception {
        while(bf.isReadable()){
            list.add(Unpooled.copiedBuffer(binaryToHex(new byte[]{bf.readByte()})));
        }
    }

    private static byte[] binaryToHex(byte[] result) {
        StringBuffer sb = new StringBuffer(result.length * 2);
        for (int i = 0; i < result.length; i++) {
            sb.append(Character.forDigit((result[i] & 240) >> 4, 16));
            sb.append(Character.forDigit(result[i] & 15, 16));
        }
        return sb.toString().getBytes();
    }
}
