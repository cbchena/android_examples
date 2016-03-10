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
 * This file <DICOMImageReader.java> is part of Droid Dicom Viewer.
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

import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.dcm4che3.io.DicomInputStream;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import be.ac.ulb.lisa.idot.android.dicomviewer.data.DCM4CheTagNameHack;
import be.ac.ulb.lisa.idot.dicom.DICOMElement;
import be.ac.ulb.lisa.idot.dicom.DICOMException;
import be.ac.ulb.lisa.idot.dicom.DICOMValueRepresentation;
import be.ac.ulb.lisa.idot.dicom.data.DICOMBody;
import be.ac.ulb.lisa.idot.dicom.data.DICOMImage;
import be.ac.ulb.lisa.idot.dicom.data.DICOMMetaInformation;
import be.ac.ulb.lisa.idot.image.data.LISAImageGray16Bit;

/**
 * DICOM image file reader that read only grayscale image
 * with the bits allocated maximum value: 16 bits.
 * It does not support also compressed file format.
 * 
 * For RGB image or compressed image, it parse just the meta
 * information and the body.
 * 
 * @author Pierre Malarme
 * @version 1.O
 *
 */
public class DICOMImageReader extends DICOMReader {

    private  File _file;
	
	// ---------------------------------------------------------------
	// + CONSTRUCTORS
	// ---------------------------------------------------------------
	
	public DICOMImageReader(File file) throws FileNotFoundException {
        super(file);
        _file = file;
	}

//	public DICOMImageReader(String fileName) throws FileNotFoundException {
//		super(fileName);
//	}
	
	
	// ---------------------------------------------------------------
	// + <final> FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Parse the image DICOM file.
	 * 
	 * @throws IOException
	 * @throws EOFException
	 * @throws DICOMException 
	 */
	public final DICOMImage parse() throws IOException, EOFException, DICOMException {
		
		
		// Variables declaration
		DICOMMetaInformation metaInformation;
		boolean isExplicit;
		short compressionStatus = DICOMImage.UNKNOWN_STATUS;
		
		// Parse meta information
		if (hasMetaInformation()) {
			
			metaInformation = parseMetaInformation();
			
			String transferSyntaxUID = metaInformation.getTransferSyntaxUID();
			
			if (transferSyntaxUID.equals("1.2.840.10008.1.2")) {
				
				isExplicit = false;
				setByteOrder(LITTLE_ENDIAN);
				compressionStatus = DICOMImage.UNCOMPRESSED;
				
			} else if (transferSyntaxUID.equals("1.2.840.10008.1.2.1")) {
				
				isExplicit = true;
				setByteOrder(LITTLE_ENDIAN);
				compressionStatus = DICOMImage.UNCOMPRESSED;
				
			}  else if (transferSyntaxUID.equals("1.2.840.10008.1.2.2")) {
				
				isExplicit = true;
				setByteOrder(BIG_ENDIAN);
				compressionStatus = DICOMImage.UNCOMPRESSED;
				
			} else {
				
				isExplicit = true;
				setByteOrder(LITTLE_ENDIAN);
				compressionStatus = DICOMImage.COMPRESSED;
				
				// Compressed image are not supported yet
				// => throw a exception
				throw new DICOMException("The image is compressed."
						+ " This is not supported yet.");
				
			}
			
		} else {
			
			metaInformation = null;
			
			isExplicit = false;
			setByteOrder(LITTLE_ENDIAN);
			
		}
		
		// Parse the body
		DICOMImageReaderFunctions dicomReaderFunctions =
			new DICOMImageReaderFunctions(isExplicit, compressionStatus);
		
		parse(null, 0xffffffffL, isExplicit, dicomReaderFunctions, true);

        // 设置属性 2016/3/10 10:05
        DicomInputStream dis = new DicomInputStream(_file);
        Attributes attribs = dis.readDataset(-1, Tag.PixelData);
        dis.close();

        int[] tags = attribs.tags();
        String description;
        Map<String, String> map = new HashMap<>();
        for (int i = 0;i < tags.length;i++) {
            int tag = tags[i];
            description = Integer.toHexString(tag);
            switch(tag){
                case Tag.PatientAge:
                    break;
                default:
                    description = DCM4CheTagNameHack.getTagName(tag);
            }
            if (description != null) {
                map.put(description, attribs.getString(tag));
            }
        }
        dicomReaderFunctions.getImage().setAttributes(map);

        DICOMImage dicomImage = new DICOMImage(metaInformation,
				dicomReaderFunctions.getBody(),
				dicomReaderFunctions.getImage(),
				compressionStatus);
		
		return dicomImage;
		
	}
	
	
	// ---------------------------------------------------------------
	// # CLASS
	// ---------------------------------------------------------------
	
