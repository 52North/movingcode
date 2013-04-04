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
 * 
 */

package org.n52.movingcode.runtime.iodata;

import java.util.ArrayList;
import java.util.Collection;

import de.tudresden.gis.geoprocessing.movingcode.schema.ExecutionParameterType;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

/**
 * @author Matthias Mueller
 * 
 */
public class IOParameter extends ArrayList implements IIOParameter {

    private static final long serialVersionUID = 1958099022514293035L;
    private final ParameterID identifier;
    private final String messageInputID;
    private final String messageOutputID;
    private final String prefix;
    private final String suffix;
    private final String separator;
    private final boolean mandatoryForExecution;
    private final int minMultiplicity;
    private final int maxMultiplicity;
    private final IODataType supportedType;

    public IOParameter(ParameterID identifier,
                       String messageInputID,
                       String messageOutputID,
                       String prefix,
                       String suffix,
                       String separator,
                       boolean mandatoryForExecution,
                       int minMultiplicity,
                       int maxMultiplicity,
                       IODataType supportedType) {

        super();
        this.identifier = identifier;
        this.messageInputID = messageInputID;
        this.messageOutputID = messageOutputID;
        this.prefix = prefix;
        this.suffix = suffix;
        this.separator = separator;
        this.mandatoryForExecution = mandatoryForExecution;
        this.minMultiplicity = minMultiplicity;
        this.maxMultiplicity = maxMultiplicity;
        this.supportedType = supportedType;
    }

