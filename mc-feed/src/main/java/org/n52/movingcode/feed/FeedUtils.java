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
package org.n52.movingcode.feed;

import java.io.File;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.MovingCodeRepository;
import org.n52.movingcode.runtime.feed.FeedTemplate;
import org.n52.movingcode.runtime.feed.CodePackageFeed;

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
	
	static Logger logger = Logger.getLogger(FeedUtils.class);
	
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
