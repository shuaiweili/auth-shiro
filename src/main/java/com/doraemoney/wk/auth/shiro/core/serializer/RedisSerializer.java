package com.doraemoney.wk.auth.shiro.core.serializer;

import com.doraemoney.wk.auth.shiro.core.exception.SerializationException;

/**
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
public interface RedisSerializer<T> {

    byte[] serialize(T t) throws SerializationException;

    T deserialize(byte[] bytes) throws SerializationException;
}