package com.doraemoney.wk.auth.shiro.core.cache;

import com.doraemoney.wk.auth.shiro.core.serializer.ObjectSerializer;
import com.doraemoney.wk.auth.shiro.core.serializer.RedisSerializer;
import com.doraemoney.wk.auth.shiro.core.serializer.StringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Shiro缓存管理器实现之Redis
 * @author: 李帅伟
 * @date: 2018/4/25
 **/
@Slf4j
public class RedisCacheManager implements CacheManager {

    // fast lookup by name map
    private final ConcurrentMap<String, Cache> caches = new ConcurrentHashMap<>();
    private RedisSerializer keySerializer = new StringSerializer();
    private RedisSerializer valueSerializer = new ObjectSerializer();

    private IRedisManager redisManager;

    // expire time in seconds
    public static final int DEFAULT_EXPIRE = 1800;
    private int expire = DEFAULT_EXPIRE;
    private static final String DEFAULT_CACHE_KEY_PREFIX = "shiro:cache:";
    private String keyPrefix = DEFAULT_CACHE_KEY_PREFIX;

    public RedisCacheManager(IRedisManager redisManager) {
        this(redisManager, DEFAULT_EXPIRE);
    }

    public RedisCacheManager(IRedisManager redisManager, int expire) {
        this(redisManager, expire, "");
    }

    public RedisCacheManager(IRedisManager redisManager, String keyPrefix) {
        this(redisManager, DEFAULT_EXPIRE, keyPrefix);
    }

    public RedisCacheManager(IRedisManager redisManager, int expire, String keyPrefix) {
        this.redisManager = redisManager;
        this.expire = expire;
        this.keyPrefix = DEFAULT_CACHE_KEY_PREFIX + ":" + keyPrefix;
    }


    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        log.debug("get cache, name=" + name);

        Cache cache = caches.get(name);

        if (cache == null) {
//            cache = new RedisCache<K, V>(redisManager, keySerializer, valueSerializer, keyPrefix + name + ":", expire);
            cache = new RedisCache<K, V>(redisManager, keySerializer, valueSerializer, keyPrefix + ":", expire);
            caches.put(name, cache);
        }
        return cache;
    }

}
