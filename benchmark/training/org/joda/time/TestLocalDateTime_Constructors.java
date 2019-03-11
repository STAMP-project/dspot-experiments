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
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for LocalDateTime.
 *
 * @author Stephen Colebourne
 */
public class TestLocalDateTime_Constructors extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone MOSCOW = DateTimeZone.forID("Europe/Moscow");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestLocalDateTime_Constructors.PARIS);

    private static final Chronology GREGORIAN_MOSCOW = GregorianChronology.getInstance(TestLocalDateTime_Constructors.MOSCOW);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private static final int OFFSET_PARIS = (TestLocalDateTime_Constructors.PARIS.getOffset(0L)) / (MILLIS_PER_HOUR);

    private static final int OFFSET_MOSCOW = (TestLocalDateTime_Constructors.MOSCOW.getOffset(0L)) / (MILLIS_PER_HOUR);

    private long MILLIS_OF_DAY = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME_NOW = (((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY)) + (MILLIS_OF_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestLocalDateTime_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new LocalDateTime(2010, 6, 30, 1, 20), LocalDateTime.parse("2010-06-30T01:20"));
        TestCase.assertEquals(new LocalDateTime(2010, 1, 2, 14, 50, 30, 432), LocalDateTime.parse("2010-002T14:50:30.432"));
    }

    public void testParse_formatter() throws Throwable {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy--dd MM HH").withChronology(ISOChronology.getInstance(TestLocalDateTime_Constructors.PARIS));
        TestCase.assertEquals(new LocalDateTime(2010, 6, 30, 13, 0), LocalDateTime.parse("2010--30 06 13", f));
    }

    // -----------------------------------------------------------------------
    public void testFactory_fromCalendarFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime(1970, 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromCalendarFields(cal));
    }

    public void testFactory_fromCalendarFields_beforeYearZero1() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1, 1, 3, 4, 5, 6);
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime(0, 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromCalendarFields(cal));
    }

    public void testFactory_fromCalendarFields_beforeYearZero3() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(3, 1, 3, 4, 5, 6);
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime((-2), 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromCalendarFields(cal));
    }

    public void testFactory_fromCalendarFields_null() throws Exception {
        try {
            LocalDateTime.fromCalendarFields(((Calendar) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_fromDateFields_after1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime(1970, 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_fromDateFields_before1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1969, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime(1969, 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_fromDateFields_beforeYearZero1() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1, 1, 3, 4, 5, 6);
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime(0, 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_fromDateFields_beforeYearZero3() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(3, 1, 3, 4, 5, 6);
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        cal.set(Calendar.MILLISECOND, 7);
        LocalDateTime expected = new LocalDateTime((-2), 2, 3, 4, 5, 6, 7);
        TestCase.assertEquals(expected, LocalDateTime.fromDateFields(cal.getTime()));
    }

    public void testFactory_fromDateFields_null() throws Exception {
        try {
            LocalDateTime.fromDateFields(((Date) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor() throws Throwable {
        LocalDateTime test = new LocalDateTime();
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalDateTime.now());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 0, 0, TestLocalDateTime_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Constructors.LONDON);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(8, test.getDayOfMonth());
        TestCase.assertEquals(23, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalDateTime.now(TestLocalDateTime_Constructors.LONDON));
        test = new LocalDateTime(TestLocalDateTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalDateTime.now(TestLocalDateTime_Constructors.PARIS));
    }

    public void testConstructor_nullDateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Chronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(test, LocalDateTime.now(TestLocalDateTime_Constructors.GREGORIAN_PARIS));
    }

    public void testConstructor_nullChronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Chronology) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME1);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_long2() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME2);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
        TestCase.assertEquals((14 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(28, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1_DateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME1, TestLocalDateTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_long2_DateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME2, TestLocalDateTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
        TestCase.assertEquals((14 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(28, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_long_nullDateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME1, ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1_Chronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME1, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_long2_Chronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME2, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
        TestCase.assertEquals((14 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(28, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_long_nullChronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(TEST_TIME1, ((Chronology) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object1() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalDateTime test = new LocalDateTime(date);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Object) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        LocalDateTime test = new LocalDateTime("1972-04-06");
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString2() throws Throwable {
        LocalDateTime test = new LocalDateTime("1972-037");
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(2, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString3() throws Throwable {
        LocalDateTime test = new LocalDateTime("1972-04-06T10:20:30.040");
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString4() throws Throwable {
        LocalDateTime test = new LocalDateTime("1972-04-06T10:20");
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new LocalDateTime("1970-04-06T+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new LocalDateTime("1970-04-06T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new LocalDateTime("T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new LocalDateTime("T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx5() throws Throwable {
        try {
            new LocalDateTime("10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx6() throws Throwable {
        try {
            new LocalDateTime("10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectLocalDateTime() throws Throwable {
        LocalDateTime dt = new LocalDateTime(1970, 5, 6, 10, 20, 30, 40, TestLocalDateTime_Constructors.BUDDHIST_UTC);
        LocalDateTime test = new LocalDateTime(dt);
        TestCase.assertEquals(TestLocalDateTime_Constructors.BUDDHIST_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectLocalDate() throws Throwable {
        LocalDate date = new LocalDate(1970, 5, 6);
        try {
            new LocalDateTime(date);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectLocalTime() throws Throwable {
        LocalTime time = new LocalTime(10, 20, 30, 40);
        try {
            new LocalDateTime(time);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object_DateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalDateTime test = new LocalDateTime(date, TestLocalDateTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_Object_DateTimeZoneMoscow() throws Throwable {
        LocalDateTime test = new LocalDateTime("1970-04-06T12:24:00", TestLocalDateTime_Constructors.MOSCOW);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_Object_DateTimeZoneMoscowBadDateTime() throws Throwable {
        // 1981-03-31T23:59:59.999+03:00 followed by 1981-04-01T01:00:00.000+04:00
        // 1981-09-30T23:59:59.999+04:00 followed by 1981-09-30T23:00:00.000+03:00
        // when a DST non-existing time is passed in, it should still work (ie. zone ignored)
        LocalDateTime test = new LocalDateTime("1981-04-01T00:30:00", TestLocalDateTime_Constructors.MOSCOW);// doesnt exist

        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1981, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(30, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_DateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Object) (null)), TestLocalDateTime_Constructors.PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_Object_nullDateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalDateTime test = new LocalDateTime(date, ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_nullDateTimeZone() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Object) (null)), ((DateTimeZone) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalDateTime test = new LocalDateTime(date, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_Object_Chronology_crossChronology() throws Throwable {
        LocalDateTime input = new LocalDateTime(1970, 4, 6, 12, 30, 0, 0, TestLocalDateTime_Constructors.ISO_UTC);
        LocalDateTime test = new LocalDateTime(input, TestLocalDateTime_Constructors.BUDDHIST_UTC);
        TestCase.assertEquals(TestLocalDateTime_Constructors.BUDDHIST_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(30, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_Object_ChronologyMoscow() throws Throwable {
        LocalDateTime test = new LocalDateTime("1970-04-06T12:24:00", TestLocalDateTime_Constructors.GREGORIAN_MOSCOW);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals(12, test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_Object_ChronologyMoscowBadDateTime() throws Throwable {
        // 1981-03-31T23:59:59.999+03:00 followed by 1981-04-01T01:00:00.000+04:00
        // 1981-09-30T23:59:59.999+04:00 followed by 1981-09-30T23:00:00.000+03:00
        // when a DST non-existing time is passed in, it should still work (ie. zone ignored)
        LocalDateTime test = new LocalDateTime("1981-04-01T00:30:00", TestLocalDateTime_Constructors.GREGORIAN_MOSCOW);// doesnt exist

        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1981, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(30, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_Chronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Object) (null)), TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_PARIS)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        LocalDateTime test = new LocalDateTime(date, ((Chronology) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
        TestCase.assertEquals((12 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(24, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_nullObject_nullChronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(((Object) (null)), ((Chronology) (null)));
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals((10 + (TestLocalDateTime_Constructors.OFFSET_MOSCOW)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_int_int_int_int_int() throws Throwable {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_int_int_int_int_int_int() throws Throwable {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20, 30);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_int_int_int_int_int_int_int() throws Throwable {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new LocalDateTime(Integer.MIN_VALUE, 6, 9, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(Integer.MAX_VALUE, 6, 9, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 0, 9, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 13, 9, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 6, 0, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 6, 31, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new LocalDateTime(2005, 7, 31, 10, 20, 30, 40);
        try {
            new LocalDateTime(2005, 7, 32, 10, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int_Chronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestLocalDateTime_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());// PARIS has no effect

        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new LocalDateTime(Integer.MIN_VALUE, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(Integer.MAX_VALUE, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 0, 9, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 13, 9, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 6, 0, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new LocalDateTime(2005, 6, 31, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new LocalDateTime(2005, 7, 31, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
        try {
            new LocalDateTime(2005, 7, 32, 10, 20, 30, 40, TestLocalDateTime_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_int_int_int_nullChronology() throws Throwable {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40, null);
        TestCase.assertEquals(TestLocalDateTime_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }
}

