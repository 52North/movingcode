package org.n52.movingcode.runtime.feed;

public class FeedTemplate {
	private String feedTitle;
	private String feedSubtitle;
	private String feedAuthorName;
	private String feedAuthorEmail;
	
	private final String feedURL;
	
	public FeedTemplate(String feedURL){
		this.feedURL = feedURL;
	}

	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	public String getFeedTitle() {
		return feedTitle;
	}

	public void setFeedSubtitle(String feedSubtitle) {
		this.feedSubtitle = feedSubtitle;
	}

	public String getFeedSubtitle() {
		return feedSubtitle;
	}

	public void setFeedAuthorName(String feedAuthorName) {
		this.feedAuthorName = feedAuthorName;
	}

	public String getFeedAuthorName() {
		return feedAuthorName;
	}

	public void setFeedAuthorEmail(String feedAuthorEmail) {
		this.feedAuthorEmail = feedAuthorEmail;
	}

	public String getFeedAuthorEmail() {
		return feedAuthorEmail;
	}

	public String getFeedURL() {
		return feedURL;
	}
	
	public String getID(){
		return feedURL;
	}
	
}
