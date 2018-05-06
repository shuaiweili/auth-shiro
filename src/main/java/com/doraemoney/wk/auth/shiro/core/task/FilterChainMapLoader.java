package com.doraemoney.wk.auth.shiro.core.task;

import com.doraemoney.wk.auth.shiro.core.config.AbstractShiroConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.util.ReflectionUtils;

//import javax.servlet.Filter;
import javax.servlet.Filter;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * 定期重新获取角色-资源信息
 * @author: 李帅伟
 * @date: 2018/5/4
 **/
@Slf4j
public class FilterChainMapLoader {

    private final static int INTERVAL = 1000 * 60 * 5; //更新间隔

    public void start(ShiroFilterFactoryBean filterFactoryBean, AbstractShiroConfig shiroConfig) {
        new Thread(new FilterChainMapLoaderTask(filterFactoryBean, shiroConfig), "filter-chain-loader-thread").start();
    }

    /**
     * 权限更新任务
     */
    class FilterChainMapLoaderTask implements Runnable {
        private final ShiroFilterFactoryBean filterFactoryBean;
        private final AbstractShiroConfig shiroConfig;
        private long lastTime = System.currentTimeMillis();
        FilterChainMapLoaderTask(final ShiroFilterFactoryBean filterFactoryBean, final AbstractShiroConfig shiroConfig) {
            this.filterFactoryBean = filterFactoryBean;
            this.shiroConfig = shiroConfig;
        }
        @Override
        public void run() {
            log.info("定期更新权限任务已启动...");
            while (true) {
                long now = System.currentTimeMillis();
                long interval = now - lastTime;
                if (interval > INTERVAL) {
                    log.info("重新加载权限信息...");
                    try {
                        updatePermission();
                    } catch (Exception e) {
                        log.error("更新权限出现异常：", e);
                    }
                    lastTime = now;
                }
            }
        }

        /**
         * 重新加载权限
         */
        public void updatePermission() throws Exception {

            Map<String, String> filterChainMap = null;
            try {
                filterChainMap = shiroConfig.loadFilterChainDefinitions();
            } catch (Exception e) {
                log.error("loadFilterChainDefinitions error:", e);
            }

            if (filterChainMap != null && filterChainMap.size() > 0) {
                synchronized (filterFactoryBean) {

                    AbstractShiroFilter shiroFilter = (AbstractShiroFilter) filterFactoryBean.getObject();
                    PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter
                            .getFilterChainResolver();
                    DefaultFilterChainManager manager = (DefaultFilterChainManager) filterChainResolver
                            .getFilterChainManager();

                    // 清空老的权限控制
                    manager.getFilterChains().clear();
                    filterFactoryBean.getFilterChainDefinitionMap().clear();
                    for (Map.Entry<String, Filter> filterEntry : manager.getFilters().entrySet()) {
                        if (("roles".equals(filterEntry.getKey()) || "perms".equals(filterEntry.getKey())) && PathMatchingFilter.class.isInstance(filterEntry.getValue())) {
                            PathMatchingFilter filter = PathMatchingFilter.class.cast(filterEntry.getValue());
                            Field f = ReflectionUtils.findField(PathMatchingFilter.class, "appliedPaths");
                            f.setAccessible(true);
                            Map<String, Object> appliedPaths = (Map<String, Object>) ReflectionUtils.getField(f, filter);
                            appliedPaths.clear();
                        }
                    }

                    //创建新的权限
                    filterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
                    Map<String, String> chains = filterFactoryBean
                            .getFilterChainDefinitionMap();
                    for (Map.Entry<String, String> entry : chains.entrySet()) {
                        String url = entry.getKey();
                        String chainDefinition = entry.getValue().trim()
                                .replace(" ", "");
                        manager.createChain(url, chainDefinition);
                    }

                    log.info("更新权限成功！！");
                }
            }
        }
    }
}
