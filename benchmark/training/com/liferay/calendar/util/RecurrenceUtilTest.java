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
package com.liferay.calendar.util;


import StringPool.UTC;
import Weekday.FRIDAY;
import Weekday.MONDAY;
import Weekday.SUNDAY;
import Weekday.THURSDAY;
import Weekday.TUESDAY;
import Weekday.WEDNESDAY;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.calendar.model.impl.CalendarBookingImpl;
import com.liferay.calendar.model.impl.CalendarBookingModelImpl;
import com.liferay.calendar.recurrence.Recurrence;
import com.liferay.calendar.recurrence.RecurrenceSerializer;
import com.liferay.calendar.recurrence.Weekday;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 *
 *
 * @author Adam Brandizzi
 */
@PrepareForTest({ CalendarBooking.class, CalendarBookingImpl.class, CalendarBookingModelImpl.class })
@RunWith(PowerMockRunner.class)
@SuppressStaticInitializationFor({ "com.liferay.calendar.model.CalendarBooking", "com.liferay.calendar.model.impl.CalendarBookingImpl", "com.liferay.calendar.model.impl.CalendarBookingModelImpl" })
public class RecurrenceUtilTest {
    @Test
    public void testGetLastCalendarBookingInstance() {
        Calendar lastInstanceStartTimeJCalendar = getJan2016Calendar(23);
        List<CalendarBooking> calendarBookings = getRecurringCalendarBookings(getJan2016Calendar(1), ("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20160105"), getJan2016Calendar(5), null, getJan2016Calendar(17), "RRULE:FREQ=DAILY;INTERVAL=1;COUNT=5", lastInstanceStartTimeJCalendar, "RRULE:FREQ=DAILY;INTERVAL=1;COUNT=3");
        CalendarBooking calendarBooking = RecurrenceUtil.getLastInstanceCalendarBooking(calendarBookings);
        Assert.assertEquals(calendarBooking.getStartTime(), lastInstanceStartTimeJCalendar.getTimeInMillis());
        Recurrence recurrence = calendarBooking.getRecurrenceObj();
        Assert.assertNull(recurrence.getUntilJCalendar());
        Assert.assertTrue(((recurrence.getCount()) == 3));
    }

    @Test
    public void testGetLastCalendarBookingInstanceReturnsUnboundRecurring() {
        Calendar lastInstanceStartTimeJCalendar = getJan2016Calendar(23);
        List<CalendarBooking> calendarBookings = getRecurringCalendarBookings(getJan2016Calendar(1), "RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116", getJan2016Calendar(17), "RRULE:FREQ=DAILY;INTERVAL=1;COUNT=5", lastInstanceStartTimeJCalendar, "RRULE:FREQ=DAILY;INTERVAL=1");
        CalendarBooking calendarBooking = RecurrenceUtil.getLastInstanceCalendarBooking(calendarBookings);
        Assert.assertEquals(calendarBooking.getStartTime(), lastInstanceStartTimeJCalendar.getTimeInMillis());
        Recurrence recurrence = calendarBooking.getRecurrenceObj();
        Assert.assertNull(recurrence.getUntilJCalendar());
        Assert.assertTrue(((recurrence.getCount()) == 0));
    }

    @Test
    public void testGetLastCalendarBookingInstanceWithExceptionOnLast() {
        Calendar lastInstanceStartTimeJCalendar = getJan2016Calendar(1);
        List<CalendarBooking> calendarBookings = getRecurringCalendarBookings(lastInstanceStartTimeJCalendar, ("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20160105"), getJan2016Calendar(5), null);
        CalendarBooking calendarBooking = RecurrenceUtil.getLastInstanceCalendarBooking(calendarBookings);
        Assert.assertEquals(calendarBooking.getStartTime(), lastInstanceStartTimeJCalendar.getTimeInMillis());
        Recurrence recurrence = calendarBooking.getRecurrenceObj();
        assertSameDay(getJan2016Calendar(16), recurrence.getUntilJCalendar());
        Assert.assertTrue(((recurrence.getCount()) == 0));
    }

    @Test
    public void testGetLastCalendarBookingInstanceWithExceptionOnLastDay() {
        Calendar lastInstanceStartTimeJCalendar = getJan2016Calendar(1);
        List<CalendarBooking> calendarBookings = getRecurringCalendarBookings(lastInstanceStartTimeJCalendar, ("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20160116"), getJan2016Calendar(16), null);
        CalendarBooking calendarBooking = RecurrenceUtil.getLastInstanceCalendarBooking(calendarBookings);
        Assert.assertEquals(calendarBooking.getStartTime(), lastInstanceStartTimeJCalendar.getTimeInMillis());
        Recurrence recurrence = calendarBooking.getRecurrenceObj();
        assertSameDay(getJan2016Calendar(16), recurrence.getUntilJCalendar());
        Assert.assertTrue(((recurrence.getCount()) == 0));
    }

