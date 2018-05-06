package com.doraemoney.wk.auth.shiro.core.cache;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

/**
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
public class RedisManager extends BaseRedisManager implements IRedisManager {

    private int database = Protocol.DEFAULT_DATABASE;
    private JedisPool jedisPool;
    private String host;
    private int port;
    private String password;
    private int timeout;
//
//    public RedisManager() {
//        init();
//    }

    private void init() {
        synchronized (this) {
            if (jedisPool == null) {
                Config config = ConfigFactory.load("shiro-redis.conf");
                host = config.getString("redis.host");
                port = config.getInt("redis.port");
                password = config.getString("redis.password");
                timeout = config.getInt("redis.timeout");

                JedisPoolConfig poolConfig = new JedisPoolConfig();
                poolConfig.setMaxIdle(config.getInt("redis.pool.maxIdle"));
                poolConfig.setMaxTotal(config.getInt("redis.pool.maxTotal"));
                poolConfig.setMaxWaitMillis(config.getInt("redis.pool.maxWait"));
                poolConfig.setMinIdle(config.getInt("redis.pool.minIdle"));

                jedisPool = new JedisPool(poolConfig, host, port, timeout, password, database);
            }
        }
    }

    @Override
    protected Jedis getJedis() {
        if (jedisPool == null) {
            init();
        }
        return jedisPool.getResource();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDatabase() {
        return database;
    }

    public void setDatabase(int database) {
        this.database = database;
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}