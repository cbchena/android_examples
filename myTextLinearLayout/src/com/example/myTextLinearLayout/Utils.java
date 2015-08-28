package com.example.myTextLinearLayout;

import android.content.Context;

/**
 * 工具类 2015/7/22 8:47
 */
public class Utils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 2015/4/21 10:38
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 2015/4/21 10:38
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
