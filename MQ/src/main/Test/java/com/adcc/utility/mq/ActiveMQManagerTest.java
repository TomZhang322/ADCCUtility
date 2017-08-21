package com.adcc.utility.mq;

import com.adcc.utility.mq.manager.active.ActiveMQManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ActiveMQManagerTest单元测试
 */
public class ActiveMQManagerTest {

    private String url = "service:jmx:rmi:///jndi/rmi://192.168.246.210:1099/jmxrmi";

    private String user = "admin";

    private String password = "admin";

    private ActiveMQManager manager;

    @Before
    public void setUp() throws Exception {
        manager = new ActiveMQManager(url,user,password);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetBrokerId() throws Exception {
        System.out.println(manager.getBrokerId());
    }

    @Test
    public void testGetBrokerName() throws Exception {
        System.out.println(manager.getBrokerName());
    }

    @Test
    public void testClearQueue() throws Exception {
        manager.clearQueue("DPC.DOWN");
    }

}
