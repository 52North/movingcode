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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

/**
 * A {@link GeoprocessingFeed} is a class that provides access to a set of {@link GeoprocessingFeedEntry}.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class GeoprocessingFeed {

	static Logger logger = Logger.getLogger(GeoprocessingFeed.class);

	private final Feed feed;
	
	public static final String atomFeedFileName = "feed.xml";
	public static final String feedMimeType = "application/atom+xml";
	public static final String PACKAGE_MIMETYPE = "application/zip";
	public static final String PACKAGE_DESCRIPTION_MIMETYPE = "text/xml";
	public static final String PACKAGE_LINK_REL = "enclosure";
	public static final String DETAILED_DESCRIPTION_LINK_REL = "alternate";
	
	/**
	 * No argument constructor. This creates an empty feed without any of the
	 * mandatory elements. Shall only be used by internal methods.
	 *  
	 */
	protected GeoprocessingFeed(){
		Abdera abdera = new Abdera();
		this.feed = abdera.newFeed();
	}
	
	/**
	 * Construct a {@link GeoprocessingFeed} from an atom XML stream
	 * 
	 * @param atomStream {@link InputStream}
	 */
	public GeoprocessingFeed(InputStream atomStream) {
		logger.debug("Loading Feed from " + atomStream);

		Parser parser = Abdera.getInstance().getParser();
		Document<Feed> doc = parser.parse(atomStream);
		this.feed = doc.getRoot();

		logger.info("New Feed: " + this.feed.getTitle());
	}
	
	/**
	 * Construct a {@link GeoprocessingFeed} from a given {@link FeedTemplate} 
	 * 
	 * @param template {@link FeedTemplate}
	 */
	public GeoprocessingFeed(FeedTemplate template) {

		Abdera abdera = new Abdera();
		feed = abdera.newFeed();

		// set ID
		feed.setId(template.getID());
		// set title and subtitle
		feed.setTitle(template.getFeedTitle());
		feed.setSubtitle(template.getFeedSubtitle());
		// set last update
		feed.setUpdated(new Date());
		// set author
		if (template.getFeedAuthorEmail() != null) {
			feed.addAuthor(template.getFeedAuthorName(), template.getFeedAuthorEmail(), null);
		}
		else {
			feed.addAuthor(template.getFeedAuthorName());
		}
		// set link
		feed.addLink(template.getFeedURL());
		feed.addLink(template.getFeedURL(), "self");

	}
	
	/**
	 * 
	 * 
	 * @param entryID
	 * @param mcp
	 * @return
	 * 
	 * TODO: add link with relation "self" to follow an atom recommendation
	 */
	public synchronized boolean addEntry(String entryID, MovingCodePackage mcp){
		// add only if ID is still free
		if (!containsEntry(entryID)){
			String feedWebRoot = getFeedWebRoot();
			GeoprocessingFeedEntry entry = new GeoprocessingFeedEntry(
					mcp.getDescription(),
	                mcp.getTimestamp(),
	                feedWebRoot + "package.zip",
	                feedWebRoot + "packagedescription.xml");
			feed.addEntry(entry.getAtomEntry());
			return true;
		}
		else {
			return false;
		}
	}
	
	@Deprecated
	public void updateFeed(Map<String, GeoprocessingFeedEntry> candidateEntries) {
		// keySet of candidate entries
		Set<String> candidateIDs = candidateEntries.keySet();
		// keySet of existing entries; is filled in the loop
		Set<String> feedIDs = new HashSet<String>();

		feed.setUpdated(new Date(System.currentTimeMillis()));

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
			logger.info("Adding new feed entry for: " + gpEntry.getIdentifier());
			feed.addEntry(gpEntry.getAtomEntry());
		}

		// set new update timestamp of the feed
		updateFeedTimestamp();
	}


	/**
	 * Returns an Array of Feed Entries. This is a snapshot of the available entries present at the time the method
	 * was called. Changes in the feed are not propagated to this array.
	 * 
	 * @return Array of {@link Entry}
	 */
	@Deprecated
	public Entry[] getEntries() {
		return feed.getEntries().toArray(new Entry[feed.getEntries().size()]);
	}
	
	public GeoprocessingFeedEntry getFeedEntry(String entryID){
		for (Entry currentEntry : feed.getEntries()){
			if (currentEntry.getId().toString().equals(entryID)){
				return new GeoprocessingFeedEntry(currentEntry);
			}
		}
		return null;
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
		writer.writeTo(feed, os);
		// feed.writeTo(os);
	}

	/**
	 * checks all update timestamps of the feed entries and sets a new update timestamp for the whole feed.
	 */
	private final void updateFeedTimestamp() {
		Date lastUpdate = feed.getUpdated();

		for (Entry currentEntry : getEntries()) {
			if (currentEntry.getUpdated().after(lastUpdate)) {
				lastUpdate = currentEntry.getUpdated();
			}
		}

		feed.setUpdated(lastUpdate);
	}

	/**
	 * Return the last update <updated>YYYY-MM-DDThh:mm:ss.nnn</updated>
	 * of this feed.
	 * 
	 * @return {@link Date} - last known update of this feed
	 */
	public Date lastUpdated(){
		return feed.getUpdated();
	}
	
	/**
	 * Returns the IDs of all registered entries.
	 * 
	 * @return {@link String} - the IDs
	 */
	public String[] getEntryIDs(){
		ArrayList<String> allIDs = new ArrayList<String>();
		for (Entry currentEntry : feed.getEntries()){
			allIDs.add(currentEntry.getId().toString());
		}
		return allIDs.toArray(new String[allIDs.size()]);
	}
	
	/**
	 * 
	 * @param entryID
	 * @return {@link Date} - the updated time stamp for the entryID (returns <code>null</code> if the entry is not registered.)
	 */
	public Date getEntryUpdatedTimeStamp(String entryID){
		for (Entry currentEntry : feed.getEntries()){
			if (currentEntry.getId().toString().equals(entryID)){
				return currentEntry.getUpdated();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @param entryID {@link String}
	 * @return {@link Date} - the updated time stamp for the entryID (returns <code>null</code> if the entry is not registered.)
	 */
	public void setEntryUpdatedTimeStamp(String entryID, Date upDate){
		for (Entry currentEntry : feed.getEntries()){
			if (currentEntry.getId().toString().equals(entryID)){
				currentEntry.setUpdated(upDate);
			}
		}
	}
	
	/**
	 * Helper get the webRoot from the first feed/link/href element
	 * Contains a trailing "/"!
	 * 
	 * If no valid web root can be obtained from this feed, an empty string will be returned.
	 * 
	 * @return {@link String} - the webRoot string
	 */
	private String getFeedWebRoot(){
		// example: <link href="http://services2.glues.geo.tu-dresden.de/feeds/gpfeed.xml" rel="self" type="application/atom+xml"/>
		
		// find the link with the self relation and use its value to determine the web root
		for (Link currentLink : feed.getLinks()){
			if (currentLink.getRel().equals("self")){
				String webRoot = currentLink.getHref().toString();
				// truncate trailing feed.xml
				webRoot = webRoot.substring(0, webRoot.indexOf(feedMimeType));
				return webRoot;
			}
		}
		
		// "default" value for web root is "" (empty String)
		return "";
	}
	
	/**
	 * Contains check for entryIDs
	 * 
	 * @param entryID
	 * @return <code>true|false</code>
	 */
	private boolean containsEntry(String entryID){
		List<String> entries = Arrays.asList(getEntryIDs());
		return entries.contains(entryID);
		
	}
	
}
