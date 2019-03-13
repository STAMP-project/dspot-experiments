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


import DateTimeZone.UTC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for Instant.
 *
 * @author Stephen Colebourne
 */
public class TestInstant_Basics extends TestCase {
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

    public TestInstant_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeFieldType() {
        Instant test = new Instant();// 2002-06-09

        TestCase.assertEquals(1, test.get(DateTimeFieldType.era()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.centuryOfEra()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.yearOfCentury()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.yearOfEra()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(9, test.get(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(2002, test.get(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(23, test.get(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(7, test.get(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(160, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));// UTC zone

        TestCase.assertEquals(24, test.get(DateTimeFieldType.clockhourOfDay()));// UTC zone

        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));// UTC zone

        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfDay()));// UTC zone

        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfDay()));
        try {
            test.get(((DateTimeFieldType) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGet_DateTimeField() {
        Instant test = new Instant();// 2002-06-09

        TestCase.assertEquals(1, test.get(ISOChronology.getInstance().era()));
        TestCase.assertEquals(20, test.get(ISOChronology.getInstance().centuryOfEra()));
        TestCase.assertEquals(2, test.get(ISOChronology.getInstance().yearOfCentury()));
        TestCase.assertEquals(2002, test.get(ISOChronology.getInstance().yearOfEra()));
        TestCase.assertEquals(2002, test.get(ISOChronology.getInstance().year()));
        TestCase.assertEquals(6, test.get(ISOChronology.getInstance().monthOfYear()));
        TestCase.assertEquals(9, test.get(ISOChronology.getInstance().dayOfMonth()));
        TestCase.assertEquals(2002, test.get(ISOChronology.getInstance().weekyear()));
        TestCase.assertEquals(23, test.get(ISOChronology.getInstance().weekOfWeekyear()));
        TestCase.assertEquals(7, test.get(ISOChronology.getInstance().dayOfWeek()));
        TestCase.assertEquals(160, test.get(ISOChronology.getInstance().dayOfYear()));
        TestCase.assertEquals(0, test.get(ISOChronology.getInstance().halfdayOfDay()));
        TestCase.assertEquals(1, test.get(ISOChronology.getInstance().hourOfHalfday()));
        TestCase.assertEquals(1, test.get(ISOChronology.getInstance().clockhourOfDay()));
        TestCase.assertEquals(1, test.get(ISOChronology.getInstance().clockhourOfHalfday()));
        TestCase.assertEquals(1, test.get(ISOChronology.getInstance().hourOfDay()));
        TestCase.assertEquals(0, test.get(ISOChronology.getInstance().minuteOfHour()));
        TestCase.assertEquals(60, test.get(ISOChronology.getInstance().minuteOfDay()));
        TestCase.assertEquals(0, test.get(ISOChronology.getInstance().secondOfMinute()));
        TestCase.assertEquals((60 * 60), test.get(ISOChronology.getInstance().secondOfDay()));
        TestCase.assertEquals(0, test.get(ISOChronology.getInstance().millisOfSecond()));
        TestCase.assertEquals(((60 * 60) * 1000), test.get(ISOChronology.getInstance().millisOfDay()));
        try {
            test.get(((DateTimeField) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetMethods() {
        Instant test = new Instant();
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), test.getChronology());
        TestCase.assertEquals(UTC, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
    }

    public void testEqualsHashCode() {
        Instant test1 = new Instant(TEST_TIME1);
        Instant test2 = new Instant(TEST_TIME1);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        Instant test3 = new Instant(TEST_TIME2);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.equals(new DateTime(TEST_TIME1)));
    }

    class MockInstant extends AbstractInstant {
        public String toString() {
            return null;
        }

        public long getMillis() {
            return TEST_TIME1;
        }

        public Chronology getChronology() {
            return ISOChronology.getInstanceUTC();
        }
    }

    public void testCompareTo() {
        Instant test1 = new Instant(TEST_TIME1);
        Instant test1a = new Instant(TEST_TIME1);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        Instant test2 = new Instant(TEST_TIME2);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        DateTime test3 = new DateTime(TEST_TIME2, GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(0, test1.compareTo(new TestInstant_Basics.MockInstant()));
        try {
            test1.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
        // try {
        // test1.compareTo(new Date());
        // fail();
        // } catch (ClassCastException ex) {}
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_long() {
        TestCase.assertEquals(false, new Instant(TEST_TIME1).isEqual(TEST_TIME2));
        TestCase.assertEquals(true, new Instant(TEST_TIME1).isEqual(TEST_TIME1));
        TestCase.assertEquals(false, new Instant(TEST_TIME2).isEqual(TEST_TIME1));
    }

    public void testIsEqualNow() {
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) - 1)).isEqualNow());
        TestCase.assertEquals(true, new Instant(TEST_TIME_NOW).isEqualNow());
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) + 1)).isEqualNow());
    }

    public void testIsEqual_RI() {
        Instant test1 = new Instant(TEST_TIME1);
        Instant test1a = new Instant(TEST_TIME1);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        Instant test2 = new Instant(TEST_TIME2);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        DateTime test3 = new DateTime(TEST_TIME2, GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(true, test1.isEqual(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) + 1)).isEqual(null));
        TestCase.assertEquals(true, new Instant(TEST_TIME_NOW).isEqual(null));
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) - 1)).isEqual(null));
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_long() {
        TestCase.assertEquals(true, new Instant(TEST_TIME1).isBefore(TEST_TIME2));
        TestCase.assertEquals(false, new Instant(TEST_TIME1).isBefore(TEST_TIME1));
        TestCase.assertEquals(false, new Instant(TEST_TIME2).isBefore(TEST_TIME1));
    }

