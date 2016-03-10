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
 * This file <DICOMReader.java> is part of Droid Dicom Viewer.
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

// This class is based on dicom4j implementation: org.dicom4j.io.DicomReader
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

import java.io.EOFException;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMItem;
import be.ac.ulb.lisa.idot.dicom.DICOMSequence;
import be.ac.ulb.lisa.idot.dicom.DICOMTag;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;

/**
 * DICOM file reader that can read meta information of
 * DICOM file.
 * 
 * @author Pierre Malarme
 * @version 1.O
 *
 */
public class DICOMReader extends DICOMBufferedInputStream {
	
	// ---------------------------------------------------------------
	// - <static> VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Length of the preamble.
	 */
	private static final int PREAMBLE_LENGTH = 128;
	
	/**
	 * Prefix of DICOM file.
	 */
	private static final String PREFIX = "DICM";
	
	
	// ---------------------------------------------------------------
	// - VARIABLES
	// ---------------------------------------------------------------
	
	/**
	 * Byte offset.
	 */
	protected long mByteOffset = 0;
	
	/**
	 * Specific charset set in the body of the DICOM file.
	 */
	protected String mSpecificCharset = "ASCII";
	
	/**
	 * File size.
	 */
	protected long mFileSize = 0;
	
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	public DICOMReader(File file) throws FileNotFoundException {
		super(file);
		mFileSize = file.length();
		mark(Integer.MAX_VALUE);
	}

	public DICOMReader(String fileName) throws FileNotFoundException {
		super(fileName);
		File file = new File(fileName);
		mFileSize = file.length();
		mark(Integer.MAX_VALUE);
	}
	
	
	// ---------------------------------------------------------------
	// - CONSTRUCTORS
	// ---------------------------------------------------------------
	
	/**
	 * Can have the file size and the BufferedInputStream do not throw
	 * a EOFException at the end of the file (test).
	 * 
	 * @param fd
	 * @throws FileNotFoundException
	 */
	private DICOMReader(FileDescriptor fd) throws FileNotFoundException {
		super(fd);
		mark(Integer.MAX_VALUE);
	}
	
	
	// ---------------------------------------------------------------
	// + <final> FUNCTIONS
	// ---------------------------------------------------------------	
	
	/**
	 * @return True if the file is a DICOM file and has meta information
	 * false otherwise.
	 * @throws IOException
	 */
	public final boolean hasMetaInformation() throws IOException {
		
		// Reset the BufferedInputStream
		if (mByteOffset > 0) {
			reset();
			mark(Integer.MAX_VALUE);
		}
		
		// If the file is smaller than the preamble and prefix
		// length there is no meta information
		if (available() < (PREAMBLE_LENGTH + PREFIX.length()))
			return false;
		
		// Skip the preamble
		skip(PREAMBLE_LENGTH);
		
		// Get the prefix
		String prefix = readASCII(PREFIX.length());
		
		// Check the prefix
		boolean toReturn = prefix.equals(PREFIX);
		
		// Reset the BufferedInputStream
		reset();
		mark(Integer.MAX_VALUE);
		
		// Skip the byte offset (mByteOffset)
		if (mByteOffset > 0)
			skip(mByteOffset);
		
		return toReturn;
		
	}
	
