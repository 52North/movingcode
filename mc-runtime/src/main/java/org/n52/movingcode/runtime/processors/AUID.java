/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
