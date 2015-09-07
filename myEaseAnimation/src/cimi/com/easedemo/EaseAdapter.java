package cimi.com.easedemo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import java.util.List;

import cimi.com.easedemo.view.CursorView;
import cimi.com.easedemo.view.EaseView;

/**
 * 适配器 2015/9/7 9:01
 */
public class EaseAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mNameList;
    private List<Interpolator> mInterpolatorList;
    private long duration;
    private int selectIndex = -1;

    public EaseAdapter(Context context, List<String> nameList, List<Interpolator> interpolatorList, long duration) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mNameList = nameList; // 存放名称列表
        mInterpolatorList = interpolatorList; // 存放动作列表 2015/9/7 8:57
        this.duration = duration;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            mHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.adapter, null);
            convertView.setBackgroundColor(Color.WHITE);
            mHolder.easeName = (TextView) convertView.findViewById(R.id.easeName); // 显示动作的名称
            mHolder.easeView = (EaseView) convertView.findViewById(R.id.easeView); // 显示运动的曲线图
            mHolder.cursor = (CursorView) convertView.findViewById(R.id.cursor); // 当前运动的图标 2015/9/7 8:57
            convertView.setTag(mHolder);
        }else {
            mHolder = (ViewHolder) convertView.getTag();
        }

        final Interpolator interpolator = mInterpolatorList.get(position); // 获取指定动作 2015/9/7 8:58
        mHolder.easeName.setText(mNameList.get(position)); // 设置动作名称
        mHolder.easeView.setDurationAndInterpolator(duration, interpolator); // 运动时长，设置动作

        int bottomMargin = mHolder.easeView.blankTB - mHolder.cursor.height / 2;
        LayoutParams params = (LayoutParams)mHolder.cursor.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.easeView);
        params.bottomMargin = bottomMargin;
        mHolder.cursor.setLayoutParams(params);


        if (position == selectIndex) { //选定项开始做Ease动画 2015/9/7 8:59
            selectIndex = -1;
            int toYDelta = mHolder.easeView.height - 2 * mHolder.easeView.blankTB;
            Animation anim = new TranslateAnimation(0, 0, 0, -toYDelta); // 设置的动作为移动动作
            anim.setDuration(duration); // 设置时长
            anim.setInterpolator(interpolator); // 设定动作
            anim.setFillAfter(true);
            anim.setFillBefore(true);
            anim.setStartOffset(300); // 开始时，缓300毫秒
            mHolder.cursor.startAnimation(anim);
        } else { //非选定项的游标回到原处 2015/9/7 8:59
            Animation anim = new TranslateAnimation(0, 0, 0, 0);
            anim.setDuration(0);
            anim.setFillAfter(true);
            anim.setFillBefore(true);
            mHolder.cursor.startAnimation(anim);
        }

        return convertView;
    }

    public void setSelectIndex(int index) {
        selectIndex = index;
        notifyDataSetChanged();
    }

    private ViewHolder mHolder = null;
    private class ViewHolder {
        public TextView easeName;
        public EaseView easeView;
        public CursorView cursor;
    }


    @Override
    public int getCount() {
        return mInterpolatorList == null ? 0 : mInterpolatorList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


}