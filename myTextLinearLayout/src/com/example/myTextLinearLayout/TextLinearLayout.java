package com.example.myTextLinearLayout;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 这个类是自定义的LinearLayout，目的是实现“TextView横向布局当控件占满时可以自动换行到下一行显示”
 * @author cbchen
 */
public class TextLinearLayout extends ViewGroup {
    private int cellHeight = 0;
    private Context _context;

    private int chidlWidthSpace = 0;
    private int childHeightSpace = 0;

    public void setCellHeight(int cellHeight) {
        this.cellHeight = cellHeight;
    }

    public void setChidlWidthSpace(int chidlWidthSpace) {
        this.chidlWidthSpace = chidlWidthSpace;
    }

    public void setChildHeightSpace(int childHeightSpace) {
        this.childHeightSpace = childHeightSpace;
    }

    public TextLinearLayout(Context context) {
        super(context);
        _context = context;
        _init();
    }

    /**
     * 知识点： 1、该自定义控件必须实现这个构造方法，不然会报android.view.InflateException: Binary XML
     * file line #异常 2、另外两种构造方法 MyLinearLayout(Context
     * context)、MyLinearLayout(Context context, AttributeSet attrs, int
     * defStyle)可不实现!
     */
    public TextLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        _context = context;
        _init();
    }

    /**
     * 初始化数据 2015/8/18 10:00
     */
    private void _init() {
        cellHeight = Utils.dip2px(_context, 30);
        chidlWidthSpace = Utils.dip2px(_context, 30);
        childHeightSpace = Utils.dip2px(_context, 10);
    }

    /**
     * 控制子控件的换行
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;// 子控件的左上角x坐标
        int top = 0;// 子控件的左上角y坐标
        int count = getChildCount();// 获得子控件的数目
        int remainingWidth = 0;// 计算一行中剩下的宽度
        boolean isFirst = true; // 是否为第一行

        r = r - l - 26;
        for (int j = 0; j < count; j++) {
            View childView = getChildAt(j);

            // 获取子控件Child的宽高
            int w = childView.getMeasuredWidth();
            int h = childView.getMeasuredHeight();

            // 如果即将显示的子控件不是位于第一列且该行位置已容不下该控件，则修改坐标参数换行显示！
            if (left != chidlWidthSpace && remainingWidth <= w) {
                left = 0;
                if (!isFirst)
                    top += (cellHeight + childHeightSpace);

                isFirst = false;
            }

//            remainingWidth = r - l - left - w;
            remainingWidth = r - left - w;
            childView.layout(left, top, left + w, top + h);
            left += (w + chidlWidthSpace);
        }

    }

    /**
     * 计算控件及子控件所占区域
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 创建测量参数
        int cellWidthSpec = MeasureSpec.makeMeasureSpec(0,
                MeasureSpec.UNSPECIFIED);// 不指定子控件的宽度
        int cellHeightSpec = MeasureSpec.makeMeasureSpec(cellHeight,
                MeasureSpec.EXACTLY);// 精确定义子控件的高度
        int count = getChildCount();// 记录ViewGroup中Child的总个数

        // 设置子控件Child的宽高
        for (int i = 0; i < count; i++) {
            View childView = getChildAt(i);
            childView.measure(cellWidthSpec, cellHeightSpec);
        }

        // 父控件宽度
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - 26;

        // 父控件高度--方法：累计每个子控件的宽度+左右间隔，当数值超过父控件的宽度时，换行。记录行数。
        // 记录ViewGroup中Child的总个数
        int lines = 1;

        int currWidth = 0; // 每行当前宽度总和
        for (int index = 0; index < count; index++) {

            final TextView child = (TextView) getChildAt(index);
            int childWidth = getWidth(child);

            currWidth += (chidlWidthSpace + childWidth);//左侧间隔+子控件宽度

            // 另起一行
            if (currWidth > widthSize) {
                currWidth = 0;
                currWidth += (chidlWidthSpace + childWidth);//左侧间隔+子控件宽度
                lines++;
            }
        }
        //  子控件高度+间隔
        int heightSize = cellHeight * lines + (lines + 1) * childHeightSpace;
        setMeasuredDimension(widthSize, heightSize);
    }

    /**
     * 子控件的宽度通过计算字符数转化而来
     * @param view
     * @return
     */
    private int getWidth(TextView view) {
        int width = (int) Layout.getDesiredWidth(view.getText(), 0, view.length(), view.getPaint());
        return width;
    }

}