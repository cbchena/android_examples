package com.yangyu.myguideview02;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * @author yangyu
 *	功能描述：主程序入口activity
 */
public class MainActivity extends Activity {
    // 定义ViewPager对象
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    //定义各个界面View对象
    private View view1,view2,view3,view4,view5,view6, view7;

    // 定义底部小点图片
    private ImageView pointImage0, pointImage1, pointImage2, pointImage3,pointImage4, pointImage5;

    //定义开始按钮对象
    private Button startBt;

    // 当前的位置索引值
    private int currIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
        //实例化各个界面的布局对象
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.guide_view01, null);
        view2 = mLi.inflate(R.layout.guide_view02, null);
        view3 = mLi.inflate(R.layout.guide_view03, null);
        view4 = mLi.inflate(R.layout.guide_view04, null);
        view5 = mLi.inflate(R.layout.guide_view05, null);
        view6 = mLi.inflate(R.layout.guide_view06, null);
        view7 = mLi.inflate(R.layout.guide_view07, null);

        // 实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        // 实例化ArrayList对象
        views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        views.add(view5);
        views.add(view6);
        views.add(view7);

        // 实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);

        // 实例化底部小点图片对象
        pointImage0 = (ImageView) findViewById(R.id.page0);
        pointImage1 = (ImageView) findViewById(R.id.page1);
        pointImage2 = (ImageView) findViewById(R.id.page2);
        pointImage3 = (ImageView) findViewById(R.id.page3);
        pointImage4 = (ImageView) findViewById(R.id.page4);
        pointImage5 = (ImageView) findViewById(R.id.page5);

        //实例化开始按钮
        startBt = (Button) view6.findViewById(R.id.startBtn);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 设置监听
        viewPager.setOnPageChangeListener(new MyOnPageChangeListener());

        // 设置适配器数据
        viewPager.setAdapter(vpAdapter);

        // 给开始按钮设置监听
        startBt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startbutton();
            }
        });
    }

    public class MyOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 1:
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage0.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 2:
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 3:
                    pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 4:
                    pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage3.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    pointImage5.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 5:
                    pointImage5.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_focused));
                    pointImage4.setImageDrawable(getResources().getDrawable(R.drawable.page_indicator_unfocused));
                    break;
                case 6:
                    startbutton();
                    break;
            }
            currIndex = position;
            // animation.setFillAfter(true);// True:图片停在动画结束位置
            // animation.setDuration(300);
            // mPageImg.startAnimation(animation);
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }
    }

    /**
     * 相应按钮点击事件，进入另一个界面
     */
    private void startbutton() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,GuideViewDoor.class);
        startActivity(intent);
        this.finish();
    }

}
