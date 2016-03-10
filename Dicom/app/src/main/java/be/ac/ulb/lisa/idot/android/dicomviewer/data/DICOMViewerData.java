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
 * This file <DICOMViewerData.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.android.dicomviewer.data;

import be.ac.ulb.lisa.idot.android.dicomviewer.mode.CLUTMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ScaleMode;
import be.ac.ulb.lisa.idot.android.dicomviewer.mode.ToolMode;

/**
 * Class containing the data specific
 * to the DICOM Viewer.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMViewerData {
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * The tool mode.
	 */
	private short mToolMode = ToolMode.DIMENSION;
	
	/**
	 * CLUT mode.
	 */
	private short mCLUTMode = CLUTMode.NORMAL;
	
	/**
	 * The scale mode: fit in or real size.
	 */
	private short mScaleMode = ScaleMode.FITIN;
	
	/**
	 * Grayscale window width. 
	 */
	private int mWindowWidth = -1;
	
	/**
	 * Grayscale window center 
	 */
	private int mWindowCenter = -1;

	/**
	 * @return the mToolMode
	 */
	public short getToolMode() {
		return mToolMode;
	}

	/**
	 * @return the mCLUTMode
	 */
	public short getCLUTMode() {
		return mCLUTMode;
	}

	/**
	 * @return the mScaleMode
	 */
	public short getScaleMode() {
		return mScaleMode;
	}

	/**
	 * @return the mWindowWidth
	 */
	public int getWindowWidth() {
		return mWindowWidth;
	}

	/**
	 * @return the mWindowCenter
	 */
	public int getWindowCenter() {
		return mWindowCenter;
	}

	/**
	 * @param mToolMode the mToolMode to set
	 */
	public void setToolMode(short mToolMode) {
		this.mToolMode = mToolMode;
	}

	/**
	 * @param mCLUTMode the mCLUTMode to set
	 */
	public void setCLUTMode(short mCLUTMode) {
		this.mCLUTMode = mCLUTMode;
	}

	/**
	 * @param mScaleMode the mScaleMode to set
	 */
	public void setScaleMode(short mScaleMode) {
		this.mScaleMode = mScaleMode;
	}

	/**
	 * @param mWindowWidth the mWindowWidth to set
	 */
	public void setWindowWidth(int mWindowWidth) {
		
		// The minimum window width is 1
		// cf. DICOM documentation
		if (mWindowWidth <= 0)
			this.mWindowWidth = 1;
		else
			this.mWindowWidth = mWindowWidth;
		
	}

	/**
	 * @param mWindowCenter the mWindowCenter to set
	 */
	public void setWindowCenter(int mWindowCenter) {
		this.mWindowCenter = mWindowCenter;
	}
	
	

}
