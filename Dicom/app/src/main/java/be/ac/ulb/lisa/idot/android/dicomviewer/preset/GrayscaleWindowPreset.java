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
 * This file <GrayscaleWindowPreset.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.android.dicomviewer.preset;

/**
 * The class WindowPreset contains the image window presets.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public final class GrayscaleWindowPreset {
	
	/**
	 * Set the window window to the CT Bone preset. 
	 */
	public static final short CT_BONE = 1;
	
	/**
	 * Set the window window to the CT Crane preset.
	 */
	public static final short CT_CRANE = 2;
	
	/**
	 * Set the window window to the CT Lung preset.
	 */
	public static final short CT_LUNG = 3;
	
	/**
	 * Set the window window to the CT Abdomen preset.
	 */
	public static final short CT_ABDOMEN = 4;

}
