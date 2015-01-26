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
package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.net.URL;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.codepackage.PID;

import com.google.common.collect.ImmutableSet;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * A repository that contains MovingCode packages.
 * 
 * @author Matthias Mueller, TU Dresden
 */

public interface MovingCodeRepository {

	final long localPollingInterval = 20 * 1000; // 20 sec
	final long remotePollingInterval = 10 * 60 * 1000; // 10 min

	/**
	 * Returns the IDs of all registered packages.
	 * 
	 * @return Array of packageIDs {@link String}
	 */
	public PID[] getPackageIDs();

	/**
	 * method to determine whether a package with the given ID is provided by this repository
	 * 
	 * @param packageID {@link String} - the internal (unique) identifier of package. 
	 * @return boolean - true if a package with the given ID is provided by this repository.
	 */
	public boolean containsPackage(PID packageId);

	/**
	 * returns a package matching a given packageID
	 * 
	 * @param {@link String} packageId - the (unique) ID of the package
	 * 
	 */
	public MovingCodePackage getPackage(PID packageId);
	
	/**
	 * Returns the latest version of the registered packages. Packages are distinguished by their
	 * names. If two packages have the same name, the package with the greater (newer) timestamp
	 * is returned.
	 * 
	 * @return
	 */
	public ImmutableSet<MovingCodePackage> getLatestPackages();

	/**
	 * Returns a package matching a given functionID.
	 * If there a multiple packages that provide the same function, this method could return any
	 * of them. If you need fine-grained control about the selection process, you have to implement
	 * your own logic and request the package directly by calling {@link MovingCodeRepository#getPackage(String)}
	 * 
	 * @param {@link String} functionID - the (unique) ID of the package
	 * @return - Array of MovingCodePackage
	 * 
	 */
	public MovingCodePackage[] getPackageByFunction(String functionId);

	/**
	 * returns package description for a given package ID
	 * the returned packageDescription shall be an exclusive copy
	 * and changes to the returned document shall not be forwarded
	 * to the package 
	 */
	public PackageDescriptionDocument getPackageDescriptionAsDocument(PID packageId);
	
	public String getPackageDescriptionAsString(PID packageId);
	
	/**
	 * Public method to determine whether this repository instance provides a certain functionality
	 * (i.e. ProcessIdentifier in WPS 1.0). 
	 * 
	 * @param functionalID {@link String}
	 * @return boolean - returns true if this repository instance contains a suitable package for a given functional ID, false otherwise.
	 */
	public boolean providesFunction(String functionId);

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
		 * Creates a {@link MovingCodeRepository} from a remote feed URL.
		 * 
		 * @param atomFeedURL {@link URL} -  the URL of the remote feed
		 * @return {@link MovingCodeRepository} - an new repository
		 */
		public static final  MovingCodeRepository createFromRemoteFeed(final URL atomFeedURL){
			return new RemoteFeedRepository(atomFeedURL);
		}

		/**
		 * Creates a {@link MovingCodeRepository} from a local folder with zipped packages. This folder may contain an arbitrary
		 * number of zipped packages. The packages can also be nested in sub-directories.
		 * 
		 * @param sourceDirectory {@link File} - the source directory, which contains all the zipped packages.
		 * @return {@link MovingCodeRepository} - an new repository
		 */
		public static final MovingCodeRepository createFromZipFilesFolder(File sourceDirectory){
			return new LocalZipPackageRepository(sourceDirectory);
		}

		/**
		 * Creates a {@link MovingCodeRepository} from a local folder with plain packages. This folder may contain an arbitrary
		 * number of plain packages. The packages <b>cannot<b> be nested in sub-directories.
		 * 
		 * @param codeSpace{{@link String} - code space prefix used to create package identifiers
		 * @param sourceDirectory {@link File} - the source directory, which contains all the zipped packages.
		 * @return {@link MovingCodeRepository} - an new repository
		 */
		public static final MovingCodeRepository createFromPlainFolder( File sourceDirectory){
			return new LocalPlainRepository(sourceDirectory);
		}

		/**
		 * Creates a {@link MovingCodeRepository} from a remote feed URL.
		 * Uses a cache directory to store its contents
		 * 
		 * @param atomFeedURL {@link URL} -  the URL of the remote feed
		 * @param cacheDirectory {@link File} - the directory that contains the cached content ot he remote repo
		 * @return {@link MovingCodeRepository} - the new repository
		 * 
		 * TODO: add a wipe trigger? --> Directory will be empties on load ... but is this really useful?
		 * 
		 */
		public static final MovingCodeRepository createCachedRemoteRepository(final URL atomFeedURL, final File cacheDirectory){
			return new CachedRemoteFeedRepository(atomFeedURL, cacheDirectory);
		}

	}

}
