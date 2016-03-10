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
 * This file <Memory.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.commons;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Implements functions useful to check
 * Memory usage.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class Memory {
	
	// ---------------------------------------------------------------
	// + <static> FUNCTION
	// ---------------------------------------------------------------
	
	/**
	 * Function that get the size of an object.
	 * 
	 * @param object
	 * @return Size in bytes of the object or -1 if the object
	 * is null.
	 * @throws IOException
	 */
	public static final int sizeOf(Object object) throws IOException {
		
		if (object == null)
			return -1;
		
		// Special output stream use to write the content
		// of an output stream to an internal byte array.
		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();
		
		// Output stream that can write object
		ObjectOutputStream objectOutputStream =
			new ObjectOutputStream(byteArrayOutputStream);
		
		// Write object and close the output stream
		objectOutputStream.writeObject(object);
		objectOutputStream.flush();
		objectOutputStream.close();
		
		// Get the byte array
		byte[] byteArray = byteArrayOutputStream.toByteArray();
		
		// TODO can the toByteArray() method return a
		// null array ?
		return byteArray == null ? 0 : byteArray.length;
		
		
	}

}
