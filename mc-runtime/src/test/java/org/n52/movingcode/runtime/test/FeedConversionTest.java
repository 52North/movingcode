package org.n52.movingcode.runtime.test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.processors.AUID;

public class FeedConversionTest extends MCRuntimeTestConfig {


	@Test
	public void queryTUDFeed() {

		try {
			URL url = new URL(MCRuntimeTestConfig.feedURL);
			MovingCodeRepository repo = MovingCodeRepository.Factory.createFromRemoteFeed(url);
			LOGGER.info("Added Repo: " + MCRuntimeTestConfig.feedURL);
			
			File tmpDir = new File(FileUtils.getTempDirectory(), AUID.randomAUID());
			tmpDir.mkdir();
			tmpDir.deleteOnExit();
			
			LOGGER.info("Using temp dir " + tmpDir.getAbsolutePath());

			// TODO: reactivate dump testing
//			RepositoryUtils.materializeAsLocalZipRepo(repo, tmpDir);

		}
		catch (MalformedURLException e) {
			Assert.fail();
			e.printStackTrace();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			Assert.fail();
		}
	}
}
