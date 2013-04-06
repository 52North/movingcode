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
/**
 * 
 */

package org.n52.movingcode.runtime.feed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;
import org.apache.log4j.Logger;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class GeoprocessingFeed {

    static Logger logger = Logger.getLogger(GeoprocessingFeed.class);

    private final Feed feed;

    public GeoprocessingFeed(InputStream atomStream) {
        Parser parser = Abdera.getInstance().getParser();
        Document<Feed> doc = parser.parse(atomStream);
        this.feed = doc.getRoot();
        logger.info("Found Feed: " + this.feed.getTitle());
    }

    public GeoprocessingFeed(FeedTemplate template) {

        Abdera abdera = new Abdera();
        this.feed = abdera.newFeed();

        // set ID
        this.feed.setId(template.getID());
        // set title and subtitle
        this.feed.setTitle(template.getFeedTitle());
        this.feed.setSubtitle(template.getFeedSubtitle());
        // set last update
        this.feed.setUpdated(new Date());
        // set author
        if (template.getFeedAuthorEmail() != null) {
            this.feed.addAuthor(template.getFeedAuthorName(), template.getFeedAuthorEmail(), null);
        }
        else {
            this.feed.addAuthor(template.getFeedAuthorName());
        }
        // set link
        this.feed.addLink(template.getFeedURL());
        this.feed.addLink(template.getFeedURL(), "self");

    }

    public void updateFeed(Map<String, GeoprocessingFeedEntry> candidateEntries) {
        // keySet of candidate entries
        Set<String> candidateIDs = candidateEntries.keySet();
        // keySet of existing entries; is filled in the loop
        Set<String> feedIDs = new HashSet<String>();

        this.feed.setUpdated(new Date(System.currentTimeMillis()));

        // MERGE newer entries into old entries
        for (Entry entry : this.feed.getEntries()) {
            GeoprocessingFeedEntry currentEntry = new GeoprocessingFeedEntry(entry);
            feedIDs.add(currentEntry.getIdentifier());
            // check if there is a candidate entry with the same ID
            if (candidateIDs.contains(currentEntry.getIdentifier())) {
                // merge candidate entry into existing entry
                currentEntry.updateWith(candidateEntries.get(currentEntry.getIdentifier()));
            }
        }

        // ADD all completely new entries
        candidateIDs.removeAll(feedIDs);
        for (String currentID : candidateIDs) {
            GeoprocessingFeedEntry gpEntry = candidateEntries.get(currentID);
            System.out.println("Adding new feed entry for: " + gpEntry.getIdentifier());
            this.feed.addEntry(gpEntry.getAtomEntry());
        }

        // set new update timestamp of the feed
        this.updateFeedTimestamp();
    }


    /**
     * Returns an Array of Feed Entries. This is a snapshot of the available entries present at the time the method
     * was called. Changes in the feed are not propagated to this array.
     * 
     * @return Array of {@link Entry}
     */
    public Entry[] getEntries() {
        return this.feed.getEntries().toArray(new Entry[this.feed.getEntries().size()]);
    }

    /**
     * Write this feed to the given output stream. Might throw an exception if the output stream signals an IO
     * Exception.
     * 
     * @param os {@link OutputStream}
     * @throws IOException {@link IOException}
     */
    public void write(OutputStream os) throws IOException {
        Writer writer = Abdera.getInstance().getWriterFactory().getWriter("prettyxml");
        writer.writeTo(this.feed, os);

        // feed.writeTo(os);
    }

    /**
     * checks all update timestamps of the feed entries and sets a new update timestamp for the whole feed.
     */
    private final void updateFeedTimestamp() {
        Date lastUpdate = this.feed.getUpdated();

        for (Entry currentEntry : this.getEntries()) {
            if (currentEntry.getUpdated().after(lastUpdate)) {
                lastUpdate = currentEntry.getUpdated();
            }
        }

        this.feed.setUpdated(lastUpdate);
    }

}
