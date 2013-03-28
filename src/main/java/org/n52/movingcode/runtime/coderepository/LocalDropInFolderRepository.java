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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.n52.movingcode.runtime.MovingCodeRepositoryManager;
import org.n52.movingcode.runtime.codepackage.MovingCodePackage;

/**
 * A repository implementing a folder watchdog.
 * Just drop a Zip-File into the {@link #getDirectory()} and
 * it will be integrated. When a new {@link MovingCodePackage}
 * has been loaded, all registered listeners ({@link #addRepositoryChangeListener(RepositoryChangeListener)})
 * are informed.
 * 
 * @author matthes rieke
 *
 */
public class LocalDropInFolderRepository extends LocalPlainRepository {

	private static final Logger logger = Logger.getLogger(LocalDropInFolderRepository.class);
	private Timer timer;
	private int checkIntervalSeconds;


	public LocalDropInFolderRepository(File dropInFolderDir,
			int checkIntervalSeconds) {
		super(dropInFolderDir);
		logger.info("New LocalDropInFolderRepository: "+dropInFolderDir.getAbsolutePath());
		
		this.checkIntervalSeconds = checkIntervalSeconds;
		
		MovingCodeRepositoryManager.getInstance().addRepository(this,
				this.getDirectory().getAbsolutePath());
		startWatching();
	}

	
	private void startWatching() {
		this.timer = new Timer(true);
		this.timer.scheduleAtFixedRate(new CheckDropInFolder(), 0, this.checkIntervalSeconds*1000);
	}
	
	
	/**
	 * A task which compares the previous fileset with the current state.
	 * if a new file was dropped, it will be checked and registered in this repository.
	 */
	private class CheckDropInFolder extends TimerTask {

		private Set<File> previousFileSet = new HashSet<File>();

		
		@Override
		public void run() {
			if (!getDirectory().exists() || !getDirectory().isDirectory()) {
				throw new RuntimeException("Folder "+ getDirectory().getAbsolutePath() +" not available.");
			}
			
			int previousCount = this.previousFileSet.size();
			
			Collection<File> directoryListing = LocalZipPackageRepository.scanForZipFiles(getDirectory());
			
			Set<File> newFiles = compareWithPreviousSet(directoryListing);
			Map<MovingCodePackage, File> newPackages = createMovingCodeObjects(newFiles);
			
			for (MovingCodePackage movingCodePackage : newPackages.keySet()) {
				if (getPackage(movingCodePackage.getPackageIdentifier()) == null) {
					// TODO: resolve this logic somewhere else
					// code repositories shouldn't call their manager.
					if (!MovingCodeRepositoryManager.getInstance().providesFunction(movingCodePackage.getFunctionalIdentifier())) {
						register(movingCodePackage);
						logger.info("Added MovingCodePackage with id "+
								movingCodePackage.getFunctionalIdentifier());
						previousFileSet.add(newPackages.get(movingCodePackage));
					}
					else {
						logger.warn("MovingCodePackage already registered: "+
								movingCodePackage.getFunctionalIdentifier());
					}
				}
			}

			if (previousCount != this.previousFileSet.size()) {
				logger.info("Calling Repository Change Listeners.");
				informRepositoryChangeListeners();
			}
		}

		private Set<File> compareWithPreviousSet(Collection<File> directoryListing) {
			Set<File> newFiles = new HashSet<File>(directoryListing);
			
			newFiles.removeAll(previousFileSet);
			
			return newFiles;
		}


		private Map<MovingCodePackage, File> createMovingCodeObjects(Set<File> newFiles) {
			Map<MovingCodePackage, File> result = new HashMap<MovingCodePackage, File>();
			
			for (File f : newFiles) {
				MovingCodePackage mcp = new MovingCodePackage(f,
						LocalZipPackageRepository.generateIDFromFilePath(f.getPath()));
				if (mcp.isValid()) {
					logger.info("Adding new Package: "+mcp.getPackageIdentifier());
					result.put(mcp, f);
				}
			}
			
			return result;
		}

		
	}


}
