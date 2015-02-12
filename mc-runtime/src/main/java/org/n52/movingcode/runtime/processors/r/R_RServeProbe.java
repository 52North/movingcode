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
package org.n52.movingcode.runtime.processors.r;

import org.n52.movingcode.runtime.processors.IPlatformComponentProbe;
import org.n52.movingcode.runtime.processors.r.util.RConnector;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Probes an R runtime environment.
 */
public class R_RServeProbe implements IPlatformComponentProbe {

    private static final Logger LOGGER = LoggerFactory.getLogger(R_RServeProbe.class);

    private RConnector c = new RConnector();

    private String host = "localhost";

    private int port = 6311;

    public String probe() {
        if ( !testExecutable()) {
            return null;
        }

        return getVersion();
    }

    private boolean testExecutable() {
        RConnection rCon = null;
        try {
            rCon = this.c.getNewConnection(true, this.host, this.port);

            if (rCon == null) {
                LOGGER.info("Connection is null.");
                return false;
            }

            boolean connected = rCon.isConnected();
            // int serverVersion = rCon.getServerVersion();

            REXP eval = rCon.eval("1+1");
            String two = eval.asString();

            return (connected && two.startsWith("2"));
        }
        catch (RserveException e) {
            LOGGER.error("Error testing executable.", e);
            return false;
        }
        catch (REXPMismatchException e) {
            LOGGER.error("Error testing executable.", e);
            return false;
        }
        catch (Exception e) {
            LOGGER.error("Error testing executable.", e);
            return false;
        }
        finally {
            if (rCon != null)
                rCon.close();
        }
    }

    private String getVersion() {
        String version = null;
        try {
            RConnection rCon = this.c.getNewConnection(true, this.host, this.port);
            version = RSessionInfo.getVersion(rCon);
        }
        catch (RserveException e) {
            LOGGER.error("Error getting version.", e);
            return e.getMessage();
        }
        catch (REXPMismatchException e) {
            LOGGER.error("Error getting version.", e);
            return e.getMessage();
        }

        return version;
    }
}
