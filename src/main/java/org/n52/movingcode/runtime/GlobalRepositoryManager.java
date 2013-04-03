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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryChangeListener;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * The Repository Manager is a singleton instance that manages all active Moving Code Repositories.
 * 
 * Considered thread safe.
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 * @author matthes rieke, 52n
 * 
 * TODO: add check to ensure that identical functions have the same ProcessDescription
 *       (otherwise we are running into severe issues when somebody queries a particular function)
 * 
 */
public class GlobalRepositoryManager implements IMovingCodeRepository {

	private static GlobalRepositoryManager instance;

	// separator for global process IDs: <global>+<separator>+<localPID>
	private static final String separator = File.pathSeparator;

	//Collections.synchronizedMap() is not really thread-safe.... do it with synchronized blocks
	private Map<String, IMovingCodeRepository> repositories = new HashMap<String, IMovingCodeRepository>();
	
	// registered changeListerners
	private List<RepositoryChangeListener> changeListeners =  new ArrayList<RepositoryChangeListener>();
	
	/**
	 * private constructor for singleton pattern
	 */
	private GlobalRepositoryManager() {
		super();
	}

	/**
	 * Static method to access the {@link GlobalRepositoryManager} instance.
	 * 
	 * @return {@link GlobalRepositoryManager}
	 */
	public static synchronized GlobalRepositoryManager getInstance() {
		if (instance == null) {
			instance = new GlobalRepositoryManager();
		}
		return instance;
	}

