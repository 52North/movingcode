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
