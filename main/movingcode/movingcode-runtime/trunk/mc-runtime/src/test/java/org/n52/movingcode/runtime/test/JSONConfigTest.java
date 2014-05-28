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

import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Test;
import org.n52.movingcode.runtime.processors.config.ProcessorConfig;

public class JSONConfigTest {
	
	public JSONConfigTest() {
		JSONConfigTest.setup();
	}
	
	@Test
	public void testJSONConfig(){
		String[] pList = ProcessorConfig.getRegisteredProcessorIDs();
		for (String id : pList){
			System.out.println(id);
			for (String container : ProcessorConfig.getContaines(id)){
				System.out.println("	" + container);
			}
			HashMap<String, String> props = ProcessorConfig.getProperties(id);
			for(String key : props.keySet()){
				System.out.println("	" + key + "  -  " + props.get(key));
			}
		}
		
		System.out.println("\nDefault Settings:");
		System.out.println(ProcessorConfig.getDefaultWorkspace());
		for (String platform : ProcessorConfig.getDefaultPlatforms()){
			System.out.println("	" + platform);
		}
		
	}
	
	private static void configureLogger(){
		// Logger stuff
		BasicConfigurator.configure();
		LogManager.getRootLogger().setLevel(Level.INFO);
	}
	
	static void setup(){
		configureLogger();
	}
}
