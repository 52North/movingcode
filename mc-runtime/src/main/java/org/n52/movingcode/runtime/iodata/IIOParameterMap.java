/**
 * Copyright (C) 2012 52°North Initiative for Geospatial Open Source Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.n52.movingcode.runtime.iodata;

import java.util.SortedMap;

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
