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
		
		// analyze IDs
		
		// 1. guess common root
		
		// 2. guess folder structure (based on PackageID String)
		
		/**
		 * http://stackoverflow.com/questions/1005551/construct-a-tree-structure-from-list-of-string-paths
		 */
		
		// or:
		
		// simply split up the ID at path separators
		
		// HashMap<sourcePackageID, targetPackageID> (latter is a short ID)
		HashMap<String, String> targetIDs = new HashMap<String, String>();
		
		
		// HashMap<sourcePackageID, targetFolder>
		HashMap<String, File> targetFolders = new HashMap<String, File>();
		

		// for each package: dump into correct folder
		for (String currentSourceID : sourcePackageIDs){
			File targetFolder = targetFolders.get(currentSourceID);
			if (!targetFolder.exists()){
				targetFolder.createNewFile();
			}
			
			File zipFile = new File(targetFolder, targetIDs.get(currentSourceID) + ".zip");
			sourceRepo.getPackage(currentSourceID).dumpPackage(zipFile); // dumpPackage creates the file automatically
		}
		
		// return new Repo for the folder
		return new LocalZipPackageRepository(targetDirectory);
	}
	
}
