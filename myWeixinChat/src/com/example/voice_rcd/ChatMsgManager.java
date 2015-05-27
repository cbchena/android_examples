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
 * 聊天信息管理 2015/2/2 17:28
 */
public class ChatMsgManager {

    public final static String TAG = "ChatMsgManager";

    private String LIST_DATA = "data"; // 存放的数据key

    private static ChatMsgManager _chatMsgManager;
    private static Context _context;
    private static SharedPreferences _preferences; // 存储数据

    /**
     * 单例 2015/2/2 19:08
     * @param context  需要注册的上下文
     * @return 广告管理
     */
    public static ChatMsgManager getInstance(Context context, String dataKey) {
        if (_chatMsgManager == null) {
            _chatMsgManager = new ChatMsgManager();
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

        return _chatMsgManager;
    }

    /**
     * 获取所有数据 2015/2/2 17:33
     * @return List<ChatMsgEntity>
     */
    public List<ChatMsgEntity> getData() {
//        del();
        List<ChatMsgEntity> lstData = new ArrayList<ChatMsgEntity>();
        String data = _preferences.getString(LIST_DATA, null);
        if (data == null) return lstData;

        try { // 获取JSON数据
            Log.i(TAG, data);
            JSONObject jsonObject = new JSONObject(data);
            JSONArray jsonArray = jsonObject.getJSONArray("lstData");
            JSONObject object;
            ChatMsgEntity chatMsgEntity;
            for (int i = 0; i < jsonArray.length(); i++) {
                object = jsonArray.getJSONObject(i);
                chatMsgEntity = new ChatMsgEntity(object.getString("id")); // 组合成对象
                chatMsgEntity.setMsgType(object.getInt("msgType"))
                        .setMsgMe(object.getBoolean("isMsgMe"))
                        .setDate(object.getString("date"))
                        .setVoiceTime(object.getString("voiceTime"))
                        .setContent(object.getString("content"))
                        .setLat(object.getString("lat"))
                        .setLng(object.getString("lng"))
                        .setDisplayTime(object.getBoolean("isDisplayTime"))
                        .setDesc(object.getString("desc"));

                lstData.add(chatMsgEntity);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return lstData;
    }

    /**
     * 添加数据 2015/2/2 17:34
     * @param chatMsgEntity 对象数据
     */
    public void add(ChatMsgEntity chatMsgEntity) {
        List<ChatMsgEntity> lstData = getData();
        lstData.add(chatMsgEntity);
        save(lstData);
    }

    /**
     * 保存 2015/2/2 17:34
     * @param lstData 列表数据
     */
    public void save(List<ChatMsgEntity> lstData) {
        SharedPreferences.Editor editor = _preferences.edit();
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            JSONObject object;
            for (ChatMsgEntity chatMsgEntity : lstData) {
                object = new JSONObject();
                object.put("id", chatMsgEntity.getId())
                        .put("msgType", chatMsgEntity.getMsgType())
                        .put("isMsgMe", chatMsgEntity.isMsgMe())
                        .put("date", chatMsgEntity.getDate())
                        .put("voiceTime", chatMsgEntity.getVoiceTime())
                        .put("content", chatMsgEntity.getContent())
                        .put("lat", chatMsgEntity.getLat())
                        .put("lng", chatMsgEntity.getLng())
                        .put("isDisplayTime", chatMsgEntity.isDisplayTime())
                        .put("desc", chatMsgEntity.getDesc());

                jsonArray.put(jsonArray.length(), object);
            }

            jsonObject.put("lstData", jsonArray);
            editor.putString(LIST_DATA, jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        editor.commit();
    }

    /**
     * 清除数据 2015/1/22 17:29
     */
    public void del() {
        SharedPreferences.Editor editor = _preferences.edit();
        editor.remove(LIST_DATA);
        editor.commit();
    }

}
