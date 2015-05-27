
package com.example.voice_rcd;

/**
 * 消息实体类 2015/1/26 16:47
 */
public class ChatMsgEntity {

    public ChatMsgEntity(String chatId) {
        setChatId(chatId);
    }

    private String id; // 唯一值

    private String chatId; // 聊天窗口Id

    private String userName; // 用户名称

    private int msgType; // 消息类型  1 文字  2 语音  3 图片 4 位置

    private boolean isMsgMe; // 是不是属于我的消息

    private String content; // 内容  msgType == 1，则是文本信息，否则全部为路径

    private String date; // 日期

    private String voiceTime; // 音频时间

    private String lat; // 经度

    private String lng; // 纬度

    private String desc; // 描述信息

    private boolean isDisplayTime; // 是否显示时间

    private int status; // 数据状态 0 上传成功 1 正在上传 2 上传失败

    private String progress; // 上传进度

    public String getSid() {
        return String.valueOf(System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public ChatMsgEntity setId(String id) {
        this.id = id;

        return this;
    }

    public int getMsgType() {
        return msgType;
    }

    public ChatMsgEntity setMsgType(int msgType) {
        this.msgType = msgType;

        return this;
    }

    public boolean isMsgMe() {
        return isMsgMe;
    }

    public ChatMsgEntity setMsgMe(boolean isMsgMe) {
        this.isMsgMe = isMsgMe;

        return this;
    }

    public String getContent() {
        if (content == null) return "";

        return content;
    }

    public ChatMsgEntity setContent(String content) {
        this.content = content;

        return this;
    }

    public String getDate() {
        if (date == null) return "";

        return date;
    }

    public ChatMsgEntity setDate(String date) {
        this.date = date;

        return this;
    }

    public String getVoiceTime() {
        if (voiceTime == null) return "";

        return voiceTime;
    }

    public ChatMsgEntity setVoiceTime(String voiceTime) {
        this.voiceTime = voiceTime;

        return this;
    }

    public String getLat() {
        if (lat == null) return "";

        return lat;
    }

    public ChatMsgEntity setLat(String lat) {
        this.lat = lat;

        return this;
    }

    public String getLng() {
        if (lng == null) return "";

        return lng;
    }

    public ChatMsgEntity setLng(String lng) {
        this.lng = lng;

        return this;
    }

    public String getDesc() {
        if (desc == null) return "";

        return desc;
    }

    public ChatMsgEntity setDesc(String desc) {
        this.desc = desc;

        return this;
    }

    public String getUserName() {
        if (userName == null) return "";

        return userName;
    }

    public ChatMsgEntity setUserName(String userName) {
        this.userName = userName;

        return this;
    }

    public boolean isDisplayTime() {
        return isDisplayTime;
    }

    public ChatMsgEntity setDisplayTime(boolean isDisplayTime) {
        this.isDisplayTime = isDisplayTime;

        return this;
    }

    public int getStatus() {
        return status;
    }

    public ChatMsgEntity setStatus(int status) {
        this.status = status;

        return this;
    }

    public String getChatId() {
        return chatId;
    }

    public ChatMsgEntity setChatId(String chatId) {
        this.chatId = chatId;

        return this;
    }

    public String getProgress() {
        return progress;
    }

    public ChatMsgEntity setProgress(String progress) {
        this.progress = progress;

        return this;
    }
}
