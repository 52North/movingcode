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

/**
 * 
 * @author Matthias Mueller, TU Dresden
 */
public class FeedTemplate {
	
	private String title;
	private String subtitle;
	private String authorName;
	private String authorEmail;

	private String feedURL;
	
	private FeedTemplate(){}
	
	public FeedTemplate(String feedURL) {
		this.feedURL = feedURL;
	}

	public String getFeedTitle() {
		return this.title;
	}

	public String getFeedSubtitle() {
		return this.subtitle;
	}

	public String getFeedAuthorName() {
		return this.authorName;
	}

	public String getFeedAuthorEmail() {
		return this.authorEmail;
	}

	public String getFeedURL() {
		return this.feedURL;
	}
	
	
	public static class Builder{
		private final FeedTemplate builder;
		
		public Builder(){
			builder = new FeedTemplate();
		}
		
		public Builder feedUrl(String url){
			this.builder.feedURL = url;
			return this;
		}
		
		public Builder feedTitle(String title){
			this.builder.title = title;
			return this;
		}
		
		public Builder feedSubtitle(String subtitle){
			this.builder.subtitle = subtitle;
			return this;
		}
		
		public Builder feedAuthorName(String authorName){
			this.builder.authorName = authorName;
			return this;
		}
		
		public Builder feedAuthorEmail(String authorEmail){
			this.builder.authorEmail = authorEmail;
			return this;
		}
		
		public FeedTemplate build(){
			return builder;
		}
	}
	
}
