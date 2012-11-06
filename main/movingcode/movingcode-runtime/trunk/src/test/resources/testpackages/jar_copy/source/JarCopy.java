package de.tudresden.gis.movingcode.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JarCopy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File inFile = new File(args[0]);
		File outFile = new File(args[1]);

		try {
			InputStream is = new FileInputStream(inFile);
			OutputStream os = new FileOutputStream(outFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = is.read(buf)) > 0){
				os.write(buf, 0, len);
			}
			is.close();
			os.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found! " + e.getMessage());
		} catch (IOException e) {
			System.out.println("Could not copy file! " + e.getMessage());
		}
		
		
	}
	
}
