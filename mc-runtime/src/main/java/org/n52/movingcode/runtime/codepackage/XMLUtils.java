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
package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public class XMLUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);
	
	public static final PackageDescriptionDocument fromFile(final File descriptionXMLFile){
		PackageDescriptionDocument doc = null;
		try {
			doc = PackageDescriptionDocument.Factory.parse(descriptionXMLFile);
		} catch (XmlException e) {
			LOGGER.error("PackageDescription could not be read. " + e.getMessage());
		} catch (IOException e) {
			LOGGER.error("PackageDescription could not be read." + e.getMessage());
		}
		
		return doc;
	}
	
	public static final PackageDescriptionDocument fromString(String xmlString){
		PackageDescriptionDocument doc = null;
		try {
			doc = PackageDescriptionDocument.Factory.parse(xmlString);
		} catch (XmlException e) {
			LOGGER.error("PackageDescription could not be read. " + e.getMessage());
		}
		
		return doc;
	}
	
	public static final String toString(PackageDescriptionDocument doc){
		StringWriter sw = new StringWriter();
		
		try {
			synchronized (doc) {
				doc.save(sw);
				sw.flush();
			}
		} catch (IOException e) {
			LOGGER.error("PackageDescription could not be converted to String. " + e.getMessage());
		}
		return sw.toString();
	}
	
}
