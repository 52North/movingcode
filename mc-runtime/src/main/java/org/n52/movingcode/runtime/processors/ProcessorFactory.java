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

package org.n52.movingcode.runtime.processors;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processors.config.ProcessorConfig;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument.PackageDescription;
import de.tudresden.gis.geoprocessing.movingcode.schema.PlatformType;

/**
 * This singleton class is responsible for managing and delivering processors that can digest the code
 * contained in the moving code packages.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class ProcessorFactory {
	
	private static ProcessorFactory instance;

	private Map<String, String[]> supportedContainers;
	private String[] availablePlatforms = ProcessorConfig.getDefaultPlatforms();
	private Map<String, File> scratchworkspaceMap;
	private Map<String, PropertyMap> processorProperties;

	Logger logger = Logger.getLogger(ProcessorFactory.class);

	private ProcessorFactory() {
		super();
		initConfig();
	}

	/**
	 * Delivers a new {@link AbstractProcessor} that is able to handle the mcPackage passed in the method
	 * call.
	 * 
	 * @param mcPackage
	 * @return {@link AbstractProcessor}
	 * 
	 */
	public AbstractProcessor newProcessor(final MovingCodePackage mcPackage) {

		String processorID = findCompatibleProcessor(mcPackage.getDescription().getPackageDescription());

		if (processorID != null) {
			return loadProcessor(processorID,
					getScratchworkspace(processorID),
					mcPackage,
					getProcessorProperties(processorID));
		}
		return null; // if no suitable processor was found
	}

	public boolean supportsPackage(final MovingCodePackage mcPackage){
		String processorID = findCompatibleProcessor(mcPackage.getDescription().getPackageDescription());

		// return true if processorID is not null
		return processorID != null ? true : false; 
	}

	public static synchronized ProcessorFactory getInstance() {
		if (instance == null) {
			instance = new ProcessorFactory();
		}
		return instance;
	}

	/**
	 * Load configuration from the config file.
	 */
	private void initConfig() {
		this.supportedContainers = new HashMap<String, String[]>();
		this.scratchworkspaceMap = new HashMap<String, File>();
		this.processorProperties = new HashMap<String, PropertyMap>();

		try {

			// deal with individual processors
			for (String processorId : ProcessorConfig.getRegisteredProcessorIDs()) {
				this.supportedContainers.put(processorId, ProcessorConfig.getSupportedContainers(processorId));
				String workspace = ProcessorConfig.getWorkspace(processorId);
				
				// create and check temp workspace
				// register this temp WS in the private Map
				File scratchWS;
				// check for $TEMP$ token
				if (workspace.equals(ProcessorConfig.randomTempDirToken)){
					scratchWS = newTempDir();
				} else {
					scratchWS = new File(workspace);
					// attempt to create directories if they don't exist
					scratchWS.mkdirs();
				}
				// TODO: check if temp dir really exists; if not: throw an error
				
				this.scratchworkspaceMap.put(processorId, scratchWS);

				PropertyMap pMap = new PropertyMap();
				pMap.putAll(ProcessorConfig.getProperties(processorId));

				this.processorProperties.put(processorId, pMap);
			}

		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		
	}

	/**
	 * Loader for processor classes. Uses dynamic class loading and returns {@link AbstractProcessor}. If the
	 * class loading and instantiation should fail for some reason NULL is returned.
	 * 
	 * @param processorClassName
	 * @param scratchworkspace
	 * @param mcp
	 * @param properties
	 * @return {@link AbstractProcessor}
	 */
	private AbstractProcessor loadProcessor(String processorClassName,
			final File scratchworkspace,
			final MovingCodePackage mcp,
			final PropertyMap properties) {

		try {
			// load class
			Class< ? > processorClass = ProcessorFactory.class.getClassLoader().loadClass(processorClassName);
			// get a suitable constructor
			Constructor< ? > processorConstructor = processorClass.getDeclaredConstructor(File.class,
					MovingCodePackage.class,
					PropertyMap.class);
			// return new Object
			return (AbstractProcessor) processorConstructor.newInstance(scratchworkspace, mcp, properties);

		}
		catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// catch everything else
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Getter for registered processors.
	 * 
	 * @return Array of {@link String} containing the IDs of the registered processors.
	 */
	public String[] registeredProcessors() {
		return this.supportedContainers.keySet().toArray(new String[this.supportedContainers.keySet().size()]);
	}

	/**
	 * Getter for registered platforms.
	 * 
	 * @return Array of {@link String} containing the IDs of the registered platforms.
	 */
	public String[] getAvailablePlatforms() {
		return this.availablePlatforms;
	}

	/**
	 * @param {@link String} processorID
	 * @return {@link File} scatchworkspace assigned to a processor class
	 */
	public File getScratchworkspace(String processorID) {
		return this.scratchworkspaceMap.get(processorID);
	}

	/**
	 * Getter for properties arrays. Currently for informative use only.
	 * 
	 * @param {@link String} processorID
	 * @return {@link PropertyMap}
	 */
	public PropertyMap getProcessorProperties(String processorID) {
		return this.processorProperties.get(processorID);
	}

	/**
	 * Helper method: Does an array of Strings contain a particular String? Comparison is case-insensitive.
	 * 
	 * @param haystack
	 *        - Array of Strings
	 * @param needle
	 *        - String to be searched
	 * @return boolean - true if haystack contains the needle, false if not.
	 */
	private static final boolean needleInHaystack(final String[] haystack, final String needle) {
		for (String currentItem : haystack) {
			if (currentItem.equalsIgnoreCase(needle)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method: Does an array of Strings contain a particular set of Strings? Comparison is
	 * case-insensitive.
	 * 
	 * @param haystack
	 *        - Array of Strings
	 * @param needles
	 *        - Strings to be searched
	 * @return boolean - true if haystack contains all the needles, false if not.
	 */
	private static final boolean allNeedlesInHaystack(final String[] haystack, final String[] needles) {
		for (String currentNeedle : needles) {
			// if one of the needles is missing return false
			if ( !needleInHaystack(haystack, currentNeedle)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Lookup method that returns the first processor's ID that is compatible with the description. If no
	 * appropriate processor is found NULL is returned.
	 * 
	 * @param {@link PackageDescription} description
	 * @return {@link String} processorID
	 */
	private String findCompatibleProcessor(final PackageDescription description) {

		PlatformType[] validPlatforms = description.getPlatformArray();

		// 1. Do we have some required platforms in place?
		boolean inPlace = false;
		for (PlatformType currentPlatform : validPlatforms) {

			// platforms defined using attribute syntax
			if (currentPlatform.isSetPlatformId()) {
				if (needleInHaystack(this.availablePlatforms, currentPlatform.getPlatformId())) {
					inPlace = true;
					break;
				}
			}

			// platforms defined by the array
			if (allNeedlesInHaystack(this.availablePlatforms, currentPlatform.getRequiredRuntimeComponentArray())) {
				inPlace = true;
				break;
			}

		}
		if ( !inPlace) {
			return null;
		}

		String requiredContainer = description.getWorkspace().getContainerType();

		// 2. return a processor that supports the particular container
		// if no processor supports this container return null
		for (String currentID : this.supportedContainers.keySet()) {
			if (needleInHaystack(this.supportedContainers.get(currentID), requiredContainer)) {
				// return first applicable processor
				return currentID;
			}
		}
		return null;
	}
	
	private static final File newTempDir(){
		File tmpDir = FileUtils.getTempDirectory();
		File newTmpFolder = new File(tmpDir, AUID.randomAUID());
		newTmpFolder.mkdir();
		newTmpFolder.deleteOnExit();
		return newTmpFolder;
	}
}
