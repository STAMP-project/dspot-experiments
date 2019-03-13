/**
 * Copyright (c) 2011-2019 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.core.impl.logging;


import io.vertx.core.logging.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Thomas Segismont
 */
public class LoggingBackendSelectionTest {
    private ClassLoader originalTccl;

    private LoggingBackendSelectionTest.TestClassLoader testClassLoader;

    @Test
    public void syspropPriority() throws Exception {
        System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, "io.vertx.core.logging.Log4j2LogDelegateFactory");
        Assert.assertEquals("Log4j2", loggingBackend());
    }

    @Test
    public void vertxJulFilePriority() throws Exception {
        Assert.assertEquals("JUL", loggingBackend());
    }

    @Test
    public void SLF4JPriority() throws Exception {
        testClassLoader.hideVertxJulFile = true;
        Assert.assertEquals("SLF4J", loggingBackend());
    }

    @Test
    public void Log4jPriority() throws Exception {
        testClassLoader.hideVertxJulFile = true;
        testClassLoader.hiddenPackages.add("org.slf4j");
        Assert.assertEquals("Log4j", loggingBackend());
    }

    @Test
    public void Log4j2Priority() throws Exception {
        testClassLoader.hideVertxJulFile = true;
        testClassLoader.hiddenPackages.add("org.slf4j");
        testClassLoader.hiddenPackages.add("org.apache.log4j");
        Assert.assertEquals("Log4j2", loggingBackend());
    }

    @Test
    public void JULDefault() throws Exception {
        testClassLoader.hideVertxJulFile = true;
        testClassLoader.hiddenPackages.add("org.slf4j");
        testClassLoader.hiddenPackages.add("org.apache.log4j");
        testClassLoader.hiddenPackages.add("org.apache.logging");
        Assert.assertEquals("JUL", loggingBackend());
    }

    private static class TestClassLoader extends ClassLoader {
        boolean hideVertxJulFile;

        Set<String> hiddenPackages = new HashSet<>();

        TestClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (hiddenPackages.stream().anyMatch(name::startsWith)) {
                throw new ClassNotFoundException(name);
            }
            if ((name.startsWith("io.vertx.core.impl.logging")) || (name.startsWith("io.vertx.core.logging"))) {
                URL url = getResource(((name.replace('.', '/')) + ".class"));
                if (url == null) {
                    throw new ClassNotFoundException(name);
                }
                try {
                    byte[] bytes = Files.readAllBytes(FileSystems.getDefault().getPath(url.getPath()));
                    Class<?> clazz = defineClass(name, bytes, 0, bytes.length);
                    if (resolve) {
                        resolveClass(clazz);
                    }
                    return clazz;
                } catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
            return super.loadClass(name, resolve);
        }

        @Override
        public URL getResource(String name) {
            if ((hideVertxJulFile) && ("vertx-default-jul-logging.properties".equals(name))) {
                return null;
            }
            return super.getResource(name);
        }
    }
}

