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
