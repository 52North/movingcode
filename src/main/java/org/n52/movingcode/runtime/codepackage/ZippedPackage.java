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
/**
 * 
 */

package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class provides reading and writing capabilities for zipped Moving Code packages.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
final class ZippedPackage {

	// local copy of the zipped package
	private final File zipFile;

	// Web location (URL) of the zipped package
	private final URL zipURL;
	
	// Two elements for a plain (unzipped) package
	private final File plainWorkspace;
	private final PackageDescriptionDocument plainDescription;
	
	static Logger logger = Logger.getLogger(ZippedPackage.class);

	/**
	 * Constructor to create a {@link ZippedPackage} from a local zipfile (i.e. a zipped package).
	 * 
	 * @param zipURL
	 */
	protected ZippedPackage(final File zipFile) {
		this.zipFile = zipFile;
		this.zipURL = null;
		this.plainWorkspace = null;
		this.plainDescription = null;
	}
	
	/**
	 * Constructor to create a {@link ZippedPackage} from a Web location.
	 * (Which points to a zipped package)
	 * 
	 * @param zipURL
	 */
	protected ZippedPackage(final URL zipURL) {
		this.zipURL = zipURL;
		this.zipFile = null;
		this.plainWorkspace = null;
		this.plainDescription = null;
	}
	
	/**
	 * Constructor to create a {@link ZippedPackage} from a plain workspace and process description XML.
	 * 
	 * @param workspace {@link File} - plain package workspace
	 * @param descriptionXML {@link PackageDescriptionDocument} - process description XML
	 */
	protected ZippedPackage(final File workspace, final PackageDescriptionDocument descriptionXML) {
		this.zipURL = null;
		this.zipFile = null;

		this.plainWorkspace = workspace;
		this.plainDescription = descriptionXML;
	}
	
	/**
	 * Getter for the ProcessDescription.
	 * 
	 * @return {@link PackageDescriptionDocument}
	 */
	protected final PackageDescriptionDocument getDescription() {
		if (isPlain()) {
			return plainDescription;
		}
		else {
			return extractDescription(this);
		}
	}
	
	/**
	 * Writes the content of this {@link ZippedPackage} Object to a plain workspace. 
	 * 
	 * @param workspaceDirName {@link String)
	 * @param targetDirectory {@link File}
	 */
	protected final void dumpPackage(String workspaceDirName, File targetDirectory) {
		
		// if plain (unzipped) package
		if (isPlain()) {
			try {
				Collection<File> files = FileUtils.listFiles(plainWorkspace, null, false);
				for (File file : files) {
					if (file.isDirectory()) {
						FileUtils.copyDirectory(file, targetDirectory);
					}
					else {
						FileUtils.copyFileToDirectory(file, targetDirectory);
					}
				}
			}
			catch (IOException e) {
				logger.error("Error! Could copy from " + plainWorkspace.getAbsolutePath() + " to "
						+ targetDirectory.getAbsolutePath());
			}
		}
		// if zipped package
		else {
			unzipWorkspace(this, workspaceDirName, targetDirectory);
		}

	}

	/**
	 * Static private method to extract the description from a package
	 * 
	 * @param archive {@link ZippedPackage}
	 * @return {@link PackageDescriptionDocument}
	 */
	private static PackageDescriptionDocument extractDescription(ZippedPackage archive) {

		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
		String archiveName = null;

		try {

			ZipInputStream zis = null;
			if (archive.zipFile != null) {
				zis = new ZipInputStream(new FileInputStream(archive.zipFile));
				archiveName = archive.zipFile.getAbsolutePath();
			}
			else if (archive.zipURL != null) {
				zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
				archiveName = archive.zipURL.toString();
			}

			ZipEntry entry;

			while ( (entry = zis.getNextEntry()) != null) {
				if (entry.getName().equalsIgnoreCase(MovingCodePackage.descriptionFileName)) {
					return PackageDescriptionDocument.Factory.parse(zis);
				}
				zis.closeEntry();
			}

		}
		catch (ZipException e) {
			logger.error("Error! Could read from archive: " + archiveName);
		}
		catch (IOException e) {
			logger.error("Error! Could not open archive: " + archiveName);
		}
		catch (XmlException e) {
			logger.error("Error! Could not parse package description from archive: " + archiveName);
		}
		return null;
	}

