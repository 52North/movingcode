package org.n52.movingcode.runtime.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.RepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;


public class FeedReadTest extends GlobalTestConfig{
	
	private RepositoryManager rm = RepositoryManager.getInstance();
	
	Logger logger = Logger.getLogger(FeedReadTest.class);
	
	@Test
	public void queryTUDFeed() {
		
		try {
			URL url = new URL(GlobalTestConfig.feedURL);
			rm.addRepository(url);
			logger.info("Added Repo: " + GlobalTestConfig.feedURL);
			
			for (String id : rm.getProcessIDs()){
				logger.info("\nFound process: " + id);
				MovingCodePackage pack = rm.getPackage(id);
				
				Assert.assertFalse(pack == null); // make sure it is not null
				
				
				
				AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(pack); //get an empty parameter Map
				if (processor == null){
					logger.info("Couldn't get a processor for package " + pack.getPackageIdentifier());	
				}
				
				else {
					
					logger.info("--- Parameters ---");
					for (IOParameter param : processor.values()){
						logger.info("Parameter " + param.getIdentifier().getHarmonizedValue() + 
								": " + param.getMinMultiplicity()
								+ ".." + param.getMaxMultiplicity());
						if (param.isMessageIn()){
							logger.info("ServiceInputID: " + param.getMessageInputIdentifier());
						}
						if (param.isMessageOut()){
							logger.info("ServiceOutputID: " + param.getMessageOutputIdentifier());
						}
						
						logger.info("Internal Type: " + param.getType().toString());
					}
				}
					
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
