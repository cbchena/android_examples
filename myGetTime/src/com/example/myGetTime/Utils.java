package com.example.myGetTime;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 工具类 2015/7/22 8:47
 */
public class Utils {

    /**
     * 获取当前时间  2014/11/11 10:48
     * @return String
     */
    public static String getCurTime() {

        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = formatter.format(d); // 当前时间

        return time;
    }

    /**
     * 获取时间  2014/11/11 10:48
     * @return String
     */
    public static String getTime(long time) {

        Date d = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        return formatter.format(d);
    }

    /**
     * 获取当前日期  2014/11/11 10:48
     * @return String
     */
    public static String getCurDate() {

        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(d); // 当前时间

        return time;
    }

    /**
     * 根据周期获取日期 2015/9/6 10:34
     * @param date 周期
     * @return 日期
     */
    public static String getDateByNum(int date) {
        Date d = new Date(System.currentTimeMillis() + date * 24 * 60 * 60 * 1000L);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String time = formatter.format(d); // 当前时间

        return time;
    }

    /**
     * 获取日期  2014/11/11 10:48
     * @return String
     */
    public static String getDate(long time) {
        Date d = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        return formatter.format(d);
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date 字符串日期
     * @return 时间戳
     */
    public static long date2TimeStamp(String date){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return formatter.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * 日期格式字符串转换成时间戳 2015/10/23 17:22
     * @param date 字符串日期
     * @param format 转换的时间格式
     * @return 时间戳
     */
    public static long date2TimeStamp(String date, String format){
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            return formatter.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

}
