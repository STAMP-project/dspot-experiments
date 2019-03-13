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


import DateTimeConstants.AM;
import DateTimeConstants.PM;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for LocalTime.
 *
 * @author Stephen Colebourne
 */
public class TestLocalTime_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestLocalTime_Basics.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestLocalTime_Basics.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestLocalTime_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(TestLocalTime_Basics.LONDON);

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    // private long TEST_TIME1 =
    // 1L * DateTimeConstants.MILLIS_PER_HOUR
    // + 2L * DateTimeConstants.MILLIS_PER_MINUTE
    // + 3L * DateTimeConstants.MILLIS_PER_SECOND
    // + 4L;
    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestLocalTime_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeFieldType() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(10, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(30, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(40, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(((TEST_TIME_NOW) / 60000), test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(((TEST_TIME_NOW) / 1000), test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(TEST_TIME_NOW, test.get(DateTimeFieldType.millisOfDay()));
        TestCase.assertEquals(10, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(AM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalTime(12, 30);
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(PM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalTime(14, 30);
        TestCase.assertEquals(2, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(14, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(PM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalTime(0, 30);
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(24, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(AM, test.get(DateTimeFieldType.halfdayOfDay()));
        try {
            test.get(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.get(DateTimeFieldType.dayOfMonth());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSize() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(4, test.size());
    }

    public void testGetFieldType_int() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertSame(DateTimeFieldType.hourOfDay(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.minuteOfHour(), test.getFieldType(1));
        TestCase.assertSame(DateTimeFieldType.secondOfMinute(), test.getFieldType(2));
        TestCase.assertSame(DateTimeFieldType.millisOfSecond(), test.getFieldType(3));
        try {
            test.getFieldType((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getFieldType(5);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldTypes() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertSame(DateTimeFieldType.hourOfDay(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.minuteOfHour(), fields[1]);
        TestCase.assertSame(DateTimeFieldType.secondOfMinute(), fields[2]);
        TestCase.assertSame(DateTimeFieldType.millisOfSecond(), fields[3]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField_int() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_UTC);
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.hourOfDay(), test.getField(0));
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.minuteOfHour(), test.getField(1));
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.secondOfMinute(), test.getField(2));
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.millisOfSecond(), test.getField(3));
        try {
            test.getField((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getField(5);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFields() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_UTC);
        DateTimeField[] fields = test.getFields();
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.hourOfDay(), fields[0]);
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.minuteOfHour(), fields[1]);
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.secondOfMinute(), fields[2]);
        TestCase.assertSame(TestLocalTime_Basics.COPTIC_UTC.millisOfSecond(), fields[3]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue_int() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);
        TestCase.assertEquals(10, test.getValue(0));
        TestCase.assertEquals(20, test.getValue(1));
        TestCase.assertEquals(30, test.getValue(2));
        TestCase.assertEquals(40, test.getValue(3));
        try {
            test.getValue((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getValue(5);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetValues() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_UTC);
        int[] values = test.getValues();
        TestCase.assertEquals(10, values[0]);
        TestCase.assertEquals(20, values[1]);
        TestCase.assertEquals(30, values[2]);
        TestCase.assertEquals(40, values[3]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported_DateTimeFieldType() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.millisOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(false, test.isSupported(((DateTimeFieldType) (null))));
        DateTimeFieldType d = new DateTimeFieldType("hours") {
            private static final long serialVersionUID = 1L;

            public DurationFieldType getDurationType() {
                return DurationFieldType.hours();
            }

            public DurationFieldType getRangeDurationType() {
                return null;
            }

            public DateTimeField getField(Chronology chronology) {
                return chronology.hourOfDay();
            }
        };
        TestCase.assertEquals(false, test.isSupported(d));
        d = new DateTimeFieldType("hourOfYear") {
            private static final long serialVersionUID = 1L;

            public DurationFieldType getDurationType() {
                return DurationFieldType.hours();
            }

            public DurationFieldType getRangeDurationType() {
                return DurationFieldType.years();
            }

            public DateTimeField getField(Chronology chronology) {
                return chronology.hourOfDay();
            }
        };
        TestCase.assertEquals(false, test.isSupported(d));
    }

    public void testIsSupported_DurationFieldType() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.hours()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.minutes()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.seconds()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.millis()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.halfdays()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.days()));
        TestCase.assertEquals(false, test.isSupported(((DurationFieldType) (null))));
    }

    class MockInstant extends MockPartial {
        public Chronology getChronology() {
            return TestLocalTime_Basics.COPTIC_UTC;
        }

        public DateTimeField[] getFields() {
            return new DateTimeField[]{ TestLocalTime_Basics.COPTIC_UTC.hourOfDay(), TestLocalTime_Basics.COPTIC_UTC.minuteOfHour(), TestLocalTime_Basics.COPTIC_UTC.secondOfMinute(), TestLocalTime_Basics.COPTIC_UTC.millisOfSecond() };
        }

        public int[] getValues() {
            return new int[]{ 10, 20, 30, 40 };
        }
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_LocalTime() {
        LocalTime test1 = new LocalTime(10, 20, 30, 40);
        LocalTime test1a = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        LocalTime test2 = new LocalTime(10, 20, 35, 40);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        LocalTime test3 = new LocalTime(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new LocalTime(10, 20, 35, 40).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_LocalTime() {
        LocalTime test1 = new LocalTime(10, 20, 30, 40);
        LocalTime test1a = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        LocalTime test2 = new LocalTime(10, 20, 35, 40);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        LocalTime test3 = new LocalTime(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new LocalTime(10, 20, 35, 40).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_LocalTime() {
        LocalTime test1 = new LocalTime(10, 20, 30, 40);
        LocalTime test1a = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        LocalTime test2 = new LocalTime(10, 20, 35, 40);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        LocalTime test3 = new LocalTime(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new LocalTime(10, 20, 35, 40).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithField_DateTimeFieldType_int_1() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        LocalTime result = test.withField(DateTimeFieldType.hourOfDay(), 15);
        TestCase.assertEquals(new LocalTime(10, 20, 30, 40), test);
        TestCase.assertEquals(new LocalTime(15, 20, 30, 40), result);
    }

    public void testWithField_DateTimeFieldType_int_2() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_DateTimeFieldType_int_3() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        try {
            test.withField(DateTimeFieldType.dayOfMonth(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_DateTimeFieldType_int_4() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        LocalTime result = test.withField(DateTimeFieldType.hourOfDay(), 10);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded_DurationFieldType_int_1() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        LocalTime result = test.withFieldAdded(DurationFieldType.hours(), 6);
        TestCase.assertEquals(new LocalTime(10, 20, 30, 40), test);
        TestCase.assertEquals(new LocalTime(16, 20, 30, 40), result);
    }

    public void testWithFieldAdded_DurationFieldType_int_2() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_3() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_4() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        LocalTime result = test.withFieldAdded(DurationFieldType.hours(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded_DurationFieldType_int_5() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        try {
            test.withFieldAdded(DurationFieldType.days(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_6() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        LocalTime result = test.withFieldAdded(DurationFieldType.hours(), 16);
        TestCase.assertEquals(new LocalTime(10, 20, 30, 40), test);
        TestCase.assertEquals(new LocalTime(2, 20, 30, 40), result);
    }

    public void testWithFieldAdded_DurationFieldType_int_7() {
        LocalTime test = new LocalTime(23, 59, 59, 999);
        LocalTime result = test.withFieldAdded(DurationFieldType.millis(), 1);
        TestCase.assertEquals(new LocalTime(0, 0, 0, 0), result);
        test = new LocalTime(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.seconds(), 1);
        TestCase.assertEquals(new LocalTime(0, 0, 0, 999), result);
        test = new LocalTime(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.minutes(), 1);
        TestCase.assertEquals(new LocalTime(0, 0, 59, 999), result);
        test = new LocalTime(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.hours(), 1);
        TestCase.assertEquals(new LocalTime(0, 59, 59, 999), result);
    }

    public void testWithFieldAdded_DurationFieldType_int_8() {
        LocalTime test = new LocalTime(0, 0, 0, 0);
        LocalTime result = test.withFieldAdded(DurationFieldType.millis(), (-1));
        TestCase.assertEquals(new LocalTime(23, 59, 59, 999), result);
        test = new LocalTime(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.seconds(), (-1));
        TestCase.assertEquals(new LocalTime(23, 59, 59, 0), result);
        test = new LocalTime(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.minutes(), (-1));
        TestCase.assertEquals(new LocalTime(23, 59, 0, 0), result);
        test = new LocalTime(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.hours(), (-1));
        TestCase.assertEquals(new LocalTime(23, 0, 0, 0), result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        LocalTime expected = new LocalTime(15, 26, 37, 48, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusHours_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.plusHours(1);
        LocalTime expected = new LocalTime(2, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMinutes_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.plusMinutes(1);
        LocalTime expected = new LocalTime(1, 3, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusSeconds_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.plusSeconds(1);
        LocalTime expected = new LocalTime(1, 2, 4, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMillis_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.plusMillis(1);
        LocalTime expected = new LocalTime(1, 2, 3, 5, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        LocalTime expected = new LocalTime(9, 19, 29, 39, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusHours_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.minusHours(1);
        LocalTime expected = new LocalTime(0, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMinutes_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.minusMinutes(1);
        LocalTime expected = new LocalTime(1, 1, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusSeconds_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.minusSeconds(1);
        LocalTime expected = new LocalTime(1, 2, 2, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMillis_int() {
        LocalTime test = new LocalTime(1, 2, 3, 4, TestLocalTime_Basics.BUDDHIST_LONDON);
        LocalTime result = test.minusMillis(1);
        LocalTime expected = new LocalTime(1, 2, 3, 3, TestLocalTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testGetters() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(TEST_TIME_NOW, test.getMillisOfDay());
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        check(test.withHourOfDay(6), 6, 20, 30, 40);
        check(test.withMinuteOfHour(6), 10, 6, 30, 40);
        check(test.withSecondOfMinute(6), 10, 20, 6, 40);
        check(test.withMillisOfSecond(6), 10, 20, 30, 6);
        check(test.withMillisOfDay(61234), 0, 1, 1, 234);
        try {
            test.withHourOfDay((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.withHourOfDay(24);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeTodayDefaultZone() {
        LocalTime base = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday();
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalTime_Basics.COPTIC_LONDON);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeToday_Zone() {
        LocalTime base = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday(TestLocalTime_Basics.TOKYO);
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalTime_Basics.COPTIC_TOKYO);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTimeToday_nullZone() {
        LocalTime base = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday(((DateTimeZone) (null)));
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalTime_Basics.COPTIC_LONDON);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        LocalTime base = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(0L);// LONDON zone

        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        DateTime test = base.toDateTime(dt);
        check(base, 10, 20, 30, 40);
        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        TestCase.assertEquals("1970-01-01T10:20:30.040+01:00", test.toString());
    }

    public void testToDateTime_nullRI() {
        LocalTime base = new LocalTime(1, 2, 3, 4);
        DateTimeUtils.setCurrentMillisFixed(TEST_TIME2);
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 1, 2, 3, 4);
        TestCase.assertEquals("1970-01-02T01:02:03.004+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals(test.hourOfDay(), test.property(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(test.minuteOfHour(), test.property(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(test.secondOfMinute(), test.property(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(test.millisOfSecond(), test.property(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(test.millisOfDay(), test.property(DateTimeFieldType.millisOfDay()));
        TestCase.assertEquals(test, test.property(DateTimeFieldType.minuteOfDay()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.secondOfDay()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.millisOfDay()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.hourOfHalfday()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.halfdayOfDay()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.clockhourOfHalfday()).getLocalTime());
        TestCase.assertEquals(test, test.property(DateTimeFieldType.clockhourOfDay()).getLocalTime());
        try {
            test.property(DateTimeFieldType.dayOfWeek());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        LocalTime test = new LocalTime(10, 20, 30, 40, TestLocalTime_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        LocalTime result = ((LocalTime) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals("10:20:30.040", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString("yyyy HH"));
        TestCase.assertEquals("10:20:30.040", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals("10 20", test.toString("H m", Locale.ENGLISH));
        TestCase.assertEquals("10:20:30.040", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("10 20", test.toString("H m", null));
        TestCase.assertEquals("10:20:30.040", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        LocalTime test = new LocalTime(10, 20, 30, 40);
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("10:20:30.040", test.toString(((DateTimeFormatter) (null))));
    }
}

