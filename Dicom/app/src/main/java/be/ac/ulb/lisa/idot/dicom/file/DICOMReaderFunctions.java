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
 * This file <DICOMReaderFunctions.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.dicom.file;

import java.io.EOFException;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;

/**
 * Interface for DICOM Reader.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public interface DICOMReaderFunctions {
	
	/**
	 * Add the DICOM element to an object (e.g. DICOMBody)
	 * or to the parent.
	 * 
	 * @param parent Parent if it is a sequence. 
	 * @param element Element to add.
	 */
	void addDICOMElement(DICOMElement parent, DICOMElement element);
	
	/**
	 * Check if the DICOM element is required for DICOMTag integer value
	 * tag.
	 * 
	 * @param tag Integer value of the DICOMTag to check.
	 * @return
	 */
	boolean isRequiredElement(int tag);
	
	
	/**
	 * Compute the image.
	 * 
	 * @param parent Parent if it is a sequence.
	 * @param VR DICOM value representation of the value.
	 * @param valueLength Length of the value.
	 */
	void computeImage(DICOMElement parent, DICOMValueRepresentation VR, long valueLength)
		throws IOException, EOFException, DICOMException;

}
