package org.n52.movingcode.runtime.test;

import org.junit.Test;
import org.n52.movingcode.runtime.processors.AUID;

/**
 * Tests for {@link AUID}
 * 
 * @author Matthias Mueller
 */
public class AUIDTest extends MCRuntimeTestConfig {
	
	@Test
	public void generateAUIDs(){
		logger.info("Genrating some AUIDs ...");
		StringBuffer report = new StringBuffer(CR);
		for (int i=1; i<=100; i++){
			report.append(AUID.randomAUID() + ", ");
			if (i%10 == 0){
				report.append(CR);
			}
		}
		logger.info(report.toString());
	}
}
