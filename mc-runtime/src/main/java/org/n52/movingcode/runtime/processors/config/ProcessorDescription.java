package org.n52.movingcode.runtime.processors.config;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Processor description class. Provides configuration properties of processors
 * defined in <code>processors.json</code>
 * 
 * @author matthias
 *
 */
public class ProcessorDescription {
	String id = null;
	ArrayList<String> containers = new ArrayList<String>();
	ArrayList<String> platforms = new ArrayList<String>();
	HashMap<String, String> properties = new HashMap<String, String>();
	String tempWorkspace = null;
	
	
	public ProcessorDescription() {
		super();
	}
	
	/**
	 * Setter for processor ID
	 * 
	 * @param processorId
	 */
	public void setId(String processorId){
		this.id = processorId;
	}
	
	/**
	 * Getter for processor ID
	 * 
	 * @return
	 */
	public String getId(){
		return this.id;
	}
	
	/**
	 * Set new properties Map
	 * 
	 * @param properties
	 */
	public void setProperties(HashMap<String, String> properties){
		this.properties = properties;
	}
	
	/**
	 * Add a new supported container ID
	 * 
	 * @param containerId
	 */
	public void addContainer(String containerId){
		containers.add(containerId);
	}
	
	
	/**
	 * Add a new supported platform ID
	 * 
	 * @param platformId
	 */
	public void addPlatform(String platformId){
		platforms.add(platformId);
	}
	
	/**
	 * Is a particular platform supported by this processor?
	 * 
	 * @param containerId
	 * @return
	 */
	public boolean supportsPlatform(String platformId){
		// do ignore case comparison
		for(String platform : platforms){
			if (platform.equalsIgnoreCase(platformId)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Is a particular container supported by this processor?
	 * 
	 * @param containerId
	 * @return
	 */
	public boolean supportsContainer(String containerId){
		// do ignore case comparison
		for(String container : containers){
			if (container.equalsIgnoreCase(containerId)){
				return true;
			}
		}
		return false;
	}
	
	
	public String[] getContainers(){
		return containers.toArray(new String[containers.size()]);
	}
	
	public HashMap<String,String> getProperties(){
		return properties;
	}
	
	public String[] getPlatforms(){
		return platforms.toArray(new String[platforms.size()]);
	}
	
	
	/**
	 * Getter for processor specific properties.
	 * 
	 * @param key
	 * @return
	 */
	public String getProperty(String key){
		return properties.get(key);
	}
	
	/**
	 * @return the tempWorkspace
	 */
	public String getTempWorkspace() {
		return tempWorkspace;
	}

	/**
	 * @param tempWorkspace the tempWorkspace to set
	 */
	public void setTempWorkspace(String tempWorkspace) {
		this.tempWorkspace = tempWorkspace;
	}
	
	
	
}
