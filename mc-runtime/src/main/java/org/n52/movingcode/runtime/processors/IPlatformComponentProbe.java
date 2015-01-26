package org.n52.movingcode.runtime.processors;

/**
 * A platform probe shall identify the runtime platform based on some magic tests.
 * 
 * TODO: needs further work
 * 
 * @author Matthias Mueller
 * 
 */
public interface IPlatformComponentProbe {
	public String probe();
}
