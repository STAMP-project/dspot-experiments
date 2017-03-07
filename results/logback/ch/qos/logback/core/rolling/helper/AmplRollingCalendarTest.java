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


package ch.qos.logback.core.rolling.helper;


public class AmplRollingCalendarTest {
    java.lang.String dailyPattern = "yyyy-MM-dd";

    @org.junit.Before
    public void setUp() {
        // Most surprisingly, in certain environments (e.g. Windows 7), setting the default locale
        // allows certain tests to pass which otherwise fail.
        // 
        // These tests are:
        // 
        // checkCollisionFreeness("yyyy-WW", false);
        // checkCollisionFreeness("yyyy-ww", true);
        // checkCollisionFreeness("ww", false);
        // {
        // RollingCalendar rc = new RollingCalendar("yyyy-ww");
        // assertEquals(PeriodicityType.TOP_OF_WEEK, rc.getPeriodicityType());
        // }
        // 
        java.util.Locale oldLocale = java.util.Locale.getDefault();
        java.util.Locale.setDefault(oldLocale);
    }

    @org.junit.After
    public void tearDown() {
    }

    @org.junit.Test
    public void testPeriodicity() {
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd_HH_mm_ss");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_SECOND, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd_HH_mm");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_MINUTE, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd_HH");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_HOUR, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd_hh");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_HOUR, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_DAY, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_MONTH, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-ww");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_WEEK, rc.getPeriodicityType());
        }
        {
            ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-WW");
            org.junit.Assert.assertEquals(ch.qos.logback.core.rolling.helper.PeriodicityType.TOP_OF_WEEK, rc.getPeriodicityType());
        }
    }

    @org.junit.Test
    public void testVaryingNumberOfHourlyPeriods() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd_HH");
        long MILLIS_IN_HOUR = 3600 * 1000;
        for (int p = 100; p > (-100); p--) {
            long now = 1223325293589L;// Mon Oct 06 22:34:53 CEST 2008
            
            java.util.Date result = rc.getEndOfNextNthPeriod(new java.util.Date(now), p);
            long expected = (now - (now % MILLIS_IN_HOUR)) + (p * MILLIS_IN_HOUR);
            org.junit.Assert.assertEquals(expected, result.getTime());
        }
    }

    @org.junit.Test
    public void testVaryingNumberOfDailyPeriods() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar("yyyy-MM-dd");
        final long MILLIS_IN_DAY = (24 * 3600) * 1000;
        for (int p = 20; p > (-100); p--) {
            long now = 1223325293589L;// Mon Oct 06 22:34:53 CEST 2008
            
            java.util.Date nowDate = new java.util.Date(now);
            java.util.Date result = rc.getEndOfNextNthPeriod(nowDate, p);
            long offset = (rc.getTimeZone().getRawOffset()) + (rc.getTimeZone().getDSTSavings());
            long origin = now - ((now + offset) % MILLIS_IN_DAY);
            long expected = origin + (p * MILLIS_IN_DAY);
            org.junit.Assert.assertEquals(("p=" + p), expected, result.getTime());
        }
    }

    // Wed Mar 23 23:07:05 CET 2016
    final long WED_2016_03_23_T_230705_CET = 1458770825333L;

    @org.junit.Test
    public void testBarrierCrossingComputation() {
        checkPeriodBarriersCrossed("yyyy-MM-dd'T'HHmmss", WED_2016_03_23_T_230705_CET, ((WED_2016_03_23_T_230705_CET) + (3 * (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_SECOND))), 3);
        checkPeriodBarriersCrossed("yyyy-MM-dd'T'HHmm", WED_2016_03_23_T_230705_CET, ((WED_2016_03_23_T_230705_CET) + (3 * (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_MINUTE))), 3);
        checkPeriodBarriersCrossed("yyyy-MM-dd'T'HH", WED_2016_03_23_T_230705_CET, ((WED_2016_03_23_T_230705_CET) + (3 * (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_HOUR))), 3);
        checkPeriodBarriersCrossed("yyyy-MM-dd", WED_2016_03_23_T_230705_CET, ((WED_2016_03_23_T_230705_CET) + (3 * (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_DAY))), 3);
    }

    private void checkPeriodBarriersCrossed(java.lang.String pattern, long start, long end, int count) {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(pattern);
        org.junit.Assert.assertEquals(count, rc.periodBarriersCrossed(start, end));
    }

    @org.junit.Test
    public void testCollisionFreenes() {
        // hourly
        checkCollisionFreeness("yyyy-MM-dd hh", false);
        checkCollisionFreeness("yyyy-MM-dd hh a", true);
        checkCollisionFreeness("yyyy-MM-dd HH", true);
        checkCollisionFreeness("yyyy-MM-dd kk", true);
        checkCollisionFreeness("yyyy-MM-dd KK", false);
        checkCollisionFreeness("yyyy-MM-dd KK a", true);
        // daily
        checkCollisionFreeness("yyyy-MM-dd", true);
        checkCollisionFreeness("yyyy-dd", false);
        checkCollisionFreeness("dd", false);
        checkCollisionFreeness("MM-dd", false);
        checkCollisionFreeness("yyyy-DDD", true);
        checkCollisionFreeness("DDD", false);
        // 'u' is new to JDK 7
        if (ch.qos.logback.core.util.EnvUtil.isJDK7OrHigher()) {
            checkCollisionFreeness("yyyy-MM-dd-uu", true);
            checkCollisionFreeness("yyyy-MM-uu", false);
        }
        // weekly
        checkCollisionFreeness("yyyy-MM-WW", true);
        dumpCurrentLocale(java.util.Locale.getDefault());
        checkCollisionFreeness("yyyy-WW", false);
        checkCollisionFreeness("yyyy-ww", true);
        checkCollisionFreeness("ww", false);
    }

    private void dumpCurrentLocale(java.util.Locale locale) {
        java.lang.System.out.println(("***Current default locale is " + locale));
    }

    private void checkCollisionFreeness(java.lang.String pattern, boolean expected) {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(pattern);
        if (expected) {
            org.junit.Assert.assertTrue(rc.isCollisionFree());
        }else {
            org.junit.Assert.assertFalse(rc.isCollisionFree());
        }
    }

    @org.junit.Test
    public void basicPeriodBarriersCrossed() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(dailyPattern, java.util.TimeZone.getTimeZone("CET"), java.util.Locale.US);
        // Thu Jan 26 19:46:58 CET 2017, GMT offset = -1h
        long start = 1485456418969L;
        // Fri Jan 27 19:46:58 CET 2017,  GMT offset = -1h
        long end = start + (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_DAY);
        org.junit.Assert.assertEquals(1, rc.periodBarriersCrossed(start, end));
    }

    @org.junit.Test
    public void testPeriodBarriersCrossedWhenGoingIntoDaylightSaving() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(dailyPattern, java.util.TimeZone.getTimeZone("CET"), java.util.Locale.US);
        // Sun Mar 26 00:02:03 CET  2017, GMT offset = -1h
        long start = 1490482923333L;
        // Mon Mar 27 00:02:03 CEST 2017,  GMT offset = -2h
        long end = 1490565723333L;
        org.junit.Assert.assertEquals(1, rc.periodBarriersCrossed(start, end));
    }

    @org.junit.Test
    public void testPeriodBarriersCrossedWhenLeavingDaylightSaving() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(dailyPattern, java.util.TimeZone.getTimeZone("CET"), java.util.Locale.US);
        // Sun Oct 29 00:02:03 CEST 2017, GMT offset = -2h
        long start = 1509228123333L;// 1490482923333L+217*CoreConstants.MILLIS_IN_ONE_DAY-CoreConstants.MILLIS_IN_ONE_HOUR;
        
        // Mon Oct 30 00:02:03 CET  2017,  GMT offset = -1h
        long end = 1509228123333L + (25 * (ch.qos.logback.core.CoreConstants.MILLIS_IN_ONE_HOUR));
        org.junit.Assert.assertEquals(1, rc.periodBarriersCrossed(start, end));
    }

    @org.junit.Test
    public void testPeriodBarriersCrosseJustBeforeEnteringDaylightSaving() {
        ch.qos.logback.core.rolling.helper.RollingCalendar rc = new ch.qos.logback.core.rolling.helper.RollingCalendar(dailyPattern, java.util.TimeZone.getTimeZone("CET"), java.util.Locale.US);
        // Sun Mar 26 22:18:38 CEST 2017, GMT offset = +2h
        long start = 1490559518333L;
        java.lang.System.out.println(new java.util.Date(start));
        // Mon Mar 27 00:05:18 CEST 2017, GMT offset = +2h
        long end = 1490565918333L;
        java.lang.System.out.println(new java.util.Date(end));
        org.junit.Assert.assertEquals(1, rc.periodBarriersCrossed(start, end));
    }
}

