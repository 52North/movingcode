package org.n52.movingcode.runtime.test;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;


public class ValidatePackage {

	@Test
	public void loadAndValidateGoodPackage() {

		// Arrange
		File packageFile = new File("src/test/resources/testpackages/ztransform.zip");
		System.out.println(packageFile.getAbsolutePath());

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
	}
	
	@Test
	public void loadAndValidatePyCopyPackage() {

		// Arrange
		File packageFile = new File("src/test/resources/testpackages/py_copy.zip");
		System.out.println(packageFile.getAbsolutePath());

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
	}
	

	@Test
	public void loadAndValidateBadPackage() {

		// Arrange
		File packageFile = new File("src/test/resources/testpackages/bad_package_structure.zip");
		System.out.println(packageFile.getAbsolutePath());

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

		// Assert
		Assert.assertFalse(mcPackage.isValid());
	}

}
