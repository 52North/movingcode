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

import java.util.HashMap;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
public class FileDataConstants {
    public static final String MIME_TYPE_ZIPPED_SHP = "application/x-zipped-shp";
    public static final String MIME_TYPE_SHP = "application/shp";
    public static final String MIME_TYPE_HDF = "application/img";
    public static final String MIME_TYPE_GEOTIFF = "application/geotiff";
    public static final String MIME_TYPE_TIFF = "image/tiff";
    public static final String MIME_TYPE_DBASE = "application/dbase";
    public static final String MIME_TYPE_REMAPFILE = "application/remap";
    public static final String MIME_TYPE_PLAIN_TEXT = "text/plain";
    public static final String MIME_TYPE_TEXT_XML = "text/xml";
    public static final String MIME_TYPE_IMAGE_GEOTIFF = "image/geotiff";
    public static final String MIME_TYPE_X_GEOTIFF = "application/x-geotiff";
    public static final String MIME_TYPE_IMAGE_PNG = "image/png";
    public static final String MIME_TYPE_IMAGE_GIF = "image/gif";
    public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_X_ERDAS_HFA = "application/x-erdas-hfa";
    public static final String MIME_TYPE_NETCDF = "application/netcdf";
    public static final String MIME_TYPE_X_NETCDF = "application/x-netcdf";
    public static final String MIME_TYPE_DGN = "application/dgn";
    public static final String MIME_TYPE_KML = "application/vnd.google-earth.kml+xml";
    public static final String MIME_TYPE_HDF4EOS = "application/hdf4-eos";

    public static final HashMap<String, String> mimeTypeFileTypeLUT() {

        HashMap<String, String> lut = new HashMap<String, String>();

        lut.put(MIME_TYPE_ZIPPED_SHP, "shp");
        lut.put(MIME_TYPE_SHP, "shp");
        lut.put(MIME_TYPE_HDF, "img");
        lut.put(MIME_TYPE_GEOTIFF, "tif");
        lut.put(MIME_TYPE_X_GEOTIFF, "tif");
        lut.put(MIME_TYPE_IMAGE_GEOTIFF, "tif");
        lut.put(MIME_TYPE_IMAGE_PNG, "png");
        lut.put(MIME_TYPE_IMAGE_JPEG, "jpeg");
        lut.put(MIME_TYPE_IMAGE_GIF, "gif");
        lut.put(MIME_TYPE_TIFF, "tif");
        lut.put(MIME_TYPE_DBASE, "dbf");
        lut.put(MIME_TYPE_REMAPFILE, "RMP");
        lut.put(MIME_TYPE_PLAIN_TEXT, "txt");
        lut.put(MIME_TYPE_TEXT_XML, "xml");
        lut.put(MIME_TYPE_X_ERDAS_HFA, "img");
        lut.put(MIME_TYPE_NETCDF, "nc");
        lut.put(MIME_TYPE_X_NETCDF, "nc");
        lut.put(MIME_TYPE_DGN, "dgn");
        lut.put(MIME_TYPE_KML, "kml");
        lut.put(MIME_TYPE_HDF4EOS, "hdf");

        return lut;
    }

    public static final String[] getMimeTypes() {
        return mimeTypeFileTypeLUT().keySet().toArray(new String[0]);
    }

    private static final String[] additionalSHPFileItems = {"shx", "dbf", "prj"};

    public static final String[] getIncludeFilesByMimeType(String mimeType) {

        String[] returnValue = null;

        if (mimeType.equalsIgnoreCase("application/x-zipped-shp")) {
            returnValue = additionalSHPFileItems;
        }

        return returnValue;

    }
}