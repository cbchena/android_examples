package com.way.pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 确认是否创建图案锁界面 2015/1/19 15:28
 */
public class GuideGesturePasswordActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_guide);
		findViewById(R.id.gesturepwd_guide_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

                // 清除所有锁 2015/1/19 15:28
                App.getInstance().getLockPatternUtils().clearLock();

                // 打开创建图案锁界面 2015/1/19 15:28
                Intent intent = new Intent(GuideGesturePasswordActivity.this,
						CreateGesturePasswordActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

}
