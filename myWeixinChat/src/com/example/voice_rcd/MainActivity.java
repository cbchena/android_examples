package com.example.voice_rcd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.example.voice_rcd.check_location.Location;
import com.example.voice_rcd.check_picture.CheckPictureActivity;
import com.example.voice_rcd.check_picture.copy.CopyPicture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天窗口 2015/2/2 16:42
 * PS:记得更改上传地址。
 */
public class MainActivity extends Activity implements OnClickListener {

    private Button _btnSend; // 发送按钮
    private ImageView _imgBtnMoreMenu; // 更多菜单按钮
    private TextView _btnRcd; // 输入音频按钮
    private Button _btnBack; // 返回按钮
    private EditText _editTextContent; // 输入框
    private RelativeLayout _llBottom;
    private ListView _listView; // 聊天信息显示列表
    private ChatMsgViewAdapter _lstmAdapter; // 显示聊天信息列表的适配器

    private boolean _isShosrt = false;

    private LinearLayout _llRcdHintLoading, _llRcdHintRcding,
    _llRcdHintTooshort, _llRcdMoreMenu; // 布局控件
    private ImageView img1, sc_img1;

    private Button _btnSendPic; // 发送图片按钮
    private Button _btnSendLoc; // 发送位置按钮

    private SoundMeter _sensorMeter; // 音频管理器
    private View _chatPopup; // 录音显示UI层
    private LinearLayout del_re; // // 取消布局
    private ImageView _btnModeWords, _btnModeVolume; // 切换文字、音频按钮
    private boolean _isVocie = false; // 是否显示音频按钮
    private int _flagVocie = 1; // 1 为可以开始录音   2 为正在录音
    private Handler _handlerVocie = new Handler(); // 处理录音进度
    private String _voiceName; // 音频名称
    private long _startVoiceT, _endVoiceT; // 记录开始、结束录音时间

    private List<ChatMsgEntity> _lstDataBk = new ArrayList<ChatMsgEntity>(); // 预存数据
    private List<ChatMsgEntity> _lstData = new ArrayList<ChatMsgEntity>(); // 存放聊天数据
    private List<ChatMsgEntity> _lstShowData = new ArrayList<ChatMsgEntity>(); // 存放显示的聊天数据
    private List<String> _lstDate;

    public DBChatManager get_dbChatManager() {
        return _dbChatManager;
    }

    private DBChatManager _dbChatManager; // 聊天信息表管理

    public String get_chatId() {
        return _chatId;
    }

    private String _chatId;
    private String _myName = "陈楚斌"; // 我的名称
    private String _otherName = "陈明溪"; // 聊天对象名
    private boolean _isOpenInput = false; // 是否打开输入框

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // 启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this._chatId = Const.CHAT_KEY_ZONE_DATE; // 设置获取信息日期列表Key
        this._dbChatManager = new DBChatManager(this, Const.DB_NAME);

        this._lstDate = ChatMsgDateManager.getInstance(this, this._chatId).getData();

        // 初始化视图
        initView();

