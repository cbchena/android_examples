package com.example.myRipple;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Random;


public class MyActivity extends Activity {

    private ImageView foundDevice;
    private ImageView foundDevice1;
    private ImageView foundDevice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);

        final Handler handler=new Handler();

        foundDevice=(ImageView)findViewById(R.id.foundDevice);
        foundDevice1=(ImageView)findViewById(R.id.foundDevice1);
        foundDevice1.setTranslationX(400); // 0 - 400
        foundDevice1.setTranslationY(600); // 50 - 600

        LayoutParams para = new LayoutParams(dip2px(getApplicationContext(), 64),
                dip2px(getApplicationContext(), 64));

        foundDevice2 = new ImageView(rippleBackground.getContext());
        foundDevice2.setImageResource(R.drawable.phone2);
        foundDevice2.setTranslationX(new Random().nextInt(400)); // 0 - 400
        foundDevice2.setTranslationY(new Random().nextInt(600) - 50); // 50 - 600
        foundDevice2.setVisibility(View.INVISIBLE);
        foundDevice2.setLayoutParams(para);
        rippleBackground.addView(foundDevice2);

        ImageView button=(ImageView)findViewById(R.id.centerImage);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foundDevice();
                    }
                },3000);
            }
        });
    }

    private void foundDevice(){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();

        // 加动画 2015/4/20 15:22
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);

        ObjectAnimator scaleXAnimator1 = ObjectAnimator.ofFloat(foundDevice1, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator1);
        ObjectAnimator scaleYAnimator1 = ObjectAnimator.ofFloat(foundDevice1, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator1);

        animatorSet.playTogether(animatorList); // 添加动画列表

        foundDevice.setVisibility(View.VISIBLE);
        foundDevice1.setVisibility(View.VISIBLE);
        foundDevice2.setVisibility(View.VISIBLE);

        animatorSet.start();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

