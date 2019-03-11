/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.apache.groovy.datetime.extensions;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


public class DateTimeExtensionsTest {
    @Test
    public void calendarConversionsDefaultTimeZone() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss SSS");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sdf.parse("20180115 153256 001"));
        LocalDate expectedLocalDate = LocalDate.of(2018, Month.JANUARY, 15);
        LocalTime expectedLocalTime = LocalTime.of(15, 32, 56, 1000000);
        LocalDateTime expectedLocalDateTime = LocalDateTime.of(expectedLocalDate, expectedLocalTime);
        Assert.assertEquals("DayOfWeek", DayOfWeek.MONDAY, DateTimeExtensions.toDayOfWeek(calendar));
        Assert.assertEquals("Month", Month.JANUARY, DateTimeExtensions.toMonth(calendar));
        Assert.assertEquals("MonthDay", MonthDay.of(Month.JANUARY, 15), DateTimeExtensions.toMonthDay(calendar));
        Assert.assertEquals("YearMonth", YearMonth.of(2018, Month.JANUARY), DateTimeExtensions.toYearMonth(calendar));
        Assert.assertEquals("Year", Year.of(2018), DateTimeExtensions.toYear(calendar));
        Assert.assertEquals("LocalDate", expectedLocalDate, DateTimeExtensions.toLocalDate(calendar));
        Assert.assertEquals("LocalTime", expectedLocalTime, DateTimeExtensions.toLocalTime(calendar));
        Assert.assertEquals("LocalDateTime", expectedLocalDateTime, DateTimeExtensions.toLocalDateTime(calendar));
        Assert.assertEquals("OffsetTime", expectedLocalTime, DateTimeExtensions.toOffsetTime(calendar).toLocalTime());
        Assert.assertEquals("OffsetDateTime", expectedLocalDateTime, DateTimeExtensions.toOffsetDateTime(calendar).toLocalDateTime());
        Assert.assertEquals("ZonedDateTime", expectedLocalDateTime, DateTimeExtensions.toZonedDateTime(calendar).toLocalDateTime());
    }

    @Test
    public void calendarConversionsDifferingTimeZones() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss SSS");
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+0"));
        calendar.setTime(sdf.parse("20180115 153256 001"));
    }

    @Test
    public void sameCalendarAndDateConvertIdentically() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmmss SSS");
        Date date = sdf.parse("20180115 153256 001");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Assert.assertEquals("DayOfWeek", DateTimeExtensions.toDayOfWeek(calendar), DateTimeExtensions.toDayOfWeek(date));
        Assert.assertEquals("Month", DateTimeExtensions.toMonth(calendar), DateTimeExtensions.toMonth(date));
        Assert.assertEquals("MonthDay", DateTimeExtensions.toMonthDay(calendar), DateTimeExtensions.toMonthDay(date));
        Assert.assertEquals("YearMonth", DateTimeExtensions.toYearMonth(calendar), DateTimeExtensions.toYearMonth(date));
        Assert.assertEquals("Year", DateTimeExtensions.toYear(calendar), DateTimeExtensions.toYear(date));
        Assert.assertEquals("LocalDate", DateTimeExtensions.toLocalDate(calendar), DateTimeExtensions.toLocalDate(date));
        Assert.assertEquals("LocalTime", DateTimeExtensions.toLocalTime(calendar), DateTimeExtensions.toLocalTime(date));
        Assert.assertEquals("LocalDateTime", DateTimeExtensions.toLocalDate(calendar), DateTimeExtensions.toLocalDate(date));
        Assert.assertEquals("OffsetTime", DateTimeExtensions.toOffsetTime(calendar), DateTimeExtensions.toOffsetTime(date));
        Assert.assertEquals("OffsetDateTime", DateTimeExtensions.toOffsetDateTime(calendar), DateTimeExtensions.toOffsetDateTime(date));
        Assert.assertEquals("ZonedDateTime", DateTimeExtensions.toZonedDateTime(calendar), DateTimeExtensions.toZonedDateTime(date));
    }
}

