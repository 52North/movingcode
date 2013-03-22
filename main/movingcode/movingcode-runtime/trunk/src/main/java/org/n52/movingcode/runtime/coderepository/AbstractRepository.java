package org.n52.movingcode.runtime.coderepository;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * Basic class for a package repository. May store multiple packages per function (i.e. multiple
 * implementations of the same functional contract). When requested by their functionID, it will return
 * only the first applicable package (this is the "default"). There is no guarantee about the internal order
 * of the registered packages so the default package is an arbitrary item. If you need guaranteed access to a
 * particular implementation you have to request that package by its packageID.
 * 
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
	 * Puts a new KVP (packageID, mcPackage) into the packages Map.
	 * Also registers a KVP (functionalID,packageID) in the fIDpID_Lookup.
	 * 
	 * @param mcPackage {@link MovingCodePackage}
	 */
	protected void register(MovingCodePackage mcPackage) {
		this.packages.put(mcPackage.getPackageIdentifier(), mcPackage);
		this.fIDpID_Lookup.put(mcPackage.getFunctionalIdentifier(), mcPackage.getPackageIdentifier());
	}

	@Override
	public boolean providesFunction(String functionID) {
		return fIDpID_Lookup.containsKey(functionID);
	}

	@Override
	public boolean containsPackage(String packageID) {
		return packages.containsKey(packageID);
	}

	@Override
	public String[] getFunctionIDs() {
		return fIDpID_Lookup.keySet().toArray(new String[fIDpID_Lookup.keySet().size()]);
	}

	@Override
	public String[] getPackageIDs() {
		return packages.keySet().toArray(new String[packages.keySet().size()]);
	}
	
    @Override
    public MovingCodePackage getPackage(String packageID) {
    	return packages.get(packageID);
    }

    @Override
    public Date getPackageTimestamp(String packageID) {
    	return packages.get(packageID).getTimestamp();
    }

    @Override
    public PackageDescriptionDocument getPackageDescription(String packageID) {
    	return packages.get(packageID).getDescription();
    }
    
    @Override
    public MovingCodePackage getPackageByFunction(String functionID){
    	Collection<String> packageIDs = fIDpID_Lookup.get(functionID);
    	if (packageIDs.size() > 0){
    		return packages.get(packageIDs.iterator().next());
    	} else {
    		return null;
    	}
    }
}
