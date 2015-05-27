package com.example.myAppLog.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告. 2015/4/13 14:21
 * @author user
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler _mDefaultHandler;

    //CrashHandler实例
    private static CrashHandler INSTANCE = new CrashHandler();

    //程序的Context对象
    private Context _mContext;

    //用来存储设备信息和异常信息
    private Map<String, String> _infos = new HashMap<String, String>();

    //用于格式化日期,作为日志文件名的一部分
    private DateFormat _formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 单例模式 2015/4/13 14:21
     * @return
     */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    /**
     * 初始化 2015/4/13 14:21
     * @param context 上下文环境
     */
    public void init(Context context) {
        _mContext = context;

        // 获取系统默认的UncaughtException处理器
        _mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();

        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理 2015/4/13 14:21
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!_handleException(ex) && _mDefaultHandler != null) {

            //如果用户没有处理则让系统默认的异常处理器来处理
            _mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }

            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 2015/4/13 14:22
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean _handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }

        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(_mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();

        //收集设备参数信息
        _collectDeviceInfo(_mContext);

        //保存日志文件
        _saveCrashInfo2File(ex);

        return true;
    }

    /**
     * 收集设备参数信息 2015/4/13 14:22
     * @param ctx 上下文环境
     */
    private void _collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                _infos.put("versionName", versionName);
                _infos.put("versionCode", versionCode);
            }

        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }

        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                _infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    /**
     * 保存错误信息到文件中 2015/4/13 14:22
     * @param ex
     * @return	返回文件名称,便于将文件传送到服务器
     */
    private String _saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : _infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = _formatter.format(new Date());
            String fileName = "n_crash_" + time + "_" + timestamp + ".log";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = "/sdcard/andy/crash/";
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }

        return null;
    }
}

