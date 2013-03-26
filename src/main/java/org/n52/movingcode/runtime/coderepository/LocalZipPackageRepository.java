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

import org.apache.commons.io.FileUtils;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

/**
 * This class implements an {@link IMovingCodeRepository} for local zipped packages, stored
 * in a folder structure.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public final class LocalZipPackageRepository extends AbstractRepository {

	private static final String[] ZIP_EXTENSION = {"zip"};

	/**
	 * 
	 * Constructor for file system based repositories. Scans all sub-directories of a given sourceDirectory
	 * for zip-Files and attempts to interpret them as MovingCodePackages. Zipfiles that do not validate will
	 * be ignored
	 * 
	 * @param sourceDirectory {@link File} - the directory to be scanned for Moving Code Packages.
	 * 
	 */
	public LocalZipPackageRepository(File sourceDirectory) {
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> zipFiles = scanForZipFiles(sourceDirectory);

		logger.info("Scanning directory: " + sourceDirectory.getAbsolutePath());

		for (File currentFile : zipFiles) {
			String id = generateIDFromFilePath(currentFile.getPath());
			logger.info("Found package: " + currentFile + "; using ID: " + id);

			MovingCodePackage mcPackage = new MovingCodePackage(currentFile, id);

			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				register(mcPackage);
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
	private static Collection<File> scanForZipFiles(File directory) {
		return FileUtils.listFiles(directory, ZIP_EXTENSION, true);
	}

	/**
	 * Private method that generates a packageID from a file path. Since the file path should be a unique ID
	 * for each zip file on disk, the generated ID is also unique for each package registered with this
	 * repository.
	 * 
	 * @param filePath {@link String} - Path
	 * @return
	 */
	private static final String generateIDFromFilePath(String filePath) {
		String id = "";

		// trim ".zip"
		for (String extension : ZIP_EXTENSION) {
			if (filePath.endsWith(extension)) {
				id = filePath.substring(0, filePath.lastIndexOf(extension));
			}
		}

		// substitute \ with /
		id = id.replace("\\", "/");

		return id;
	}
}
