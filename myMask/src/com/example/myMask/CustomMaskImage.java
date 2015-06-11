package com.example.myMask;

import android.graphics.*;

/**
 * 添加遮罩 2015/4/13 11:07
 */
public class CustomMaskImage{

    /**
     * 制作遮罩 2015/4/13 11:07
     * @param original 被遮罩图
     * @param mask 遮罩图
     * @return 返回结果图
     */
    public static Bitmap maskImage(Bitmap original, Bitmap mask) {

        // 将遮罩层的图片放到画布中
        Bitmap result = Bitmap.createBitmap(original.getWidth(),
                original.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas mCanvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        NinePatch np = new NinePatch(mask, mask.getNinePatchChunk(), null);
        Rect rect = new Rect(0, 0, original.getWidth(), original.getHeight());
        np.draw(mCanvas, rect, null);
        mCanvas.drawBitmap(original, 0, 0, paint);
        paint.setXfermode(null);

        return result;
    }
}
