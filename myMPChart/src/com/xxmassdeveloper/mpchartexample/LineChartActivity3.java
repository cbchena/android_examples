package com.xxmassdeveloper.mpchartexample;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.WindowManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.LargeValueFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.xxmassdeveloper.mpchartexample.notimportant.DemoBase;

import java.util.ArrayList;

public class LineChartActivity3 extends DemoBase {

    private LineChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart3);

        mChart = (LineChart) findViewById(R.id.chart1);
        mChart.setDescription("血压量表");
        mChart.setNoDataTextDescription("当前没有数据可供展示");

        mChart.setBackgroundColor(Color.WHITE);

        // 如果禁止，缩放可以在X或Y轴分别进行
        mChart.setPinchZoom(true);

        // 禁止网格的背景颜色
        mChart.setDrawGridBackground(false);

        // 添加Y轴动画
        mChart.animateY(1000);

        // 设置数据 2015/7/23 9:10
        _setData();

        // 设置字体样式
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");

        // 设置图标展示的样式
        Legend l = mChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        l.setFormSize(0f);
        l.setTypeface(tf);
        l.setTextSize(16f);
        l.setTextColor(Color.BLACK); // 黑色字体
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT); // 从下面的左边开始绘画

        // 设置X轴样式
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // 将X轴放置底部
        xAxis.setTypeface(tf);
        xAxis.setTextSize(16f);
        xAxis.setGridColor(Color.rgb(236, 236, 236));

        // 设置Y轴样式
        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTypeface(tf);
        yAxis.setValueFormatter(new LargeValueFormatter());
        yAxis.setDrawGridLines(false);
//        yAxis.setSpaceTop(30f);
        yAxis.setTextSize(16f);
        yAxis.setGridColor(Color.rgb(236, 236, 236));
        mChart.getAxisRight().setGridColor(Color.rgb(236, 236, 236));
    }

    /**
     * 设置数据 2015/7/23 9:15
     */
    private void _setData() {

        // 设置X轴的数值点
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            if (i == 1)
                xVals.add("月/日");
            else
                xVals.add("7/" + i + "");
        }

        // 添加曲线
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(_createDataSet(92, 142, 1));
        dataSets.add(_createDataSet(75, 134, 2));
        dataSets.add(_createDataSet(86, 115, 3));
        dataSets.add(_createDataSet(84, 128, 4));

        // 设置点的样式
        LineData data = new LineData(xVals, dataSets);
        data.setValueTextColor(Color.rgb(0, 0, 0)); // 设置文本颜色
        data.setValueTextSize(16f);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "";
            }
        });

        // 设置数据 2015/7/23 10:04
        mChart.setData(data);
    }

    /**
     * 创建曲线 2015/7/23 10:02
     * @param value1 第一个数值
     * @param value2 第二个数值
     * @param xIdx X轴的索引
     * @return 曲线
     */
    private LineDataSet _createDataSet(float value1, float value2, int xIdx) {

        // 设置曲线的数值点
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        yVals1.add(new Entry(value1, xIdx));
        yVals1.add(new Entry(value2, xIdx));

        // 设置点的颜色 2015/7/23 9:53
        int[] VORDIPLOM_COLORS = {
                Color.rgb(80, 193, 233), Color.rgb(255, 122, 0)
        };

        // 创建数据集合 2015/7/23 9:24
        LineDataSet lineDataSet = new LineDataSet(yVals1, "");
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setColor(Color.rgb(255, 187, 75)); // 设置线的颜色
        lineDataSet.setCircleColors(VORDIPLOM_COLORS); // 设置点的颜色
        lineDataSet.setLineWidth(2f); // 设置线的宽度
        lineDataSet.setCircleSize(10f); // 设置点的大小

        return lineDataSet;
    }
}
