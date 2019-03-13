/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.quartz.impl.triggers;


import DailyTimeIntervalTrigger.REPEAT_INDEFINITELY;
import DateBuilder.FRIDAY;
import DateBuilder.IntervalUnit.MINUTE;
import DateBuilder.MONDAY;
import DateBuilder.THURSDAY;
import DateBuilder.TUESDAY;
import DateBuilder.WEDNESDAY;
import IntervalUnit.DAY;
import IntervalUnit.HOUR;
import IntervalUnit.SECOND;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DateBuilder;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.TimeOfDay;
import org.quartz.TriggerUtils;
import org.quartz.impl.calendar.CronCalendar;


/**
 * Unit test for {@link DailyTimeIntervalTriggerImpl}.
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
 */
public class DailyTimeIntervalTriggerImplTest extends TestCase {
    public void testNormalExample() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(11, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(72);// this interval will give three firings per day (8:00, 9:12, and 10:24)

        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(10, 24, 0, 16, 1, 2011), fireTimes.get(47));
    }

    public void testQuartzCalendarExclusion() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(new TimeOfDay(8, 0));
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        CronCalendar cronCal = new CronCalendar("* * 9-12 * * ?");// exclude 9-12

        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, cronCal, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(13, 0, 0, 1, 1, 2011), fireTimes.get(1));
        Assert.assertEquals(DateBuilder.dateOf(23, 0, 0, 4, 1, 2011), fireTimes.get(47));
    }

    public void testValidateTimeOfDayOrder() throws Exception {
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTimeOfDay(new TimeOfDay(12, 0, 0));
        trigger.setEndTimeOfDay(new TimeOfDay(8, 0, 0));
        try {
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when time of day is not in order.");
        } catch (SchedulerException e) {
            // expected.
        }
    }

    public void testValidateInterval() throws Exception {
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setName("test");
        trigger.setGroup("test");
        trigger.setJobKey(JobKey.jobKey("test"));
        trigger.setRepeatIntervalUnit(HOUR);
        trigger.setRepeatInterval(25);
        try {
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when interval is greater than 24 hours.");
        } catch (SchedulerException e) {
            // expected.
        }
        trigger.setRepeatIntervalUnit(IntervalUnit.MINUTE);
        trigger.setRepeatInterval((60 * 25));
        try {
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when interval is greater than 24 hours.");
        } catch (SchedulerException e) {
            // expected.
        }
        trigger.setRepeatIntervalUnit(SECOND);
        trigger.setRepeatInterval(((60 * 60) * 25));
        try {
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when interval is greater than 24 hours.");
        } catch (SchedulerException e) {
            // expected.
        }
        try {
            trigger.setRepeatIntervalUnit(DAY);
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when interval unit > HOUR.");
        } catch (Exception e) {
            // expected.
        }
        try {
            trigger.setRepeatIntervalUnit(SECOND);
            trigger.setRepeatInterval(0);
            trigger.validate();
            TestCase.fail("Trigger should be invalidate when interval is zero.");
        } catch (Exception e) {
            // expected.
        }
    }

    public void testStartTimeWithoutStartTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(23, 0, 0, 2, 1, 2011), fireTimes.get(47));
    }

    public void testEndTimeWithoutEndTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(22, 0, 0, 2, 1, 2011);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(47, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(22, 0, 0, 2, 1, 2011), fireTimes.get(46));
    }

    public void testStartTimeBeforeStartTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(23, 0, 0, 3, 1, 2011), fireTimes.get(47));
    }

    public void testStartTimeBeforeStartTimeOfDayOnInvalidDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);// Jan 1, 2011 was a saturday...

        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        Set<Integer> daysOfWeek = new HashSet<Integer>();
        daysOfWeek.add(MONDAY);
        daysOfWeek.add(TUESDAY);
        daysOfWeek.add(WEDNESDAY);
        daysOfWeek.add(THURSDAY);
        daysOfWeek.add(FRIDAY);
        trigger.setDaysOfWeek(daysOfWeek);
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 3, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(6, 0, 0, 22, 5, 2010)));
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 3, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(23, 0, 0, 5, 1, 2011), fireTimes.get(47));
    }

    public void testStartTimeAfterStartTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(9, 23, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(10, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(9, 0, 0, 4, 1, 2011), fireTimes.get(47));
    }

    public void testEndTimeBeforeEndTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(16, 0, 0, 2, 1, 2011);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(35, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 1, 1, 2011), fireTimes.get(17));
        Assert.assertEquals(DateBuilder.dateOf(16, 0, 0, 2, 1, 2011), fireTimes.get(34));
    }

    public void testEndTimeAfterEndTimeOfDay() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(18, 0, 0, 2, 1, 2011);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(36, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 1, 1, 2011), fireTimes.get(17));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 2, 1, 2011), fireTimes.get(35));
    }

    public void testTimeOfDayWithStartTime() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 1, 1, 2011), fireTimes.get(9));// The 10th hours is the end of day.

        Assert.assertEquals(DateBuilder.dateOf(15, 0, 0, 5, 1, 2011), fireTimes.get(47));
    }

    public void testTimeOfDayWithEndTime() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(0, 0, 0, 4, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(30, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 1, 1, 2011), fireTimes.get(9));// The 10th hours is the end of day.

        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 3, 1, 2011), fireTimes.get(29));
    }

    public void testTimeOfDayWithEndTime2() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 23, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(23, 59, 59);// edge case when endTime is last second of day, which is default too.

        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 23, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(23, 23, 0, 3, 1, 2011), fireTimes.get(47));
    }

    public void testAllDaysOfTheWeek() throws Exception {
        Set<Integer> daysOfWeek = DailyTimeIntervalScheduleBuilder.ALL_DAYS_OF_THE_WEEK;
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);// SAT

        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setDaysOfWeek(daysOfWeek);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(17, 0, 0, 1, 1, 2011), fireTimes.get(9));// The 10th hours is the end of day.

        Assert.assertEquals(DateBuilder.dateOf(15, 0, 0, 5, 1, 2011), fireTimes.get(47));
    }

    public void testMonThroughFri() throws Exception {
        Set<Integer> daysOfWeek = DailyTimeIntervalScheduleBuilder.MONDAY_THROUGH_FRIDAY;
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);// SAT(7)

        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setDaysOfWeek(daysOfWeek);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 3, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(Calendar.MONDAY, getDayOfWeek(fireTimes.get(0)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 4, 1, 2011), fireTimes.get(10));
        Assert.assertEquals(Calendar.TUESDAY, getDayOfWeek(fireTimes.get(10)));
        Assert.assertEquals(DateBuilder.dateOf(15, 0, 0, 7, 1, 2011), fireTimes.get(47));
        Assert.assertEquals(Calendar.FRIDAY, getDayOfWeek(fireTimes.get(47)));
    }

    public void testSatAndSun() throws Exception {
        Set<Integer> daysOfWeek = DailyTimeIntervalScheduleBuilder.SATURDAY_AND_SUNDAY;
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);// SAT(7)

        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setDaysOfWeek(daysOfWeek);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(Calendar.SATURDAY, getDayOfWeek(fireTimes.get(0)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 2, 1, 2011), fireTimes.get(10));
        Assert.assertEquals(Calendar.SUNDAY, getDayOfWeek(fireTimes.get(10)));
        Assert.assertEquals(DateBuilder.dateOf(15, 0, 0, 15, 1, 2011), fireTimes.get(47));
        Assert.assertEquals(Calendar.SATURDAY, getDayOfWeek(fireTimes.get(47)));
    }

    public void testMonOnly() throws Exception {
        Set<Integer> daysOfWeek = new HashSet<Integer>();
        daysOfWeek.add(Calendar.MONDAY);
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);// SAT(7)

        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(17, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setDaysOfWeek(daysOfWeek);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(60);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 3, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(Calendar.MONDAY, getDayOfWeek(fireTimes.get(0)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 10, 1, 2011), fireTimes.get(10));
        Assert.assertEquals(Calendar.MONDAY, getDayOfWeek(fireTimes.get(10)));
        Assert.assertEquals(DateBuilder.dateOf(15, 0, 0, 31, 1, 2011), fireTimes.get(47));
        Assert.assertEquals(Calendar.MONDAY, getDayOfWeek(fireTimes.get(47)));
    }

    public void testTimeOfDayWithEndTimeOddInterval() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(0, 0, 0, 4, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(10, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setEndTime(endTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(23);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(18, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(9, 55, 0, 1, 1, 2011), fireTimes.get(5));
        Assert.assertEquals(DateBuilder.dateOf(9, 55, 0, 3, 1, 2011), fireTimes.get(17));
    }

    public void testHourInterval() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(13, 0, 0, 15, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 1, 15);
        TimeOfDay endTimeOfDay = new TimeOfDay(16, 1, 15);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTime(endTime);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(DateBuilder.IntervalUnit.HOUR);
        trigger.setRepeatInterval(2);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 1, 15, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(12, 1, 15, 10, 1, 2011), fireTimes.get(47));
    }

    public void testSecondInterval() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 2);
        TimeOfDay endTimeOfDay = new TimeOfDay(13, 30, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(DateBuilder.IntervalUnit.SECOND);
        trigger.setRepeatInterval(72);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 2, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(8, 56, 26, 1, 1, 2011), fireTimes.get(47));
    }

    public void testRepeatCountInf() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(11, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(72);
        // Setting this (which is default) should make the trigger just as normal one.
        trigger.setRepeatCount(REPEAT_INDEFINITELY);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(10, 24, 0, 16, 1, 2011), fireTimes.get(47));
    }

    public void testRepeatCount() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(11, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(72);
        trigger.setRepeatCount(7);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(8, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(9, 12, 0, 3, 1, 2011), fireTimes.get(7));
    }

    public void testRepeatCount0() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(11, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(MINUTE);
        trigger.setRepeatInterval(72);
        trigger.setRepeatCount(0);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(trigger, null, 48);
        Assert.assertEquals(1, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
    }

    public void testGetFireTime() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(13, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(DateBuilder.IntervalUnit.HOUR);
        trigger.setRepeatInterval(1);
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(7, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(7, 59, 59, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(9, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(10, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(9, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(13, 0, 0, 1, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(12, 59, 59, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 2, 1, 2011), trigger.getFireTimeAfter(DateBuilder.dateOf(13, 0, 0, 1, 1, 2011)));
    }

    public void testGetFireTimeWithDateBeforeStartTime() throws Exception {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2012);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(13, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(DateBuilder.IntervalUnit.HOUR);
        trigger.setRepeatInterval(1);
        // NOTE that if you pass a date past the startTime, you will get the first firing on or after the startTime back!
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(0, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(7, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(7, 59, 59, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(9, 0, 0, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(12, 59, 59, 1, 1, 2011)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(13, 0, 0, 1, 1, 2011)));
        // Now try some test times at or after startTime
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(0, 0, 0, 1, 1, 2012)));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 2, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(13, 0, 0, 1, 1, 2012)));
    }

    public void testGetFireTimeWhenStartTimeAndTimeOfDayIsSame() throws Exception {
        // A test case for QTZ-369
        Date startTime = DateBuilder.dateOf(8, 0, 0, 1, 1, 2012);
        TimeOfDay startTimeOfDay = new TimeOfDay(8, 0, 0);
        TimeOfDay endTimeOfDay = new TimeOfDay(13, 0, 0);
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl();
        trigger.setStartTime(startTime);
        trigger.setStartTimeOfDay(startTimeOfDay);
        trigger.setEndTimeOfDay(endTimeOfDay);
        trigger.setRepeatIntervalUnit(DateBuilder.IntervalUnit.HOUR);
        trigger.setRepeatInterval(1);
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getFireTimeAfter(DateBuilder.dateOf(0, 0, 0, 1, 1, 2012)));
    }

    public void testExtraConstructors() throws Exception {
        // A test case for QTZ-389 - some extra constructors didn't set all parameters
        DailyTimeIntervalTriggerImpl trigger = new DailyTimeIntervalTriggerImpl("triggerName", "triggerGroup", "jobName", "jobGroup", DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), null, new TimeOfDay(8, 0, 0), new TimeOfDay(17, 0, 0), IntervalUnit.HOUR, 1);
        Assert.assertEquals("triggerName", trigger.getName());
        Assert.assertEquals("triggerGroup", trigger.getGroup());
        Assert.assertEquals("jobName", trigger.getJobName());
        Assert.assertEquals("jobGroup", trigger.getJobGroup());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getStartTime());
        Assert.assertEquals(null, trigger.getEndTime());
        Assert.assertEquals(new TimeOfDay(8, 0, 0), trigger.getStartTimeOfDay());
        Assert.assertEquals(new TimeOfDay(17, 0, 0), trigger.getEndTimeOfDay());
        Assert.assertEquals(HOUR, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(1, trigger.getRepeatInterval());
        trigger = new DailyTimeIntervalTriggerImpl("triggerName", "triggerGroup", DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), null, new TimeOfDay(8, 0, 0), new TimeOfDay(17, 0, 0), IntervalUnit.HOUR, 1);
        Assert.assertEquals("triggerName", trigger.getName());
        Assert.assertEquals("triggerGroup", trigger.getGroup());
        Assert.assertEquals(null, trigger.getJobName());
        Assert.assertEquals("DEFAULT", trigger.getJobGroup());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2012), trigger.getStartTime());
        Assert.assertEquals(null, trigger.getEndTime());
        Assert.assertEquals(new TimeOfDay(8, 0, 0), trigger.getStartTimeOfDay());
        Assert.assertEquals(new TimeOfDay(17, 0, 0), trigger.getEndTimeOfDay());
        Assert.assertEquals(HOUR, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(1, trigger.getRepeatInterval());
    }
}

