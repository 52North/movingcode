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
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

/**
 * This class implements an {@link MovingCodeRepository} for local zipped packages, stored
 * in a possibly nested folder structure.
 * 
 * <absPath>-<folder1>\<zip1.1>
 *          |         \<zip1.2>
 *          |         \<zip1.3>
 *          |
 *          -<folder2>\<zip2.1>
 *          |         \<zip2.2>
 *          |
 *          -<folder3>\<zip3.1>
 *          |         \<zip1.2>
 *          |         \<zip1.3>
 *          |         \<zip1.4>
 *          |         \<zip1.5>
 *          |
 *          ...
 *          |
 *          -<folderN>\<zipN.1>
 *                    \<zipN.2>
 * 
 * For any zip file found in <absPath> it will be assumed that it potentially contains a zipped
 * Code Package. Thus, if the parser encounters a zip file, it will attempt an
 * interpretation as a Code Package.
 * 
 * This Repo performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link MovingCodeRepository#localPollingInterval})
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
		reloadContent();

		// start timer daemon
		timerDaemon = new Timer(true);
		timerDaemon.scheduleAtFixedRate(new CheckFolder(), 0, MovingCodeRepository.localPollingInterval);
	}

	private synchronized void reloadContent(){
		PackageInventory newInventory = new PackageInventory();
		
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> zipFiles = scanForZipFiles(directory);

		logger.info("Scanning directory: " + directory.getAbsolutePath());

		for (File currentFile : zipFiles) {
			logger.debug("Found package: " + currentFile);

			MovingCodePackage mcPackage = new MovingCodePackage(currentFile);

			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				newInventory.add(mcPackage);
				logger.debug("Registered package: " + currentFile + "; using ID: " + mcPackage.getPackageId().toString());	
			} else {
				logger.error(currentFile.getAbsolutePath() + " is an invalid package.");
			}

		}
		
		updateInventory(newInventory);
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
				
				// ... and trigger reload
				reloadContent();
			}			
		}
	}

}
