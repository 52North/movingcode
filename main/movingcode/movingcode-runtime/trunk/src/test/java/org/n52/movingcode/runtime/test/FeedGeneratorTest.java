package org.n52.movingcode.runtime.test;

import org.junit.Test;

import org.n52.movingcode.runtime.feed.FeedGenerator;
import org.n52.movingcode.runtime.feed.FeedTemplate;

public class FeedGeneratorTest extends GlobalTestConfig{
	
	// the source folder where the packages currently reside 
	private static final String repositoryFolder = "src/test/resources/testpackages";
	
	// the target folder where the feed shall be created / updated
	private static final String webFolder = "D:\\atomFeedWeb2";
	private static final String baseURL = "http://gis.geo.tu-dresden.de/gpfeed/";
	private static final String feedFileName = "gpfeed.xml";
	
	private static final String feedTitle = "GeoprocessingAlgorithms";
	private static final String feedSubtitle = "A random collection of processing algorithms for various purposes.";
	private static final String feedAuthor = "Matthias Mueller";
	


	@Test
	public void makeFeed() {
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
