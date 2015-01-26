package org.n52.movingcode.runtime.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.GlobalRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class FeedReadTest extends MCRuntimeTestConfig {

	private GlobalRepositoryManager rm = GlobalRepositoryManager.getInstance();

	@Test
	public void queryTUDFeed() {
		// create string buffer for the test report
		StringBuffer report = new StringBuffer(CR);
		
		try {
			URL url = new URL(MCRuntimeTestConfig.feedURL);
			rm.addRepository(url);
			LOGGER.info("Added Repo: " + MCRuntimeTestConfig.feedURL);

			for (PID pID : rm.getPackageIDs()) {
				report.append("\nFound process: " + pID + CR);
				MovingCodePackage pack = rm.getPackage(pID);

				Assert.assertFalse(pack == null); // make sure it is not null

				AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(pack); // get an
				// empty
				// parameter
				// Map
				if (processor == null) {
					report.append("Couldn't get a processor for package " + pID + CR);
				}

				else {

					report.append("Compatible Processors: " + processor.getClass().getName() + CR);

					report.append("--- Parameters ---" + CR);
					for (IOParameter param : processor.values()) {
						report.append("Parameter " + param.getIdentifier().getHarmonizedValue() + ": "
								+ param.getMinMultiplicity() + ".." + param.getMaxMultiplicity() + CR);
						if (param.isMessageIn()) {
							report.append("ServiceInputID: " + param.getMessageInputIdentifier() + CR);
						}
						if (param.isMessageOut()) {
							report.append("ServiceOutputID: " + param.getMessageOutputIdentifier() + CR);
						}

						report.append("Internal Type: " + param.getType().toString() + CR);
					}
				}

			}
			
			// show report
			LOGGER.info(report.toString());

		}
		catch (MalformedURLException e) {
			LOGGER.info("Could not read test feed from URL " + feedURL + CR + "Please check if this feed is indeed up and running.");
		}
	}
}
