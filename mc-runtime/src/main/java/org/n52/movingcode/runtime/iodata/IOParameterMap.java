package org.n52.movingcode.runtime.iodata;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.iodata.IIOParameter.ParameterID;

import de.tudresden.gis.geoprocessing.movingcode.schema.ExecutionParameterType;
import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

public class IOParameterMap extends TreeMap<ParameterID, IOParameter> implements IIOParameterMap {

    private static final long serialVersionUID = -6265228017198887781L;
    private boolean isWPSdescription = false;
    private final Map<String, ParameterID> messageIDparamID_lookup;

    // private Map<String, ParameterID> = new Multimap

    public IOParameterMap(final MovingCodePackage mcp) {
        if ( !mcp.isValid()) {
            throw new IllegalArgumentException("Package did not validate!");
        }

        // retrieve functional description types
        // TODO: rework resolution mechanism for WPS 1.0 cs 2.0
        this.isWPSdescription = true;
        PackageDescriptionDocument pd = mcp.getDescriptionAsDocument();

        // create input index
        Map<String, InputDescriptionType> inputs = new HashMap<String, InputDescriptionType>();
        for (InputDescriptionType input : pd.getPackageDescription().getFunctionality().getWps100ProcessDescription().getDataInputs().getInputArray()) {
            inputs.put(input.getIdentifier().getStringValue(), input);
        }

        // create output index
        Map<String, OutputDescriptionType> outputs = new HashMap<String, OutputDescriptionType>();
        for (OutputDescriptionType output : pd.getPackageDescription().getFunctionality().getWps100ProcessDescription().getProcessOutputs().getOutputArray()) {
            outputs.put(output.getIdentifier().getStringValue(), output);
        }

        // initialize lookup
        this.messageIDparamID_lookup = new HashMap<String, ParameterID>();

        if (this.isWPSdescription) {
            for (ExecutionParameterType param : pd.getPackageDescription().getWorkspace().getExecutionParameters().getParameterArray()) {
                IOParameter exItem = new IOParameter(param,
                                                     inputs.get(param.isSetFunctionalInputID() ? param.getFunctionalInputID()
                                                                                              : null),
                                                     outputs.get(param.isSetFunctionalOutputID() ? param.getFunctionalOutputID()
                                                                                                : null));

                // add item to this map
                this.put(exItem.getIdentifier(), exItem);

                // create a lookup entry
                if (exItem.getMessageInputIdentifier() != null) {
                    this.messageIDparamID_lookup.put(exItem.getMessageInputIdentifier(), exItem.getIdentifier());
                }
                if (exItem.getMessageOutputIdentifier() != null) {
                    this.messageIDparamID_lookup.put(exItem.getMessageOutputIdentifier(), exItem.getIdentifier());
                }

            }
        }
        else {
            // TODO: implement other types of descriptions (e.g. WSDL)
        }
    }

    @Override
    public void putAll(Map map) {
        super.putAll(map);
    }

    /**
     * Add data to a parameter contained by this map. Returns false if: 1) the parameterID is not in this map
     * 2) the value cannot be added to the parameter
     * 
     * @param identifier
     * @param data
     * @return
     */
    public boolean addData(ParameterID identifier, Object data) {
        // TODO: add check for data
        if (this.keySet().contains(identifier)) {
            return (this.get(identifier)).add(data);
        }
        return false;
    }

    /**
     * Remove data from parameter contained by this map. Returns false if: 1) the parameterID is not in this
     * map 2) the value cannot be added to the parameter
     * 
     * @param identifier
     * @param data
     * @return
     */
    public boolean removeData(ParameterID identifier, Object data) {
        if (this.keySet().contains(identifier)) {
            return this.get(identifier).remove(data);
        }
        return false;
    }

    public boolean addData(String messageID, Object data) {
        // lookup paramID for messageID
        ParameterID id = this.messageIDparamID_lookup.get(messageID);

        // add data
        if (id != null) {
            return addData(id, data);
        }
        return false;
    }

    public boolean removeData(String messageID, Object data) {
        // lookup paramID for messageID
        ParameterID id = this.messageIDparamID_lookup.get(messageID);

        // remove data
        if (id != null) {
            return removeData(id, data);
        }
        return false;
    }

}
