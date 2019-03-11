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


import DateTimeConstants.AD;
import DateTimeConstants.BC;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.chrono.StrictChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;


/**
 * This class is a Junit unit test for LocalDate.
 *
 * @author Stephen Colebourne
 */
public class TestLocalDate_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEW_YORK = DateTimeZone.forID("America/New_York");

    // private static final int OFFSET = 1;
    private static final GJChronology GJ_UTC = GJChronology.getInstanceUTC();

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestLocalDate_Basics.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestLocalDate_Basics.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestLocalDate_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    // private static final Chronology ISO_PARIS = ISOChronology.getInstance(PARIS);
    private static final Chronology ISO_LONDON = ISOChronology.getInstance(TestLocalDate_Basics.LONDON);

    private static final Chronology ISO_NEW_YORK = ISOChronology.getInstance(TestLocalDate_Basics.NEW_YORK);

    // private static final Chronology ISO_TOKYO = ISOChronology.getInstance(TOKYO);
    // private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();
    private static final Chronology BUDDHIST_PARIS = BuddhistChronology.getInstance(TestLocalDate_Basics.PARIS);

    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(TestLocalDate_Basics.LONDON);

    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestLocalDate_Basics.TOKYO);

    // private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();
    /**
     * Mock zone simulating Asia/Gaza cutover at midnight 2007-04-01
     */
    private static long CUTOVER_GAZA = 1175378400000L;

    private static int OFFSET_GAZA = 7200000;// +02:00


    private static final DateTimeZone MOCK_GAZA = new MockZone(TestLocalDate_Basics.CUTOVER_GAZA, TestLocalDate_Basics.OFFSET_GAZA, 3600);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // private long TEST_TIME1 =
    // (31L + 28L + 31L + 6L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 12L * DateTimeConstants.MILLIS_PER_HOUR
    // + 24L * DateTimeConstants.MILLIS_PER_MINUTE;
    // 
    // private long TEST_TIME2 =
    // (365L + 31L + 28L + 31L + 30L + 7L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 14L * DateTimeConstants.MILLIS_PER_HOUR
    // + 28L * DateTimeConstants.MILLIS_PER_MINUTE;
    private DateTimeZone zone = null;

    private Locale systemDefaultLocale = null;

    public TestLocalDate_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeFieldType() {
        LocalDate test = new LocalDate();
        TestCase.assertEquals(1970, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(9, test.get(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(160, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(24, test.get(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(1970, test.get(DateTimeFieldType.weekyear()));
        try {
            test.get(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.get(DateTimeFieldType.hourOfDay());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSize() {
        LocalDate test = new LocalDate();
        TestCase.assertEquals(3, test.size());
    }

    public void testGetFieldType_int() {
        LocalDate test = new LocalDate(TestLocalDate_Basics.COPTIC_PARIS);
        TestCase.assertSame(DateTimeFieldType.year(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), test.getFieldType(1));
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), test.getFieldType(2));
        try {
            test.getFieldType((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getFieldType(3);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldTypes() {
        LocalDate test = new LocalDate(TestLocalDate_Basics.COPTIC_PARIS);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertSame(DateTimeFieldType.year(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), fields[1]);
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), fields[2]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField_int() {
        LocalDate test = new LocalDate(TestLocalDate_Basics.COPTIC_PARIS);
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.year(), test.getField(0));
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.monthOfYear(), test.getField(1));
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.dayOfMonth(), test.getField(2));
        try {
            test.getField((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getField(3);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFields() {
        LocalDate test = new LocalDate(TestLocalDate_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.year(), fields[0]);
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.monthOfYear(), fields[1]);
        TestCase.assertSame(TestLocalDate_Basics.COPTIC_UTC.dayOfMonth(), fields[2]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue_int() {
        LocalDate test = new LocalDate();
        TestCase.assertEquals(1970, test.getValue(0));
        TestCase.assertEquals(6, test.getValue(1));
        TestCase.assertEquals(9, test.getValue(2));
        try {
            test.getValue((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getValue(3);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetValues() {
        LocalDate test = new LocalDate();
        int[] values = test.getValues();
        TestCase.assertEquals(1970, values[0]);
        TestCase.assertEquals(6, values[1]);
        TestCase.assertEquals(9, values[2]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported_DateTimeFieldType() {
        LocalDate test = new LocalDate(TestLocalDate_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.yearOfCentury()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.yearOfEra()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.centuryOfEra()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekyearOfCentury()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.era()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(false, test.isSupported(((DateTimeFieldType) (null))));
    }

    public void testIsSupported_DurationFieldType() {
        LocalDate test = new LocalDate(1970, 6, 9);
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.eras()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.centuries()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.years()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.months()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.weekyears()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.weeks()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.days()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.hours()));
        TestCase.assertEquals(false, test.isSupported(((DurationFieldType) (null))));
    }

    class MockInstant extends MockPartial {
        public Chronology getChronology() {
            return TestLocalDate_Basics.COPTIC_UTC;
        }

        public DateTimeField[] getFields() {
            return new DateTimeField[]{ TestLocalDate_Basics.COPTIC_UTC.year(), TestLocalDate_Basics.COPTIC_UTC.monthOfYear(), TestLocalDate_Basics.COPTIC_UTC.dayOfMonth() };
        }

        public int[] getValues() {
            return new int[]{ 1970, 6, 9 };
        }
    }

    public void testEqualsHashCodeLenient() {
        LocalDate test1 = new LocalDate(1970, 6, 9, LenientChronology.getInstance(TestLocalDate_Basics.COPTIC_PARIS));
        LocalDate test2 = new LocalDate(1970, 6, 9, LenientChronology.getInstance(TestLocalDate_Basics.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
    }

    public void testEqualsHashCodeStrict() {
        LocalDate test1 = new LocalDate(1970, 6, 9, StrictChronology.getInstance(TestLocalDate_Basics.COPTIC_PARIS));
        LocalDate test2 = new LocalDate(1970, 6, 9, StrictChronology.getInstance(TestLocalDate_Basics.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
    }

    public void testEqualsHashCodeAPI() {
        LocalDate test = new LocalDate(1970, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        int expected = 157;
        expected = (23 * expected) + 1970;
        expected = (23 * expected) + (TestLocalDate_Basics.COPTIC_UTC.year().getType().hashCode());
        expected = (23 * expected) + 6;
        expected = (23 * expected) + (TestLocalDate_Basics.COPTIC_UTC.monthOfYear().getType().hashCode());
        expected = (23 * expected) + 9;
        expected = (23 * expected) + (TestLocalDate_Basics.COPTIC_UTC.dayOfMonth().getType().hashCode());
        expected += TestLocalDate_Basics.COPTIC_UTC.hashCode();
        TestCase.assertEquals(expected, test.hashCode());
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_LocalDate() {
        LocalDate test1 = new LocalDate(2005, 6, 2);
        LocalDate test1a = new LocalDate(2005, 6, 2);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        LocalDate test2 = new LocalDate(2005, 7, 2);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        LocalDate test3 = new LocalDate(2005, 7, 2, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new LocalDate(2005, 7, 2).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_LocalDate() {
        LocalDate test1 = new LocalDate(2005, 6, 2);
        LocalDate test1a = new LocalDate(2005, 6, 2);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        LocalDate test2 = new LocalDate(2005, 7, 2);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        LocalDate test3 = new LocalDate(2005, 7, 2, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new LocalDate(2005, 7, 2).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_LocalDate() {
        LocalDate test1 = new LocalDate(2005, 6, 2);
        LocalDate test1a = new LocalDate(2005, 6, 2);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        LocalDate test2 = new LocalDate(2005, 7, 2);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        LocalDate test3 = new LocalDate(2005, 7, 2, GregorianChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new LocalDate(2005, 7, 2).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithField_DateTimeFieldType_int_1() {
        LocalDate test = new LocalDate(2004, 6, 9);
        LocalDate result = test.withField(DateTimeFieldType.year(), 2006);
        TestCase.assertEquals(new LocalDate(2004, 6, 9), test);
        TestCase.assertEquals(new LocalDate(2006, 6, 9), result);
    }

    public void testWithField_DateTimeFieldType_int_2() {
        LocalDate test = new LocalDate(2004, 6, 9);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_DateTimeFieldType_int_3() {
        LocalDate test = new LocalDate(2004, 6, 9);
        try {
            test.withField(DateTimeFieldType.hourOfDay(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_DateTimeFieldType_int_4() {
        LocalDate test = new LocalDate(2004, 6, 9);
        LocalDate result = test.withField(DateTimeFieldType.year(), 2004);
        TestCase.assertEquals(new LocalDate(2004, 6, 9), test);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded_DurationFieldType_int_1() {
        LocalDate test = new LocalDate(2004, 6, 9);
        LocalDate result = test.withFieldAdded(DurationFieldType.years(), 6);
        TestCase.assertEquals(new LocalDate(2004, 6, 9), test);
        TestCase.assertEquals(new LocalDate(2010, 6, 9), result);
    }

    public void testWithFieldAdded_DurationFieldType_int_2() {
        LocalDate test = new LocalDate(2004, 6, 9);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_3() {
        LocalDate test = new LocalDate(2004, 6, 9);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_4() {
        LocalDate test = new LocalDate(2004, 6, 9);
        LocalDate result = test.withFieldAdded(DurationFieldType.years(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded_DurationFieldType_int_5() {
        LocalDate test = new LocalDate(2004, 6, 9);
        try {
            test.withFieldAdded(DurationFieldType.hours(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.plus(new Period(1, 2, 3, 4, 29, 6, 7, 8));
        LocalDate expected = new LocalDate(2003, 7, 28, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusYears_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.plusYears(1);
        LocalDate expected = new LocalDate(2003, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.plusMonths(1);
        LocalDate expected = new LocalDate(2002, 6, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusWeeks_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.plusWeeks(1);
        LocalDate expected = new LocalDate(2002, 5, 10, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusDays_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.plusDays(1);
        LocalDate expected = new LocalDate(2002, 5, 4, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        // TODO breaks because it subtracts millis now, and thus goes
        // into the previous day
        LocalDate expected = new LocalDate(2001, 3, 26, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusYears_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.minusYears(1);
        LocalDate expected = new LocalDate(2001, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.minusMonths(1);
        LocalDate expected = new LocalDate(2002, 4, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusWeeks_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.minusWeeks(1);
        LocalDate expected = new LocalDate(2002, 4, 26, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusDays_int() {
        LocalDate test = new LocalDate(2002, 5, 3, TestLocalDate_Basics.BUDDHIST_LONDON);
        LocalDate result = test.minusDays(1);
        LocalDate expected = new LocalDate(2002, 5, 2, TestLocalDate_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusDays(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testGetters() {
        LocalDate test = new LocalDate(1970, 6, 9, TestLocalDate_Basics.GJ_UTC);
        TestCase.assertEquals(1970, test.getYear());
        TestCase.assertEquals(6, test.getMonthOfYear());
        TestCase.assertEquals(9, test.getDayOfMonth());
        TestCase.assertEquals(160, test.getDayOfYear());
        TestCase.assertEquals(2, test.getDayOfWeek());
        TestCase.assertEquals(24, test.getWeekOfWeekyear());
        TestCase.assertEquals(1970, test.getWeekyear());
        TestCase.assertEquals(70, test.getYearOfCentury());
        TestCase.assertEquals(20, test.getCenturyOfEra());
        TestCase.assertEquals(1970, test.getYearOfEra());
        TestCase.assertEquals(AD, test.getEra());
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        LocalDate test = new LocalDate(1970, 6, 9, TestLocalDate_Basics.GJ_UTC);
        check(test.withYear(2000), 2000, 6, 9);
        check(test.withMonthOfYear(2), 1970, 2, 9);
        check(test.withDayOfMonth(2), 1970, 6, 2);
        check(test.withDayOfYear(6), 1970, 1, 6);
        check(test.withDayOfWeek(6), 1970, 6, 13);
        check(test.withWeekOfWeekyear(6), 1970, 2, 3);
        check(test.withWeekyear(1971), 1971, 6, 15);
        check(test.withYearOfCentury(60), 1960, 6, 9);
        check(test.withCenturyOfEra(21), 2070, 6, 9);
        check(test.withYearOfEra(1066), 1066, 6, 9);
        check(test.withEra(BC), (-1970), 6, 9);
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
    public void testToDateTimeAtStartOfDay() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        DateTime test = base.toDateTimeAtStartOfDay();
        check(base, 2005, 6, 9);
        TestCase.assertEquals(new DateTime(2005, 6, 9, 0, 0, 0, 0, TestLocalDate_Basics.COPTIC_LONDON), test);
    }

    public void testToDateTimeAtStartOfDay_avoidDST() {
        LocalDate base = new LocalDate(2007, 4, 1);
        DateTimeZone.setDefault(TestLocalDate_Basics.MOCK_GAZA);
        DateTime test = base.toDateTimeAtStartOfDay();
        check(base, 2007, 4, 1);
        TestCase.assertEquals(new DateTime(2007, 4, 1, 1, 0, 0, 0, TestLocalDate_Basics.MOCK_GAZA), test);
    }

    public void testToDateTimeAtStartOfDay_handleMidnightDST() {
        LocalDate test = new LocalDate(2018, 10, 28);
        DateTime result = test.toDateTimeAtStartOfDay(DateTimeZone.forID("Atlantic/Azores"));
        DateTime expected = new DateTime(2018, 10, 28, 0, 0, DateTimeZone.forID("Atlantic/Azores"));
        TestCase.assertEquals(expected, result);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeAtStartOfDay_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        DateTime test = base.toDateTimeAtStartOfDay(TestLocalDate_Basics.TOKYO);
        check(base, 2005, 6, 9);
        TestCase.assertEquals(new DateTime(2005, 6, 9, 0, 0, 0, 0, TestLocalDate_Basics.COPTIC_TOKYO), test);
    }

    public void testToDateTimeAtStartOfDay_Zone_avoidDST() {
        LocalDate base = new LocalDate(2007, 4, 1);
        DateTime test = base.toDateTimeAtStartOfDay(TestLocalDate_Basics.MOCK_GAZA);
        check(base, 2007, 4, 1);
        TestCase.assertEquals(new DateTime(2007, 4, 1, 1, 0, 0, 0, TestLocalDate_Basics.MOCK_GAZA), test);
    }

    public void testToDateTimeAtStartOfDay_nullZone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        DateTime test = base.toDateTimeAtStartOfDay(((DateTimeZone) (null)));
        check(base, 2005, 6, 9);
        TestCase.assertEquals(new DateTime(2005, 6, 9, 0, 0, 0, 0, TestLocalDate_Basics.COPTIC_LONDON), test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeAtCurrentTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeAtCurrentTime();
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalDate_Basics.COPTIC_LONDON);
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(9);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTimeAtCurrentTime_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeAtCurrentTime(TestLocalDate_Basics.TOKYO);
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalDate_Basics.COPTIC_TOKYO);
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(9);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTimeAtCurrentTime_nullZone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime dt = new DateTime(2004, 6, 9, 6, 7, 8, 9);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTimeAtCurrentTime(((DateTimeZone) (null)));
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(dt.getMillis(), TestLocalDate_Basics.COPTIC_LONDON);
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(9);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToLocalDateTime_LocalTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        LocalDateTime test = base.toLocalDateTime(tod);
        check(base, 2005, 6, 9);
        LocalDateTime expected = new LocalDateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_UTC);
        TestCase.assertEquals(expected, test);
    }

    public void testToLocalDateTime_nullLocalTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        try {
            base.toLocalDateTime(((LocalTime) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testToLocalDateTime_wrongChronologyLocalTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.BUDDHIST_PARIS);// PARIS irrelevant

        try {
            base.toLocalDateTime(tod);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_LocalTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        DateTime test = base.toDateTime(tod);
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullLocalTime() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        long now = getMillis();
        DateTimeUtils.setCurrentMillisFixed(now);
        DateTime test = base.toDateTime(((LocalTime) (null)));
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_LocalTime_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        DateTime test = base.toDateTime(tod, TestLocalDate_Basics.TOKYO);
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_LocalTime_nullZone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        DateTime test = base.toDateTime(tod, null);
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullLocalTime_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        long now = getMillis();
        DateTimeUtils.setCurrentMillisFixed(now);
        DateTime test = base.toDateTime(((LocalTime) (null)), TestLocalDate_Basics.TOKYO);
        check(base, 2005, 6, 9);
        DateTime expected = new DateTime(2005, 6, 9, 12, 13, 14, 15, TestLocalDate_Basics.COPTIC_TOKYO);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_LocalTime_Zone_dstGap() {
        LocalDate base = new LocalDate(2014, 3, 30, TestLocalDate_Basics.ISO_LONDON);
        LocalTime tod = new LocalTime(1, 30, 0, 0, TestLocalDate_Basics.ISO_LONDON);
        try {
            base.toDateTime(tod, TestLocalDate_Basics.LONDON);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
        }
    }

    public void testToDateTime_LocalTime_Zone_dstOverlap() {
        LocalDate base = new LocalDate(2014, 10, 26, TestLocalDate_Basics.ISO_LONDON);
        LocalTime tod = new LocalTime(1, 30, 0, 0, TestLocalDate_Basics.ISO_LONDON);
        DateTime test = base.toDateTime(tod, TestLocalDate_Basics.LONDON);
        DateTime expected = withEarlierOffsetAtOverlap();
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_LocalTime_Zone_dstOverlap_NewYork() {
        LocalDate base = new LocalDate(2007, 11, 4, TestLocalDate_Basics.ISO_NEW_YORK);
        LocalTime tod = new LocalTime(1, 30, 0, 0, TestLocalDate_Basics.ISO_NEW_YORK);
        DateTime test = base.toDateTime(tod, TestLocalDate_Basics.NEW_YORK);
        DateTime expected = withEarlierOffsetAtOverlap();
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_wrongChronoLocalTime_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime tod = new LocalTime(12, 13, 14, 15, TestLocalDate_Basics.BUDDHIST_TOKYO);
        try {
            base.toDateTime(tod, TestLocalDate_Basics.LONDON);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTime test = base.toDateTime(dt);
        check(base, 2005, 6, 9);
        DateTime expected = dt;
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(9);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullRI() {
        LocalDate base = new LocalDate(2005, 6, 9);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7);
        DateTimeUtils.setCurrentMillisFixed(dt.getMillis());
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 2005, 6, 9);
        DateTime expected = dt;
        expected = expected.year().setCopy(2005);
        expected = expected.monthOfYear().setCopy(6);
        expected = expected.dayOfMonth().setCopy(9);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToInterval() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval();
        check(base, 2005, 6, 9);
        DateTime start = base.toDateTimeAtStartOfDay();
        DateTime end = start.plus(Period.days(1));
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToInterval_Zone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval(TestLocalDate_Basics.TOKYO);
        check(base, 2005, 6, 9);
        DateTime start = base.toDateTimeAtStartOfDay(TestLocalDate_Basics.TOKYO);
        DateTime end = start.plus(Period.days(1));
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    public void testToInterval_Zone_noMidnight() {
        LocalDate base = new LocalDate(2006, 4, 1, TestLocalDate_Basics.ISO_LONDON);// LONDON irrelevant

        DateTimeZone gaza = DateTimeZone.forID("Asia/Gaza");
        Interval test = base.toInterval(gaza);
        check(base, 2006, 4, 1);
        DateTime start = new DateTime(2006, 4, 1, 1, 0, 0, 0, gaza);
        DateTime end = new DateTime(2006, 4, 2, 0, 0, 0, 0, gaza);
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    public void testToInterval_nullZone() {
        LocalDate base = new LocalDate(2005, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);// PARIS irrelevant

        Interval test = base.toInterval(null);
        check(base, 2005, 6, 9);
        DateTime start = base.toDateTimeAtStartOfDay(TestLocalDate_Basics.LONDON);
        DateTime end = start.plus(Period.days(1));
        Interval expected = new Interval(start, end);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDate_summer() {
        LocalDate base = new LocalDate(2005, 7, 9, TestLocalDate_Basics.COPTIC_PARIS);
        Date test = base.toDate();
        check(base, 2005, 7, 9);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JULY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_winter() {
        LocalDate base = new LocalDate(2005, 1, 9, TestLocalDate_Basics.COPTIC_PARIS);
        Date test = base.toDate();
        check(base, 2005, 1, 9);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JANUARY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_springDST() {
        LocalDate base = new LocalDate(2007, 4, 2);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 4, 2);
            TestCase.assertEquals("Mon Apr 02 01:00:00 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_springDST_2Hour40Savings() {
        LocalDate base = new LocalDate(2007, 4, 2);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000, ((3600000 / 6) * 16));
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 4, 2);
            TestCase.assertEquals("Mon Apr 02 02:40:00 GMT+03:40 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_autumnDST() {
        LocalDate base = new LocalDate(2007, 10, 2);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 10, 2);
            TestCase.assertEquals("Tue Oct 02 00:00:00 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        LocalDate test = new LocalDate(2005, 6, 9, TestLocalDate_Basics.GJ_UTC);
        TestCase.assertEquals(test.year(), test.property(DateTimeFieldType.year()));
        TestCase.assertEquals(test.monthOfYear(), test.property(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(test.dayOfMonth(), test.property(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(test.dayOfWeek(), test.property(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(test.dayOfYear(), test.property(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(test.weekOfWeekyear(), test.property(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(test.weekyear(), test.property(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(test.yearOfCentury(), test.property(DateTimeFieldType.yearOfCentury()));
        TestCase.assertEquals(test.yearOfEra(), test.property(DateTimeFieldType.yearOfEra()));
        TestCase.assertEquals(test.centuryOfEra(), test.property(DateTimeFieldType.centuryOfEra()));
        TestCase.assertEquals(test.era(), test.property(DateTimeFieldType.era()));
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
        LocalDate test = new LocalDate(1972, 6, 9, TestLocalDate_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        LocalDate result = ((LocalDate) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        LocalDate test = new LocalDate(2002, 6, 9);
        TestCase.assertEquals("2002-06-09", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        LocalDate test = new LocalDate(2002, 6, 9);
        TestCase.assertEquals("2002 \ufffd\ufffd", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06-09", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        LocalDate test = new LocalDate(1970, 6, 9);
        TestCase.assertEquals("Tue 9/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("mar. 9/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("1970-06-09", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("Tue 9/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("1970-06-09", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        LocalDate test = new LocalDate(2002, 6, 9);
        TestCase.assertEquals("2002 \ufffd\ufffd", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("2002-06-09", test.toString(((DateTimeFormatter) (null))));
    }
}

