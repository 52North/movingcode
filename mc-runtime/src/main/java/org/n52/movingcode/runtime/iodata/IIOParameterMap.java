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

package org.n52.movingcode.runtime.iodata;

import java.util.SortedMap;

import org.n52.movingcode.runtime.iodata.IIOParameter.ParameterID;

public interface IIOParameterMap extends SortedMap<ParameterID, IOParameter> {

    /**
     * Method for adding data to this map using the ParameterID. (The {@link ParameterID} is the *internal* ID
     * of the parameter)
     * 
     * 
     * @param identifier
     * @param data
     * @return <code>true</code> if adding was successful, <code>false</code> otherwise
     */
    public boolean addData(ParameterID identifier, Object data);

    /**
     * Method for removing data from this map using the ParameterID. (The {@link ParameterID} is the
     * *internal* ID of the parameter)
     * 
     * 
     * @param identifier
     * @param data
     * @return <code>true</code> if remove was successful, <code>false</code> otherwise
     */
    public boolean removeData(ParameterID identifier, Object data);

    /**
     * Method for adding data to this map using the message ID. (The message ID public ID of the parameter
     * from the functional description)
     * 
     * 
     * @param {@link String} identifier
     * @param data
     * @return <code>true</code> if adding was successful, <code>false</code> otherwise
     */
    public boolean addData(String messageID, Object data);

    /**
     * Method for removing data from this map using the message ID. (The message ID public ID of the parameter
     * from the functional description)
     * 
     * 
     * @param {@link String} identifier
     * @param data
     * @return <code>true</code> if remove was successful, <code>false</code> otherwise
     */
    public boolean removeData(String messageID, Object data);

}
