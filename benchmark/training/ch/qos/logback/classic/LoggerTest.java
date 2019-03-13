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
package ch.qos.logback.classic;


import Level.ALL;
import Level.DEBUG;
import Level.INFO;
import Level.OFF;
import Logger.ROOT_LOGGER_NAME;
import Status.WARN;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import ch.qos.logback.core.status.Status;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


public class LoggerTest {
    LoggerContext lc = new LoggerContext();

    Logger root = lc.getLogger(ROOT_LOGGER_NAME);

    Logger loggerTest = lc.getLogger(LoggerTest.class);

    ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();

    @Test
    public void smoke() {
        ListAppender<ILoggingEvent> listAppender = new ListAppender<ILoggingEvent>();
        listAppender.start();
        root.addAppender(listAppender);
        Logger logger = lc.getLogger(LoggerTest.class);
        Assert.assertEquals(0, listAppender.list.size());
        logger.debug("hello");
        Assert.assertEquals(1, listAppender.list.size());
    }

    @Test
    public void testNoStart() {
        // listAppender.start();
        listAppender.setContext(lc);
        root.addAppender(listAppender);
        Logger logger = lc.getLogger(LoggerTest.class);
        logger.debug("hello");
        List<Status> statusList = lc.getStatusManager().getCopyOfStatusList();
        Status s0 = statusList.get(0);
        Assert.assertEquals(WARN, s0.getLevel());
        Assert.assertTrue(s0.getMessage().startsWith("Attempted to append to non started"));
    }

    @Test
    public void testAdditive() {
        listAppender.start();
        root.addAppender(listAppender);
        loggerTest.addAppender(listAppender);
        loggerTest.setAdditive(false);
        loggerTest.debug("hello");
        // 1 instead of two, since logger is not additive
        Assert.assertEquals(1, listAppender.list.size());
    }

    @Test
    public void testRootLogger() {
        Logger logger = ((Logger) (LoggerFactory.getLogger(ROOT_LOGGER_NAME)));
        LoggerContext lc = logger.getLoggerContext();
        Assert.assertNotNull("Returned logger is null", logger);
        Assert.assertEquals("Return logger isn't named root", logger.getName(), ROOT_LOGGER_NAME);
        Assert.assertTrue("logger instances should be indentical", (logger == (lc.root)));
    }

    @Test
    public void testBasicFiltering() throws Exception {
        listAppender.start();
        root.addAppender(listAppender);
        root.setLevel(INFO);
        loggerTest.debug("x");
        Assert.assertEquals(0, listAppender.list.size());
        loggerTest.info("x");
        loggerTest.warn("x");
        loggerTest.error("x");
        Assert.assertEquals(3, listAppender.list.size());
    }

    @Test
    public void innerClass_I() {
        root.setLevel(DEBUG);
        Logger a = lc.getLogger("a");
        a.setLevel(INFO);
        Logger a_b = lc.getLogger("a$b");
        Assert.assertEquals(INFO, a_b.getEffectiveLevel());
    }

    @Test
    public void innerClass_II() {
        root.setLevel(DEBUG);
        Logger a = lc.getLogger(this.getClass());
        a.setLevel(INFO);
        Logger a_b = lc.getLogger(new LoggerTest.Inner().getClass());
        Assert.assertEquals(INFO, a_b.getEffectiveLevel());
    }

    class Inner {}

    @Test
    public void testEnabled_All() throws Exception {
        root.setLevel(ALL);
        checkLevelThreshold(loggerTest, ALL);
    }

    @Test
    public void testEnabled_Debug() throws Exception {
        root.setLevel(DEBUG);
        checkLevelThreshold(loggerTest, DEBUG);
    }

    @Test
    public void testEnabled_Info() throws Exception {
        root.setLevel(INFO);
        checkLevelThreshold(loggerTest, INFO);
    }

    @Test
    public void testEnabledX_Warn() throws Exception {
        root.setLevel(Level.WARN);
        checkLevelThreshold(loggerTest, Level.WARN);
    }

    @Test
    public void testEnabledX_Off() throws Exception {
        root.setLevel(OFF);
        checkLevelThreshold(loggerTest, OFF);
    }

    @Test
    public void setRootLevelToNull() {
        try {
            root.setLevel(null);
            Assert.fail("The level of the root logger should not be settable to null");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void setLevelToNull_A() {
        loggerTest.setLevel(null);
        Assert.assertEquals(root.getEffectiveLevel(), loggerTest.getEffectiveLevel());
    }

    @Test
    public void setLevelToNull_B() {
        loggerTest.setLevel(DEBUG);
        loggerTest.setLevel(null);
        Assert.assertEquals(root.getEffectiveLevel(), loggerTest.getEffectiveLevel());
    }

    @Test
    public void setLevelToNull_LBCLASSIC_91() {
        loggerTest.setLevel(DEBUG);
        ch.qos.logback.classic.Logger child = lc.getLogger(((loggerTest.getName()) + ".child"));
        loggerTest.setLevel(null);
        Assert.assertEquals(root.getEffectiveLevel(), loggerTest.getEffectiveLevel());
        Assert.assertEquals(root.getEffectiveLevel(), child.getEffectiveLevel());
    }
}

