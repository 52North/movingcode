package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlOptions;
import org.joda.time.DateTime;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;
import static org.n52.movingcode.runtime.codepackage.Constants.*;

/**
 * This class provides methods for handling MovingCode Packages. This includes methods for validation,
 * copying, unzipping and for accessing the package's description.
 * 
 * MovingCode Packages a the basic entities for shipping code from platform to platform.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class MovingCodePackage implements Comparable<MovingCodePackage>{

	private static final Logger logger = Logger.getLogger(MovingCodePackage.class);

	// the physical instance of this package
	private final ICodePackage archive;

	// package description XML document
//	private PackageDescriptionDocument packageDescription = null;
	
	// immutable representation of the package description XML
	private final String packageDescription;

	// identifier of the provided functionality (e.g. WPS process identifier)
	private final String functionIdentifier;

	// Package id and time stamp, i.e. date of creation or last modification
	private final PID packageId;
	
	private final boolean isValid;

	private final List<FunctionalType> supportedFuncTypes;

	/**
	 * Constructor for zipFiles. Creates a MovingCodePackage from a zipFile on disk.
	 * 
	 * @param {@link File} zipFile - a zip file with a valid package structure
	 */
	public MovingCodePackage(final File zipFile) {

		this.archive = new ZippedPackage(zipFile);
		PackageDescriptionDocument doc = this.archive.getDescription();
		
		// assign properties fields
		if (doc != null) {
			functionIdentifier = doc.getPackageDescription().getFunctionality().getWps100ProcessDescription().getIdentifier().getStringValue();
			supportedFuncTypes = getFunctionalTypes(doc);
			
			DateTime timestamp = new DateTime(doc.getPackageDescription().getTimestamp());
			String id = doc.getPackageDescription().getPackageId();
			packageId = new PID(id, timestamp);
			packageDescription = XMLUtils.toString(doc);
			
		}
		else {
			functionIdentifier = null;
			supportedFuncTypes = null;
			packageId = null;
			packageDescription = null;
		}
		
		isValid = validate(doc, this);
	}
	
	/**
	 * Constructor for geoprocessing feed entries. Creates a MovingCodePackage from an atom feed entry.
	 * 
	 * @param {@link GeoprocessingFeedEntry} atomFeedEntry - an entry from a geoprocessing feed
	 * 
	 */
	/**
	 * Constructor for geoprocessing feed entries. Creates a MovingCodePackage from a remote URL.
	 * Also requires the intended packageID an
	 * 
	 * @param zipPackageURL
	 * @param packageId
	 * @param packageStamp
	 */
	public MovingCodePackage(final URL zipPackageURL) {
		
		ZippedPackage archive = null;
		
		archive = new ZippedPackage(zipPackageURL);
		PackageDescriptionDocument doc = archive.getDescription();
		
		// TODO: Information from the feed might lag during updates
		// how can deal with that?
		// assign properties fields
		if (doc != null) {
			functionIdentifier = doc.getPackageDescription().getFunctionality().getWps100ProcessDescription().getIdentifier().getStringValue();
			supportedFuncTypes = getFunctionalTypes(doc);
				
			DateTime timestamp = new DateTime(doc.getPackageDescription().getTimestamp());
			String id = doc.getPackageDescription().getPackageId();
			packageId = new PID(id, timestamp);
			packageDescription = XMLUtils.toString(doc);
		}
		else {
			functionIdentifier = null;
			supportedFuncTypes = null;
			packageId = null;
			packageDescription = null;
		}
		
		this.archive = archive;
		isValid = validate(doc, this);

	}

	/**
	 * Constructor for directories. Creates a MovingCodePackage from a workspace directory on disk.
	 * 
	 * @param {@link File} workspace - the directory where the code and possibly some related data is stored.
	 * @param {@link PackageDescriptionDocument} packageDescription - the XML document that contains the
	 *        description of the provided logic
	 * @param {@link DateTime} lastModified - the date of latest modification. This value is optional. If NULL,
	 *        the lastModified date is obtained from the workspace's content.
	 */
	public MovingCodePackage(final File workspace, final PackageDescriptionDocument doc) {
		
		// assign properties fields
		if (doc != null) {
			functionIdentifier = doc.getPackageDescription().getFunctionality().getWps100ProcessDescription().getIdentifier().getStringValue();
			supportedFuncTypes = getFunctionalTypes(doc);
			
			DateTime timestamp = new DateTime(doc.getPackageDescription().getTimestamp());
			String id = doc.getPackageDescription().getPackageId();
			packageId = new PID(id, timestamp);
			packageDescription = XMLUtils.toString(doc);
		}
		else {
			functionIdentifier = null;
			supportedFuncTypes = null;
			packageId = null;
			packageDescription = null;
		}
		
		this.archive = new PlainPackage(workspace, doc);
		isValid = validate(doc, this);

	}

	/**
	 * Dump workspace to a given directory. Used to create copies from a template for execution or further
	 * manipulation.
	 * 
	 * @param {@link File} targetDirectory - directory to store the unzipped content
	 * @return {@link String} dumpWorkspacePath - absolute path of the dumped workspace
	 */
	public String dumpWorkspace(File targetDirectory) {
		String wsRoot = archive.getDescription().getPackageDescription().getWorkspace().getWorkspaceRoot();
		this.archive.dumpPackage(wsRoot, targetDirectory);
		if (wsRoot.startsWith("./")) {
			wsRoot = wsRoot.substring(2);
		}

		if (wsRoot.startsWith(".\\")) {
			wsRoot = wsRoot.substring(2);
		}
		return targetDirectory + File.separator + wsRoot;
	}

	/**
	 * Writes a copy of the {@link MovingCodePackage} to a given directory. This is going to be a zipFile
	 * 
	 * @param targetFile
	 *        - destination path and file
	 * @return boolean - true if successful, false if not
	 */
	public boolean dumpPackage(File targetFile) {
		return this.archive.dumpPackage(targetFile);
	}
	
	public boolean dumpPackage(OutputStream os) {
		return this.archive.dumpPackage(os);
	}

	/**
	 * writes a copy of the package (zipfile) to a given directory TODO: implement for URL sources
	 * 
	 * @param targetFile
	 * @return boolean
	 */
	public boolean dumpDescription(File targetFile) {
		try {
			PrintWriter out = new PrintWriter(targetFile);
			out.print(packageDescription);
			out.close();
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	
	public PID getPackageId(){
		return packageId;
	}
	
	public boolean isNewerThan(final MovingCodePackage mcPackage){
		return this.packageId.isNewerThan(mcPackage.packageId);
	}
	
	/**
	 * Returns the PackageDescription in String representation
	 * 
	 * @return {@link String}
	 */
	public String getDescriptionAsString() {
		return packageDescription;
	}
	
	public PackageDescriptionDocument getDescriptionAsDocument() {
		return XMLUtils.fromString(packageDescription);
	}
	
	public boolean isValid(){
		return isValid;
	}

	/**
	 * Returns the unique *functional* identifier of this package. The identifier refers to the functional
	 * contract and not to a concrete implementation in this particular package.
	 * 
	 * @return String
	 */
	public final String getFunctionIdentifier() {
		return this.functionIdentifier;
	}

	/**
	 * Returns the timestamp of this package. The timestamp indicates the last update of the package content
	 * and can be used as a simple versioning machanism.
	 * 
	 * TODO: timestamp now covered by package ID - is this method still required?
	 * 
	 * 
	 * @return {@link DateTime} package timestamp
	 */
	public DateTime getTimestamp() {
		return this.packageId.timestamp;
	}

	/**
	 * Static internal method to evaluate a {@link PackageDescriptionDocument} and return the type (i.e. the
	 * schema) of the functional description.
	 * 
	 * TODO: slight overhead (?) - currently only WPS 1.0 is supported in the schema.
	 * 
	 * @param description
	 *        {@link PackageDescriptionDocument}
	 * @return {@link List} of {@link FunctionalType} - the type of the functional description (i.e. WPS 1.0,
	 *         WSDL, ...).
	 */
	private static final List<FunctionalType> getFunctionalTypes(final PackageDescriptionDocument description) {
		
		ArrayList<FunctionalType> availableFunctionalDescriptions = new ArrayList<FunctionalType>();
		
		// retrieve functional description types
		// TODO: make fit for WPS 2.0?
		availableFunctionalDescriptions.add(FunctionalType.WPS100);
		
		return availableFunctionalDescriptions;
	}
	
	/**
	 * Does this object contain valid content?
	 * 
	 * @return boolean - true if content is valid, false if not
	 */
	private static final boolean validate(PackageDescriptionDocument doc, MovingCodePackage mcp) {
		
		if(doc == null || doc.isNil()){
			return false;
		}
		
		// a valid Code Package must have a package ID
		if(mcp.packageId.name == null || mcp.packageId.equals("")){
			return false;
		}
		// ... and timestamp
		if(mcp.packageId.timestamp == null){
			return false;
		}
		
		// a valid Code Package MUST have a function (aka process) identifier
		if (mcp.functionIdentifier == null) {
			return false;
		}

		// TODO: Identifiers of IO data must be unique!
		
		// TODO: verify path to executable.
		String exLoc = doc.getPackageDescription().getWorkspace().getExecutableLocation();
		if (!mcp.archive.containsFileInWorkspace(exLoc)){
			return false;
		}
		
		// check if there exists a package description
		// and return the validation result
		//information on validation errors
		if (!doc.validate()) {
			List<XmlError> errors = new ArrayList<XmlError>();
			doc.validate(new XmlOptions().setErrorListener(errors));
			logger.warn("Package is not valid: "+errors);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MovingCodePackage [archive=");
		builder.append(this.archive);
		builder.append(", packageDescription=");
		builder.append(this.packageDescription);
		builder.append(", PackageID=");
		builder.append(this.packageId.name);
		builder.append(", timeStamp=");
		builder.append(this.packageId.timestamp.toString());
		builder.append(", supportedFuncTypes=");
		builder.append(this.supportedFuncTypes);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int compareTo(MovingCodePackage other) {
		return this.packageId.compareTo(other.packageId);
	}

}
