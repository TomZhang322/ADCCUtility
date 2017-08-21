package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.*;
import io.netty.channel.ChannelOption;

/**
 * Created by zf on 2017/8/9.
 */
public class TCPClientTest {

    public static void main(String[] args){

        TCPClient tcpClient = new TCPClient("127.0.0.1",8923);
        tcpClient.setDataListener(new DataListener() {
            @Override
            public void onData(DataPacket dataPacket) {
                System.out.println(dataPacket.toString());
            }
        });
        tcpClient.setStateListener(new StateListener() {
            @Override
            public void onState(TranState state) {
                System.out.println(state);
            }

            @Override
            public void onState(String channelID, TranState state) {

            }

            @Override
            public void onState(String hostName, String channelID, TranState state) {

            }
        });
        tcpClient.start();
        try {
            Thread.sleep(5000);
        }catch (Exception e){

        }
        tcpClient.send(new DataPacket("hehehe".getBytes()));
    }
}
