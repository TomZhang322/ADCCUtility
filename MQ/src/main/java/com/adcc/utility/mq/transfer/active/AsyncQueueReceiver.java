package com.adcc.utility.mq.transfer.active;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.entity.active.AckMode;
import com.adcc.utility.mq.transfer.AsyncMsgListener;
import com.adcc.utility.mq.transfer.AsyncReceiver;
import com.google.common.base.Optional;
import org.apache.activemq.ActiveMQConnection;

import javax.jms.*;
import javax.jms.Message;
import java.util.Enumeration;

/**
 * 队列异步接收处理
 */
public class AsyncQueueReceiver implements AsyncReceiver,MessageListener{

    // 名称
    private String name;

    // 应答模式
    private AckMode ackMode = AckMode.AUTO_ACKNOWLEDGE;

    // 消息队列连接
    private ActiveMQConnection connection;

    // 消息会话
    private Session session;

    // 消费者
    private MessageConsumer consumer;

    // 异步接收监听器
    private AsyncMsgListener listener;

    /**
     * 构造函数
     */
    public AsyncQueueReceiver(){

    }

    /**
     * 构造函数
     * @param name
     */
    public AsyncQueueReceiver(String name){
        this.name = name;
    }

    /**
     * 构造函数
     * @param name
     * @param ackMode
     */
    public AsyncQueueReceiver(String name,AckMode ackMode){
        this.name = name;
        this.ackMode = ackMode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAckMode(AckMode ackMode) {
        this.ackMode = ackMode;
    }

    public void setConnection(ActiveMQConnection connection) {
        this.connection = connection;
    }

    /**
     * 格式化消息
     * @param msg
     * @return
     */
    private Optional<com.adcc.utility.mq.entity.Message> formatMsg(Message msg){
        try{
            com.adcc.utility.mq.entity.Message message = null;
            if(msg == null){
                return Optional.absent();
            }else{
                if(msg instanceof TextMessage){
                    TextMessage tm = (TextMessage)msg;
                    if(tm != null && tm.getText().length() > 0){
                        message = new com.adcc.utility.mq.entity.Message(tm.getText().getBytes());

                        // 获取消息头
                        Enumeration<String> enumeration = tm.getPropertyNames();
                        while(enumeration.hasMoreElements()){
                            String key = enumeration.nextElement();
                            Object value = tm.getObjectProperty(key);
                            message.getHead().put(key,value);
                        }
                    }
                }else if(msg instanceof BytesMessage){
                    BytesMessage bm = (BytesMessage)msg;
                    if(bm != null && bm.getBodyLength() > 0){
                        byte[] buffer = new byte[(int)bm.getBodyLength()];
                        bm.readBytes(buffer);
                        message = new com.adcc.utility.mq.entity.Message(buffer);

                        // 获取消息头
                        Enumeration<String> enumeration = bm.getPropertyNames();
                        while(enumeration.hasMoreElements()){
                            String key = enumeration.nextElement();
                            Object value = bm.getObjectProperty(key);
                            message.getHead().put(key,value);
                        }
                    }
                }else{
                    return Optional.absent();
                }
            }
            return Optional.fromNullable(message);
        }catch (Exception ex){
            Log.error("formatMsg() error",ex);
            return Optional.absent();
        }
    }

    @Override
    public void setMsgListener(AsyncMsgListener listener) {
        this.listener = listener;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {
        try{
            if(connection.isStarted()){
                if(ackMode == AckMode.SESSION_TRANSACTED){
                    session = connection.createSession(true,ackMode.ordinal());
                }else{
                    session = connection.createSession(false,ackMode.ordinal());
                }
                Destination destination = session.createQueue(name);
                consumer = session.createConsumer(destination);
                consumer.setMessageListener(this);
            }else{
                Log.error(AsyncQueueReceiver.class.getName(),"connection is not started");
            }
        }catch (Exception ex){
            Log.error(AsyncQueueReceiver.class.getName(),"start() error",ex);
        }
    }

    @Override
    public void stop() {
        try{
            if(consumer != null){
                consumer.close();
            }
            if(session != null){
                session.close();
            }
        }catch (Exception ex){
            Log.error(AsyncQueueReceiver.class.getName(),"stop() error",ex);
        }
    }

    @Override
    public void onMessage(Message msg) {
        try{
            Optional<com.adcc.utility.mq.entity.Message> optional = formatMsg(msg);
            if(ackMode == AckMode.SESSION_TRANSACTED){
                session.commit();
            }
            if(optional.isPresent()){
                if(listener != null){
                    listener.onMessage(name,optional.get(),0);
                }
            }
        }catch (Exception ex){
            Log.error(AsyncQueueReceiver.class.getName(),"onMessage() error",ex);
            if(ackMode == AckMode.SESSION_TRANSACTED){
                try {
                    session.rollback();
                } catch (JMSException e) {
                    Log.error("rollback session error",e);
                }
            }
        }
    }
}
