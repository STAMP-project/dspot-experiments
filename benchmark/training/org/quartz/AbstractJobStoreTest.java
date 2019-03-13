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


import Trigger.CompletedExecutionInstruction.SET_TRIGGER_ERROR;
import TriggerState.ERROR;
import TriggerState.NORMAL;
import java.util.Date;
import java.util.List;
import java.util.Set;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.simpl.CascadingClassLoadHelper;


/**
 * Unit test for JobStores.  These tests were submitted by Johannes Zillmann
 * as part of issue QUARTZ-306.
 */
public abstract class AbstractJobStoreTest extends TestCase {
    private JobStore fJobStore;

    private JobDetailImpl fJobDetail;

    private AbstractJobStoreTest.SampleSignaler fSignaler;

    public void testStoreAndRetrieveJobs() throws Exception {
        SchedulerSignaler schedSignaler = new AbstractJobStoreTest.SampleSignaler();
        ClassLoadHelper loadHelper = new CascadingClassLoadHelper();
        loadHelper.initialize();
        JobStore store = createJobStore("testStoreAndRetrieveJobs");
        store.initialize(loadHelper, schedSignaler);
        // Store jobs.
        for (int i = 0; i < 10; i++) {
            String group = (i < 5) ? "a" : "b";
            JobDetail job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity(("job" + i), group).build();
            store.storeJob(job, false);
        }
        // Retrieve jobs.
        for (int i = 0; i < 10; i++) {
            String group = (i < 5) ? "a" : "b";
            JobKey jobKey = JobKey.jobKey(("job" + i), group);
            JobDetail storedJob = store.retrieveJob(jobKey);
            Assert.assertEquals(jobKey, storedJob.getKey());
        }
        // Retrieve by group
        Assert.assertEquals("Wrong number of jobs in group 'a'", store.getJobKeys(GroupMatcher.jobGroupEquals("a")).size(), 5);
        Assert.assertEquals("Wrong number of jobs in group 'b'", store.getJobKeys(GroupMatcher.jobGroupEquals("b")).size(), 5);
    }

    public void testStoreAndRetriveTriggers() throws Exception {
        SchedulerSignaler schedSignaler = new AbstractJobStoreTest.SampleSignaler();
        ClassLoadHelper loadHelper = new CascadingClassLoadHelper();
        loadHelper.initialize();
        JobStore store = createJobStore("testStoreAndRetriveTriggers");
        store.initialize(loadHelper, schedSignaler);
        // Store jobs and triggers.
        for (int i = 0; i < 10; i++) {
            String group = (i < 5) ? "a" : "b";
            JobDetail job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity(("job" + i), group).build();
            store.storeJob(job, true);
            SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(("job" + i), group).withSchedule(schedule).forJob(job).build();
            store.storeTrigger(((OperableTrigger) (trigger)), true);
        }
        // Retrieve job and trigger.
        for (int i = 0; i < 10; i++) {
            String group = (i < 5) ? "a" : "b";
            JobKey jobKey = JobKey.jobKey(("job" + i), group);
            JobDetail storedJob = store.retrieveJob(jobKey);
            Assert.assertEquals(jobKey, storedJob.getKey());
            TriggerKey triggerKey = TriggerKey.triggerKey(("job" + i), group);
            Trigger storedTrigger = store.retrieveTrigger(triggerKey);
            Assert.assertEquals(triggerKey, storedTrigger.getKey());
        }
        // Retrieve by group
        Assert.assertEquals("Wrong number of triggers in group 'a'", store.getTriggerKeys(GroupMatcher.triggerGroupEquals("a")).size(), 5);
        Assert.assertEquals("Wrong number of triggers in group 'b'", store.getTriggerKeys(GroupMatcher.triggerGroupEquals("b")).size(), 5);
    }

