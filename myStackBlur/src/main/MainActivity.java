package main;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.enrique.stackblur.R;

/**
 * 毛玻璃效果
 * @author cbchen.
 * @time 2015/10/13 10:21.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final ImageView imageView = (ImageView) this.findViewById(R.id.img);

        final TextView text = (TextView) this.findViewById(R.id.text);
        final ImageView imageView1 = (ImageView) this.findViewById(R.id.img1);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                imageView.buildDrawingCache();
                Bitmap bmp = imageView.getDrawingCache();

                _blur(bmp, imageView1); // 毛全部
//                _blur(bmp, text); // 毛textview的部分
            }
        };

        imageView.getViewTreeObserver().addOnPreDrawListener(
            new ViewTreeObserver.OnPreDrawListener() {
                private boolean isUse = false;

                @Override
                public boolean onPreDraw() {
                    if (!isUse) {
                        handler.sendEmptyMessage(0);
                        isUse = true;
                    }

                    return true;
                }
        });

    }

    /**
     * 制作毛玻璃 2015/10/13 10:33
     * @param bitmap 图片来源
     * @param view 显示视图
     */
    private void _blur(Bitmap bitmap, View view) {
        long startMs = System.currentTimeMillis();
        float radius = 2;
        float scaleFactor = 8; // 模糊度

        Bitmap overlay = Bitmap.createBitmap(
                (int) (view.getMeasuredWidth() / scaleFactor),
                (int) (view.getMeasuredHeight() / scaleFactor),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(overlay);
        canvas.translate(-view.getLeft() / scaleFactor, -view.getTop()
                / scaleFactor);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);

        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        overlay = FastBlur.doBlur(overlay, (int) radius, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(new BitmapDrawable(getResources(), overlay));
        } else {
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), overlay));
        }

        System.out.println("cost " + (System.currentTimeMillis() - startMs) + "ms");
    }
}
