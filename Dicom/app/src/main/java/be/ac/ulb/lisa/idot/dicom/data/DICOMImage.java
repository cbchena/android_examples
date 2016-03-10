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
 * This file <DICOMImage.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.dicom.data;

import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

/**
 * DICOM image containing a compression status, a meta information
 * object (DICOMMetaInformation), a DICOM body object (DICOMBody)
 * and a LISA 16-Bit grayscale image (LISAImageGray16Bit).
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMImage extends DICOMFile {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Unknown image status.
	 */
	public static final short UNKNOWN_STATUS = 0;
	
	/**
	 * Uncompressed image status.
	 */
	public static final short UNCOMPRESSED = 1;
	
	/**
	 * Compressed image status. 
	 */
	public static final short COMPRESSED = 2;
	
	
	// ---------------------------------------------------------------
	// - <final> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * LISA 16-Bit grayscale image.
	 */
	private final LISAImageGray16Bit mImage;
	
	/**
	 * The compression status.
	 */
	private final short mCompressionStatus;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMImage(DICOMMetaInformation metaInformation, DICOMBody body,
			LISAImageGray16Bit image, short compressionStatus) {
		
		super(metaInformation, body);
		
		mImage = image;
		mCompressionStatus = compressionStatus;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * @return DICOM image.
	 */
	public LISAImageGray16Bit getImage() {
		return mImage;
	}
	
	/**
	 * @return Compression status.
	 */
	public short getCompressionStatus() {
		return mCompressionStatus;
	}
	
	/**
	 * @return Check if the image is uncompressed.
	 */
	public boolean isUncompressed() {
		return mCompressionStatus == UNCOMPRESSED;
	}
	
	/**
	 * @return Check if the image as data.
	 */
	public boolean hasImageData() {
		
		if (mImage == null)
			return false;
		
		return mImage.getData() != null;
	}
	
}
