/**
 * Copyright 2001-2015 Stephen Colebourne
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
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateTime.
 *
 * @author Stephen Colebourne
 */
public class TestDateTime_Constructors extends TestCase {
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

    private DateTimeZone zone = null;

    private Locale locale = null;

    public TestDateTime_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    /**
     * Test now ()
     */
    public void test_now() throws Throwable {
        DateTime test = DateTime.now();
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test now (DateTimeZone)
     */
    public void test_now_DateTimeZone() throws Throwable {
        DateTime test = DateTime.now(TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test now (DateTimeZone=null)
     */
    public void test_now_nullDateTimeZone() throws Throwable {
        try {
            DateTime.now(((DateTimeZone) (null)));
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    /**
     * Test now (Chronology)
     */
    public void test_now_Chronology() throws Throwable {
        DateTime test = DateTime.now(GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test now (Chronology=null)
     */
    public void test_now_nullChronology() throws Throwable {
        try {
            DateTime.now(((Chronology) (null)));
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new DateTime(2010, 6, 30, 1, 20, ISOChronology.getInstance(DateTimeZone.forOffsetHours(2))), DateTime.parse("2010-06-30T01:20+02:00"));
        TestCase.assertEquals(new DateTime(2010, 1, 2, 14, 50, ISOChronology.getInstance(TestDateTime_Constructors.LONDON)), DateTime.parse("2010-002T14:50"));
    }

    public void testParse_noFormatter_vs_constructor_noOffset() throws Throwable {
        DateTime parsed = DateTime.parse("2010-06-30T01:20");
        DateTime constructed = new DateTime("2010-06-30T01:20");
        TestCase.assertEquals(constructed, parsed);
        TestCase.assertEquals(DateTimeZone.getDefault(), constructed.getZone());
        TestCase.assertEquals(DateTimeZone.getDefault(), parsed.getZone());
    }

    public void testParse_noFormatter_vs_constructor_correctOffset() throws Throwable {
        DateTime parsed = DateTime.parse("2010-06-30T01:20+01:00");
        DateTime constructed = new DateTime("2010-06-30T01:20+01:00");
        TestCase.assertEquals(DateTimeZone.getDefault(), constructed.getZone());
        TestCase.assertEquals(DateTimeZone.forOffsetHours(1), parsed.getZone());
    }

    public void testParse_formatter() throws Throwable {
        DateTimeFormatter f = DateTimeFormat.forPattern("yyyy--dd MM HH").withChronology(ISOChronology.getInstance(TestDateTime_Constructors.PARIS));
        TestCase.assertEquals(new DateTime(2010, 6, 30, 13, 0, ISOChronology.getInstance(TestDateTime_Constructors.PARIS)), DateTime.parse("2010--30 06 13", f));
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor() throws Throwable {
        DateTime test = new DateTime();
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (DateTimeZone)
     */
    public void testConstructor_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (DateTimeZone=null)
     */
    public void testConstructor_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Chronology)
     */
    public void testConstructor_Chronology() throws Throwable {
        DateTime test = new DateTime(GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Chronology=null)
     */
    public void testConstructor_nullChronology() throws Throwable {
        DateTime test = new DateTime(((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (long)
     */
    public void testConstructor_long1() throws Throwable {
        DateTime test = new DateTime(TEST_TIME1);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (long)
     */
    public void testConstructor_long2() throws Throwable {
        DateTime test = new DateTime(TEST_TIME2);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME2, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone)
     */
    public void testConstructor_long1_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(TEST_TIME1, TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone)
     */
    public void testConstructor_long2_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(TEST_TIME2, TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME2, test.getMillis());
    }

    /**
     * Test constructor (long, DateTimeZone=null)
     */
    public void testConstructor_long_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(TEST_TIME1, ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long1_Chronology() throws Throwable {
        DateTime test = new DateTime(TEST_TIME1, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology)
     */
    public void testConstructor_long2_Chronology() throws Throwable {
        DateTime test = new DateTime(TEST_TIME2, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME2, test.getMillis());
    }

    /**
     * Test constructor (long, Chronology=null)
     */
    public void testConstructor_long_nullChronology() throws Throwable {
        DateTime test = new DateTime(TEST_TIME1, ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object() throws Throwable {
        Date date = new Date(TEST_TIME1);
        DateTime test = new DateTime(date);
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (Object)
     */
    public void testConstructor_invalidObject() throws Throwable {
        try {
            new DateTime(new Object());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null)
     */
    public void testConstructor_nullObject() throws Throwable {
        DateTime test = new DateTime(((Object) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Object=null)
     */
    public void testConstructor_badconverterObject() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateTime test = new DateTime(new Integer(0));
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals(0L, test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    public void testConstructor_ObjectString1() throws Throwable {
        DateTime test = new DateTime("1972-12-03");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString2() throws Throwable {
        DateTime test = new DateTime("2006-06-03T+14:00");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(2006, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(2, test.getDayOfMonth());// timezone

        TestCase.assertEquals(11, test.getHourOfDay());// test zone is +1, so shift back (14 - 1) hours from midnight

        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString3() throws Throwable {
        DateTime test = new DateTime("1972-12-03T10:20:30.040");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(1972, test.getYear());
        TestCase.assertEquals(12, test.getMonthOfYear());
        TestCase.assertEquals(3, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString4() throws Throwable {
        DateTime test = new DateTime("2006-06-03T10:20:30.040+14:00");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(2006, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(2, test.getDayOfMonth());// timezone

        TestCase.assertEquals(21, test.getHourOfDay());// test zone is +1, so shift back (14 - 1) hours from 10am

        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString5() throws Throwable {
        DateTime test = new DateTime("T10:20:30.040");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(1, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString6() throws Throwable {
        DateTime test = new DateTime("T10:20:30.040+14:00");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(1969, test.getYear());// timezone

        TestCase.assertEquals(12, test.getMonthOfYear());// timezone

        TestCase.assertEquals(31, test.getDayOfMonth());// timezone

        TestCase.assertEquals(21, test.getHourOfDay());// test zone is +1, so shift back (14 - 1) hours from 10am

        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectString7() throws Throwable {
        DateTime test = new DateTime("10");
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(10, test.getYear());
        TestCase.assertEquals(1, test.getMonthOfYear());
        TestCase.assertEquals(1, test.getDayOfMonth());
        TestCase.assertEquals(0, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals(0, test.getMillisOfSecond());
    }

    public void testConstructor_ObjectStringEx1() throws Throwable {
        try {
            new DateTime("10:20:30.040");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testConstructor_ObjectStringEx2() throws Throwable {
        try {
            new DateTime("10:20:30.040+14:00");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_Object_DateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        DateTime test = new DateTime(date, TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_invalidObject_DateTimeZone() throws Throwable {
        try {
            new DateTime(new Object(), TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null, DateTimeZone)
     */
    public void testConstructor_nullObject_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(((Object) (null)), TestDateTime_Constructors.PARIS);
        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone=null)
     */
    public void testConstructor_Object_nullDateTimeZone() throws Throwable {
        Date date = new Date(TEST_TIME1);
        DateTime test = new DateTime(date, ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (Object=null, DateTimeZone=null)
     */
    public void testConstructor_nullObject_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(((Object) (null)), ((DateTimeZone) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Object, DateTimeZone)
     */
    public void testConstructor_badconverterObject_DateTimeZone() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateTime test = new DateTime(new Integer(0), GregorianChronology.getInstance());
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals(0L, test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_Object_Chronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        DateTime test = new DateTime(date, GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_invalidObject_Chronology() throws Throwable {
        try {
            new DateTime(new Object(), GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (Object=null, Chronology)
     */
    public void testConstructor_nullObject_Chronology() throws Throwable {
        DateTime test = new DateTime(((Object) (null)), GregorianChronology.getInstance());
        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology=null)
     */
    public void testConstructor_Object_nullChronology() throws Throwable {
        Date date = new Date(TEST_TIME1);
        DateTime test = new DateTime(date, ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME1, test.getMillis());
    }

    /**
     * Test constructor (Object=null, Chronology=null)
     */
    public void testConstructor_nullObject_nullChronology() throws Throwable {
        DateTime test = new DateTime(((Object) (null)), ((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (Object, Chronology)
     */
    public void testConstructor_badconverterObject_Chronology() throws Throwable {
        try {
            ConverterManager.getInstance().addInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
            DateTime test = new DateTime(new Integer(0), GregorianChronology.getInstance());
            TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
            TestCase.assertEquals(0L, test.getMillis());
        } finally {
            ConverterManager.getInstance().removeInstantConverter(MockZeroNullIntegerConverter.INSTANCE);
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int, int, int, int)
     */
    public void testConstructor_int_int_int_int_int() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0);// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TestDateTime_Constructors.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, DateTimeZone)
     */
    public void testConstructor_int_int_int_int_int_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 2, 0, TestDateTime_Constructors.PARIS);// +02:00

        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, DateTimeZone=null)
     */
    public void testConstructor_int_int_int_int_int_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, ((DateTimeZone) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_int_int_Chronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, GregorianChronology.getInstance());// +01:00

        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_int_int_nullChronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, ((Chronology) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int, int, int, int, int)
     */
    public void testConstructor_int_int_int_int_int_int() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0);// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TestDateTime_Constructors.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, int, DateTimeZone)
     */
    public void testConstructor_int_int_int_int_int_int_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 2, 0, 0, TestDateTime_Constructors.PARIS);// +02:00

        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, int, DateTimeZone=null)
     */
    public void testConstructor_int_int_int_int_int_int_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, ((DateTimeZone) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_int_int_int_Chronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, GregorianChronology.getInstance());// +01:00

        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_int_int_int_nullChronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, ((Chronology) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (int, int, int)
     */
    public void testConstructor_int_int_int_int_int_int_int() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, 0);// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TestDateTime_Constructors.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
        try {
            new DateTime(Integer.MIN_VALUE, 6, 9, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(Integer.MAX_VALUE, 6, 9, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 0, 9, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 13, 9, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 0, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 31, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateTime(2002, 7, 31, 0, 0, 0, 0);
        try {
            new DateTime(2002, 7, 32, 0, 0, 0, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, DateTimeZone)
     */
    public void testConstructor_int_int_int_int_int_int_int_DateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 2, 0, 0, 0, TestDateTime_Constructors.PARIS);// +02:00

        TestCase.assertEquals(ISOChronology.getInstance(TestDateTime_Constructors.PARIS), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
        try {
            new DateTime(Integer.MIN_VALUE, 6, 9, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(Integer.MAX_VALUE, 6, 9, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 0, 9, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 13, 9, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 0, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 31, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateTime(2002, 7, 31, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
        try {
            new DateTime(2002, 7, 32, 0, 0, 0, 0, TestDateTime_Constructors.PARIS);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, DateTimeZone=null)
     */
    public void testConstructor_int_int_int_int_int_int_int_nullDateTimeZone() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, 0, ((DateTimeZone) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    /**
     * Test constructor (int, int, int, Chronology)
     */
    public void testConstructor_int_int_int_int_int_int_int_Chronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, 0, GregorianChronology.getInstance());// +01:00

        TestCase.assertEquals(GregorianChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
        try {
            new DateTime(Integer.MIN_VALUE, 6, 9, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(Integer.MAX_VALUE, 6, 9, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 0, 9, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 13, 9, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 0, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            new DateTime(2002, 6, 31, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        new DateTime(2002, 7, 31, 0, 0, 0, 0, GregorianChronology.getInstance());
        try {
            new DateTime(2002, 7, 32, 0, 0, 0, 0, GregorianChronology.getInstance());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Test constructor (int, int, int, Chronology=null)
     */
    public void testConstructor_int_int_int_int_int_int_int_nullChronology() throws Throwable {
        DateTime test = new DateTime(2002, 6, 9, 1, 0, 0, 0, ((Chronology) (null)));// +01:00

        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }
}

