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
package org.n52.movingcode.runtime;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;
import org.n52.movingcode.runtime.coderepository.CachedRemoteFeedRepository;
import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryChangeListener;

import com.google.common.collect.ImmutableSet;

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
 *       This could be be done in {@link #registerRepo(String, MovingCodeRepository)}
 *       
 */
public class GlobalRepositoryManager implements MovingCodeRepository {

	private static GlobalRepositoryManager instance;

	private Map<String, MovingCodeRepository> repositories = new HashMap<String, MovingCodeRepository>();

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
	 * Creates a new {@link MovingCodeRepository} for the given directory and tries to add this repository to
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
		MovingCodeRepository repo = MovingCodeRepository.Factory.createFromPlainFolder(new File(directory)); 
		return registerRepo(repoID, repo);
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
	public boolean addLocalZipPackageRepository(final String directory) {
		final String repoID = directory;

		// add new repo
		MovingCodeRepository repo = MovingCodeRepository.Factory.createFromZipFilesFolder(new File(directory)); 
		return registerRepo(repoID, repo);

	}

	/**
	 * Creates a new {@link MovingCodeRepository} for the given Geoprocessing Feed URL and tries to add this
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
		MovingCodeRepository repo = MovingCodeRepository.Factory.createCachedRemoteRepository(atomFeedURL, cacheDirectory); 
		return registerRepo(repoID, repo);
	}

	/**
	 * Registers a previously created {@link MovingCodeRepository}.
	 * 
	 * @param repo a previously created {@link MovingCodeRepository} instance
	 * @param repoId the id 
	 * @return true if successfully added
	 */
	public boolean addRepository(final MovingCodeRepository repo, final String repoID) {
		// add new repo
		return registerRepo(repoID, repo);
	}

	/**
	 * Creates a new {@link MovingCodeRepository} for the given Geoprocessing Feed URL and tries to add this
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
		MovingCodeRepository repo = MovingCodeRepository.Factory.createFromRemoteFeed(atomFeedURL); 
		return registerRepo(repoID, repo);
	}

	/**
	 * Private convenience method. Puts a {@link MovingCodeRepository} on the private
	 * Map {@link #repositories} and adds a default change listener to it. Thus 
	 * the RepositoryManager will be noticed about content changes in this repository and
	 * propagate this notice to its own listeners, i.e. the subscribers to the RepositoryManager.
	 * 
	 * @param repoID {@link String} - the ID of the repository
	 * @param repo {@link MovingCodeRepository} - the repository
	 */
	private final synchronized boolean registerRepo(final String repoID, final MovingCodeRepository repo){
		// if already registered: Exit
		if (repositories.containsKey(repoID)){
			return false;
		}

		// add repo to map
		repositories.put(repoID, repo);

		// add change listener
		repo.addRepositoryChangeListener(new RepositoryChangeListener() {
			@Override
			public void onRepositoryUpdate(MovingCodeRepository updatedRepo) {
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
			return mcpArray[0].getDescriptionAsDocument().getPackageDescription().getFunctionality().getWps100ProcessDescription();
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

		for (MovingCodeRepository repo : repositories.values()) {
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
		for (MovingCodeRepository repo : repositories.values()) {
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
		for(MovingCodeRepository currentRepo : repositories.values()){
			if (currentRepo.containsPackage(packageId)){
				return currentRepo.getPackage(packageId);
			}
		}
		return null;
	}

	@Override
	public PID[] getPackageIDs() {
		Set<PID> globalPIDs = new HashSet<PID>();
		for (String currentRepoID : repositories.keySet()){

			for (PID pid : repositories.get(currentRepoID).getPackageIDs()){
				globalPIDs.add(pid);
			}
		}

		return globalPIDs.toArray(new PID[globalPIDs.size()]);
	}
	
	@Override
	public ImmutableSet<MovingCodePackage> getLatestPackages() {
		Set<MovingCodePackage> packages = new HashSet<MovingCodePackage>();
		for (MovingCodeRepository currentRepo : repositories.values()){
			packages.addAll(currentRepo.getLatestPackages());
		}
		return ImmutableSet.copyOf(packages);
	}

	@Override
	public boolean containsPackage(final PID packageId) {
		for(MovingCodeRepository currentRepo : repositories.values()){
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
		for (MovingCodeRepository currentRepo : repositories.values()){
			// add the matching packages to the result set
			resultSet.addAll( Arrays.asList(currentRepo.getPackageByFunction(functionID)) );
		}

		return resultSet.toArray(new MovingCodePackage[resultSet.size()]);
	}

	@Override
	public PackageDescriptionDocument getPackageDescriptionAsDocument(PID packageId) {
		for(MovingCodeRepository currentRepo : repositories.values()){
			// cycle through all repos to find the packageId
			if (currentRepo.containsPackage(packageId)){
				// attempt a package retrieval
				PackageDescriptionDocument doc = currentRepo.getPackageDescriptionAsDocument(packageId);
				if (doc != null){
					return doc;
				}
			}
		}
		return null; 
	}
	
	@Override
	public String getPackageDescriptionAsString(PID packageId) {
		for(MovingCodeRepository currentRepo : repositories.values()){
			// cycle through all repos to find the packageId
			if (currentRepo.containsPackage(packageId)){
				// attempt a package retrieval
				String packageDescription = currentRepo.getPackageDescriptionAsString(packageId);
				if (packageDescription != null){
					return packageDescription;
				}
			}
		}
		return null; 
	}

	@Override
	public String[] getFunctionIDs() {
		HashSet<String> fids = new HashSet<String>();
		for (MovingCodeRepository currentRepo : repositories.values()){
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

}
