package com.ai.android.picker.test;

import com.ai.android.picker.R;
import com.ai.android.picker.R.id;
import com.ai.android.picker.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SampleActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_samples);

        findViewById(R.id.btnDark).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SampleActivity.this, DarkThemeActivity.class));
            }
        });

        findViewById(R.id.btnLight).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SampleActivity.this, LightThemeActivity.class));
            }
        });
    }

    /**
     * 打开时间 2015/7/23 16:59
     * @param view
     */
    public void OnOpen(View view) {
        startActivity(new Intent(this, DatePickerActivity.class));
    }
}
