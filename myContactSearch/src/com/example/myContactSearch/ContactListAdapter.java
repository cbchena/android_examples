package com.example.myContactSearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 联系人适配器 2015/1/28 11:40
 */
public class ContactListAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<ContactBean> _lstData; // 绑定适配器的数据列表
    private String[] _sections; // 存储每个章节
    private Context _context; // 上下文

    public ContactListAdapter(Context context, List<ContactBean> list) {
        this._context = context;
        this.inflater = LayoutInflater.from(context);
        this._lstData = list;
        this._sections = new String[list.size()];

        updateListView(list); // 更新索引
    }

    /**
     * 当ListView数据发生变化时,调用此方法来更新ListView
     * @param list
     */
    public void updateListView(List<ContactBean> list) {
        if (list == null) {
            this._lstData = new ArrayList<ContactBean>();
        } else {
            this._lstData = list;
        }

        notifyDataSetChanged();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = _lstData.get(i).sortLetters;
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getCount() {
        return _lstData.size();
    }

    @Override
    public Object getItem(int position) {
        return _lstData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void remove(int position) {
        _lstData.remove(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) { // 创建item
            convertView = inflater.inflate(R.layout.fy_contact_phone_list_item, null);
            holder = new ViewHolder();
//            holder.quickContactBadge = (QuickContactBadge) convertView
//                    .findViewById(R.id.qcb);
            holder.quickContactBadge = (ImageView) convertView
                    .findViewById(R.id.qcb);
            holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.number = (TextView) convertView.findViewById(R.id.number);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置数据 2015/1/28 11:39
        ContactBean contact = _lstData.get(position);
        String name = contact.getDesplayName();
        String number = contact.getPhoneNum();
        holder.name.setText(name);
        holder.number.setText(number);

//        holder.quickContactBadge.assignContactUri(Contacts.getLookupUri(
//                contact.getContactId(), contact.getLookUpKey()));
        if (0 == contact.getPhotoId()) { // 没有头像，使用默认头像
            holder.quickContactBadge.setImageResource(R.drawable.ic_contact_picture_holo);
        } else { // 添加头像
            Bitmap bitmap = contact.getPhoto(this._context);
            if (bitmap != null)
                holder.quickContactBadge.setImageBitmap(bitmap);
//            Uri uri = ContentUris.withAppendedId(
//                    Contacts.CONTENT_URI,
//                    contact.getContactId());
//            InputStream input = Contacts
//                    .openContactPhotoInputStream(_context.getContentResolver(), uri);
//            Bitmap contactPhoto = BitmapFactory.decodeStream(input);
//            holder.quickContactBadge.setImageBitmap(contactPhoto);
        }

        // 前面的字母
        String previewStr = (position - 1) >= 0 ? _lstData.get(
                position - 1).sortLetters : " ";

        if (!previewStr.equals(contact.sortLetters)) {
            holder.alpha.setVisibility(View.VISIBLE);
            holder.alpha.setText(contact.sortLetters);
        } else {
            holder.alpha.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * item数据 2015/1/28 11:40
     */
    private static class ViewHolder {
//        QuickContactBadge quickContactBadge;
        ImageView quickContactBadge;
        TextView alpha;
        TextView name;
        TextView number;
    }

}
