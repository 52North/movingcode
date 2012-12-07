package org.n52.movingcode.runtime.feed;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.MovingCodeRepository;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;


public class FeedGenerator {

	static Logger logger = Logger.getLogger(FeedGenerator.class);
	
	/**
	 * 
	 * Creates and updates atom feed from a number of Moving Code packages
	 * 
	 */
	public static void generate(
			String feedFileName,
			String webFolder,
			String baseURL,
			String repositoryFolder,
			FeedTemplate template
			){
		try {
	    	logger.info("Building new Feed ...");
	    	if (template != null){
	    		GeoprocessingFeed feed = new GeoprocessingFeed(template);
	    		updateFeedFile(feed, baseURL, feedFileName, repositoryFolder, webFolder, null);
	    	} else {
	    		updateFeedFile(null,baseURL, feedFileName, repositoryFolder, webFolder, template);
	    	}
	    	logger.info("Finished.");
	    } catch (Exception e) {
	    	logger.error("Feed generation failed!" + e.getMessage());
	    }
	}
	
	private static void updateFeedFile (GeoprocessingFeed feed, String rootURL, String targetFeedFileName, String repositoryFolder, String webFolder, FeedTemplate template) throws Exception{
		
		
		// REMOVE trailing "/" from base folder if necessary
		if (webFolder.endsWith("/") || webFolder.endsWith("\\")){
			webFolder = webFolder.substring(0, webFolder.length()-1);
		}
		
		// ADD trailing "/" to root URL if necessary
		if (!rootURL.endsWith("/")){
			rootURL = rootURL.concat("/");
		}
		
		File feedFile = new File(webFolder + File.separator + targetFeedFileName);
		
		// if a template is provided run the update on an existing feed
		if (feed == null){
			feed = new GeoprocessingFeed(template);
		}
		
		Map<String,GeoprocessingFeedEntry> candidateFeedEntries = new HashMap<String,GeoprocessingFeedEntry>();
		MovingCodeRepository localRepo = new MovingCodeRepository(new File(repositoryFolder));
		for (String currentLocalPackageID : localRepo.getPackageIDs()){
			logger.info("Processing package: " + currentLocalPackageID);
			
			String dumpLocation = webFolder + File.separator + currentLocalPackageID + File.separator;
			String webLocation = rootURL + currentLocalPackageID + "/";
			
			// dump package to new web location
			File newLocation = new File(dumpLocation + "package.zip");
			MovingCodePackage packageToDump = localRepo.getPackage(currentLocalPackageID);
			packageToDump.dumpPackage(newLocation);
			
			// dump description to new web location
			packageToDump.dumpDescription(new File(dumpLocation + "packagedescription.xml"));
			
			GeoprocessingFeedEntry entry = new GeoprocessingFeedEntry(
					localRepo.getPackageDescription(currentLocalPackageID),
					localRepo.getPackageTimestamp(currentLocalPackageID),
					webLocation + "package.zip",
					webLocation + "packagedescription.xml"
			);
			
			// public package ID is the published URL
			String publicPackageID = webLocation + "package.zip";
			candidateFeedEntries.put(publicPackageID, entry);
		}
		
		feed.updateFeed(candidateFeedEntries);
		
		logger.info("Writing new Feed to disk.");
		feed.write(new FileOutputStream(feedFile));
	}
	
	
	
}
