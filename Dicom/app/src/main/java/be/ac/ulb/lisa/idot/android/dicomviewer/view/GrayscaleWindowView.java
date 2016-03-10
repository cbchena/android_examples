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
 * This file <GrayScaleWindowView.java> is part of Droid Dicom Viewer.
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
 * Version: 1.0
 *
 */

package be.ac.ulb.lisa.idot.android.dicomviewer.view;

import be.ac.ulb.lisa.idot.android.dicomviewer.R;
import be.ac.ulb.lisa.idot.android.dicomviewer.data.DICOMViewerData;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.CLUTMode;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class GrayscaleWindowView extends ImageView {
	
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * The LISA 16-Bit grayscale image.
	 */
	private LISAImageGray16Bit mImage = null;
	
	/**
	 * DICOMViewer data.
	 */
	private DICOMViewerData mDICOMViewerData = null;
	
	/**
	 * The window drawable int.
	 */
	private int mWindowDrawable = R.drawable.gradient_bar;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------

	public GrayscaleWindowView(Context context) {
		super(context);
	}
	
	public GrayscaleWindowView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public GrayscaleWindowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	
	// ---------------------------------------------------------------
	// # <override> FUNCTIONS
	// ---------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {
		
		if (mImage == null || mDICOMViewerData == null)
			return;
		
		int imageDataMax = mImage.getDataMax() + 1;
		imageDataMax = (imageDataMax) == 1 ? mImage.getGrayLevel() : imageDataMax;
		
		int windowWidth = mDICOMViewerData.getWindowWidth();
		int windowCenter = mDICOMViewerData.getWindowCenter();
		
		// TO CONFIGURE
		int stepLength = 20;
		int graduationSizeBig = 10;
		int graduationSizeNormal = 5;
		int fontSize = 10;
		
		// ---------------------------------------
		// VARIABLE DECLARATION
		// ---------------------------------------
		
		// ImageView size
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		// ImageView half size
		int halfWidth = width / 2;
		int halfHeight = height / 2;
		
		// The half height of the scroll bar
		int scaleBarHalfHeight = height / 4;
		
		// TO have step of the length of stepLength
		scaleBarHalfHeight -= scaleBarHalfHeight % stepLength;
		
		// The coordinate of the scale bar
		int topScaleBar = halfHeight - scaleBarHalfHeight;
		int middleScaleBar = halfHeight; // TODO not necessary
		int bottomScaleBar = halfHeight + scaleBarHalfHeight;
		
		// The size of the graduations
		int endGraduationBig = halfWidth + graduationSizeBig;
		int endGraduationNormal = halfWidth + graduationSizeNormal;
		
		// The number of graduation
		int countGraduation = scaleBarHalfHeight / stepLength;
		
		// The fontsize offset to center the text
		int fontSizeOffset = fontSize / 2 - 1;
		
		// The half size of the grays scale window
		int windowHalfWidth = windowWidth * (2 * scaleBarHalfHeight) / imageDataMax/*mGrayLevel*/ /2;
		
		// The window center in ImageView
		int windowCenterCoordinate = bottomScaleBar
			- (2 * scaleBarHalfHeight) * windowCenter / imageDataMax/*mGrayLevel*/;
		
		// The grayscale window bounds
		int topWindowBounds = windowCenterCoordinate - windowHalfWidth;
		int bottomWindowBounds = windowCenterCoordinate + windowHalfWidth;
		
		// Paint
		Paint paint = new Paint(); 
		// TODO we can do different paint object but we do not need it
		paint.setColor(0xaaff0000);
		paint.setAntiAlias(true);
		
		// ---------------------------------------
		// HISTOGRAM
		// ---------------------------------------
		
		int[] imageHistogram = mImage.getHistogramData();
		
		int max = mImage.getHistogramMax();
		max = max > 0 ? max : 1;
		
		if (imageHistogram != null) {
			
			int imgHistLength = imageDataMax < imageHistogram.length ? imageDataMax : imageHistogram.length;
			
			int step = imgHistLength / scaleBarHalfHeight / 2;
			
			int upperBounds = (2 * scaleBarHalfHeight) - 1;
			
			for (int i = 0; i <= upperBounds; i++) {
					
				int count = 0;
				
				int upperStep = (i == upperBounds) ? (imgHistLength - i * step) : step;
				
				for (int j=0; j < upperStep; j++) {
					
					int index = (i * step) + j;
					
					if (index < imgHistLength)
						count += imageHistogram[index];
					
				}
				
				long histBarWidth = (long) halfWidth + (long) graduationSizeBig * 50 * (long) count / (long) max;
				
				canvas.drawLine(halfWidth, bottomScaleBar - i, histBarWidth, bottomScaleBar - i, paint);
				
			}
			
		}
		
		paint.setColor(0xff77c1fb);
		
		// ---------------------------------------
		// GRAYSCALE WINDOW
		// ---------------------------------------
			
		// Set the paint to a stroke paint type
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		
		// Get the grayscale bitmap
		Bitmap grayscaleBitmap =
			BitmapFactory.decodeResource(this.getResources(), mWindowDrawable);
		
		// Compute the destination rect for the bitmap
		Rect destRect = new Rect( halfWidth - 15, topWindowBounds,
				halfWidth - 5, bottomWindowBounds);
		
		// Draw the bitmap and a rect around
		canvas.drawBitmap(grayscaleBitmap, null, destRect, null);
		canvas.drawRect(destRect, paint);
		
		// Set the paint to a fill paint with
		// a text align right and its size set to fontSize
		paint.setStrokeWidth(0);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Align.RIGHT);
		paint.setTextSize(fontSize);
		
		// Draw the upper value
		canvas.drawText(String.valueOf(windowCenter + windowWidth / 2 - 1024),
				halfWidth - 5, topWindowBounds - 5, paint);
		
		// Draw the bottom value
		canvas.drawText(String.valueOf(windowCenter - windowWidth / 2 - 1024),
				halfWidth - 5, bottomWindowBounds + 5 + fontSize, paint);
		
		// Draw the center arrow
		canvas.drawText(">", halfWidth - 15 - 5,
				windowCenterCoordinate + fontSizeOffset, paint);
		
		// Change text align to left
		paint.setTextAlign(Align.LEFT);
		
		// Change the center value
		canvas.drawText(String.valueOf(windowCenter - 1024), endGraduationBig + 5,
				windowCenterCoordinate + fontSizeOffset, paint);
		
		// ---------------------------------------
		// GRAYSCALE SCALE
		// ---------------------------------------
		
		// Draw the vertical line
		canvas.drawLine(halfWidth, topScaleBar, halfWidth, 
				bottomScaleBar, paint);

		// Draw topScaleBar graduation and text
		canvas.drawLine(halfWidth, topScaleBar, endGraduationBig,
				topScaleBar, paint);
		canvas.drawText(String.valueOf(/*mGrayLevel*/imageDataMax - 1 - 1024), endGraduationBig + 5,
				topScaleBar + fontSizeOffset, paint);

		// Draw intermediate graduations
		for (int i = 1; i < countGraduation; i++) {
			// The middle graduation'll be drawn => < and not <= countGraduation
			int yCoordinate = topScaleBar + i * stepLength;
			canvas.drawLine(halfWidth, yCoordinate, endGraduationNormal,
					yCoordinate, paint);	
		}

		// Draw middle graduation
		canvas.drawLine(halfWidth, middleScaleBar, endGraduationBig,
				middleScaleBar, paint);
		
		// Draw intermediate graduations
		for (int i = 1; i < countGraduation; i++) {
			// The bottom graduation'll be drawn => < and not <= countGraduation
			int yCoordinate = middleScaleBar + i * stepLength;
			canvas.drawLine(halfWidth, yCoordinate, endGraduationNormal,
					yCoordinate, paint);
		}
		
		// Draw bottomScaleBar graduation and text
		canvas.drawLine(halfWidth, bottomScaleBar, endGraduationBig,
				bottomScaleBar, paint);
		canvas.drawText("-1024", endGraduationBig + 5,
				bottomScaleBar + fontSizeOffset, paint);
		
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
	 * Set the DICOMViewer data.
	 * 
	 * @param data
	 */
	public void setDICOMViewerData(DICOMViewerData data) {
		mDICOMViewerData = data;
	}
	
	/**
	 * Set the CLUT Mode.
	 * 
	 * This must be set each time, the CLUT mode is changed.
	 * 
	 * @param clutMode
	 */
	public void updateCLUTMode() {
		
		if (mDICOMViewerData == null)
			return;
		
		switch(mDICOMViewerData.getCLUTMode()) {
		
		default:
		case CLUTMode.NORMAL:
			mWindowDrawable = R.drawable.gradient_bar;
			break;
			
		case CLUTMode.INVERSE:
			mWindowDrawable = R.drawable.gradient_inverse_bar;
			break;
			
		case CLUTMode.RAINBOW:
			mWindowDrawable = R.drawable.gradient_color_bar;
			break;
		
		};
		
	}

}
