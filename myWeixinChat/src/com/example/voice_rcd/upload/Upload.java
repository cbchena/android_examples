package com.example.voice_rcd.upload;

import android.os.Handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Upload {

    public static boolean isUploading = false;
    private static List<String> _lstPath = new ArrayList<String>(); // 文件路径
    private static List<Handler> _lstBeginHandler = new ArrayList<Handler>(); // 开始处理器
    private static List<Handler> _lstEndHandler = new ArrayList<Handler>(); // 结束处理器
    private static List<Handler> _lstErrorHandler = new ArrayList<Handler>(); // 错误处理器
    private static List<Map<String, String>> _lstParams = new ArrayList<Map<String, String>>(); // 参数
    private static Handler _AllEndHandler; // 全部结束处理器

    /**
     * 设置AllEnd处理器 2015/4/14 19:09
     */
    public static void set_AllEndHandler(Handler allEnd){
        _AllEndHandler = allEnd;
    }

    /**
     * 继续上传 2015/2/27 17:49
     */
    public static void continueUpload() {
        isUploading = false;
        _upload();
    }

    /**
     * 上传 2015/2/4 17:55
     * @param path
     * @param begin
     * @param end
     */
    public static void upload(String path, Handler begin, Handler end,
                              Handler error, Map<String, String> params) {

        _lstPath.add(path);
        _lstBeginHandler.add(begin);
        _lstEndHandler.add(end);
        _lstErrorHandler.add(error);
        _lstParams.add(params);
        _upload();
    }

    /**
     * 上传 2015/2/4 18:02
     */
    private static void _upload() {
        if (_lstPath.size() == 0 && _AllEndHandler != null)
            _AllEndHandler.sendEmptyMessage(0);

        if (!isUploading && _lstPath.size() > 0) {
            isUploading = true;
            List<String> _lstFilePath = new ArrayList<String>();
            _lstFilePath.add(_lstPath.remove(0));

            HttpMultipartPost post = new HttpMultipartPost(_lstFilePath,
                    _lstBeginHandler.remove(0), _lstEndHandler.remove(0),
                    _lstErrorHandler.remove(0), _lstParams.remove(0));
            post.execute();
        }

    }

}
