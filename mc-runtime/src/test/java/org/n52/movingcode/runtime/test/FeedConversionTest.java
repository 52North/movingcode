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
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.coderepository.IMovingCodeRepository;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;

public class FeedConversionTest extends GlobalTestConfig {

    Logger logger = Logger.getLogger(FeedConversionTest.class);
    

    @Test
    public void queryTUDFeed() {
    	
        try {
            URL url = new URL(GlobalTestConfig.feedURL);
            IMovingCodeRepository repo = IMovingCodeRepository.Factory.createFromRemoteFeed(url);
            logger.info("Added Repo: " + GlobalTestConfig.feedURL);
            
            File tempDir = new File ("D:\\1111\\repo\\");
            
            RepositoryUtils.materializeAsLocalZipRepo(repo, tempDir);
            

        }
        catch (MalformedURLException e) {
        	Assert.assertFalse(true);
        	e.printStackTrace();
        } catch (IOException e) {
        	Assert.assertFalse(true);
			e.printStackTrace();
		}
    }
}
