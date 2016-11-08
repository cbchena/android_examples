package com.example.cbchen.mydicom1;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.mec.library.utils.DicomUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String sdPath = Environment.getExternalStorageDirectory().getPath();
        String path = sdPath + "/dicom/0.dcm";

//        DicomUtils.DicomInfo dicomInfo = DicomUtils.getImageProperties(path);
        DicomUtils.DicomInfo dicomInfo = DicomUtils.getImage(path, true, null, null);
        if (dicomInfo != null) {
            System.out.println("===================  getWindowCenter  " + dicomInfo.getWindowCenter());
            System.out.println("===================  getWindowWidth  " + dicomInfo.getWindowWidth());

            System.out.println("============================   imageWidth  " + dicomInfo.getWidth());
            System.out.println("============================   imageHeight  " + dicomInfo.getHeight());

            System.out.println("============================   getScale  " + dicomInfo.getScale());
            System.out.println("============================   isCanChangeWindow  " + dicomInfo.isCanChangeWindow());

            ImageView img = (ImageView) this.findViewById(R.id.img);
            img.setImageBitmap(dicomInfo.getmBitmap());

//            DicomUtils.DicomInfo data = DicomUtils.getImageProperties(path);
//
//            for (String key:data.getData().keySet()) {
//                System.out.println("============================   key  " + key);
//                System.out.println("============================   values  " + data.getData().get(key));
//            }


        }
    }
}
