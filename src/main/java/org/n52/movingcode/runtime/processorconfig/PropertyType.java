/*
 * XML Type:  propertyType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.PropertyType
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig;


/**
 * An XML propertyType(@http://52north.org/movingcode/runtime/processorconfig).
 *
 * This is a complex type.
 */
public interface PropertyType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PropertyType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("propertytype7664type");
    
    /**
     * Gets the "key" attribute
     */
    java.lang.String getKey();
    
    /**
     * Gets (as xml) the "key" attribute
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType.Key xgetKey();
    
    /**
     * Sets the "key" attribute
     */
    void setKey(java.lang.String key);
    
    /**
     * Sets (as xml) the "key" attribute
     */
    void xsetKey(org.n52.movingcode.runtime.processorconfig.PropertyType.Key key);
    
    /**
     * Gets the "value" attribute
     */
    java.lang.String getValue();
    
    /**
     * Gets (as xml) the "value" attribute
     */
    org.n52.movingcode.runtime.processorconfig.PropertyType.Value xgetValue();
    
    /**
     * Sets the "value" attribute
     */
    void setValue(java.lang.String value);
    
    /**
     * Sets (as xml) the "value" attribute
     */
    void xsetValue(org.n52.movingcode.runtime.processorconfig.PropertyType.Value value);
    
    /**
     * An XML key(@).
     *
     * This is an atomic type that is a restriction of org.n52.movingcode.runtime.processorconfig.PropertyType$Key.
     */
    public interface Key extends org.apache.xmlbeans.XmlString
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Key.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("key41fdattrtype");
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Key newValue(java.lang.Object obj) {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) type.newValue( obj ); }
            
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Key newInstance() {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Key newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * An XML value(@).
     *
     * This is an atomic type that is a restriction of org.n52.movingcode.runtime.processorconfig.PropertyType$Value.
     */
    public interface Value extends org.apache.xmlbeans.XmlString
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Value.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("value632battrtype");
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Value newValue(java.lang.Object obj) {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) type.newValue( obj ); }
            
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Value newInstance() {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static org.n52.movingcode.runtime.processorconfig.PropertyType.Value newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.n52.movingcode.runtime.processorconfig.PropertyType newInstance() {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.PropertyType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.PropertyType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
