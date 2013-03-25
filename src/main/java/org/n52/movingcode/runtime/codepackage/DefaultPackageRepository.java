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

package org.n52.movingcode.runtime.codepackage;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.MovingCodeRepository;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * A simple default package repository. May store multiple packages per functional ID (i.e. multiple
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
 * Deprecation warning: This class will be deleted in the near future. It's logic has been migrated to
 * {@link org.n52.movingcode.runtime.coderepository.AbstractRepository}
 * 
 * 
 */
@Deprecated
public class DefaultPackageRepository {
	
	// registered packages - KVP (packageID, mPackage)
	private Map<String, MovingCodePackage> packages = new HashMap<String, MovingCodePackage>();
	
	// lookup table between functionalID (i.e. WPS ProcessIdentifier) <--> packageID 
	private Multimap<String, String> fIDpID_Lookup = ArrayListMultimap.create();

	static Logger logger = Logger.getLogger(DefaultPackageRepository.class);

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
		return this.packages.get(packageID);
	}

	/**
	 * Public method to determine whether this repository instance provides a certain functionality
	 * (i.e. ProcessIdentifier in WPS 1.0). 
	 * 
	 * @param functionalID {@link String}
	 * @return boolean - returns true if this repository instance contains a suitable package for a given functional ID, false otherwise.
	 */
	public boolean providesFunction(String functionalID) {
		return this.fIDpID_Lookup.containsKey(functionalID);
	}

	/**
	 * Public method to determine whether a package with the given ID is provided by this repository?
	 * 
	 * @param packageID {@link String} - the internal (unique) identifier of package. 
	 * @return boolean - true if a package with the given ID is provided by this repository.
	 */
	public boolean containsPackage(String packageID) {
		return this.packages.containsKey(packageID);
	}

	/**
	 * Returns the *functional* IDs of all registered packages
	 * 
	 * Returns a snapshot of all currently used identifiers. Later changes in the MovingCodeRepository (new or
	 * deleted packages) are not propagated to the returned array. If you need up-to-date information call
	 * this method again.
	 */
	public String[] getFunctionIDs() {
		return this.fIDpID_Lookup.keySet().toArray(new String[this.fIDpID_Lookup.keySet().size()]);
	}

	/**
	 * Returns the *package* IDs of all registered packages.
	 * 
	 * @return Array of packageIDs {@link String}
	 */
	public String[] getPackageIDs() {
		return this.packages.keySet().toArray(new String[this.packages.keySet().size()]);
	}
}
