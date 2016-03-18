package com.example.mySignature;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 签名 2016/3/18 11:11
 */
public class MyActivity extends Activity implements View.OnClickListener {

    private SignatureView _svDoctorName; // 医生签名
    private Button _btnSave; // 保存
    private Button _btnReWrite; // 重写

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature);

        this._svDoctorName = (SignatureView) this.findViewById(R.id.svDoctorName);

        this._btnSave = (Button) this.findViewById(R.id.btnSave);
        this._btnSave.setOnClickListener(this);

        this._btnReWrite = (Button) this.findViewById(R.id.btnReWrite);
        this._btnReWrite.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave: // 保存
                _save(this._svDoctorName);
                break;
            case R.id.btnReWrite: // 重写
                this._svDoctorName.clear();
                break;
        }
    }

    /**
     * 保存签名 2016/3/18 10:52
     * @param view 视图
     */
    private void _save(View view) {
        final Bitmap bmp = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bmp));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String time = dateFormat.format(new Date());

        String path = Environment.getExternalStorageDirectory() + "/ml_home/";
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        final String photoUrl = path + time + ".png";//换成自己的图片保存路径
        final File file = new File(photoUrl);
        new Thread() {

            @Override
            public void run() {
                try {
                    boolean bitMapOk = bmp.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                    if(bitMapOk) {
                        Looper.prepare();
                        Toast.makeText(MyActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        _svDoctorName.clear();
                        Looper.loop();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
}
