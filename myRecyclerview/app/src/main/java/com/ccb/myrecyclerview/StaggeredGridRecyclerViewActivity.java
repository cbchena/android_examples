package com.ccb.myrecyclerview;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.recyclerview.itemanimator.ScaleInOutItemAnimator;

/**
 * 网格的RecyclerView 2016/11/10 9:08
 */
public class StaggeredGridRecyclerViewActivity extends Activity {

    private RecyclerView _recyclerView;
    private List<String> _lstDatas;
    private HomeAdapter _adapter;
    private StaggereHeaderAndFooterWrapper _wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stagger_grid);

        _initData();
        _recyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);

        _adapter = new HomeAdapter();

        // 添加头部 2016/11/10 14:15
        _wrapper = new StaggereHeaderAndFooterWrapper(_adapter);
        TextView t1 = new TextView(this);
        t1.setText("Head");
        _wrapper.addHeaderView(t1);

        TextView t2 = new TextView(this);
        t2.setText("Foot");
        _wrapper.addFootView(t2);

        // 设置线形布局管理器 2016/11/10 9:00
        /**
         * LinearLayoutManager 现行管理器，支持横向、纵向。
         * GridLayoutManager 网格布局管理器
         * StaggeredGridLayoutManager 瀑布就式布局管理器
         */
        _recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)); // 3列，方向 2016/11/10 9:07
        _recyclerView.setAdapter(_wrapper);

        // 添加分割线 2016/11/10 9:11
        _recyclerView.addItemDecoration(new DividerGridItemDecoration(this));

        // 设置item之间的间隔 2016/11/10 9:25
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        _recyclerView.addItemDecoration(decoration);

        // 设置item动画 系统默认动画 DefaultItemAnimator 2016/11/10 9:37
        // 使用【RecyclerViewItemAnimators】提供的动画 2016/11/10 9:40
        // url: https://github.com/gabrielemariotti/RecyclerViewItemAnimators
        _recyclerView.setItemAnimator(new ScaleInOutItemAnimator(_recyclerView));
//        _recyclerView.setItemAnimator(new DefaultItemAnimator());
        _recyclerView.setItemAnimator(new CustomItemAnimator());

        // 添加点击事件 2016/11/10 9:54
        _adapter.setOnItemClickLitener(new OnItemClickLitener() {

            @Override
            public void onItemClick(View view, int position) {
                // PS: 添加了头部，这里需要-1 2016/11/10 14:24
                Toast.makeText(StaggeredGridRecyclerViewActivity.this, (position - 1)
                                + " click  content  is " + _lstDatas.get(position - 1),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(StaggeredGridRecyclerViewActivity.this, (position - 1)
                                + " long click  content  is " + _lstDatas.get(position - 1),
                        Toast.LENGTH_SHORT).show();
                _lstDatas.remove(position - 1);
                _wrapper.notifyItemRemoved(position); // 指定位置删除更新 2016/11/10 9:36
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
     * 注意，这里更新数据集不是用adapter.notifyDataSetChanged()而是
     * notifyItemInserted(position)与notifyItemRemoved(position)
     *
     * @param view
     */
    public void OnAdd(View view) {
        _lstDatas.add(1, "Insert One");
        _wrapper.notifyItemInserted(1 + 1); // 指定位置添加更新，因为加了头部，所以需要+1指定更新 2016/11/10 9:36
//        _adapter.notifyDataSetChanged();
    }

    public void OnDel(View view) {
        _lstDatas.remove(1);
        _wrapper.notifyItemRemoved(1 + 1); // 指定位置删除更新，因为加了头部，所以需要+1指定更新 2016/11/10 9:36
//        _adapter.notifyDataSetChanged();
    }

    /**
     * 适配器 2016/11/10 8:52
     */
    private class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(LayoutInflater.from(
                    StaggeredGridRecyclerViewActivity.this).inflate(R.layout.activity_stagger_grid_item, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.tv.setText(_lstDatas.get(position));

            // 模拟瀑布流，动态生成不同高度的view 2016/11/10 9:31
            ViewGroup.LayoutParams p = holder.tv.getLayoutParams();
            p.height = (int) (200 + Math.random() * 400);
//            p.height = 200;
            holder.tv.setLayoutParams(p);

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

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

    interface OnItemClickLitener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }

}
