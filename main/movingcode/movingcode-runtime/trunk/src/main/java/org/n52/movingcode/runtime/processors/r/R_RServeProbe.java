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

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.processors.IPlatformComponentProbe;
import org.n52.movingcode.runtime.processors.r.util.RConnector;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 * Probes an R runtime environment.
 */
public class R_RServeProbe implements IPlatformComponentProbe {

    private static final int TIMEOUT_SECONDS = 10;

    private static Logger log = Logger.getLogger(R_RServeProbe.class);

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
                log.info("Connection is null.");
                return false;
            }

            boolean connected = rCon.isConnected();
            // int serverVersion = rCon.getServerVersion();

            REXP eval = rCon.eval("1+1");
            String two = eval.asString();

            return (connected && two.startsWith("2"));
        }
        catch (RserveException e) {
            log.error("Error testing executable.", e);
            return false;
        }
        catch (REXPMismatchException e) {
            log.error("Error testing executable.", e);
            return false;
        }
        catch (Exception e) {
            log.error("Error testing executable.", e);
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
            log.error("Error getting version.", e);
            return e.getMessage();
        }
        catch (REXPMismatchException e) {
            log.error("Error getting version.", e);
            return e.getMessage();
        }

        return version;
    }
}
