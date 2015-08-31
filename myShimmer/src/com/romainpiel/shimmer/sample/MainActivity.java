package com.romainpiel.shimmer.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.romainpiel.shimmer.R;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerButton;
import com.romainpiel.shimmer.ShimmerTextView;

public class MainActivity extends Activity {

    ShimmerTextView tv;
    ShimmerButton btn;
    Shimmer shimmer_tv;
    Shimmer shimmer_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (ShimmerTextView) findViewById(R.id.shimmer_tv);
        btn = (ShimmerButton) findViewById(R.id.shimmer_btn);
    }

    public void toggleAnimation(View target) {
        if (shimmer_tv != null && shimmer_tv.isAnimating()) {
            shimmer_tv.cancel();
        } else {
            shimmer_tv = new Shimmer();
            shimmer_tv.setRepeatCount(0); // 设置重复次数  0，则表示不需要重复  -1，表示无限重复
            shimmer_tv.setDuration(2000); // 设置时间
            shimmer_tv.setDirection(Shimmer.ANIMATION_DIRECTION_LTR); // 设置方向
            shimmer_tv.start(tv);
        }

        if (shimmer_btn != null && shimmer_btn.isAnimating()) {
            shimmer_btn.cancel();
        } else {
            shimmer_btn = new Shimmer();
            shimmer_btn.setRepeatCount(0); // 设置重复次数  0，则表示不需要重复  -1，表示无限重复
            shimmer_btn.setDuration(2000); // 设置时间
            shimmer_btn.setDirection(Shimmer.ANIMATION_DIRECTION_LTR); // 设置方向
            shimmer_btn.start(btn);
        }
    }
}
