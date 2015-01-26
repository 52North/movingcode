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
