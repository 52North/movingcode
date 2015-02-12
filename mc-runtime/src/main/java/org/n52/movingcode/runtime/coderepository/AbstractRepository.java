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

import java.util.ArrayList;
import java.util.List;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;
import org.n52.movingcode.runtime.codepackage.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

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
 * Considered thread safe. Uses separate read and write locks.
 * 
 */
public abstract class AbstractRepository implements MovingCodeRepository{

	private volatile PackageInventory inventory = new PackageInventory();

	// registered changeListerners
	private List<RepositoryChangeListener> changeListeners =  new ArrayList<RepositoryChangeListener>();

	static final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

	/**
	 * Protected method for registering packages with this repository.
	 * Puts a new KVP (packageID, mcPackage) into the packages Map {@link AbstractRepository#packages}.
	 * Also registers a KVP (functionalID,packageID) in the lookup Map {@link AbstractRepository#fIDpID_Lookup}.
	 * 
	 * @param mcPackage {@link MovingCodePackage}
	 */
	protected void register(final MovingCodePackage mcPackage) {
		inventory.add(mcPackage);
		informRepositoryChangeListeners();
	}

	/**
	 * Protected method for un-registering packages with this repository.
	 * Removes a package from {@link AbstractRepository#packages} and
	 * from {@link AbstractRepository#fIDpID_Lookup}
	 * 
	 * @param packageID
	 */
	protected void unregister(PID packageId){
		inventory.remove(packageId);
		informRepositoryChangeListeners();
	}
	
	/**
	 * Performs a comparison between old and new inventory. If the content has changed
	 * the current inventory will be replaced with the new inventory.
	 * 
	 * @param newInventory
	 */
	protected void updateInventory(final PackageInventory newInventory){
		if (!inventory.equals(newInventory)){
			inventory = newInventory;
			informRepositoryChangeListeners();
		}
	}

	@Override
	public boolean providesFunction(String functionId) {
		return inventory.contains(functionId);
	}

	@Override
	public boolean containsPackage(PID packageId) {
		return inventory.contains(packageId);
	}

	@Override
	public String[] getFunctionIDs() {
		ImmutableSet<String> s = inventory.getFunctionIDs();
		return s.toArray(new String[s.size()]);
	}

	@Override
	public PID[] getPackageIDs() {
		ImmutableSet<PID> s = inventory.getPackageIDs();
		return s.toArray(new PID[s.size()]);
	}

	@Override
	public MovingCodePackage getPackage(final PID packageId) {
		return inventory.getPackage(packageId);
	}

	@Override
	public PackageDescriptionDocument getPackageDescriptionAsDocument(PID packageId) {
		return XMLUtils.fromString(getPackageDescriptionAsString(packageId));
	}
	
	@Override
	public String getPackageDescriptionAsString(PID packageId) {
		return inventory.getPackage(packageId).getDescriptionAsString();
	}

	@Override
	public MovingCodePackage[] getPackageByFunction(String functionId){
		return inventory.getPackagesByFunctionId(functionId);
	}
	
	@Override
	public ImmutableSet<MovingCodePackage> getLatestPackages() {
		return inventory.latestPackageVersions();
	}

	@Override
	public synchronized void addRepositoryChangeListener(RepositoryChangeListener l) {
		this.changeListeners.add(l);
	}

	@Override
	public synchronized void removeRepositoryChangeListener(RepositoryChangeListener l) {
		this.changeListeners.remove(l);
	}

	/**
	 * informs all listeners about an update.
	 */
	private synchronized void informRepositoryChangeListeners() {
		for (RepositoryChangeListener l : this.changeListeners) {
			l.onRepositoryUpdate(this);
		}
	}

	/**
	 * Clear this repository. (Removes all contained packages and informs
	 * the registered listeners.)
	 */
	protected void clear(){
		this.inventory = new PackageInventory();

		// inform change listeners
		informRepositoryChangeListeners();
	}

}
