package com.example.bitmaptest;

import java.util.List;

public class BitmapUtils {

    public static final int DECODE_BITMAP = 0;
    public static final int DECODE_CONFIG = 1;
    
    
    static {
        try {
            System.loadLibrary("dicom");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class BitmapPro {
        List<String> paths;
        int width;
        int height;
    }
    
    public int getImagePath(String comPath, String dir) {
        return getImagePath(comPath, dir, DECODE_CONFIG);
    }
    
    /**
     * 解析dicom
     * @param comPath dcm文件
     * @param dir   目标文件前缀
     * @param decode    0：解析生成图片和信息文件， 1：只解析信息文件
     * 图片文件生成路径 == dir + i + ".bmp", 信息文件 == dir + "instance.conf"
     * @return 0 成功， 其他失败
     */
    public native int getImagePath(String comPath, String dir, int decode);
    
    /**
     * 调窗
     * @param comPath data文件路径
     * @param dir 调窗目标文件(临时文件)
     * @param windowWidth 目标窗宽
     * @param windowCenter 目标窗位
     * @return
     */
    public native int changeWindowSize(String comPath, String dir, double windowWidth, double windowCenter);
    
    public native long getDirPath(String dir);
}
