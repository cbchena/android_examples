package com.example.myContactSearch;

import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.*;

/**
 * 通讯录界面 2015/1/28 11:35
 */
public class PhoneContactActivity extends Activity {

    private ContactListAdapter _adapter; // 列表适配器
    private ListView _contactList; // 显示联系人列表

    private List<ContactBean> _lstData; // 适配器绑定数据列表
    private List<ContactBean> lstBk; // 所有联系人的数据备份

    private AsyncQueryHandler _asyncQueryHandler; // 异步查询数据库类对象
    private QuickAlphabeticBar _alphabeticBar; // 快速索引条

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private Map<String, ContactBean> _mapContactId = null; // 存放联系人，key为号码
    private EditText _editSearch; // 搜索控件
    private ImageView _btnClearSearch; // 搜索控件的清除按钮
    private boolean _isAll = true; // 是否显示的是全部人

    /**
     * 汉字转换成拼音的类 2015/4/25 10:19
     */
    private CharacterParser _characterParser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fy_contact_list_view);

        // 快速索引条
        _alphabeticBar = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

        _characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        // 显示联系人列表
        _contactList = (ListView) findViewById(R.id.contact_list);
        _contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ContactBean contactBean = _lstData.get(i);
                System.out.println("=========  name:   " + contactBean.getDesplayName());
                System.out.println("=========  phone:   " + contactBean.getPhoneNum());
            }
        });

        // 实例化
        _asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
        init();

        // 清除搜索 2015/1/27 11:13
        _btnClearSearch = (ImageView) findViewById(R.id.btn_clean_search);
        _btnClearSearch.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // TODO Auto-generated method stub
                _editSearch.setText("");
                _btnClearSearch.setVisibility(View.GONE);
            }
        });

        // 添加搜索控件以及输入改变监听器 2015/1/28 11:34
        _editSearch = (EditText) findViewById(R.id.edit_search);
        _editSearch.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                //Log.i("before text changed", "start=" + start );

            }

            public void afterTextChanged(Editable ss) {
                // TODO Auto-generated method stub

                String content = _editSearch.getText().toString();
                if ((content == null || content.trim().length() == 0) && _isAll)
                    return;

                if (_lstData != null) {
                    if (content.trim().length() > 0) { // 有东西
                        _btnClearSearch.setVisibility(View.VISIBLE);
//                        _lstData.clear();
                        List<ContactBean> lstContacts = search(content, _lstData);
//                        _lstData.addAll(search(s.toString(), lstBk));
                        _updateAdapter(lstContacts); // 更新适配器数据
                        _isAll = false;
                    } else {
                        _btnClearSearch.setVisibility(View.GONE);
//                        _lstData.clear();
//                        _lstData.addAll(lstBk);
                        _isAll = true;
                        _updateAdapter(_lstData); // 更新适配器数据
                    }

                    _contactList.setSelection(0);
                }
            }
        });
    }

    /**
     * 更新数据 2015/1/27 11:44
     * @param lstList
     */
    private void _updateAdapter(List<ContactBean> lstList) {
        updateSearchHint(); // 更新提示语
        _adapter.updateListView(lstList);
        _adapter.notifyDataSetChanged();
    }

    /**
     * 初始化数据库查询参数 2015/1/28 11:34
     */
    private void init() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // 联系人Uri；

        // 查询的字段
        String[] projection = { ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY };

        // 按照sort_key升序查詢
        _asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");

    }

    /**
     * 更新搜索条的提示语 2015/1/27 11:36
     */
    public void updateSearchHint(){
        Resources res = getResources();
        String text = String.format("共有 %d 个电话号码可以添加", lstBk.size());
        _editSearch.setHint(text);
    }

    /**
     * 查询通讯录数据库 2015/1/27 10:23
     * @author Administrator
     */
    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        public MyAsyncQueryHandler(ContentResolver cr) {
            super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                _mapContactId = new HashMap<String, ContactBean>();
                _lstData = new ArrayList<ContactBean>();
                cursor.moveToFirst(); // 游标移动到第一项
                for (int i = 0; i < cursor.getCount(); i++) {
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    int contactId = cursor.getInt(4);
                    Long photoId = cursor.getLong(5);
                    String lookUpKey = cursor.getString(6);

                    if (_mapContactId.containsKey(number)) { // 判断是否已经存在号码
                        // 无操作
                    } else {
                        // 创建联系人对象
                        ContactBean contact = new ContactBean();
                        contact.setDesplayName(name);
                        contact.setPhoneNum(number);
                        contact.setSortKey(sortKey);
                        contact.setPhotoId(photoId);
                        contact.setLookUpKey(lookUpKey);

                        String pyName = contact.getDesplayName();

                        //优先使用系统sortkey取,取不到再使用工具取
                        String sortLetters = getSortLetterBySortKey(sortKey);
                        if (sortLetters == null) {
                            sortLetters = getSortLetter(pyName);
                        }

                        contact.sortLetters = sortLetters;
                        contact.sortToken = parseSortKey(sortKey);

                        _lstData.add(contact);
                        _mapContactId.put(number, contact);
                    }
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (_lstData.size() > 0) { // 有联系人
                            lstBk = new ArrayList<ContactBean>(_lstData); // 保存备份
                            Collections.sort(_lstData, pinyinComparator);
                            setAdapter(_lstData); // 设置适配器

                            updateSearchHint(); // 更新提示语
                        }
                    }
                });

            }

            super.onQueryComplete(token, cookie, cursor);
        }

    }

    /**
     * 名字转拼音,取首字母 2015/4/25 11:18
     * @param name
     * @return
     */
    private String getSortLetter(String name) {
        String letter = "#";
        if (name == null) {
            return letter;
        }
        //汉字转换成拼音
        String pinyin = _characterParser.getSelling(name);
        String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);

        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 取sort_key的首字母 2015/4/25 11:18
     * @param sortKey
     * @return
     */
    private String getSortLetterBySortKey(String sortKey) {
        if (sortKey == null || "".equals(sortKey.trim())) {
            return null;
        }
        String letter = "#";
        //汉字转换成拼音
        String sortString = sortKey.trim().substring(0, 1).toUpperCase(Locale.CHINESE);
        // 正则表达式，判断首字母是否是英文字母
        if (sortString.matches("[A-Z]")) {
            letter = sortString.toUpperCase(Locale.CHINESE);
        }
        return letter;
    }

    /**
     * 设置适配器 2015/1/27 10:23
     * @param list
     */
    private void setAdapter(List<ContactBean> list) {
        _adapter = new ContactListAdapter(this, list);
        _alphabeticBar.contactListAdapter = _adapter;
        _contactList.setAdapter(_adapter);
        _alphabeticBar.init(PhoneContactActivity.this);
        _alphabeticBar.setListView(_contactList);
        _alphabeticBar.setHight(_alphabeticBar.getHeight());
        _alphabeticBar.setVisibility(View.VISIBLE);
    }

    /**
     * 按号码-拼音搜索联系人 2015/1/28 11:33
     * @param str 搜索的条件
     * @param allContacts 搜索的联系人群
     */
    public List<ContactBean> search(final String str,
                                            final List<ContactBean> allContacts) {
        List<ContactBean> contactList = new ArrayList<ContactBean>();

        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (ContactBean contact : allContacts) {
                if (contact.getPhoneNum() != null && contact.getDesplayName() != null) {
                    if (contact.simpleNumber.contains(simpleStr) || contact.getDesplayName().contains(str)) {
                        if (!contactList.contains(contact)) {
                            contactList.add(contact);
                        }
                    }
                }
            }
        }else {
            for (ContactBean contact : allContacts) {
                if (contact.getPhoneNum() != null && contact.getDesplayName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    if (contact.getDesplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortKey().toLowerCase(Locale.CHINESE).replace(" ", "").contains(str.toLowerCase(Locale.CHINESE))
                            || contact.sortToken.simpleSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))) {
                        if (!contactList.contains(contact)) {
                            contactList.add(contact);
                        }
                    }
                }
            }
        }

        return contactList;
    }

    String chReg = "[\\u4E00-\\u9FA5]+";//中文字符串匹配

    //String chReg="[^\\u4E00-\\u9FA5]";//除中文外的字符匹配
    /**
     * 解析sort_key,封装简拼,全拼
     * @param sortKey
     * @return
     */
    public SortToken parseSortKey(String sortKey) {
        SortToken token = new SortToken();
        if (sortKey != null && sortKey.length() > 0) {
            //其中包含的中文字符
            String[] enStrs = sortKey.replace(" ", "").split(chReg);
            for (int i = 0, length = enStrs.length; i < length; i++) {
                if (enStrs[i].length() > 0) {
                    //拼接简拼
                    token.simpleSpell += enStrs[i].charAt(0);
                    token.wholeSpell += enStrs[i];
                }
            }
        }
        return token;
    }

    @Override
    public void finish() {
        super.finish();
    }
}
