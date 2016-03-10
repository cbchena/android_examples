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
 * This file <DICOMValueRepresentation.java> is part of Droid Dicom Viewer.
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

import java.util.HashMap;
import java.util.Map;

/**
 * DICOM tag.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
/**
 * @author pmalarme
 *
 */
/**
 * @author pmalarme
 *
 */
public class DICOMValueRepresentation {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLE
	// ---------------------------------------------------------------
	
	// The class raw types of the value representation.
	
	/**
	 * Byte array set as String. 
	 */
	@SuppressWarnings("rawtypes")
	public static final Class BYTE = String.class;
	
	/**
	 * Double.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class DOUBLE = Double.class;
	
	/**
	 * Float.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class FLOAT = Float.class;
	
	/**
	 * Integer.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class INT = Integer.class;
	
	/**
	 * Long.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class LONG = Long.class;
	
	/**
	 * Object: just for sequence.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class OBJECT = Object.class;
	
	/**
	 * String.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class STRING = String.class;
	
	/**
	 * Short.
	 */
	@SuppressWarnings("rawtypes")
	public static final Class SHORT = Short.class;

	
	/**
	 * Map of value representation.
	 */
	public static final Map<String, DICOMValueRepresentation> c = new HashMap<String, DICOMValueRepresentation>() {
		
		/**
		 * Eclipse generated serial version UID.
		 */
		private static final long serialVersionUID = 4021611561632736549L;

	{
		put("AE", new DICOMValueRepresentation("AE", "Application Entity", STRING, 16, false));
		put("AS", new DICOMValueRepresentation("AS", "Age String", STRING, 4, true));
		put("AT", new DICOMValueRepresentation("AS", "Attribute Tag", INT, 4, true));
		put("CS", new DICOMValueRepresentation("CS", "Code String", STRING, 16, true));
		put("DA", new DICOMValueRepresentation("DA", "Date", STRING, 18, false));
		// TODO DICOM 3.5-2009 page 25 : In the context of a Query with range matching
		// the length is 18 bytes maximum => in that case create a new ValueRepresentation ?
		// => not set to 18 and maximum for future instead of 8 and fixed.
		put("DS", new DICOMValueRepresentation("DS", "Decimal String", STRING, 16, false));
		put("DT", new DICOMValueRepresentation("DT", "Date Time", STRING, 54, false));
		// TODO DICOM 3.5-2009 page 26 : In the context of a Query with range matching
		// the length is 54 bytes maximum => in that case create a new ValueRepresentation ?
		// => not set to 54 and maximum for future instead of 26 and maximum.
		put("FL", new DICOMValueRepresentation("FL", "Floating Point Single", FLOAT, 4, true));
		put("FD", new DICOMValueRepresentation("FD", "Floating Point Double", DOUBLE, 8, true));
		put("IS", new DICOMValueRepresentation("IS", "Integer String", STRING, 12, false));
		put("LO", new DICOMValueRepresentation("LO", "Long String", STRING, 64, false));
		put("LT", new DICOMValueRepresentation("LT", "Long Text", STRING, 1024, false));
		put("OB", new DICOMValueRepresentation("OB", "Other Byte String", BYTE));
		put("OF", new DICOMValueRepresentation("OF", "Other Float String", STRING));
		// TODO Set the max at 2^32-4 cf. pg 28
		put("OW", new DICOMValueRepresentation("OW", "Other Word String", STRING));
		put("PN", new DICOMValueRepresentation("PN", "Person Name", STRING));
		// TODO ADD A PN OBJECT TYPE because 64-chars per component group (pg. 28)
	    put("SH", new DICOMValueRepresentation("SH", "Short String", STRING, 16, false));
	    put("SL", new DICOMValueRepresentation("SL", "Signed Long", INT, 4 , true));
	    put("SQ", new DICOMValueRepresentation("SQ", "Sequence of Items", OBJECT));
	    put("SS", new DICOMValueRepresentation("SS", "Signed Short", SHORT, 2, true));
	    put("ST", new DICOMValueRepresentation("ST", "Short Text", STRING, 1024, false));
	    put("TM", new DICOMValueRepresentation("TM", "Time", STRING, 28, false));
	    // TODO DICOM 3.5-2009 page 31 : In the context of a Query with range matching
		// the length is 28 bytes maximum => in that case create a new ValueRepresentation ?
		// => not set to 28 and maximum for future instead of 16 and maximum.
	    put("UI", new DICOMValueRepresentation("UI", "Unique Identifier", STRING, 64, false));
	    put("UL", new DICOMValueRepresentation("UL", "Unsigned Long", LONG, 4, false));
	    put("UN", new DICOMValueRepresentation("UN", "Unknown", STRING));
	    // TODO Any length valid for any of the DICOM Value representation cf. pg 32
	    put("US", new DICOMValueRepresentation("US", "Unsigned Short", INT, 2, false));
	    // Page 32 US: vale n: 0 <= n < 2^16
	    put("UT", new DICOMValueRepresentation("UT", "Unlimited Text", STRING));
	    // TODO pg. 32: maximum length 2^32-2
	    
	}};
	
	
	// ---------------------------------------------------------------
	// - VARIABLE
	// ---------------------------------------------------------------
	
