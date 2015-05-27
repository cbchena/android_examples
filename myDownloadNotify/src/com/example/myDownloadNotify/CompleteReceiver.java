package com.example.myDownloadNotify;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

/**
 * 监听下载成功 2014/9/17 18:46
 */
class CompleteReceiver extends BroadcastReceiver {

    private String _strFilePath; // 文件路径

    public CompleteReceiver() {}

    public CompleteReceiver(String strFilePath) {
        this._strFilePath = strFilePath;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // get complete download id
        long completeDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

        // 完成后通知声
        NotificationManager manger = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification();

        //自定义声音   声音文件放在ram目录下，没有此目录自己创建一个
//        notification.sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mm);

        //使用系统默认声音用下面这条
        notification.defaults=Notification.DEFAULT_SOUND;
        manger.notify(1, notification);

        // 打开下载文件 2015/3/9 12:14
        DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
        myDownloadQuery.setFilterById(completeDownloadId);
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Activity.DOWNLOAD_SERVICE);
        Cursor myDownload = downloadManager.query(myDownloadQuery);
        if (myDownload.moveToFirst()) {
            int fileUriIdx = myDownload.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String uri = myDownload.getString(fileUriIdx); // 例如：file:///storage/sdcard0/ky_SDK/水果忍者.apk

            myDownload.close();
            if (uri != null) {
                String filePath = getFilePathFromUri(context, Uri.parse(uri)); //得到apk路径
                File file = new File(filePath);
                Intent intentOpen = new Intent();
                intentOpen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intentOpen.setAction(android.content.Intent.ACTION_VIEW);
                intentOpen.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                context.startActivity(intentOpen);
            }
        }
    }

    /**
     * 获得路径 2015/3/9 12:10
     * @param c 上下文环境
     * @param uri 本地地址
     * @return
     */
    public String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = { MediaStore.MediaColumns.DATA };
            ContentResolver contentResolver = c.getContentResolver();
            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }

        Log.i("CompleteReceiver", "filePath=" + filePath);
        return filePath;
    }
}


