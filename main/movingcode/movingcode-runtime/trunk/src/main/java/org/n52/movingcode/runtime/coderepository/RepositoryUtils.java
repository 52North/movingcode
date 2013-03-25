package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class contains static utility methods to convert between different
 * {@link IMovingCodeRepository} implementations.
 * 
 * @author Matthias Mueller
 *
 */
public class RepositoryUtils {
	
	// HTTP-URI prefix
	private static final String httpPrefix = "http://";
	
	// strings that shall be replaces by File.separator
	private static final String[] separatorReplacements = {":/", ":\\", "\\"};
	
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
		
		// check if directory exists, create if necessary
		if (!targetDirectory.exists()){
			targetDirectory.createNewFile();
		}
		
		// check if directory is empty
		if (targetDirectory.list().length > 0){
			throw new IOException("Cannot create repository. Target directory is not empty.");
		}
		
		// get contents of old repository
		String[] sourcePackageIDs = sourceRepo.getPackageIDs();
				
		// HashMap<sourcePackageID, targetPackageID> (latter is a short ID)
		HashMap<String, String> targetIDs = new HashMap<String, String>();
		
		//
		for (String currentSourceID : sourcePackageIDs){
			
			// 1. clone ID
			String targetID = new String(currentSourceID);
			
			
			// 2. remove http prefix
			if (targetID.startsWith(httpPrefix)){
				targetID = targetID.substring(httpPrefix.length());
			}
			
			// 3. replace invalid char sequences with File.separator
			for (String sequence : separatorReplacements){
				targetID = targetID.replaceAll(sequence, File.separator);
			}
			
			// 4. remove consecutive occurrences of File.separator
			targetID = removeConsecutiveFileSeparator(targetID);
			
			// 5. remove leading File.separator
			if (targetID.startsWith(File.separator)){
				targetID = targetID.substring(File.separator.length());
			}
			
			// 6. store in map
			targetIDs.put(currentSourceID, targetID);
			
		}
		

		// for each package: dump into correct folder
		for (String currentSourceID : sourcePackageIDs){
			
			// assemble absolute directory path for the package 
			String absTargetPath = targetDirectory.getAbsolutePath() + File.separator + targetIDs.get(currentSourceID);
			File targetFolder = new File (absTargetPath);
			
			if (!targetFolder.exists()){
				targetFolder.createNewFile();
			}
			
			File zipFile = new File(targetFolder, targetIDs.get(currentSourceID) + ".zip");
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
	private static String removeConsecutiveFileSeparator(String s) {
		StringBuffer res = new StringBuffer();
		boolean previousWasFileSep = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == File.separatorChar) {
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
	
}
