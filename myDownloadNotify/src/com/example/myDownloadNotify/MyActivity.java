package com.example.myDownloadNotify;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.File;

public class MyActivity extends Activity {
    private Button btn;

    private CompleteReceiver completeReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btn = (Button) findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                // 下载目录
                File folder = new File(Environment.getExternalStorageDirectory(),"testDL/");
                boolean isSuccess = (folder.exists() && folder.isDirectory()) || folder.mkdirs();
                if (isSuccess) {
                    DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

                    // 下载路径
                    String apkUrl = "http://dlql.qq.com/dlied5.qq.com/wegame/wepang/RedGame_Android_2017_1.0.13.0_2014-07-09.apk?f2=491a0a79&mkey=53f1c997394e5cfb&f=d488&p=.apk&p=.apk";
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
                    request.setDestinationInExternalPublicDir("testDL", "ttaxc.apk"); // 文件名

                    request.setTitle("天天爱消除"); // 设置下载中通知栏提示的标题
                    request.setDescription("正在下载天天爱消除"); // 设置下载中通知栏提示的介绍

                    /**
                    表示下载进行中和下载完成的通知栏是否显示。默认只显示下载中通知。
                    VISIBILITY_VISIBLE_NOTIFY_COMPLETED表示下载完成后显示通知栏提示。
                    VISIBILITY_HIDDEN表示不显示任何通知栏提示，
                        这个需要在AndroidMainfest中添加权限android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
                     */
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    /**
                    表示下载允许的网络类型，默认在任何网络下都允许下载。
                    有NETWORK_MOBILE、NETWORK_WIFI、NETWORK_BLUETOOTH三种及其组合可供选择。
                    如果只允许wifi下载，而当前网络为3g，则下载会等待。
                     */
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

                    // 执行下载，返回downloadId，downloadId可用于后面查询下载信息。若网络不满足条件、Sdcard挂载中、超过最大并发数等异常会等待下载，正常则直接下载。
                    long downloadId = downloadManager.enqueue(request);

                    // 设置下载完成监听
                    completeReceiver = new CompleteReceiver(Environment.getExternalStorageDirectory() + "testDL/ttaxc.apk");
                    registerReceiver(completeReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                }

                // 自定义下载管理
//                Intent updateIntent = new Intent(MyActivity.this, UpdateService.class);
//                updateIntent.putExtra("titleId", 20140917);
//                startService(updateIntent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(completeReceiver);
    }
}
