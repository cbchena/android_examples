package com.example.myDICOM;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import com.example.myDICOM.data.DICOMViewerData;
import com.example.myDICOM.image.LISAImageGray16Bit;
import com.example.myDICOM.image.LISAImageGray16BitReader;
import com.example.myDICOM.mode.ToolMode;
import com.example.myDICOM.view.DICOMImageView;
import com.imebra.dicom.*;

import java.io.File;

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

        // LOAD THE LIBRARY
        System.loadLibrary("imebra_lib");

//        String path = Environment.getExternalStorageDirectory() + "/CR-MONO1-10-chest";
        String path = Environment.getExternalStorageDirectory() + "/test.dcm";
        System.out.println("==========   " + path);
//        LoadImage(path);

        imageView = (DICOMImageView) findViewById(R.id.imageView);

        mDICOMViewerData = new DICOMViewerData();

        // DIMENSION 控制大小   GRAYSCALE  控制灰度
        mDICOMViewerData.setToolMode(ToolMode.GRAYSCALE);

        imageView.setDICOMViewerData(mDICOMViewerData);

        try {
            File currentFile = new File(path);
            LISAImageGray16BitReader reader =
                    new LISAImageGray16BitReader(currentFile + ".lisa");

            LISAImageGray16Bit image = reader.parseImage();
            reader.close();

            imageView.setImage(image);
            mDICOMViewerData.setWindowWidth(image.getWindowWidth());
            mDICOMViewerData.setWindowCenter(image.getWindowCenter());

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    imageView.draw();
                    imageView.fitIn();
                }
            }, 100);

        } catch (Exception ex) {
            // Do nothing and create a LISA image
        }

    }

    // Load an image
    public void LoadImage(String fileName) {
        // Open the dicom file from sdcard
        Stream stream = new Stream();
        stream.openFileRead(fileName);
        // Build an internal representation of the Dicom file. Tags larger than 256 bytes
        //  will be loaded on demand from the file
        DataSet dataSet = CodecFactory.load(new StreamReader(stream), 256);
        // Get the first image
        Image image = dataSet.getImage(0);
        // Monochrome images may have a modality transform
        if(ColorTransformsFactory.isMonochrome(image.getColorSpace()))
        {
            ModalityVOILUT modalityVOILUT = new ModalityVOILUT(dataSet);
            if(!modalityVOILUT.isEmpty())
            {
                Image modalityImage = modalityVOILUT.allocateOutputImage(image, image.getSizeX(), image.getSizeY());
                modalityVOILUT.runTransform(image, 0, 0, image.getSizeX(), image.getSizeY(), modalityImage, 0, 0);
                image = modalityImage;
            }
        }
        // Just for fun: get the color space and the patient name
        String colorSpace = image.getColorSpace();
        String patientName = dataSet.getString(0x0010, 0, 0x0010, 0);
        String dataType = dataSet.getDataType(0x0010, 0, 0x0010);
        // Allocate a transforms chain: contains all the transforms to execute before displaying
        //  an image
        TransformsChain transformsChain = new TransformsChain();
        // Monochromatic image may require a presentation transform to display interesting data
        if(ColorTransformsFactory.isMonochrome(image.getColorSpace()))
        {
            VOILUT voilut = new VOILUT(dataSet);
            int voilutId = voilut.getVOILUTId(0);
            if(voilutId != 0)
            {
                voilut.setVOILUT(voilutId);
            }
            else
            {
                // No presentation transform is present: here we calculate the optimal window/width (brightness,
                //  contrast) and we will use that
                voilut.applyOptimalVOI(image, 0, 0, image.getSizeX(), image.getSizeY());
            }
            transformsChain.addTransform(voilut);
        }
        // Let's use a DrawBitmap object to generate a buffer with the pixel data. We will
        // use that buffer to create an Android Bitmap
        com.imebra.dicom.DrawBitmap drawBitmap = new com.imebra.dicom.DrawBitmap(image, transformsChain);
        int temporaryBuffer[] = new int[1]; // Temporary buffer. Just used to get the needed buffer size
        int bufferSize = drawBitmap.getBitmap(image.getSizeX(), image.getSizeY(), 0, 0, image.getSizeX(), image.getSizeY(), temporaryBuffer, 0);
        int buffer[] = new int[bufferSize]; // Ideally you want to reuse this in subsequent calls to getBitmap()
        // Now fill the buffer with the image data and create a bitmap from it
        drawBitmap.getBitmap(image.getSizeX(), image.getSizeY(), 0, 0, image.getSizeX(), image.getSizeY(), buffer, bufferSize);
        renderBitmap = Bitmap.createBitmap(buffer, image.getSizeX(), image.getSizeY(), Bitmap.Config.ARGB_8888);

        // Let's find the ImageView and se the image
        imageView = (DICOMImageView)findViewById(R.id.imageView);
        imageView.setImageBitmap(renderBitmap);


    }

}
