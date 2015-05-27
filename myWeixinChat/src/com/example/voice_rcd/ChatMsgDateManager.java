package com.example.voice_rcd;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天信息日期管理 2015/2/2 17:28
 */
public class ChatMsgDateManager {

    public final static String TAG = "ChatMsgDateManager";

    private String LIST_DATA = "data"; // 存放的数据key

    private static ChatMsgDateManager _chatMsgDateManager;
    private static Context _context;
    private static SharedPreferences _preferences; // 存储数据

    /**
     * 单例 2015/2/2 19:07
     * @param context  需要注册的上下文
     * @return 广告管理
     */
    public static ChatMsgDateManager getInstance(Context context, String dataKey) {
        if (_chatMsgDateManager == null) {
            _chatMsgDateManager = new ChatMsgDateManager();
        }

        _context = context;

        /**
         * Context.MODE_PRIVATE：为默认操作模式，代表该文件是私有数据，只能被应用本身访问，在该模式下，
             写入的内容会覆盖原文件的内容，如果想把新写入的内容追加到原文件中。可以使用Context.MODE_APPEND
             Context.MODE_APPEND：模式会检查文件是否存在，存在就往文件追加内容，否则就创建新文件。
             Context.MODE_WORLD_READABLE和Context.MODE_WORLD_WRITEABLE用来控制其他应用是否有权限读写该文件。
             MODE_WORLD_READABLE：表示当前文件可以被其他应用读取；
             MODE_WORLD_WRITEABLE：表示当前文件可以被其他应用写入。
         */
        _preferences = _context.getSharedPreferences(dataKey, PreferenceActivity.MODE_PRIVATE);

        return _chatMsgDateManager;
    }

    /**
     * 获取数据 2015/2/2 19:06
     * @return List<String>
     */
    public List<String> getData() {
//        del();
        List<String> lstData = new ArrayList<String>();
        String data = _preferences.getString(LIST_DATA, null);
        if (data == null) return lstData;

        try { // 获取JSON数据
            Log.i(TAG, data);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("lstDate");
            String object;
            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getString(i);
                lstData.add(object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lstData;
    }

    /**
     * 添加数据 2015/2/2 19:06
     * @param date 对象数据
     */
    public void add(String date) {
        List<String> lstData = getData();
        lstData.add(0, date);
        save(date);
    }

    /**
     * 保存 2015/2/2 19:06
     * @param date 时间数据
     */
    public void save(String date) {
        SharedPreferences.Editor editor = _preferences.edit();
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonArray.length(), date);
            jsonObject.put("lstDate", jsonArray);
            editor.putString(LIST_DATA, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.commit();
    }

    /**
     * 清除数据 2015/2/2 19:06
     */
    public void del() {
        SharedPreferences.Editor editor = _preferences.edit();
        editor.remove(LIST_DATA);
        editor.commit();
    }

}
