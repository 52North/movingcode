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
