package org.n52.movingcode.runtime.iodata;

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
	STRING(String.class), INTEGER(Integer.class), DOUBLE(Double.class), BOOLEAN(Boolean.class), MEDIA(MediaData.class);

	private final Class< ? extends Object> clazz;

	IODataType(Class< ? extends Object> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Returns the Java Class associated with a particular IODataType.
	 * Currently performs educated guesses rather than performing a lookup
	 * in XML type systems.
	 * 
	 * @return Class<? extends Object> - the Java Class
	 */
	public Class< ? extends Object> getSupportedClass() {
		return this.clazz;
	}
	
	/**
	 * Private method that guesses the internal data type from a ows:domainMetadataType
	 * 
	 * @param wpsLiteral {@link DomainMetadataType}
	 * @return {@link IODataType} - the internal data type, i.e. {string|boolean|float|double|int|integer}
	 */
	private static final IODataType findDataType(DomainMetadataType wpsLiteral) {

		// 1. try it with well-known data types in the OGC type system
		String datatype = wpsLiteral.getStringValue();

		// 2. try it with external type systems
		// TODO: might need more intelligence for educated data
		// type guesses
		if (datatype == null || datatype.trim().isEmpty()) {
			if (wpsLiteral.isSetReference()) {

				// Current procedure works well for xml schema data types
				// of the form [ns]:[string|boolean|float|double|int|integer]
				datatype = wpsLiteral.getReference().trim();
				String[] split = datatype.split(":");
				if (split != null && split.length > 1) {
					datatype = split[split.length-1];
				}
			}
		}
		
		// make sure we are not running into NPE in the following comparison
		if (datatype == null)
		{
			return null;
		}
		// return internal data type based on the type guess
		else
		{
			
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
		}

		return null;
	}

	public static IODataType findType(InputDescriptionType wpsInput) {
		// return null for null
		if (wpsInput == null) {
			return null;
		}

		// assign mimeType and genericDataType
		if (wpsInput.isSetComplexData()) {
			return IODataType.MEDIA;
		}

		if (wpsInput.isSetBoundingBoxData()) {
			// TODO not implemented
		}
		if (wpsInput.isSetLiteralData()) {
			return findDataType(wpsInput.getLiteralData().getDataType());
		}
		return null; // should not occur, but who knows ...
	}

	public static IODataType findType(OutputDescriptionType wpsOutput) {
		// return null for null
		if (wpsOutput == null) {
			return null;
		}

		// assign mimeType and genericDataType
		if (wpsOutput.isSetComplexOutput()) {
			return IODataType.MEDIA;
		}
		if (wpsOutput.isSetBoundingBoxOutput()) {
			// TODO not implemented
		}
		if (wpsOutput.isSetLiteralOutput()) {
			return findDataType(wpsOutput.getLiteralOutput().getDataType());
		}
		return null; // should not occur, but who knows ...
	}
}