	/**
	 * Parse meta information.
	 * 
	 * @throws IOException
	 * @throws EOFException
	 * @throws DICOMException 
	 */
	public final DICOMMetaInformation parseMetaInformation()
			throws IOException, EOFException, DICOMException {
		
		// Reset the BufferedInputStream
		if (mByteOffset > 0) {
			reset();
			mark(Integer.MAX_VALUE);
			mByteOffset = 0;
		}
		
		try {
			
			// Skip the preamble
			skip(PREAMBLE_LENGTH);
			mByteOffset += PREAMBLE_LENGTH;
			
			// Check the prefix
			if (!PREFIX.equals(readASCII(4)))
				throw new DICOMException("This is not a DICOM file");
			mByteOffset += 4;
			
			// Create a DICOM meta information object
			DICOMMetaInformation metaInformation = new DICOMMetaInformation();
			
			// Tag File Meta group length = the length of
			// the meta of the dicom file
			int tag = readTag();
			mByteOffset += 4;
			
			// If this is not this tag => error because the
			// DICOM 7.1 (3.5-2009) tags must be ordered by increasing
			// data element
			if (tag != 0x00020000)
				throw new DICOMException("Meta Information has now length");
			
			// Skip 4 byte because we now that it is an UL
			skip(4);
			mByteOffset += 4;
			
			// Get the FileMeta group length
			long groupLength = readUnsignedLong();
			mByteOffset+= 4;
			
			// Set the group length (meta information length)
			metaInformation.setGroupLength(groupLength);
			
			DICOMMetaInformationReaderFunctions dicomReaderFunctions =
				new DICOMMetaInformationReaderFunctions(metaInformation);
			
			// Fast parsing of the header with skiping sequences
			parse(null, groupLength, true, dicomReaderFunctions, true);
			
			// Return the meta information
			return metaInformation;
			
		} catch (EOFException ex) {
			
			throw new EOFException(
					"Cannot read the Meta Information of the DICOM file\n\n"
					+ex.getMessage());
			
		} catch (IOException ex) {
			
			throw new IOException(
					"Cannot read the Meta Information of the DICOM file\n\n"
					+ex.getMessage());
			
		}
		
	}
	
	
	// ---------------------------------------------------------------
	// # FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Parse the DICOM file.
	 * 
	 * @param parentElement If a sequence is parsed.
	 * @param length The length to parse. 0xffffffffL is
	 * the undefined length.
	 * @param isExplicit Set if the content of the BufferedInputStream
	 * has explicit (true) or implicit (false) value representation.
	 * @param dicomReaderFunctions Implementation of the DICOMReaderFunctions
	 * interface.
	 * @param skipSequence Set if the sequence must be skipped (true)
	 * or not (false).
	 * @throws IOException
	 * @throws EOFException If the end of the file is reached before
	 * the end of the parsing.
	 * @throws DICOMException 
	 */
	protected void parse(DICOMItem parentElement, long length, boolean isExplicit,
			DICOMReaderFunctions dicomReaderFunctions, boolean skipSequence)
			throws IOException, EOFException, DICOMException {
		
		// Set if the the length of the element to parse is defined
		boolean isLengthUndefined = length == 0xffffffffL;
		
		try {
			
			// Variable declaration and initialization
			DICOMTag dicomTag = null;
			DICOMValueRepresentation VR = null;
			long valueLength = 0;
			int tag = 0;
			length = length & 0xffffffffL;
			long lastByteOffset = isLengthUndefined ? 0xffffffffL 
					: mByteOffset + length - 1;
			
			// Loop while the length is undefined or 
			// while the byte offset is smaller than
			// the last byte offset
			while (((isLengthUndefined) || (mByteOffset < lastByteOffset))
					&& (mByteOffset < mFileSize)) {
				
				DICOMElement element = null;
				
				// Read the tag
				tag = readTag();
				mByteOffset += 4;
				
				if (tag == 0) {
					
					reset();
					mark(Integer.MAX_VALUE);
					skip(mByteOffset - 4);
					
					tag = readTag();
					
				}
				
				// If the tag is an item delimitation 
				// skip 4 bytes because there are 
				// 4 null bytes after the item delimitation tag
				if (tag == 0xfffee00d) {
					
					skip(4);
					mByteOffset += 4;
					
					return;
				}
				
				// If the tag is an Item, ignore it
				// and skip 4 bytes because there are 
				// 4 null bytes after the item delimitation tag
				if (tag == 0xfffee000) {
					
					skip(4);
					mByteOffset += 4;
					
					continue;
				}
				
				// Get the value representation and length
				// and create the DICOMTag.
				if (isExplicit) {
					
					// Get the DICOM value representation code/abreviation
					String VRAbbreviation = readASCII(2);
					mByteOffset += 2;
					
					VR = DICOMValueRepresentation.c.get(VRAbbreviation);
					VR = (VR == null) ? DICOMValueRepresentation.c.get("UN") : VR;
					
					dicomTag = DICOMTag.createDICOMTag(tag, VR);
					
					// If the value is on 2 bytes
					if (hasValueLengthOn2Bytes(VR.getVR())) {
						
						valueLength = readUnsignedInt16();
						mByteOffset += 2;
						
					} else {
						
						skip(2); // Because VR abbreviation is coded
						// on 2 bytes
						
						valueLength = readUnsignedLong();
						mByteOffset += 6; // 2 for the skip and 4 for the unsigned long
						
					}
					
				} else {
					
					dicomTag = DICOMTag.createDICOMTag(tag);
					
					VR = dicomTag.getValueRepresentation();
					VR = (VR == null) ? DICOMValueRepresentation.c.get("UN") : VR;
					
					// If the value lengths are implicit, the length of the value
					// comes directly after the tag
					valueLength = readUnsignedLong();
					mByteOffset += 4;
					
				}
				
				valueLength = valueLength & 0xffffffffL;
				
				// Get the value
				// If it is a sequence, read a new sequence
				if (VR.equals("SQ")
						|| VR.equals("UN") && valueLength == 0xffffffffL) {
					
					// If the attribute has undefined value length
					// and/or do not skip sequence
					if (!skipSequence || valueLength == 0xffffffffL) {
						
						// Parse the sequence
						element = new DICOMSequence(dicomTag);
						parseSequence((DICOMSequence) element, valueLength, isExplicit,
								dicomReaderFunctions, skipSequence);
						
					} else {
						
						// Skip the value length
						skip(valueLength);
						mByteOffset += valueLength;
						continue;
						
					}
				
				// Else if tag is PixelData
				} else if (tag == 0x7fe00010) {
					
					dicomReaderFunctions.computeImage(parentElement, VR, valueLength);
					continue; // Return to the while begin
					
				} else if ( valueLength != 0xffffffffL) {
					
					// If it's not a required element, skip it
					if (parentElement != null ||
							!dicomReaderFunctions.isRequiredElement(tag)) {
						
						skip(valueLength);
						mByteOffset += valueLength;
						continue;
						
					}
					
					Object value = null;
					
					if (VR.equals("UL")) {
						
						if (valueLength == 4) {
							
							value = readUnsignedLong();
							mByteOffset += 4;
							
						} else {
							
							int size = (int) (valueLength / 4);
							long[] values = new long[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readUnsignedLong();
								mByteOffset += 4;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("AT")) {
						
						value = readTag();
						mByteOffset += 4;
					
					} else if (VR.equals("OB") || VR.equals("OF")
							|| VR.equals("OW")) {
						
						String valueString = new String();
						
						for (int i = 0; i < valueLength; i++) {
							
							valueString += (i == 0) ? "" : "\\";
							valueString += String.valueOf(read());
							mByteOffset++;
							
						}
						
						value = valueString;
						
					} else if (VR.equals("FL")) {
						
						// if the value length is greater
						// than 4 it is an array of float
						if (valueLength ==  4) {
							
							value = readFloatSingle();
							mByteOffset += 4;
							
						} else {
						
							int size = (int) (valueLength / 4);
							float[] values = new float[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readFloatSingle();
								mByteOffset += 4;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("FD")) {
						
						// if the value length is greater
						// than 8 it is an array of double
						if (valueLength == 8) {
							
							value = readFloatDouble();
							mByteOffset += 8;
							
						} else {
							
							int size = (int) (valueLength / 8);
							double[] values = new double[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readFloatDouble();
								mByteOffset += 8;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("SL")) {
						
						// if the value length is greater
						// than 4 it is an array of int
						if (valueLength == 4) {
							
							value = readSignedLong();
							mByteOffset += 4;
							
						} else {
							
							int size = (int) (valueLength / 4);
							int[] values = new int[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readSignedLong();
								mByteOffset += 4;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("SS")) {
						
						// if the value length is greater
						// than 2 it is an array of short
						if (valueLength == 2) {
							
							value = readSignedShort();
							mByteOffset += 2;
							
						} else {
						
							int size = (int) (valueLength / 2);
							short[] values = new short[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readSignedShort();
								mByteOffset += 2;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("US")) {
						
						// if the value length is greater
						// than 2 it is an array of int
						if (valueLength ==  2) {
							
							value = readUnsignedShort();
							mByteOffset += 2;
							
						} else {
						
							int size = (int) (valueLength / 2);
							int[] values = new int[size];
							
							for (int i = 0; i < size; i++) {
								
								values[i] = readUnsignedShort();
								mByteOffset += 2;
								
							}
							
							value = values;
							
						}
						
					} else if (VR.equals("LO") || VR.equals("LT")
							|| VR.equals("PN") || VR.equals("SH")
							|| VR.equals("ST") || VR.equals("UT")) {
						
						value = readString((int) valueLength, mSpecificCharset);
						mByteOffset += valueLength;
											
					// Else interpreted as ASCII String
					} else {
						
						value = readASCII((int) valueLength);
						mByteOffset += valueLength;
						
					}
					
					// Create the element
					element = new DICOMElement(dicomTag, valueLength, value);
				
				}
				
				if (element != null) {

					// Add the DICOM element
					dicomReaderFunctions.addDICOMElement(parentElement, element);
					
				}
				
			} // end of the while
		
		// End of the stream exception
		} catch (EOFException e) {
			
			if (!isLengthUndefined)
				throw new EOFException();
			
		// I/O Exception
		} catch (IOException e) {
			
			if (!isLengthUndefined)
				throw new IOException();
			
		}
		
	}
	
	/**
	 * Parse a DICOM sequence.
	 * 
	 * @param sequence DICOM sequence to parse.
	 * @param length Length of DICOM sequence.
	 * @param isExplicit Set if the content of the BufferedInputStream
	 * has explicit (true) or implicit (false) value representation.
	 * @param dicomReaderFunctions Implementation of the DICOMReaderFunctions
	 * interface.
	 * @param skipSequence Set if the sequence must be skipped (true)
	 * or not (false).
	 * @throws IOException
	 * @throws DICOMException 
	 * @throws EOFException If the end of the file is reached before
	 * the end of the parsing.
	 */
	protected void parseSequence(DICOMSequence sequence, long length, boolean isExplicit,
			DICOMReaderFunctions dicomReaderFunctions, boolean skipSequence)
			throws IOException, EOFException, DICOMException {
		
		if (sequence == null) {
			throw new NullPointerException("Null Sequence");
		}
		
		length = length & 0xffffffffL;
		boolean isLengthUndefined = length == 0xffffffffL;
		
		try {
			
			long lastByteOffset = isLengthUndefined ? 0xffffffffL
					: mByteOffset + length - 1;
			
			// Loop on all the items
			while (isLengthUndefined || mByteOffset < lastByteOffset) {
				
				// Get the tag
				int tag = readTag();
				mByteOffset += 4;
				
				long valueLength = readUnsignedLong();
				mByteOffset += 4;
				
				// If the tag is an Item
				if (tag == 0xfffee0dd) {
					break;
					
				} else if (tag == 0xfffee000) {
					
					DICOMItem item = new DICOMItem();
					
					parse(item, valueLength, isExplicit,
							dicomReaderFunctions, skipSequence);
					
					sequence.addChild(item);
				
				// else if the tag is different that end
				// of sequence, this is not a sequence item
				} else {
					
					throw new DICOMException("Error Sequence: unknown tag" + (tag >> 16) + (tag & 0xffff));
					
				}
				
			}
			
		} catch (EOFException e) {
			
			if (!isLengthUndefined)
				throw new EOFException();
				
		} catch (IOException ex) {
			
			if (!isLengthUndefined)
				throw new IOException(ex.getMessage());
			
		}		
		
	}
	
	/**
	 * Check if the value representation is on 2 bytes.
	 * 
	 * @param VR DICOM value representation code on 2 bytes (character).
	 * @return
	 */
	protected static final boolean hasValueLengthOn2Bytes(String VR) {
		return VR.equals("AR") || VR.equals("AE") || VR.equals("AS") || VR.equals("AT")
			|| VR.equals("CS") || VR.equals("DA") || VR.equals("DS") || VR.equals("DT")
			|| VR.equals("FD") || VR.equals("FL") || VR.equals("IS") || VR.equals("LO")
			|| VR.equals("LT") || VR.equals("PN") || VR.equals("SH") || VR.equals("SL")
			|| VR.equals("SL") || VR.equals("SS") || VR.equals("ST") || VR.equals("TM")
			|| VR.equals("UI") || VR.equals("UL") || VR.equals("US");
	}
	
	
	// ---------------------------------------------------------------
	// # CLASS
	// ---------------------------------------------------------------
	
	/**
	 * Implementation of the DICOMReaderFunctions for
	 * meta information.
	 * 
	 * @author Pierre Malarme
	 * @version 1.O
	 *
	 */
	protected class DICOMMetaInformationReaderFunctions implements DICOMReaderFunctions {
		
		private DICOMMetaInformation mMetaInformation;
		
		public DICOMMetaInformationReaderFunctions() {
			mMetaInformation = new DICOMMetaInformation();	
		}
		
		public DICOMMetaInformationReaderFunctions(DICOMMetaInformation metaInformation) {
			mMetaInformation = metaInformation;	
		}

		public void addDICOMElement(DICOMElement parent, DICOMElement element) {
			
			// If this is a sequence, do nothing
			if (parent != null)
				return;
			
			int tag = element.getDICOMTag().getTag();
			
			// SOP Class UID
			if (tag == 0x00020002) {
				
				mMetaInformation.setSOPClassUID(element.getValueString());
				
			// SOP Instance UID
			} else if (tag == 0x00020003) {
				
				mMetaInformation.setSOPInstanceUID(element.getValueString());
			
			// Transfer syntax UID
			} else if (tag == 0x00020010) {
				
				mMetaInformation.setTransferSyntaxUID(element.getValueString());
				
			// Implementation Class UID
			} else if (tag == 0x00020012) {
				
				mMetaInformation.setImplementationClassUID(element.getValueString());
				
			// Implementation version name
			} else if (tag == 0x00020013) {
				
				mMetaInformation.setImplementationVersionName(element.getValueString());
				
			// Implementation Application Entity Title
			} else if (tag == 0x00020016) {
				
				mMetaInformation.setAET(element.getValueString());

            // Patient name
			} else if (tag == 0x00100010) {

                mMetaInformation.setPatientName(element.getValueString());
            }
			
		}

		public boolean isRequiredElement(int tag) {
			return (tag == 0x00020002) || (tag == 0x00020003) || (tag == 0x00020010)
				|| (tag == 0x00020012) || (tag == 0x00020013) || (tag == 0x00020016);
		}

		public void computeImage(DICOMElement parent, DICOMValueRepresentation VR,
				long length) throws IOException, EOFException, DICOMException {
			
			throw new IOException("PixelData in Meta Information.");
			
		}
		
	}

}
