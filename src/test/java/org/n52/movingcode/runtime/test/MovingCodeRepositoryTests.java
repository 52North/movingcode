package org.n52.movingcode.runtime.test;

import java.io.File;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.MovingCodeRepository;
import org.n52.movingcode.runtime.RepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.IOParameterMap;
import org.n52.movingcode.runtime.processors.ProcessorFactory;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public class MovingCodeRepositoryTests extends GlobalTestConfig{
	
	private static final String zTransformID = "de.tu-dresden.geo.gis.algorithms.raster.ztransform";
	private static final String packageFolderName = "src/test/resources/testpackages";
	
	private static final String workspace = packageFolderName + File.separator + "ztransform/ztransform";
	private static final String descriptionXML = packageFolderName + File.separator + "ztransform/packagedescription.xml";
	
	private static final String tempFolder = "C:\\tmp";
	
	
	@Test
	public void testDirectoryRepository (){
		
		// Arrange
		File packageFolder = new File(packageFolderName);
		System.out.println(packageFolder.getAbsolutePath());

		// Act
		MovingCodeRepository mcRep = new MovingCodeRepository(packageFolder);

		// Assert
		Assert.assertTrue(mcRep.containsPackage(zTransformID));
		System.out.println("Information for package: " + zTransformID);
		System.out.println("Package Timestamp is: " + mcRep.getPackageTimestamp(zTransformID));
		
		
		MovingCodePackage pack = mcRep.getPackage(zTransformID); //get the test package
		Assert.assertFalse(pack == null); // make sure it is not null
		
		IOParameterMap paramsMap = ProcessorFactory.getInstance().newProcessor(pack); //get an empty parameter Map
		Assert.assertFalse(paramsMap == null); // make sure it is not null
		
		System.out.println("--- Parameters ---");
		for (IOParameter param : paramsMap.values()){
			System.out.println("Parameter " + param.getIdentifier().getHarmonizedValue() + 
					": " + param.getMinMultiplicity()
					+ ".." + param.getMaxMultiplicity());
			if (param.isMessageIn()){
				System.out.println("ServiceInputID: " + param.getMessageInputIdentifier());
			}
			if (param.isMessageOut()){
				System.out.println("ServiceOutputID: " + param.getMessageOutputIdentifier());
			}
			
			System.out.println("Internal Type: " + param.getType().toString());
			
		}
	}
	
	@Test
	public void testRepoManager(){
		
		// Arrange
		File packageFolder = new File(packageFolderName);
		System.out.println(packageFolder.getAbsolutePath());
		
		// Act
		RepositoryManager repoMan = RepositoryManager.getInstance();
		repoMan.addRepository(packageFolderName);
		
		// Assert
		Assert.assertTrue(repoMan.providesFunction(zTransformID));
	}
	
	@Test
	public void testPackageZipping() throws Exception{
		
		// Arrange
		File wsFolder = new File(workspace);
		PackageDescriptionDocument doc = PackageDescriptionDocument.Factory.parse(new File(descriptionXML)); 
		
		// Act
		// create a new zipped package
		File tempFile = new File(tempFolder + File.separator + UUID.randomUUID().toString() + ".zip");
		String packageIdentifier = tempFile.getPath();
		System.out.println(packageIdentifier);
		
		MovingCodePackage mcp = new MovingCodePackage(wsFolder, doc, null, packageIdentifier);
		
		mcp.dumpPackage(tempFile);
		// close package and reopen
		mcp = null;
		mcp = new MovingCodePackage(tempFile, packageIdentifier);
		
		// Assert
		Assert.assertTrue(mcp.isValid());
	}
}
