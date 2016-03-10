/*
 *
 * Copyright (C) 2011 Pierre Malarme
 *
 * Authors: Pierre Malarme <pmalarme at ulb.ac.be>
 *
 * Institution: Laboratory of Image Synthesis and Analysis (LISA)
 *              Faculty of Applied Science
 *              Universite Libre de Bruxelles (U.L.B.)
 *
 * Website: http://lisa.ulb.ac.be
 *
 * This file <DICOMImageView.java> is part of Droid Dicom Viewer.
 *
 * Droid Dicom Viewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Droid Dicom Viewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Droid Dicom Viewer. If not, see <http://www.gnu.org/licenses/>.
 *
 * Released date: 17-02-2011
 *
 * Version: 1.1
 *
 */

package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.CLUTMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ScaleMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.TouchMode;
import be.ac.ulb.lisa.idot.commons.Geometry;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

/**
 * Dicom ImageView that extends Android ImageView objects and
 * implements onTouchListener.
 * 
 * The DICOMViewerData must be set at the creation of the activity
 * that contains this View.
 * 
 * @author Pierre Malarme
 * @version 1.1
 *
 */
public class DICOMImageView extends ImageView implements OnTouchListener {
	
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	// IMAGE VARIABLES
	/**
	 * The LISA 16-Bit Grayscale Image. 
	 */
	private LISAImageGray16Bit mImage = null;
	
	/**
	 * The transformation matrix.
	 */
	private Matrix mMatrix;
	
	/**
	 * The scale factor.
	 */
	private float mScaleFactor = 1f;
	
	// TOUCH EVENT VARIABLES
	/**
	 * The touch mode.
	 */
	private short mTouchMode;
	
	/**
	 * The transformation matrix saved when a touch down
	 * dimension event is caught.
	 */
	private Matrix mSavedMatrix;
	
	/**
	 * The time of the first touch down.
	 */
	private long mTouchTime;
	
	/**
	 * The point where the touch start.
	 */
	private PointF mTouchStartPoint;
	
	/**
	 * The mid point of the touch event.
	 */
	private PointF mTouchMidPoint;
	
	/**
	 * The distance between the two point
	 * for a TWO_FINGERS touch event.
	 */
	private float mTouchOldDist;
	
	/**
	 * The old scaleFactor.
	 */
	private float mTouchOldScaleFactor;
	
	// INITIALIZATION VARIABLE
	/**
	 * Set if the view is initialized or not.
	 */
	private boolean mIsInit = false;
	
	// DICOMVIEWER DATA
	/**
	 * DICOMViewer data.
	 */
	private DICOMViewerData mDICOMViewerData = null;
	
	// IS IMAGE TO BE ROTATED
	
	private boolean mIsRotate = false;
	
	// CONTEXT
	/**
	 * Context.
	 */
	private Context mContext;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	public DICOMImageView(Context context) {
		super(context);
	
		mContext = context;
		init();
	}
	
