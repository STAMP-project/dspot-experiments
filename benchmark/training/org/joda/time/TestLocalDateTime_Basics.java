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
import DateTimeConstants.AM;
import DateTimeConstants.BC;
import DateTimeConstants.PM;
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
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for LocalDate.
 *
 * @author Stephen Colebourne
 */
public class TestLocalDateTime_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEW_YORK = DateTimeZone.forID("America/New_York");

    private static final GJChronology GJ_UTC = GJChronology.getInstanceUTC();

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestLocalDateTime_Basics.PARIS);

    private static final Chronology COPTIC_LONDON = CopticChronology.getInstance(TestLocalDateTime_Basics.LONDON);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestLocalDateTime_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology ISO_LONDON = ISOChronology.getInstance(TestLocalDateTime_Basics.LONDON);

    private static final Chronology ISO_NEW_YORK = ISOChronology.getInstance(TestLocalDateTime_Basics.NEW_YORK);

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(TestLocalDateTime_Basics.LONDON);

    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestLocalDateTime_Basics.TOKYO);

    // private long TEST_TIME1 =
    // (31L + 28L + 31L + 6L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 12L * DateTimeConstants.MILLIS_PER_HOUR
    // + 24L * DateTimeConstants.MILLIS_PER_MINUTE;
    // 
    // private long TEST_TIME2 =
    // (365L + 31L + 28L + 31L + 30L + 7L -1L) * DateTimeConstants.MILLIS_PER_DAY
    // + 14L * DateTimeConstants.MILLIS_PER_HOUR
    // + 28L * DateTimeConstants.MILLIS_PER_MINUTE;
    private int MILLIS_OF_DAY_UTC = ((int) ((((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L));

    private long TEST_TIME_NOW_UTC = (((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY)) + (MILLIS_OF_DAY_UTC);

    private DateTimeZone zone = null;

    private Locale systemDefaultLocale = null;

    public TestLocalDateTime_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet_DateTimeFieldType() {
        LocalDateTime test = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals(1970, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, test.get(DateTimeFieldType.monthOfYear()));
        TestCase.assertEquals(9, test.get(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.dayOfWeek()));
        TestCase.assertEquals(160, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(24, test.get(DateTimeFieldType.weekOfWeekyear()));
        TestCase.assertEquals(1970, test.get(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(10, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(30, test.get(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(40, test.get(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(((MILLIS_OF_DAY_UTC) / 60000), test.get(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(((MILLIS_OF_DAY_UTC) / 1000), test.get(DateTimeFieldType.secondOfDay()));
        TestCase.assertEquals(MILLIS_OF_DAY_UTC, test.get(DateTimeFieldType.millisOfDay()));
        TestCase.assertEquals(10, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(AM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalDateTime(1970, 6, 9, 12, 30);
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(PM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalDateTime(1970, 6, 9, 14, 30);
        TestCase.assertEquals(2, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(2, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(14, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(PM, test.get(DateTimeFieldType.halfdayOfDay()));
        test = new LocalDateTime(1970, 6, 9, 0, 30);
        TestCase.assertEquals(0, test.get(DateTimeFieldType.hourOfHalfday()));
        TestCase.assertEquals(12, test.get(DateTimeFieldType.clockhourOfHalfday()));
        TestCase.assertEquals(24, test.get(DateTimeFieldType.clockhourOfDay()));
        TestCase.assertEquals(AM, test.get(DateTimeFieldType.halfdayOfDay()));
        try {
            test.get(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSize() {
        LocalDateTime test = new LocalDateTime();
        TestCase.assertEquals(4, test.size());
    }

    public void testGetFieldType_int() {
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.COPTIC_PARIS);
        TestCase.assertSame(DateTimeFieldType.year(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), test.getFieldType(1));
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), test.getFieldType(2));
        TestCase.assertSame(DateTimeFieldType.millisOfDay(), test.getFieldType(3));
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
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.COPTIC_PARIS);
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertSame(DateTimeFieldType.year(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.monthOfYear(), fields[1]);
        TestCase.assertSame(DateTimeFieldType.dayOfMonth(), fields[2]);
        TestCase.assertSame(DateTimeFieldType.millisOfDay(), fields[3]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField_int() {
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.COPTIC_PARIS);
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.year(), test.getField(0));
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.monthOfYear(), test.getField(1));
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.dayOfMonth(), test.getField(2));
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.millisOfDay(), test.getField(3));
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
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.year(), fields[0]);
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.monthOfYear(), fields[1]);
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.dayOfMonth(), fields[2]);
        TestCase.assertSame(TestLocalDateTime_Basics.COPTIC_UTC.millisOfDay(), fields[3]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue_int() {
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.ISO_UTC);
        TestCase.assertEquals(1970, test.getValue(0));
        TestCase.assertEquals(6, test.getValue(1));
        TestCase.assertEquals(9, test.getValue(2));
        TestCase.assertEquals(MILLIS_OF_DAY_UTC, test.getValue(3));
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
        LocalDateTime test = new LocalDateTime(TestLocalDateTime_Basics.ISO_UTC);
        int[] values = test.getValues();
        TestCase.assertEquals(1970, values[0]);
        TestCase.assertEquals(6, values[1]);
        TestCase.assertEquals(9, values[2]);
        TestCase.assertEquals(MILLIS_OF_DAY_UTC, values[3]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported_DateTimeFieldType() {
        LocalDateTime test = new LocalDateTime();
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
        TestCase.assertEquals(false, test.isSupported(((DateTimeFieldType) (null))));
    }

    public void testIsSupported_DurationFieldType() {
        LocalDateTime test = new LocalDateTime();
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.eras()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.centuries()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.years()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.months()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.weekyears()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.weeks()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.days()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.hours()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.minutes()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.seconds()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.millis()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.halfdays()));
        TestCase.assertEquals(false, test.isSupported(((DurationFieldType) (null))));
    }

    public void testEqualsHashCode() {
        LocalDateTime test1 = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        LocalDateTime test2 = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        LocalDateTime test3 = new LocalDateTime(1971, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestLocalDateTime_Basics.MockInstant()));
        Partial partial = new Partial(new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.millisOfDay() }, new int[]{ 1970, 6, 9, MILLIS_OF_DAY_UTC }, TestLocalDateTime_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test1.equals(partial));
        TestCase.assertEquals(true, ((test1.hashCode()) == (partial.hashCode())));
        TestCase.assertEquals(false, test1.equals(MockPartial.EMPTY_INSTANCE));
    }

    class MockInstant extends MockPartial {
        public Chronology getChronology() {
            return TestLocalDateTime_Basics.COPTIC_UTC;
        }

        public DateTimeField[] getFields() {
            return new DateTimeField[]{ TestLocalDateTime_Basics.COPTIC_UTC.year(), TestLocalDateTime_Basics.COPTIC_UTC.monthOfYear(), TestLocalDateTime_Basics.COPTIC_UTC.dayOfMonth(), TestLocalDateTime_Basics.COPTIC_UTC.millisOfDay() };
        }

        public int[] getValues() {
            return new int[]{ 1970, 6, 9, MILLIS_OF_DAY_UTC };
        }
    }

    // -----------------------------------------------------------------------
    public void testCompareTo() {
        LocalDateTime test1 = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        LocalDateTime test1a = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        TestCase.assertEquals(0, test1.compareTo(test1a));
        TestCase.assertEquals(0, test1a.compareTo(test1));
        TestCase.assertEquals(0, test1.compareTo(test1));
        TestCase.assertEquals(0, test1a.compareTo(test1a));
        LocalDateTime test2 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40);
        TestCase.assertEquals((-1), test1.compareTo(test2));
        TestCase.assertEquals((+1), test2.compareTo(test1));
        LocalDateTime test3 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40, TestLocalDateTime_Basics.GREGORIAN_UTC);
        TestCase.assertEquals((-1), test1.compareTo(test3));
        TestCase.assertEquals((+1), test3.compareTo(test1));
        TestCase.assertEquals(0, test3.compareTo(test2));
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.monthOfYear(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.millisOfDay() };
        int[] values = new int[]{ 2005, 6, 2, MILLIS_OF_DAY_UTC };
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
        try {
            @SuppressWarnings("deprecation")
            YearMonthDay ymd = new YearMonthDay();
            test1.compareTo(ymd);
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
        try {
            @SuppressWarnings("deprecation")
            TimeOfDay tod = new TimeOfDay();
            test1.compareTo(tod);
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
        Partial partial = new Partial().with(DateTimeFieldType.centuryOfEra(), 1).with(DateTimeFieldType.halfdayOfDay(), 0).with(DateTimeFieldType.dayOfMonth(), 9);
        try {
            new LocalDateTime(1970, 6, 9, 10, 20, 30, 40).compareTo(partial);
            TestCase.fail();
        } catch (ClassCastException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_LocalDateTime() {
        LocalDateTime test1 = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        LocalDateTime test1a = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        LocalDateTime test2 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        LocalDateTime test3 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40, TestLocalDateTime_Basics.GREGORIAN_UTC);
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            new LocalDateTime(2005, 7, 2, 10, 20, 30, 40).isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_LocalDateTime() {
        LocalDateTime test1 = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        LocalDateTime test1a = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        LocalDateTime test2 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        LocalDateTime test3 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40, TestLocalDateTime_Basics.GREGORIAN_UTC);
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            new LocalDateTime(2005, 7, 2, 10, 20, 30, 40).isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_LocalDateTime() {
        LocalDateTime test1 = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        LocalDateTime test1a = new LocalDateTime(2005, 6, 2, 10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        LocalDateTime test2 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        LocalDateTime test3 = new LocalDateTime(2005, 7, 2, 10, 20, 30, 40, TestLocalDateTime_Basics.GREGORIAN_UTC);
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            new LocalDateTime(2005, 7, 2, 10, 20, 30, 40).isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithDate() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withDate(2006, 2, 1);
        check(test, 2004, 6, 9, 10, 20, 30, 40);
        check(result, 2006, 2, 1, 10, 20, 30, 40);
    }

    // -----------------------------------------------------------------------
    public void testWithTime() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withTime(9, 8, 7, 6);
        check(test, 2004, 6, 9, 10, 20, 30, 40);
        check(result, 2004, 6, 9, 9, 8, 7, 6);
    }

    // -----------------------------------------------------------------------
    public void testWithField_DateTimeFieldType_int_1() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withField(DateTimeFieldType.year(), 2006);
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30, 40), test);
        TestCase.assertEquals(new LocalDateTime(2006, 6, 9, 10, 20, 30, 40), result);
    }

    public void testWithField_DateTimeFieldType_int_2() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithField_DateTimeFieldType_int_3() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withField(DateTimeFieldType.year(), 2004);
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30, 40), test);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded_DurationFieldType_int_1() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withFieldAdded(DurationFieldType.years(), 6);
        TestCase.assertEquals(new LocalDateTime(2004, 6, 9, 10, 20, 30, 40), test);
        TestCase.assertEquals(new LocalDateTime(2010, 6, 9, 10, 20, 30, 40), result);
    }

    public void testWithFieldAdded_DurationFieldType_int_2() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_3() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithFieldAdded_DurationFieldType_int_4() {
        LocalDateTime test = new LocalDateTime(2004, 6, 9, 10, 20, 30, 40);
        LocalDateTime result = test.withFieldAdded(DurationFieldType.years(), 0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plus(new Period(1, 2, 3, 4, 29, 6, 7, 8));
        LocalDateTime expected = new LocalDateTime(2003, 7, 29, 15, 26, 37, 48, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testPlusYears_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusYears(1);
        LocalDateTime expected = new LocalDateTime(2003, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMonths_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusMonths(1);
        LocalDateTime expected = new LocalDateTime(2002, 6, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusWeeks_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusWeeks(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 10, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusDays_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusDays(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 4, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusDays(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusHours_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusHours(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 11, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMinutes_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusMinutes(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 21, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusSeconds_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusSeconds(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 20, 31, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testPlusMillis_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.plusMillis(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 20, 30, 41, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.plusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        LocalDateTime expected = new LocalDateTime(2001, 3, 26, 9, 19, 29, 39, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    public void testMinusYears_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusYears(1);
        LocalDateTime expected = new LocalDateTime(2001, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusYears(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMonths_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusMonths(1);
        LocalDateTime expected = new LocalDateTime(2002, 4, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMonths(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusWeeks_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusWeeks(1);
        LocalDateTime expected = new LocalDateTime(2002, 4, 26, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusWeeks(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusDays_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusDays(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 2, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusDays(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusHours_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusHours(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 9, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusHours(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMinutes_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusMinutes(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 19, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMinutes(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusSeconds_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusSeconds(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 20, 29, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusSeconds(0);
        TestCase.assertSame(test, result);
    }

    public void testMinusMillis_int() {
        LocalDateTime test = new LocalDateTime(2002, 5, 3, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        LocalDateTime result = test.minusMillis(1);
        LocalDateTime expected = new LocalDateTime(2002, 5, 3, 10, 20, 30, 39, TestLocalDateTime_Basics.BUDDHIST_LONDON);
        TestCase.assertEquals(expected, result);
        result = test.minusMillis(0);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testGetters() {
        LocalDateTime test = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.GJ_UTC);
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
        TestCase.assertEquals(10, test.getHourOfDay());
        TestCase.assertEquals(20, test.getMinuteOfHour());
        TestCase.assertEquals(30, test.getSecondOfMinute());
        TestCase.assertEquals(40, test.getMillisOfSecond());
        TestCase.assertEquals(MILLIS_OF_DAY_UTC, test.getMillisOfDay());
    }

    // -----------------------------------------------------------------------
    public void testWithers() {
        LocalDateTime test = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.GJ_UTC);
        check(test.withYear(2000), 2000, 6, 9, 10, 20, 30, 40);
        check(test.withMonthOfYear(2), 1970, 2, 9, 10, 20, 30, 40);
        check(test.withDayOfMonth(2), 1970, 6, 2, 10, 20, 30, 40);
        check(test.withDayOfYear(6), 1970, 1, 6, 10, 20, 30, 40);
        check(test.withDayOfWeek(6), 1970, 6, 13, 10, 20, 30, 40);
        check(test.withWeekOfWeekyear(6), 1970, 2, 3, 10, 20, 30, 40);
        check(test.withWeekyear(1971), 1971, 6, 15, 10, 20, 30, 40);
        check(test.withYearOfCentury(60), 1960, 6, 9, 10, 20, 30, 40);
        check(test.withCenturyOfEra(21), 2070, 6, 9, 10, 20, 30, 40);
        check(test.withYearOfEra(1066), 1066, 6, 9, 10, 20, 30, 40);
        check(test.withEra(BC), (-1970), 6, 9, 10, 20, 30, 40);
        check(test.withHourOfDay(6), 1970, 6, 9, 6, 20, 30, 40);
        check(test.withMinuteOfHour(6), 1970, 6, 9, 10, 6, 30, 40);
        check(test.withSecondOfMinute(6), 1970, 6, 9, 10, 20, 6, 40);
        check(test.withMillisOfSecond(6), 1970, 6, 9, 10, 20, 30, 6);
        check(test.withMillisOfDay(61234), 1970, 6, 9, 0, 1, 1, 234);
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
    public void testToDateTime() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime test = base.toDateTime();
        check(base, 2005, 6, 9, 6, 7, 8, 9);
        DateTime expected = new DateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_Zone() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime test = base.toDateTime(TestLocalDateTime_Basics.TOKYO);
        check(base, 2005, 6, 9, 6, 7, 8, 9);
        DateTime expected = new DateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_TOKYO);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullZone() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        DateTime test = base.toDateTime(((DateTimeZone) (null)));
        check(base, 2005, 6, 9, 6, 7, 8, 9);
        DateTime expected = new DateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_Zone_dstGap() {
        LocalDateTime base = new LocalDateTime(2014, 3, 30, 1, 30, 0, 0, TestLocalDateTime_Basics.ISO_LONDON);
        try {
            base.toDateTime(TestLocalDateTime_Basics.LONDON);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
        }
    }

    public void testToDateTime_Zone_dstOverlap() {
        LocalDateTime base = new LocalDateTime(2014, 10, 26, 1, 30, 0, 0, TestLocalDateTime_Basics.ISO_LONDON);
        DateTime test = base.toDateTime(TestLocalDateTime_Basics.LONDON);
        DateTime expected = withEarlierOffsetAtOverlap();
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_Zone_dstOverlap_NewYork() {
        LocalDateTime base = new LocalDateTime(2007, 11, 4, 1, 30, 0, 0, TestLocalDateTime_Basics.ISO_NEW_YORK);
        DateTime test = base.toDateTime(TestLocalDateTime_Basics.NEW_YORK);
        DateTime expected = withEarlierOffsetAtOverlap();
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToLocalDate() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalDate expected = new LocalDate(2005, 6, 9, TestLocalDateTime_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, base.toLocalDate());
    }

    public void testToLocalTime() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_PARIS);// PARIS irrelevant

        LocalTime expected = new LocalTime(6, 7, 8, 9, TestLocalDateTime_Basics.COPTIC_LONDON);
        TestCase.assertEquals(expected, base.toLocalTime());
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(2002, 1, 3, 4, 5, 6, 7, TestLocalDateTime_Basics.BUDDHIST_TOKYO);
        DateTime test = base.toDateTime(dt);
        check(base, 2005, 6, 9, 10, 20, 30, 40);
        DateTime expected = new DateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.BUDDHIST_TOKYO);
        TestCase.assertEquals(expected, test);
    }

    public void testToDateTime_nullRI() {
        LocalDateTime base = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 2005, 6, 9, 10, 20, 30, 40);
        DateTime expected = new DateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.ISO_LONDON);
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToDate_summer() {
        LocalDateTime base = new LocalDateTime(2005, 7, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        Date test = base.toDate();
        check(base, 2005, 7, 9, 10, 20, 30, 40);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JULY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        gcal.set(Calendar.HOUR_OF_DAY, 10);
        gcal.set(Calendar.MINUTE, 20);
        gcal.set(Calendar.SECOND, 30);
        gcal.set(Calendar.MILLISECOND, 40);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_winter() {
        LocalDateTime base = new LocalDateTime(2005, 1, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        Date test = base.toDate();
        check(base, 2005, 1, 9, 10, 20, 30, 40);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JANUARY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        gcal.set(Calendar.HOUR_OF_DAY, 10);
        gcal.set(Calendar.MINUTE, 20);
        gcal.set(Calendar.SECOND, 30);
        gcal.set(Calendar.MILLISECOND, 40);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_springDST() {
        LocalDateTime base = new LocalDateTime(2007, 4, 2, 0, 20, 0, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 4, 2, 0, 20, 0, 0);
            TestCase.assertEquals("Mon Apr 02 01:00:00 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_springDST_2Hour40Savings() {
        LocalDateTime base = new LocalDateTime(2007, 4, 2, 0, 20, 0, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000, ((3600000 / 6) * 16));
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 4, 2, 0, 20, 0, 0);
            TestCase.assertEquals("Mon Apr 02 02:40:00 GMT+03:40 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_autumnDST() {
        LocalDateTime base = new LocalDateTime(2007, 10, 2, 0, 20, 30, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate();
            check(base, 2007, 10, 2, 0, 20, 30, 0);
            TestCase.assertEquals("Tue Oct 02 00:20:30 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    // -----------------------------------------------------------------------
    public void testToDate_summer_Zone() {
        LocalDateTime base = new LocalDateTime(2005, 7, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        Date test = base.toDate(TimeZone.getDefault());
        check(base, 2005, 7, 9, 10, 20, 30, 40);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JULY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        gcal.set(Calendar.HOUR_OF_DAY, 10);
        gcal.set(Calendar.MINUTE, 20);
        gcal.set(Calendar.SECOND, 30);
        gcal.set(Calendar.MILLISECOND, 40);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_winter_Zone() {
        LocalDateTime base = new LocalDateTime(2005, 1, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        Date test = base.toDate(TimeZone.getDefault());
        check(base, 2005, 1, 9, 10, 20, 30, 40);
        GregorianCalendar gcal = new GregorianCalendar();
        gcal.clear();
        gcal.set(Calendar.YEAR, 2005);
        gcal.set(Calendar.MONTH, Calendar.JANUARY);
        gcal.set(Calendar.DAY_OF_MONTH, 9);
        gcal.set(Calendar.HOUR_OF_DAY, 10);
        gcal.set(Calendar.MINUTE, 20);
        gcal.set(Calendar.SECOND, 30);
        gcal.set(Calendar.MILLISECOND, 40);
        TestCase.assertEquals(gcal.getTime(), test);
    }

    public void testToDate_springDST_Zone() {
        LocalDateTime base = new LocalDateTime(2007, 4, 2, 0, 20, 0, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate(TimeZone.getDefault());
            check(base, 2007, 4, 2, 0, 20, 0, 0);
            TestCase.assertEquals("Mon Apr 02 01:00:00 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_springDST_2Hour40Savings_Zone() {
        LocalDateTime base = new LocalDateTime(2007, 4, 2, 0, 20, 0, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000, ((3600000 / 6) * 16));
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate(TimeZone.getDefault());
            check(base, 2007, 4, 2, 0, 20, 0, 0);
            TestCase.assertEquals("Mon Apr 02 02:40:00 GMT+03:40 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    public void testToDate_autumnDST_Zone() {
        LocalDateTime base = new LocalDateTime(2007, 10, 2, 0, 20, 30, 0);
        SimpleTimeZone testZone = new SimpleTimeZone(3600000, "NoMidnight", Calendar.APRIL, 2, 0, 0, Calendar.OCTOBER, 2, 0, 3600000);
        TimeZone currentZone = TimeZone.getDefault();
        try {
            TimeZone.setDefault(testZone);
            Date test = base.toDate(TimeZone.getDefault());
            check(base, 2007, 10, 2, 0, 20, 30, 0);
            TestCase.assertEquals("Tue Oct 02 00:20:30 GMT+02:00 2007", test.toString());
        } finally {
            TimeZone.setDefault(currentZone);
        }
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        LocalDateTime test = new LocalDateTime(2005, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.GJ_UTC);
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
        TestCase.assertEquals(test.hourOfDay(), test.property(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(test.minuteOfHour(), test.property(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(test.secondOfMinute(), test.property(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(test.millisOfSecond(), test.property(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(test.millisOfDay(), test.property(DateTimeFieldType.millisOfDay()));
        try {
            test.property(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(test, test.property(DateTimeFieldType.minuteOfDay()).getLocalDateTime());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        LocalDateTime test = new LocalDateTime(1972, 6, 9, 10, 20, 30, 40, TestLocalDateTime_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        LocalDateTime result = ((LocalDateTime) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
        TestCase.assertTrue(result.isSupported(DateTimeFieldType.dayOfMonth()));// check deserialization

    }

    // -----------------------------------------------------------------------
    public void testToString() {
        LocalDateTime test = new LocalDateTime(2002, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals("2002-06-09T10:20:30.040", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        LocalDateTime test = new LocalDateTime(2002, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals("2002 10", test.toString("yyyy HH"));
        TestCase.assertEquals("2002-06-09T10:20:30.040", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        LocalDateTime test = new LocalDateTime(1970, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals("Tue 9/6", test.toString("EEE d/M", Locale.ENGLISH));
        TestCase.assertEquals("mar. 9/6", test.toString("EEE d/M", Locale.FRENCH));
        TestCase.assertEquals("1970-06-09T10:20:30.040", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("Tue 9/6", test.toString("EEE d/M", null));
        TestCase.assertEquals("1970-06-09T10:20:30.040", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        LocalDateTime test = new LocalDateTime(2002, 6, 9, 10, 20, 30, 40);
        TestCase.assertEquals("2002 10", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("2002-06-09T10:20:30.040", test.toString(((DateTimeFormatter) (null))));
    }
}

