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
 * This file <LISAImageGray16Bit.java> is part of Droid Dicom Viewer.
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

package com.example.myDICOM.image;

/**
 * LISA 16-Bit grayscale image.
 * 
 * @author Pierre Malarme
 * @version 1.O
 *
 */
public class LISAImageGray16Bit {
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Image width that correspond to
	 * the number of column.
	 */
	protected short mWidth = 0;
	
	/**
	 * Image height that correspond to
	 * the number of row.
	 */
	protected short mHeight = 0;
	
	/**
	 * Image data.
	 */
	protected int[] mData = null;
	
	/**
	 * Maximum value of the data.
	 */
	protected int mDataMax = 0;
	
	/**
	 * The histogram data.
	 */
	protected int[] mHistogramData = null;
	
	/**
	 * Maximum value of the Histogram.
	 */
	protected int mHistogramMax = 0;
	
	/**
	 * The total number of gray level.
	 */
	protected int mGrayLevel = 4096;
	
	/**
	 * Window width. 
	 */
	protected int mWindowWidth = -1;
	
	/**
	 * Window center 
	 */
	protected int mWindowCenter = -1;
	
	/**
	 * Image orientation.
	 */
	protected float[] mImageOrientation = new float[6];
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public LISAImageGray16Bit() {
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return Image width.
	 */
	public short getWidth() {
		return mWidth;
	}

	/**
	 * @return Image height.
	 */
	public short getHeight() {
		return mHeight;
	}

	/**
	 * @return Image data.
	 */
	public int[] getData() {
		return mData;
	}
	
	/**
	 * @return Image data length.
	 */
	public int getDataLength() {
		return mData == null ? 0 : mData.length;
	}

	/**
	 * @return Maximum data value.
	 */
	public int getDataMax() {
		return mDataMax;
	}
	

	/**
	 * @return Histogram data.
	 */
	public int[] getHistogramData() {
		return mHistogramData;
	}
	
	/**
	 * @return Histogram length.
	 */
	public int getHistogramLength() {
		return mHistogramData == null ? 0
				: mHistogramData.length;
	}

	/**
	 * @return Histogram max value.
	 */
	public int getHistogramMax() {
		return mHistogramMax;
	}

	/**
	 * @return The number of gray level.
	 */
	public int getGrayLevel() {
		return mGrayLevel;
	}

	/**
	 * @return Window width.
	 */
	public int getWindowWidth() {
		return mWindowWidth;
	}

	/**
	 * @return Window Center
	 */
	public int getWindowCenter() {
		return mWindowCenter;
	}
	
	/**
	 * @return Window offset.
	 */
	public int getWindowOffset() {
		return ((2 * mWindowCenter - mWindowWidth)) / 2;
	}
	
	/**
	 * @return Image orientation.
	 */
	public float[] getImageOrientation() {
		return mImageOrientation;	
	}

	/**
	 * @param width The width to set.
	 */
	public void setWidth(short width) {
		mWidth = width;
	}

	/**
	 * @param height The height to set.
	 */
	public void setHeight(short height) {
		mHeight = height;
	}

	/**
	 * @param data The data to set.
	 */
	public void setData(int[] data) {
		mData = data;
	}

	/**
	 * @param dataMax The dataMax to set.
	 */
	public void setDataMax(int dataMax) {
		mDataMax = dataMax;
	}
	
	/**
	 * @param histogramData The histogram data to set.
	 */
	public void setHistogramData(int[] histogramData) {
		mHistogramData = histogramData;
	}

	/**
	 * @param histogramMax The histogram max value to set.
	 */
	public void setHistogramMax(int histogramMax) {
		mHistogramMax = histogramMax;
	}

	/**
	 * @param grayLevel The maximum number of gray level to set.
	 */
	public void setGrayLevel(int grayLevel) {
		mGrayLevel = grayLevel;
	}

	/**
	 * @param windowWidth The window width to set.
	 */
	public void setWindowWidth(int windowWidth) {
		mWindowWidth = windowWidth;
	}

	/**
	 * @param windowCenter The window center to set.
	 */
	public void setWindowCenter(int windowCenter) {
		mWindowCenter = windowCenter;
	}
	
	/**
	 * @param imageOrientation The image orientation array.
	 */
	public void setImageOrientation(float[] imageOrientation) {
		
		if (imageOrientation == null)
			return;
		
		if (imageOrientation.length == 6)
			mImageOrientation = imageOrientation;
	}

}
