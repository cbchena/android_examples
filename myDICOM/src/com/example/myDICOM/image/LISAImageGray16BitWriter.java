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
 * This file <LISAImageGray16BitWriter.java> is part of Droid Dicom Viewer.
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

package com.example.myDICOM.image;

import java.io.*;

/**
 * Writer for LISA 16-Bit grayscale image.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class LISAImageGray16BitWriter extends FileOutputStream {
	
	// ---------------------------------------------------------------
	// - <static> VARIABLE
	// ---------------------------------------------------------------
	
	protected static final String PREFIX = "LISAGRAY0016";
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------

	public LISAImageGray16BitWriter(File file) throws FileNotFoundException {
		super(file);
	}
	
	public LISAImageGray16BitWriter(FileDescriptor fd) {
		super(fd);
	}
	
	public LISAImageGray16BitWriter(String filename) throws FileNotFoundException {
		super(filename);
	}
	
	
	// ---------------------------------------------------------------
	// # CONSTRUCTORS
	// ---------------------------------------------------------------
	
	/**
	 * Function private to forbid its use.
	 * 
	 * @param file
	 * @param append
	 * @throws FileNotFoundException
	 */
	private LISAImageGray16BitWriter(File file, boolean append) throws FileNotFoundException {
		super(file, append);
	}
	
	/**
	 * Function private to forbid its use.
	 * 
	 * @param filename
	 * @param append
	 * @throws FileNotFoundException
	 */
	private LISAImageGray16BitWriter(String filename, boolean append) throws FileNotFoundException {
		super(filename, append);
	}
	
	
	// ---------------------------------------------------------------
	// + FUNCTION
	// ---------------------------------------------------------------

	/**
	 * Write a LISA 16-Bit grayscale image.
	 * @param image A LISA 16-bit grayscale image.
	 * @throws IOException
	 */
	public void write(LISAImageGray16Bit image) throws IOException {
		
		if (image == null)
			throw new NullPointerException("Image is null");
		
		try {
			
			// PREFIX
			// Write the prefix
			write(PREFIX.getBytes());
			
			// IMAGE SIZE
			// Write width
			writeInt16(image.getWidth());
			
			// Write height
			writeInt16(image.getHeight());
			
			// GRAY LEVELS AND WINDOW
			// Write the gray levels
			writeLong32(image.getGrayLevel());
			
			// Write window width
			writeInt16(image.getWindowWidth());
			
			// Write window center
			writeInt16(image.getWindowCenter());
			
			// Write the image orientation
			writeImageOrientation(image);
			
			// Write image length
			writeLong32(image.getDataLength());
			
			// Write the image data
			writeInt16Array(image.getData());
			
		} catch (IOException e) {
			throw new IOException("Cannot open write LISA image.\n"
					+ e.getMessage());
		}
		
		
	}
	
	
	// ---------------------------------------------------------------
	// # FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Write an integer on 2 bytes.
	 * @param value Integer value.
	 * @throws IOException
	 */
	protected final void writeInt16(int value) throws IOException {
		
		byte[] int16Bytes = new byte[2];
		
		int16Bytes[0] = (byte) ((value >> 8) & 0xff);
		int16Bytes[1] = (byte) ((value) & 0xff);
		
		super.write(int16Bytes);
		
	}
	
	/**
	 * Write a long on 4 bytes.
	 * 
	 * If the value correspond to the image length the maximum value
	 * must be set as Integer.MAX_VALUE due to java array limitation:
	 * the maximum length is the maximum integer value.
	 * 
	 * @param value Long value.
	 * @throws IOException
	 */
	protected final void writeLong32(long value) throws IOException {
		
		byte[] long32Bytes = new byte[4];
		
		long32Bytes[0] = (byte) ((value >> 24) & 0xff);
		long32Bytes[1] = (byte) ((value >> 16) & 0xff);
		long32Bytes[2] = (byte) ((value >> 8) & 0xff);
		long32Bytes[3] = (byte) ((value) & 0xff);
		
		super.write(long32Bytes);
		
	}
	
	/**
	 * Write an array of integer.
	 * 
	 * Each integer value is coded in 2 bytes.
	 * 
	 * @param intArray Array of integer values.
	 * @throws IOException
	 */
	protected final void writeInt16Array(int[] intArray) throws IOException {
		
		byte[] intArrayBytes = new byte[intArray.length * 2];
		
		for (int i = 0; i < intArray.length; i ++) {
			
			intArrayBytes[(2 * i) + 0] =
				(byte) ((intArray[i] >> 8) & 0xff);
			
			intArrayBytes[(2 * i) + 1] =
				(byte) ((intArray[i]) & 0xff);
			
		}
		
		super.write(intArrayBytes);
		
	}
	
	/**
	 * Write an array of float values.
	 * 
	 * @param floatArray
	 * @throws IOException
	 */
	protected final void writeFloatArray(float[] floatArray) throws IOException {
		
		for (int i = 0; i < floatArray.length; i++) {
			
			int binaryValue = Float.floatToRawIntBits(floatArray[i]);
			
			writeLong32(binaryValue);
			
		}
		
	}
	
	/**
	 * Write the image orientation float array.
	 * 
	 * @param image
	 * @throws IOException
	 */
	protected final void writeImageOrientation(LISAImageGray16Bit image) throws IOException {
		
		float[] imageOrientation = image.getImageOrientation();
		
		// Check if the array is null or not null
		imageOrientation = (imageOrientation == null) ? new float[6] : imageOrientation;
		
		writeFloatArray(imageOrientation);
		
	}
	
}
