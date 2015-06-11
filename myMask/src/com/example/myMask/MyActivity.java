package com.example.myMask;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * 遮罩 2015/6/11 11:05
 */
public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 动态添加遮罩 2015/6/11 11:20
        ImageView imageView = (ImageView) findViewById(R.id.img);
        Bitmap mask = BitmapFactory.decodeResource(getResources(),
                R.drawable.chatting_content_right_bg);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.shuaige);
        imageView.setImageBitmap(CustomMaskImage.maskImage(bmp, mask));
    }
}
