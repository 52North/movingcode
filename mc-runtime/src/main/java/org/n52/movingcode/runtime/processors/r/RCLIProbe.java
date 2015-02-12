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

import java.io.ByteArrayOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.n52.movingcode.runtime.processors.IPlatformComponentProbe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Probes an R runtime environment.
 */
public class RCLIProbe implements IPlatformComponentProbe {

    private static final int TIMEOUT_SECONDS = 10;

    private static final String VERSION_CALL = "config --version";

    private static final Logger LOGGER = LoggerFactory.getLogger(RCLIProbe.class);

    public String probe() {
        // is there an R executable?
        if ( !testExecutable()) {
            return null;
        }

        // return the version string
        return getVersion();
    }

    private static boolean testExecutable() {
        CommandLine commandLine = CommandLine.parse(RCLIProcessor.rExecutable + " " + VERSION_CALL);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        Executor executor = new DefaultExecutor();

        // put a watchdog with a timeout
        ExecuteWatchdog watchdog = new ExecuteWatchdog(new Long(TIMEOUT_SECONDS) * 1000);
        executor.setWatchdog(watchdog);

        try {
            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
            int exitVal = resultHandler.getExitValue();
            if (exitVal != 0) {
                return false;
            }
                return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static String getVersion() {
        try {
            CommandLine commandLine = CommandLine.parse(RCLIProcessor.rExecutable + " " + VERSION_CALL);

            DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
            Executor executor = new DefaultExecutor();

            // put a watchdog with a timeout
            ExecuteWatchdog watchdog = new ExecuteWatchdog(new Long(TIMEOUT_SECONDS) * 1000);
            executor.setWatchdog(watchdog);

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PumpStreamHandler psh = new PumpStreamHandler(os);
            executor.setStreamHandler(psh);

            executor.execute(commandLine, resultHandler);
            resultHandler.waitFor();
            int exitVal = resultHandler.getExitValue();
            if (exitVal != 0) {
                return null;
            }

            String osString = os.toString();

            String versionString = osString.substring(osString.lastIndexOf(": ") + 2);
            versionString = versionString.substring(0, versionString.indexOf('\n'));

            return (versionString);
        }
        catch (Exception e) {
            LOGGER.error("Could not get version string.", e);
            return null;
        }
    }
}
