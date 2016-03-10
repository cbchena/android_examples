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
 * This file <DICOMSequence.java> is part of Droid Dicom Viewer.
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

import java.util.ArrayList;
import java.util.List;


/**
 * DICOM sequence.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class DICOMSequence extends DICOMElement {
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------

	/**
	 * List of DICOMElement children (normally DICOMItem).
	 */
	protected List<DICOMElement> mChildrenList;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTOR
	// ---------------------------------------------------------------
	
	public DICOMSequence(DICOMTag dicomTag) {
		super(dicomTag, null);
		
		mChildrenList = new ArrayList<DICOMElement>();
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Add a DICOMElement child to the sequence (List).
	 * @param element
	 */
	public void addChild(DICOMElement element) {
		mChildrenList.add(element);
	}
	
	/**
	 * Get a DICOMElement child from the List correspond to the index.
	 * @param index Index of the child.
	 * @return DICOMElement child.
	 * @throws IndexOutOfBoundsException
	 */
	public DICOMElement getChild(int index) throws IndexOutOfBoundsException {
		return mChildrenList.get(index);
	}
	
	/**
	 * @return DICOMElement children List.
	 */
	public List<DICOMElement> getChildrenList() {
		return mChildrenList;
	}
	
	/**
	 * @return Number of children in the List.
	 */
	public int getChildrenCount() {
		return mChildrenList.size();
	}

}
