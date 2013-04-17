/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.movingcode.runtime.processors;

import java.security.SecureRandom;

/**
 * A Class that creates an Almost Unique ID (AUID). Compared to a UUID, the AUID
 * is consists of only eight characters which should be sufficiently unique for
 * random file name generation within this framework.
 * 
 * @author Matthias Mueller, Geoinformation Systems, TU Dresden
 * 
 */
public class AUID {
	/*
	 * The random number generator used by this class to create random based AUIDs.
	 */
	private static volatile SecureRandom numberGenerator = null;

	private static final String alphabetPlusNum = "0123456789abcdefghijklmnopqrstuvwxyz";
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * Creates a random AUID
	 * 
	 * @return - an eight digit alphanumeric string starting with a letter
	 */
	public static String randomAUID() {
		SecureRandom ng = numberGenerator;
		if (ng == null) {
			numberGenerator = ng = new SecureRandom();
		}

		char[] auid = new char[8];
		// make the first element a character
		auid[0] = alphabet.charAt(ng.nextInt(alphabet.length()));

		// make the other characters alphanumeric
		for (int i = 1; i < 8; i++) {
			auid[i] = alphabetPlusNum.charAt(ng.nextInt(alphabet.length()));
		}

		return new String(auid);
	}
}
