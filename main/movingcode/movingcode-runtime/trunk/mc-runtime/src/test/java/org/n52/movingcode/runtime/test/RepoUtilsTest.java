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

import org.junit.Assert;
import org.junit.Test;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;


public class RepoUtilsTest extends MCRuntimeTestConfig{
	
	private static final String someNastyPID = "C:\\global//repo//ID;http://141.30.100.173/my/package/folder/package.for.some.function.zip";
	
	private static final String normalizedNastyPID = "C/global/repo/ID/http/141.30.100.173/my/package/folder/package.for.some.function";
	
	@Test
	public void packageIdNormalizationTest(){
		logger.trace(someNastyPID);
		String normID = RepositoryUtils.normalizePackageID(someNastyPID);
		logger.trace(normID);
		Assert.assertEquals(normalizedNastyPID, normID);
	}
}
