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

package org.n52.movingcode.runtime.processors.r;

import java.io.File;
import java.io.IOException;

import org.rosuda.REngine.REXP;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * Connection handler for RServer calls.
 * 
 * 
 * @author Stefan Wiemann, Matthias Mueller
 * 
 */
public class RServerController {

    public static final String rDataSuffix = ".RData";

    // public static final String FILE = "test.tif";

    /**
     * Static execution method for RScripts
     * 
     * TODO: refactor to really take arguments
     * 
     * @param args
     * @return
     */
    public static boolean execute(String workspacePath,
                                  String function,
                                  String scriptPath,
                                  String[] parameters,
                                  String[] libraries) {
        try {

            // determine RDATA workspace
            String rData = scriptPath + rDataSuffix;

            // get sensor observations as vector (R format: 'c('','',...)')
            // String stations =
            // "c('DESN017','DESN045','DESN079','DESN076','DESN004','DESN075','DESN061','DESN012','DESN047','DESN060','DESN074','DESN019','DESN050','DESN025','DESN051','DESN024','DESN014','DESN049','DESN020','DESN011','DESN077','DESN085','DESN059','DESN006','DESN084','DESN083')";
            // String measurements =
            // "c(27.33,26.77,21.50,20.44,25.45,28.06,34.42,25.65,25.44,26.25,18.70,27.85,25.03,37.09,23.91,21.74,33.35,14.14,32.14,25.52,38.94,27.36,23.38,31.49,33.39,34.99)";

            RConnection conn = getRServeConnection("localhost", workspacePath);

            // check if we have a connection
            if (conn == null) {
                return false;
            }

            // try loading RData Workspace
            if ( (new File(rData)).exists()) {
                if ( !loadRDataWorkspace(conn, rData)) {
                    // TODO are we too strict here?
                    return false;
                }
            }

            // load required libraries
            if ( !loadLibraries(conn, new String[] {"raster"})) {
                return false;
            }

            // in case there is no function call but just the plain script
            if (function == null) {
                // TODO
            }

            // execute void function
            if ( !executeVoidFunction(conn, function, parameters)) {
                return false;
            }

            // close connection
            close(conn);

            // return success if we get up to here
            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Executes an R function (return: void)
     * 
     * @param function
     *        name of the R function
     * @param Parameters
     *        array of parameters required by the function
     */
    public static boolean executeVoidFunction(RConnection conn, String function, String[] parameters) {
        // create request
        try {
            String request = "try(" + function + "(";
            if (parameters.length != 0) {
                for (String parameter : parameters) {
                    request += parameter + ",";
                }
                // remove last ","
                request = request.substring(0, request.length() - 1);
                request += "))";
            }
            // execute function
            REXP xp = conn.parseAndEval(request);
            if (xp.inherits("try-error")) {
                close(conn);
                throw new IOException("failed to execute function '" + function + "'; \nrequest: " + request
                        + "; \nError: " + xp.asString());
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }

    /**
     * Executes an R function (return: String)
     * 
     * @param function
     *        name of the R function
     * @param parameters
     *        array of parameters required by the function
     * 
     *        TODO: make more generic - not just for Strings ...
     */
    public static String executeStringFunction(RConnection conn, String function, String[] parameters) {

        try {
            // create request
            String request = "try(" + function + "(";
            for (String parameter : parameters) {
                request += parameter + ",";
            }
            // remove last ","
            request = request.substring(0, request.length() - 1);
            request += "))";
            // execute function
            REXP xp = conn.parseAndEval(request);
            if (xp.inherits("try-error")) {
                close(conn);
                throw new IOException("failed to execute function '" + function + "'; \nrequest: " + request
                        + "; \nError: " + xp.asString());
            }
            String retval = xp.asString();
            return retval;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Get an R connection
     * 
     * @param host
     * @param workspace
     * @return {@link RConnection} rConnection - a connection to RServer
     */
    private static RConnection getRServeConnection(String host, String workspace) {

        try {
            // establish R connection
            RConnection conn = new RConnection(host);
            // check connection
            if (conn == null || !conn.isConnected())
                throw new IOException("Failed to establish RServe connection");

            // set workspace
            if (workspace != null) {
                REXP xp = conn.parseAndEval("try(setwd('" + workspace.replace("\\", "\\\\") + "'))");
                if (xp.inherits("try-error"))
                    throw new IOException("Failed to load R workspace; \nError: " + xp.asString());
            }
            return conn;
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Load RData workspace
     * 
     * @param RData
     *        Name of the RData file, relative to R workspace
     */
    private static boolean loadRDataWorkspace(RConnection conn, String rData) throws IOException {
        // load specified RData file from workspace
        try {
            REXP xp = conn.parseAndEval("try(load(paste(getwd(),'" + rData + "',sep='/')))");
            if (xp.inherits("try-error")) {
                throw new IOException("failed to load RData workspace; \nError: " + xp.toString());
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }

    }

    /**
     * Load required R libraries
     * 
     * @param Libraries
     *        Array of libraries to be loaded
     */
    private static boolean loadLibraries(RConnection conn, String[] libraries) {
        // load specified libraries
        try {
            for (String library : libraries) {
                if (conn.parseAndEval("suppressWarnings(require('" + library + "',quietly=TRUE))").asInteger() == 0) {
                    throw new IOException("failed to load library '" + library + "'");
                }
            }
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Close R connection
     */
    private static void close(RConnection conn) {
        try {
            REXP xp = conn.parseAndEval("gc()");
            if (xp.inherits("try-error")) {
                throw new IOException("failed to load R workspace; \nError: " + xp.asString());
            }
            conn.close();
        }
        catch (Exception e) {
            // do nothing
            // should not occur
            // TODO: ...
        }
    }

}
