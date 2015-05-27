package com.example.voice_rcd;

import java.util.HashMap;
import java.util.Map;

public class Const {

    public final static String DB_NAME = "andy_db"; // 数据库名称
    public final static String CHAT_KEY_ZONE_DATE = "zone_date"; // 安全空间聊天信息日期key

    // 存放聊天窗口是否显示时间 key chatId, value 发送时间
    public final static Map<String, String> mapDisplayTime = new HashMap<String, String>();

    public final static int CHAT_TYPE_WORDS = 1; // 文本
    public final static int CHAT_TYPE_VOICE = 2; // 音频
    public final static int CHAT_TYPE_PICTURE = 3; // 图片
    public final static int CHAT_TYPE_LOCATION = 4; // 位置信息

    public final static int CHAT_UPLOAD_SUCCESS = 0; // 上传成功
    public final static int CHAT_UPLOAD_ING = 1; // 正在上传
    public final static int CHAT_UPLOAD_ERROR = 2; // 上传失败

}
