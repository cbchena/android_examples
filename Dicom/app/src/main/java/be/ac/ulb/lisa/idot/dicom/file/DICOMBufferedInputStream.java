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
 * This file <DICOMBufferedInputStream.java> is part of Droid Dicom Viewer.
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

// This class is based on dicom4j implementation: org.dicom4j.io.BinaryInputStream
// author: <a href="mailto:straahd@users.sourceforge.net">Laurent Lecomte
// http://dicom4j.sourceforge.net/
// Dicom4j License:
/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.                                 
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
// Dicom4j License [End]

package be.ac.ulb.lisa.idot.dicom.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * DICOM BufferedInputStream needed to read a DICOM file.
 * 
 * It implements protected read methods for the
 * DICOMImageReader.
 * 
 * @author Pierre Malarme
 * @version 1.O
 *
 */
public class DICOMBufferedInputStream extends BufferedInputStream {
	
	// ---------------------------------------------------------------
	// + <static> VARIABLES
	// ---------------------------------------------------------------
	
	public static final short LITTLE_ENDIAN = 0;
	public static final short BIG_ENDIAN = 1;
	
	
	// ---------------------------------------------------------------
	// # VARIABLES
	// ---------------------------------------------------------------
	
	protected short mByteOrder = LITTLE_ENDIAN;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	public DICOMBufferedInputStream(File file) throws FileNotFoundException {
		super(new FileInputStream(file), 8192);
	}
	
	public DICOMBufferedInputStream(FileDescriptor fd) throws FileNotFoundException {
		super(new FileInputStream(fd), 8192);
	}

	public DICOMBufferedInputStream(String fileName) throws FileNotFoundException {
		super(new FileInputStream(fileName), 8192);
	}
	
	public DICOMBufferedInputStream(InputStream inputStream) {
		super(inputStream, 8192);
	}
	
	public DICOMBufferedInputStream(InputStream inputStream, int bufferSize) {
		super(inputStream, bufferSize);
	}
	

	// ---------------------------------------------------------------
	// # FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Read an unsigned short that is coded on 2 bytes.
	 * 
	 * @return Unsigned short value.
	 * @throws IOException
	 */
	protected final int readUnsignedShort() throws IOException {
		return  readUnsignedInt16();
	}
	
	
	/**
	 * Read an unsigned integer of 16-Bit.
	 * 
	 * @return Int16 value.
	 * @throws IOException
	 */
	protected final int readUnsignedInt16() throws IOException {
		
		byte[] unsignedInt16 = new byte[2];
		
		if (read(unsignedInt16) != 2)
			throw new IOException("Cannot read an unsigned int 16-Bit.");
		
		if (mByteOrder == LITTLE_ENDIAN)
			return (unsignedInt16[1] & 0xff) << 8 | (unsignedInt16[0] & 0xff);
		else
			return (unsignedInt16[0] & 0xff) << 8 | (unsignedInt16[1] & 0xff);
		
	}
	
	/**
	 * Read an unsigned long coded on 4 bytes.
	 * 
	 * @return Unsigned long 32-Bit value.
	 * @throws IOException
	 */
	protected final long readUnsignedLong() throws IOException {
		
		byte[] unsignedLong = new byte[4];
		
		if (read(unsignedLong) != 4)
			throw new IOException("Cannot read an unsigned long 32-Bit.");
		
		if (mByteOrder == LITTLE_ENDIAN)
			return ((long) unsignedLong[3] & 0xff) << 24 | ((long) unsignedLong[2] & 0xFF) << 16
				| ((long) unsignedLong[1] & 0xFF) << 8 | ((long) unsignedLong[0] & 0xff);
		else
			return ((long) unsignedLong[0] & 0xff) << 24 | ((long) unsignedLong[1] & 0xFF) << 16
				| ((long) unsignedLong[2] & 0xFF) << 8 | ((long) unsignedLong[3] & 0xff);
		
	}
	
	/**
	 * Read an unsigned long coded on 8 bytes.
	 * 
	 * @return Unsigned long 64-Bit value.
	 * @throws IOException
	 */
	protected final long readUnsignedLong64() throws IOException {
		
		byte[] unsignedLong64 = new byte[8];
		
		if (read(unsignedLong64) != 8)
			throw new IOException("Cannot read an unsigned long 64-Bit.");
		
		if (mByteOrder == LITTLE_ENDIAN)
			return (((long) unsignedLong64[7] & 0xff) << 56) | (((long) unsignedLong64[6] & 0xff) << 48)
				| (((long) unsignedLong64[5] & 0xff) << 40) | (((long) unsignedLong64[4] & 0xff) << 32)
				| (((long) unsignedLong64[3] & 0xff) << 24) | (((long) unsignedLong64[2] & 0xff) << 16)
				| (((long) unsignedLong64[1] & 0xff) << 8) | ((long) unsignedLong64[0] & 0xff);
		else
			return (((long) unsignedLong64[0] & 0xff) << 56) | (((long) unsignedLong64[1] & 0xff) << 48)
			| (((long) unsignedLong64[2] & 0xff) << 40) | (((long) unsignedLong64[3] & 0xff) << 32)
			| (((long) unsignedLong64[4] & 0xff) << 24) | (((long) unsignedLong64[5] & 0xff) << 16)
			| (((long) unsignedLong64[6] & 0xff) << 8) | ((long) unsignedLong64[7] & 0xff);
		
	}
	
