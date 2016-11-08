package com.ccb.myvoice;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private SoundMeter _sensorMeter; // 音频管理器
    private boolean _isBeginVocie; // 是否开始录音
    private boolean _isShosrt = false; // 时间是否太短
    private int _flagVocie = 1; // 1 为可以开始录音   2 为正在录音
    private String _voiceName; // 音频名称
    private long _startVoiceT, _endVoiceT; // 记录开始、结束录音时间
    private View _chatPopup; // 录音显示UI层
    private LinearLayout _llRcdHintLoading, _llRcdHintRcding,
            _llRcdHintTooshort; // 布局控件
    private ImageView _btnModeVolume; // 显示音频大小
    private LinearLayout _llVoice; // 录音层
    private LinearLayout _llCancelVoice; // 取消录音层布局
    private ImageView _imgX; // 关闭的叉
    private Button _btnVoice; // 当前按下的录音键
    private LinearLayout _llBtnVoice; // 布局录音键
    private Handler _handlerVoice = new Handler(); // 处理录音进度

    private MediaPlayer mMediaPlayer = new MediaPlayer(); // 播放器

    private Timer _timer; // 计时器
    private int _limitTime = 60; // 限时60秒 2016/11/7 17:08

    /**
     * 时间到，停止并上传 2016/11/7 17:04
     */
    private Handler _handlerUpload = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            _llRcdHintRcding.setVisibility(View.GONE); // 隐藏手机录音状态
            _stop(); // 停止录音
            _flagVocie = 1;

            // 处理发送音频 2015/7/21 13:59
            _chatPopup.setVisibility(View.GONE); // 隐藏录音显示UI层
            List<String> lstPaths = new ArrayList<>();
            lstPaths.add(_sensorMeter.ARM_PATH + _voiceName);
            _uploadVoice(lstPaths, _limitTime); // 上传

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this._llBtnVoice = (LinearLayout) this.findViewById(R.id.llBtnVoice);
        this._btnVoice = (Button) this.findViewById(R.id.btnVoice);
        this._btnVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                _isBeginVocie = true; // 标志开始录音 2016/11/7 14:29
                return false;
            }
        });

        this._sensorMeter = new SoundMeter();
        this._chatPopup = this.findViewById(R.id.rcChat_popup); // 录音显示UI层
        this._llVoice = (LinearLayout) this.findViewById(R.id.llVoice); // 获取录音层
        this._llCancelVoice = (LinearLayout) this.findViewById(R.id.del_re); // 取消录音层布局
        this._imgX = (ImageView) this.findViewById(R.id.sc_img1);
        this._btnModeVolume = (ImageView) this.findViewById(R.id.volume); // 显示音频大小

        this._llRcdHintRcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding); // 显示手机录音状态

        this._llRcdHintLoading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading); // 录音开始前的加载准备

        this._llRcdHintTooshort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort); // 显示时间太短的提示
    }

    /**
     * 按下语音录制按钮时 2015/7/21 9:39
     * @param event 触发事件
     * @return boolean
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Utils.closeInput(this); // 关闭输入法 2015/8/17 14:58
        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "没有sd卡", Toast.LENGTH_LONG).show();
            return false;
        }

        if (_isBeginVocie && _llBtnVoice != null) { // 是否开始音频模式
            int[] location = new int[2];
            _llBtnVoice.getLocationInWindow(location); // 获取录音按钮在当前窗口内的绝对坐标
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];

            int[] del_location = new int[2];
            _llCancelVoice.getLocationInWindow(del_location); // 获取取消录音层的绝对坐标
            int del_Y = del_location[1];
            int del_x = del_location[0];

            if (event.getAction() == MotionEvent.ACTION_DOWN && _flagVocie == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) { // 判断是否有内存卡
                    Toast.makeText(MainActivity.this, "没有sd卡",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                // 判断手势按下的位置是否是语音录制按钮的范围内
                if (event.getRawY() > btn_rc_Y
                        && (event.getRawX() > btn_rc_X && event.getRawX() < btn_rc_X + _llBtnVoice.getWidth())) {

                    // 显示录音显示UI层
                    _chatPopup.setVisibility(View.VISIBLE);
                    _llRcdHintLoading.setVisibility(View.VISIBLE); // 显示手机录音加载进度
                    _llRcdHintRcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    _llRcdHintTooshort.setVisibility(View.GONE); // 隐藏时间太短的提示
                    _handlerVoice.postDelayed(new Runnable() { // 开始后，响应处理
                        public void run() {
                            if (!_isShosrt) {
                                _llRcdHintLoading.setVisibility(View.GONE); // 隐藏手机录音加载进度
                                _llRcdHintRcding.setVisibility(View.VISIBLE); // 显示手机录音状态
                            }
                        }
                    }, 300);

                    _llVoice.setVisibility(View.VISIBLE);
                    _llCancelVoice.setVisibility(View.GONE);

                    _startVoiceT = System.currentTimeMillis(); // 获得开始录音时间
                    _voiceName = _startVoiceT + ".aac"; // aac  安卓、苹果公用格式
                    _start(_voiceName); // 开始录制 2015/7/21 14:15
                    _flagVocie = 2; // 标志正在录音

                    // 限时 2016/11/7 17:06
                    TimerTask _task = new TimerTask() {
                        @Override
                        public void run() {
                            _endVoiceT = System.currentTimeMillis(); // 获得结束录音时间
                            int time = (int) ((_endVoiceT - _startVoiceT) / 1000);
                            if (time >= _limitTime) {
                                _timer.cancel();
                                _timer = null;
                                _handlerUpload.sendEmptyMessage(0);
                            }
                        }
                    };

                    _timer = new Timer();
                    _timer.schedule(_task, 0, 1000);
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP && _flagVocie == 2) { // 松开手势时执行录制完成
                if (event.getRawY() >= del_Y
                        && event.getRawY() <= del_Y + _llCancelVoice.getHeight()
                        && event.getRawX() >= del_x
                        && event.getRawX() <= del_x + _llCancelVoice.getWidth()) { // 在取消布局的范围内，取消发送

                    // 显示录音显示UI层
                    _chatPopup.setVisibility(View.GONE);
                    _llVoice.setVisibility(View.VISIBLE);
                    _llCancelVoice.setVisibility(View.GONE);

                    _timer.cancel();
                    _timer = null;

                    _stop(); // 停止录音
                    _flagVocie = 1;
                    File file = new File(_sensorMeter.ARM_PATH + _voiceName);
                    if (file.exists()) { // 判断是否存在
                        file.delete(); // 存在则删除
                    }
                } else { // 将音频发送出去 2015/7/21 13:53
                    _llRcdHintRcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    _timer.cancel();
                    _timer = null;
                    _stop(); // 停止录音
                    _endVoiceT = System.currentTimeMillis(); // 获得结束录音时间
                    _flagVocie = 1;
                    int time = (int) ((_endVoiceT - _startVoiceT) / 1000);
                    if (time < 1) { // 判断时间长度 2015/7/21 13:59
                        _isShosrt = true;
                        _llRcdHintLoading.setVisibility(View.GONE);
                        _llRcdHintRcding.setVisibility(View.GONE);
                        _llRcdHintTooshort.setVisibility(View.VISIBLE);
                        _handlerVoice.postDelayed(new Runnable() {
                            public void run() {
                                _llRcdHintTooshort
                                        .setVisibility(View.GONE);
                                _chatPopup.setVisibility(View.GONE);
                                _isShosrt = false;
                            }
                        }, 500);

                        File file = new File(_sensorMeter.ARM_PATH + _voiceName);
                        if (file.exists()) { // 判断是否存在
                            file.delete(); // 存在则删除
                        }

                        return false;
                    }

                    // 处理发送音频 2015/7/21 13:59
                    _chatPopup.setVisibility(View.GONE); // 隐藏录音显示UI层
                    List<String> lstPaths = new ArrayList<>();
                    lstPaths.add(_sensorMeter.ARM_PATH + _voiceName);
                    _uploadVoice(lstPaths, time); // 上传
                }
            }

            // 手势按下的位置不在语音录制按钮的范围内
            if (!(event.getRawY() > btn_rc_Y
                    && event.getRawY() < btn_rc_Y + _llBtnVoice.getHeight()
                    && event.getRawX() > btn_rc_X
                    && event.getRawX() < btn_rc_X + _llBtnVoice.getWidth()) && _flagVocie == 2) {

                Animation mLitteAnimation = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(MainActivity.this,
                        R.anim.cancel_rc2);

                // 隐藏录音显示UI层
                _llVoice.setVisibility(View.GONE);
                _llCancelVoice.setVisibility(View.VISIBLE);
                _llCancelVoice.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
                if (event.getRawY() >= del_Y
                        && event.getRawY() <= del_Y + _llCancelVoice.getHeight()
                        && event.getRawX() >= del_x
                        && event.getRawX() <= del_x + _llCancelVoice.getWidth()) { // 如果在取消布局内，则变换取消按钮的背景
                    _llCancelVoice.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
                    _imgX.startAnimation(mLitteAnimation);
                    _imgX.startAnimation(mBigAnimation);
                }
            } else {
                _llVoice.setVisibility(View.VISIBLE); // 显示录音机
                _llCancelVoice.setVisibility(View.GONE);
                _llCancelVoice.setBackgroundResource(0);
            }

        }

        return super.onTouchEvent(event);
    }

    /**
     * 发送语音 2015/7/29 9:35
     * @param lstPaths 语音路径
     * @param time 时间长度
     */
    private void _uploadVoice(List<String> lstPaths, int time) {
        for(String path:lstPaths) {
            System.out.println("===================  path  " + path);
            System.out.println("===================  time  " + time);
            _playMusic(path);
        }
    }

    /**
     * 播放音频 2016/11/7 14:51
     * @param name 路径
     */
    private void _playMusic(String name) {
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }

            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(name);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {

                }
            });

        } catch (Exception e) {
        }

    }

    private void _stopMusic() {
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer = null;
            }

        } catch (Exception e) {
        }
    }

    private static final int POLL_INTERVAL = 200; // 200毫秒更新一次

    /**
     * 开始录制后，响应的手柄 2015/7/21 14:09
     */
    private Runnable _mPollTask = new Runnable() {
        public void run() {
            double amp = _sensorMeter.getAmplitude();
            _updateDisplay(amp);
            _handlerVoice.postDelayed(_mPollTask, POLL_INTERVAL);
        }
    };

    /**
     * 开始录制 2015/7/21 14:09
     * @param name 音频名称
     */
    private void _start(String name) {
        _sensorMeter.start(name);
        _handlerVoice.postDelayed(_mPollTask, POLL_INTERVAL);
    }

    /**
     * 停止录制 2015/7/21 14:09
     */
    private void _stop() {
        _handlerVoice.removeCallbacks(_mPollTask);
        _sensorMeter.stop();
        _btnModeVolume.setImageResource(R.drawable.amp1);
        _isBeginVocie = false;
    }

    /**
     * 更新录制的音量大小的图片状态 2015/1/26 18:55
     * @param signalEMA
     */
    private void _updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                _btnModeVolume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                _btnModeVolume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                _btnModeVolume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                _btnModeVolume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                _btnModeVolume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                _btnModeVolume.setImageResource(R.drawable.amp6);
                break;
            default:
                _btnModeVolume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this._stopMusic();
    }
}
