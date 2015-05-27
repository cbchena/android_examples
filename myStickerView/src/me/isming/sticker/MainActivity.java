package me.isming.sticker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import me.isming.sticker.library.StickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 主界面 2015/5/27 14:49
 */
public class MainActivity extends Activity{

    StickerView stickerView;
    RelativeLayout rl_image;
    RelativeLayout rl_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rl_image = (RelativeLayout) this.findViewById(R.id.ll_img);
        rl_main = (RelativeLayout) this.findViewById(R.id.rl_main);

        // 初始化贴纸对象 2015/5/27 14:51
        stickerView = new StickerView(this);

        // 初始化布局参数
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.image);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.image);

        // 将贴纸对象添加到底图视图中
        rl_image.addView(stickerView, params);

        // 绘画一张贴纸
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        stickerView.setWaterMark(bitmap);
    }

    public void OnSave(View view) {
        stickerView.setShowDrawController(false); // 隐藏控制组 2015/5/27 15:33
        saveBitmap(loadBitmapFromView(rl_image)); // 保存图片
    }

    /**
     * 将视图转换成bitmap 2015/5/27 15:15
     * @return
     */
    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();

        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);

        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */

        v.layout(0, 0, w, h);
        v.draw(c);

        return bmp;
    }

    /**
     * 保存bitmap 2015/5/27 14:59
     * @param bm
     */
    public void saveBitmap(Bitmap bm) {
        File folder = new File("/sdcard/sticker/");
        if (!folder.exists()) { // 创建文件夹
            folder.mkdirs();// 创建文件夹
        }

        File f = new File("/sdcard/sticker/", "11111.jpg");
        if (f.exists()) {
            f.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
