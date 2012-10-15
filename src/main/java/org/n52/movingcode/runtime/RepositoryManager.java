package org.n52.movingcode.runtime;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;


/**
 * The Repository Manager is a singleton instance that manages all
 * active Moving Code Repositories.
 * 
 * Considered thread safe.
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class RepositoryManager {
	
	private static RepositoryManager instance;
	private Map<String,MovingCodeRepository> repositories = Collections.synchronizedMap(new HashMap<String,MovingCodeRepository>());
	
	private RepositoryManager (){
		super();
	}
	
	public static synchronized RepositoryManager getInstance(){
		if (instance == null){
			instance = new RepositoryManager();
		}
		return instance;
	}
	
	public boolean addRepository(String directory){
		if (!repositories.containsKey(directory)){
			return repositories.put(directory, new MovingCodeRepository(new File(directory))) != null;
		} else {
			return false;
		}
	}
	
	public boolean addRepository(URL atomFeedURL){
		if (!repositories.containsKey(atomFeedURL.toString())){
			return repositories.put(atomFeedURL.toString(), new MovingCodeRepository(atomFeedURL)) != null;
		} else {
			return false;
		}
	}
	
	/**
	 * Finds you a package for a given identifier. This method will return the first
	 * Package matching this identifier. To check for multiple occurrences of the same
	 * identifier call {@link RepositoryManager#checkMultiplicityOfPackage(String)}  
	 * 
	 * @param functionalIdentifier
	 * @return {@link MovingCodePackage}
	 */
	public MovingCodePackage getPackage(String functionalIdentifier){
		for (MovingCodeRepository repo : repositories.values()){
			if (repo.containsPackage(functionalIdentifier)){
				return repo.getPackage(functionalIdentifier);
			}
		}
		return null;
	}
	
	/**
	 * Method used for checking multiple occurrences of the same process ID.
	 * 
	 * @param identifier
	 * @return int - multiplicity
	 */
	public int checkMultiplicityOfPackage(String identifier){
		int counter = 0;
		
		for (MovingCodeRepository repo : repositories.values()){
			if (repo.containsPackage(identifier)){
				counter++;
			}
		}
		
		return counter;
	}
	
	/**
	 * Contains check: Is a there a package registered for a given functional ID?
	 * 
	 * @param identifier
	 * @return boolean
	 */
	public boolean providesFunction(String functionalID){
		for (MovingCodeRepository repo : repositories.values()){
			if (repo.providesFunction(functionalID)){
				return true;
			}
		}
		return false;
	}
	
	public String[] getProcessIDs(){
		ArrayList<String> retval= new ArrayList<String>();
		for (MovingCodeRepository repo : repositories.values()){
			retval.addAll(Arrays.asList(repo.getFunctionalIDs()));
		}
		return retval.toArray(new String[retval.size()]);
	}
	
	public String[] getRegisteredRepositories(){
		return repositories.keySet().toArray(new String[repositories.size()]);
	}
	
	public boolean isRegisteredRepository (String directory){
		return repositories.containsKey(directory);
	}
	
	public boolean isRegisteredRepository (URL atomFeedURL){
		return repositories.containsKey(atomFeedURL.toString());
	}
	
	public void removeRepository(String directory){
		repositories.remove(directory);
	}
	
	public void removeRepository(URL atomFeedURL){
		repositories.remove(atomFeedURL.toString());
	}
	
}
