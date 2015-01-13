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
