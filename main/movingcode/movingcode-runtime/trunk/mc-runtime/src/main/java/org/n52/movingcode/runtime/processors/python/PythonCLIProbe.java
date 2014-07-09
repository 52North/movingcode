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

package org.n52.movingcode.runtime.processors.python;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import org.n52.movingcode.runtime.processors.IPlatformComponentProbe;

/**
 * Probes a python runtime environment.
 * 
 * 
 * http://docs.python.org/library/sys.html
 * 
 * sys.version_info A tuple containing the five components of the version number: major, minor, micro,
 * releaselevel, and serial. All values except releaselevel are integers; the release level is 'alpha',
 * 'beta', 'candidate', or 'final'. The version_info value corresponding to the Python version 2.0 is (2, 0,
 * 0, 'final', 0).
 * 
 * The components can also be accessed by name, so sys.version_info[0] is equivalent to sys.version_info.major
 * and so on.
 * 
 * New in version 2.0. Changed in version 2.7: Added named component attributes
 * 
 * 
 * @author Matthias Mueller
 * 
 */
public class PythonCLIProbe implements IPlatformComponentProbe {

	private static final int timeout_seconds = 10;
	private static final String versionScriptFile = "version.py";

	public String probe() {
		// is there a python executable?
		if ( !testExecutable()) {
			return null;
		}

		// return the version string
		return getVersion();
	}

	public static boolean testExecutable() {
		CommandLine commandLine = CommandLine.parse(PythonCLIProcessor.pythonExecutable + " --version");

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		Executor executor = new DefaultExecutor();

		// put a watchdog with a timeout
		ExecuteWatchdog watchdog = new ExecuteWatchdog(new Long(timeout_seconds) * 1000);
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

	public static String getVersion() {

		try {
			URL scriptURL = PythonCLIProbe.class.getResource(versionScriptFile);
			File sf = new File(scriptURL.toURI());
			String scriptPath = sf.getAbsolutePath();

			CommandLine commandLine = CommandLine.parse(PythonCLIProcessor.pythonExecutable + " " + scriptPath);

			DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
			Executor executor = new DefaultExecutor();

			// put a watchdog with a timeout
			ExecuteWatchdog watchdog = new ExecuteWatchdog(new Long(timeout_seconds) * 1000);
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

			return (os.toString());

		}
		catch (Exception e) {
			return null;
		}
	}
}
