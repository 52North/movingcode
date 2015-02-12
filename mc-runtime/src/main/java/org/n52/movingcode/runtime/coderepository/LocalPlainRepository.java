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

		LOGGER.info("Scanning directory: " + directory.getAbsolutePath());
		
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
				LOGGER.info("Found package: " + currentFolder + "; using ID: " + mcPackage.getPackageId().toString());
			}
			else {
				LOGGER.error(currentFolder + " is an invalid package.");
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

				LOGGER.debug("Update thread started."
						+"\nDirectory: " + directory.getAbsolutePath()
						+ "\nUpdate interval: " + updateInterval + " milliseconds"
						);

				try {
					Thread.sleep(updateInterval);
				} catch (InterruptedException e1) {
					LOGGER.debug("Interrupt received. Update thread stopped.");
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
