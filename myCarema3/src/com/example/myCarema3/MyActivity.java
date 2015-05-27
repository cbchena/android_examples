package com.example.myCarema3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyActivity extends Activity {

    private static final int PHOTO_WITH_CAMERA = 37;// 拍摄照片
    private Button _btnOpen;
    private ImageView _ivTemp;

    private String _imgPath = "";
    private String _imgName = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 目录路劲
        _imgPath = Environment.getExternalStorageDirectory() + "/andy/imageCache/";

        // 展示图片
        _ivTemp = (ImageView) findViewById(R.id.imageView);
        _ivTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPictureDialog(); // 打开图片
            }
        });

        // 打开相机按钮
        _btnOpen = (Button) findViewById(R.id.btn_open);
        _btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _imgName = createPhotoFileName();
                doTakePhoto();
            }
        });
    }

    /**
     * 拍照获取相片 2015/1/22 11:43
     */
    private void doTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //调用系统相机

        //指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
        Uri imageUri = Uri.fromFile(new File(_imgPath + _imgName));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        //直接使用，没有缩小
        startActivityForResult(intent, PHOTO_WITH_CAMERA);  //用户点击了从相机获取
    }

    /**
     * 打开图片查看对话框 2015/1/22 11:43
     */
    private void openPictureDialog() {


    }

    /**
     * 创建图片不同的文件名 2015/1/22 11:44
     * @return
     */
    private String createPhotoFileName() {
        String fileName;
        Date date = new Date(System.currentTimeMillis());  //系统当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + ".jpg";
        return fileName;
    }

    /**
     * 保存图片到本应用下 2015/1/22 11:45
     * @param fileName
     * @param bitmap
     */
    private void savePicture(String fileName,Bitmap bitmap) {

        FileOutputStream fos =null;
        try {//直接写入名称即可，没有会被自动创建；私有：只有本应用才能访问，重新内容写入会被覆盖
            fos = MyActivity.this.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把图片写入指定文件夹中

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != fos) {
                    fos.close();
                    fos = null;
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**You will receive this call immediately before onResume() when your activity is re-starting.**/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK) {  //返回成功
            switch (requestCode) {
                case PHOTO_WITH_CAMERA:  {//拍照获取图片
                    String status = Environment.getExternalStorageState();
                    if(status.equals(Environment.MEDIA_MOUNTED)) { //是否有SD卡

                        File file = new File(_imgPath);
                        if (!file.exists()) { // 创建文件夹
                            file.mkdirs();// 创建文件夹
                        }

                        Bitmap bitmap = BitmapFactory.decodeFile(_imgPath + _imgName);

                        // 写一个方法将此文件保存到本应用下面啦
                        savePicture(_imgName, bitmap);
                        if (bitmap != null) {

                            //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                            Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
                            _ivTemp.setImageBitmap(smallBitmap);
                        }

                        System.out.println("-----------    " + _imgName);
                        deleteFile(_imgName); // 删除私有文件
                        Toast.makeText(MyActivity.this, "文件路劲为: " + _imgPath + _imgName, Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MyActivity.this, "没有SD卡", Toast.LENGTH_LONG).show();
                    }
                    break;
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
