package org.n52.movingcode.runtime.test;

import org.junit.Test;
import org.n52.movingcode.runtime.coderepository.RepositoryUtils;


public class RepoUtilsTest extends MCRuntimeTestConfig{
	
	private static final String someNastyPID = "C:\\global//repo//ID;http://141.30.100.173/my/package/folder/package.for.some.function.zip";
	
	private static final String normalizedNastyPID = "C/global/repo/ID/http/141.30.100.173/my/package/folder/package.for.some.function";
	
	@Test
	public void packageIdNormalizationTest(){
		logger.info(someNastyPID);
		String normID = RepositoryUtils.normalizePackageID(someNastyPID);
		logger.info(normID);
	}
}
