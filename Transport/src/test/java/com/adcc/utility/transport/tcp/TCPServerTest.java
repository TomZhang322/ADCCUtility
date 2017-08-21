package com.adcc.utility.transport.tcp;

import com.adcc.utility.transport.DataListener;
import com.adcc.utility.transport.DataPacket;

/**
 * Created by zf on 2017/8/9.
 */
public class TCPServerTest {

    public static void main(String[] args){

        TCPServer tcpServer = new TCPServer(8923);
        tcpServer.setDataListener(new DataListener() {
            @Override
            public void onData(DataPacket dataPacket) {
                System.out.println(dataPacket.toString());
            }
        });
        tcpServer.start();
    }
}
