package org.n52.movingcode.runtime.test;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;

public class IOParameterTest extends GlobalTestConfig{
	@Test
	public void createParameterValues(){
		
		//Arrange
		IOParameter data = new IOParameter(
				new IIOParameter.ParameterID(new BigInteger("1")),
				"tinker",
				null,
				null,
				null,
				null,
				true,
				1,
				1,
				IODataType.STRING);
		
		//Act & Assert
		Assert.assertTrue(data.add("somevalue")); // Strings a safe to add
		Assert.assertFalse(data.add(new Integer(2))); // Integers are not
		Assert.assertFalse(data.add(new Double(3.14))); // Doubles are not
		Assert.assertFalse(data.add(new Boolean(true))); // Booleans are not
		Assert.assertFalse(data.add(new MediaData(null, "application/tiff"))); // Media are not
	}
}
