package com.doraemoney.wk.auth.shiro.core.model;

import lombok.Data;

/**
 * @author: 李帅伟
 * @date: 2018/5/2
 **/
@Data
public class UserAuthInfo {

    private String username;
    private String password;
    private String salt;

    public UserAuthInfo(String username, String password, String salt) {
        this.username = username;
        this.password = password;
        this.salt = salt;
    }

}
