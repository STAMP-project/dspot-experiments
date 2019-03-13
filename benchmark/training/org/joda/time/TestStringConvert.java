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


import StringConvert.INSTANCE;
import junit.framework.TestCase;
import org.joda.time.chrono.ISOChronology;


/**
 * Test string conversion.
 *
 * @author Stephen Colebourne
 */
public class TestStringConvert extends TestCase {
    private static final DateTimeZone ZONE = DateTimeZone.forID("+02:00");

    public TestStringConvert(String name) {
        super(name);
    }

    public void testDateTime() {
        DateTime test = new DateTime(2010, 6, 30, 2, 30, 50, 678, ISOChronology.getInstance(TestStringConvert.ZONE));
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("2010-06-30T02:30:50.678+02:00", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(DateTime.class, str));
    }

    public void testMutableDateTime() {
        MutableDateTime test = new MutableDateTime(2010, 6, 30, 2, 30, 50, 678, ISOChronology.getInstance(TestStringConvert.ZONE));
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("2010-06-30T02:30:50.678+02:00", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(MutableDateTime.class, str));
    }

    public void testLocalDateTime() {
        LocalDateTime test = new LocalDateTime(2010, 6, 30, 2, 30);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("2010-06-30T02:30:00.000", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(LocalDateTime.class, str));
    }

    public void testLocalDate() {
        LocalDate test = new LocalDate(2010, 6, 30);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("2010-06-30", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(LocalDate.class, str));
    }

    public void testLocalTime() {
        LocalTime test = new LocalTime(2, 30, 50, 678);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("02:30:50.678", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(LocalTime.class, str));
    }

    public void testYearMonth() {
        YearMonth test = new YearMonth(2010, 6);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("2010-06", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(YearMonth.class, str));
    }

    public void testMonthDay() {
        MonthDay test = new MonthDay(6, 30);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("--06-30", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(MonthDay.class, str));
    }

    public void testMonthDay_leapDay() {
        MonthDay test = new MonthDay(2, 29);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("--02-29", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(MonthDay.class, str));
    }

    // -----------------------------------------------------------------------
    public void testTimeZone() {
        DateTimeZone test = DateTimeZone.forID("Europe/Paris");
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("Europe/Paris", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(DateTimeZone.class, str));
    }

    // public void testInterval() {
    // DateTime a = new DateTime(2010, 6, 30, 2, 30, 50, 678, ISOChronology.getInstance(ZONE));
    // DateTime b = new DateTime(2011, 9, 10, 4, 20, 40, 234, ISOChronology.getInstance(ZONE));
    // Interval test = new Interval(a, b);
    // String str = StringConvert.INSTANCE.convertToString(test);
    // assertEquals("2010-06-30T02:30:50.678+02:00/2011-09-10T04:20:40.234+02:00", str);
    // assertEquals(test, StringConvert.INSTANCE.convertFromString(Interval.class, str));
    // }
    public void testDuration() {
        Duration test = new Duration(12345678L);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("PT12345.678S", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Duration.class, str));
    }

    public void testPeriod() {
        Period test = new Period(1, 2, 3, 4, 5, 6, 7, 8);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P1Y2M3W4DT5H6M7.008S", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Period.class, str));
    }

    public void testMutablePeriod() {
        MutablePeriod test = new MutablePeriod(1, 2, 3, 4, 5, 6, 7, 8);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P1Y2M3W4DT5H6M7.008S", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(MutablePeriod.class, str));
    }

    public void testYears() {
        Years test = Years.years(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P5Y", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Years.class, str));
    }

    public void testMonths() {
        Months test = Months.months(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P5M", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Months.class, str));
    }

    public void testWeeks() {
        Weeks test = Weeks.weeks(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P5W", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Weeks.class, str));
    }

    public void testDays() {
        Days test = Days.days(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("P5D", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Days.class, str));
    }

    public void testHours() {
        Hours test = Hours.hours(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("PT5H", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Hours.class, str));
    }

    public void testMinutes() {
        Minutes test = Minutes.minutes(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("PT5M", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Minutes.class, str));
    }

    public void testSeconds() {
        Seconds test = Seconds.seconds(5);
        String str = INSTANCE.convertToString(test);
        TestCase.assertEquals("PT5S", str);
        TestCase.assertEquals(test, INSTANCE.convertFromString(Seconds.class, str));
    }
}

