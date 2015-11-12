package com.example.myGetTime;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;

public class MyActivity extends Activity {

    private long ld;
    private TextView _txtTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _txtTime = (TextView) this.findViewById(R.id.txtTime);

//        _runable();
        _getCurrentNetworkTime();
    }

    /**
     * 方法一：时间误差20秒左右 2015/11/12 9:24
     */
    private void _runable() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                URLConnection uc = null;//生成连接对象
                try {
                    URL url = new URL("http://www.bjtime.cn");//取得资源对象
                    uc = url.openConnection();
                    uc.connect(); //发出连接
                    ld = uc.getDate(); //取得网站日期时间

                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        new Thread(runnable).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            _txtTime.setText(Utils.getTime(ld));
        }
    };

    private static final String TIME_SERVER = "time-a.nist.gov";

    /**
     * 方法二：时间准确 2015/11/12 9:24
     */
    private void  _getCurrentNetworkTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TimeInfo timeInfo = null;
                try {
                    NTPUDPClient timeClient = new NTPUDPClient();
                    InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                    timeInfo = timeClient.getTime(inetAddress);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (timeInfo != null) // 服务器时间 2015/11/12 9:15
                    ld = timeInfo.getMessage().getTransmitTimeStamp().getTime();

                handler.sendEmptyMessage(0);
            }
        };

        new Thread(runnable).start();
    }

}
