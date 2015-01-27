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
package org.n52.movingcode.runtime.processors.r;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.iodata.MimeTypeDatabase;
import org.n52.movingcode.runtime.iodata.IIOParameter.Direction;
import org.n52.movingcode.runtime.iodata.ParameterID;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.PropertyMap;

/**
 * 
 * 
 * 
 * @author Stefan Wiemann, Matthias Mueller
 * 
 */
public class RServerProcessor extends AbstractProcessor {

    private static final long serialVersionUID = 365036617414065260L;

    private static Logger logger = Logger.getLogger(RServerProcessor.class);

    private static MimeTypeDatabase mimeRegistry = getMimeRegistry();
    
    private static final String mimeTypeFile = "mime.types";
    
    private File clonedWorkspace;

    /**
     * A sorted Map containing all executionValues in an ascending order
     */
    private SortedMap<ParameterID, String[]> executionValues = new TreeMap<ParameterID, String[]>();

    private static MimeTypeDatabase getMimeRegistry() {
        URL registryURL = RServerProcessor.class.getResource(mimeTypeFile);
        try {
            return new MimeTypeDatabase(registryURL.openStream());
        }
        catch (Exception e) {
            System.out.println("Could not open MimeType Registry file." + e.getMessage());
            System.out.println("Creating empty Registry instead.");
            return new MimeTypeDatabase();
        }
    }

