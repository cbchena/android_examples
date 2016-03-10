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
 * This file <LISAImageGray16BitReader.java> is part of Droid Dicom Viewer.
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

package be.ac.ulb.lisa.idot.image.file;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

/**
 * Reader for LISA 16-Bit grayscale image.
 * 
 * @author Pierre Malarme
 * @version 1.0
 *
 */
public class LISAImageGray16BitReader extends FileInputStream {
	
	// ---------------------------------------------------------------
	// - <static> VARIABLE
	// ---------------------------------------------------------------
	
	protected static final String PREFIX = "LISAGRAY0016";
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------

	public LISAImageGray16BitReader(File file) throws FileNotFoundException {
		super(file);
	}
	
	public LISAImageGray16BitReader(FileDescriptor fd) {
		super(fd);
	}
	
	public LISAImageGray16BitReader(String fileName) throws FileNotFoundException {
		super(fileName);
	}

    /**
     * 解析文件 2016/3/10 12:45
     * @param filePath 文件路径
     * @return
     * @throws IOException
     * @throws EOFException
     */
    public synchronized LISAImageGray16Bit parseImage(String filePath) throws IOException, EOFException {
        LISAImageGray16Bit imageGray16Bit;
        InputStreamReader read = new InputStreamReader(
                new FileInputStream(new File(filePath + ".dcminfo")));
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt;
        Map<String, String> myMap = new HashMap<>();
        while((lineTxt = bufferedReader.readLine()) != null){
            if (lineTxt != null) {
                String[] pairs = lineTxt.split("::");
                for (int i = 0;i < pairs.length;i++) {
                    String pair = pairs[i];
                    if (pair != null && pair.length() > 0) {
                        String[] keyValue = pair.split("=");
                        myMap.put(keyValue[0], keyValue[1]);
                    }
                }

                break;
            }
        }

        read.close();
        imageGray16Bit = parseImage();
        imageGray16Bit.setAttributes(myMap);

        return imageGray16Bit;
    }

	// ---------------------------------------------------------------
	// + FUCTIONS
	// ---------------------------------------------------------------
	
	public synchronized LISAImageGray16Bit parseImage() throws IOException, EOFException {

		// Check the prefix
		if (!PREFIX.equals(readASCII(PREFIX.length())))
				throw new IOException("This is not a LISA 16-Bit" +
						"grayscale image");
		
		try {
			
			byte[] buffer = new byte[available()];
			
			read(buffer);
			
			// Create the image
			LISAImageGray16Bit image = new LISAImageGray16Bit();
			
			// Get the image attributes
			int byteOffset = 0;
			
			// Image Width
			image.setWidth((short) ((buffer[byteOffset + 0] & 0xff) << 8 | (buffer[byteOffset + 1] & 0xff)));
			byteOffset += 2;

			// Image Height
			image.setHeight((short) ((buffer[byteOffset + 0] & 0xff) << 8 | (buffer[byteOffset + 1] & 0xff)));
			byteOffset += 2;
			
			// Image gray levels count
			int grayLevel = (buffer[byteOffset + 0] & 0xff) << 24 | (buffer[byteOffset + 1] & 0xff) << 16
				| (buffer[byteOffset + 2] & 0xff) << 8 | (buffer[byteOffset + 3] & 0xff);
			byteOffset += 4;
			image.setGrayLevel(grayLevel);
			
			// Window width
			image.setWindowWidth(((buffer[byteOffset + 0] & 0xff) << 8 | (buffer[byteOffset + 1] & 0xff)));
			byteOffset += 2;
			
			// Window Height
			image.setWindowCenter(((buffer[byteOffset + 0] & 0xff) << 8 | (buffer[byteOffset + 1] & 0xff)));
			byteOffset += 2;
			
			// Image orientation
			float[] imageOrientation = new float[6];
			
			for (int i = 0; i < 6; i++) {
				
				int binaryValue = (buffer[byteOffset + 0] & 0xff) << 24 | (buffer[byteOffset + 1] & 0xff) << 16
				| (buffer[byteOffset + 2] & 0xff) << 8 | (buffer[byteOffset + 3] & 0xff);
				
				byteOffset += 4;
				
				imageOrientation[i] = Float.intBitsToFloat(binaryValue);
				
			}
			
			image.setImageOrientation(imageOrientation);
			
			// Data length
			int dataLength =  (buffer[byteOffset + 0] & 0xff) << 24 | (buffer[byteOffset + 1] & 0xff) << 16
				| (buffer[byteOffset + 2] & 0xff) << 8 | (buffer[byteOffset + 3] & 0xff);
			byteOffset += 4;
			
			// Compute the histogram data and max
			// and the image data and max
			int[] imageData = new int[dataLength];
			int imageDataMax = 0;
			
			int[] imageHistogram = new int[grayLevel];
			int imageHistogramMax = 0;
			
			for (int i = 0; i < dataLength; i ++) {
				
				imageData[i] = (buffer[byteOffset] & 0xff) << 8
					| (buffer[byteOffset + 1] & 0xff);
				
				byteOffset += 2;
				
				if (imageData[i] > imageDataMax)
					imageDataMax = imageData[i];
				if (imageData[i] >= 0 && imageData[i] < grayLevel) {
					imageHistogram[imageData[i]] += 1;
					if (imageHistogram[imageData[i]] > imageHistogramMax)
						imageHistogramMax = imageHistogram[imageData[i]];
				}
				
			}
			
			image.setData(imageData);
			image.setDataMax(imageDataMax);
			
			image.setHistogramData(imageHistogram);
			image.setHistogramMax(imageHistogramMax);
			
			return image;
			
		} catch (EOFException ex) {
			
			throw new EOFException("Reached the end of the file before " +
					"reading all data. \n" + ex.getMessage());
			
		
		} catch (IOException ex) {
			
			throw new IOException("Cannot parse the LISA Image " +
					"grayscale 16-Bit. \n" + ex.getMessage());
		}
		
	}
	
	
	// ---------------------------------------------------------------
	// # FUNCTION
	// ---------------------------------------------------------------
	
	/**
	 * Read byte[length] as ASCII.
	 * @param length The number of bytes to read.
	 * @return String that contains the ASCII value.
	 * @throws IOException
	 */
	protected synchronized final String readASCII(int length) throws IOException, EOFException {
		byte[] ASCIIbyte = new byte[length];
		
		if (read(ASCIIbyte) == -1)
			throw new IOException();
		
		// To avoid the null char : ASCII(0)
		String toReturnString = new String(ASCIIbyte, "ASCII");
		
		for (int i = 0; i < length; i++)
			if (ASCIIbyte[i] == 0x00)
					return toReturnString.substring(0, i);
		
		return toReturnString;
	}
	
	
}
