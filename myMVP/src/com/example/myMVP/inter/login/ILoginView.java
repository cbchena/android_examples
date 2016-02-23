package com.example.myMVP.inter.login;

/**
 * 登录视图的接口
 * PS：处理View逻辑。
 * @author cbchen.
 * @time 2016/2/23 16:16.
 */
public interface ILoginView {

    /**
     * 获取登录名称 2016/2/23 16:18
     * @return 登录名称
     */
    String getLoginName();

    /**
     * 获取登录密码 2016/2/23 16:18
     * @return 登录密码
     */
    String getLoginPassword();

    /**
     * 清除数据 2016/2/23 16:20
     */
    void clear();
}
