package org.n52.movingcode.runtime.processors.arctoolbox;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.esri.arcgis.geoprocessing.GeoProcessor;
import com.esri.arcgis.geoprocessing.IGeoProcessor;
import com.esri.arcgis.interop.AutomationException;
import com.esri.arcgis.server.IServerContext;
import com.esri.arcgis.server.IServerObjectManager;
import com.esri.arcgis.server.ServerConnection;
import com.esri.arcgis.system.IVariantArray;
import com.esri.arcgis.system.ServerInitializer;
import com.esri.arcgis.system.VarArray;

public class ArcServerController{
	private static Logger logger = Logger.getLogger(ArcServerController.class);
	
	protected static final boolean executeGPTool(String toolName, String toolboxPath, String[] parameters, String domain, String user, String pass, String ip, String arcObjectsJAR) {
		
		if (System.getProperty("JINTEGRA_NATIVE_MODE")==null) {
			logger.info("Running geoprocessor in DCOM Mode");
		} else {
			logger.info("Running geoprocessor in Native Mode");
		} 
		
		try{
			//get a connection to the server
			logger.info("Getting AGS connection object ...");
			ServerConnection connection = getAGSConnection(domain, user, pass, ip);
			// Get reference to ServerObjectManager
			IServerObjectManager som = connection.getServerObjectManager();
			IServerContext context = som.createServerContext("", "");
			logger.info("ServerContext created!");
			
			//create GP object on server
			IGeoProcessor gp = (GeoProcessor) (context.createObject(GeoProcessor.getClsid()));
			logger.info("GeoProcessor initialized!");
			
			//create the parameter object
			IVariantArray fParams = (VarArray)(context.createObject(VarArray.getClsid()));
			
			for (int i = 0; i < parameters.length; i++)
				fParams.add(parameters[i]);
			
			//load the toolbox
			//null: internal toolbox - skip loading
			if (toolboxPath != null){
				gp.addToolbox(toolboxPath);
				logger.info("Added: " + toolboxPath);
			}
			
			//execute GP tool
			logger.info("Executing GPTool ...");
			gp.execute(toolName, fParams, null);
			
			//free ags resources
			context.releaseContext();
			logger.info("done!");
			return true;
		}
		catch (AutomationException ae){
			logger.error("Caught J-Integra AutomationException: " + ae.getMessage() + "\n");
			logger.error(ae.getDescription());
			ae.printStackTrace();
			return false;
		}
			
		catch (IOException e){
			logger.error("Caught IOException: " + e.getMessage() + "\n");
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	private static final ServerConnection getAGSConnection(String domain, String user, String pass, String ip) throws IOException{
		
		// Initialize server
		logger.info("initializing server ...");
		ServerInitializer serverInitializer = new ServerInitializer();
		serverInitializer.initializeServer(domain, user, pass);
		ServerConnection connection = null;
		try {
			//connect
			connection = new ServerConnection();
			connection.connect(ip);
			logger.info("server initialized!");

		} catch (UnknownHostException e) {
			logger.error("UnknownHostException - Could not connect to AGS host " + domain + " with user " + user);
			logger.info("Please check connection parameters and firewall setup!");
			e.printStackTrace();
			throw new IOException("Error connecting to ArcGIS Server.");
		} catch (IOException e) {
			logger.error("IOException - Could not connect to AGS host " + domain + " with user " + user);
			logger.info("Please check firewall setup! - and maybe the folder permissions, too");
			e.printStackTrace();
			throw new IOException("Error connecting to ArcGIS Server.");
		}

		return connection;
	}
	
}
