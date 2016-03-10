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
 * This file <ToolMode.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.android.dicomviewer.mode;

/**
 * The class ToolMode contains the tool modes.
 * 
 * @author Pierre Malarme
 * @version 1.0
 * 
 */
public final class ToolMode {
	
	/**
	 * Change the size and the position of the image.
	 */
	public static final short DIMENSION = 0;
	
	/**
	 * Change the grayscale window center and position.
	 */
	public static final short GRAYSCALE = 1;
	
	/**
	 * Rotate the image. 
	 */
	public static final short ROTATION = 2;
	
	/**
	 * Crop the image.
	 */
	public static final short CROP = 3;
	
	/**
	 *	Make measurement on the image. 
	 */
	public static final short MEASURE = 4;

}
