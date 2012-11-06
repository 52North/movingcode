package org.n52.movingcode.runtime.test;

import org.apache.log4j.BasicConfigurator;
import org.junit.Test;

import org.n52.movingcode.runtime.feed.FeedGenerator;
import org.n52.movingcode.runtime.feed.FeedTemplate;

public class FeedGeneratorTest {
	
	// the source folder where the packages currently reside 
	private static final String repositoryFolder = "src/test/resources/testpackages";
	
	// the target folder where the feed shall be created / updated
	private static final String webFolder = "D:\\atomFeedWeb2";
	private static final String baseURL = "http://gis.geo.tu-dresden.de/gpfeed/";
	private static final String feedFileName = "gpfeed.xml";
	
	private static final String feedTitle = "FeedTitle";
	private static final String feedSubtitle = "FeedSubTitle";
	private static final String feedAuthor = "Matthias Müller";
	
	@Test
	public void makeFeed() {
		BasicConfigurator.configure();
		FeedGenerator.generate(feedFileName, webFolder, baseURL, repositoryFolder, staticFeedTemplate());
	}
	
	
	private static final FeedTemplate staticFeedTemplate(){
		FeedTemplate ft = new FeedTemplate(baseURL + feedFileName);
		ft.setFeedTitle(feedTitle);
		ft.setFeedSubtitle(feedSubtitle);
		ft.setFeedAuthorName(feedAuthor);
		ft.setFeedAuthorEmail(null);
		return ft;
	}
	
}
