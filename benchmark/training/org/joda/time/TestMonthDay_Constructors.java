/**
 * Copyright 2001-2010 Stephen Colebourne
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
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for MonthDay. Based on {@link TestYearMonth_Constuctors}
 */
public class TestMonthDay_Constructors extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestMonthDay_Constructors.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestMonthDay_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new MonthDay(6, 30), MonthDay.parse("--06-30"));
        TestCase.assertEquals(new MonthDay(2, 29), MonthDay.parse("--02-29"));
        TestCase.assertEquals(new MonthDay(6, 30), MonthDay.parse("2010-06-30"));
        TestCase.assertEquals(new MonthDay(1, 2), MonthDay.parse("2010-002"));
    }

    public void testParse_formatter() throws Throwable {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy--dd MM").withChronology(ISOChronology.getInstance(TestMonthDay_Constructors.PARIS));
        TestCase.assertEquals(new MonthDay(6, 30), MonthDay.parse("2010--30 06", f));
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromCalendarFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        MonthDay expected = new MonthDay(2, 3);
        TestCase.assertEquals(expected, MonthDay.fromCalendarFields(cal));
        try {
            MonthDay.fromCalendarFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromDateFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        MonthDay expected = new MonthDay(2, 3);
        TestCase.assertEquals(expected, MonthDay.fromDateFields(cal.getTime()));
        try {
            MonthDay.fromDateFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        MonthDay test = new MonthDay();
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(test, MonthDay.now());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 30, 23, 59, 0, 0, TestMonthDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        MonthDay test = new MonthDay(TestMonthDay_Constructors.LONDON);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(30, test.getDayOfMonth());
        TestCase.assertEquals(test, MonthDay.now(TestMonthDay_Constructors.LONDON));
        test = new MonthDay(TestMonthDay_Constructors.PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(7, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(test, MonthDay.now(TestMonthDay_Constructors.PARIS));
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 30, 23, 59, 0, 0, TestMonthDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        MonthDay test = new MonthDay(((DateTimeZone) (null)));
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(30, test.getDayOfMonth());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        MonthDay test = new MonthDay(TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(test, MonthDay.now(TestMonthDay_Constructors.GREGORIAN_PARIS));
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        MonthDay test = new MonthDay(((Chronology) (null)));
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        MonthDay test = new MonthDay(TEST_TIME1);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        MonthDay test = new MonthDay(TEST_TIME2);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        MonthDay test = new MonthDay(TEST_TIME1, TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        MonthDay test = new MonthDay(TEST_TIME2, TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        MonthDay test = new MonthDay(TEST_TIME1, null);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object() throws Throwable {
        Date date = new Date(TEST_TIME1);
        MonthDay test = new MonthDay(date);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    public void testConstructor_nullObject() throws Throwable {
        MonthDay test = new MonthDay(((Object) (null)));
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        MonthDay test = new MonthDay("1972-12");
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
    }

    public void testConstructor_ObjectString5() throws Throwable {
        MonthDay test = new MonthDay("10");
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new MonthDay("T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new MonthDay("T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new MonthDay("10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new MonthDay("10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_Object_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        MonthDay test = new MonthDay(date, TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        MonthDay test = new MonthDay(((Object) (null)), TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        MonthDay test = new MonthDay(date, null);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        MonthDay test = new MonthDay(((Object) (null)), null);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int)
     */
    public void testConstructor_int_int() throws Throwable {
        MonthDay test = new MonthDay(6, 30);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(30, test.getDayOfMonth());
        try {
            new MonthDay(Integer.MIN_VALUE, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(Integer.MAX_VALUE, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(1970, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(1970, 13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, Chronology)
     */
    public void testConstructor_int_int_Chronology() throws Throwable {
        MonthDay test = new MonthDay(6, 30, TestMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(30, test.getDayOfMonth());
        try {
            new MonthDay(Integer.MIN_VALUE, 6, TestMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(Integer.MAX_VALUE, 6, TestMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(1970, 0, TestMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new MonthDay(1970, 13, TestMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, Chronology=null)
     */
    public void testConstructor_int_int_nullChronology() throws Throwable {
        MonthDay test = new MonthDay(6, 30, null);
        TestCase.assertEquals(TestMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(30, test.getDayOfMonth());
    }
}

