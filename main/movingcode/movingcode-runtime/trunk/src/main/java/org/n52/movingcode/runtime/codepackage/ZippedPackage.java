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

package org.n52.movingcode.runtime.codepackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;

/**
 * @author Matthias Mueller, TU Dresden
 * 
 */
final class ZippedPackage {

    private static Logger log = Logger.getLogger(ZippedPackage.class);

    private final File zipFile;
    private final URL zipURL;

    private final File rawWorkspace;
    private final PackageDescriptionDocument rawDescription;

    protected ZippedPackage(final File zipFile) {
        this.zipFile = zipFile;
        this.zipURL = null;
        this.rawWorkspace = null;
        this.rawDescription = null;
    }

    protected ZippedPackage(final URL zipURL) {
        this.zipURL = zipURL;
        this.zipFile = null;
        this.rawWorkspace = null;
        this.rawDescription = null;
    }

    protected ZippedPackage(final File workspace, final PackageDescriptionDocument descriptionXML) {
        this.zipURL = null;
        this.zipFile = null;

        this.rawWorkspace = workspace;
        this.rawDescription = descriptionXML;
    }

    protected final PackageDescriptionDocument extractDescription() {
        if (isRaw()) {
            return rawDescription;
        }
        else {
            return extractDescription(this);
        }
    }

