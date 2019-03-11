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
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for YearMonthDay.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestYearMonthDay_Constructors extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestYearMonthDay_Constructors.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestYearMonthDay_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromCalendarFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        YearMonthDay expected = new YearMonthDay(1970, 2, 3);
        TestCase.assertEquals(expected, YearMonthDay.fromCalendarFields(cal));
        try {
            YearMonthDay.fromCalendarFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromDateFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        YearMonthDay expected = new YearMonthDay(1970, 2, 3);
        TestCase.assertEquals(expected, YearMonthDay.fromDateFields(cal.getTime()));
        try {
            YearMonthDay.fromDateFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        YearMonthDay test = new YearMonthDay();
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 0, 0, TestYearMonthDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        YearMonthDay test = new YearMonthDay(TestYearMonthDay_Constructors.LONDON);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(8, test.getDayOfMonth());
        test = new YearMonthDay(TestYearMonthDay_Constructors.PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 0, 0, TestYearMonthDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        YearMonthDay test = new YearMonthDay(((DateTimeZone) (null)));
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(8, test.getDayOfMonth());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(((Chronology) (null)));
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        YearMonthDay test = new YearMonthDay(TEST_TIME1);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        YearMonthDay test = new YearMonthDay(TEST_TIME2);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(TEST_TIME1, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(TEST_TIME2, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
        TestCase.assertEquals(7, test.getDayOfMonth());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(TEST_TIME1, null);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object() throws Throwable {
        Date date = new Date(TEST_TIME1);
        YearMonthDay test = new YearMonthDay(date);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    public void testConstructor_nullObject() throws Throwable {
        YearMonthDay test = new YearMonthDay(((Object) (null)));
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        YearMonthDay test = new YearMonthDay("1972-12-03");
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
    }

    public void testConstructor_ObjectString2() throws Throwable {
        YearMonthDay test = new YearMonthDay("1972-12-03T+14:00");
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(2, test.getDayOfMonth());// timezone

    }

    public void testConstructor_ObjectString3() throws Throwable {
        YearMonthDay test = new YearMonthDay("1972-12-03T10:20:30.040");
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
    }

    public void testConstructor_ObjectString4() throws Throwable {
        YearMonthDay test = new YearMonthDay("1972-12-03T10:20:30.040+14:00");
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(2, test.getDayOfMonth());// timezone

    }

    public void testConstructor_ObjectString5() throws Throwable {
        YearMonthDay test = new YearMonthDay("10");
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getYear());
        TestCase.assertEquals(1, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new YearMonthDay("T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new YearMonthDay("T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new YearMonthDay("10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new YearMonthDay("10:20:30.040+14:00");
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
        YearMonthDay test = new YearMonthDay(date, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(((Object) (null)), TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        YearMonthDay test = new YearMonthDay(date, null);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
        TestCase.assertEquals(6, test.getDayOfMonth());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(((Object) (null)), null);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int, int)
     */
    public void testConstructor_int_int_int() throws Throwable {
        YearMonthDay test = new YearMonthDay(1970, 6, 9);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        try {
            new YearMonthDay(Integer.MIN_VALUE, 6, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(Integer.MAX_VALUE, 6, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 0, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 13, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 6, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 6, 31);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new YearMonthDay(1970, 7, 31);
        try {
            new YearMonthDay(1970, 7, 32);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_Chronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(1970, 6, 9, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonthDay_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        try {
            new YearMonthDay(Integer.MIN_VALUE, 6, 9, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(Integer.MAX_VALUE, 6, 9, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 0, 9, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 13, 9, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 6, 0, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonthDay(1970, 6, 31, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new YearMonthDay(1970, 7, 31, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
        try {
            new YearMonthDay(1970, 7, 32, TestYearMonthDay_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_nullChronology() throws Throwable {
        YearMonthDay test = new YearMonthDay(1970, 6, 9, null);
        TestCase.assertEquals(TestYearMonthDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }
}

