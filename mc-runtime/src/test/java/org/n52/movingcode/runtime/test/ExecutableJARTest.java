/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.iodata.ParameterID;
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
		LOGGER.info(System.getProperties().getProperty("java.class.path", null));
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
		Assert.assertTrue(processor.addData(new ParameterID(inPosition),
				new MediaData(new FileInputStream(new File(dataFileName)), mimeType)));
		// add output declaration
		Assert.assertTrue(processor.addData(new ParameterID(outPosition), new MediaData(null, mimeType)));

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
