package org.n52.movingcode.runtime.codepackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
	
	private PackageID (String[] packageId){
		this.packageID = packageId;
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
	
	/**
	 * Qualified Id is composed of codespace and id
	 * 
	 * @return
	 */
	public String getQualifiedId(){
		StringBuffer sb = new StringBuffer();
		if (packageID[CODESPACE] != null && !packageID[CODESPACE].isEmpty()){
			sb.append(packageID[CODESPACE]);
			sb.append(slash);
		}
		
		// package ID should never be empty
		// and if it is by mistake we want to know it is null!
		sb.append(packageID[ID]);
		
		return sb.toString();
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
	
	@Override
	public boolean equals(Object o){
		if (this == o){
			return true;
		}
		if (o instanceof PackageID){
			String anotherString = (String) o;
			return this.getId().equalsIgnoreCase(anotherString);
		} else {
			return false;
		}
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
					} else if (kvp[0].equalsIgnoreCase(Constants.KEY_PACKAGEID_VERSION)){
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
	 * Static method for writing package IDs
	 * 
	 * @param file
	 * @param pid
	 * @return
	 */
	private static final boolean writePackageIdFile (File file, PackageID pid){
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(Constants.KEY_PACKAGEID_CODESPACE + Constants.KEY_PACKAGE_SEPARATOR + pid.getCodespace());
			fw.write(Constants.KEY_PACKAGEID_IDENTIFIER + Constants.KEY_PACKAGE_SEPARATOR + pid.getId());
			fw.write(Constants.KEY_PACKAGEID_VERSION + Constants.KEY_PACKAGE_SEPARATOR + pid.getVersion());
			fw.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
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
	
	/**
	 * Parse Id from id file
	 * 
	 * @param file
	 * @return
	 */
	public static final PackageID parse(File file){
		return new PackageID(readPackageIdFile(file));
	}
	
	/**
	 * Parse from id file
	 * 
	 * @param filePath
	 * @return
	 */
	public static final PackageID parse(String filePath){
		return new PackageID(readPackageIdFile(new File(filePath)));
	}
	
	/**
	 * dumps an id file to a given directory
	 * 
	 * @param targetDir
	 */
	public final void dump(File targetDir){
		File idFile = new File(targetDir.getAbsolutePath() + File.separator + Constants.PACKAGE_ID_FILE);
		writePackageIdFile(idFile, this);
	}
	
//	/**
//	 * Normalizes any given packageID so that it can be used to create a local file path.
//	 * The following operations are performed:
//	 * 
//	 * 1. remove {@value #httpPrefix}
//	 * 2. replace any of {@value #separatorReplacements} with a {@value #normalizedFileSeparator}
//	 * 3. collapse consecutive occurrences of {@value #normalizedFileSeparator} to one
//	 * 4. remove a leading {@value #normalizedFileSeparator}
//	 * 5. remove trailing {@value #zipExtension}
//	 * 
//	 * @param {@link String} packageID - some String ID that shall be normalized
//	 * @return {@link String} - the normalized ID
//	 */
//	private static String toNormalizedLocalPath(final String packageID){
//		// 0. get as string
//		String normalizedID = packageID.toString();
//		
//		// 1. remove {@value #httpPrefix}
//		if (normalizedID.startsWith(Constants.httpPrefix)){
//			normalizedID = normalizedID.substring(Constants.httpPrefix.length());
//		}
//		
//		// 2. replace any of {@value #separatorReplacements} with a {@value #normalizedFileSeparator}
//		for (String sequence : Constants.separatorReplacements){
//			if (normalizedID.contains(sequence)){
//				StringTokenizer st = new StringTokenizer(normalizedID, sequence);
//				StringBuffer retval = new StringBuffer(st.nextToken());
//				while (st.hasMoreTokens()){
//					retval.append(Constants.normalizedFileSeparator + st.nextToken());
//				}
//				normalizedID = retval.toString();
//			}
//		}
//		
//		// 3. reduce consecutive occurrences of {@link File#separator} to one
//		normalizedID = removeConsecutiveFileSeparator(normalizedID);
//		
//		// 4. remove leading normalizedFileSeparator
//		if (normalizedID.startsWith(Constants.normalizedFileSeparator)){
//			normalizedID = normalizedID.substring(Constants.normalizedFileSeparator.length());
//		}
//		
//		// 5. replace invalid char sequences with File.separator
//		for (String ext : Constants.zipExtensions){
//			if (normalizedID.endsWith(ext)){
//				normalizedID = normalizedID.substring(0, normalizedID.lastIndexOf(ext));
//			}
//		}
//		
//		return normalizedID;
//	}
//	
//	/**
//	 * Remove consecutive occurrences of file separator character in s
//	 * 
//	 * @param s the string to parse.
//	 * @return s without consecutive occurrences of file separator character
//	 */
//	private static String removeConsecutiveFileSeparator(final String s) {
//		StringBuffer res = new StringBuffer();
//		boolean previousWasFileSep = false;
//		for (int i = 0; i < s.length(); i++) {
//			char c = s.charAt(i);
//			if (c == Constants.normalizedFileSeparatorChar) {
//				if (!previousWasFileSep) {
//					res.append(c);
//					previousWasFileSep = true;
//				}
//			} else {
//				previousWasFileSep = false;
//				res.append(c);
//			}
//		}
//		return res.toString();
//	}

}