    public void testMatchers() throws Exception {
        SchedulerSignaler schedSignaler = new AbstractJobStoreTest.SampleSignaler();
        ClassLoadHelper loadHelper = new CascadingClassLoadHelper();
        loadHelper.initialize();
        JobStore store = createJobStore("testMatchers");
        store.initialize(loadHelper, schedSignaler);
        JobDetail job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity("job1", "aaabbbccc").build();
        store.storeJob(job, true);
        SimpleScheduleBuilder schedule = SimpleScheduleBuilder.simpleSchedule();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("trig1", "aaabbbccc").withSchedule(schedule).forJob(job).build();
        store.storeTrigger(((OperableTrigger) (trigger)), true);
        job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity("job1", "xxxyyyzzz").build();
        store.storeJob(job, true);
        schedule = SimpleScheduleBuilder.simpleSchedule();
        trigger = TriggerBuilder.newTrigger().withIdentity("trig1", "xxxyyyzzz").withSchedule(schedule).forJob(job).build();
        store.storeTrigger(((OperableTrigger) (trigger)), true);
        job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity("job2", "xxxyyyzzz").build();
        store.storeJob(job, true);
        schedule = SimpleScheduleBuilder.simpleSchedule();
        trigger = TriggerBuilder.newTrigger().withIdentity("trig2", "xxxyyyzzz").withSchedule(schedule).forJob(job).build();
        store.storeTrigger(((OperableTrigger) (trigger)), true);
        Set<JobKey> jkeys = store.getJobKeys(GroupMatcher.anyJobGroup());
        Assert.assertEquals("Wrong number of jobs found by anything matcher", 3, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupEquals("xxxyyyzzz"));
        Assert.assertEquals("Wrong number of jobs found by equals matcher", 2, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupEquals("aaabbbccc"));
        Assert.assertEquals("Wrong number of jobs found by equals matcher", 1, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupStartsWith("aa"));
        Assert.assertEquals("Wrong number of jobs found by starts with matcher", 1, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupStartsWith("xx"));
        Assert.assertEquals("Wrong number of jobs found by starts with matcher", 2, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupEndsWith("cc"));
        Assert.assertEquals("Wrong number of jobs found by ends with matcher", 1, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupEndsWith("zzz"));
        Assert.assertEquals("Wrong number of jobs found by ends with matcher", 2, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupContains("bc"));
        Assert.assertEquals("Wrong number of jobs found by contains with matcher", 1, jkeys.size());
        jkeys = store.getJobKeys(GroupMatcher.jobGroupContains("yz"));
        Assert.assertEquals("Wrong number of jobs found by contains with matcher", 2, jkeys.size());
        Set<TriggerKey> tkeys = store.getTriggerKeys(GroupMatcher.anyTriggerGroup());
        Assert.assertEquals("Wrong number of triggers found by anything matcher", 3, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupEquals("xxxyyyzzz"));
        Assert.assertEquals("Wrong number of triggers found by equals matcher", 2, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupEquals("aaabbbccc"));
        Assert.assertEquals("Wrong number of triggers found by equals matcher", 1, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupStartsWith("aa"));
        Assert.assertEquals("Wrong number of triggers found by starts with matcher", 1, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupStartsWith("xx"));
        Assert.assertEquals("Wrong number of triggers found by starts with matcher", 2, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupEndsWith("cc"));
        Assert.assertEquals("Wrong number of triggers found by ends with matcher", 1, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupEndsWith("zzz"));
        Assert.assertEquals("Wrong number of triggers found by ends with matcher", 2, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupContains("bc"));
        Assert.assertEquals("Wrong number of triggers found by contains with matcher", 1, tkeys.size());
        tkeys = store.getTriggerKeys(GroupMatcher.triggerGroupContains("yz"));
        Assert.assertEquals("Wrong number of triggers found by contains with matcher", 2, tkeys.size());
    }

    public void testAcquireTriggers() throws Exception {
        SchedulerSignaler schedSignaler = new AbstractJobStoreTest.SampleSignaler();
        ClassLoadHelper loadHelper = new CascadingClassLoadHelper();
        loadHelper.initialize();
        JobStore store = createJobStore("testAcquireTriggers");
        store.initialize(loadHelper, schedSignaler);
        // Setup: Store jobs and triggers.
        long MIN = 60 * 1000L;
        Date startTime0 = new Date(((System.currentTimeMillis()) + MIN));// a min from now.

        for (int i = 0; i < 10; i++) {
            Date startTime = new Date(((startTime0.getTime()) + (i * MIN)));// a min apart

            JobDetail job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity(("job" + i)).build();
            SimpleScheduleBuilder schedule = SimpleScheduleBuilder.repeatMinutelyForever(2);
            OperableTrigger trigger = ((OperableTrigger) (TriggerBuilder.newTrigger().withIdentity(("job" + i)).withSchedule(schedule).forJob(job).startAt(startTime).build()));
            // Manually trigger the first fire time computation that scheduler would do. Otherwise
            // the store.acquireNextTriggers() will not work properly.
            Date fireTime = trigger.computeFirstFireTime(null);
            Assert.assertEquals(true, (fireTime != null));
            store.storeJobAndTrigger(job, trigger);
        }
        // Test acquire one trigger at a time
        for (int i = 0; i < 10; i++) {
            long noLaterThan = (startTime0.getTime()) + (i * MIN);
            int maxCount = 1;
            long timeWindow = 0;
            List<OperableTrigger> triggers = store.acquireNextTriggers(noLaterThan, maxCount, timeWindow);
            Assert.assertEquals(1, triggers.size());
            Assert.assertEquals(("job" + i), triggers.get(0).getKey().getName());
            // Let's remove the trigger now.
            store.removeJob(triggers.get(0).getJobKey());
        }
    }

