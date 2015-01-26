package org.n52.movingcode.runtime.processors;

import org.junit.Test;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.test.MCRuntimeTestConfig;

/**
 * Tests for {@link AUID}
 * 
 * @author Matthias Mueller
 */
public class AUIDTest extends MCRuntimeTestConfig {
	
	@Test
	public void generateAUIDs(){
		LOGGER.info("Genrating some AUIDs ...");
		StringBuffer report = new StringBuffer(CR);
		for (int i=1; i<=100; i++){
			report.append(AUID.randomAUID() + ", ");
			if (i%10 == 0){
				report.append(CR);
			}
		}
		LOGGER.info(report.toString());
	}
}
