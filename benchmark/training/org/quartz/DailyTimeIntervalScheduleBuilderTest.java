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
package org.quartz;


import IntervalUnit.HOUR;
import IntervalUnit.MINUTE;
import IntervalUnit.SECOND;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.spi.OperableTrigger;


/**
 * Unit test for DailyTimeIntervalScheduleBuilder.
 *
 * @author Zemian Deng <saltnlight5@gmail.com>
 */
public class DailyTimeIntervalScheduleBuilderTest extends TestCase {
    public void testScheduleActualTrigger() throws Exception {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(DailyTimeIntervalScheduleBuilderTest.MyJob.class).build();
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInSeconds(3)).build();
        scheduler.scheduleJob(job, trigger);// We are not verify anything other than just run through the scheduler.

        scheduler.shutdown();
    }

    public void testScheduleInMiddleOfDailyInterval() throws Exception {
        Calendar currTime = Calendar.getInstance();
        int currHour = currTime.get(Calendar.HOUR);
        // this test won't work out well in the early hours, where 'backing up' would give previous day,
        // or where daylight savings transitions could occur and confuse the assertions...
        if (currHour < 3)
            return;

        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(DailyTimeIntervalScheduleBuilderTest.MyJob.class).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().startingDailyAt(TimeOfDay.hourAndMinuteOfDay(2, 15)).withIntervalInMinutes(5)).startAt(currTime.getTime()).build();
        scheduler.scheduleJob(job, trigger);
        trigger = scheduler.getTrigger(trigger.getKey());
        System.out.println(("testScheduleInMiddleOfDailyInterval: currTime = " + (currTime.getTime())));
        System.out.println(("testScheduleInMiddleOfDailyInterval: computed first fire time = " + (trigger.getNextFireTime())));
        Assert.assertTrue("First fire time is not after now!", trigger.getNextFireTime().after(currTime.getTime()));
        Date startTime = DateBuilder.todayAt(2, 15, 0);
        job = JobBuilder.newJob(DailyTimeIntervalScheduleBuilderTest.MyJob.class).build();
        trigger = TriggerBuilder.newTrigger().withIdentity("test2").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().startingDailyAt(TimeOfDay.hourAndMinuteOfDay(2, 15)).withIntervalInMinutes(5)).startAt(startTime).build();
        scheduler.scheduleJob(job, trigger);
        trigger = scheduler.getTrigger(trigger.getKey());
        System.out.println(("testScheduleInMiddleOfDailyInterval: startTime = " + startTime));
        System.out.println(("testScheduleInMiddleOfDailyInterval: computed first fire time = " + (trigger.getNextFireTime())));
        Assert.assertTrue("First fire time is not after now!", trigger.getNextFireTime().equals(startTime));
        scheduler.shutdown();
    }

    public void testHourlyTrigger() {
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInHours(1)).build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("DEFAULT", trigger.getKey().getGroup());
        Assert.assertEquals(HOUR, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(1, trigger.getRepeatInterval());
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(48, fireTimes.size());
    }

    public void testMinutelyTriggerWithTimeOfDay() {
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test", "group").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(72).startingDailyAt(TimeOfDay.hourAndMinuteOfDay(8, 0)).endingDailyAt(TimeOfDay.hourAndMinuteOfDay(17, 0)).onMondayThroughFriday()).build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("group", trigger.getKey().getGroup());
        Assert.assertEquals(true, ((new Date().getTime()) >= (trigger.getStartTime().getTime())));
        Assert.assertEquals(true, (null == (trigger.getEndTime())));
        Assert.assertEquals(MINUTE, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(72, trigger.getRepeatInterval());
        Assert.assertEquals(new TimeOfDay(8, 0), trigger.getStartTimeOfDay());
        Assert.assertEquals(new TimeOfDay(17, 0), trigger.getEndTimeOfDay());
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(48, fireTimes.size());
    }

    public void testSecondlyTriggerWithStartAndEndTime() {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        Date endTime = DateBuilder.dateOf(0, 0, 0, 2, 1, 2011);
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test", "test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInSeconds(121).startingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(10, 0, 0)).endingDailyAt(TimeOfDay.hourMinuteAndSecondOfDay(23, 59, 59)).onSaturdayAndSunday()).startAt(startTime).endAt(endTime).build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("test", trigger.getKey().getGroup());
        Assert.assertEquals(true, ((startTime.getTime()) == (trigger.getStartTime().getTime())));
        Assert.assertEquals(true, ((endTime.getTime()) == (trigger.getEndTime().getTime())));
        Assert.assertEquals(SECOND, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(121, trigger.getRepeatInterval());
        Assert.assertEquals(new TimeOfDay(10, 0, 0), trigger.getStartTimeOfDay());
        Assert.assertEquals(new TimeOfDay(23, 59, 59), trigger.getEndTimeOfDay());
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(48, fireTimes.size());
    }

    public void testRepeatCountTrigger() {
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInHours(1).withRepeatCount(9)).build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("DEFAULT", trigger.getKey().getGroup());
        Assert.assertEquals(HOUR, trigger.getRepeatIntervalUnit());
        Assert.assertEquals(1, trigger.getRepeatInterval());
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(10, fireTimes.size());
    }

    public void testEndingAtAfterCount() {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(15).startingDailyAt(TimeOfDay.hourAndMinuteOfDay(8, 0)).endingDailyAfterCount(12)).startAt(startTime).build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("DEFAULT", trigger.getKey().getGroup());
        Assert.assertEquals(MINUTE, trigger.getRepeatIntervalUnit());
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(10, 45, 0, 4, 1, 2011), fireTimes.get(47));
        Assert.assertEquals(new TimeOfDay(10, 45), trigger.getEndTimeOfDay());
    }

    public void testEndingAtAfterCountOf1() {
        Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
        DailyTimeIntervalTrigger trigger = TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(15).startingDailyAt(TimeOfDay.hourAndMinuteOfDay(8, 0)).endingDailyAfterCount(1)).startAt(startTime).forJob("testJob", "testJobGroup").build();
        Assert.assertEquals("test", trigger.getKey().getName());
        Assert.assertEquals("DEFAULT", trigger.getKey().getGroup());
        Assert.assertEquals(MINUTE, trigger.getRepeatIntervalUnit());
        validateTrigger(trigger);
        List<Date> fireTimes = TriggerUtils.computeFireTimes(((OperableTrigger) (trigger)), null, 48);
        Assert.assertEquals(48, fireTimes.size());
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 1, 1, 2011), fireTimes.get(0));
        Assert.assertEquals(DateBuilder.dateOf(8, 0, 0, 17, 2, 2011), fireTimes.get(47));
        Assert.assertEquals(new TimeOfDay(8, 0), trigger.getEndTimeOfDay());
    }

    public void testEndingAtAfterCountOf0() {
        try {
            Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
            TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(15).startingDailyAt(TimeOfDay.hourAndMinuteOfDay(8, 0)).endingDailyAfterCount(0)).startAt(startTime).build();
            TestCase.fail("We should not accept endingDailyAfterCount(0)");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        try {
            Date startTime = DateBuilder.dateOf(0, 0, 0, 1, 1, 2011);
            TriggerBuilder.newTrigger().withIdentity("test").withSchedule(DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule().withIntervalInMinutes(15).endingDailyAfterCount(1)).startAt(startTime).build();
            TestCase.fail("We should not accept endingDailyAfterCount(x) without first setting startingDailyAt.");
        } catch (IllegalArgumentException e) {
            // Expected.
        }
    }

    /**
     * An empty job for testing purpose.
     */
    public static class MyJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            // 
        }
    }
}

