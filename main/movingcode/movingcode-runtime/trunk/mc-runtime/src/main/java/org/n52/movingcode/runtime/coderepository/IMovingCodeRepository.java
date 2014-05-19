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
package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * A repository that contains MovingCode packages.
 * 
 * @author Matthias Mueller, TU Dresden
 */

public interface IMovingCodeRepository {
	
	final long localPollingInterval = 5 * 1000; // 5 sec
	final long remotePollingInterval = 10 * 60 * 1000; // 10 min
	
	/**
	 * Returns the IDs of all registered packages.
	 * 
	 * @return Array of packageIDs {@link String}
	 */
	public String[] getPackageIDs();
	
	/**
	 * method to determine whether a package with the given ID is provided by this repository
	 * 
	 * @param packageID {@link String} - the internal (unique) identifier of package. 
	 * @return boolean - true if a package with the given ID is provided by this repository.
	 */
	public boolean containsPackage(String packageID);
	
	/**
	 * returns a package matching a given packageID
	 * 
	 * @param {@link String} packageID - the (unique) ID of the package
	 * 
	 */
	public MovingCodePackage getPackage(String packageID);
	
	/**
	 * Returns a package matching a given functionID.
	 * If there a multiple packages that provide the same function, this method could return any
	 * of them. If you need fine-grained control about the selection process, you have to implement
	 * your own logic and request the package directly by calling {@link IMovingCodeRepository#getPackage(String)}
	 * 
	 * @param {@link String} functionID - the (unique) ID of the package
	 * @return - Array of MovingCodePackage
	 * 
	 */
	public MovingCodePackage[] getPackageByFunction(String functionID);

	/**
	 * returns the last known update timestamp of a MovingCodePackage
	 */
	public Date getPackageTimestamp(String packageID);

	/**
	 * returns package description for a given package ID
	 * the returned packageDescription shall be an exclusive copy
	 */
	public PackageDescriptionDocument getPackageDescription(String packageID);
	
	/**
	 * Public method to determine whether this repository instance provides a certain functionality
	 * (i.e. ProcessIdentifier in WPS 1.0). 
	 * 
	 * @param functionalID {@link String}
	 * @return boolean - returns true if this repository instance contains a suitable package for a given functional ID, false otherwise.
	 */
	public boolean providesFunction(String functionID);
	
	/**
	 * Returns the function IDs (i.e. WPS processIdentifiers) of all registered packages
	 * 
	 * Returns a snapshot of all currently used identifiers. Later changes in the MovingCodeRepository (new or
	 * deleted packages) are not propagated to the returned array. If you need up-to-date information call
	 * this method again.
	 */
	public String[] getFunctionIDs();
	
	/**
	 * registers a changelistener. Implementations decide if they support
	 * the changelistener pattern.
	 * 
	 * @param l the listener
	 */
	public void addRepositoryChangeListener(RepositoryChangeListener l);

	/**
	 * removes a changelistener. Implementations decide if they support
	 * the changelistener pattern.
	 * 
	 * @param l the listener
	 */
	public void removeRepositoryChangeListener(RepositoryChangeListener l);
	
	
	//###########################################################################//
	/**
	 * Static Factory method to create MovingCodeRepositories from various sources
	 * 
	 * @author Matthias Mueller, TU Dresden
	 *
	 */
	static class Factory {
		
		/**
		 * Creates a {@link IMovingCodeRepository} from a remote feed URL.
		 * 
		 * @param atomFeedURL {@link URL} -  the URL of the remote feed
		 * @return {@link IMovingCodeRepository} - an new repository
		 */
		public static final  IMovingCodeRepository createFromRemoteFeed(final URL atomFeedURL){
			return new RemoteFeedRepository(atomFeedURL);
		}
		
		/**
		 * Creates a {@link IMovingCodeRepository} from a local folder with zipped packages. This folder may contain an arbitrary
		 * number of zipped packages. The packages can also be nested in sub-directories.
		 * 
		 * @param sourceDirectory {@link File} - the source directory, which contains all the zipped packages.
		 * @return {@link IMovingCodeRepository} - an new repository
		 */
		public static final IMovingCodeRepository createFromZipFilesFolder(File sourceDirectory){
			return new LocalZipPackageRepository(sourceDirectory);
		}
		
		/**
		 * Creates a {@link IMovingCodeRepository} from a local folder with plain packages. This folder may contain an arbitrary
		 * number of plain packages. The packages <b>cannot<b> be nested in sub-directories.
		 * 
		 * @param sourceDirectory {@link File} - the source directory, which contains all the zipped packages.
		 * @return {@link IMovingCodeRepository} - an new repository
		 */
		public static final IMovingCodeRepository createFromPlainFolder(File sourceDirectory){
			return new LocalPlainRepository(sourceDirectory);
		}
		
		/**
		 * Creates a {@link IMovingCodeRepository} from a remote feed URL.
		 * Uses a cache directory to store its contents
		 * 
		 * @param atomFeedURL {@link URL} -  the URL of the remote feed
		 * @param cacheDirectory {@link File} - the directory that contains the cached content ot he remote repo
		 * @return {@link IMovingCodeRepository} - the new repository
		 * 
		 * TODO: add a wipe trigger? --> Directory will be empties on load ... but is this really useful?
		 * 
		 */
		public static final IMovingCodeRepository createCachedRemoteRepository(final URL atomFeedURL, final File cacheDirectory){
			return new CachedRemoteFeedRepository(atomFeedURL, cacheDirectory);
		}
		
	}

}
