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
