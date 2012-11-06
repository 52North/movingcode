package org.n52.movingcode.runtime.test;

import org.junit.Test;

import org.n52.movingcode.runtime.processors.python.PythonCLIProbe;


public class ProbeTest {
	
	@Test
	public void probePythonCLI(){
		
		String retVal = new PythonCLIProbe().probe();
		
		System.out.println(retVal);
		
	}
}
