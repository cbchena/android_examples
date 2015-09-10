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

import java.util.List;

/**
 * 清除后台服务 2015/9/10 16:57
 */
public class MyActivity extends Activity {

    private ActivityManager mActivityManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 获得ActivityManager服务的对象
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        _getRunningServiceInfo();
    }

    /**
     * 获得系统正在运行的进程信息 2015/9/10 16:25
     */
    private void _getRunningServiceInfo() {

        // 设置一个默认Service的数量大小
        int defaultNum = 500;
        // 通过调用ActivityManager的getRunningAppServicees()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningServiceInfo> runServiceList = mActivityManager
                .getRunningServices(defaultNum);

        for (ActivityManager.RunningServiceInfo runServiceInfo : runServiceList) {

            // 获得Service所在的进程的信息
            int pid = runServiceInfo.pid; // service所在的进程ID号
            int uid = runServiceInfo.uid; // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            // 进程名，默认是包名或者由属性android：process指定
            String processName = runServiceInfo.process;

            // 该Service启动时的时间值
            long activeSince = runServiceInfo.activeSince;

            // 如果该Service是通过Bind方法方式连接，则clientCount代表了service连接客户端的数目
            int clientCount = runServiceInfo.clientCount;

            // 获得该Service的组件信息 可能是pkgname/servicename
            ComponentName serviceCMP = runServiceInfo.service;
            String serviceName = serviceCMP.getShortClassName(); // service 的类名
            String pkgName = serviceCMP.getPackageName(); // 包名

            // 打印Log
//            Log.i("Main", "所在进程id :" + pid + " 所在进程名：" + processName + " 所在进程uid:"
//                    + uid + "\n" + " service启动的时间值：" + activeSince
//                    + " 客户端绑定数目:" + clientCount + "\n" + "该service的组件信息:"
//                    + serviceName + " and " + pkgName);

            // 这儿我们通过service的组件信息，利用PackageManager获取该service所在应用程序的包名 ，图标等
            PackageManager mPackageManager = this.getPackageManager(); // 获取PackagerManager对象;

            try {
                // 获取该pkgName的信息
                ApplicationInfo appInfo = mPackageManager.getApplicationInfo(
                        pkgName, 0);

                // 设置该service的组件信息
                Intent intent = new Intent();
                intent.setComponent(serviceCMP);
                try {
                    stopService(intent);
                    android.os.Process.killProcess(uid);
                    System.out.println("==============  kill  " + "所在进程名：" + processName + " 所在进程uid:" + uid);
                } catch (SecurityException sEx) {

                }

//                RunSericeModel runService = new RunSericeModel();
//                runService.setAppIcon(appInfo.loadIcon(mPackageManager));
//                runService.setAppLabel(appInfo.loadLabel(mPackageManager) + "");
//                runService.setServiceName(serviceName);
//                runService.setPkgName(pkgName);
//
//                // 设置该service的组件信息
//                Intent intent = new Intent();
//                intent.setComponent(serviceCMP);
//                runService.setIntent(intent);
//
//                runService.setPid(pid);
//                runService.setProcessName(processName);

            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                System.out.println("--------------------- error ---------------------");
                e.printStackTrace();
            }

        }
    }
}
