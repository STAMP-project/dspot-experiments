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


import JCalendarUtil.HOUR;
import TimeZoneUtil.GMT;
import com.liferay.portal.kernel.util.CalendarFactoryUtil;
import java.util.Calendar;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;

import static JCalendarUtil.HOUR;


/**
 *
 *
 * @author Adam Brandizzi
 */
public class JCalendarUtilTest {
    @Test
    public void testGetDSTShiftAtLosAngelesDuringDST() {
        Calendar jCalendar1 = JCalendarUtil.getJCalendar(2012, Calendar.MAY, 1, 12, 0, 0, 0, GMT);
        Calendar jCalendar2 = JCalendarUtil.getJCalendar(2013, Calendar.JULY, 2, 12, 0, 0, 0, GMT);
        int shift = JCalendarUtil.getDSTShift(jCalendar1, jCalendar2, JCalendarUtilTest._losAngelesTimeZone);
        Assert.assertEquals(0, shift);
    }

    @Test
    public void testGetDSTShiftAtLosAngelesDuringNoDST() {
        Calendar jCalendar1 = JCalendarUtil.getJCalendar(2013, Calendar.DECEMBER, 1, 12, 0, 0, 0, GMT);
        Calendar jCalendar2 = JCalendarUtil.getJCalendar(2013, Calendar.JANUARY, 2, 12, 0, 0, 0, GMT);
        int shift = JCalendarUtil.getDSTShift(jCalendar1, jCalendar2, JCalendarUtilTest._losAngelesTimeZone);
        Assert.assertEquals(0, shift);
    }

    @Test
    public void testGetDSTShiftAtLosAngelesFromDSTToNoDST() {
        Calendar jCalendar1 = JCalendarUtil.getJCalendar(2013, Calendar.JULY, 1, 12, 0, 0, 0, GMT);
        Calendar jCalendar2 = JCalendarUtil.getJCalendar(2013, Calendar.JANUARY, 1, 12, 0, 0, 0, GMT);
        int shift = JCalendarUtil.getDSTShift(jCalendar1, jCalendar2, JCalendarUtilTest._losAngelesTimeZone);
        Assert.assertEquals(HOUR, shift);
    }

    @Test
    public void testGetDSTShiftAtLosAngelesFromNoDSTToDST() {
        Calendar jCalendar1 = JCalendarUtil.getJCalendar(2013, Calendar.JANUARY, 1, 12, 0, 0, 0, GMT);
        Calendar jCalendar2 = JCalendarUtil.getJCalendar(2013, Calendar.JULY, 1, 12, 0, 0, 0, GMT);
        int shift = JCalendarUtil.getDSTShift(jCalendar1, jCalendar2, JCalendarUtilTest._losAngelesTimeZone);
        Assert.assertEquals(((-1) * (HOUR)), shift);
    }

    @Test
    public void testGetJCalendar() {
        Calendar losAngelesJCalendar = CalendarFactoryUtil.getCalendar(randomYear(), randomMonth(), randomDayOfMonth(), randomHour(), randomMinute(), randomSecond(), randomMillisecond(), JCalendarUtilTest._losAngelesTimeZone);
        Calendar madridJCalendar = JCalendarUtil.getJCalendar(losAngelesJCalendar, JCalendarUtilTest._madridTimeZone);
        Assert.assertEquals(JCalendarUtilTest._madridTimeZone, madridJCalendar.getTimeZone());
        Assert.assertEquals(losAngelesJCalendar.getTimeInMillis(), madridJCalendar.getTimeInMillis());
    }

    @Test
    public void testIsSameDayOfWeek() {
        Calendar jCalendar1 = CalendarFactoryUtil.getCalendar(2015, Calendar.DECEMBER, 4);
        Calendar jCalendar2 = CalendarFactoryUtil.getCalendar(2015, Calendar.DECEMBER, 11);
        Assert.assertTrue(JCalendarUtil.isSameDayOfWeek(jCalendar1, jCalendar2));
        jCalendar2 = CalendarFactoryUtil.getCalendar(2015, Calendar.DECEMBER, 12);
        Assert.assertFalse(JCalendarUtil.isSameDayOfWeek(jCalendar1, jCalendar2));
    }

    @Test
    public void testMergeJCalendar() {
        Calendar dateJCalendar = CalendarFactoryUtil.getCalendar(randomYear(), randomMonth(), randomDayOfMonth(), randomHour(), randomMinute(), randomSecond(), randomMillisecond(), JCalendarUtilTest._losAngelesTimeZone);
        Calendar timeJCalendar = CalendarFactoryUtil.getCalendar(randomYear(), randomMonth(), randomDayOfMonth(), randomHour(), randomMinute(), randomSecond(), randomMillisecond(), JCalendarUtilTest._madridTimeZone);
        Calendar jCalendar = JCalendarUtil.mergeJCalendar(dateJCalendar, timeJCalendar, JCalendarUtilTest._calcuttaTimeZone);
        Assert.assertEquals(dateJCalendar.get(Calendar.YEAR), jCalendar.get(Calendar.YEAR));
        Assert.assertEquals(dateJCalendar.get(Calendar.MONTH), jCalendar.get(Calendar.MONTH));
        Assert.assertEquals(dateJCalendar.get(Calendar.DAY_OF_MONTH), jCalendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(timeJCalendar.get(Calendar.HOUR), jCalendar.get(Calendar.HOUR));
        Assert.assertEquals(timeJCalendar.get(Calendar.MINUTE), jCalendar.get(Calendar.MINUTE));
        Assert.assertEquals(timeJCalendar.get(Calendar.SECOND), jCalendar.get(Calendar.SECOND));
        Assert.assertEquals(timeJCalendar.get(Calendar.MILLISECOND), jCalendar.get(Calendar.MILLISECOND));
        Assert.assertEquals(timeJCalendar.get(Calendar.AM_PM), jCalendar.get(Calendar.AM_PM));
        Assert.assertEquals(JCalendarUtilTest._calcuttaTimeZone, jCalendar.getTimeZone());
    }

    private static final TimeZone _calcuttaTimeZone = TimeZone.getTimeZone("Asia/Calcutta");

    private static final TimeZone _losAngelesTimeZone = TimeZone.getTimeZone("America/Los_Angeles");

    private static final TimeZone _madridTimeZone = TimeZone.getTimeZone("Europe/Madrid");
}