    protected final void dumpWorkspace(String workspaceDirName, File targetDirectory) {

        if (isRaw()) {
            try {
                Collection<File> files = FileUtils.listFiles(rawWorkspace, null, false);
                for (File file : files) {
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, targetDirectory);
                    }
                    else {
                        FileUtils.copyFileToDirectory(file, targetDirectory);
                    }
                }
            }
            catch (IOException e) {
                System.err.println("Error! Could copy from " + rawWorkspace.getAbsolutePath() + " to "
                        + targetDirectory.getAbsolutePath());
            }

        }
        else {
            unzipWorkspace(this, workspaceDirName, targetDirectory);
        }

    }

    /*
     * static method to extract the description from a package
     */
    private static PackageDescriptionDocument extractDescription(ZippedPackage archive) {

        // zipFile and zip url MUST not be null at the same time
        assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
        String archiveName = null;

        ZipInputStream zis = null;
        PackageDescriptionDocument doc = null;
        
        try {
            if (archive.zipFile != null) {
                zis = new ZipInputStream(new FileInputStream(archive.zipFile));
                archiveName = archive.zipFile.getAbsolutePath();
            }
            else if (archive.zipURL != null) {
                zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
                archiveName = archive.zipURL.toString();
            }

            if (zis == null) {
                log.error("ZipInputStream is null, returning...");
                return null;
            }

            ZipEntry entry;

            while ( (entry = zis.getNextEntry()) != null) {
                if (entry.getName().equalsIgnoreCase(MovingCodePackage.descriptionFileName)) {
                    doc = PackageDescriptionDocument.Factory.parse(zis);
                    break;
                }
            }

            zis.close();
        }
        catch (ZipException e) {
            System.err.println("Error! Could read from archive: " + archiveName);
        }
        catch (IOException e) {
            System.err.println("Error! Could not open archive: " + archiveName);
        }
        catch (XmlException e) {
            System.err.println("Error! Could not parse package description from archive: " + archiveName);
        }

        return doc;
    }

    /*
     * creates an unzipped copy of the archive
     */
    private static void unzipPackage(ZippedPackage archive, File targetDirectory) {

        // zipFile and zip url MUST not be null at the same time
        assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
        String archiveName = null;

        try {

            ZipInputStream zis = null;
            if (archive.zipFile != null) {
                zis = new ZipInputStream(new FileInputStream(archive.zipFile));
                archiveName = archive.zipFile.getAbsolutePath();
            }
            else if (archive.zipURL != null) {
                zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
                archiveName = archive.zipURL.toString();
            }

            ZipEntry entry;
            while ( (entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);

                // create all required directories
                new File(newFile.getParent()).mkdirs();

                // if current zip entry is not a directory, do unzip
                if ( !entry.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    IOUtils.copy(zis, fos);
                    zis.close();
                    fos.close();
                }
                zis.closeEntry();
            }

            // Enumeration<? extends ZipEntry> entries = zis.entries();
            // while (entries.hasMoreElements()){
            // ZipEntry entry = entries.nextElement();
            // String fileName = entry.getName();
            // File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);
            //
            // // create all required directories
            // new File(newFile.getParent()).mkdirs();
            //
            // // if current zip entry is not a directory, do unzip
            // if(!entry.isDirectory()){
            // FileOutputStream fos = new FileOutputStream(newFile);
            // InputStream is = zif.getInputStream(entry);
            // IOUtils.copy(is, fos);
            // is.close();
            // fos.close();
            // }
            // }
        }
        catch (ZipException e) {
            System.err.println("Error! Could read from archive: " + archiveName);
        }
        catch (IOException e) {
            System.err.println("Error! Could not open archive: " + archiveName);
        }
        catch (NullPointerException e) {
            System.err.println("No archive has been declared. This should not happen ...");
        }
    }

    /*
     * creates an unzipped copy of the archive
     */
    private static void unzipWorkspace(ZippedPackage archive, String workspaceDirName, File targetDirectory) {

        // zipFile and zip url and isRaw() MUST not be null at the same time
        assert ( ! ( (archive.zipFile == null) || (archive.zipURL == null)));
        String archiveName = null;

        if (workspaceDirName.startsWith("./")) {
            workspaceDirName = workspaceDirName.substring(2);
        }

        if (workspaceDirName.startsWith(".\\")) {
            workspaceDirName = workspaceDirName.substring(2);
        }

        try {

            ZipInputStream zis = null;
            if (archive.zipFile != null) {
                zis = new ZipInputStream(new FileInputStream(archive.zipFile));
                archiveName = archive.zipFile.getAbsolutePath();
            }
            else if (archive.zipURL != null) {
                zis = new ZipInputStream(archive.zipURL.openConnection().getInputStream());
                archiveName = archive.zipURL.toString();
            }

            ZipEntry entry;
            while ( (entry = zis.getNextEntry()) != null) {
                if (entry.getName().startsWith(workspaceDirName)) {

                    String fileName = entry.getName();
                    File newFile = new File(targetDirectory.getAbsolutePath() + File.separator + fileName);

                    // create all required directories
                    new File(newFile.getParent()).mkdirs();

                    // if current zip entry is not a directory, do unzip
                    if ( !entry.isDirectory()) {
                        FileOutputStream fos = new FileOutputStream(newFile);
                        IOUtils.copy(zis, fos);
                        fos.close();
                    }
                }
                zis.closeEntry();
            }
            if (zis != null) {
                zis.close();
            }
        }
        catch (ZipException e) {
            System.err.println("Error! Could read from archive: " + archiveName);
        }
        catch (IOException e) {
            System.err.println("Error! Could not open archive: " + archiveName);
        }
        catch (NullPointerException e) {
            System.err.println("No archive has been declared. This should not happen ...");
        }
    }

    /*
     * writes a copy of the package (zipfile) to a given directory
     */
    protected boolean dumpPackage(File targetFile) {
        // zipFile and zip url MUST not be null at the same time
        assert ( ! ( (zipFile == null) || (zipURL == null)) || (this.isRaw()));

        // in case there is a zipped package file on disk
        if (zipFile != null) {
            try {
                FileUtils.copyFile(zipFile, targetFile);
                return true;
            }
            catch (Exception e) {
                return false;
            }
            // in case there is no file on disk and but a valid url to a zipped package
        }
        else if (zipURL != null) {
            try {
                FileUtils.copyURLToFile(zipURL, targetFile);
                return true;
            }
            catch (IOException e) {
                return false;
            }
        }
        // in case there is a raw package with separate workspace and description
        else if (isRaw()) {
            try {
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile));
                // add package description to zipFile
                zos.putNextEntry(new ZipEntry(MovingCodePackage.descriptionFileName));
                IOUtils.copy(rawDescription.newInputStream(), zos);
                zos.closeEntry();

                // add workspace recursively, with relative pathnames
                File base = rawWorkspace.getAbsoluteFile().getParentFile();
                addDir(rawWorkspace, zos, base);

                zos.close();

                return true;
            }
            catch (IOException e) {
                return false;
            }
        }

        return false;
    }

    private boolean isRaw() {
        return (rawWorkspace != null && rawDescription != null);
    }

    // helper method, adds contents of a directory to Zipped Output Stream
    private static void addDir(File dirObj, ZipOutputStream out, File base) throws IOException {
        File[] files = dirObj.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                addDir(files[i], out, base);
                continue;
            }
            FileInputStream in = new FileInputStream(files[i].getAbsolutePath());
            // construct relative path
            out.putNextEntry(new ZipEntry(relative(base, files[i])));
            // do copy
            IOUtils.copy(in, out);

            out.closeEntry();
            in.close();
        }
    }

    private static String relative(final File base, final File file) {
        final int rootLength = base.getAbsolutePath().length();
        final String absFileName = file.getAbsolutePath();
        final String relFileName = absFileName.substring(rootLength + 1);
        return relFileName;
    }
}
