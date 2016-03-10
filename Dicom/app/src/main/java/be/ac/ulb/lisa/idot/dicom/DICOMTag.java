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
 * This file <DICOMTag.java> is part of Droid Dicom Viewer.
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
public class DICOMTag {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLE
	// ---------------------------------------------------------------
	
	/**
	 * Map of defined tag.
	 */
	public static final Map<Integer, DICOMTag> c = new HashMap<Integer, DICOMTag>() {
		
		/**
		 * Eclipse generated Serial version. 
		 */
		private static final long serialVersionUID = -8652499398694995133L;
		// To generate each time this hashmap is modified.

	{
		
	    put(0x00020000, new DICOMTag(0x00020000,
	    		"File Meta Information Group Length",
	    		DICOMValueRepresentation.c.get("UL")));
	    
	    put(0x00020001, new DICOMTag(0x00020001,
	    		"File Meta Information Group Length",
	    		DICOMValueRepresentation.c.get("OB")));
	    
	    put(0x00020002, new DICOMTag(0x00020002,
	    		"Media Storage SOP Class UID",
	    		DICOMValueRepresentation.c.get("UI")));
	    
	    put(0x00020003, new DICOMTag(0x00020003,
	    		"Media Storage SOP Instance UID",
	    		DICOMValueRepresentation.c.get("UI")));
	    
	    put(0x00020010, new DICOMTag(0x00020010,
	    		"TransferSyntax UID",
	    		DICOMValueRepresentation.c.get("UI")));
	    
	    put(0x00020012, new DICOMTag(0x00020012,
	    		"Implementation Class UID",
	    		DICOMValueRepresentation.c.get("UI")));
	    
	    put(0x00020013, new DICOMTag(0x00020013,
	    		"Implementation Version Name",
	    		DICOMValueRepresentation.c.get("SH")));
	    
	    put(0x00020016, new DICOMTag(0x00020016,
	    		"Source Application Entity",
	    		DICOMValueRepresentation.c.get("AE")));
	    
	    put(0x00020100, new DICOMTag(0x00020100,
	    		"Private Information creator UID",
	    		DICOMValueRepresentation.c.get("UI")));
	    
	    put(0x00020102, new DICOMTag(0x00020102,
	    		"Private Information creator UID",
	    		DICOMValueRepresentation.c.get("OB")));
	    
	    put(0x00280002, new DICOMTag(0x00280002,
	    		"Samples per pixel",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280010, new DICOMTag(0x00280010,
	    		"Rows",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280011, new DICOMTag(0x00280011,
	    		"Columns",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280100, new DICOMTag(0x00280100,
	    		"Bits allocated",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280101, new DICOMTag(0x00280101,
	    		"Bits stored",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280102, new DICOMTag(0x00280102,
	    		"High Bit",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x00280103, new DICOMTag(0x00280103,
	    		"Pixel Representation",
	    		DICOMValueRepresentation.c.get("US")));
	    
	    put(0x7fe00010, new DICOMTag(0x7fe00010,
	    		"Pixel Data",
	    		DICOMValueRepresentation.c.get("UN")));
	    
	    put(0xfffee000, new DICOMTag(0xfffee000,
	    		"Item",
	    		DICOMValueRepresentation.c.get("UN")));
	    
	    put(0xfffee00d, new DICOMTag(0xfffee00d,
	    		"Item Delimitation Tag",
	    		DICOMValueRepresentation.c.get("UN")));
	    
	    put(0xfffee0dd, new DICOMTag(0xfffee0dd,
	    		"Sequence Delimitation Tag",
	    		DICOMValueRepresentation.c.get("UN")));
	}};
	
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Tag integer value. 
	 */
	private final int mTag;
	
	/**
	 * Tag description.
	 */
	private final String mName;
	
	/**
	 * Tag value representation.
	 */
	private final DICOMValueRepresentation mVR;
	
	
	// ---------------------------------------------------------------
	// + <static> FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Create a DICOM tag using a tag integer value.
	 * 
	 * @param tag Tag integer value
	 * @return
	 */
	public static final DICOMTag createDICOMTag(int tag) {
		
		// If the tag is known by Droid Dicom Viewer
		if (c.containsKey(tag)) {
			
			return c.get(tag);
			
		} else {
			
			int tagGroup = (tag >> 16) & 0xff;
			
			// If the tagGroup is an odd Number, the tag is
			// Private
			String name = (tagGroup % 2 == 0) ? "Unknown" : "Private";
			
			DICOMValueRepresentation VR = DICOMValueRepresentation.c.get("UN");
			
			return new DICOMTag(tag, name, VR);
			
		}
		
	}
	
	/**
	 * Create a DICOM tag using a tag integer value
	 * and a value representation.
	 * 
	 * @param tag Tag integer value
	 * @param VR Value representation.
	 * @return
	 */
	public static final DICOMTag createDICOMTag(int tag, DICOMValueRepresentation VR) {
		
		String name;
		
		// If the tag is known by Droid Dicom Viewer
		if (c.containsKey(tag)) {
			
			// If the VR is the same as a tag in memory, return this tag
			if (VR.getVR().equals(c.get(tag).getValueRepresentation().getVR()))
				return c.get(tag);
			
			name = c.get(tag).getName();
			
		} else {
			
			int tagGroup = (tag >> 16) & 0xff;
			
			// If the tagGroup is an odd Number, the tag is
			// Private
			name = (tagGroup % 2 == 0) ? "Unknown" : "Private";
			
		}
		
		return new DICOMTag(tag, name, VR);
		
	}
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMTag(int tag, String name, DICOMValueRepresentation VR) {
		mTag = tag;
		mName = name;
		mVR = VR;
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------

	/**
	 * @return the mTag
	 */
	public int getTag() {
		return mTag;
	}
	
	/**
	 * @return Tag UID as a String (group + element). 
	 */
	public String toString() {
		return getGroup() + getElement();
	}
	
	/**
	 * @return Tag group as a String.
	 */
	public String getGroup() {
		String toReturn = Integer.toHexString((mTag >> 16) & 0xffff);
		
		while (toReturn.length() < 4)
			toReturn = "0" + toReturn;
		
		return toReturn;
	}
	
	/**
	 * @return Tag element as a String.
	 */
	public String getElement() {
		String toReturn = Integer.toHexString((mTag) & 0xffff);
		
		while (toReturn.length() < 4)
			toReturn = "0" + toReturn;
		
		return toReturn;
	}

	/**
	 * @return Tag description.
	 */
	public String getName() {
		return mName;
	}

	/**
	 * @return Value representation.
	 */
	public DICOMValueRepresentation getValueRepresentation() {
		return mVR;
	}

}
