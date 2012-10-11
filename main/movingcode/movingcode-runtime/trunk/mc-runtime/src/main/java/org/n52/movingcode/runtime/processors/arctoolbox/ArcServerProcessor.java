/**
 * 
 */
package org.n52.movingcode.runtime.processors.arctoolbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

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
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.PropertyMap;


/**
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class ArcServerProcessor extends AbstractProcessor{
	
	private static final long serialVersionUID = -7794250740419482721L;
	private static MimeTypeDatabase mimeRegistry = getMimeRegistry();
	private static final String mimeTypeFile = "mime.types";
	private File clonedWorkspace;
	
	Logger logger = Logger.getLogger(ArcServerProcessor.class);

	
	/**
	 * A sorted Map containing all executionValues in an ascending order
	 */
	private SortedMap<ParameterID,String[]> executionValues = new TreeMap<ParameterID,String[]>();
	
	private static MimeTypeDatabase getMimeRegistry(){
		URL registryURL = ArcServerProcessor.class.getResource(mimeTypeFile);
		try {
			return new MimeTypeDatabase(registryURL.openStream());
		} catch (Exception e) {
			System.out.println("Could not open MimeType Registry file." + e.getMessage());
			System.out.println("Creating empty Registry instead.");
			return new MimeTypeDatabase();
		}
	}
	
	public ArcServerProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
		super(scratchworkspace, mcp, properties);
	}
	
	private boolean init(){
		// 1. check workspace constraints
		// CLI: NONE
		// 2. create unique subdirectory
		
		File tmpWorkspace = new File(this.scratchWorkspace.getAbsolutePath()
				+ File.separator
				+ UUID.randomUUID().toString());
		
		if (!tmpWorkspace.mkdir()){
			logger.error("Could not create instance workspace!");
			return false;
		}
		
		// 3. unzip workspace from package and assign workspaceDir
		try {
			this.clonedWorkspace = new File(this.mcPackage.dumpWorkspace(tmpWorkspace));
		} catch (Exception e) {
			logger.error("Cannot write to instance workspace. " + clonedWorkspace.getAbsolutePath());
			return false;
		}
		
		//Log Access Data to Server
		logger.debug("  IP: " + getIP());
		logger.debug("  DOMAIN: " + getDomain());
		
		if (getUser() != null) logger.debug("  USER: ***");
		else logger.debug("  USER: missing!");
		
		if (getPass() != null) logger.debug("  PASS: ***");
		else logger.debug("  PASS: missing!");
		
		// if configured: switch to JINTEGRA native mode 
		if (this.getNativeDCOM() == true){
			logger.info("Switching to JINTEGRA_NATIVE_MODE");
			System.setProperty("JINTEGRA_NATIVE_MODE", "");
		}
		
		// load ArcObjects JAR
		bootstrapArcobjectsJar();
		
		return true;
	}
	
	public boolean isFeasible() {
		// TODO implement
		return true;
	}
	
	public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException {

		if (!init()){
			throw new IOException("Could not initialize the processor. Aborting operation.");
		}
		
		// load arguments and parse them to internal data format (--> Strings)
		for (IOParameter item : this.values()){
			try {
				setValue(item);
			} catch (IOException e) {
				throw new IOException("Could not deal with parameter: " + item.getIdentifier().getHarmonizedValue() + "\n" + e.getMessage());
			}
		}
		
		// create toolName and path
		String toolboxPath = this.mcPackage.getDescription().getPackageDescription().getWorkspace().getExecutableLocation();
		if (toolboxPath.startsWith("./")){
			toolboxPath = toolboxPath.substring(2);
		}
		
		if (toolboxPath.startsWith(".\\")){
			toolboxPath = toolboxPath.substring(2);
		}
		toolboxPath = this.clonedWorkspace
			+ File.separator
			+ toolboxPath;
		
		String toolName = this.mcPackage.getDescription().getPackageDescription().getWorkspace().getExecutableMethodCall();
		
		
		// execute and break if an error occurs
		boolean success = executeGPTool(toolName, toolboxPath);
		if (!success){
			throw new RuntimeException("Execution terminated with an error.");
		}
		
		// update executionData - file data only
		// code below is all about setting the input stream for output media data
		for (IIOParameter.ParameterID identifier : this.keySet()){
			if (this.get(identifier).isMessageOut()){
				if (this.get(identifier).supportsType(IODataType.MEDIA)){
					@SuppressWarnings("unchecked")
					List<MediaData> mediaValues = (List<MediaData>)this.get(identifier);
					for (int i = 0; i < mediaValues.size(); i++){
						String fileName = executionValues.get(identifier)[i];
						// <-- this is the important line -->
						mediaValues.get(i).setMediaStream(new FileInputStream(fileName));
					}
					
				} else {
					// not supported for ArcToolbox
				}
			}
		}
		
	}
	
	
	private boolean executeGPTool(String toolName, String toolboxPath) {
		
		// build parameters array for ArcGIS
		String[] paramArray = new String[executionValues.keySet().size()];
		int i = 0;
		for (ParameterID identifier : executionValues.keySet()){
			String valString = "";
			for (String value : executionValues.get(identifier)){
				if (valString == ""){
					valString = value;
				} else {
					valString = valString + " " + value;
				} 
			}
			
			paramArray[i] = valString;
			i++;
		}
		
		return ArcServerController.executeGPTool(toolName, toolboxPath, paramArray, getDomain(), getUser(), getPass(), getIP(), getArcObjectsJAR());
	}
	
	private void setValue(final IOParameter data) throws IllegalArgumentException, IOException{
		
		boolean isInput = data.isMessageIn() && data.isMandatoryForExecution();
		boolean isOutputOnly = data.isMessageOut() && !data.isMessageIn();
		
		// TODO: substitute IF statements with SWITCH/CASE statements?
		
		// case: Boolean
		if (data.getType().equals(IODataType.BOOLEAN)
				&& data.getType().getSupportedClass().equals(Boolean.class)){
			if (isInput){
				@SuppressWarnings("unchecked")
				List<Boolean> boolValues = (List<Boolean>)data;
				String[] stringValues = new String[boolValues.size()];
				for (int i = 0; i < stringValues.length; i++){
					stringValues[i] = boolValues.get(i).toString();
				}
				executionValues.put(data.getIdentifier(), stringValues);
			} else {
				if (data.getDirection() == Direction.OUT){
					// not supported for CLI
				} else {
					// TODO: cannot happen (?)
				}
			}
		}
		
		// case: Integer
		if (data.getType().equals(IODataType.INTEGER)
				&& data.getType().getSupportedClass().equals(Integer.class)){
			if (isInput){
				@SuppressWarnings("unchecked")
				List<Integer> intValues = (List<Integer>)data;
				String[] stringValues = new String[intValues.size()];
				for (int i = 0; i < stringValues.length; i++){
					stringValues[i] = intValues.get(i).toString();
				}
				executionValues.put(data.getIdentifier(), stringValues);
			} else {
				if (data.getDirection() == Direction.OUT){
					// not supported for CLI
				} else {
					// TODO: cannot happen (?)
				}
			}
		}
		
		// case: Double
		if (data.getType().equals(IODataType.DOUBLE)
				&& data.getType().getSupportedClass().equals(Double.class)){
			if (isInput){
				@SuppressWarnings("unchecked")
				List<Integer> dblValues = (List<Integer>)data;
				String[] stringValues = new String[dblValues.size()];
				for (int i = 0; i < stringValues.length; i++){
					stringValues[i] = dblValues.get(i).toString();
				}
				executionValues.put(data.getIdentifier(), stringValues);
			} else {
				if (data.getDirection() == Direction.OUT){
					// not supported for CLI
				} else {
					// TODO: cannot happen (?)
				}
			}			
		}
		
		// case: String
		if (data.getType().equals(IODataType.STRING)
				&& data.getType().getSupportedClass().equals(String.class)){
			if (isInput){
				@SuppressWarnings("unchecked")
				String[] stringValues = ((List<String>)data).toArray(new String[data.size()]);
				executionValues.put(data.getIdentifier(), stringValues);
			} else {
				if (data.getDirection() == Direction.OUT){
					// not supported for CLI
				} else {
					// TODO: cannot happen (?)
				}
			}			
		}
		
		// case: Media Data
		if (data.getType().equals(IODataType.MEDIA)
				&& data.getType().getSupportedClass().equals(MediaData.class)){
			if (isInput){
				@SuppressWarnings("unchecked")
				List<MediaData> mediaValues = (List<MediaData>)data;
				String[] stringValues = new String[mediaValues.size()];
				for (int i = 0; i < stringValues.length; i++){
					// get suitable file extension for the mime type
					// trow an exception if it cannot be resolved
					String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
					if (fileExt == null){
						throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType() + "' could not be resolved to a file extension.");
					}
					String path = this.clonedWorkspace
						+ File.separator
						+ UUID.randomUUID().toString()
						+ "."
						+ fileExt;
					
					File file = new File(path);
					
					OutputStream os = new FileOutputStream(file);
					IOUtils.copy(mediaValues.get(i).getMediaStream(),os);
					
					stringValues[i] = file.getAbsolutePath();
					
				}
				executionValues.put(data.getIdentifier(), stringValues);
			} else {
				// special treatment for output-only data
				// create a unique filename that shall be passed as a command line argument 
				if (isOutputOnly){
					@SuppressWarnings("unchecked")
					List<MediaData> mediaValues = (List<MediaData>)data;
					String[] stringValues = new String[mediaValues.size()];
					for (int i = 0; i < stringValues.length; i++){
						String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
						if (fileExt == null){
							throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType() + "' could not be resolved to a file extension.");
						}
						
						String path = this.clonedWorkspace
							+ File.separator
							+ UUID.randomUUID().toString()
							+ "."
							+ fileExt;
						
						stringValues[i] = path;
					}
					executionValues.put(data.getIdentifier(), stringValues);
				} else {
					// TODO: cannot happen (?)
				}
			}
		}	
	}
	
	
	protected final String getDomain(){
		return properties.get("DOMAIN");
	}
	
	protected final String getUser(){
		return properties.get("USER");
	}
	
	protected final String getPass(){
		return properties.get("PASS");
	}
	
	protected final String getIP(){
		return properties.get("IP");
	}
	
	protected final boolean getNativeDCOM(){
		return properties.get("DCOM_NATIVE").equalsIgnoreCase("TRUE");
	}
	
	protected final String getArcObjectsJAR(){
		return properties.get("ARCOBJECTSJAR");
	}
	
	
	protected void bootstrapArcobjectsJar() {
		
		//bootstrap arcobjects.jar
		logger.info("Bootstrapping ArcObjects: " + getArcObjectsJAR());
		
		File aoFile = new File(getArcObjectsJAR());
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		try {

			Method method = sysclass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] {aoFile.toURI().toURL()});
			
		}
		catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Could not add arcobjects.jar to system classloader");
					
		}
		
		Thread.currentThread().setContextClassLoader(sysloader);
	}
}
