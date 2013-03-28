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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.MovingCodeRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class FeedReadTest extends GlobalTestConfig {

    private MovingCodeRepositoryManager rm = MovingCodeRepositoryManager.getInstance();

    Logger logger = Logger.getLogger(FeedReadTest.class);

    @Test
    public void queryTUDFeed() {

        try {
            URL url = new URL(GlobalTestConfig.feedURL);
            rm.addRepository(url);
            logger.info("Added Repo: " + GlobalTestConfig.feedURL);

            for (String id : rm.getProcessIDs()) {
                logger.info("\nFound process: " + id);
                MovingCodePackage pack = rm.getPackage(id);

                Assert.assertFalse(pack == null); // make sure it is not null

                AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(pack); // get an
                                                                                                 // empty
                                                                                                 // parameter
                                                                                                 // Map
                if (processor == null) {
                    logger.info("Couldn't get a processor for package " + pack.getPackageIdentifier());
                }

                else {
                	
                	logger.info("Compatible Processors: " + processor.getClass().getName());
                	
                    logger.info("--- Parameters ---");
                    for (IOParameter param : processor.values()) {
                        logger.info("Parameter " + param.getIdentifier().getHarmonizedValue() + ": "
                                + param.getMinMultiplicity() + ".." + param.getMaxMultiplicity());
                        if (param.isMessageIn()) {
                            logger.info("ServiceInputID: " + param.getMessageInputIdentifier());
                        }
                        if (param.isMessageOut()) {
                            logger.info("ServiceOutputID: " + param.getMessageOutputIdentifier());
                        }

                        logger.info("Internal Type: " + param.getType().toString());
                    }
                }

            }

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
