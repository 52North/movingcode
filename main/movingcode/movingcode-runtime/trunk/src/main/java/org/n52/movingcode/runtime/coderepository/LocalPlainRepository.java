package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.xmlbeans.XmlException;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class implements an {@link IMovingCodeRepository} for local plain (unzipped) packages, stored
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
 * @author Matthias Mueller, TU Dresden
 *
 */
public final class LocalPlainRepository extends AbstractRepository {
	// valid name for the package description XML file
	private static final String PACKAGE_DESCRIPTION_XML = "packagedescription.xml";
	
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
		
		// recursively obtain all zipfiles in sourceDirectory
		Collection<File> packageFolders = scanForFolders(sourceDirectory);

		logger.info("Scanning directory: " + sourceDirectory.getAbsolutePath());
		
		
		
		for (File currentFolder : packageFolders) {
			
			// attempt to read packageDescription XML
			File packageDescriptionFile = new File(currentFolder, PACKAGE_DESCRIPTION_XML);
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
			
			// attempt to access workspace root folder
			String workspace = pd.getPackageDescription().getWorkspace().getWorkspaceRoot();
			if (workspace.startsWith("./")){
				workspace = workspace.substring(2); // remove leading "./" if it exists
			}
			File workspaceDir = new File(currentFolder, workspace);
			if (!workspaceDir.exists()){
				continue; // skip this and immediately jump to the next iteration
			}
			
			// guess timestamp
			Date timestamp = new Date(lastFileModified(currentFolder));
			
			// packageID = absolute path
			String id = currentFolder.getPath();
			
			logger.info("Found package: " + currentFolder + "; using ID: " + id);

			MovingCodePackage mcPackage = new MovingCodePackage(workspaceDir, pd, timestamp, id);
			// validate
			// and add to package map
			// and add current file to zipFiles map
			if (mcPackage.isValid()) {
				register(mcPackage);
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
	private static Collection<File> scanForFolders(File directory) {
		return FileUtils.listFiles(directory, FileFilterUtils.directoryFileFilter(), null);
	}
	
	/**
	 * Finds the last modified date of a directory by scanning it's contents
	 * 
	 * TODO: does this also check the modification date of directories?
	 * 
	 * @param directory {@link File} - the directory to be scanned
	 * @return 
	 */
	private static long lastFileModified(File directory) {
		// recursively find all files in subdirectory 
		Collection<File> files = FileUtils.listFiles(directory, null, true);
		
		// initialize with modification date of the directory argument
		long lastMod = directory.lastModified(); 
		
		for (File file : files) {
			if (file.lastModified() > lastMod) {
				lastMod = file.lastModified();
			}
		}
		return lastMod;
	}
}
