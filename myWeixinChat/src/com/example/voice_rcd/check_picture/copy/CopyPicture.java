package com.example.voice_rcd.check_picture.copy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import com.example.voice_rcd.Utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class CopyPicture {

    private static CopyPicture _copyPicture;
    private static Context _context;

    private final String WHOLESALE_CONV = ".cach";
    private String _imgPath = Environment.getExternalStorageDirectory() + "/andy/imageCache/";
    private String _imgName = "";

    /**
     * 单例 2015/1/22 15:51
     * @param context  需要注册的上下文
     * @return 相机
     */
    public static CopyPicture getInstance(Context context) {
        if (_copyPicture == null) {
            _copyPicture = new CopyPicture();
        }

        _context = context;

        return _copyPicture;
    }

    public Bitmap getBitmapByUrl(String url) {
        return getBitmapByUrl(url, -1, -1);
    }

    public Bitmap getBitmapByUrl(String url, int width, int height) {
        System.out.println("========    " + url);
        return Utils.getImageThumbnail(url, width, height);
    }

    /**
     * 复制图片 2015/2/4 15:37
     * @param lstPaths
     * @return 新路径列表
     */
    public ArrayList<String> copy(List<String> lstPaths) {

        ArrayList<String> lstNewPaths = new ArrayList<String>();
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)) { //是否有SD卡
            int idx = 0;
            for (String path: lstPaths) {
                File file = new File(_imgPath);
                if (!file.exists()) { // 创建文件夹
                    file.mkdirs();// 创建文件夹
                }

                _imgName = _createPhotoFileName(idx, path);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inJustDecodeBounds = false;
                options.inSampleSize = 5;   // width，hight设为原来的5分一

                Bitmap bitmap = BitmapFactory.decodeStream(fis, null, options);
                if (bitmap != null) {
                    File fileImg = new File(_imgPath + _imgName);
                    try {
                        fileImg.createNewFile();

                        // 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
                        Bitmap smallBitmap = ImageTools.zoomBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight());
                        FileOutputStream fos = new FileOutputStream(fileImg);
                        smallBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fos);
                        fos.flush();
                        fos.close();

                        if (!bitmap.isRecycled()) {
                            bitmap.recycle();   //回收图片所占的内存
                            bitmap = null;
                        }

                        if (!smallBitmap.isRecycled()) {
                            smallBitmap.isRecycled();
                            smallBitmap = null;
                        }

                        lstNewPaths.add(_imgPath + _imgName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                idx++;
            }
        }

        return lstNewPaths;
    }

    /**
     * 复制图片 2015/2/9 14:14
     * @param bitmap
     * @return 新路径列表
     */
    public String addBitmap(Bitmap bitmap) {
        String loc = null;
        String status = Environment.getExternalStorageState();
        if(status.equals(Environment.MEDIA_MOUNTED)) { //是否有SD卡

            File file = new File(_imgPath);
            if (!file.exists()) { // 创建文件夹
                file.mkdirs();// 创建文件夹
            }

            _imgName = _createPhotoFileName(0, ".jpg");
            loc = _imgPath + _imgName;
            if (bitmap != null) {
                File fileImg = new File(loc);
                try {
                    fileImg.createNewFile();
                    FileOutputStream fos = new FileOutputStream(fileImg);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return loc;
    }

    /**
     * 创建图片不同的文件名 2015/1/22 11:44
     * @return
     */
    private String _createPhotoFileName(int idx, String path) {
        String fileName;
        long time = System.currentTimeMillis();
        Date date = new Date(time);  //系统当前时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        fileName = dateFormat.format(date) + idx + path.substring(path.lastIndexOf(".")) + WHOLESALE_CONV;

        return fileName;
    }
}
