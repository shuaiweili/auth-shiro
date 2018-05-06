package com.doraemoney.wk.auth.shiro.core.cache;

import org.apache.shiro.cache.ehcache.EhCacheManager;

/**
 * @author: 李帅伟
 * @date: 2018/5/2
 **/
public class EhCacheManagerFactory {

    private static EhCacheManager cacheManager;

    static {
        cacheManager = new EhCacheManager();
        cacheManager.setCacheManagerConfigFile("classpath:ehcache-shiro.xml");
    }

    public static EhCacheManager getCacheManager() {
        return cacheManager;
    }
}
