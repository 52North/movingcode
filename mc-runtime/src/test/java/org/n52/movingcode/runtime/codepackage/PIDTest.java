package org.n52.movingcode.runtime.codepackage;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Test;

public class PIDTest {

	@Test
	public void pidToStringTest() {
		PID p = new PID("my.package.name", DateTime.now());
		System.out.println(p.toString());
	}
	
	@Test
	public void pidParseTest() {
		PID p = new PID("my.package.name", DateTime.now());
		System.out.println(p.toString());
		PID p2 = PID.fromString(p.toString());
		System.out.println(p2.toString());
		assertTrue(p.equals(p2));
	}

}
