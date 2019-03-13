/**
 * Copyright 2001-2013 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;


import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a JUnit test for MutableDateTime.
 *
 * @author Stephen Colebourne
 */
public class TestMutableDateTime_Sets extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-04-05
    private long TEST_TIME1 = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    // 2003-05-06
    private long TEST_TIME2 = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestMutableDateTime_Sets(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMillis_long1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setMillis(TEST_TIME2);
        TestCase.assertEquals(TEST_TIME2, test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testSetChronology_Chronology1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setChronology(GregorianChronology.getInstance(TestMutableDateTime_Sets.PARIS));
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestMutableDateTime_Sets.PARIS), test.getChronology());
    }

    public void testSetChronology_Chronology2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setChronology(null);
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testSetZone_DateTimeZone1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setZone(TestMutableDateTime_Sets.PARIS);
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Sets.PARIS), test.getChronology());
    }

    public void testSetZone_DateTimeZone2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setZone(null);
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testSetZoneRetainFields_DateTimeZone1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setZoneRetainFields(TestMutableDateTime_Sets.PARIS);
        TestCase.assertEquals(((TEST_TIME1) - (MILLIS_PER_HOUR)), test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Sets.PARIS), test.getChronology());
    }

    public void testSetZoneRetainFields_DateTimeZone2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.setZoneRetainFields(null);
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
    }

    public void testSetZoneRetainFields_DateTimeZone3() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, GregorianChronology.getInstance(TestMutableDateTime_Sets.PARIS));
        test.setZoneRetainFields(null);
        TestCase.assertEquals(((TEST_TIME1) + (MILLIS_PER_HOUR)), test.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
    }

    public void testSetZoneRetainFields_DateTimeZone4() {
        Chronology chrono = new MockNullZoneChronology();
        MutableDateTime test = new MutableDateTime(TEST_TIME1, chrono);
        test.setZoneRetainFields(TestMutableDateTime_Sets.PARIS);
        TestCase.assertEquals(((TEST_TIME1) - (MILLIS_PER_HOUR)), test.getMillis());
        TestCase.assertSame(chrono, test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testSetMillis_RI1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, BuddhistChronology.getInstance());
        test.setMillis(new Instant(TEST_TIME2));
        TestCase.assertEquals(TEST_TIME2, test.getMillis());
        TestCase.assertEquals(BuddhistChronology.getInstance(), test.getChronology());
    }

    public void testSetMillis_RI2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, BuddhistChronology.getInstance());
        test.setMillis(null);
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
        TestCase.assertEquals(BuddhistChronology.getInstance(), test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testSet_DateTimeFieldType_int1() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        test.set(DateTimeFieldType.year(), 2010);
        TestCase.assertEquals(2010, test.getYear());
    }

    public void testSet_DateTimeFieldType_int2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        try {
            test.set(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    public void testSet_DateTimeFieldType_int3() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        try {
            test.set(DateTimeFieldType.monthOfYear(), 13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testSetDate_int_int_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setDate(2010, 12, 3);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    public void testSetDate_int_int_int2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        try {
            test.setDate(2010, 13, 3);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testSetDate_long1() {
        long setter = new DateTime(2010, 12, 3, 5, 7, 9, 501).getMillis();
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setDate(setter);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testSetDate_RI1() {
        DateTime setter = new DateTime(2010, 12, 3, 5, 7, 9, 501);
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setDate(setter);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    public void testSetDate_RI2() {
        MutableDateTime test = new MutableDateTime(2010, 7, 8, 12, 24, 48, 501);
        test.setDate(null);// sets to TEST_TIME_NOW

        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    public void testSetDate_RI_same() {
        MutableDateTime setter = new MutableDateTime(2010, 12, 3, 2, 24, 48, 501, DateTimeZone.forID("America/Los_Angeles"));
        MutableDateTime test = new MutableDateTime(2010, 12, 3, 2, 24, 48, 501, DateTimeZone.forID("America/Los_Angeles"));
        test.setDate(setter);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(2, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    public void testSetDate_RI_different1() {
        MutableDateTime setter = new MutableDateTime(2010, 12, 1, 0, 0, 0, 0, DateTimeZone.forID("America/Los_Angeles"));
        MutableDateTime test = new MutableDateTime(2010, 12, 3, 2, 24, 48, 501, DateTimeZone.forID("Europe/Moscow"));
        test.setDate(setter);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(2, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    public void testSetDate_RI_different2() {
        MutableDateTime setter = new MutableDateTime(2010, 12, 1, 0, 0, 0, 0, DateTimeZone.forID("Europe/Moscow"));
        MutableDateTime test = new MutableDateTime(2010, 12, 3, 2, 24, 48, 501, DateTimeZone.forID("America/Los_Angeles"));
        test.setDate(setter);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(2, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(48, test.getSecondOfMinute());
        TestCase.assertEquals(501, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testSetTime_int_int_int_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setTime(5, 6, 7, 8);
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(5, test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    public void testSetTime_int_int_int2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        try {
            test.setTime(60, 6, 7, 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testSetTime_long1() {
        long setter = new DateTime(2010, 12, 3, 5, 7, 9, 11).getMillis();
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setTime(setter);
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(5, test.getHourOfDay());
        TestCase.assertEquals(7, test.getMinuteOfHour());
        TestCase.assertEquals(9, test.getSecondOfMinute());
        TestCase.assertEquals(11, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testSetTime_RI1() {
        DateTime setter = new DateTime(2010, 12, 3, 5, 7, 9, 11);
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setTime(setter);
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(5, test.getHourOfDay());
        TestCase.assertEquals(7, test.getMinuteOfHour());
        TestCase.assertEquals(9, test.getSecondOfMinute());
        TestCase.assertEquals(11, test.getMillisOfSecond());
    }

    public void testSetTime_RI2() {
        MutableDateTime test = new MutableDateTime(2010, 7, 8, 12, 24, 48, 501);
        test.setTime(null);// sets to TEST_TIME_NOW, which has no time part

        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(7, test.getMonthOfYear());
        TestCase.assertEquals(8, test.getDayOfMonth());
        TestCase.assertEquals(new DateTime(TEST_TIME_NOW).getHourOfDay(), test.getHourOfDay());
        TestCase.assertEquals(new DateTime(TEST_TIME_NOW).getMinuteOfHour(), test.getMinuteOfHour());
        TestCase.assertEquals(new DateTime(TEST_TIME_NOW).getSecondOfMinute(), test.getSecondOfMinute());
        TestCase.assertEquals(new DateTime(TEST_TIME_NOW).getMillisOfSecond(), test.getMillisOfSecond());
    }

    public void testSetTime_Object3() {
        DateTime temp = new DateTime(2010, 12, 3, 5, 7, 9, 11);
        DateTime setter = new DateTime(temp.getMillis(), new MockNullZoneChronology());
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setTime(setter);
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(5, test.getHourOfDay());
        TestCase.assertEquals(7, test.getMinuteOfHour());
        TestCase.assertEquals(9, test.getSecondOfMinute());
        TestCase.assertEquals(11, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testSetDateTime_int_int_int_int_int_int_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 12, 24, 48, 501);
        test.setDateTime(2010, 12, 3, 5, 6, 7, 8);
        TestCase.assertEquals(2010, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(5, test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    public void testSetDateTime_int_int_int_int_int_int_int2() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        try {
            test.setDateTime(2010, 13, 3, 5, 6, 7, 8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testSetYear_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setYear(2010);
        TestCase.assertEquals("2010-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMonthOfYear_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setMonthOfYear(12);
        TestCase.assertEquals("2002-12-09T05:06:07.008Z", test.toString());
    }

    public void testSetMonthOfYear_int_dstOverlapSummer_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
        test.setMonthOfYear(10);
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
    }

    public void testSetMonthOfYear_int_dstOverlapWinter_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        test.addHours(1);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
        test.setMonthOfYear(10);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
    }

    public void testSetMonthOfYear_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setMonthOfYear(13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetDayOfMonth_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setDayOfMonth(17);
        TestCase.assertEquals("2002-06-17T05:06:07.008+01:00", test.toString());
    }

    public void testSetDayOfMonth_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setDayOfMonth(31);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    public void testSetDayOfMonth_int_dstOverlapSummer_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
        test.setDayOfMonth(30);
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
    }

    public void testSetDayOfMonth_int_dstOverlapWinter_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        test.addHours(1);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
        test.setDayOfMonth(30);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetDayOfYear_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setDayOfYear(3);
        TestCase.assertEquals("2002-01-03T05:06:07.008Z", test.toString());
    }

    public void testSetDayOfYear_int_dstOverlapSummer_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
        test.setDayOfYear(303);
        TestCase.assertEquals("2011-10-30T02:30:00.000+02:00", test.toString());
    }

    public void testSetDayOfYear_int_dstOverlapWinter_addZero() {
        MutableDateTime test = new MutableDateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        test.addHours(1);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
        test.setDayOfYear(303);
        TestCase.assertEquals("2011-10-30T02:30:00.000+01:00", test.toString());
    }

    public void testSetDayOfYear_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setDayOfYear(366);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetWeekyear_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setWeekyear(2001);
        TestCase.assertEquals("2001-06-10T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetWeekOfWeekyear_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setWeekOfWeekyear(2);
        TestCase.assertEquals("2002-01-13T05:06:07.008Z", test.toString());
    }

    public void testSetWeekOfWeekyear_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setWeekOfWeekyear(53);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetDayOfWeek_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setDayOfWeek(5);
        TestCase.assertEquals("2002-06-07T05:06:07.008+01:00", test.toString());
    }

    public void testSetDayOfWeek_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setDayOfWeek(8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetHourOfDay_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setHourOfDay(13);
        TestCase.assertEquals("2002-06-09T13:06:07.008+01:00", test.toString());
    }

    public void testSetHourOfDay_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setHourOfDay(24);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMinuteOfHour_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setMinuteOfHour(13);
        TestCase.assertEquals("2002-06-09T05:13:07.008+01:00", test.toString());
    }

    public void testSetMinuteOfHour_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setMinuteOfHour(60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMinuteOfDay_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setMinuteOfDay(13);
        TestCase.assertEquals("2002-06-09T00:13:07.008+01:00", test.toString());
    }

    public void testSetMinuteOfDay_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setMinuteOfDay((24 * 60));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetSecondOfMinute_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setSecondOfMinute(13);
        TestCase.assertEquals("2002-06-09T05:06:13.008+01:00", test.toString());
    }

    public void testSetSecondOfMinute_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setSecondOfMinute(60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetSecondOfDay_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setSecondOfDay(13);
        TestCase.assertEquals("2002-06-09T00:00:13.008+01:00", test.toString());
    }

    public void testSetSecondOfDay_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setSecondOfDay(((24 * 60) * 60));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMilliOfSecond_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setMillisOfSecond(13);
        TestCase.assertEquals("2002-06-09T05:06:07.013+01:00", test.toString());
    }

    public void testSetMilliOfSecond_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setMillisOfSecond(1000);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSetMilliOfDay_int1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setMillisOfDay(13);
        TestCase.assertEquals("2002-06-09T00:00:00.013+01:00", test.toString());
    }

    public void testSetMilliOfDay_int2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setMillisOfDay((((24 * 60) * 60) * 1000));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
    }
}

