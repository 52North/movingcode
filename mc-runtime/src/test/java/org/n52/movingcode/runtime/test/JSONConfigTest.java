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

import java.util.HashMap;

import org.junit.Test;
import org.n52.movingcode.runtime.processors.config.ProcessorConfig;

public class JSONConfigTest {
	
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
	
}
