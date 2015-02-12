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
package org.n52.movingcode.feed;

import java.io.File;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.feed.FeedTemplate;
import org.n52.movingcode.runtime.feed.CodePackageFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;

/**
 * This class contains static utility methods that are used to create and update AtomFeeds for MC
 * 
 * 
 * @author Matthias Mueller, TU Dresden
 * 
 * TODO: move or refactor
 *
 */
public class FeedUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedUtils.class);
	
	/**
	 * Static method to update an AtomFeed file in a zipped feed directory. (Supports nested folders etc.)
	 * The AtomFeed file must reside in the root of the <code>feedFolderDirectory</code> and must be
	 * named {@value CodePackageFeed#atomFeedFileName}. If this file is not present it will be created. In this case,
	 * it will surely lack some mandatory information such as title, author, etc. In this case the logger
	 * will print a warning and you must manually fix the file. 
	 * 
	 * @param feedFolderDirectory {@link File} - a folder containing the zipped packages in a nested
	 *        structure as well as the AtomFeed XML file, named {@value CodePackageFeed#atomFeedFileName} 
	 */
	public static final CodePackageFeed makeFeed(final MovingCodeRepository repo, final FeedTemplate feedTemplate, final String webRoot){
		
		CodePackageFeed feed = new CodePackageFeed(feedTemplate);
		
		ImmutableSet<MovingCodePackage> latestPackages = repo.getLatestPackages();
		
		for (MovingCodePackage mcp : latestPackages){
			feed.addEntry(mcp, webRoot);
		}
		
		return feed;
	}
	

}
