package org.n52.movingcode.runtime.coderepository;

import java.io.File;
import java.net.URL;
import java.util.Date;

import org.n52.movingcode.runtime.codepackage.MovingCodePackage;
import org.n52.movingcode.runtime.coderepository.RemoteFeedRepository;

import de.tudresden.gis.geoprocessing.movingcode.schema.PackageDescriptionDocument;


/**
 * A repository that contains MovingCode packages.
 * 
 * @author Matthias Mueller, TU Dresden
 */

public interface IMovingCodeRepository {

	/**
	 * returns a package matching a given identifier
	 * 
	 * @param {@link String} packageIdentifier - the (unique) ID of the package
	 * 
	 */
	public MovingCodePackage getPackage(String packageID);

	/**
	 * returns the last known update of a MovingCodePackage
	 */
	public Date getPackageTimestamp(String packageID);

	/**
	 * returns package description for a given package ID
	 */
	public PackageDescriptionDocument getPackageDescription(String packageID);

	/**
	 * Static Factory method to create MovingCodeRepositories from various sources
	 * 
	 * @author Matthias Mueller, TU Dresden
	 *
	 */
	static class Factory{
		
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
			return new LocalZipPackageRepository(sourceDirectory);
		}
		
	}

}
