/**
 * Copyright 2001-2005 Stephen Colebourne
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


import Duration.ZERO;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;
import static Duration.ZERO;


/**
 * This class is a JUnit test for Duration.
 *
 * @author Stephen Colebourne
 */
public class TestDuration_Constructors extends TestCase {
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

    public TestDuration_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testZERO() throws Throwable {
        Duration test = ZERO;
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new Duration(3200), Duration.parse("PT3.2S"));
        TestCase.assertEquals(new Duration(6000), Duration.parse("PT6S"));
    }

    // -----------------------------------------------------------------------
    public void testFactory_standardDays_long() throws Throwable {
        Duration test = Duration.standardDays(1);
        TestCase.assertEquals((((24L * 60L) * 60L) * 1000L), test.getMillis());
        test = Duration.standardDays(2);
        TestCase.assertEquals(((((2L * 24L) * 60L) * 60L) * 1000L), test.getMillis());
        test = Duration.standardDays(0);
        TestCase.assertSame(ZERO, test);
    }

    // -----------------------------------------------------------------------
    public void testFactory_standardHours_long() throws Throwable {
        Duration test = Duration.standardHours(1);
        TestCase.assertEquals(((60L * 60L) * 1000L), test.getMillis());
        test = Duration.standardHours(2);
        TestCase.assertEquals((((2L * 60L) * 60L) * 1000L), test.getMillis());
        test = Duration.standardHours(0);
        TestCase.assertSame(ZERO, test);
    }

    // -----------------------------------------------------------------------
    public void testFactory_standardMinutes_long() throws Throwable {
        Duration test = Duration.standardMinutes(1);
        TestCase.assertEquals((60L * 1000L), test.getMillis());
        test = Duration.standardMinutes(2);
        TestCase.assertEquals(((2L * 60L) * 1000L), test.getMillis());
        test = Duration.standardMinutes(0);
        TestCase.assertSame(ZERO, test);
    }

    // -----------------------------------------------------------------------
    public void testFactory_standardSeconds_long() throws Throwable {
        Duration test = Duration.standardSeconds(1);
        TestCase.assertEquals(1000L, test.getMillis());
        test = Duration.standardSeconds(2);
        TestCase.assertEquals((2L * 1000L), test.getMillis());
        test = Duration.standardSeconds(0);
        TestCase.assertSame(ZERO, test);
    }

    // -----------------------------------------------------------------------
    public void testFactory_millis_long() throws Throwable {
        Duration test = Duration.millis(1);
        TestCase.assertEquals(1L, test.getMillis());
        test = Duration.millis(2);
        TestCase.assertEquals(2L, test.getMillis());
        test = Duration.millis(0);
        TestCase.assertSame(ZERO, test);
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Duration test = new Duration(length);
        TestCase.assertEquals(length, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration test = new Duration(dt1.getMillis(), dt2.getMillis());
        TestCase.assertEquals(((dt2.getMillis()) - (dt1.getMillis())), test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RI_RI1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration test = new Duration(dt1, dt2);
        TestCase.assertEquals(((dt2.getMillis()) - (dt1.getMillis())), test.getMillis());
    }

    public void testConstructor_RI_RI2() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        Duration test = new Duration(dt1, dt2);
        TestCase.assertEquals(((dt2.getMillis()) - (TEST_TIME_NOW)), test.getMillis());
    }

    public void testConstructor_RI_RI3() throws Throwable {
        DateTime dt1 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        DateTime dt2 = null;// 2002-06-09T01:00+01:00

        Duration test = new Duration(dt1, dt2);
        TestCase.assertEquals(((TEST_TIME_NOW) - (dt1.getMillis())), test.getMillis());
    }

    public void testConstructor_RI_RI4() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = null;// 2002-06-09T01:00+01:00

        Duration test = new Duration(dt1, dt2);
        TestCase.assertEquals(0L, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object1() throws Throwable {
        Duration test = new Duration("PT72.345S");
        TestCase.assertEquals(72345, test.getMillis());
    }

    public void testConstructor_Object2() throws Throwable {
        Duration test = new Duration(((Object) (null)));
        TestCase.assertEquals(0L, test.getMillis());
    }

    public void testConstructor_Object3() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Long base = new Long(length);
        Duration test = new Duration(base);
        TestCase.assertEquals(length, test.getMillis());
    }

    public void testConstructor_Object4() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration base = new Duration(dt1, dt2);
        Duration test = new Duration(base);
        TestCase.assertEquals(((dt2.getMillis()) - (dt1.getMillis())), test.getMillis());
    }

    public void testConstructor_Object5() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Interval base = new Interval(dt1, dt2);
        Duration test = new Duration(base);
        TestCase.assertEquals(((dt2.getMillis()) - (dt1.getMillis())), test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor overflow
     */
    public void testConstructor_Overflow1() {
        try {
            Duration test = new Duration((-1), Long.MAX_VALUE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testConstructor_Overflow2() {
        try {
            Duration test = new Duration(Long.MIN_VALUE, 1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testConstructor_Overflow3() {
        try {
            Instant max = new Instant(Long.MAX_VALUE);
            Duration test = new Duration(new Instant((-1)), max);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testConstructor_Overflow4() {
        try {
            Instant min = new Instant(Long.MIN_VALUE);
            Duration test = new Duration(min, new Instant(1));
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }
}

