/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.util;


import CoreConstants.SAFE_JORAN_CONFIGURATION;
import CoreConstants.STATUS_LISTENER_CLASS_KEY;
import CoreConstants.SYSOUT;
import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.ClassicTestConstants;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.LogbackException;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.testUtil.TrivialStatusListener;
import ch.qos.logback.core.util.Loader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class ContextInitializerTest {
    LoggerContext loggerContext = new LoggerContext();

    Logger root = loggerContext.getLogger(ROOT_LOGGER_NAME);

    @Test
    public void autoConfigFromSystemProperties() throws JoranException {
        doAutoConfigFromSystemProperties(((ClassicTestConstants.INPUT_PREFIX) + "autoConfig.xml"));
        doAutoConfigFromSystemProperties("autoConfigAsResource.xml");
        // test passing a URL. note the relative path syntax with file:src/test/...
        doAutoConfigFromSystemProperties((("file:" + (ClassicTestConstants.INPUT_PREFIX)) + "autoConfig.xml"));
    }

    @Test
    public void autoConfigFromServiceLoaderJDK6andAbove() throws Exception {
        Assume.assumeTrue((!(ContextInitializerTest.isJDK5())));
        setupMockServiceLoader();
        Assert.assertNull(MockConfigurator.context);
        autoConfig();
        Assert.assertNotNull(MockConfigurator.context);
        Assert.assertSame(loggerContext, MockConfigurator.context);
    }

    @Test
    public void autoConfigFromServiceLoaderJDK5() throws Exception {
        Assume.assumeTrue(ContextInitializerTest.isJDK5());
        setupMockServiceLoader();
        Assert.assertNull(MockConfigurator.context);
        autoConfig();
        Assert.assertNull(MockConfigurator.context);
    }

    @Test
    public void autoStatusListener() throws JoranException {
        System.setProperty(STATUS_LISTENER_CLASS_KEY, TrivialStatusListener.class.getName());
        List<StatusListener> statusListenerList = loggerContext.getStatusManager().getCopyOfStatusListenerList();
        Assert.assertEquals(0, statusListenerList.size());
        doAutoConfigFromSystemProperties(((ClassicTestConstants.INPUT_PREFIX) + "autoConfig.xml"));
        statusListenerList = loggerContext.getStatusManager().getCopyOfStatusListenerList();
        Assert.assertTrue(((statusListenerList.size()) + " should be 1"), ((statusListenerList.size()) == 1));
        // LOGBACK-767
        TrivialStatusListener tsl = ((TrivialStatusListener) (statusListenerList.get(0)));
        Assert.assertTrue("expecting at least one event in list", ((tsl.list.size()) > 0));
    }

    @Test
    public void autoOnConsoleStatusListener() throws JoranException {
        System.setProperty(STATUS_LISTENER_CLASS_KEY, SYSOUT);
        List<StatusListener> sll = loggerContext.getStatusManager().getCopyOfStatusListenerList();
        Assert.assertEquals(0, sll.size());
        doAutoConfigFromSystemProperties(((ClassicTestConstants.INPUT_PREFIX) + "autoConfig.xml"));
        sll = loggerContext.getStatusManager().getCopyOfStatusListenerList();
        Assert.assertTrue(((sll.size()) + " should be 1"), ((sll.size()) == 1));
    }

    @Test
    public void shouldConfigureFromXmlFile() throws JoranException, MalformedURLException {
        LoggerContext loggerContext = new LoggerContext();
        ContextInitializer initializer = new ContextInitializer(loggerContext);
        Assert.assertNull(loggerContext.getObject(SAFE_JORAN_CONFIGURATION));
        URL configurationFileUrl = Loader.getResource("BOO_logback-test.xml", Thread.currentThread().getContextClassLoader());
        initializer.configureByResource(configurationFileUrl);
        Assert.assertNotNull(loggerContext.getObject(SAFE_JORAN_CONFIGURATION));
    }

    // @Test
    // public void shouldConfigureFromGroovyScript() throws MalformedURLException, JoranException {
    // LoggerContext loggerContext = new LoggerContext();
    // ContextInitializer initializer = new ContextInitializer(loggerContext);
    // assertNull(loggerContext.getObject(CoreConstants.CONFIGURATION_WATCH_LIST));
    // 
    // URL configurationFileUrl = Loader.getResource("test.groovy", Thread.currentThread().getContextClassLoader());
    // initializer.configureByResource(configurationFileUrl);
    // 
    // assertNotNull(loggerContext.getObject(CoreConstants.CONFIGURATION_WATCH_LIST));
    // }
    @Test
    public void shouldThrowExceptionIfUnexpectedConfigurationFileExtension() throws JoranException {
        LoggerContext loggerContext = new LoggerContext();
        ContextInitializer initializer = new ContextInitializer(loggerContext);
        URL configurationFileUrl = Loader.getResource("README.txt", Thread.currentThread().getContextClassLoader());
        try {
            initializer.configureByResource(configurationFileUrl);
            Assert.fail("Should throw LogbackException");
        } catch (LogbackException expectedException) {
            // pass
        }
    }

    static class WrappedClassLoader extends ClassLoader {
        final ClassLoader delegate;

        public WrappedClassLoader(ClassLoader delegate) {
            super();
            this.delegate = delegate;
        }

        public Class<?> loadClass(String name) throws ClassNotFoundException {
            return delegate.loadClass(name);
        }

        public URL getResource(String name) {
            return delegate.getResource(name);
        }

        public Enumeration<URL> getResources(String name) throws IOException {
            return delegate.getResources(name);
        }

        public InputStream getResourceAsStream(String name) {
            return delegate.getResourceAsStream(name);
        }

        public void setDefaultAssertionStatus(boolean enabled) {
            delegate.setDefaultAssertionStatus(enabled);
        }

        public void setPackageAssertionStatus(String packageName, boolean enabled) {
            delegate.setPackageAssertionStatus(packageName, enabled);
        }

        public void setClassAssertionStatus(String className, boolean enabled) {
            delegate.setClassAssertionStatus(className, enabled);
        }

        public void clearAssertionStatus() {
            delegate.clearAssertionStatus();
        }
    }
}