    public void testAcquireTriggersInBatch() throws Exception {
        SchedulerSignaler schedSignaler = new AbstractJobStoreTest.SampleSignaler();
        ClassLoadHelper loadHelper = new CascadingClassLoadHelper();
        loadHelper.initialize();
        JobStore store = createJobStore("testAcquireTriggersInBatch");
        store.initialize(loadHelper, schedSignaler);
        // Setup: Store jobs and triggers.
        long MIN = 60 * 1000L;
        Date startTime0 = new Date(((System.currentTimeMillis()) + MIN));// a min from now.

        for (int i = 0; i < 10; i++) {
            Date startTime = new Date(((startTime0.getTime()) + (i * MIN)));// a min apart

            JobDetail job = JobBuilder.newJob(AbstractJobStoreTest.MyJob.class).withIdentity(("job" + i)).build();
            SimpleScheduleBuilder schedule = SimpleScheduleBuilder.repeatMinutelyForever(2);
            OperableTrigger trigger = ((OperableTrigger) (TriggerBuilder.newTrigger().withIdentity(("job" + i)).withSchedule(schedule).forJob(job).startAt(startTime).build()));
            // Manually trigger the first fire time computation that scheduler would do. Otherwise
            // the store.acquireNextTriggers() will not work properly.
            Date fireTime = trigger.computeFirstFireTime(null);
            Assert.assertEquals(true, (fireTime != null));
            store.storeJobAndTrigger(job, trigger);
        }
        // Test acquire batch of triggers at a time
        long noLaterThan = (startTime0.getTime()) + (10 * MIN);
        int maxCount = 7;
        // time window needs to be big to be able to pick up multiple triggers when they are a minute apart
        long timeWindow = 8 * MIN;
        List<OperableTrigger> triggers = store.acquireNextTriggers(noLaterThan, maxCount, timeWindow);
        Assert.assertEquals(7, triggers.size());
        for (int i = 0; i < 7; i++) {
            Assert.assertEquals(("job" + i), triggers.get(i).getKey().getName());
        }
    }

    public void testResetErrorTrigger() throws Exception {
        Date baseFireTimeDate = DateBuilder.evenMinuteDateAfterNow();
        long baseFireTime = baseFireTimeDate.getTime();
        // create and store a trigger
        OperableTrigger trigger1 = new org.quartz.impl.triggers.SimpleTriggerImpl("trigger1", "triggerGroup1", this.fJobDetail.getName(), this.fJobDetail.getGroup(), new Date((baseFireTime + 200000)), new Date((baseFireTime + 200000)), 2, 2000);
        trigger1.computeFirstFireTime(null);
        this.fJobStore.storeTrigger(trigger1, false);
        long firstFireTime = new Date(trigger1.getNextFireTime().getTime()).getTime();
        // pretend to fire it
        List<OperableTrigger> aqTs = this.fJobStore.acquireNextTriggers((firstFireTime + 10000), 1, 0L);
        TestCase.assertEquals(trigger1.getKey(), aqTs.get(0).getKey());
        List<TriggerFiredResult> fTs = this.fJobStore.triggersFired(aqTs);
        TriggerFiredResult ft = fTs.get(0);
        // get the trigger into error state
        this.fJobStore.triggeredJobComplete(ft.getTriggerFiredBundle().getTrigger(), ft.getTriggerFiredBundle().getJobDetail(), SET_TRIGGER_ERROR);
        TriggerState state = this.fJobStore.getTriggerState(trigger1.getKey());
        TestCase.assertEquals(ERROR, state);
        // test reset
        this.fJobStore.resetTriggerFromErrorState(trigger1.getKey());
        state = this.fJobStore.getTriggerState(trigger1.getKey());
        TestCase.assertEquals(NORMAL, state);
    }

    public static class SampleSignaler implements SchedulerSignaler {
        volatile int fMisfireCount = 0;

        public void notifyTriggerListenersMisfired(Trigger trigger) {
            System.out.println(((("Trigger misfired: " + (trigger.getKey())) + ", fire time: ") + (trigger.getNextFireTime())));
            (fMisfireCount)++;
        }

        public void signalSchedulingChange(long candidateNewNextFireTime) {
        }

        public void notifySchedulerListenersFinalized(Trigger trigger) {
        }

        public void notifySchedulerListenersJobDeleted(JobKey jobKey) {
        }

        public void notifySchedulerListenersError(String string, SchedulerException jpe) {
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

