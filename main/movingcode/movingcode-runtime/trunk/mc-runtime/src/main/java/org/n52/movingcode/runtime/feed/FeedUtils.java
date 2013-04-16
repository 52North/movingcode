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
		List<String> folderPIDs = Arrays.asList(repo.getPackageIDs());
		List<String> feedPIDs = Arrays.asList(gpFeed.getEntryIDs());
		List<String> checkedFeedPIDs = new ArrayList<String>();
		
		for (String currentFolderPID: folderPIDs){
			// 1. normalize folder PID
			String feedPID = RepositoryUtils.normalizePackageID(currentFolderPID);
			// 2. for each local package: check if it was previously present in AtomFeed
			if (feedPIDs.contains(feedPID)){
				Date folderTimeStamp = repo.getPackageTimestamp(currentFolderPID);
				Date feedTimeStamp = gpFeed.getEntryUpdatedTimeStamp(feedPID);
				// 2.a if so: check time stamp to determine if it was updated
				if (folderTimeStamp.after(feedTimeStamp)){
					// set new update timestamp
					gpFeed.setEntryUpdatedTimeStamp(feedPID, folderTimeStamp);
					// TODO: do we have to update even more information?
				}
				// indicate that we have updated/checked this local PID
				checkedFeedPIDs.add(feedPID);
			}
			// 2.b if not: just add the current package
			else {
				gpFeed.addEntry(feedPID, repo.getPackage(currentFolderPID));
			}
		}
		
		// TODO: write feed back to disk
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
