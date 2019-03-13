/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.ejb3.timer.schedule;


import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.ejb.ScheduleExpression;
import org.jboss.as.ejb3.timerservice.schedule.CalendarBasedTimeout;
import org.junit.Assert;
import org.junit.Test;


/**
 * CalendarBasedTimeoutTestCase
 *
 * @author Jaikiran Pai
 * @author Eduardo Martins
 * @author "<a href=\"mailto:wfink@redhat.com\">Wolf-Dieter Fink</a>"
 */
public class CalendarBasedTimeoutTestCase {
    /**
     * Logger
     */
    // private static Logger logger = Logger.getLogger(CalendarBasedTimeoutTestCase.class);
    /**
     * The timezone which is in use
     */
    private TimeZone timezone;

    private String timeZoneDisplayName;

    /**
     * Asserts timeouts based on next day of week.
     * Uses expression dayOfWeek=saturday hour=3 minute=21 second=50.
     * Expected next timeout is SAT 2014-03-29 3:21:50
     */
    @Test
    public void testNextDayOfWeek() {
        // start date is SAT 2014-03-22 4:00:00, has to advance to SAT of next week
        testNextDayOfWeek(new GregorianCalendar(2014, 2, 22, 4, 0, 0).getTime());
        // start date is TUE 2014-03-25 2:00:00, has to advance to SAT of same week
        testNextDayOfWeek(new GregorianCalendar(2014, 2, 25, 2, 0, 0).getTime());
    }

    @Test
    public void testCalendarBasedTimeout() {
        for (TimeZone tz : CalendarBasedTimeoutTestCase.getTimezones()) {
            this.timezone = tz;
            this.timeZoneDisplayName = this.timezone.getDisplayName();
            testEverySecondTimeout();
            testEveryMinuteEveryHourEveryDay();
            testEveryMorningFiveFifteen();
            testEveryWeekdayEightFifteen();
            testEveryMonWedFriTwelveThirtyNoon();
            testEvery31stOfTheMonth();
            testRun29thOfFeb();
            testSomeSpecificTime();
            testEvery10Seconds();
        }
    }

