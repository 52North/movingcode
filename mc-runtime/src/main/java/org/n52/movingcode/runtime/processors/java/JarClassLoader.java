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
