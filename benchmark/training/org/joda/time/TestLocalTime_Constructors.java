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


import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;
import static DateTimeZone.UTC;
import static LocalTime.MIDNIGHT;


/**
 * This class is a Junit unit test for LocalTime.
 *
 * @author Stephen Colebourne
 */
public class TestLocalTime_Constructors extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEW_YORK = DateTimeZone.forID("America/New_York");

    private static final ISOChronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final JulianChronology JULIAN_LONDON = JulianChronology.getInstance(TestLocalTime_Constructors.LONDON);

    private static final JulianChronology JULIAN_PARIS = JulianChronology.getInstance(TestLocalTime_Constructors.PARIS);

    private static final JulianChronology JULIAN_UTC = JulianChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private static final int OFFSET_LONDON = (TestLocalTime_Constructors.LONDON.getOffset(0L)) / (MILLIS_PER_HOUR);

    private static final int OFFSET_PARIS = (TestLocalTime_Constructors.PARIS.getOffset(0L)) / (MILLIS_PER_HOUR);

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME1 = (((1L * (MILLIS_PER_HOUR)) + (2L * (MILLIS_PER_MINUTE))) + (3L * (MILLIS_PER_SECOND))) + 4L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestLocalTime_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstantMidnight() throws Throwable {
        LocalTime test = MIDNIGHT;
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new LocalTime(1, 20), LocalTime.parse("01:20"));
        TestCase.assertEquals(new LocalTime(14, 50, 30, 432), LocalTime.parse("14:50:30.432"));
    }

    public void testParse_formatter() throws Throwable {
        DateTimeFormatter f = DateTimeFormat.forPattern("HH mm").withChronology(ISOChronology.getInstance(TestLocalTime_Constructors.PARIS));
        TestCase.assertEquals(new LocalTime(13, 30), LocalTime.parse("13 30", f));
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromCalendarFields_Calendar() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalTime expected = new LocalTime(4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalTime.fromCalendarFields(cal));
        try {
            LocalTime.fromCalendarFields(((Calendar) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromDateFields_after1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalTime expected = new LocalTime(4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_FromDateFields_before1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1969, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalTime expected = new LocalTime(4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_FromDateFields_null() throws Exception {
        try {
            LocalTime.fromDateFields(((Date) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactoryMillisOfDay_long() throws Throwable {
        LocalTime test = LocalTime.fromMillisOfDay(TEST_TIME1);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testFactoryMillisOfDay_long_Chronology() throws Throwable {
        LocalTime test = LocalTime.fromMillisOfDay(TEST_TIME1, TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testFactoryMillisOfDay_long_nullChronology() throws Throwable {
        LocalTime test = LocalTime.fromMillisOfDay(TEST_TIME1, null);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor() throws Throwable {
        LocalTime test = new LocalTime();
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalTime.now());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 30, 40, TestLocalTime_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        LocalTime test = new LocalTime(TestLocalTime_Constructors.LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(23, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalTime.now(TestLocalTime_Constructors.LONDON));
        test = new LocalTime(TestLocalTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalTime.now(TestLocalTime_Constructors.PARIS));
    }

    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 30, 40, TestLocalTime_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        LocalTime test = new LocalTime(((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(23, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Chronology() throws Throwable {
        LocalTime test = new LocalTime(TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalTime.now(TestLocalTime_Constructors.JULIAN_LONDON));
    }

    public void testConstructor_nullChronology() throws Throwable {
        LocalTime test = new LocalTime(((Chronology) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME1);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_long2() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME2);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((5 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_DateTimeZone() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME1, TestLocalTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_long_DateTimeZone_2() throws Throwable {
        DateTime dt = new DateTime(2007, 6, 9, 1, 2, 3, 4, TestLocalTime_Constructors.PARIS);
        DateTime dtUTC = new DateTime(1970, 1, 1, 1, 2, 3, 4, UTC);
        LocalTime test = new LocalTime(dt.getMillis(), TestLocalTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
        TestCase.assertEquals(dtUTC.getMillis(), test.getLocalMillis());
    }

    public void testConstructor_long_nullDateTimeZone() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME1, ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1_Chronology() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME1, TestLocalTime_Constructors.JULIAN_PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_long2_Chronology() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME2, TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals((5 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    public void testConstructor_long_nullChronology() throws Throwable {
        LocalTime test = new LocalTime(TEST_TIME1, ((Chronology) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object1() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalTime test = new LocalTime(date);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_Object2() throws Throwable {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(TEST_TIME1));
        LocalTime test = new LocalTime(cal);
        TestCase.assertEquals(GJChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject() throws Throwable {
        LocalTime test = new LocalTime(((Object) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        LocalTime test = new LocalTime("10:20:30.040");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString1Tokyo() throws Throwable {
        DateTimeZone.setDefault(TestLocalTime_Constructors.TOKYO);
        LocalTime test = new LocalTime("10:20:30.040");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString1NewYork() throws Throwable {
        DateTimeZone.setDefault(TestLocalTime_Constructors.NEW_YORK);
        LocalTime test = new LocalTime("10:20:30.040");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString2() throws Throwable {
        LocalTime test = new LocalTime("T10:20:30.040");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString3() throws Throwable {
        LocalTime test = new LocalTime("10:20");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString4() throws Throwable {
        LocalTime test = new LocalTime("10");
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new LocalTime("1970-04-06");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new LocalTime("1970-04-06T+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new LocalTime("1970-04-06T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new LocalTime("1970-04-06T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx5() throws Throwable {
        try {
            new LocalTime("T10:20:30.040+04:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx6() throws Throwable {
        try {
            new LocalTime("10:20:30.040+04:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectLocalTime() throws Throwable {
        LocalTime time = new LocalTime(10, 20, 30, 40, TestLocalTime_Constructors.BUDDHIST_UTC);
        LocalTime test = new LocalTime(time);
        TestCase.assertEquals(TestLocalTime_Constructors.BUDDHIST_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectLocalDate() throws Throwable {
        LocalDate date = new LocalDate(1970, 4, 6, TestLocalTime_Constructors.BUDDHIST_UTC);
        try {
            new LocalTime(date);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectLocalDateTime() throws Throwable {
        LocalDateTime dt = new LocalDateTime(1970, 5, 6, 10, 20, 30, 40, TestLocalTime_Constructors.BUDDHIST_UTC);
        LocalTime test = new LocalTime(dt);
        TestCase.assertEquals(TestLocalTime_Constructors.BUDDHIST_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object1_DateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalTime test = new LocalTime(date, TestLocalTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString_DateTimeZoneLondon() throws Throwable {
        LocalTime test = new LocalTime("04:20", TestLocalTime_Constructors.LONDON);
        TestCase.assertEquals(4, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
    }

    public void testConstructor_ObjectString_DateTimeZoneTokyo() throws Throwable {
        LocalTime test = new LocalTime("04:20", TestLocalTime_Constructors.TOKYO);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
    }

    public void testConstructor_ObjectString_DateTimeZoneNewYork() throws Throwable {
        LocalTime test = new LocalTime("04:20", TestLocalTime_Constructors.NEW_YORK);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
    }

    public void testConstructor_nullObject_DateTimeZone() throws Throwable {
        LocalTime test = new LocalTime(((Object) (null)), TestLocalTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_Object_nullDateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalTime test = new LocalTime(date, ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_nullDateTimeZone() throws Throwable {
        LocalTime test = new LocalTime(((Object) (null)), ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object1_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalTime test = new LocalTime(date, TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_Object2_Chronology() throws Throwable {
        LocalTime test = new LocalTime("T10:20");
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new LocalTime("T1020");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_nullObject_Chronology() throws Throwable {
        LocalTime test = new LocalTime(((Object) (null)), TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalTime test = new LocalTime(date, ((Chronology) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_nullChronology() throws Throwable {
        LocalTime test = new LocalTime(((Object) (null)), ((Chronology) (null)));
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestLocalTime_Constructors.OFFSET_LONDON)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_int_int() throws Throwable {
        LocalTime test = new LocalTime(10, 20);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new LocalTime((-1), 20);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(24, 20);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int() throws Throwable {
        LocalTime test = new LocalTime(10, 20, 30);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new LocalTime((-1), 20, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(24, 20, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, (-1), 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 60, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int_int() throws Throwable {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new LocalTime((-1), 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(24, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, (-1), 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 60, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, (-1), 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 60, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 30, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 30, 1000);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int_int_Chronology() throws Throwable {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Constructors.JULIAN_LONDON);
        TestCase.assertEquals(TestLocalTime_Constructors.JULIAN_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new LocalTime((-1), 20, 30, 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(24, 20, 30, 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, (-1), 30, 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 60, 30, 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, (-1), 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 60, 40, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 30, (-1), TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalTime(10, 20, 30, 1000, TestLocalTime_Constructors.JULIAN_LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int_int_nullChronology() throws Throwable {
        LocalTime test = new LocalTime(10, 20, 30, 40, null);
        TestCase.assertEquals(TestLocalTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }
}

