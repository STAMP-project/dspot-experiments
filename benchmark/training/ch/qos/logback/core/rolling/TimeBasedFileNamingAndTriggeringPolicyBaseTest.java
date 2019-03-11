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
package ch.qos.logback.core.rolling;


import Status.ERROR;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.testUtil.StatusChecker;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Ceki G&uuml;lc&uuml;
 */
public class TimeBasedFileNamingAndTriggeringPolicyBaseTest {
    static long MILLIS_IN_MINUTE = 60 * 1000;

    static long MILLIS_IN_HOUR = 60 * (TimeBasedFileNamingAndTriggeringPolicyBaseTest.MILLIS_IN_MINUTE);

    Context context = new ContextBase();

    RollingFileAppender<Object> rfa = new RollingFileAppender<Object>();

    TimeBasedRollingPolicy<Object> tbrp = new TimeBasedRollingPolicy<Object>();

    DefaultTimeBasedFileNamingAndTriggeringPolicy<Object> timeBasedFNATP = new DefaultTimeBasedFileNamingAndTriggeringPolicy<Object>();

    @Test
    public void singleDate() {
        // Tuesday December 20th 17:59:01 CET 2011
        long startTime = 1324400341553L;
        tbrp.setFileNamePattern("foo-%d{yyyy-MM'T'mm}.log");
        tbrp.start();
        timeBasedFNATP.setCurrentTime(startTime);
        timeBasedFNATP.start();
        timeBasedFNATP.setCurrentTime((startTime + (TimeBasedFileNamingAndTriggeringPolicyBaseTest.MILLIS_IN_MINUTE)));
        timeBasedFNATP.isTriggeringEvent(null, null);
        String elapsedPeriodsFileName = timeBasedFNATP.getElapsedPeriodsFileName();
        Assert.assertEquals("foo-2011-12T59.log", elapsedPeriodsFileName);
    }

    // see "log rollover should be configurable using %d multiple times in file name pattern"
    // http://jira.qos.ch/browse/LBCORE-242
    @Test
    public void multiDate() {
        // Tuesday December 20th 17:59:01 CET 2011
        long startTime = 1324400341553L;
        tbrp.setFileNamePattern("foo-%d{yyyy-MM, AUX}/%d{mm}.log");
        tbrp.start();
        timeBasedFNATP.setCurrentTime(startTime);
        timeBasedFNATP.start();
        timeBasedFNATP.setCurrentTime((startTime + (TimeBasedFileNamingAndTriggeringPolicyBaseTest.MILLIS_IN_MINUTE)));
        boolean triggerred = timeBasedFNATP.isTriggeringEvent(null, null);
        Assert.assertTrue(triggerred);
        String elapsedPeriodsFileName = timeBasedFNATP.getElapsedPeriodsFileName();
        Assert.assertEquals("foo-2011-12/59.log", elapsedPeriodsFileName);
    }

    @Test
    public void withTimeZone() {
        // Tuesday December 20th 17:59:01 CET 2011
        long startTime = 1324400341553L;
        tbrp.setFileNamePattern("foo-%d{yyyy-MM-dd, GMT+5}.log");
        tbrp.start();
        timeBasedFNATP.setCurrentTime(startTime);
        timeBasedFNATP.start();
        timeBasedFNATP.setCurrentTime(((startTime + (TimeBasedFileNamingAndTriggeringPolicyBaseTest.MILLIS_IN_MINUTE)) + (2 * (TimeBasedFileNamingAndTriggeringPolicyBaseTest.MILLIS_IN_HOUR))));
        boolean triggerred = timeBasedFNATP.isTriggeringEvent(null, null);
        Assert.assertTrue(triggerred);
        String elapsedPeriodsFileName = timeBasedFNATP.getElapsedPeriodsFileName();
        Assert.assertEquals("foo-2011-12-20.log", elapsedPeriodsFileName);
    }

    @Test
    public void extraIntegerTokenInFileNamePatternShouldBeDetected() {
        String pattern = "test-%d{yyyy-MM-dd'T'HH}-%i.log.zip";
        tbrp.setFileNamePattern(pattern);
        tbrp.start();
        Assert.assertFalse(tbrp.isStarted());
        StatusChecker statusChecker = new StatusChecker(context);
        statusChecker.assertContainsMatch(ERROR, "Filename pattern .{37} contains an integer token converter");
    }
}

