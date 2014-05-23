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
import java.util.Collection;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.n52.movingcode.runtime.codepackage.Constants;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PackageID;

/**
 * This class implements an {@link IMovingCodeRepository} for local zipped packages, stored
 * in a folder structure.
 * 
 * Performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link IMovingCodeRepository#localPollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public final class LocalZipPackageRepository extends AbstractRepository {

	private static final String[] ZIP_EXTENSION = {"zip"};
	
	private final File directory;
	
	private String fingerprint;
	
	private Timer timerDaemon;

	/**
	 * 
	 * Constructor for file system based repositories. Scans all sub-directories of a given sourceDirectory
	 * for zip-Files and attempts to interpret them as MovingCodePackages. Zipfiles that do not validate will
	 * be ignored
	 * 
	 * @param sourceDirectory {@link File} - the directory to be scanned for Moving Code Packages.
	 * 
	 */
	public LocalZipPackageRepository(final File sourceDirectory) {
		this.directory = sourceDirectory;
		// compute directory fingerprint
		fingerprint = RepositoryUtils.directoryFingerprint(directory);
		
		// load packages from folder
		load();
		
		// start timer daemon
		timerDaemon = new Timer(true);
		timerDaemon.scheduleAtFixedRate(new CheckFolder(), 0, IMovingCodeRepository.localPollingInterval);
	}
	
	private void load(){
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> zipFiles = scanForZipFiles(directory);

		logger.info("Scanning directory: " + directory.getAbsolutePath());

		for (File currentFile : zipFiles) {
			logger.debug("Found package: " + currentFile);

			MovingCodePackage mcPackage = new MovingCodePackage(currentFile);
			PackageID id = makeId(null, currentFile, mcPackage.getTimestamp());
			
			logger.debug("Registered package: " + currentFile + "; using ID: " + id);	
			
			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				register(mcPackage, id);
			}
			else {
				logger.error(currentFile.getAbsolutePath() + " is an invalid package.");
			}
		}
	}
	
	/**
	 * Scans a directory recursively for zipFiles and adds them to the global Collection "zipFiles".
	 * 
	 * @param directory {@link File} - parent directory to scan.
	 * @return A {@link Collection} of {@link File} - the zipfiles found
	 */
	public static Collection<File> scanForZipFiles(File directory) {
		return FileUtils.listFiles(directory, ZIP_EXTENSION, true);
	}
	
	/**
	 * A task which re-computes the directory's fingerprint and
	 * triggers a content reload if required.
	 * 
	 * @author Matthias Mueller
	 *
	 */
	private final class CheckFolder extends TimerTask {
		
		@Override
		public void run() {
			String newFingerprint = RepositoryUtils.directoryFingerprint(directory);
			if (!newFingerprint.equals(fingerprint)){
				logger.info("Repository content has silently changed. Running update ...");
				// set new fingerprint
				fingerprint = newFingerprint;
				// clear an reload
				clear();
				load();
				
				logger.info("Reload finished. Calling Repository Change Listeners.");
				informRepositoryChangeListeners();
			}			
		}
	}
	
	private static final PackageID makeId(String codeSpace, File zipFile, DateTime timeStamp){
		return new PackageID(codeSpace, toNormalizedLocalPath(zipFile), timeStamp.toString());
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
	 * @param {@link String} packageID - some String ID that shall be normalized
	 * @return {@link String} - the normalized ID
	 */
	private static String toNormalizedLocalPath(File file){
		// 0. get as string
		String normalizedID = file.getAbsolutePath();
		
		// 2. replace any of {@value #separatorReplacements} with a {@value #normalizedFileSeparator}
		for (String sequence : Constants.separatorReplacements){
			if (normalizedID.contains(sequence)){
				StringTokenizer st = new StringTokenizer(normalizedID, sequence);
				StringBuffer retval = new StringBuffer(st.nextToken());
				while (st.hasMoreTokens()){
					retval.append(Constants.normalizedFileSeparator + st.nextToken());
				}
				normalizedID = retval.toString();
			}
		}
		
		// 3. reduce consecutive occurrences of {@link File#separator} to one
		normalizedID = removeConsecutiveFileSeparator(normalizedID);
		
		// 4. remove leading normalizedFileSeparator
		if (normalizedID.startsWith(Constants.normalizedFileSeparator)){
			normalizedID = normalizedID.substring(Constants.normalizedFileSeparator.length());
		}
		
		// 5. replace invalid char sequences with File.separator
		for (String ext : Constants.zipExtensions){
			if (normalizedID.endsWith(ext)){
				normalizedID = normalizedID.substring(0, normalizedID.lastIndexOf(ext));
			}
		}
		
		return normalizedID;
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
			if (c == Constants.normalizedFileSeparatorChar) {
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
