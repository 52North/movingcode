package org.n52.movingcode.runtime.test;

import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Test;
import org.n52.movingcode.runtime.NewProcessorConfig;

public class JSONConfigTest {
	
	public JSONConfigTest() {
		JSONConfigTest.setup();
	}
	
	@Test
	public void testJSONConfig(){
		String[] pList = NewProcessorConfig.getRegisteredProcessorIDs();
		for (String id : pList){
			System.out.println(id);
			for (String container : NewProcessorConfig.getContaines(id)){
				System.out.println("	" + container);
			}
			HashMap<String, String> props = NewProcessorConfig.getProperties(id);
			for(String key : props.keySet()){
				System.out.println("	" + key + "  -  " + props.get(key));
			}
		}
		
		System.out.println("\nDefault Settings:");
		System.out.println(NewProcessorConfig.getDefaultWorkspace());
		for (String platform : NewProcessorConfig.getDefaultPlatforms()){
			System.out.println("	" + platform);
		}
		
	}
	
	private static void configureLogger(){
		// Logger stuff
		BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.INFO);
	}
	
	static void setup(){
		configureLogger();
	}
}
