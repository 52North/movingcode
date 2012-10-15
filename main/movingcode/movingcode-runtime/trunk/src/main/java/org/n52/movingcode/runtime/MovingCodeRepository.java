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
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.model.Entry;
import org.apache.commons.io.FileUtils;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
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
public class MovingCodeRepository {
	
	private static final String[] zipExtension = {"zip"};
	
	private final Map<String, MovingCodePackage> packages;
	
	/**
	 * 
	 * Constructor for file system based repositories.
	 * Scans all sub-directories of a given sourceDirectory for zip-Files
	 * and attempts to interpret them as MovingCodePackages.
	 * Zipfiles that do not validate will be ignored
	 *  
	 */
	public MovingCodeRepository(File sourceDirectory){
		this.packages = new HashMap<String, MovingCodePackage>();
		
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> zipFiles = scanForZipFiles(sourceDirectory);
		
		for (File currentFile : zipFiles){
			MovingCodePackage mcPackage = new MovingCodePackage(currentFile);
			
			// validate
			// and add to package map
			// and add current file to zipFiles map 
			if (mcPackage.isValid()){
				this.packages.put(mcPackage.getIdentifier(), mcPackage);
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
		this.packages = new HashMap<String, MovingCodePackage>();
		
		try {
			InputStream stream = atomFeedURL.openStream();
			GeoprocessingFeed feed = new GeoprocessingFeed(stream);
			
			for (Entry entry : feed.getEntries()){
				// create new moving code package from the entry
				MovingCodePackage mvcPackage = new MovingCodePackage(new GeoprocessingFeedEntry(entry));
				
				// validate
				// and add to package map
				// and add current file to zipFiles map 
				if (mvcPackage.isValid()){
					this.packages.put(mvcPackage.getIdentifier(), mvcPackage);
				} else {
					System.out.println("Info: " + atomFeedURL.toString() + " contains an invalid package: " + mvcPackage.getIdentifier());
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
	public MovingCodePackage getPackage(String identifier){
		if (this.packages.containsKey(identifier)){
			return this.packages.get(identifier);
		}
		return null;
	}
	
	/*
	 * Returns a snapshot of all currently used identifiers.
	 * Later changes in the MovingCodeRepository (new or deleted packages)
	 * are not propagated to the returned array.
	 * If you need up-to-date information call this method again.
	 * 
	 */
	public String[] getRegisteredProcessIDs(){
		return this.packages.keySet().toArray(new String[packages.size()]);
	}
	
	/**
	 * 
	 * @param identifier
	 * @return
	 */
	public String[] getRegisteredPackageIDs(){
		//TODO: refactor to use real package IDS
		// Right now we are using processIDs which forbids identical processes in one feed
		return this.packages.keySet().toArray(new String[packages.size()]);
	}
	
	/*
	 * returns package description for a given ID 
	 */
	public boolean containsPackage(String identifier){
		return this.packages.containsKey(identifier);
	}
	
	/*
	 * returns the last known update of a MovingCodePackage
	 */
	public Date getPackageTimestamp(String identifier){
		return this.packages.get(identifier).getTimestamp();
	}
	
	/*
	 * returns package description for a given ID 
	 */
	public PackageDescriptionDocument getPackageDescription(String identifier){
		return this.packages.get(identifier).getDescription();
	}
	
	/*
	 * Scans a directory recursively for zipFiles
	 * and adds them to the global Collection "zipFiles".
	 */
	private Collection<File> scanForZipFiles(File directory){
		return FileUtils.listFiles(directory, zipExtension, true);	
	}
}
