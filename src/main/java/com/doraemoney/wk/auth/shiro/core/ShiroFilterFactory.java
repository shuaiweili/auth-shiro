package com.doraemoney.wk.auth.shiro.core;

import com.doraemoney.wk.auth.shiro.core.config.AbstractShiroConfig;
import com.doraemoney.wk.auth.shiro.core.task.FilterChainMapLoader;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;

import java.util.Map;

/**
 * ShiroFilterBean工厂
 * @author: 李帅伟
 * @date: 2018/4/26
 **/
public class ShiroFilterFactory {

    /**
     * 创建ShiroFilterFactory，默认路径：
     */
    public static ShiroFilterFactoryBean create() {
        ShiroFilterFactoryBean filterFactoryBean = new ShiroFilterFactoryBean();
        filterFactoryBean.setLoginUrl("/login");// 未登录时候跳转URL,如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        filterFactoryBean.setSuccessUrl("/index");// 成功后欢迎页面
        filterFactoryBean.setUnauthorizedUrl("/unAuthorized");// 未认证页面
        return filterFactoryBean;
    }

    /**
     * @param securityManager 安全管理
     */
    public static ShiroFilterFactoryBean create(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = create();
        factoryBean.setSecurityManager(securityManager);
        return factoryBean;
    }

    /**
     * @param securityManager 安全管理
     * @param filterChainDefinitionMap filterChainDefinition
     */
    public static ShiroFilterFactoryBean create(DefaultWebSecurityManager securityManager, Map<String, String> filterChainDefinitionMap, AbstractShiroConfig shiroConfig) {
        ShiroFilterFactoryBean factoryBean = create(securityManager);
        factoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        new FilterChainMapLoader().start(factoryBean, shiroConfig); //开启定期更新权限任务
        return factoryBean;
    }
}
