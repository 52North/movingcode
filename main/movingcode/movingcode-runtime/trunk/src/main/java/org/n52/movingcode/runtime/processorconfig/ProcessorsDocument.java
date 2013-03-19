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
/*
 * An XML document type.
 * Localname: processors
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.ProcessorsDocument
 *
 * Automatically generated - do not modify.
 */

package org.n52.movingcode.runtime.processorconfig;

/**
 * A document containing one processors(@http://52north.org/movingcode/runtime/processorconfig) element.
 * 
 * This is a complex type.
 */
public interface ProcessorsDocument extends org.apache.xmlbeans.XmlObject {
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType) org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ProcessorsDocument.class.getClassLoader(),
                                                                                                                                                     "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("processorsf8a6doctype");

    /**
     * Gets the "processors" element
     */
    org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors getProcessors();

    /**
     * Sets the "processors" element
     */
    void setProcessors(org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors processors);

    /**
     * Appends and returns a new empty "processors" element
     */
    org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors addNewProcessors();

    /**
     * An XML processors(@http://52north.org/movingcode/runtime/processorconfig).
     * 
     * This is a complex type.
     */
    public interface Processors extends org.apache.xmlbeans.XmlObject {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType) org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Processors.class.getClassLoader(),
                                                                                                                                                         "schemaorg_apache_xmlbeans.system.s5CA9CC80AA7DF65F32E34E65F955B070").resolveHandle("processorsdac9elemtype");

        /**
         * Gets array of all "processor" elements
         */
        org.n52.movingcode.runtime.processorconfig.ProcessorType[] getProcessorArray();

        /**
         * Gets ith "processor" element
         */
        org.n52.movingcode.runtime.processorconfig.ProcessorType getProcessorArray(int i);

        /**
         * Returns number of "processor" element
         */
        int sizeOfProcessorArray();

        /**
         * Sets array of all "processor" element
         */
        void setProcessorArray(org.n52.movingcode.runtime.processorconfig.ProcessorType[] processorArray);

        /**
         * Sets ith "processor" element
         */
        void setProcessorArray(int i, org.n52.movingcode.runtime.processorconfig.ProcessorType processor);

        /**
         * Inserts and returns a new empty value (as xml) as the ith "processor" element
         */
        org.n52.movingcode.runtime.processorconfig.ProcessorType insertNewProcessor(int i);

        /**
         * Appends and returns a new empty value (as xml) as the last "processor" element
         */
        org.n52.movingcode.runtime.processorconfig.ProcessorType addNewProcessor();

        /**
         * Removes the ith "processor" element
         */
        void removeProcessor(int i);

        /**
         * Gets the "defaults" element
         */
        org.n52.movingcode.runtime.processorconfig.DefaultsType getDefaults();

        /**
         * True if has "defaults" element
         */
        boolean isSetDefaults();

        /**
         * Sets the "defaults" element
         */
        void setDefaults(org.n52.movingcode.runtime.processorconfig.DefaultsType defaults);

        /**
         * Appends and returns a new empty "defaults" element
         */
        org.n52.movingcode.runtime.processorconfig.DefaultsType addNewDefaults();

        /**
         * Unsets the "defaults" element
         */
        void unsetDefaults();

        /**
         * A factory class with static methods for creating instances of this type.
         */

        public static final class Factory {
            public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors newInstance() {
                return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance(type,
                                                                                                                                                                  null);
            }

            public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors newInstance(org.apache.xmlbeans.XmlOptions options) {
                return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance(type,
                                                                                                                                                                  options);
            }

            private Factory() {
            } // No instance of this class allowed
        }
    }

    /**
     * A factory class with static methods for creating instances of this type.
     */

    public static final class Factory {
        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument newInstance() {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance(type,
                                                                                                                                                   null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance(type,
                                                                                                                                                   options);
        }

        /**
         * @param xmlAsString
         *        the string value to parse
         */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(xmlAsString,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.lang.String xmlAsString,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(xmlAsString,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        /**
         * @param file
         *        the file from which to load an xml document
         */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(file,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.File file,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(file,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(u,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.net.URL u,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(u,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(is,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.InputStream is,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(is,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(r,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(java.io.Reader r,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                java.io.IOException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(r,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(sr,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(javax.xml.stream.XMLStreamReader sr,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(sr,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(node,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(org.w3c.dom.Node node,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(node,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException,
                org.apache.xmlbeans.xml.stream.XMLStreamException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(xis,
                                                                                                                                             type,
                                                                                                                                             null);
        }

        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.n52.movingcode.runtime.processorconfig.ProcessorsDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis,
                                                                                          org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                org.apache.xmlbeans.xml.stream.XMLStreamException {
            return (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse(xis,
                                                                                                                                             type,
                                                                                                                                             options);
        }

        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException,
                org.apache.xmlbeans.xml.stream.XMLStreamException {
            return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, type, null);
        }

        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis,
                                                                                                org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException,
                org.apache.xmlbeans.xml.stream.XMLStreamException {
            return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream(xis, type, options);
        }

        private Factory() {
        } // No instance of this class allowed
    }
}
