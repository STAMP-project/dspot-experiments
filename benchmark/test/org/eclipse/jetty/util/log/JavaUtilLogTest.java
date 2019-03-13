/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.util.log;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.Level;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class JavaUtilLogTest {
    private static Handler[] originalHandlers;

    private static CapturingJULHandler jul;

    @Test
    public void testNamedLogger() {
        JavaUtilLogTest.jul.clear();
        JavaUtilLog log = new JavaUtilLog("test");
        log.info("Info test");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test|Info test");
        JavaUtilLog loglong = new JavaUtilLog("test.a.long.name");
        loglong.info("Long test");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.a.long.name|Long test");
    }

    @Test
    public void testDebugOutput() {
        JavaUtilLogTest.jul.clear();
        // Common Throwable (for test)
        Throwable th = new Throwable("Message");
        // Capture raw string form
        StringWriter tout = new StringWriter();
        th.printStackTrace(new PrintWriter(tout));
        String ths = tout.toString();
        // Tests
        JavaUtilLog log = new JavaUtilLog("test.de.bug");
        setJulLevel("test.de.bug", Level.FINE);
        log.debug("Simple debug");
        log.debug("Debug with {} parameter", 1);
        log.debug("Debug with {} {} parameters", 2, "spiffy");
        log.debug("Debug with throwable", th);
        log.debug(th);
        // jul.dump();
        JavaUtilLogTest.jul.assertContainsLine("FINE|test.de.bug|Simple debug");
        JavaUtilLogTest.jul.assertContainsLine("FINE|test.de.bug|Debug with 1 parameter");
        JavaUtilLogTest.jul.assertContainsLine("FINE|test.de.bug|Debug with 2 spiffy parameters");
        JavaUtilLogTest.jul.assertContainsLine("FINE|test.de.bug|Debug with throwable");
        JavaUtilLogTest.jul.assertContainsLine(ths);
    }

    @Test
    public void testInfoOutput() {
        JavaUtilLogTest.jul.clear();
        // Common Throwable (for test)
        Throwable th = new Throwable("Message");
        // Capture raw string form
        StringWriter tout = new StringWriter();
        th.printStackTrace(new PrintWriter(tout));
        String ths = tout.toString();
        // Tests
        JavaUtilLog log = new JavaUtilLog("test.in.fo");
        setJulLevel("test.in.fo", Level.INFO);
        log.info("Simple info");
        log.info("Info with {} parameter", 1);
        log.info("Info with {} {} parameters", 2, "spiffy");
        log.info("Info with throwable", th);
        log.info(th);
        // jul.dump();
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.in.fo|Simple info");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.in.fo|Info with 1 parameter");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.in.fo|Info with 2 spiffy parameters");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.in.fo|Info with throwable");
        JavaUtilLogTest.jul.assertContainsLine(ths);
    }

    @Test
    public void testWarnOutput() {
        JavaUtilLogTest.jul.clear();
        // Common Throwable (for test)
        Throwable th = new Throwable("Message");
        // Capture raw string form
        StringWriter tout = new StringWriter();
        th.printStackTrace(new PrintWriter(tout));
        String ths = tout.toString();
        // Tests
        JavaUtilLog log = new JavaUtilLog("test.wa.rn");
        setJulLevel("test.wa.rn", Level.WARNING);
        log.warn("Simple warn");
        log.warn("Warn with {} parameter", 1);
        log.warn("Warn with {} {} parameters", 2, "spiffy");
        log.warn("Warn with throwable", th);
        log.warn(th);
        // jul.dump();
        JavaUtilLogTest.jul.assertContainsLine("WARNING|test.wa.rn|Simple warn");
        JavaUtilLogTest.jul.assertContainsLine("WARNING|test.wa.rn|Warn with 1 parameter");
        JavaUtilLogTest.jul.assertContainsLine("WARNING|test.wa.rn|Warn with 2 spiffy parameters");
        JavaUtilLogTest.jul.assertContainsLine("WARNING|test.wa.rn|Warn with throwable");
        JavaUtilLogTest.jul.assertContainsLine(ths);
    }

    @Test
    public void testFormattingWithNulls() {
        JavaUtilLogTest.jul.clear();
        JavaUtilLog log = new JavaUtilLog("test.nu.ll");
        setJulLevel("test.nu.ll", Level.INFO);
        log.info("Testing info(msg,null,null) - {} {}", "arg0", "arg1");
        log.info("Testing info(msg,null,null) - {}/{}", null, null);
        log.info("Testing info(msg,null,null) > {}", null, null);
        log.info("Testing info(msg,null,null)", null, null);
        log.info(null, "Testing", "info(null,arg0,arg1)");
        log.info(null, null, null);
        // jul.dump();
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.nu.ll|Testing info(msg,null,null) - null/null");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.nu.ll|Testing info(msg,null,null) > null null");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.nu.ll|Testing info(msg,null,null) null null");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.nu.ll|null Testing info(null,arg0,arg1)");
        JavaUtilLogTest.jul.assertContainsLine("INFO|test.nu.ll|null null null");
    }

    @Test
    public void testIsDebugEnabled() {
        JavaUtilLog log = new JavaUtilLog("test.legacy");
        setJulLevel("test.legacy", Level.ALL);
        MatcherAssert.assertThat("log.level(all).isDebugEnabled", log.isDebugEnabled(), Matchers.is(true));
        setJulLevel("test.legacy", Level.FINEST);
        MatcherAssert.assertThat("log.level(finest).isDebugEnabled", log.isDebugEnabled(), Matchers.is(true));
        setJulLevel("test.legacy", Level.FINER);
        MatcherAssert.assertThat("log.level(finer).isDebugEnabled", log.isDebugEnabled(), Matchers.is(true));
        setJulLevel("test.legacy", Level.FINE);
        MatcherAssert.assertThat("log.level(fine).isDebugEnabled", log.isDebugEnabled(), Matchers.is(true));
        setJulLevel("test.legacy", Level.INFO);
        MatcherAssert.assertThat("log.level(info).isDebugEnabled", log.isDebugEnabled(), Matchers.is(false));
        setJulLevel("test.legacy", Level.WARNING);
        MatcherAssert.assertThat("log.level(warn).isDebugEnabled", log.isDebugEnabled(), Matchers.is(false));
        log.setDebugEnabled(true);
        MatcherAssert.assertThat("log.isDebugEnabled", log.isDebugEnabled(), Matchers.is(true));
        log.setDebugEnabled(false);
        MatcherAssert.assertThat("log.isDebugEnabled", log.isDebugEnabled(), Matchers.is(false));
    }
}

