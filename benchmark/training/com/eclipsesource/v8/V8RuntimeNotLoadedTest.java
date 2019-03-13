/**
 * *****************************************************************************
 * Copyright (c) 2014 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 * ****************************************************************************
 */
package com.eclipsesource.v8;


import java.net.URLClassLoader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;


// A separate class loaded must be used since we don't want these tests to interfere
// with other tests.
@RunWith(V8RuntimeNotLoadedTest.SeparateClassloaderTestRunner.class)
public class V8RuntimeNotLoadedTest {
    private static final String JAVA_LIBRARY_PATH = "java.library.path";

    private String existingLibraryPath;

    private static final String skipMessage = "Skipped test (not implemented for Android)";

    @Test
    public void testJ2V8NotEnabled() {
        Assume.assumeFalse(V8RuntimeNotLoadedTest.skipMessage, V8RuntimeNotLoadedTest.skipTest());// conditional skip

        Assert.assertFalse(V8.isLoaded());
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testJ2V8CannotCreateRuntime() {
        Assume.assumeFalse(V8RuntimeNotLoadedTest.skipMessage, V8RuntimeNotLoadedTest.skipTest());// conditional skip

        String oldValue = System.getProperty("os.arch");
        System.setProperty("os.arch", "unknown");
        try {
            V8.createV8Runtime();
        } catch (UnsatisfiedLinkError ex) {
            Assert.assertEquals("Unsupported arch: unknown", ex.getMessage());
            throw ex;
        } finally {
            System.setProperty("os.arch", oldValue);
        }
    }

    public static class SeparateClassloaderTestRunner extends BlockJUnit4ClassRunner {
        public SeparateClassloaderTestRunner(final Class<?> clazz) throws InitializationError {
            super(V8RuntimeNotLoadedTest.SeparateClassloaderTestRunner.getFromTestClassloader(clazz));
        }

        private static Class<?> getFromTestClassloader(final Class<?> clazz) throws InitializationError {
            try {
                if (V8RuntimeNotLoadedTest.skipTest())
                    return clazz;

                ClassLoader testClassLoader = new V8RuntimeNotLoadedTest.SeparateClassloaderTestRunner.TestClassLoader();
                return Class.forName(clazz.getName(), true, testClassLoader);
            } catch (ClassNotFoundException e) {
                throw new InitializationError(e);
            }
        }

        public static class TestClassLoader extends URLClassLoader {
            public TestClassLoader() {
                // TODO: this crashes on Android (see: https://stackoverflow.com/q/31920245)
                super(((URLClassLoader) (ClassLoader.getSystemClassLoader())).getURLs());
            }

            @Override
            public Class<?> loadClass(final String name) throws ClassNotFoundException {
                if (name.startsWith("com.eclipsesource.v8")) {
                    return super.findClass(name);
                }
                return super.loadClass(name);
            }
        }
    }
}

