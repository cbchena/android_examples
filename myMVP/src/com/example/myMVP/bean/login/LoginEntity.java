package com.example.myMVP.bean.login;

/**
 * 登录实体
 * @author cbchen.
 * @time 2016/2/23 16:10.
 */
public class LoginEntity {

    private String loginName; // 登录名
    private String loginPassword; // 登录密码

    public String getLoginName() {
        return loginName;
    }

    public LoginEntity setLoginName(String loginName) {
        this.loginName = loginName;

        return this;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public LoginEntity setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;

        return this;
    }
}
