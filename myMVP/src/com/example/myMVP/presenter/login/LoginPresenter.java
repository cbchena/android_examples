package com.example.myMVP.presenter.login;

import com.example.myMVP.bean.login.LoginEntity;
import com.example.myMVP.inter.login.ILoginModel;
import com.example.myMVP.inter.login.ILoginView;
import com.example.myMVP.view.login.LoginActivity;

/**
 * 登录的视图逻辑交互
 * PS：处理View的逻辑和控制逻辑。
 * @author cbchen.
 * @time 2016/2/23 16:27.
 */
public class LoginPresenter implements ILoginView, ILoginModel {

    private LoginActivity _loginActivity; // 视图

    public LoginPresenter(LoginActivity loginActivity) {
        this._loginActivity = loginActivity;
    }

    @Override
    public String getLoginName() {
        return this._loginActivity.getEdtLoginName().getText().toString();
    }

    @Override
    public String getLoginPassword() {
        return this._loginActivity.getEdtLoginPass().getText().toString();
    }

    @Override
    public void clear() {
        this._loginActivity.getEdtLoginName().setText("");
        this._loginActivity.getEdtLoginPass().setText("");
    }

    @Override
    public void login(LoginEntity loginEntity) {
        System.out.println("登录了。。。" + loginEntity.getLoginName()
                + "   " + loginEntity.getLoginPassword());
    }

    @Override
    public LoginEntity getLoginEntity() {
        return new LoginEntity()
                .setLoginName(getLoginName())
                .setLoginPassword(getLoginPassword());
    }
}
