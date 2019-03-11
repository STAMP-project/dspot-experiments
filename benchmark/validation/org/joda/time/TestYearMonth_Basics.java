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
 * This class is a Junit unit test for YearMonth.
 *
 * @author Stephen Colebourne
 */
public class TestYearMonth_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestYearMonth_Basics.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestYearMonth_Basics.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestYearMonth_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    // private static final Chronology ISO_PARIS = ISOChronology.getInstance(PARIS);
    // private static final Chronology ISO_LONDON = ISOChronology.getInstance(LONDON);
    // private static final Chronology ISO_TOKYO = ISOChronology.getInstance(TOKYO);
    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    // private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(PARIS);
    // private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(LONDON);
    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestYearMonth_Basics.TOKYO);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private DateTimeZone zone = null;

    public TestYearMonth_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet() {
        YearMonth test = new YearMonth();
        TestCase.assertEquals(1970, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
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
        YearMonth test = new YearMonth();
        TestCase.assertEquals(2, test.size());
    }

    public void testGetFieldType() {
        YearMonth test = new YearMonth(TestYearMonth_Basics.COPTIC_PARIS);
        TestCase.assertSame(DateTimeFieldType.year(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), test.getFieldType(1));
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
        YearMonth test = new YearMonth(TestYearMonth_Basics.COPTIC_PARIS);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(DateTimeFieldType.year(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), fields[1]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField() {
        YearMonth test = new YearMonth(TestYearMonth_Basics.COPTIC_PARIS);
        TestCase.assertSame(TestYearMonth_Basics.COPTIC_UTC.year(), test.getField(0));
        TestCase.assertSame(TestYearMonth_Basics.COPTIC_UTC.monthOfYear(), test.getField(1));
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
        YearMonth test = new YearMonth(TestYearMonth_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(TestYearMonth_Basics.COPTIC_UTC.year(), fields[0]);
        TestCase.assertSame(TestYearMonth_Basics.COPTIC_UTC.monthOfYear(), fields[1]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue() {
        YearMonth test = new YearMonth();
        TestCase.assertEquals(1970, test.getValue(0));
        TestCase.assertEquals(6, test.getValue(1));
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
        YearMonth test = new YearMonth();
        int[] values = test.getValues();
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(1970, values[0]);
        TestCase.assertEquals(6, values[1]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported() {
        YearMonth test = new YearMonth(TestYearMonth_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.hourOfDay()));
    }

    public void testEqualsHashCode() {
        YearMonth test1 = new YearMonth(1970, 6, TestYearMonth_Basics.COPTIC_PARIS);
        YearMonth test2 = new YearMonth(1970, 6, TestYearMonth_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        YearMonth test3 = new YearMonth(1971, 6);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestYearMonth_Basics.MockYM()));
        TestCase.assertEquals(false, test1.equals(MockPartial.EMPTY_INSTANCE));
    }

    class MockYM extends MockPartial {
        public Chronology getChronology() {
            return TestYearMonth_Basics.COPTIC_UTC;
        }

        public DateTimeField[] getFields() {
            return new DateTimeField[]{ TestYearMonth_Basics.COPTIC_UTC.year(), TestYearMonth_Basics.COPTIC_UTC.monthOfYear() };
        }

        public int[] getValues() {
            return new int[]{ 1970, 6 };
        }
    }

    // -----------------------------------------------------------------------
    public void testCompareTo() {
        YearMonth test1 = new YearMonth(2005, 6);
        YearMonth test1a = new YearMonth(2005, 6);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        YearMonth test2 = new YearMonth(2005, 7);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        YearMonth test3 = new YearMonth(2005, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.monthOfYear() };
        int[] values = new int[]{ 2005, 6 };
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
            new YearMonth(1970, 6).compareTo(partial);
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_YM() {
        YearMonth test1 = new YearMonth(2005, 6);
        YearMonth test1a = new YearMonth(2005, 6);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        YearMonth test2 = new YearMonth(2005, 7);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        YearMonth test3 = new YearMonth(2005, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new YearMonth(2005, 7).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_YM() {
        YearMonth test1 = new YearMonth(2005, 6);
        YearMonth test1a = new YearMonth(2005, 6);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        YearMonth test2 = new YearMonth(2005, 7);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        YearMonth test3 = new YearMonth(2005, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new YearMonth(2005, 7).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_YM() {
        YearMonth test1 = new YearMonth(2005, 6);
        YearMonth test1a = new YearMonth(2005, 6);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        YearMonth test2 = new YearMonth(2005, 7);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        YearMonth test3 = new YearMonth(2005, 7, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new YearMonth(2005, 7).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithChronologyRetainFields_Chrono() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);
        YearMonth test = base.withChronologyRetainFields(TestYearMonth_Basics.BUDDHIST_TOKYO);
        check(base, 2005, 6);
        TestCase.assertEquals(TestYearMonth_Basics.COPTIC_UTC, base.getChronology());
        check(test, 2005, 6);
        TestCase.assertEquals(TestYearMonth_Basics.BUDDHIST_UTC, test.getChronology());
    }

    public void testWithChronologyRetainFields_sameChrono() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);
        YearMonth test = base.withChronologyRetainFields(TestYearMonth_Basics.COPTIC_TOKYO);
        TestCase.assertSame(base, test);
    }

    public void testWithChronologyRetainFields_nullChrono() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);
        YearMonth test = base.withChronologyRetainFields(null);
        check(base, 2005, 6);
        TestCase.assertEquals(TestYearMonth_Basics.COPTIC_UTC, base.getChronology());
        check(test, 2005, 6);
        TestCase.assertEquals(TestYearMonth_Basics.ISO_UTC, test.getChronology());
    }

    public void testWithChronologyRetainFields_invalidInNewChrono() {
        YearMonth base = new YearMonth(2005, 13, TestYearMonth_Basics.COPTIC_UTC);
        try {
            base.withChronologyRetainFields(TestYearMonth_Basics.ISO_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testWithField() {
        YearMonth test = new YearMonth(2004, 6);
        YearMonth result = test.withField(DateTimeFieldType.year(), 2006);
        TestCase.assertEquals(new YearMonth(2004, 6), test);
        TestCase.assertEquals(new YearMonth(2006, 6), result);
    }

    public void testWithField_nullField() {
        YearMonth test = new YearMonth(2004, 6);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_unknownField() {
        YearMonth test = new YearMonth(2004, 6);
        try {
            test.withField(DateTimeFieldType.hourOfDay(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_same() {
        YearMonth test = new YearMonth(2004, 6);
        YearMonth result = test.withField(DateTimeFieldType.year(), 2004);
        TestCase.assertEquals(new YearMonth(2004, 6), test);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded() {
        YearMonth test = new YearMonth(2004, 6);
        YearMonth result = test.withFieldAdded(DurationFieldType.years(), 6);
        TestCase.assertEquals(new YearMonth(2004, 6), test);
        TestCase.assertEquals(new YearMonth(2010, 6), result);
    }

    public void testWithFieldAdded_nullField_zero() {
        YearMonth test = new YearMonth(2004, 6);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_nullField_nonZero() {
        YearMonth test = new YearMonth(2004, 6);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_zero() {
        YearMonth test = new YearMonth(2004, 6);
        YearMonth result = test.withFieldAdded(DurationFieldType.years(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded_unknownField() {
        YearMonth test = new YearMonth(2004, 6);
        try {
            test.withFieldAdded(DurationFieldType.hours(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        YearMonth expected = new YearMonth(2003, 7, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusYears_int() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.plusYears(1);
        YearMonth expected = new YearMonth(2003, 5, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.plusMonths(1);
        YearMonth expected = new YearMonth(2002, 6, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        YearMonth expected = new YearMonth(2001, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusYears_int() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.minusYears(1);
        YearMonth expected = new YearMonth(2001, 5, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        YearMonth test = new YearMonth(2002, 5, BuddhistChronology.getInstance());
        YearMonth result = test.minusMonths(1);
        YearMonth expected = new YearMonth(2002, 4, BuddhistChronology.getInstance());
        TestCase.assertEquals(expected, result);
        result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToLocalDate() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_UTC);
        LocalDate test = base.toLocalDate(9);
        TestCase.assertEquals(new LocalDate(2005, 6, 9, TestYearMonth_Basics.COPTIC_UTC), test);
        try {
            base.toLocalDate(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTime test = base.toDateTime(dt);
        check(base, 2005, 6);
        DateTime expected = dt;
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullRI() {
        YearMonth base = new YearMonth(2005, 6);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 2005, 6);
        DateTime expected = dt;
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToInterval() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval();
        check(base, 2005, 6);
        DateTime start = new DateTime(2005, 6, 1, 0, 0, TestYearMonth_Basics.COPTIC_LONDON);
        DateTime end = new DateTime(2005, 7, 1, 0, 0, TestYearMonth_Basics.COPTIC_LONDON);
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToInterval_Zone() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval(TestYearMonth_Basics.TOKYO);
        check(base, 2005, 6);
        DateTime start = new DateTime(2005, 6, 1, 0, 0, TestYearMonth_Basics.COPTIC_TOKYO);
        DateTime end = new DateTime(2005, 7, 1, 0, 0, TestYearMonth_Basics.COPTIC_TOKYO);
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    public void testToInterval_nullZone() {
        YearMonth base = new YearMonth(2005, 6, TestYearMonth_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval(null);
        check(base, 2005, 6);
        DateTime start = new DateTime(2005, 6, 1, 0, 0, TestYearMonth_Basics.COPTIC_LONDON);
        DateTime end = new DateTime(2005, 7, 1, 0, 0, TestYearMonth_Basics.COPTIC_LONDON);
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        YearMonth test = new YearMonth(1970, 6);
        check(test.withYear(2000), 2000, 6);
        check(test.withMonthOfYear(2), 1970, 2);
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
        YearMonth test = new YearMonth(2005, 6);
        TestCase.assertEquals(test.year(), test.property(DateTimeFieldType.year()));
        TestCase.assertEquals(test.monthOfYear(), test.property(DateTimeFieldType.monthOfYear()));
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
        YearMonth test = new YearMonth(1972, 6, TestYearMonth_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        YearMonth result = ((YearMonth) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        YearMonth test = new YearMonth(2002, 6);
        TestCase.assertEquals("2002-06", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        YearMonth test = new YearMonth(2002, 6);
        TestCase.assertEquals("2002 \ufffd\ufffd", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        YearMonth test = new YearMonth(2002, 6);
        TestCase.assertEquals("\ufffd \ufffd/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("\ufffd \ufffd/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("2002-06", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("\ufffd \ufffd/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("2002-06", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        YearMonth test = new YearMonth(2002, 6);
        TestCase.assertEquals("2002 \ufffd\ufffd", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("2002-06", test.toString(((DateTimeFormatter) (null))));
    }
}

