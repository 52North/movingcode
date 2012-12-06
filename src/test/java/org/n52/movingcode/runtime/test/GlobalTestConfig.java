package org.n52.movingcode.runtime.test;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.xmlbeans.XmlException;
import org.n52.movingcode.runtime.ProcessorConfig;

public class GlobalTestConfig {
	static final String PROCESSOR_CONFIG_FILE = "src/test/resources/processors.xml";
	
	static final String feedURL = "http://141.30.100.178/gpfeed/gpfeed.xml";
	
	
	
	public GlobalTestConfig() {
		GlobalTestConfig.setup();
	}

	private static void configureProcessors() throws XmlException, IOException{
		File procConfigFile = new File(PROCESSOR_CONFIG_FILE);
		System.out.println(procConfigFile.getAbsolutePath());
		ProcessorConfig.getInstance().setConfig(procConfigFile);
	}
	
	private static void configureLogger(){
		// Logger stuff
		BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.INFO);
	}
	
	static void setup(){
		configureLogger();
		try {
			configureProcessors();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
