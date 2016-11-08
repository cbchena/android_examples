package com.mec.library.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 解析dicom工具类
 * 需要权限 android.permission.WRITE_EXTERNAL_STORAGE 
 * @author pasino
 */
public class DicomUtils {

    
    static {
        try {
            System.loadLibrary("dicom");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class DicomInfo {
        
        public DicomInfo() {
        	init0d();
		}
        private void init0d()
        {
        	add(null, null);
        	setCode(0);
        	setDatas(null);
        	setWindowCenter(0);
        	setWindowWidth(0);
        	setCanChangeWindow(false);
        	setHasImage(false);
        	setPath(null);
        }

		private void add(String key,String v)
        {
        	if(key != null)
        	{
        		data.put(key, v);
        	}
        }
        
        public Map<String, String> getData() {
			return data;
		}
		public int getCode() {
			return code;
		}

		 private void setCode(int code)
		{
			this.code = code;
		}
		
		 private void setDatas(byte[] datas) {
        	if(datas == null || datas.length < 1)
        	{
        		mBitmap = null;
        	}
        	else
        	{
        		try
        		{
        			mBitmap = BitmapFactory.decodeByteArray(datas, 0, datas.length);
        		}catch(Throwable e)
        		{
        			e.printStackTrace();
        		}
        	}
		}

		public int getWindowWidth() {
			return windowWidth;
		}

	    private void setWindowWidth(int windowWidth) {
			this.windowWidth = windowWidth;
		}

		public int getWindowCenter() {
			return windowCenter;
		}

		 private void setWindowCenter(int windowCenter) {
			this.windowCenter = windowCenter;
		}

		public boolean isCanChangeWindow() {
			return canChangeWindow;
		}

		 private  void setCanChangeWindow(boolean canChangeWindow) {
			this.canChangeWindow = canChangeWindow;
		}

		public Bitmap getmBitmap() {
			return mBitmap;
		}
		
		public boolean isHasImage() {
			return hasImage;
		}
		private void setHasImage(boolean hasImage) {
			this.hasImage = hasImage;
		}

		/** 解析成功图像属性 与传参有关
		 *  当properties 为true是此数据有数据
		 *  属性有dicom 标准格式 key像0028,1052等
		 *  此外有其他属性
		 *  
		 *  */
		private final Map<String,String> data = new HashMap<String, String>();
		
		/** 解析成功时是否有图片 */
		private boolean hasImage;
		
		/** 0 正常解析 1 文件不能解析或不是dicom影像 -1 解析时异常 */
		private int code ;
        /** 解析成功的bitmap图像 */
        private Bitmap mBitmap;
        /** 窗宽 */
        private int windowWidth;
        /** 窗位  */
        private int windowCenter;
        /** 是否能调窗 */
        private boolean canChangeWindow;
        
        /** dicom路径 即输入路径地址 */
        private String path;

		public String getPath() {
			return path;
		}
		private void setPath(String path) {
			this.path = path;
		}
		/**
		 * 得到图像宽度
		 * @return
		 */
		public int getWidth() {
			try
			{
				return Integer.valueOf(data.get("columns"));
			}catch(Throwable e)
			{
				
			}
			return 0;
		}
		/**
		 * 得到图像高度
		 * @return
		 */
		public int getHeight() {
			try
			{
				return Integer.valueOf(data.get("rows"));
			}catch(Throwable e)
			{
				
			}
			return 0;
		}

		public int getMin() {
			try
			{
				return Integer.valueOf(data.get("max"));
			}catch(Throwable e)
			{

			}
			return 0;
		}

		public int getMax() {
			try
			{
				return Integer.valueOf(data.get("min"));
			}catch(Throwable e)
			{

			}
			return 0;
		}

		/**
		 * 得到图像比例尺 为空不能测量
		 * @return
		 */
		public String getScale()
		{
			String scale =  null;
			
			try
			{
				scale = data.get("0028,0030").trim();
			}catch(Throwable e)
			{
				
			}
			if(scale == null || scale.length() < 1)
			{
				try
				{
					scale = data.get("0018,1164").trim();
				}catch(Throwable e)
				{
					
				}
			}
			return scale;
		}
    }

    /**
     * 解析影像
     * @param comPath 影像图片地址
     * @param properties 是否解析影像图片属性
     * @param viewimg 是否显示图片
     * @param windowWidth 窗宽  当窗宽或窗位有一项为null 则用默认窗宽窗位显示图片
     * @param windowCenter 窗位 
     * @return 解析图片结果
     */
    private synchronized static native DicomInfo getImageIntro(String comPath,boolean properties,boolean viewimg,Integer windowWidth,Integer windowCenter);
    
    
    /**
     * 解析影像 忽略读取影像属性
     * @param comPath 影像图片地址
     * @param viewimg 是否显示图片
     * @param windowWidth 窗宽  当窗宽或窗位有一项为null 则用默认窗宽窗位显示图片，当不是默认窗宽显示时，窗宽不能为0
     * @param windowCenter 窗位 
     * @return 解析图片结果
     */
    public static  DicomInfo getImage(String comPath,boolean viewimg,Integer windowWidth,Integer windowCenter)
    {
    	try
    	{
    		return getImageIntro(comPath,true, viewimg, windowWidth, windowCenter);
    	}catch(Throwable e)
    	{
    		e.printStackTrace();
    	}
    	return null;
    }
    
    /**
     * 获取图像属性
     * @param comPath 影像图片地址
     * @return
     */
    public static  DicomInfo getImageProperties(String comPath)
    {
    	try
    	{
    		return getImageIntro(comPath,true, false, null, null);
    	}catch(Throwable e)
    	{
    		
    	}
    	return null;
    }
    
}
