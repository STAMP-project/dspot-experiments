/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static DateHelper.DATE_FORMAT;
import static DateHelper.SQL_DATE_FORMAT;
import static DateHelper.SQL_TIME_FORMAT;
import static DateHelper.TIMESTAMP_FORMAT;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class DateHelperTest {
    public static final String DATE_FORMAT = DATE_FORMAT;

    public static final String TIMESTAMP_FORMAT = TIMESTAMP_FORMAT;

    public static final String SQL_DATE_FORMAT = SQL_DATE_FORMAT;

    public static final String SQL_TIME_FORMAT = SQL_TIME_FORMAT;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSqlDate() {
        long now = System.currentTimeMillis();
        Date date1 = new Date(now);
        Date date2 = DateHelper.parseSqlDate(date1.toString());
        assertSqlDatesEqual(date1, date2);
    }

    @Test
    public void testSqlDateWithLeadingZerosInMonthAndDay() throws Exception {
        // Given
        long expectedDateInMillis = new SimpleDateFormat(DateHelperTest.SQL_DATE_FORMAT).parse("2003-01-04").getTime();
        Date expectedDate = new Date(expectedDateInMillis);
        // When
        Date actualDate = DateHelper.parseSqlDate(expectedDate.toString());
        // Then
        assertSqlDatesEqual(expectedDate, actualDate);
    }

    @Test
    public void testSqlDateWithTrailingZerosInMonthAndDay() throws Exception {
        // Given
        long expectedDateInMillis = new SimpleDateFormat(DateHelperTest.SQL_DATE_FORMAT).parse("2000-10-20").getTime();
        Date expectedDate = new Date(expectedDateInMillis);
        // When
        Date actualDate = DateHelper.parseSqlDate(expectedDate.toString());
        // Then
        assertSqlDatesEqual(expectedDate, actualDate);
    }

    @Test
    public void testSqlDateFailsForInvalidDate() throws Exception {
        // Given
        String invalidDate = "Trust me, I am a date";
        // When
        thrown.expect(RuntimeException.class);
        thrown.expectCause(instanceOf(ParseException.class));
        DateHelper.parseSqlDate(invalidDate);
        // Then
        // No-op
    }

    @Test
    public void testUtilDate() {
        long now = System.currentTimeMillis();
        java.util.Date date1 = new java.util.Date(now);
        java.util.Date date2 = DateHelper.parseDate(new SimpleDateFormat(DateHelperTest.DATE_FORMAT, Locale.US).format(date1));
        Calendar cal1 = Calendar.getInstance(Locale.US);
        cal1.setTimeInMillis(date1.getTime());
        Calendar cal2 = Calendar.getInstance(Locale.US);
        cal2.setTimeInMillis(date2.getTime());
        Assert.assertEquals(cal1.get(Calendar.YEAR), cal2.get(Calendar.YEAR));
        Assert.assertEquals(cal1.get(Calendar.MONTH), cal2.get(Calendar.MONTH));
        Assert.assertEquals(cal1.get(Calendar.DAY_OF_MONTH), cal2.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(cal1.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(cal1.get(Calendar.MINUTE), cal2.get(Calendar.MINUTE));
        Assert.assertEquals(cal1.get(Calendar.SECOND), cal2.get(Calendar.SECOND));
    }

    @Test
    public void testTimestamp() {
        long now = System.currentTimeMillis();
        Timestamp date1 = new Timestamp(now);
        Timestamp date2 = DateHelper.parseTimeStamp(date1.toString());
        assertTimestampsEqual(date1, date2);
    }

    @Test
    public void testTimestampWithLeadingZeros() throws Exception {
        // Given
        Timestamp expectedTimestamp = new Timestamp(new SimpleDateFormat(DateHelperTest.TIMESTAMP_FORMAT).parse("2000-01-02 03:04:05.006").getTime());
        // When
        Timestamp actualTimestamp = DateHelper.parseTimeStamp(expectedTimestamp.toString());
        // Then
        assertTimestampsEqual(expectedTimestamp, actualTimestamp);
    }

    @Test
    public void testTimestampWithTrailingZeros() throws Exception {
        // Given
        Timestamp expectedTimestamp = new Timestamp(new SimpleDateFormat(DateHelperTest.TIMESTAMP_FORMAT).parse("2010-10-20 10:20:30.040").getTime());
        // When
        Timestamp actualTimestamp = DateHelper.parseTimeStamp(expectedTimestamp.toString());
        // Then
        assertTimestampsEqual(expectedTimestamp, actualTimestamp);
    }

    @Test
    public void testTimestampFailsForInvalidValue() throws Exception {
        // Given
        String invalidTimestamp = "Quid temporem est";
        // When
        thrown.expectCause(instanceOf(ParseException.class));
        DateHelper.parseTimeStamp(invalidTimestamp);
        // Then
        // No-op
    }

    @Test
    public void testTime() {
        long now = System.currentTimeMillis();
        Time time1 = new Time(now);
        Time time2 = DateHelper.parseSqlTime(time1.toString());
        assertSqlTimesEqual(time1, time2);
    }

    @Test
    public void testTimeWithLeadingZeros() throws Exception {
        // Given
        Time expectedTime = new Time(new SimpleDateFormat(DateHelperTest.SQL_TIME_FORMAT).parse("01:02:03").getTime());
        // When
        Time actualTime = DateHelper.parseSqlTime(expectedTime.toString());
        // Then
        assertSqlTimesEqual(expectedTime, actualTime);
    }

    @Test
    public void testTimeWithTrailingZeros() throws Exception {
        // Given
        Time expectedTime = new Time(new SimpleDateFormat(DateHelperTest.SQL_TIME_FORMAT).parse("10:20:30").getTime());
        // When
        Time actualTime = DateHelper.parseSqlTime(expectedTime.toString());
        // Then
        assertSqlTimesEqual(expectedTime, actualTime);
    }

    @Test
    public void testTimeFailsForInvalidValue() throws Exception {
        // Given
        String invalidTime = "Time is now";
        // When
        thrown.expectCause(instanceOf(ParseException.class));
        DateHelper.parseSqlTime(invalidTime);
        // Then
        // No-op
    }
}

