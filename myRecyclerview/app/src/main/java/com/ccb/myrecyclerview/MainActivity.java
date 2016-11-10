package com.ccb.myrecyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
        // 使用的步骤 2016/11/10 8:56

        //设置布局管理器
        mRecyclerView.setLayoutManager(layout);

        //设置adapter
        mRecyclerView.setAdapter(adapter)

        //设置Item增加、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        //添加分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
     */

    /**
     * 基础 2016/11/10 8:44
     * @param view 视图
     */
    public void OnBase(View view) {
        startActivity(new Intent(this, BaseRecyclerViewActivity.class));
    }

    /**
     * 头部 2016/11/10 11:36
     * @param view 视图
     */
    public void OnHead(View view) {
        startActivity(new Intent(this, HeadRecyclerViewActivity.class));
    }

    /**
     * 网格 2016/11/10 8:44
     * @param view 视图
     */
    public void OnGrid(View view) {
        startActivity(new Intent(this, GridRecyclerViewActivity.class));
    }

    /**
     * 瀑布流 2016/11/10 8:44
     * @param view 视图
     */
    public void OnStaggerGrid(View view) {
        startActivity(new Intent(this, StaggeredGridRecyclerViewActivity.class));
    }

    /**
     * 多动 2016/11/10 14:47
     * @param view 视图
     */
    public void OnDrag(View view) {
        startActivity(new Intent(this, DragRecyclerViewActivity.class));
    }

}
