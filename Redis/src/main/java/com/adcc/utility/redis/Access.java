package com.adcc.utility.redis;

import com.google.common.base.Optional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ZHANG on 2016/8/31.
 */
public interface Access {
    /**
     * 创建Redis连接
     * @throws Exception
     */
    public void connect() throws  Exception;

    /**
     * 断开Redis连接
     */
    public void disconnect() throws Exception;

    /**
     * 取得Redis状态
     * @return
     */
    public AccessState getState();

    /**
     * 关闭Redis
     * @return
     */
    public Optional<String> shutdown() throws Exception;

    /**
     * 是否存在KEY
     * @param key
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Optional<Boolean> isExistKey(String key,int dataBase) throws Exception;

    /**
     * 查找KEY
     * @param key
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Optional<Set<String>> findKey(String key, int dataBase) throws Exception;

    /**
     * 查找KEY-VALUE
     * @param key
     * @return
     * @throws Exception
     */
    public Optional<String> findValueByKey(String key,int dataBase) throws Exception;

    /**
     * 查找KEY-VALUE
     * @param key
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Optional<List<String>> findValueByKey(String[] key,int dataBase) throws Exception;

    /**
     * 查询并保存KEY-VALUE(返回上Value次结果)
     * @param key
     * @param value
     * @return
     */
    public Optional<String> findAndSaveKeyValue(String key, String value,int dataBase) throws Exception;

    /**
     * 保存KEY-VALUE
     * @param key
     * @param value
     * @return
     */
    public Optional<String> saveKeyValue(String key, String value,int dataBase) throws Exception;

    /**
     * 删除所有KeyValue
     * @return
     */
    public Optional<String> deleteAllKey(int dataBase) throws Exception;

    /**
     * 删除KeyValue
     * @param key
     * @return
     */
    public Optional<Long> deleteKey(String key, int dataBase) throws Exception;

    /**
     * 设置KEY-VALUE超时时间
     * @return
     * @throws Exception
     */
    public Optional<Long> setKeyExpiredTime(String key,int expiredTime,int dataBase) throws  Exception;

    /**
     * 保存KEY-VALUE
     * @param key
     * @param value
     * @param expiredTime
     * @return
     */
    public Optional<String> saveKeyValue(String key, String value, int expiredTime,int dataBase) throws Exception;

    /**
     * 获取zset的长度
     * @param key
     * @return
     */
    public Optional<Long> getZsetSize(String key,int dataBase) throws Exception;

    /**
     * 根据Zset升序查找Value
     * @param key
     * @return
     */
    public Optional<Set<String>> findZSetOrderByASC(String key,int dataBase) throws Exception;

    /**
     * 根据Zset降序查找Value
     * @param key
     * @return
     */
    public Optional<Set<String>> findZSetOrderByDESC(String key,int dataBase) throws Exception;

    /**
     * 根据Zset score查找符合条件的降序数据
     * @param key
     * @param max
     * @param min
     * @return
     */
    public Optional<Set<String>> findZSetByScoreDESC(String key,int dataBase,double min,double max) throws Exception;

    /**
     * 根据Zset score查找符合条件的升序数据
     * @param key
     * @param min
     * @param max
     * @return
     */
    public Optional<Set<String>> findZSetByScoreASC(String key,int dataBase,double min,double max) throws Exception;

    /**
     * 根据Zset score查找符合条件的降序数据
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @return
     */
    public Optional<Set<String>> findZSetByScoreDESCWithLimit(String key,int dataBase,double min,double max,int offset,int count) throws Exception;

    /**
     * 根据Zset score查找符合条件的升序数据
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @return
     */
    public Optional<Set<String>> findZSetByScoreASCWithLimit(String key,int dataBase,double min,double max,int offset,int count) throws Exception;

    /**
     * 根据升/降顺序查找zset最近的几条数据
     * @param key zset的key值
     * @param dataBase redis分区
     * @param order 顺序
     * @param index 索引
     * @return
     * @throws Exception
     */
    public Optional<Set<String>> findZSetWithOrderAndCount(String key,int dataBase,boolean order,int index) throws Exception;

    /**
     * 保存ZSet
     * @param key
     * @param score
     * @param value
     * @return
     */
    public Optional<Long> saveZSet(String key, double score, String value,int dataBase) throws Exception;

    /**
     * 删除ZSet
     * @param key
     * @param value
     * @return
     */
    public Optional<Long> deleteZSet(String key, String value,int dataBase) throws Exception;

    /**
     * 是否存在HashSet键
     * @param key
     * @param field
     * @param dataBase
     * @return
     * @throws Exception
     */
    public Optional<Boolean> isExistHashKey(String key,String field,int dataBase) throws Exception;

    /**
     * 查询所有HashSet键和值
     * @param key
     * @return
     * @throws Exception
     */
    public Optional<Map<String,String>> findHashKeyAndValue(String key,int dataBase) throws Exception;

    /**
     * 查询所有HashSet值
     * @param key
     * @return
     * @throws Exception
     */
    public Optional<List<String>> findHashValue(String key,int database) throws Exception;

    /**
     * 根据Field查询HashSet值
     * @param key
     * @param field
     * @return
     * @throws Exception
     */
    public Optional<String> findHashValue(String key, String field,int database) throws Exception;

    /**
     * 根据Field查询HashSet值
     * @param key
     * @param field
     * @return
     * @throws Exception
     */
    public Optional<List<String>> findHashValue(String key, String[] field,int database) throws Exception;

    /**
     * 保存HashSet
     * @param key
     * @param field
     * @param value
     * @return
     * @throws Exception
     */
    public Optional<Long> saveHashValue(String key, String field, String value,int database) throws Exception;

    /**
     * 保存HashSet
     * @param key
     * @param map
     * @return
     * @throws Exception
     */
    public Optional<String> saveHashValue(String key,Map<String,String> map,int database) throws Exception;

    /**
     * 获取List大小
     * @param key
     * @return
     * @throws Exception
     */
    public Optional<Long> llenList(String key, int database) throws Exception;

    /**
     * 保存数据到List头部
     * @param key
     * @param values
     * @return
     * @throws Exception
     */
    public Optional<Long> lpushList(String key, int database, String... values) throws Exception;

    /**
     * 删除数据从List尾部
     * @param key
     * @return
     * @throws Exception
     */
    public Optional<String> rpopList(String key, int database) throws Exception;
}
