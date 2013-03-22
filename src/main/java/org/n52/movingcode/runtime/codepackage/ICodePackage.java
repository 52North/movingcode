package org.n52.movingcode.runtime.codepackage;

import java.io.File;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public interface ICodePackage {
	
	/**
	 * Getter for the ProcessDescription.
	 * 
	 * @return {@link PackageDescriptionDocument}
	 */
	public PackageDescriptionDocument getDescription();
	
	/**
	 * Writes the content of this {@link ICodePackage} Object to a plain workspace. 
	 * 
	 * @param workspaceDirName {@link String)
	 * @param targetDirectory {@link File}
	 */
	public void dumpPackage(String workspaceDirName, File targetDirectory);
	
	/**
	 * Writes the content of this {@link ICodePackage} Object to a zipfile. 
	 * 
	 * @param targetZipFile {@link File}
	 * @return boolean - true if the package was successfully dumped, false otherwise
	 */
	public boolean dumpPackage(File targetZipFile);
	
	/**
	 * TODO: deep compare: Package introspection to see if ICodePackage packages have the same contents
	 * 
	 */
}