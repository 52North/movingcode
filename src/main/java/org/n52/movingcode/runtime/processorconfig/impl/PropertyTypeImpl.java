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
 * XML Type:  propertyType
 * Namespace: http://52north.org/movingcode/runtime/processorconfig
 * Java type: org.n52.movingcode.runtime.processorconfig.PropertyType
 *
 * Automatically generated - do not modify.
 */

package org.n52.movingcode.runtime.processorconfig.impl;

/**
 * An XML propertyType(@http://52north.org/movingcode/runtime/processorconfig).
 * 
 * This is a complex type.
 */
public class PropertyTypeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements
        org.n52.movingcode.runtime.processorconfig.PropertyType {
    private static final long serialVersionUID = 1L;

    public PropertyTypeImpl(org.apache.xmlbeans.SchemaType sType) {
        super(sType);
    }

    private static final javax.xml.namespace.QName KEY$0 = new javax.xml.namespace.QName("", "key");
    private static final javax.xml.namespace.QName VALUE$2 = new javax.xml.namespace.QName("", "value");

    /**
     * Gets the "key" attribute
     */
    public java.lang.String getKey() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(KEY$0);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "key" attribute
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType.Key xgetKey() {
        synchronized (monitor()) {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType.Key target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) get_store().find_attribute_user(KEY$0);
            return target;
        }
    }

    /**
     * Sets the "key" attribute
     */
    public void setKey(java.lang.String key) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(KEY$0);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue) get_store().add_attribute_user(KEY$0);
            }
            target.setStringValue(key);
        }
    }

    /**
     * Sets (as xml) the "key" attribute
     */
    public void xsetKey(org.n52.movingcode.runtime.processorconfig.PropertyType.Key key) {
        synchronized (monitor()) {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType.Key target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) get_store().find_attribute_user(KEY$0);
            if (target == null) {
                target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Key) get_store().add_attribute_user(KEY$0);
            }
            target.set(key);
        }
    }

    /**
     * Gets the "value" attribute
     */
    public java.lang.String getValue() {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(VALUE$2);
            if (target == null) {
                return null;
            }
            return target.getStringValue();
        }
    }

    /**
     * Gets (as xml) the "value" attribute
     */
    public org.n52.movingcode.runtime.processorconfig.PropertyType.Value xgetValue() {
        synchronized (monitor()) {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType.Value target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) get_store().find_attribute_user(VALUE$2);
            return target;
        }
    }

    /**
     * Sets the "value" attribute
     */
    public void setValue(java.lang.String value) {
        synchronized (monitor()) {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue) get_store().find_attribute_user(VALUE$2);
            if (target == null) {
                target = (org.apache.xmlbeans.SimpleValue) get_store().add_attribute_user(VALUE$2);
            }
            target.setStringValue(value);
        }
    }

    /**
     * Sets (as xml) the "value" attribute
     */
    public void xsetValue(org.n52.movingcode.runtime.processorconfig.PropertyType.Value value) {
        synchronized (monitor()) {
            check_orphaned();
            org.n52.movingcode.runtime.processorconfig.PropertyType.Value target = null;
            target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) get_store().find_attribute_user(VALUE$2);
            if (target == null) {
                target = (org.n52.movingcode.runtime.processorconfig.PropertyType.Value) get_store().add_attribute_user(VALUE$2);
            }
            target.set(value);
        }
    }

    /**
     * An XML key(@).
     * 
     * This is an atomic type that is a restriction of
     * org.n52.movingcode.runtime.processorconfig.PropertyType$Key.
     */
    public static class KeyImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements
            org.n52.movingcode.runtime.processorconfig.PropertyType.Key {
        private static final long serialVersionUID = 1L;

        public KeyImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected KeyImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }

    /**
     * An XML value(@).
     * 
     * This is an atomic type that is a restriction of
     * org.n52.movingcode.runtime.processorconfig.PropertyType$Value.
     */
    public static class ValueImpl extends org.apache.xmlbeans.impl.values.JavaStringHolderEx implements
            org.n52.movingcode.runtime.processorconfig.PropertyType.Value {
        private static final long serialVersionUID = 1L;

        public ValueImpl(org.apache.xmlbeans.SchemaType sType) {
            super(sType, false);
        }

        protected ValueImpl(org.apache.xmlbeans.SchemaType sType, boolean b) {
            super(sType, b);
        }
    }
}
