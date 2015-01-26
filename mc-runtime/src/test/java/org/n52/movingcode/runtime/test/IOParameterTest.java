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
package org.n52.movingcode.runtime.test;

import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;

public class IOParameterTest extends MCRuntimeTestConfig {
	@Test
	public void createParameterValues() {

		// Arrange
		IOParameter data = new IOParameter(new IIOParameter.ParameterID(new BigInteger("1")),
				"tinker",
				null,
				null,
				null,
				null,
				true,
				1,
				1,
				IODataType.STRING);

		// Act & Assert
		Assert.assertTrue(data.add("somevalue")); // Strings a safe to add
		Assert.assertFalse(data.add(new Integer(2))); // Integers are not
		Assert.assertFalse(data.add(new Double(3.14))); // Doubles are not
		Assert.assertFalse(data.add(new Boolean(true))); // Booleans are not
		Assert.assertFalse(data.add(new MediaData(null, "application/tiff"))); // Media are not
	}
}
