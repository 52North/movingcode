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
 * XML Type:  processorType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.ProcessorType
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig.impl;
/**
 * An XML processorType(@http://52north.org/movingcode/runtime/processorconfig).
 *
 * This is a complex type.
 */
public class ProcessorTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.n52.movingcode.runtime.processorconfig.ProcessorType
{
    private static final long serialVersionUID = 1L;
    
    public ProcessorTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUPPORTEDCONTAINER$0 = 
        new javax.xml.namespace.QName("", "supportedContainer");
    private static final javax.xml.namespace.QName TEMPWORKSPACE$2 = 
        new javax.xml.namespace.QName("", "tempWorkspace");
    private static final javax.xml.namespace.QName PROPERTY$4 = 
        new javax.xml.namespace.QName("", "property");
    private static final javax.xml.namespace.QName ID$6 = 
        new javax.xml.namespace.QName("", "id");
    
    
    /**
     * Gets array of all "supportedContainer" elements
     */
    public java.lang.String[] getSupportedContainerArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(SUPPORTEDCONTAINER$0, targetList);
            java.lang.String[] result = new java.lang.String[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
            return result;
        }
    }
    
    /**
     * Gets ith "supportedContainer" element
     */
    public java.lang.String getSupportedContainerArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUPPORTEDCONTAINER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) array of all "supportedContainer" elements
     */
    public org.apache.xmlbeans.XmlAnyURI[] xgetSupportedContainerArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(SUPPORTEDCONTAINER$0, targetList);
            org.apache.xmlbeans.XmlAnyURI[] result = new org.apache.xmlbeans.XmlAnyURI[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "supportedContainer" element
     */
    public org.apache.xmlbeans.XmlAnyURI xgetSupportedContainerArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().find_element_user(SUPPORTEDCONTAINER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return (org.apache.xmlbeans.XmlAnyURI)target;
        }
    }
    
    /**
     * Returns number of "supportedContainer" element
     */
    public int sizeOfSupportedContainerArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(SUPPORTEDCONTAINER$0);
        }
    }
    
    /**
     * Sets array of all "supportedContainer" element
     */
    public void setSupportedContainerArray(java.lang.String[] supportedContainerArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(supportedContainerArray, SUPPORTEDCONTAINER$0);
        }
    }
    
    /**
     * Sets ith "supportedContainer" element
     */
    public void setSupportedContainerArray(int i, java.lang.String supportedContainer)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUPPORTEDCONTAINER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(supportedContainer);
        }
    }
    
    /**
     * Sets (as xml) array of all "supportedContainer" element
     */
    public void xsetSupportedContainerArray(org.apache.xmlbeans.XmlAnyURI[]supportedContainerArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(supportedContainerArray, SUPPORTEDCONTAINER$0);
        }
    }
    
    /**
     * Sets (as xml) ith "supportedContainer" element
     */
    public void xsetSupportedContainerArray(int i, org.apache.xmlbeans.XmlAnyURI supportedContainer)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().find_element_user(SUPPORTEDCONTAINER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(supportedContainer);
        }
    }
    
    /**
     * Inserts the value as the ith "supportedContainer" element
     */
    public void insertSupportedContainer(int i, java.lang.String supportedContainer)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(SUPPORTEDCONTAINER$0, i);
            target.setStringValue(supportedContainer);
        }
    }
    
    /**
     * Appends the value as the last "supportedContainer" element
     */
    public void addSupportedContainer(java.lang.String supportedContainer)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUPPORTEDCONTAINER$0);
            target.setStringValue(supportedContainer);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "supportedContainer" element
     */
    public org.apache.xmlbeans.XmlAnyURI insertNewSupportedContainer(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().insert_element_user(SUPPORTEDCONTAINER$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "supportedContainer" element
     */
    public org.apache.xmlbeans.XmlAnyURI addNewSupportedContainer()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().add_element_user(SUPPORTEDCONTAINER$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "supportedContainer" element
     */
    public void removeSupportedContainer(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(SUPPORTEDCONTAINER$0, i);
        }
    }
    
    /**
     * Gets the "tempWorkspace" element
     */
    public java.lang.String getTempWorkspace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TEMPWORKSPACE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "tempWorkspace" element
     */
    public org.apache.xmlbeans.XmlString xgetTempWorkspace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TEMPWORKSPACE$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "tempWorkspace" element
     */
    public boolean isSetTempWorkspace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(TEMPWORKSPACE$2) != 0;
        }
    }
    
    /**
     * Sets the "tempWorkspace" element
     */
    public void setTempWorkspace(java.lang.String tempWorkspace)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TEMPWORKSPACE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TEMPWORKSPACE$2);
            }
            target.setStringValue(tempWorkspace);
        }
    }
    
    /**
     * Sets (as xml) the "tempWorkspace" element
     */
    public void xsetTempWorkspace(org.apache.xmlbeans.XmlString tempWorkspace)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TEMPWORKSPACE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TEMPWORKSPACE$2);
            }
            target.set(tempWorkspace);
        }
    }
    
    /**
     * Unsets the "tempWorkspace" element
     */
    public void unsetTempWorkspace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(TEMPWORKSPACE$2, 0);
        }
    }
    
    /**
     * Gets array of all "property" elements
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType[] getPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(PROPERTY$4, targetList);
            org.n52.movingcode.runtime.processorconfig.PropertyType[] result = new org.n52.movingcode.runtime.processorconfig.PropertyType[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "property" element
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType getPropertyArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType)get_store().find_element_user(PROPERTY$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "property" element
     */
    public int sizeOfPropertyArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(PROPERTY$4);
        }
    }
    
    /**
     * Sets array of all "property" element
     */
    public void setPropertyArray(org.n52.movingcode.runtime.processorconfig.PropertyType[] propertyArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(propertyArray, PROPERTY$4);
        }
    }
    
    /**
     * Sets ith "property" element
     */
    public void setPropertyArray(int i, org.n52.movingcode.runtime.processorconfig.PropertyType property)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType)get_store().find_element_user(PROPERTY$4, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(property);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "property" element
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType insertNewProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType)get_store().insert_element_user(PROPERTY$4, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "property" element
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType addNewProperty()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType)get_store().add_element_user(PROPERTY$4);
            return target;
        }
    }
    
    /**
     * Removes the ith "property" element
     */
    public void removeProperty(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(PROPERTY$4, i);
        }
    }
    
    /**
     * Gets the "id" attribute
     */
    public java.lang.String getId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$6);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "id" attribute
     */
    public org.apache.xmlbeans.XmlString xgetId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$6);
            return target;
        }
    }
    
    /**
     * Sets the "id" attribute
     */
    public void setId(java.lang.String id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_attribute_user(ID$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_attribute_user(ID$6);
            }
            target.setStringValue(id);
        }
    }
    
    /**
     * Sets (as xml) the "id" attribute
     */
    public void xsetId(org.apache.xmlbeans.XmlString id)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_attribute_user(ID$6);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_attribute_user(ID$6);
            }
            target.set(id);
        }
    }
}
