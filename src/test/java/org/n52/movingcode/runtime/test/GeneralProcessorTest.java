package org.n52.movingcode.runtime.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import org.n52.movingcode.runtime.processors.ProcessorFactory;


public class GeneralProcessorTest extends GlobalTestConfig{

	Logger logger = Logger.getLogger(GeneralProcessorTest.class);
	
	@Test
	public void loadSupportedProcessors() {
		
		// Arrange
		
		String[] processors = ProcessorFactory.getInstance().registeredProcessors();
		for (String processor : processors){
			logger.info("Processor found: " + processor.toString());
		}
	}
	
}