	/**
	 * Value representation code on 2 bytes (character).
	 */
	private final String mVR;
	
	/**
	 * Value representation description.
	 */
	private final String mName;
	
	/**
	 * Raw type of the value representation. 
	 */
	@SuppressWarnings("rawtypes")
	private final Class mReturnType;
	
	/**
	 * The maximum byte count.
	 * -1 = no maximum
	 */
	private final int mMaxByteCount;
	
	/**
	 * If the number of byte is fixed or not. If it is the case,
	 * the mMaxByteCount is the fixed number of bytes.
	 */
	private final boolean mIsFixedByteCount;
	
	
	// ---------------------------------------------------------------
	// - CONSTRUCTORS
	// ---------------------------------------------------------------
	
	/**
	 * The constructor is private because only known value representation
	 * are accepted. If the value is unknown, it is set to UN.
	 * 
	 * @param VR
	 * @param name
	 * @param returnType
	 */
	@SuppressWarnings("rawtypes")
	private DICOMValueRepresentation(String VR, String name, Class returnType) {
		
		this(VR, name, returnType, -1, false);
	}
	
	/**
	 * The constructor is private because only known value representation
	 * are accepted. If the value is unknown, it is set to UN.
	 * 
	 * @param VR
	 * @param name
	 * @param returnType
	 * @param maxByteCount
	 * @param isFixedByteCount
	 */
	@SuppressWarnings("rawtypes")
	private DICOMValueRepresentation(String VR, String name, Class returnType,
			int maxByteCount, boolean isFixedByteCount) {
		
		mVR = VR;
		mName = name;
		mReturnType = returnType;
		mMaxByteCount = maxByteCount;
		mIsFixedByteCount = isFixedByteCount;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Get if this object is a value representation identical to VR.
	 * @param VR
	 * @return
	 */
	public boolean equals(String VR) {
		
		if (VR == null)
			return false;
		else if (mVR == VR)
			return true;
		else
			return false;
		
	}

	/**
	 * @return Value representation code on 2 bytes (character).
	 */
	public String getVR() {
		return mVR;
	}

	/**
	 * @return Value representation description.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return Raw type.
	 */
	@SuppressWarnings("rawtypes")
	public Class getReturnType() {
		return mReturnType;
	}

	/**
	 * @return Maximum byte.
	 */
	public int getMaxByteCount() {
		return mMaxByteCount;
	}

	/**
	 * @return If the number of byte is fixed or not.
	 */
	public boolean isFixedByteCount() {
		return mIsFixedByteCount;
	}

}
