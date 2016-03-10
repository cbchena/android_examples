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
 * This file <ThreadState.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.android.dicomviewer.thread;

/**
 * Encapsulate thread states that correspond to the wath of
 * the Handler.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class ThreadState {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Thread has catch a OutOfMemoryError exception.
	 */
	public static final short OUT_OF_MEMORY = 0;
	
	/**
	 * The thread is started.
	 */
	public static final short STARTED = 1;
	
	/**
	 * The thread is finished.
	 */
	public static final short FINISHED = 2;
	
	/**
	 * The thread progression update.
	 */
	public static final short PROGRESSION_UPDATE = 3;
	
	/**
	 * An error occurred while the thread running that cannot
	 * be managed.
	 */
	public static final short UNCATCHABLE_ERROR_OCCURRED = 4;
	
	/**
	 * An error occurred while the thread running that can be
	 * managed or ignored.
	 */
	public static final short CATCHABLE_ERROR_OCCURRED = 5;

}
