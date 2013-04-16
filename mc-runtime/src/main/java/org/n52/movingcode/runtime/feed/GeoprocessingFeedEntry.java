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

import java.util.Date;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.apache.log4j.Logger;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class represents an entry in a GeoprocessingFeed.
 * 
 * TODO: implements {@link Entry} ? TODO: Make use of Atom license extension -->
 * http://tools.ietf.org/html/rfc4946
 * 
 * @author Matthias Mueller, TU Dresden
 *
 * TODO: make all methods protected
 * TODO: Should be used by {@link GeoprocessingFeed} class only.
 * 
 */
public final class GeoprocessingFeedEntry {

	private Entry entry;

	static Logger logger = Logger.getLogger(GeoprocessingFeedEntry.class);

	/**
	 * Creates a new entry from an existing entry. Both entries share the same content. If one of them is
	 * changed, the changes are propagated to its twin.
	 * 
	 * @param entry
	 * 
	 * TODO: decrease visibility to protected
	 */
	protected GeoprocessingFeedEntry(Entry entry) {
		this.entry = entry;
	}
	
	protected GeoprocessingFeedEntry(){
		this.entry = makeNewEntry();
	}

	/**
	 * Constructor for a brand new GeoprocessingFeedEntry.
	 * 
	 * 
	 * @param packageDesc
	 *        {@link PackageDescriptionDocument} - the packageDescription
	 * @param creationDate
	 *        {@link Date} - the creation Date of the new entry.
	 * @param packageURL
	 *        {@link String} - a URL from which the zipped content of the package can be retrieved
	 * @param descriptionURL
	 *        {@link String} - a URL that links to additional documentation of the package contents
	 *        
	 * TODO: decrease visibility to protected
	 */
	protected GeoprocessingFeedEntry(String entryID, PackageDescriptionDocument packageDesc,
			Date creationDate,
			String packageURL,
			String descriptionURL) {

		if (packageDesc.getPackageDescription().getContractedFunctionality().isSetWpsProcessDescription()) {
			ProcessDescriptionType wpsDesc = packageDesc.getPackageDescription().getContractedFunctionality().getWpsProcessDescription();
			this.entry = makeNewEntry();

			String identifier = wpsDesc.getIdentifier().getStringValue();

			// String title = wpsDesc.getTitle().getStringValue();

			// String summary = null;
			// if (wpsDesc.isSetAbstract()){
			// String procAbstract = wpsDesc.getAbstract().getStringValue();
			// summary = "<div><b>" + title + "</b><br/>" + procAbstract + "</div";
			// } else {
			// summary = "<div><b>" + title + "</b><br/>Sorry, the short description is missing.</div";
			// }
			// assert(summary!=null);

			this.entry.setTitle(entryID); // set title
			this.entry.setId(entryID); // set identifier
			this.entry.setPublished(creationDate); // set published date
			this.entry.setUpdated(creationDate); // set creation date

			// this.entry.setSummaryAsHtml(summary); // set summary //TODO: a detailed IO description of the
			// feed,
			// TODO: either smaller summary or much less content
			// we cannot be sure that summary is properly displayed (rss readers may discard HTML tags)

			// this.entry.setContentElement(makeContent(packageURL)); // set package content
			// TODO: Content should be some nice HTML stuff
			this.entry.setContentElement(generateHTMLContent(wpsDesc));

			this.entry.addLink(makePackageDescriptionLink(descriptionURL)); // set package description link
			this.entry.addLink(makePackageLink(packageURL)); // also set package link

			// TODO: add alternate link
			// e.g. <link rel="alternate" href="http://www.gtfs-data-exchange.com/meta/6195524"
			// type="text/html"/>
			// use DETAILED_DESCRIPTION_LINK_REL
		}
		else {
			// TODO: support other descriptions, e.g. WSDL
		}

	}

	/**
	 * Returns the identifier of this entry.
	 * 
	 * @return {@link String}
	 */
	public String getIdentifier() {
		return entry.getId().toString();
	}
	
