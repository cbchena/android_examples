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
 * This file <ExternalStorage.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.android.commons;

import android.os.Environment;

/**
 * The class Geometry contains external storage functions.
 * 
 * @author Pierre Malarme
 * @version 1.O
 *
 */
public class ExternalStorage {
	
	// ---------------------------------------------------------------
	// + <static> FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return True if the external storage is available.
	 * False otherwise.
	 */
	public static boolean checkAvailable() {
		
		// Retrieving the external storage state
		String state = Environment.getExternalStorageState();
		
		// Check if available
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return True if the external storage is writable.
	 * False otherwise.
	 */
	public static boolean checkWritable() {
		
		// Retrieving the external storage state
		String state = Environment.getExternalStorageState();
		
		// Check if writable
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		
		return false;
		
	}

}
