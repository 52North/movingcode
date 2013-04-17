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
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.GlobalRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.IOParameterMap;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public class MovingCodeRepositoryTests extends MCRuntimeTestConfig {

	private static final String zTransformFunctionID = "de.tu-dresden.geo.gis.algorithms.raster.ztransform";
	private static final String packageFolderName = "src/test/resources/testpackages";

	private static final String workspace = packageFolderName + File.separator + "ztransform/ztransform";
	private static final String descriptionXML = packageFolderName + File.separator
	+ "ztransform/packagedescription.xml";

	private static final String tempFolder = "C:\\tmp";

	@Test
	public void testDirectoryRepository() {
		// create string buffer for the package report
		StringBuffer report = new StringBuffer(CR);
		
		// Arrange
		File packageFolder = new File(packageFolderName);
		logger.info(packageFolder.getAbsolutePath());

		// Act
		IMovingCodeRepository mcRep = IMovingCodeRepository.Factory.createFromZipFilesFolder(packageFolder);

		// Assert
		Assert.assertTrue(mcRep.providesFunction(zTransformFunctionID));
		report.append("Information for package: " + zTransformFunctionID + CR);
		
		MovingCodePackage pack = mcRep.getPackageByFunction(zTransformFunctionID)[0]; // get the test package
		Assert.assertFalse(pack == null); // make sure it is not null
		report.append("Package Timestamp is: " + pack.getTimestamp() + CR);

		IOParameterMap paramsMap = ProcessorFactory.getInstance().newProcessor(pack); // get an empty
		// parameter Map
		Assert.assertFalse(paramsMap == null); // make sure it is not null

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
	public void testRepoManager() {

		// Arrange
		File packageFolder = new File(packageFolderName);
		logger.info(packageFolder.getAbsolutePath());

		// Act
		GlobalRepositoryManager repoMan = GlobalRepositoryManager.getInstance();
		repoMan.addLocalPlainRepository(packageFolderName);

		// Assert
		Assert.assertTrue(repoMan.providesFunction(zTransformFunctionID));
	}

	@Test
	public void testPackageZipping() throws Exception {

		// Arrange
		File wsFolder = new File(workspace);
		PackageDescriptionDocument doc = PackageDescriptionDocument.Factory.parse(new File(descriptionXML));

		// Act
		// create a new zipped package
		File tempFile = new File(tempFolder + File.separator + UUID.randomUUID().toString() + ".zip");
		String packageIdentifier = tempFile.getPath();
		logger.info(packageIdentifier);

		MovingCodePackage mcp = new MovingCodePackage(wsFolder, doc, null);

		mcp.dumpPackage(tempFile);
		// close package and reopen
		mcp = null;
		mcp = new MovingCodePackage(tempFile);

		// Assert
		Assert.assertTrue(mcp.isValid());
	}
}
