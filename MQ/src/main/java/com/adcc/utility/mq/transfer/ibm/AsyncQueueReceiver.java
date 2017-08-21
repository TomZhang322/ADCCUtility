package com.adcc.utility.mq.transfer.ibm;

import com.adcc.utility.log.Log;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.transfer.AsyncMsgListener;
import com.adcc.utility.mq.transfer.AsyncReceiver;
import com.google.common.base.Optional;
import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;

import java.util.Enumeration;

/**
 * IBM MQ 队列异步接收处理类
 */
public class AsyncQueueReceiver implements AsyncReceiver {

    // 线程名称
    private final static String THREAD_NAME = "Thread.%s";

    // 名称
    private String name;

    // 队列
    private MQQueue queue;

    // 队列管理器
    private MQQueueManager queueManager;

    // 状态
    private AsyncReceiverState state = AsyncReceiverState.Closed;

    // 消息接收挂起标识
    private boolean suspend;

    // 异步接收线程
    private AsyncReceiveRunnable asyncReceiveRunnable;

    // 异步接收监听器
    private AsyncMsgListener listener;

    /**
     * 构造函数
     * */
    public AsyncQueueReceiver() {

    }

    /**
     * 构造函数
     * */
    public AsyncQueueReceiver(String name) {
        this.name = name;
    }

    /**
     * 构造函数
     * */
    public AsyncQueueReceiver(String name, MQQueueManager queueManager) {
        this.name = name;
        this.queueManager = queueManager;
    }

    /**
     * 构造函数
     * */
    public AsyncQueueReceiver(String name, MQQueueManager queueManager, AsyncMsgListener listener) {
        this.name = name;
        this.queueManager = queueManager;
        this.listener = listener;
    }

    public void setQueueManager(MQQueueManager queueManager) {
        this.queueManager = queueManager;
    }

    public void suspend(boolean flag) {
        suspend = flag;
    }

    /**
     * 取得队列深度
     * @return
     */
    private int getDepth() {
        try {
            if (queue.isOpen()) {
                return queue.getCurrentDepth();
            } else {
                Log.error(AsyncQueueReceiver.class.getName(), "queue:" + name + " is not open");
                state = AsyncReceiverState.Opening;
                return -1;
            }
        } catch (Exception ex) {
            Log.error(AsyncQueueReceiver.class.getName(), "getDepth() error", ex);
            state = AsyncReceiverState.Opening;
            return -1;
        }
    }

    /**
     * 打开主题
     */
    private void open() {
        try {
            int options = CMQC.MQOO_INPUT_AS_Q_DEF | CMQC.MQOO_INQUIRE;
            queue = queueManager.accessQueue(name, options);
            if (queue.isOpen()) {
                state = AsyncReceiverState.Open;
            } else {
                state = AsyncReceiverState.Opening;
            }
        } catch (Exception ex) {
            Log.error("open() error", ex);
            state = AsyncReceiverState.Opening;
        }
    }

    /**
     * 接收消息
     * @return
     */
    private Optional<Message> receive() {
        MQMessage mqMessage = null;
        try {
            MQGetMessageOptions gmo = new MQGetMessageOptions();
            gmo.options = CMQC.MQGMO_NO_WAIT | CMQC.MQGMO_CONVERT | CMQC.MQGMO_FAIL_IF_QUIESCING;

            mqMessage = new MQMessage();
            mqMessage.characterSet = 1208;
            queue.get(mqMessage, gmo);

            Message message = new Message();
            Enumeration<String> propertyNames = mqMessage.getPropertyNames("%");
            if (propertyNames!= null) {
                while(propertyNames.hasMoreElements()) {
                    String propertyName = propertyNames.nextElement();
                    message.getHead().put(propertyName, mqMessage.getObjectProperty(propertyName));
                }
            }
            byte[] buffer = new byte[mqMessage.getMessageLength()];
            mqMessage.readFully(buffer, 0, buffer.length);
            message.setContent(buffer);
            return Optional.of(message);
        } catch (Exception ex) {
            if (ex instanceof  MQException) {
                MQException e = (MQException)ex;
                if (e.getReason() == CMQC.MQRC_NO_MSG_AVAILABLE || e.getReason() == CMQC.MQRC_OPTIONS_CHANGED) {
                    return Optional.absent();
                }
            }
            Log.error(AsyncQueueReceiver.class.getName(), "receive() error", ex);
            return Optional.of(null);
        }finally {
            try{
                if(mqMessage != null){
                    mqMessage.clearMessage();
                }
            }catch (Exception ex){
                Log.error(AsyncTopicReceiver.class.getName(),"clear MQ message error",ex);
            }
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
        try {
            if (asyncReceiveRunnable == null) {
                asyncReceiveRunnable = new AsyncReceiveRunnable();
                Thread thread = new Thread(asyncReceiveRunnable, String.format(THREAD_NAME, name));
                thread.start();
            }
        } catch (Exception ex) {
            Log.error(AsyncQueueReceiver.class.getName(), "start() error", ex);
        }
    }

    @Override
    public void stop() {
        try {
            if (asyncReceiveRunnable != null) {
                asyncReceiveRunnable.stop();
                asyncReceiveRunnable = null;
            }
            queue.close();
        } catch (Exception ex) {
            Log.error(AsyncQueueReceiver.class.getName(), "stop() error", ex);
        }
    }

    /**
     * 异步接收线程
     */
    class AsyncReceiveRunnable implements Runnable {

        private boolean started;

        public void stop() {
            started = false;
        }

        @Override
        public void run() {
            try {
                started = true;
                while (started) {
                    try {
                        if(!suspend) {
                            if (state == AsyncReceiverState.Open) {
                                if (getDepth() > 0) {
                                    Optional<Message> optional = receive();
                                    if (optional.isPresent() && listener != null) {
                                        listener.onMessage(name, optional.get(), 0);
                                    }
                                }else {
                                    Thread.sleep(10);
                                }
                            }else {
                                open();
                                Thread.sleep(100);
                            }
                        }else {
                            Thread.sleep(100);
                        }
                    }catch (Exception ex) {
                        Log.error("async receive topic error", ex);
                        state = AsyncReceiverState.Opening;
                    }
                }
            } catch (Exception ex) {
                Log.error(AsyncReceiveRunnable.class.getName(), "run() error", ex);
            }
        }
    }
}