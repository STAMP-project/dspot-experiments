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


import MutableDateTime.ROUND_CEILING;
import MutableDateTime.ROUND_FLOOR;
import MutableDateTime.ROUND_HALF_CEILING;
import MutableDateTime.ROUND_HALF_EVEN;
import MutableDateTime.ROUND_HALF_FLOOR;
import MutableDateTime.ROUND_NONE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.base.AbstractInstant;
import org.joda.time.chrono.BaseChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.UnsupportedDateTimeField;
import org.joda.time.field.UnsupportedDurationField;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a JUnit test for MutableDateTime.
 *
 * @author Stephen Colebourne
 */
public class TestMutableDateTime_Basics extends TestCase {
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

    public TestMutableDateTime_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeField() {
        MutableDateTime test = new MutableDateTime();
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

    public void testGet_DateTimeFieldType() {
        MutableDateTime test = new MutableDateTime();
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
        TestCase.assertEquals(1, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(1, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(60, test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals((60 * 60), test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(0, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(((60 * 60) * 1000), test.get(DateTimeFieldType.millisOfDay()));
        try {
            test.get(((DateTimeFieldType) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testGetMethods() {
        MutableDateTime test = new MutableDateTime();
        TestCase.assertEquals(ISOChronology.getInstance(), test.getChronology());
        TestCase.assertEquals(TestMutableDateTime_Basics.LONDON, test.getZone());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillis());
        TestCase.assertEquals(1, test.getEra());
        TestCase.assertEquals(20, test.getCenturyOfEra());
        TestCase.assertEquals(2, test.getYearOfCentury());
        TestCase.assertEquals(2002, test.getYearOfEra());
        TestCase.assertEquals(2002, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(2002, test.getWeekyear());
        TestCase.assertEquals(23, test.getWeekOfWeekyear());
        TestCase.assertEquals(7, test.getDayOfWeek());
        TestCase.assertEquals(160, test.getDayOfYear());
        TestCase.assertEquals(1, test.getHourOfDay());
        TestCase.assertEquals(0, test.getMinuteOfHour());
        TestCase.assertEquals(60, test.getMinuteOfDay());
        TestCase.assertEquals(0, test.getSecondOfMinute());
        TestCase.assertEquals((60 * 60), test.getSecondOfDay());
        TestCase.assertEquals(0, test.getMillisOfSecond());
        TestCase.assertEquals(((60 * 60) * 1000), test.getMillisOfDay());
    }

    public void testEqualsHashCode() {
        MutableDateTime test1 = new MutableDateTime(TEST_TIME1);
        MutableDateTime test2 = new MutableDateTime(TEST_TIME1);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        MutableDateTime test3 = new MutableDateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        DateTime test4 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, test4.equals(test3));
        TestCase.assertEquals(true, test3.equals(test4));
        TestCase.assertEquals(false, test4.equals(test1));
        TestCase.assertEquals(false, test1.equals(test4));
        TestCase.assertEquals(true, ((test3.hashCode()) == (test4.hashCode())));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test4.hashCode())));
        MutableDateTime test5 = new MutableDateTime(TEST_TIME2);
        test5.setRounding(ISOChronology.getInstance().millisOfSecond());
        TestCase.assertEquals(true, test5.equals(test3));
        TestCase.assertEquals(true, test5.equals(test4));
        TestCase.assertEquals(true, test3.equals(test5));
        TestCase.assertEquals(true, test4.equals(test5));
        TestCase.assertEquals(true, ((test3.hashCode()) == (test5.hashCode())));
        TestCase.assertEquals(true, ((test4.hashCode()) == (test5.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.equals(new MutableDateTime(TEST_TIME1, GregorianChronology.getInstance())));
        TestCase.assertEquals(true, new MutableDateTime(TEST_TIME1, new TestMutableDateTime_Basics.MockEqualsChronology()).equals(new MutableDateTime(TEST_TIME1, new TestMutableDateTime_Basics.MockEqualsChronology())));
        TestCase.assertEquals(false, new MutableDateTime(TEST_TIME1, new TestMutableDateTime_Basics.MockEqualsChronology()).equals(new MutableDateTime(TEST_TIME1, ISOChronology.getInstance())));
    }

    class MockInstant extends AbstractInstant {
        public String toString() {
            return null;
        }

        public long getMillis() {
            return TEST_TIME1;
        }

        public Chronology getChronology() {
            return ISOChronology.getInstance();
        }
    }

    class MockEqualsChronology extends BaseChronology {
        private static final long serialVersionUID = 1L;

        public boolean equals(Object obj) {
            return obj instanceof TestMutableDateTime_Basics.MockEqualsChronology;
        }

        public DateTimeZone getZone() {
            return null;
        }

        public Chronology withUTC() {
            return this;
        }

        public Chronology withZone(DateTimeZone zone) {
            return this;
        }

        public String toString() {
            return "";
        }
    }

    public void testCompareTo() {
        MutableDateTime test1 = new MutableDateTime(TEST_TIME1);
        MutableDateTime test1a = new MutableDateTime(TEST_TIME1);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        MutableDateTime test2 = new MutableDateTime(TEST_TIME2);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        MutableDateTime test3 = new MutableDateTime(TEST_TIME2, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(0, test1.compareTo(new TestMutableDateTime_Basics.MockInstant()));
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

    public void testIsEqual() {
        MutableDateTime test1 = new MutableDateTime(TEST_TIME1);
        MutableDateTime test1a = new MutableDateTime(TEST_TIME1);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        MutableDateTime test2 = new MutableDateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        MutableDateTime test3 = new MutableDateTime(TEST_TIME2, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(true, test1.isEqual(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, new MutableDateTime(((TEST_TIME_NOW) + 1)).isEqual(null));
        TestCase.assertEquals(true, new MutableDateTime(TEST_TIME_NOW).isEqual(null));
        TestCase.assertEquals(false, new MutableDateTime(((TEST_TIME_NOW) - 1)).isEqual(null));
    }

    public void testIsBefore() {
        MutableDateTime test1 = new MutableDateTime(TEST_TIME1);
        MutableDateTime test1a = new MutableDateTime(TEST_TIME1);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        MutableDateTime test2 = new MutableDateTime(TEST_TIME2);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        MutableDateTime test3 = new MutableDateTime(TEST_TIME2, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isBefore(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, new MutableDateTime(((TEST_TIME_NOW) + 1)).isBefore(null));
        TestCase.assertEquals(false, new MutableDateTime(TEST_TIME_NOW).isBefore(null));
        TestCase.assertEquals(true, new MutableDateTime(((TEST_TIME_NOW) - 1)).isBefore(null));
    }

    public void testIsAfter() {
        MutableDateTime test1 = new MutableDateTime(TEST_TIME1);
        MutableDateTime test1a = new MutableDateTime(TEST_TIME1);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        MutableDateTime test2 = new MutableDateTime(TEST_TIME2);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        MutableDateTime test3 = new MutableDateTime(TEST_TIME2, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.isAfter(new TestMutableDateTime_Basics.MockInstant()));
        TestCase.assertEquals(true, new MutableDateTime(((TEST_TIME_NOW) + 1)).isAfter(null));
        TestCase.assertEquals(false, new MutableDateTime(TEST_TIME_NOW).isAfter(null));
        TestCase.assertEquals(false, new MutableDateTime(((TEST_TIME_NOW) - 1)).isAfter(null));
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        MutableDateTime test = new MutableDateTime(TEST_TIME_NOW);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        MutableDateTime result = ((MutableDateTime) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        MutableDateTime test = new MutableDateTime(TEST_TIME_NOW);
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString());
        test = new MutableDateTime(TEST_TIME_NOW, TestMutableDateTime_Basics.PARIS);
        TestCase.assertEquals("2002-06-09T02:00:00.000+02:00", test.toString());
    }

    public void testToString_String() {
        MutableDateTime test = new MutableDateTime(TEST_TIME_NOW);
        TestCase.assertEquals("2002 01", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(((String) (null))));
    }

    public void testToString_String_String() {
        MutableDateTime test = new MutableDateTime(TEST_TIME_NOW);
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("dim. 9/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("Sun 9/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(null, null));
    }

    public void testToString_DTFormatter() {
        MutableDateTime test = new MutableDateTime(TEST_TIME_NOW);
        TestCase.assertEquals("2002 01", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("2002-06-09T01:00:00.000+01:00", test.toString(((DateTimeFormatter) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToInstant() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        Instant result = test.toInstant();
        TestCase.assertEquals(TEST_TIME1, result.getMillis());
    }

    public void testToDateTime() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        DateTime result = test.toDateTime();
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
    }

    public void testToDateTimeISO() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        DateTime result = test.toDateTimeISO();
        TestCase.assertSame(DateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
    }

    public void testToDateTime_DateTimeZone() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(TestMutableDateTime_Basics.LONDON);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        TestCase.assertEquals(TestMutableDateTime_Basics.LONDON, result.getZone());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(TestMutableDateTime_Basics.PARIS);
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestMutableDateTime_Basics.PARIS, result.getZone());
        test = new MutableDateTime(TEST_TIME1, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestMutableDateTime_Basics.LONDON), result.getChronology());
        test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestMutableDateTime_Basics.LONDON, result.getZone());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(TestMutableDateTime_Basics.LONDON, result.getZone());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDateTime_Chronology() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(ISOChronology.getInstance());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
        test = new MutableDateTime(TEST_TIME1, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTime() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTime();
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
    }

    public void testToMutableDateTimeISO() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        MutableDateTime result = test.toMutableDateTimeISO();
        TestCase.assertSame(MutableDateTime.class, result.getClass());
        TestCase.assertSame(ISOChronology.class, result.getChronology().getClass());
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
        TestCase.assertNotSame(test, result);
    }

    public void testToMutableDateTime_DateTimeZone() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(TestMutableDateTime_Basics.LONDON);
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.LONDON), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(TestMutableDateTime_Basics.PARIS);
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
        test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(((DateTimeZone) (null)));
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToMutableDateTime_Chronology() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = test.toMutableDateTime(ISOChronology.getInstance());
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS), result.getChronology());
        test = new MutableDateTime(TEST_TIME1, GregorianChronology.getInstance(TestMutableDateTime_Basics.PARIS));
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
        test = new MutableDateTime(TEST_TIME1);
        result = test.toMutableDateTime(((Chronology) (null)));
        TestCase.assertTrue((test != result));
        TestCase.assertEquals(test.getMillis(), result.getMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), result.getChronology());
    }

    public void testToDate() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        Date result = test.toDate();
        TestCase.assertEquals(test.getMillis(), result.getTime());
    }

    public void testToCalendar_Locale() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        Calendar result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        result = test.toCalendar(null);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
        test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        result = test.toCalendar(Locale.UK);
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    public void testToGregorianCalendar() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        GregorianCalendar result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/London"), result.getTimeZone());
        test = new MutableDateTime(TEST_TIME1, TestMutableDateTime_Basics.PARIS);
        result = test.toGregorianCalendar();
        TestCase.assertEquals(test.getMillis(), result.getTime().getTime());
        TestCase.assertEquals(TimeZone.getTimeZone("Europe/Paris"), result.getTimeZone());
    }

    public void testClone() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = ((MutableDateTime) (test.clone()));
        TestCase.assertEquals(true, test.equals(result));
        TestCase.assertEquals(true, (test != result));
    }

    public void testCopy() {
        MutableDateTime test = new MutableDateTime(TEST_TIME1);
        MutableDateTime result = test.copy();
        TestCase.assertEquals(true, test.equals(result));
        TestCase.assertEquals(true, (test != result));
    }

    public void testRounding1() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay());
        TestCase.assertEquals("2002-06-09T05:00:00.000+01:00", test.toString());
        TestCase.assertEquals(ROUND_FLOOR, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
    }

    public void testRounding2() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_CEILING);
        TestCase.assertEquals("2002-06-09T06:00:00.000+01:00", test.toString());
        TestCase.assertEquals(ROUND_CEILING, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
    }

    public void testRounding3() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_CEILING);
        TestCase.assertEquals("2002-06-09T05:00:00.000+01:00", test.toString());
        TestCase.assertEquals(ROUND_HALF_CEILING, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
        test = new MutableDateTime(2002, 6, 9, 5, 30, 0, 0);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_CEILING);
        TestCase.assertEquals("2002-06-09T06:00:00.000+01:00", test.toString());
    }

