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
	
	@Test
	public void equalsTest() {
		PID p1 = new PID("my.package.name", DateTime.now());
		PID p2 = new PID(p1.name, p1.timestamp);	
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
	}
	
	@Test
	public void hashTest() {
		PID p1 = new PID("my.package.name", DateTime.now());
		PID p2 = new PID(p1.name, p1.timestamp);
		
		assertTrue(p1.hashCode() == p2.hashCode());
	}
	
	@Test
	public void comparesTest() throws InterruptedException {
		PID p1 = new PID("my.package.name", DateTime.now());
		Thread.sleep(100);
		PID p2 = new PID(p1.name, DateTime.now());
		PID p3 = new PID(p2.name, p2.timestamp);
		
		assertTrue(p2.compareTo(p1) > 0);
		assertTrue(p1.compareTo(p2) < 0);
		assertTrue (p2.compareTo(p3) == 0);
	}
	

}