	protected class DICOMImageReaderFunctions implements DICOMReaderFunctions {
		
		// TODO support encapsulated PixelData ? or throw an error
		
		DICOMBody mBody;
		LISAImageGray16Bit mImage;
		boolean mIsExplicit;
		short mCompressionStatus;
		
		public DICOMImageReaderFunctions(boolean isExplicit, short compressionStatus) {
			
			mBody = new DICOMBody();
			mImage = new LISAImageGray16Bit();
			mIsExplicit = isExplicit;
			mCompressionStatus = compressionStatus;
			
		}

		public void addDICOMElement(DICOMElement parent, DICOMElement element) {
			
			// If this is a sequence, do nothing
			if (parent != null)
				return;
			
			int tag = element.getDICOMTag().getTag();
			
			// Specific character set
			if (tag == 0x00080005) {
				mBody.setSpecificCharset(element.getValueString());
				
				// Set the specific character set
				mSpecificCharset = mBody.getSpecificCharset();
				
			// If tag == image type
			} else if (tag == 0x00080008) {
				mBody.setImageType(element.getValueString());
				
			// If tag == image orientation
			} else if (tag == 0x00200037) {
				mImage.setImageOrientation(getImageOrientation(element));
				
			// If tag == samples per pixel	
			} else if (tag == 0x00280002) {
				mBody.setSamplesPerPixel(element.getValueInt());
				
			// If tag == rows
			} else if (tag == 0x00280010) {
				mImage.setHeight((short) element.getValueInt());
				
			// If tags == columns
			} else if (tag == 0x00280011) {
				mImage.setWidth((short) element.getValueInt());
				
			// If tag == bits allocated	
			} else if (tag == 0x00280100) {
				mBody.setBitsAllocated(element.getValueInt());
				
			// If tag == bits stored
			} else if (tag == 0x00280101) {
				mBody.setBitsStored(element.getValueInt());
				
				// Set the image gray level
				mImage.setGrayLevel((int) Math.pow(2, mBody.getBitsStored()));
				
			// If tag == high bit
			} else if (tag == 0x00280102) {
				mBody.setHightBit(element.getValueInt());
				
			// If tag == pixel representation
			} else if (tag == 0x00280103) {
				mBody.setPixelRepresentation(element.getValueInt());
				
			// If tag == window center	
			} else if (tag == 0x00281050) {
				mImage.setWindowCenter(getIntFromStringArray(element));
					
			// If tag == window width	
			} else if (tag == 0x00281051) {
				mImage.setWindowWidth(getIntFromStringArray(element));
					
			}
			
		}

		public boolean isRequiredElement(int tag) {
			
			return (tag == 0x00080005) || (tag == 0x00080008) || (tag == 0x00200037)
			|| (tag == 0x00280002) || (tag == 0x00280010) || (tag == 0x00280011)
			|| (tag == 0x00280100) || (tag == 0x00280101) || (tag == 0x00280102)
			|| (tag == 0x00280103) || (tag == 0x00281050) || (tag == 0x00281051);
			
		}