	//    /*
	//     * creates an unzipped copy of the archive
	//     */
	//    private static void unzipPackage(ZippedPackage archive, File targetDirectory) {
	//
	//        // zipFile and zip url MUST not be null at the same time
	//        assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
	//        String archiveName = null;
	//
	//        try {
	//
	//            ZipInputStream zis = null;
	//            if (archive.zipFile != null) {
	//                zis = new ZipInputStream(new FileInputStream(archive.zipFile));
	//                archiveName = archive.zipFile.getAbsolutePath();
	//            }
	//            else if (archive.zipURL != null) {
	//                zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
	//                archiveName = archive.zipURL.toString();
	//            }
	//
	//            ZipEntry entry;
	//            while ( (entry = zis.getNextEntry()) != null) {
	//                String fileName = entry.getName();
	//                File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);
	//
	//                // create all required directories
	//                new File(newFile.getParent()).mkdirs();
	//
	//                // if current zip entry is not a directory, do unzip
	//                if ( !entry.isDirectory()) {
	//                    FileOutputStream fos = new FileOutputStream(newFile);
	//                    IOUtils.copy(zis, fos);
	//                    zis.close();
	//                    fos.close();
	//                }
	//                zis.closeEntry();
	//            }
	//
	//            // Enumeration<? extends ZipEntry> entries = zis.entries();
	//            // while (entries.hasMoreElements()){
	//            // ZipEntry entry = entries.nextElement();
	//            // String fileName = entry.getName();
	//            // File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);
	//            //
	//            // // create all required directories
	//            // new File(newFile.getParent()).mkdirs();
	//            //
	//            // // if current zip entry is not a directory, do unzip
	//            // if(!entry.isDirectory()){
	//            // FileOutputStream fos = new FileOutputStream(newFile);
	//            // InputStream is = zif.getInputStream(entry);
	//            // IOUtils.copy(is, fos);
	//            // is.close();
	//            // fos.close();
	//            // }
	//            // }
	//        }
	//        catch (ZipException e) {
	//            System.err.println("Error! Could read from archive: " + archiveName);
	//        }
	//        catch (IOException e) {
	//            System.err.println("Error! Could not open archive: " + archiveName);
	//        }
	//        catch (NullPointerException e) {
	//            System.err.println("No archive has been declared. This should not happen ...");
	//        }
	//    }

