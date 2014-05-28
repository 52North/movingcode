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
