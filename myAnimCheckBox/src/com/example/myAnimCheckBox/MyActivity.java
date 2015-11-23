package com.example.myAnimCheckBox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

/**
 * 复选框特效 2015/11/23 9:16
 */
public class MyActivity extends Activity implements AnimCheckBox.OnCheckedChangeListener{

    private AnimCheckBox mAnimCheckBox1, mAnimCheckBox2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mAnimCheckBox1 = (AnimCheckBox)findViewById(R.id.checkbox_1);
        mAnimCheckBox1.setChecked(false, false);
        mAnimCheckBox2 = (AnimCheckBox)findViewById(R.id.checkbox_2);
        mAnimCheckBox2.setChecked(false, false);
        mAnimCheckBox1.setOnCheckedChangeListener(this);
        mAnimCheckBox2.setOnCheckedChangeListener(this);
    }

    @Override
    public void onChange(boolean checked) {
        Log.d("MainActivity", "checked-->" + checked);
    }
}
