package com.iqiyi.srdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ccb.myscreenrecorder.R;
import com.iqiyi.srdemo.service.ScreenRecordService;

/** 
* @Description: 视频录制程序入口
* @author   jianglin
* @date 2016年4月7日 下午1:39:27 
* @version V1.0 
*/
public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	
	private TextView mTextView;
	
	private static final String RECORD_STATUS = "record_status";
	private static final int REQUEST_CODE = 1000;
	
	private int mScreenWidth;
	private int mScreenHeight;
	private int mScreenDensity;
	
	/** 是否已经开启视频录制 */
	private boolean isStarted = false;
	/** 是否为标清视频 */
	private boolean isVideoSd = true;
	/** 是否开启音频录制 */
	private boolean isAudio = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Log.i(TAG, "onCreate");
		if(savedInstanceState != null) {
			isStarted = savedInstanceState.getBoolean(RECORD_STATUS);
		}
		getView() ;
		getScreenBaseInfo();
	}
	
	private void getView() {
		mTextView = (TextView) findViewById(R.id.button_control);
		if(isStarted) {
			statusIsStarted();
		} else {
			statusIsStoped();
		}
		mTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isStarted) {
					stopScreenRecording();
					statusIsStoped();
					Log.i(TAG, "Stoped screen recording");
				} else {
					startScreenRecording();
				}
			}
		});
		
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.sd_button:
					isVideoSd = true;
					break;
				case R.id.hd_button:
					isVideoSd = false;
					break;

				default:
					break;
				}
			}
		});
		
		CheckBox audioBox = (CheckBox) findViewById(R.id.audio_check_box);
		audioBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				isAudio = isChecked;
			}
		});
	}
	
	/**
	 * 开启屏幕录制时的UI状态
	 */
	private void statusIsStarted() {
		mTextView.setText(R.string.stop_recording);
		mTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_red_bg));
	}
	
	/**
	 * 结束屏幕录制后的UI状态
	 */
	private void statusIsStoped() {
		mTextView.setText(R.string.start_recording);
		mTextView.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_green_bg));
	}
	
	/**
	 * 获取屏幕相关数据
	 */
	private void getScreenBaseInfo() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		mScreenWidth = metrics.widthPixels;
		mScreenHeight = metrics.heightPixels;
		mScreenDensity = metrics.densityDpi;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putBoolean(RECORD_STATUS, isStarted);
	}
	
	/**
	 * 获取屏幕录制的权限
	 */
	private void startScreenRecording() {
		// TODO Auto-generated method stub
		MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
		Intent permissionIntent = mediaProjectionManager.createScreenCaptureIntent();
		startActivityForResult(permissionIntent, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_CODE) {
			if(resultCode == RESULT_OK) {

				// 获得权限，启动Service开始录制
				Intent service = new Intent(this, ScreenRecordService.class);
				service.putExtra("code", resultCode);
				service.putExtra("data", data);
				service.putExtra("audio", isAudio);
				service.putExtra("width", mScreenWidth);
				service.putExtra("height", mScreenHeight);
				service.putExtra("density", mScreenDensity);
				service.putExtra("quality", isVideoSd);
				startService(service);

				// 已经开始屏幕录制，修改UI状态
				isStarted = !isStarted;
				statusIsStarted();
				simulateHome(); // this.finish();  // 可以直接关闭Activity
				Log.i(TAG, "Started screen recording");
			} else {
				Toast.makeText(this, R.string.user_cancelled, Toast.LENGTH_LONG).show();
				Log.i(TAG, "User cancelled");
			}
		}
	}
	
	/**
	 * 关闭屏幕录制，即停止录制Service
	 */
	private void stopScreenRecording() {
		// TODO Auto-generated method stub
		Intent service = new Intent(this, ScreenRecordService.class);
		stopService(service);
		isStarted = !isStarted;
	}
	
	/**
	 * 模拟HOME键返回桌面的功能
	 */
	private void simulateHome() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(intent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 在这里将BACK键模拟了HOME键的返回桌面功能（并无必要）
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			simulateHome();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
