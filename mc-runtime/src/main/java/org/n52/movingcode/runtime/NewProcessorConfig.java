package org.n52.movingcode.runtime;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.processorconfig2.ProcessorDescription;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import static com.fasterxml.jackson.core.JsonToken.*;

public class NewProcessorConfig {
	
	static final String KEY_PROCESSORS = "processors";
	static final String KEY_DEFAULTS = "defaults";
	
	static final String KEY_ID = "id";
	static final String KEY_SUPPORTED_CONTAINER = "supportedContainer";
	static final String KEY_PROPERTIES = "properties";
	static final String KEY_AVAILABLE_PLATFORMS = "availablePlatforms";
	static final String KEY_TEMPWORKSPACE = "tempWorkspace";
	
	static final String DEFAULT_PROCESSOR_CONFIG_ID = "DEFAULT";
	
	static final String configFile = "processors.json";
	private static final HashMap<String, ProcessorDescription> processors = readProperties();
	
	private static transient Logger logger = Logger.getLogger(NewProcessorConfig.class);
	
	
	/**
	 * Getter for registered processor IDs
	 * 
	 * @return
	 */
	public static final String[] getRegisteredProcessorIDs(){
		ArrayList<String> processorList = new ArrayList<String>();
		
		// remove default id since this is not considered a processor
		for (String proc : processors.keySet()){
			if (proc != DEFAULT_PROCESSOR_CONFIG_ID){
				processorList.add(proc);
			}
		}
		
		return processorList.toArray(new String[processorList.size()]);
	}
	
	/**
	 * Getter for container IDs
	 * 
	 * @param processorId
	 * @return
	 */
	public static final String[] getContaines(String processorId){
		return processors.get(processorId).getContainers();
	}
	
	
	public static final HashMap<String, String> getProperties(String processorID){
		return processors.get(processorID).getProperties();
	}
	
	public static final String getDefaultWorkspace(){
		ProcessorDescription ret = processors.get(DEFAULT_PROCESSOR_CONFIG_ID);
		return ret.getTempWorkspace();
	}
	
	public static final String[] getDefaultPlatforms(){
		return processors.get(DEFAULT_PROCESSOR_CONFIG_ID).getPlatforms();
	}
	
	public static final HashMap<String, String> getDefaultProperties(String processorID){
		return processors.get(DEFAULT_PROCESSOR_CONFIG_ID).getProperties();
	}
	
	
	/**
	 * Properties reader
	 * 
	 * @return
	 */
	private static final HashMap<String, ProcessorDescription> readProperties(){
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(configFile);
		
		HashMap<String, ProcessorDescription> processorMap = new HashMap<String, ProcessorDescription>();
		
		JsonFactory f = new JsonFactory();
		JsonParser jp;
		
		try {
			jp = f.createParser(is);
			jp.nextToken(); // will return JsonToken.START_OBJECT
			while (jp.nextToken() != END_OBJECT){
				String field = jp.getCurrentName();
				
				if (field.equalsIgnoreCase(KEY_PROCESSORS)){
					// get next token, make sure it is the beginning of an array
					if (jp.nextToken() != START_ARRAY){
						break;
					}
					
					while (jp.nextToken() != END_ARRAY){
						// do the parsing
						if (jp.getCurrentToken() == START_OBJECT){
							ProcessorDescription p = parseProcessorJSON(jp);
							// only add those processor that have a valid ID
							if (p.getId() != null){
								processorMap.put(p.getId(), p);
							}
						}
						
					}
					
				} else {
					if (field.equalsIgnoreCase(KEY_DEFAULTS)){
						// parse defaults
						ProcessorDescription p = parseProcessorJSON(jp);
						p.setId(DEFAULT_PROCESSOR_CONFIG_ID);
						processorMap.put(p.getId(), p);
					}
				}
				
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return processorMap;
	}
	
	/**
	 * Parsing routine for processor objects
	 * 
	 * @param jp
	 * @return
	 * @throws JsonParseException
	 * @throws IOException
	 */
	private static final ProcessorDescription parseProcessorJSON(JsonParser jp) throws JsonParseException, IOException{
		ProcessorDescription p = new ProcessorDescription();
		while (jp.nextToken() != END_OBJECT){
			JsonToken jt = jp.getCurrentToken();
			
			// look for ID and parse it
			if (jp.getCurrentName().equalsIgnoreCase(KEY_ID) && jt == VALUE_STRING){
				p.setId(jp.getValueAsString());
			}
			
			// look for temp workspace and parse it
			if (jp.getCurrentName().equalsIgnoreCase(KEY_TEMPWORKSPACE) && jt == VALUE_STRING){
				p.setTempWorkspace(jp.getValueAsString());
			}
			
			// look for containers and parse them (Value Case)
			if (jp.getCurrentName().equalsIgnoreCase(KEY_SUPPORTED_CONTAINER) && jt == VALUE_STRING){
				p.addContainer(jp.getValueAsString());
			}
			
			// look for containers and parse them (Array Case)
			if (jp.getCurrentName().equalsIgnoreCase(KEY_SUPPORTED_CONTAINER) && jt == START_ARRAY){
				while (jp.nextToken() != END_ARRAY){
					if (jp.getCurrentToken() == VALUE_STRING){
						p.addPlatform(jp.getValueAsString());
					}
				}
			}
			
			// look for platforms and parse them (Array Case)
			if (jp.getCurrentName().equalsIgnoreCase(KEY_AVAILABLE_PLATFORMS) && jt == JsonToken.START_ARRAY){
				while (jp.nextToken() != END_ARRAY){
					if (jp.getCurrentToken() == VALUE_STRING){
						p.addPlatform(jp.getValueAsString());
					}
				}
			}
			
			// look for platforms and parse them (Value Case)
			if (jp.getCurrentName().equalsIgnoreCase(KEY_AVAILABLE_PLATFORMS) && jt == JsonToken.VALUE_STRING){
				p.addPlatform(jp.getValueAsString());
			}
			
			// look for properties and parse them
			if (jp.getCurrentName().equalsIgnoreCase(KEY_PROPERTIES) && jt == JsonToken.START_ARRAY){
				HashMap<String, String> props = new HashMap<String,String>();
				while (jp.nextToken() != END_ARRAY){
					if(jp.getCurrentToken() == FIELD_NAME){
						props.put(jp.getCurrentName(), jp.nextTextValue());
					}
					
				}
				
				p.setProperties(props);
				
			}
		}
			
		return p;
	}
	
	
}
