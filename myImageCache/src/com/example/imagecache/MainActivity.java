package com.example.imagecache;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * 用于测试图片缓存 2014/8/19 11:16
 */
public class MainActivity extends Activity {

    private ImageMemoryCache memoryCache;
    private ImageFileCache fileCache;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        memoryCache=new ImageMemoryCache(this);
        fileCache=new ImageFileCache();
        imageView=(ImageView) findViewById(R.id.img);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("value");
                System.out.println("请求结果:" + val);

                byte[] img = data.getByteArray("img");
                Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);  // 生成位图
                imageView.setImageBitmap(bitmap);

            }
        };

        final Runnable runnable = new Runnable(){
            @Override
            public void run() {
                String url = "http://f.hiphotos.baidu.com/album/w%3D2048/sign=7aa167f79f2f07085f052d00dd1cb999/472309f7905298228f794c7bd6ca7bcb0b46d4c4.jpg";
                Bitmap b = getBitmap(url);
                if (b == null) return;

                // 可以用下面的发送参数，执行某些事后操作
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("value","cbchen");
                data.putByteArray("img", bitmap2Bytes(b));

                msg.setData(data);
                handler.sendMessage(msg);
            }
        };

        new Thread(runnable).start();
    }

    /**
     * Bitmap → byte[] 2014/8/19 11:09
     * @param bm
     * @return
     */
    public byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public Bitmap getBitmap(String url) {
        // 从内存缓存中获取图片
        Bitmap result = memoryCache.getBitmapFromCache(url);
        if (result == null) {
            // 文件缓存中获取
            result = fileCache.getImage(url);
            if (result == null) {
                // 从网络获取
                result = ImageGetFromHttp.downloadBitmap(url);
                if (result != null) {
                    fileCache.saveBitmap(result, url);
                    memoryCache.addBitmapToCache(url, result);
                }
            } else {
                // 添加到内存缓存
                memoryCache.addBitmapToCache(url, result);
            }
        }
        return result;
    }

}

