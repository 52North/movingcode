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
package org.n52.movingcode.runtime.feed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.coderepository.LocalZipPackageRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;

/**
 * This class contains static utility methods that are used to create and update AtomFeeds for MC
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class FeedUtils {
	
	static Logger logger = Logger.getLogger(FeedUtils.class);
	
	/**
	 * Static method to update an AtomFeed file in a zipped feed directory. (Supports nested folders etc.)
	 * The AtomFeed file must reside in the root of the <code>feedFolderDirectory</code> and must be
	 * named {@value GeoprocessingFeed#atomFeedFileName}. If this file is not present it will be created. In this case,
	 * it will surely lack some mandatory information such as title, author, etc. In this case the logger
	 * will print a warning and you must manually fix the file. 
	 * 
	 * @param feedFolderDirectory {@link File} - a folder containing the zipped packages in a nested
	 *        structure as well as the AtomFeed XML file, named {@value GeoprocessingFeed#atomFeedFileName} 
	 */
	public static final boolean updateFeed(final File feedFolderDirectory){
		// 1. create a local repo on the paticular folder
		LocalZipPackageRepository repo = new LocalZipPackageRepository(feedFolderDirectory);
		
		// 2. attempt to access "feed.xml"
		File feedFile = new File(feedFolderDirectory.getAbsolutePath() + File.separator + GeoprocessingFeed.atomFeedFileName);
		GeoprocessingFeed gpFeed = readOrCreateFeed(feedFile);
		
		// 3. do the content update
		List<String> zipFilePIDs = Arrays.asList(repo.getPackageIDs());
		List<String> feedPIDs = Arrays.asList(gpFeed.getEntryIDs());
		List<String> checkedFeedPIDs = new ArrayList<String>();
		
		for (String currentZipPID: zipFilePIDs){
			// 1. build and normalize folder PID
			// remove leading feedFolder String, i.e. D:\feed1
			String feedPID = currentZipPID.substring(feedFolderDirectory.getAbsolutePath().length());
			feedPID = RepositoryUtils.normalizePackageID(feedPID);
			if (feedPID.startsWith("/")){
				feedPID = feedPID.substring(1, feedPID.length());
			}
			
			// 2. for each local package: check if it was previously present in AtomFeed
			if (feedPIDs.contains(feedPID)){
				Date folderTimeStamp = repo.getPackageTimestamp(currentZipPID);
				Date feedTimeStamp = gpFeed.getEntryUpdatedTimeStamp(feedPID);
				// 2.a if so: check time stamp to determine if it was updated
				if (folderTimeStamp.after(feedTimeStamp)){
					// set new update timestamp
					gpFeed.setEntryUpdatedTimeStamp(feedPID, folderTimeStamp);
					// write out new packagedescription
					String currentXMLFileName = currentZipPID + ".xml";
					File currentXMLFile = new File(currentXMLFileName);
					repo.getPackage(currentZipPID).dumpDescription(currentXMLFile);
					// TODO: do we have to update even more information?
				}
				// indicate that we have updated/checked this local PID
				checkedFeedPIDs.add(feedPID);
			}
			// 2.b if not: just add the current package
			else {
				String currentXMLFileName = currentZipPID + ".xml";
				// write out new packagedescription
				File currentXMLFile = new File(currentXMLFileName);
				repo.getPackage(currentZipPID).dumpDescription(currentXMLFile);
				gpFeed.addEntry(feedPID, repo.getPackage(currentZipPID));
			}
		}
		
		// 3. report unsafe packages (?)
		// TODO: delete packages, if it really makes sense,
		// maybe have a trigger or so

		feedPIDs.removeAll(checkedFeedPIDs);
		if(feedPIDs.size() != 0){
			StringBuffer report = new StringBuffer("\n");
			report.append(
					"Package folder updated. The following packages are no longer present in the remote feed."
					+ "However, they will be kept in the local mirror until you manually delete them.\n"
			);
			for (String currentPID : feedPIDs){
				report.append(currentPID + "\n");
			}
			logger.info(report.toString());
		}
		
		// write feed back to disk
		logger.info("Writing new Feed to disk.");
        try {
        	OutputStream os = new FileOutputStream(feedFile);
			gpFeed.write(os);
			os.close();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("Could write feed file " + feedFile.getAbsolutePath());
		} catch (IOException e) {
			logger.error("Could write feed file " + feedFile.getAbsolutePath());
		}
		
		// this code is only reached if an exception is thrown
		return false;
	}
	
	/**
	 * Private helper method that tries to read a {@link GeoprocessingFeed} from an AtomFeed XML file.
	 * If this read should fail, a new empty {@link GeoprocessingFeed} will be returned.
	 *  
	 * @param feedFile {@link File}
	 * @return {@link GeoprocessingFeed}
	 */
	private static final GeoprocessingFeed readOrCreateFeed(final File feedFile){
		GeoprocessingFeed gpFeed = null;
		try {
			FileInputStream fis = new FileInputStream(feedFile);
			gpFeed = new GeoprocessingFeed(fis);
			fis.close();
		} catch (FileNotFoundException e) {
			logger.warn("Could load feed file " + feedFile.getAbsolutePath());
		} catch (IOException e) {
			// assume this is not critical ...
			logger.warn("Could not close feed file. (Not sure why this has happened.)");
		}
		
		// if the feed could not be loaded, create new one 
		if (gpFeed == null){
			logger.info(feedFile + " was not found or could not be accessed. Creating empty feed." );
			gpFeed = new GeoprocessingFeed();
		}
		
		return gpFeed;
	}
	

}
