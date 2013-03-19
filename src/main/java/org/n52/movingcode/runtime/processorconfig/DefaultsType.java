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
/**
 * XML Type:  defaultsType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.DefaultsType
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig;


/**
 * An XML defaultsType(@http://52north.org/movingcode/runtime/processorconfig).
 *
 * This is a complex type.
 */
public interface DefaultsType extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(DefaultsType.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("defaultstype84a1type");
    
    /**
     * Gets the "tempWorkspace" element
     */
    java.lang.String getTempWorkspace();
    
    /**
     * Gets (as xml) the "tempWorkspace" element
     */
    org.apache.xmlbeans.XmlString xgetTempWorkspace();
    
    /**
     * Sets the "tempWorkspace" element
     */
    void setTempWorkspace(java.lang.String tempWorkspace);
    
    /**
     * Sets (as xml) the "tempWorkspace" element
     */
    void xsetTempWorkspace(org.apache.xmlbeans.XmlString tempWorkspace);
    
    /**
     * Gets array of all "availablePlatform" elements
     */
    java.lang.String[] getAvailablePlatformArray();
    
    /**
     * Gets ith "availablePlatform" element
     */
    java.lang.String getAvailablePlatformArray(int i);
    
    /**
     * Gets (as xml) array of all "availablePlatform" elements
     */
    org.apache.xmlbeans.XmlAnyURI[] xgetAvailablePlatformArray();
    
    /**
     * Gets (as xml) ith "availablePlatform" element
     */
    org.apache.xmlbeans.XmlAnyURI xgetAvailablePlatformArray(int i);
    
    /**
     * Returns number of "availablePlatform" element
     */
    int sizeOfAvailablePlatformArray();
    
    /**
     * Sets array of all "availablePlatform" element
     */
    void setAvailablePlatformArray(java.lang.String[] availablePlatformArray);
    
    /**
     * Sets ith "availablePlatform" element
     */
    void setAvailablePlatformArray(int i, java.lang.String availablePlatform);
    
    /**
     * Sets (as xml) array of all "availablePlatform" element
     */
    void xsetAvailablePlatformArray(org.apache.xmlbeans.XmlAnyURI[] availablePlatformArray);
    
    /**
     * Sets (as xml) ith "availablePlatform" element
     */
    void xsetAvailablePlatformArray(int i, org.apache.xmlbeans.XmlAnyURI availablePlatform);
    
    /**
     * Inserts the value as the ith "availablePlatform" element
     */
    void insertAvailablePlatform(int i, java.lang.String availablePlatform);
    
    /**
     * Appends the value as the last "availablePlatform" element
     */
    void addAvailablePlatform(java.lang.String availablePlatform);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "availablePlatform" element
     */
    org.apache.xmlbeans.XmlAnyURI insertNewAvailablePlatform(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "availablePlatform" element
     */
    org.apache.xmlbeans.XmlAnyURI addNewAvailablePlatform();
    
    /**
     * Removes the ith "availablePlatform" element
     */
    void removeAvailablePlatform(int i);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType newInstance() {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.DefaultsType parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (org.n52.movingcode.runtime.processorconfig.DefaultsType) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
