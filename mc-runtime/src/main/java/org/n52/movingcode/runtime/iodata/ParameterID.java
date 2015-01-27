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
package org.n52.movingcode.runtime.iodata;

import java.math.BigInteger;

/**
 * ParameterID class. Stores type and name / id of an input or output parameter
 * and provides some conversion methods.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class ParameterID implements Comparable<ParameterID> {

	private final String stringValue;
	private final int seqVal;
	
	static enum IDType {
		SEQUENTIAL, NAMED
	}
	
	/**
	 * Creates a ParameterID from a BigInteger. This is usually required with
	 * PackageDescription XML.
	 * 
	 * @param position
	 */
	public ParameterID(BigInteger position) {
		if (position.compareTo(new BigInteger(Integer.toString(1))) == -1
				|| position.compareTo(new BigInteger(Integer
						.toString(Integer.MAX_VALUE))) == 1) {
			throw new IllegalArgumentException(
					"Sequential Values must be greater than zero and smaller than "
							+ (Integer.MAX_VALUE - 1) + "!");
		}
		this.stringValue = position.toString();
		this.seqVal = position.intValue();
	}

	/**
	 * Simple constructor for int positions
	 * 
	 * @param position
	 */
	public ParameterID(int position) throws IllegalArgumentException {
		if (position < 1) {
			throw new IllegalArgumentException(
					"Sequential Values must be greater than zero!");
		}
		this.stringValue = Integer.toString(position);
		this.seqVal = position;
	}

	public ParameterID(String name) {
		this.stringValue = name;
		this.seqVal = -1;
	}

	public IDType getType() {
		if (this.seqVal == -1) {
			return IDType.NAMED;
		}
		return IDType.SEQUENTIAL;
	}
	
	@Override
	public String toString() {
		return this.stringValue;
	}

	/**
	 * Converts a harmonized value to an INT. If this is not possible an
	 * exception will be thrown.
	 * 
	 * @param harmonizedValue
	 * @return int
	 * @throws IllegalArgumentException
	 * 
	 */
	public static final int toInt(String harmonizedValue)
			throws IllegalArgumentException {
		try {
			return Integer.parseInt(harmonizedValue);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot convert "
					+ harmonizedValue + " to INT!");
		}
	}

	@Override
	public boolean equals(Object obj) {
		try {
			ParameterID id = (ParameterID) obj;
			if (id.toString().equals(this.toString())) {
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public int compareTo(ParameterID otherID) {

		// make sure we have identical types
		if (!this.getType().equals(otherID.getType())) {
			throw new ClassCastException(
					"Mixing sequential and named parameters in comparison operations is not allowed.");
		}

		// logic for sequential params
		if (!this.getType().equals(IDType.SEQUENTIAL)) {
			return compare(this.seqVal, otherID.seqVal);
		}

		return this.stringValue.compareTo(otherID.stringValue);
	}

	private static int compare(int x, int y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}
}
