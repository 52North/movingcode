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

import java.util.List;

/**
 * Interface for accessing IO data throughout the library.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public interface IIOParameter extends List {

	/**
	 * Directions for communication with backend processors
	 * ("internal direction")
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
	 * Minimum multiplicity of this parameter: 0 (zero) means optional >0 is
	 * mandatory
	 * 
	 * @return int - minimum Multiplicity
	 */
	public int getMinMultiplicity();

	public Direction getDirection();

	/**
	 * Maximum multiplicity of this parameter: equal to minMultiplicity means an
	 * exactly n-times Integer.MAX_VALUE is the maximum possible multiplicity
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

}
