package org.n52.movingcode.runtime.codepackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

/**
 * A class for package IDs
 * 
 * @author matthias
 *
 */
public class PackageID {
	
	public static final int CODESPACE = 0;
	public static final int ID = 1;
	public static final int VERSION = 2;
	private static final String slash = "/";
	
	private final String[] packageID;
	
	public PackageID (final String codespace, final String id, final String version){
		packageID = new String[3];
		packageID[CODESPACE] = codespace;
		packageID[ID] = id;
		packageID[VERSION] = version;
	}
	
	public PackageID(String filename){
		packageID = readPackageIdFile(new File(filename));
	}
	
	public PackageID(File file){
		packageID = readPackageIdFile(file);
	}
	
	public String getCodespace(){
		return packageID[CODESPACE];
	}
	
	public String getId(){
		return packageID[ID];
	}
	
	public String getVersion(){
		return packageID[VERSION];
	}
	
	public String[] getAsArray(){
		return packageID;
	}
	
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		if (packageID[CODESPACE] != null && !packageID[CODESPACE].isEmpty()){
			sb.append(packageID[CODESPACE]);
			sb.append(slash);
		}
		
		// package ID should never be empty
		// and if it is by mistake we want to know it is null!
		sb.append(packageID[ID]);
		
		if (packageID[VERSION] != null && !packageID[VERSION].isEmpty()){
			sb.append(slash);
			sb.append(packageID[VERSION]);
		}
		
		return sb.toString();
	}
	
	/**
	 * package.id file reader
	 * 
	 * @param file
	 * @return
	 */
	private static final String[] readPackageIdFile (File file){
		// Process input stream and return String Array
		BufferedReader br;
		String line;
		String[] retval = new String[3];
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while ((line = br.readLine()) != null) {
			    // Deal with the line
				String[] kvp = processLine(line);
				if (kvp != null){
					if (kvp[0].equalsIgnoreCase(Constants.KEY_PACKAGEID_CODESPACE)){
						retval[CODESPACE] = kvp[1];
					} else if (kvp[0].equalsIgnoreCase(Constants.KEY_PACKAGEID_IDENTIFIER)){
						retval[ID] = kvp[1];
					} else if (kvp[0].equalsIgnoreCase(Constants.KEY_PACKAGE_VERSION)){
						retval[VERSION] = kvp[1];
					}
				}
			}
			br.close();
		} catch (Exception e){
			// TODO: Log.
		}
		
		return retval;
	}
	
	
	/**
	 * Parse line, return array of strings (key,value)
	 * 
	 * @param line
	 * @return
	 */
	private static final String[] processLine(String line){
		
		// strip leading and trailing spaces
		line = StringUtils.trim(line);
		line = StringUtils.strip(line);
		
		// check no of occurences of "="
		if (StringUtils.countMatches(line, Constants.KEY_PACKAGE_SEPARATOR) != 1){
			// skip line
			return null;
		}
		
		StringTokenizer tokenizer = new StringTokenizer(line, Constants.KEY_PACKAGE_SEPARATOR);
		
		// extract the two values and strip leading and trailing spaces
		String key = tokenizer.nextToken();
		key = StringUtils.trim(key);
		key = StringUtils.strip(key);
		
		String value = tokenizer.nextToken();
		value = StringUtils.trim(value);
		value = StringUtils.strip(value);
		
		return new String[]{key, value};
	}
}
