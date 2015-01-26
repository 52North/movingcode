package org.n52.movingcode.runtime.processors.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.iodata.MimeTypeDatabase;
import org.n52.movingcode.runtime.iodata.IIOParameter.Direction;
import org.n52.movingcode.runtime.iodata.IIOParameter.ParameterID;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.PropertyMap;
import org.n52.movingcode.runtime.processors.python.PythonCLIProcessor;

public class JavaJARProcessor extends AbstractProcessor {

	private static final long serialVersionUID = -4370516192933571872L;
	private File clonedWorkspace;
	private static MimeTypeDatabase mimeRegistry = getMimeRegistry();
	private static final String mimeTypeFile = "mime.types";

	Logger logger = Logger.getLogger(JavaJARProcessor.class);

	/**
	 * A sorted Map containing all executionValues in an ascending order
	 */
	private SortedMap<ParameterID, String[]> executionValues = new TreeMap<ParameterID, String[]>();

	public JavaJARProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
		super(scratchworkspace, mcp, properties);
	}

	private boolean init() {
		// 1. check workspace constraints
		// CLI: NONE
		// 2. create unique subdirectory

		File tmpWorkspace = new File(this.scratchWorkspace.getAbsolutePath() + File.separator + AUID.randomAUID());

		if ( !tmpWorkspace.mkdir()) {
			this.logger.error("Could not create instance workspace!");
			return false;
		}

		// 3. unzip workspace from package and assign workspaceDir
		try {
			this.clonedWorkspace = new File(this.mcPackage.dumpWorkspace(tmpWorkspace));
		}
		catch (Exception e) {
			this.logger.error("Cannot write to instance workspace. " + this.clonedWorkspace.getAbsolutePath());
			return false;
		}

		return true;
	}

	public boolean isFeasible() {
		// TODO implement
		return true;
	}

	/**
	 * TODO: Or call separate virtual machine, e.g.:
	 * 
	 * %java -Xmx1g -classpath ./netcdfAll-4.3.jar 
	 * ucar.nc2.dataset.GeoTiffWriter -in a.nc -out b.tif
	 * 
	 */
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
				throw new IOException("Could not deal with parameter: " + item.getIdentifier().getHarmonizedValue()
						+ "\n" + e.getMessage());
			}
		}

		// create command from parameters and values
		String executable = this.packageDescriptionDoc.getPackageDescription().getWorkspace().getExecutableLocation();
		if (executable.startsWith("./")) {
			executable = executable.substring(2);
		}

		if (executable.startsWith(".\\")) {
			executable = executable.substring(2);
		}

		executable = this.clonedWorkspace + File.separator + executable;

		// check if JAR exists
		File exFile = new File(executable);
		if (exFile.exists()) {
			// use the class loader to load it
			JarClassLoader cl = new JarClassLoader(exFile.toURI().toURL());

			// try to get the name of the main class
			String mainClassName = cl.getMainClassName();

			// if it wasn't declared throw an exception
			// (no way to guess it)
			if (mainClassName == null) {
				//XXX: URLClassLoader#close() is Java 1.7+ only. If 1.7
				//is needed, change configuration of maven-compiler-plugin
				//                cl.close();
				throw new IOException("No declared main class in JAR: " + executable);
			}

			// build String[] args for executing "public static void main"
			String[] args = buildArgs(this.executionValues, this);

			// execute by invoking the main class with arguments
			try {
				cl.invokeClassMain(mainClassName, args);
			}
			catch (ClassNotFoundException e) {
				throw new IOException("Could not execute main class in JAR: " + e.getMessage());
			}
			catch (NoSuchMethodException e) {
				// can only happen at this point if the main class of the jar
				// has no declared main method
				throw new IOException("Could not execute main class in JAR: " + e.getMessage());
			}
			catch (InvocationTargetException e) {
				throw new IOException("Could not execute main class in JAR: " + e.getMessage());
			}
			finally {
				//XXX: URLClassLoader#close() is Java 1.7+ only. If 1.7
				//is needed, change configuration of maven-compiler-plugin
				//                cl.close();
			}
		}
		else {
			throw new IOException("Could not find executable: " + executable);
		}

		// update executionData - file data only
		// code below is all about setting the input stream for output media data
		for (IIOParameter.ParameterID identifier : this.keySet()) {
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

	/**
	 * Creates an args[] object for executing a main method
	 * 
	 * @param paramSet
	 *        - the parameter specification
	 * @param executionValues
	 *        - the values for the parameters
	 */
	private static String[] buildArgs(SortedMap<ParameterID, String[]> executionValues,
			SortedMap<ParameterID, IOParameter> paramMap) throws IllegalArgumentException {

		List<String> arguments = new ArrayList<String>();

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
			arguments.add(argument);
		}

		return arguments.toArray(new String[arguments.size()]);
	}

}
