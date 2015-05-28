package com.zcw.togglebutton.sample;
import android.app.Activity;
import android.os.Bundle;
import com.zcw.togglebutton.ToggleButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ToggleButton toggleButton = (ToggleButton) this.findViewById(R.id.tglBtn1);
        toggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                System.out.println("=================    " + on);
            }
        });
	}
}
