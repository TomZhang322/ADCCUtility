package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.*;
import junit.framework.TestCase;

public class UDPClientTest extends TestCase {

    private static String PEK = "232.0.0.1:9001";

    public void testStart() throws Exception {

    }

    public void testStop() throws Exception {

    }

    public static void main(String[] args) throws InterruptedException {
        int port = 9001;
        /***************组播测试start*****************/
        String host = "232.0.0.1";
        final UDPClient client = new UDPClient(host,port);
//        client.setLocalHost("ip");//可指定本地网卡地址
        client.setTransport(Transport.MULTICAST);
        /****************组播测试end******************/
        /***************广播测试start*****************/
//        String host = "255.255.255.255";
//        final UDPClient client = new UDPClient(host,port);
//        client.setTransport(Transport.BROADCAST);
        /****************单播测试end******************/
        /***************单播测试start*****************/
//        String host = "192.168.247.190";
//        final UDPClient client = new UDPClient(host,port);
//        client.setTransport(Transport.UNICAST);
        /****************单播测试end******************/
        client.getConfiguration().setWriteIdleTime(5);
        client.setDataListener(new DataListener() {
            @Override
            public void onData(DataPacket dataPacket) {
                System.out.println(dataPacket.toString());
            }
        });
        client.setStateListener(new StateListener() {
            @Override
            public void onState(TranState state) {
                System.out.println(state.toString());
            }

            @Override
            public void onState(String channelID, TranState state) {

            }

            @Override
            public void onState(String hostName, String channelID, TranState state) {

            }
        });
        client.start();
        Thread.sleep(15000);
        while (true){
            DataPacket packet = new DataPacket(new String("Hello world").getBytes());
            ((UDPClient) client).send(packet);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}