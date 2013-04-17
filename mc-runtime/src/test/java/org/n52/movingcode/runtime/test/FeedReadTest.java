/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.movingcode.runtime.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.GlobalRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
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
			logger.info("Added Repo: " + MCRuntimeTestConfig.feedURL);

			for (String pID : rm.getPackageIDs()) {
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
			logger.info(report.toString());

		}
		catch (MalformedURLException e) {
			logger.info("Could not read test feed from URL " + feedURL + CR + "Please check if this feed is indeed up and running.");
		}
	}
}
