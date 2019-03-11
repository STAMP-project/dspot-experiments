/**
 * Copyright 2001-2005 Stephen Colebourne
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


/**
 * This class is a JUnit test for MutableDuration.
 *
 * @author Stephen Colebourne
 */
public class TestMutablePeriod_Constructors extends TestCase {
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

    public TestMutablePeriod_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParse_noFormatter() throws Throwable {
        TestCase.assertEquals(new MutablePeriod(1, 2, 3, 4, 5, 6, 7, 890), MutablePeriod.parse("P1Y2M3W4DT5H6M7.890S"));
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor ()
     */
    public void testConstructor1() throws Throwable {
        MutablePeriod test = new MutablePeriod();
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
     * Test constructor (PeriodType)
     */
    public void testConstructor_PeriodType1() throws Throwable {
        MutablePeriod test = new MutablePeriod(PeriodType.yearMonthDayTime());
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

    public void testConstructor_PeriodType2() throws Throwable {
        MutablePeriod test = new MutablePeriod(((PeriodType) (null)));
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
        MutablePeriod test = new MutablePeriod(length);
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
        MutablePeriod test = new MutablePeriod(length);
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
        MutablePeriod test = new MutablePeriod(length);
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

    // -----------------------------------------------------------------------
    public void testConstructor_long_PeriodType1() throws Throwable {
        long length = ((((4 * (MILLIS_PER_DAY)) + (5 * (MILLIS_PER_HOUR))) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        MutablePeriod test = new MutablePeriod(length, ((PeriodType) (null)));
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
        MutablePeriod test = new MutablePeriod(length, PeriodType.millis());
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
        MutablePeriod test = new MutablePeriod(length, PeriodType.standard());
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

    public void testConstructor_long_PeriodType4() throws Throwable {
        long length = (((5 * (MILLIS_PER_HOUR)) + (6 * (MILLIS_PER_MINUTE))) + (7 * (MILLIS_PER_SECOND))) + 8;
        MutablePeriod test = new MutablePeriod(length, PeriodType.standard().withMillisRemoved());
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
        MutablePeriod test = new MutablePeriod(length, ISOChronology.getInstance());
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
        MutablePeriod test = new MutablePeriod(length, ISOChronology.getInstanceUTC());
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
        MutablePeriod test = new MutablePeriod(length, ((Chronology) (null)));
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
        MutablePeriod test = new MutablePeriod(length, PeriodType.time().withMillisRemoved(), ISOChronology.getInstance());
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
        MutablePeriod test = new MutablePeriod(length, PeriodType.standard(), ISOChronology.getInstanceUTC());
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
        MutablePeriod test = new MutablePeriod(length, PeriodType.standard(), ((Chronology) (null)));
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
        MutablePeriod test = new MutablePeriod(length, ((PeriodType) (null)), ((Chronology) (null)));
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
        MutablePeriod test = new MutablePeriod(5, 6, 7, 8);
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
        MutablePeriod test = new MutablePeriod(1, 2, 3, 4, 5, 6, 7, 8);
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
        MutablePeriod test = new MutablePeriod(1, 2, 3, 4, 5, 6, 7, 8, null);
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
        MutablePeriod test = new MutablePeriod(0, 0, 0, 0, 5, 6, 7, 8, PeriodType.dayTime());
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
            new MutablePeriod(1, 2, 3, 4, 5, 6, 7, 8, PeriodType.dayTime());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testConstructor_long_long1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis());
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis());
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)));
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), PeriodType.dayTime());
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), PeriodType.standard().withMillisRemoved());
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

    // -----------------------------------------------------------------------
    public void testConstructor_long_long_Chronology1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0, CopticChronology.getInstance());
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1, CopticChronology.getInstance());
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), CopticChronology.getInstance());
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), ((Chronology) (null)));
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)), CopticChronology.getInstance());
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
        MutablePeriod test = new MutablePeriod(dt1.getMillis(), dt2.getMillis(), ((PeriodType) (null)), null);
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
        MutablePeriod test = new MutablePeriod(dt1, dt2);
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
        MutablePeriod test = new MutablePeriod(dt1, dt2);
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
        MutablePeriod test = new MutablePeriod(dt1, dt2);
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

        MutablePeriod test = new MutablePeriod(dt1, dt2);
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

        MutablePeriod test = new MutablePeriod(dt1, dt2);
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
    public void testConstructor_RI_RI_PeriodType1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        MutablePeriod test = new MutablePeriod(dt1, dt2, null);
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
        MutablePeriod test = new MutablePeriod(dt1, dt2, PeriodType.dayTime());
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
        MutablePeriod test = new MutablePeriod(dt1, dt2, PeriodType.standard().withMillisRemoved());
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
        MutablePeriod test = new MutablePeriod(dt1, dt2, PeriodType.standard());
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

        MutablePeriod test = new MutablePeriod(dt1, dt2, PeriodType.standard());
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
    public void testConstructor_RI_RD1() throws Throwable {
        DateTime dt1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime dt2 = new DateTime(2005, 7, 10, 1, 1, 1, 1);
        Duration dur = toDuration();
        MutablePeriod test = new MutablePeriod(dt1, dur);
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
        MutablePeriod test = new MutablePeriod(dt1, dur);
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
        MutablePeriod test = new MutablePeriod(dt1, dur, PeriodType.yearDayTime());
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
        MutablePeriod test = new MutablePeriod(dt1, dur, ((PeriodType) (null)));
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
        MutablePeriod test = new MutablePeriod("P1Y2M3D");
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
        MutablePeriod test = new MutablePeriod(((Object) (null)));
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
        MutablePeriod test = new MutablePeriod(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()));
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
        MutablePeriod test = new MutablePeriod(base);
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
     * Test constructor (Object,PeriodType)
     */
    public void testConstructor_Object_PeriodType1() throws Throwable {
        MutablePeriod test = new MutablePeriod("P1Y2M3D", PeriodType.yearMonthDayTime());
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
        MutablePeriod test = new MutablePeriod(((Object) (null)), PeriodType.yearMonthDayTime());
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
        MutablePeriod test = new MutablePeriod(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()), PeriodType.yearMonthDayTime());
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
        MutablePeriod test = new MutablePeriod(new Period(0, 0, 0, 0, 1, 2, 3, 4, PeriodType.dayTime()), ((PeriodType) (null)));
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
    public void testConstructor_Object_Chronology1() throws Throwable {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        MutablePeriod test = new MutablePeriod(new Duration(length), ISOChronology.getInstance());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());// (4 + (3 * 7) + (2 * 30) + 365) == 450

        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(0, test.getWeeks());
        TestCase.assertEquals(0, test.getDays());
        TestCase.assertEquals(((450 * 24) + 5), test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }

    public void testConstructor_Object_Chronology2() throws Throwable {
        long length = (((((((4L + (3L * 7L)) + (2L * 30L)) + 365L) * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;
        MutablePeriod test = new MutablePeriod(new Duration(length), ISOChronology.getInstanceUTC());
        TestCase.assertEquals(PeriodType.standard(), test.getPeriodType());
        TestCase.assertEquals(0, test.getYears());// (4 + (3 * 7) + (2 * 30) + 365) == 450

        TestCase.assertEquals(0, test.getMonths());
        TestCase.assertEquals(64, test.getWeeks());
        TestCase.assertEquals(2, test.getDays());
        TestCase.assertEquals(5, test.getHours());
        TestCase.assertEquals(6, test.getMinutes());
        TestCase.assertEquals(7, test.getSeconds());
        TestCase.assertEquals(8, test.getMillis());
    }
}

