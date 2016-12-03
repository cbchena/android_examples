package com.iqiyi.srdemo.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * 采用MediaProjection和MediaRecorder录制视频，或者是命令录制都是保存到本地文件了，
 * 但是在真正推流的过程中需要获取到视频流数据，所以这种方式不适合后续的推流工作。
 */
public class ScreenRecordService extends Service {

	private static final String TAG = "ScreenRecordingService";
	
	private int mScreenWidth;
	private int mScreenHeight;
	private int mScreenDensity;
	private int mResultCode;
	private Intent mResultData;

	/** 是否为标清视频 */
	private boolean isVideoSd;

	/** 是否开启音频录制 */
	private boolean isAudio;
	
	private MediaProjection mMediaProjection;
	private MediaRecorder mMediaRecorder;
	private VirtualDisplay mVirtualDisplay;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		Log.i(TAG, "Service onCreate() is called");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i(TAG, "Service onStartCommand() is called");
		
		mResultCode = intent.getIntExtra("code", -1);
		mResultData = intent.getParcelableExtra("data");
		mScreenWidth = intent.getIntExtra("width", 720);
		mScreenHeight = intent.getIntExtra("height", 1280);
		mScreenDensity = intent.getIntExtra("density", 1);
		isVideoSd = intent.getBooleanExtra("quality", true);
		isAudio = intent.getBooleanExtra("audio", true);
		
		mMediaProjection =  createMediaProjection();
		mMediaRecorder = createMediaRecorder();
		mVirtualDisplay = createVirtualDisplay(); // 必须在mediaRecorder.prepare() 之后调用，否则报错"fail to get surface"
		mMediaRecorder.start();
		
		return Service.START_NOT_STICKY;
	}
	
	private MediaProjection createMediaProjection() {
		Log.i(TAG, "Create MediaProjection");
		return ((MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE))
				.getMediaProjection(mResultCode, mResultData);
	}
	
	private MediaRecorder createMediaRecorder() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date curDate = new Date(System.currentTimeMillis());
		String curTime = formatter.format(curDate).replace(" ", "");
		String videoQuality = "HD";
		if(isVideoSd) videoQuality = "SD";
		
		Log.i(TAG, "Create MediaRecorder");
		MediaRecorder mediaRecorder = new MediaRecorder();
		if(isAudio) mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
		mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE); 
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

		String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
				+ "/" + videoQuality + curTime + ".mp4";

		System.out.println("===============  path  " + path);

		mediaRecorder.setOutputFile(path);
		mediaRecorder.setVideoSize(mScreenWidth, mScreenHeight);  //after setVideoSource(), setOutFormat()
		mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
		if(isAudio) mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  //after setOutputFormat()
		int bitRate;
		if(isVideoSd) { // 标清 2016/12/1 16:52
			mediaRecorder.setVideoEncodingBitRate(mScreenWidth * mScreenHeight); 
			mediaRecorder.setVideoFrameRate(30); 
			bitRate = mScreenWidth * mScreenHeight / 1000;
		} else { // 高清 2016/12/1 16:52
			mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight); 
			mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
			bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
		}

		try {
			mediaRecorder.prepare();
		} catch (IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.i(TAG, "Audio: " + isAudio + ", SD video: " + isVideoSd + ", BitRate: " + bitRate + "kbps");
		
		return mediaRecorder;
	}
	
	private VirtualDisplay createVirtualDisplay() {
		Log.i(TAG, "Create VirtualDisplay");
		return mMediaProjection.createVirtualDisplay(TAG, mScreenWidth, mScreenHeight, mScreenDensity, 
				DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mMediaRecorder.getSurface(), null, null);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i(TAG, "Service onDestroy");
		if(mVirtualDisplay != null) {
			mVirtualDisplay.release();
			mVirtualDisplay = null;
		}
		if(mMediaRecorder != null) {
			mMediaRecorder.setOnErrorListener(null);
			mMediaProjection.stop();
			mMediaRecorder.reset();
		}
		if(mMediaProjection != null) {
			mMediaProjection.stop();
			mMediaProjection = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
