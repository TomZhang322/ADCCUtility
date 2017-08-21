package com.adcc.utility.mq.transfer;

import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.transfer.active.ActiveMQTransfer;
import com.adcc.utility.mq.transfer.ibm.IBMMQTransfer;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * MQ通信工厂
 */
public class MQTransferFactory  {

    // 单例方法
    private static MQTransferFactory instance = null;

    // MQTransfer Map
    private Map<String,MQTransfer> transferMap = Maps.newConcurrentMap();

    /**
     * 构造函数
     */
    private MQTransferFactory(){

    }

    /**
     * 单例方法
     * @return
     */
    public synchronized static MQTransferFactory getInstance(){
        if(instance == null){
            instance = new MQTransferFactory();
        }
        return instance;
    }

    public Map<String,MQTransfer> getTransferMap(){
        return transferMap;
    }

    /**
     * 创建MQTransfer
     * @param name
     * @param configuration
     * @param pool
     * @return
     * @throws Exception
     */
    public MQTransfer createActiveMQTransfer(String name,MQConfiguration configuration,MQConnectionPool pool) throws Exception {
        if(transferMap.containsKey(name)){
            return transferMap.get(name);
        }else{
            MQTransfer transfer = new ActiveMQTransfer();
            transfer.setConfiguration(configuration);
            if(pool != null){
                transfer.setConnectionPool(pool);
            }
            return transfer;
        }
    }

    /**
     * 创建MQTransfer
     * @param name
     * @param configuration
     * @param pool
     * @return
     * @throws Exception
     */
    public MQTransfer createIBMMQTransfer(String name,MQConfiguration configuration,MQConnectionPool pool) throws Exception {
        if(transferMap.containsKey(name)){
            return transferMap.get(name);
        }else{
            MQTransfer transfer = new IBMMQTransfer();
            transfer.setConfiguration(configuration);
            if(pool != null){
                transfer.setConnectionPool(pool);
            }
            return transfer;
        }
    }

    /**
     * 销毁MQTransfer
     * @param name
     */
    public void disposeMQTransfer(String name){
        if(transferMap.containsKey(name)){
            transferMap.remove(name);
        }
    }

    /**
     * 销毁所有MQTransfer
     */
    public void disposeAllMQTransfer(){
        transferMap.clear();
    }
}
