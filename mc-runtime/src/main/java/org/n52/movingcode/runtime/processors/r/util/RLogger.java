package org.n52.movingcode.runtime.processors.r.util;

import java.text.DateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

public class RLogger {

    private static Logger LOGGER = Logger.getLogger(RLogger.class);
    
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
