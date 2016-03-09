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
 * This file <Geometry.java> is part of Droid Dicom Viewer.
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

package com.example.myDICOM.utils;

import android.graphics.PointF;
import android.util.FloatMath;

/**
 * The class Geometry contains geometry functions.
 * 
 * @author Pierre Malarme
 * 
 * @version 1.0
 *
 */
public class Geometry {
	
	// ---------------------------------------------------------------
	// + <static> FUNCTIONS
	// ---------------------------------------------------------------
	
	/**
	 * Get the euclidian distance between P1 (x1, y1) and P2 (x2, y2).
	 * 
	 * @param x1 The x-coordinate of the P1.
	 * @param y1 The y-coordinate of the P1.
	 * @param x2 The x-coordinate of the P2.
	 * @param y2 The y-coordinate of the P2.
	 * @return The euclidian distance between (x1, y1) and (x2, y2).
	 */
	public static final float euclidianDistance(float x1, float y1, float x2, float y2) {
		
		// Compute coordinate subtraction
		float x = x2 - x1;
		float y = y2 - y1;
		
		// Compute the euclidian distance
		return FloatMath.sqrt(x * x + y * y);
		
	}
	
	/**
	 * Get the middle point between P1 (x1, y1) and P2 (x2, y2).
	 * 
	 * @param x1 The x-coordinate of the P1.
	 * @param y1 The y-coordinate of the P1.
	 * @param x2 The x-coordinate of the P2.
	 * @param y2 The y-coordinate of the P2.
	 * @return The middle point between (x1, y1) and (x2, y2).
	 */
	public static final PointF midPoint(float x1, float y1, float x2, float y2) {
		
		// Compute coordinate addition
		float x = x1 + x2;
		float y = y1 + y2;
		
		return new PointF(x / 2f, y / 2f);
		
	}

}