		public void computeImage(DICOMElement parent,
				DICOMValueRepresentation VR, long valueLength)
				throws IOException, EOFException, DICOMException {
			
			// If the image is compressed, or if the compression status
			// is unknown or if the parent exists or if the bits
			// allocated is not defined, skip it
			if (mCompressionStatus == DICOMImage.UNKNOWN_STATUS
					|| mCompressionStatus == DICOMImage.COMPRESSED
					|| mBody.getBitsAllocated() == 0
					|| parent != null) {
				
				if (valueLength == 0xffffffffL) {
					throw new DICOMException("Cannot skip the PixelData" +
							" because the length is undefined");
				} else {
					skip(valueLength);
					mByteOffset += valueLength;
					return;
				}
				
			}
			
			// Check the samples per pixel, just 1 is implemented yet
			if (mBody.getSamplesPerPixel() != 1)
				throw new DICOMException("The samples per pixel ("
						+ mBody.getSamplesPerPixel() + ") is not"
						+ " supported yet.");
			
			// For Implicit: OW and little endian
			if (!mIsExplicit) {
				
				computeOWImage(valueLength);
				
			// Explicit VR
			} else {
				
				// If it is OB return because OB is not
				// supported yet
				if (VR.equals("OB")) {
					
					skip(valueLength);
					mByteOffset += valueLength;
					return;
					
					// TODO throw an error if bits allocated > 8
					// and VR == OB because PS 3.5-2009 Pg. 66-68:
					// If the bits allocated > 8 => OW !
					// But it's not done because we do not know
					// if this specification of the standard is
					// respected and we do not implement for now
					// the OB reading.
					
				} else if (VR.equals("OW")) {
					
					computeOWImage(valueLength);
					
				} else {
					
					// Unknown data pixel value representation
					throw new DICOMException("Unknown PixelData");
				}
				
			}
			
		}
		
		public DICOMBody getBody() {
			return mBody;
		}
		
		public LISAImageGray16Bit getImage() {
			return mImage;
		}
		
		/**
		 * Compute an
		 * 
		 * @param valueLength
		 * @throws IOException
		 * @throws EOFException
		 * @throws DICOMException
		 */
		private void computeOWImage(long valueLength)
				throws IOException, EOFException, DICOMException {
			
			// Check the value length
			if (valueLength == 0xffffffffL)
				throw new DICOMException("Cannot parse PixelData " +
						"because the length is undefined");
			
			// Get the bits allocated
			int bitsAllocated = mBody.getBitsAllocated();
			
			// Cf. PS 3.5-2009 Pg. 66-67
			if (bitsAllocated == 8) {
			
				computeOW8BitImage(valueLength);
				
			} else if (bitsAllocated == 16) {
				
				computeOW16BitImage(valueLength);
				
			} else if (bitsAllocated == 32) {
				
				/* for (int i = 0; i < mPixelData.length; i++) {
					mPixelData[i] = (int) readUnsignedLong();
				}
			
				mByteOffset += valueLength; */
			
				// TODO We can sample the gray level on 16 bit but
				// is it compatible with the DICOM standard ?
			
				throw new DICOMException("This image cannot be parsed "
						+ "because the bits allocated value ("
						+ bitsAllocated
						+ ") is not supported yet.");
				
			} else if (bitsAllocated == 64) {
				
				/* for (int i = 0; i < mPixelData.length; i++) {
					mPixelData[i] = (int) readUnsignedLong64();
				}
				
				mByteOffset += valueLength; */
				
				// TODO We can sample the gray level on 16 bit but
				// is it compatible with the DICOM standard ?
				
				throw new DICOMException("This image cannot be parsed "
						+ "because the bits allocated value ("
						+ bitsAllocated
						+ ") is not supported yet.");
				
			} else {
				
				throw new DICOMException("This image cannot be parsed "
						+ "because the bits allocated value ("
						+ bitsAllocated
						+ ") is not supported yet.");
				
			}
			
			// Add the value length to the byte offset
			mByteOffset += valueLength;
			
		}
		
		private void computeOW16BitImage(long valueLength)
				throws IOException, EOFException, DICOMException {
			
			// Check that the value length correspond to 2 * width * height
			if (valueLength != (2 * mImage.getWidth() * mImage.getHeight()))
				throw new DICOMException("The size of the image does not " +
						"correspond to the size of the Pixel Data coded " +
						"in byte.");
			
			// Get the bit shift (e.g.: highBit = 11, bitsStored = 12
			// => 11 - 12 + 1 = 0 i.e. no bit shift), (e.g.: highBit = 15,
			// bitsStored = 12 => 15 - 12 + 1 = 4
			int bitShift = mBody.getHightBit() - mBody.getBitsStored() + 1;
			int grayLevel = mImage.getGrayLevel();
			
			int[] imageData = new int[(int) (valueLength / 2)];
			int imageDataMax = 0;
			int[] imageHistogram = new int[grayLevel];
			int imageHistogramMax = 0;
			
			if (bitShift == 0) {
				
				for (int i = 0; i < imageData.length; i++) {
					
					imageData[i] = readUnsignedInt16() & 0x0000ffff;
					
					if (imageData[i] > imageDataMax)
						imageDataMax = imageData[i];
					
					if (imageData[i] >= 0 && imageData[i] < grayLevel) {
						
						imageHistogram[imageData[i]] += 1;
						
						if (imageHistogram[imageData[i]] > imageHistogramMax)
							imageHistogramMax = imageHistogram[imageData[i]];
						
					}
					
				}
				
				
			} else {
				
				for (int i = 0; i < imageData.length; i++) {
					
					imageData[i] = (readUnsignedInt16() >> bitShift) & 0x0000ffff;
					
					if (imageData[i] > imageDataMax)
						imageDataMax = imageData[i];
					
					if (imageData[i] >= 0 && imageData[i] < grayLevel) {
						
						imageHistogram[imageData[i]] += 1;
						
						if (imageHistogram[imageData[i]] > imageHistogramMax)
							imageHistogramMax = imageHistogram[imageData[i]];
						
					}
					
				}
				
			}
			
			mImage.setData(imageData);
			mImage.setDataMax(imageDataMax);
			mImage.setHistogramData(imageHistogram);
			mImage.setHistogramMax(imageHistogramMax);
			
		}
		
