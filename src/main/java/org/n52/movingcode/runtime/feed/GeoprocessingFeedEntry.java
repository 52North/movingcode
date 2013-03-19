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

import java.util.Date;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Content;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Link;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
public final class GeoprocessingFeedEntry {

    private Entry entry;
    public static final String PACKAGE_MIMETYPE = "application/zip";
    public static final String PACKAGE_DESCRIPTION_MIMETYPE = "text/xml";
    public static final String PACKAGE_LINK_REL = "enclosure";
    public static final String DETAILED_DESCRIPTION_LINK_REL = "alternate";

    public GeoprocessingFeedEntry(Entry entry) {
        this.entry = entry;
    }

    public GeoprocessingFeedEntry(PackageDescriptionDocument packageDesc,
                                  Date creationDate,
                                  String packageURL,
                                  String descriptionURL) {

        if (packageDesc.getPackageDescription().getContractedFunctionality().isSetWpsProcessDescription()) {
            ProcessDescriptionType wpsDesc = packageDesc.getPackageDescription().getContractedFunctionality().getWpsProcessDescription();
            this.entry = makeNewEntry();

            String identifier = wpsDesc.getIdentifier().getStringValue();

            String title = wpsDesc.getTitle().getStringValue();

            // String summary = null;
            // if (wpsDesc.isSetAbstract()){
            // String procAbstract = wpsDesc.getAbstract().getStringValue();
            // summary = "<div><b>" + title + "</b><br/>" + procAbstract + "</div";
            // } else {
            // summary = "<div><b>" + title + "</b><br/>Sorry, the short description is missing.</div";
            // }
            // assert(summary!=null);

            this.entry.setTitle(identifier); // set title
            this.entry.setId(identifier); // set identifier
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

    public String getIdentifier() {
        return entry.getId().toString();
    }

    public Date getPublished() {
        return entry.getPublished();
    }

    public Date getUpdated() {
        return entry.getUpdated();
    }

    public Entry getAtomEntry() {
        return this.entry;
    }

    private static Entry makeNewEntry() {
        return Abdera.getInstance().newEntry();
    }

    public void mergeWith(GeoprocessingFeedEntry otherEntry) {
        // check if other entry is newer
        if (otherEntry.getUpdated().after(this.entry.getUpdated())) {
            // replace with newer entry but keep old PublishedDate
            Date published = this.getPublished();
            this.entry = otherEntry.getAtomEntry();
            this.entry.setPublished(published);
            System.out.println("Updating feed entry for: " + this.getIdentifier());
        }
        // if it is not newer - just keep the old one!
    }

    // private static Content makeContent(String packageURL){
    // Content content = Abdera.getInstance().getFactory().newContent();
    // content.setMimeType(PACKAGE_MIMETYPE);
    // content.setSrc(packageURL);
    // return content;
    // }

    private static final Link makePackageLink(String packageURL) {
        Link link = Abdera.getInstance().getFactory().newLink();
        link.setHref(packageURL);
        link.setMimeType(PACKAGE_MIMETYPE);
        link.setRel(PACKAGE_LINK_REL);
        return link;
    }

    private static Link makePackageDescriptionLink(String descriptionURL) {
        Link link = Abdera.getInstance().getFactory().newLink();
        link.setHref(descriptionURL);
        link.setMimeType(PACKAGE_DESCRIPTION_MIMETYPE);
        link.setRel(PACKAGE_LINK_REL);
        return link;
    }

    /*
     * generates a human-readable description from a PackageDescriptionDocument TODO: implement!
     */
    private static Content generateHTMLContent(final ProcessDescriptionType wpsDesc) {
        Content content = Abdera.getInstance().getFactory().newContent(Content.Type.HTML);
        content.setText(WPSDescriptionPrinter.printAsHTML(wpsDesc));

        return content;
    }

}
