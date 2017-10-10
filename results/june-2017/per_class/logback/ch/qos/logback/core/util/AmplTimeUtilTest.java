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


package ch.qos.logback.core.util;


public class AmplTimeUtilTest {
    @org.junit.Test
    public void testSecond() {
        // Mon Nov 20 18:05:17,522 CET 2006
        long now = 1164042317522L;
        // Mon Nov 20 18:05:18,000 CET 2006
        long expected = 1164042318000L;
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextSecond(now);
        org.junit.Assert.assertEquals((expected - now), 478);
        org.junit.Assert.assertEquals(expected, computed);
    }

    @org.junit.Test
    public void testMinute() {
        // Mon Nov 20 18:05:17,522 CET 2006
        long now = 1164042317522L;
        // Mon Nov 20 18:06:00 CET 2006
        long expected = 1164042360000L;
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextMinute(now);
        org.junit.Assert.assertEquals((expected - now), ((1000 * 42) + 478));
        org.junit.Assert.assertEquals(expected, computed);
    }

    @org.junit.Test
    public void testHour() {
        // Mon Nov 20 18:05:17,522 GMT 2006
        long now = 1164045917522L;
        now = correctBasedOnTimeZone(now);
        // Mon Nov 20 19:00:00 GMT 2006
        long expected = 1164049200000L;
        expected = correctBasedOnTimeZone(expected);
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextHour(now);
        org.junit.Assert.assertEquals((expected - now), ((1000 * (42 + (60 * 54))) + 478));
        org.junit.Assert.assertEquals(expected, computed);
    }

    @org.junit.Test
    public void testDay() {
        // Mon Nov 20 18:05:17 GMT 2006
        long now = 1164045917522L;
        now = correctBasedOnTimeZone(now);
        // Tue Nov 21 00:00:00 GMT 2006
        long expected = 1164067200000L;
        expected = correctBasedOnTimeZone(expected);
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextDay(now);
        org.junit.Assert.assertEquals((expected - now), ((1000 * (((3600 * 5) + (60 * 54)) + 42)) + 478));
        org.junit.Assert.assertEquals(expected, computed);
    }

    @org.junit.Test
    public void testWeek() {
        // Mon Nov 20 18:05:17 GMT 2006
        long now = 1164045917522L;
        now = correctBasedOnTimeZone(now);
        // Sun Nov 26 00:00:00 GMT 2006
        long expected = 1164499200000L;
        expected = correctBasedOnTimeZone(expected);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(new java.util.Date(now));
        int dayOffset = (cal.getFirstDayOfWeek()) - (java.util.Calendar.SUNDAY);
        if (dayOffset != 0) {
            expected += ((24L * 3600) * 1000) * ((cal.getFirstDayOfWeek()) - (java.util.Calendar.SUNDAY));
        }
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextWeek(now);
        // System.out.println("now "+new Date(now));
        // System.out.println("computed "+new Date(computed));
        // System.out.println("expected "+new Date(expected));
        org.junit.Assert.assertEquals((expected - now), ((1000 * (((3600 * (5 + (24 * (5 + dayOffset)))) + (60 * 54)) + 42)) + 478));
        org.junit.Assert.assertEquals(expected, computed);
    }

    @org.junit.Test
    public void testMonth() {
        // Mon Nov 20 18:05:17 GMT 2006
        long now = 1164045917522L;
        now = correctBasedOnTimeZone(now);
        // Fri Dec 01 00:00:00 GMT 2006
        long expected = 1164931200000L;
        expected = correctBasedOnTimeZone(expected);
        long computed = ch.qos.logback.core.util.TimeUtil.computeStartOfNextMonth(now);
        org.junit.Assert.assertEquals((expected - now), ((1000 * (((3600 * (5 + (24 * 10))) + (60 * 54)) + 42)) + 478));
        org.junit.Assert.assertEquals(expected, computed);
    }

    private long correctBasedOnTimeZone(long gmtLong) {
        int offset = java.util.TimeZone.getDefault().getRawOffset();
        return gmtLong - offset;
    }
}

