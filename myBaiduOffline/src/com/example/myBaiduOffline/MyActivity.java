package com.example.myBaiduOffline;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyActivity extends Activity {

    private static final String TAG = "MyActivity";

    private ExpandableListView _lstView; // 列表
    private ListView _lstUpdateItem; // 下载管理列表
    private CityListAdapter _mCityListAdapter; // 二级城市适配器
    private UpdateListAdapter _updateListAdapter; // 下载城市适配器
    private Button _btnDwnManager; // 下载管理
    private Button _btnCityList; // 城市列表

    private Map<Integer, CityEntity> _mapDownload; // 已经下载的城市
    private Map<Integer, CityEntity> _mapDwning; // 正在下载

    /**
     * 离线地图功能
     */
    private MKOfflineMap mOfflineMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main);

        mOfflineMap = new MKOfflineMap();
        mOfflineMap.init(new MKOfflineMapListener() { // 设置监听
            @Override
            public void onGetOfflineMapState(int type, int state) {
                switch (type) {
                    case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: // 离线地图下载更新
                        MKOLUpdateElement update = mOfflineMap.getUpdateInfo(state);
                        Log.e(TAG, update.cityName + " ," + update.ratio);
                        CityEntity cityEntity = _mCityListAdapter.getSubCity(update.cityID);
                        cityEntity.setRatio(update.ratio);
                        if (update.ratio == 100) { // 下载完成 2015/5/26 10:57
                            CityEntity city = new CityEntity();
                            city.setCityID(update.cityID)
                                .setCityName(update.cityName);

                            _mapDownload.put(update.cityID, city);
                            _updateListAdapter.addCity(city);
                            _updateListAdapter.notifyDataSetChanged();
                            _mapDwning.remove(update.cityID);
                        }

                        _mCityListAdapter.notifyDataSetChanged();
                        break;
                    case MKOfflineMap.TYPE_NEW_OFFLINE:
                        System.out.println("====================  111  ");
                        break;
                    case MKOfflineMap.TYPE_VER_UPDATE: // 版本更新提示
                        System.out.println("====================  222  ");
                        break;
                }
            }
        });

        _btnDwnManager = (Button) this.findViewById(R.id.btnDwnManager); // 下载管理
        _btnCityList = (Button) this.findViewById(R.id.btnCityList); // 城市列表

        _lstView = (ExpandableListView) this.findViewById(R.id.lstItem);
        _mCityListAdapter = new CityListAdapter();
        _lstView.setAdapter(_mCityListAdapter);
        _lstView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() { // 点击子城市
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i2, long l) {
                CityEntity cityEntity = (CityEntity) _mCityListAdapter.getChild(i, i2);
                if (!_mapDownload.containsKey(cityEntity.getCityID())
                        && !_mapDwning.containsKey(cityEntity.getCityID())) { // 判断是否存在，不存在就启动下载
                    mOfflineMap.start(cityEntity.getCityID()); // 启动下载指定城市ID的离线地图
                    _mapDwning.put(cityEntity.getCityID(), cityEntity);
                } else if (_mapDwning.containsKey(cityEntity.getCityID())) {
                    Toast.makeText(MyActivity.this, "该城市正在下载", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyActivity.this, "该城市已经下载", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });

        _lstUpdateItem = (ListView) this.findViewById(R.id.lstUpdateItem);
        _updateListAdapter = new UpdateListAdapter();
        _lstUpdateItem.setAdapter(_updateListAdapter);

        _mapDownload = new HashMap<Integer, CityEntity>();
        _mapDwning = new HashMap<Integer, CityEntity>();

        // 设置所有已经下载的城市列表
        setDownloadList();

        // 设置离线下载的城市列表
        setOfflineCityList();
    }

    /**
     * 设置所有已经下载的城市列表 2015/5/25 15:35
     */
    private void setDownloadList() {
        ArrayList<MKOLUpdateElement> allUpdateInfo = mOfflineMap
                .getAllUpdateInfo();

        if (allUpdateInfo == null)
            return;

        for(MKOLUpdateElement mkolUpdateElement:allUpdateInfo) {
            CityEntity cityEntity = new CityEntity();
            cityEntity.setCityID(mkolUpdateElement.cityID)
                    .setCityName(mkolUpdateElement.cityName);

            _mapDownload.put(mkolUpdateElement.cityID, cityEntity);
            _updateListAdapter.addCity(cityEntity);
        }

        _updateListAdapter.notifyDataSetChanged();
    }

    /**
     * 设置所有支持离线下载的城市列表 2015/5/25 14:27
     */
    private void setOfflineCityList() {
        ArrayList<MKOLSearchRecord> offlineCityList = mOfflineMap
                .getOfflineCityList();
        for(MKOLSearchRecord mkolSearchRecord:offlineCityList) { // 遍历所有支持离线下载的城市
            CityEntity cityEntity = new CityEntity();
            cityEntity.setCityID(mkolSearchRecord.cityID)
                    .setCityName(mkolSearchRecord.cityName)
                    .setSize(mkolSearchRecord.size);
            List<CityEntity> lstChildCities = new ArrayList<CityEntity>();

//            mOfflineMap.remove(mkolSearchRecord.cityID); // 删除指定城市ID的离线地图
            if (mkolSearchRecord.childCities != null) { // 判断是否有子城市
                cityEntity.setChildCities(true);
                for (MKOLSearchRecord mkolSearchRecord1 : mkolSearchRecord.childCities) { // 遍历子城市
                    CityEntity childCityEntity = new CityEntity();
                    childCityEntity.setCityID(mkolSearchRecord1.cityID)
                            .setCityName(mkolSearchRecord1.cityName)
                            .setChildCities(false)
                            .setSize(mkolSearchRecord1.size);
                    lstChildCities.add(childCityEntity);

//                    mOfflineMap.remove(mkolSearchRecord1.cityID); // 删除指定城市ID的离线地图
                }
            } else { // 将本身添加进去
                cityEntity.setChildCities(false);
                CityEntity childCityEntity = new CityEntity();
                childCityEntity.setCityID(cityEntity.getCityID())
                        .setCityName(cityEntity.getCityName())
                        .setChildCities(false)
                        .setSize(cityEntity.getSize());

                lstChildCities.add(childCityEntity);
            }

            _mCityListAdapter.addCity(cityEntity);
            _mCityListAdapter.addSubCityList(lstChildCities);
        }

        _mCityListAdapter.notifyDataSetChanged();
    }

    public String formatDataSize(int size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    /**
     * 下载管理 2015/5/26 10:28
     * @param view
     */
    public void OnDownloadManager(View view) {
        _btnDwnManager.setBackgroundResource(R.drawable.register_get_code_btn_bg);
        _btnDwnManager.setEnabled(false);

        _btnCityList.setBackgroundResource(R.drawable.register_icon_bg);
        _btnCityList.setEnabled(true);

        _lstUpdateItem.setVisibility(View.VISIBLE);
        _lstView.setVisibility(View.GONE);

        _updateListAdapter.notifyDataSetChanged();
    }

    /**
     * 城市列表 2015/5/26 10:28
     * @param view
     */
    public void OnCityList(View view) {
        _btnCityList.setBackgroundResource(R.drawable.register_get_code_btn_bg);
        _btnCityList.setEnabled(false);

        _btnDwnManager.setBackgroundResource(R.drawable.register_icon_bg);
        _btnDwnManager.setEnabled(true);

        _lstUpdateItem.setVisibility(View.GONE);
        _lstView.setVisibility(View.VISIBLE);

        _mCityListAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onDestroy() {
        mOfflineMap.destroy();
        super.onDestroy();
    }

    /**
     * 列表适配器 2015/5/18 16:40
     */
    private class CityListAdapter extends BaseExpandableListAdapter {
        private ArrayList<CityEntity> mCityEntity;
        private ArrayList<List<CityEntity>> mSubCityEntity;
        private LayoutInflater mInflator;

        public CityListAdapter() {
            super();
            mCityEntity = new ArrayList<CityEntity>();
            mSubCityEntity = new ArrayList<List<CityEntity>>();
            mInflator = MyActivity.this.getLayoutInflater();
        }

        public void addCity(CityEntity cityEntity) {
            mCityEntity.add(cityEntity);
        }

        public void addSubCityList(List<CityEntity> lstCitys) {
            mSubCityEntity.add(lstCitys);
        }

        public CityEntity getSubCity(int cityID) {
            for(List<CityEntity> lstSub:mSubCityEntity) {
                for(CityEntity cityEntity:lstSub) {
                    if (cityEntity.getCityID() == cityID)
                        return cityEntity;
                }
            }

            return null;
        }

        @Override
        public int getGroupCount() {
            return mCityEntity.size();
        }

        @Override
        public int getChildrenCount(int i) {
            return mSubCityEntity.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
            return mCityEntity.get(i);
        }

        @Override
        public Object getChild(int i, int i2) {
            return mSubCityEntity.get(i).get(i2);
        }

        @Override
        public long getGroupId(int i) {
            return mCityEntity.get(i).getCityID();
        }

        @Override
        public long getChildId(int i, int i2) {
            return mSubCityEntity.get(i).get(i2).getCityID();
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.fy_city_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.txtName = (TextView) view
                        .findViewById(R.id.name);
                viewHolder.imageView = (ImageView) view
                        .findViewById(R.id.icon);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            CityEntity cityEntity = mCityEntity.get(i);
            viewHolder.txtName.setText(cityEntity.getCityName());
            if (b) { // 判断是否扩展
                viewHolder.imageView.setImageResource(R.drawable.icon_2);
            } else {
                viewHolder.imageView.setImageResource(R.drawable.icon_1);
            }

            return view;
        }

        @Override
        public View getChildView(int i, int i2, boolean b, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.fy_city_sub_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.txtName = (TextView) view
                        .findViewById(R.id.name);
                viewHolder.txtStatus = (TextView) view
                        .findViewById(R.id.txtStatus);
                viewHolder.txtSize = (TextView) view
                        .findViewById(R.id.txtSize);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            CityEntity cityEntity = mSubCityEntity.get(i).get(i2);
            viewHolder.txtName.setText(cityEntity.getCityName());
//            if (b) { // 判断是否扩展
//                System.out.println("已扩展");
//            } else {
//                System.out.println("未扩展");
//            }

            viewHolder.txtSize.setText(formatDataSize(cityEntity.getSize()));

            if (cityEntity.getRatio() == 0 || cityEntity.getRatio() == 100) { // 判断是否正在下载 2015/5/25 16:50
                if (_mapDownload.containsKey(cityEntity.getCityID())) { // 判断是否已经下载
                    viewHolder.txtStatus.setText("已下载");
                    viewHolder.txtName.setTextColor(0xffc3c3c3);
                    viewHolder.txtStatus.setTextColor(0xffc3c3c3);
                } else {
                    viewHolder.txtStatus.setText("未下载");
                    viewHolder.txtName.setTextColor(0xff000000);
                    viewHolder.txtStatus.setTextColor(0xff000000);
                }
            } else {
                viewHolder.txtStatus.setText(cityEntity.getRatio() + "%");
            }

            return view;
        }

        @Override
        public boolean isChildSelectable(int i, int i2) {
            return true;
        }
    }

    /**
     * 列表适配器 2015/5/26 10:22
     */
    private class UpdateListAdapter extends BaseAdapter {
        private ArrayList<CityEntity> mCity;
        private LayoutInflater mInflator;

        public UpdateListAdapter() {
            super();
            mCity = new ArrayList<CityEntity>();
            mInflator = MyActivity.this.getLayoutInflater();
        }

        public void addCity(CityEntity cityEntity) {
            mCity.add(cityEntity);
        }

        public CityEntity getCityBypositon(int position) {
            return mCity.get(position);
        }

        public void clear() {
            mCity.clear();
        }

        @Override
        public int getCount() {
            return mCity.size();
        }

        @Override
        public Object getItem(int i) {
            return mCity.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.fy_city_update_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.txtName = (TextView) view
                        .findViewById(R.id.name);
                viewHolder.txtStatus = (TextView) view
                        .findViewById(R.id.txtStatus);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            CityEntity cityEntity = mCity.get(i);
            viewHolder.txtName.setText(cityEntity.getCityName());

//            if (cityEntity.getRatio() == 100) { // 判断是否正在下载 2015/5/25 16:50
                if (_mapDownload.containsKey(cityEntity.getCityID())) { // 判断是否已经下载
                    viewHolder.txtStatus.setText("完成");
                }
//            } else {
//                viewHolder.txtStatus.setText(cityEntity.getRatio() + "%");
//            }

            return view;
        }
    }


    static class ViewHolder {
        TextView txtName;
        TextView txtStatus;
        TextView txtSize;
        ImageView imageView;
    }
}
