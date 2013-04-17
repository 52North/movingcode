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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class ExecutableJARTest extends MCRuntimeTestConfig {

	private static final String packageFileName = "src/test/resources/testpackages/jar_copy.zip";
	private static final String dataFileName = "src/test/resources/testpackages/jar_copy/testdata/test_null.tif";
	private static final String inID = "CALL";
	private static final int inPosition = 1;
	private static final String outID = "RESPONSE";
	private static final int outPosition = 2;
	private static final String mimeType = "application/geotiff";

	@Test
	public void getClasspath() {
		logger.info(System.getProperties().getProperty("java.class.path", null));
	}

	@Test
	public void loadJarCopyPackage() {

		// Arrange
		File packageFile = new File(packageFileName);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
		Assert.assertTrue(new File(dataFileName).exists());
		Assert.assertTrue(ProcessorFactory.getInstance().newProcessor(mcPackage) != null);
	}

	@Test
	public void executeJarCopyPackage() throws IllegalArgumentException, FileNotFoundException {

		// Arrange
		File packageFile = new File(packageFileName);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);
		// get a processor
		AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(mcPackage); 

		// add input
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(inPosition),
				new MediaData(new FileInputStream(new File(dataFileName)), mimeType)));
		// add output declaration
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(outPosition), new MediaData(null, mimeType)));

		// Assert
		Assert.assertTrue(processor.isFeasible());

		boolean succExecute = false;
		try {
			processor.execute(0);
			succExecute = true;
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

	/**
	 * Same test as above but parameters are set by their public (functional) ID
	 * 
	 * @throws IllegalArgumentException
	 * @throws FileNotFoundException
	 * @throws XmlException
	 */
	@Test
	public void executeJarCopyPackage2() throws IllegalArgumentException, FileNotFoundException {

		// Arrange
		File packageFile = new File(packageFileName);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);
		AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(mcPackage); // get a
		// processor

		// add input
		Assert.assertTrue(processor.addData(inID, new MediaData(new FileInputStream(new File(dataFileName)), mimeType)));
		// add output declaration
		Assert.assertTrue(processor.addData(outID, new MediaData(null, mimeType)));

		// Assert
		Assert.assertTrue(processor.isFeasible());

		boolean succExecute = false;
		try {
			processor.execute(0);
			succExecute = true;
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
