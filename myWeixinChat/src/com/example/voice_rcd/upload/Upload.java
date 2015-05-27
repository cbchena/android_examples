package com.example.voice_rcd.upload;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

public class Upload {

    private static boolean _isUploading = false;
    private static List<String> _lstPath = new ArrayList<String>();
    private static List<Handler> _lstBeginHandler = new ArrayList<Handler>();
    private static List<Handler> _lstEndHandler = new ArrayList<Handler>();
    private static List<Handler> _lstErrorHandler = new ArrayList<Handler>();

    private static Handler _handler = new Handler() { // 开始时，发送
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            if (bundle == null) return;

            // 发送 2015/2/4 18:03
            if (_lstBeginHandler.size() > 0) {
                Message message = new Message();
                message.setData(bundle);
                Handler handler = _lstBeginHandler.remove(0);
                handler.sendMessage(message);
            }
        }
    };

    private static Handler _endHandler = new Handler() { // 结束时，发送
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            // 发送 2015/2/4 18:03
            if (_lstEndHandler.size() > 0) {
                Handler handler = _lstEndHandler.remove(0);
                handler.sendEmptyMessage(0);
            }

            _isUploading = false;
            _upload();
        }
    };

    private static Handler _errorHandler = new Handler() { // 发送错误
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            _isUploading = false;

            // 发送 2015/2/4 18:03
            if (_lstErrorHandler.size() > 0) {
                Handler handler = _lstErrorHandler.remove(0);
                handler.sendEmptyMessage(0);
            }

            _upload();
        }
    };

    /**
     * 上传 2015/2/4 17:55
     * @param path
     * @param begin
     * @param end
     */
    public static void upload(String path, Handler begin, Handler end, Handler error) {
        _lstPath.add(path);
        _lstBeginHandler.add(begin);
        _lstEndHandler.add(end);
        _lstErrorHandler.add(error);
        _upload();
    }

    /**
     * 上传 2015/2/4 18:02
     */
    private static void _upload() {
        if (!_isUploading && _lstPath.size() > 0) {
            _isUploading = true;
            List<String> _lstFilePath = new ArrayList<String>();
            _lstFilePath.add(_lstPath.remove(0));

            HttpMultipartPost post = new HttpMultipartPost(_lstFilePath,
                    _handler, _endHandler, _errorHandler);
            post.execute();
        }
    }

}
