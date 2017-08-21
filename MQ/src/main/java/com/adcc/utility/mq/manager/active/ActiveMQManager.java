package com.adcc.utility.mq.manager.active;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.entity.Queue;
import com.adcc.utility.mq.entity.Topic;
import com.adcc.utility.mq.entity.active.ActiveQueue;
import com.adcc.utility.mq.entity.active.ActiveTopic;
import com.adcc.utility.mq.manager.MQManager;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.broker.jmx.TopicViewMBean;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.config.SystemPropertiesConfiguration;

import java.util.List;

/**
 * Active MQ管理类
 */
public class ActiveMQManager<T1 extends Queue,T2 extends Topic> implements MQManager<T1,T2>{

    // URL
    private String url = "service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi";

    // 用户名
    private String user = "admin";

    // 密码
    private String password = "admin";

    /**
     * 构造函数
     */
    public ActiveMQManager(){

    }

    /**
     * 构造函数
     * @param url
     */
    public ActiveMQManager(String url) throws Exception{
        if(Strings.isNullOrEmpty(url)){
            throw new NullPointerException("jmx url is null or empty");
        }
        this.url = url;
    }

    /**
     * 构造函数
     * @param url
     * @param user
     * @param password
     * @throws Exception
     */
    public ActiveMQManager(String url,String user,String password) throws Exception{
        if(Strings.isNullOrEmpty(url)){
            throw new NullPointerException("url is null or empty");
        }
        if(Strings.isNullOrEmpty(user)){
            throw new NullPointerException("user is null or empty");
        }
        if(Strings.isNullOrEmpty(password)){
            throw new NullPointerException("password is null or empty");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private RemoteJMXBrokerFacade getResource() throws Exception{
        try{
            RemoteJMXBrokerFacade facade = new RemoteJMXBrokerFacade();
            System.setProperty("webconsole.jmx.url", url);
            System.setProperty("webconsole.jmx.user", user);
            System.setProperty("webconsole.jmx.password", password);
            SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
            facade.setConfiguration(configuration);
            return facade;
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"getResource() error",ex);
            throw ex;
        }
    }

    private void returnResource(RemoteJMXBrokerFacade facade){
        try{
            facade.shutdown();
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"returnResource() error",ex);
        }
    }

