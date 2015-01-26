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

public class Constants {
	// common name for the package description XML file
	public static final String PACKAGE_DESCRIPTION_XML = "packagedescription.xml";

	// common name for ID file
	// this file should not be included in the zipped version
	public static final String PACKAGE_ID_FILE = "package.id";

	// default extension for zipped packages
	public static final String defaultZipExtension = ".zip";

	// typical extensions for zipped packages
	public static final String[] zipExtensions = {Constants.defaultZipExtension};

	// common separator char in nomalizedPackageID is a slash [/]
	public static final String normalizedFileSeparator = "/";
	public static final char normalizedFileSeparatorChar = '/';

	// HTTP-URI prefix
	static final String httpPrefix = "http://";

	// strings that shall be replaces by File.separator
	public static final String[] separatorReplacements = {"\\", File.separator, ":/", ":\\\\", "\\\\", ";"};

	public static final String KEY_PACKAGEID_CODESPACE = "codespace";
	public static final String KEY_PACKAGEID_IDENTIFIER = "name";
	public static final String KEY_PACKAGEID_VERSION = "version";

	public static final String KEY_PACKAGE_SEPARATOR = "=";

	public static enum FunctionalType {
		WPS100, WPS200
	};

}
