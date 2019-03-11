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

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a JUnit test for PeriodType.
 *
 * @author Stephen Colebourne
 */
public class TestPeriodType extends TestCase {
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

    public TestPeriodType(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testStandard() throws Exception {
        PeriodType type = PeriodType.standard();
        TestCase.assertEquals(8, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(6));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(7));
        TestCase.assertEquals("Standard", type.getName());
        TestCase.assertEquals("PeriodType[Standard]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.standard())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearMonthDayTime() throws Exception {
        PeriodType type = PeriodType.yearMonthDayTime();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals("YearMonthDayTime", type.getName());
        TestCase.assertEquals("PeriodType[YearMonthDayTime]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearMonthDayTime())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearMonthDayTime().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearMonthDay() throws Exception {
        PeriodType type = PeriodType.yearMonthDay();
        TestCase.assertEquals(3, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals("YearMonthDay", type.getName());
        TestCase.assertEquals("PeriodType[YearMonthDay]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearMonthDay())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearMonthDay().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearWeekDayTime() throws Exception {
        PeriodType type = PeriodType.yearWeekDayTime();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals("YearWeekDayTime", type.getName());
        TestCase.assertEquals("PeriodType[YearWeekDayTime]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearWeekDayTime())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearWeekDayTime().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearWeekDay() throws Exception {
        PeriodType type = PeriodType.yearWeekDay();
        TestCase.assertEquals(3, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals("YearWeekDay", type.getName());
        TestCase.assertEquals("PeriodType[YearWeekDay]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearWeekDay())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearWeekDay().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearDayTime() throws Exception {
        PeriodType type = PeriodType.yearDayTime();
        TestCase.assertEquals(6, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(5));
        TestCase.assertEquals("YearDayTime", type.getName());
        TestCase.assertEquals("PeriodType[YearDayTime]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearDayTime())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearDayTime().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYearDay() throws Exception {
        PeriodType type = PeriodType.yearDay();
        TestCase.assertEquals(2, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(1));
        TestCase.assertEquals("YearDay", type.getName());
        TestCase.assertEquals("PeriodType[YearDay]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.yearDay())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.yearDay().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testDayTime() throws Exception {
        PeriodType type = PeriodType.dayTime();
        TestCase.assertEquals(5, type.size());
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(4));
        TestCase.assertEquals("DayTime", type.getName());
        TestCase.assertEquals("PeriodType[DayTime]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.dayTime())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.dayTime().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testTime() throws Exception {
        PeriodType type = PeriodType.time();
        TestCase.assertEquals(4, type.size());
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(3));
        TestCase.assertEquals("Time", type.getName());
        TestCase.assertEquals("PeriodType[Time]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.time())));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.time().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testYears() throws Exception {
        PeriodType type = PeriodType.years();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals("Years", type.getName());
        TestCase.assertEquals("PeriodType[Years]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.years())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.years().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMonths() throws Exception {
        PeriodType type = PeriodType.months();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(0));
        TestCase.assertEquals("Months", type.getName());
        TestCase.assertEquals("PeriodType[Months]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.months())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.months().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testWeeks() throws Exception {
        PeriodType type = PeriodType.weeks();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(0));
        TestCase.assertEquals("Weeks", type.getName());
        TestCase.assertEquals("PeriodType[Weeks]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.weeks())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.weeks().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testDays() throws Exception {
        PeriodType type = PeriodType.days();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(0));
        TestCase.assertEquals("Days", type.getName());
        TestCase.assertEquals("PeriodType[Days]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.days())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.days().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testHours() throws Exception {
        PeriodType type = PeriodType.hours();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(0));
        TestCase.assertEquals("Hours", type.getName());
        TestCase.assertEquals("PeriodType[Hours]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.hours())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.hours().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMinutes() throws Exception {
        PeriodType type = PeriodType.minutes();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(0));
        TestCase.assertEquals("Minutes", type.getName());
        TestCase.assertEquals("PeriodType[Minutes]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.minutes())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.minutes().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testSeconds() throws Exception {
        PeriodType type = PeriodType.seconds();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(0));
        TestCase.assertEquals("Seconds", type.getName());
        TestCase.assertEquals("PeriodType[Seconds]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.seconds())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.seconds().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMillis() throws Exception {
        PeriodType type = PeriodType.millis();
        TestCase.assertEquals(1, type.size());
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(0));
        TestCase.assertEquals("Millis", type.getName());
        TestCase.assertEquals("PeriodType[Millis]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.millis())));
        TestCase.assertEquals(false, type.equals(PeriodType.standard()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.standard().hashCode())));
        assertSameAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testForFields1() throws Exception {
        PeriodType type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.years() });
        TestCase.assertSame(PeriodType.years(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.months() });
        TestCase.assertSame(PeriodType.months(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.weeks() });
        TestCase.assertSame(PeriodType.weeks(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.days() });
        TestCase.assertSame(PeriodType.days(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.hours() });
        TestCase.assertSame(PeriodType.hours(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.minutes() });
        TestCase.assertSame(PeriodType.minutes(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.seconds() });
        TestCase.assertSame(PeriodType.seconds(), type);
        type = PeriodType.forFields(new DurationFieldType[]{ DurationFieldType.millis() });
        TestCase.assertSame(PeriodType.millis(), type);
    }

    public void testForFields2() throws Exception {
        DurationFieldType[] types = new DurationFieldType[]{ DurationFieldType.years(), DurationFieldType.hours() };
        PeriodType type = PeriodType.forFields(types);
        TestCase.assertEquals(2, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(1));
        TestCase.assertEquals("StandardNoMonthsNoWeeksNoDaysNoMinutesNoSecondsNoMillis", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoMonthsNoWeeksNoDaysNoMinutesNoSecondsNoMillis]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.forFields(types))));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.forFields(types).hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    public void testForFields3() throws Exception {
        DurationFieldType[] types = new DurationFieldType[]{ DurationFieldType.months(), DurationFieldType.weeks() };
        PeriodType type = PeriodType.forFields(types);
        TestCase.assertEquals(2, type.size());
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(1));
        TestCase.assertEquals("StandardNoYearsNoDaysNoHoursNoMinutesNoSecondsNoMillis", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoYearsNoDaysNoHoursNoMinutesNoSecondsNoMillis]", type.toString());
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, (type == (PeriodType.forFields(types))));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.forFields(types).hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        assertSameAfterSerialization(type);
    }

    public void testForFields4() throws Exception {
        DurationFieldType[] types = new DurationFieldType[]{ DurationFieldType.weeks(), DurationFieldType.days()// adding this makes this test unique, so cache is not pre-populated
        , DurationFieldType.months() };
        DurationFieldType[] types2 = new DurationFieldType[]{ DurationFieldType.months(), DurationFieldType.days(), DurationFieldType.weeks() };
        PeriodType type = PeriodType.forFields(types);
        PeriodType type2 = PeriodType.forFields(types2);
        TestCase.assertEquals(true, (type == type2));
    }

    public void testForFields5() throws Exception {
        DurationFieldType[] types = new DurationFieldType[]{ DurationFieldType.centuries(), DurationFieldType.months() };
        try {
            PeriodType.forFields(types);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            PeriodType.forFields(types);// repeated for test coverage of cache

            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testForFields6() throws Exception {
        DurationFieldType[] types = null;
        try {
            PeriodType.forFields(types);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        types = new DurationFieldType[0];
        try {
            PeriodType.forFields(types);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        types = new DurationFieldType[]{ null, DurationFieldType.months() };
        try {
            PeriodType.forFields(types);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        types = new DurationFieldType[]{ DurationFieldType.months(), null };
        try {
            PeriodType.forFields(types);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // ensure hash key distribution
    public void testForFields7() throws Exception {
        DurationFieldType[] types = new DurationFieldType[]{ DurationFieldType.weeks(), DurationFieldType.months() };
        DurationFieldType[] types2 = new DurationFieldType[]{ DurationFieldType.seconds() };
        PeriodType type = PeriodType.forFields(types);
        PeriodType type2 = PeriodType.forFields(types2);
        TestCase.assertEquals(false, (type == type2));
        TestCase.assertEquals(false, type.equals(type2));
        TestCase.assertEquals(false, ((type.hashCode()) == (type2.hashCode())));
    }

    // -----------------------------------------------------------------------
    public void testMaskYears() throws Exception {
        PeriodType type = PeriodType.standard().withYearsRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withYearsRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withYearsRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoYears", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoYears]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskMonths() throws Exception {
        PeriodType type = PeriodType.standard().withMonthsRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withMonthsRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withMonthsRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoMonths", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoMonths]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskWeeks() throws Exception {
        PeriodType type = PeriodType.standard().withWeeksRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withWeeksRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withWeeksRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoWeeks", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoWeeks]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskDays() throws Exception {
        PeriodType type = PeriodType.standard().withDaysRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withDaysRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withDaysRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoDays", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoDays]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskHours() throws Exception {
        PeriodType type = PeriodType.standard().withHoursRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withHoursRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withHoursRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoHours", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoHours]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskMinutes() throws Exception {
        PeriodType type = PeriodType.standard().withMinutesRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withMinutesRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withMinutesRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoMinutes", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoMinutes]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskSeconds() throws Exception {
        PeriodType type = PeriodType.standard().withSecondsRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withSecondsRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withSecondsRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoSeconds", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoSeconds]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskMillis() throws Exception {
        PeriodType type = PeriodType.standard().withMillisRemoved();
        TestCase.assertEquals(7, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.hours(), type.getFieldType(4));
        TestCase.assertEquals(DurationFieldType.minutes(), type.getFieldType(5));
        TestCase.assertEquals(DurationFieldType.seconds(), type.getFieldType(6));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withMillisRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withMillisRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoMillis", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoMillis]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskHoursMinutesSeconds() throws Exception {
        PeriodType type = PeriodType.standard().withHoursRemoved().withMinutesRemoved().withSecondsRemoved();
        TestCase.assertEquals(5, type.size());
        TestCase.assertEquals(DurationFieldType.years(), type.getFieldType(0));
        TestCase.assertEquals(DurationFieldType.months(), type.getFieldType(1));
        TestCase.assertEquals(DurationFieldType.weeks(), type.getFieldType(2));
        TestCase.assertEquals(DurationFieldType.days(), type.getFieldType(3));
        TestCase.assertEquals(DurationFieldType.millis(), type.getFieldType(4));
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.standard().withHoursRemoved().withMinutesRemoved().withSecondsRemoved()));
        TestCase.assertEquals(false, type.equals(PeriodType.millis()));
        TestCase.assertEquals(true, ((type.hashCode()) == (type.hashCode())));
        TestCase.assertEquals(true, ((type.hashCode()) == (PeriodType.standard().withHoursRemoved().withMinutesRemoved().withSecondsRemoved().hashCode())));
        TestCase.assertEquals(false, ((type.hashCode()) == (PeriodType.millis().hashCode())));
        TestCase.assertEquals("StandardNoHoursNoMinutesNoSeconds", type.getName());
        TestCase.assertEquals("PeriodType[StandardNoHoursNoMinutesNoSeconds]", type.toString());
        assertEqualsAfterSerialization(type);
    }

    // -----------------------------------------------------------------------
    public void testMaskTwice1() throws Exception {
        PeriodType type = PeriodType.standard().withYearsRemoved();
        PeriodType type2 = type.withYearsRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withMonthsRemoved();
        type2 = type.withMonthsRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withWeeksRemoved();
        type2 = type.withWeeksRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withDaysRemoved();
        type2 = type.withDaysRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withHoursRemoved();
        type2 = type.withHoursRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withMinutesRemoved();
        type2 = type.withMinutesRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withSecondsRemoved();
        type2 = type.withSecondsRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.standard().withMillisRemoved();
        type2 = type.withMillisRemoved();
        TestCase.assertEquals(true, (type == type2));
    }

    // -----------------------------------------------------------------------
    public void testMaskTwice2() throws Exception {
        PeriodType type = PeriodType.dayTime();
        PeriodType type2 = type.withYearsRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.dayTime();
        type2 = type.withMonthsRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.dayTime();
        type2 = type.withWeeksRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.millis();
        type2 = type.withDaysRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.millis();
        type2 = type.withHoursRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.millis();
        type2 = type.withMinutesRemoved();
        TestCase.assertEquals(true, (type == type2));
        type = PeriodType.millis();
        type2 = type.withSecondsRemoved();
        TestCase.assertEquals(true, (type == type2));
    }

    // -----------------------------------------------------------------------
    public void testEquals() throws Exception {
        PeriodType type = PeriodType.dayTime().withMillisRemoved();
        TestCase.assertEquals(true, type.equals(type));
        TestCase.assertEquals(true, type.equals(PeriodType.dayTime().withMillisRemoved()));
        TestCase.assertEquals(false, type.equals(null));
        TestCase.assertEquals(false, type.equals(""));
    }

    public void testHashCode() throws Exception {
        PeriodType type = PeriodType.dayTime().withMillisRemoved();
        TestCase.assertEquals(type.hashCode(), type.hashCode());
    }

    // -----------------------------------------------------------------------
    public void testIsSupported() throws Exception {
        PeriodType type = PeriodType.dayTime().withMillisRemoved();
        TestCase.assertEquals(false, type.isSupported(DurationFieldType.years()));
        TestCase.assertEquals(false, type.isSupported(DurationFieldType.months()));
        TestCase.assertEquals(false, type.isSupported(DurationFieldType.weeks()));
        TestCase.assertEquals(true, type.isSupported(DurationFieldType.days()));
        TestCase.assertEquals(true, type.isSupported(DurationFieldType.hours()));
        TestCase.assertEquals(true, type.isSupported(DurationFieldType.minutes()));
        TestCase.assertEquals(true, type.isSupported(DurationFieldType.seconds()));
        TestCase.assertEquals(false, type.isSupported(DurationFieldType.millis()));
    }

    // -----------------------------------------------------------------------
    public void testIndexOf() throws Exception {
        PeriodType type = PeriodType.dayTime().withMillisRemoved();
        TestCase.assertEquals((-1), type.indexOf(DurationFieldType.years()));
        TestCase.assertEquals((-1), type.indexOf(DurationFieldType.months()));
        TestCase.assertEquals((-1), type.indexOf(DurationFieldType.weeks()));
        TestCase.assertEquals(0, type.indexOf(DurationFieldType.days()));
        TestCase.assertEquals(1, type.indexOf(DurationFieldType.hours()));
        TestCase.assertEquals(2, type.indexOf(DurationFieldType.minutes()));
        TestCase.assertEquals(3, type.indexOf(DurationFieldType.seconds()));
        TestCase.assertEquals((-1), type.indexOf(DurationFieldType.millis()));
    }
}

