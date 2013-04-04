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
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;

/**
 * This class provides a generator for Geoprocessing Feeds, i.e.
 * Atom feeds that serve a compilation of Moving Code packages.
 * 
 * TODO: some overlapping functionality with {@link GeoprocessingFeed}
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class FeedGenerator {

    static Logger logger = Logger.getLogger(FeedGenerator.class);

    /**
     * 
     * Creates and updates atom feed from a number of Moving Code packages
     * 
     */
    public static void generate(String feedFileName,
                                String webFolder,
                                String baseURL,
                                String repositoryFolder,
                                FeedTemplate template) {
        try {
            logger.info("Building new Feed ...");
            if (template != null) {
                GeoprocessingFeed feed = new GeoprocessingFeed(template);
                updateFeedFile(feed, baseURL, feedFileName, repositoryFolder, webFolder, null);
            }
            else {
                updateFeedFile(null, baseURL, feedFileName, repositoryFolder, webFolder, template);
            }
            logger.info("Finished.");
        }
        catch (Exception e) {
            logger.error("Feed generation failed!" + e.getMessage());
        }
    }
    
    /**
     * This method updates an existing feed {@link GeoprocessingFeed}.
     * 
     * It does three things:
     * 
     * 1) update existing entries
     * 2) add new entries
     * 3) remove old entries (TODO. implement)
     * 
     * @param feed {@link GeoprocessingFeed}
     * @param rootURL {@link String}
     * @param targetFeedFileName {@link String}
     * @param repositoryFolder {@link String}
     * @param webFolder {@link String}
     * @param template {@link FeedTemplate}
     * @throws Exception {@link Exception}
     */
    private static void updateFeedFile(GeoprocessingFeed feed,
                                       String rootURL,
                                       String targetFeedFileName,
                                       String repositoryFolder,
                                       String webFolder,
                                       FeedTemplate template) throws Exception {

        // REMOVE trailing "/" from base folder if necessary
        if (webFolder.endsWith("/") || webFolder.endsWith("\\")) {
            webFolder = webFolder.substring(0, webFolder.length() - 1);
        }

        // ADD trailing "/" to root URL if necessary
        if ( !rootURL.endsWith("/")) {
            rootURL = rootURL.concat("/");
        }

        File feedFile = new File(webFolder + File.separator + targetFeedFileName);

        // if a template is provided run the update on an existing feed
        if (feed == null) {
            feed = new GeoprocessingFeed(template);
        }

        Map<String, GeoprocessingFeedEntry> candidateFeedEntries = new HashMap<String, GeoprocessingFeedEntry>();
        IMovingCodeRepository mcRepo = IMovingCodeRepository.Factory.createFromZipFilesFolder(new File(repositoryFolder));
        
       // MovingCodeRepository localRepo = new MovingCodeRepository(new File(repositoryFolder));
        for (String currentLocalPackageID : mcRepo.getPackageIDs()) {
            logger.info("Processing package: " + currentLocalPackageID);

            String dumpLocation = webFolder + File.separator + currentLocalPackageID + File.separator;
            String webLocation = rootURL + currentLocalPackageID + "/";

            // dump package to new web location
            File newLocation = new File(dumpLocation + "package.zip");
            MovingCodePackage packageToDump = mcRepo.getPackage(currentLocalPackageID);
            packageToDump.dumpPackage(newLocation);

            // dump description to new web location
            packageToDump.dumpDescription(new File(dumpLocation + "packagedescription.xml"));

            GeoprocessingFeedEntry entry = new GeoprocessingFeedEntry(mcRepo.getPackageDescription(currentLocalPackageID),
                                                                      mcRepo.getPackageTimestamp(currentLocalPackageID),
                                                                      webLocation + "package.zip",
                                                                      webLocation + "packagedescription.xml");

            // public package ID is the published URL
            String publicPackageID = webLocation + "package.zip";
            candidateFeedEntries.put(publicPackageID, entry);
        }

        feed.updateFeed(candidateFeedEntries);

        logger.info("Writing new Feed to disk.");
        feed.write(new FileOutputStream(feedFile));
    }

}
