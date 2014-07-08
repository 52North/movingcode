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

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.joda.time.DateTime;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

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
	private Map<PID, MovingCodePackage> packages = new HashMap<PID, MovingCodePackage>();

	// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
	private Multimap<String, PID> fIDpID_Lookup = ArrayListMultimap.create();

	// registered changeListerners
	private List<RepositoryChangeListener> changeListeners =  new ArrayList<RepositoryChangeListener>();

	static Logger logger = Logger.getLogger(AbstractRepository.class);

	// volatile read and write locks
	protected volatile boolean writeLock = false;
	protected volatile int readLock = 0;

	/**
	 * Protected method for registering packages with this repository.
	 * Puts a new KVP (packageID, mcPackage) into the packages Map {@link AbstractRepository#packages}.
	 * Also registers a KVP (functionalID,packageID) in the lookup Map {@link AbstractRepository#fIDpID_Lookup}.
	 * 
	 * @param mcPackage {@link MovingCodePackage}
	 */
	protected void register(MovingCodePackage mcPackage) {
		acquireWriteLock();
		this.packages.put(mcPackage.getVersionedPackageId(), mcPackage);
		this.fIDpID_Lookup.put(mcPackage.getFunctionIdentifier(), mcPackage.getVersionedPackageId());
		returnWriteLock();
		informRepositoryChangeListeners();
	}
	
	/**
	 * Protected method for un-registering packages with this repository.
	 * Removes a package from {@link AbstractRepository#packages} and
	 * from {@link AbstractRepository#fIDpID_Lookup}
	 * 
	 * @param packageID
	 */
	protected void unregister(PID packageID){
		acquireWriteLock();
		if (fIDpID_Lookup.containsValue(packageID)){
			String fID = getPackage(packageID).getFunctionIdentifier();
			fIDpID_Lookup.remove(fID, packageID);
		}
		
		returnWriteLock();
		informRepositoryChangeListeners();
	}

	@Override
	public boolean providesFunction(String functionID) {
		acquireReadLock();
		boolean retval = this.fIDpID_Lookup.containsKey(functionID);
		returnReadLock();
		return retval;
	}

	@Override
	public boolean containsPackage(PID packageID) {
		acquireReadLock();
		boolean retval = this.packages.containsKey(packageID);
		returnReadLock();
		return retval;
	}

	@Override
	public String[] getFunctionIDs() {
		acquireReadLock();
		String[] retval = this.fIDpID_Lookup.keySet().toArray(new String[this.fIDpID_Lookup.keySet().size()]);
		returnReadLock();
		return retval;
	}

	@Override
	public PID[] getPackageIDs() {
		acquireReadLock();
		PID[] retval = this.packages.keySet().toArray(new PID[this.packages.keySet().size()]);
		returnReadLock();
		return retval;
	}

	@Override
	public MovingCodePackage getPackage(PID packageID) {
		acquireReadLock();
		MovingCodePackage retval = this.packages.get(packageID);
		returnReadLock();
		return retval;
	}
	
	@Override
	public PackageDescriptionDocument getPackageDescription(PID packageID) {
		acquireReadLock();
		// 1. Create an *immutable* copy of the original package description
		// 2. return only this copy
		// (Having an immutable copy means that later modifications of this repo will not alter the returned object.)
		PackageDescriptionDocument originalDescription = this.packages.get(packageID).getDescription();
		PackageDescriptionDocument retval = null;
		try {
			StringWriter writer = new StringWriter();  
			originalDescription.save(writer);
			String immutableCopy = writer.toString();
			retval = PackageDescriptionDocument.Factory.parse(immutableCopy);
		} catch (XmlException e) {
			// should not occur since we had everything validated before ...
			logger.error("Could not read PackageDescription from String.\n" + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			// should not occur since writing a valid XML document to a new String is always possible ...
			logger.error("Could not copy PackageDescription to String.\n" + e.getMessage());
			e.printStackTrace();
		} catch (NullPointerException e){
			logger.debug("PackageDescription not found for packageID " + packageID + "\n(Package is either unavailable or has disapperared temporarily.)");
			retval = null;
		}
		returnReadLock();
		return retval;
	}

	@Override
	public MovingCodePackage[] getPackageByFunction(String functionID){
		acquireReadLock(); // acquire lock
		Collection<PID> packageIDs = this.fIDpID_Lookup.get(functionID);
		if (packageIDs.size() != 0){
			ArrayList<MovingCodePackage> resultSet = new ArrayList<MovingCodePackage>();
			for (PID currentPID : packageIDs){
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
	 * Clear this repository. (Removes all contained packages and informs
	 * the registered listeners.)
	 */
	protected void clear(){
		acquireWriteLock();

		// registered packages - KVP (packageID, mPackage)
		this.packages = new HashMap<PID, MovingCodePackage>();

		// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
		this.fIDpID_Lookup = ArrayListMultimap.create();

		returnWriteLock();
		
		// inform change listeners
		informRepositoryChangeListeners();
	}

	private synchronized void acquireWriteLock(){
		while (writeLock || readLock !=0 ){
			// spin
		}
		writeLock = true;
	}

	private synchronized void returnWriteLock(){
		writeLock = false;
	}

	private synchronized void acquireReadLock(){
		while (writeLock){
			// spin
		}
		readLock++;
	}

	private void returnReadLock(){
		readLock--;
	}

}
