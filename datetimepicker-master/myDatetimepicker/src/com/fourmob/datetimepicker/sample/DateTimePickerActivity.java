package com.fourmob.datetimepicker.sample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * 日期选择器 2015/8/26 10:47
 */
public class DateTimePickerActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {

    public static final String DATEPICKER_TAG = "datepicker";

    public static final int DATE_TIME_PICKER_RESULT = 82;

    private Calendar _calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dc_date_time_picker);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = (int)(display.getWidth() - 300); //设置宽度
        this.getWindow().setAttributes(lp);

        _calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog =
            DatePickerDialog.newInstance(this, _calendar.get(Calendar.YEAR),
                    _calendar.get(Calendar.MONTH),
                    _calendar.get(Calendar.DAY_OF_MONTH),
                    true);

        datePickerDialog.setVibrate(true); // 是否震动
        datePickerDialog.setYearRange(1985, 2028); // 年份范围
        datePickerDialog.setCloseOnSingleTapDay(true); // 点击后，是否关闭窗口
        datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        Toast.makeText(DateTimePickerActivity.this, "new date:" + year + "-" + month + "-" + day, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClose() {
        finish();
    }
}
