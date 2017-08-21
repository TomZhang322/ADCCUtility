package com.adcc.utility.redis;

import com.adcc.utility.log.Log;
import com.google.common.base.Optional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ZHANG on 2016/8/31.
 */
public class JedisAccess implements Access{
    // 连接池
    private JedisPool jedisPool;

    // 连接状态
    private AccessState state = AccessState.DISCONNECT;

    // Redis连接配置
    private Optional<JedisConfiguration> configuration = Optional.absent();

    /**
     * 构造函数
     */
    public JedisAccess() {

    }

    /**
     * 构造函数
     * @param configuration
     */
    public JedisAccess(Optional<JedisConfiguration> configuration) {
        this.configuration = configuration;
    }

    @Override
    public AccessState getState() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if ("PONG".equals(jedis.ping())) {
                state = AccessState.CONNECTED;
            } else {
                if (jedisPool.isClosed()) {
                    state = AccessState.DISCONNECT;
                } else {
                    state = AccessState.CONNECTING;
                }
            }
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "getState() error", ex);
            if (jedisPool.isClosed()) {
                state = AccessState.DISCONNECT;
            } else {
                state = AccessState.CONNECTING;
            }
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return state;
    }

    @Override
    public void connect() throws Exception {
        if (!configuration.isPresent()) {
            throw new InvalidParameterException("qm parameters is empty");
        }
        JedisPoolFactory.getInstance().setHost(configuration.get().getHost());
        JedisPoolFactory.getInstance().setPort(configuration.get().getPort());
        JedisPoolFactory.getInstance().setMaxActive(configuration.get().getMaxActive());
        JedisPoolFactory.getInstance().setMaxIdle(configuration.get().getMaxIdle());
        JedisPoolFactory.getInstance().setMaxWait(configuration.get().getMaxWait());
        JedisPoolFactory.getInstance().setTimeout(configuration.get().getTimeout());
        JedisPoolFactory.getInstance().setTestOnBorrow(configuration.get().isTestOnBorrow());
        JedisPoolFactory.getInstance().setTestOnReturn(configuration.get().isTestOnReturn());
        JedisPoolFactory.getInstance().setTestWhileIdle(configuration.get().isTestWhileIdle());
        Optional<JedisPool> optional = JedisPoolFactory.getInstance().createRedisPool();
        if (optional.isPresent()) {
            jedisPool = optional.get();
        } else {
            throw new NullPointerException("jedisPool is null");
        }
        state = AccessState.CONNECTED;
    }

    @Override
    public void disconnect() {
        try {
            state = AccessState.DISCONNECT;
            if (!jedisPool.isClosed()) {
                JedisPoolFactory.getInstance().destroyRedisPool(jedisPool);
            }
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "disconnect() error", ex);
        }
    }

    @Override
    public Optional<String> shutdown() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String result = jedis.shutdown();
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "shutdown() error", ex);
            throw ex;
        } finally {
            jedis.close();
        }
    }

    @Override
    public Optional<Boolean> isExistKey(String key, int dataBase) throws Exception {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Boolean result = jedis.exists(key);
            return Optional.fromNullable(result);
        }catch (Exception ex){
            Log.error(JedisAccess.class.getName(),"isExistKey() error",ex);
            throw ex;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> setKeyExpiredTime(String key, int expiredTime,int dataBase) throws Exception {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.expire(key, expiredTime);
            return Optional.of(result);
        }catch (Exception ex){
            Log.error(JedisAccess.class.getName(), "setKeyExpiredTime() error",ex);
            throw ex;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> findValueByKey(String key,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String result = jedis.get(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findValueByKey() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<List<String>> findValueByKey(String[] key,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            List<String> result = jedis.mget(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findValueByKey() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findKey(String key, int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.keys(key);
            return Optional.fromNullable(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findKey() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> findAndSaveKeyValue(String key, String value,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String strValue = jedis.getSet(key, value);
            return Optional.fromNullable(strValue);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findAndSaveKeyValue() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> saveKeyValue(String key, String value,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String result = jedis.set(key, value);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "saveKeyValue() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> saveKeyValue(String key, String value, int expiredTime,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String result = jedis.setex(key, expiredTime, value);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "saveKeyValue() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> deleteAllKey(int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String result = jedis.flushDB();
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "deleteAllKey() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> deleteKey(String key, int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.del(key);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "deleteKey() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> getZsetSize(String key,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.zcard(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "getZsetSize() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetOrderByASC(String key,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrange(key, 0, -1);
            return Optional.fromNullable(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByASC() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetOrderByDESC(String key,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrevrange(key, 0, -1);
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByDESC() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetByScoreDESC(String key, int dataBase, double min, double max) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrevrangeByScore(key, max, min);
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByScore() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetByScoreASC(String key, int dataBase, double min, double max) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrangeByScore(key, min, max);
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByScore() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetByScoreDESCWithLimit(String key, int dataBase, double min, double max, int offset,
                                                              int count) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrevrangeByScore(key, max, min, offset, count);
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByScore() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetByScoreASCWithLimit(String key, int dataBase, double min, double max,int offset,
                                                      int count) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = jedis.zrangeByScore(key, min, max,offset,count);
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByScore() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Set<String>> findZSetWithOrderAndCount(String key, int dataBase, boolean order, int index) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Set<String> set = null;
            if(order){
                set = jedis.zrange(key, 0, index);
            }else{
                set = jedis.zrevrange(key, 0, index);
            }
            return Optional.of(set);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findZSetOrderByScore() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> saveZSet(String key, double score, String value,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.zadd(key, score, value);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "saveZSet() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> deleteZSet(String key, String value,int dataBase) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.zrem(key, value);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "deleteZSet() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Boolean> isExistHashKey(String key, String field, int dataBase) throws Exception {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Boolean result = jedis.hexists(key,field);
            return Optional.fromNullable(result);
        }catch (Exception ex){
            Log.error(JedisAccess.class.getName(),"isExistHashKey() error",ex);
            throw ex;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Map<String,String>> findHashKeyAndValue(String key,int dataBase) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Map<String, String> map = jedis.hgetAll(key);
            return Optional.fromNullable(map);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findHGet() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<List<String>> findHashValue(String key,int dataBase) throws Exception{
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            List<String> value = jedis.hvals(key);
            return Optional.fromNullable(value);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findHashValues() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> findHashValue(String key, String field,int dataBase) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            String value = jedis.hget(key, field);
            return Optional.fromNullable(value);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findHashValue() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<List<String>> findHashValue(String key, String[] field,int dataBase) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            List<String> value = jedis.hmget(key, field);
            return Optional.fromNullable(value);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "findHashValue() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> saveHashValue(String key, String field, String value,int dataBase) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(dataBase);
            Long result = jedis.hset(key, field, value);
            return Optional.of(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "saveHSet() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> saveHashValue(String key, Map<String, String> map,int database) throws Exception {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.select(database);
            String result = jedis.hmset(key,map);
            return Optional.of(result);
        }catch (Exception ex){
            Log.error(JedisAccess.class.getName(),"saveHashValue() error",ex);
            throw ex;
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    @Override
    public Optional<Long> llenList(String key, int database) throws Exception {
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.select(database);
            Long result = jedis.llen(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "llenList() error", ex);
            throw ex;
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public Optional<Long> lpushList(String key, int database, String... values) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(database);
            Long result = jedis.lpush(key, values);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "lpushList() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    @Override
    public Optional<String> rpopList(String key, int database) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(database);
            String result = jedis.rpop(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "rpopList() error",ex);
            throw ex;
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }
    }

    public Optional<Long> insertList(String key, int database, String value) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(database);
            Long result = jedis.rpush(key, new String[]{value});
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "insertList() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Optional<String> deleteList(String key, int database) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(database);
            String result = jedis.rpop(key);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "deleteList() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

    public Optional<List<String>> selectList(String key, int database) throws Exception {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.select(database);
            List<String> result = jedis.lrange(key, 0L, -1L);
            return Optional.fromNullable(result);
        } catch (Exception ex) {
            Log.error(JedisAccess.class.getName(), "selectList() error", ex);
            throw ex;
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
