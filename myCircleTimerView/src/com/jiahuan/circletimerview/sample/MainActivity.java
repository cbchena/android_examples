package com.jiahuan.circletimerview.sample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.jiahuan.circletimerview.CircleTimerView;


public class MainActivity extends Activity implements CircleTimerView.CircleTimerListener {

    CircleTimerView timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (CircleTimerView) findViewById(R.id.ctv);
        timer.setCircleTimerListener(this); // 设置监听器
        timer.setTime(30 * 60 + 3); // 设置时间
    }

    public void start(View v) {
        timer.startTimer();
    }

    public void pause(View v) {
        timer.pauseTimer();
    }

    public void stop(View v) {
        timer.stopTimer();
    }

    @Override
    public void onTimerStop() {
        Toast.makeText(this, "onTimerStop", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimerStart() {
        Toast.makeText(this, "onTimerStart", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTimerPause() {
        Toast.makeText(this, "onTimerPause", Toast.LENGTH_LONG).show();
    }
}
