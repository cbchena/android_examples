package com.example.myRunningService;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

/**
 * 一键清除，清除除本身外，正在运行的进程 2015/9/10 16:57
 */
public class MyActivity1 extends Activity {
    private static final String TAG = "ClearMemoryActivity";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button clear = (Button) findViewById(R.id.clear);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //To change body of implemented methods use File | Settings | File Templates.
                ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();

                long beforeMem = getAvailMemory(MyActivity1.this);
                Log.d(TAG, "清除前的内存:: " + beforeMem);
                int count = 0;
                if (infoList != null) {
                    for (int i = 0; i < infoList.size(); ++i) {
                        ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                        Log.d(TAG, "process name : " + appProcessInfo.processName);

                        //importance 该进程的重要程度  分为几个级别，数值越低就越重要。
                        Log.d(TAG, "importance : " + appProcessInfo.importance);

                        // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                        // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                        //      清理的效果跟金山清理大师和360桌面的一键清理效果差不多。
                        if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                            String[] pkgList = appProcessInfo.pkgList;
                            for (int j = 0; j < pkgList.length; ++j) {//pkgList 得到该进程下运行的包名
                                Log.d(TAG, "Kill: " + pkgList[j]);
                                am.killBackgroundProcesses(pkgList[j]); // 除了本身，全部清除 2015/9/10 16:57
                                count++;
                            }
                        }

                    }
                }

                long afterMem = getAvailMemory(MyActivity1.this);
                Log.d(TAG, "清除后的内存: " + afterMem);
                Toast.makeText(MyActivity1.this, "清除 " + count + " 个进程, 剩余"
                        + (afterMem - beforeMem) + "M可用", Toast.LENGTH_LONG).show();
            }
        });

    }

    //获取可用内存大小
    private long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
//        Log.d(TAG, "当前可用内存：" + mi.availMem / (1024 * 1024));
        return mi.availMem / (1024 * 1024);
    }
}
