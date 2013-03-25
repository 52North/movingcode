
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
            stream = atomFeedURL.openStream();
            GeoprocessingFeed feed = new GeoprocessingFeed(stream);

            for (Entry entry : feed.getEntries()) {
                // create new moving code package from the entry
                GeoprocessingFeedEntry gpfe = new GeoprocessingFeedEntry(entry);
                MovingCodePackage mcPackage = new MovingCodePackage(gpfe);

                // validate
                // and add to package map
                // and add current file to zipFiles map
                if (mcPackage.isValid()) {
                    register(mcPackage);
                }
                else {
                    System.out.println("Info: " + atomFeedURL.toString() + " contains an invalid package: "
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
    }

}
