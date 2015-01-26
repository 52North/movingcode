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
import java.io.IOException;

import org.junit.Test;

import org.n52.movingcode.runtime.iodata.MimeTypeDatabase;

public class MimetypesTest extends MCRuntimeTestConfig {

    private static final String MT_NETCDF = "application/netcdf";
    private static final String MT_XNETCDF = "application/x-netcdf";

    @Test
    public void loadMimeFile() {

        // Arrange
        File packageFile = new File("src/test/resources/mimetypes/netcdf.types");
        MimeTypeDatabase rmf = null;
        try {
            rmf = new MimeTypeDatabase(packageFile.getAbsolutePath());
        }
        catch (IOException e) {
            LOGGER.error("Could not read MimeTypeDatabase from " + packageFile.getAbsolutePath());
        }

        // Act
        String ext = rmf.getExtensionStrings(MT_NETCDF)[0];
        LOGGER.info("File for " + MT_NETCDF + " is: " + ext);
    }

}
