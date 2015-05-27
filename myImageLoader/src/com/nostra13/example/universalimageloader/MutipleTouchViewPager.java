package com.nostra13.example.universalimageloader;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author ZhiCheng Guo
 * @version 2014年11月18日 下午12:44:59 精品界面多点触摸有bug,会导致pointerIndex out of range的异常,
 *          所以加了这个view
 */
public class MutipleTouchViewPager extends ViewPager {

    public MutipleTouchViewPager(Context context) {
        super(context);
    }

    public MutipleTouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}

