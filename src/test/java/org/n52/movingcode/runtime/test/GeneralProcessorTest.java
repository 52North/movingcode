package org.n52.movingcode.runtime.test;

import org.junit.Test;

import org.n52.movingcode.runtime.processors.ProcessorFactory;


public class GeneralProcessorTest {
	
	@Test
	public void loadSupportedProcessors() {
		String[] processors = ProcessorFactory.getInstance().registeredProcessors();
		for (String processor : processors){
			System.out.println(processor.toString());
		}
	}
	
}
