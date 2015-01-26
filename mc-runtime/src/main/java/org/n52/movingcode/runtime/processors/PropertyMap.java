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

import java.util.HashMap;

/**
 * A {@link HashMap} of the type <String, String>. It serves as a (Key,Value) map to describe additional
 * properties of a processor.
 * 
 * 
 * @author Matthias Mueller
 * 
 * TODO: make it a value object --> comparable, .toString(), ...
 */
public class PropertyMap extends HashMap<String, String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3104930318249367786L;

	public PropertyMap() {
		super();
	}

	/**
	 * Convenience method for printing a list of properties stored in this map.
	 * 
	 * @return {@link String} properties list
	 */
	public String print() {
		String retval = "";
		for (String key : keySet()) {
			retval = retval + key + "\t" + get(key) + "\n";
		}
		return retval;
	}
}
