package org.n52.movingcode.runtime;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.movingcode.runtime.processorconfig.ProcessorsDocument;

/**
 * Config Object for Code Processors.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public class ProcessorConfig implements Serializable {

	private static final long serialVersionUID = 3112343084611936675L;
	private static transient ProcessorConfig instance;
	private static transient ProcessorsDocument procConfigXMLBeans;
	private static final ProcessorsDocument blankConfig = ProcessorsDocument.Factory.newInstance();
	
	protected final PropertyChangeSupport propertyChangeSupport;
	public static final String PROCESSORCONFIG_UPDATE_EVENT_NAME = "ProcessorConfigUpdate";


	private static transient Logger logger = Logger.getLogger(ProcessorConfig.class);

	// private constructor for a blank config
	private ProcessorConfig(ProcessorsDocument blankConfig){
		procConfigXMLBeans = blankConfig;
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public static final ProcessorConfig EMPTY_RUNTIME_CONFIG = new ProcessorConfig(blankConfig);

	public boolean setConfig (File processorConfigXML) {
		synchronized (procConfigXMLBeans) {
			try {
				procConfigXMLBeans = ProcessorsDocument.Factory.parse(processorConfigXML);
				return true;
			} catch (XmlException e) {
				logger.error("Reading new processor configuration failed! " + e.getMessage());
				return false;
			} catch (IOException e) {
				logger.error("Reading new processor configuration failed! " + e.getMessage());
				return false;
			}
		}
		
	}

	public boolean setConfig (InputStream processorConfigXMLStream){
		synchronized (procConfigXMLBeans) {
			try {
				procConfigXMLBeans = ProcessorsDocument.Factory.parse(processorConfigXMLStream);
				return true;
			} catch (XmlException e) {
				logger.error("Reading new processor configuration failed! " + e.getMessage());
				return false;
			} catch (IOException e) {
				logger.error("Reading new processor configuration failed! " + e.getMessage());
				return false;
			}
		}
		
	}

	
	/**
	 * Add an Listener to the processorConfig
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * remove a listener from the wpsConfig
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	// For Testing purpose only
	public void notifyListeners() {
		propertyChangeSupport.firePropertyChange(PROCESSORCONFIG_UPDATE_EVENT_NAME, null, null);
	}

	/**
	 * 
	 * @return ProcessorConfig object
	 */
	public static synchronized ProcessorConfig getInstance() {
		
		if (instance == null){
			instance = new ProcessorConfig(blankConfig);
		}
		
		return instance;
	}
	
	public ProcessorsDocument getConfig(){
		return procConfigXMLBeans;
	}
	
	private synchronized void writeObject(java.io.ObjectOutputStream oos) throws IOException {
		oos.writeObject(procConfigXMLBeans.xmlText());
	}

	private synchronized void readObject(java.io.ObjectInputStream oos) throws IOException, ClassNotFoundException {
		try {
			String processorConfigXMLBeansAsXml = (String) oos.readObject();
			XmlObject configXmlObject = XmlObject.Factory.parse(processorConfigXMLBeansAsXml);
			ProcessorsDocument configurationDocument = ProcessorsDocument.Factory.newInstance();
			configurationDocument.addNewProcessors().set(configXmlObject);
			this.setConfig(new ByteArrayInputStream(configurationDocument.xmlText().getBytes()));
		}
		catch (XmlException e) {
			logger.error(e.getMessage());
			throw new IOException(e.getMessage());
		}
	}

}
