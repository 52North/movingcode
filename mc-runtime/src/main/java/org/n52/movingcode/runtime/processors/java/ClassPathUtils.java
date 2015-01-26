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

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

final class ClassPathUtils {

	protected static void bootstrapJar(final String jarPath) {

		// bootstrap arcobjects.jar
		System.out.println("Bootstrapping JAR: " + jarPath);

		File jarFile = new File(jarPath);
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;

		try {

			Method method = sysclass.getDeclaredMethod("addURL", new Class[] {URL.class});
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] {jarFile.toURI().toURL()});

		}
		catch (Throwable t) {
			t.printStackTrace();
			System.err.println("Could not add JAR to system classloader: " + jarPath);
		}

		Thread.currentThread().setContextClassLoader(sysloader);

	}

	// protected static void somemethod(){
	// JarClassLoader jcl = new JarClassLoader();
	// jcl.add("myjar.jar"); //Load jar file
	// jcl.add(new URL("http://myserver.com/myjar.jar")); //Load jar from a URL
	// jcl.add(new FileInputStream("myotherjar.jar")); //Load jar file from stream
	// jcl.add("myclassfolder/"); //Load class folder
	// jcl.add("myjarlib/"); //Recursively load all jar files in the folder/sub-folder(s)
	//
	// JclObjectFactory factory = JclObjectFactory.getInstance();
	//
	// //Create object of loaded class
	// Object obj = factory.create(jcl,"mypackage.MyClass");
	//
	// }

	public static void main(String jarFilePath) throws Exception {
		URL jarURL = new File(jarFilePath).toURI().toURL();

		// Entweder so
		URLClassLoader classLoader = new URLClassLoader(new URL[] {jarURL});

		// //Oder so
		// ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		// if(classLoader != null && (classLoader instanceof URLClassLoader)){
		// URLClassLoader urlClassLoader = (URLClassLoader)classLoader;
		// Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		// addURL.setAccessible(true);
		// addURL.invoke(urlClassLoader, new Object[]{jarURL});
		// }

		Class testRunnerClass = classLoader.loadClass("junit.swingui.TestRunner");
		testRunnerClass.getMethod("main", new Class[] {String[].class}).invoke(null, new Object[] {new String[0]});

	}
}
