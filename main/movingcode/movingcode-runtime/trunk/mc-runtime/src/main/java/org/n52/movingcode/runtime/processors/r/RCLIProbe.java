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

import java.io.ByteArrayOutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.processors.IPlatformComponentProbe;

/**
 * Probes an R runtime environment.
 */
public class RCLIProbe implements IPlatformComponentProbe {

    private static final int TIMEOUT_SECONDS = 10;

    private static final String VERSION_CALL = "config --version";

    private static Logger log = Logger.getLogger(RCLIProbe.class);

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
            log.error("Could not get version string.", e);
            return null;
        }
    }
}
