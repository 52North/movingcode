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

import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

public class ValidatePackageTest extends MCRuntimeTestConfig {

    @Test
    public void loadAndValidateGoodPackage() {

        // Arrange
        File packageFile = new File("src/test/resources/testpackages/ztransform.zip");
        String packageIdentifier = packageFile.getAbsolutePath();
        LOGGER.info(packageIdentifier);

        // Act
        MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

        // Assert
        Assert.assertTrue(mcPackage.isValid());
    }

    @Test
    public void loadAndValidatePyCopyPackage() {

        // Arrange
        File packageFile = new File("src/test/resources/testpackages/py_copy.zip");

        // Act
        MovingCodePackage mcPackage = new MovingCodePackage(packageFile);

        // Assert
        Assert.assertTrue(mcPackage.isValid());
    }

//    @Test
//    public void loadAndValidateBadPackage() {
//
//        // Arrange
//        File packageFile = new File("src/test/resources/testpackages/bad_package_structure.zip");
//
//        // Act
//        MovingCodePackage mcPackage = new MovingCodePackage(packageFile);
//
//        // Assert
//        Assert.assertTrue(!mcPackage.isValid());
//    }

}
