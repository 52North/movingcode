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

package org.n52.movingcode.runtime.test;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.xmlbeans.XmlException;
import org.n52.movingcode.runtime.ProcessorConfig;

public class GlobalTestConfig {
    static final String PROCESSOR_CONFIG_FILE = "src/test/resources/processors.xml";

    static final String feedURL = "http://141.30.100.178/gpfeed/gpfeed.xml";

    public GlobalTestConfig() {
        GlobalTestConfig.setup();
    }

    private static void configureProcessors() throws XmlException, IOException {
        File procConfigFile = new File(PROCESSOR_CONFIG_FILE);
        System.out.println(procConfigFile.getAbsolutePath());
        ProcessorConfig.getInstance().setConfig(procConfigFile);
    }

    private static void configureLogger() {
        // Logger stuff
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel(Level.INFO);
    }

    static void setup() {
        configureLogger();
        try {
            configureProcessors();
        }
        catch (XmlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}