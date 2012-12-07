package org.n52.movingcode.runtime.codepackage;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A simple default package repository. May store multiple packages per functional ID
 * (i.e. multiple implementations of the same functional contract).
 * When requested by their functional ID, it will return only the first registered
 * package (this is the "default"). There is no guarantee about the internal order of the
 * registered packages so the default package is an arbitrary item.
 * If you need guaranteed access to a particular implementation you have to request that package
 * by its *packageID*. 
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class DefaultPackageRepository {
	private Map<String, MovingCodePackage> packages = new HashMap<String, MovingCodePackage>();
	private Multimap<String, String> fIDpID_Lookup = ArrayListMultimap.create();
	
	static Logger logger = Logger.getLogger(DefaultPackageRepository.class);
	
	/**
	 * private method for registering packages
	 * @param mcPackage
	 */
	protected void register(MovingCodePackage mcPackage){
		this.packages.put(mcPackage.getPackageIdentifier(), mcPackage);
		this.fIDpID_Lookup.put(mcPackage.getFunctionalIdentifier(), mcPackage.getPackageIdentifier());
	}
	
	/**
	 * Private method or package retrieval
	 * @param functionalID
	 * @return
	 */
	protected MovingCodePackage retrievePackage(String packageID){
		return packages.get(packageID);
	}
	
	/*
	 * returns true if this repo contains a suitable package for a given functional ID 
	 */
	public boolean providesFunction (String functionalID){
		return fIDpID_Lookup.containsKey(functionalID);
	}
	
	/*
	 * returns package description for a given package ID 
	 */
	public boolean containsPackage (String packageID){
		return packages.containsKey(packageID);
	}
	
	/**
	 * Returns the *functional* IDs of all registered packages
	 * 
	 * Returns a snapshot of all currently used identifiers.
	 * Later changes in the MovingCodeRepository (new or deleted packages)
	 * are not propagated to the returned array.
	 * If you need up-to-date information call this method again.
	 * 
	 */
	public String[] getFunctionalIDs(){
		return fIDpID_Lookup.keySet().toArray(new String[fIDpID_Lookup.keySet().size()]);
	}
	
	/**
	 * Returns the *package* IDs of all registered packages
	 * 
	 * @param identifier
	 * @return
	 */
	public String[] getPackageIDs(){
		return packages.keySet().toArray(new String[packages.keySet().size()]);
	}
}
