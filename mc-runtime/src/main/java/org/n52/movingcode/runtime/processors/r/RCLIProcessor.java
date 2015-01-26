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

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import org.n52.movingcode.runtime.iodata.IIOParameter;
import org.n52.movingcode.runtime.iodata.IODataType;
import org.n52.movingcode.runtime.iodata.IOParameter;
import org.n52.movingcode.runtime.iodata.MediaData;
import org.n52.movingcode.runtime.iodata.MimeTypeDatabase;
import org.n52.movingcode.runtime.iodata.IIOParameter.Direction;
import org.n52.movingcode.runtime.iodata.IIOParameter.ParameterID;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.processors.AUID;
import org.n52.movingcode.runtime.processors.AbstractProcessor;
import org.n52.movingcode.runtime.processors.PropertyMap;

public class RCLIProcessor extends AbstractProcessor {

    private static final long serialVersionUID = -620750931084064871L;
    private static MimeTypeDatabase mimeRegistry = getMimeRegistry();
    private static final String mimeTypeFile = "mime.types";

    protected static final String rExecutable = "R CMD";

    private static Logger LOGGER = Logger.getLogger(RCLIProcessor.class);

    /**
     * A sorted Map containing all executionValues in an ascending order
     */
    private SortedMap<ParameterID, String[]> executionValues = new TreeMap<ParameterID, String[]>();

    public RCLIProcessor(final File scratchworkspace, final MovingCodePackage mcp, final PropertyMap properties) {
        super(scratchworkspace, mcp, properties);
    }

    private boolean init() {
        // 1. check workspace constraints
        // CLI: NONE
        // 2. create unique subdirectory

        File tmpWorkspace = new File(this.scratchWorkspace.getAbsolutePath() + File.separator + AUID.randomAUID());

        if ( !tmpWorkspace.mkdir()) {
            LOGGER.error("Could not create instance workspace!");
            return false;
        }

        // 3. unzip workspace from package and assign workspaceDir
        // try {
        // this.clonedWorkspace = new File(this.mcPackage.dumpWorkspace(tmpWorkspace));
        // }
        // catch (Exception e) {
        // logger.error("Cannot write to instance workspace. " + clonedWorkspace.getAbsolutePath());
        // return false;
        // }

        return true;
    }

    public boolean isFeasible() {
        // FIXME implement
        return true;
    }

    public void execute(int timeoutSeconds) throws IllegalArgumentException, RuntimeException, IOException {
        LOGGER.debug("Executing...");
        
        // http://stat.ethz.ch/R-manual/R-patched/library/utils/html/BATCH.html
        
        
        LOGGER.debug("Done.");
    }

    private static MimeTypeDatabase getMimeRegistry() {
        URL registryURL = RCLIProcessor.class.getResource(mimeTypeFile);
        try {
            return new MimeTypeDatabase(registryURL.openStream());
        }
        catch (Exception e) {
            LOGGER.debug("Could not open MimeType Registry file." + e.getMessage());
            LOGGER.debug("Creating empty Registry instead.");
            return new MimeTypeDatabase();
        }
    }

    /**
     * Creates a CommandLine Object for execution
     * 
     * @param paramSet
     *        - the parameter specification
     * @param executionValues
     *        - the values for the parameters
     * @return CommandLine - an executable CommandLine
     */
    private static CommandLine buildCommandLine(String executable,
                                                SortedMap<ParameterID, String[]> executionValues,
                                                SortedMap<ParameterID, IOParameter> paramMap) throws IllegalArgumentException {

        CommandLine commandLine = CommandLine.parse(executable);

        // assemble commandLine with values, separators and all that stuff
        for (ParameterID identifier : executionValues.keySet()) {
            String argument = "";
            // 1. add prefix
            argument = argument + paramMap.get(identifier).printPrefix();
            // 2. add values with subsequent separator
            for (String value : executionValues.get(identifier)) {
                argument = argument + value + paramMap.get(identifier).printSeparator();
            }
            // remove last occurrence of separator
            if (argument.length() != 0) {
                argument = argument.substring(0, argument.length() - paramMap.get(identifier).printSeparator().length());
            }
            // 3. add suffix
            argument = argument + paramMap.get(identifier).printSuffix();
            // 4. add to argument to CommandLine
            commandLine.addArgument(argument, false);
        }

        return commandLine;
    }

    // delete the current workspace
    protected void finalize() throws IOException {
        // try {
        // FileUtils.deleteDirectory(clonedWorkspace.getParentFile());
        // }
        // catch (IOException e) {
        // log.error("Could not delete dead workspace:\n" +
        // clonedWorkspace.getParentFile().getAbsolutePath());
        // }
    }

}
