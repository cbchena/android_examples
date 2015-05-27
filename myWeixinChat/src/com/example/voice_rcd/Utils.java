package com.example.voice_rcd;

import android.graphics.*;
import android.media.ThumbnailUtils;
import android.support.v4.util.LruCache;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cbchen on 2015/2/4 16:01.
 */
public class Utils {

    public static Bitmap clipit(Bitmap bitmapimg, int direct) {

        //0 = direction right
        //1 = direction left

        Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                bitmapimg.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        if(direct == 0) {
            canvas.drawRect(0, 0, bitmapimg.getWidth()-15, bitmapimg.getHeight(), paint);
            Path path = new Path();

            path.moveTo(bitmapimg.getWidth()-15, 25);
            path.lineTo(bitmapimg.getWidth(), 35);
            path.lineTo(bitmapimg.getWidth()-15, 45);
            path.lineTo(bitmapimg.getWidth()-15, 25);
            canvas.drawPath(path,paint);
        }
        if(direct == 1) {
            canvas.drawRect(15, 0, bitmapimg.getWidth(), bitmapimg.getHeight(), paint);
            Path path = new Path();
            path.moveTo(15, 25);
            path.lineTo(0, 32);
            path.lineTo(15, 45);
            path.lineTo(15, 25);
            canvas.drawPath(path,paint);
        }


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmapimg, rect, rect, paint);
        return output;

    }

    public static Bitmap getImageThumbnail(String imagePath) {
        return getImageThumbnail(imagePath, -1, -1);
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图
     * 此方法有两点好处：
     *     1. 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     *        第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。
     *     2. 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使
     *        用这个工具生成的图像不会被拉伸。
     * @param imagePath 图像的路径
     * @return 生成的缩略图
     */
    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false

        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;

        options.inSampleSize = 1;

        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        ImageSize imageSize = null;
        if (width == -1 || height == -1)
            imageSize = _getSize(w, h);
        else {
            imageSize = new ImageSize();
            imageSize.width = width;
            imageSize.height = height;
        }

        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, imageSize.width, imageSize.height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        return bitmap;
    }

    private static ImageSize _getSize(int w, int h) {
        ImageSize imageSize = new ImageSize();
        int value = Math.abs(w - h);
        if (w == h) {
            imageSize.width = 200 + w * value;
            imageSize.height = 200 + h * value;
        } else if (w > h) {
            imageSize.width = 233;
            imageSize.height = 131;
        } else {
            imageSize.width = 131;
            imageSize.height = 233;
        }

        return imageSize;
    }

    private static class ImageSize
    {
        int width;
        int height;
    }

    /**
     * 图片缓存的核心类
     */
    private static LruCache<String, Bitmap> mLruCache;
    private static void _initLru() {
        if (mLruCache != null) return;

        // 获取应用程序最大可用内存
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }

            ;
        };
    }

    /**
     * 从LruCache中获取一张图片，如果不存在就返回null。 2015/2/4 17:09
     */
    public static Bitmap getBitmapFromLruCache(String key) {
        _initLru();
        return mLruCache.get(key);
    }

    /**
     * 往LruCache中添加一张图片 2015/2/4 17:09
     * @param key
     * @param bitmap
     */
    public static void addBitmapToLruCache(String key, Bitmap bitmap) {
        _initLru();
        if (getBitmapFromLruCache(key) == null) {
            if (bitmap != null)
                mLruCache.put(key, bitmap);
        }
    }

    /**
     * 二进制转Bitmap 2015/1/27 18:12
     * @param b 二进制数组
     * @return Bitmap
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    /**
     * 格式化时间 2015/2/25 14:28
     * @param curTime 毫秒数 1424835258672
     * @return time:今天 15:41  time:昨天 15:45  time:2015年8月11日 15:43
     */
    public static String formatDateTime(String curTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if(curTime==null ||"".equals(curTime)){
            return "";
        }

        Date date = new Date(Long.valueOf(curTime));
        String time = format.format(date);

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();	//今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set( Calendar.HOUR_OF_DAY, 0);
        today.set( Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();	//昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
        yesterday.set( Calendar.HOUR_OF_DAY, 0);
        yesterday.set( Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        String t = "";
        if (date.getHours() >= 0 && date.getHours() < 6)
            t = "凌晨";
        else if (date.getHours() >= 6 && date.getHours() < 12)
            t = "上午";
        else if (date.getHours() >= 12 && date.getHours() < 18)
            t = "下午";
        else if (date.getHours() >= 18)
            t = "晚上";

        if(current.after(today)){
            return "今天 " + t + " " + time.split(" ")[1];
        }else if(current.before(today) && current.after(yesterday)){
            return "昨天 " + t + " " +  time.split(" ")[1];
        }else{
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy年M月d日 HH:mm");
            Date date1 = new Date(Long.valueOf(curTime));
            String tmpTime = format1.format(date1);
            return tmpTime.split(" ")[0] + " " + t + " " + tmpTime.split(" ")[1];
        }
    }
}
