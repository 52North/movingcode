package org.n52.movingcode.runtime.processors;

import java.security.SecureRandom;

/**
 * A Class that creates an Almost Unique ID (AUID).
 * 
 * @author Matthias Mueller, Geoinformation Systems, TU Dresden
 *
 */
public class AUID {
	/*
	 * The random number generator used by this class to create random
	 * based AUIDs.
	 */
	private static volatile SecureRandom numberGenerator = null;
	
	private static final String alphabetPlusNum = "0123456789abcdefghijklmnopqrstuvwxyz";
	private static final String alphabet = "abcdefghijklmnopqrstuvwxyz";

	/**
	 * Creates a random AUID
	 * 
	 * @return - an eight digit alphanumeric string starting with a letter 
	 */
	public static String randomAUID() {
		SecureRandom ng = numberGenerator;
		if (ng == null) {
			numberGenerator = ng = new SecureRandom();
		}
		
		char[] auid = new char[8];
		// make the first element a character
		auid[0] = alphabet.charAt(ng.nextInt(alphabet.length()));
		
		// make the other characters alphanumeric
		for (int i = 1; i < 8; i++) {
	        auid[i] = alphabetPlusNum.charAt(ng.nextInt(alphabet.length()));
	    }
		
		return new String(auid);
	}
	
//	public static void main(String args[]){
//		for (int i=0; i<100; i++){
//			System.out.println(randomAUID());
//		}
//	}
}
