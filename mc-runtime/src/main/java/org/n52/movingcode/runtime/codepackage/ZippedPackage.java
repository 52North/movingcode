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
package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.lf5.util.StreamUtils;
import org.apache.xmlbeans.XmlException;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class provides reading and writing capabilities for zipped Moving Code packages.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
final class ZippedPackage implements ICodePackage {

	// local copy of the zipped package
	private final File zipFile;

	// Web location (URL) of the zipped package
	private final URL zipURL;

	static Logger logger = Logger.getLogger(ZippedPackage.class);

	/**
	 * Constructor to create a {@link ZippedPackage} from a local zipfile (i.e. a zipped package).
	 * 
	 * @param zipURL
	 */
	protected ZippedPackage(final File zipFile) {
		this.zipFile = zipFile;
		this.zipURL = null;
	}

	/**
	 * Constructor to create a {@link ZippedPackage} from a Web location. (Which points to a zipped package)
	 * 
	 * @param zipURL
	 */
	protected ZippedPackage(final URL zipURL) {
		this.zipURL = zipURL;
		this.zipFile = null;
	}

	@Override
	public final PackageDescriptionDocument getDescription() {
		return extractDescription(this);
	}

	/**
	 * Static private method to extract the description from a package
	 * 
	 * @param archive
	 *        {@link ZippedPackage}
	 * @return {@link PackageDescriptionDocument}
	 */
	private static PackageDescriptionDocument extractDescription(ZippedPackage archive) {

		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (archive.zipFile == null) && (archive.zipURL == null)));
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
				if (entry.getName().equalsIgnoreCase(Constants.PACKAGE_DESCRIPTION_XML)) {
					PackageDescriptionDocument doc = PackageDescriptionDocument.Factory.parse(zis);
					zis.close();
					return doc;
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

	/**
	 * Static private method that creates an unzipped copy of the archive.
	 * 
	 * @param archive
	 *        {@link ZippedPackage}
	 * @param workspaceDirName
	 *        {@link String}
	 * @param targetDirectory
	 *        {@link File}
	 */
	private static void unzipWorkspace(ZippedPackage archive, String workspaceDirName, File targetDirectory) {

		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (archive.zipFile == null) && (archive.zipURL == null)));
		String archiveName = null;

		String wdName = workspaceDirName;
		if (wdName.startsWith("./") || wdName.startsWith(".\\")) {
			wdName = wdName.substring(2);
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
				if (entry.getName().startsWith(wdName)) {

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


	@Override
	public final void dumpPackage(String workspaceDirName, File targetDirectory) {
		unzipWorkspace(this, workspaceDirName, targetDirectory);
	}

	@Override
	public boolean dumpPackage(File targetZipFile) {
		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (zipFile == null) && (zipURL == null)));

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

		return false;
	}

	@Override
	public boolean dumpPackage(OutputStream os) {
		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (zipFile == null) && (zipURL == null)));

		// in case there is a zipped package file on disk
		if (zipFile != null) {
			try {
				FileUtils.copyFile(zipFile, os);
				return true;
			}
			catch (Exception e) {
				return false;
			}
			// in case there is no file on disk and but a valid url to a zipped package
		}
		else if (zipURL != null) {
			try (InputStream is = zipURL.openStream()) {
				IOUtils.copy(is,os);
				return true;
			}
			catch (IOException e) {
				return false;
			}
		}
		
		// we should never get here.
		// TODO: restructure and avoid this clause
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ZippedPackage [zipFile=");
		builder.append(this.zipFile);
		builder.append(", zipURL=");
		builder.append(this.zipURL);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean containsFileInWorkspace(String relativePath) {
		if (relativePath.startsWith("./") || relativePath.startsWith(".\\")){
			relativePath = relativePath.substring(2);
		}
		if (relativePath.startsWith("/") || relativePath.startsWith("\\")){
			relativePath = relativePath.substring(1);
		}

		// zipFile and zip url MUST not be null at the same time
		assert ( ! ( (zipFile == null) && (zipURL == null)));
		String archiveName = null;

		String wsName = getDescription().getPackageDescription().getWorkspace().getWorkspaceRoot();
		if (wsName.startsWith("./") || wsName.startsWith(".\\")) {
			wsName = wsName.substring(2);
		}

		String searchEntry = wsName + "/" + relativePath;


		boolean retval = false;
		try {

			ZipInputStream zis = null;
			if (zipFile != null) {
				zis = new ZipInputStream(new FileInputStream(zipFile));
				archiveName = zipFile.getAbsolutePath();
			}
			else if (zipURL != null) {
				zis = new ZipInputStream(zipURL.openConnection().getInputStream());
				archiveName = zipURL.toString();
			}

			ZipEntry entry;

			while ( (entry = zis.getNextEntry()) != null) {
				if (samePath(entry.getName(), searchEntry)) {
					retval = true;
					zis.closeEntry();
					break;
				} else {
					zis.closeEntry();
				}
			}

			zis.close();

		}
		catch (ZipException e) {
			logger.error("Error! Could read from archive: " + archiveName);
		}
		catch (IOException e) {
			logger.error("Error! Could not open archive: " + archiveName);
		}

		return retval;
	}

	private static final boolean samePath(String p1, String p2){
		p1 = p1.replace("\\", "/");
		p2 = p2.replace("\\", "/");

		return p1.equalsIgnoreCase(p2);
	}

}