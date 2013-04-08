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

import org.apache.abdera.model.Entry;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.feed.GeoprocessingFeed;
import org.n52.movingcode.runtime.feed.GeoprocessingFeedEntry;

/**
 * This class implements an {@link IMovingCodeRepository} for Remote Geoprocessing Feeds
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class RemoteFeedRepository extends AbstractRepository {

    static Logger logger = Logger.getLogger(RemoteFeedRepository.class);

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
        InputStream stream = null;
        try {
            logger.debug("Create RemoteFeedRepository from " + atomFeedURL);
            stream = atomFeedURL.openStream();
            GeoprocessingFeed feed = new GeoprocessingFeed(stream);

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
            System.err.println("Could read feed from URL: " + atomFeedURL);
        }
        finally {
            if (stream != null)
                try {
                    stream.close();
                }
                catch (IOException e) {
                    logger.error("Could not close GeoprocessingFeed stream.", e);
                }
        }

        logger.trace("Created!");
    }

}