    public void testRounding4() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_FLOOR);
        TestCase.assertEquals("2002-06-09T05:00:00.000+01:00", test.toString());
        TestCase.assertEquals(ROUND_HALF_FLOOR, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
        test = new MutableDateTime(2002, 6, 9, 5, 30, 0, 0);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_FLOOR);
        TestCase.assertEquals("2002-06-09T05:00:00.000+01:00", test.toString());
    }

    public void testRounding5() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_EVEN);
        TestCase.assertEquals("2002-06-09T05:00:00.000+01:00", test.toString());
        TestCase.assertEquals(ROUND_HALF_EVEN, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
        test = new MutableDateTime(2002, 6, 9, 5, 30, 0, 0);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_EVEN);
        TestCase.assertEquals("2002-06-09T06:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2002, 6, 9, 4, 30, 0, 0);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_HALF_EVEN);
        TestCase.assertEquals("2002-06-09T04:00:00.000+01:00", test.toString());
    }

    public void testRounding6() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_NONE);
        TestCase.assertEquals("2002-06-09T05:06:07.008+01:00", test.toString());
        TestCase.assertEquals(ROUND_NONE, test.getRoundingMode());
        TestCase.assertEquals(null, test.getRoundingField());
    }

    public void testRounding7() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        try {
            test.setRounding(ISOChronology.getInstance().hourOfDay(), (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testRounding8() {
        MutableDateTime test = new MutableDateTime(2002, 6, 9, 5, 6, 7, 8);
        TestCase.assertEquals(ROUND_NONE, test.getRoundingMode());
        TestCase.assertEquals(null, test.getRoundingField());
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_CEILING);
        TestCase.assertEquals(ROUND_CEILING, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
        test.setRounding(ISOChronology.getInstance().hourOfDay(), ROUND_NONE);
        TestCase.assertEquals(ROUND_NONE, test.getRoundingMode());
        TestCase.assertEquals(null, test.getRoundingField());
        test.setRounding(null, (-1));
        TestCase.assertEquals(ROUND_NONE, test.getRoundingMode());
        TestCase.assertEquals(null, test.getRoundingField());
        test.setRounding(ISOChronology.getInstance().hourOfDay());
        TestCase.assertEquals(ROUND_FLOOR, test.getRoundingMode());
        TestCase.assertEquals(ISOChronology.getInstance().hourOfDay(), test.getRoundingField());
        test.setRounding(null);
        TestCase.assertEquals(ROUND_NONE, test.getRoundingMode());
        TestCase.assertEquals(null, test.getRoundingField());
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        MutableDateTime test = new MutableDateTime();
        TestCase.assertEquals(test.year(), test.property(DateTimeFieldType.year()));
        TestCase.assertEquals(test.dayOfWeek(), test.property(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(test.secondOfMinute(), test.property(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(test.millisOfSecond(), test.property(DateTimeFieldType.millisOfSecond()));
        DateTimeFieldType bad = new DateTimeFieldType("bad") {
            private static final long serialVersionUID = 1L;

            public DurationFieldType getDurationType() {
                return DurationFieldType.weeks();
            }

            public DurationFieldType getRangeDurationType() {
                return null;
            }

            public DateTimeField getField(Chronology chronology) {
                return UnsupportedDateTimeField.getInstance(this, UnsupportedDurationField.getInstance(getDurationType()));
            }
        };
        try {
            test.property(bad);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

