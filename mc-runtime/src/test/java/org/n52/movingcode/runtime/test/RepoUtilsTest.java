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
