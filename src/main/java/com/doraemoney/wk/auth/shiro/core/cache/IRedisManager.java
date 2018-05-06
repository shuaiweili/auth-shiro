package com.doraemoney.wk.auth.shiro.core.cache;

import java.util.Set;

/**
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
public interface IRedisManager {

    /**
     * get value from redis
     * @param key
     * @return
     */
    byte[] get(byte[] key);

    /**
     * set
     * @param key
     * @param value
     * @return
     */
    byte[] set(byte[] key, byte[] value, int expire);

    /**
     * del
     * @param key
     */
    void del(byte[] key);

    /**
     * size
     */
    Long dbSize();

    /**
     * keys
     * @param pattern
     * @return
     */
    Set<byte[]> keys(byte[] pattern);

}