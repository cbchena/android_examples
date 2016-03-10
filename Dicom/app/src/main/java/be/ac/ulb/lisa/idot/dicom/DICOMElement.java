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
 * This file <DICOMElement.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.dicom;

/**
 * DICOM element.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMElement {
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * DICOM element DICOM tag.
	 */
	protected DICOMTag mDICOMTag;
	
	/**
	 * DICOM element value. 
	 */
	protected Object mValue;
	
	/**
	 * DICOM element length in byte.
	 */
	protected long mLength;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	/**
	 * Construct a DICOM item with a undefined length.
	 * @param dicomTag
	 * @param value
	 */
	public DICOMElement(DICOMTag dicomTag, Object value) {
		this(dicomTag, 0xffffffffL, value);
	}
	
	public DICOMElement(DICOMTag dicomTag, long length, Object value) {
		mDICOMTag = dicomTag;
		mLength = length;
		mValue = value;
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------

	/**
	 * @return the mDICOMTag
	 */
	public DICOMTag getDICOMTag() {
		return mDICOMTag;
	}

	/**
	 * @return the mValue
	 */
	public Object getValue() {
		return mValue;
	}
	
	/**
	 * @return DICOM element length in bytes.
	 */
	public long getLength() {
		return mLength;
	}
	
	/**
	 * @return DICOM element value as a String or null
	 * if there is an error.
	 */
	public String getValueString() {
		
		// If there is no value return null
		if (mValue == null)
			return "NULL";
		
		// If no tag return a null object
		if (mDICOMTag == null)
			return null;
		// TODO throw a DICOM Exception
		
		// Get the value representation
		DICOMValueRepresentation VR = mDICOMTag.getValueRepresentation();
		
		if (mDICOMTag.getTag() == 0x7fe00010) {
			if (VR.equals("OW"))
				return "Pixel DICOM OW";
			else if (VR.equals("OB"))
				return "Pixel DICOM OB";
		}
		
		if (VR.equals("US") || VR.equals("SS")) {
			if (mLength > 2)
				return "Array of numerical values coded in 2 bits";
		} else if (VR.equals("UL") || VR.equals("FL") || VR.equals("SL")) {
			if (mLength > 4)
				return "Array of numerical values coded in 4 bits";
		} else if (VR.equals("FD")) {
			if (mLength > 8)
				return "Array of numerical values coded in 8 bits";
		}
		
		// Get the value class from the VR
		@SuppressWarnings("rawtypes")
		Class valueClass = VR.getReturnType();
		
		// If type match return the string representing the value
		if (valueClass.equals(mValue.getClass())) {
			String toReturn = "" + valueClass.cast(mValue);
			return toReturn;
		} else {
			return null;
		}
		
	}
	
	/**
	 * @param dicomTag DICOMTag to set
	 */
	public void setDICOMTag(DICOMTag dicomTag) {
		mDICOMTag = dicomTag;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		mValue = value;
	}
	
	/**
	 * @param length the length to set.
	 */
	public void setLength(int length) {
		mLength = length;
	}
	
	/**
	 * @return DICOM element value as an integer.
	 * @throws NumberFormatException If the value is not an
	 * integer, it throws a NumberFormatException.
	 */
	public int getValueInt() throws NumberFormatException {
		
		int toReturn = 0;
		
		if (mValue instanceof String) {
			
			toReturn = Integer.parseInt((String) mValue);
			
		} else if (mValue instanceof Short) {
			
			toReturn = (int) (Short) mValue;
			
		} else if (mValue instanceof Integer) {
			
			toReturn = (Integer) mValue;
			
		} else if (mValue instanceof Long) {
			
			toReturn = ((Long) mValue).intValue();
			
		} else {
			
			toReturn = Integer.parseInt(getValueString());
			
		}
		
		return toReturn;
		
	}

}
