package com.github.jjobes.slidedatetimepicker.sample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import com.github.jjobes.slidedatetimepicker.R;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Sample test class for SlideDateTimePicker.
 *
 * @author jjobes
 *
 */
@SuppressLint("SimpleDateFormat")
public class SampleActivity extends FragmentActivity
{
//    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Button mButton;

    private SlideDateTimeListener listener = new SlideDateTimeListener() {

        @Override
        public void onDateTimeSet(Date date)
        {
            Toast.makeText(SampleActivity.this,
                    mFormatter.format(date), Toast.LENGTH_SHORT).show();
        }

        // Optional cancel listener
        @Override
        public void onDateTimeCancel()
        {
            Toast.makeText(SampleActivity.this,
                    "Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sample);

        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
                new SlideDateTimePicker.Builder(getSupportFragmentManager())
                    .setListener(listener) // 设置监听器
                    .setInitialDate(new Date()) // 设置当前时间
                    //.setMinDate(minDate)
                    //.setMaxDate(maxDate)
                    .setIs24HourTime(true) // 设置24小时
//                    .setTheme(SlideDateTimePicker.HOLO_DARK) // 设置主题  白天  夜晚
//                    .setIndicatorColor(Color.parseColor("#990000"))
                    .build()
                    .show();
            }
        });
    }
}
