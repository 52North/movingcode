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
package org.n52.movingcode.runtime.processors.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.JarURLConnection;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.InvocationTargetException;
import java.util.jar.Attributes;
import java.io.IOException;

/**
 * A class loader for loading jar files, both local and remote.
 * 
 * http://java.sun.com/developer/Books/javaprogramming/JAR/api/example-1dot2/JarClassLoader.java
 * 
 */
class JarClassLoader extends URLClassLoader {
	private URL url;

	/**
	 * Creates a new JarClassLoader for the specified url.
	 * 
	 * @param url
	 *        the url of the jar file
	 */
	public JarClassLoader(URL url) {
		super(new URL[] {url});
		this.url = url;
	}

	/**
	 * Returns the name of the jar file main class, or null if no "Main-Class" manifest attributes was
	 * defined.
	 */
	public String getMainClassName() throws IOException {
		URL u = new URL("jar", "", this.url + "!/");
		JarURLConnection uc = (JarURLConnection) u.openConnection();
		Attributes attr = uc.getMainAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}

	/**
	 * Invokes the application in this jar file given the name of the main class and an array of arguments.
	 * The class must define a static method "main" which takes an array of String arguments and is of return
	 * type "void".
	 * 
	 * @param name
	 *        the name of the main class
	 * @param args
	 *        the arguments for the application
	 * @exception ClassNotFoundException
	 *            if the specified class could not be found
	 * @exception NoSuchMethodException
	 *            if the specified class does not contain a "main" method
	 * @exception InvocationTargetException
	 *            if the application raised an exception
	 */
	public void invokeClassMain(String name, String[] args) throws ClassNotFoundException,
	NoSuchMethodException,
	InvocationTargetException {
		Class c = loadClass(name);
		Method m = c.getMethod("main", new Class[] {args.getClass()});
		m.setAccessible(true);
		int mods = m.getModifiers();
		if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException("main");
		}
		try {
			m.invoke(null, new Object[] {args});
		}
		catch (IllegalAccessException e) {
			// This should not happen, as we have disabled access checks
		}
	}

}
