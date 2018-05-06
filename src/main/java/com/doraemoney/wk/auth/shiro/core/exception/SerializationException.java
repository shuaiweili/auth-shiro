package com.doraemoney.wk.auth.shiro.core.exception;

/**
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
public class SerializationException extends Exception {
    public SerializationException(String msg) {
        super(msg);
    }
    public SerializationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}