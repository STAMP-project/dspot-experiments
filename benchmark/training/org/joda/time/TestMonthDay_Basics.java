/**
 * Copyright 2001-2010 Stephen Colebourne
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


/**
 * This class is a Junit unit test for MonthDay. Based on {@link TestYearMonth_Basics}
 */
public class TestMonthDay_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestMonthDay_Basics.PARIS);

    // private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(LONDON);
    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestMonthDay_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    // private static final Chronology ISO_PARIS = ISOChronology.getInstance(PARIS);
    // private static final Chronology ISO_LONDON = ISOChronology.getInstance(LONDON);
    // private static final Chronology ISO_TOKYO = ISOChronology.getInstance(TOKYO);
    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    // private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(PARIS);
    // private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(LONDON);
    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestMonthDay_Basics.TOKYO);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private DateTimeZone zone = null;

    public TestMonthDay_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet() {
        MonthDay test = new MonthDay();
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(9, test.get(DateTimeFieldType.dayOfMonth()));
        try {
            test.get(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.get(DateTimeFieldType.year());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSize() {
        MonthDay test = new MonthDay();
        TestCase.assertEquals(2, test.size());
    }

    public void testGetFieldType() {
        MonthDay test = new MonthDay(TestMonthDay_Basics.COPTIC_PARIS);
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), test.getFieldType(1));
        try {
            test.getFieldType((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getFieldType(2);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldTypes() {
        MonthDay test = new MonthDay(TestMonthDay_Basics.COPTIC_PARIS);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), fields[1]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField() {
        MonthDay test = new MonthDay(TestMonthDay_Basics.COPTIC_PARIS);
        TestCase.assertSame(TestMonthDay_Basics.COPTIC_UTC.monthOfYear(), test.getField(0));
        TestCase.assertSame(TestMonthDay_Basics.COPTIC_UTC.dayOfMonth(), test.getField(1));
        try {
            test.getField((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getField(2);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFields() {
        MonthDay test = new MonthDay(TestMonthDay_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(TestMonthDay_Basics.COPTIC_UTC.monthOfYear(), fields[0]);
        TestCase.assertSame(TestMonthDay_Basics.COPTIC_UTC.dayOfMonth(), fields[1]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue() {
        MonthDay test = new MonthDay();
        TestCase.assertEquals(6, test.getValue(0));
        TestCase.assertEquals(9, test.getValue(1));
        try {
            test.getValue((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getValue(2);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetValues() {
        MonthDay test = new MonthDay();
        int[] values = test.getValues();
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(6, values[0]);
        TestCase.assertEquals(9, values[1]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported() {
        MonthDay test = new MonthDay(TestMonthDay_Basics.COPTIC_PARIS);
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.hourOfDay()));
    }

    public void testEqualsHashCode() {
        MonthDay test1 = new MonthDay(10, 6, TestMonthDay_Basics.COPTIC_PARIS);
        MonthDay test2 = new MonthDay(10, 6, TestMonthDay_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        MonthDay test3 = new MonthDay(10, 6);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestMonthDay_Basics.MockMD()));
        TestCase.assertEquals(false, test1.equals(MockPartial.EMPTY_INSTANCE));
    }

    class MockMD extends MockPartial {
        @Override
        public Chronology getChronology() {
            return TestMonthDay_Basics.COPTIC_UTC;
        }

        @Override
        public DateTimeField[] getFields() {
            return new DateTimeField[]{ TestMonthDay_Basics.COPTIC_UTC.monthOfYear(), TestMonthDay_Basics.COPTIC_UTC.dayOfMonth() };
        }

        @Override
        public int[] getValues() {
            return new int[]{ 10, 6 };
        }
    }

    // -----------------------------------------------------------------------
    public void testCompareTo() {
        MonthDay test1 = new MonthDay(6, 6);
        MonthDay test1a = new MonthDay(6, 6);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        MonthDay test2 = new MonthDay(6, 7);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        MonthDay test3 = new MonthDay(6, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth() };
        int[] values = new int[]{ 6, 6 };
        Partial p = new Partial(types, values);
        TestCase.assertEquals(0, test1.compareTo(p));
        try {
            test1.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
        try {
            test1.compareTo(new LocalTime());
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
        Partial partial = new Partial().with(DateTimeFieldType.centuryOfEra(), 1).with(DateTimeFieldType.halfdayOfDay(), 0).with(DateTimeFieldType.dayOfMonth(), 9);
        try {
            new MonthDay(10, 6).compareTo(partial);
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_MD() {
        MonthDay test1 = new MonthDay(6, 6);
        MonthDay test1a = new MonthDay(6, 6);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        MonthDay test2 = new MonthDay(6, 7);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        MonthDay test3 = new MonthDay(6, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new MonthDay(6, 7).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_MD() {
        MonthDay test1 = new MonthDay(6, 6);
        MonthDay test1a = new MonthDay(6, 6);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        MonthDay test2 = new MonthDay(6, 7);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        MonthDay test3 = new MonthDay(6, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new MonthDay(6, 7).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_MD() {
        MonthDay test1 = new MonthDay(6, 6);
        MonthDay test1a = new MonthDay(6, 6);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        MonthDay test2 = new MonthDay(6, 7);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        MonthDay test3 = new MonthDay(6, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new MonthDay(6, 7).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithChronologyRetainFields_Chrono() {
        MonthDay base = new MonthDay(6, 6, TestMonthDay_Basics.COPTIC_PARIS);
        MonthDay test = base.withChronologyRetainFields(TestMonthDay_Basics.BUDDHIST_TOKYO);
        check(base, 6, 6);
        TestCase.assertEquals(TestMonthDay_Basics.COPTIC_UTC, base.getChronology());
        check(test, 6, 6);
        TestCase.assertEquals(TestMonthDay_Basics.BUDDHIST_UTC, test.getChronology());
    }

    public void testWithChronologyRetainFields_sameChrono() {
        MonthDay base = new MonthDay(6, 6, TestMonthDay_Basics.COPTIC_PARIS);
        MonthDay test = base.withChronologyRetainFields(TestMonthDay_Basics.COPTIC_TOKYO);
        TestCase.assertSame(base, test);
    }

    public void testWithChronologyRetainFields_nullChrono() {
        MonthDay base = new MonthDay(6, 6, TestMonthDay_Basics.COPTIC_PARIS);
        MonthDay test = base.withChronologyRetainFields(null);
        check(base, 6, 6);
        TestCase.assertEquals(TestMonthDay_Basics.COPTIC_UTC, base.getChronology());
        check(test, 6, 6);
        TestCase.assertEquals(TestMonthDay_Basics.ISO_UTC, test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testWithField() {
        MonthDay test = new MonthDay(9, 6);
        MonthDay result = test.withField(DateTimeFieldType.monthOfYear(), 10);
        TestCase.assertEquals(new MonthDay(9, 6), test);
        TestCase.assertEquals(new MonthDay(10, 6), result);
    }

    public void testWithField_nullField() {
        MonthDay test = new MonthDay(9, 6);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_unknownField() {
        MonthDay test = new MonthDay(9, 6);
        try {
            test.withField(DateTimeFieldType.hourOfDay(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_same() {
        MonthDay test = new MonthDay(9, 6);
        MonthDay result = test.withField(DateTimeFieldType.monthOfYear(), 9);
        TestCase.assertEquals(new MonthDay(9, 6), test);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded() {
        MonthDay test = new MonthDay(9, 6);
        MonthDay result = test.withFieldAdded(DurationFieldType.months(), 1);
        TestCase.assertEquals(new MonthDay(9, 6), test);
        TestCase.assertEquals(new MonthDay(10, 6), result);
    }

    public void testWithFieldAdded_nullField_zero() {
        MonthDay test = new MonthDay(9, 6);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_nullField_nonZero() {
        MonthDay test = new MonthDay(9, 6);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_zero() {
        MonthDay test = new MonthDay(9, 6);
        MonthDay result = test.withFieldAdded(DurationFieldType.months(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded_unknownField() {
        MonthDay test = new MonthDay(9, 6);
        try {
            test.withFieldAdded(DurationFieldType.hours(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        MonthDay test = new MonthDay(6, 5, BuddhistChronology.getInstance());
        MonthDay result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        MonthDay expected = new MonthDay(8, 9, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        MonthDay test = new MonthDay(6, 5, BuddhistChronology.getInstance());
        MonthDay result = test.plusMonths(1);
        MonthDay expected = new MonthDay(7, 5, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_fromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusMonths(1);
        MonthDay expected = new MonthDay(3, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_negativeFromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusMonths((-1));
        MonthDay expected = new MonthDay(1, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_endOfMonthAdjust() {
        MonthDay test = new MonthDay(3, 31, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusMonths(1);
        MonthDay expected = new MonthDay(4, 30, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_negativeEndOfMonthAdjust() {
        MonthDay test = new MonthDay(3, 31, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusMonths((-1));
        MonthDay expected = new MonthDay(2, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_same() {
        MonthDay test = new MonthDay(6, 5, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int_wrap() {
        MonthDay test = new MonthDay(6, 5, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.plusMonths(10);
        MonthDay expected = new MonthDay(4, 5, TestMonthDay_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
    }

    public void testPlusMonths_int_adjust() {
        MonthDay test = new MonthDay(7, 31, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.plusMonths(2);
        MonthDay expected = new MonthDay(9, 30, TestMonthDay_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
    }

    // -------------------------------------------------------------------------
    public void testPlusDays_int() {
        MonthDay test = new MonthDay(5, 10, BuddhistChronology.getInstance());
        MonthDay result = test.plusDays(1);
        MonthDay expected = new MonthDay(5, 11, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_wrapMonth() {
        MonthDay test = new MonthDay(11, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(31);
        MonthDay expected = new MonthDay(12, 2, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_wrapMonthTwice() {
        MonthDay test = new MonthDay(10, 31, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(32);
        MonthDay expected = new MonthDay(12, 2, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_wrapMonthIntoNextYear() {
        MonthDay test = new MonthDay(12, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(31);
        MonthDay expected = new MonthDay(1, 1, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_wrapMonthTwiceIntoNextYear() {
        MonthDay test = new MonthDay(11, 30, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(32);
        MonthDay expected = new MonthDay(1, 1, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_wrap50() {
        MonthDay test = new MonthDay(5, 15, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(50);
        MonthDay expected = new MonthDay(7, 4, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_toLeap() {
        MonthDay test = new MonthDay(2, 28, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(1);
        MonthDay expected = new MonthDay(2, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_fromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays(1);
        MonthDay expected = new MonthDay(3, 1, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_int_negativeFromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.plusDays((-1));
        MonthDay expected = new MonthDay(2, 28, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testPlusDays_same() {
        MonthDay test = new MonthDay(5, 10, BuddhistChronology.getInstance());
        MonthDay result = test.plusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        MonthDay test = new MonthDay(6, 5, BuddhistChronology.getInstance());
        MonthDay result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        MonthDay expected = new MonthDay(5, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        MonthDay test = new MonthDay(6, 5, BuddhistChronology.getInstance());
        MonthDay result = test.minusMonths(1);
        MonthDay expected = new MonthDay(5, 5, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_fromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusMonths(1);
        MonthDay expected = new MonthDay(1, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_negativeFromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusMonths((-1));
        MonthDay expected = new MonthDay(3, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_endOfMonthAdjust() {
        MonthDay test = new MonthDay(3, 31, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusMonths(1);
        MonthDay expected = new MonthDay(2, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_negativeEndOfMonthAdjust() {
        MonthDay test = new MonthDay(3, 31, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusMonths((-1));
        MonthDay expected = new MonthDay(4, 30, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_same() {
        MonthDay test = new MonthDay(6, 5, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int_wrap() {
        MonthDay test = new MonthDay(6, 5, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.minusMonths(10);
        MonthDay expected = new MonthDay(8, 5, TestMonthDay_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
    }

    public void testMinusMonths_int_adjust() {
        MonthDay test = new MonthDay(7, 31, TestMonthDay_Basics.ISO_UTC);
        MonthDay result = test.minusMonths(3);
        MonthDay expected = new MonthDay(4, 30, TestMonthDay_Basics.ISO_UTC);
        TestCase.assertEquals(expected, result);
    }

    // -------------------------------------------------------------------------
    public void testMinusDays_int() {
        MonthDay test = new MonthDay(5, 11, BuddhistChronology.getInstance());
        MonthDay result = test.minusDays(1);
        MonthDay expected = new MonthDay(5, 10, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_wrapMonth() {
        MonthDay test = new MonthDay(12, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(30);
        MonthDay expected = new MonthDay(11, 1, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_wrapMonthTwice() {
        MonthDay test = new MonthDay(12, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(31);
        MonthDay expected = new MonthDay(10, 31, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_wrapMonthIntoLastYear() {
        MonthDay test = new MonthDay(1, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(31);
        MonthDay expected = new MonthDay(12, 1, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_wrapMonthTwiceIntoLastYear() {
        MonthDay test = new MonthDay(1, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(32);
        MonthDay expected = new MonthDay(11, 30, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_toLeap() {
        MonthDay test = new MonthDay(3, 1, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(1);
        MonthDay expected = new MonthDay(2, 29, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_fromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays(1);
        MonthDay expected = new MonthDay(2, 28, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_int_negativeFromLeap() {
        MonthDay test = new MonthDay(2, 29, ISOChronology.getInstanceUTC());
        MonthDay result = test.minusDays((-1));
        MonthDay expected = new MonthDay(3, 1, ISOChronology.getInstance());
        TestCase.assertEquals(expected, result);
    }

    public void testMinusDays_same() {
        MonthDay test = new MonthDay(5, 11, BuddhistChronology.getInstance());
        MonthDay result = test.minusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToLocalDate() {
        MonthDay base = new MonthDay(6, 6, TestMonthDay_Basics.COPTIC_UTC);
        LocalDate test = base.toLocalDate(2009);
        TestCase.assertEquals(new LocalDate(2009, 6, 6, TestMonthDay_Basics.COPTIC_UTC), test);
        try {
            base.toLocalDate(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        MonthDay base = new MonthDay(6, 6, TestMonthDay_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTime test = base.toDateTime(dt);
        check(base, 6, 6);
        DateTime expected = dt;
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(6);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullRI() {
        MonthDay base = new MonthDay(6, 6);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 6, 6);
        DateTime expected = dt;
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(6);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        MonthDay test = new MonthDay(10, 6);
        check(test.withMonthOfYear(5), 5, 6);
        check(test.withDayOfMonth(2), 10, 2);
        try {
            test.withMonthOfYear(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.withMonthOfYear(13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        MonthDay test = new MonthDay(6, 6);
        TestCase.assertEquals(test.monthOfYear(), test.property(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(test.dayOfMonth(), test.property(DateTimeFieldType.dayOfMonth()));
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
        MonthDay test = new MonthDay(5, 6, TestMonthDay_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        MonthDay result = ((MonthDay) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        MonthDay test = new MonthDay(5, 6);
        TestCase.assertEquals("--05-06", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        MonthDay test = new MonthDay(5, 6);
        TestCase.assertEquals("05 \ufffd\ufffd", test.toString("MM HH"));
        TestCase.assertEquals("--05-06", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        MonthDay test = new MonthDay(5, 6);
        TestCase.assertEquals("\ufffd 6/5", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("\ufffd 6/5", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("--05-06", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("\ufffd 6/5", test.toString("EEE d/M", null));
        TestCase.assertEquals("--05-06", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        MonthDay test = new MonthDay(5, 6);
        TestCase.assertEquals("05 \ufffd\ufffd", test.toString(DateTimeFormat.forPattern("MM HH")));
        TestCase.assertEquals("--05-06", test.toString(((DateTimeFormatter) (null))));
    }
}

