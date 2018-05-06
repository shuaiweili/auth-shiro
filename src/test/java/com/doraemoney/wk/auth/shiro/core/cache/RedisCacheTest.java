package com.doraemoney.wk.auth.shiro.core.cache;



/**
 * @author: 李帅伟
 * @date: 2018/5/3
 **/
public class RedisCacheTest {

    public static void main(String[] args) throws InterruptedException {
        RedisCacheManager cacheManager = new RedisCacheManager(new RedisManager(), 10, "session");

        RedisCache<String, Object> cache = (RedisCache)cacheManager.getCache("");

        cache.put("key", "value");
        System.out.println(cache.get("key"));
        Thread.sleep(11000);
        System.out.println(cache.get("key"));
    }
}
