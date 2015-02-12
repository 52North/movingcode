/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.movingcode.runtime.feed;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * This class represents an entry in a GeoprocessingFeed.
 * 
 * TODO: implements {@link Entry} ? TODO: Make use of Atom license extension -->
 * http://tools.ietf.org/html/rfc4946
 * 
 * @author Matthias Mueller, TU Dresden
 *
 * TODO: Should be used by {@link CodePackageFeed} class only.
 * 
 */
final class GeoprocessingFeedEntry {

	private Entry entry;

	private static final Logger LOGGER = LoggerFactory.getLogger(GeoprocessingFeedEntry.class);

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

			ProcessDescriptionType wpsDesc = packageDesc.getPackageDescription().getFunctionality().getWps100ProcessDescription();
			this.entry = makeNewEntry();

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

	/**
	 * Returns the identifier of this entry.
	 * 
	 * @return {@link String}
	 */
	protected String getIdentifier() {
		return entry.getId().toString();
	}
	
	/**
	 * Set the identifier of this entry.
	 * 
	 * @param entryID {@link String}
	 */
	protected void setIdentifier(final String entryID){
		entry.setId(entryID);
	}

	/**
	 * Returns the publication date of this entry.
	 * 
	 * @return {@link Date}
	 */
	protected Date getPublished() {
		return entry.getPublished();
	}
	
	/**
	 * Sets the publication date of this entry.
	 * 
	 * @param pubDate {@link Date}
	 */
	protected void setPublished(Date pubDate){
		entry.setPublished(pubDate);
	}

	/**
	 * Returns the last modified date of this entry.
	 * 
	 * @return {@link Date}
	 */
	protected Date getUpdated() {
		return entry.getUpdated();
	}
	
	/**
	 * Sets the Update date of this entry.
	 * 
	 * @param upDate {@link Date}
	 */
	protected void setUpdated(Date upDate){
		entry.setUpdated(upDate);
	}

	/**
	 * Return the general atom representation of this entry.
	 * 
	 * @return {@link Entry}
	 */
	protected Entry getAtomEntry() {
		return this.entry;
	}
	
	/**
	 * Returns the URL of the referenced Code Package 
	 * 
	 * @return {@link URL} - the URL; will be <code>null</code> if the entryID is invalid, or if the package URL in the feed is incorrect
	 */
	protected URL getZipPackageURL(){
		for (Link currentLink : entry.getLinks()){
			String rel = currentLink.getRel();
			String mt = currentLink.getMimeType().toString();
			if (rel.equals(CodePackageFeed.PACKAGE_LINK_REL)
					&& mt.equals(CodePackageFeed.PACKAGE_MIMETYPE)){
				try {
					return currentLink.getHref().toURL();
				} catch (MalformedURLException e) {
					LOGGER.error("Wrong package URL: " + currentLink.getHref().toString());
					return null;
				} catch (URISyntaxException e) {
					LOGGER.error("Wrong package URL: " + currentLink.getHref().toString());
					return null;
				}
			}
		}
		return null;
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
	 * Static helper method that creates an Atom {@link Link} object from a given package URL.
	 * 
	 * @param packageURL
	 *        {@link String} - the package URL
	 * @return {@link Link}
	 */
	private static final Link makePackageLink(String packageURL) {
		Link link = Abdera.getInstance().getFactory().newLink();
		link.setHref(packageURL);
		link.setMimeType(CodePackageFeed.PACKAGE_MIMETYPE);
		link.setRel(CodePackageFeed.PACKAGE_LINK_REL);
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
		link.setMimeType(CodePackageFeed.PACKAGE_DESCRIPTION_MIMETYPE);
		link.setRel(CodePackageFeed.PACKAGE_LINK_REL);
		return link;
	}

	/**
	 * Generates a human-readable description from a PackageDescriptionDocument. This description is return as
	 * an Atom content object.
	 * 
	 * @param wpsDesc {@link ProcessDescriptionType}
	 * @return {@link Content}
	 */
	private static Content generateHTMLContent(final ProcessDescriptionType wpsDesc) {
		Content content = Abdera.getInstance().getFactory().newContent(Content.Type.HTML);
		content.setText(WPSDescriptionPrinter.printAsHTML(wpsDesc));

		return content;
	}

}