	/**
	 * Read a signed long coded on 4 bytes.
	 * 
	 * @return Signed long 32-Bit (= Java int) value.
	 * @throws IOException
	 */
	protected final int readSignedLong() throws IOException {
		
		byte[] signedLong = new byte[4];
		
		if (read(signedLong) != 4)
			throw new IOException("Cannot read a signed long 32-Bit.");
		
		if (mByteOrder == LITTLE_ENDIAN)
			return ((signedLong[3] & 0xFF) << 24) | ((signedLong[2] & 0xff) << 16)
				| ((signedLong[1] & 0xff) << 8) | (signedLong[0] & 0xff);
		else
			return ((signedLong[0] & 0xFF) << 24) | ((signedLong[1] & 0xff) << 16)
			| ((signedLong[2] & 0xff) << 8) | (signedLong[3] & 0xff);
		
	}
	
	/**
	 * Read a signed short coded on 2 bytes.
	 * 
	 * @return Signed Short 16-Bit value.
	 * @throws IOException
	 */
	protected final short readSignedShort() throws IOException {
		
		byte[] signedShort = new byte[2];
		
		if (read(signedShort) != 2)
			throw new IOException("Cannot read a signed short 16-bit");
		
		short s1 = (short) (signedShort[0] & 0xff);
		short s2 = (short) (signedShort[1] & 0xff);
		
		if (mByteOrder == LITTLE_ENDIAN)
			return (short) (s2 << 8 | s1);
		else
			return (short) (s1 << 8 | s2);
		
	}
	
	/**
	 * Read a float single.
	 * 
	 * @return Float single value.
	 * @throws IOException
	 */
	protected final float readFloatSingle() throws IOException {
		
		int intBits = (int) readUnsignedLong();
		
		return Float.intBitsToFloat(intBits);
		
	}
	
	/**
	 * Read float double.
	 * 
	 * @return Float double value.
	 * @throws IOException
	 */
	protected final double readFloatDouble() throws IOException {
		
		long longBits = readUnsignedLong64();
		
		return Double.longBitsToDouble(longBits);
		
	}
	
	/**
	 * Read a tag and coded on 32 bit.
	 * 
	 * @return Tag coded on 32 bit and stored as an integer.
	 * @throws IOException
	 */
	protected final int readTag() throws IOException {
		
		int group = readUnsignedInt16();
		int element = readUnsignedInt16();
		
		return ((group & 0xffff) << 16 | (element & 0xffff));
	}
	
	/**
	 * Read byte[length] as ASCII.
	 * 
	 * @param length The number of bytes to read.
	 * @return String that contains the ASCII value.
	 * @throws IOException
	 */
	protected final String readASCII(int length) throws IOException {
		byte[] ASCIIbyte = new byte[length];
		read(ASCIIbyte);
		
		// To avoid the null char : ASCII(0)
		String toReturnString = new String(ASCIIbyte, "ASCII");
		
		for (int i = 0; i < length; i++)
			if (ASCIIbyte[i] == 0x00)
					return toReturnString.substring(0, i);
		
		return toReturnString;
	}
	
	/**
	 * Read byte[length] as ASCII.
	 * @param length The number of bytes to read.
	 * @return String that contains the ASCII value.
	 * @throws IOException
	 */
	protected final String readString(int length, String charset) throws IOException {
		byte[] stringIbyte = new byte[length];
		
		if (read(stringIbyte) != length)
			throw new IOException("readString: Size mismatch");
		
		// To avoid the null char : ASCII(0)
		String toReturnString;
		
		try {
			
			toReturnString = new String(stringIbyte, charset);
			
		} catch (UnsupportedEncodingException ex) {
			
			toReturnString = new String(stringIbyte, "ASCII");
			
		}
		
		for (int i = 0; i < length; i++)
			if (stringIbyte[i] == 0x00)
				if (i == (length - 1))
					return toReturnString.substring(0,length - 1);
			
		return toReturnString;
	}

	/**
	 * @return Byte order.
	 */
	protected short getByteOrder() {
		return mByteOrder;
	}

	/**
	 * @param mByteOrder Byte order to set
	 */
	protected void setByteOrder(short byteOrder) {
		this.mByteOrder = byteOrder;
	}

}
