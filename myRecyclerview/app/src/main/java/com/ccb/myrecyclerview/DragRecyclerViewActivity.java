package com.ccb.myrecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基础的RecyclerView 2016/11/10 8:45
 */
public class DragRecyclerViewActivity extends Activity {

    private RecyclerView _recyclerView;
    private List<String> _lstDatas;
    private HomeAdapter _adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);

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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

               // actionState : action状态类型，有三类 ACTION_STATE_DRAG （拖曳），ACTION_STATE_SWIPE（滑动），ACTION_STATE_IDLE（静止）
                int dragFlags = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);  // 支持上下左右的拖曳
                int swipeFlags = makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);  // 表示支持左右的滑动
                return makeMovementFlags(dragFlags, swipeFlags);  // 直接返回0表示不支持拖曳和滑动
            }

            /*
            @param recyclerView attach的RecyclerView
            @param viewHolder 拖动的Item
            @param target 放置Item的目标位置
            @return
            */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition(); // 要拖曳的位置
                int toPosition = target.getAdapterPosition(); // 要放置的目标位置
                Collections.swap(_lstDatas, fromPosition, toPosition); // 做数据的交换
                _adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            /*
            @param viewHolder 滑动移除的Item
            @param direction
            */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();  // 获取要滑动删除的Item位置
                _lstDatas.remove(position);  // 删除数据
                _adapter.notifyItemRemoved(position);
            }

        });
        itemTouchHelper.attachToRecyclerView(_recyclerView);

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
                    DragRecyclerViewActivity.this).inflate(R.layout.activity_drag_item, parent,
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
