package org.n52.movingcode.runtime.coderepository;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * Basic class for a package repository. May store multiple packages per functional ID (i.e. multiple
 * implementations of the same functional contract). When requested by their functional ID, it will return
 * only the first registered package (this is the "default"). There is no guarantee about the internal order
 * of the registered packages so the default package is an arbitrary item. If you need guaranteed access to a
 * particular implementation you have to request that package by its *packageID*.
 * 
 * TODO: unify/split/re-arrange with {@link MovingCodeRepository}  
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 * 
 */
public abstract class AbstractRepository implements IMovingCodeRepository{
	// registered packages - KVP (packageID, mPackage)
	private Map<String, MovingCodePackage> packages = new HashMap<String, MovingCodePackage>();
	
	// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
	private Multimap<String, String> fIDpID_Lookup = ArrayListMultimap.create();

	static Logger logger = Logger.getLogger(AbstractRepository.class);
	
	/**
	 * Private method for registering packages with this repository instance.
	 * Puts a new KVP (packageID, mPackage) into the packages Map.
	 * Also registers a KVP (functionalID,packageID) in the fIDpID_Lookup.
	 * 
	 * @param mcPackage {@link MovingCodePackage}
	 */
	protected void register(MovingCodePackage mcPackage) {
		this.packages.put(mcPackage.getPackageIdentifier(), mcPackage);
		this.fIDpID_Lookup.put(mcPackage.getFunctionalIdentifier(), mcPackage.getPackageIdentifier());
	}
	
	/**
	 * Protected method for package retrieval. Returns a package for a given ID.
	 * 
	 * @param packageID {@link String} - the internal (unique) identifier of package.
	 * @return a package {@link MovingCodePackage}
	 */
	protected MovingCodePackage retrievePackage(String packageID) {
		return packages.get(packageID);
	}

	/**
	 * Public method to determine whether this repository instance provides a certain functionality
	 * (i.e. ProcessIdentifier in WPS 1.0). 
	 * 
	 * @param functionalID {@link String}
	 * @return boolean - returns true if this repository instance contains a suitable package for a given functional ID, false otherwise.
	 */
	public boolean providesFunction(String functionalID) {
		return fIDpID_Lookup.containsKey(functionalID);
	}

	/**
	 * Public method to determine whether a package with the given ID is provided by this repository?
	 * 
	 * @param packageID {@link String} - the internal (unique) identifier of package. 
	 * @return boolean - true if a package with the given ID is provided by this repository.
	 */
	public boolean containsPackage(String packageID) {
		return packages.containsKey(packageID);
	}

	/**
	 * Returns the *functional* IDs of all registered packages
	 * 
	 * Returns a snapshot of all currently used identifiers. Later changes in the MovingCodeRepository (new or
	 * deleted packages) are not propagated to the returned array. If you need up-to-date information call
	 * this method again.
	 */
	public String[] getFunctionalIDs() {
		return fIDpID_Lookup.keySet().toArray(new String[fIDpID_Lookup.keySet().size()]);
	}

	/**
	 * Returns the *package* IDs of all registered packages.
	 * 
	 * @return Array of packageIDs {@link String}
	 */
	public String[] getPackageIDs() {
		return packages.keySet().toArray(new String[packages.keySet().size()]);
	}
	
    @Override
    public MovingCodePackage getPackage(String packageID) {
        return retrievePackage(packageID);
    }

    @Override
    public Date getPackageTimestamp(String packageID) {
        return retrievePackage(packageID).getTimestamp();
    }

    @Override
    public PackageDescriptionDocument getPackageDescription(String packageID) {
        return retrievePackage(packageID).getDescription();
    }
}
