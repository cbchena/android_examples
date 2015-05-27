package com.example.voice_rcd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */

    private Button mBtnSend; // 发送按钮
    private TextView mBtnRcd; // 输入音频按钮
    private Button mBtnBack; // 返回按钮
    private EditText mEditTextContent; // 输入框
    private RelativeLayout mBottom;
    private ListView mListView; // 聊天信息显示列表
    private ChatMsgViewAdapter mAdapter; // 显示聊天信息列表的适配器

    private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>(); // 存放聊天数据

    private boolean isShosrt = false;
    private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
            voice_rcd_hint_tooshort;
    private ImageView img1, sc_img1;
    private SoundMeter mSensor; // 音频管理器
    private View rcChat_popup;
    private LinearLayout del_re;
    private ImageView chatting_mode_btn, volume;
    private boolean btn_vocie = false; // 是否显示发送音频模式
    private int flag = 1;
    private Handler mHandler = new Handler();
    private String voiceName;
    private long startVoiceT, endVoiceT;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // 初始化视图
        initView();

        initData();
    }

    /**
     * 初始化视图 2015/1/26 16:50
     */
    public void initView() {
        mListView = (ListView) findViewById(R.id.listview); // 列表视图
        mBtnSend = (Button) findViewById(R.id.btn_send); // 发送按钮
        mBtnRcd = (TextView) findViewById(R.id.btn_rcd); // 发送音频按钮
        mBtnSend.setOnClickListener(this); // 绑定点击事件

        mBtnBack = (Button) findViewById(R.id.btn_back); // 返回按钮
        mBtnBack.setOnClickListener(this);

        mBottom = (RelativeLayout) findViewById(R.id.btn_bottom); // 底部布局，文本输入

        chatting_mode_btn = (ImageView) this.findViewById(R.id.ivPopUp); // 切换模式的图片

        volume = (ImageView) this.findViewById(R.id.volume);

        rcChat_popup = this.findViewById(R.id.rcChat_popup); // 录音显示UI层

        img1 = (ImageView) this.findViewById(R.id.img1); // 关闭按钮
        sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
        del_re = (LinearLayout) this.findViewById(R.id.del_re); // 取消布局

        voice_rcd_hint_rcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding); // 显示手机录音状态

        voice_rcd_hint_loading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading); // 录音开始前的加载准备

        voice_rcd_hint_tooshort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort); // 显示时间太短的提示
        mSensor = new SoundMeter();
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);

        //语音文字切换按钮
        chatting_mode_btn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (btn_vocie) { // 是否显示音频模式
                    mBtnRcd.setVisibility(View.GONE); // 隐藏发送音频按钮
                    mBottom.setVisibility(View.VISIBLE); // 显示文本输入
                    btn_vocie = false;
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_msg_btn); // 更换模式切换图片

                } else {
                    mBtnRcd.setVisibility(View.VISIBLE);
                    mBottom.setVisibility(View.GONE);
                    chatting_mode_btn
                            .setImageResource(R.drawable.chatting_setmode_voice_btn);
                    btn_vocie = true;
                }
            }
        });

        mBtnRcd.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                //按下语音录制按钮时返回false执行父类OnTouch
                return false;
            }
        });
    }

    private String[] msgArray = new String[] { "有人就有恩怨","有恩怨就有江湖","人就是江湖","你怎么退出？ ","生命中充满了巧合","两条平行线也会有相交的一天。"};

    private String[] dataArray = new String[] { "2012-10-31 18:00",
            "2012-10-31 18:10", "2012-10-31 18:11", "2012-10-31 18:20",
            "2012-10-31 18:30", "2012-10-31 18:35"};
    private final static int COUNT = 6;

    /**
     * 初始化模拟数据 2015/1/26 17:00
     */
    public void initData() {
        for (int i = 0; i < COUNT; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(dataArray[i]);
            if (i % 2 == 0) {
                entity.setName("白富美");
                entity.setMsgType(true);
            } else {
                entity.setName("高富帅");
                entity.setMsgType(false);
            }

            entity.setText(msgArray[i]);
            mDataArrays.add(entity);
        }

        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);

    }

    /**
     * 点击事件 2015/1/26 17:00
     * @param v
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_send: // 点击发送
                send();
                break;
            case R.id.btn_back: // 点击返回
                finish();
                break;
        }
    }

    /**
     * 发送文本数据 2015/1/26 17:01
     */
    private void send() {
        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(getDate());
            entity.setName("高富帅");
            entity.setMsgType(false);
            entity.setText(contString);

            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();

            mEditTextContent.setText("");

            // 显示列表动态选择最后一条信息
            mListView.setSelection(mListView.getCount() - 1);
        }
    }

    /**
     * 获取当前时间 2015/1/26 17:06
     * @return
     */
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));

        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":"
                + mins);

        return sbBuffer.toString();
    }

    /**
     * 按下语音录制按钮时 2015/1/26 17:10
     * @param event 触发事件
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (!Environment.getExternalStorageDirectory().exists()) {
            Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
            return false;
        }

        if (btn_vocie) { // 是否显示发送音频模式
            System.out.println("1");
            int[] location = new int[2];
            mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int[] del_location = new int[2];
            del_re.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) { // 判断是否有内存卡
                    Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
                    return false;
                }

                System.out.println("2");
                if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) { // 判断手势按下的位置是否是语音录制按钮的范围内
                    System.out.println("3");
                    mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed); // 切换音频按钮图片显示状态
                    rcChat_popup.setVisibility(View.VISIBLE); // 显示录音显示UI层，看到的效果就是一个透明底层的手机状态
                    voice_rcd_hint_loading.setVisibility(View.VISIBLE); // 显示手机录音加载进度
                    voice_rcd_hint_rcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    voice_rcd_hint_tooshort.setVisibility(View.GONE); // 隐藏时间太短的提示
                    mHandler.postDelayed(new Runnable() { // 开始后，响应处理
                        public void run() {
                            if (!isShosrt) {
                                voice_rcd_hint_loading.setVisibility(View.GONE); // 隐藏手机录音加载进度
                                voice_rcd_hint_rcding.setVisibility(View.VISIBLE); // 显示手机录音状态
                            }
                        }
                    }, 300);

                    img1.setVisibility(View.VISIBLE); // 显示关闭按钮
                    del_re.setVisibility(View.GONE); // 隐藏取消布局
//                    startVoiceT = SystemClock.currentThreadTimeMillis(); 2015/1/5 17:18  不准
                    startVoiceT = System.currentTimeMillis();
                    voiceName = startVoiceT + ".amr"; // 拼接得到音频名称
                    start(voiceName); // 开始录制
                    flag = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) { // 松开手势时执行录制完成
                System.out.println("4");
                mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor); // 切换音频按钮图片显示状态
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) { // 在取消布局的范围内
                    rcChat_popup.setVisibility(View.GONE); // 隐藏录音显示UI层，看到的效果就是一个透明底层的手机状态
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    stop(); // 停止录音
                    flag = 1;
                    File file = new File(android.os.Environment.getExternalStorageDirectory()+"/"
                            + voiceName);
                    if (file.exists()) { // 判断是否存在
                        file.delete(); // 存在则删除
                    }
                } else { // 不属于取消，则发送音频出去 2015/1/26 17:45

                    voice_rcd_hint_rcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    stop(); // 停止录音
//                    endVoiceT = SystemClock.currentThreadTimeMillis();  2015/1/5 17:18  不准
                    endVoiceT = System.currentTimeMillis();
                    flag = 1;

                    int time = (int) ((endVoiceT - startVoiceT) / 1000);
                    if (time < 1) { // 判断时间长度
                        isShosrt = true;
                        voice_rcd_hint_loading.setVisibility(View.GONE);
                        voice_rcd_hint_rcding.setVisibility(View.GONE);
                        voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
                        mHandler.postDelayed(new Runnable() {
                            public void run() {
                                voice_rcd_hint_tooshort
                                        .setVisibility(View.GONE);
                                rcChat_popup.setVisibility(View.GONE);
                                isShosrt = false;
                            }
                        }, 500);
                        return false;
                    }

                    // 发送音频 2015/1/26 17:46
                    ChatMsgEntity entity = new ChatMsgEntity();
                    entity.setDate(getDate());
                    entity.setName("高富帅");
                    entity.setMsgType(false); // 设置信息类型 false在右边  true在左边
                    entity.setTime(time+"\"");
                    entity.setText(voiceName);
                    mDataArrays.add(entity);
                    mAdapter.notifyDataSetChanged();
                    mListView.setSelection(mListView.getCount() - 1);
                    rcChat_popup.setVisibility(View.GONE);

                }
            }

            if (event.getY() < btn_rc_Y) {//手势按下的位置不在语音录制按钮的范围内
                System.out.println("5");
                Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.cancel_rc);
                Animation mBigAnimation = AnimationUtils.loadAnimation(this,
                        R.anim.cancel_rc2);
                img1.setVisibility(View.GONE); // 隐藏关闭按钮
                del_re.setVisibility(View.VISIBLE); // 显示取消布局
                del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) { // 如果在取消布局内，则变换取消按钮的背景
                    del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
                    sc_img1.startAnimation(mLitteAnimation);
                    sc_img1.startAnimation(mBigAnimation);
                }
            } else {

                img1.setVisibility(View.VISIBLE);
                del_re.setVisibility(View.GONE);
                del_re.setBackgroundResource(0);
            }
        }
        return super.onTouchEvent(event);
    }

    private static final int POLL_INTERVAL = 300;

    private Runnable mSleepTask = new Runnable() {
        public void run() {
            stop();
        }
    };

    /**
     * 开始录制后，响应的手柄 2015/1/26 18:56
     */
    private Runnable mPollTask = new Runnable() {
        public void run() {
            double amp = mSensor.getAmplitude();
            updateDisplay(amp);
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);

        }
    };

    /**
     * 开始录制 2015/1/26 18:54
     * @param name
     */
    private void start(String name) {
        mSensor.start(name);
        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
    }

    /**
     * 停止录制 2015/1/26 18:54
     */
    private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        mSensor.stop();
        volume.setImageResource(R.drawable.amp1);
    }

    /**
     * 更新录制的音量大小的图片状态 2015/1/26 18:55
     * @param signalEMA
     */
    private void updateDisplay(double signalEMA) {

        switch ((int) signalEMA) {
            case 0:
            case 1:
                volume.setImageResource(R.drawable.amp1);
                break;
            case 2:
            case 3:
                volume.setImageResource(R.drawable.amp2);

                break;
            case 4:
            case 5:
                volume.setImageResource(R.drawable.amp3);
                break;
            case 6:
            case 7:
                volume.setImageResource(R.drawable.amp4);
                break;
            case 8:
            case 9:
                volume.setImageResource(R.drawable.amp5);
                break;
            case 10:
            case 11:
                volume.setImageResource(R.drawable.amp6);
                break;
            default:
                volume.setImageResource(R.drawable.amp7);
                break;
        }
    }

    public void head_xiaohei(View v) { // 标题栏 返回按钮

    }
}