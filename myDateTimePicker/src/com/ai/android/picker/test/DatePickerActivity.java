package com.ai.android.picker.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.ai.android.picker.DatePicker;
import com.ai.android.picker.R;
import com.ai.android.picker.R.id;
import com.ai.android.picker.R.layout;
import com.ai.android.picker.TimePicker;

import java.util.Calendar;

public class DatePickerActivity extends Activity {

	DatePicker datePicker;
	TextView timeView;
	Button submitView;

	Calendar mCalendar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(layout.activity_date_picker);
		mCalendar = Calendar.getInstance();

		datePicker = (DatePicker) findViewById(id.datePicker);
		timeView = (TextView) findViewById(id.time_view);
		submitView = (Button) findViewById(id.get_time_btn);

		submitView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				mCalendar.set(Calendar.YEAR, datePicker.getYear());
				mCalendar.set(Calendar.MONTH, datePicker.getMonth());
				mCalendar.set(Calendar.DAY_OF_MONTH, datePicker.getDay());
				timeView.setText(mCalendar.getTime().toLocaleString());
			}
		});
		
	}
}
