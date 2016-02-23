package com.example.myMVP.view.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.myMVP.R;
import com.example.myMVP.presenter.login.LoginPresenter;

/**
 * 登录
 * PS：View的操作和控制逻辑全部放在Presenter中进行处理。Activity只做展示与接收用户的输入！！！
 * @author cbchen.
 * @time 2016/2/23 16:30.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText _edtLoginName; // 登录名
    private EditText _edtLoginPass; // 登录密码
    private Button _btnLogin; // 登录按钮
    private Button _btnClear; // 重置按钮

    private LoginPresenter _loginPresenter; // 交互类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        this._loginPresenter = new LoginPresenter(this);

        this._edtLoginName = (EditText) this.findViewById(R.id.edtLoginName);
        this._edtLoginPass = (EditText) this.findViewById(R.id.edtLoginPass);
        this._btnLogin = (Button) this.findViewById(R.id.btnLogin);
        this._btnClear = (Button) this.findViewById(R.id.btnClear);

        // 设置事件 2016/2/23 17:01
        this._btnLogin.setOnClickListener(this);
        this._btnClear.setOnClickListener(this);
    }

    public EditText getEdtLoginName() {
        return _edtLoginName;
    }

    public EditText getEdtLoginPass() {
        return _edtLoginPass;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin: // 登录
                this._loginPresenter.login(this._loginPresenter.getLoginEntity());
                break;
            case R.id.btnClear: // 重置
                this._loginPresenter.clear();
                break;
        }
    }
}
