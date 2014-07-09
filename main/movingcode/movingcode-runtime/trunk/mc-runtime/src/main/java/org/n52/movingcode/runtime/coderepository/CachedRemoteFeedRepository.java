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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.PID;

/**
 * This class implements an {@link IMovingCodeRepository} for Remote Geoprocessing Feeds
 * and caches the contents on disk.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class CachedRemoteFeedRepository extends AbstractRepository {

	static Logger logger = Logger.getLogger(CachedRemoteFeedRepository.class);

	private final URL atomFeedURL;
	private final File cacheDirectory;

	private RemoteFeedRepository remoteRepo;
	private LocalVersionedFileRepository localRepoMirror;

	private Date mirrorTimestamp;

	/**
	 * Constructor for cached atom feed repositories. Additionally requires a cache directory to
	 * which the content of the atom feed will be mirrored.
	 * 
	 * Tries to access the atom feed at the given URL and scans its
	 * entries. Then attempts to interpret the entries as MovingCodePackages. Packages that do not validate
	 * will be ignored.
	 * 
	 * @param atomFeedURL {@link URL} - Direct HTTP link to the Geoprocessing Feed.
	 * @param cacheDirectory {@link File} - Local directory that serves as a mirror for the feed
	 * 
	 */
	public CachedRemoteFeedRepository(final URL atomFeedURL, final File cacheDirectory) {
		// assign local variables
		this.atomFeedURL = atomFeedURL;
		this.cacheDirectory = cacheDirectory;

		// load packages
		load();

	}

	/**
	 * private method that encapsulates the logic for loading 
	 * MovingCode packages.  
	 */
	private void load(){

		// 1. check if directory exists
		//    if not: create and set lastModifiedDate to zero
		if (!cacheDirectory.exists()){
			cacheDirectory.mkdirs();
			cacheDirectory.setLastModified(0);
		}

		// 2. check if directory is empty
		//    if so: reset lastModifiedDate
		String[] contents = cacheDirectory.list();
		if (contents == null || contents.length == 0){
			cacheDirectory.setLastModified(0);
		}

		// 3. determine last cache directory update
		this.mirrorTimestamp = new Date(cacheDirectory.lastModified());

		// 4. now load the directory contents
		registerLocalPackages();

		// 5. trigger remote repo init in separate thread
		Thread tLoadRemote = new LoadRepoThread();
		tLoadRemote.start();

	}

	/**
	 * This method initializes the {@link #localRepoMirror} and registers its
	 * packages with this repo.
	 */
	private void registerLocalPackages(){
		// 1. init local mirror and load contents from disk
		localRepoMirror = new LocalVersionedFileRepository(cacheDirectory);

		// 2. Add all processes in the localRepoMirror to our list
		for (PID currentPID : localRepoMirror.getPackageIDs()){
			register(localRepoMirror.getPackage(currentPID));
		}
	}

	/**
	 * Private update method. Updates the content of the local mirror. Usually
	 * triggered if the remote repository has received an update.
	 * 
	 * Synchronized method to avoid race conditions in update threads.
	 * 
	 */
	private synchronized void updateLocalMirror(){

		// clear the mirrors visible contents during the update process
		// this will inform all change listeners that this repository 
		// currently has no available packages.
		clear();

		// Since we are the exclusive owner of the localRepoMirror
		// and no change listener is registered with the local repository,
		// the update of the underlying content can safely take place

		// perform the content update
		List<PID> remotePIDs = Arrays.asList(remoteRepo.getPackageIDs());
		List<PID> localPIDs = Arrays.asList(localRepoMirror.getPackageIDs());
		List<PID> checkedLocalPIDs = new ArrayList<PID>();

		for (PID currentRemotePID : remotePIDs){

			// 1. for each remote package: check if it was previously present in mirror
			if (isInMirror(currentRemotePID)){
				// indicate that we have updated/checked this local PID
				checkedLocalPIDs.add(currentRemotePID);
			} 
			// 2. if not: just dump the new package to folder
			else {
				localRepoMirror.addPackage(remoteRepo.getPackage(currentRemotePID));
			}

		}

		// 3. report unsafe packages (?)
		// TODO: delete packages, if it really makes sense,
		// maybe have a trigger or so

		localPIDs.removeAll(checkedLocalPIDs);
		if(localPIDs.size() != 0){
			StringBuffer report = new StringBuffer("\n");
			report.append(
					"Package folder updated. The following packages are no longer present in the remote feed."
							+ "However, they will be kept in the local mirror until you manually delete them.\n"
					);
			for (PID currentPID : localPIDs){
				report.append(currentPID + "\n");
			}
			logger.info(report.toString());
		}
		// if everything went well:
		// set new timeStamp for folder and repo
		mirrorTimestamp = ((RemoteFeedRepository)remoteRepo).lastUpdated();
		cacheDirectory.setLastModified(mirrorTimestamp.getTime());


		// now re-read the local folder
		registerLocalPackages();

		// ... and inform the change listeners
		informRepositoryChangeListeners();
	}

	/**
	 * A task which re-computes the directory's fingerprint and
	 * triggers a content reload if required.
	 * 
	 * @author Matthias Mueller
	 *
	 */
	private final class LoadRepoThread extends Thread {

		@Override
		public void run() {
			// 1. check whether there is another thread of this kind running


			logger.info("Loading remote repository from URL " +  atomFeedURL.toString());
			// create new remote repo
			remoteRepo = new RemoteFeedRepository(atomFeedURL);

			// add change listener
			remoteRepo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
					updateLocalMirror();
				}
			});
			logger.info("Finished loading remote repository from URL " +  atomFeedURL.toString());

			// compare remote repo date and local cache date
			// run an update if required
			Date remoteDate = ((RemoteFeedRepository)remoteRepo).lastUpdated();
			if (remoteDate.after(mirrorTimestamp)){
				updateLocalMirror();
			}
		}
	}


	/**
	 * Does a versioned comparison.
	 * 
	 * @param candidate
	 * @return
	 */
	private boolean isInMirror(PID candidate){
		for (PID id : localRepoMirror.getPackageIDs()){
			if(id.equals(candidate)){
				return true;
			}
		}

		return false;
	}
}
