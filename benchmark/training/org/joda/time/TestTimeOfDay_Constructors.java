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
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;
import static TimeOfDay.MIDNIGHT;


/**
 * This class is a Junit unit test for TimeOfDay.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestTimeOfDay_Constructors extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final ISOChronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final int OFFSET = 1;

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME1 = (((1L * (MILLIS_PER_HOUR)) + (2L * (MILLIS_PER_MINUTE))) + (3L * (MILLIS_PER_SECOND))) + 4L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestTimeOfDay_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstantMidnight() throws Throwable {
        TimeOfDay test = MIDNIGHT;
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromCalendarFields() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        TimeOfDay expected = new TimeOfDay(4, 5, 6, 7);
        TestCase.assertEquals(expected, TimeOfDay.fromCalendarFields(cal));
        try {
            TimeOfDay.fromCalendarFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testFactory_FromDateFields_after1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1970, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        TimeOfDay expected = new TimeOfDay(4, 5, 6, 7);
        TestCase.assertEquals(expected, TimeOfDay.fromDateFields(cal.getTime()));
    }

    public void testFactory_FromDateFields_before1970() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(1969, 1, 3, 4, 5, 6);
        cal.set(Calendar.MILLISECOND, 7);
        TimeOfDay expected = new TimeOfDay(4, 5, 6, 7);
        TestCase.assertEquals(expected, TimeOfDay.fromDateFields(cal.getTime()));
    }

    public void testFactory_FromDateFields_null() throws Exception {
        try {
            TimeOfDay.fromDateFields(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test factory (long)
     */
    public void testFactoryMillisOfDay_long1() throws Throwable {
        TimeOfDay test = TimeOfDay.fromMillisOfDay(TEST_TIME1);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test factory (long, Chronology)
     */
    public void testFactoryMillisOfDay_long1_Chronology() throws Throwable {
        TimeOfDay test = TimeOfDay.fromMillisOfDay(TEST_TIME1, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test factory (long, Chronology=null)
     */
    public void testFactoryMillisOfDay_long_nullChronology() throws Throwable {
        TimeOfDay test = TimeOfDay.fromMillisOfDay(TEST_TIME1, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        TimeOfDay test = new TimeOfDay();
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 30, 40, TestTimeOfDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Constructors.LONDON);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(23, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        test = new TimeOfDay(TestTimeOfDay_Constructors.PARIS);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime dt = new DateTime(2005, 6, 8, 23, 59, 30, 40, TestTimeOfDay_Constructors.LONDON);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        // 23:59 in London is 00:59 the following day in Paris
        TimeOfDay test = new TimeOfDay(((DateTimeZone) (null)));
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(23, test.getHourOfDay());
        TestCase.assertEquals(59, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(((Chronology) (null)));
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        TimeOfDay test = new TimeOfDay(TEST_TIME1);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        TimeOfDay test = new TimeOfDay(TEST_TIME2);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((5 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(TEST_TIME1, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(TEST_TIME2, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((5 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(6, test.getMinuteOfHour());
        TestCase.assertEquals(7, test.getSecondOfMinute());
        TestCase.assertEquals(8, test.getMillisOfSecond());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(TEST_TIME1, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object1() throws Throwable {
        Date date = new Date(TEST_TIME1);
        TimeOfDay test = new TimeOfDay(date);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object2() throws Throwable {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date(TEST_TIME1));
        TimeOfDay test = new TimeOfDay(cal);
        TestCase.assertEquals(GJChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object=null)
     */
    public void testConstructor_nullObject() throws Throwable {
        TimeOfDay test = new TimeOfDay(((Object) (null)));
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object)
     */
    public void testConstructor_todObject() throws Throwable {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, CopticChronology.getInstance(TestTimeOfDay_Constructors.PARIS));
        TimeOfDay test = new TimeOfDay(base);
        TestCase.assertEquals(CopticChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString1() throws Throwable {
        TimeOfDay test = new TimeOfDay("10:20:30.040");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString2() throws Throwable {
        TimeOfDay test = new TimeOfDay("10:20:30.040+04:00");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(((10 + (TestTimeOfDay_Constructors.OFFSET)) - 4), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString3() throws Throwable {
        TimeOfDay test = new TimeOfDay("T10:20:30.040");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString4() throws Throwable {
        TimeOfDay test = new TimeOfDay("T10:20:30.040+04:00");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(((10 + (TestTimeOfDay_Constructors.OFFSET)) - 4), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString5() throws Throwable {
        TimeOfDay test = new TimeOfDay("10:20");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString6() throws Throwable {
        TimeOfDay test = new TimeOfDay("10");
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new TimeOfDay("1970-04-06");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new TimeOfDay("1970-04-06T+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx3() throws Throwable {
        try {
            new TimeOfDay("1970-04-06T10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_ObjectStringEx4() throws Throwable {
        try {
            new TimeOfDay("1970-04-06T10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_Object_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        TimeOfDay test = new TimeOfDay(date, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor2_Object_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay("T10:20");
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new TimeOfDay("T1020");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(((Object) (null)), JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        TimeOfDay test = new TimeOfDay(date, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((1 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(2, test.getMinuteOfHour());
        TestCase.assertEquals(3, test.getSecondOfMinute());
        TestCase.assertEquals(4, test.getMillisOfSecond());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(((Object) (null)), null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals((10 + (TestTimeOfDay_Constructors.OFFSET)), test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int)
     */
    public void testConstructor_int_int() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology)
     */
    public void testConstructor_int_int_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1), JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    /**
     * Test constructor (int, int, int)
     */
    public void testConstructor_int_int_int() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1), 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60, 30);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20, 30, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20, 30, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1), 30, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60, 30, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, (-1), JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 60, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    /**
     * Test constructor (int, int, int, int)
     */
    public void testConstructor_int_int_int_int() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1), 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60, 30, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, (-1), 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 60, 40);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 30, (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 30, 1000);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_int_Chronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, JulianChronology.getInstance());
        TestCase.assertEquals(JulianChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        try {
            new TimeOfDay((-1), 20, 30, 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(24, 20, 30, 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, (-1), 30, 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 60, 30, 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, (-1), 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 60, 40, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 30, (-1), JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new TimeOfDay(10, 20, 30, 1000, JulianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_int_nullChronology() throws Throwable {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, null);
        TestCase.assertEquals(TestTimeOfDay_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }
}

