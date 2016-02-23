package com.example.myMVP.inter.login;

import com.example.myMVP.bean.login.LoginEntity;

/**
 * 登录的逻辑处理接口
 * PS：处理控制逻辑，如访问网络，访问系统，访问数据库。
 * @author cbchen.
 * @time 2016/2/23 16:20.
 */
public interface ILoginModel {

    /**
     * 登录 2016/2/23 16:22
     */
    void login(LoginEntity loginEntity);

    /**
     * 获取登录实体
     * @return 登录实体
     */
    LoginEntity getLoginEntity();
}