        // 初始化数据
        initData();
    }

    private final Handler _handlerMsg = new Handler(); // 在主线程之外，使用Handler进行更新ui

    /**
     * 初始化视图 2015/1/26 16:50
     */
    public void initView() {
        _listView = (ListView) findViewById(R.id.listview); // 列表视图
        _listView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        if (_isOpenInput)
                            _closeInput(); // 关闭输入法 2015/2/2 11:40

                        _llRcdMoreMenu.setVisibility(View.GONE); // 隐藏更多菜单

                        break;
                }

                return false;
            }
        });

        _listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        _listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            private boolean _isScrollTop = false;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // 到达顶部并且不滚动时，加载新数据
                if (_isScrollTop
                        && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    _isScrollTop = false;

                    // 判断待显示数据是否还足够显示两次，不足够则存储数据
                    if (_lstmAdapter.count + 20 > _lstData.size())
                        _getData();

                    if (_lstmAdapter.count <= _lstData.size()) {
                        _handlerMsg.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                int number = (_lstmAdapter.count + 10 <= _lstData.size()) ? 10 : (_lstData.size() - _lstmAdapter.count);
                                // 将时间从上到下，从旧到新依次显示 2015/2/2 20:44
                                for (int i = 0; i < number; i++) {
                                    // 1 2 3 4 5 6
                                    _lstShowData.add(0, _lstData.get(_lstData.size() - _lstmAdapter.count - 1 - i));
                                }

                                _lstmAdapter.count += number;
                                _lstmAdapter.notifyDataSetChanged();
                                _listView.setSelection(number - 1);

                            }
                        }, 1000);
                    }

                }
            }

            /**
             * 滚动监听事件 2015/2/2 10:32
             * @param absListView 控件
             * @param mFirstVisibleItem 滚动条上面看不见的还有几条
             * @param mVisibleItemCount 当前看得到的listview显示的item有几条
             * @param mTotalItemCount 当前listview总共的有几条
             */
            @Override
            public void onScroll(AbsListView absListView, int mFirstVisibleItem, int mVisibleItemCount, int mTotalItemCount) {
                if (mFirstVisibleItem == 0 && !_isScrollTop
                        && (_lstmAdapter != null && _lstmAdapter.count < _lstData.size())) {// 到达顶部
                    _isScrollTop = true;
                    _listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
                }
            }
        });

        _lstmAdapter = new ChatMsgViewAdapter(this, _lstShowData); // 适配器
        _listView.setAdapter(_lstmAdapter);

        _btnSend = (Button) findViewById(R.id.btn_send); // 发送按钮
        _btnSend.setOnClickListener(this); // 绑定点击事件

        _btnSendPic = (Button) findViewById(R.id.btnSendPic); // 发送图片按钮
        _btnSendPic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle b = new Bundle();

                Intent intent = new Intent(MainActivity.this, CheckPictureActivity.class);
                intent.putExtra("data", b);

                startActivityForResult(intent, CheckPictureActivity.CHECK_PICTURE_RESULT_OK);

            }
        });

        _btnSendLoc = (Button) findViewById(R.id.btnSendLoc); // 发送位置
        _btnSendLoc.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Location.getCurBitmap(MainActivity.this, _handlerSendLoc);
            }
        });

        // 更多菜单按钮 2015/2/2 15:58
        _imgBtnMoreMenu = (ImageView) findViewById(R.id.btn_more_menu);
        _imgBtnMoreMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                _closeInput(); // 关闭输入法 2015/2/2 11:40
                if (_llRcdMoreMenu.getVisibility() == View.VISIBLE)
                    _llRcdMoreMenu.setVisibility(View.GONE);
                else
                    _llRcdMoreMenu.setVisibility(View.VISIBLE);
            }
        });

        _btnRcd = (TextView) findViewById(R.id.btn_rcd); // 发送音频按钮

        _btnBack = (Button) findViewById(R.id.btn_back); // 返回按钮
        _btnBack.setOnClickListener(this);

        _llBottom = (RelativeLayout) findViewById(R.id.btn_bottom); // 底部布局，文本输入

        _btnModeWords = (ImageView) this.findViewById(R.id.ivPopUp); // 切换模式的图片

        _btnModeVolume = (ImageView) this.findViewById(R.id.volume);

        _chatPopup = this.findViewById(R.id.rcChat_popup); // 录音显示UI层

        img1 = (ImageView) this.findViewById(R.id.img1); // 关闭按钮
        sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
        del_re = (LinearLayout) this.findViewById(R.id.del_re); // 取消布局

        _llRcdHintRcding = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_rcding); // 显示手机录音状态

        _llRcdHintLoading = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_loading); // 录音开始前的加载准备

        _llRcdHintTooshort = (LinearLayout) this
                .findViewById(R.id.voice_rcd_hint_tooshort); // 显示时间太短的提示

        _llRcdMoreMenu = (LinearLayout)this
                .findViewById(R.id.ll_menu); // 显示更多菜单 2015/2/2 15:59

        _sensorMeter = new SoundMeter();
        _editTextContent = (EditText) findViewById(R.id.et_sendmessage); // 输入框
        _editTextContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        _isOpenInput = true;
                        _llRcdMoreMenu.setVisibility(View.GONE);
                        _listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);

                        break;
                }

                return false;
            }
        });

        _editTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                if (charSequence.length() != 0) { // 显示发送按钮
                    _imgBtnMoreMenu.setVisibility(View.GONE);
                    _btnSend.setVisibility(View.VISIBLE);
                } else { // 显示更多操作菜单
                    _imgBtnMoreMenu.setVisibility(View.VISIBLE);
                    _btnSend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // 语音文字切换按钮
        _btnModeWords.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {

                if (_isVocie) { // 是否显示音频按钮
                    _btnRcd.setVisibility(View.GONE); // 隐藏发送音频按钮
                    _editTextContent.setVisibility(View.VISIBLE); // 显示文本输入
                    _isVocie = false;
                    _btnModeWords
                            .setImageResource(R.drawable.chatting_setmode_msg_btn); // 更换模式切换图片

                    _openInput();
                    _llRcdMoreMenu.setVisibility(View.GONE);

                } else {
                    _btnRcd.setVisibility(View.VISIBLE);
                    _editTextContent.setVisibility(View.GONE);
                    _btnModeWords
                            .setImageResource(R.drawable.chatting_setmode_voice_btn);
                    _isVocie = true;

                    _closeInput(); // 关闭输入法 2015/2/2 11:40
                    _llRcdMoreMenu.setVisibility(View.GONE);
                }
            }
        });

        _btnRcd.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                //按下语音录制按钮时返回false执行父类OnTouch
                return false;
            }
        });
    }

    /**
     * 初始化数据 2015/2/2 19:16
     */
    public void initData() {
        this._getData(); // 获取数据
        int number = (_lstmAdapter.count + 10 <= _lstData.size()) ? 10 : (_lstData.size() - _lstmAdapter.count);
        _lstmAdapter.count = number;
        for (int i = 0; i < number; i++) {
            // 1 2 3 4 5 6
            _lstShowData.add(_lstData.get(_lstData.size() - number + i));
        }

        _lstmAdapter.notifyDataSetChanged();
    }

    private Handler _handlerSendLoc = new Handler() { // 处理发送位置
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle == null) return;

            String loc = bundle.getString("location");
            byte[] json = bundle.getByteArray("img");
            String local = CopyPicture.getInstance(MainActivity.this)
                    .addBitmap(Utils.Bytes2Bimap(json));

            ChatMsgEntity entity = new ChatMsgEntity(_chatId);
            entity.setDate(_getDate())
                    .setMsgType(Const.CHAT_TYPE_LOCATION)
                    .setUserName(_myName)
                    .setMsgMe(true)
                    .setDisplayTime(true)
                    .setContent(local)
                    .setLat(bundle.getString("lat"))
                    .setLng(bundle.getString("lng"))
                    .setDesc(loc)
                    .setStatus(Const.CHAT_UPLOAD_ING);

            _saveData(entity); // 存储数据
        }
    };

    /**
     * 获取数据 2015/2/2 19:37
     */
    private void _getData1() {
        while (true) {
            // 如果预存数据达到100条或者已经没有存储的数据，则停止预存
            if (this._lstDataBk.size() >= 100
                    || this._lstDate.size() == 0)
                break;

            List<ChatMsgEntity> lstData = ChatMsgManager.getInstance(this,
                    this._lstDate.remove(0)).getData(); // 根据日期获取数据

            // 1 2 3 4 5 6
            this._lstDataBk.addAll(0, lstData); // 将日期较新挤到后面
        }

        // 将备份数据转移至待显示列表
        while (true) {
            // 没有数据或者转移的数据已经达到50条，则停止转移
            if (this._lstDataBk.size() == 0
                    || (this._lstData.size() - this._lstShowData.size() >= 50)) break;

            // this._lstDataBk.size() - 1 将数据从新往旧读取，放置
            // 6 5 4 3 2 1
            this._lstData.add(this._lstDataBk.remove(this._lstDataBk.size() - 1));
        }
    }

    /**
     * 获取数据(Sqlite读取) 2015/2/3 10:41
     */
    private void _getData() {

//        _dbChatManager.del(); // 删除数据

        // 根据聊天窗口，获取总数量
        int allCount = this._dbChatManager.getCountByUserId(this._chatId);
        if (allCount > this._lstData.size()) { // 还有数据未读取
            int limit = (allCount >= 50 + this._lstData.size())?
                    50 : (allCount - this._lstData.size()); // 得到需要读取的数量

            if (limit <= 0) return;

            int offset = allCount - 50 - this._lstData.size();
            if (offset < 0) offset = 0;

            // 1 2 3 4 5 6
            this._lstData.addAll(this._dbChatManager.getData(this._chatId, limit, offset));
        }
    }

    /**
     * 存放数据 2015/2/2 19:47
     * @param chatMsgEntity
     */
    private void _saveData1(ChatMsgEntity chatMsgEntity) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(Long.valueOf(chatMsgEntity.getDate()));
        String strDate = dateFormat.format(date);

        String key = this._chatId + "_" + strDate;
        if (!this._lstDate.contains(strDate)) { // 没有存在，则先存储当前日期
            ChatMsgDateManager.getInstance(this, this._chatId).save(key);
        }

        // 存放
        ChatMsgManager.getInstance(this, key).add(chatMsgEntity);
    }

    /**
     * 存放数据(放置数据库) 2015/2/3 10:42
     * @param chatMsgEntity
     */
    private void _saveData(ChatMsgEntity chatMsgEntity) {
        String time = Const.mapDisplayTime.get(_chatId);
        Const.mapDisplayTime.put(_chatId, chatMsgEntity.getDate());
        if (time == null) { // 还没有发送过信息，可以显示时间
            chatMsgEntity.setDisplayTime(true);
        } else { // 判断该发送的时间是不是在5分钟之内，是的话不用显示时间，否则显示时间
            long time1 = Long.valueOf(time) + 5 * 60 * 1000;
            long time2 = Long.valueOf(chatMsgEntity.getDate());
            if (time1 > time2) // 在5分钟之内
                chatMsgEntity.setDisplayTime(false);
            else
                chatMsgEntity.setDisplayTime(true);
        }

        this._dbChatManager.add(chatMsgEntity);

        _lstData.add(chatMsgEntity);
        _lstShowData.add(chatMsgEntity);
        _lstmAdapter.count += 1;
        _lstmAdapter.notifyDataSetChanged();

        // 显示列表动态选择最后一条信息
        _listView.setSelection(_listView.getCount() - 1);
    }

    /**
     * 点击事件 2015/1/26 17:00
     * @param v
     */
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn_send: // 点击发送
                String contString = _editTextContent.getText().toString();
                send(contString, true);
                break;
            case R.id.btn_back: // 点击返回
                finish();
                break;
        }
    }

    /**
     * 发送文本数据 2015/1/26 17:01
     */
    public void send(String contString, boolean isMe) {
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity(this._chatId);
            entity.setDate(_getDate())
                    .setMsgType(Const.CHAT_TYPE_WORDS)
                    .setUserName(this._myName)
                    .setMsgMe(isMe)
                    .setDisplayTime(true)
                    .setContent(contString)
                    .setStatus(Const.CHAT_UPLOAD_ING);

            _saveData(entity); // 存储数据
            _editTextContent.setText("");
        }
    }

    /**
     * 获取当前时间 2015/1/26 17:06
     * @return
     */
    private String _getDate() {
        return String.valueOf(System.currentTimeMillis());
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

        if (_isVocie) { // 是否显示发送音频模式
            int[] location = new int[2];
            _btnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
            int btn_rc_Y = location[1];
            int btn_rc_X = location[0];
            int[] del_location = new int[2];
            del_re.getLocationInWindow(del_location);
            int del_Y = del_location[1];
            int del_x = del_location[0];
            if (event.getAction() == MotionEvent.ACTION_DOWN && _flagVocie == 1) {
                if (!Environment.getExternalStorageDirectory().exists()) { // 判断是否有内存卡
                    Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
                    return false;
                }

                System.out.println("2");
                // 判断手势按下的位置是否是语音录制按钮的范围内
                if (event.getY() > btn_rc_Y
                        && (event.getX() > btn_rc_X && event.getX() < btn_rc_X + _btnRcd.getWidth())) {
                    System.out.println("3");
                    _btnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed); // 切换音频按钮图片显示状态
                    _chatPopup.setVisibility(View.VISIBLE); // 显示录音显示UI层，看到的效果就是一个透明底层的手机状态
                    _llRcdHintLoading.setVisibility(View.VISIBLE); // 显示手机录音加载进度
                    _llRcdHintRcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    _llRcdHintTooshort.setVisibility(View.GONE); // 隐藏时间太短的提示
                    _handlerVocie.postDelayed(new Runnable() { // 开始后，响应处理
                        public void run() {
                            if (!_isShosrt) {
                                _llRcdHintLoading.setVisibility(View.GONE); // 隐藏手机录音加载进度
                                _llRcdHintRcding.setVisibility(View.VISIBLE); // 显示手机录音状态
                            }
                        }
                    }, 300);

                    img1.setVisibility(View.VISIBLE); // 显示关闭按钮
                    del_re.setVisibility(View.GONE); // 隐藏取消布局

                    _startVoiceT = System.currentTimeMillis(); // 获得开始录音时间
                    _voiceName = _startVoiceT + ".amr"; // 拼接得到音频名称
                    _start(_voiceName); // 开始录制
                    _flagVocie = 2;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP && _flagVocie == 2) { // 松开手势时执行录制完成
                System.out.println("4");
                _btnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor); // 切换音频按钮图片显示状态
                if (event.getY() >= del_Y
                        && event.getY() <= del_Y + del_re.getHeight()
                        && event.getX() >= del_x
                        && event.getX() <= del_x + del_re.getWidth()) { // 在取消布局的范围内
                    _chatPopup.setVisibility(View.GONE); // 隐藏录音显示UI层，看到的效果就是一个透明底层的手机状态
                    img1.setVisibility(View.VISIBLE);
                    del_re.setVisibility(View.GONE);
                    _stop(); // 停止录音
                    _flagVocie = 1;
                    File file = new File(_sensorMeter.ARM_PATH + _voiceName);
                    if (file.exists()) { // 判断是否存在
                        file.delete(); // 存在则删除
                    }
                } else { // 不属于取消，则发送音频出去 2015/1/26 17:45

                    _llRcdHintRcding.setVisibility(View.GONE); // 隐藏手机录音状态
                    _stop(); // 停止录音
                    _endVoiceT = System.currentTimeMillis(); // 获得结束录音时间
                    _flagVocie = 1;

                    int time = (int) ((_endVoiceT - _startVoiceT) / 1000);
                    if (time < 1) { // 判断时间长度
                        _isShosrt = true;
                        _llRcdHintLoading.setVisibility(View.GONE);
                        _llRcdHintRcding.setVisibility(View.GONE);
                        _llRcdHintTooshort.setVisibility(View.VISIBLE);
                        _handlerVocie.postDelayed(new Runnable() {
                            public void run() {
                                _llRcdHintTooshort
                                        .setVisibility(View.GONE);
                                _chatPopup.setVisibility(View.GONE);
                                _isShosrt = false;
                            }
                        }, 500);
                        return false;
                    }

                    // 发送音频 2015/1/26 17:46
                    ChatMsgEntity entity = new ChatMsgEntity(this._chatId);
                    entity.setDate(_getDate())
                            .setMsgType(Const.CHAT_TYPE_VOICE)
                            .setUserName(this._myName)
                            .setMsgMe(true)
                            .setDisplayTime(true)
                            .setVoiceTime(time + "\"")
                            .setContent(_sensorMeter.ARM_PATH + _voiceName)
                            .setStatus(Const.CHAT_UPLOAD_ING);

                    _saveData(entity); // 存储数据
                    _chatPopup.setVisibility(View.GONE); // 隐藏录音显示UI层

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

    private static final int POLL_INTERVAL = 300; // 300毫秒更新一次
    private Runnable _mSleepTask = new Runnable() {
        public void run() {
            _stop();
        }
    };

    /**
     * 开始录制后，响应的手柄 2015/1/26 18:56
     */
    private Runnable _mPollTask = new Runnable() {
        public void run() {
            double amp = _sensorMeter.getAmplitude();
            _updateDisplay(amp);
            _handlerVocie.postDelayed(_mPollTask, POLL_INTERVAL);

        }
    };

    /**
     * 开始录制 2015/1/26 18:54
     * @param name
     */
    private void _start(String name) {
        _sensorMeter.start(name);
        _handlerVocie.postDelayed(_mPollTask, POLL_INTERVAL);
    }

    /**
     * 停止录制 2015/1/26 18:54
     */
    private void _stop() {
        _handlerVocie.removeCallbacks(_mSleepTask);
        _handlerVocie.removeCallbacks(_mPollTask);
        _sensorMeter.stop();
        _btnModeVolume.setImageResource(R.drawable.amp1);
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

    public void head_xiaohei(View v) { // 标题栏 返回按钮

    }

    /**
     * 关闭输入法 2015/2/2 16:27
     */
    private void _closeInput() {

        // 关闭输入法 2015/2/2 11:40
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken()
                ,InputMethodManager.HIDE_NOT_ALWAYS);

        _isOpenInput = false;
        _listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    }

    /**
     * 打开输入法 2015/2/2 16:28
     */
    private void _openInput() {
        _isOpenInput = true;
        InputMethodManager inputManager = (InputMethodManager) getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        _editTextContent.requestFocus();
    }

    /**
     * 修改数据 2015/2/4 17:34
     * @param chatMsgEntity
     */
    public void updateChatMsgEntity (ChatMsgEntity chatMsgEntity){
        this._dbChatManager.update(chatMsgEntity);
    }

    public String getMyName() {
        return this._myName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {  //返回成功
            switch (requestCode) {
                case CheckPictureActivity.CHECK_PICTURE_RESULT_OK: // 选择图片成功
                    if (data == null) return;
                    Bundle bundle = data.getBundleExtra("data");
                    if (bundle != null) {
                        _llRcdMoreMenu.setVisibility(View.GONE); // 隐藏更多菜单操作
                        List<String> lstPaths = bundle.getStringArrayList("lstPaths");
                        for (String path: lstPaths) { // 发送图片 2015/2/4 16:07
                            System.out.println("======    " + path);
                            ChatMsgEntity entity = new ChatMsgEntity(this._chatId);
                            entity.setDate(_getDate())
                                    .setId(entity.getDate())
                                    .setMsgType(Const.CHAT_TYPE_PICTURE)
                                    .setUserName(this._myName)
                                    .setMsgMe(true)
                                    .setDisplayTime(true)
                                    .setContent(path)
                                    .setStatus(Const.CHAT_UPLOAD_ING);

                            _saveData(entity); // 存储数据
                        }
                    }
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}