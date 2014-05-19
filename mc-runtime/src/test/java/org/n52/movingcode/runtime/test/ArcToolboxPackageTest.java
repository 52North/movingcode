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

import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

public class ArcToolboxPackageTest extends MCRuntimeTestConfig{

	private static final String packageFileName = "src/test/resources/testpackages/arcgis_ndvi_tbx.zip";

	private static final String nir_data = "src/test/resources/testpackages/arctoolbox_ndvi/testdata/nir.tif";
	private static final String nir_id = "NIR";
	private static final int nir_position = 1;

	private static final String red_data = "src/test/resources/testpackages/arctoolbox_ndvi/testdata/red.tif";
	private static final String red_id = "RED";
	private static final int red_position = 2;

	private static final String ndvi_id = "NDVI";
	private static final int ndvi_position = 3;
	private static final String mimeType = "application/geotiff";

	@Test
	public void loadNDVIPackage() {

		// Arrange
		File packageFile = new File(packageFileName);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
		Assert.assertTrue(new File(nir_data).exists());
		Assert.assertTrue(new File(red_data).exists());
		Assert.assertTrue(ProcessorFactory.getInstance().newProcessor(mcPackage) != null);
	}

	@Test
	public void executeNDVIPackage() throws IllegalArgumentException, FileNotFoundException {

		// Arrange
		File packageFile = new File(packageFileName);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);
		AbstractProcessor processor = ProcessorFactory.getInstance().newProcessor(mcPackage); // get a
		// processor

		// add NIR
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(nir_position),
				new MediaData(new FileInputStream(new File(nir_data)), mimeType)));
		// add RED
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(red_position),
				new MediaData(new FileInputStream(new File(red_data)), mimeType)));
		// add output (NDVI) declaration
		Assert.assertTrue(processor.addData(new IIOParameter.ParameterID(ndvi_position), new MediaData(null, mimeType)));

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

		Assert.assertTrue(succExecute);

	}

}
