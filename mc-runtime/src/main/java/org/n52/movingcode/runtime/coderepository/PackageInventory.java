/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.movingcode.runtime.coderepository;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
 * An inventory for code packages. Thread safe for concurrent updates
 * (blocking ADD and REMOVE methods).
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class PackageInventory {
	
	// lookup table packageId -> package 
	private final Map<PID, MovingCodePackage> packagesByIdMap;
	
	// lookup table packageName -> package 
	private final Multimap<String, MovingCodePackage> packagesByNameMap;
	
	// lookup table functionId -> packageID 
	private final Multimap<String, MovingCodePackage> packagesByFunctionIdMap;
	
	/**
	 * No argument constructor.
	 */
	PackageInventory(){
		packagesByIdMap = new ConcurrentHashMap<PID, MovingCodePackage>();
		
		// multimap for packageName -> packageId LUT
		Multimap<String, MovingCodePackage> delegate1 = ArrayListMultimap.create();
		packagesByNameMap = Multimaps.synchronizedMultimap(delegate1);
		
		// multimap for functionId -> packageId LUT
		Multimap<String, MovingCodePackage> delegate2 = ArrayListMultimap.create();
		packagesByFunctionIdMap = Multimaps.synchronizedMultimap(delegate2);
	}
	
	/**
	 * Adds a package to the inventory
	 * 
	 * @param mcPackage
	 */
	void add(final MovingCodePackage mcPackage) {
		this.packagesByIdMap.put(mcPackage.getPackageId(), mcPackage);
		this.packagesByNameMap.put(mcPackage.getPackageId().name, mcPackage);
		this.packagesByFunctionIdMap.put(mcPackage.getFunctionIdentifier(), mcPackage);
	}
	
	/**
	 * Removes the package with the given packageID from the inventory.
	 * 
	 * @param packageId
	 */
	void remove(final PID packageId) {
		MovingCodePackage mcp = packagesByIdMap.get(packageId);
		if (mcp==null){
			return;
		}
		packagesByFunctionIdMap.remove(mcp.getFunctionIdentifier(), packageId);
		packagesByNameMap.remove(mcp.getPackageId().name, mcp.getPackageId());
		packagesByIdMap.remove(packageId);
	}
	
	/**
	 * Contains method for packageIDs
	 * 
	 * @param packageId
	 * @return
	 */
	boolean contains(final PID packageId){
		return packagesByIdMap.containsKey(packageId);
	}
	
	/**
	 * Contains method for function IDs
	 * 
	 * @param functionId
	 * @return
	 */
	boolean contains(final String functionId){
		return packagesByFunctionIdMap.containsKey(functionId);
	}
	
	/**
	 * Returns an immutable view of the registered function IDs.
	 * Subsequent updates to the inventory are not forwarded to this view.
	 * 
	 * @return
	 */
	ImmutableSet<String> getFunctionIDs(){
		synchronized (packagesByFunctionIdMap){
			return ImmutableSet.copyOf(packagesByFunctionIdMap.keys());
		}
	}
	
	/**
	 * Returns an immutable view of the registered packageIDs.
	 * Subsequent updates to the inventory are not forwarded to this view.
	 * 
	 * @return
	 */
	ImmutableSet<PID> getPackageIDs(){
		synchronized (packagesByIdMap){
			return ImmutableSet.copyOf(packagesByIdMap.keySet());
		}
	}
	
	/**
	 * Returns an array of the currently registered code packages that match a given ID
	 * 
	 * @param functionId
	 * @return
	 */
	MovingCodePackage[] getPackagesByFunctionId(String functionId){
		Collection<MovingCodePackage> retval = packagesByFunctionIdMap.asMap().get(functionId);
		if (retval == null){
			return new MovingCodePackage[0];
		}
		return retval.toArray(new MovingCodePackage[retval.size()]);
	}
	
	/**
	 * Get the package for a given packageId. Returns null if the packageId is unknown.
	 * 
	 * @param packageId
	 * @return
	 */
	MovingCodePackage getPackage(final PID packageId){
		return packagesByIdMap.get(packageId);
	}
	
	/**
	 * Utility method that returns a current copy of 
	 * 
	 * TODO: Should this be a copy or a view in future releases?
	 * 
	 * @param inventory
	 */
	final ImmutableSet<MovingCodePackage> latestPackageVersions(){
		
		Set<MovingCodePackage> retval = new HashSet<MovingCodePackage>();
		
		// synchronized access to the inventory
		// to prevent errors due to concurrent content updates
		synchronized (packagesByIdMap){
			for (Collection<MovingCodePackage> namedPages : packagesByNameMap.asMap().values()){
				// add latest (= greatest) package to result set
				retval.add(Collections.max(namedPages));
			}
		}
		
		return ImmutableSet.copyOf(retval);
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
		synchronized (packagesByIdMap){
			synchronized (ref.packagesByIdMap) {
				symmDiff = Sets.symmetricDifference(ref.packagesByIdMap.keySet(), this.packagesByIdMap.keySet());
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
		synchronized (packagesByIdMap){
			for (PID key : packagesByIdMap.keySet()){
				hash = hash * 31 + key.hashCode();
			}
		}
		return hash;
	}
}
