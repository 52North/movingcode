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

import org.junit.Test;

import org.n52.movingcode.runtime.feed.FeedGenerator;
import org.n52.movingcode.runtime.feed.FeedTemplate;

public class FeedGeneratorTest extends GlobalTestConfig {

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

    private static final FeedTemplate staticFeedTemplate() {

        FeedTemplate ft = new FeedTemplate(baseURL + feedFileName);
        ft.setFeedTitle(feedTitle);
        ft.setFeedSubtitle(feedSubtitle);
        ft.setFeedAuthorName(feedAuthor);
        ft.setFeedAuthorEmail(null);
        return ft;
    }

}
