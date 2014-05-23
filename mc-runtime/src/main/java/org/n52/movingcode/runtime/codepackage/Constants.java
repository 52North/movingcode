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
