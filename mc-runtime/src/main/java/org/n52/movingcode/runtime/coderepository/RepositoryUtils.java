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
package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.processors.ProcessorFactory;


/**
 * This class contains static utility methods to convert between different
 * {@link IMovingCodeRepository} implementations.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class RepositoryUtils {
	
	private static final Logger logger = Logger.getLogger(RepositoryUtils.class);
	
	// HTTP-URI prefix
	private static final String httpPrefix = "http://";
	
	// strings that shall be replaces by File.separator
	private static final String[] separatorReplacements = {"\\", File.separator, ":/", ":\\\\", "\\\\", ";"};
	
	// default extension for zipped packages
	public static final String defaultZipExtension = ".zip";
	
	// typical extensions for zipped packages
	public static final String[] zipExtension = {defaultZipExtension};
	
	// common separator char in nomalizedPackageID is a slash [/]
	public static final String normalizedFileSeparator = "/";
	public static final char normalizedFileSeparatorChar = '/';
	
	/**
	 * Conversion method that creates a new {@link LocalZipPackageRepository} repository from any given {@link IMovingCodeRepository}.
	 * Automatically attempts to create the targetDirectory if it does not exist.
	 * 
	 * @param sourceRepo {@link IMovingCodeRepository} - source repository to copy the content from
	 * @param targetDirectory {@link File} - blank(!) target directory that shall be used to create the new repository
	 * @return {@link LocalZipPackageRepository}
	 * @throws IOException - throws an exception in case the repository cannot be created
	 */
	public static final LocalZipPackageRepository materializeAsLocalZipRepo(IMovingCodeRepository sourceRepo, File targetDirectory) throws IOException{
		
		// check if directory exists and is empty, create new one if necessary
		// using #list instead of #exists prevents some issues with directory permissions
		String[] contents = targetDirectory.list();
		if (contents == null){
			boolean success = targetDirectory.mkdirs();
			if (!success){
				throw new IOException("Cannot create repository folder. (Probably insufficent file permissions.)");
			}
		} else if (contents.length > 0) {
			throw new IOException("Cannot create repository. Target directory is not empty.");
		}
		
		// get contents of old repository
		String[] sourcePackageIDs = sourceRepo.getPackageIDs();
				
		// HashMap<sourcePackageID, targetPackageID> (latter is a short ID)
		HashMap<String, String> targetIDs = new HashMap<String, String>();
		
		//
		for (String currentSourceID : sourcePackageIDs){
			
			String targetID = normalizePackageID(currentSourceID);
			
			// store in map
			targetIDs.put(currentSourceID, targetID);
			
		}
		

		// for each package: dump into correct folder
		for (String currentSourceID : sourcePackageIDs){
			// build location for package zipFile
			File zipFile = new File(targetDirectory, targetIDs.get(currentSourceID) + ".zip");
			// create necessary directories
			//String relPath = targetIDs.get(currentSourceID) + ".zip";
			String path = zipFile.getAbsolutePath();
			if(path.contains(File.separator)){
				int idx = path.lastIndexOf(File.separator);
				String dirPart = zipFile.getAbsolutePath().substring(0, idx);
				File dir = new File (dirPart);
				dir.mkdirs();
			}

			// dump package as zipFile
			sourceRepo.getPackage(currentSourceID).dumpPackage(zipFile); // dumpPackage creates the file automatically
		}
		
		// return new Repo for the @param targetDirectory
		return new LocalZipPackageRepository(targetDirectory);
	}
	
	/**
	 * Remove consecutive occurrences of file separator character in s
	 * 
	 * @param s the string to parse.
	 * @return s without consecutive occurrences of file separator character
	 */
	private static String removeConsecutiveFileSeparator(final String s) {
		StringBuffer res = new StringBuffer();
		boolean previousWasFileSep = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == normalizedFileSeparatorChar) {
				if (!previousWasFileSep) {
					res.append(c);
					previousWasFileSep = true;
				}
			} else {
				previousWasFileSep = false;
				res.append(c);
			}
		}
		return res.toString();
	}
	
	/**
	 * Computes a fingerprint for a given directory
	 * 
	 * @param directory
	 * @return
	 * @throws NoSuchAlgorithmException - should not occur
	 */
	public static String directoryFingerprint(final File directory) {
		final Collection<File> files = FileUtils.listFiles(directory, null, true);
		
		StringBuffer fNamesAndTimes = new StringBuffer("");
		for (File file : files){
			fNamesAndTimes = fNamesAndTimes.append(file.getAbsolutePath() + file.lastModified());
		}
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] hash = md.digest(fNamesAndTimes.toString().getBytes());
			
			String result = "";
		    for ( byte b : hash ) {
		        result += Integer.toHexString(b + 256) + " ";
		    }
			
		    return result;
		} catch (NoSuchAlgorithmException e) {
			logger.error("Could not find SHA-1 algorithm.");
			return null;
		}
		
	}
	
	/**
	 * Static helper method that performs a cross check with the processor factory
	 * to filter out unsupported packages (i.e. packages that cannot be executed with
	 * the current processor configuration).  
	 * 
	 * @param packageIDs - a given Array of {@link String} packageIDs
	 * @param repo - a {@link IMovingCodeRepository} that is assumed to contain the packageIDs and provides the according MovingCode packages
	 * @return Array of {@link String} that contains only those packageIDs that represent executable packages.
	 */
	public static final String[] filterExecutablePackageIDs(String[] packageIDs, IMovingCodeRepository repo){
		ArrayList<String> resultSet = new ArrayList<String>();
		for (String currentPID : packageIDs){
			boolean supported = ProcessorFactory.getInstance().supportsPackage(repo.getPackage(currentPID));
			if (supported){
				resultSet.add(currentPID);
			}
		}
		
		return resultSet.toArray(new String[resultSet.size()]);
	}
	
	/**
	 * Normalizes any given packageID so that it can be used to create a local file path.
	 * The following operations are performed:
	 * 
	 * 1. remove {@value #httpPrefix}
	 * 2. replace any of {@value #separatorReplacements} with a {@value #normalizedFileSeparator}
	 * 3. collapse consecutive occurrences of {@value #normalizedFileSeparator} to one
	 * 4. remove a leading {@value #normalizedFileSeparator}
	 * 5. remove trailing {@value #zipExtension}
	 * 
	 * @param {@link String} packageID - ID that shall be normalized
	 * @return {@link String} - the normalized ID
	 */
	public static String normalizePackageID(final String packageID){
		// 0. clone ID
		String normalizedID = new String(packageID);
		
		
		// 1. remove {@value #httpPrefix}
		if (normalizedID.startsWith(httpPrefix)){
			normalizedID = normalizedID.substring(httpPrefix.length());
		}
		
		// 2. replace any of {@value #separatorReplacements} with a {@value #normalizedFileSeparator}
		for (String sequence : separatorReplacements){
			if (normalizedID.contains(sequence)){
				StringTokenizer st = new StringTokenizer(normalizedID, sequence);
				StringBuffer retval = new StringBuffer(st.nextToken());
				while (st.hasMoreTokens()){
					retval.append(normalizedFileSeparator + st.nextToken());
				}
				normalizedID = retval.toString();
			}
		}
		
		// 3. reduce consecutive occurrences of {@link File#separator} to one
		normalizedID = removeConsecutiveFileSeparator(normalizedID);
		
		// 4. remove leading normalizedFileSeparator
		if (normalizedID.startsWith(normalizedFileSeparator)){
			normalizedID = normalizedID.substring(normalizedFileSeparator.length());
		}
		
		// 5. replace invalid char sequences with File.separator
		for (String ext : zipExtension){
			if (normalizedID.endsWith(ext)){
				normalizedID = normalizedID.substring(0, normalizedID.lastIndexOf(ext));
			}
		}
		
		return normalizedID;
	}
	
	/**
	 * Static method that generates a packageID from a file's absolute path.
	 * Since the file path should be a unique ID for each zip file on disk,
	 * the generated ID is also locally unique.
	 * 
	 * @param file {@link File} - the packageFile
	 * @return packageID {@link String} - a normalized packageID
	 */
	public static final String generateNormalizedIDFromFile(final File file) {
		return normalizePackageID(file.getAbsolutePath());
	}
}
