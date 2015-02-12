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
package org.n52.movingcode.runtime.processors.r.util;

import java.text.DateFormat;
import java.util.Date;

import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(RLogger.class);
    
    private static DateFormat format = DateFormat.getDateTimeInstance();

    public static void logGenericRProcess(RConnection rCon, String message) {
        String msg = prepareMessage(message);
        
        StringBuilder evalString = new StringBuilder();
        evalString.append("cat(\"[GenericRProcess @ ");
        evalString.append(format.format(new Date(System.currentTimeMillis())));
        evalString.append("] ");
        evalString.append(msg);
        evalString.append("\\n\")");
        
        try {
            rCon.eval(evalString.toString());
        }
        catch (RserveException e) {
            LOGGER.warn("Could not log message '" + msg + "'", e);
        }
    }

    public static void log(RConnection rCon, String message) {
        String msg = prepareMessage(message);
        
        StringBuilder evalString = new StringBuilder();
        evalString.append("cat(\"[WPS4R @ ");
        evalString.append(format.format(new Date(System.currentTimeMillis())));
        evalString.append("] ");
        evalString.append(msg);
        evalString.append("\\n\")");
        
        try {
            rCon.eval(evalString.toString());
        }
        catch (RserveException e) {
            LOGGER.warn("Could not log message '" + msg + "'", e);
        }
    }

    private static String prepareMessage(String message) {
        // return message.replace("\"", "\\\"");
        return new String(message);
    }

}
