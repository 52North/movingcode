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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.LocalZipPackageRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryChangeListener;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;
import org.n52.movingcode.runtime.processors.AUID;

public class SilentFolderUpdateTest extends MCRuntimeTestConfig{
	
//	protected boolean updateReceived;
//
//	@Test
//	public void shouldReceiveNotificationOnPackageDrop() throws URISyntaxException, IOException, InterruptedException {
//		File tmpDir = FileUtils.getTempDirectory();
//		File tmpDropIn = new File(tmpDir, AUID.randomAUID());
//		tmpDropIn.mkdir();
//		tmpDropIn.deleteOnExit();
//		
//		URL testPackage = getClass().getResource("/testpackages/py_copy.zip");
//		File testFile = new File(testPackage.toURI());
//		File targetFile = new File(tmpDropIn, testFile.getName());
//		
//		// get us a normalized packageID from the pathname
//		final String pID = RepositoryUtils.generateNormalizedIDFromFile(targetFile);
//	
//		IMovingCodeRepository repo = new LocalZipPackageRepository(tmpDropIn);
//		repo.addRepositoryChangeListener(new RepositoryChangeListener() {
//			@Override
//			public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
//				logger.info("Received update on Repo "+updatedRepo);
//				synchronized (SilentFolderUpdateTest.this) {
//					logger.info("Repos packages: "+ Arrays.toString(updatedRepo.getPackageIDs()));
//					if (updatedRepo.containsPackage(pID)) {
//						logger.info("Expected package included in repo: " + pID);
//						updateReceived = true;
//					}
//				}
//			}
//		});
//		
//		logger.info("Copying "+ testFile.getAbsolutePath() +" to "+tmpDropIn.getAbsolutePath());
//		FileUtils.copyFileToDirectory(testFile, tmpDropIn);
//		
//		Thread.sleep(IMovingCodeRepository.localPollingInterval*2);
//		
//		synchronized (this) {
//			Assert.assertTrue("Package update not received!", updateReceived);
//		}
//	}
	
}
