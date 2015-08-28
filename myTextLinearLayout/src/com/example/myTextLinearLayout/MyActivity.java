package com.example.myTextLinearLayout;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MyActivity extends Activity {

    private TextLinearLayout _llInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // 获取布局 2015/8/28 11:30
        _llInfo = (TextLinearLayout) this.findViewById(R.id.llInfo);
        _llInfo.setCellHeight(Utils.dip2px(this, 40));
        _llInfo.setChidlWidthSpace(Utils.dip2px(this, 15));
        _llInfo.setChildHeightSpace(Utils.dip2px(this, 5));

        TextView textView;

        // 文本的布局参数 2015/8/28 11:32
        LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // 初始化文本 2015/8/28 11:32
        for(int i = 0; i < 50; i++) {
            textView = new TextView(_llInfo.getContext());
            textView.setLayoutParams(txtParams);
            textView.setText("这是标签" + i);
            textView.setTextSize(16f);
            _llInfo.addView(textView);
        }
    }
}
