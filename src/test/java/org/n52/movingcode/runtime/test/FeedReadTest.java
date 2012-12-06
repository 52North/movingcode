package org.n52.movingcode.runtime.test;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import org.n52.movingcode.runtime.RepositoryManager;


public class FeedReadTest extends GlobalTestConfig{
	
	private RepositoryManager rm = RepositoryManager.getInstance();
	
	@Test
	public void readTUDFeed() {
		
		try {
			URL url = new URL(GlobalTestConfig.feedURL);
			rm.addRepository(url);
			System.out.println("Added Repo: " + GlobalTestConfig.feedURL);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
