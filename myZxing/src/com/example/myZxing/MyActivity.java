package com.example.myZxing;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    /**
     * 按钮绑定生成二维码 2015/3/31 10:26
     * @param view
     */
    public void onCreateImg(View view) {
        TextView textView = (TextView) this.findViewById(R.id.txtUrl);
        String url = textView.getText().toString();
        if (url == null || url.length() == 0)
            return;

        ImageView imageView = (ImageView) this.findViewById(R.id.img);
        imageView.setImageBitmap(_createQRImage(url));
    }

    /**
     * 创建二维码  2014/10/30 15:07
     * @param url
     * @return
     */
    private Bitmap _createQRImage(String url)
    {
        Bitmap bitmap = null;
        int QR_WIDTH = 400, QR_HEIGHT = 400;

        try {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
                return bitmap;

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];

            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            //生成二维码图片的格式，使用ARGB_8888
            bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
        }
        catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }
}
