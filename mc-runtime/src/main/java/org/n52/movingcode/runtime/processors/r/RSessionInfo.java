package org.n52.movingcode.runtime.processors.r;

import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RSessionInfo {

    public static String getVersion(RConnection rCon) throws RserveException, REXPMismatchException {
        return getConsoleOutput(rCon, "R.version[[\"version.string\"]]");
    }

    public String getSessionInfo(RConnection rCon) throws RserveException, REXPMismatchException {
        return getConsoleOutput(rCon, "sessionInfo()");
    }

    private static String getConsoleOutput(RConnection rCon, String cmd) throws RserveException, REXPMismatchException {
        return rCon.eval("paste(capture.output(print(" + cmd + ")),collapse='\\n')").asString();
    }
}
