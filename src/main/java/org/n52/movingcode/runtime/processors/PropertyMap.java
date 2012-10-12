package org.n52.movingcode.runtime.processors;

import java.util.HashMap;

/**
 * A {@link HashMap} of the type <String, String>. It serves as a (Key,Value) map
 * to describe additional properties of a processor.
 * 
 * 
 * @author Matthias Mueller
 * 
 */
public class PropertyMap extends HashMap<String,String>{
	
	public PropertyMap() {
		super();
	}
	
	/**
	 * Convenience method for printing a list of properties stored in this map.
	 * 
	 * @return {@link String} properties list
	 */
	public String print(){
		String retval = "";
		for (String key : keySet()){
			retval = retval + key + "\t" + get(key) + "\n";
		}
		return retval;
	}
}
