package org.n52.movingcode.runtime.coderepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

/**
 * An inventory for code packages. Supports concurrency for updates (add, remove).
 * 
 * @author matthias
 *
 */
public class PackageInventory {
	
	private final Map<PID, MovingCodePackage> packages;
	// lookup table between functionID (i.e. WPS ProcessIdentifier) <--> packageID 
	private final Multimap<String, PID> functionToPackageLookup;
	
	/**
	 * No argument constructor.
	 */
	PackageInventory(){
		packages = new ConcurrentHashMap<PID, MovingCodePackage>();
		Multimap<String, PID> delegate = ArrayListMultimap.create();
		functionToPackageLookup = Multimaps.synchronizedMultimap(delegate);
	}
	
	/**
	 * Adds a package to the inventory
	 * 
	 * @param mcPackage
	 */
	void add(final MovingCodePackage mcPackage) {
		this.packages.put(mcPackage.getVersionedPackageId(), mcPackage);
		this.functionToPackageLookup.put(mcPackage.getFunctionIdentifier(), mcPackage.getVersionedPackageId());
	}
	
	/**
	 * Removes the package with the given packageID from the inventory.
	 * 
	 * @param packageId
	 */
	void remove(final PID packageId) {
		MovingCodePackage mcp = packages.get(packageId);
		if (mcp==null){
			return;
		}
		String functionID = mcp.getFunctionIdentifier();
		functionToPackageLookup.remove(functionID, packageId);
		packages.remove(packageId);
	}
	
	/**
	 * Contains method for packageIDs
	 * 
	 * @param packageId
	 * @return
	 */
	boolean contains(final PID packageId){
		return packages.containsKey(packageId);
	}
	
	/**
	 * Contains method for function IDs
	 * 
	 * @param functionId
	 * @return
	 */
	boolean contains(final String functionId){
		return functionToPackageLookup.containsKey(functionId);
	}
	
	/**
	 * Returns an immutable view of the registered function IDs.
	 * Subsequent updates to the inventory are not forwarded to this view.
	 * 
	 * @return
	 */
	ImmutableSet<String> getFunctionIDs(){
		synchronized (functionToPackageLookup){
			return ImmutableSet.copyOf(functionToPackageLookup.keys());
		}
	}
	
	/**
	 * Returns an immutable view of the registered packageIDs.
	 * Subsequent updates to the inventory are not forwarded to this view.
	 * 
	 * @return
	 */
	ImmutableSet<PID> getPackageIDs(){
		synchronized (packages){
			return ImmutableSet.copyOf(packages.keySet());
		}
	}
	
	/**
	 * Returns an array of the currently registered code packages that match a given ID
	 * 
	 * @param functionId
	 * @return
	 */
	MovingCodePackage[] getPackagesByFunctionId(String functionId){
		List<MovingCodePackage> packageList = new ArrayList<MovingCodePackage>();
		for (PID packageId : functionToPackageLookup.get(functionId)){
			packageList.add(getPackage(packageId));
		}
		return packageList.toArray(new MovingCodePackage[packageList.size()]);
	}
	
	/**
	 * Get the package for a given packageId. Returns null if the packageId is unknown.
	 * 
	 * @param packageId
	 * @return
	 */
	MovingCodePackage getPackage(final PID packageId){
		return packages.get(packageId);
	}
	
	/**
	 * Two instances of {@link PackageInventory} are identical if they
	 * contain the same set packages.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PackageInventory)){
			return false;
		}
		PackageInventory ref = (PackageInventory) obj;
		// comparison of the PIDs is sufficient
		SetView<PID> symmDiff;
		// synchronized access to the underlying maps
		synchronized (packages){
			synchronized (ref.packages) {
				symmDiff = Sets.symmetricDifference(ref.packages.keySet(), this.packages.keySet());
			}
		}
		return symmDiff.isEmpty();
	}
	
	/**
	 * Same contract as {@link #equals(Object)}.
	 * The hashCode is computed from the hash codes of the contained
	 * packages.
	 */
	@Override
	public int hashCode() {
		int hash = 17;
		synchronized (packages){
			for (PID key : packages.keySet()){
				hash = hash * 31 + key.hashCode();
			}
		}
		return hash;
	}
}
