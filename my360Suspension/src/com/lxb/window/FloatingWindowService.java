package com.lxb.window;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.*;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FloatingWindowService extends Service {

    public static final String OPERATION = "operation";
    public static final int OPERATION_SHOW = 100; // 判断显示
    public static final int OPERATION_HIDE = 101; // 判断隐藏

    private static final int HANDLE_CHECK_ACTIVITY = 200;

    private boolean isAdded = false; // 是否已增加悬浮窗
    private static WindowManager wm;
    private static WindowManager.LayoutParams params;

    private List<String> homeList; // 桌面应用程序包名列表
    private ActivityManager mActivityManager;

    private LinearLayout _advertisingLayout; // 广告位布局
    private ImageView _img; // 显示的图片(可以点击)
    private int _statusBarHeight; // 状态栏的高度
    private int screenWidth; // 屏幕宽度
    private int screenHeight; // 屏幕高度

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        homeList = getHomes(); // 获得属于桌面的应用的应用包名称
        createFloatView(); // 创建视图
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
        switch(operation) {
            case OPERATION_SHOW:
                mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
                mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
                break;
            case OPERATION_HIDE:
                mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case HANDLE_CHECK_ACTIVITY:
                    if(isHome()) {
                        if(!isAdded) {
                            wm.addView(_advertisingLayout, params);
                            isAdded = true;
                        }
                    } else {
                        if(isAdded) {
                            wm.removeView(_advertisingLayout);
                            isAdded = false;
                        }
                    }
                    mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
                    break;
            }
        }
    };

    /**
     * 创建悬浮窗
     */
    private void createFloatView() {

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        this._advertisingLayout = (LinearLayout) inflater.inflate(R.layout.desktop_layout, null);
        if (this._advertisingLayout == null) return;

        _img = (ImageView)this._advertisingLayout.findViewById(R.id.img);

        wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        // 设置window type
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT; // 窗口可以获得焦点，响应操作
        /*
         * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE;
         * 那么优先级会降低一些, 即拉下通知栏不可见
         */

        params.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明

        // 设置Window flag
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //调整悬浮窗显示的停靠位置为左侧置顶
        params.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity
        params.x = 0;
        params.y = 50;

        // 设置悬浮窗的长得宽
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
         */

        // 获得屏幕的大小 2014/12/20 16:05
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        System.out.println("-----------------   aaa  " + screenWidth);
        System.out.println("-----------------   bbb  " + screenHeight);

        this._setEvents(); // 设置事件 2014/12/20 15:43

        wm.addView(_advertisingLayout, params);
        isAdded = true;
    }

    /**
     * 设置事件 2014/12/20 15:42
     */
    private void _setEvents() {
        _registerOperClick(false);

        // 保证可以点击的控件也可以控制移动  2014/12/20 15:09
        _img.setOnTouchListener(new OnTouchListener() {

            int lastX, lastY;
            int paramX, paramY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int motion = event.getAction();
                switch(motion) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;

                        _registerOperClick(false); // 注册点击事件
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        if (dx != 0 || dy != 0) { // 已经移动
                            _registerOperClick(true); // 取消点击事件
                        }

                        // 更新悬浮窗位置
                        wm.updateViewLayout(_advertisingLayout, params);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (params.x < 0)
                            params.x = 0;

                        if (params.y < 0)
                            params.y = 0;

                        if (params.x + _img.getWidth() > screenWidth)
                            params.x = screenWidth - _img.getWidth();

                        if (params.y + _getStatusBarHeight() + _img.getHeight() > screenHeight)
                            params.y = screenHeight - _getStatusBarHeight() - _img.getHeight();

                        break;
                }

                return false;
            }
        });

        // 设置悬浮窗的Touch监听 保证全局可以移动  2014/12/20 15:08
        this._advertisingLayout.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;

                        // 更新悬浮窗位置
                        wm.updateViewLayout(_advertisingLayout, params);
                        break;
                }

                return false;
            }
        });
    }

    /**
     * 操作点击事件 2014/12/20 15:18
     * @param view
     */
    private void _onOperClick(View view) {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), Floating_windowActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * 注册点击事件 2014/12/20 15:22
     * @param isNull
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void _registerOperClick(boolean isNull) {
        if (isNull && _img.hasOnClickListeners())
            _img.setOnClickListener(null);
        else if (!isNull) {
            // 注册点击事件
            _img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _onOperClick(view);
                }
            });
        }
    }

    /**
     * 获得属于桌面的应用的应用包名称
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        // 属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public boolean isHome(){
        if(mActivityManager == null) {
            mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        }
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return homeList.contains(rti.get(0).topActivity.getPackageName());
    }

    /**
 　　* 用于获取状态栏的高度。 2014/12/20 16:29
 　　*
 　　* @return 返回状态栏高度的像素值。
 　　*/
    private int _getStatusBarHeight() {
        if (_statusBarHeight == 0) {
            try {
                Class c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                _statusBarHeight = getResources().getDimensionPixelSize(x);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return _statusBarHeight;
    }
}
