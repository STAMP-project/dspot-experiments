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
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for TimeOfDay.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestTimeOfDay_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final int OFFSET = 1;

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestTimeOfDay_Basics.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestTimeOfDay_Basics.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestTimeOfDay_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestTimeOfDay_Basics.TOKYO);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestTimeOfDay_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet() {
        TimeOfDay test = new TimeOfDay();
        TestCase.assertEquals((10 + (TestTimeOfDay_Basics.OFFSET)), test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(30, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(40, test.get(DateTimeFieldType.millisOfSecond()));
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
        TimeOfDay test = new TimeOfDay();
        TestCase.assertEquals(4, test.size());
    }

    public void testGetFieldType() {
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Basics.COPTIC_PARIS);
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
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Basics.COPTIC_PARIS);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertSame(DateTimeFieldType.hourOfDay(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.minuteOfHour(), fields[1]);
        TestCase.assertSame(DateTimeFieldType.secondOfMinute(), fields[2]);
        TestCase.assertSame(DateTimeFieldType.millisOfSecond(), fields[3]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField() {
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Basics.COPTIC_PARIS);
        TestCase.assertSame(CopticChronology.getInstanceUTC().hourOfDay(), test.getField(0));
        TestCase.assertSame(CopticChronology.getInstanceUTC().minuteOfHour(), test.getField(1));
        TestCase.assertSame(CopticChronology.getInstanceUTC().secondOfMinute(), test.getField(2));
        TestCase.assertSame(CopticChronology.getInstanceUTC().millisOfSecond(), test.getField(3));
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
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertSame(CopticChronology.getInstanceUTC().hourOfDay(), fields[0]);
        TestCase.assertSame(CopticChronology.getInstanceUTC().minuteOfHour(), fields[1]);
        TestCase.assertSame(CopticChronology.getInstanceUTC().secondOfMinute(), fields[2]);
        TestCase.assertSame(CopticChronology.getInstanceUTC().millisOfSecond(), fields[3]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
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
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        int[] values = test.getValues();
        TestCase.assertEquals(10, values[0]);
        TestCase.assertEquals(20, values[1]);
        TestCase.assertEquals(30, values[2]);
        TestCase.assertEquals(40, values[3]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported() {
        TimeOfDay test = new TimeOfDay(TestTimeOfDay_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.dayOfMonth()));
    }

    public void testEqualsHashCode() {
        TimeOfDay test1 = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        TimeOfDay test2 = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        TimeOfDay test3 = new TimeOfDay(15, 20, 30, 40);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestTimeOfDay_Basics.MockInstant()));
        TestCase.assertEquals(false, test1.equals(MockPartial.EMPTY_INSTANCE));
    }

    class MockInstant extends MockPartial {
        public Chronology getChronology() {
            return CopticChronology.getInstanceUTC();
        }

        public DateTimeField[] getFields() {
            return new DateTimeField[]{ CopticChronology.getInstanceUTC().hourOfDay(), CopticChronology.getInstanceUTC().minuteOfHour(), CopticChronology.getInstanceUTC().secondOfMinute(), CopticChronology.getInstanceUTC().millisOfSecond() };
        }

        public int[] getValues() {
            return new int[]{ 10, 20, 30, 40 };
        }
    }

    // -----------------------------------------------------------------------
    public void testCompareTo() {
        TimeOfDay test1 = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay test1a = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        TimeOfDay test2 = new TimeOfDay(10, 20, 35, 40);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        TimeOfDay test3 = new TimeOfDay(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(), DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond() };
        int[] values = new int[]{ 10, 20, 30, 40 };
        Partial p = new Partial(types, values);
        TestCase.assertEquals(0, test1.compareTo(p));
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
    public void testIsEqual_TOD() {
        TimeOfDay test1 = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay test1a = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        TimeOfDay test2 = new TimeOfDay(10, 20, 35, 40);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        TimeOfDay test3 = new TimeOfDay(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new TimeOfDay(10, 20, 35, 40).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_TOD() {
        TimeOfDay test1 = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay test1a = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        TimeOfDay test2 = new TimeOfDay(10, 20, 35, 40);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        TimeOfDay test3 = new TimeOfDay(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new TimeOfDay(10, 20, 35, 40).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_TOD() {
        TimeOfDay test1 = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay test1a = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        TimeOfDay test2 = new TimeOfDay(10, 20, 35, 40);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        TimeOfDay test3 = new TimeOfDay(10, 20, 35, 40, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new TimeOfDay(10, 20, 35, 40).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithChronologyRetainFields_Chrono() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        TimeOfDay test = base.withChronologyRetainFields(TestTimeOfDay_Basics.BUDDHIST_TOKYO);
        check(base, 10, 20, 30, 40);
        TestCase.assertEquals(TestTimeOfDay_Basics.COPTIC_UTC, base.getChronology());
        check(test, 10, 20, 30, 40);
        TestCase.assertEquals(TestTimeOfDay_Basics.BUDDHIST_UTC, test.getChronology());
    }

    public void testWithChronologyRetainFields_sameChrono() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        TimeOfDay test = base.withChronologyRetainFields(TestTimeOfDay_Basics.COPTIC_TOKYO);
        TestCase.assertSame(base, test);
    }

    public void testWithChronologyRetainFields_nullChrono() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        TimeOfDay test = base.withChronologyRetainFields(null);
        check(base, 10, 20, 30, 40);
        TestCase.assertEquals(TestTimeOfDay_Basics.COPTIC_UTC, base.getChronology());
        check(test, 10, 20, 30, 40);
        TestCase.assertEquals(TestTimeOfDay_Basics.ISO_UTC, test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testWithField1() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay result = test.withField(DateTimeFieldType.hourOfDay(), 15);
        TestCase.assertEquals(new TimeOfDay(10, 20, 30, 40), test);
        TestCase.assertEquals(new TimeOfDay(15, 20, 30, 40), result);
    }

    public void testWithField2() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField3() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        try {
            test.withField(DateTimeFieldType.dayOfMonth(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField4() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay result = test.withField(DateTimeFieldType.hourOfDay(), 10);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded1() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay result = test.withFieldAdded(DurationFieldType.hours(), 6);
        TestCase.assertEquals(new TimeOfDay(10, 20, 30, 40), test);
        TestCase.assertEquals(new TimeOfDay(16, 20, 30, 40), result);
    }

    public void testWithFieldAdded2() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded3() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded4() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay result = test.withFieldAdded(DurationFieldType.hours(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded5() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        try {
            test.withFieldAdded(DurationFieldType.days(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded6() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay result = test.withFieldAdded(DurationFieldType.hours(), 16);
        TestCase.assertEquals(new TimeOfDay(10, 20, 30, 40), test);
        TestCase.assertEquals(new TimeOfDay(2, 20, 30, 40), result);
    }

    public void testWithFieldAdded7() {
        TimeOfDay test = new TimeOfDay(23, 59, 59, 999);
        TimeOfDay result = test.withFieldAdded(DurationFieldType.millis(), 1);
        TestCase.assertEquals(new TimeOfDay(0, 0, 0, 0), result);
        test = new TimeOfDay(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.seconds(), 1);
        TestCase.assertEquals(new TimeOfDay(0, 0, 0, 999), result);
        test = new TimeOfDay(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.minutes(), 1);
        TestCase.assertEquals(new TimeOfDay(0, 0, 59, 999), result);
        test = new TimeOfDay(23, 59, 59, 999);
        result = test.withFieldAdded(DurationFieldType.hours(), 1);
        TestCase.assertEquals(new TimeOfDay(0, 59, 59, 999), result);
    }

    public void testWithFieldAdded8() {
        TimeOfDay test = new TimeOfDay(0, 0, 0, 0);
        TimeOfDay result = test.withFieldAdded(DurationFieldType.millis(), (-1));
        TestCase.assertEquals(new TimeOfDay(23, 59, 59, 999), result);
        test = new TimeOfDay(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.seconds(), (-1));
        TestCase.assertEquals(new TimeOfDay(23, 59, 59, 0), result);
        test = new TimeOfDay(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.minutes(), (-1));
        TestCase.assertEquals(new TimeOfDay(23, 59, 0, 0), result);
        test = new TimeOfDay(0, 0, 0, 0);
        result = test.withFieldAdded(DurationFieldType.hours(), (-1));
        TestCase.assertEquals(new TimeOfDay(23, 0, 0, 0), result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, BuddhistChronology.getInstance());
        TimeOfDay result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        TimeOfDay expected = new TimeOfDay(15, 26, 37, 48, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusHours_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.plusHours(1);
        TimeOfDay expected = new TimeOfDay(2, 2, 3, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMinutes_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.plusMinutes(1);
        TimeOfDay expected = new TimeOfDay(1, 3, 3, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusSeconds_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.plusSeconds(1);
        TimeOfDay expected = new TimeOfDay(1, 2, 4, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMillis_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.plusMillis(1);
        TimeOfDay expected = new TimeOfDay(1, 2, 3, 5, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, BuddhistChronology.getInstance());
        TimeOfDay result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        TimeOfDay expected = new TimeOfDay(9, 19, 29, 39, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusHours_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.minusHours(1);
        TimeOfDay expected = new TimeOfDay(0, 2, 3, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMinutes_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.minusMinutes(1);
        TimeOfDay expected = new TimeOfDay(1, 1, 3, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusSeconds_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.minusSeconds(1);
        TimeOfDay expected = new TimeOfDay(1, 2, 2, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMillis_int() {
        TimeOfDay test = new TimeOfDay(1, 2, 3, 4, BuddhistChronology.getInstance());
        TimeOfDay result = test.minusMillis(1);
        TimeOfDay expected = new TimeOfDay(1, 2, 3, 3, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToLocalTime() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_UTC);
        LocalTime test = base.toLocalTime();
        TestCase.assertEquals(new LocalTime(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_UTC), test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeToday() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday();
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestTimeOfDay_Basics.COPTIC_LONDON);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeToday_Zone() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday(TestTimeOfDay_Basics.TOKYO);
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestTimeOfDay_Basics.COPTIC_TOKYO);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTimeToday_nullZone() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeToday(((DateTimeZone) (null)));
        check(base, 10, 20, 30, 40);
        DateTime expected = new DateTime(dt.getMillis(), TestTimeOfDay_Basics.COPTIC_LONDON);
        expected = expected.hourOfDay().setCopy(10);
        expected = expected.minuteOfHour().setCopy(20);
        expected = expected.secondOfMinute().setCopy(30);
        expected = expected.millisOfSecond().setCopy(40);
        TestCase.assertEquals(expected, test);
    }

    // Removed as too complex
    // /**
    // * Merges two partial together, taking account of the different chronologies.
    // *
    // * @param main  the main partial
    // * @param base  the partial to use as a base to merge on top of
    // * @param instant  the instant to start from and to use for missing fields
    // * @return the merged instant
    // */
    // public long merge(ReadablePartial main, ReadablePartial base, long instant) {
    // DateTimeZone zone = main.getChronology().getZone();
    // instant = base.getChronology().withZone(zone).set(base, instant);
    // return set(main, instant);
    // }
    // 
    // //-----------------------------------------------------------------------
    // /**
    // * Converts this object to a DateTime using a YearMonthDay to fill in the
    // * missing fields and using the default time zone.
    // * This instance is immutable and unaffected by this method call.
    // * <p>
    // * The resulting chronology is determined by the chronology of this
    // * TimeOfDay plus the time zone.
    // * <p>
    // * This method makes use of the chronology of the specified YearMonthDay
    // * in the calculation. This can be significant when mixing chronologies.
    // * If the YearMonthDay is in the same chronology as this instance the
    // * method will perform exactly as you might expect.
    // * <p>
    // * If the chronologies differ, then both this TimeOfDay and the YearMonthDay
    // * are converted to the destination chronology and then merged. As a result
    // * it may be the case that the year, monthOfYear and dayOfMonth fields on
    // * the result are different from the values returned by the methods on the
    // * YearMonthDay.
    // * <p>
    // * See {@link DateTime#withFields(ReadablePartial)} for an algorithm that
    // * ignores the chronology.
    // *
    // * @param date  the date to use, null means today
    // * @return the DateTime instance
    // */
    // public DateTime toDateTime(YearMonthDay date) {
    // return toDateTime(date, null);
    // }
    // 
    // /**
    // * Converts this object to a DateTime using a YearMonthDay to fill in the
    // * missing fields.
    // * This instance is immutable and unaffected by this method call.
    // * <p>
    // * The resulting chronology is determined by the chronology of this
    // * TimeOfDay plus the time zone.
    // * <p>
    // * This method makes use of the chronology of the specified YearMonthDay
    // * in the calculation. This can be significant when mixing chronologies.
    // * If the YearMonthDay is in the same chronology as this instance the
    // * method will perform exactly as you might expect.
    // * <p>
    // * If the chronologies differ, then both this TimeOfDay and the YearMonthDay
    // * are converted to the destination chronology and then merged. As a result
    // * it may be the case that the year, monthOfYear and dayOfMonth fields on
    // * the result are different from the values returned by the methods on the
    // * YearMonthDay.
    // * <p>
    // * See {@link DateTime#withFields(ReadablePartial)} for an algorithm that
    // * ignores the chronology and just assigns the fields.
    // *
    // * @param date  the date to use, null means today
    // * @param zone  the zone to get the DateTime in, null means default
    // * @return the DateTime instance
    // */
    // public DateTime toDateTime(YearMonthDay date, DateTimeZone zone) {
    // Chronology chrono = getChronology().withZone(zone);
    // if (date == null) {
    // DateTime dt = new DateTime(chrono);
    // return dt.withFields(this);
    // } else {
    // long millis = chrono.merge(this, date, DateTimeUtils.currentTimeMillis());
    // return new DateTime(millis, chrono);
    // }
    // }
    // 
    // //-----------------------------------------------------------------------
    // public void testToDateTime_YMD() {
    // TimeOfDay base = new TimeOfDay(10, 20, 30, 40, COPTIC_PARIS); // PARIS irrelevant
    // YearMonthDay ymd = new YearMonthDay(new DateMidnight(2004, 6, 9), BUDDHIST_TOKYO);
    // 
    // DateTime test = base.toDateTime(ymd);
    // check(base, 10, 20, 30, 40);
    // DateTime expected = new DateTime(ymd.toDateMidnight(LONDON), COPTIC_LONDON);
    // expected = expected.hourOfDay().setCopy(10);
    // expected = expected.minuteOfHour().setCopy(20);
    // expected = expected.secondOfMinute().setCopy(30);
    // expected = expected.millisOfSecond().setCopy(40);
    // assertEquals(expected, test);
    // }
    // 
    // public void testToDateTime_nullYMD() {
    // TimeOfDay base = new TimeOfDay(10, 20, 30, 40, COPTIC_PARIS); // PARIS irrelevant
    // 
    // DateTime test = base.toDateTime((YearMonthDay) null);
    // check(base, 10, 20, 30, 40);
    // DateTime expected = new DateTime(COPTIC_LONDON);
    // expected = expected.hourOfDay().setCopy(10);
    // expected = expected.minuteOfHour().setCopy(20);
    // expected = expected.secondOfMinute().setCopy(30);
    // expected = expected.millisOfSecond().setCopy(40);
    // assertEquals(expected, test);
    // }
    // 
    // //-----------------------------------------------------------------------
    // public void testToDateTime_YMD_Zone() {
    // TimeOfDay base = new TimeOfDay(10, 20, 30, 40, COPTIC_PARIS); // PARIS irrelevant
    // YearMonthDay ymd = new YearMonthDay(new DateMidnight(2004, 6, 9), BUDDHIST_LONDON);
    // 
    // DateTime test = base.toDateTime(ymd, TOKYO);
    // check(base, 10, 20, 30, 40);
    // DateTime expected = new DateTime(ymd.toDateMidnight(TOKYO), COPTIC_TOKYO);
    // expected = expected.hourOfDay().setCopy(10);
    // expected = expected.minuteOfHour().setCopy(20);
    // expected = expected.secondOfMinute().setCopy(30);
    // expected = expected.millisOfSecond().setCopy(40);
    // assertEquals(expected, test);
    // }
    // 
    // public void testToDateTime_YMD_nullZone() {
    // TimeOfDay base = new TimeOfDay(10, 20, 30, 40, COPTIC_PARIS); // PARIS irrelevant
    // YearMonthDay ymd = new YearMonthDay(new DateMidnight(2004, 6, 9), BUDDHIST_LONDON);
    // 
    // DateTime test = base.toDateTime(ymd, null);
    // check(base, 10, 20, 30, 40);
    // DateTime expected = new DateTime(ymd.toDateMidnight(LONDON), COPTIC_LONDON);
    // expected = expected.hourOfDay().setCopy(10);
    // expected = expected.minuteOfHour().setCopy(20);
    // expected = expected.secondOfMinute().setCopy(30);
    // expected = expected.millisOfSecond().setCopy(40);
    // assertEquals(expected, test);
    // }
    // 
    // public void testToDateTime_nullYMD_Zone() {
    // TimeOfDay base = new TimeOfDay(10, 20, 30, 40, COPTIC_PARIS); // PARIS irrelevant
    // 
    // DateTime test = base.toDateTime((YearMonthDay) null, TOKYO);
    // check(base, 10, 20, 30, 40);
    // DateTime expected = new DateTime(COPTIC_TOKYO);
    // expected = expected.hourOfDay().setCopy(10);
    // expected = expected.minuteOfHour().setCopy(20);
    // expected = expected.secondOfMinute().setCopy(30);
    // expected = expected.millisOfSecond().setCopy(40);
    // assertEquals(expected, test);
    // }
    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        TimeOfDay base = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(0L);// LONDON zone

        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        DateTime test = base.toDateTime(dt);
        check(base, 10, 20, 30, 40);
        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        TestCase.assertEquals("1970-01-01T10:20:30.040+01:00", test.toString());
    }

    public void testToDateTime_nullRI() {
        TimeOfDay base = new TimeOfDay(1, 2, 3, 4);
        DateTimeUtils.setCurrentMillisFixed(TEST_TIME2);
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 1, 2, 3, 4);
        TestCase.assertEquals("1970-01-02T01:02:03.004+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        check(test.withHourOfDay(6), 6, 20, 30, 40);
        check(test.withMinuteOfHour(6), 10, 6, 30, 40);
        check(test.withSecondOfMinute(6), 10, 20, 6, 40);
        check(test.withMillisOfSecond(6), 10, 20, 30, 6);
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
    public void testProperty() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(test.hourOfDay(), test.property(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(test.minuteOfHour(), test.property(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(test.secondOfMinute(), test.property(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(test.millisOfSecond(), test.property(DateTimeFieldType.millisOfSecond()));
        try {
            test.property(DateTimeFieldType.millisOfDay());
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
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40, TestTimeOfDay_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        TimeOfDay result = ((TimeOfDay) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals("T10:20:30.040", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString("yyyy HH"));
        TestCase.assertEquals("T10:20:30.040", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals("10 20", test.toString("H m", Locale.ENGLISH));
        TestCase.assertEquals("T10:20:30.040", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("10 20", test.toString("H m", null));
        TestCase.assertEquals("T10:20:30.040", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("T10:20:30.040", test.toString(((DateTimeFormatter) (null))));
    }
}

