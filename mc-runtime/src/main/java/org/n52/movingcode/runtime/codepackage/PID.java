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

/**
 * A immutable class for package IDs
 * 
 * @author matthias
 *
 */
public class PID {

	public final String id;
	public final DateTime timestamp;

	public PID (final String id, final DateTime timestamp){
		this.id = id;
		this.timestamp = timestamp;
	}
	

	@Override
	public boolean equals(Object o){
		if (this == o){
			return true;
		}
		if (o instanceof PID){
			PID otherPID = (PID) o;
			return this.id.equalsIgnoreCase((otherPID).id) && this.timestamp.isEqual((otherPID).timestamp);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 17;
		hash = hash * 31 + id.hashCode();
		hash = hash * 13 + timestamp.hashCode();
		return hash;
	}

	@Override
	public String toString(){
		return new String(id + "#" + timestamp.toString());
	}

}
