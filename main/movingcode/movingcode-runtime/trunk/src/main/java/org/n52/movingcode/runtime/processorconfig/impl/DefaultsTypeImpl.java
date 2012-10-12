/*
 * XML Type:  defaultsType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.DefaultsType
 *
 * Automatically generated - do not modify.
 */
package org.n52.movingcode.runtime.processorconfig.impl;
/**
 * An XML defaultsType(@http://52north.org/movingcode/runtime/processorconfig).
 *
 * This is a complex type.
 */
public class DefaultsTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements org.n52.movingcode.runtime.processorconfig.DefaultsType
{
    private static final long serialVersionUID = 1L;
    
    public DefaultsTypeImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName TEMPWORKSPACE$0 = 
        new javax.xml.namespace.QName("", "tempWorkspace");
    private static final javax.xml.namespace.QName AVAILABLEPLATFORM$2 = 
        new javax.xml.namespace.QName("", "availablePlatform");
    
    
    /**
     * Gets the "tempWorkspace" element
     */
    public java.lang.String getTempWorkspace()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TEMPWORKSPACE$0, 0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TEMPWORKSPACE$0, 0);
            return target;
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
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(TEMPWORKSPACE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(TEMPWORKSPACE$0);
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
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(TEMPWORKSPACE$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(TEMPWORKSPACE$0);
            }
            target.set(tempWorkspace);
        }
    }
    
    /**
     * Gets array of all "availablePlatform" elements
     */
    public java.lang.String[] getAvailablePlatformArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(AVAILABLEPLATFORM$2, targetList);
            java.lang.String[] result = new java.lang.String[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getStringValue();
            return result;
        }
    }
    
    /**
     * Gets ith "availablePlatform" element
     */
    public java.lang.String getAvailablePlatformArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AVAILABLEPLATFORM$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) array of all "availablePlatform" elements
     */
    public org.apache.xmlbeans.XmlAnyURI[] xgetAvailablePlatformArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(AVAILABLEPLATFORM$2, targetList);
            org.apache.xmlbeans.XmlAnyURI[] result = new org.apache.xmlbeans.XmlAnyURI[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "availablePlatform" element
     */
    public org.apache.xmlbeans.XmlAnyURI xgetAvailablePlatformArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().find_element_user(AVAILABLEPLATFORM$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return (org.apache.xmlbeans.XmlAnyURI)target;
        }
    }
    
    /**
     * Returns number of "availablePlatform" element
     */
    public int sizeOfAvailablePlatformArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AVAILABLEPLATFORM$2);
        }
    }
    
    /**
     * Sets array of all "availablePlatform" element
     */
    public void setAvailablePlatformArray(java.lang.String[] availablePlatformArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(availablePlatformArray, AVAILABLEPLATFORM$2);
        }
    }
    
    /**
     * Sets ith "availablePlatform" element
     */
    public void setAvailablePlatformArray(int i, java.lang.String availablePlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(AVAILABLEPLATFORM$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setStringValue(availablePlatform);
        }
    }
    
    /**
     * Sets (as xml) array of all "availablePlatform" element
     */
    public void xsetAvailablePlatformArray(org.apache.xmlbeans.XmlAnyURI[]availablePlatformArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(availablePlatformArray, AVAILABLEPLATFORM$2);
        }
    }
    
    /**
     * Sets (as xml) ith "availablePlatform" element
     */
    public void xsetAvailablePlatformArray(int i, org.apache.xmlbeans.XmlAnyURI availablePlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().find_element_user(AVAILABLEPLATFORM$2, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(availablePlatform);
        }
    }
    
    /**
     * Inserts the value as the ith "availablePlatform" element
     */
    public void insertAvailablePlatform(int i, java.lang.String availablePlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(AVAILABLEPLATFORM$2, i);
            target.setStringValue(availablePlatform);
        }
    }
    
    /**
     * Appends the value as the last "availablePlatform" element
     */
    public void addAvailablePlatform(java.lang.String availablePlatform)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(AVAILABLEPLATFORM$2);
            target.setStringValue(availablePlatform);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "availablePlatform" element
     */
    public org.apache.xmlbeans.XmlAnyURI insertNewAvailablePlatform(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().insert_element_user(AVAILABLEPLATFORM$2, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "availablePlatform" element
     */
    public org.apache.xmlbeans.XmlAnyURI addNewAvailablePlatform()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlAnyURI target = null;
            target = (org.apache.xmlbeans.XmlAnyURI)get_store().add_element_user(AVAILABLEPLATFORM$2);
            return target;
        }
    }
    
    /**
     * Removes the ith "availablePlatform" element
     */
    public void removeAvailablePlatform(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AVAILABLEPLATFORM$2, i);
        }
    }
}
