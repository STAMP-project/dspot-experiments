/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.util.Calendar;
import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class DateUtilsFragmentTest {
    private static final int months = 7;// second final prime before 12


    private static final int days = 23;// second final prime before 31 (and valid)


    private static final int hours = 19;// second final prime before 24


    private static final int minutes = 53;// second final prime before 60


    private static final int seconds = 47;// third final prime before 60


    private static final int millis = 991;// second final prime before 1000


    private Date aDate;

    private Calendar aCalendar;

    @Test
    public void testNullDate() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMilliseconds(((Date) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInSeconds(((Date) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMinutes(((Date) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInHours(((Date) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInDays(((Date) (null)), Calendar.MILLISECOND));
    }

    @Test
    public void testNullCalendar() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMilliseconds(((Calendar) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInSeconds(((Calendar) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMinutes(((Calendar) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInHours(((Calendar) (null)), Calendar.MILLISECOND));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInDays(((Calendar) (null)), Calendar.MILLISECOND));
    }

    @Test
    public void testInvalidFragmentWithDate() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMilliseconds(aDate, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInSeconds(aDate, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMinutes(aDate, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInHours(aDate, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInDays(aDate, 0));
    }

    @Test
    public void testInvalidFragmentWithCalendar() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMilliseconds(aCalendar, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInSeconds(aCalendar, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInMinutes(aCalendar, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInHours(aCalendar, 0));
        Assertions.assertThrows(IllegalArgumentException.class, () -> DateUtils.getFragmentInDays(aCalendar, 0));
    }

    @Test
    public void testMillisecondFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInMilliseconds(aDate, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInSeconds(aDate, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aDate, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aDate, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.MILLISECOND));
    }

    @Test
    public void testMillisecondFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInSeconds(aCalendar, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aCalendar, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aCalendar, Calendar.MILLISECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.MILLISECOND));
    }

    @Test
    public void testSecondFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInSeconds(aDate, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aDate, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aDate, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.SECOND));
    }

    @Test
    public void testSecondFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInSeconds(aCalendar, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aCalendar, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aCalendar, Calendar.SECOND));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.SECOND));
    }

    @Test
    public void testMinuteFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aDate, Calendar.MINUTE));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aDate, Calendar.MINUTE));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.MINUTE));
    }

    @Test
    public void testMinuteFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInMinutes(aCalendar, Calendar.MINUTE));
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aCalendar, Calendar.MINUTE));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.MINUTE));
    }

    @Test
    public void testHourOfDayFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aDate, Calendar.HOUR_OF_DAY));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testHourOfDayFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInHours(aCalendar, Calendar.HOUR_OF_DAY));
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testDayOfYearFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.DAY_OF_YEAR));
    }

    @Test
    public void testDayOfYearFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.DAY_OF_YEAR));
    }

    @Test
    public void testDateFragmentInLargerUnitWithDate() {
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aDate, Calendar.DATE));
    }

    @Test
    public void testDateFragmentInLargerUnitWithCalendar() {
        Assertions.assertEquals(0, DateUtils.getFragmentInDays(aCalendar, Calendar.DATE));
    }

    // Calendar.SECOND as useful fragment
    @Test
    public void testMillisecondsOfSecondWithDate() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.SECOND);
        Assertions.assertEquals(DateUtilsFragmentTest.millis, testResult);
    }

    @Test
    public void testMillisecondsOfSecondWithCalendar() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.SECOND);
        Assertions.assertEquals(DateUtilsFragmentTest.millis, testResult);
        Assertions.assertEquals(aCalendar.get(Calendar.MILLISECOND), testResult);
    }

    // Calendar.MINUTE as useful fragment
    @Test
    public void testMillisecondsOfMinuteWithDate() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.MINUTE);
        Assertions.assertEquals(((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testMillisecondsOfMinuteWithCalender() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.MINUTE);
        Assertions.assertEquals(((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testSecondsofMinuteWithDate() {
        final long testResult = DateUtils.getFragmentInSeconds(aDate, Calendar.MINUTE);
        Assertions.assertEquals(DateUtilsFragmentTest.seconds, testResult);
    }

    @Test
    public void testSecondsofMinuteWithCalendar() {
        final long testResult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.MINUTE);
        Assertions.assertEquals(DateUtilsFragmentTest.seconds, testResult);
        Assertions.assertEquals(aCalendar.get(Calendar.SECOND), testResult);
    }

    // Calendar.HOUR_OF_DAY as useful fragment
    @Test
    public void testMillisecondsOfHourWithDate() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testMillisecondsOfHourWithCalendar() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testSecondsofHourWithDate() {
        final long testResult = DateUtils.getFragmentInSeconds(aDate, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testSecondsofHourWithCalendar() {
        final long testResult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testMinutesOfHourWithDate() {
        final long testResult = DateUtils.getFragmentInMinutes(aDate, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals(DateUtilsFragmentTest.minutes, testResult);
    }

    @Test
    public void testMinutesOfHourWithCalendar() {
        final long testResult = DateUtils.getFragmentInMinutes(aCalendar, Calendar.HOUR_OF_DAY);
        Assertions.assertEquals(DateUtilsFragmentTest.minutes, testResult);
    }

    // Calendar.DATE and Calendar.DAY_OF_YEAR as useful fragment
    @Test
    public void testMillisecondsOfDayWithDate() {
        long testresult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.DATE);
        final long expectedValue = (((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR));
        Assertions.assertEquals(expectedValue, testresult);
        testresult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testresult);
    }

    @Test
    public void testMillisecondsOfDayWithCalendar() {
        long testresult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.DATE);
        final long expectedValue = (((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR));
        Assertions.assertEquals(expectedValue, testresult);
        testresult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testresult);
    }

    @Test
    public void testSecondsOfDayWithDate() {
        long testresult = DateUtils.getFragmentInSeconds(aDate, Calendar.DATE);
        final long expectedValue = (DateUtilsFragmentTest.seconds) + ((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) / (DateUtils.MILLIS_PER_SECOND));
        Assertions.assertEquals(expectedValue, testresult);
        testresult = DateUtils.getFragmentInSeconds(aDate, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testresult);
    }

    @Test
    public void testSecondsOfDayWithCalendar() {
        long testresult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.DATE);
        final long expectedValue = (DateUtilsFragmentTest.seconds) + ((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) / (DateUtils.MILLIS_PER_SECOND));
        Assertions.assertEquals(expectedValue, testresult);
        testresult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testresult);
    }

    @Test
    public void testMinutesOfDayWithDate() {
        long testResult = DateUtils.getFragmentInMinutes(aDate, Calendar.DATE);
        final long expectedValue = (DateUtilsFragmentTest.minutes) + (((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) / (DateUtils.MILLIS_PER_MINUTE));
        Assertions.assertEquals(expectedValue, testResult);
        testResult = DateUtils.getFragmentInMinutes(aDate, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testResult);
    }

    @Test
    public void testMinutesOfDayWithCalendar() {
        long testResult = DateUtils.getFragmentInMinutes(aCalendar, Calendar.DATE);
        final long expectedValue = (DateUtilsFragmentTest.minutes) + (((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) / (DateUtils.MILLIS_PER_MINUTE));
        Assertions.assertEquals(expectedValue, testResult);
        testResult = DateUtils.getFragmentInMinutes(aCalendar, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testResult);
    }

    @Test
    public void testHoursOfDayWithDate() {
        long testResult = DateUtils.getFragmentInHours(aDate, Calendar.DATE);
        final long expectedValue = DateUtilsFragmentTest.hours;
        Assertions.assertEquals(expectedValue, testResult);
        testResult = DateUtils.getFragmentInHours(aDate, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testResult);
    }

    @Test
    public void testHoursOfDayWithCalendar() {
        long testResult = DateUtils.getFragmentInHours(aCalendar, Calendar.DATE);
        final long expectedValue = DateUtilsFragmentTest.hours;
        Assertions.assertEquals(expectedValue, testResult);
        testResult = DateUtils.getFragmentInHours(aCalendar, Calendar.DAY_OF_YEAR);
        Assertions.assertEquals(expectedValue, testResult);
    }

    // Calendar.MONTH as useful fragment
    @Test
    public void testMillisecondsOfMonthWithDate() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.MONTH);
        Assertions.assertEquals((((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))), testResult);
    }

    @Test
    public void testMillisecondsOfMonthWithCalendar() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.MONTH);
        Assertions.assertEquals((((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))), testResult);
    }

    @Test
    public void testSecondsOfMonthWithDate() {
        final long testResult = DateUtils.getFragmentInSeconds(aDate, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testSecondsOfMonthWithCalendar() {
        final long testResult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testMinutesOfMonthWithDate() {
        final long testResult = DateUtils.getFragmentInMinutes(aDate, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.minutes) + ((((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testMinutesOfMonthWithCalendar() {
        final long testResult = DateUtils.getFragmentInMinutes(aCalendar, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.minutes) + ((((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) + (((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testHoursOfMonthWithDate() {
        final long testResult = DateUtils.getFragmentInHours(aDate, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.hours) + ((((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY)) / (DateUtils.MILLIS_PER_HOUR))), testResult);
    }

    @Test
    public void testHoursOfMonthWithCalendar() {
        final long testResult = DateUtils.getFragmentInHours(aCalendar, Calendar.MONTH);
        Assertions.assertEquals(((DateUtilsFragmentTest.hours) + ((((DateUtilsFragmentTest.days) - 1) * (DateUtils.MILLIS_PER_DAY)) / (DateUtils.MILLIS_PER_HOUR))), testResult);
    }

    // Calendar.YEAR as useful fragment
    @Test
    public void testMillisecondsOfYearWithDate() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aDate, Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals((((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((cal.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))), testResult);
    }

    @Test
    public void testMillisecondsOfYearWithCalendar() {
        final long testResult = DateUtils.getFragmentInMilliseconds(aCalendar, Calendar.YEAR);
        Assertions.assertEquals((((((DateUtilsFragmentTest.millis) + ((DateUtilsFragmentTest.seconds) * (DateUtils.MILLIS_PER_SECOND))) + ((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE))) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((aCalendar.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))), testResult);
    }

    @Test
    public void testSecondsOfYearWithDate() {
        final long testResult = DateUtils.getFragmentInSeconds(aDate, Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((cal.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testSecondsOfYearWithCalendar() {
        final long testResult = DateUtils.getFragmentInSeconds(aCalendar, Calendar.YEAR);
        Assertions.assertEquals(((DateUtilsFragmentTest.seconds) + (((((DateUtilsFragmentTest.minutes) * (DateUtils.MILLIS_PER_MINUTE)) + ((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR))) + (((aCalendar.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_SECOND))), testResult);
    }

    @Test
    public void testMinutesOfYearWithDate() {
        final long testResult = DateUtils.getFragmentInMinutes(aDate, Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals(((DateUtilsFragmentTest.minutes) + ((((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) + (((cal.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testMinutesOfYearWithCalendar() {
        final long testResult = DateUtils.getFragmentInMinutes(aCalendar, Calendar.YEAR);
        Assertions.assertEquals(((DateUtilsFragmentTest.minutes) + ((((DateUtilsFragmentTest.hours) * (DateUtils.MILLIS_PER_HOUR)) + (((aCalendar.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY))) / (DateUtils.MILLIS_PER_MINUTE))), testResult);
    }

    @Test
    public void testMinutesOfYearWithWrongOffsetBugWithCalendar() {
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DAY_OF_YEAR, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        final long testResult = DateUtils.getFragmentInMinutes(c, Calendar.YEAR);
        Assertions.assertEquals(0, testResult);
    }

    @Test
    public void testHoursOfYearWithDate() {
        final long testResult = DateUtils.getFragmentInHours(aDate, Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals(((DateUtilsFragmentTest.hours) + ((((cal.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY)) / (DateUtils.MILLIS_PER_HOUR))), testResult);
    }

    @Test
    public void testHoursOfYearWithCalendar() {
        final long testResult = DateUtils.getFragmentInHours(aCalendar, Calendar.YEAR);
        Assertions.assertEquals(((DateUtilsFragmentTest.hours) + ((((aCalendar.get(Calendar.DAY_OF_YEAR)) - 1) * (DateUtils.MILLIS_PER_DAY)) / (DateUtils.MILLIS_PER_HOUR))), testResult);
    }

    @Test
    public void testDaysOfMonthWithCalendar() {
        final long testResult = DateUtils.getFragmentInDays(aCalendar, Calendar.MONTH);
        Assertions.assertEquals(DateUtilsFragmentTest.days, testResult);
    }

    @Test
    public void testDaysOfMonthWithDate() {
        final long testResult = DateUtils.getFragmentInDays(aDate, Calendar.MONTH);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals(cal.get(Calendar.DAY_OF_MONTH), testResult);
    }

    @Test
    public void testDaysOfYearWithCalendar() {
        final long testResult = DateUtils.getFragmentInDays(aCalendar, Calendar.YEAR);
        Assertions.assertEquals(aCalendar.get(Calendar.DAY_OF_YEAR), testResult);
    }

    @Test
    public void testDaysOfYearWithDate() {
        final long testResult = DateUtils.getFragmentInDays(aDate, Calendar.YEAR);
        final Calendar cal = Calendar.getInstance();
        cal.setTime(aDate);
        Assertions.assertEquals(cal.get(Calendar.DAY_OF_YEAR), testResult);
    }
}

