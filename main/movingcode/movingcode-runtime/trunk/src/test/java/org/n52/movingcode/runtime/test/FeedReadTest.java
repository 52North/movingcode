package org.n52.movingcode.runtime.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import org.n52.movingcode.runtime.RepositoryManager;


public class FeedReadTest {
	
	static String feedURL = "http://141.30.100.178/gpfeed/gpfeed.xml";
	private RepositoryManager rm = RepositoryManager.getInstance();
	
	@Test
	public void readTUDFeed() {
		try {
			URL url = new URL(feedURL);
			rm.addRepository(url);
			System.out.println("Added Repo: " + feedURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
