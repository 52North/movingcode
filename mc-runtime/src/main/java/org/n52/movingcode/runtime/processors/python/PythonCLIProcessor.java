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
package org.n52.movingcode.runtime.processors.python;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.iodata.MimeTypeDatabase;
import org.n52.movingcode.runtime.iodata.IIOParameter.Direction;
import org.n52.movingcode.runtime.iodata.ParameterID;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PythonCLIProcessor extends AbstractProcessor {

	private File clonedWorkspace;
	private static MimeTypeDatabase mimeRegistry = getMimeRegistry();
	private static final String mimeTypeFile = "mime.types";
	private static final long serialVersionUID = -5623433596146815129L;

	protected static final String pythonExecutable = "python";

	private static final Logger LOGGER = LoggerFactory.getLogger(PythonCLIProcessor.class);

	/**
	 * A sorted Map containing all executionValues in an ascending order
	 */
	private SortedMap<ParameterID, String[]> executionValues = new TreeMap<ParameterID, String[]>();

	public PythonCLIProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
		super(scratchworkspace, mcp, properties);
	}

	private boolean init() {
		// 1. check workspace constraints
		// CLI: NONE
		// 2. create unique subdirectory

		File tmpWorkspace = new File(this.scratchWorkspace.getAbsolutePath() + File.separator + AUID.randomAUID());

		if ( !tmpWorkspace.mkdir()) {
			LOGGER.error("Could not create instance workspace!");
			return false;
		}

		// 3. unzip workspace from package and assign workspaceDir
		try {
			this.clonedWorkspace = new File(this.mcPackage.dumpWorkspace(tmpWorkspace));
			LOGGER.info("Using temporary workspace at "+this.clonedWorkspace);
		}
		catch (Exception e) {
			LOGGER.error("Cannot write to instance workspace. " + this.clonedWorkspace.getAbsolutePath());
			return false;
		}

		return true;
	}

	public boolean isFeasible() {
		// TODO implement
		return true;
	}

	public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException {

		if ( !init()) {
			throw new IOException("Could not initialize the processor. Aborting operation.");
		}

		// load arguments and parse them to internal data format (--> Strings)
		for (IOParameter item : this.values()) {
			try {
				setValue(item);
			}
			catch (IOException e) {
				throw new IOException("Could not deal with parameter: " + item.getIdentifier().toString()
						+ "\n" + e.getMessage());
			}
		}

		// create command from parameters and values
		String executable = packageDescriptionDoc.getPackageDescription().getWorkspace().getExecutableLocation();
		if (executable.startsWith("./")) {
			executable = executable.substring(2);
		}

		if (executable.startsWith(".\\")) {
			executable = executable.substring(2);
		}
		executable = "python " + this.clonedWorkspace + File.separator + executable;

		CommandLine cmdLine = buildCommandLine(executable, this.executionValues, this);

		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
		Executor executor = new DefaultExecutor();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
		executor.setStreamHandler(streamHandler);

		// put a watchdog if required
		if (timeoutSeconds > 0) {
			ExecuteWatchdog watchdog = new ExecuteWatchdog(new Long(timeoutSeconds) * 1000);
			executor.setWatchdog(watchdog);
		}

		try {
			executor.execute(cmdLine, resultHandler);
			resultHandler.waitFor();
			int exitVal = resultHandler.getExitValue();
			if (exitVal != 0) {
				LOGGER.error("stderr was: "+errorStream.toString());
				LOGGER.error("stdout was: "+outputStream.toString());
			}
			else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("stdout was:"+outputStream.toString());
					LOGGER.debug("stderr was:"+errorStream.toString());
				}
			}
		}
		catch (ExecuteException e) {
			throw new RuntimeException(e.getMessage());
		}
		catch (IOException e) {
			throw new IOException(e.getMessage());
		}
		catch (InterruptedException e) {
			throw new RuntimeException("Execution was interrupted. Process aborted.\n Message was: " + e.getMessage());
		}

		// update executionData - file data only
		// code below is all about setting the input stream for output media data
		for (ParameterID identifier : this.keySet()) {
			if (this.get(identifier).isMessageOut()) {
				if (this.get(identifier).supportsType(IODataType.MEDIA)) {
					@SuppressWarnings("unchecked")
					List<MediaData> mediaValues = (List<MediaData>) this.get(identifier);
					for (int i = 0; i < mediaValues.size(); i++) {
						String fileName = this.executionValues.get(identifier)[i];
						// <-- this is the important line -->
						mediaValues.get(i).setMediaStream(new FileInputStream(fileName));
					}

				}
				else {
					// not supported for CLI
				}
			}
		}

	}

	private static MimeTypeDatabase getMimeRegistry() {
		URL registryURL = PythonCLIProcessor.class.getResource(mimeTypeFile);
		try {
			return new MimeTypeDatabase(registryURL.openStream());
		}
		catch (Exception e) {
			System.out.println("Could not open MimeType Registry file." + e.getMessage());
			System.out.println("Creating empty Registry instead.");
			return new MimeTypeDatabase();
		}
	}

	/**
	 * Creates a CommandLine Object for execution
	 * 
	 * @param paramSet
	 *        - the parameter specification
	 * @param executionValues
	 *        - the values for the parameters
	 * @return CommandLine - an executable CommandLine
	 */
	private static CommandLine buildCommandLine(String executable,
			SortedMap<ParameterID, String[]> executionValues,
			SortedMap<ParameterID, IOParameter> paramMap) throws IllegalArgumentException {

		CommandLine commandLine = CommandLine.parse(executable);

		// assemble commandLine with values, separators and all that stuff
		for (ParameterID identifier : executionValues.keySet()) {
			String argument = "";
			// 1. add prefix
			argument = argument + paramMap.get(identifier).printPrefix();
			// 2. add values with subsequent separator
			for (String value : executionValues.get(identifier)) {
				argument = argument + value + paramMap.get(identifier).printSeparator();
			}
			// remove last occurrence of separator
			if (argument.length() != 0) {
				argument = argument.substring(0, argument.length() - paramMap.get(identifier).printSeparator().length());
			}
			// 3. add suffix
			argument = argument + paramMap.get(identifier).printSuffix();
			// 4. add to argument to CommandLine
			commandLine.addArgument(argument, false);
		}

		return commandLine;
	}

	private void setValue(final IOParameter data) throws IllegalArgumentException, IOException {

		boolean isInput = data.isMessageIn() && data.isMandatoryForExecution();
		boolean isOutputOnly = data.isMessageOut() && !data.isMessageIn();

		// TODO: substitute IF statements with SWITCH/CASE statements?

		// case: Boolean
		if (data.getType().equals(IODataType.BOOLEAN) && data.getType().getSupportedClass().equals(Boolean.class)) {
			if (isInput) {
				@SuppressWarnings("unchecked")
				List<Boolean> boolValues = (List<Boolean>) data;
				String[] stringValues = new String[boolValues.size()];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = boolValues.get(i).toString();
				}
				this.executionValues.put(data.getIdentifier(), stringValues);
			}
			else {
				if (data.getDirection() == Direction.OUT) {
					// not supported for CLI
				}
				else {
					// TODO: cannot happen (?)
				}
			}
		}

		// case: Integer
		if (data.getType().equals(IODataType.INTEGER) && data.getType().getSupportedClass().equals(Integer.class)) {
			if (isInput) {
				@SuppressWarnings("unchecked")
				List<Integer> intValues = (List<Integer>) data;
				String[] stringValues = new String[intValues.size()];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = intValues.get(i).toString();
				}
				this.executionValues.put(data.getIdentifier(), stringValues);
			}
			else {
				if (data.getDirection() == Direction.OUT) {
					// not supported for CLI
				}
				else {
					// TODO: cannot happen (?)
				}
			}
		}

		// case: Double
		if (data.getType().equals(IODataType.DOUBLE) && data.getType().getSupportedClass().equals(Double.class)) {
			if (isInput) {
				@SuppressWarnings("unchecked")
				List<Integer> dblValues = (List<Integer>) data;
				String[] stringValues = new String[dblValues.size()];
				for (int i = 0; i < stringValues.length; i++) {
					stringValues[i] = dblValues.get(i).toString();
				}
				this.executionValues.put(data.getIdentifier(), stringValues);
			}
			else {
				if (data.getDirection() == Direction.OUT) {
					// not supported for CLI
				}
				else {
					// TODO: cannot happen (?)
				}
			}
		}

		// case: String
		if (data.getType().equals(IODataType.STRING) && data.getType().getSupportedClass().equals(String.class)) {
			if (isInput) {
				@SuppressWarnings("unchecked")
				String[] stringValues = ((List<String>) data).toArray(new String[data.size()]);
				this.executionValues.put(data.getIdentifier(), stringValues);
			}
			else {
				if (data.getDirection() == Direction.OUT) {
					// not supported for CLI
				}
				else {
					// TODO: cannot happen (?)
				}
			}
		}

		// case: Media Data
		if (data.getType().equals(IODataType.MEDIA) && data.getType().getSupportedClass().equals(MediaData.class)) {
			if (isInput) {
				@SuppressWarnings("unchecked")
				List<MediaData> mediaValues = (List<MediaData>) data;
				String[] stringValues = new String[mediaValues.size()];
				for (int i = 0; i < stringValues.length; i++) {
					// get suitable file extension for the mime type
					// trow an exception if it cannot be resolved
					String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
					if (fileExt == null) {
						throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType()
								+ "' could not be resolved to a file extension.");
					}
					String path = this.clonedWorkspace + File.separator + AUID.randomAUID() + "." + fileExt;

					File file = new File(path);

					OutputStream os = new FileOutputStream(file);
					InputStream is = mediaValues.get(i).getMediaStream();
					IOUtils.copy(is, os);
					os.close();
					is.close();

					stringValues[i] = file.getAbsolutePath();

				}
				this.executionValues.put(data.getIdentifier(), stringValues);
			}
			else {
				// special treatment for output-only data
				// create a unique filename that shall be passed as a command line argument
				if (isOutputOnly) {
					@SuppressWarnings("unchecked")
					List<MediaData> mediaValues = (List<MediaData>) data;
					String[] stringValues = new String[mediaValues.size()];
					for (int i = 0; i < stringValues.length; i++) {
						String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
						if (fileExt == null) {
							throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType()
									+ "' could not be resolved to a file extension.");
						}

						String path = this.clonedWorkspace + File.separator + AUID.randomAUID() + "." + fileExt;

						stringValues[i] = path;
					}
					this.executionValues.put(data.getIdentifier(), stringValues);
				}
				else {
					// TODO: cannot happen (?)
				}
			}
		}

	}

	// delete the current workspace
	protected void finalize() throws IOException {
		try {
			FileUtils.deleteDirectory(this.clonedWorkspace.getParentFile());
		}
		catch (IOException e) {
			System.out.println("Could not delete dead workspace:\n" + this.clonedWorkspace.getParentFile().getAbsolutePath());
		}
	}

}
