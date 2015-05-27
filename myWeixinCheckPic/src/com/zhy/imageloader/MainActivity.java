package com.zhy.imageloader;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.*;
import android.widget.PopupWindow.OnDismissListener;
import com.zhy.bean.ImageFloder;
import com.zhy.imageloader.ListImageDirPopupWindow.OnImageDirSelected;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class MainActivity extends Activity implements OnImageDirSelected
{
	private ProgressDialog mProgressDialog;

	/**
	 * 存储文件夹中的图片数量
	 */
	private int mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File mImgDir;

    private File maxImgDir;

	/**
	 * 所有的图片
	 */
	private List<String> mImgs;

	private GridView mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String> mDirPaths = new HashSet<String>();

	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView mChooseDir;
	private TextView mImageCount;

    private Button _btnCarema; // 照相机

    private Button _btnSend; // 发送

	int totalCount = 0;

	private int mScreenHeight;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			mProgressDialog.dismiss();

            // 初始化展示文件夹的popupWindw
            initListDirPopupWindw();

            // 为View绑定数据
            data2View();
        }
	};

	/**
	 * 为View绑定数据
	 */
	private void data2View()
	{
		if (maxImgDir == null || mImageFloders.size() == 0)
		{
			Toast.makeText(getApplicationContext(), "暂时没有图片",
					Toast.LENGTH_SHORT).show();
			return;
		}

        mListImageDirPopupWindow.getmImageDirSelected().selected(mImageFloders.get(0));

//        mImgs = Arrays.asList(maxImgDir.list());
//
//        Collections.reverse(mImgs); // 倒序
//
//		/**
//		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
//		 */
//		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
//				R.layout.grid_item, maxImgDir.getAbsolutePath());
//		mGirdView.setAdapter(mAdapter);
//		mImageCount.setText(totalCount + "张");
	};

    /**
     * 排序图片文件夹，根据数量进行倒序 2015/2/3 15:03
     */
    class ComparatorImageFloders implements Comparator {

        public int compare(Object arg0, Object arg1) {
            ImageFloder imgF1 = (ImageFloder)arg0;
            ImageFloder imgF2 = (ImageFloder)arg1;

            //首先比较年龄，如果年龄相同，则比较名字
            if (imgF1.getCount() > imgF2.getCount()) return -1; // 倒序，所以返回-1
            else if (imgF1.getCount() < imgF2.getCount()) return 1;
            else return 0;
        }

    }

    /**
     * 倒序排序 2015/2/3 15:03
     */
    private void _sortImageFloders() {
        Collections.sort(mImageFloders, new ComparatorImageFloders());
    }

	/**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw()
	{
        _sortImageFloders(); // 排序
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener()
		{

			@Override
			public void onDismiss()
			{
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.check_pic_main);

		DisplayMetrics outMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
		mScreenHeight = outMetrics.heightPixels;

		initView();
		getImages();
		initEvent();

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages()
	{
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		// 显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");

		new Thread(new Runnable()
		{
			@Override
			public void run()
			{

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MainActivity.this
						.getContentResolver();

				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" },
						MediaStore.Images.Media.DATE_MODIFIED);

				Log.e("TAG", mCursor.getCount() + "");
				while (mCursor.moveToNext())
				{
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));

//					Log.e("TAG", path);
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;

					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;

					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder;

					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					}

                    mDirPaths.add(dirPath);

                    // 初始化imageFloder
                    imageFloder = new ImageFloder();
                    imageFloder.setDir(dirPath);
                    imageFloder.setFirstImagePath(path);
					int picSize = parentFile.list(new FilenameFilter()
					{
						@Override
						public boolean accept(File dir, String filename)
						{
							if (filename.endsWith(".jpg")
									|| filename.endsWith(".png")
									|| filename.endsWith(".jpeg"))
								return true;
							return false;
						}
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize)
					{
						mPicsSize = picSize;
                        maxImgDir = parentFile;
					}
				}

				mCursor.close();

				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;

				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);

			}
		}).start();

	}

	/**
	 * 初始化View
	 */
	private void initView()
	{
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);

		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);

        _btnCarema = (Button) findViewById(R.id.btnCarema);
        _btnCarema.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("调用照相机");
            }
        });

        _btnSend = (Button) findViewById(R.id.btnSend);
        _btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("发送照片");
            }
        });
	}

	private void initEvent()
	{
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@Override
	public void selected(ImageFloder floder)
	{
        mImgDir = new File(floder.getDir());
        List<File> lstFile = getFileSort(floder.getDir());
        if (mImgs != null) {
            mImgs.clear();
        }
        else {
            mImgs = new ArrayList<String>();
        }

        if (lstFile != null && lstFile.size() > 0) {
            for (File file: lstFile) {
                String filename = file.getName();
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
						|| filename.endsWith(".jpeg"))
                    mImgs.add(filename);
            }
        }

//		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter()
//		{
//			@Override
//			public boolean accept(File dir, String filename)
//			{
//                if (filename.endsWith(".jpg") || filename.endsWith(".png")
//						|| filename.endsWith(".jpeg"))
//					return true;
//				return false;
//			}
//		}));
//
//        Collections.reverse(mImgs); // 倒序

		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.grid_item, mImgDir.getAbsolutePath(), _checkImgHandler);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();

	}

    /**
     * 处理图片选择 2015/2/3 18:40
     */
    private Handler _checkImgHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mAdapter != null && mAdapter.mSelectedImage != null) {
                int chkNumber = mAdapter.mSelectedImage.size();
                if (chkNumber > 0) {
                    _btnSend.setText("发送(" + chkNumber + "/" + mAdapter.count + ")");
                    _btnSend.setEnabled(true);
                } else {
                    _btnSend.setText("发送");
                    _btnSend.setEnabled(false);
                }
            }
        }
    };

    /**
     * 获取目录下所有文件(按时间排序，最新在前面) 2015/2/3 15:50
     * @param path 读取文件路径
     * @return List<File>
     */
    public List<File> getFileSort(String path) {

        List<File> list = getFiles(path, new ArrayList<File>());
        if (list != null && list.size() > 0) {

            Collections.sort(list, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if (file.lastModified() < newFile.lastModified()) {
                        return 1;
                    } else if (file.lastModified() == newFile.lastModified()) {
                        return 0;
                    } else {
                        return -1;
                    }

                }
            });

        }

        return list;
    }

    /**
     *
     * 获取目录下所有文件 2015/2/3 15:50
     * @param realpath 读取文件路径
     * @param files 存放列表
     * @return List<File>
     */
    public List<File> getFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            if (subfiles != null) {
                for (File file : subfiles) {
                    if (file.isDirectory()) {
                        getFiles(file.getAbsolutePath(), files);
                    } else {
                        files.add(file);
                    }
                }
            }
        }

        return files;
    }

}
