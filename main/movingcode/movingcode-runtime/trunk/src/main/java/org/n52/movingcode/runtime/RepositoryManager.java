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
 * The Repository Manager is a singleton instance that manages all active Moving Code Repositories.
 * 
 * Considered thread safe.
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class RepositoryManager {

	private static RepositoryManager instance;
	private Map<String, MovingCodeRepository> repositories = Collections.synchronizedMap(new HashMap<String, MovingCodeRepository>());

	/**
	 * private constructor for singleton pattern
	 */
	private RepositoryManager() {
		super();
	}

	/**
	 * Static method to access the {@link RepositoryManager} instance.
	 * 
	 * @return {@link RepositoryManager}
	 */
	public static synchronized RepositoryManager getInstance() {
		if (instance == null) {
			instance = new RepositoryManager();
		}
		return instance;
	}

	/**
	 * Creates a new {@link MovingCodeRepository} for the given directory and tries to add this repository to
	 * the internal repositories Map. 
	 * 1) If the directory was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param directory {@link String} - A directory that contains a collection of {@link MovingCodePackage}.
	 * @return boolean - indicates that the new Repository was added.
	 */
	public boolean addRepository(String directory) {
		if ( !repositories.containsKey(directory)) {
			return repositories.put(directory, new MovingCodeRepository(new File(directory))) != null;
		}
		else {
			return false;
		}
	}

	/**
	 * Creates a new {@link MovingCodeRepository} for the given Geoprocessing Feed URL and tries to add this
	 * repository to the internal repositories Map. 
	 * 1) If the feed URL was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param atomFeedURL {@link URL} - A directory that contains a collection of {@link MovingCodePackage}.
	 * @return boolean - indicates that the new Repository was added.
	 */
	public boolean addRepository(URL atomFeedURL) {
		if ( !repositories.containsKey(atomFeedURL.toString())) {
			return repositories.put(atomFeedURL.toString(), new MovingCodeRepository(atomFeedURL)) != null;
		}
		else {
			return false;
		}
	}

	/**
	 * Finds you a package for a given function identifier. This method will return the first Package matching this
	 * identifier. To check for multiple occurrences of the same identifier call
	 * {@link RepositoryManager#checkMultiplicityOfPackage(String)}
	 * 
	 * @param functionalIdentifier
	 * @return {@link MovingCodePackage}
	 */
	public MovingCodePackage getPackage(String functionIdentifier) {
		for (MovingCodeRepository repo : repositories.values()) {
			if (repo.containsPackage(functionIdentifier)) {
				return repo.getPackage(functionIdentifier);
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
	public int checkMultiplicityOfPackage(String identifier) {
		int counter = 0;

		for (MovingCodeRepository repo : repositories.values()) {
			if (repo.containsPackage(identifier)) {
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
	public boolean providesFunction(String functionalID) {
		for (MovingCodeRepository repo : repositories.values()) {
			if (repo.providesFunction(functionalID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the IDs of all registered processes.
	 * 
	 * @return Array of {@link String} - IDs of the registered {@link MovingCodePackage}.
	 */
	public String[] getProcessIDs() {
		ArrayList<String> retval = new ArrayList<String>();
		for (MovingCodeRepository repo : repositories.values()) {
			retval.addAll(Arrays.asList(repo.getFunctionIDs()));
		}
		return retval.toArray(new String[retval.size()]);
	}

	/**
	 * Get the IDs of all registered repositories (typically directories or URLs).
	 * 
	 * @return @return Array of {@link String} - IDs of the registered {@link MovingCodeRepository}.
	 */
	public String[] getRegisteredRepositories() {
		return repositories.keySet().toArray(new String[repositories.size()]);
	}

	/**
	 * Check if a (directory-based) repository has been registered already.
	 * 
	 * @param directory {@link String} - the repository directory
	 * @return boolean - true if the repository is registered, false otherwise
	 */
	public boolean isRegisteredRepository(String directory) {
		return repositories.containsKey(directory);
	}

	/**
	 * Check if a (remote feed-based) repository has been registered already.
	 * 
	 * @param atomFeedURL {@link URL} - the repository feed URL
	 * @return boolean - true if the repository is registered, false otherwise
	 */
	public boolean isRegisteredRepository(URL atomFeedURL) {
		return repositories.containsKey(atomFeedURL.toString());
	}

	/**
	 * Remove/unregister a (directory-based) repository.
	 * 
	 * @param directory {@link String} - the repository directory
	 */
	public void removeRepository(String directory) {
		repositories.remove(directory);
	}

	/**
	 * Remove/unregister a (remote feed-based) repository.
	 * 
	 * @param atomFeedURL {@link URL} - the repository feed URL
	 */
	public void removeRepository(URL atomFeedURL) {
		repositories.remove(atomFeedURL.toString());
	}

}
