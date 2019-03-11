/**
 * -
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vall?e-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package soot.asm.backend;


import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.G;
import soot.Main;


/**
 * Test for fields that contain constant values
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 */
public class ConstantPoolTest extends AbstractASMBackendTest {
    private static final Logger logger = LoggerFactory.getLogger(ConstantPoolTest.class);

    @Test
    public void loadClass() {
        G.reset();
        // Location of the rt.jar
        String rtJar = ((((System.getProperty("java.home")) + (File.separator)) + "lib") + (File.separator)) + "rt.jar";
        // Run Soot and print output to .asm-files.
        Main.main(new String[]{ "-cp", ((getClassPathFolder()) + (File.pathSeparator)) + rtJar, "-process-dir", getTargetFolder(), "-src-prec", "only-class", "-output-format", "class", "-asm-backend", "-allow-phantom-refs", "-java-version", getRequiredJavaVersion(), getTargetClass() });
        File file = new File("./sootOutput/ConstantPool.class");
        URL[] urls = null;
        try {
            URL url = file.toURI().toURL();
            urls = new URL[]{ url };
            URLClassLoader cl = new URLClassLoader(urls);
            cl.loadClass(getTargetClass());
            // cl.close();
            // Java 6 backwards compatibility hack
            try {
                for (Method m : URLClassLoader.class.getDeclaredMethods()) {
                    if (m.getName().equals("close")) {
                        m.invoke(cl);
                        break;
                    }
                }
            } catch (Exception e) {
            }
            return;
        } catch (MalformedURLException e) {
            ConstantPoolTest.logger.error(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            ConstantPoolTest.logger.error(e.getMessage(), e);
        }
        Assert.fail();
    }
}

