package com.example.voice_rcd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天信息表管理 2015/2/3 9:37
 */
public class DBChatManager {

    private String TAG = "DBChatManager";

    private DBChatInfoTable _dbChatInfoTable; // 聊天信息表
    private String table = "uset_chat_info";

    public DBChatManager(Context context, String dbName) {
        _dbChatInfoTable = new DBChatInfoTable(context, dbName);
    }

    /**
     * 根据指定userId获取数据条数 2015/2/3 10:01
     * @param chatId 聊天窗口Id
     * @return 数量
     */
    public int getCountByUserId(String chatId) {
        String sql = "select count(*)from " + table + " where chatId=?";
        Log.i(TAG + " --> getCountByUserId", sql);

        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();

        // 查询注定用户Id的条数
        Cursor cursor = db.rawQuery(sql, new String[]{chatId});

        //游标移到第一条记录准备获取数据
        cursor.moveToFirst();

        return (int) cursor.getLong(0); // 获取数据中的LONG类型数据
    }

    /**
     * 根据设置条件，获取数据 2015/2/3 9:55
     * @param chatId 聊天窗口Id
     * @param limit 选取条数
     * @param offset 指定跳过条数后，开始选取
     * @return List<ChatMsgEntity>
     */
    public List<ChatMsgEntity> getData(String chatId, int limit, int offset) {
        String sql = "select * from " + table + " where chatId=? limit " + limit + " offset " + offset;
        Log.i(TAG + " --> getData", sql);

        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{chatId});
        List<ChatMsgEntity> lstData = new ArrayList<ChatMsgEntity>();
        ChatMsgEntity chatMsgEntity;
        String id;
        String json;
        int status;
        while(cursor.moveToNext()){ // 利用游标遍历所有数据对象
            chatMsgEntity = new ChatMsgEntity(chatId);
            id = cursor.getString(cursor.getColumnIndex("id"));
            json = cursor.getString(cursor.getColumnIndex("json"));
            status = cursor.getInt(cursor.getColumnIndex("status"));
            try {
                JSONObject object = new JSONObject(json);
                chatMsgEntity.setId(id)
                        .setMsgType(object.getInt("msgType"))
                        .setMsgMe(object.getBoolean("isMsgMe"))
                        .setDate(object.getString("date"))
                        .setVoiceTime(object.getString("voiceTime"))
                        .setContent(object.getString("content"))
                        .setLat(object.getString("lat"))
                        .setLng(object.getString("lng"))
                        .setDisplayTime(object.getBoolean("isDisplayTime"))
                        .setDesc(object.getString("desc"))
                        .setStatus(status);

                lstData.add(chatMsgEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        db.close();
        return lstData;
    }

    /**
     * 根据设置条件，获取数据 2015/2/3 9:55
     * @param chatId 聊天窗口Id
     * @return List<ChatMsgEntity>
     */
    public List<ChatMsgEntity> getData(String chatId) {
        String sql = "select * from " + table + " where chatId=?";
        Log.i(TAG + " --> getData", sql);

        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{chatId});
        List<ChatMsgEntity> lstData = new ArrayList<ChatMsgEntity>();
        ChatMsgEntity chatMsgEntity;
        String id;
        String json;
        int status;
        while(cursor.moveToNext()){ // 利用游标遍历所有数据对象
            chatMsgEntity = new ChatMsgEntity(chatId);
            id = cursor.getString(cursor.getColumnIndex("id"));
            json = cursor.getString(cursor.getColumnIndex("json"));
            status = cursor.getInt(cursor.getColumnIndex("status"));
            try {
                JSONObject object = new JSONObject(json);
                chatMsgEntity.setId(id)
                        .setMsgType(object.getInt("msgType"))
                        .setMsgMe(object.getBoolean("isMsgMe"))
                        .setDate(object.getString("date"))
                        .setVoiceTime(object.getString("voiceTime"))
                        .setContent(object.getString("content"))
                        .setLat(object.getString("lat"))
                        .setLng(object.getString("lng"))
                        .setDisplayTime(object.getBoolean("isDisplayTime"))
                        .setDesc(object.getString("desc"))
                        .setStatus(status);

                lstData.add(chatMsgEntity);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        db.close();
        return lstData;
    }

    /**
     * 保存数据 2015/2/3 10:02
     * @param chatMsgEntity
     */
    public void add(ChatMsgEntity chatMsgEntity) {
        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();

        // 创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("id", chatMsgEntity.getDate());
        values.put("chatId", chatMsgEntity.getChatId());
        values.put("status", chatMsgEntity.getStatus());
        try {
            JSONObject object = new JSONObject();
            object.put("msgType", chatMsgEntity.getMsgType())
                    .put("isMsgMe", chatMsgEntity.isMsgMe())
                    .put("date", chatMsgEntity.getDate())
                    .put("voiceTime", chatMsgEntity.getVoiceTime())
                    .put("content", chatMsgEntity.getContent())
                    .put("lat", chatMsgEntity.getLat())
                    .put("lng", chatMsgEntity.getLng())
                    .put("isDisplayTime", chatMsgEntity.isDisplayTime())
                    .put("desc", chatMsgEntity.getDesc());

            values.put("json", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //数据库执行插入命令
        db.insert(table, null, values);
        db.close();
    }

    /**
     * 修改数据 2015/2/4 17:35
     * @param chatMsgEntity 数据对象
     */
    public void update(ChatMsgEntity chatMsgEntity) {
        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();

        // 创建存放数据的ContentValues对象
        ContentValues values = new ContentValues();
        values.put("id", chatMsgEntity.getId());
        values.put("chatId", chatMsgEntity.getChatId());
        values.put("status", chatMsgEntity.getStatus());
        try {
            JSONObject object = new JSONObject();
            object.put("msgType", chatMsgEntity.getMsgType())
                    .put("isMsgMe", chatMsgEntity.isMsgMe())
                    .put("date", chatMsgEntity.getDate())
                    .put("voiceTime", chatMsgEntity.getVoiceTime())
                    .put("content", chatMsgEntity.getContent())
                    .put("lat", chatMsgEntity.getLat())
                    .put("lng", chatMsgEntity.getLng())
                    .put("isDisplayTime", chatMsgEntity.isDisplayTime())
                    .put("desc", chatMsgEntity.getDesc());

            values.put("json", object.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        System.out.println("修改： " + db.update(table, values, "id=?", new String[]{chatMsgEntity.getId()}));
        db.close();
    }

    public void del() {
        SQLiteDatabase db = _dbChatInfoTable.getReadableDatabase();
        db.delete(table, "", new String[]{});
    }
}
