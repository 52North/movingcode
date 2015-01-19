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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

import com.google.common.collect.ImmutableSet;

/**
 * This class implements an {@link MovingCodeRepository} for Remote Geoprocessing Feeds
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
	
	private volatile boolean initDone = false;

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

		// init local mirror
		// also loads previously mirrored content
		initLocalMirror();
		
		// trigger initial update from remote repo in separate thread
		Thread tLoadRemote = new UpdateContentThread();
		tLoadRemote.start();

	}

	/**
	 * private method that encapsulates the logic for loading 
	 * MovingCode packages.  
	 */
	private void initLocalMirror(){

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

	}

	/**
	 * This method initializes the {@link #localRepoMirror} and registers its
	 * packages with this repo.
	 */
	private synchronized void registerLocalPackages(){
		// 1. init local mirror and load contents from disk
		localRepoMirror = new LocalVersionedFileRepository(cacheDirectory);
		
		// 2. Add all processes in the localRepoMirror to a new inventory list
		PackageInventory newInventory = new PackageInventory();
		for (PID currentPID : localRepoMirror.getPackageIDs()){
			newInventory.add(localRepoMirror.getPackage(currentPID));
		}
		
		// 3. update the current inventory
		this.updateInventory(newInventory);
	}

	/**
	 * Private update method. Updates the content of the local mirror. Usually
	 * triggered if the remote repository has received an update.
	 * 
	 * Synchronized method to avoid race conditions in update threads.
	 * 
	 */
	private synchronized void updateLocalMirror(){

		List<PID> remotePIDs = Arrays.asList(remoteRepo.getPackageIDs());
		
		for (PID currentRemotePID : remotePIDs){
			
			// no need to perform validity checks since the remote repo
			// shall only deliver valid packages
			
			// add packages that have not yet been downloaded
			if(!localRepoMirror.containsPackage(currentRemotePID)){
				localRepoMirror.addPackage(remoteRepo.getPackage(currentRemotePID));
			}

		}
		
		// remove any packages from repo that are no longer provided by the remote repo
		for (PID pid : getPackageIDs()){
			if (!remotePIDs.contains(pid)){
				unregister(pid);
			}
		}
		
		// set to latest update dates
		mirrorTimestamp = ((RemoteFeedRepository)remoteRepo).lastUpdated();
		cacheDirectory.setLastModified(mirrorTimestamp.getTime());
		
		// mark init done
		initDone = true;
	}

	/**
	 * A task which re-computes the directory's fingerprint and
	 * triggers a content reload if required.
	 * 
	 * @author Matthias Mueller
	 *
	 */
	private final class UpdateContentThread extends Thread {

		@Override
		public void run() {

			logger.info("Loading remote repository from URL " +  atomFeedURL.toString());
			// create new remote repo
			remoteRepo = new RemoteFeedRepository(atomFeedURL);

			// add change listener
			remoteRepo.addRepositoryChangeListener(new RepositoryChangeListener() {
				@Override
				public void onRepositoryUpdate(MovingCodeRepository updatedRepo) {
					updateLocalMirror();
				}
			});
			logger.info("Finished loading remote repository from URL " +  atomFeedURL.toString());
			
			// since we added a change listener, an update may already have taken place before we get here
			// so we need to check initDone variable.
			if (!initDone){
				updateLocalMirror();
			}
		}
	}
	
}
