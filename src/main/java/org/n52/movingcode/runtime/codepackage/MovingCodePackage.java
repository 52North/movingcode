/**
 * 
 */
package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.abdera.model.Link;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.n52.movingcode.runtime.feed.GeoprocessingFeedEntry;

import de.tudresden.gis.geoprocessing.movingcode.schema.FunctionalDescriptionsListType;
import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * This class provides methods for handling MovingCode Packages. This includes
 * methods for validation, copying, unzipping and for accessing the package's
 * description.
 * 
 * MovingCode Packages a the basic entities for shipping code from platform to platform.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class MovingCodePackage {
	
	public static enum FunctionalType{WPS100,WSDL10,WSDL20};
	
	public static final String descriptionFileName = "packagedescription.xml";
	
	private final ZippedPackage archive;
	private PackageDescriptionDocument packageDescription = null;
	private final String identifier;
	private final Date timeStamp;
	private final List<FunctionalType> supportedFuncTypes;
	
	/**
	 * Constructor for zipFiles. Creates a MovingCodePackage
	 * from a zipFile on disk.
	 * 
	 * @param {@link File} zipFile - a zip file with a valid package structure
	 */
	public MovingCodePackage (final File zipFile)  {
		
		archive = new ZippedPackage(zipFile);
		packageDescription = archive.extractDescription();
		
		if (packageDescription != null && packageDescription.getPackageDescription().getContractedFunctionality().isSetWpsProcessDescription()){
			identifier = packageDescription.getPackageDescription().getContractedFunctionality().getWpsProcessDescription().getIdentifier().getStringValue();
			supportedFuncTypes = getFunctionalTypes(packageDescription);
		} else {
			identifier = null;
			supportedFuncTypes = null;
		}
		timeStamp = getTimestamp(zipFile);
	}
	
	/**
	 * Constructor for AtomFeed entries. Creates a MovingCodePackage
	 * from an atom feed entry.
	 * 
	 * @param {@link GeoprocessingFeedEntry} atomFeedEntry - an entry from a geoprocessing feed
	 * 
	 */
	public MovingCodePackage (final GeoprocessingFeedEntry atomFeedEntry){
		
		PackageDescriptionDocument packageDescription = null;
		ZippedPackage archive = null;
		
		// retrieve links and look for a ZippedPackage
		// TODO: parse packageDescription first!
		List<Link> links = atomFeedEntry.getAtomEntry().getLinks();
		for (Link link : links){
			if (link.getMimeType() != null && link.getMimeType().toString().equals(GeoprocessingFeedEntry.PACKAGE_MIMETYPE)){
				// try to extract a package description
				try {
					URL zipURL = link.getHref().toURL();
					archive = new ZippedPackage(zipURL);
					packageDescription = archive.extractDescription();
				} catch (MalformedURLException e) {
					// do nothing
				} catch (URISyntaxException e) {
					// do nothing
				}
				
			}
		}
		
		this.packageDescription = packageDescription;
		// TODO: Information from the feed might lag during updates
		// how can deal with that?
		this.identifier = atomFeedEntry.getIdentifier();
		this.timeStamp = atomFeedEntry.getUpdated();
		this.archive = archive;
		this.supportedFuncTypes = getFunctionalTypes(packageDescription);
		
	}
	
	/**
	 * Constructor for directories. Creates a MovingCodePackage
	 * from a workspace directory on disk.
	 * 
	 * @param {@link File} workspace - the directory where the code and possibly some related data is stored.
	 * @param {@link PackageDescriptionDocument} packageDescription - the XML document that contains the description of the provided logic
	 * @param {@link Date} lastModified - the date of latest modification. This value is optional. If NULL, the lastModified date is obtained from the workspace's content.
	 */
	public MovingCodePackage(File workspace, PackageDescriptionDocument packageDescription, Date timestamp){
		
		this.packageDescription = packageDescription;
		
		if (packageDescription != null && packageDescription.getPackageDescription().getContractedFunctionality().isSetWpsProcessDescription()){
			identifier = packageDescription.getPackageDescription().getContractedFunctionality().getWpsProcessDescription().getIdentifier().getStringValue();
			supportedFuncTypes = getFunctionalTypes(packageDescription);
		} else {
			identifier = null;
			supportedFuncTypes = null;
		}
		
		// set timestamp
		if (timestamp != null){
			this.timeStamp = timestamp;
		} else {
			this.timeStamp = getLastModified(workspace);
		}
		
		this.archive = new ZippedPackage(workspace, packageDescription);
		
	}
	
	
	/**
	 * Dump workspace to a given directory. Used to create copies from a template for execution
	 * or further manipulation.
	 * 
	 * @param {@link File} targetDirectory - directory to store the unzipped content
	 * @return {@link String} dumpWorkspacePath - absolute path of the dumped workspace
	 */
	public String dumpWorkspace(File targetDirectory){
		String wsRoot = packageDescription.getPackageDescription().getWorkspace().getWorkspaceRoot();
		archive.dumpWorkspace(wsRoot, targetDirectory);
		if (wsRoot.startsWith("./")){
			wsRoot = wsRoot.substring(2);
		}
		
		if (wsRoot.startsWith(".\\")){
			wsRoot = wsRoot.substring(2);
		}
		return targetDirectory + File.separator + wsRoot;
	}
	
	/**
	 * Writes a copy of the {@link MovingCodePackage} to a given directory.
	 * This is going to be a zipFile
	 * 
	 * @param targetFile - destination path and file
	 * @return boolean - true if successful, false if not
	 */
	public boolean dumpPackage(File targetFile){
		return archive.dumpPackage(targetFile);
	}
	
	/**
	 * writes a copy of the package (zipfile) to a given directory
	 * TODO: implement for URL sources
	 * 
	 * @param targetFile 
	 * @return
	 * boolean
	 */
	public boolean dumpDescription(File targetFile){
		try {
			this.packageDescription.save(targetFile);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * Returns the PackageDescription
	 * 
	 * @return PackageDescriptionDocument
	 */
	public PackageDescriptionDocument getDescription(){
		return this.packageDescription;
	}
	
	/**
	 * Does this object contain valid content?
	 * 
	 * @return boolean - true if content is valid, false if not 
	 */
	public boolean isValid(){
		
		// a valid MovingCodePackage MUST have an identifier
		if (this.identifier == null){
			return false;
		}
		
		// TODO: Identifiers of IO data must be unique!		
		
		// check if there exists a package description
		// and return the validation result
		if (this.packageDescription != null){
			if (!this.packageDescription.isNil()){
				return this.packageDescription.validate();
			}
		}
		return false;
	}
	
	/**
	 * Returns the unique *functional* identifier of this package.
	 * The identifier refers to the functional contract and not
	 * to a concrete implementation in this particular package.
	 * 
	 * @return String
	 */
	public String getIdentifier(){
		return this.identifier;
	}
	
	/**
	 * Returns the timestamp of this package. The timestamp indicates the
	 * last update of the package content and can be used as a simple
	 * versioning machanism.
	 * 
	 * @return {@link Date} package timestamp
	 */
	public Date getTimestamp(){
		return this.timeStamp;
	}
	
	
	/**
	 * Helper method: returns the modification date of a given file.
	 * @param file - the file
	 * @return Date - date of last modification
	 */
	private static Date getTimestamp(File file){
		return new Date(file.lastModified());
	}
	
	private static final List<FunctionalType> getFunctionalTypes(final PackageDescriptionDocument description){
		ArrayList<FunctionalType> availableFunctionalDescriptions = new ArrayList<FunctionalType>();
		
		// retrieve functional description types
		FunctionalDescriptionsListType funcDescArray = description.getPackageDescription().getContractedFunctionality();
		if (funcDescArray.isSetWpsProcessDescription()){
			availableFunctionalDescriptions.add(FunctionalType.WPS100);
		}
		return availableFunctionalDescriptions;
	}
	
	private Date getLastModified(File directory){
		List<File> files = new ArrayList<File>(FileUtils.listFiles(directory, null, true));
		Collections.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		return new Date(files.get(0).lastModified());
	}
}
