package com.doraemoney.wk.auth.shiro.core.session;

import com.doraemoney.wk.auth.shiro.core.cache.RedisCache;
import com.doraemoney.wk.auth.shiro.core.cache.RedisCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * shiro session管理（创建，删除等）
 * @author: 李帅伟
 * @date: 2018/5/3
 **/
@Slf4j
public class ShiroRedisSessionDao extends AbstractSessionDAO {

    private RedisCache<String, Object> cache;

    public ShiroRedisSessionDao(RedisCacheManager redisCacheManager) {
        this.cache = (RedisCache)redisCacheManager.getCache("session-cache-manager");
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = this.generateSessionId(session);
        this.assignSessionId(session, sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if(sessionId == null){
            log.error("session id is null");
            return null;
        }

        return (Session)cache.get(sessionId.toString());
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
        cache.remove(session.getId().toString());
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<Session> sessions = new HashSet<>();
        Set<String> keys = cache.keys();
        if(keys != null && keys.size()>0){
            for(String key : keys){
                Session s = (Session)cache.get(key);
                sessions.add(s);
            }
        }
        return sessions;
    }

    private void saveSession(Session session) throws UnknownSessionException{
        if (session == null || session.getId() == null) {
            log.error("session or session id is null");
            return;
        }
//        //设置过期时间
//        long expireTime = 1800000l;
//        session.setTimeout(expireTime);
        cache.put(session.getId().toString(), session);
    }
}
