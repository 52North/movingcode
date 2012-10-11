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

/**
 * @author Matthias Mueller, TU Dresden
 *
 */
public final class GeoprocessingFeed {
	
	private final Feed feed;
	
	public GeoprocessingFeed(InputStream atomStream){
		Parser parser = Abdera.getInstance().getParser();
		Document<Feed> doc = parser.parse(atomStream);
		feed = doc.getRoot();
		System.out.println("Found Feed: " + feed.getTitle());
	}
	
	public GeoprocessingFeed(FeedTemplate template){
		
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
		if (template.getFeedAuthorEmail() != null){
			feed.addAuthor(template.getFeedAuthorName(), template.getFeedAuthorEmail(), null);
		} else {
			feed.addAuthor(template.getFeedAuthorName());
		}
		// set link
		feed.addLink(template.getFeedURL());
		feed.addLink(template.getFeedURL(),"self");
		
	}
	
	public void updateFeed(Map<String,GeoprocessingFeedEntry> candidateEntries){
		// keySet of candidate entries
		Set<String> candidateIDs = candidateEntries.keySet();
		// keySet of existing entries; is filled in the loop
		Set<String> feedIDs = new HashSet<String>();
		
		feed.setUpdated(new Date(System.currentTimeMillis()));
		
		// MERGE newer entries into old entries 
		for (Entry entry : feed.getEntries()){
			GeoprocessingFeedEntry currentEntry = new GeoprocessingFeedEntry(entry);
			feedIDs.add(currentEntry.getIdentifier());
			// check if there is a candidate entry with the same ID
			if (candidateIDs.contains(currentEntry.getIdentifier())){
				// merge candidate entry into existing entry
				currentEntry.mergeWith(candidateEntries.get(currentEntry.getIdentifier()));
			}
		}
		
		// ADD all completely new entries
		candidateIDs.removeAll(feedIDs);
		for (String currentID : candidateIDs){
			GeoprocessingFeedEntry gpEntry = candidateEntries.get(currentID);
			System.out.println("Adding new feed entry for: " + gpEntry.getIdentifier());
			feed.addEntry(gpEntry.getAtomEntry());
		}
		
		// set new update timestamp of the feed
		this.updateFeedTimestamp();
	}
	
	/*
	 * Returns an Array of Entries. This is a snapshot of the available
	 * entries present at the time the method was called. Changes in the feed
	 * are not propagated to this array.
	 */
	public Entry[] getEntries(){
		return feed.getEntries().toArray(new Entry[feed.getEntries().size()]);
	}
	
	/*
	 * Write this feed to the given output stream.
	 * Might throw an exception if the output stream 
	 * signals an IO Exception.
	 */
	public void write (OutputStream os) throws IOException{
		Writer writer = Abdera.getInstance().getWriterFactory().getWriter("prettyxml");
		writer.writeTo(feed, os);
		
		//feed.writeTo(os);
	}
	
	/*
	 * checks all update timestamps of the feed entries and 
	 * sets a new update timestamp for the whole feed. 
	 */
	private final void updateFeedTimestamp() {
		Date lastUpdate = this.feed.getUpdated();
		
		for (Entry currentEntry : this.getEntries()){
			if (currentEntry.getUpdated().after(lastUpdate)){
				lastUpdate = currentEntry.getUpdated();
			}
		}
		
		this.feed.setUpdated(lastUpdate);
	}
	
}
