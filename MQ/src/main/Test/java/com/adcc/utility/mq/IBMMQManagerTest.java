package com.adcc.utility.mq;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.MQConfigurationFactory;
import com.adcc.utility.mq.entity.Queue;
import com.adcc.utility.mq.entity.ibm.IBMQueue;
import com.adcc.utility.mq.entity.ibm.LocalQueue;
import com.adcc.utility.mq.entity.ibm.QueueManager;
import com.adcc.utility.mq.manager.MQManager;
import com.adcc.utility.mq.manager.ibm.IBMMQManager;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.adcc.utility.mq.transfer.MQConnectionPoolFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * IBMMQManager单元测试
 */
public class IBMMQManagerTest {

    // MQConfiguration
    private MQConfiguration configuration;

    // MQConnectionPool
    private MQConnectionPool pool;

    // MQManager
    private MQManager manager;

    @Before
    public void setUp() throws Exception {
        configuration = MQConfigurationFactory.getInstance().createIBMMQConfiguration("192.168.246.197",1415,"GW1","CHL.GW1");
        pool = MQConnectionPoolFactory.getInstance().createIBMMQConnectionPool();
        pool.init(configuration);
        manager = new IBMMQManager(configuration,pool);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testFindQueueManager() throws Exception{
        List<QueueManager> list = ((IBMMQManager)manager).findQueueManager();
        if(list != null && list.size() > 0){
            for(QueueManager qm : list){
                StringBuffer sb = new StringBuffer();
                sb.append("ip:" + qm.getIp() + " ");
                sb.append("port:" + qm.getPort() + " ");
                sb.append("startTime:" + qm.getStartTime() + " ");
                sb.append("connections:" + qm.getConnectCount() + " ");
                sb.append("status:" + qm.isStatus() + " ");
                System.out.println(sb.toString());
            }
        }
    }

    @Test
    public void testFindQueue() throws Exception{
        String strQueue = "Q.U_TEST_ZKD";
        List<Queue> list = ((IBMMQManager) manager).findQueue(strQueue);
        if(list != null && list.size() > 0){
            for(Queue q : list){
                StringBuffer sb = new StringBuffer();
                sb.append("name:" + ((LocalQueue) q).getName() + " ");
                sb.append("type:" + ((LocalQueue) q).getType() + " ");
                sb.append("persistent:" + ((LocalQueue) q).isPersistent() + " ");
                sb.append("priority:" + ((LocalQueue) q).getPriority() + " ");
                sb.append("maxMsgLength:" + ((LocalQueue) q).getMaxMsgLength() + " ");
                sb.append("retainInterval:" + ((LocalQueue) q).getRetainInterval() + " ");
                sb.append("producer:" + ((LocalQueue)q).getProducers() + " ");
                sb.append("consumer:" + ((LocalQueue)q).getConsumers() + " ");
                sb.append("enqueued:" + ((LocalQueue)q).getEnqueued() + " ");
                sb.append("dequeued:" + ((LocalQueue)q).getDequeued() + " ");
                sb.append("depth:" + ((LocalQueue)q).getDepth());
                System.out.println(sb.toString());
            }
        }
    }

    @Test
    public void testFindAllQueue() throws Exception{
        List<Queue> list = ((IBMMQManager) manager).findAllQueue();
        if(list != null && list.size() > 0){
            for(Queue q : list){
                StringBuffer sb = new StringBuffer();
                sb.append("name:" + ((IBMQueue) q).getName() + " ");
                sb.append("type:" + ((IBMQueue) q).getType() + " ");
                sb.append("persistent:" + ((IBMQueue) q).isPersistent() + " ");
                sb.append("priority:" + ((IBMQueue) q).getPriority() + " ");
                sb.append("producer:" + ((IBMQueue)q).getProducers() + " ");
                sb.append("consumer:" + ((IBMQueue)q).getConsumers() + " ");
                sb.append("enqueued:" + ((IBMQueue)q).getEnqueued() + " ");
                sb.append("dequeued:" + ((IBMQueue)q).getDequeued() + " ");
                sb.append("depth:" + ((IBMQueue)q).getDepth());
                System.out.println(sb.toString());
            }
        }
    }

    @Test
    public void testClearQueue() throws Exception{
        ((IBMMQManager) manager).clearQueue("Q.U_GWTOWUH");
    }

}
