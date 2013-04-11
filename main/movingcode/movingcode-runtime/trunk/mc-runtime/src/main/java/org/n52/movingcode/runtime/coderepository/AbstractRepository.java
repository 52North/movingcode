/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.movingcode.runtime.coderepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
 * Considered thread safe. Uses separate read and write locks.
 * 
 */
public abstract class AbstractRepository implements IMovingCodeRepository{

	// registered packages - KVP (packageID, mPackage)
	private Map<String, MovingCodePackage> packages = new HashMap<String, MovingCodePackage>();

	// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
	private Multimap<String, String> fIDpID_Lookup = ArrayListMultimap.create();

	// registered changeListerners
	private List<RepositoryChangeListener> changeListeners =  new ArrayList<RepositoryChangeListener>();

	static Logger logger = Logger.getLogger(AbstractRepository.class);

	// volatile read and write locks
	protected volatile boolean writeLock = false;
	protected volatile int readLock = 0;

	/**
	 * Private method for registering packages with this repository instance.
	 * Puts a new KVP (packageID, mcPackage) into the packages Map.
	 * Also registers a KVP (functionalID,packageID) in the fIDpID_Lookup.
	 * 
	 * @param mcPackage {@link MovingCodePackage}
	 */
	protected void register(MovingCodePackage mcPackage) {
		aquireWriteLock();
		this.packages.put(mcPackage.getPackageIdentifier(), mcPackage);
		this.fIDpID_Lookup.put(mcPackage.getFunctionalIdentifier(), mcPackage.getPackageIdentifier());
		returnWriteLock();
	}

	@Override
	public boolean providesFunction(String functionID) {
		aquireReadLock();
		boolean retval = this.fIDpID_Lookup.containsKey(functionID);
		returnReadLock();
		return retval;
	}

	@Override
	public boolean containsPackage(String packageID) {
		aquireReadLock();
		boolean retval = this.packages.containsKey(packageID);
		returnReadLock();
		return retval;
	}

	@Override
	public String[] getFunctionIDs() {
		aquireReadLock();
		String[] retval = this.fIDpID_Lookup.keySet().toArray(new String[this.fIDpID_Lookup.keySet().size()]);
		returnReadLock();
		return retval;
	}

	@Override
	public String[] getPackageIDs() {
		aquireReadLock();
		String[] retval = this.packages.keySet().toArray(new String[this.packages.keySet().size()]);
		returnReadLock();
		return retval;
	}

	@Override
	public MovingCodePackage getPackage(String packageID) {
		aquireReadLock();
		MovingCodePackage retval = this.packages.get(packageID);
		returnReadLock();
		return retval;
	}

	@Override
	public Date getPackageTimestamp(String packageID) {
		aquireReadLock();
		Date retval = this.packages.get(packageID).getTimestamp();
		returnReadLock();	
		return retval;
	}

	@Override
	public PackageDescriptionDocument getPackageDescription(String packageID) {
		aquireReadLock();
		PackageDescriptionDocument retval = this.packages.get(packageID).getDescription();
		returnReadLock();
		return retval;
	}

	@Override
	public MovingCodePackage[] getPackageByFunction(String functionID){
		aquireReadLock(); // acquire lock
		Collection<String> packageIDs = this.fIDpID_Lookup.get(functionID);
		if (packageIDs.size() != 0){
			ArrayList<MovingCodePackage> resultSet = new ArrayList<MovingCodePackage>();
			for (String currentPID : packageIDs){
				resultSet.add(packages.get(currentPID));
			}
			returnReadLock(); // return lock
			return resultSet.toArray(new MovingCodePackage[resultSet.size()]);
		} else {
			returnReadLock(); // return lock
			return null;
		}
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
	protected synchronized void informRepositoryChangeListeners() {
		for (RepositoryChangeListener l : this.changeListeners) {
			l.onRepositoryUpdate(this);
		}
	}

	/**
	 * Clear this repository. (Removes all contained packages)
	 */
	protected void clear(){
		aquireWriteLock();

		// registered packages - KVP (packageID, mPackage)
		this.packages = new HashMap<String, MovingCodePackage>();

		// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
		this.fIDpID_Lookup = ArrayListMultimap.create();

		returnWriteLock();
	}

	protected synchronized void aquireWriteLock(){
		while (writeLock || readLock !=0 ){
			// spin
		}
		writeLock = true;
	}

	protected synchronized void returnWriteLock(){
		writeLock = false;
	}

	protected synchronized void aquireReadLock(){
		while (writeLock){
			// spin
		}
		readLock++;
	}

	protected void returnReadLock(){
		readLock--;
	}

}
