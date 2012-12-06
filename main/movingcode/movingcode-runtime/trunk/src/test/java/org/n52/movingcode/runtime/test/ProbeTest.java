package org.n52.movingcode.runtime.test;

import org.apache.log4j.Logger;
import org.junit.Test;

import org.n52.movingcode.runtime.processors.python.PythonCLIProbe;


public class ProbeTest extends GlobalTestConfig{
	
	Logger logger = Logger.getLogger(ProbeTest.class);
	
	@Test
	public void probePythonCLI(){
		
		String retVal = new PythonCLIProbe().probe();
		
		logger.info("Python Probe reported: " + retVal);
		
	}
}
