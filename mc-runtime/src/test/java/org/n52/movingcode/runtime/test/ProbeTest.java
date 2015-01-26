package org.n52.movingcode.runtime.test;

import org.junit.Test;
import org.n52.movingcode.runtime.processors.python.PythonCLIProbe;
import org.n52.movingcode.runtime.processors.r.RCLIProbe;
import org.n52.movingcode.runtime.processors.r.R_RServeProbe;

public class ProbeTest extends MCRuntimeTestConfig {

    @Test
    public void probePythonCLI() {
        PythonCLIProbe p = new PythonCLIProbe();
        String retVal = p.probe();

        LOGGER.info("Python Probe reported: " + retVal);
    }

    @Test
    public void probeRCLI() {
        RCLIProbe p = new RCLIProbe();
        String retVal = p.probe();

        LOGGER.info("R CLI Probe reported: " + retVal);
    }

    @Test
    public void probeR_RServe() {
        R_RServeProbe p = new R_RServeProbe();
        String retVal = p.probe();

        LOGGER.info("R RServe Probe reported: " + retVal);
    }
}
