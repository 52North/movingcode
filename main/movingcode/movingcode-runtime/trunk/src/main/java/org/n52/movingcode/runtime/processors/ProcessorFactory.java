/**
 * 
 */
package org.n52.movingcode.runtime.processors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.ProcessorConfig;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processorconfig.ProcessorType;
import org.n52.movingcode.runtime.processorconfig.ProcessorsDocument;
import org.n52.movingcode.runtime.processorconfig.PropertyType;


import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument.PackageDescription;
import de.tudresden.gis.geoprocessing.movingcode.schema.PlatformType;

/**
 * This singleton class is responsible for managing and delivering processors that can digest
 * the code contained in the moving code packages. 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class ProcessorFactory {

	private static ProcessorFactory instance;

	private Map<String,String[]> supportedContainers;
	private String[] availablePlatforms = null;
	private Map<String,File> scratchworkspaceMap;
	private Map<String,PropertyMap> processorProperties;

	private String defaultWorkspace = null;

	Logger logger = Logger.getLogger(ProcessorFactory.class);

	private ProcessorFactory (){
		super();
		initConfig();
	}

	/**
	 * Delivers a new {@link AbstractProcessor} that is able to handle the mcPackage
	 * passed in the method call.
	 * 
	 * @param mcPackage
	 * @return {@link AbstractProcessor}
	 * 
	 */
	public AbstractProcessor newProcessor(final MovingCodePackage mcPackage){

		String processorID = findCompatibleProcessor(mcPackage.getDescription().getPackageDescription());

		if (processorID != null){
			return loadProcessor(processorID,
					getScratchworkspace(processorID),
					mcPackage,
					getProcessorProperties(processorID));
		} else {
			return null; // if no suitable processor was found
		}

	}

	public static synchronized ProcessorFactory getInstance(){
		if (instance == null){
			instance = new ProcessorFactory();
		}
		return instance;
	}

	/**
	 * Load configuration from the config file.
	 */
	private void initConfig(){
		supportedContainers = new HashMap<String,String[]>();
		scratchworkspaceMap = new HashMap<String,File>();
		processorProperties = new HashMap<String, PropertyMap>();

		try{
			ProcessorsDocument processors = ProcessorConfig.getInstance().getConfig();

			//deal with defaults
			defaultWorkspace = processors.getProcessors().getDefaults().getTempWorkspace();
			availablePlatforms = processors.getProcessors().getDefaults().getAvailablePlatformArray();

			// deal with individual processors
			for (ProcessorType processor : processors.getProcessors().getProcessorArray()){
				supportedContainers.put(processor.getId(), processor.getSupportedContainerArray());
				if (processor.isSetTempWorkspace()){
					File scratchWS = new File(processor.getTempWorkspace());
					scratchWS.mkdirs(); // TODO check retval
					scratchworkspaceMap.put(processor.getId(), scratchWS);
				} else {
					File scratchWS = new File(defaultWorkspace);
					scratchworkspaceMap.put(processor.getId(), scratchWS);
				}

				PropertyMap pMap = new PropertyMap();

				for (PropertyType property : processor.getPropertyArray()){
					pMap.put(property.getKey(), property.getValue());
				}

				processorProperties.put(processor.getId(), pMap);
			}

		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		ProcessorConfig.getInstance().addPropertyChangeListener(ProcessorConfig.PROCESSORCONFIG_UPDATE_EVENT_NAME, new PropertyChangeListener() {
			public void propertyChange(
					final PropertyChangeEvent propertyChangeEvent) {
				initConfig();
			}
		});

	}

	/**
	 * Loader for processor classes. Uses dynamic class loading and returns {@link AbstractProcessor}.
	 * If the class loading and instantiation should fail for some reason NULL is returned.
	 * 
	 * @param processorClassName
	 * @param scratchworkspace
	 * @param mcp
	 * @param properties
	 * @return {@link AbstractProcessor}
	 */
	private AbstractProcessor loadProcessor(String processorClassName, final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {

		try {
			// load class
			Class<?> processorClass = ProcessorFactory.class.getClassLoader().loadClass(processorClassName);
			// get a suitable constructor
			Constructor<?> processorConstructor = processorClass.getDeclaredConstructor(File.class, MovingCodePackage.class, PropertyMap.class);
			// return new Object
			return (AbstractProcessor)processorConstructor.newInstance(scratchworkspace, mcp, properties);

		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			// catch everything
		}
		return null;
	}

	/**
	 * Getter for registered processors.
	 * 
	 * @return Array of {@link String} containing the IDs of the registered processors.
	 */
	public String[] registeredProcessors(){
		return supportedContainers.keySet().toArray(new String[supportedContainers.keySet().size()]);
	}

	/**
	 * Getter for registered platforms.
	 * 
	 * @return Array of {@link String} containing the IDs of the registered platforms.
	 */
	public String[] getAvailablePlatforms(){
		return availablePlatforms;
	}

	/**
	 * @param {@link String} processorID
	 * @return {@link File} scatchworkspace assigned to a processor class 
	 */
	public File getScratchworkspace(String processorID){
		return scratchworkspaceMap.get(processorID);
	}

	/**
	 * Getter for properties arrays. Currently for informative use only. 
	 * 
	 * @param {@link String}  processorID
	 * @return {@link PropertyMap}
	 */
	public PropertyMap getProcessorProperties(String processorID){
		return processorProperties.get(processorID);
	}

	/**
	 * Helper method: Does an array of Strings contain a particular String?
	 * Comparison is case-insensitive.
	 * 
	 * @param haystack - Array of Strings
	 * @param needle - String to be searched
	 * @return boolean - true if haystack contains the needle, false if not.
	 */
	private static final boolean needleInHaystack(final String[] haystack, final String needle){
		for (String currentItem : haystack){
			if (currentItem.equalsIgnoreCase(needle)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Helper method: Does an array of Strings contain a particular set of Strings?
	 * Comparison is case-insensitive.
	 * 
	 * @param haystack - Array of Strings
	 * @param needles - Strings to be searched
	 * @return boolean - true if haystack contains all the needles, false if not.
	 */
	private static final boolean allNeedlesInHaystack(final String[] haystack, final String[] needles){
		for (String currentNeedle : needles){
			// if one of the needles is missing return false 
			if (!needleInHaystack(haystack, currentNeedle)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Lookup method that returns the first processor's ID that is compatible with the
	 * description. If no appropriate processor is found NULL is returned.
	 * 
	 * @param {@link PackageDescription} description
	 * @return {@link String} processorID
	 */
	private String findCompatibleProcessor(final PackageDescription description){

		PlatformType[] validPlatforms = description.getContractedPlatformArray();

		// 1. Do we have some required platforms in place?
		boolean inPlace = false;
		for (PlatformType currentPlatform : validPlatforms){

			// platforms defined using attribute syntax
			if (currentPlatform.isSetPlatformId()){
				if (needleInHaystack(availablePlatforms,currentPlatform.getPlatformId())){
					inPlace = true;
					break;
				}
			}			

			// platforms defined by the array
			if (allNeedlesInHaystack(availablePlatforms,currentPlatform.getRequiredRuntimeComponentArray())){
				inPlace = true;
				break;
			}

		}
		if (!inPlace){
			return null;
		}

		String requiredContainer = description.getWorkspace().getContainerType();

		// 2. return a processor that supports the particular container
		//    if no processor supports this container return null
		for (String currentID : supportedContainers.keySet()){
			if (needleInHaystack(supportedContainers.get(currentID), requiredContainer)){
				// return first applicable processor
				return currentID;
			}
		}
		return null;
	}

}
