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
import java.util.Collection;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.jackrabbit.uuid.UUID;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.n52.movingcode.runtime.codepackage.Constants;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PackageID;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * This class implements an {@link IMovingCodeRepository} for local (unzipped) packages, stored
 * in a flat folder structure. This folder structure shall have the following appearance:
 * 
 * <absPath>-<folder1>-<packagedescription.xml>
 * 			|         \<package.id>
 *          |         \<workspacefolder1>
 *          |
 *          -<folder2>-<packagedescription.xml>
 *          |         \<package.id>
 *          |         \<workspacefolder2>
 *          |
 *          -<folder3>-<packagedescription.xml>
 *          |         \<package.id>
 *          |         \<workspacefolder3>
 *          |
 *          ...
 *          |
 *          -<folderN>-<processdescriptionN>
 *                    \<package.id>
 *                    \<workspacefolderN>
 * 
 * For any sub-folders found in <absPath> it will be assumed that it contains a plain (unzipped)
 * Moving Code package.
 * 
 * The file <packagedescription.xml> contains the description of the MovingCode package.
 * 
 * The file <package.id> contains the hierarchical ID of that package, usually a URL path or path fragment.
 * The following layout / mapping is used:
 * (mapping to {@link PackageID})
 * 
 * <packageroot>/<username>/<collectionname>/<packagename>/<timestamp>
 * 
 * pageroot => namespace
 * identifier => <username>/<collectionname>/<packagename>
 * timestamp => version
 * 
 * Performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link IMovingCodeRepository#localPollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class LocalVersionedFileRepository extends AbstractRepository {
	
	private final File directory;
	private String fingerprint;
	private Timer timerDaemon;
	
	private static final HashMap<PackageID,String> packageFolders = new HashMap<PackageID,String>();
	
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
		// compute directory fingerprint
		fingerprint = RepositoryUtils.directoryFingerprint(directory);
		
		// load packages from folder
		load();
		
		// start timer daemon
		timerDaemon = new Timer(true);
		timerDaemon.scheduleAtFixedRate(new CheckFolder(), 0, IMovingCodeRepository.localPollingInterval);
	}
	
	public synchronized void addPackage(File workspace, PackageDescriptionDocument pd, PackageID pid){
		// 1. create moving code packe object
		// 2. make new directory with UUID
		// 3. put packagedescription XML in place
		// 4. dump workspace to repo
		// 5. dump packageid to repo
		
		MovingCodePackage mcp = new MovingCodePackage(workspace, pd, new DateTime(pid.getVersion()));
		File targetDir = makeNewDirectory();
		mcp.dumpWorkspace(targetDir);
		mcp.dumpDescription(targetDir);
		pid.dump(targetDir);
		
		// 6. register package
		register(mcp, pid);
		packageFolders.put(pid, targetDir.getAbsolutePath());
		
	}
	
	public synchronized void addPackage(MovingCodePackage mcp, PackageID pid){
		File targetDir = makeNewDirectory();
		mcp.dumpWorkspace(targetDir);
		mcp.dumpDescription(targetDir);
		pid.dump(new File(targetDir.getAbsolutePath() + File.separator + Constants.PACKAGE_DESCRIPTION_XML));
		
		// finally: register package
		register(mcp, pid);
	}
	
	/**
	 * Remove a package with a given ID from this repository
	 * 
	 * @param pid
	 * @return
	 */
	public boolean removePackage(PackageID pid){
		
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
	
	/**
	 * private method that encapsulates the logic for loading zipped
	 * MovingCode packages from a local folder.  
	 */
	private final void load(){
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> packageFolders = scanForFolders(directory);

		logger.info("Scanning directory: " + directory.getAbsolutePath());
		
		
		for (File currentFolder : packageFolders) {
			
			// attempt to read packageDescription XML
			File packageDescriptionFile = new File(currentFolder, Constants.PACKAGE_DESCRIPTION_XML);
			if (!packageDescriptionFile.exists()){
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
			
			// attempt to read package.id
			File packageIdFile = new File(currentFolder, Constants.PACKAGE_ID_FILE);
			PackageID packageId = PackageID.parse(packageIdFile);
			
			// packageID = absolute path
			logger.info("Found package: " + currentFolder + "; using ID: " + packageId.toString());
			
			// attempt to access workspace root folder
			String workspace = pd.getPackageDescription().getWorkspace().getWorkspaceRoot();
			if (workspace.startsWith("./")){
				workspace = workspace.substring(2); // remove leading "./" if it exists
			}
			File workspaceDir = new File(currentFolder, workspace);
			if (!workspaceDir.exists()){
				continue; // skip this and immediately jump to the next iteration
			}

			MovingCodePackage mcPackage = new MovingCodePackage(workspaceDir, pd, null);
			
			
			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				register(mcPackage, packageId);
			}
			else {
				logger.error(currentFolder.getAbsolutePath() + " is an invalid package.");
			}
		}
	}
	
	/**
	 * Scans a directory for subfolders (i.e. immediate child folders) and adds them
	 * to the resulting Collection.
	 * 
	 * @param directory {@link File} - parent directory to scan.
	 * @return {@link Collection} of {@link File} - the directories found
	 */
	private static final Collection<File> scanForFolders(File directory) {
		return FileUtils.listFiles(directory, FileFilterUtils.directoryFileFilter(), null);
	}
	
	/**
	 * Generate a new directory.
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
}
