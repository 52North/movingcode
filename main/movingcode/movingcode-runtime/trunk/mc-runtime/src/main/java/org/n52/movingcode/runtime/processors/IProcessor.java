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