    public IOParameter(final ExecutionParameterType exParam,
                       final InputDescriptionType wpsInput,
                       final OutputDescriptionType wpsOutput) {

        super();

        // 1. set identifier
        if (exParam.isSetPositionID()) {
            this.identifier = new ParameterID(exParam.getPositionID());
        }
        else {
            if (exParam.isSetStringID()) {
                this.identifier = new ParameterID(exParam.getStringID());
            }
            else {
                throw new IllegalArgumentException("Legacy Identifier missing.");
            }
        }

        // 2. set message InputID (without verification)
        if (exParam.isSetFunctionalInputID()) {
            this.messageInputID = exParam.getFunctionalInputID();
        }
        else {
            this.messageInputID = null;
        }

        // 3. set message OutputID (without verification)
        if (exParam.isSetFunctionalOutputID()) {
            this.messageOutputID = exParam.getFunctionalOutputID();
        }
        else {
            this.messageOutputID = null;
        }

        // 4. set prefix, suffix, separator
        if (exParam.isSetPrefixString()) {
            this.prefix = exParam.getPrefixString();
        }
        else
            this.prefix = "";
        if (exParam.isSetSuffixString()) {
            this.suffix = exParam.getSuffixString();
        }
        else
            this.suffix = "";
        if (exParam.isSetSeparatorString()) {
            this.separator = exParam.getSeparatorString();
        }
        else
            this.separator = "";

        // 5. set min/max Multiplicity
        // and 6. supported type
        if (wpsInput != null) {
            this.minMultiplicity = wpsInput.getMinOccurs().intValue();
            this.maxMultiplicity = wpsInput.getMaxOccurs().intValue();
            this.supportedType = IODataType.findType(wpsInput);
        }
        else {
            if (wpsOutput != null) {
                this.minMultiplicity = 0;
                this.maxMultiplicity = 1;
                this.supportedType = IODataType.findType(wpsOutput);
            }
            else {
                // should not occur
                this.minMultiplicity = 0;
                this.maxMultiplicity = 0;
                this.supportedType = null;
            }
        }

        // 7. set mandatoryExecution
        this.mandatoryForExecution = !exParam.isSetOptional();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getType()
     */
    public IODataType getType() {
        return this.supportedType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tudresden.gis.geoprocessing.movingcode.iodata.IData#supportsType(de.tudresden.gis.geoprocessing.
     * movingcode.iodata.IODataType)
     */
    public boolean supportsType(IODataType type) {
        return this.supportedType == type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getMinMultiplicity()
     */
    public int getMinMultiplicity() {
        return this.minMultiplicity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getMaxMultiplicity()
     */
    public int getMaxMultiplicity() {
        return this.maxMultiplicity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getIdentifier()
     */
    public ParameterID getIdentifier() {
        return this.identifier;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getMessageInputIdentifier()
     */
    public String getMessageInputIdentifier() {
        return this.messageInputID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#getMessageOutputIdentifier()
     */
    public String getMessageOutputIdentifier() {
        return this.messageOutputID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#isMessageIn()
     */
    public boolean isMessageIn() {
        return this.messageInputID != null && !this.messageInputID.equalsIgnoreCase("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#isMessageOut()
     */
    public boolean isMessageOut() {
        return this.messageOutputID != null && !this.messageOutputID.equalsIgnoreCase("");
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#isMandatoryMessage()
     */
    public boolean isMandatoryMessage() {
        return this.minMultiplicity > 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#isMandatoryForExecution()
     */
    public boolean isMandatoryForExecution() {
        return this.mandatoryForExecution;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#isSequential()
     */
    public boolean isSequential() {
        return this.identifier.getType() == ParameterID.IDType.SEQUENTIAL;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#printPrefix(de.tudresden.gis.geoprocessing.
     * movingcode.iodata.IODataID)
     */
    public String printPrefix() {
        if (this.prefix != null) {
            return this.prefix;
        }
        else
            return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IData#printSuffix(de.tudresden.gis.geoprocessing.
     * movingcode.iodata.IODataID)
     */
    public String printSuffix() {
        if (this.suffix != null) {
            return this.suffix;
        }
        else
            return "";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.tudresden.gis.geoprocessing.movingcode.iodata.IData#printSeparator(de.tudresden.gis.geoprocessing
     * .movingcode.iodata.IODataID)
     */
    public String printSeparator() {
        if (this.separator != null) {
            return this.separator;
        }
        else
            return "";
    }

    public boolean isValidData() {
        // case A: is mandatory IO and data NOT set
        if (isMandatoryMessage() && isEmpty()) {
            return false;
        }

        // check if all object are of the right type
        // TODO: we compare classes, thus subclasses are not allowed
        for (Object item : this) {
            if (item.getClass() != getType().getSupportedClass()) {
                return false;
            }
        }

        // check if multiplicity matches the given constraints
        return (this.size() >= this.minMultiplicity && this.size() <= this.maxMultiplicity);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IOParameter#getDirection()
     */
    public Direction getDirection() {
        if (isMessageIn()) {
            if (isMessageOut()) {
                return Direction.BOTH;
            }
            else {
                return Direction.IN;
            }
        }
        else {
            if (isMessageOut()) {
                return Direction.OUT;
            }
            else {
                return Direction.NONE; // should not occur, but who knows ...
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IOParameter#isMessageInputID(java.lang.String)
     */
    public boolean isMessageInputID(String messageInputID) {
        return this.messageInputID.equalsIgnoreCase(messageInputID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.tudresden.gis.geoprocessing.movingcode.iodata.IOParameter#isMessageOutputID(java.lang.String)
     */
    public boolean isMessageOutputID(String messageOutputID) {
        return this.messageOutputID.equalsIgnoreCase(messageOutputID);
    }

    @Override
    public boolean add(Object e) {
        if (this.supportsValue(e)) {
            return super.add(e);
        }
        return false;
    }

    @Override
    public void add(int index, Object element) {
        if (this.supportsValue(element)) {
            super.add(index, element);
        }
        else
            throw new IllegalArgumentException("Adding object of class " + element.getClass().getCanonicalName()
                    + " is not allowed.\nParameter type was defined as "
                    + this.supportedType.getSupportedClass().getCanonicalName());
    }

    @Override
    public boolean addAll(Collection c) {
        boolean supported = true;
        for (Object o : c) {
            supported = supported && supportsValue(o);
        }

        if (supported) {
            return super.addAll(c);
        }
        else
            return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
        boolean supported = true;
        for (Object o : c) {
            supported = supported && supportsValue(o);
        }
        if (supported) {
            return super.addAll(index, c);
        }
        else
            return false;
    }

    public boolean supportsValue(Object value) {
        return value.getClass() == this.supportedType.getSupportedClass();
    }

}
