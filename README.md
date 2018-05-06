# shiro auth

使用spring对auth进行了封装，业务方只需要实现简单的方法即可实现权限的管理。

```
@Configuration
public class ShiroConfig extends AbstractShiroConfig {
    
    @Bean
    public ShiroFilterFactoryBean filterFactoryBean() throws Exception {
        return createShiroFilterBean();//默认
        //return createShiroFilterBean(myFilterChainDefinitionMap);
        //return createShiroFilterBean(mySecurityManager, myFilterChainDefinitionMap);
    }


    @Override
    public AbstractAuthorizingRealm getRealm() {
        return new AbstractAuthorizingRealm() {
            @Override
            public RoleAndPermissions getRoleAndPermissionsFromUsername(String s) {
                //TODO 自己实现
                return null;
            }

            @Override
            public UserAuthInfo getAuthInfoFromUsername(String s) {
                //TODO 自己实现
                return null;
            }

        };
    }
 
}
```

默认项：

loginUrl:  /login

unauthorizedUrl: /unAuthorized

cacheManager:  EhcacheManager

session cache: Redis cache