    @Test
    public void testInTimeZoneDoesNotUpdateExceptionJCalendarsInSameDay() {
        Recurrence recurrence = RecurrenceSerializer.deserialize(("RRULE:FREQ=DAILY;INTERVAL=1\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20151225,20151231"), RecurrenceUtilTest._utcTimeZone);
        List<Calendar> exceptionJCalendars = recurrence.getExceptionJCalendars();
        Calendar exceptionJCalendar = exceptionJCalendars.get(0);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(25, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        exceptionJCalendar = exceptionJCalendars.get(1);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(31, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 10, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        exceptionJCalendars = recurrence.getExceptionJCalendars();
        exceptionJCalendar = exceptionJCalendars.get(0);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(25, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        exceptionJCalendar = exceptionJCalendars.get(1);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(31, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testInTimeZoneDoesNotUpdateUntilJCalendarInSameDay() {
        Recurrence recurrence = RecurrenceSerializer.deserialize("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116", RecurrenceUtilTest._utcTimeZone);
        Calendar untilJCalendar = recurrence.getUntilJCalendar();
        Assert.assertEquals(2016, untilJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JANUARY, untilJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(16, untilJCalendar.get(Calendar.DAY_OF_MONTH));
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 10, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        untilJCalendar = recurrence.getUntilJCalendar();
        Assert.assertEquals(2016, untilJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JANUARY, untilJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(16, untilJCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testInTimeZoneNullRecurrence() {
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 1, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        Recurrence recurrence = RecurrenceUtil.inTimeZone(null, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        Assert.assertNull(recurrence);
    }

    @Test
    public void testInTimeZoneUpdatesExceptionJCalendars() {
        Recurrence recurrence = RecurrenceSerializer.deserialize(("RRULE:FREQ=DAILY;INTERVAL=1\n" + "EXDATE;TZID=\"UTC\";VALUE=DATE:20151225,20151231"), RecurrenceUtilTest._utcTimeZone);
        List<Calendar> exceptionJCalendars = recurrence.getExceptionJCalendars();
        Calendar exceptionJCalendar = exceptionJCalendars.get(0);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(25, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        exceptionJCalendar = exceptionJCalendars.get(1);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(31, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 1, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        exceptionJCalendars = recurrence.getExceptionJCalendars();
        exceptionJCalendar = exceptionJCalendars.get(0);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(24, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
        exceptionJCalendar = exceptionJCalendars.get(1);
        Assert.assertEquals(2015, exceptionJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.DECEMBER, exceptionJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(30, exceptionJCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testInTimeZoneUpdatesUntilJCalendar() {
        Recurrence recurrence = RecurrenceSerializer.deserialize("RRULE:FREQ=DAILY;INTERVAL=1;UNTIL=20160116", RecurrenceUtilTest._utcTimeZone);
        Calendar untilJCalendar = recurrence.getUntilJCalendar();
        Assert.assertEquals(2016, untilJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JANUARY, untilJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(16, untilJCalendar.get(Calendar.DAY_OF_MONTH));
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 1, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        untilJCalendar = recurrence.getUntilJCalendar();
        Assert.assertEquals(2016, untilJCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.JANUARY, untilJCalendar.get(Calendar.MONTH));
        Assert.assertEquals(15, untilJCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testInTimeZoneUpdatesWeekdays() {
        Recurrence recurrence = RecurrenceSerializer.deserialize("RRULE:FREQ=WEEKLY;INTERVAL=1;BYDAY=MO,WE,FR", RecurrenceUtilTest._utcTimeZone);
        List<Weekday> weekdays = recurrence.getWeekdays();
        Assert.assertTrue(weekdays.toString(), weekdays.contains(MONDAY));
        Assert.assertTrue(weekdays.toString(), weekdays.contains(WEDNESDAY));
        Assert.assertTrue(weekdays.toString(), weekdays.contains(FRIDAY));
        Calendar startTimeJCalendar = JCalendarUtil.getJCalendar(2015, Calendar.DECEMBER, 11, 1, 0, 0, 0, RecurrenceUtilTest._utcTimeZone);
        recurrence = RecurrenceUtil.inTimeZone(recurrence, startTimeJCalendar, RecurrenceUtilTest._losAngelesTimeZone);
        weekdays = recurrence.getWeekdays();
        Assert.assertTrue(weekdays.toString(), weekdays.contains(SUNDAY));
        Assert.assertTrue(weekdays.toString(), weekdays.contains(TUESDAY));
        Assert.assertTrue(weekdays.toString(), weekdays.contains(THURSDAY));
    }

    private static final TimeZone _losAngelesTimeZone = TimeZone.getTimeZone("America/Los_Angeles");

    private static final TimeZone _utcTimeZone = TimeZone.getTimeZone(UTC);
}

