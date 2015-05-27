package com.way.pattern;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.way.view.LockPatternUtils;
import com.way.view.LockPatternView;
import com.way.view.LockPatternView.Cell;
import com.way.view.LockPatternView.DisplayMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建图案锁界面 2015/1/19 15:29
 */
public class CreateGesturePasswordActivity extends Activity implements
		OnClickListener {
	private static final int ID_EMPTY_MESSAGE = -1;
	private static final String KEY_UI_STAGE = "uiStage";
	private static final String KEY_PATTERN_CHOICE = "chosenPattern";
	private LockPatternView mLockPatternView;
	private Button mFooterRightButton;
	private Button mFooterLeftButton;
	protected TextView mHeaderText;
	protected List<LockPatternView.Cell> mChosenPattern = null;
	private Toast mToast;
	private Stage mUiStage = Stage.Introduction;
	private View mPreviewViews[][] = new View[3][3];
	/**
	 * The patten used during the help screen to show how to draw a pattern.
	 */
	private final List<LockPatternView.Cell> mAnimatePattern = new ArrayList<LockPatternView.Cell>();

	/**
	 * The states of the left footer button.
     * 左边按钮 2015/1/19 16:41
     */
	enum LeftButtonMode {
		Cancel(android.R.string.cancel, true), // 取消
        CancelDisabled(android.R.string.cancel, false), // 取消，禁用
        Retry(R.string.lockpattern_retry_button_text, true), // 重试
        RetryDisabled(R.string.lockpattern_retry_button_text, false), // 重试禁用
        Gone(ID_EMPTY_MESSAGE, false); // 忽略，不使用

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		LeftButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}

	/**
	 * The states of the right button.
     * 右边按钮
	 */
	enum RightButtonMode {
		Continue(R.string.lockpattern_continue_button_text, true), // 继续
        ContinueDisabled(R.string.lockpattern_continue_button_text, false), // 继续，禁用
        Confirm(R.string.lockpattern_confirm_button_text, true), // 确认
        ConfirmDisabled(R.string.lockpattern_confirm_button_text, false), // 确认禁用
        Ok(android.R.string.ok, true); // 确定

		/**
		 * @param text
		 *            The displayed text for this mode.
		 * @param enabled
		 *            Whether the button should be enabled.
		 */
		RightButtonMode(int text, boolean enabled) {
			this.text = text;
			this.enabled = enabled;
		}

		final int text;
		final boolean enabled;
	}

	/**
	 * Keep track internally of where the user is in choosing a pattern.
     * 支持用户选测的的各种场景模式
	 */
	protected enum Stage {

        // 第一次进入
		Introduction(R.string.lockpattern_recording_intro_header,
				LeftButtonMode.Cancel, RightButtonMode.ContinueDisabled,
				ID_EMPTY_MESSAGE, true),

        // 帮助模式
        HelpScreen(R.string.lockpattern_settings_help_how_to_record,
				LeftButtonMode.Gone, RightButtonMode.Ok, ID_EMPTY_MESSAGE,
				false),

        // 输入图案太短
        ChoiceTooShort( R.string.lockpattern_recording_incorrect_too_short,
				LeftButtonMode.Retry, RightButtonMode.ContinueDisabled,
				ID_EMPTY_MESSAGE, true),

        // 第一次输入有效
        FirstChoiceValid(R.string.lockpattern_pattern_entered_header,
				LeftButtonMode.Retry, RightButtonMode.Continue,
				ID_EMPTY_MESSAGE, false),

        // 需要再次输入
        NeedToConfirm(R.string.lockpattern_need_to_confirm, LeftButtonMode.Cancel,
				RightButtonMode.ConfirmDisabled, ID_EMPTY_MESSAGE, true),

        // 输入有误
        ConfirmWrong(R.string.lockpattern_need_to_unlock_wrong,
				LeftButtonMode.Cancel, RightButtonMode.ConfirmDisabled,
				ID_EMPTY_MESSAGE, true),

        // 两次输入一致，确认结束
        ChoiceConfirmed(R.string.lockpattern_pattern_confirmed_header,
				LeftButtonMode.Cancel, RightButtonMode.Confirm,
				ID_EMPTY_MESSAGE, false);

		/**
		 * @param headerMessage
		 *            The message displayed at the top.
		 * @param leftMode
		 *            The mode of the left button.
		 * @param rightMode
		 *            The mode of the right button.
		 * @param footerMessage
		 *            The footer message.
		 * @param patternEnabled
		 *            Whether the pattern widget is enabled.
		 */
		Stage(int headerMessage, LeftButtonMode leftMode,
				RightButtonMode rightMode, int footerMessage,
				boolean patternEnabled) {
			this.headerMessage = headerMessage;
			this.leftMode = leftMode;
			this.rightMode = rightMode;
			this.footerMessage = footerMessage;
			this.patternEnabled = patternEnabled;
		}

		final int headerMessage;
		final LeftButtonMode leftMode;
		final RightButtonMode rightMode;
		final int footerMessage;
		final boolean patternEnabled;
	}

    /**
     * 吐司提示 2015/1/19 16:37
     * @param message
     */
	private void showToast(CharSequence message) {
		if (null == mToast) {
			mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(message);
		}

		mToast.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gesturepassword_create);

		// 初始化演示动画
		mAnimatePattern.add(LockPatternView.Cell.of(0, 0));
		mAnimatePattern.add(LockPatternView.Cell.of(0, 1));
		mAnimatePattern.add(LockPatternView.Cell.of(1, 1));
		mAnimatePattern.add(LockPatternView.Cell.of(2, 1));
		mAnimatePattern.add(LockPatternView.Cell.of(2, 2));

		mLockPatternView = (LockPatternView) this
				.findViewById(R.id.gesturepwd_create_lockview);
		mHeaderText = (TextView) findViewById(R.id.gesturepwd_create_text);
		mLockPatternView.setOnPatternListener(mChooseNewLockPatternListener);
		mLockPatternView.setTactileFeedbackEnabled(true);

		mFooterRightButton = (Button) this.findViewById(R.id.right_btn);
		mFooterLeftButton = (Button) this.findViewById(R.id.reset_btn);
		mFooterRightButton.setOnClickListener(this);
		mFooterLeftButton.setOnClickListener(this);

		initPreviewViews(); // 初始化显示用户输入的小界面

        if (savedInstanceState == null) {
			updateStage(Stage.Introduction);
			updateStage(Stage.HelpScreen);
		} else { // 从先前的状态恢复 2015/1/19 16:33
            final String patternString = savedInstanceState
					.getString(KEY_PATTERN_CHOICE);
			if (patternString != null) {
				mChosenPattern = LockPatternUtils
						.stringToPattern(patternString);
			}

			updateStage(Stage.values()[savedInstanceState.getInt(KEY_UI_STAGE)]);
		}

	}

    /**
     * 初始化显示用户输入的小界面 2015/1/19 16:29
     */
	private void initPreviewViews() {
		mPreviewViews = new View[3][3];
		mPreviewViews[0][0] = findViewById(R.id.gesturepwd_setting_preview_0);
		mPreviewViews[0][1] = findViewById(R.id.gesturepwd_setting_preview_1);
		mPreviewViews[0][2] = findViewById(R.id.gesturepwd_setting_preview_2);
		mPreviewViews[1][0] = findViewById(R.id.gesturepwd_setting_preview_3);
		mPreviewViews[1][1] = findViewById(R.id.gesturepwd_setting_preview_4);
		mPreviewViews[1][2] = findViewById(R.id.gesturepwd_setting_preview_5);
		mPreviewViews[2][0] = findViewById(R.id.gesturepwd_setting_preview_6);
		mPreviewViews[2][1] = findViewById(R.id.gesturepwd_setting_preview_7);
		mPreviewViews[2][2] = findViewById(R.id.gesturepwd_setting_preview_8);
	}

	private void updatePreviewViews() {
		if (mChosenPattern == null)
			return;

		Log.i("way", "result = " + mChosenPattern.toString());
		for (LockPatternView.Cell cell : mChosenPattern) {
			Log.i("way", "cell.getRow() = " + cell.getRow()
					+ ", cell.getColumn() = " + cell.getColumn());
			mPreviewViews[cell.getRow()][cell.getColumn()]
					.setBackgroundResource(R.drawable.gesture_create_grid_selected);

		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_UI_STAGE, mUiStage.ordinal());
		if (mChosenPattern != null) {
			outState.putString(KEY_PATTERN_CHOICE,
					LockPatternUtils.patternToString(mChosenPattern));
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mUiStage == Stage.HelpScreen) {
				updateStage(Stage.Introduction);
				return true;
			}
		}
		if (keyCode == KeyEvent.KEYCODE_MENU && mUiStage == Stage.Introduction) {
			updateStage(Stage.HelpScreen);
			return true;
		}
		return false;
	}

	private Runnable mClearPatternRunnable = new Runnable() {
		public void run() {
			mLockPatternView.clearPattern();
		}
	};

    /**
     * 输入图案监听器 2015/1/19 16:16
     */
	protected LockPatternView.OnPatternListener mChooseNewLockPatternListener = new LockPatternView.OnPatternListener() {

        /**
         * 检测到用户开始输入 2015/1/19 16:16
         */
		public void onPatternStart() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
			patternInProgress();
		}

		public void onPatternCleared() {
			mLockPatternView.removeCallbacks(mClearPatternRunnable);
		}

        /**
         * 检测到用户的输入 2015/1/19 16:16
         * @param pattern
         */
		public void onPatternDetected(List<LockPatternView.Cell> pattern) {
			if (pattern == null)
				return;

			// Log.i("way", "result = " + pattern.toString());
			if (mUiStage == Stage.NeedToConfirm
					|| mUiStage == Stage.ConfirmWrong) { // 输入错误或者需要继续再次确认
				if (mChosenPattern == null)
					throw new IllegalStateException(
							"null chosen pattern in stage 'need to confirm");

				if (mChosenPattern.equals(pattern)) { // 两次输入图案相等，则确认结束
					updateStage(Stage.ChoiceConfirmed);
				} else {
					updateStage(Stage.ConfirmWrong);
				}
			} else if (mUiStage == Stage.Introduction
					|| mUiStage == Stage.ChoiceTooShort) { // 属于第一次或者输入太短
				if (pattern.size() < LockPatternUtils.MIN_LOCK_PATTERN_SIZE) { // 设置数量太小，重置
					updateStage(Stage.ChoiceTooShort);
				} else {
					mChosenPattern = new ArrayList<LockPatternView.Cell>(pattern); // 保存第一次输入结果
					updateStage(Stage.FirstChoiceValid); // 标志第一次输入有效
				}
			} else {
				throw new IllegalStateException("Unexpected stage " + mUiStage
						+ " when " + "entering the pattern.");
			}
		}

		public void onPatternCellAdded(List<Cell> pattern) {

		}

        /**
         * 输入过程调用1次 2015/1/19 16:25
         */
		private void patternInProgress() {
			mHeaderText.setText(R.string.lockpattern_recording_inprogress);
			mFooterLeftButton.setEnabled(false);
			mFooterRightButton.setEnabled(false);
		}
	};

    /**
     * 更换场景 2015/1/19 16:26
     * @param stage
     */
	private void updateStage(Stage stage) {
		mUiStage = stage;
		if (stage == Stage.ChoiceTooShort) { // 选择太短，进行提示
			mHeaderText.setText(getResources().getString(stage.headerMessage,
					LockPatternUtils.MIN_LOCK_PATTERN_SIZE));
		} else {
			mHeaderText.setText(stage.headerMessage);
		}

		if (stage.leftMode == LeftButtonMode.Gone) {
			mFooterLeftButton.setVisibility(View.GONE);
		} else {
			mFooterLeftButton.setVisibility(View.VISIBLE);
			mFooterLeftButton.setText(stage.leftMode.text);
			mFooterLeftButton.setEnabled(stage.leftMode.enabled);
		}

		mFooterRightButton.setText(stage.rightMode.text);
		mFooterRightButton.setEnabled(stage.rightMode.enabled);

		// same for whether the patten is enabled
		if (stage.patternEnabled) {
			mLockPatternView.enableInput();
		} else {
			mLockPatternView.disableInput();
		}

		mLockPatternView.setDisplayMode(DisplayMode.Correct);

		switch (mUiStage) {
		case Introduction: // 第一次输入界面
			mLockPatternView.clearPattern();
			break;
		case HelpScreen: // 帮助场景
			mLockPatternView.setPattern(DisplayMode.Animate, mAnimatePattern);
			break;
		case ChoiceTooShort: // 输入太短
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case FirstChoiceValid: // 第一次输入有效
			break;
		case NeedToConfirm: // 需要再次确认
			mLockPatternView.clearPattern();
			updatePreviewViews();
			break;
		case ConfirmWrong: // 输入出错
			mLockPatternView.setDisplayMode(DisplayMode.Wrong);
			postClearPatternRunnable();
			break;
		case ChoiceConfirmed: // 相等，确认结束
			break;
		}

	}

	// clear the wrong pattern unless they have started a new one
	// already
	private void postClearPatternRunnable() {
		mLockPatternView.removeCallbacks(mClearPatternRunnable);
		mLockPatternView.postDelayed(mClearPatternRunnable, 2000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset_btn: // 重置
			if (mUiStage.leftMode == LeftButtonMode.Retry) {
				mChosenPattern = null;
				mLockPatternView.clearPattern();
				updateStage(Stage.Introduction);
			} else if (mUiStage.leftMode == LeftButtonMode.Cancel) {
				// They are canceling the entire wizard
				finish();
			} else {
				throw new IllegalStateException(
						"left footer button pressed, but stage of " + mUiStage
								+ " doesn't make sense");
			}

			break;
		case R.id.right_btn: // 确认
			if (mUiStage.rightMode == RightButtonMode.Continue) { // 点击【继续】按钮
				if (mUiStage != Stage.FirstChoiceValid) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.FirstChoiceValid + " when button is "
							+ RightButtonMode.Continue);
				}
				updateStage(Stage.NeedToConfirm); // 修改场景，需要继续配置
			} else if (mUiStage.rightMode == RightButtonMode.Confirm) { // 点击【确认】按钮
				if (mUiStage != Stage.ChoiceConfirmed) {
					throw new IllegalStateException("expected ui stage "
							+ Stage.ChoiceConfirmed + " when button is "
							+ RightButtonMode.Confirm);
				}

				saveChosenPatternAndFinish(); // 保存更改，并结束该界面
			} else if (mUiStage.rightMode == RightButtonMode.Ok) { // 在帮助模式下，点击【确定】按钮
				if (mUiStage != Stage.HelpScreen) {
					throw new IllegalStateException(
							"Help screen is only mode with ok button, but "
									+ "stage is " + mUiStage);
				}

                // 清除图案，并保持显示正确模式，让用户输入图案
				mLockPatternView.clearPattern();
				mLockPatternView.setDisplayMode(DisplayMode.Correct);
				updateStage(Stage.Introduction); // 更换场景
			}
			break;
		}
	}

    /**
     * 保存更改，并结束该界面 2015/1/19 16:15
     */
	private void saveChosenPatternAndFinish() {
		App.getInstance().getLockPatternUtils().saveLockPattern(mChosenPattern);
		showToast("密码设置成功");
		startActivity(new Intent(this,UnlockGesturePasswordActivity.class));
		finish();
	}
}
