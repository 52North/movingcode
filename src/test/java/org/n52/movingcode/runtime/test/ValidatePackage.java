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
		String packageIdentifier = packageFile.getAbsolutePath();
		System.out.println(packageIdentifier);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile, packageIdentifier);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
	}
	
	@Test
	public void loadAndValidatePyCopyPackage() {

		// Arrange
		File packageFile = new File("src/test/resources/testpackages/py_copy.zip");
		String packageIdentifier = packageFile.getAbsolutePath();
		System.out.println(packageIdentifier);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile, packageIdentifier);

		// Assert
		Assert.assertTrue(mcPackage.isValid());
	}
	

	@Test
	public void loadAndValidateBadPackage() {

		// Arrange
		File packageFile = new File("src/test/resources/testpackages/bad_package_structure.zip");
		String packageIdentifier = packageFile.getAbsolutePath();
		System.out.println(packageIdentifier);

		// Act
		MovingCodePackage mcPackage = new MovingCodePackage(packageFile, packageIdentifier);

		// Assert
		Assert.assertFalse(mcPackage.isValid());
	}

}
