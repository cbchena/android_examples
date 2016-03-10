package be.ac.ulb.lisa.idot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import java.io.File;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.DICOMImageView;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.file.DICOMImageReader;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitReader;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitWriter;

public class MyActivity extends Activity {

    DICOMImageView imageView;
    Bitmap renderBitmap;

    private DICOMViewerData mDICOMViewerData = null;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        String path = Environment.getExternalStorageDirectory() + "/CR-MONO1-10-chest";
        String path = Environment.getExternalStorageDirectory() + "/aa.dcm";
        System.out.println("==========   " + path);
//        LoadImage(path);

        imageView = (DICOMImageView) findViewById(R.id.imageView);

        mDICOMViewerData = new DICOMViewerData();

        // DIMENSION 控制大小   GRAYSCALE  控制灰度
        mDICOMViewerData.setToolMode(ToolMode.MEASURE);

        imageView.setDICOMViewerData(mDICOMViewerData);

        try {

            // 读取文件 2016/3/9 11:31
            File currentFile = new File(path);
            if (currentFile.exists()) {

                File lisaFile = new File(currentFile + ".lisa");
                if (!lisaFile.exists()) {

                    // 写入文件 2016/3/10 12:48
                    DICOMImageReader dicomFileReader = new DICOMImageReader(currentFile);
                    DICOMImage dicomImage = dicomFileReader.parse();
                    dicomFileReader.close();
                    if (dicomImage.isUncompressed()) {
                        LISAImageGray16BitWriter out =
                                new LISAImageGray16BitWriter(currentFile + ".lisa");

                        out.write(dicomImage.getImage(), path);
                        out.flush();
                        out.close();
                    }
                }

                LISAImageGray16BitReader reader =
                        new LISAImageGray16BitReader(currentFile + ".lisa");

                LISAImageGray16Bit image = reader.parseImage(path);
                reader.close();

                // 读取数据 2016/3/10 13:40
//                Map<String, String> attributes = image.getAttributes();
//                if (attributes != null) {
//                    for(String name:attributes.keySet()) {
//                        System.out.println("========== name  " + name);
//                        System.out.println("========== value  " + attributes.get(name));
//                    }
//                } else {
//                    System.out.println("================================");
//                }

                // 设置相关数据
                imageView.setImage(image);
                mDICOMViewerData.setWindowWidth(image.getWindowWidth());
                mDICOMViewerData.setWindowCenter(image.getWindowCenter());

                // 这里先延迟绘画
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        imageView.draw();
                        imageView.fitIn();
                    }
                }, 100);
            }
        }catch(Exception ex){
            // Do nothing and create a LISA image
            System.out.println(ex);
        }

    }

}
