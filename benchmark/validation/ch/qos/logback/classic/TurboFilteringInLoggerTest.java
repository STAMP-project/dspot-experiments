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
import Level.ERROR;
import Level.INFO;
import Level.OFF;
import Level.WARN;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


public class TurboFilteringInLoggerTest {
    static final String BLUE = "BLUE";

    LoggerContext context;

    Logger logger;

    Marker blueMarker = MarkerFactory.getMarker(TurboFilteringInLoggerTest.BLUE);

    @Test
    public void testIsDebugEnabledWithYesFilter() {
        addYesFilter();
        logger.setLevel(INFO);
        Assert.assertTrue(logger.isDebugEnabled());
    }

    @Test
    public void testIsInfoEnabledWithYesFilter() {
        addYesFilter();
        logger.setLevel(WARN);
        Assert.assertTrue(logger.isInfoEnabled());
    }

    @Test
    public void testIsWarnEnabledWithYesFilter() {
        addYesFilter();
        logger.setLevel(ERROR);
        Assert.assertTrue(logger.isWarnEnabled());
    }

    @Test
    public void testIsErrorEnabledWithYesFilter() {
        addYesFilter();
        logger.setLevel(OFF);
        Assert.assertTrue(logger.isErrorEnabled());
    }

    @Test
    public void testIsEnabledForWithYesFilter() {
        addYesFilter();
        logger.setLevel(ERROR);
        Assert.assertTrue(logger.isEnabledFor(INFO));
    }

    @Test
    public void testIsEnabledForWithNoFilter() {
        addNoFilter();
        logger.setLevel(DEBUG);
        Assert.assertFalse(logger.isEnabledFor(INFO));
    }

    @Test
    public void testIsDebugEnabledWithNoFilter() {
        addNoFilter();
        logger.setLevel(DEBUG);
        Assert.assertFalse(logger.isDebugEnabled());
    }

    @Test
    public void testIsInfoEnabledWithNoFilter() {
        addNoFilter();
        logger.setLevel(DEBUG);
        Assert.assertFalse(logger.isInfoEnabled());
    }

    @Test
    public void testIsWarnEnabledWithNoFilter() {
        addNoFilter();
        logger.setLevel(DEBUG);
        Assert.assertFalse(logger.isWarnEnabled());
    }

    @Test
    public void testIsErrorEnabledWithNoFilter() {
        addNoFilter();
        logger.setLevel(DEBUG);
        Assert.assertFalse(logger.isErrorEnabled());
    }

    @Test
    public void testIsErrorEnabledWithAcceptBlueFilter() {
        addAcceptBLUEFilter();
        logger.setLevel(ERROR);
        Assert.assertTrue(logger.isDebugEnabled(blueMarker));
    }

    @Test
    public void testIsErrorEnabledWithDenyBlueFilter() {
        addDenyBLUEFilter();
        logger.setLevel(ALL);
        Assert.assertFalse(logger.isDebugEnabled(blueMarker));
    }

    @Test
    public void testLoggingContextReset() {
        addYesFilter();
        Assert.assertNotNull(context.getTurboFilterList().get(0));
        context.reset();
        Assert.assertEquals(0, context.getTurboFilterList().size());
    }
}

