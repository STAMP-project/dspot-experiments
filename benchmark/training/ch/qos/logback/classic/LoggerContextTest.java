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


import CoreConstants.EVALUATOR_MAP;
import CoreConstants.RFA_FILENAME_PATTERN_COLLISION_MAP;
import Level.DEBUG;
import Level.INFO;
import Level.TRACE;
import Level.WARN;
import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.turbo.NOPTurboFilter;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.status.StatusManager;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class LoggerContextTest {
    LoggerContext lc;

    @Test
    public void testRootGetLogger() {
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        Assert.assertEquals(DEBUG, root.getLevel());
        Assert.assertEquals(DEBUG, root.getEffectiveLevel());
    }

    @Test
    public void testLoggerX() {
        Logger x = lc.getLogger("x");
        Assert.assertNotNull(x);
        Assert.assertEquals("x", x.getName());
        Assert.assertNull(x.getLevel());
        Assert.assertEquals(DEBUG, x.getEffectiveLevel());
    }

    @Test
    public void testNull() {
        try {
            lc.getLogger(((String) (null)));
            Assert.fail("null should cause an exception");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testEmpty() {
        Logger empty = lc.getLogger("");
        LoggerTestHelper.assertNameEquals(empty, "");
        LoggerTestHelper.assertLevels(null, empty, DEBUG);
        Logger dot = lc.getLogger(".");
        LoggerTestHelper.assertNameEquals(dot, ".");
        // LoggerTestHelper.assertNameEquals(dot.parent, "");
        // LoggerTestHelper.assertNameEquals(dot.parent.parent, "root");
        // assertNull(dot.parent.parent.parent);
        LoggerTestHelper.assertLevels(null, dot, DEBUG);
        Assert.assertEquals(3, lc.getLoggerList().size());
    }

    @Test
    public void testDotDot() {
        Logger dotdot = lc.getLogger("..");
        Assert.assertEquals(4, lc.getLoggerList().size());
        LoggerTestHelper.assertNameEquals(dotdot, "..");
        // LoggerTestHelper.assertNameEquals(dotdot.parent, ".");
        // LoggerTestHelper.assertNameEquals(dotdot.parent.parent, "");
        // LoggerTestHelper.assertNameEquals(dotdot.parent.parent.parent, "root");
    }

    @Test
    public void testLoggerXY() {
        Assert.assertEquals(1, lc.getLoggerList().size());
        Logger xy = lc.getLogger("x.y");
        Assert.assertEquals(3, instanceCount());
        LoggerTestHelper.assertNameEquals(xy, "x.y");
        LoggerTestHelper.assertLevels(null, xy, DEBUG);
        Logger x = lc.getLogger("x");
        Assert.assertEquals(3, instanceCount());
        Logger xy2 = lc.getLogger("x.y");
        Assert.assertEquals(xy, xy2);
        Logger x2 = lc.getLogger("x");
        Assert.assertEquals(x, x2);
        Assert.assertEquals(3, instanceCount());
    }

    @Test
    public void testLoggerMultipleChildren() {
        Assert.assertEquals(1, instanceCount());
        Logger xy0 = lc.getLogger("x.y0");
        LoggerTestHelper.assertNameEquals(xy0, "x.y0");
        Logger xy1 = lc.getLogger("x.y1");
        LoggerTestHelper.assertNameEquals(xy1, "x.y1");
        LoggerTestHelper.assertLevels(null, xy0, DEBUG);
        LoggerTestHelper.assertLevels(null, xy1, DEBUG);
        Assert.assertEquals(4, instanceCount());
        for (int i = 0; i < 100; i++) {
            Logger xy_i = lc.getLogger(("x.y" + i));
            LoggerTestHelper.assertNameEquals(xy_i, ("x.y" + i));
            LoggerTestHelper.assertLevels(null, xy_i, DEBUG);
        }
        Assert.assertEquals(102, instanceCount());
    }

    @Test
    public void testMultiLevel() {
        Logger wxyz = lc.getLogger("w.x.y.z");
        LoggerTestHelper.assertNameEquals(wxyz, "w.x.y.z");
        LoggerTestHelper.assertLevels(null, wxyz, DEBUG);
        Logger wx = lc.getLogger("w.x");
        wx.setLevel(INFO);
        LoggerTestHelper.assertNameEquals(wx, "w.x");
        LoggerTestHelper.assertLevels(INFO, wx, INFO);
        LoggerTestHelper.assertLevels(null, lc.getLogger("w.x.y"), INFO);
        LoggerTestHelper.assertLevels(null, wxyz, INFO);
    }

    @Test
    public void testStatusWithUnconfiguredContext() {
        Logger logger = lc.getLogger(LoggerContextTest.class);
        for (int i = 0; i < 3; i++) {
            logger.debug("test");
        }
        logger = lc.getLogger("x.y.z");
        for (int i = 0; i < 3; i++) {
            logger.debug("test");
        }
        StatusManager sm = lc.getStatusManager();
        Assert.assertTrue("StatusManager has recieved too many messages", ((sm.getCount()) == 1));
    }

    @Test
    public void resetTest() {
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        Logger a = lc.getLogger("a");
        Logger ab = lc.getLogger("a.b");
        ab.setLevel(WARN);
        root.setLevel(INFO);
        lc.reset();
        Assert.assertEquals(DEBUG, root.getEffectiveLevel());
        Assert.assertTrue(root.isDebugEnabled());
        Assert.assertEquals(DEBUG, a.getEffectiveLevel());
        Assert.assertEquals(DEBUG, ab.getEffectiveLevel());
        Assert.assertEquals(DEBUG, root.getLevel());
        Assert.assertNull(a.getLevel());
        Assert.assertNull(ab.getLevel());
    }

    // http://jira.qos.ch/browse/LBCLASSIC-89
    @Test
    public void turboFilterStopOnReset() {
        NOPTurboFilter nopTF = new NOPTurboFilter();
        start();
        lc.addTurboFilter(nopTF);
        Assert.assertTrue(isStarted());
        lc.reset();
        Assert.assertFalse(isStarted());
    }

    @Test
    public void resetTest_LBCORE_104() {
        lc.putProperty("keyA", "valA");
        lc.putObject("keyA", "valA");
        Assert.assertEquals("valA", lc.getProperty("keyA"));
        Assert.assertEquals("valA", lc.getObject("keyA"));
        lc.reset();
        Assert.assertNull(lc.getProperty("keyA"));
        Assert.assertNull(lc.getObject("keyA"));
    }

    @Test
    public void loggerNameEndingInDotOrDollarShouldWork() {
        {
            String loggerName = "toto.x.";
            Logger logger = lc.getLogger(loggerName);
            Assert.assertEquals(loggerName, logger.getName());
        }
        {
            String loggerName = "toto.x$";
            Logger logger = lc.getLogger(loggerName);
            Assert.assertEquals(loggerName, logger.getName());
        }
    }

    @Test
    public void levelResetTest() {
        Logger root = lc.getLogger(ROOT_LOGGER_NAME);
        root.setLevel(TRACE);
        Assert.assertTrue(root.isTraceEnabled());
        lc.reset();
        Assert.assertFalse(root.isTraceEnabled());
        Assert.assertTrue(root.isDebugEnabled());
    }

    @Test
    public void evaluatorMapPostReset() {
        lc.reset();
        Assert.assertNotNull(lc.getObject(EVALUATOR_MAP));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void collisionMapsPostReset() {
        lc.reset();
        Map<String, String> fileCollisions = ((Map<String, String>) (lc.getObject(CoreConstants.FA_FILENAME_COLLISION_MAP)));
        Assert.assertNotNull(fileCollisions);
        Assert.assertTrue(fileCollisions.isEmpty());
        Map<String, FileNamePattern> filenamePatternCollisionMap = ((Map<String, FileNamePattern>) (lc.getObject(RFA_FILENAME_PATTERN_COLLISION_MAP)));
        Assert.assertNotNull(filenamePatternCollisionMap);
        Assert.assertTrue(filenamePatternCollisionMap.isEmpty());
    }

    // http://jira.qos.ch/browse/LOGBACK-142
    @Test
    public void concurrentModification() {
        final int runLen = 100;
        Thread thread = new Thread(new Runnable() {
            public void run() {
                for (int i = 0; i < runLen; i++) {
                    lc.getLogger(("a" + i));
                    Thread.yield();
                }
            }
        });
        thread.start();
        for (int i = 0; i < runLen; i++) {
            lc.putProperty(("a" + i), "val");
            Thread.yield();
        }
    }
}

