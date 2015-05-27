package com.lxb.window;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Floating_windowActivity extends Activity implements OnClickListener {

	private Button btn_show;
	private Button btn_hide;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btn_show = (Button) findViewById(R.id.btn_show);
        btn_hide = (Button) findViewById(R.id.btn_hide);
        btn_show.setOnClickListener(this);
        btn_hide.setOnClickListener(this);
    }

	public void onClick(View v) {


        switch(v.getId()) {
            case R.id.btn_show:
                System.out.println("------------------------------------  R.id.btn_show  " + v.getId());
                Intent show = new Intent(this, FloatingWindowService.class);
                show.putExtra(FloatingWindowService.OPERATION, FloatingWindowService.OPERATION_SHOW);
                startService(show);
                break;
            case R.id.btn_hide:
                System.out.println("------------------------------------  R.id.btn_hide  " + v.getId());
                Intent hide = new Intent(this, FloatingWindowService.class);
                hide.putExtra(FloatingWindowService.OPERATION, FloatingWindowService.OPERATION_HIDE);
                startService(hide);
                break;
		}
	}

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }

    /**
     * HOME暂时拦截不到  2014/12/20 17:29
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        System.out.println("+++++++++++++++++++++++++++++  " + keyCode);

        // 按下HOME键或者按下返回
        if(keyCode == KeyEvent.KEYCODE_HOME || keyCode == KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return false; // 不会在继续往底层传递事件
        }

        return super.onKeyDown(keyCode, event);
    }

}