		private void computeOW8BitImage(long valueLength)
				throws IOException, EOFException, DICOMException {
			
			// Check that the value length correspond to 2 * width * height
			if (valueLength != (mImage.getWidth() * mImage.getHeight()))
				throw new DICOMException("The size of the image does not " +
						"correspond to the size of the Pixel Data coded " +
						"in byte.");
			
			// Get the bit shift (e.g.: highBit = 4, bitsStored = 5
			// => 4 - 5 + 1 = 0 i.e. no bit shift), (e.g.: highBit = 6,
			// bitsStored = 5 => 6 - 5 + 1 = 2
			int bitShift = mBody.getHightBit() - mBody.getBitsStored() + 1;
			int grayLevel = mImage.getGrayLevel();
			
			int[] imageData = new int[(int) (valueLength)];
			int imageDataMax = 0;
			int[] imageHistogram = new int[grayLevel];
			int imageHistogramMax = 0;
			
			if (bitShift == 0) {
				
				for (int i = 0; i < imageData.length; i++) {
					
					imageData[i] = read() & 0x000000ff;
					
					if (imageData[i] > imageDataMax)
						imageDataMax = imageData[i];
					
					if (imageData[i] >= 0 && imageData[i] < grayLevel) {
						
						imageHistogram[imageData[i]] += 1;
						
						if (imageHistogram[imageData[i]] > imageHistogramMax)
							imageHistogramMax = imageHistogram[imageData[i]];
						
					}
					
				}
				
				
			} else {
				
				for (int i = 0; i < imageData.length; i++) {
					
					imageData[i] = (read() >> bitShift) & 0x000000ff;
					
					if (imageData[i] > imageDataMax)
						imageDataMax = imageData[i];
					
					if (imageData[i] >= 0 && imageData[i] < grayLevel) {
						
						imageHistogram[imageData[i]] += 1;
						
						if (imageHistogram[imageData[i]] > imageHistogramMax)
							imageHistogramMax = imageHistogram[imageData[i]];
						
					}
					
				}
				
			}
			
			mImage.setData(imageData);
			mImage.setDataMax(imageDataMax);
			mImage.setHistogramData(imageHistogram);
			mImage.setHistogramMax(imageHistogramMax);
			
		}
		
		private int getIntFromStringArray(DICOMElement element) {
			
			// Explode the string
			String[] values = element.getValueString().split("\\\\");
			
			if (values.length >= 1) {
				
				try {
					
					// We do this because if the value is coded as
					// a float single
					return Math.round(Float.parseFloat(values[0]));
					
				} catch (NumberFormatException ex) {
					
					return -1;
					
				}
				
			}
			
			return -1;
		}
		
		private float[] getImageOrientation(DICOMElement element) {
			
			// Explode the string
			String[] values = element.getValueString().split("\\\\");
			
			if (values.length != 6)
				return null;
			
			float[] imageOrientation = new float[6];
			
			for (int i = 0; i < 6; i ++) {
				
				try {
					
					imageOrientation[i] = Float.parseFloat(values[i]);
					
				} catch (NumberFormatException ex) {
					
					return null;
					
				}
				
			}
			
			return imageOrientation;
			
		}
		
	}
	
}
