package com.example.voice_rcd.check_location;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.knowyou.ky_sdk.FilesService;
import com.knowyou.ky_sdk.utils.KyUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 暂定最简单的发送位置 2015/2/9 10:04
 */
public class Location {

    private static final String MAP_KEY = "C1RpE1cGOyzi2ooAETlcDtqr";
    private static Context _context;
    private static String _lat;
    private static String _lng;
    private static Handler _handler;
    private static LocationClient locationClient = null;

    private static byte[] _json;

    /**
     * 获取当前位置的Bitmap图
     */
    public static void getCurBitmap(Context context, Handler handler) {
        _context = context;
        _handler = handler;

        locationClient = new LocationClient(_context);

        //设置定位条件
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);                                //是否打开GPS
        option.setCoorType("bd09ll");                           //设置返回值的坐标类型。
        option.setProdName("LocationDemo");                     //设置产品线名称。强烈建议您使用自定义的产品线名称，方便我们以后为您提供更高效准确的定位服务。
        option.setScanSpan(60000);                        //设置定时定位的时间间隔。单位毫秒

        /**
         * 1、高精度模式定位策略：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
           2、低功耗模式定位策略：该定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；
           3、仅用设备模式定位策略：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。
         Hight_Accuracy高精度、Battery_Saving低功耗、Device_Sensors仅设备(GPS)
         */
        boolean isNetwork = KyUtils.networkStatusOK(_context);
        if (isNetwork) // 有网络情况(wifi或者网络数据)下，使用网络发送
            option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        else // 否则使用gps定位发送
            option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors);

        locationClient.setLocOption(option);

        //注册位置监听器
        locationClient.registerLocationListener(new BDLocationListener() {

            @Override
            public void onReceiveLocation(BDLocation location) {
                // TODO Auto-generated method stub
                if (location == null) {
                    return;
                }

                boolean isSuccess = false;
                Log.i("Locaion", "LocType: " + location.getLocType());
                if (location.getLocType() == BDLocation.TypeGpsLocation) { // GPS定位结果
                    isSuccess = true;
                    Log.i("Locaion", "GpsLocation: " + location.getLatitude() + ", " + location.getLongitude());
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) { // 网络定位结果
                    isSuccess = true;
                    Log.i("Locaion", "NetWorkLocation: " + location.getLatitude() + ", " + location.getLongitude());
                }

                if (isSuccess) {
                    _lat = String.valueOf(location.getLatitude());
                    _lng = String.valueOf(location.getLongitude());
                    _runnAbleImg();
                }
            }
        });

        locationClient.start();
        locationClient.requestLocation();
    }

    private static Handler _handlerImg = new Handler() { // 得到图片后，进行处理
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            _runnAble();
        }
    };

    /**
     * 开启子线程运行，根据url获取json数据 2014/8/15 10:44
     */
    private static void _runnAbleImg(){
        locationClient.stop();
        Runnable runnable = new Runnable(){
            @Override
            public void run() {

                boolean isNetwork = KyUtils.networkStatusOK(_context);
                String urlPathContent = "http://api.map.baidu.com/staticimage?center="
                        + _lng + "," + _lat + "&width=480&height=300&zoom=17";
                try {
                    if (isNetwork) {
                        _json = FilesService.getBytes(urlPathContent); // 获取json数据
                    } else {
                        /*使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，
                        消息处理时就在主线程中完成，如果线程中使用Looper.prepare()和Looper.loop()创建了
                            消息队列就可以让消息处理在该线程中完成*/
                        Looper.prepare();
                        Toast.makeText(_context, "网络异常",
                                Toast.LENGTH_LONG).show();  // 通知用户连接超时信息
                        Looper.loop();
                    }
                } catch (IOException e) {
                    Log.w("Location", "Method: runnAble Reason: IOException");
                    /*使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，
                    消息处理时就在主线程中完成，如果线程中使用Looper.prepare()和Looper.loop()创建了
                        消息队列就可以让消息处理在该线程中完成*/
                    Looper.prepare();
                    Toast.makeText(_context, "网络异常",
                            Toast.LENGTH_LONG).show();  // 通知用户连接超时信息
                    Looper.loop();
                }

                _handlerImg.sendEmptyMessage(0);
            }
        };

        new Thread(runnable).start();
    }

    /**
     * 开启子线程运行，根据url获取json数据 2014/8/15 10:44
     */
    private static void _runnAble(){
        Runnable runnable = new Runnable(){
            @Override
            public void run() {

                boolean isNetwork = KyUtils.networkStatusOK(_context);
                String strData = null;
                // 图片地址
                String urlPathContent = "http://api.map.baidu.com/geocoder?location="
                        + _lat + "," + _lng + "&output=json&key=" + MAP_KEY;
                try {
                    if (isNetwork) {
                        byte[] json = FilesService.getBytes(urlPathContent); // 获取json数据
                        JSONObject objJson = new JSONObject(new String(json)); // 解析JSON
                        JSONObject objResult = objJson.getJSONObject("result");
                        strData = objResult.getString("formatted_address");
                    } else {
                        /*使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，
                        消息处理时就在主线程中完成，如果线程中使用Looper.prepare()和Looper.loop()创建了
                            消息队列就可以让消息处理在该线程中完成*/
                        Looper.prepare();
                        Toast.makeText(_context, "网络异常",
                                Toast.LENGTH_LONG).show();  // 通知用户连接超时信息
                        Looper.loop();
                    }
                } catch (IOException e) {
                    Log.w("LibraryActivity", "Method: runnAble Reason: IOException");
                    /*使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，
                    消息处理时就在主线程中完成，如果线程中使用Looper.prepare()和Looper.loop()创建了
                        消息队列就可以让消息处理在该线程中完成*/
                    Looper.prepare();
                    Toast.makeText(_context, "网络异常",
                            Toast.LENGTH_LONG).show();  // 通知用户连接超时信息
                    Looper.loop();
                } catch (JSONException e) {
                    Log.w("LibraryActivity", "Method: runnAble Reason: JSONException");
                    /*使线程拥有自己的消息列队，主线程拥有自己的消息列队，一般线程创建时没有自己的消息列队，
                    消息处理时就在主线程中完成，如果线程中使用Looper.prepare()和Looper.loop()创建了
                        消息队列就可以让消息处理在该线程中完成*/
                    Looper.prepare();
                    Toast.makeText(_context, "网络异常",
                            Toast.LENGTH_LONG).show();  // 通知用户连接超时信息
                    Looper.loop();
                }

                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("location", strData);
                data.putString("lat", _lat);
                data.putString("lng", _lng);
                data.putByteArray("img", _json);

                msg.setData(data);
                _handler.sendMessage(msg); // 在主线程之外，要更新ui就得使用Handler进行消息处理
            }
        };

        new Thread(runnable).start();
    }

}
