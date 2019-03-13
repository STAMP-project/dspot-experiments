/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.io.gcal.internal.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import junit.framework.Assert;
import org.apache.commons.lang.math.LongRange;
import org.junit.Test;


/**
 *
 *
 * @author Thomas.Eichstaedt-Engelen
 */
public class TimeRangeCalendarTest {
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss:SSS");

    TimeRangeCalendar calendar;

    @Test
    public void testIsTimeIncluded() throws ParseException {
        Date startTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 16:00:00:000");
        Date endTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:15:00:000");
        Date included = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:00:00:000");
        Date excluded_before = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 15:59:59:999");
        Date excluded_after = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:15:00:001");
        LongRange timeRange = new LongRange(startTime.getTime(), endTime.getTime());
        calendar.addTimeRange(timeRange);
        Assert.assertEquals(true, calendar.isTimeIncluded(included.getTime()));
        Assert.assertEquals(false, calendar.isTimeIncluded(excluded_before.getTime()));
        Assert.assertEquals(false, calendar.isTimeIncluded(excluded_after.getTime()));
    }

    @Test
    public void testGetNextIncludedTime() throws ParseException {
        Date startTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 16:00:00:000");
        Date endTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:15:00:000");
        Date included = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 17:23:21:000");
        Date expected = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:15:00:001");
        LongRange timeRange = new LongRange(startTime.getTime(), endTime.getTime());
        calendar.addTimeRange(timeRange);
        Assert.assertEquals(expected.getTime(), calendar.getNextIncludedTime(included.getTime()));
    }

    @Test
    public void testAddRemoveExcludedDate() throws ParseException {
        Date startTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 16:00:00:000");
        Date endTime = TimeRangeCalendarTest.DATE_FORMATTER.parse("04.04.2012 19:15:00:000");
        LongRange timeRange_1 = new LongRange(startTime.getTime(), endTime.getTime());
        LongRange timeRange_2 = new LongRange(startTime.getTime(), endTime.getTime());
        calendar.addTimeRange(timeRange_2);
        calendar.addTimeRange(timeRange_1);
        Assert.assertEquals(2, calendar.getExcludedRanges().size());
        Assert.assertEquals(timeRange_1, calendar.getExcludedRanges().get(1));
        Assert.assertEquals(timeRange_2, calendar.getExcludedRanges().get(0));
        calendar.removeExcludedDate(timeRange_1);
        Assert.assertEquals(1, calendar.getExcludedRanges().size());
        Assert.assertEquals(timeRange_2, calendar.getExcludedRanges().get(0));
    }
}

