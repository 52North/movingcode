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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class PythonGecaTest extends MCRuntimeTestConfig {

	private static final String packageFileName = "src/test/resources/testpackages/py_geca.zip";
	private static final String mimeType = "application/pdf";

	@Before
	public void loadPyCopyPackage() {

		// Arrange
		File packageFile = new File(packageFileName);
		String packageIdentifier = packageFile.getAbsolutePath();
		logger.info(packageIdentifier);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile, packageIdentifier);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
		Assert.assertTrue(ProcessorFactory.getInstance().newProcessor(mcPackage) != null);
	}

	@Test
	public void executePyCopyPackage() throws IllegalArgumentException, FileNotFoundException {

		// Arrange
		File packageFile = new File(packageFileName);
		String packageIdentifier = packageFile.getAbsolutePath();
		logger.info(packageIdentifier);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile, packageIdentifier);
		AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(mcPackage);

		// add input
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(1), "MIP_NL__2P"));
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(2), "GOM_NL__2P"));
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(3), "2"));
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(4), "1000"));
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(5), "nearest_neighbour"));
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(6), "satellite_b"));
		// add output declaration
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(7), new MediaData(null, mimeType)));

		// Assert
		Assert.assertTrue(processor.isFeasible());

		try {
			processor.execute(0);
			logger.info(processor.pollLastEntry());
		}
		catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


}
