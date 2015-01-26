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
package org.n52.movingcode.runtime.feed;

import net.opengis.wps.x100.DescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class WPSDescriptionPrinter {

    // HTML templates
    private static final String COMMON_HTML_TEMPLATE_PROCESS =
    // "<p><b><tt><big>%identifier%</big></tt> - %title%</b></p>" +
    "<p><b>%title%</b></p>" + "<p>%abstract%</p><hr/>";

    private static final String COMMON_HTML_TEMPLATE_PARAM = "<p><b><tt><big>%identifier%</big></tt> - %title%</b>"
            + "<br/>%abstract%<br/></p>";

    private static final String COMMON_HTML_PROCESS_INPUTS_LINE = "<b>Parameters - Input</b><br/>";
    private static final String COMMON_HTML_PROCESS_OUTPUTS_LINE = "<b>Parameters - Output</b><br/>";

    // XHTML templates
    private static final String COMMON_XHTML_TEMPLATE_PROCESS = "<xhtml:h3>%identifier%</xhtml:h3>"
            + "<xhtml:h4>%title%</xhtml:h4>" + "<xhtml:p>%abstract%</xhtml:p>";

    private static final String COMMON_XHTML_TEMPLATE_PARAM = "<xhtml:h4>%identifier%</xhtml:h4>"
            + "<xhtml:p>%title%<br/>%abstract%</xhtml:p>";

    public static String printAsHTML(ProcessDescriptionType wpsProcessDescription) {

        String htmlOutput = COMMON_HTML_TEMPLATE_PROCESS;
        htmlOutput = substitute(htmlOutput, "%identifier%", wpsProcessDescription.getIdentifier().getStringValue());
        htmlOutput = substitute(htmlOutput, "%title%", wpsProcessDescription.getTitle().getStringValue());
        htmlOutput = substitute(htmlOutput, "%abstract%", wpsProcessDescription.getAbstract().getStringValue());

        htmlOutput = htmlOutput + COMMON_HTML_PROCESS_INPUTS_LINE;

        for (DescriptionType input : wpsProcessDescription.getDataInputs().getInputArray()) {
            String htmlItem = COMMON_HTML_TEMPLATE_PARAM;
            htmlItem = substitute(htmlItem, "%identifier%", input.getIdentifier().getStringValue());
            htmlItem = substitute(htmlItem, "%title%", input.getTitle().getStringValue());
            htmlItem = substitute(htmlItem, "%abstract%", input.getAbstract().getStringValue());
            htmlOutput = htmlOutput + htmlItem;
        }

        htmlOutput = htmlOutput + COMMON_HTML_PROCESS_OUTPUTS_LINE;

        for (DescriptionType output : wpsProcessDescription.getProcessOutputs().getOutputArray()) {
            String htmlItem = COMMON_HTML_TEMPLATE_PARAM;
            htmlItem = substitute(htmlItem, "%identifier%", output.getIdentifier().getStringValue());
            htmlItem = substitute(htmlItem, "%title%", output.getTitle().getStringValue());
            htmlItem = substitute(htmlItem, "%abstract%", output.getAbstract().getStringValue());
            htmlOutput = htmlOutput + htmlItem;
        }

        return htmlOutput;
    }

    public String getAsXHTML(ProcessDescriptionType wpsProcessDescription) {
        String xhtmlOutput = COMMON_XHTML_TEMPLATE_PROCESS;
        xhtmlOutput = substitute(xhtmlOutput, "%identifier%", wpsProcessDescription.getIdentifier().getStringValue());
        xhtmlOutput = substitute(xhtmlOutput, "%title%", wpsProcessDescription.getTitle().getStringValue());
        xhtmlOutput = substitute(xhtmlOutput, "%abstract%", wpsProcessDescription.getAbstract().getStringValue());

        for (DescriptionType input : wpsProcessDescription.getDataInputs().getInputArray()) {
            String xhtmlItem = COMMON_XHTML_TEMPLATE_PARAM;
            xhtmlItem = substitute(xhtmlItem, "%identifier%", input.getIdentifier().getStringValue());
            xhtmlItem = substitute(xhtmlItem, "%title%", input.getTitle().getStringValue());
            xhtmlItem = substitute(xhtmlItem, "%abstract%", input.getAbstract().getStringValue());
            xhtmlOutput = xhtmlOutput + xhtmlItem;
        }

        for (DescriptionType output : wpsProcessDescription.getProcessOutputs().getOutputArray()) {
            String xhtmlItem = COMMON_XHTML_TEMPLATE_PARAM;
            xhtmlItem = substitute(xhtmlItem, "%identifier%", output.getIdentifier().getStringValue());
            xhtmlItem = substitute(xhtmlItem, "%title%", output.getTitle().getStringValue());
            xhtmlItem = substitute(xhtmlItem, "%abstract%", output.getAbstract().getStringValue());
            xhtmlOutput = xhtmlOutput + xhtmlItem;
        }

        return xhtmlOutput;
    }

    private static String substitute(String input, String substring, String substitute) {
        int result;
        StringBuffer newstring;

        result = input.indexOf(substring);

        if (result < 0)
            return input;

        newstring = new StringBuffer();
        newstring.append(input.substring(0, result));
        newstring.append(substitute);
        newstring.append(input.substring(result + substring.length()));
        return newstring.toString();
    }

}
