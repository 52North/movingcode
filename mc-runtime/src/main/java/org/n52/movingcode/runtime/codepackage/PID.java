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
package org.n52.movingcode.runtime.codepackage;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * A immutable class for package IDs
 * 
 * @author matthias
 *
 */
public class PID implements Comparable<PID> {

	public final String name;
	public final DateTime timestamp;

	public PID (final String name, final DateTime timestamp){
		this.name = name;
		this.timestamp = timestamp;
	}
	
	/**
	 * Parses a packageId of the form
	 * my.package.name(version-timestamp)
	 * 
	 * 
	 * @param s
	 * @return
	 */
	public static final PID fromString(String s){
		int versionStart = s.lastIndexOf("_");
		String name = s.substring(0,versionStart);
		DateTime timestamp = DateTime.parse(s.substring(versionStart+1, s.length()));
		return new PID(name, timestamp);
	}
	
	public boolean isNewerThan(final PID packageId){
		return timestamp.isAfter(packageId.timestamp);
	}

	@Override
	public boolean equals(Object o){
		if (this == o){
			return true;
		}
		if (o instanceof PID){
			PID otherPID = (PID) o;
			return this.name.equals((otherPID).name) && this.timestamp.isEqual((otherPID).timestamp);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + name.hashCode();
		hash = hash * 13 + (int)timestamp.getMillis();
		return hash;
	}

	@Override
	public String toString(){
		return new StringBuilder()
			.append(name)
			.append("_")
			.append(timestamp.withZone(DateTimeZone.UTC).toString())
			.toString();
	}

	/**
	 * Compares two PID by name and timestamp. The name is evaluated first and compared
	 * lexicographically. 
	 * 
	 * @return
	 * 0: both PID are equal
	 * +: this PID is greater than the argument
	 * -: this PID is smaller than the argument
	 * 
	 */
	@Override
	public int compareTo(PID other) {
		int retval = this.name.compareTo(other.name);
		if(retval == 0){
			return this.timestamp.compareTo(other.timestamp);
		} else {
			return retval;
		}
	}

}
