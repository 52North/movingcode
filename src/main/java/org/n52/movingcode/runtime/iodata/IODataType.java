/**
 * 
 */
package org.n52.movingcode.runtime.iodata;

import java.util.UnknownFormatConversionException;

import net.opengis.ows.x11.DomainMetadataType;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;


/**
 * Defines types for data exchange throughout the library.
 * 
 * @author Matthias Mueller, TU Dresden
 *
 */
public enum IODataType {
	STRING (String.class),
	INTEGER (Integer.class),
	DOUBLE (Double.class),
	BOOLEAN (Boolean.class),
	MEDIA (MediaData.class);
	
	private final Class<? extends Object> clazz;
	
	IODataType(Class<? extends Object> clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Returns the Java Class associated with a particular IODataType.
	 * @return Class<? extends Object> -  the Java Class
	 */
	public Class<? extends Object> getSupportedClass(){
		return this.clazz;
	}
	
	public static final IODataType findDataType(DomainMetadataType wpsLiteral) throws UnknownFormatConversionException {
		
		// check possible well-known data types
		String datatype = wpsLiteral.getStringValue();

		if (datatype.equalsIgnoreCase("string")) {
			return IODataType.STRING;
		}
		if (datatype.equalsIgnoreCase("boolean")) {
			return IODataType.BOOLEAN;
		}
		if (datatype.equalsIgnoreCase("float")) {
			return IODataType.DOUBLE;
		}
		if (datatype.equalsIgnoreCase("double")) {
			return IODataType.DOUBLE;
		}
		if (datatype.equalsIgnoreCase("int")) {
			return IODataType.INTEGER;
		}
		if (datatype.equalsIgnoreCase("integer")) {
			return IODataType.INTEGER;
		}
		
		else return null;
	}
	
	public static IODataType findType(InputDescriptionType wpsInput){
		// return null for null
		if (wpsInput == null){
			return null;
		}
		
		// assign mimeType and genericDataType
		if (wpsInput.isSetComplexData()){
			return IODataType.MEDIA;
		} else {
			if(wpsInput.isSetBoundingBoxData()){
				//TODO not implemented
			}
			if(wpsInput.isSetLiteralData()){
				return findDataType(wpsInput.getLiteralData().getDataType());
			} else {
				return null; // should not occur, but who knows ...
			}
		}
	}
	
	public static IODataType findType(OutputDescriptionType wpsOutput){
		// return null for null
		if (wpsOutput == null){
			return null;
		}
		
		// assign mimeType and genericDataType
		if (wpsOutput.isSetComplexOutput()){
			return IODataType.MEDIA;
		} else {
			if(wpsOutput.isSetBoundingBoxOutput()){
				//TODO not implemented
			}
			if(wpsOutput.isSetLiteralOutput()){
				return findDataType(wpsOutput.getLiteralOutput().getDataType());
			} else {
				return null; // should not occur, but who knows ...
			}
		}
	}
}