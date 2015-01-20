/**
 * ﻿Copyright (C) 2012-2015
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
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.writer.Writer;
import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

/**
 * A {@link CodePackageFeed} is a class that provides access to a set of {@link GeoprocessingFeedEntry}.
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class CodePackageFeed {

	static Logger logger = Logger.getLogger(CodePackageFeed.class);

	private final Feed feed;
	
	public static final String feedMimeType = "application/atom+xml";
	public static final String PACKAGE_MIMETYPE = "application/zip";
	public static final String PACKAGE_DESCRIPTION_MIMETYPE = "text/xml";
	public static final String PACKAGE_LINK_REL = "enclosure";
	public static final String DETAILED_DESCRIPTION_LINK_REL = "alternate";

	/**
	 * Construct a {@link CodePackageFeed} from an atom XML stream
	 * 
	 * @param atomStream {@link InputStream}
	 */
	public CodePackageFeed(InputStream atomStream) {
		logger.debug("Loading Feed from " + atomStream);

		Parser parser = Abdera.getInstance().getParser();
		Document<Feed> doc = parser.parse(atomStream);
		feed = doc.getRoot();

		logger.info("New Feed: " + feed.getTitle());
	}

	/**
	 * Construct a {@link CodePackageFeed} from a given {@link FeedTemplate} 
	 * 
	 * @param template {@link FeedTemplate}
	 */
	public CodePackageFeed(FeedTemplate template) {

		Abdera abdera = new Abdera();
		feed = abdera.newFeed();

		// set ID
		feed.setId(template.getFeedURL());
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
	 * @param entryID
	 * @param mcp
	 * @return
	 * 
	 * TODO: add link with relation "self" to follow an atom recommendation
	 */
	public synchronized boolean addEntry(final MovingCodePackage mcp, final String webRoot){
		// add only if ID is still free
		String entryId = mcp.getPackageId().name;
		if (!containsEntry(entryId)){
			GeoprocessingFeedEntry entry = new GeoprocessingFeedEntry(
					webRoot + entryId,
					mcp.getDescriptionAsDocument(),
					mcp.getTimestamp().toDate(),
					webRoot + mcp.getPackageId().toString() + ".zip",
					webRoot + mcp.getPackageId().toString() + ".xml"
			);
			feed.addEntry(entry.getAtomEntry());

			// call global time stamp update routine
			updateFeedTimestamp();
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns a {@link GeoprocessingFeedEntry} for a given entryID.
	 * If the entryID is not associated with a package, this method returns <code>null</code>.
	 * 
	 * @param entryID {@link String}
	 * @return {@link GeoprocessingFeedEntry} -  the entry
	 */
	public GeoprocessingFeedEntry getFeedEntry(final String entryID){
		for (Entry currentEntry : feed.getEntries()){
			if (currentEntry.getId().toString().equals(entryID)){
				return new GeoprocessingFeedEntry(currentEntry);
			}
		}
		return null;
	}

	/**
	 * Returns a {@link MovingCodePackage} for a given entryID.
	 * If the entryID is not associated with a package, this method returns <code>null</code>.
	 * 
	 * @param entryID {@link String}
	 * @return {@link MovingCodePackage} - the package
	 */
	public MovingCodePackage getPackage(final String entryID){
		GeoprocessingFeedEntry gpfe = getFeedEntry(entryID);
		// if there is no entry or if its URL is invalid, return null
		if (gpfe == null || gpfe.getZipPackageURL() == null){
			return null;
		} 
		// else: return new package
		else {
			return new MovingCodePackage(gpfe.getZipPackageURL());
		}
	}

	/**
	 * Write this feed to the given output stream. Might throw an exception if the output stream signals an IO
	 * Exception.
	 * 
	 * @param os {@link OutputStream}
	 * @throws IOException {@link IOException}
	 */
	public void write(OutputStream os) throws IOException {
		// Writer writer = Abdera.getInstance().getWriterFactory().getWriter("prettyxml");
		// TODO: pipe stream through a filter that removes lines with nothing but spaces in it

		Writer writer = Abdera.getInstance().getWriterFactory().getWriter();
		writer.writeTo(feed, os);
		// feed.writeTo(os);
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
		// call global time stamp update routine
		updateFeedTimestamp();
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

	/**
	 * checks all update timestamps of the feed entries and sets a new update timestamp for the whole feed.
	 */
	private final void updateFeedTimestamp() {
		Date lastUpdate = feed.getUpdated();

		for (Entry currentEntry : feed.getEntries()) {
			if (currentEntry.getUpdated().after(lastUpdate)) {
				lastUpdate = currentEntry.getUpdated();
			}
		}
		feed.setUpdated(lastUpdate);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof CodePackageFeed) ){
			return false;
		}
		CodePackageFeed ref = (CodePackageFeed) obj;
		return ref.feed.getId().equals(this.feed.getId());
	}
	
	@Override
	public int hashCode() {
		return this.feed.getId().hashCode();
	}

}
