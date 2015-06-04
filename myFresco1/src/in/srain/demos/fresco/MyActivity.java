package in.srain.demos.fresco;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private ListView _lstView; // 列表
    private ImageListAdapter _imageListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _lstView = (ListView) this.findViewById(R.id.lstItem);
        _imageListAdapter = new ImageListAdapter();
        _lstView.setAdapter(_imageListAdapter);

        List<String> lstUrl = new ArrayList<String>();
        lstUrl.add("http://www.easyicon.net/api/resize_png_new.php?id=1185824&size=128");
        lstUrl.add("http://www.easyicon.net/api/resize_png_new.php?id=1185841&size=128");
        lstUrl.add("http://www.easyicon.net/api/resize_png_new.php?id=1186368&size=128");
        lstUrl.add("http://www.easyicon.net/api/resize_png_new.php?id=1137550&size=128");
        lstUrl.add("http://www.easyicon.net/api/resize_png_new.php?id=1137581&size=128");

        ImageEntity imageEntity;
        for(int i = 0;i < lstUrl.size();i++) {
            imageEntity = new ImageEntity();
            imageEntity.setName("ccb" + i);
            imageEntity.setDesc("desc" + i);
            imageEntity.setUrl(lstUrl.get(i));

            _imageListAdapter.addImage(imageEntity);
        }

        _imageListAdapter.notifyDataSetChanged();
    }

    /**
     * 列表适配器 2015/5/18 16:40
     */
    private class ImageListAdapter extends BaseAdapter {
        private ArrayList<ImageEntity> mLeDevices;
        private LayoutInflater mInflator;

        public ImageListAdapter() {
            super();
            mLeDevices = new ArrayList<ImageEntity>();
            mInflator = MyActivity.this.getLayoutInflater();
        }

        public void addImage(ImageEntity device) {
            mLeDevices.add(device);
        }

        public ImageEntity getImage(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            if (view == null) {
                view = mInflator.inflate(R.layout.fy_img_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.desc = (TextView) view
                        .findViewById(R.id.mac);
                viewHolder.name = (TextView) view
                        .findViewById(R.id.name);
                viewHolder.img = (SimpleDraweeView) view
                        .findViewById(R.id.img);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageEntity item = mLeDevices.get(i);
            viewHolder.name.setText(item.getName());
            viewHolder.desc.setText(item.getDesc());

            // 加载图片 2015/6/4 16:09
            Uri logoUri = Uri.parse(item.getUrl());
            viewHolder.img.setImageURI(logoUri);

            return view;
        }
    }

    static class ViewHolder {
        TextView name;
        TextView desc;
        SimpleDraweeView img;
    }

}
