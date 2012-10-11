package org.n52.movingcode.runtime.processors;

import java.io.IOException;

/**
 * A common interface for all processors in this framework.
 * Processors can be executed and additionally allow to perform a feasibility check.
 * A successful feasibility check is no guarantee for execution; it is rather meant
 * to prevent some silly mistakes.
 * 
 * @author Matthias Mueller
 *
 */
public interface IProcessor {
	
	/**
	 * Performs a feasibility check for the request.
	 * 
	 * @return <code>true</code> if everything looks sane and ok, <code>false</code> otherwise.
	 */
	public boolean isFeasible();
	
	/**
	 * Executes the processor.
	 * 
	 * @param timeoutSeconds - optional timeout in seconds; 0 means no timeout
	 * @throws {@link IllegalArgumentException} - thrown if the parameters were incorrectly set 
	 * @throws {@link RuntimeException} - thrown if a general error occurred during execution
	 */
	public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException;
}
