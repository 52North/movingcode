/**
 * 
 */
package org.n52.movingcode.runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;

import org.apache.abdera.model.Entry;
import org.apache.commons.io.FileUtils;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.DefaultPackageRepository;
import org.n52.movingcode.runtime.feed.GeoprocessingFeed;
import org.n52.movingcode.runtime.feed.GeoprocessingFeedEntry;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * A repository that contains MovingCode packages.
 * 
 * Two constructors allow:
 * 1. MovingCode Repositories from directories in the file system
 * 2. MovingCode Repositories from a remote location (URL) 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class MovingCodeRepository extends DefaultPackageRepository{
	
	private static final String[] zipExtension = {"zip"};
	
	/**
	 * 
	 * Constructor for file system based repositories.
	 * Scans all sub-directories of a given sourceDirectory for zip-Files
	 * and attempts to interpret them as MovingCodePackages.
	 * Zipfiles that do not validate will be ignored
	 *  
	 */
	public MovingCodeRepository(File sourceDirectory){		
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> zipFiles = scanForZipFiles(sourceDirectory);
		
		for (File currentFile : zipFiles){
			MovingCodePackage mcPackage = new MovingCodePackage(currentFile, sourceDirectory.getAbsolutePath());
			
			// validate
			// and add to package map
			// and add current file to zipFiles map 
			if (mcPackage.isValid()){
				register(mcPackage);
			} else {
				System.out.println("Info: " + currentFile.getAbsolutePath() + " is an invalid package.");
			}
		}
	}
	
	/**
	 * 
	 * Constructor for atom feed repositories.
	 * Tries to access the atom feed at the given URL and scans its entries. 
	 * Then attempts to interpret the entries as MovingCodePackages.
	 * Packages that do not validate will be ignored
	 * 
	 */
	public MovingCodeRepository(URL atomFeedURL){
		try {
			InputStream stream = atomFeedURL.openStream();
			GeoprocessingFeed feed = new GeoprocessingFeed(stream);
			
			for (Entry entry : feed.getEntries()){
				// create new moving code package from the entry
				MovingCodePackage mcPackage = new MovingCodePackage(new GeoprocessingFeedEntry(entry));
				
				// validate
				// and add to package map
				// and add current file to zipFiles map 
				if (mcPackage.isValid()){
					register(mcPackage);
				} else {
					System.out.println("Info: " + atomFeedURL.toString() + " contains an invalid package: " + mcPackage.getPackageIdentifier());
				}
			}
			
			
		} catch (IOException e) {
			System.err.println("Could read feed from URL: " + atomFeedURL);
		}
	}
	
	
	/**
	 * returns a package matching a given identifier
	 * 
	 * @param {@link String} packageIdentifier - the (unique) ID of the package
	 * 
	 */
	public MovingCodePackage getPackage(String functionalID){
		return retrievePackage(functionalID);
	}
	
	
	/*
	 * returns the last known update of a MovingCodePackage
	 */
	public Date getPackageTimestamp(String packageID){
		return retrievePackage(packageID).getTimestamp();
	}
	
	/*
	 * returns package description for a given functional ID 
	 */
	public PackageDescriptionDocument getPackageDescription(String functionalID){
		return retrievePackage(functionalID).getDescription();
	}
	
	/*
	 * Scans a directory recursively for zipFiles
	 * and adds them to the global Collection "zipFiles".
	 */
	private Collection<File> scanForZipFiles(File directory){
		return FileUtils.listFiles(directory, zipExtension, true);	
	}
	
}