    /**
     * 取得BrokerId
     * @return
     * @throws Exception
     */
    public String getBrokerId() throws Exception{
        RemoteJMXBrokerFacade facade = getResource();
        try{
            return facade.getBrokerAdmin().getBrokerId();
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"createQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    /**
     * 取得BrokerName
     * @return
     * @throws Exception
     */
    public String getBrokerName() throws Exception{
        RemoteJMXBrokerFacade facade = getResource();
        try{
            return facade.getBrokerAdmin().getBrokerName();
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"createQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void createQueue(T1 queue) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            if(queue == null){
                throw new NullPointerException("queue is null");
            }
            if(Strings.isNullOrEmpty(queue.getName())){
                throw new NullPointerException("queue name is null or empty");
            }
            facade.getBrokerAdmin().addQueue(queue.getName());
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"createQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void removeQueue(T1 queue) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            if(queue == null){
                throw new NullPointerException("queue is null");
            }
            if(Strings.isNullOrEmpty(queue.getName())){
                throw new NullPointerException("queue name is null or empty");
            }
            facade.getBrokerAdmin().removeQueue(queue.getName());
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"removeQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void updateQueue(T1 queue) throws Exception {
        createQueue(queue);
    }

    @Override
    public List<T1> findQueue(String name) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        List<ActiveQueue> result = Lists.newArrayListWithExpectedSize(100);
        try{
            if(Strings.isNullOrEmpty(name)){
                throw new NullPointerException("queue name is null or empty");
            }
            QueueViewMBean qvb = facade.getQueue(name);
            if(qvb != null){
                ActiveQueue q = new ActiveQueue();
                q.setName(qvb.getName());
                q.setDepth(qvb.getQueueSize());
                q.setEnqueued(qvb.getEnqueueCount());
                q.setDequeued(qvb.getDequeueCount());
                q.setProducers(qvb.getProducerCount());
                q.setConsumers(qvb.getConsumerCount());
                result.add(q);
            }
            return (List<T1>) result;
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public List<T1> findAllQueue() throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        List<ActiveQueue> result = Lists.newArrayListWithExpectedSize(100);
        try{
            List<QueueViewMBean> list = (List<QueueViewMBean>)facade.getQueues();
            if(list != null && list.size() > 0){
                for(QueueViewMBean qvb : list){
                    ActiveQueue q = new ActiveQueue();
                    q.setName(qvb.getName());
                    q.setDepth(qvb.getQueueSize());
                    q.setEnqueued(qvb.getEnqueueCount());
                    q.setDequeued(qvb.getDequeueCount());
                    q.setProducers(qvb.getProducerCount());
                    q.setConsumers(qvb.getConsumerCount());
                    result.add(q);
                }
            }
            return (List<T1>) result;
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findAllQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void clearQueue(String name) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            if(Strings.isNullOrEmpty(name)){
                throw new NullPointerException("queue name is null or empty");
            }
            facade.purgeQueue(ActiveMQDestination.createDestination(name,(byte)1));
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findAllQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void clearAllQueue() throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            List<QueueViewMBean> list = (List<QueueViewMBean>)facade.getQueues();
            if(list != null && list.size() > 0){
                for(QueueViewMBean qvb : list){
                    try{
                        facade.purgeQueue(ActiveMQDestination.createDestination(qvb.getName(),(byte)1));
                    }catch (Exception ex){
                        Log.error(ActiveMQManager.class.getName(),"clearAllQueue() error",ex);
                    }
                }
            }
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findAllQueue() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void createTopic(T2 topic) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            ActiveTopic t = (ActiveTopic)topic;
            if(t == null){
                throw new NullPointerException("topic is null");
            }
            if(Strings.isNullOrEmpty(t.getName())){
                throw new NullPointerException("topic name is null or empty");
            }
            facade.getBrokerAdmin().addTopic(t.getName());
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"createTopic() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void removeTopic(T2 topic) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        try{
            if(topic == null){
                throw new NullPointerException("topic is null");
            }
            if(Strings.isNullOrEmpty(topic.getName())){
                throw new NullPointerException("topic name is null or empty");
            }
            facade.getBrokerAdmin().removeTopic(topic.getName());
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"removeTopic() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public void updateTopic(T2 topic) throws Exception {
        createTopic(topic);
    }

    @Override
    public List<T2> findTopic(String name) throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        List<ActiveTopic> result = Lists.newArrayListWithExpectedSize(100);
        try{
            if(Strings.isNullOrEmpty(name)){
                throw new NullPointerException("topic name is null or empty");
            }
            TopicViewMBean tvb = facade.getTopic(name);
            if(tvb != null){
                ActiveTopic t = new ActiveTopic();
                t.setName(tvb.getName());
                t.setEnqueued(tvb.getEnqueueCount());
                t.setDequeued(tvb.getDequeueCount());
                t.setProducers(tvb.getProducerCount());
                t.setConsumers(tvb.getConsumerCount());
                result.add(t);
            }
            return (List<T2>) result;
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findTopic() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }

    @Override
    public List<T2> findAllTopic() throws Exception {
        RemoteJMXBrokerFacade facade = getResource();
        List<ActiveTopic> result = Lists.newArrayListWithExpectedSize(100);
        try{
            List<TopicViewMBean> list = (List<TopicViewMBean>) facade.getTopics();
            if(list != null && list.size() > 0){
                for(TopicViewMBean tvb : list){
                    ActiveTopic t = new ActiveTopic();
                    t.setName(tvb.getName());
                    t.setEnqueued(tvb.getEnqueueCount());
                    t.setDequeued(tvb.getDequeueCount());
                    t.setProducers(tvb.getProducerCount());
                    t.setConsumers(tvb.getConsumerCount());
                    result.add(t);
                }
            }
            return (List<T2>) result;
        }catch (Exception ex){
            Log.error(ActiveMQManager.class.getName(),"findAllTopic() error",ex);
            throw ex;
        }finally {
            returnResource(facade);
        }
    }
}
