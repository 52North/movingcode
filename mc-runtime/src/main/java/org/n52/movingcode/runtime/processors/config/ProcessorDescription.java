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
