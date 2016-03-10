package be.ac.ulb.lisa.idot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.view.DICOMImageView;
import be.ac.ulb.lisa.idot.commons.Geometry;

/**
 * 绘制直线
 * @author cbchen.
 * @time 2016/3/10 15:13.
 */
public class LineView extends View {
    private float upX;
    private float upY;
    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private Paint paint = null;
    private DICOMImageView _dicomImageView;
    private DICOMViewerData _dicomViewerData;

    //构造方法用于初始化Paint对象
    public LineView(Context context,
                    DICOMImageView dicomImageView,
                    DICOMViewerData dicomViewerData) {
        super(context);
        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        _dicomImageView = dicomImageView;
        _dicomViewerData = dicomViewerData;
        this.setAlpha(0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(downX, downY, moveX, moveY, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();
                invalidate();
                break;
        }

        moveX = event.getX();
        moveY = event.getY();

        // 测量距离 2016/3/10 15:26
        if (_dicomViewerData.getToolMode() == ToolMode.MEASURE) {
                float pixelSpacing = Float.parseFloat(_dicomImageView.getImage()
                        .getAttributes().get("PixelSpacing"));
                float distance = Geometry.euclidianDistance(downX, downY,
                        moveX, moveY);
                int result = (int) (pixelSpacing * (distance / _dicomImageView.getScaleFactor()) + 1);
                System.out.println("====== pixelSpacing distance result  ScaleFactor  "
                        + pixelSpacing
                        + "   " + distance
                        + "   " + result
                        + "   " + _dicomImageView.getScaleFactor());
        }

        return true;
    }
}
