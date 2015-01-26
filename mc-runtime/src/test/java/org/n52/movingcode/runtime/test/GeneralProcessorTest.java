package org.n52.movingcode.runtime.test;

import org.junit.Test;

import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class GeneralProcessorTest extends MCRuntimeTestConfig {

	@Test
	public void loadSupportedProcessors() {
		
		StringBuffer report = new StringBuffer(CR);
		
		String[] processors = ProcessorFactory.getInstance().registeredProcessors();
		for (String processor : processors) {
			report.append("Processor found: " + processor.toString() + CR);
		}
		LOGGER.info(report.toString());
	}

}
