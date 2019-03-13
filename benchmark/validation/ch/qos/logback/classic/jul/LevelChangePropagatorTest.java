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
package ch.qos.logback.classic.jul;


import Level.DEBUG;
import Level.INFO;
import Level.TRACE;
import Logger.ROOT_LOGGER_NAME;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.testUtil.RandomUtil;
import java.util.logging.Level;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class LevelChangePropagatorTest {
    int rand = RandomUtil.getPositiveInt();

    LoggerContext loggerContext = new LoggerContext();

    LevelChangePropagator levelChangePropagator = new LevelChangePropagator();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void smoke() {
        checkLevelChange("a", INFO);
        checkLevelChange("a.b", DEBUG);
    }

    @Test
    public void root() {
        checkLevelChange(ROOT_LOGGER_NAME, TRACE);
    }

    // see http://jira.qos.ch/browse/LBCLASSIC-256
    @Test
    public void gc() {
        Logger logger = loggerContext.getLogger(("gc" + (rand)));
        logger.setLevel(INFO);
        // invoke GC so that the relevant julLogger can be garbage collected.
        System.gc();
        java.util.logging.Logger julLogger = JULHelper.asJULLogger(logger);
        Level julLevel = JULHelper.asJULLevel(INFO);
        Assert.assertEquals(julLevel, julLogger.getLevel());
    }

    @Test
    public void julHelperAsJulLevelRejectsNull() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Unexpected level [null]");
        JULHelper.asJULLevel(null);
    }

    @Test
    public void settingLevelToNullGetsParentLevel() {
        // first set level of "a" (the parent) to DEBUG
        Logger parent = loggerContext.getLogger("a");
        parent.setLevel(DEBUG);
        // then set level of "a.b" (child logger of a) to null
        // for b to inherit its parent's level
        Logger child = loggerContext.getLogger("a.b");
        child.setLevel(INFO);
        child.setLevel(null);
        Assert.assertEquals(parent.getEffectiveLevel(), child.getEffectiveLevel());
        Assert.assertEquals(DEBUG, child.getEffectiveLevel());
    }
}