	/**
	 * Static private method that creates an unzipped copy of the archive.
	 * 
	 * @param archive {@link ZippedPackage}
	 * @param workspaceDirName {@link String}
	 * @param targetDirectory {@link File}
	 */
	private static void unzipWorkspace(ZippedPackage archive, String workspaceDirName, File targetDirectory) {

		// zipFile and zip url and isRaw() MUST not be null at the same time
		assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
		String archiveName = null;

		if (workspaceDirName.startsWith("./")) {
			workspaceDirName = workspaceDirName.substring(2);
		}

		if (workspaceDirName.startsWith(".\\")) {
			workspaceDirName = workspaceDirName.substring(2);
		}

		try {

			ZipInputStream zis = null;
			if (archive.zipFile != null) {
				zis = new ZipInputStream(new FileInputStream(archive.zipFile));
				archiveName = archive.zipFile.getAbsolutePath();
			}
			else if (archive.zipURL != null) {
				zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
				archiveName = archive.zipURL.toString();
			}

			ZipEntry entry;
			while ( (entry = zis.getNextEntry()) != null) {
				if (entry.getName().startsWith(workspaceDirName)) {

					String fileName = entry.getName();
					File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);

					// create all required directories
					new File(newFile.getParent()).mkdirs();

					// if current zip entry is not a directory, do unzip
					if ( !entry.isDirectory()) {
						FileOutputStream fos = new FileOutputStream(newFile);
						IOUtils.copy(zis, fos);
						fos.close();
					}
				}
				zis.closeEntry();
			}
			if (zis != null) {
				zis.close();
			}
		}
		catch (ZipException e) {
			logger.error("Error! Could read from archive: " + archiveName);
		}
		catch (IOException e) {
			logger.error("Error! Could not open archive: " + archiveName);
		}
		catch (NullPointerException e) {
			logger.error("No archive has been declared. This should not happen ...");
		}
	}

	/**
	 * Writes the content of this {@link ZippedPackage} Object to a zipfile. 
	 * 
	 * @param targetZipFile {@link File}
	 * @return boolean - true if the package was successfully dumped, false otherwise
	 */
	protected boolean dumpPackage(File targetZipFile) {
		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (zipFile == null) || (zipURL == null)) || (this.isPlain()));

		// in case there is a zipped package file on disk
		if (zipFile != null) {
			try {
				FileUtils.copyFile(zipFile, targetZipFile);
				return true;
			}
			catch (Exception e) {
				return false;
			}
			// in case there is no file on disk and but a valid url to a zipped package
		}
		else if (zipURL != null) {
			try {
				FileUtils.copyURLToFile(zipURL, targetZipFile);
				return true;
			}
			catch (IOException e) {
				return false;
			}
		}
		// in case there is a plain (non-zipped) package with separate workspace and description
		else if (isPlain()) {
			try {
				ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetZipFile));
				// add package description to zipFile
				zos.putNextEntry(new ZipEntry(MovingCodePackage.descriptionFileName));
				IOUtils.copy(plainDescription.newInputStream(), zos);
				zos.closeEntry();

				// add workspace recursively, with relative pathnames
				File base = plainWorkspace.getAbsoluteFile().getParentFile();
				addDir(plainWorkspace, base, zos);

				zos.close();

				return true;
			}
			catch (IOException e) {
				return false;
			}
		}

		return false;
	}
	
	/**
	 * Is this a plain (unzipped) workspace?
	 * 
	 * @return boolean - true if it is a plain (unzipped) package, false otherwise
	 */
	private boolean isPlain() {
		return (plainWorkspace != null && plainDescription != null);
	}

	/**
	 * Static private helper method that writes contents of a directory (e.g. a workspace)
	 * to a {@link ZipOutputStream}.
	 * 
	 * @param contentDirectory {@link File} - the directory that shall be zipped
	 * @param baseDirectory {@link File} - the part of the @param contentDirectory path that shall be truncated from the zip-Entry
	 * @param zos {@link ZipOutputStream} - the stream to write the directory contents to
	 * @throws IOException - if writing to the stream (zos) fails
	 */
	private static void addDir(File contentDirectory, File baseDirectory, ZipOutputStream zos) throws IOException {
		File[] files = contentDirectory.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				addDir(files[i], baseDirectory, zos);
				continue;
			}
			FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
			// construct relative path
			zos.putNextEntry(new ZipEntry(relative(baseDirectory, files[i])));
			// do copy
			IOUtils.copy(in, zos);

			zos.closeEntry();
			in.close();
		}
	}

	/**
	 * Static private method that removes the <base> part from an absolute path.
	 * 
	 * @param base {@link File} - the <base> part of a path
	 * @param file {@link File} - an absolute path of the structure <base><relative> 
	 * @return {@link String} - the <relative> part of the absolute path 
	 */
	private static String relative(final File base, final File file) {
		final int rootLength = base.getAbsolutePath().length();
		final String absFileName = file.getAbsolutePath();
		final String relFileName = absFileName.substring(rootLength + 1);
		return relFileName;
	}
}