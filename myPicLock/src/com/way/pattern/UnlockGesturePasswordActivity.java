package com.way.pattern;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.way.view.LockPatternUtils;
import com.way.view.LockPatternView;
import com.way.view.LockPatternView.Cell;

import java.util.List;

/**
 * 解锁图案界面 2015/1/19 15:26
 */
public class UnlockGesturePasswordActivity extends Activity {
	private LockPatternView mLockPatternView;
	private int mFailedPatternAttemptsSinceLastTimeout = 0;
	private CountDownTimer mCountdownTimer = null;
	private Handler mHandler = new Handler();
	private TextView mHeadTextView;
	private Animation mShakeAnim;

	private Toast mToast;

    /**
     * 吐司提示 2015/1/19 15:27
     * @param message
     */
	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			mToast.setGravity(Gravity.CENTER, 0, 0);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_unlock);

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_unlock_lockview);

        // 设置图案解锁的监听器
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);

        // 设置视图是否会使用触觉反馈，PS:每设置一个点，手机会震动一下
		mLockPatternView.setTactileFeedbackEnabled(true);

		mHeadTextView = (TextView) findViewById(R.id.gesturepwd_unlock_text);
		mShakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_x);

        // 尝试修改密码 2015/1/19 17:09
//        List<LockPatternView.Cell> mAnimatePattern = new ArrayList<Cell>();
//        mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
//        mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
//        mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
//        mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
//        mAnimatePattern.add(LockPatternView.Cell.of(2, 2));
//        App.getInstance().getLockPatternUtils().saveLockPattern(mAnimatePattern);
	}

	@Override
	protected void onResume() {
		super.onResume();

        // 检查是否用户已经存储了一个锁定模式，否，则打开是否创建解锁界面
        if (!App.getInstance().getLockPatternUtils().savedPatternExists()) {
			startActivity(new Intent(this, GuideGesturePasswordActivity.class));
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mCountdownTimer != null)
			mCountdownTimer.cancel();
	}

    /**
     * 清除图案 2015/1/19 15:31
     */
	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern(); // 清除图案
		}
	};

    /**
     * 图案解锁的监听器 2015/1/19 15:31
     */
	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        /**
         * 检测到用户开始输入图案 2015/1/19 15:31
         */
		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

        /**
         * 图案已经被清除 2015/1/19 15:32
         */
		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

        /**
         * 检测到用户输入的图案 2015/1/19 15:33
         * @param pattern
         */
		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
            if (pattern == null)
				return;

            // 检查用户此次输入的图案与之前设置的图案是否一致
			if (App.getInstance().getLockPatternUtils().checkPattern(pattern)) {

                // 设置显示的模式为正确模式
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Correct);

                // 打开界面
				Intent intent = new Intent(UnlockGesturePasswordActivity.this,
						GuideGesturePasswordActivity.class);
				startActivity(intent);
				showToast("解锁成功");
				finish();
			} else { // 不一致

                // 设置显示的模式为错误模式
				mLockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong);

                // 检查输入长度
				if (pattern.size() >= LockPatternUtils.MIN_PATTERN_REGISTER_FAIL) {
					mFailedPatternAttemptsSinceLastTimeout++; // 允许出错的次数

                    // 获取当前还剩余允许出错的次数
					int retry = LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT
							- mFailedPatternAttemptsSinceLastTimeout;
					if (retry >= 0) {
						if (retry == 0)
							showToast("您已5次输错密码，请30秒后再试");

						mHeadTextView.setText("密码错误，还可以再输入" + retry + "次");
						mHeadTextView.setTextColor(Color.RED);
						mHeadTextView.startAnimation(mShakeAnim);
					}

				}else{ // 长度不够
					showToast("输入长度不够，请重试");
				}

                // 当次数已经大于5次，限制一段时间后，重试 2015/1/19 15:46
                if (mFailedPatternAttemptsSinceLastTimeout >= LockPatternUtils.FAILED_ATTEMPTS_BEFORE_TIMEOUT) {
					mHandler.postDelayed(attemptLockout, 200); // 延迟200毫秒后进行
				} else {

                    // 清除图案
					mLockPatternView.postDelayed(mClearPatternRunnable, 1500);
				}
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

		private void patternInProgress() {
		}
	};

    /**
     * 限制一段时间无法输入图案 2015/1/19 15:48
     */
	Runnable attemptLockout = new Runnable() {

		@Override
		public void run() {
			mLockPatternView.clearPattern(); // 清除图案
			mLockPatternView.setEnabled(false); // 限制输入

            // 定时器：参数：总毫秒数，每1000毫秒跳动1次 2015/1/19 15:51
            mCountdownTimer = new CountDownTimer(
					LockPatternUtils.FAILED_ATTEMPT_TIMEOUT_MS + 1, 1000) {

                /**
                 * 心跳 2015/1/19 15:50
                 * @param millisUntilFinished  剩余的毫秒数
                 */
				@Override
				public void onTick(long millisUntilFinished) {
					int secondsRemaining = (int) (millisUntilFinished / 1000);
                    if (secondsRemaining > 0) {
						mHeadTextView.setText(secondsRemaining + " 秒后重试");
					}
				}

				@Override
				public void onFinish() {

                    // 时间到，重置
                    mHeadTextView.setText("请绘制手势密码");
                    mHeadTextView.setTextColor(Color.WHITE);
					mLockPatternView.setEnabled(true);
					mFailedPatternAttemptsSinceLastTimeout = 0;
				}
			}.start();
		}
	};

}
