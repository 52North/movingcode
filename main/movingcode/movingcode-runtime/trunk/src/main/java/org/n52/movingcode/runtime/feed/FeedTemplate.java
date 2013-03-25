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

public class FeedTemplate {
    private String feedTitle;
    private String feedSubtitle;
    private String feedAuthorName;
    private String feedAuthorEmail;

    private final String feedURL;

    public FeedTemplate(String feedURL) {
        this.feedURL = feedURL;
    }

    public void setFeedTitle(String feedTitle) {
        this.feedTitle = feedTitle;
    }

    public String getFeedTitle() {
        return this.feedTitle;
    }

    public void setFeedSubtitle(String feedSubtitle) {
        this.feedSubtitle = feedSubtitle;
    }

    public String getFeedSubtitle() {
        return this.feedSubtitle;
    }

    public void setFeedAuthorName(String feedAuthorName) {
        this.feedAuthorName = feedAuthorName;
    }

    public String getFeedAuthorName() {
        return this.feedAuthorName;
    }

    public void setFeedAuthorEmail(String feedAuthorEmail) {
        this.feedAuthorEmail = feedAuthorEmail;
    }

    public String getFeedAuthorEmail() {
        return this.feedAuthorEmail;
    }

    public String getFeedURL() {
        return this.feedURL;
    }

    public String getID() {
        return this.feedURL;
    }

}
