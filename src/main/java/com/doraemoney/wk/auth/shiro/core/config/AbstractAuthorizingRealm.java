package com.doraemoney.wk.auth.shiro.core.config;

import com.doraemoney.wk.auth.shiro.core.cache.EhCacheManagerFactory;
import com.doraemoney.wk.auth.shiro.core.model.RoleAndPermissions;
import com.doraemoney.wk.auth.shiro.core.model.UserAuthInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;


/**
 * 权限认证实现逻辑
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
@Slf4j
public abstract class AbstractAuthorizingRealm extends AuthorizingRealm {

    public AbstractAuthorizingRealm() {
        HashedCredentialsMatcher credentialsMatcher = new RetryLimitHashedCredentialsMatcher(EhCacheManagerFactory.getCacheManager());
        credentialsMatcher.setHashAlgorithmName(ShiroConstant.hashAlgorithmName);
        credentialsMatcher.setHashIterations(ShiroConstant.hashIterations);//加密次数
        credentialsMatcher.setStoredCredentialsHexEncoded(true);
        setCredentialsMatcher(credentialsMatcher);
    }

    /**
     * 获取当前用户的角色和权限
     */
    public abstract RoleAndPermissions getRoleAndPermissionsFromUsername(String username);

    /**
     * 获取认证信息
     * @param username 用户名
     */
    public abstract UserAuthInfo getAuthInfoFromUsername(String username);

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("##################执行Shiro权限认证(默认)##################");
        // 获取用户名
        String loginName = (String) principals.fromRealm(getName()).iterator().next();
        // 判断用户名是否存在
        if (loginName == null || loginName.length() == 0) {
            return null;
        }
        // 创建一个授权对象
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //角色和权限设置
        RoleAndPermissions rps = getRoleAndPermissionsFromUsername(loginName);
        if (rps.getPermissions() != null) {
            info.addStringPermissions(rps.getPermissions());
        }
        if (rps.getRoles() != null) {
            info.addRoles(rps.getRoles());
        }

        return info;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        log.info("##################执行Shiro登陆认证(默认)##################");
        UsernamePasswordToken authenticationToken = (UsernamePasswordToken) token;
        // 用户名
        String username = authenticationToken.getUsername();
        if (username != null && !"".equals(username)) {
            UserAuthInfo userAuthInfo =  getAuthInfoFromUsername(username);
            if (userAuthInfo != null) {
                Object principal = token.getPrincipal();
                // shiro的用户认证对象
                return new SimpleAuthenticationInfo(principal, userAuthInfo.getPassword(), ByteSource.Util.bytes(userAuthInfo.getSalt()), getName());
            }
        }
        return null;
    }
}
