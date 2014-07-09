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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import static org.junit.Assert.*;
import org.junit.Test;
import org.n52.movingcode.runtime.GlobalRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.IOParameterMap;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public class MovingCodeRepositoryTests extends MCRuntimeTestConfig {

	private static final String zTransformFunctionID = "de.tu-dresden.geo.gis.algorithms.raster.ztransform";
	private static final String packageFolderName = "src/test/resources/testpackages";

	private static final String workspace = packageFolderName + File.separator + "ztransform/ztransform";
	private static final String descriptionXML = packageFolderName + File.separator + "ztransform/packagedescription.xml";

	@Test
	public void localZipRepoTest() {
		
		// Arrange
		File packageFolder = new File(packageFolderName);
		logger.info(packageFolder.getAbsolutePath());

		// Act
		IMovingCodeRepository mcRep = IMovingCodeRepository.Factory.createFromZipFilesFolder(packageFolder);

		// Assert
		assertTrue(mcRep.providesFunction(zTransformFunctionID));
		logger.debug("Retrieving process: " + zTransformFunctionID);
		
		MovingCodePackage pack = mcRep.getPackageByFunction(zTransformFunctionID)[0]; // get the test package
		if (pack == null){
			logger.warn("Could not find a package for process: " + zTransformFunctionID);
		} else {
			logger.debug("Found package: " + pack.getVersionedPackageId().toString() + " supplying process " + zTransformFunctionID);
		}
		
		assertFalse(pack == null); // make sure it is not null

		IOParameterMap paramsMap = ProcessorFactory.getInstance().newProcessor(pack); // get an empty
		// parameter Map
		assertFalse(paramsMap == null); // make sure it is not null
		
		// create string buffer for the package report
		StringBuffer report = new StringBuffer(CR);
		report.append("--- Parameters ---" + CR);
		for (IOParameter param : paramsMap.values()) {
			report.append(
					"Parameter "
					+ param.getIdentifier().getHarmonizedValue()
					+ ": "
					+ param.getMinMultiplicity()
					+ ".."
					+ param.getMaxMultiplicity()
					+ CR
			);
			
			if (param.isMessageIn()) {
				report.append("ServiceInputID: " + param.getMessageInputIdentifier() + CR);
			}
			if (param.isMessageOut()) {
				report.append("ServiceOutputID: " + param.getMessageOutputIdentifier() + CR);
			}
			
			report.append("Internal Type: " + param.getType().toString() + CR);
		}
		
		// show report
		logger.info(report.toString());
	}
	
	
	@Test
	public void localPlainRepoTest() {
		// create string buffer for the package report
		StringBuffer report = new StringBuffer(CR);
		
		// Arrange
		File packageFolder = new File(packageFolderName);
		logger.info("Adding plain folder repo: " + packageFolder.getAbsolutePath());

		// Act
		IMovingCodeRepository mcRep = IMovingCodeRepository.Factory.createFromPlainFolder(packageFolder);

		// Assert
		assertTrue(mcRep.providesFunction(zTransformFunctionID));
		report.append("Information for package: " + zTransformFunctionID + CR);
		
		MovingCodePackage pack = mcRep.getPackageByFunction(zTransformFunctionID)[0]; // get the test package
		assertFalse(pack == null); // make sure it is not null
		report.append("Package Timestamp is: " + pack.getTimestamp() + CR);

		IOParameterMap paramsMap = ProcessorFactory.getInstance().newProcessor(pack); // get an empty parameter Map
		assertFalse(paramsMap == null); // make sure it is not null

		report.append("--- Parameters ---" + CR);
		for (IOParameter param : paramsMap.values()) {
			report.append(
					"Parameter "
					+ param.getIdentifier().getHarmonizedValue()
					+ ": "
					+ param.getMinMultiplicity()
					+ ".."
					+ param.getMaxMultiplicity()
					+ CR
			);
			
			if (param.isMessageIn()) {
				report.append("ServiceInputID: " + param.getMessageInputIdentifier() + CR);
			}
			if (param.isMessageOut()) {
				report.append("ServiceOutputID: " + param.getMessageOutputIdentifier() + CR);
			}
			
			report.append("Internal Type: " + param.getType().toString() + CR);
		}
		
		// show report
		logger.info(report.toString());
	}
	
	@Test
	public void remoteFeedRepoTest() {
		// create string buffer for the test report
		StringBuffer report = new StringBuffer(CR);
		
		try {
			URL url = new URL(MCRuntimeTestConfig.feedURL);
			IMovingCodeRepository mcRep = IMovingCodeRepository.Factory.createFromRemoteFeed(url);
			logger.info("Added Repo: " + MCRuntimeTestConfig.feedURL);

			for (PID pID : mcRep.getPackageIDs()) {
				report.append("\nFound process: " + pID + CR);
				MovingCodePackage pack = mcRep.getPackage(pID);

				assertFalse(pack == null); // make sure it is not null

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
	
	@Test
	public void cachedRemoteFeedRepoTest() {
		// create string buffer for the test report
		StringBuffer report = new StringBuffer(CR);
		
		try {
			URL url = new URL(MCRuntimeTestConfig.feedURL);
			File cacheWS = newTempDir();
			IMovingCodeRepository mcRep = IMovingCodeRepository.Factory.createCachedRemoteRepository(url, cacheWS);
			logger.info("Added Repo: " + MCRuntimeTestConfig.feedURL);
			logger.info("Using Cache directory: " + cacheWS.getAbsolutePath());

			for (PID pID : mcRep.getPackageIDs()) {
				report.append("\nFound process: " + pID + CR);
				MovingCodePackage pack = mcRep.getPackage(pID);

				assertFalse(pack == null); // make sure it is not null

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

	@Test
	public void testRepoManager() {

		// Arrange
		File packageFolder = new File(packageFolderName);
		logger.info(packageFolder.getAbsolutePath());

		// Act
		GlobalRepositoryManager repoMan = GlobalRepositoryManager.getInstance();
		repoMan.addLocalPlainRepository(packageFolderName);

		// Assert
		assertTrue(repoMan.providesFunction(zTransformFunctionID));
	}

	@Test
	public void testPackageZipping() throws Exception {

		// Arrange
		File wsFolder = new File(workspace);
		PackageDescriptionDocument doc = PackageDescriptionDocument.Factory.parse(new File(descriptionXML));

		// Act
		// create a temp folder
		File tempFolder = new File(FileUtils.getTempDirectory(), AUID.randomAUID());
		tempFolder.mkdir();
		tempFolder.deleteOnExit();
		
		// create a new zipped package
		File tempFile = new File(tempFolder + File.separator + AUID.randomAUID() + ".zip");
		String packageIdentifier = tempFile.getPath();
		logger.info(packageIdentifier);

		MovingCodePackage mcp = new MovingCodePackage(wsFolder, doc);

		mcp.dumpPackage(tempFile);
		// close package and reopen
		mcp = null;
		mcp = new MovingCodePackage(tempFile);

		// Assert
		assertTrue(mcp.isValid());
	}
	
	private static final File newTempDir(){
		File tmpDir = FileUtils.getTempDirectory();
		File newTmpFolder = new File(tmpDir, AUID.randomAUID());
		newTmpFolder.mkdir();
		newTmpFolder.deleteOnExit();
		return newTmpFolder;
	}
}
