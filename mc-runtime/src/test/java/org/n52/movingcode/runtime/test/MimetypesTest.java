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
