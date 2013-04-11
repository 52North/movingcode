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
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.abdera.model.Entry;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.feed.GeoprocessingFeed;
import org.n52.movingcode.runtime.feed.GeoprocessingFeedEntry;

/**
 * This class implements an {@link IMovingCodeRepository} for Remote Geoprocessing Feeds.
 * 
 * Performs occasional checks for updated content.
 * (Interval for periodical checks is given by {@link IMovingCodeRepository#remotePollingInterval})
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class RemoteFeedRepository extends AbstractRepository {

	static Logger logger = Logger.getLogger(RemoteFeedRepository.class);
	
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
		load();
		// start timer daemon
		timerDaemon = new Timer(true);
		timerDaemon.scheduleAtFixedRate(new CheckFeed(), 0, IMovingCodeRepository.remotePollingInterval);
	}
	
	private void load(){
		InputStream stream = null;
		try {
			logger.debug("Create RemoteFeedRepository from " + atomFeedURL);
			// TODO: Do it with Apache HTTPClient
			stream = atomFeedURL.openStream();
			GeoprocessingFeed feed = new GeoprocessingFeed(stream);
			lastFeedUpdate = feed.lastUpdated();

			for (Entry entry : feed.getEntries()) {
				// create new moving code package from the entry
				GeoprocessingFeedEntry gpfe = new GeoprocessingFeedEntry(entry);
				logger.trace("Loading entry " + gpfe.toString());

				// FIXME this call slows down startup of WPS server
				MovingCodePackage mcPackage = new MovingCodePackage(gpfe);

				// validate
				// and add to package map
				// and add current file to zipFiles map
				if (mcPackage.isValid()) {
					register(mcPackage);
				}
				else {
					logger.debug("Info: " + atomFeedURL.toString() + " contains an invalid package: "
							+ mcPackage.getPackageIdentifier());
				}
			}

			stream.close();
		}
		catch (IOException e) {
			logger.error("Could read feed from URL: " + atomFeedURL);
		}
		finally {
			if (stream != null)
				try {
					stream.close();
				}
			catch (IOException e) {
				logger.error("Could not close GeoprocessingFeed stream.", e);
				stream = null;
			}
		}

		logger.trace("Created!");
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
				GeoprocessingFeed feed = new GeoprocessingFeed(stream);
				
				// if feed's update time is newer than last known update time
				// re-init the feed
				if (feed.lastUpdated().after(lastFeedUpdate)){
					logger.info("Repository content has silently changed. Running update ...");
					
					// clear contents and reload
					clear();
					load();
					
					logger.info("Reload finished. Calling Repository Change Listeners.");
					
					informRepositoryChangeListeners();
				}
				stream.close();
			}
			catch (IOException e) {
				logger.error("Could read feed from URL: " + atomFeedURL);
			}
			finally {
				if (stream != null)
					try {
						stream.close();
					}
				catch (IOException e) {
					logger.error("Could not close GeoprocessingFeed stream.", e);
					stream = null;
				}
			}		
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
