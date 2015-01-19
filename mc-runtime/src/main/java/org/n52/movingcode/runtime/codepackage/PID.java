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
		int versionStart = s.indexOf("(");
		int versionEnd = s.indexOf(")");
		String name = s.substring(0,versionStart);
		DateTime timestamp = DateTime.parse(s.substring(versionStart+1, versionEnd));
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
			return this.name.equalsIgnoreCase((otherPID).name) && this.timestamp.isEqual((otherPID).timestamp);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + name.hashCode();
		hash = hash * 13 + timestamp.hashCode();
		return hash;
	}

	@Override
	public String toString(){
		return new StringBuilder()
			.append(name)
			.append("(")
			.append(timestamp.withZone(DateTimeZone.UTC).toString())
			.append(")")
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
