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

    private static Logger LOGGER = Logger.getLogger(R_RServeProbe.class);

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
