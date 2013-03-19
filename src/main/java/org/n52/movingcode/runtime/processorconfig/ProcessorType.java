/*
 * XML Type:  processorType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.ProcessorType
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig;


/**
 * An XML processorType(@http://52north.org/movingcode/runtime/processorconfig).
 *
 * This is a complex type.
 */
public interface ProcessorType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ProcessorType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("processortype7baftype");
    
    /**
     * Gets array of all "supportedContainer" elements
     */
    java.lang.String[] getSupportedContainerArray();
    
    /**
     * Gets ith "supportedContainer" element
     */
    java.lang.String getSupportedContainerArray(int i);
    
    /**
     * Gets (as xml) array of all "supportedContainer" elements
     */
    org.apache.xmlbeans.XmlAnyURI[] xgetSupportedContainerArray();
    
    /**
     * Gets (as xml) ith "supportedContainer" element
     */
    org.apache.xmlbeans.XmlAnyURI xgetSupportedContainerArray(int i);
    
    /**
     * Returns number of "supportedContainer" element
     */
    int sizeOfSupportedContainerArray();
    
    /**
     * Sets array of all "supportedContainer" element
     */
    void setSupportedContainerArray(java.lang.String[] supportedContainerArray);
    
    /**
     * Sets ith "supportedContainer" element
     */
    void setSupportedContainerArray(int i, java.lang.String supportedContainer);
    
    /**
     * Sets (as xml) array of all "supportedContainer" element
     */
    void xsetSupportedContainerArray(org.apache.xmlbeans.XmlAnyURI[] supportedContainerArray);
    
    /**
     * Sets (as xml) ith "supportedContainer" element
     */
    void xsetSupportedContainerArray(int i, org.apache.xmlbeans.XmlAnyURI supportedContainer);
    
    /**
     * Inserts the value as the ith "supportedContainer" element
     */
    void insertSupportedContainer(int i, java.lang.String supportedContainer);
    
    /**
     * Appends the value as the last "supportedContainer" element
     */
    void addSupportedContainer(java.lang.String supportedContainer);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "supportedContainer" element
     */
    org.apache.xmlbeans.XmlAnyURI insertNewSupportedContainer(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "supportedContainer" element
     */
    org.apache.xmlbeans.XmlAnyURI addNewSupportedContainer();
    
    /**
     * Removes the ith "supportedContainer" element
     */
    void removeSupportedContainer(int i);
    
    /**
     * Gets the "tempWorkspace" element
     */
    java.lang.String getTempWorkspace();
    
    /**
     * Gets (as xml) the "tempWorkspace" element
     */
    org.apache.xmlbeans.XmlString xgetTempWorkspace();
    
    /**
     * True if has "tempWorkspace" element
     */
    boolean isSetTempWorkspace();
    
    /**
     * Sets the "tempWorkspace" element
     */
    void setTempWorkspace(java.lang.String tempWorkspace);
    
    /**
     * Sets (as xml) the "tempWorkspace" element
     */
    void xsetTempWorkspace(org.apache.xmlbeans.XmlString tempWorkspace);
    
    /**
     * Unsets the "tempWorkspace" element
     */
    void unsetTempWorkspace();
    
    /**
     * Gets array of all "property" elements
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType[] getPropertyArray();
    
    /**
     * Gets ith "property" element
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType getPropertyArray(int i);
    
    /**
     * Returns number of "property" element
     */
    int sizeOfPropertyArray();
    
    /**
     * Sets array of all "property" element
     */
    void setPropertyArray(org.n52.movingcode.runtime.processorconfig.PropertyType[] propertyArray);
    
    /**
     * Sets ith "property" element
     */
    void setPropertyArray(int i, org.n52.movingcode.runtime.processorconfig.PropertyType property);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "property" element
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType insertNewProperty(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "property" element
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType addNewProperty();
    
    /**
     * Removes the ith "property" element
     */
    void removeProperty(int i);
    
    /**
     * Gets the "id" attribute
     */
    java.lang.String getId();
    
    /**
     * Gets (as xml) the "id" attribute
     */
    org.apache.xmlbeans.XmlString xgetId();
    
    /**
     * Sets the "id" attribute
     */
    void setId(java.lang.String id);
    
    /**
     * Sets (as xml) the "id" attribute
     */
    void xsetId(org.apache.xmlbeans.XmlString id);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType newInstance() {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.ProcessorType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
