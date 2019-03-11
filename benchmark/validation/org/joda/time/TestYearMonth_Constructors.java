/**
 * Copyright 2001-2009 Stephen Colebourne
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
 * This class is a Junit unit test for YearMonth.
 *
 * @author Stephen Colebourne
 */
public class TestYearMonth_Constructors extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestYearMonth_Constructors.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestYearMonth_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new YearMonth(2010, 6), YearMonth.parse("2010-06-30"));
        TestCase.assertEquals(new YearMonth(2010, 1), YearMonth.parse("2010-002"));
    }

    public void testParse_formatter() throws Throwable {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy--MM").withChronology(ISOChronology.getInstance(TestYearMonth_Constructors.PARIS));
        TestCase.assertEquals(new YearMonth(2010, 6), YearMonth.parse("2010--06", f));
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromCalendarFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        YearMonth expected = new YearMonth(1970, 2);
        TestCase.assertEquals(expected, YearMonth.fromCalendarFields(cal));
        try {
            YearMonth.fromCalendarFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromDateFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        YearMonth expected = new YearMonth(1970, 2);
        TestCase.assertEquals(expected, YearMonth.fromDateFields(cal.getTime()));
        try {
            YearMonth.fromDateFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        YearMonth test = new YearMonth();
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(test, YearMonth.now());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 30, 23, 59, 0, 0, TestYearMonth_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        YearMonth test = new YearMonth(TestYearMonth_Constructors.LONDON);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(test, YearMonth.now(TestYearMonth_Constructors.LONDON));
        test = new YearMonth(TestYearMonth_Constructors.PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(7, test.getMonthOfYear());
        TestCase.assertEquals(test, YearMonth.now(TestYearMonth_Constructors.PARIS));
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 30, 23, 59, 0, 0, TestYearMonth_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        YearMonth test = new YearMonth(((DateTimeZone) (null)));
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2005, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        YearMonth test = new YearMonth(TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(test, YearMonth.now(TestYearMonth_Constructors.GREGORIAN_PARIS));
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        YearMonth test = new YearMonth(((Chronology) (null)));
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        YearMonth test = new YearMonth(TEST_TIME1);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        YearMonth test = new YearMonth(TEST_TIME2);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        YearMonth test = new YearMonth(TEST_TIME1, TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        YearMonth test = new YearMonth(TEST_TIME2, TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1971, test.getYear());
        TestCase.assertEquals(5, test.getMonthOfYear());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        YearMonth test = new YearMonth(TEST_TIME1, null);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_Object() throws Throwable {
        Date date = new Date(TEST_TIME1);
        YearMonth test = new YearMonth(date);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    public void testConstructor_nullObject() throws Throwable {
        YearMonth test = new YearMonth(((Object) (null)));
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        YearMonth test = new YearMonth("1972-12");
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
    }

    public void testConstructor_ObjectString5() throws Throwable {
        YearMonth test = new YearMonth("10");
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getYear());
        TestCase.assertEquals(1, test.getMonthOfYear());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new YearMonth("T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new YearMonth("T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new YearMonth("10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new YearMonth("10:20:30.040+14:00");
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
        YearMonth test = new YearMonth(date, TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        YearMonth test = new YearMonth(((Object) (null)), TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        YearMonth test = new YearMonth(date, null);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(4, test.getMonthOfYear());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        YearMonth test = new YearMonth(((Object) (null)), null);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int)
     */
    public void testConstructor_int_int() throws Throwable {
        YearMonth test = new YearMonth(1970, 6);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        try {
            new YearMonth(Integer.MIN_VALUE, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(Integer.MAX_VALUE, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(1970, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(1970, 13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, Chronology)
     */
    public void testConstructor_int_int_Chronology() throws Throwable {
        YearMonth test = new YearMonth(1970, 6, TestYearMonth_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestYearMonth_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        try {
            new YearMonth(Integer.MIN_VALUE, 6, TestYearMonth_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(Integer.MAX_VALUE, 6, TestYearMonth_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(1970, 0, TestYearMonth_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new YearMonth(1970, 13, TestYearMonth_Constructors.GREGORIAN_PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, Chronology=null)
     */
    public void testConstructor_int_int_nullChronology() throws Throwable {
        YearMonth test = new YearMonth(1970, 6, null);
        TestCase.assertEquals(TestYearMonth_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
    }
}

