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
