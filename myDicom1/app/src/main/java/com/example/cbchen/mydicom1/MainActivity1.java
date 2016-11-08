package com.example.cbchen.mydicom1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.bitmaptest.BitmapUtils;

public class MainActivity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String path = sdPath + "/dicom/Image1.DCM";
        String temp = sdPath + "/dicom/temp/Image1";

        BitmapUtils bitmapUtils = new BitmapUtils();
//        int result = bitmapUtils.getImagePath(path, temp, BitmapUtils.DECODE_BITMAP);


        String data = sdPath + "/dicom/temp/Image10.data";
//        int result = bitmapUtils.changeWindowSize(data, temp, 100, 100);
//        System.out.println("=====   " + result);

        Bitmap bitmap = BitmapFactory.decodeFile(temp);
        ImageView img = (ImageView) this.findViewById(R.id.img);
        img.setImageBitmap(bitmap);
    }
}