	/**
	 * Creates a new {@link IMovingCodeRepository} for the given directory and tries to add this repository to
	 * the internal repositories Map. 
	 * 1) If the directory was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param directory {@link String} - A directory that contains a collection of {@link MovingCodePackage}.
	 * @return boolean - indicates that the new Repository was added.
	 */
	public synchronized boolean addLocalPlainRepository(String directory) {
		if ( !repositories.containsKey(directory)) {
			
			// add new repo
			IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromPlainFolder(new File(directory)); 
			repositories.put(directory, repo);
					
			// add change listener
			repo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
					informRepositoryChangeListeners();
				}
			});
			
			// inform listeners
			informRepositoryChangeListeners();
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a new {@link IMovingCodeRepository} for the given directory and tries to add this repository to
	 * the internal repositories Map. 
	 * 1) If the directory was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param directory {@link String} - A directory that contains a collection of {@link MovingCodePackage}.
	 * @return boolean - indicates that the new Repository was added.
	 */
	public synchronized boolean addLocalZipPackageRepository(String directory) {
		if ( !repositories.containsKey(directory)) {
			
			// add new repo
			IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromPlainFolder(new File(directory)); 
			repositories.put(directory, repo);
					
			// add change listener
			repo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
					informRepositoryChangeListeners();
				}
			});
			
			// inform listeners
			informRepositoryChangeListeners();
			return true;
		}
		return false;
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
	public synchronized boolean addRepository(URL atomFeedURL) {
		if ( !repositories.containsKey(atomFeedURL.toString())) {
			
			// add new repo
			IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromRemoteFeed(atomFeedURL); 
			repositories.put(atomFeedURL.toString(), repo);
			
			// add change listener
			repo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
					informRepositoryChangeListeners();
				}
			});
			
			// inform listeners
			informRepositoryChangeListeners();
			return true;
		}
		return false;
	}

	/**
	 * Registers a previously created {@link MovingCodeRepository}.
	 * 
	 * @param repo a previously created {@link MovingCodeRepository} instance
	 * @param repoId the id 
	 * @return true if succesfully added
	 */
	public synchronized boolean addRepository(IMovingCodeRepository repo, String repoId) {
		if ( !repositories.containsKey(repoId)) {
			
			// add new repo
			repositories.put(repoId, repo);
			
			// add change listener
			repo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
					informRepositoryChangeListeners();
				}
			});
			
			// inform listeners
			informRepositoryChangeListeners();
			return true;
		}
		return false;
	}

	/**
	 * Finds a package for a given function identifier.
	 * 
	 * @param functionIdentifier
	 * @return - {@link ProcessDescriptionType}
	 */
	public synchronized ProcessDescriptionType getProcessDescription(String functionIdentifier) {
		MovingCodePackage[] mcpArray = getPackageByFunction(functionIdentifier);
		if (mcpArray != null && mcpArray.length > 0){
			return mcpArray[1].getDescription().getPackageDescription().getContractedFunctionality().getWpsProcessDescription();
		} else {
			return null;
		}
	}

	/**
	 * Method used for checking multiple occurrences of the same process ID.
	 * 
	 * @param identifier
	 * @return int - multiplicity
	 */
	public synchronized int checkMultiplicityOfPackage(String identifier) {
		int counter = 0;

		for (IMovingCodeRepository repo : repositories.values()) {
			if (repo.containsPackage(identifier)) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * Contains check: Is a there a package registered for a given function ID?
	 * 
	 * @param identifier
	 * @return boolean
	 */
	public synchronized boolean providesFunction(String functionalID) {
		for (IMovingCodeRepository repo : repositories.values()) {
			if (repo.providesFunction(functionalID)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the IDs of all registered repositories (typically directories or URLs).
	 * 
	 * @return @return Array of {@link String} - IDs of the registered {@link MovingCodeRepository}.
	 */
	public synchronized String[] getRegisteredRepositories() {
		return repositories.keySet().toArray(new String[repositories.size()]);
	}

	/**
	 * Check if a (directory-based) repository has been registered already.
	 * 
	 * @param directory {@link String} - the repository directory
	 * @return boolean - true if the repository is registered, false otherwise
	 */
	public synchronized boolean isRegisteredRepository(String directory) {
		return repositories.containsKey(directory);
	}

	/**
	 * Check if a (remote feed-based) repository has been registered already.
	 * 
	 * @param atomFeedURL {@link URL} - the repository feed URL
	 * @return boolean - true if the repository is registered, false otherwise
	 */
	public synchronized boolean isRegisteredRepository(URL atomFeedURL) {
		return repositories.containsKey(atomFeedURL.toString());
	}

	/**
	 * Remove/unregister a (directory-based) repository.
	 * 
	 * @param directory {@link String} - the repository directory
	 */
	public synchronized void removeRepository(String directory) {
		repositories.remove(directory);
	}

	/**
	 * Remove/unregister a (remote feed-based) repository.
	 * 
	 * @param atomFeedURL {@link URL} - the repository feed URL
	 */
	public synchronized void removeRepository(URL atomFeedURL) {
		repositories.remove(atomFeedURL.toString());
	}
	
	@Override
	public synchronized MovingCodePackage getPackage(String packageID) {
		return repositories.get(repoID(packageID)).getPackage(localPID(packageID));
	}

	@Override
	public String[] getPackageIDs() {
		ArrayList<String> globalPIDs = new ArrayList<String>();
		for (String currentRepoID : repositories.keySet()){

			for (String pid : repositories.get(currentRepoID).getPackageIDs()){
				globalPIDs.add(currentRepoID + separator + pid);
			}
		}

		return globalPIDs.toArray(new String[globalPIDs.size()]);
	}

	@Override
	public boolean containsPackage(String packageID) {
		// -- begin validity check --
		String repoID = repoID(packageID);
		if (repoID == null){
			return false;
		}
		String localPID = localPID(packageID);
		if (localPID == null){
			return false;
		}
		// -- end validity check --
		return repositories.get(repoID).containsPackage(localPID);
	}

	@Override
	public MovingCodePackage[] getPackageByFunction(String functionID) {
		ArrayList<MovingCodePackage> resultSet = new ArrayList<MovingCodePackage>();
		// for each repo
		for (IMovingCodeRepository currentRepo : repositories.values()){
			// add the matching packages to the result set
			resultSet.addAll( Arrays.asList(currentRepo.getPackageByFunction(functionID)) );
		}

		return resultSet.toArray(new MovingCodePackage[resultSet.size()]);
	}

	@Override
	public Date getPackageTimestamp(String packageID) {
		return repositories.get(repoID(packageID)).getPackageTimestamp(localPID(packageID)); 
	}

	@Override
	public PackageDescriptionDocument getPackageDescription(String packageID) {
		return repositories.get(repoID(packageID)).getPackageDescription(localPID(packageID));
	}

	@Override
	public String[] getFunctionIDs() {
		HashSet<String> fids = new HashSet<String>();
		for (IMovingCodeRepository currentRepo : repositories.values()){
			fids.addAll(Arrays.asList(currentRepo.getFunctionIDs()));
		}
		
		return fids.toArray(new String[fids.size()]);
	}

	@Override
	public void addRepositoryChangeListener(RepositoryChangeListener l) {
		this.changeListeners.add(l);
	}

	@Override
	public void removeRepositoryChangeListener(RepositoryChangeListener l) {
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
	 * Private convenience method that filters the repoID from a global packageID
	 * of the form <repoid>+<separator>+<localPID>.
	 * 
	 * If no repo with a matching ID was found this methods return null
	 * 
	 * @param packageID {@link String}
	 * @return a valid repo id {@link String} 
	 */
	private final String repoID(String packageID){
		for (String currentRepoID : repositories.keySet()){
			// <prefix> = <repoid>+"/"
			String prefix = currentRepoID + separator;
			// if packageID starts with  <prefix>
			if ( packageID.startsWith(prefix) ){
				return currentRepoID;
			}
		}
		return null;
	}
	
	/**
	 * Private convenience method that filters the local Package ID (localPID) from a
	 * global packageID of the form <repoid>+<separator>+<localPID>.
	 * The local package ID is the package ID that was assigned to a package by the 
	 * particular repository.
	 * 
	 * @param packageID {@link String}
	 * @return a localPID {@link String} 
	 */
	private final String localPID(String packageID){
		String repoID = repoID(packageID);
		// safety check to avoid null pointers
		// should not happen during regular operation
		if (repoID == null){
			return null;
		}
		// <prefix> = <repoid>+"/"
		String prefix = repoID + separator;
		// if packageID starts with  <prefix>
		if ( packageID.startsWith(prefix) ){
			return packageID.substring(prefix.length());
		} else {
			return null;
		}
	}

}
