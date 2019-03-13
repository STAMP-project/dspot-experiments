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


import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;
import static Period.ZERO;


/**
 * This class is a JUnit test for Duration.
 *
 * @author Stephen Colebourne
 */
public class TestPeriod_Constructors extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestPeriod_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() throws Throwable {
        Period test = ZERO;
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new Period(1, 2, 3, 4, 5, 6, 7, 890), Period.parse("P1Y2M3W4DT5H6M7.890S"));
    }

    // -----------------------------------------------------------------------
    public void testConstructor1() throws Throwable {
        Period test = new Period();
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long2() throws Throwable {
        long length = (((5 * (MILLIS_PER_HOUR)) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long3() throws Throwable {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        Period test = new Period(length);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        // only time fields are precise in AllType
        TestCase.assertEquals(0, test.getYears());// (4 + (3 * 7) + (2 * 30) + 365) == 450

        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((450 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_fixedZone() throws Throwable {
        DateTimeZone zone = DateTimeZone.getDefault();
        try {
            DateTimeZone.setDefault(DateTimeZone.forOffsetHours(2));
            long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
            Period test = new Period(length);
            TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
            // only time fields are precise in AllType
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
    public void testConstructor_long_PeriodType1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, ((PeriodType) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_PeriodType2() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.millis());
        TestCase.assertEquals(PeriodType.millis(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(length, test.getMillis());
    }

    public void testConstructor_long_PeriodType3() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.dayTime());
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_PeriodType4() throws Throwable {
        long length = (((5 * (MILLIS_PER_HOUR)) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(PeriodType.standard().withMillisRemoved(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_Chronology1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, ISOChronology.getInstance());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_Chronology2() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, ISOChronology.getInstanceUTC());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(4, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_Chronology3() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, ((Chronology) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_PeriodType_Chronology1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.time().withMillisRemoved(), ISOChronology.getInstance());
        TestCase.assertEquals(PeriodType.time().withMillisRemoved(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_long_PeriodType_Chronology2() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.standard(), ISOChronology.getInstanceUTC());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(4, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_PeriodType_Chronology3() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, PeriodType.standard(), ((Chronology) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_long_PeriodType_Chronology4() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        Period test = new Period(length, ((PeriodType) (null)), ((Chronology) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((4 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (4ints)
     */
    public void testConstructor_4int1() throws Throwable {
        Period test = new Period(5, 6, 7, 8);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (8ints)
     */
    public void testConstructor_8int1() throws Throwable {
        Period test = new Period(1, 2, 3, 4, 5, 6, 7, 8);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(2, test.getMonths());
        TestCase.assertEquals(3, test.getWeeks());
        TestCase.assertEquals(4, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (8ints)
     */
    public void testConstructor_8int__PeriodType1() throws Throwable {
        Period test = new Period(1, 2, 3, 4, 5, 6, 7, 8, null);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(2, test.getMonths());
        TestCase.assertEquals(3, test.getWeeks());
        TestCase.assertEquals(4, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_8int__PeriodType2() throws Throwable {
        Period test = new Period(0, 0, 0, 0, 5, 6, 7, 8, PeriodType.dayTime());
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_8int__PeriodType3() throws Throwable {
        try {
            new Period(1, 2, 3, 4, 5, 6, 7, 8, PeriodType.dayTime());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_long_long2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(1, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long_PeriodType1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_long_long_PeriodType2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2004, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), PeriodType.dayTime());
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(31, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_long_long_PeriodType3() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2004, 6, 9, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(PeriodType.standard().withMillisRemoved(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testToPeriod_PeriodType3() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10);
        DateTime dt2 = new DateTime(2005, 6, 9, 12, 14, 16, 18);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), PeriodType.yearWeekDayTime());
        TestCase.assertEquals(PeriodType.yearWeekDayTime(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());// tests using years and not weekyears

        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long_Chronology1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0, CopticChronology.getInstance());
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1, CopticChronology.getInstance());
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), CopticChronology.getInstance());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_long_long_Chronology2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), ((Chronology) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long_PeriodType_Chronology1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0, CopticChronology.getInstance());
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1, CopticChronology.getInstance());
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)), CopticChronology.getInstance());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_long_long_PeriodType_Chronology2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)), null);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RI_RI1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(1, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI3() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(3, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(1, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI4() throws Throwable {
        DateTime dt1 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        DateTime dt2 = null;// 2002-06-09T01:00+01:00

        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals((-3), test.getYears());
        TestCase.assertEquals((-1), test.getMonths());
        TestCase.assertEquals((-1), test.getWeeks());
        TestCase.assertEquals((-1), test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals((-1), test.getMinutes());
        TestCase.assertEquals((-1), test.getSeconds());
        TestCase.assertEquals((-1), test.getMillis());
    }

    public void testConstructor_RI_RI5() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = null;// 2002-06-09T01:00+01:00

        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RI_RI6() throws Throwable {
        DateTimeZone zone = TestPeriod_Constructors.PARIS;
        DateTime dt1 = withLaterOffsetAtOverlap();
        DateTime dt2 = withLaterOffsetAtOverlap();
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(15, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RI_RI7() throws Throwable {
        DateTimeZone zone = TestPeriod_Constructors.PARIS;
        DateTime dt1 = withEarlierOffsetAtOverlap();
        DateTime dt2 = withLaterOffsetAtOverlap();
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(15, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RI_RI_PeriodType1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2, null);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI_PeriodType2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2004, 7, 10, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2, PeriodType.dayTime());
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(31, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI_PeriodType3() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2004, 6, 9, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2, PeriodType.standard().withMillisRemoved());
        TestCase.assertEquals(PeriodType.standard().withMillisRemoved(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RI_RI_PeriodType4() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = new DateTime(2005, 7, 17, 1, 1, 1, 1);
        Period test = new Period(dt1, dt2, PeriodType.standard());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(3, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(1, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RI_PeriodType5() throws Throwable {
        DateTime dt1 = null;// 2002-06-09T01:00+01:00

        DateTime dt2 = null;// 2002-06-09T01:00+01:00

        Period test = new Period(dt1, dt2, PeriodType.standard());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RP_RP2Local() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 6, 9);
        LocalDate dt2 = new LocalDate(2005, 5, 17);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(11, test.getMonths());
        TestCase.assertEquals(1, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RP_RP7() throws Throwable {
        Partial dt1 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.monthOfYear(), 12);
        Partial dt2 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 14);
        try {
            new Period(dt1, dt2);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_RP_RP8() throws Throwable {
        Partial dt1 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 12);
        Partial dt2 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 14);
        try {
            new Period(dt1, dt2);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_RP_RP_PeriodType2Local() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 6, 9);
        LocalDate dt2 = new LocalDate(2005, 5, 17);
        Period test = new Period(dt1, dt2, PeriodType.yearMonthDay());
        TestCase.assertEquals(PeriodType.yearMonthDay(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(11, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(8, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_RP_RP_PeriodType7() throws Throwable {
        Partial dt1 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.monthOfYear(), 12);
        Partial dt2 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 14);
        try {
            new Period(dt1, dt2, PeriodType.standard());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testConstructor_RP_RP_PeriodType8() throws Throwable {
        Partial dt1 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 12);
        Partial dt2 = new Partial().with(DateTimeFieldType.year(), 2005).with(DateTimeFieldType.hourOfDay(), 14);
        try {
            new Period(dt1, dt2, PeriodType.standard());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RI_RD1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration dur = toDuration();
        Period test = new Period(dt1, dur);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RD2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Duration dur = null;
        Period test = new Period(dt1, dur);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RI_RD_PeriodType1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration dur = toDuration();
        Period test = new Period(dt1, dur, PeriodType.yearDayTime());
        TestCase.assertEquals(PeriodType.yearDayTime(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(31, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RI_RD_PeriodType2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Duration dur = null;
        Period test = new Period(dt1, dur, ((PeriodType) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RD_RI1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration dur = toDuration();
        Period test = new Period(dur, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RD_RI2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Duration dur = null;
        Period test = new Period(dur, dt1);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testConstructor_RD_RI_PeriodType1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration dur = toDuration();
        Period test = new Period(dur, dt2, PeriodType.yearDayTime());
        TestCase.assertEquals(PeriodType.yearDayTime(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(31, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    public void testConstructor_RD_RI_PeriodType2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        Duration dur = null;
        Period test = new Period(dur, dt1, ((PeriodType) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object1() throws Throwable {
        Period test = new Period("P1Y2M3D");
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(2, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(3, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_Object2() throws Throwable {
        Period test = new Period(((Object) (null)));
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_Object3() throws Throwable {
        Period test = new Period(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()));
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(2, test.getMinutes());
        TestCase.assertEquals(3, test.getSeconds());
        TestCase.assertEquals(4, test.getMillis());
    }

    public void testConstructor_Object4() throws Throwable {
        Period base = new Period(1, 1, 0, 1, 1, 1, 1, 1, PeriodType.standard());
        Period test = new Period(base);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(1, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(1, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(1, test.getMinutes());
        TestCase.assertEquals(1, test.getSeconds());
        TestCase.assertEquals(1, test.getMillis());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor (Object)
     */
    public void testConstructor_Object_PeriodType1() throws Throwable {
        Period test = new Period("P1Y2M3D", PeriodType.yearMonthDayTime());
        TestCase.assertEquals(PeriodType.yearMonthDayTime(), test.getPeriodType());
        TestCase.assertEquals(1, test.getYears());
        TestCase.assertEquals(2, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(3, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_Object_PeriodType2() throws Throwable {
        Period test = new Period(((Object) (null)), PeriodType.yearMonthDayTime());
        TestCase.assertEquals(PeriodType.yearMonthDayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testConstructor_Object_PeriodType3() throws Throwable {
        Period test = new Period(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()), PeriodType.yearMonthDayTime());
        TestCase.assertEquals(PeriodType.yearMonthDayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(2, test.getMinutes());
        TestCase.assertEquals(3, test.getSeconds());
        TestCase.assertEquals(4, test.getMillis());
    }

    public void testConstructor_Object_PeriodType4() throws Throwable {
        Period test = new Period(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()), ((PeriodType) (null)));
        TestCase.assertEquals(PeriodType.dayTime(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(1, test.getHours());
        TestCase.assertEquals(2, test.getMinutes());
        TestCase.assertEquals(3, test.getSeconds());
        TestCase.assertEquals(4, test.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testFactoryYears() throws Throwable {
        Period test = Period.years(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(6, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryMonths() throws Throwable {
        Period test = Period.months(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(6, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryWeeks() throws Throwable {
        Period test = Period.weeks(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(6, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryDays() throws Throwable {
        Period test = Period.days(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(6, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryHours() throws Throwable {
        Period test = Period.hours(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(6, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryMinutes() throws Throwable {
        Period test = Period.minutes(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactorySeconds() throws Throwable {
        Period test = Period.seconds(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(6, test.getSeconds());
        TestCase.assertEquals(0, test.getMillis());
    }

    public void testFactoryMillis() throws Throwable {
        Period test = Period.millis(6);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());
        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(0, test.getHours());
        TestCase.assertEquals(0, test.getMinutes());
        TestCase.assertEquals(0, test.getSeconds());
        TestCase.assertEquals(6, test.getMillis());
    }

    // -------------------------------------------------------------------------
    public void testConstructor_trickyDifferences_RI_RI_toFeb_standardYear() throws Throwable {
        DateTime dt1 = new DateTime(2011, 1, 1, 0, 0);
        DateTime dt2 = new DateTime(2011, 2, 28, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 3, 6, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toFeb_leapYear() throws Throwable {
        DateTime dt1 = new DateTime(2012, 1, 1, 0, 0);
        DateTime dt2 = new DateTime(2012, 2, 29, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 4, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toFeb_exactMonths() throws Throwable {
        DateTime dt1 = new DateTime(2004, 12, 28, 0, 0);
        DateTime dt2 = new DateTime(2005, 2, 28, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toFeb_endOfMonth1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 12, 29, 0, 0);
        DateTime dt2 = new DateTime(2005, 2, 28, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toFeb_endOfMonth2() throws Throwable {
        DateTime dt1 = new DateTime(2004, 12, 30, 0, 0);
        DateTime dt2 = new DateTime(2005, 2, 28, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toFeb_endOfMonth3() throws Throwable {
        DateTime dt1 = new DateTime(2004, 12, 31, 0, 0);
        DateTime dt2 = new DateTime(2005, 2, 28, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toMar_endOfMonth1() throws Throwable {
        DateTime dt1 = new DateTime(2013, 1, 31, 0, 0);
        DateTime dt2 = new DateTime(2013, 3, 30, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 4, 2, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_RI_RI_toMar_endOfMonth2() throws Throwable {
        DateTime dt1 = new DateTime(2013, 1, 31, 0, 0);
        DateTime dt2 = new DateTime(2013, 3, 31, 0, 0);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    // -------------------------------------------------------------------------
    public void testConstructor_trickyDifferences_LD_LD_toFeb_standardYear() throws Throwable {
        LocalDate dt1 = new LocalDate(2011, 1, 1);
        LocalDate dt2 = new LocalDate(2011, 2, 28);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 3, 6, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toFeb_leapYear() throws Throwable {
        LocalDate dt1 = new LocalDate(2012, 1, 1);
        LocalDate dt2 = new LocalDate(2012, 2, 29);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 4, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toFeb_exactMonths() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 12, 28);
        LocalDate dt2 = new LocalDate(2005, 2, 28);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toFeb_endOfMonth1() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 12, 29);
        LocalDate dt2 = new LocalDate(2005, 2, 28);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toFeb_endOfMonth2() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 12, 30);
        LocalDate dt2 = new LocalDate(2005, 2, 28);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toFeb_endOfMonth3() throws Throwable {
        LocalDate dt1 = new LocalDate(2004, 12, 31);
        LocalDate dt2 = new LocalDate(2005, 2, 28);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toMar_endOfMonth1() throws Throwable {
        LocalDate dt1 = new LocalDate(2013, 1, 31);
        LocalDate dt2 = new LocalDate(2013, 3, 30);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 1, 4, 2, 0, 0, 0, 0), test);
    }

    public void testConstructor_trickyDifferences_LD_LD_toMar_endOfMonth2() throws Throwable {
        LocalDate dt1 = new LocalDate(2013, 1, 31);
        LocalDate dt2 = new LocalDate(2013, 3, 31);
        Period test = new Period(dt1, dt2);
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(new Period(0, 2, 0, 0, 0, 0, 0, 0), test);
    }

    public void testFactoryFieldDifference5() throws Throwable {
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.dayOfWeek() };
        Partial start = new Partial(types, new int[]{ 1, 2, 3 });
        Partial end = new Partial(types, new int[]{ 1, 2, 3 });
        try {
            Period.fieldDifference(start, end);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

