package com.example.myVideo;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.widget.Button;

import java.io.File;
import java.util.Calendar;

public class MyActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = "MyActivity";
    private SurfaceView mSurfaceview;
    private Button mBtnStartStop;
    private boolean mStartedFlg = false;
    private MediaRecorder mRecorder;
    private SurfaceHolder mSurfaceHolder;
    private Camera c;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        // 设置横屏显示
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        // 选择支持半透明模式,在有surfaceview的activity中使用。
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        setContentView(R.layout.main);

        mSurfaceview  = (SurfaceView)findViewById(R.id.surfaceview);
        mBtnStartStop = (Button)findViewById(R.id.btnStartStop);
        mBtnStartStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!mStartedFlg) {
                    // Start
//                    if (mRecorder == null) {
//                        mRecorder = new MediaRecorder(); // Create MediaRecorder
//                    }
                    try {

                        // 设置摄像头竖屏时，旋转90° 2015/8/25 9:56
//                        Camera c = Camera.open();
//                        c.setDisplayOrientation(90);
//                        c.unlock();
//                        mRecorder.setCamera(c);
//
//                        // Set audio and video source and encoder
//                        // 这两项需要放在setOutputFormat之前
//                        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
//                        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
//
//                        //相机参数配置类
////                        CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF); // 504k
//                        CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
//                        mRecorder.setProfile(cProfile);

//                        mRecorder.setVideoFrameRate(30); // 设置帧数
//                        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 设置显示的视图

                        // Set output file path
                        String path = getSDPath();
                        if (path != null) {

                            File dir = new File(path + "/recordtest");
                            if (!dir.exists()) {
                                dir.mkdir();
                            }

                            path = dir + "/" + getDate() + ".3gp";
                            mRecorder.setOutputFile(path);
                            Log.d(TAG, "bf mRecorder.prepare()");
                            mRecorder.prepare();
                            Log.d(TAG, "af mRecorder.prepare()");
                            Log.d(TAG, "bf mRecorder.start()");
                            mRecorder.start();   // Recording is now started
                            Log.d(TAG, "af mRecorder.start()");
                            mStartedFlg = true;
                            mBtnStartStop.setText("Stop");
                            Log.d(TAG, "Start recording ...");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // stop
                    if (mStartedFlg) {
                        try {
                            Log.d(TAG, "Stop recording ...");
                            Log.d(TAG, "bf mRecorder.stop(");
                            mRecorder.stop();
                            Log.d(TAG, "af mRecorder.stop(");
                            mRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
                            mBtnStartStop.setText("Start");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mStartedFlg = false; // Set button status flag
                }
            }

        });

        SurfaceHolder holder = mSurfaceview.getHolder();// 取得holder

        // 设置该组件不会让屏幕自动关闭
        mSurfaceview.getHolder().setKeepScreenOn(true);

        holder.addCallback(this); // holder加入回调接口

        // setType必须设置，要不出错.设置Surface不需要维护自己的缓冲区
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        if (mRecorder == null) {
            mRecorder = new MediaRecorder(); // Create MediaRecorder
        }

        // 设置摄像头竖屏时，旋转90° 2015/8/25 9:56
        if (c == null)
            c = Camera.open();

        c.setDisplayOrientation(90);
        c.unlock();
        mRecorder.setCamera(c);

        // Set audio and video source and encoder
        // 这两项需要放在setOutputFormat之前
        mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        mRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);

        //相机参数配置类
//                        CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_CIF); // 504k
        CamcorderProfile cProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mRecorder.setProfile(cProfile);

        mRecorder.setVideoFrameRate(30); // 设置帧数
    }

    /**
     * 获取系统时间
     * @return
     */
    public static String getDate(){
        Calendar ca = Calendar.getInstance();
        int year = ca.get(Calendar.YEAR);			// 获取年份
        int month = ca.get(Calendar.MONTH);			// 获取月份
        int day = ca.get(Calendar.DATE);			// 获取日
        int minute = ca.get(Calendar.MINUTE);		// 分
        int hour = ca.get(Calendar.HOUR);			// 小时
        int second = ca.get(Calendar.SECOND);		// 秒

        String date = "" + year + (month + 1 )+ day + hour + minute + second;
        Log.d(TAG, "date:" + date);

        return date;
    }

    /**
     * 获取SD path
     * @return
     */
    public String getSDPath(){
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
        if (sdCardExist)
        {
            return Environment.getExternalStorageDirectory().getPath();// 获取跟目录
        }

        return null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int previewWidth,
                               int previewHeight) {
        // TODO Auto-generated method stub
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder

//        final int width = 720;
//        final int height = 480;
//        if (width * previewHeight > height * previewWidth) {
//            final int surfaceViewWidth = previewWidth * height / previewHeight;
//            mSurfaceview.layout((int)((width - surfaceViewWidth)*0.5), 0, (int)((width + surfaceViewWidth)*0.5), height);
//        } else {
//            final int surfaceViewHeight = previewHeight * width / previewWidth;
//            mSurfaceview.layout(0, (int)((height - surfaceViewHeight)*0.5), width, (int)((height + surfaceViewHeight)*0.5));
//        }

        mSurfaceHolder = holder;
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 设置显示的视图
        Log.d(TAG, "surfaceChanged 1");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder
        mSurfaceHolder = holder;
        mRecorder.setPreviewDisplay(mSurfaceHolder.getSurface()); // 设置显示的视图
        Log.d(TAG, "surfaceChanged 2");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // surfaceDestroyed的时候同时对象设置为null
        mSurfaceview = null;
        mSurfaceHolder = null;
        if (mRecorder != null) {
            mRecorder.release(); // Now the object cannot be reused
            mRecorder = null;
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
    }
}
