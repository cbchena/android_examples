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
 * This file <DICOMImageCacher.java> is part of Droid Dicom Viewer.
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

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;

import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.file.DICOMFileFilter;
import be.ac.ulb.lisa.idot.dicom.file.DICOMImageReader;
import be.ac.ulb.lisa.idot.image.file.LISAImageGray16BitWriter;

/**
 * DICOM image cacher that cached DICOM image in LISA
 * 16-Bit grayscale image.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public final class DICOMImageCacher extends Thread {
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * The handler to send message to.
	 */
	private final Handler mHandler;
	
	/**
	 * The directory containing the DICOM image files. 
	 */
	private final File mTopDirectory;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	public DICOMImageCacher(Handler handler, String topDirectoryName)
			throws FileNotFoundException {
		
		// Set the handler
		if (handler == null)
			throw new NullPointerException("The handler is null");
		
		mHandler = handler;
		
		// Set the top directory
		mTopDirectory = new File(topDirectoryName);
		
		if (!mTopDirectory.exists())
			throw new FileNotFoundException("The directory ("
					+ topDirectoryName + ") doesn't exist.");
		
	}
	
	public DICOMImageCacher(Handler handler, File topDirectory)
			throws FileNotFoundException {
		
		// Set the handler
		if (handler == null)
			throw new NullPointerException("The handler is null");
		
		mHandler = handler;
		
		// Set the top directory
		if (!topDirectory.exists())
			throw new FileNotFoundException("The directory ("
					+ topDirectory.getPath() + ") doesn't exist.");
		
		mTopDirectory = topDirectory;
		
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTIONS
	// ---------------------------------------------------------------
	
	public void run() {
		
		// Get the Files' list for the mTopDirectory
		File[] children = mTopDirectory.listFiles(new DICOMFileFilter());
		
		// If the children array is null
		if (children == null) {
			
			mHandler.sendEmptyMessage(ThreadState.UNCATCHABLE_ERROR_OCCURRED);
			return;
			
		}
		
		// Get the message from the handler and send
		// the children list length
		Message message = mHandler.obtainMessage();
		message.what = ThreadState.STARTED;
		message.arg1 = children.length;
		mHandler.sendMessage(message);
		
		try {
			
			for (int i = 0; i < children.length; i++) {
				
				// Load the children
				if (loadImage(children[i])) {
					
					// Send a progression update message
					message = mHandler.obtainMessage();
					message.what = ThreadState.PROGRESSION_UPDATE;
					message.arg1 = i + 1;
					mHandler.sendMessage(message);
					
				} else {
					
					// Send a catchable error occurred
					message = mHandler.obtainMessage();
					message.what = ThreadState.CATCHABLE_ERROR_OCCURRED;
					message.arg1 = i + 1;
					message.obj = children[i].getName();
					mHandler.sendMessage(message);
					
				}
				
				// Hint the garbage collector
				System.gc();	
				
			}
			
		} catch (OutOfMemoryError ex) {
			
			// If the image can be loaded because an out of
			// memory error occurred, display it
			System.gc();
			
			mHandler.sendEmptyMessage(ThreadState.OUT_OF_MEMORY);
			
		}
		
		// Send that the thread is finished
		mHandler.sendEmptyMessage(ThreadState.FINISHED);
		
		// Hint the garbage collector a last time
		System.gc();
		
	}
	
	
	// ---------------------------------------------------------------
	// - <static> FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Load an image.
	 * 
	 * @param currentFile
	 * @return True if the image is cached, false otherwise.
	 */
	private static boolean loadImage(File currentFile) {
		
		// If the file doesn't exist return false
		if (!currentFile.exists())
			return false;
		
		// Set the current LISA 16-Bit grayscale image
		File currentLISAFile = new File(currentFile + ".lisa");
		
		// If current LISA 16-Bit grayscale image exists,
		// don't create it
		if (currentLISAFile.exists())
			return true;
		
		// Else write it
			
		try {
			
			DICOMImageReader dicomFileReader = new DICOMImageReader(currentFile);
			
			DICOMImage dicomImage = dicomFileReader.parse();
			
			dicomFileReader.close();
			
			// Compressed file are not supported => do not cached it.
			if (dicomImage.isUncompressed()) {
				
				// Write the image
				LISAImageGray16BitWriter out = new LISAImageGray16BitWriter(currentFile + ".lisa");
				out.write(dicomImage.getImage(), currentFile.getPath());
				out.flush();
				out.close();
				
			}
			
			dicomImage = null;
			
			return true;
			
			
		} catch (Exception ex) {
			
			return false;
			
		}
		
	}
	
}
