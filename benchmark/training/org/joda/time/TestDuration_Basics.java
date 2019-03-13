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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.base.AbstractDuration;
import org.joda.time.base.BaseDuration;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for Duration.
 *
 * @author Stephen Colebourne
 */
public class TestDuration_Basics extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
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

    public TestDuration_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testGetMillis() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(0, test.getMillis());
        test = new Duration(1234567890L);
        TestCase.assertEquals(1234567890L, test.getMillis());
    }

    public void testEqualsHashCode() {
        Duration test1 = new Duration(123L);
        Duration test2 = new Duration(123L);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        Duration test3 = new Duration(321L);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestDuration_Basics.MockDuration(123L)));
    }

    class MockDuration extends AbstractDuration {
        private final long iValue;

        public MockDuration(long value) {
            super();
            iValue = value;
        }

        public long getMillis() {
            return iValue;
        }
    }

    public void testCompareTo() {
        Duration test1 = new Duration(123L);
        Duration test1a = new Duration(123L);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        Duration test2 = new Duration(321L);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        TestCase.assertEquals((+1), test2.compareTo(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(0, test1.compareTo(new TestDuration_Basics.MockDuration(123L)));
        try {
            test1.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
        // try {
        // test1.compareTo(new Long(123L));
        // fail();
        // } catch (ClassCastException ex) {}
    }

    public void testIsEqual() {
        Duration test1 = new Duration(123L);
        Duration test1a = new Duration(123L);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        Duration test2 = new Duration(321L);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        TestCase.assertEquals(false, test2.isEqual(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(true, test1.isEqual(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(false, test1.isEqual(null));
        TestCase.assertEquals(true, new Duration(0L).isEqual(null));
    }

    public void testIsBefore() {
        Duration test1 = new Duration(123L);
        Duration test1a = new Duration(123L);
        TestCase.assertEquals(false, test1.isShorterThan(test1a));
        TestCase.assertEquals(false, test1a.isShorterThan(test1));
        TestCase.assertEquals(false, test1.isShorterThan(test1));
        TestCase.assertEquals(false, test1a.isShorterThan(test1a));
        Duration test2 = new Duration(321L);
        TestCase.assertEquals(true, test1.isShorterThan(test2));
        TestCase.assertEquals(false, test2.isShorterThan(test1));
        TestCase.assertEquals(false, test2.isShorterThan(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(false, test1.isShorterThan(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(false, test1.isShorterThan(null));
        TestCase.assertEquals(false, new Duration(0L).isShorterThan(null));
    }

    public void testIsAfter() {
        Duration test1 = new Duration(123L);
        Duration test1a = new Duration(123L);
        TestCase.assertEquals(false, test1.isLongerThan(test1a));
        TestCase.assertEquals(false, test1a.isLongerThan(test1));
        TestCase.assertEquals(false, test1.isLongerThan(test1));
        TestCase.assertEquals(false, test1a.isLongerThan(test1a));
        Duration test2 = new Duration(321L);
        TestCase.assertEquals(false, test1.isLongerThan(test2));
        TestCase.assertEquals(true, test2.isLongerThan(test1));
        TestCase.assertEquals(true, test2.isLongerThan(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(false, test1.isLongerThan(new TestDuration_Basics.MockDuration(123L)));
        TestCase.assertEquals(true, test1.isLongerThan(null));
        TestCase.assertEquals(false, new Duration(0L).isLongerThan(null));
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Duration test = new Duration(123L);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Duration result = ((Duration) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testGetStandardSeconds() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(0, test.getStandardSeconds());
        test = new Duration(1L);
        TestCase.assertEquals(0, test.getStandardSeconds());
        test = new Duration(999L);
        TestCase.assertEquals(0, test.getStandardSeconds());
        test = new Duration(1000L);
        TestCase.assertEquals(1, test.getStandardSeconds());
        test = new Duration(1001L);
        TestCase.assertEquals(1, test.getStandardSeconds());
        test = new Duration(1999L);
        TestCase.assertEquals(1, test.getStandardSeconds());
        test = new Duration(2000L);
        TestCase.assertEquals(2, test.getStandardSeconds());
        test = new Duration((-1L));
        TestCase.assertEquals(0, test.getStandardSeconds());
        test = new Duration((-999L));
        TestCase.assertEquals(0, test.getStandardSeconds());
        test = new Duration((-1000L));
        TestCase.assertEquals((-1), test.getStandardSeconds());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        long length = (((((((365L + (2L * 30L)) + (3L * 7L)) + 4L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 845L;
        Duration test = new Duration(length);
        TestCase.assertEquals((((("PT" + (length / 1000)) + ".") + (length % 1000)) + "S"), test.toString());
        TestCase.assertEquals("PT0S", new Duration(0L).toString());
        TestCase.assertEquals("PT10S", new Duration(10000L).toString());
        TestCase.assertEquals("PT1S", new Duration(1000L).toString());
        TestCase.assertEquals("PT12.345S", new Duration(12345L).toString());
        TestCase.assertEquals("PT-12.345S", new Duration((-12345L)).toString());
        TestCase.assertEquals("PT-1.123S", new Duration((-1123L)).toString());
        TestCase.assertEquals("PT-0.123S", new Duration((-123L)).toString());
        TestCase.assertEquals("PT-0.012S", new Duration((-12L)).toString());
        TestCase.assertEquals("PT-0.001S", new Duration((-1L)).toString());
    }

    // -----------------------------------------------------------------------
    public void testToDuration1() {
        Duration test = new Duration(123L);
        Duration result = test.toDuration();
        TestCase.assertSame(test, result);
    }

    public void testToDuration2() {
        TestDuration_Basics.MockDuration test = new TestDuration_Basics.MockDuration(123L);
        Duration result = toDuration();
        TestCase.assertNotSame(test, result);
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardDays() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(Days.days(0), test.toStandardDays());
        test = new Duration(1L);
        TestCase.assertEquals(Days.days(0), test.toStandardDays());
        test = new Duration((((24 * 60) * 60000L) - 1));
        TestCase.assertEquals(Days.days(0), test.toStandardDays());
        test = new Duration(((24 * 60) * 60000L));
        TestCase.assertEquals(Days.days(1), test.toStandardDays());
        test = new Duration((((24 * 60) * 60000L) + 1));
        TestCase.assertEquals(Days.days(1), test.toStandardDays());
        test = new Duration(((((2 * 24) * 60) * 60000L) - 1));
        TestCase.assertEquals(Days.days(1), test.toStandardDays());
        test = new Duration((((2 * 24) * 60) * 60000L));
        TestCase.assertEquals(Days.days(2), test.toStandardDays());
        test = new Duration((-1L));
        TestCase.assertEquals(Days.days(0), test.toStandardDays());
        test = new Duration(((((-24) * 60) * 60000L) + 1));
        TestCase.assertEquals(Days.days(0), test.toStandardDays());
        test = new Duration((((-24) * 60) * 60000L));
        TestCase.assertEquals(Days.days((-1)), test.toStandardDays());
    }

    public void testToStandardDays_overflow() {
        Duration test = new Duration(((((((long) (Integer.MAX_VALUE)) + 1) * 24L) * 60L) * 60000L));
        try {
            test.toStandardDays();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToStandardHours() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(Hours.hours(0), test.toStandardHours());
        test = new Duration(1L);
        TestCase.assertEquals(Hours.hours(0), test.toStandardHours());
        test = new Duration((3600000L - 1));
        TestCase.assertEquals(Hours.hours(0), test.toStandardHours());
        test = new Duration(3600000L);
        TestCase.assertEquals(Hours.hours(1), test.toStandardHours());
        test = new Duration((3600000L + 1));
        TestCase.assertEquals(Hours.hours(1), test.toStandardHours());
        test = new Duration(((2 * 3600000L) - 1));
        TestCase.assertEquals(Hours.hours(1), test.toStandardHours());
        test = new Duration((2 * 3600000L));
        TestCase.assertEquals(Hours.hours(2), test.toStandardHours());
        test = new Duration((-1L));
        TestCase.assertEquals(Hours.hours(0), test.toStandardHours());
        test = new Duration(((-3600000L) + 1));
        TestCase.assertEquals(Hours.hours(0), test.toStandardHours());
        test = new Duration((-3600000L));
        TestCase.assertEquals(Hours.hours((-1)), test.toStandardHours());
    }

    public void testToStandardHours_overflow() {
        Duration test = new Duration(((((long) (Integer.MAX_VALUE)) * 3600000L) + 3600000L));
        try {
            test.toStandardHours();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToStandardMinutes() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(Minutes.minutes(0), test.toStandardMinutes());
        test = new Duration(1L);
        TestCase.assertEquals(Minutes.minutes(0), test.toStandardMinutes());
        test = new Duration((60000L - 1));
        TestCase.assertEquals(Minutes.minutes(0), test.toStandardMinutes());
        test = new Duration(60000L);
        TestCase.assertEquals(Minutes.minutes(1), test.toStandardMinutes());
        test = new Duration((60000L + 1));
        TestCase.assertEquals(Minutes.minutes(1), test.toStandardMinutes());
        test = new Duration(((2 * 60000L) - 1));
        TestCase.assertEquals(Minutes.minutes(1), test.toStandardMinutes());
        test = new Duration((2 * 60000L));
        TestCase.assertEquals(Minutes.minutes(2), test.toStandardMinutes());
        test = new Duration((-1L));
        TestCase.assertEquals(Minutes.minutes(0), test.toStandardMinutes());
        test = new Duration(((-60000L) + 1));
        TestCase.assertEquals(Minutes.minutes(0), test.toStandardMinutes());
        test = new Duration((-60000L));
        TestCase.assertEquals(Minutes.minutes((-1)), test.toStandardMinutes());
    }

    public void testToStandardMinutes_overflow() {
        Duration test = new Duration(((((long) (Integer.MAX_VALUE)) * 60000L) + 60000L));
        try {
            test.toStandardMinutes();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToStandardSeconds() {
        Duration test = new Duration(0L);
        TestCase.assertEquals(Seconds.seconds(0), test.toStandardSeconds());
        test = new Duration(1L);
        TestCase.assertEquals(Seconds.seconds(0), test.toStandardSeconds());
        test = new Duration(999L);
        TestCase.assertEquals(Seconds.seconds(0), test.toStandardSeconds());
        test = new Duration(1000L);
        TestCase.assertEquals(Seconds.seconds(1), test.toStandardSeconds());
        test = new Duration(1001L);
        TestCase.assertEquals(Seconds.seconds(1), test.toStandardSeconds());
        test = new Duration(1999L);
        TestCase.assertEquals(Seconds.seconds(1), test.toStandardSeconds());
        test = new Duration(2000L);
        TestCase.assertEquals(Seconds.seconds(2), test.toStandardSeconds());
        test = new Duration((-1L));
        TestCase.assertEquals(Seconds.seconds(0), test.toStandardSeconds());
        test = new Duration((-999L));
        TestCase.assertEquals(Seconds.seconds(0), test.toStandardSeconds());
        test = new Duration((-1000L));
        TestCase.assertEquals(Seconds.seconds((-1)), test.toStandardSeconds());
    }

    public void testToStandardSeconds_overflow() {
        Duration test = new Duration(((((long) (Integer.MAX_VALUE)) * 1000L) + 1000L));
        try {
            test.toStandardSeconds();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToPeriod() {
        DateTimeZone zone = DateTimeZone.getDefault();
        try {
            DateTimeZone.setDefault(DateTimeZone.forID("Europe/Paris"));
            long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
            Duration dur = new Duration(length);
            Period test = dur.toPeriod();
            TestCase.assertEquals(0, test.getYears());// (4 + (3 * 7) + (2 * 30) + 365) == 450

            TestCase.assertEquals(0, test.getMonths());
            TestCase.assertEquals(0, test.getWeeks());
            TestCase.assertEquals(0, test.getDays());
            TestCase.assertEquals(((450 * 24) + 5), test.getHours());
            TestCase.assertEquals(6, test.getMinutes());
            TestCase.assertEquals(7, test.getSeconds());
            TestCase.assertEquals(8, test.getMillis());
        } finally {
            DateTimeZone.setDefault(zone);
        }
    }

    public void testToPeriod_fixedZone() throws Throwable {
        DateTimeZone zone = DateTimeZone.getDefault();
        try {
            DateTimeZone.setDefault(DateTimeZone.forOffsetHours(2));
            long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
            Duration dur = new Duration(length);
            Period test = dur.toPeriod();
            TestCase.assertEquals(0, test.getYears());// (4 + (3 * 7) + (2 * 30) + 365) == 450

            TestCase.assertEquals(0, test.getMonths());
            TestCase.assertEquals(0, test.getWeeks());
            TestCase.assertEquals(0, test.getDays());
            TestCase.assertEquals(((450 * 24) + 5), test.getHours());
            TestCase.assertEquals(6, test.getMinutes());
            TestCase.assertEquals(7, test.getSeconds());
            TestCase.assertEquals(8, test.getMillis());
        } finally {
            DateTimeZone.setDefault(zone);
        }
    }

    // -----------------------------------------------------------------------
    public void testToPeriod_PeriodType() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        Period result = test.toPeriod(PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(new Period(test, PeriodType.standard().withMillisRemoved()), result);
        TestCase.assertEquals(new Period(test.getMillis(), PeriodType.standard().withMillisRemoved()), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriod_Chronology() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        Period result = test.toPeriod(ISOChronology.getInstanceUTC());
        TestCase.assertEquals(new Period(test, ISOChronology.getInstanceUTC()), result);
        TestCase.assertEquals(new Period(test.getMillis(), ISOChronology.getInstanceUTC()), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriod_PeriodType_Chronology() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        Period result = test.toPeriod(PeriodType.standard().withMillisRemoved(), ISOChronology.getInstanceUTC());
        TestCase.assertEquals(new Period(test, PeriodType.standard().withMillisRemoved(), ISOChronology.getInstanceUTC()), result);
        TestCase.assertEquals(new Period(test.getMillis(), PeriodType.standard().withMillisRemoved(), ISOChronology.getInstanceUTC()), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriodFrom() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Period result = test.toPeriodFrom(dt);
        TestCase.assertEquals(new Period(dt, test), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriodFrom_PeriodType() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Period result = test.toPeriodFrom(dt, PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(new Period(dt, test, PeriodType.standard().withMillisRemoved()), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriodTo() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Period result = test.toPeriodTo(dt);
        TestCase.assertEquals(new Period(test, dt), result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriodTo_PeriodType() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Period result = test.toPeriodTo(dt, PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(new Period(test, dt, PeriodType.standard().withMillisRemoved()), result);
    }

    // -----------------------------------------------------------------------
    public void testToIntervalFrom() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Interval result = test.toIntervalFrom(dt);
        TestCase.assertEquals(new Interval(dt, test), result);
    }

    // -----------------------------------------------------------------------
    public void testToIntervalTo() {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Duration test = new Duration(length);
        DateTime dt = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Interval result = test.toIntervalTo(dt);
        TestCase.assertEquals(new Interval(test, dt), result);
    }

    // -----------------------------------------------------------------------
    public void testWithMillis1() {
        Duration test = new Duration(123L);
        Duration result = test.withMillis(123L);
        TestCase.assertSame(test, result);
    }

    public void testWithMillis2() {
        Duration test = new Duration(123L);
        Duration result = test.withMillis(1234567890L);
        TestCase.assertEquals(1234567890L, result.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_long_int1() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(8000L, 1);
        TestCase.assertEquals(8123L, result.getMillis());
    }

    public void testWithDurationAdded_long_int2() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(8000L, 2);
        TestCase.assertEquals(16123L, result.getMillis());
    }

    public void testWithDurationAdded_long_int3() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(8000L, (-1));
        TestCase.assertEquals((123L - 8000L), result.getMillis());
    }

    public void testWithDurationAdded_long_int4() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(0L, 1);
        TestCase.assertSame(test, result);
    }

    public void testWithDurationAdded_long_int5() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(8000L, 0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_long1() {
        Duration test = new Duration(123L);
        Duration result = test.plus(8000L);
        TestCase.assertEquals(8123L, result.getMillis());
    }

    public void testPlus_long2() {
        Duration test = new Duration(123L);
        Duration result = test.plus(0L);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_long1() {
        Duration test = new Duration(123L);
        Duration result = test.minus(8000L);
        TestCase.assertEquals((123L - 8000L), result.getMillis());
    }

    public void testMinus_long2() {
        Duration test = new Duration(123L);
        Duration result = test.minus(0L);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMultipliedBy_long1() {
        Duration test = new Duration(123L);
        Duration result = test.multipliedBy(2L);
        TestCase.assertEquals(246L, result.getMillis());
    }

    public void testMultipliedBy_long2() {
        Duration test = new Duration(123L);
        Duration result = test.multipliedBy(1L);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testDividedBy_long1() {
        Duration test = new Duration(246L);
        Duration result = test.dividedBy(2L);
        TestCase.assertEquals(123L, result.getMillis());
    }

    public void testDividedBy_long2() {
        Duration test = new Duration(123L);
        Duration result = test.dividedBy(1L);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testDividedByRoundingMode_long1() {
        Duration test = new Duration(246L);
        Duration result = test.dividedBy(2L, RoundingMode.UNNECESSARY);
        TestCase.assertEquals(123L, result.getMillis());
    }

    public void testDividedByRoundingMode_long2() {
        Duration test = new Duration(123L);
        Duration result = test.dividedBy(2L, RoundingMode.FLOOR);
        TestCase.assertEquals(61L, result.getMillis());
    }

    public void testDividedByRoundingMode_long3() {
        Duration test = new Duration(123L);
        Duration result = test.dividedBy(7L, RoundingMode.CEILING);
        TestCase.assertEquals(18L, result.getMillis());
    }

    public void testDividedByRoundingMode_long4() {
        Duration test = new Duration(33L);
        Duration result = test.dividedBy(1L, RoundingMode.FLOOR);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testNegated_long1() {
        Duration test = new Duration(246L);
        Duration result = test.negated();
        TestCase.assertEquals((-246L), result.getMillis());
    }

    public void testNegated_long2() {
        Duration test = new Duration((-246L));
        Duration result = test.negated();
        TestCase.assertEquals(246L, result.getMillis());
    }

    public void testNegated_long3() {
        Duration test = new Duration(Long.MIN_VALUE);
        try {
            test.negated();
            TestCase.fail();
        } catch (ArithmeticException e) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAbs() {
        TestCase.assertEquals(246L, getMillis());
        TestCase.assertEquals(0L, getMillis());
        TestCase.assertEquals(246L, getMillis());
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RD_int1() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(new Duration(8000L), 1);
        TestCase.assertEquals(8123L, result.getMillis());
    }

    public void testWithDurationAdded_RD_int2() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(new Duration(8000L), 2);
        TestCase.assertEquals(16123L, result.getMillis());
    }

    public void testWithDurationAdded_RD_int3() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(new Duration(8000L), (-1));
        TestCase.assertEquals((123L - 8000L), result.getMillis());
    }

    public void testWithDurationAdded_RD_int4() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(new Duration(0L), 1);
        TestCase.assertSame(test, result);
    }

    public void testWithDurationAdded_RD_int5() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(new Duration(8000L), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithDurationAdded_RD_int6() {
        Duration test = new Duration(123L);
        Duration result = test.withDurationAdded(null, 0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_RD1() {
        Duration test = new Duration(123L);
        Duration result = test.plus(new Duration(8000L));
        TestCase.assertEquals(8123L, result.getMillis());
    }

    public void testPlus_RD2() {
        Duration test = new Duration(123L);
        Duration result = test.plus(new Duration(0L));
        TestCase.assertSame(test, result);
    }

    public void testPlus_RD3() {
        Duration test = new Duration(123L);
        Duration result = test.plus(null);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RD1() {
        Duration test = new Duration(123L);
        Duration result = test.minus(new Duration(8000L));
        TestCase.assertEquals((123L - 8000L), result.getMillis());
    }

    public void testMinus_RD2() {
        Duration test = new Duration(123L);
        Duration result = test.minus(new Duration(0L));
        TestCase.assertSame(test, result);
    }

    public void testMinus_RD3() {
        Duration test = new Duration(123L);
        Duration result = test.minus(null);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMutableDuration() {
        // no MutableDuration, so...
        TestDuration_Basics.MockMutableDuration test = new TestDuration_Basics.MockMutableDuration(123L);
        TestCase.assertEquals(123L, getMillis());
        test.setMillis(2345L);
        TestCase.assertEquals(2345L, getMillis());
    }

    static class MockMutableDuration extends BaseDuration {
        public MockMutableDuration(long duration) {
            super(duration);
        }

        public void setMillis(long duration) {
            super.setMillis(duration);
        }
    }
}

