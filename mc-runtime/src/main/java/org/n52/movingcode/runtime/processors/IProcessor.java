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

import java.io.IOException;

/**
 * A common interface for all processors in this framework. Processors can be executed and additionally allow
 * to perform a feasibility check. A successful feasibility check is no guarantee for execution; it is rather
 * meant to prevent some silly mistakes.
 * 
 * @author Matthias Mueller
 * 
 */
public interface IProcessor {

	public static final String randomTempDirToken = "$TEMP$";

	/**
	 * Performs a feasibility check for the request.
	 * 
	 * @return <code>true</code> if everything looks sane and ok, <code>false</code> otherwise.
	 */
	public boolean isFeasible();

	/**
	 * Executes the processor.
	 * 
	 * @param timeoutSeconds
	 *        - optional timeout in seconds; 0 means no timeout
	 * @throws {@link IllegalArgumentException} - thrown if the parameters were incorrectly set
	 * @throws {@link RuntimeException} - thrown if a general error occurred during execution
	 */
	public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException;
}
