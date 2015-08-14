package com.ai.android.picker;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ai.android.picker.NumberPicker.OnValueChangeListener;

public class DatePicker extends FrameLayout {

	private Context mContext;
	private NumberPicker mDayPicker;
	private NumberPicker mMonthPicker;
	private NumberPicker mYearPicker;
	private Calendar mCalendar;

	private String[] mDayDisplay;
	private String[] mMonthDisplay;
	private String[] mYearDisplay;

	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mCalendar = Calendar.getInstance();
		initDayDisplay();
		initMonthDisplay();
		initYearDisplay();
		((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.date_picker, this, true);
		mDayPicker = (NumberPicker) findViewById(R.id.date_day);
		mMonthPicker = (NumberPicker) findViewById(R.id.date_month);
		mYearPicker = (NumberPicker) findViewById(R.id.date_year);

		mDayPicker.setMinValue(1);
		mDayPicker.setMaxValue(31);
		mDayPicker.setValue(20);
		mDayPicker.setDisplayedValues(mDayDisplay);
		mDayPicker.setFormatter(NumberPicker.TWO_DIGIT_FORMATTER);

		mMonthPicker.setMinValue(0);
		mMonthPicker.setMaxValue(11);
		mMonthPicker.setDisplayedValues(mMonthDisplay);
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));

		mYearPicker.setMinValue(1950);
		mYearPicker.setMaxValue(2100);
		mYearPicker.setDisplayedValues(mYearDisplay);
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));

		mMonthPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.MONTH, newVal);
				updateDate();
			}
		});
		mDayPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {

				mCalendar.set(Calendar.DATE, newVal);
				updateDate();
			}
		});
		mYearPicker.setOnValueChangedListener(new OnValueChangeListener() {

			@Override
			public void onValueChange(NumberPicker picker, int oldVal,
					int newVal) {
				mCalendar.set(Calendar.YEAR, newVal);
				updateDate();

			}
		});

		updateDate();

	}

	private void initDayDisplay() {
		mDayDisplay = new String[31];
		for(int i = 0; i < 31; i++) {
			mDayDisplay[i] = (i + 1) + "日";
		}
	}

	private void initMonthDisplay() {
//		mMonthDisplay = new String[12];
//		mMonthDisplay[0] = "Jan";
//		mMonthDisplay[1] = "Feb";
//		mMonthDisplay[2] = "Mar";
//		mMonthDisplay[3] = "Apr";
//		mMonthDisplay[4] = "May";
//		mMonthDisplay[5] = "Jun";
//		mMonthDisplay[6] = "Jul";
//		mMonthDisplay[7] = "Aug";
//		mMonthDisplay[8] = "Sep";
//		mMonthDisplay[9] = "Oct";
//		mMonthDisplay[10] = "Nov";
//		mMonthDisplay[11] = "Dec";

		mMonthDisplay = new String[12];
		mMonthDisplay[0] = "1月";
		mMonthDisplay[1] = "2月";
		mMonthDisplay[2] = "3月";
		mMonthDisplay[3] = "4月";
		mMonthDisplay[4] = "5月";
		mMonthDisplay[5] = "6月";
		mMonthDisplay[6] = "7月";
		mMonthDisplay[7] = "8月";
		mMonthDisplay[8] = "9月";
		mMonthDisplay[9] = "10月";
		mMonthDisplay[10] = "11月";
		mMonthDisplay[11] = "12月";
	}

	private void initYearDisplay() {
		mYearDisplay = new String[151];
		for(int i = 0; i < 151; i++) {
			mYearDisplay[i] = (1950 + i) + "年";
		}
	}

	private void updateDate() {
		System.out.println("Month: " + mCalendar.get(Calendar.MONTH) + " Max: "
				+ mCalendar.getActualMaximum(Calendar.DATE));
		mDayPicker.setMinValue(mCalendar.getActualMinimum(Calendar.DATE));
		mDayPicker.setMaxValue(mCalendar.getActualMaximum(Calendar.DATE));
		mDayPicker.setValue(mCalendar.get(Calendar.DATE));
		mMonthPicker.setValue(mCalendar.get(Calendar.MONTH));
		mYearPicker.setValue(mCalendar.get(Calendar.YEAR));
	}

	public DatePicker(Context context) {
		this(context, null);
	}

	public String getDate() {
		String date = mYearPicker.getValue() + "-"
				+ (mMonthPicker.getValue() + 1) + "-" + mDayPicker.getValue();
		return date;

	}

	public int getDay() {
		return mCalendar.get(Calendar.DAY_OF_MONTH);
	}

	public int getMonth() {
		return mCalendar.get(Calendar.MONTH);
	}

	public int getYear() {
		return mCalendar.get(Calendar.YEAR);
	}

	public void setCalendar(Calendar calendar) {
		mCalendar = calendar;
		updateDate();
	}

}
