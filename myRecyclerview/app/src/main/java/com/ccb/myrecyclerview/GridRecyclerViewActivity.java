package com.ccb.myrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 网格的RecyclerView 2016/11/10 9:08
 */
public class GridRecyclerViewActivity extends Activity {

    private RecyclerView _recyclerView;
    private List<String> _lstDatas;
    private HomeAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        _initData();
        _recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);

        _adapter = new HomeAdapter();

        // 添加头部 2016/11/10 14:15
        GridHeaderAndFooterWrapper wrapper = new GridHeaderAndFooterWrapper(_adapter);
        TextView t1 = new TextView(this);
        t1.setText("Head");
        wrapper.addHeaderView(t1);

        TextView t2 = new TextView(this);
        t2.setText("Foot");
        wrapper.addFootView(t2);

        // 设置线形布局管理器 2016/11/10 9:00
        /**
         * LinearLayoutManager 现行管理器，支持横向、纵向。
         * GridLayoutManager 网格布局管理器
         * StaggeredGridLayoutManager 瀑布就式布局管理器
         */
        _recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3列 2016/11/10 9:07
        _recyclerView.setAdapter(wrapper);

        // 添加分割线 2016/11/10 9:11
        _recyclerView.addItemDecoration(new DividerGridItemDecoration(this));
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
                    GridRecyclerViewActivity.this).inflate(R.layout.activity_grid_item, parent,
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
