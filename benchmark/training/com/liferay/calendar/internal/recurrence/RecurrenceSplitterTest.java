/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.calendar.internal.recurrence;


import StringPool.UTC;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class RecurrenceSplitterTest {
    @Test
    public void testSplitRecurrence() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;UNTIL=20170109;INTERVAL=1");
        assertSecondRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithCount() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1;COUNT=20");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;COUNT=9;INTERVAL=1");
        assertSecondRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;COUNT=11;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithCountAndSplitDateBeforeStartDate() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;COUNT=5;INTERVAL=1");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertNotSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;COUNT=5;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithCountBeforeSplitDate() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1;COUNT=5");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertNotSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;COUNT=5;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithExDate() {
        Recurrence recurrence = getRecurrence(("RRULE:FREQ=DAILY;INTERVAL=1\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20170108,20170112"));
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertSplit(recurrenceSplit);
        Assert.assertEquals(("RRULE:FREQ=DAILY;UNTIL=20170109;INTERVAL=1\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20170108"), RecurrenceSerializer.serialize(recurrenceSplit.getFirstRecurrence()));
        Assert.assertEquals(("RRULE:FREQ=DAILY;INTERVAL=1\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20170112"), RecurrenceSerializer.serialize(recurrenceSplit.getSecondRecurrence()));
    }

    @Test
    public void testSplitRecurrenceWithSplitDateAfterUntilDate() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20170108");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertNotSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;UNTIL=20170108;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithSplitDateBeforeStartDate() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1");
        Calendar startTimeJCalendar = getJCalendar(4);
        Calendar splitTimeJCalendar = getJCalendar(1);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertNotSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;INTERVAL=1");
    }

    @Test
    public void testSplitRecurrenceWithUntilDate() {
        Recurrence recurrence = getRecurrence("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20170131");
        Calendar startTimeJCalendar = getJCalendar(1);
        Calendar splitTimeJCalendar = getJCalendar(10);
        RecurrenceSplit recurrenceSplit = _recurrenceSplitter.split(recurrence, startTimeJCalendar, splitTimeJCalendar);
        assertSplit(recurrenceSplit);
        assertFirstRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;UNTIL=20170109;INTERVAL=1");
        assertSecondRecurrenceEquals(recurrenceSplit, "RRULE:FREQ=DAILY;UNTIL=20170131;INTERVAL=1");
    }

    private static final TimeZone _utcTimeZone = TimeZone.getTimeZone(UTC);

    private RecurrenceSplitter _recurrenceSplitter;
}

