package com.doraemoney.wk.auth.shiro.core.config;

import com.doraemoney.wk.auth.shiro.core.CacheType;
import com.doraemoney.wk.auth.shiro.core.ShiroFilterFactory;
import com.doraemoney.wk.auth.shiro.core.cache.EhCacheManagerFactory;
import com.doraemoney.wk.auth.shiro.core.cache.RedisCacheManager;
import com.doraemoney.wk.auth.shiro.core.cache.RedisManager;
import com.doraemoney.wk.auth.shiro.core.session.ShiroSessionManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author: 李帅伟
 * @date: 2018/4/26
 **/
public abstract class AbstractShiroConfig {

    /**
     * 此方法由业务方重写
     * @return 具体的Realm
     */
    public abstract AbstractAuthorizingRealm getRealm();

    /**
     * 动态获取角色-资源信息
     * @return filterChainDefinitions
     */
    public abstract Map<String, String> loadFilterChainDefinitions() throws Exception;

    /**
     * 创建ShiroFilterFactoryBean
     *      默认使用Ehcache缓存
     * @return
     */
    public ShiroFilterFactoryBean createShiroFilterBean() {
        return createShiroFilterBean(defaultWebSecurityManager(CacheType.EHCACHE), defaultFilterChainMap());
    }

    public ShiroFilterFactoryBean createShiroFilterBean(DefaultWebSecurityManager securityManager, Map<String, String> filterChainDefinitionMap) {
        return ShiroFilterFactory.create(securityManager, filterChainDefinitionMap, this);
    }

    public ShiroFilterFactoryBean createShiroFilterBean(Map<String, String> filterChainDefinitionMap) {
        return ShiroFilterFactory.create(defaultWebSecurityManager(CacheType.EHCACHE), filterChainDefinitionMap, this);
    }

    //    public ShiroFilterFactoryBean createShiroFilterBean(CacheType cacheType) {
//        return createShiroFilterBean(defaultWebSecurityManager(cacheType), defaultFilterChainMap());
//    }
//
//    public ShiroFilterFactoryBean createShiroFilterBean(DefaultWebSecurityManager securityManager) {
//        return ShiroFilterFactory.create(securityManager, defaultFilterChainMap());
//    }
//

//
//    public ShiroFilterFactoryBean createShiroFilterBean(CacheType cacheType, Map<String, String> filterChainDefinitionMap) {
//        return ShiroFilterFactory.create(defaultWebSecurityManager(cacheType), filterChainDefinitionMap);
//    }
//
//    public ShiroFilterFactoryBean createShiroFilterBean(CacheManager cacheManager) {
//        return ShiroFilterFactory.create(defaultWebSecurityManager(cacheManager), defaultFilterChainMap());
//    }
//
//    public ShiroFilterFactoryBean createShiroFilterBean(CacheManager cacheManager, Map<String, String> filterChainDefinitionMap) {
//        return ShiroFilterFactory.create(defaultWebSecurityManager(cacheManager), filterChainDefinitionMap);
//    }

    /***
     * 默认的安全管理配置
     */
    public DefaultWebSecurityManager defaultWebSecurityManager(CacheType cacheType) {
       return defaultWebSecurityManager(cacheType, null);
    }

    public DefaultWebSecurityManager defaultWebSecurityManager(CacheManager cacheManager) {
        return defaultWebSecurityManager(null, cacheManager);
    }

    public DefaultWebSecurityManager defaultWebSecurityManager(CacheType cacheType, CacheManager cacheManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        //设置realm
        securityManager.setRealm(getRealm());
        // 配置securityManager
        SecurityUtils.setSecurityManager(securityManager);
        // 根据情况选择缓存器
        CacheManager cm = cacheManager == null ? (cacheType == null ? defaultShiroCacheManager() : getCacheManager(cacheType)) : cacheManager;
        securityManager.setCacheManager(cm);

        //设置session manager
        securityManager.setSessionManager(new ShiroSessionManager());
        return securityManager;
    }

    /**
     * shiro缓存：ehcache缓存 (用户认证信息和权限信息等)
     */
    public CacheManager defaultShiroCacheManager() {
        return EhCacheManagerFactory.getCacheManager();
    }

    /**
     * redis缓存：redis缓存 (用户认证信息和权限信息等)
     */
    public CacheManager defaultShiroRedisCacheManager() {
        return new RedisCacheManager(new RedisManager(), "auth");
    }

    private Map<String, String> defaultFilterChainMap() {
        // 配置拦截地址和拦截器, 使用LinkedHashMap,因为拦截有先后顺序
        // authc:所有url都必须认证通过才可以访问;
        // anon:所有url都都可以匿名访问
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();

        // 以下配置同样可以通过注解@RequiresPermissions("user:edit")来配置访问权限和角色注解@RequiresRoles(value={"ROLE_USER"})方式定义
//        filterChainDefinitionMap.put("/user/**", "roles[ROLE_USER]");// /user/下面的需要ROLE_USER角色或者query权限才能访问
//        filterChainDefinitionMap.put("/admin/**", "roles[ROLE_ADMIN]");// /admin/下面的所有需要ROLE_ADMIN的角色才能访问
        //登录注册不需要认证
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/register", "anon");
        // 其他资源地址全部需要用户认证才能访问
        filterChainDefinitionMap.put("/**", "authc");

        return filterChainDefinitionMap;
    }

    /**
     * 根据缓存类型选择对应的缓存管理器
     * @param cacheType 缓存类型
     * @return
     */
    private CacheManager getCacheManager(CacheType cacheType) {
        CacheManager cacheManager = defaultShiroCacheManager();
        if (cacheType == CacheType.REDIS) {
            cacheManager = defaultShiroRedisCacheManager();
        }
        return cacheManager;
    }
}
