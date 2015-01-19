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
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;

import org.apache.xmlbeans.XmlException;
import org.n52.movingcode.runtime.codepackage.Constants;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class implements an {@link MovingCodeRepository} for local plain (unzipped) packages, stored
 * in a nested folder structure. This folder structure shall have the following appearance:
 * 
 * <absPath>-<folder1>-<packagedescription.xml>
 *          |         \<workspacefolder1>
 *          |
 *          -<folder2>-<packagedescription.xml>
 *          |         \<workspacefolder2>
 *          |
 *          -<folder3>-<packagedescription.xml>
 *          |         \<workspacefolder3>
 *          |
 *          ...
 *          |
 *          -<folderN>-<processdescriptionN>
 *                    \<workspacefolderN>
 * 
 * For any sub-folder found in <absPath> it will be assumed that it potentially contains a plain (unzipped)
 * Code Package. Thus, if the parser encounters any <packagedescription.xml> file, it will attempt an
 * interpretation as a package description.
 * 
 * This Repo performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link MovingCodeRepository#localPollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public final class LocalPlainRepository extends AbstractRepository {

	private final File directory;

	private final Thread updateThread;


	/**
	 * 
	 * Constructor for file system based repositories. Scans all sub-directories of a given sourceDirectory
	 * for valid workspaces and attempt to interpret them as MovingCodePackages.
	 * Incomplete or malformed packages will be ignored.
	 * 
	 * @param sourceDirectory {@link File} - the directory to be scanned for Moving Code Packages.
	 * 
	 */
	public LocalPlainRepository(File sourceDirectory) {
		this.directory = sourceDirectory;

		// load packages from folder
		updateContent();

		// start update thread
		updateThread = new UpdateInventoryThread();
		updateThread.start();
	}
	
	private synchronized void updateContent(){
		
		PackageInventory newInventory = new PackageInventory();
		
		// recursively obtain all folders in sourceDirectory
		Path repoRoot = FileSystems.getDefault().getPath(directory.getAbsolutePath());
		Collection<Path> potentialPackageFolders = listSubdirs(repoRoot);

		logger.info("Scanning directory: " + directory.getAbsolutePath());
		
		for (Path currentFolder : potentialPackageFolders) {
			
			// attempt to read packageDescription XML
			File packageDescriptionFile = new File(currentFolder.toFile(), Constants.PACKAGE_DESCRIPTION_XML);
			if (!packageDescriptionFile.exists()){
				continue; // skip this and immediately jump to the next iteration
			}

			PackageDescriptionDocument pd = null;
			try {
				pd = PackageDescriptionDocument.Factory.parse(packageDescriptionFile);
			} catch (XmlException e) {
				// silently skip this and immediately jump to the next iteration
				continue;
			} catch (IOException e) {
				// silently skip this and immediately jump to the next iteration
				continue;
			}

			// attempt to access workspace root folder
			String workspace = pd.getPackageDescription().getWorkspace().getWorkspaceRoot();
			if (workspace.startsWith("./")){
				workspace = workspace.substring(2); // remove leading "./" if it exists
			}
			File workspaceDir = new File(currentFolder.toFile(), workspace);
			if (!workspaceDir.exists()){
				continue; // skip this and immediately jump to the next iteration
			}

			MovingCodePackage mcPackage = new MovingCodePackage(workspaceDir, pd);
			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				newInventory.add(mcPackage);
				logger.info("Found package: " + currentFolder + "; using ID: " + mcPackage.getPackageId().toString());
			}
			else {
				logger.error(currentFolder + " is an invalid package.");
			}
		}
		
		this.updateInventory(newInventory);
	}


	private static final Collection<Path> listSubdirs(Path path) {
		Collection<Path> dirs = new HashSet<Path>();
		DirectoryStream<Path> stream;
		try {
			stream = Files.newDirectoryStream(path);
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					dirs.add(entry);
					dirs.addAll(listSubdirs(entry));
				}
				//		        files.add(entry);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return dirs;
	}

	/**
	 * A thread that occasionally updates the repo's inventory
	 * triggers a content reload if required.
	 * 
	 * @author Matthias Mueller
	 *
	 */
	private final class UpdateInventoryThread extends Thread {
		
		private static final long updateInterval = MovingCodeRepository.localPollingInterval;
		
		@Override
		public void run() {

			while(true){ // spin forever

				logger.debug("Update thread started."
						+"\nDirectory: " + directory.getAbsolutePath()
						+ "\nUpdate interval: " + updateInterval + " milliseconds"
						);

				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e1) {
					logger.debug("Interrupt received. Update thread stopped.");
					this.interrupt();
				}
				
				updateContent();
			}

		}
	}

	@Override
	protected void finalize() throws Throwable {
		updateThread.interrupt();
		super.finalize();
	}
}
