package com.doraemoney.wk.auth.shiro.core.cache;

import com.doraemoney.wk.auth.shiro.core.exception.SerializationException;
import com.doraemoney.wk.auth.shiro.core.serializer.ObjectSerializer;
import com.doraemoney.wk.auth.shiro.core.serializer.RedisSerializer;
import com.doraemoney.wk.auth.shiro.core.serializer.StringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.CollectionUtils;

import java.util.*;

/**
 * Shiro的缓存实现之Redis
 * @author: 李帅伟
 * @date: 2018/4/25
 **/
@Slf4j
public class RedisCache<K, V> implements Cache<K, V> {

    private RedisSerializer keySerializer;
    private RedisSerializer valueSerializer;
    private IRedisManager redisManager;
    private String keyPrefix = "";
    private int expire = 0;

    /**
     * Construction
     * @param redisManager
     */
    public RedisCache(IRedisManager redisManager, RedisSerializer keySerializer, RedisSerializer valueSerializer, String prefix, int expire) {
        if (redisManager == null) {
            throw new IllegalArgumentException("redisManager cannot be null.");
        }
        this.redisManager = redisManager;
        if (keySerializer == null) {
            throw new IllegalArgumentException("keySerializer cannot be null.");
        }
        this.keySerializer = keySerializer;
        if (valueSerializer == null) {
            throw new IllegalArgumentException("valueSerializer cannot be null.");
        }
        this.valueSerializer = valueSerializer;
        if (prefix != null && !"".equals(prefix)) {
            this.keyPrefix = prefix;
        }
        if (expire != -1) {
            this.expire = expire;
        }
    }

    @Override
    public V get(K key) throws CacheException {
        log.debug("get key [" + key + "]");

        if (key == null) {
            return null;
        }

        try {
            Object redisCacheKey = getCacheKey(key);
            byte[] rawValue = redisManager.get(keySerializer.serialize(redisCacheKey));
            if (rawValue == null) {
                return null;
            }
            V value = (V) valueSerializer.deserialize(rawValue);
            return value;
        } catch (Exception e) {
            log.error("shiro redis cache get error, key={}", key, e);
            return null;
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        log.debug("put key: {}, value: {}", key, value);
        if (key == null) {
            log.warn("Saving a null key is meaningless, return value directly without call Redis.");
            return value;
        }
        try {
            Object redisCacheKey = getCacheKey(key);
            redisManager.set(keySerializer.serialize(redisCacheKey), value != null ? valueSerializer.serialize(value) : null, expire);
            return value;
        } catch (Exception e) {
            log.error("shiro redis cache put error:", e);
            return null;
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        log.debug("remove key [" + key + "]");
        if (key == null) {
            return null;
        }
        try {
            Object redisCacheKey = getCacheKey(key);
            byte[] rawValue = redisManager.get(keySerializer.serialize(redisCacheKey));
            V previous = (V) valueSerializer.deserialize(rawValue);
            redisManager.del(keySerializer.serialize(redisCacheKey));
            return previous;
        } catch (Exception e) {
            log.error("shiro redis cache remove error:", e);
            return null;
        }
    }

    @Override
    public void clear() throws CacheException {
        log.debug("clear cache");
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get keys error", e);
        }
        if (keys == null || keys.size() == 0) {
            return;
        }
        for (byte[] key: keys) {
            redisManager.del(key);
        }
    }

    @Override
    public int size() {
        Long longSize = new Long(redisManager.dbSize());
        return longSize.intValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keys() {
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get keys error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        Set<K> convertedKeys = new HashSet<K>();
        for (byte[] key:keys) {
            try {
                convertedKeys.add((K) keySerializer.deserialize(key));
            } catch (SerializationException e) {
                log.error("deserialize keys error", e);
            }
        }
        return convertedKeys;
    }

    @Override
    public Collection<V> values() {
        Set<byte[]> keys = null;
        try {
            keys = redisManager.keys(keySerializer.serialize(this.keyPrefix + "*"));
        } catch (SerializationException e) {
            log.error("get values error", e);
            return Collections.emptySet();
        }

        if (CollectionUtils.isEmpty(keys)) {
            return Collections.emptySet();
        }

        List<V> values = new ArrayList<V>(keys.size());
        for (byte[] key : keys) {
            V value = null;
            try {
                value = (V) valueSerializer.deserialize(redisManager.get(key));
            } catch (SerializationException e) {
                log.error("deserialize values= error", e);
            }
            if (value != null) {
                values.add(value);
            }
        }
        return Collections.unmodifiableList(values);
    }

    private String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    private K getCacheKey(Object key) {
        return (K) (this.keyPrefix + key);
    }
}