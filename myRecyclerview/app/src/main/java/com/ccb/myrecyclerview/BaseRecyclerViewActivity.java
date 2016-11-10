package com.ccb.myrecyclerview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础的RecyclerView 2016/11/10 8:45
 */
public class BaseRecyclerViewActivity extends Activity {

    private RecyclerView _recyclerView;
    private List<String> _lstDatas;
    private HomeAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        _initData();
        _recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);

        // 设置线形布局管理器 2016/11/10 9:00
        /**
         * LinearLayoutManager 现行管理器，支持横向、纵向。
         * GridLayoutManager 网格布局管理器
         * StaggeredGridLayoutManager 瀑布就式布局管理器
         */
        _recyclerView.setLayoutManager(new LinearLayoutManager(this));
        _recyclerView.setAdapter(_adapter = new HomeAdapter());

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

        // 设置title的信息
        collapsingToolbarLayout.setTitle("ActionBar标题");

        // 设置展开后title的颜色
        collapsingToolbarLayout.setExpandedTitleColor(Color.BLACK);

        // 设置收缩后titile的颜色
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        ImageButton btnBack = (ImageButton) this.findViewById(R.id.imgBtnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 初始化数据 2016/11/10 8:51
     */
    private void _initData() {
        _lstDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            _lstDatas.add("" + (char) i);
        }
    }

    /**
     * 适配器 2016/11/10 8:52
     */
    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(
                    BaseRecyclerViewActivity.this).inflate(R.layout.activity_base_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv.setText(_lstDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return _lstDatas.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView tv;

            public ViewHolder(View view) {
                super(view);
                tv = (TextView) view.findViewById(R.id.id_num);
            }
        }
    }

}