    public void testIsBeforeNow() {
        TestCase.assertEquals(true, new Instant(((TEST_TIME_NOW) - 1)).isBeforeNow());
        TestCase.assertEquals(false, new Instant(TEST_TIME_NOW).isBeforeNow());
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) + 1)).isBeforeNow());
    }

    public void testIsBefore_RI() {
        Instant test1 = new Instant(TEST_TIME1);
        Instant test1a = new Instant(TEST_TIME1);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        Instant test2 = new Instant(TEST_TIME2);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        DateTime test3 = new DateTime(TEST_TIME2, GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isBefore(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) + 1)).isBefore(null));
        TestCase.assertEquals(false, new Instant(TEST_TIME_NOW).isBefore(null));
        TestCase.assertEquals(true, new Instant(((TEST_TIME_NOW) - 1)).isBefore(null));
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_long() {
        TestCase.assertEquals(false, new Instant(TEST_TIME1).isAfter(TEST_TIME2));
        TestCase.assertEquals(false, new Instant(TEST_TIME1).isAfter(TEST_TIME1));
        TestCase.assertEquals(true, new Instant(TEST_TIME2).isAfter(TEST_TIME1));
    }

    public void testIsAfterNow() {
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) - 1)).isAfterNow());
        TestCase.assertEquals(false, new Instant(TEST_TIME_NOW).isAfterNow());
        TestCase.assertEquals(true, new Instant(((TEST_TIME_NOW) + 1)).isAfterNow());
    }

    public void testIsAfter_RI() {
        Instant test1 = new Instant(TEST_TIME1);
        Instant test1a = new Instant(TEST_TIME1);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        Instant test2 = new Instant(TEST_TIME2);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        DateTime test3 = new DateTime(TEST_TIME2, GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isAfter(new TestInstant_Basics.MockInstant()));
        TestCase.assertEquals(true, new Instant(((TEST_TIME_NOW) + 1)).isAfter(null));
        TestCase.assertEquals(false, new Instant(TEST_TIME_NOW).isAfter(null));
        TestCase.assertEquals(false, new Instant(((TEST_TIME_NOW) - 1)).isAfter(null));
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Instant test = new Instant(TEST_TIME_NOW);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Instant result = ((Instant) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Instant test = new Instant(TEST_TIME_NOW);
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToInstant() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.toInstant();
        TestCase.assertSame(test, result);
    }

    public void testToDateTime() {
        Instant test = new Instant(TEST_TIME1);
        DateTime result = test.toDateTime();
        TestCase.assertEquals(TEST_TIME1, result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDateTimeISO() {
        Instant test = new Instant(TEST_TIME1);
        DateTime result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDateTime_DateTimeZone() {
        Instant test = new Instant(TEST_TIME1);
        DateTime result = test.toDateTime(TestInstant_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestInstant_Basics.LONDON), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toDateTime(TestInstant_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestInstant_Basics.PARIS), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDateTime_Chronology() {
        Instant test = new Instant(TEST_TIME1);
        DateTime result = test.toDateTime(ISOChronology.getInstance());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toDateTime(GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestInstant_Basics.PARIS), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toDateTime(((Chronology) (null)));
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTime() {
        Instant test = new Instant(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime();
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTimeISO() {
        Instant test = new Instant(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTimeISO();
        TestCase.assertSame(MutableDateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTime_DateTimeZone() {
        Instant test = new Instant(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(TestInstant_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toMutableDateTime(TestInstant_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestInstant_Basics.PARIS), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTime_Chronology() {
        Instant test = new Instant(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(ISOChronology.getInstance());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toMutableDateTime(GregorianChronology.getInstance(TestInstant_Basics.PARIS));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestInstant_Basics.PARIS), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDate() {
        Instant test = new Instant(TEST_TIME1);
        Date result = test.toDate();
        TestCase.assertEquals(test.getMillis(), result.getTime());
    }

    // -----------------------------------------------------------------------
    public void testWithMillis_long() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.withMillis(TEST_TIME2);
        TestCase.assertEquals(TEST_TIME2, result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        test = new Instant(TEST_TIME1);
        result = test.withMillis(TEST_TIME1);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_long_int() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.withDurationAdded(123456789L, 1);
        Instant expected = new Instant(((TEST_TIME1) + 123456789L));
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(123456789L, 2);
        expected = new Instant(((TEST_TIME1) + (2L * 123456789L)));
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(123456789L, (-3));
        expected = new Instant(((TEST_TIME1) - (3L * 123456789L)));
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAdded_RD_int() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.withDurationAdded(new Duration(123456789L), 1);
        Instant expected = new Instant(((TEST_TIME1) + 123456789L));
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(null, 1);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 0);
        TestCase.assertSame(test, result);
        result = test.withDurationAdded(new Duration(123456789L), 2);
        expected = new Instant(((TEST_TIME1) + (2L * 123456789L)));
        TestCase.assertEquals(expected, result);
        result = test.withDurationAdded(new Duration(123456789L), (-3));
        expected = new Instant(((TEST_TIME1) - (3L * 123456789L)));
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_long() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.plus(123456789L);
        Instant expected = new Instant(((TEST_TIME1) + 123456789L));
        TestCase.assertEquals(expected, result);
    }

    public void testPlus_RD() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.plus(new Duration(123456789L));
        Instant expected = new Instant(((TEST_TIME1) + 123456789L));
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_long() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.minus(123456789L);
        Instant expected = new Instant(((TEST_TIME1) - 123456789L));
        TestCase.assertEquals(expected, result);
    }

    public void testMinus_RD() {
        Instant test = new Instant(TEST_TIME1);
        Instant result = test.minus(new Duration(123456789L));
        Instant expected = new Instant(((TEST_TIME1) - 123456789L));
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadableDuration) (null)));
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testImmutable() {
        TestCase.assertTrue(Modifier.isFinal(Instant.class.getModifiers()));
    }
}