	public DICOMImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mContext = context;
		init();
	}
	
	public DICOMImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mContext = context;
		init();
	}
	
	
	// ---------------------------------------------------------------
	// + <override> FUNCTIONS
	// ---------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see android.widget.ImageView#setScaleType(android.widget.ImageView.ScaleType)
	 */
	@Override
	public void setScaleType(ScaleType scaleType) {
		
		// Do nothing because we accept only the matrix scale type
		
	}
	
	
	// ---------------------------------------------------------------
	// # <override> FUNCTIONS
	// ---------------------------------------------------------------
	
	// This function is override to fit the image in the
	// ImageView at initialization. Because when this method
	// is called, the size of the ImageView is set.
	// The override of the onDraw method lead to a slower
	// display of the image. It is for that we override
	// this method.
	/* (non-Javadoc)
	 * @see android.widget.ImageView#drawableStateChanged()
	 */
	@Override
	protected void drawableStateChanged() {
		
		if (mIsInit == false) {
			
			mIsInit = true;
			
			if (mImage != null)
				fitIn();
			
		}

		super.drawableStateChanged();
		
	}
	
	// This function is override to center the image
	// when the size of the screen change
	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		// If the image is not null, center it
		if (mImage != null)
			center();
		
		super.onSizeChanged(w, h, oldw, oldh);
		
	}
	
	private boolean isMeasuring = false; // 是否正在测量
    private PointF _pointFirst;
    private PointF _pointSecond;

    private float upX;
    private float upY;
    private float downX;
    private float downY;
    private float moveX;
    private float moveY;
    private Paint paint = null;
    private int distance = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 当前类型属于测距才会进行绘制 2016/3/10 15:56
        if (mDICOMViewerData.getToolMode() == ToolMode.MEASURE) {
            canvas.drawLine(downX, downY, moveX, moveY, paint);
            canvas.drawText(distance + "mm", downX, downY, paint);
        }
    }

	// ---------------------------------------------------------------
	// + <implement> FUNCTIONS
	// ---------------------------------------------------------------
	
	public boolean onTouch(View v, MotionEvent event) {
		
		if (mImage == null
				|| mDICOMViewerData == null)
			return false;
		
		// Get the tool mode
		short toolMode = mDICOMViewerData.getToolMode();
		
		// Handle touch event
		switch(event.getAction() & MotionEvent.ACTION_MASK) {
		
		case MotionEvent.ACTION_DOWN:

            downX = event.getX();
            downY = event.getY();

			// Double tap
			if ((System.currentTimeMillis() - mTouchTime) < 450) {
				
				// The touch mode is set to none
				mTouchMode = TouchMode.NONE;
				mTouchTime = 0;
				
				// If toolMode is DIMENSION, fit the image
				// in the screen.
				if (toolMode == ToolMode.DIMENSION) { // 放大缩小
					
					if (mDICOMViewerData.getScaleMode() == ScaleMode.FITIN)
						fitIn();
					else
						realSize();
					
				} else if (toolMode == ToolMode.GRAYSCALE) { // 控制灰度/曝光度
					
					mDICOMViewerData.setWindowWidth(mImage.getWindowWidth());
					mDICOMViewerData.setWindowCenter(mImage.getWindowCenter());
					draw();
					
				}
				
				return true;
			
			// Single tap
			} else if (mTouchMode == TouchMode.NONE) {
				
				// Set the touch time
				mTouchTime = System.currentTimeMillis();
				
				// Set the touch mode to ONE_FINGER
				mTouchMode = TouchMode.ONE_FINGER;
				
				// Set the mSavedMatrix
				mSavedMatrix.set(mMatrix);
				
				// Set the mTouchStartPoint value
				mTouchStartPoint.set(event.getX(), event.getY());
				
			} else {
				
				mTouchTime = 0;
				
			}

			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:
			
			// Check if there is two pointers
			if (event.getPointerCount() == 2) {
				
				// Set mTouchMode to TouchMode.TWO_FINGERS
				mTouchMode = TouchMode.TWO_FINGERS;
				
				// Reset mTouchTime
				mTouchTime = 0;
				
				// DIMENSION MODE
				if (toolMode == ToolMode.DIMENSION) {
					
					// Compute the olf dist between the two pointer.
					mTouchOldDist = Geometry.euclidianDistance(event.getX(0), event.getY(0),
							event.getX(1), event.getY(1));
					
					// Compute the old scale factor as the mScaleFactor
					// at the begining of the touch event.
					mTouchOldScaleFactor = mScaleFactor;
					
					// Compute the midPoint
					if ((mImage.getWidth() * mScaleFactor) <= getMeasuredWidth()
							|| (mImage.getHeight() * mScaleFactor) <= getMeasuredHeight()) {
						
						mTouchMidPoint = new PointF(getMeasuredWidth() / 2f,
								getMeasuredHeight() / 2f);
						
					} else {
						
						mTouchMidPoint = Geometry.midPoint(event.getX(0), event.getY(0),
								event.getX(1), event.getY(1));
						
					}
					
				}
				
			} else if (event.getPointerCount() == 3) {
				
				// Set the touch mode to three fingers
				mTouchMode = TouchMode.THREE_FINGERS;
				
				// Reset the mTouchTime
				mTouchTime = 0;
				
				// The start point is the average of the three fingers
				// just for the x coordinate
				mTouchStartPoint.set(
						(event.getX(0) + event.getX(1) + event.getX(2)) / 3f,
						0f);
				
			}
			
			break;
			
		case MotionEvent.ACTION_MOVE:

            moveX = event.getX();
            moveY = event.getY();

			// If this is a ONE_FINGER touch mode
			if (mTouchMode == TouchMode.ONE_FINGER) {
				
				// Switch on toolMode
				switch(toolMode) {
				
				case ToolMode.DIMENSION:
					// Set the matrix
					mMatrix.set(mSavedMatrix);
					
					// Variable declaration
					float dx = 0;
					float dy = 0;
					
					// Compute the translation
					dx = event.getX() - mTouchStartPoint.x;
					dy = event.getY() - mTouchStartPoint.y;
					
					// TODO center the image if width or height > this size
					
					// Set the translation
					mMatrix.postTranslate(dx, dy);
					
					// Set the transformation matrix
					setImageMatrix(mMatrix);
					
					break;
					
				case ToolMode.GRAYSCALE:
					
					// Compute the grayscale window center
					int center = (getMeasuredHeight() - 10 - (int) event.getY())
						* /*grayscaleWindow.getGrayLevel()*/mImage.getDataMax()
						/ (getMeasuredHeight());
					
					// Compute the grayscale window width
					int width = (int) event.getX() * /*grayscaleWindow.getGrayLevel()*/mImage.getDataMax()
						/ (getMeasuredWidth());
					
					// Set the grayscale window attributes
					mDICOMViewerData.setWindowWidth(width);
					mDICOMViewerData.setWindowCenter(center);
					
					// Compute the RGB image
					draw();
					
					break;
				
				};
				
			} else if (mTouchMode == TouchMode.TWO_FINGERS
					&& toolMode == ToolMode.DIMENSION) {
				
				// Compute the distance between the two finger
				float newDist = Geometry.euclidianDistance(event.getX(0), event.getY(0),
						event.getX(1), event.getY(1));
				
				// TODO necessary ?
				//if (newDist > 3f) {
				if (newDist != mTouchOldDist) {
					
					// Set the matrix
					mMatrix.set(mSavedMatrix);
					
					// Scale factor
					float scaleFactor = newDist / mTouchOldDist;
					
					// Compute the global scale factor
					mScaleFactor = mTouchOldScaleFactor * scaleFactor;
					
					// Set the scale center at the mid point of the event
					mMatrix.postScale(scaleFactor, scaleFactor, mTouchMidPoint.x, mTouchMidPoint.y);
					
					// Set the transformation matrix
					setImageMatrix(mMatrix);
					
				}
				
			} else if (mTouchMode == TouchMode.THREE_FINGERS) {
				
				// Get the current event average (3 fingers) x coordinate
				float eventAverageX = event.getX(0) + event.getX(1) + event.getX(2) / 3f;
				
				// If the distance is greater than 40% of the ImageView width, change
				// image.
				if (Geometry.euclidianDistance(eventAverageX, 0, mTouchStartPoint.x, 0)
						>= (0.4f * (float) getMeasuredWidth())) {
					
					// Get the direction of the touch event
					float directionX = eventAverageX - mTouchStartPoint.x;
					
					// Show next or previous image
//					if (directionX < 0)
//						((DICOMViewer) mContext).nextImage(null);
//					else
//						((DICOMViewer) mContext).previousImage(null);
						
					// Set the touchmode to none
					mTouchMode = TouchMode.NONE;
					
				}
			}
			
			break;
			
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:

            upX = event.getX();
            upY = event.getY();

			// Set that this is the end of the touch event
			mTouchMode = TouchMode.NONE;
			
			// Check the dimension
			if (toolMode == ToolMode.DIMENSION) {
				
				// Compute mImageView width and height and the image
				// or the image size if is in ScaleMode real size
				// scaled width and height
				float imageWidth, imageHeight;
					
				// Check the scale mode to see if the image size is
				// smaller than the required size
				if (mDICOMViewerData.getScaleMode() == ScaleMode.FITIN) {
					
					imageWidth = getMeasuredWidth();
					imageHeight = getMeasuredHeight();
					
				} else {
					
					imageWidth = mImage.getWidth();
					imageHeight = mImage.getHeight();
					
				}
				
				float scaledImageWidth = (float) mImage.getWidth() * mScaleFactor;
				float scaledImageHeight = (float) mImage.getHeight() * mScaleFactor;
				
				// If the image fit int the window => fit in the window
				if (scaledImageWidth <= imageWidth
						&& scaledImageHeight <= imageHeight) {
					
					if (mDICOMViewerData.getScaleMode() == ScaleMode.FITIN)
						fitIn();
					else
						realSize();
					
				} else {
					
					// The ImageView size is needed
					imageWidth = getMeasuredWidth();
					imageHeight = getMeasuredHeight();
					
					// Get the matrix for the transformation
					mMatrix.set(getImageMatrix());
					
					// Set the source and destination rect points that correpond
					// to the upper left corner and the bottom right corner
					float[] srcRectPoints = { 0f, 0f, mImage.getWidth(), mImage.getHeight()};
					float[] dstRectPoints = new float[4];
					
					// Apply the image matrix transformation on these points
					mMatrix.mapPoints(dstRectPoints, srcRectPoints);
					
					// Init transalation variables
					float dx = 0f;
					float dy = 0f;
					
					// If the scaled image width is greater than the mImageView width
					if (scaledImageWidth > imageWidth) {
						
						// If there is black at the left of the screen
						if (dstRectPoints[0] > 0f) {
							
							dx = (-1f) * dstRectPoints[0];
						
						// Else if there is black at the right of the screen
						} else if (dstRectPoints[2] < imageWidth) {
							
							dx = imageWidth - dstRectPoints[2];
							
						}
						
					} else {
						
						// Compute the left border and the right border
						// to center the image
						float lx = (imageWidth - scaledImageWidth) / 2f;
						float rx = (imageWidth + scaledImageWidth) / 2f;
						
						// If the image is more left than the left border
						if (dstRectPoints[0] < lx) {
							
							dx = (-1f) * dstRectPoints[0] + lx;
						
						// Else if the image is more right than the right border
						} else if (dstRectPoints[2] > rx) {
							
							dx = rx - dstRectPoints[2];
							
						}
						
					}
					
					// If the scaled image height is greater than the mImageView height
					if (scaledImageHeight > imageHeight) {
						
						// If there is black at the top of the screen
						if (dstRectPoints[1] > 0f) {
							
							dy = (-1f) * dstRectPoints[1];
							
						// Else if there is black at the bottom of the screen
						} else if (dstRectPoints[3] < imageHeight) {
							
							dy = imageHeight - dstRectPoints[3];
							
						}
						
					} else {
						
						// Compute the top border and the bottom border
						// to center the image
						float ty = (imageHeight - scaledImageHeight) / 2f;
						float by = (imageHeight + scaledImageHeight) / 2f;
						
						// If the image is upper the top border
						if (dstRectPoints[1] < 0f) {
							
							dy = (-1f) * dstRectPoints[1] + ty;
							
						// Else if the image is under the bottom border
						} else if (dstRectPoints[3] > imageHeight) {
							
							dy = by - dstRectPoints[3];
							
						}
						
					}
					
					// If there is translation to compute
					if (dx != 0f || dy != 0f) {
						
						// Add the translation
						mMatrix.postTranslate(dx, dy);
						
						// Set the image matrix
						setImageMatrix(mMatrix);
						
					}
					
				}
				
			}
			
			break;
		
		}

        moveX = event.getX();
        moveY = event.getY();

        // 测量距离 2016/3/10 15:26
        if (toolMode == ToolMode.MEASURE) {
            float pixelSpacing = Float.parseFloat(getImage()
                    .getAttributes().get("PixelSpacing"));
            float distancePx = Geometry.euclidianDistance(downX, downY,
                    moveX, moveY);
            distance = (int) (pixelSpacing * (distancePx / getScaleFactor()) + 1);
            invalidate();
        }
		
		return true; // Do not draw
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Draw the image. 
	 */
	public void draw() {
		
		// Declaration output pixels vector
		int[] outputPixels = new int[mImage.getDataLength()];
		
		// Get the gray scale window width
		int windowWidth = mDICOMViewerData.getWindowWidth();
		
		// Compute the window offset x the number of gray levels (256)
		int windowOffset = ((2 * mDICOMViewerData.getWindowCenter() - windowWidth)) / 2;
		
		switch(mDICOMViewerData.getCLUTMode()) {
		
		case CLUTMode.NORMAL:
			computeGrayscaleRGBImage(windowWidth, windowOffset, outputPixels);
			break;
			
		case CLUTMode.INVERSE:
			computeInverseGrayscaleRGBImage(windowWidth, windowOffset, outputPixels);
			break;
			
		case CLUTMode.RAINBOW:
			computeRainbowRGBImage(windowWidth, windowOffset, outputPixels);
			break;
			
		};
		
		// Create the bitmap
		Bitmap imageBitmap = Bitmap.createBitmap(outputPixels, mImage.getWidth(),
				mImage.getHeight(), Bitmap.Config.ARGB_8888);
		
		// Check if image is to be rotated 90 degrees
		if (mIsRotate) {
			Matrix m = new Matrix();
			m.postRotate(90);
			imageBitmap = Bitmap.createBitmap(imageBitmap,
					0,	0, mImage.getWidth(), mImage.getHeight(), 
					m, true);
		}
		
		
		// Set the image
		setImageBitmap(imageBitmap);
		
	}
	
	/**
	 * Should this image be rotated?
	 */
	public void toggleRotate() {
		mIsRotate = !mIsRotate;
		draw();
	}
	
	/**
	 * Reset size and position of the image regarding
	 * mScaleMode variable.
	 */
	public void resetSize() {
		
		switch(mDICOMViewerData.getScaleMode()) {
		
		case ScaleMode.FITIN:
			fitIn();
			break;
		
		case ScaleMode.REALSIZE:
			realSize();
			break;
			
		};
		
	}
	
	/**
	 * Fit the image in the screen
	 */
	public void fitIn() {
		
		// Get the image width and height
		int imageWidth = mImage.getWidth();
		int imageHeight = mImage.getHeight();
		
		// Variable declaration
		float dx = 0f;
		float dy = 0f;

		// Get the image matrix
		mMatrix.set(getImageMatrix());

		// If the width of the ImageView is smaller than the
		// height, the image width is set to the ImageView width.
		if (getMeasuredWidth() <= getMeasuredHeight()) {

			float measuredWidth = getMeasuredWidth();

			mScaleFactor = measuredWidth / imageWidth;

			// Translate to center the image.
			dy = ((float) getMeasuredHeight() - imageHeight
					* mScaleFactor) / 2f;
			
		// Else the image height is set to the ImageView height.
		} else {

			float measuredHeight = getMeasuredHeight();

			mScaleFactor = measuredHeight / imageHeight;

			// Translate to center the image.
			dx = ((float) getMeasuredWidth() - imageWidth * mScaleFactor) / 2f;

		}

		// Set the transformation
		mMatrix.setScale(mScaleFactor, mScaleFactor, 0f, 0f);
		mMatrix.postTranslate(dx, dy);

		// Set the Image Matrix
		setImageMatrix(mMatrix);
		
	}
	
	/**
	 * Display the real size of the image.
	 */
	public void realSize() {
		
		// Get the image width and height
		int imageWidth = mImage.getWidth();
		int imageHeight = mImage.getHeight();
		
		// Compute the translation
		float dx = ((float) getMeasuredWidth() - imageWidth) / 2f;
		
		float dy = ((float) getMeasuredHeight() - imageHeight) / 2f;
		
		mScaleFactor = 1f;

		mMatrix.set(getImageMatrix());

		// Set the transformation
		mMatrix.setScale(mScaleFactor, mScaleFactor, 0f, 0f);
		mMatrix.postTranslate(dx, dy);

		// Set the Image Matrix
		setImageMatrix(mMatrix);
		
	}
	
	/**
	 * Center the image in X and/or Y regarding
	 * the dimension of the scaled image and the dimension
	 * of the ImageView.
	 */
	public void center() {
		
		// Scaled image sizes.
		float scaledImageWidth = (float) mImage.getWidth() * mScaleFactor;
		float scaledImageHeight = (float) mImage.getHeight() * mScaleFactor;
		
		if (scaledImageWidth <= getMeasuredWidth()
				&& scaledImageHeight <= getMeasuredHeight()) {
			
			// Compute the translation
			float dx = ((float) getMeasuredWidth() - scaledImageWidth) / 2f;
			float dy = ((float) getMeasuredHeight() - scaledImageHeight) / 2f;
			
			mMatrix.set(getImageMatrix());
			
			mMatrix.setScale(mScaleFactor, mScaleFactor, 0f, 0f);
			mMatrix.postTranslate(dx, dy);
			
			
			// Set the Image Matrix
			setImageMatrix(mMatrix);
			
		}
		
	}

	/**
	 * Set the LISA 16-Bit grayscale image.
	 * 
	 * @param image
	 */
	public void setImage(LISAImageGray16Bit image) {
		mImage = image;	
	}
	
	/**
	 * Get the LISA 16-Bit grayscale image.
	 */
	public LISAImageGray16Bit getImage() {
		return mImage;
	}
	
	/**
	 * Get the image scaled width.
	 * 
	 * @return Image scaled width.
	 */
	public float getScaledImageWidth() {
		return mImage.getWidth() * mScaleFactor;
	}
	
	/**
	 * Get the image scaled height.
	 * 
	 * @return Image scaled height.
	 */
	public float getScaledImageHeight() {
		return mImage.getHeight() * mScaleFactor;
	}
	
	/**
	 * @return Transformation matrix.
	 */
	public Matrix getMatrix() {
		return mMatrix;
	}
	
	/**
	 * Get the scale factor.
	 * 
	 * @return Scale factor.
	 */
	public float getScaleFactor() {
		return mScaleFactor;
	}
	
	/**
	 * Set the scale factor and apply
	 * it on the image.
	 */
	public void setScaleFactor(float scaleFactor) {
		mScaleFactor = scaleFactor;
	}
	
	/**
	 * Set the DICOMViewer data.
	 * @param data
	 */
	public void setDICOMViewerData(DICOMViewerData data) {
		mDICOMViewerData = data;
	}
	
	
	// ---------------------------------------------------------------
	// - FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Init this object.
	 */
	private void init() {
		
		// Set the transformation attribute
		super.setScaleType(ScaleType.MATRIX);
		mMatrix = new Matrix();
		mScaleFactor = 1f;
		
		// TOUCH
		mTouchMode = TouchMode.NONE;
		mSavedMatrix = new Matrix();
		mTouchTime = 0;
		mTouchStartPoint = new PointF();
		mTouchMidPoint = new PointF();
		mTouchOldDist = 1f;
		
		// Set the onTouchListener as this object
		setOnTouchListener(this);

        paint = new Paint();
        paint.setStrokeWidth(4);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(40);
	}
	
	/**
	 * Compute the RGB image using a grayscale LUT.
	 * 
	 * @param windowWidth
	 * @param windowOffset
	 * @param outputPixels
	 */
	private void computeGrayscaleRGBImage(int windowWidth, int windowOffset,
			int[] outputPixels) {
		
		// The gray level of the current pixel
		int pixelGrayLevel = 0;
		
		int[] mImageData = mImage.getData();
		
		// Compute the outputPixels vector (matrix)
		for (int i = 0; i < mImageData.length; i++) {		
				pixelGrayLevel = (256 * (mImageData[i] - windowOffset)
					/ windowWidth);
				
				pixelGrayLevel = (pixelGrayLevel > 255) ? 255 :
					((pixelGrayLevel < 0) ? 0 : pixelGrayLevel);
				
				outputPixels[i] = (0xFF << 24) | // alpha
					(pixelGrayLevel << 16) | // red
					(pixelGrayLevel << 8) | // green
					pixelGrayLevel; // blue
		}
		
	}
	
	/**
	 * Compute the RGB image using an inverse grayscale LUT.
	 * 
	 * @param windowWidth
	 * @param windowOffset
	 * @param outputPixels
	 */
	private void computeInverseGrayscaleRGBImage(int windowWidth, int windowOffset,
			int[] outputPixels) {
		
		// The gray level of the current pixel
		int pixelGrayLevel = 0;
		
		int[] mImageData = mImage.getData();
		
		// Compute the outputPixels vector (matrix)
		for (int i = 0; i < mImageData.length; i++) {		
				pixelGrayLevel = 255 - (256 * (mImageData[i] - windowOffset)
					/ windowWidth);
				
				pixelGrayLevel = (pixelGrayLevel > 255) ? 255 :
					((pixelGrayLevel < 0) ? 0 : pixelGrayLevel);
				
				outputPixels[i] = (0xFF << 24) | // alpha
					(pixelGrayLevel << 16) | // red
					(pixelGrayLevel << 8) | // green
					pixelGrayLevel; // blue
		}
		
	}
	
	/**
	 * Compute the RGB image using a rainbow CLUT.
	 * 
	 * @param windowWidth
	 * @param outputPixels
	 */
	private void computeRainbowRGBImage(int windowWidth, int windowMin, int[] outputPixels) {	
		
		float[] pixelHSV = new float[3];
		
		pixelHSV[0] = 0f;
		pixelHSV[1] = 240f;
		pixelHSV[2] = 1f;
		
		float mult = 0;
		
		int[] mImageData = mImage.getData();
		
		// Compute the outputPixels vector (matrix)
		for (int i = 0; i < mImageData.length; i++) {
			
			mult = (mImageData[i] - windowMin) / (float) windowWidth;
			
			mult = (mult > 1f) ? 1f :
				((mult < 0f) ? 0f : mult);
			
			pixelHSV[0] = 300f - 360f * mult;
			pixelHSV[0] = (pixelHSV[0] > 300f) ? 300f :
				(pixelHSV[0] < -60f) ? 360-60f : pixelHSV[0];
				
			
			pixelHSV[2] = 4f * mult;
			
			outputPixels[i] = Color.HSVToColor(0xFF, pixelHSV);
			
		}
		
	}

}
