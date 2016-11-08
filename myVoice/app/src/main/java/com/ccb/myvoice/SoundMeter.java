package com.ccb.myvoice;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;

import java.io.File;
import java.io.IOException;

/**
 * 音频管理 2015/7/21 14:10
 */
public class SoundMeter {

	static final private double EMA_FILTER = 0.6;

    public final String ARM_PATH = Environment.getExternalStorageDirectory() + "/ml_home/armCache/";
	private MediaRecorder _mRecorder = null;
	private double _mEMA = 0.0;
    private boolean _isStarted = false; // 是否开始

    /**
     * 播放音频 2015/7/21 14:11
     * @param name 播放名称
     */
	public void start(String name) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}

		try {
			File file = new File(ARM_PATH);
			if (!file.exists()) { // 创建文件夹
				file.mkdirs();// 创建文件夹
			}

			if (_mRecorder == null) {
				_mRecorder = new MediaRecorder();
				_mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				_mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4); // 设置输出编码格式
				_mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC); // 设置音频格式为aac
				_mRecorder.setOutputFile(ARM_PATH + name);

				try {
					_mRecorder.prepare();
					_mRecorder.start();

					_mEMA = 0.0;
					_isStarted = true;
				} catch (IllegalStateException e) {
					System.out.print(e.getMessage());
				} catch (IOException e) {
					System.out.print(e.getMessage());
				}

			}
		} catch (Exception e) {}
	}

    /**
     * 停止音频播放 2015/7/21 14:11
     */
	public synchronized void stop() {
		try {
			if (_mRecorder != null && _isStarted) {
				_mRecorder.stop();
				_mRecorder.release();
				_mRecorder = null;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						_isStarted = false;
					}
				}, 500);
			}
		} catch (Exception e) { // 快速点击停止时，会出现stop failed的异常 2016/11/7 15:28
			if (_mRecorder != null) {
				_mRecorder.release();
				_mRecorder = null;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						_isStarted = false;
					}
				}, 500);
			}
		}
	}

	public void pause() {
		if (_mRecorder != null) {
			_mRecorder.stop();
		}
	}

	public void start() {
		if (_mRecorder != null && !_isStarted) {
            _isStarted = true;
            _mRecorder.start();
        }
	}

	public double getAmplitude() {
		if (_mRecorder != null)
			return (_mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		_mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * _mEMA;
		return _mEMA;
	}
}
