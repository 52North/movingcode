package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class provides reading and writing capabilities for plain (unzipped) Moving Code packages.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
final class PlainPackage implements ICodePackage {
	
	// Two elements for a plain (unzipped) package
	private final File plainWorkspace;
	private final PackageDescriptionDocument plainDescription;
	
	// logger
	static Logger logger = Logger.getLogger(PlainPackage.class);
	
	/**
	 * Constructor to create a {@link PlainPackage} from a plain workspace and process description XML.
	 * 
	 * @param workspace {@link File} - plain package workspace
	 * @param descriptionXML {@link PackageDescriptionDocument} - process description XML
	 */
	protected PlainPackage(final File workspace, final PackageDescriptionDocument descriptionXML) {

		this.plainWorkspace = workspace;
		this.plainDescription = descriptionXML;
	}
	
	/**
	 * Constructor to create a {@link PlainPackage} from a plain workspace and process description XML.
	 * 
	 * @param workspace {@link File} - plain package workspace
	 * @param descriptionXML {@link PackageDescriptionDocument} - process description XML
	 */
	protected PlainPackage(final File workspace, final File descriptionXMLFile) {
		// register workspace
		this.plainWorkspace = workspace;
		
		// try to read package description
		// will be null if reading fails
		PackageDescriptionDocument doc = null;
		try {
			doc = PackageDescriptionDocument.Factory.parse(descriptionXMLFile);
		} catch (XmlException e) {
			logger.error("PackageDescription could not be read. " + e.getMessage());
		} catch (IOException e) {
			logger.error("PackageDescription could not be read." + e.getMessage());
		} finally {
			this.plainDescription = doc;
		}
		
		assert this.plainDescription != null;
	}
	
	@Override
	public PackageDescriptionDocument getDescription() {
		return plainDescription;
	}
	@Override
	public void dumpPackage(String workspaceDirName, File targetDirectory) {
		// TODO Auto-generated method stub
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

	@Override
	public boolean dumpPackage(File targetZipFile) {
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
