/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
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
import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.LocalZipPackageRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryChangeListener;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;
import org.n52.movingcode.runtime.processors.AUID;

public class SilentFolderUpdateTest extends MCRuntimeTestConfig{
	
	protected boolean updateReceived;

	@Test
	public void shouldReceiveNotificationOnPackageDrop() throws URISyntaxException, IOException, InterruptedException {
		File tmpDir = FileUtils.getTempDirectory();
		File tmpDropIn = new File(tmpDir, AUID.randomAUID());
		tmpDropIn.mkdir();
		tmpDropIn.deleteOnExit();
		
		URL testPackage = getClass().getResource("/testpackages/py_copy.zip");
		File testFile = new File(testPackage.toURI());
		File targetFile = new File(tmpDropIn, testFile.getName());
		
		// get us a normalized packageID from the pathname
		final String pID = RepositoryUtils.generateNormalizedIDFromFile(targetFile);
	
		IMovingCodeRepository repo = new LocalZipPackageRepository(tmpDropIn);
		repo.addRepositoryChangeListener(new RepositoryChangeListener() {
			@Override
			public void onRepositoryUpdate(IMovingCodeRepository updatedRepo) {
				logger.info("Received update on Repo "+updatedRepo);
				synchronized (SilentFolderUpdateTest.this) {
					logger.info("Repos packages: "+ Arrays.toString(updatedRepo.getPackageIDs()));
					if (updatedRepo.containsPackage(pID)) {
						logger.info("Expected package included in repo: " + pID);
						updateReceived = true;
					}
				}
			}
		});
		
		logger.info("Copying "+ testFile.getAbsolutePath() +" to "+tmpDropIn.getAbsolutePath());
		FileUtils.copyFileToDirectory(testFile, tmpDropIn);
		
		Thread.sleep(IMovingCodeRepository.localPollingInterval*2);
		
		synchronized (this) {
			Assert.assertTrue("Package update not received!", updateReceived);
		}
	}
	
}