    public RServerProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
        super(scratchworkspace, mcp, properties);
    }

    private boolean init() {
        // 1. check workspace constraints
        // CLI: NONE
        // 2. create unique subdirectory

        File tmpWorkspace = new File(this.scratchWorkspace.getAbsolutePath() + File.separator + AUID.randomAUID());

        if ( !tmpWorkspace.mkdir()) {
            logger.error("Could not create instance workspace!");
            return false;
        }

        // 3. unzip workspace from package and assign workspaceDir
        try {
            this.clonedWorkspace = new File(this.mcPackage.dumpWorkspace(tmpWorkspace));
        }
        catch (Exception e) {
            logger.error("Cannot write to instance workspace. " + this.clonedWorkspace.getAbsolutePath());
            return false;
        }

        return true;
    }

    public boolean isFeasible() {
        // TODO implement
        return true;
    }

    public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException {

        if ( !init()) {
            throw new IOException("Could not initialize the processor. Aborting operation.");
        }

        // load arguments and parse them to internal data format (--> Strings)
        for (IOParameter item : this.values()) {
            try {
                setValue(item);
            }
            catch (IOException e) {
                throw new IOException("Could not deal with parameter: " + item.getIdentifier().toString()
                        + "\n" + e.getMessage());
            }
        }

        // create toolName and path
        String rScript = this.packageDescriptionDoc.getPackageDescription().getWorkspace().getExecutableLocation();
        if (rScript.startsWith("./")) {
            rScript = rScript.substring(2);
        }

        if (rScript.startsWith(".\\")) {
            rScript = rScript.substring(2);
        }
        rScript = this.clonedWorkspace + File.separator + rScript;

        String rFunction = null;
        if (this.packageDescriptionDoc.getPackageDescription().getWorkspace().isSetExecutableMethodCall()) {
            rFunction = this.packageDescriptionDoc.getPackageDescription().getWorkspace().getExecutableMethodCall();
        }

        // execute and break if an error occurs
        boolean success = executeRScript(rFunction, rScript);
        if ( !success) {
            throw new RuntimeException("Execution terminated with an error.");
        }

        // update executionData - file data only
        // code below is all about setting the input stream for output media data
        for (ParameterID identifier : this.keySet()) {
            if (this.get(identifier).isMessageOut()) {
                if (this.get(identifier).supportsType(IODataType.MEDIA)) {
                    @SuppressWarnings("unchecked")
                    List<MediaData> mediaValues = (List<MediaData>) this.get(identifier);
                    for (int i = 0; i < mediaValues.size(); i++) {
                        String fileName = this.executionValues.get(identifier)[i];
                        // <-- this is the important line -->
                        mediaValues.get(i).setMediaStream(new FileInputStream(fileName));
                    }

                }
                else {
                    // currently not supported
                }
            }
        }

    }

    private boolean executeRScript(String rFunction, String rScriptPath) {

        // build parameters array for ArcGIS
        String[] paramArray = new String[this.executionValues.keySet().size()];
        int i = 0;
        for (ParameterID identifier : this.executionValues.keySet()) {
            String valString = "";
            for (String value : this.executionValues.get(identifier)) {
                if (valString == "") {
                    valString = value;
                }
                else {
                    valString = valString + " " + value;
                }
            }

            paramArray[i] = valString;
            i++;
        }

        // TODO: dynamic libraries
        String[] libraries = new String[0];

        return RServerController.execute(this.clonedWorkspace.getAbsolutePath(),
                                         rFunction,
                                         rScriptPath,
                                         paramArray,
                                         libraries);

    }

    private void setValue(final IOParameter data) throws IllegalArgumentException, IOException {

        boolean isInput = data.isMessageIn() && data.isMandatoryForExecution();
        boolean isOutputOnly = data.isMessageOut() && !data.isMessageIn();

        // TODO: substitute IF statements with SWITCH/CASE statements?

        // case: Boolean
        if (data.getType().equals(IODataType.BOOLEAN) && data.getType().getSupportedClass().equals(Boolean.class)) {
            if (isInput) {
                @SuppressWarnings("unchecked")
                List<Boolean> boolValues = (List<Boolean>) data;
                String[] stringValues = new String[boolValues.size()];
                for (int i = 0; i < stringValues.length; i++) {
                    stringValues[i] = boolValues.get(i).toString();
                }
                this.executionValues.put(data.getIdentifier(), stringValues);
            }
            else {
                if (data.getDirection() == Direction.OUT) {
                    // currently not supported
                }
                else {
                    // TODO: cannot happen (?)
                }
            }
        }

        // case: Integer
        if (data.getType().equals(IODataType.INTEGER) && data.getType().getSupportedClass().equals(Integer.class)) {
            if (isInput) {
                @SuppressWarnings("unchecked")
                List<Integer> intValues = (List<Integer>) data;
                String[] stringValues = new String[intValues.size()];
                for (int i = 0; i < stringValues.length; i++) {
                    stringValues[i] = intValues.get(i).toString();
                }
                this.executionValues.put(data.getIdentifier(), stringValues);
            }
            else {
                if (data.getDirection() == Direction.OUT) {
                    // currently not supported
                }
                else {
                    // TODO: cannot happen (?)
                }
            }
        }

        // case: Double
        if (data.getType().equals(IODataType.DOUBLE) && data.getType().getSupportedClass().equals(Double.class)) {
            if (isInput) {
                @SuppressWarnings("unchecked")
                List<Integer> dblValues = (List<Integer>) data;
                String[] stringValues = new String[dblValues.size()];
                for (int i = 0; i < stringValues.length; i++) {
                    stringValues[i] = dblValues.get(i).toString();
                }
                this.executionValues.put(data.getIdentifier(), stringValues);
            }
            else {
                if (data.getDirection() == Direction.OUT) {
                    // currently not supported
                }
                else {
                    // TODO: cannot happen (?)
                }
            }
        }

        // case: String
        if (data.getType().equals(IODataType.STRING) && data.getType().getSupportedClass().equals(String.class)) {
            if (isInput) {
                @SuppressWarnings("unchecked")
                String[] stringValues = ((List<String>) data).toArray(new String[data.size()]);
                this.executionValues.put(data.getIdentifier(), stringValues);
            }
            else {
                if (data.getDirection() == Direction.OUT) {
                    // currently not supported
                }
                else {
                    // TODO: cannot happen (?)
                }
            }
        }

        // case: Media Data
        if (data.getType().equals(IODataType.MEDIA) && data.getType().getSupportedClass().equals(MediaData.class)) {
            if (isInput) {
                @SuppressWarnings("unchecked")
                List<MediaData> mediaValues = (List<MediaData>) data;
                String[] stringValues = new String[mediaValues.size()];
                for (int i = 0; i < stringValues.length; i++) {
                    // get suitable file extension for the mime type
                    // trow an exception if it cannot be resolved
                    String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
                    if (fileExt == null) {
                        throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType()
                                + "' could not be resolved to a file extension.");
                    }
                    String path = this.clonedWorkspace + File.separator + AUID.randomAUID() + "." + fileExt;

                    File file = new File(path);

                    OutputStream os = new FileOutputStream(file);
                    IOUtils.copy(mediaValues.get(i).getMediaStream(), os);

                    stringValues[i] = file.getAbsolutePath();

                }
                this.executionValues.put(data.getIdentifier(), stringValues);
            }
            else {
                // special treatment for output-only data
                // create a unique filename that shall be passed as a command line argument
                if (isOutputOnly) {
                    @SuppressWarnings("unchecked")
                    List<MediaData> mediaValues = (List<MediaData>) data;
                    String[] stringValues = new String[mediaValues.size()];
                    for (int i = 0; i < stringValues.length; i++) {
                        String fileExt = mimeRegistry.getExtensionStrings(mediaValues.get(i).getMimeType())[0];
                        if (fileExt == null) {
                            throw new IllegalArgumentException("MimeType '" + mediaValues.get(i).getMimeType()
                                    + "' could not be resolved to a file extension.");
                        }

                        String path = this.clonedWorkspace + File.separator + AUID.randomAUID() + "." + fileExt;

                        stringValues[i] = path;
                    }
                    this.executionValues.put(data.getIdentifier(), stringValues);
                }
                else {
                    // TODO: cannot happen (?)
                }
            }
        }
    }
}
