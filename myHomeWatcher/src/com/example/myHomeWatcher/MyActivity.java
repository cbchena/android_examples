package com.example.myHomeWatcher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

/**
 * home键监听 2015/10/14 10:29
 */
public class MyActivity extends Activity {

    private static final String LOG_TAG = "MyActivity";

    private static HomeWatcherReceiver mHomeKeyReceiver = null;

    /**
     * 注册监听 2015/10/14 10:30
     * @param context 上下文环境
     */
    private static void registerHomeKeyReceiver(Context context) {
        Log.i(LOG_TAG, "registerHomeKeyReceiver");
        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    /**
     * 销毁监听 2015/10/14 10:30
     * @param context 上下文环境
     */
    private static void unregisterHomeKeyReceiver(Context context) {
        Log.i(LOG_TAG, "unregisterHomeKeyReceiver");
        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerHomeKeyReceiver(this);
    }

    @Override
    protected void onPause() {

        unregisterHomeKeyReceiver(this);
        super.onPause();
    }
}
