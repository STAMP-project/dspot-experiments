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


import java.util.Date;
import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.convert.ConverterManager;
import org.joda.time.convert.MockZeroNullIntegerConverter;
import org.joda.time.format.DateTimeFormat;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateMidnight.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestDateMidnight_Constructors extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW_UTC = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME_NOW_LONDON = (TEST_TIME_NOW_UTC) - (MILLIS_PER_HOUR);

    private long TEST_TIME_NOW_PARIS = (TEST_TIME_NOW_UTC) - (2 * (MILLIS_PER_HOUR));

    // 2002-04-05
    private long TEST_TIME1_UTC = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME1_LONDON = (((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) - (MILLIS_PER_HOUR);

    private long TEST_TIME1_PARIS = (((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) - (2 * (MILLIS_PER_HOUR));

    // 2003-05-06
    private long TEST_TIME2_UTC = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2_LONDON = ((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) - (MILLIS_PER_HOUR);

    private long TEST_TIME2_PARIS = ((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) - (2 * (MILLIS_PER_HOUR));

    private DateTimeZone zone = null;

    private Locale locale = null;

    public TestDateMidnight_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW_UTC).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1_UTC).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2_UTC).toString());
    }

    // -----------------------------------------------------------------------
    /**
     * Test now ()
     */
    public void test_now() throws Throwable {
        DateMidnight test = DateMidnight.now();
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test now (DateTimeZone)
     */
    public void test_now_DateTimeZone() throws Throwable {
        DateMidnight test = DateMidnight.now(TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_PARIS, test.getMillis());
    }

    /**
     * Test now (DateTimeZone=null)
     */
    public void test_now_nullDateTimeZone() throws Throwable {
        try {
            DateMidnight.now(((DateTimeZone) (null)));
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    /**
     * Test now (Chronology)
     */
    public void test_now_Chronology() throws Throwable {
        DateMidnight test = DateMidnight.now(GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test now (Chronology=null)
     */
    public void test_now_nullChronology() throws Throwable {
        try {
            DateMidnight.now(((Chronology) (null)));
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new DateMidnight(2010, 6, 30, ISOChronology.getInstance(TestDateMidnight_Constructors.LONDON)), DateMidnight.parse("2010-06-30"));
        TestCase.assertEquals(new DateMidnight(2010, 1, 2, ISOChronology.getInstance(TestDateMidnight_Constructors.LONDON)), DateMidnight.parse("2010-002"));
    }

    public void testParse_formatter() throws Throwable {
        TestCase.assertEquals(new DateMidnight(2010, 6, 30, ISOChronology.getInstance(TestDateMidnight_Constructors.LONDON)), DateMidnight.parse("2010--30 06", DateTimeFormat.forPattern("yyyy--dd MM")));
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        DateMidnight test = new DateMidnight();
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_PARIS, test.getMillis());
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        DateMidnight test = new DateMidnight(GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        DateMidnight test = new DateMidnight(((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME2_UTC);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME2_LONDON, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone)
     */
    public void testConstructor_long1_DateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_PARIS, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone)
     */
    public void testConstructor_long2_DateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME2_UTC, TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME2_PARIS, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone=null)
     */
    public void testConstructor_long_nullDateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME2_UTC, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME2_LONDON, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        DateMidnight test = new DateMidnight(TEST_TIME1_UTC, ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object() throws Throwable {
        Date date = new Date(TEST_TIME1_UTC);
        DateMidnight test = new DateMidnight(date);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object)
     */
    public void testConstructor_invalidObject() throws Throwable {
        try {
            new DateMidnight(new Object());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null)
     */
    public void testConstructor_nullObject() throws Throwable {
        DateMidnight test = new DateMidnight(((Object) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object=null)
     */
    public void testConstructor_badconverterObject() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateMidnight test = new DateMidnight(new Integer(0));
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals((0L - (MILLIS_PER_HOUR)), test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_Object_DateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1_UTC);
        DateMidnight test = new DateMidnight(date, TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_PARIS, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_invalidObject_DateTimeZone() throws Throwable {
        try {
            new DateMidnight(new Object(), TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null, DateTimeZone)
     */
    public void testConstructor_nullObject_DateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(((Object) (null)), TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_PARIS, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone=null)
     */
    public void testConstructor_Object_nullDateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1_UTC);
        DateMidnight test = new DateMidnight(date, ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object=null, DateTimeZone=null)
     */
    public void testConstructor_nullObject_nullDateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(((Object) (null)), ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_badconverterObject_DateTimeZone() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateMidnight test = new DateMidnight(new Integer(0), GregorianChronology.getInstance());
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals((0L - (MILLIS_PER_HOUR)), test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_Object_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1_UTC);
        DateMidnight test = new DateMidnight(date, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_invalidObject_Chronology() throws Throwable {
        try {
            new DateMidnight(new Object(), GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        DateMidnight test = new DateMidnight(((Object) (null)), GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1_UTC);
        DateMidnight test = new DateMidnight(date, ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        DateMidnight test = new DateMidnight(((Object) (null)), ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_badconverterObject_Chronology() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateMidnight test = new DateMidnight(new Integer(0), GregorianChronology.getInstance());
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals((0L - (MILLIS_PER_HOUR)), test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int, int)
     */
    public void testConstructor_int_int_int() throws Throwable {
        DateMidnight test = new DateMidnight(2002, 6, 9);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TestDateMidnight_Constructors.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        try {
            new DateMidnight(Integer.MIN_VALUE, 6, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(Integer.MAX_VALUE, 6, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 0, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 13, 9);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 31);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateMidnight(2002, 7, 31);
        try {
            new DateMidnight(2002, 7, 32);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, DateTimeZone)
     */
    public void testConstructor_int_int_int_DateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(2002, 6, 9, TestDateMidnight_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateMidnight_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_PARIS, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        try {
            new DateMidnight(Integer.MIN_VALUE, 6, 9, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(Integer.MAX_VALUE, 6, 9, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 0, 9, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 13, 9, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 0, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 31, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateMidnight(2002, 7, 31, TestDateMidnight_Constructors.PARIS);
        try {
            new DateMidnight(2002, 7, 32, TestDateMidnight_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, DateTimeZone=null)
     */
    public void testConstructor_int_int_int_nullDateTimeZone() throws Throwable {
        DateMidnight test = new DateMidnight(2002, 6, 9, ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }

    /**
     * Test constructor (int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_Chronology() throws Throwable {
        DateMidnight test = new DateMidnight(2002, 6, 9, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        try {
            new DateMidnight(Integer.MIN_VALUE, 6, 9, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(Integer.MAX_VALUE, 6, 9, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 0, 9, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 13, 9, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateMidnight(2002, 6, 31, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateMidnight(2002, 7, 31, GregorianChronology.getInstance());
        try {
            new DateMidnight(2002, 7, 32, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_nullChronology() throws Throwable {
        DateMidnight test = new DateMidnight(2002, 6, 9, ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW_LONDON, test.getMillis());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
    }
}

