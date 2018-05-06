package com.doraemoney.wk.auth.shiro.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色和权限
 * @author: 李帅伟
 * @date: 2018/4/27
 **/
@Data
public class RoleAndPermissions {

    private List<String> roles;
    private List<String> permissions;

    public RoleAndPermissions() {}

    public RoleAndPermissions(List<String> roles, List<String> permissions) {
        this.roles = roles;
        this.permissions = permissions;
    }

    public RoleAndPermissions addRole(String role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
        return this;
    }

    public RoleAndPermissions addPermission(String permission) {
        if (permissions == null) {
            permissions = new ArrayList<>();
        }
        permissions.add(permission);
        return this;
    }
}
