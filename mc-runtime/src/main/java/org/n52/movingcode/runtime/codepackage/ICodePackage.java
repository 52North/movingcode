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
package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.OutputStream;

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
	 * Writes the content of this {@link ICodePackage} Object to an output stream. 
	 * 
	 * @param os
	 * @return
	 */
	public boolean dumpPackage(OutputStream os);
	
	/**
	 * 
	 * 
	 * @param relativePath
	 * @return
	 */
	public boolean containsFileInWorkspace(String relativePath);
	
	/**
	 * TODO: deep compare: Package introspection to see if ICodePackage packages have the same contents
	 * 
	 */
}
