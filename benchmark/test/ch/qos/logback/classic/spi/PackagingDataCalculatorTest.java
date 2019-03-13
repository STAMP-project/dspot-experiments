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
package ch.qos.logback.classic.spi;


import ch.qos.logback.classic.util.TestHelper;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import org.junit.Test;


public class PackagingDataCalculatorTest {
    @Test
    public void smoke() throws Exception {
        Throwable t = new Throwable("x");
        ThrowableProxy tp = new ThrowableProxy(t);
        PackagingDataCalculator pdc = tp.getPackagingDataCalculator();
        pdc.calculate(tp);
        verify(tp);
        tp.fullDump();
    }

    @Test
    public void nested() throws Exception {
        Throwable t = TestHelper.makeNestedException(3);
        ThrowableProxy tp = new ThrowableProxy(t);
        PackagingDataCalculator pdc = tp.getPackagingDataCalculator();
        pdc.calculate(tp);
        verify(tp);
    }

    // Test http://jira.qos.ch/browse/LBCLASSIC-125
    @Test
    public void noClassDefFoundError_LBCLASSIC_125Test() throws MalformedURLException {
        ClassLoader cl = ((URLClassLoader) (makeBogusClassLoader()));
        Thread.currentThread().setContextClassLoader(cl);
        Throwable t = new Throwable("x");
        ThrowableProxy tp = new ThrowableProxy(t);
        StackTraceElementProxy[] stepArray = tp.getStackTraceElementProxyArray();
        StackTraceElement bogusSTE = new StackTraceElement("com.Bogus", "myMethod", "myFile", 12);
        stepArray[0] = new StackTraceElementProxy(bogusSTE);
        PackagingDataCalculator pdc = tp.getPackagingDataCalculator();
        // NoClassDefFoundError should be caught
        pdc.calculate(tp);
    }
}

