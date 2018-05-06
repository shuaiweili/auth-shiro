package com.doraemoney.wk.auth.shiro.core.session;

import com.doraemoney.wk.auth.shiro.core.cache.RedisCacheManager;
import com.doraemoney.wk.auth.shiro.core.cache.RedisManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

/**
 * 自定义session manager
 * @author: 李帅伟
 * @date: 2018/5/3
 **/
public class ShiroSessionManager extends DefaultWebSessionManager {

    public ShiroSessionManager() {
        setSessionDAO(new ShiroRedisSessionDao(new RedisCacheManager(new RedisManager(), 1740, "session"))); //session 管理
        //cookie 对象
        SimpleCookie cookie = new SimpleCookie("SHIROSESSIONID");
        cookie.setPath("/");
        setSessionIdCookie(cookie);
    }
}
