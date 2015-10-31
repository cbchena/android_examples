package com.xiaowu.colorpicker;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import com.xiaowu.colorpicker.ColorPickView.OnColorChangedListener;

public class MainActivity extends Activity {

	private TextView txtColor; // 显示颜色
	private ColorPickView myView; // 选色盘

    private TextView _txtR;
    private TextView _txtG;
    private TextView _txtB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		myView = (ColorPickView) findViewById(R.id.color_picker_view);
		txtColor = (TextView) findViewById(R.id.txt_color);
        _txtR = (TextView) this.findViewById(R.id.txtR);
        _txtG = (TextView) this.findViewById(R.id.txtG);
        _txtB = (TextView) this.findViewById(R.id.txtB);

		myView.setOnColorChangedListener(new OnColorChangedListener() {

			@Override
			public void onColorChange(int color) {
				txtColor.setTextColor(color);

                // 如果你想做的更细致的话 可以把颜色值的RGB拿到做响应的处理 笔者在这里就不做更多解释
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);

                _txtR.setText("R: " + r);
                _txtG.setText("G: " + g);
                _txtB.setText("B: " + b);
			}

		});
	}

}
