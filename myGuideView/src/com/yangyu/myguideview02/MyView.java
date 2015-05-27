package com.yangyu.myguideview02;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MyView extends ViewGroup {
    View leftView = null;
    View rightView = null;
    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = new View(context, attrs);
        view.setBackgroundColor(Color.BLACK);
        this.addView(view, 0);
    }

    /**
     * 测量
     */
    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() != 2) {
            try {
                // 自定义Exception
                throw new Exception() {
                    @Override
                    public void printStackTrace() {
                        System.err.println("MyView中只能存在一个View");
                        super.printStackTrace();
                    }
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        leftView = getChildAt(0);
        // 设置leftview的高和宽
        leftView.measure(widthMeasureSpec, heightMeasureSpec);
        rightView = getChildAt(1);
        // 设置rightview的高和宽
        rightView.measure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    /**
     * 布局
     */
    @SuppressLint("NewApi")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        System.out.println(l + "," + t + "," + r + "," + b);
        if (leftView != null & rightView != null) {
            // 设置leftview的位置,在屏幕右侧(初始不可见)
            leftView.layout(-r, 0, 0, b);
            // 设置rightView的位置,在屏幕中
            rightView.layout(l, t, r, b);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float X = event.getX();
        float Y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                System.out.println("X:" + X);
                if (X < 100) {
                    scrollTo(0, 0);
                } else if (X > rightView.getMeasuredWidth() - 100) {// 当用户滑动至离右边缘100时,页面关闭
                    new Thread(new Runnable() {// 新建线程,滑动关闭
                        @Override
                        public void run() {
                            for (int i = 0;; i++) {
                                try {
                                    Thread.sleep(10);// rightView每隔10ms右移3,保证滑动流畅
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                int len = (int) (X + 3 * i);
                                // System.out.println("len:" + len);
                                Message message = new Message();// android中非UI线程不允许直接操作控件,可以将消息发送至主线程的handler类
                                if (len >= rightView.getMeasuredWidth()) {
                                    message.what = 1;
                                    handler.sendMessage(message);// 发送消息
                                    // 关闭View
                                    break;
                                } else {
                                    message.what = 0; // 发送消息 自动滑动
                                    handler.sendMessage(message);
                                }
                            }
                        }
                    }).start();
                } else {
                    scrollTo((int) -X, 0);
                    // 计算透明度信息
                    float alpha = (float) (1.0 - (float) (1.0 / 400) * X);
                    // System.out.println("alpha:" + al);
                    // 设置透明度
                    leftView.setAlpha(alpha);
                }
                break;
        }
        // 设置true,消费event事件,不在向外传递
        return true;
    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @SuppressLint("NewApi")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                scrollBy(-3, 0);// viewgroup向右滑动3
            } else if (msg.what == 1) {
                Toast.makeText(getContext(), "关闭", Toast.LENGTH_SHORT).show();
                setVisibility(View.GONE);// 设置viewgroup不可见(隐藏)
            }
        }
    };
}

