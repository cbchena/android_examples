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
 * This file <DICOMItem.java> is part of Droid Dicom Viewer.
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
 * DICOM item.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMItem extends DICOMElement {
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * The map of DICOMElement children.
	 */
	protected Map<Integer, DICOMElement> mChildrenMap;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMItem() {
		super(DICOMTag.createDICOMTag(0xfffee000), null);
		
		mChildrenMap = new HashMap<Integer, DICOMElement>();
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Add a DICOMElement child to the map.
	 * 
	 * @param tag
	 * @param element
	 */
	public void addChild(int tag, DICOMElement element) {
		mChildrenMap.put(tag, element);
	}
	
	/**
	 * Get a DICOMElement from the map.
	 * 
	 * @param tag The tag integer value of the child.
	 * @return DICOMElement or null if it does
	 * not exist.
	 */
	public DICOMElement getChild(int tag) {
		return mChildrenMap.get(tag);
	}
	
	/**
	 * @return DICOMElement children map.
	 */
	public Map<Integer, DICOMElement> getChildrenMap() {
		return mChildrenMap;
	}
	
	/**
	 * @return Number of DICOMElement children.
	 */
	public int getChildrenCount() {
		return mChildrenMap.size();
	}
	
	/**
	 * @param tag
	 * @return True if there is a child with is DICOMTag
	 * integer value set as tag. False otherwise.
	 */
	public boolean containsChild(int tag) {
		return mChildrenMap.containsKey(tag);
	}

}
