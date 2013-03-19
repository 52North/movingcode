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
/**
 * 
 */

package org.n52.movingcode.runtime.iodata;

import java.math.BigInteger;
import java.util.List;

/**
 * Interface for accessing IO data throughout the library.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public interface IIOParameter extends List {

    /**
     * Directions for communication with backend processors ("internal direction")
     * 
     * @author Matthias Mueller, TU Dresden
     * 
     */
    public static enum Direction {
        IN, OUT, BOTH, NONE;
    }

    /**
     * Directions for service IO ("external direction")
     * 
     * @author Matthias Mueller, TU Dresden
     * 
     */
    public static enum MessageDirection {
        INPUT, OUTPUT
    }

    /**
     * Identifies the content type.
     * 
     * @return IODataType
     */
    public IODataType getType();

    /**
     * Checks appropriate content type.
     * 
     * @param type
     * @return
     */
    public boolean supportsType(IODataType type);

    /**
     * Minimum multiplicity of this parameter: 0 (zero) means optional >0 is mandatory
     * 
     * @return int - minimum Multiplicity
     */
    public int getMinMultiplicity();

    public Direction getDirection();

    /**
     * Maximum multiplicity of this parameter: equal to minMultiplicity means an exactly n-times
     * Integer.MAX_VALUE is the maximum possible multiplicity
     * 
     * @return int
     */
    public int getMaxMultiplicity();

    /**
     * Returns a unique identifier
     * 
     * @return String - the unique Identifier of this data item
     */
    public ParameterID getIdentifier();

    /**
     * Returns the service input ID.
     * 
     * @return String - the InputID; null if not applicable
     */
    public String getMessageInputIdentifier();

    /**
     * Returns the service output ID.
     * 
     * @return String - the OutputID; null if not applicable
     */
    public String getMessageOutputIdentifier();

    public boolean isMessageIn();

    public boolean isMessageOut();

    public boolean isMessageInputID(String messageInputID);

    public boolean isMessageOutputID(String messageOutputID);

    public boolean isMandatoryMessage();

    public boolean isMandatoryForExecution();

    public boolean supportsValue(Object o);

    /**
     * true if all IODataID is sequential, false if not
     * 
     * @param identifier
     * @return String
     */
    public boolean isSequential();

    /**
     * Returns the parameter's prefix. Returns "" (empty String) if not set.
     * 
     * @param identifier
     * @return String
     */
    public String printPrefix();

    /**
     * Prints the parameter's suffix. Returns "" (empty String) if not set.
     * 
     * @param identifier
     * @return String
     */
    public String printSuffix();

    /**
     * Returns the parameter's separator. Returns "" (empty String) if not set.
     * 
     * @param identifier
     * @return String
     */
    public String printSeparator();

    /**
     * Class for harmonized use of a parameterID. Stores type and value and provides access and conversion
     * methods.
     * 
     * @author Matthias Mueller, TU Dresden
     * 
     */
    public final class ParameterID implements Comparable {
        public static enum IDType {
            SEQUENTIAL, NAMED
        }

        private final String stringValue;
        private final int seqVal;

        /**
         * Creates a ParameterID from a BigInteger. This is usually required with PackageDescription XML.
         * 
         * @param position
         */
        public ParameterID(BigInteger position) {
            if (position.compareTo(new BigInteger(Integer.toString(1))) == -1
                    || position.compareTo(new BigInteger(Integer.toString(Integer.MAX_VALUE))) == 1) {
                throw new IllegalArgumentException("Sequential Values must be greater than zero and smaller than "
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
                throw new IllegalArgumentException("Sequential Values must be greater than zero!");
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
            else {
                return IDType.SEQUENTIAL;
            }
        }

        public String getHarmonizedValue() {
            return this.stringValue;
        }

        /**
         * Converts a harmonized value to an INT. If this is not possible an exception will be thrown.
         * 
         * @param harmonizedValue
         * @return int
         * @throws IllegalArgumentException
         * 
         */
        public static final int toInt(String harmonizedValue) throws IllegalArgumentException {
            try {
                return Integer.parseInt(harmonizedValue);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Cannot convert " + harmonizedValue + " to INT!");
            }
        }

        @Override
        public boolean equals(Object obj) {
            try {
                ParameterID id = (ParameterID) obj;
                if (id.getHarmonizedValue().equalsIgnoreCase(this.getHarmonizedValue())) {
                    return true;
                }
                else {
                    return false;
                }
            }
            catch (Exception e) {
                return false;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(Object o) throws ClassCastException {

            // cast
            ParameterID otherID = (ParameterID) o;

            // make sure we have identical types
            if ( !this.getType().equals(otherID.getType())) {
                throw new ClassCastException("Mixing sequential and named parameters in comparison operations is not allowed.");
            }

            // logic for sequential params
            if ( !this.getType().equals(IDType.SEQUENTIAL)) {
                return compare(this.seqVal, otherID.seqVal);
            }
            else {
                return this.stringValue.compareTo(otherID.stringValue);
            }

        }

        private static int compare(int x, int y) {
            return (x < y) ? -1 : ( (x == y) ? 0 : 1);
        }
    }

}
