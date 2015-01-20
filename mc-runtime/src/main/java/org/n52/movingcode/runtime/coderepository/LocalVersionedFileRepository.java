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
import java.util.HashMap;
import java.util.HashSet;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.uuid.UUID;
import org.apache.xmlbeans.XmlException;
import org.n52.movingcode.runtime.codepackage.Constants;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * This class implements an {@link MovingCodeRepository} for local (unzipped) packages, stored
 * in a flat folder structure. This folder structure shall have the following appearance:
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
 * For any sub-folders found in <absPath> it will be assumed that it contains a plain (unzipped)
 * Moving Code package.
 * 
 * The file <packagedescription.xml> contains the description of the MovingCode package.
 * 
 * This Repo performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link MovingCodeRepository#localPollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class LocalVersionedFileRepository extends AbstractRepository {

	private final File directory;
	private final Thread updateThread;

	private static final HashMap<PID,String> packageFolders = new HashMap<PID,String>();

	/**
	 * 
	 * Constructor for file system based repositories. Scans all sub-directories of a given sourceDirectory
	 * for valid workspaces and attempt to interpret them as MovingCodePackages.
	 * Incomplete or malformed packages will be ignored.
	 * 
	 * @param sourceDirectory {@link File} - the directory to be scanned for Moving Code Packages.
	 * 
	 */
	public LocalVersionedFileRepository(File sourceDirectory) {
		this.directory = sourceDirectory;

		// load packages from folder
		reloadContent();

		// start update thread
		updateThread = new UpdateInventoryThread();
		updateThread.start();
	}

	public synchronized void addPackage(File workspace, PackageDescriptionDocument pd){
		// 1. create moving code packe object
		// 2. make new directory with UUID
		// 3. put packagedescription XML in place
		// 4. dump workspace to repo
		// 5. dump packageid to repo

		MovingCodePackage mcp = new MovingCodePackage(workspace, pd);
		File targetDir = makeNewDirectory();
		mcp.dumpWorkspace(targetDir);
		mcp.dumpDescription(new File(targetDir.getAbsolutePath() + File.separator + Constants.PACKAGE_DESCRIPTION_XML));

		// 6. register package
		register(mcp);
		packageFolders.put(mcp.getPackageId(), targetDir.getAbsolutePath());

	}

	public synchronized void addPackage(MovingCodePackage mcp){
		File targetDir = makeNewDirectory();
		mcp.dumpWorkspace(targetDir);
		mcp.dumpDescription(new File(targetDir.getAbsolutePath() + File.separator + Constants.PACKAGE_DESCRIPTION_XML));

		// finally: register package
		register(mcp);
	}

	/**
	 * Remove a package with a given ID from this repository
	 * 
	 * @param pid
	 * @return
	 */
	public boolean removePackage(PID pid){

		// 1. unregister package, so it cannot be found any longer
		// 2. remove directory
		// 3. TODO: care for errors in case something goes wrong to make sure we are
		//    not left in an undefined state 
		unregister(pid);

		File packageDir = new File(packageFolders.get(pid));
		try {
			FileUtils.cleanDirectory(packageDir);
			FileUtils.deleteDirectory(packageDir);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// 4. remove folder from inventory
		packageFolders.remove(pid);

		return true;
	}


	private final synchronized void reloadContent(){
		
		PackageInventory newInventory = new PackageInventory();

		// obtain all immediate subfolders
		Path repoRoot = FileSystems.getDefault().getPath(directory.getAbsolutePath());
		Collection<Path> packageFolders = listSubdirs(repoRoot);

		logger.info("Scanning directory: " + directory.getAbsolutePath());


		for (Path currentFolder : packageFolders) {

			// attempt to read packageDescription XML
			File packageDescriptionFile = new File(currentFolder.toFile(), Constants.PACKAGE_DESCRIPTION_XML);
			// deal with empty inventory folders
			if (!packageDescriptionFile.exists()){
				// TODO: remove such invalid folders?
				logger.warn("Found empty inventory folder: " + currentFolder.toAbsolutePath());
				continue; // skip this and immediately jump to the next iteration
			}

			PackageDescriptionDocument pd;
			try {
				pd = PackageDescriptionDocument.Factory.parse(packageDescriptionFile);
			} catch (XmlException e) {
				// silently skip this and immediately jump to the next iteration
				continue;
			} catch (IOException e) {
				// silently skip this and immediately jump to the next iteration
				continue;
			}

			// packageID = absolute path
			logger.info("Found package: " + currentFolder + "; using ID: " + RepositoryUtils.extractId(pd).toString());

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
		
		// announce new content scan
		updateInventory(newInventory);
	}

	/**
	 * Scans the root directory for subfolders and returns them.
	 * In this type of repo, each subfolder SHALL contain a single package.
	 * 
	 * @param path
	 * @return
	 */
	private static final Collection<Path> listSubdirs(Path path) {
		Collection<Path> dirs = new HashSet<Path>();
		DirectoryStream<Path> stream;
		try {
			stream = Files.newDirectoryStream(path);
			for (Path entry : stream) {
				if (Files.isDirectory(entry)) {
					dirs.add(entry);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return dirs;
	}

	/**
	 * Generate a new directory; use a UUID for this task.
	 * 
	 * @return
	 */
	private final File makeNewDirectory(){
		String dirName = directory.getAbsolutePath() + File.separator + UUID.randomUUID().toString();
		File newDir = new File(dirName);
		newDir.mkdirs();
		return newDir;
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
					sleep(updateInterval);
				} catch (InterruptedException e1) {
					logger.debug("Interrupt received. Update thread stopped.");
					this.interrupt();
				}
				
				// trigger update routine
				reloadContent();
			}

		}
	}

	@Override
	protected void finalize() throws Throwable {
		updateThread.interrupt();
		super.finalize();
	}
}