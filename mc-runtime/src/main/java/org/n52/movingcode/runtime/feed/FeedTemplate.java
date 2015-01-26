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
