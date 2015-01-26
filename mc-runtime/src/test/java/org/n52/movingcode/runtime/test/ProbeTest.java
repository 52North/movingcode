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
