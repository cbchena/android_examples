package com.example.myMask;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.ImageView;

public class MaskImage extends ImageView {
    int mImageSource = 0;
    int mMaskSource = 0;
    RuntimeException mException;

    public MaskImage(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.MaskImage, 0, 0);

        mImageSource = a.getResourceId(R.styleable.MaskImage_image, 0);
        mMaskSource = a.getResourceId(R.styleable.MaskImage_mask, 0);

        if (mImageSource == 0 || mMaskSource == 0) {
            mException = new IllegalArgumentException(
                    a.getPositionDescription()
                            + ": The content attribute is required and must refer to a valid image.");
        }

        a.recycle();
        if (mException != null)
            throw mException;
        /**
         * 主要代码实现
         */
        // 获取图片的资源文件
        Bitmap original = BitmapFactory.decodeResource(getResources(),
                mImageSource);

        // 获取遮罩层图片
        Bitmap mask = BitmapFactory.decodeResource(getResources(), mMaskSource);

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
        setImageBitmap(result);
        setScaleType(ScaleType.CENTER);
        paint.setXfermode(null);
    }
}
