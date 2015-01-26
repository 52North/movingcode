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
package org.n52.movingcode.runtime.processors;

import java.io.File;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IOParameterMap;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * An abstract processor that is going to handle MovingCodePackages. It implements the {@link IProcessor}
 * interface and extends an {@link IOParameterMap}.
 * 
 * So to say it is an IOParameterMap that can be executed through an IProcessor facade.
 * 
 * 
 * @author Matthias Mueller
 * 
 */
public abstract class AbstractProcessor extends IOParameterMap implements IProcessor {

	private static final long serialVersionUID = -3617747844919275088L;
	protected final File scratchWorkspace;
	protected final MovingCodePackage mcPackage;
	protected final PropertyMap properties;
	protected final PackageDescriptionDocument packageDescriptionDoc;

	/**
	 * The default and mandatory constructor for all processors in this framework.
	 * 
	 * @param {@link File} scratchworkspace
	 * @param {@link MovingCodePackage} mcp
	 * @param {@link PropertyMap} properties
	 */
	public AbstractProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
		super(mcp);
		this.scratchWorkspace = scratchworkspace;
		this.mcPackage = mcp;
		this.properties = properties;
		this.packageDescriptionDoc = mcp.getDescriptionAsDocument();
	}

}
