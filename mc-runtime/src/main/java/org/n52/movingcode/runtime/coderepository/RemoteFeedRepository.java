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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.feed.CodePackageFeed;

/**
 * This class implements an {@link MovingCodeRepository} for Remote Geoprocessing Feeds.
 * 
 * Performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link MovingCodeRepository#remotePollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class RemoteFeedRepository extends AbstractRepository {

	// Atom Feed URL
	private final URL atomFeedURL;

	// Last known update of the feed
	private Date lastFeedUpdate;

	// Timer for content updates
	private final Timer timerDaemon;



	/**
	 * 
	 * Constructor for atom feed repositories. Tries to access the atom feed at the given URL and scans its
	 * entries. Then attempts to interpret the entries as MovingCodePackages. Packages that do not validate
	 * will be ignored
	 * 
	 * @param atomFeedURL
	 *        {@link URL} - Direct HTTP link to the Geoprocessing Feed.
	 * 
	 */
	public RemoteFeedRepository(final URL atomFeedURL) {
		this.atomFeedURL = atomFeedURL;
		reloadContent();
		// start timer daemon
		timerDaemon = new Timer(true);
		timerDaemon.scheduleAtFixedRate(new CheckFeed(), 0, MovingCodeRepository.remotePollingInterval);
	}

	private synchronized void reloadContent(){
		
		PackageInventory newInventory = new PackageInventory();
		
		InputStream stream = null;
		try {
			LOGGER.debug("Create RemoteFeedRepository from " + atomFeedURL);
			// TODO: Do it with Apache HTTPClient
			stream = atomFeedURL.openStream();
			CodePackageFeed feed = new CodePackageFeed(stream);
			lastFeedUpdate = feed.lastUpdated();

			for (String currentEntryID : feed.getEntryIDs()) {
				// create new moving code package from the entry
				MovingCodePackage mcp = feed.getPackage(currentEntryID);
				LOGGER.debug("Loading package for feed entry " + currentEntryID);

				// validate
				// and add to package map
				// and add current file to zipFiles map
				if (mcp.isValid()) {
					newInventory.add(mcp);
				}
				else {
					LOGGER.debug("Info: " + atomFeedURL.toString() + " contains an invalid package: "
							+ mcp.getPackageId().toString());
				}
			}

			stream.close();
		}
		catch (IOException e) {
			LOGGER.error("Could read feed from URL: " + atomFeedURL);
		}
		finally {
			if (stream != null)
				try {
					stream.close();
				}
			catch (IOException e) {
				LOGGER.error("Could not close GeoprocessingFeed stream.", e);
				stream = null;
			}
		}
		
		updateInventory(newInventory);
	}

	/**
	 * A task which re-checks the remote feed's last update and
	 * triggers a content reload if required.
	 * 
	 * @author Matthias Mueller
	 * 
	 */
	private final class CheckFeed extends TimerTask {

		@Override
		public void run() {
			InputStream stream = null;
			
			try {
				// TODO: Do it with Apache HTTPClient
				stream = atomFeedURL.openStream();
				CodePackageFeed feed = new CodePackageFeed(stream);

				// if feed's update time is newer than last known update time
				// re-read the feed and update contents accordingly
				if (feed.lastUpdated().after(lastFeedUpdate)){
					LOGGER.info("Repository content has  changed. Running update ...");
					
					// do a re-load
					reloadContent();
					
				}
				
				lastFeedUpdate = feed.lastUpdated();
				stream.close();
				
			}
			catch (IOException e) {
				LOGGER.error("Could read feed from URL: " + atomFeedURL);
			}
			finally {
				if (stream != null)
					try {
						stream.close();
					}
				catch (IOException e) {
					LOGGER.error("Could not close GeoprocessingFeed stream.", e);
					stream = null;
				}
			}
			
			LOGGER.info("Reload finished.");
		}
	}
	
	/**
	 * Returns the time at which the feed was last updated.
	 * 
	 * @return {@link Date}
	 */
	protected Date lastUpdated(){
		return lastFeedUpdate;
	}

}
