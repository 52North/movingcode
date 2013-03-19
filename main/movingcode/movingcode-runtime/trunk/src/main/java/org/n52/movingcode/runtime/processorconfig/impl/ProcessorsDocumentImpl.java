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
 * An XML document type.
 * Localname: processors
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.ProcessorsDocument
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig.impl;
/**
 * A document containing one processors(@http://52north.org/movingcode/runtime/processorconfig) element.
 *
 * This is a complex type.
 */
public class ProcessorsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.n52.movingcode.runtime.processorconfig.ProcessorsDocument
{
    private static final long serialVersionUID = 1L;
    
    public ProcessorsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PROCESSORS$0 = 
        new javax.xml.namespace.QName("http://52north.org/movingcode/runtime/processorconfig", "processors");
    
    
    /**
     * Gets the "processors" element
     */
    public org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors getProcessors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors target = null;
            target = (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors)get_store().find_element_user(PROCESSORS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "processors" element
     */
    public void setProcessors(org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors processors)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors target = null;
            target = (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors)get_store().find_element_user(PROCESSORS$0, 0);
            if (target == null)
            {
                target = (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors)get_store().add_element_user(PROCESSORS$0);
            }
            target.set(processors);
        }
    }
    
    /**
     * Appends and returns a new empty "processors" element
     */
    public org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors addNewProcessors()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors target = null;
            target = (org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors)get_store().add_element_user(PROCESSORS$0);
            return target;
        }
    }
    /**
     * An XML processors(@http://52north.org/movingcode/runtime/processorconfig).
     *
     * This is a complex type.
     */
    public static class ProcessorsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.n52.movingcode.runtime.processorconfig.ProcessorsDocument.Processors
    {
        private static final long serialVersionUID = 1L;
        
        public ProcessorsImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName PROCESSOR$0 = 
            new javax.xml.namespace.QName("", "processor");
        private static final javax.xml.namespace.QName DEFAULTS$2 = 
            new javax.xml.namespace.QName("", "defaults");
        
        
        /**
         * Gets array of all "processor" elements
         */
        public org.n52.movingcode.runtime.processorconfig.ProcessorType[] getProcessorArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                java.util.List targetList = new java.util.ArrayList();
                get_store().find_all_element_users(PROCESSOR$0, targetList);
                org.n52.movingcode.runtime.processorconfig.ProcessorType[] result = new org.n52.movingcode.runtime.processorconfig.ProcessorType[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        /**
         * Gets ith "processor" element
         */
        public org.n52.movingcode.runtime.processorconfig.ProcessorType getProcessorArray(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.ProcessorType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.ProcessorType)get_store().find_element_user(PROCESSOR$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        /**
         * Returns number of "processor" element
         */
        public int sizeOfProcessorArray()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(PROCESSOR$0);
            }
        }
        
        /**
         * Sets array of all "processor" element
         */
        public void setProcessorArray(org.n52.movingcode.runtime.processorconfig.ProcessorType[] processorArray)
        {
            synchronized (monitor())
            {
                check_orphaned();
                arraySetterHelper(processorArray, PROCESSOR$0);
            }
        }
        
        /**
         * Sets ith "processor" element
         */
        public void setProcessorArray(int i, org.n52.movingcode.runtime.processorconfig.ProcessorType processor)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.ProcessorType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.ProcessorType)get_store().find_element_user(PROCESSOR$0, i);
                if (target == null)
                {
                    throw new IndexOutOfBoundsException();
                }
                target.set(processor);
            }
        }
        
        /**
         * Inserts and returns a new empty value (as xml) as the ith "processor" element
         */
        public org.n52.movingcode.runtime.processorconfig.ProcessorType insertNewProcessor(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.ProcessorType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.ProcessorType)get_store().insert_element_user(PROCESSOR$0, i);
                return target;
            }
        }
        
        /**
         * Appends and returns a new empty value (as xml) as the last "processor" element
         */
        public org.n52.movingcode.runtime.processorconfig.ProcessorType addNewProcessor()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.ProcessorType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.ProcessorType)get_store().add_element_user(PROCESSOR$0);
                return target;
            }
        }
        
        /**
         * Removes the ith "processor" element
         */
        public void removeProcessor(int i)
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(PROCESSOR$0, i);
            }
        }
        
        /**
         * Gets the "defaults" element
         */
        public org.n52.movingcode.runtime.processorconfig.DefaultsType getDefaults()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.DefaultsType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.DefaultsType)get_store().find_element_user(DEFAULTS$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * True if has "defaults" element
         */
        public boolean isSetDefaults()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(DEFAULTS$2) != 0;
            }
        }
        
        /**
         * Sets the "defaults" element
         */
        public void setDefaults(org.n52.movingcode.runtime.processorconfig.DefaultsType defaults)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.DefaultsType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.DefaultsType)get_store().find_element_user(DEFAULTS$2, 0);
                if (target == null)
                {
                    target = (org.n52.movingcode.runtime.processorconfig.DefaultsType)get_store().add_element_user(DEFAULTS$2);
                }
                target.set(defaults);
            }
        }
        
        /**
         * Appends and returns a new empty "defaults" element
         */
        public org.n52.movingcode.runtime.processorconfig.DefaultsType addNewDefaults()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.n52.movingcode.runtime.processorconfig.DefaultsType target = null;
                target = (org.n52.movingcode.runtime.processorconfig.DefaultsType)get_store().add_element_user(DEFAULTS$2);
                return target;
            }
        }
        
        /**
         * Unsets the "defaults" element
         */
        public void unsetDefaults()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(DEFAULTS$2, 0);
            }
        }
    }
}
