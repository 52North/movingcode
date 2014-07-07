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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.joda.time.DateTime;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;
import org.n52.movingcode.runtime.coderepository.CachedRemoteFeedRepository;
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryChangeListener;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * The Repository Manager is a singleton instance that manages all active Moving Code Repositories.
 * 
 * Almost thread safe, but:
 * TODO: add read/write locks to make it really thread safe (see {@link CachedRemoteFeedRepository})
 * 
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 * @author matthes rieke, 52n
 * 
 * TODO: add check to ensure that identical functions have the same ProcessDescription
 *       (otherwise we are running into severe issues when somebody queries a particular function)
 *       This could be be done in {@link #registerRepo(String, IMovingCodeRepository)}
 *       
 */
public class GlobalRepositoryManager implements IMovingCodeRepository {

	private static GlobalRepositoryManager instance;
	
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
	public boolean addLocalPlainRepository(final String directory) {
		final String repoID = directory;
			
		// add new repo
		IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromPlainFolder(repoID, new File(directory)); 
		return registerRepo(repoID, repo);
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
	public boolean addLocalZipPackageRepository(final String directory) {
		final String repoID = directory;
			
		// add new repo
		IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromPlainFolder(repoID, new File(directory)); 
		return registerRepo(repoID, repo);

	}

	/**
	 * Creates a new {@link IMovingCodeRepository} for the given Geoprocessing Feed URL and tries to add this
	 * repository to the internal repositories Map. This repository is implemented as a {@link CachedRemoteFeedRepository},
	 * i.e. the content will be mirrored to a local directory.
	 * This speeds up initial load time, access time and provides some fail over capabilities in the case of weak internet
	 * connections.
	 * 
	 * 1) If the feed URL was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param atomFeedURL {@link URL} - A URL that links to a remote feed.
	 * @param cacheDirectory {@link File} - Local directory that shall be used as a cache / mirror.
	 * @return <code>true|false</code> - indicates that the new Repository was added (or not).
	 */
	public boolean addCachedRemoteRepository(final URL atomFeedURL, final File cacheDirectory) {
		// TODO: maybe create a simpler repo ID ...
		final String repoID = atomFeedURL.toString() + cacheDirectory.getAbsolutePath();
		
		// add new repo
		IMovingCodeRepository repo = IMovingCodeRepository.Factory.createCachedRemoteRepository(atomFeedURL, cacheDirectory); 
		return registerRepo(repoID, repo);
	}

	/**
	 * Registers a previously created {@link IMovingCodeRepository}.
	 * 
	 * @param repo a previously created {@link IMovingCodeRepository} instance
	 * @param repoId the id 
	 * @return true if successfully added
	 */
	public boolean addRepository(final IMovingCodeRepository repo, final String repoID) {
		// add new repo
		return registerRepo(repoID, repo);
	}
	
	/**
	 * Creates a new {@link IMovingCodeRepository} for the given Geoprocessing Feed URL and tries to add this
	 * repository to the internal repositories Map. 
	 * 1) If the feed URL was already loaded/registered, this method returns false.
	 * 2) If, for some other reason, the new repository cannot be added, this method returns false. 
	 * 3) If the new Repository was successfully added, this method returns true.
	 * 
	 * @param atomFeedURL {@link URL} - A URL that links to a remote feed.
	 * @return boolean - indicates that the new Repository was added.
	 */
	public boolean addRepository(final URL atomFeedURL) {
		final String repoID = atomFeedURL.toString();
		IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromRemoteFeed(atomFeedURL); 
		return registerRepo(repoID, repo);
	}
	
	/**
	 * Private convenience method. Puts a {@link IMovingCodeRepository} on the private
	 * Map {@link #repositories} and adds a default change listener to it. Thus 
	 * the RepositoryManager will be noticed about content changes in this repository and
	 * propagate this notice to its own listeners, i.e. the subscribers to the RepositoryManager.
	 * 
	 * @param repoID {@link String} - the ID of the repository
	 * @param repo {@link IMovingCodeRepository} - the repository
	 */
	private final synchronized boolean registerRepo(final String repoID, final IMovingCodeRepository repo){
		// if already registered: Exit
		if (repositories.containsKey(repoID)){
			return false;
		}
		
		// add repo to map
		repositories.put(repoID, repo);
		
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
	
	/**
	 * Finds a package for a given function identifier.
	 * 
	 * @param functionIdentifier
	 * @return - {@link ProcessDescriptionType}
	 */
	public ProcessDescriptionType getProcessDescription(final String functionIdentifier) {
		MovingCodePackage[] mcpArray = getPackageByFunction(functionIdentifier);
		if (mcpArray != null && mcpArray.length > 0){
			return mcpArray[0].getDescription().getPackageDescription().getFunctionality().getWps100ProcessDescription();
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
	public int checkMultiplicityOfPackage(final PID identifier) {
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
	public boolean providesFunction(final String functionalID) {
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
	public String[] getRegisteredRepositories() {
		return repositories.keySet().toArray(new String[repositories.size()]);
	}
	
	/**
	 * Check if a (remote feed-based) repository has been registered already.
	 * 
	 * @param atomFeedURL {@link URL} - the repository feed URL
	 * @return boolean - true if the repository is registered, false otherwise
	 */
	public boolean isRegisteredRepository(final String repoID) {
		return repositories.containsKey(repoID);
	}

	/**
	 * Remove/unregister a repository.
	 * 
	 * @param repoID {@link String} - the repository ID
	 */
	public synchronized void removeRepository(final String repoID) {
		repositories.remove(repoID);
	}
	
	@Override
	public synchronized MovingCodePackage getPackage(final PID packageId) {
		for(IMovingCodeRepository currentRepo : repositories.values()){
			if (currentRepo.containsPackage(packageId)){
				return currentRepo.getPackage(packageId);
			}
		}
		return null;
	}

	@Override
	public PID[] getPackageIDs() {
		ArrayList<PID> globalPIDs = new ArrayList<PID>();
		for (String currentRepoID : repositories.keySet()){

			for (PID pid : repositories.get(currentRepoID).getPackageIDs()){
				globalPIDs.add(pid);
			}
		}

		return globalPIDs.toArray(new PID[globalPIDs.size()]);
	}

	@Override
	public boolean containsPackage(final PID packageId) {
		for(IMovingCodeRepository currentRepo : repositories.values()){
			if (currentRepo.containsPackage(packageId)){
				return true;
			}
		}
		return false;
	}

	@Override
	public MovingCodePackage[] getPackageByFunction(final String functionID) {
		ArrayList<MovingCodePackage> resultSet = new ArrayList<MovingCodePackage>();
		// for each repo
		for (IMovingCodeRepository currentRepo : repositories.values()){
			// add the matching packages to the result set
			resultSet.addAll( Arrays.asList(currentRepo.getPackageByFunction(functionID)) );
		}

		return resultSet.toArray(new MovingCodePackage[resultSet.size()]);
	}

	@Override
	public DateTime getPackageTimestamp(final PID packageId) {
		for(IMovingCodeRepository currentRepo : repositories.values()){
			if (currentRepo.containsPackage(packageId)){
				currentRepo.getPackage(packageId).getTimestamp();
			}
		}
		return null; 
	}

	@Override
	public PackageDescriptionDocument getPackageDescription(PID packageId) {
		for(IMovingCodeRepository currentRepo : repositories.values()){
			if (currentRepo.containsPackage(packageId)){
				currentRepo.getPackage(packageId).getTimestamp();
			}
		}
		return null; 
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
	public void addRepositoryChangeListener(final RepositoryChangeListener l) {
		this.changeListeners.add(l);
	}

	@Override
	public void removeRepositoryChangeListener(final RepositoryChangeListener l) {
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
	
//	/**
//	 * Private convenience method that filters the repoID from a global packageID
//	 * of the form <repoid>+<separator>+<localPID>.
//	 * 
//	 * If no repo with a matching ID was found this methods return null
//	 * 
//	 * @param packageID {@link String}
//	 * @return a valid repo id {@link String} 
//	 */
//	private final PackageID repoID(final PackageID packageID){
//		for (String currentRepoID : repositories.keySet()){
//			// <prefix> = <repoid>+"/"
//			String prefix = currentRepoID + separator;
//			// if packageID starts with  <prefix>
//			if ( packageID.startsWith(prefix) ){
//				return currentRepoID;
//			}
//		}
//		return null;
//	}
	
//	/**
//	 * Private convenience method that filters the local Package ID (localPID) from a
//	 * global packageID of the form <repoid>+<separator>+<localPID>.
//	 * The local package ID is the package ID that was assigned to a package by the 
//	 * particular repository.
//	 * 
//	 * @param packageID {@link String}
//	 * @return a localPID {@link String} 
//	 */
//	private final String localPID(final String packageID){
//		String repoID = repoID(packageID);
//		// safety check to avoid null pointers
//		// should not happen during regular operation
//		if (repoID == null){
//			return null;
//		}
//		// <prefix> = <repoid>+"/"
//		String prefix = repoID + separator;
//		// if packageID starts with  <prefix>
//		if ( packageID.startsWith(prefix) ){
//			return packageID.substring(prefix.length());
//		} else {
//			return null;
//		}
//	}

}