	/**
	 * Set the identifier of this entry.
	 * 
	 * @param entryID {@link String}
	 */
	public void setIdentifier(final String entryID){
		entry.setId(entryID);
	}

	/**
	 * Returns the publication date of this entry.
	 * 
	 * @return {@link Date}
	 */
	public Date getPublished() {
		return entry.getPublished();
	}
	
	/**
	 * Sets the publication date of this entry.
	 * 
	 * @param pubDate {@link Date}
	 */
	public void setPublished(Date pubDate){
		entry.setPublished(pubDate);
	}

	/**
	 * Returns the last modified date of this entry.
	 * 
	 * @return {@link Date}
	 */
	public Date getUpdated() {
		return entry.getUpdated();
	}
	
	/**
	 * Sets the Update date of this entry.
	 * 
	 * @param upDate {@link Date}
	 */
	public void setUpdated(Date upDate){
		entry.setUpdated(upDate);
	}

	/**
	 * Return the general atom representation of this entry.
	 * 
	 * @return {@link Entry}
	 */
	public Entry getAtomEntry() {
		return this.entry;
	}

	/**
	 * private static method to create a blank {@link Entry}
	 * 
	 * @return {@link Entry}
	 */
	private static Entry makeNewEntry() {
		return Abdera.getInstance().newEntry();
	}

	/**
	 * Updates this entry with the contents of a newer one. Procedure is as follows:
	 * 
	 * If the otherEntry is newer: 1. its contents will be copied to this entry 2. the old publication date
	 * will be kept
	 * 
	 * @param otherEntry
	 *        {@link GeoprocessingFeedEntry}
	 */
	public void updateWith(GeoprocessingFeedEntry otherEntry) {
		// check if other entry is newer
		if (otherEntry.getUpdated().after(this.entry.getUpdated())) {
			// replace with newer entry but keep old PublishedDate
			Date published = this.getPublished();
			this.entry = otherEntry.getAtomEntry();
			this.entry.setPublished(published);
			logger.info("Updating feed entry for: " + this.getIdentifier());
		}
		// if it is not newer - just keep the old one!
	}

	// private static Content makeContent(String packageURL){
	// Content content = Abdera.getInstance().getFactory().newContent();
	// content.setMimeType(PACKAGE_MIMETYPE);
	// content.setSrc(packageURL);
	// return content;
	// }

	/**
	 * Static helper method that creates an Atom {@link Link} object from a given package URL.
	 * 
	 * @param packageURL
	 *        {@link String} - the package URL
	 * @return {@link Link}
	 */
	private static final Link makePackageLink(String packageURL) {
		Link link = Abdera.getInstance().getFactory().newLink();
		link.setHref(packageURL);
		link.setMimeType(GeoprocessingFeed.PACKAGE_MIMETYPE);
		link.setRel(GeoprocessingFeed.PACKAGE_LINK_REL);
		return link;
	}

	/**
	 * Static helper method that creates an Atom {@link Link} object from a given description URL.
	 * 
	 * @param descriptionURL
	 *        {@link String} - the package description URL
	 * @return {@link Link}
	 */
	private static Link makePackageDescriptionLink(String descriptionURL) {
		Link link = Abdera.getInstance().getFactory().newLink();
		link.setHref(descriptionURL);
		link.setMimeType(GeoprocessingFeed.PACKAGE_DESCRIPTION_MIMETYPE);
		link.setRel(GeoprocessingFeed.PACKAGE_LINK_REL);
		return link;
	}

	/**
	 * Generates a human-readable description from a PackageDescriptionDocument. This description is return as
	 * an Atom content object.
	 * 
	 * @param wpsDesc
	 *        {@link ProcessDescriptionType}
	 * @return {@link Content}
	 */
	private static Content generateHTMLContent(final ProcessDescriptionType wpsDesc) {
		Content content = Abdera.getInstance().getFactory().newContent(Content.Type.HTML);
		content.setText(WPSDescriptionPrinter.printAsHTML(wpsDesc));

		return content;
	}

}
