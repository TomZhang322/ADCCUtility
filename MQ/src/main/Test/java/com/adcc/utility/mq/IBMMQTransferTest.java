package com.adcc.utility.mq;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.MQConfigurationFactory;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.adcc.utility.mq.transfer.MQConnectionPoolFactory;
import com.adcc.utility.mq.transfer.QueueMsgListener;
import com.adcc.utility.mq.transfer.ibm.IBMMQConnectionPool;
import com.adcc.utility.mq.transfer.ibm.IBMMQTransfer;

import java.util.List;
import java.util.Map;

/**
 * IBMMQTransfer单元测试
 */
public class IBMMQTransferTest {

    private static final String host = "192.168.246.197";

    private static final int port = 1415;

    private static final String queueManager = "GW1";

    private static final String channel = "CHL.GW1";

    private static final String queue = "Q.S_REMOTE";

    private static final String topic = "T.GWTOBGS";

    private static final String message = "\u0001QU LHWE1YA\n" +
            ".QXSXMXS 260559\n" +
            "\u0002PAR\n" +
            "FI LY0075/AN 4X-ECE\n" +
            "DT QXT IOR2 260559 F00A\n" +
            "-  DIS.4X-ECE8094AA\n" +
            "\u0003\n";

    /**
     * 测试队列发送消息
     * @param transfer
     * @param queue
     * @throws Exception
     */
    private static void testSendQueue(IBMMQTransfer transfer,String queue) throws Exception {
        Message msg = new Message();
        msg.getHead().put("MSG.TYPE","GWTOGW");
        msg.getHead().put("MSG.SENDER","CES");
        msg.getHead().put("MSG.RECEIVER","CCA");
        msg.setContent(message.getBytes());
        transfer.sendQueue(queue,msg);
        System.out.println("send queue message:\r\n" + msg.toString());
    }

    /**
     * 测试队列发送超时报文
     * @param transfer
     * @param queue
     * @param expiredTime
     * @throws Exception
     */
    private static void testSendQueue(IBMMQTransfer transfer,String queue,int expiredTime) throws Exception {
        Message msg = new Message();
        msg.setContent(message.getBytes());
        transfer.sendQueue(queue,msg,expiredTime);
        System.out.println("send queue message:\r\n" + msg.toString());
    }

    /**
     * 测试队列接收消息
     * @param transfer
     * @param queue
     * @throws Exception
     */
    private static void testReceiveQueue(IBMMQTransfer transfer,String queue) throws Exception {
        Message msg = transfer.receiveQueue(queue);
        if(message != null){
            System.out.println("receive queue message:\r\n" + msg.toString());
        }
    }

    /**
     * 测试队列预览消息
     * @param transfer
     * @param queue
     * @throws Exception
     */
    private static void testBrowsingQueue(IBMMQTransfer transfer,String queue) throws Exception {
        List<Message> list = transfer.browsingQueue(queue);
        if(list != null && list.size() > 0){
            System.out.println("browsing queue message count:" + list.size());
            for(Message msg : list){
                System.out.println("browsing queue message:\r\n" + msg.toString());
            }
        }
    }

    /**
     * 测试会话发送消息
     * @param transfer
     * @param topic
     */
    private static void testSendTopic(IBMMQTransfer transfer,String topic) throws Exception {
        Message msg = new Message(message.getBytes());
        transfer.sendTopic(topic,msg);
        System.out.println("send topic message:\r\n" + msg.toString());
    }

    /**
     * 测试会话发送消息
     * @param transfer
     * @param topic
     * @param expiredTime
     */
    private static void testSendTopic(IBMMQTransfer transfer,String topic,int expiredTime) throws Exception {
        Message msg = new Message(message.getBytes());
        transfer.sendTopic(topic,msg,expiredTime);
        System.out.println("send topic message:\r\n" + msg.toString());
    }

    /**
     * 测试会话接收消息
     * @param transfer
     * @param topic
     */
    private static void testReceiveTopic(IBMMQTransfer transfer,String topic) throws Exception {
        Message msg = transfer.receiveTopic(topic);
        if(msg != null){
            System.out.println("receive topic message:\r\n" + msg.toString());
        }
    }

    public static void main(String[] args) throws Exception {
        MQConfiguration configuration = MQConfigurationFactory.getInstance().createIBMMQConfiguration(host,port,queueManager,channel);
        MQConnectionPool pool = MQConnectionPoolFactory.getInstance().createIBMMQConnectionPool(configuration);
        ((IBMMQConnectionPool)pool).setActiveMode(0);
        ((IBMMQConnectionPool)pool).setTimeout(3600);
        ((IBMMQConnectionPool)pool).setMaxConnections(75);
        ((IBMMQConnectionPool)pool).setMaxIdelConnections(50);
        pool.init(configuration);
        IBMMQTransfer transfer = new IBMMQTransfer(configuration,pool);
        transfer.setQueueListener(new QueueMsgListener() {
            @Override
            public void onQueueMsg(String name, Message message, Map<String, String> info) {
                System.out.println(message.toString());
            }
        },"Q.U_GWTOCCA");
        transfer.suspendQueue("Q.U_GWTOCCA", false);
        transfer.startAsync();
        while (true){
            Message msg = new Message(message.getBytes());
            transfer.sendQueueForTest("Q.U_GWTOCCA",msg);
            Thread.sleep(500);
        }
    }
}
