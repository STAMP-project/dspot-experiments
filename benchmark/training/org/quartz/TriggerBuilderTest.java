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


import IntervalUnit.WEEK;
import JobKey.DEFAULT_GROUP;
import java.util.Date;
import junit.framework.TestCase;

import static Trigger.DEFAULT_PRIORITY;


/**
 * Test TriggerBuilder functionality
 */
public class TriggerBuilderTest extends TestCase {
    @SuppressWarnings("deprecation")
    public static class TestStatefulJob implements StatefulJob {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public static class TestJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    @DisallowConcurrentExecution
    @PersistJobDataAfterExecution
    public static class TestAnnotatedJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public void testTriggerBuilder() throws Exception {
        Trigger trigger = TriggerBuilder.newTrigger().build();
        TestCase.assertTrue("Expected non-null trigger name ", ((trigger.getKey().getName()) != null));
        TestCase.assertTrue(("Unexpected trigger group: " + (trigger.getKey().getGroup())), trigger.getKey().getGroup().equals(DEFAULT_GROUP));
        TestCase.assertTrue(("Unexpected job key: " + (trigger.getJobKey())), ((trigger.getJobKey()) == null));
        TestCase.assertTrue(("Unexpected job description: " + (trigger.getDescription())), ((trigger.getDescription()) == null));
        TestCase.assertTrue(("Unexpected trigger priortiy: " + (trigger.getPriority())), ((trigger.getPriority()) == (DEFAULT_PRIORITY)));
        TestCase.assertTrue(("Unexpected start-time: " + (trigger.getStartTime())), ((trigger.getStartTime()) != null));
        TestCase.assertTrue(("Unexpected end-time: " + (trigger.getEndTime())), ((trigger.getEndTime()) == null));
        Date stime = DateBuilder.evenSecondDateAfterNow();
        trigger = TriggerBuilder.newTrigger().withIdentity("t1").withDescription("my description").withPriority(2).endAt(DateBuilder.futureDate(10, WEEK)).startAt(stime).build();
        TestCase.assertTrue(("Unexpected trigger name " + (trigger.getKey().getName())), trigger.getKey().getName().equals("t1"));
        TestCase.assertTrue(("Unexpected trigger group: " + (trigger.getKey().getGroup())), trigger.getKey().getGroup().equals(DEFAULT_GROUP));
        TestCase.assertTrue(("Unexpected job key: " + (trigger.getJobKey())), ((trigger.getJobKey()) == null));
        TestCase.assertTrue(("Unexpected job description: " + (trigger.getDescription())), trigger.getDescription().equals("my description"));
        TestCase.assertTrue(("Unexpected trigger priortiy: " + trigger), ((trigger.getPriority()) == 2));
        TestCase.assertTrue(("Unexpected start-time: " + (trigger.getStartTime())), trigger.getStartTime().equals(stime));
        TestCase.assertTrue(("Unexpected end-time: " + (trigger.getEndTime())), ((trigger.getEndTime()) != null));
    }

    /**
     * QTZ-157
     */
    public void testTriggerBuilderWithEndTimePriorCurrrentTime() throws Exception {
        TriggerBuilder.newTrigger().withIdentity("some trigger name", "some trigger group").forJob("some job name", "some job group").startAt(new Date(((System.currentTimeMillis()) - 200000000))).endAt(new Date(((System.currentTimeMillis()) - 100000000))).withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?")).build();
    }
}

