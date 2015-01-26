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
package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;





import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.n52.movingcode.runtime.codepackage.PID;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * This class contains static utility methods to convert between different
 * {@link MovingCodeRepository} implementations.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class RepositoryUtils {

	private static final Logger logger = Logger.getLogger(RepositoryUtils.class);

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
	 * Extracts a packageId from a process Description.
	 * 
	 * Does a safe extract, in case the description is invalid, partially valid or null
	 * 
	 * 
	 * @param pdd
	 * @return
	 */
	public static final PID extractId(PackageDescriptionDocument pdd){
		if (pdd == null){
			return new PID(null,null);
		} else {
			try {
				return new PID(pdd.getPackageDescription().getPackageId(), new DateTime(pdd.getPackageDescription().getTimestamp()));
			} catch (Exception e){
				return new PID(null,null);
			}
		}
	}

	/**
	 * Helper method: returns the modification date of a given file.
	 * 
	 * @param file
	 *        - the file
	 * @return DateTime - date of last modification
	 */
	public static final DateTime getTimestamp(File file) {
		return new DateTime(file.lastModified());
	}

	/**
	 * Static helper method to determine when a directory or its content has been modified last time.
	 * 
	 * @param directory
	 *        {@link File}
	 * @return last modification date {@link DateTime}
	 */
	public static final DateTime getLastModified(File directory) {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(directory, null, true));
		Collections.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
		return new DateTime(files.get(0).lastModified());
	}

	//	/**
	//	 * Conversion method that creates a new {@link LocalZipPackageRepository} repository from any given {@link IMovingCodeRepository}.
	//	 * Automatically attempts to create the targetDirectory if it does not exist.
	//	 * 
	//	 * @param sourceRepo {@link IMovingCodeRepository} - source repository to copy the content from
	//	 * @param targetDirectory {@link File} - blank(!) target directory that shall be used to create the new repository
	//	 * @return {@link LocalZipPackageRepository}
	//	 * @throws IOException - throws an exception in case the repository cannot be created
	//	 */
	//	private static final LocalZipPackageRepository materializeAsLocalZipRepo(IMovingCodeRepository sourceRepo, File targetDirectory) throws IOException{
	//		
	//		// check if directory exists and is empty, create new one if necessary
	//		// using #list instead of #exists prevents some issues with directory permissions
	//		String[] contents = targetDirectory.list();
	//		if (contents == null){
	//			boolean success = targetDirectory.mkdirs();
	//			if (!success){
	//				throw new IOException("Cannot create repository folder. (Probably insufficent file permissions.)");
	//			}
	//		} else if (contents.length > 0) {
	//			throw new IOException("Cannot create repository. Target directory is not empty.");
	//		}
	//		
	//		// get contents of old repository
	//		PackageID[] sourcePackageIDs = sourceRepo.getPackageIDs();
	//				
	//		// HashMap<sourcePackageID, targetPackageID> (latter is a short ID)
	//		HashMap<String, String> targetIDs = new HashMap<String, String>();
	//		
	//		//
	//		for (PackageID currentSourceID : sourcePackageIDs){
	//			
	//			String targetID = normalizePackageID(currentSourceID);
	//			
	//			// store in map
	//			targetIDs.put(currentSourceID, targetID);
	//			
	//		}
	//		
	//
	//		// for each package: dump into correct folder
	//		for (String currentSourceID : sourcePackageIDs){
	//			// build location for package zipFile
	//			File zipFile = new File(targetDirectory, targetIDs.get(currentSourceID) + ".zip");
	//			// create necessary directories
	//			//String relPath = targetIDs.get(currentSourceID) + ".zip";
	//			String path = zipFile.getAbsolutePath();
	//			if(path.contains(File.separator)){
	//				int idx = path.lastIndexOf(File.separator);
	//				String dirPart = zipFile.getAbsolutePath().substring(0, idx);
	//				File dir = new File (dirPart);
	//				dir.mkdirs();
	//			}
	//
	//			// dump package as zipFile
	//			sourceRepo.getPackage(currentSourceID).dumpPackage(zipFile); // dumpPackage creates the file automatically
	//		}
	//		
	//		// return new Repo for the @param targetDirectory
	//		return new LocalZipPackageRepository(targetDirectory);
	//	}

	//	/**
	//	 * Static helper method that performs a cross check with the processor factory
	//	 * to filter out unsupported packages (i.e. packages that cannot be executed with
	//	 * the current processor configuration).  
	//	 * 
	//	 * @param packageIDs - a given Array of {@link String} packageIDs
	//	 * @param repo - a {@link IMovingCodeRepository} that is assumed to contain the packageIDs and provides the according MovingCode packages
	//	 * @return Array of {@link String} that contains only those packageIDs that represent executable packages.
	//	 */
	//	public static final String[] filterExecutablePackageIDs(String[] packageIDs, IMovingCodeRepository repo){
	//		ArrayList<String> resultSet = new ArrayList<String>();
	//		for (String currentPID : packageIDs){
	//			boolean supported = ProcessorFactory.getInstance().supportsPackage(repo.getPackage(currentPID));
	//			if (supported){
	//				resultSet.add(currentPID);
	//			}
	//		}
	//		
	//		return resultSet.toArray(new String[resultSet.size()]);
	//	}
}
