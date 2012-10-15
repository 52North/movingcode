package org.n52.movingcode.runtime.feed;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.n52.movingcode.runtime.MovingCodeRepository;


public class FeedGenerator {


	
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
	    	System.out.println("Building new Feed ...");
	    	if (template != null){
	    		GeoprocessingFeed feed = new GeoprocessingFeed(template);
	    		updateFeedFile(feed, baseURL, feedFileName, repositoryFolder, webFolder);
	    	} else {
	    		updateFeedFile(null,baseURL, feedFileName, repositoryFolder, webFolder);
	    	}
			System.out.println("Writing new Feed to disk.");
			System.out.println("Finished.");
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
	}
	
	private static void updateFeedFile (GeoprocessingFeed feed, String rootURL, String targetFeedFileName, String repositoryFolder, String webFolder) throws Exception{
		
		
		// REMOVE trailing "/" from base folder if necessary
		if (webFolder.endsWith("/") || webFolder.endsWith("\\")){
			webFolder = webFolder.substring(0, webFolder.length()-1);
		}
		
		// ADD trailing "/" to root URL if necessary
		if (!rootURL.endsWith("/")){
			rootURL = rootURL.concat("/");
		}
		
		File feedFile = new File(webFolder + File.separator + targetFeedFileName);
		
		// if no template is provided run the update on an existing feed
		if (feed == null){
			feed = new GeoprocessingFeed(new FileInputStream(feedFile));
		}
		
		Map<String,GeoprocessingFeedEntry> candidateFeedEntries = new HashMap<String,GeoprocessingFeedEntry>();
		MovingCodeRepository localRepo = new MovingCodeRepository(new File(repositoryFolder));
		for (String currentID : localRepo.getRegisteredPackageIDs()){
			
			String dumpLocation = webFolder + File.separator + currentID + File.separator;
			String webLocation = rootURL + currentID + "/";
			
			// dump package to new web location
			localRepo.getPackage(currentID).dumpPackage(new File(dumpLocation + "package.zip"));
			
			// dump description to new web location
			localRepo.getPackage(currentID).dumpDescription(new File(dumpLocation + "packagedescription.xml"));
			
			GeoprocessingFeedEntry entry = new GeoprocessingFeedEntry(
					localRepo.getPackageDescription(currentID),
					localRepo.getPackageTimestamp(currentID),
					webLocation + "package.zip",
					webLocation + "packagedescription.xml"
			);
			
			candidateFeedEntries.put(currentID, entry);
		}
		
		feed.updateFeed(candidateFeedEntries);
		
		feed.write(new FileOutputStream(feedFile));
	}
	
//	/**
//	 * @param args
//	 * @author Matthias Mueller
//	 * 
//	 * Creates and updates atom feed from a number of Moving Code packages
//	 * 
//	 */
//	public static void main(String[] args) {
//		
//		String feedFileName = null;
//		String webFolder = null;
//		String baseURL = null;
//		String repositoryFolder = null;
//		
//		if (args.length == 4) {
//		    try {
//		    	repositoryFolder = args[0];
//		    	webFolder = args[1];
//		    	baseURL = args[2];
//		    	feedFileName = args[3];
//		    	
//		    	System.out.println("Building new Feed ...");
//		    	updateFeedFile(baseURL, feedFileName, repositoryFolder, webFolder);
//				System.out.println("Writing new Feed to disk.");
//				System.out.println("Finished.");
//		    } catch (Exception e) {
//		        System.err.println("Wrong arguments given.");
//		        System.out.println("Usage: FeedGenerator [repositoryFolder] [webFolder] [webRootURL] [feedFileName]");
//		        System.exit(1);
//		    }
//		} else {
//			System.err.println("Wrong arguments given.");
//			System.out.println("Usage: FeedGenerator [repositoryFolder] [webFolder] [webRootURL] [feedFileName]");
//			System.exit(1);
//		}
//		
//	}
}
