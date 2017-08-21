package com.adcc.utility.transport.udp;

import com.adcc.utility.transport.*;
import junit.framework.TestCase;

public class UDPServerTest extends TestCase {

    private static String PEK = "232.0.0.1:9001";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    public static void main(String[] args) throws InterruptedException {
        int port = 9001;
        /**************组播测试start***************/
        String host = "192.168.247.190";
        String multiAddress = "232.0.0.1";
        TranServer server = new UDPServer(port);
        ((UDPServer)server).setTransport(Transport.MULTICAST);//设置传输方式为组播
        ((UDPServer)server).getMulticastAddress().add(multiAddress);//添加组播地址
        /**************组播测试end****************/
        /**************广播测试start***************/
//        String host = "255.255.255.255";
//        TranServer server = new UDPServer(host,port);
//        ((UDPServer)server).setTransport(Transport.BROADCAST);//设置传输方式为广播
        /**************广播测试end***************/
        /**************单播测试start***************/
//        String host = "192.168.247.190";
//        TranServer server = new UDPServer(host,port);
//        ((UDPServer)server).setTransport(Transport.UNICAST);//设置传输方式为单播
        /**************单播测试end***************/
        server.getConfiguration().setReadIdleTime(10);
        server.setDataListener(new DataListener() {
            @Override
            public void onData(DataPacket dataPacket) {
                System.out.println(dataPacket.toString());
            }
        });
        server.setStateListener(new StateListener() {
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
        server.start();
        Thread.sleep(60000);

        //server.stop();
    }
}