    /**
     * WFLY-1468
     * Create a Timeout with a schedule start date in the past (day before) to ensure the time is set correctly.
     * The schedule is on the first day of month to ensure that the calculated time must be moved to the next month.
     *
     * The test is run for each day of a whole year.
     */
    @Test
    public void testWFLY1468() {
        ScheduleExpression schedule = new ScheduleExpression();
        int year = 2013;
        int month = Calendar.JUNE;
        int dayOfMonth = 3;
        int hourOfDay = 2;
        int minutes = 0;
        Calendar start = new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minutes);
        schedule.hour("0-12").month("*").dayOfMonth("3").minute("0/5").second("0").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        if (((((firstTimeout.get(Calendar.DAY_OF_MONTH)) != 3) || ((firstTimeout.get(Calendar.HOUR_OF_DAY)) != 2)) || ((firstTimeout.get(Calendar.MINUTE)) != 0)) || ((firstTimeout.get(Calendar.SECOND)) != 0)) {
            Assert.fail(firstTimeout.toString());
        }
    }

    /**
     * Testcase #1 for WFLY-3947
     */
    @Test
    public void testWFLY3947_1() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Lisbon");
        int year = 2013;
        int month = Calendar.MARCH;
        int dayOfMonth = 31;
        int hourOfDay = 3;
        int minute = 30;
        int second = 0;
        Calendar start = new GregorianCalendar(timeZone);
        start.clear();
        start.set(year, month, dayOfMonth, hourOfDay, minute, second);
        ScheduleExpression expression = new ScheduleExpression().timezone(timeZone.getID()).dayOfMonth("*").hour("1").minute("30").second("0").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(expression);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(firstTimeout);
        Assert.assertEquals(year, firstTimeout.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.APRIL, firstTimeout.get(Calendar.MONTH));
        Assert.assertEquals(1, firstTimeout.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(1, firstTimeout.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(30, firstTimeout.get(Calendar.MINUTE));
        Assert.assertEquals(second, firstTimeout.get(Calendar.SECOND));
    }

    /**
     * Testcase #2 for WFLY-3947
     */
    @Test
    public void testWFLY3947_2() {
        TimeZone timeZone = TimeZone.getTimeZone("Australia/Lord_Howe");
        int year = 2013;
        int month = Calendar.OCTOBER;
        int dayOfMonth = 6;
        int hourOfDay = 2;
        int minute = 41;
        int second = 0;
        Calendar start = new GregorianCalendar(timeZone);
        start.clear();
        start.set(year, month, dayOfMonth, hourOfDay, minute, second);
        ScheduleExpression expression = new ScheduleExpression().timezone(timeZone.getID()).dayOfMonth("*").hour("2, 3").minute("20, 40").second("0").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(expression);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(firstTimeout);
        Assert.assertEquals(year, firstTimeout.get(Calendar.YEAR));
        Assert.assertEquals(month, firstTimeout.get(Calendar.MONTH));
        Assert.assertEquals(dayOfMonth, firstTimeout.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(3, firstTimeout.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(20, firstTimeout.get(Calendar.MINUTE));
        Assert.assertEquals(second, firstTimeout.get(Calendar.SECOND));
    }

    /**
     * If we have an overflow for minutes, the seconds must be reseted.
     * Test for WFLY-5995
     */
    @Test
    public void testWFLY5995_MinuteOverflow() {
        int year = 2016;
        int month = Calendar.JANUARY;
        int dayOfMonth = 14;
        int hourOfDay = 9;
        int minute = 46;
        int second = 42;
        Calendar start = new GregorianCalendar();
        start.clear();
        start.set(year, month, dayOfMonth, hourOfDay, minute, second);
        ScheduleExpression expression = new ScheduleExpression().dayOfMonth("*").hour("*").minute("0-45").second("0/10").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(expression);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(firstTimeout);
        Assert.assertEquals(year, firstTimeout.get(Calendar.YEAR));
        Assert.assertEquals(month, firstTimeout.get(Calendar.MONTH));
        Assert.assertEquals(dayOfMonth, firstTimeout.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(10, firstTimeout.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, firstTimeout.get(Calendar.MINUTE));
        Assert.assertEquals(0, firstTimeout.get(Calendar.SECOND));
    }

    /**
     * If we have an overflow for hours, the minutes and seconds must be reseted.
     * Test for WFLY-5995
     */
    @Test
    public void testWFLY5995_HourOverflow() {
        int year = 2016;
        int month = Calendar.JANUARY;
        int dayOfMonth = 14;
        int hourOfDay = 9;
        int minute = 45;
        int second = 35;
        Calendar start = new GregorianCalendar();
        start.clear();
        start.set(year, month, dayOfMonth, hourOfDay, minute, second);
        ScheduleExpression expression = new ScheduleExpression().dayOfMonth("*").hour("20-22").minute("0/5").second("20,40").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(expression);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(firstTimeout);
        Assert.assertEquals(year, firstTimeout.get(Calendar.YEAR));
        Assert.assertEquals(month, firstTimeout.get(Calendar.MONTH));
        Assert.assertEquals(dayOfMonth, firstTimeout.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(20, firstTimeout.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, firstTimeout.get(Calendar.MINUTE));
        Assert.assertEquals(20, firstTimeout.get(Calendar.SECOND));
    }

    /**
     * Check if the hour/minute/second is reseted correct if the day must be updated
     */
    @Test
    public void testDayOverflow() {
        int year = 2016;
        int month = Calendar.JANUARY;
        int dayOfMonth = 14;
        int hourOfDay = 9;
        int minute = 56;
        int second = 0;
        Calendar start = new GregorianCalendar();
        start.clear();
        start.set(year, month, dayOfMonth, hourOfDay, minute, second);
        ScheduleExpression expression = new ScheduleExpression().dayOfMonth("2-13").hour("3-9").minute("0/5").second("0").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(expression);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(firstTimeout);
        Assert.assertEquals(year, firstTimeout.get(Calendar.YEAR));
        Assert.assertEquals(1, firstTimeout.get(Calendar.MONTH));
        Assert.assertEquals(2, firstTimeout.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(3, firstTimeout.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(0, firstTimeout.get(Calendar.MINUTE));
        Assert.assertEquals(0, firstTimeout.get(Calendar.SECOND));
    }

    /**
     * Change CET winter time to CEST summer time.
     * The timer should be fired every 15 minutes (absolutely).
     * The calendar time will jump from 2:00CET to 3:00CEST
     * The test should be run similar in any OS/JVM default timezone
     * This is a test to ensure WFLY-9537 will not break this.
     */
    @Test
    public void testChangeCET2CEST() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        start.clear();
        // set half an hour before the CET->CEST DST switch 2017
        start.set(2017, Calendar.MARCH, 26, 1, 30, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        // don't fail the check below if running in a not default TZ
        schedule.hour("*").minute("0/15").second("0").timezone("Europe/Berlin").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if ((((((((firstTimeout.get(Calendar.YEAR)) != 2017) || ((firstTimeout.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((firstTimeout.get(Calendar.DAY_OF_MONTH)) != 26)) || ((firstTimeout.get(Calendar.HOUR_OF_DAY)) != 1)) || ((firstTimeout.get(Calendar.MINUTE)) != 30)) || ((firstTimeout.get(Calendar.SECOND)) != 0)) || ((firstTimeout.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("Start time unexpected : " + (firstTimeout.toString())));
        }
        Calendar current = firstTimeout;
        for (int i = 0; i < 3; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if ((current.getTimeInMillis()) != ((next.getTimeInMillis()) - 900000)) {
                Assert.fail(((("Schedule is more than 15 minutes from " + (current.getTime())) + " to ") + (next.getTime())));
            }
            current = next;
        }
        if (((((((current.get(Calendar.YEAR)) != 2017) || ((current.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((current.get(Calendar.DAY_OF_MONTH)) != 26)) || ((current.get(Calendar.HOUR_OF_DAY)) != 3)) || ((current.get(Calendar.MINUTE)) != 15)) || ((current.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("End time unexpected : " + (current.toString())));
        }
    }

    /**
     * Change CEST summer time to CEST winter time.
     * The timer should be fired every 15 minutes (absolutely).
     * The calendar time will jump from 3:00CEST back to 2:00CET
     * but the timer must run within 2:00-3:00 CEST and 2:00-3:00CET!
     * The test should be run similar in any OS/JVM default timezone
     * This is a test for WFLY-9537
     */
    @Test
    public void testChangeCEST2CET() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Berlin"));
        start.clear();
        // set half an hour before the CEST->CET DST switch 2017
        start.set(2017, Calendar.OCTOBER, 29, 1, 30, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        // don't fail the check below if running in a not default TZ
        schedule.hour("*").minute("5/15").second("0").timezone("Europe/Berlin").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if ((((((((firstTimeout.get(Calendar.YEAR)) != 2017) || ((firstTimeout.get(Calendar.MONTH)) != (Calendar.OCTOBER))) || ((firstTimeout.get(Calendar.DAY_OF_MONTH)) != 29)) || ((firstTimeout.get(Calendar.HOUR_OF_DAY)) != 1)) || ((firstTimeout.get(Calendar.MINUTE)) != 35)) || ((firstTimeout.get(Calendar.SECOND)) != 0)) || ((firstTimeout.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("Start time unexpected : " + (firstTimeout.toString())));
        }
        Calendar current = firstTimeout;
        for (int i = 0; i < 7; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if ((current.getTimeInMillis()) != ((next.getTimeInMillis()) - 900000)) {
                Assert.fail(((("Schedule is more than 15 minutes from " + (current.getTime())) + " to ") + (next.getTime())));
            }
            current = next;
        }
        if (((((((current.get(Calendar.YEAR)) != 2017) || ((current.get(Calendar.MONTH)) != (Calendar.OCTOBER))) || ((current.get(Calendar.DAY_OF_MONTH)) != 29)) || ((current.get(Calendar.HOUR_OF_DAY)) != 2)) || ((current.get(Calendar.MINUTE)) != 20)) || ((current.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("End time unexpected : " + (current.toString())));
        }
    }

    /**
     * Change PST winter time to PST summer time.
     * The timer should be fired every 15 minutes (absolutely).
     * This is a test to ensure WFLY-9537 will not break this.
     */
    @Test
    public void testChangeUS2Summer() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        start.clear();
        // set half an hour before Los Angeles summer time switch
        start.set(2017, Calendar.MARCH, 12, 1, 30, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        // don't fail the check below if running in a not default TZ
        schedule.hour("*").minute("0/15").second("0").timezone("America/Los_Angeles").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if ((((((((firstTimeout.get(Calendar.YEAR)) != 2017) || ((firstTimeout.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((firstTimeout.get(Calendar.DAY_OF_MONTH)) != 12)) || ((firstTimeout.get(Calendar.HOUR_OF_DAY)) != 1)) || ((firstTimeout.get(Calendar.MINUTE)) != 30)) || ((firstTimeout.get(Calendar.SECOND)) != 0)) || ((firstTimeout.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("Start time unexpected : " + (firstTimeout.toString())));
        }
        Calendar current = firstTimeout;
        for (int i = 0; i < 3; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if ((current.getTimeInMillis()) != ((next.getTimeInMillis()) - 900000)) {
                Assert.fail(((("Schedule is more than 15 minutes from " + (current.getTime())) + " to ") + (next.getTime())));
            }
            current = next;
        }
        if (((((((current.get(Calendar.YEAR)) != 2017) || ((current.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((current.get(Calendar.DAY_OF_MONTH)) != 12)) || ((current.get(Calendar.HOUR_OF_DAY)) != 3)) || ((current.get(Calendar.MINUTE)) != 15)) || ((current.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("End time unexpected : " + (current.toString())));
        }
    }

    /**
     * Change PST summer time to PST winter time.
     * The timer should be fired every 15 minutes (absolutely).
     * This is a test for WFLY-9537
     */
    @Test
    public void testChangeUS2Winter() {
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("America/Los_Angeles"));
        start.clear();
        // set half an hour before Los Angeles time switch to winter time
        start.set(2017, Calendar.NOVEMBER, 5, 0, 30, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        // don't fail the check below if running in a not default TZ
        schedule.hour("*").minute("0/15").second("0").timezone("America/Los_Angeles").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar firstTimeout = calendarTimeout.getFirstTimeout();
        // assert first timeout result
        Assert.assertNotNull(firstTimeout);
        if ((((((((firstTimeout.get(Calendar.YEAR)) != 2017) || ((firstTimeout.get(Calendar.MONTH)) != (Calendar.NOVEMBER))) || ((firstTimeout.get(Calendar.DAY_OF_MONTH)) != 5)) || ((firstTimeout.get(Calendar.HOUR_OF_DAY)) != 0)) || ((firstTimeout.get(Calendar.MINUTE)) != 30)) || ((firstTimeout.get(Calendar.SECOND)) != 0)) || ((firstTimeout.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("Start time unexpected : " + (firstTimeout.toString())));
        }
        Calendar current = firstTimeout;
        for (int i = 0; i < 7; i++) {
            Calendar next = calendarTimeout.getNextTimeout(current);
            if ((current.getTimeInMillis()) != ((next.getTimeInMillis()) - 900000)) {
                Assert.fail(((("Schedule is more than 15 minutes from " + (current.getTime())) + " to ") + (next.getTime())));
            }
            current = next;
        }
        if (((((((current.get(Calendar.YEAR)) != 2017) || ((current.get(Calendar.MONTH)) != (Calendar.NOVEMBER))) || ((current.get(Calendar.DAY_OF_MONTH)) != 5)) || ((current.get(Calendar.HOUR_OF_DAY)) != 1)) || ((current.get(Calendar.MINUTE)) != 15)) || ((current.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("End time unexpected : " + (current.toString())));
        }
    }

    /**
     * This test asserts that the timer increments in seconds, minutes and hours
     * are the same for a complete year using a DST timezone and a non-DST timezone.
     *
     * This test covers WFLY-10106 issue.
     */
    @Test
    public void testTimeoutIncrements() {
        TimeZone dstTimezone = TimeZone.getTimeZone("Atlantic/Canary");
        TimeZone nonDstTimezone = TimeZone.getTimeZone("Africa/Abidjan");
        Assert.assertTrue(dstTimezone.useDaylightTime());
        Assert.assertTrue((!(nonDstTimezone.useDaylightTime())));
        for (TimeZone tz : Arrays.asList(dstTimezone, nonDstTimezone)) {
            this.timezone = tz;
            testSecondIncrement();
            testMinutesIncrement();
            testHoursIncrement();
        }
    }

    /**
     * This test asserts that a timer scheduled to run during the ambiguous hour when the
     * Daylight Savings period ends is not executed twice.
     *
     * It configures a timer to be fired on October 29, 2017 at 01:30:00 in Europe/Lisbon TZ.
     * There are two 01:30:00 that day: 01:30:00 WEST and 01:30:00 WET. The timer has to be
     * fired just once.
     */
    @Test
    public void testTimerAtAmbiguousHourWESTtoWET() {
        // WEST -> WET
        // Sunday, 29 October 2017, 02:00:00 -> 01:00:00
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Lisbon"));
        start.clear();
        start.set(2017, Calendar.OCTOBER, 29, 0, 0, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("1").minute("30").second("0").timezone("Europe/Lisbon").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar timeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(timeout);
        // Assert timeout is 29 October at 01:30 WEST
        if ((((((((timeout.get(Calendar.YEAR)) != 2017) || ((timeout.get(Calendar.MONTH)) != (Calendar.OCTOBER))) || ((timeout.get(Calendar.DAY_OF_MONTH)) != 29)) || ((timeout.get(Calendar.HOUR_OF_DAY)) != 1)) || ((timeout.get(Calendar.MINUTE)) != 30)) || ((timeout.get(Calendar.SECOND)) != 0)) || ((timeout.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("Time unexpected : " + (timeout.toString())));
        }
        // Asserts elapsed time from start was 1h 30min:
        Assert.assertTrue(((("Schedule is more than 1h 30min hours from " + (start.getTime())) + " to ") + (timeout.getTime())), (((timeout.getTimeInMillis()) - (start.getTimeInMillis())) == ((((1 * 60) * 60) * 1000) + ((30 * 60) * 1000))));
        timeout = calendarTimeout.getNextTimeout(timeout);
        // Assert timeout is 30 October at 01:30 WET
        if ((((((((timeout.get(Calendar.YEAR)) != 2017) || ((timeout.get(Calendar.MONTH)) != (Calendar.OCTOBER))) || ((timeout.get(Calendar.DAY_OF_MONTH)) != 30)) || ((timeout.get(Calendar.HOUR_OF_DAY)) != 1)) || ((timeout.get(Calendar.MINUTE)) != 30)) || ((timeout.get(Calendar.SECOND)) != 0)) || ((timeout.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("Time unexpected : " + (timeout.toString())));
        }
    }

    /**
     * This test asserts that a timer scheduled to run during the removed hour when the
     * Daylight Savings period starts is executed.
     *
     * It configures a timer to be fired on March 26, 2017 at 03:30:00 in Europe/Helsinki TZ.
     * This hour does not exist in that timezone, this test asserts the timer is fired once
     * during this ambiguous hour.
     */
    @Test
    public void testTimerAtAmbiguousHourEETtoEEST() {
        // EET --> EEST
        // Sunday, 26 March 2017, 03:00:00 --> 04:00:00
        Calendar start = new GregorianCalendar(TimeZone.getTimeZone("Europe/Helsinki"));
        start.clear();
        start.set(2017, Calendar.MARCH, 26, 0, 0, 0);
        ScheduleExpression schedule = new ScheduleExpression();
        schedule.hour("3").minute("30").second("0").timezone("Europe/Helsinki").start(start.getTime());
        CalendarBasedTimeout calendarTimeout = new CalendarBasedTimeout(schedule);
        Calendar timeout = calendarTimeout.getFirstTimeout();
        Assert.assertNotNull(timeout);
        // Assert timeout is 26 March at 03:30 EET
        if ((((((((timeout.get(Calendar.YEAR)) != 2017) || ((timeout.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((timeout.get(Calendar.DAY_OF_MONTH)) != 26)) || ((timeout.get(Calendar.HOUR_OF_DAY)) != 3)) || ((timeout.get(Calendar.MINUTE)) != 30)) || ((timeout.get(Calendar.SECOND)) != 0)) || ((timeout.get(Calendar.DST_OFFSET)) != 0)) {
            Assert.fail(("Time unexpected : " + (timeout.toString())));
        }
        // Asserts elapsed time from start was 3h 30min:
        Assert.assertTrue(((("Schedule is more than 3h 30min hours from " + (start.getTime())) + " to ") + (timeout.getTime())), (((timeout.getTimeInMillis()) - (start.getTimeInMillis())) == ((((3 * 60) * 60) * 1000) + ((30 * 60) * 1000))));
        timeout = calendarTimeout.getNextTimeout(timeout);
        // Assert timeout is 27 March at 03:30 EEST
        if ((((((((timeout.get(Calendar.YEAR)) != 2017) || ((timeout.get(Calendar.MONTH)) != (Calendar.MARCH))) || ((timeout.get(Calendar.DAY_OF_MONTH)) != 27)) || ((timeout.get(Calendar.HOUR_OF_DAY)) != 3)) || ((timeout.get(Calendar.MINUTE)) != 30)) || ((timeout.get(Calendar.SECOND)) != 0)) || ((timeout.get(Calendar.DST_OFFSET)) != 3600000)) {
            Assert.fail(("Time unexpected : " + (timeout.toString())));
        }
    }
}

