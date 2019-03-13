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


import JobKey.DEFAULT_GROUP;
import TriggerState.NORMAL;
import TriggerState.PAUSED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Trigger.TriggerState;
import org.quartz.impl.matchers.GroupMatcher;


/**
 * Test High Level Scheduler functionality (implicitly tests the underlying jobstore (RAMJobStore))
 */
public abstract class AbstractSchedulerTest {
    private static final String BARRIER = "BARRIER";

    private static final String DATE_STAMPS = "DATE_STAMPS";

    private static final String JOB_THREAD = "JOB_THREAD";

    @SuppressWarnings("deprecation")
    public static class TestStatefulJob implements StatefulJob {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public static class TestJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    public static final long TEST_TIMEOUT_SECONDS = 125;

    public static class TestJobWithSync implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                @SuppressWarnings("unchecked")
                List<Long> jobExecTimestamps = ((List<Long>) (context.getScheduler().getContext().get(AbstractSchedulerTest.DATE_STAMPS)));
                CyclicBarrier barrier = ((CyclicBarrier) (context.getScheduler().getContext().get(AbstractSchedulerTest.BARRIER)));
                jobExecTimestamps.add(System.currentTimeMillis());
                barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new AssertionError(("Await on barrier was interrupted: " + (e.toString())));
            }
        }
    }

    @DisallowConcurrentExecution
    @PersistJobDataAfterExecution
    public static class TestAnnotatedJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
        }
    }

    @Test
    public void testBasicStorageFunctions() throws Exception {
        Scheduler sched = createScheduler("testBasicStorageFunctions", 2);
        // test basic storage functions of scheduler...
        JobDetail job = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j1").storeDurably().build();
        Assert.assertFalse("Unexpected existence of job named 'j1'.", sched.checkExists(JobKey.jobKey("j1")));
        sched.addJob(job, false);
        Assert.assertTrue("Expected existence of job named 'j1' but checkExists return false.", sched.checkExists(JobKey.jobKey("j1")));
        job = sched.getJobDetail(JobKey.jobKey("j1"));
        Assert.assertNotNull("Stored job not found!", job);
        sched.deleteJob(JobKey.jobKey("j1"));
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity("t1").forJob(job).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(5)).build();
        Assert.assertFalse("Unexpected existence of trigger named '11'.", sched.checkExists(TriggerKey.triggerKey("t1")));
        sched.scheduleJob(job, trigger);
        Assert.assertTrue("Expected existence of trigger named 't1' but checkExists return false.", sched.checkExists(TriggerKey.triggerKey("t1")));
        job = sched.getJobDetail(JobKey.jobKey("j1"));
        Assert.assertNotNull("Stored job not found!", job);
        trigger = sched.getTrigger(TriggerKey.triggerKey("t1"));
        Assert.assertNotNull("Stored trigger not found!", trigger);
        job = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j2", "g1").build();
        trigger = TriggerBuilder.newTrigger().withIdentity("t2", "g1").forJob(job).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(5)).build();
        sched.scheduleJob(job, trigger);
        job = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j3", "g1").build();
        trigger = TriggerBuilder.newTrigger().withIdentity("t3", "g1").forJob(job).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(5)).build();
        sched.scheduleJob(job, trigger);
        List<String> jobGroups = sched.getJobGroupNames();
        List<String> triggerGroups = sched.getTriggerGroupNames();
        Assert.assertTrue("Job group list size expected to be = 2 ", ((jobGroups.size()) == 2));
        Assert.assertTrue("Trigger group list size expected to be = 2 ", ((triggerGroups.size()) == 2));
        Set<JobKey> jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals(DEFAULT_GROUP));
        Set<TriggerKey> triggerKeys = sched.getTriggerKeys(GroupMatcher.triggerGroupEquals(TriggerKey.DEFAULT_GROUP));
        Assert.assertTrue("Number of jobs expected in default group was 1 ", ((jobKeys.size()) == 1));
        Assert.assertTrue("Number of triggers expected in default group was 1 ", ((triggerKeys.size()) == 1));
        jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals("g1"));
        triggerKeys = sched.getTriggerKeys(GroupMatcher.triggerGroupEquals("g1"));
        Assert.assertTrue("Number of jobs expected in 'g1' group was 2 ", ((jobKeys.size()) == 2));
        Assert.assertTrue("Number of triggers expected in 'g1' group was 2 ", ((triggerKeys.size()) == 2));
        TriggerState s = sched.getTriggerState(TriggerKey.triggerKey("t2", "g1"));
        Assert.assertTrue("State of trigger t2 expected to be NORMAL ", s.equals(NORMAL));
        sched.pauseTrigger(TriggerKey.triggerKey("t2", "g1"));
        s = sched.getTriggerState(TriggerKey.triggerKey("t2", "g1"));
        Assert.assertTrue("State of trigger t2 expected to be PAUSED ", s.equals(PAUSED));
        sched.resumeTrigger(TriggerKey.triggerKey("t2", "g1"));
        s = sched.getTriggerState(TriggerKey.triggerKey("t2", "g1"));
        Assert.assertTrue("State of trigger t2 expected to be NORMAL ", s.equals(NORMAL));
        Set<String> pausedGroups = sched.getPausedTriggerGroups();
        Assert.assertTrue("Size of paused trigger groups list expected to be 0 ", ((pausedGroups.size()) == 0));
        sched.pauseTriggers(GroupMatcher.triggerGroupEquals("g1"));
        // test that adding a trigger to a paused group causes the new trigger to be paused also...
        job = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j4", "g1").build();
        trigger = TriggerBuilder.newTrigger().withIdentity("t4", "g1").forJob(job).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatForever().withIntervalInSeconds(5)).build();
        sched.scheduleJob(job, trigger);
        pausedGroups = sched.getPausedTriggerGroups();
        Assert.assertTrue("Size of paused trigger groups list expected to be 1 ", ((pausedGroups.size()) == 1));
        s = sched.getTriggerState(TriggerKey.triggerKey("t2", "g1"));
        Assert.assertTrue("State of trigger t2 expected to be PAUSED ", s.equals(PAUSED));
        s = sched.getTriggerState(TriggerKey.triggerKey("t4", "g1"));
        Assert.assertTrue("State of trigger t4 expected to be PAUSED ", s.equals(PAUSED));
        sched.resumeTriggers(GroupMatcher.triggerGroupEquals("g1"));
        s = sched.getTriggerState(TriggerKey.triggerKey("t2", "g1"));
        Assert.assertTrue("State of trigger t2 expected to be NORMAL ", s.equals(NORMAL));
        s = sched.getTriggerState(TriggerKey.triggerKey("t4", "g1"));
        Assert.assertTrue("State of trigger t4 expected to be NORMAL ", s.equals(NORMAL));
        pausedGroups = sched.getPausedTriggerGroups();
        Assert.assertTrue("Size of paused trigger groups list expected to be 0 ", ((pausedGroups.size()) == 0));
        Assert.assertFalse("Scheduler should have returned 'false' from attempt to unschedule non-existing trigger. ", sched.unscheduleJob(TriggerKey.triggerKey("foasldfksajdflk")));
        Assert.assertTrue("Scheduler should have returned 'true' from attempt to unschedule existing trigger. ", sched.unscheduleJob(TriggerKey.triggerKey("t3", "g1")));
        jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals("g1"));
        triggerKeys = sched.getTriggerKeys(GroupMatcher.triggerGroupEquals("g1"));
        Assert.assertTrue("Number of jobs expected in 'g1' group was 1 ", ((jobKeys.size()) == 2));// job should have been deleted also, because it is non-durable

        Assert.assertTrue("Number of triggers expected in 'g1' group was 1 ", ((triggerKeys.size()) == 2));
        Assert.assertTrue("Scheduler should have returned 'true' from attempt to unschedule existing trigger. ", sched.unscheduleJob(TriggerKey.triggerKey("t1")));
        jobKeys = sched.getJobKeys(GroupMatcher.jobGroupEquals(DEFAULT_GROUP));
        triggerKeys = sched.getTriggerKeys(GroupMatcher.triggerGroupEquals(TriggerKey.DEFAULT_GROUP));
        Assert.assertTrue("Number of jobs expected in default group was 1 ", ((jobKeys.size()) == 1));// job should have been left in place, because it is non-durable

        Assert.assertTrue("Number of triggers expected in default group was 0 ", ((triggerKeys.size()) == 0));
        sched.shutdown(true);
    }

    @Test
    public void testDurableStorageFunctions() throws Exception {
        Scheduler sched = createScheduler("testDurableStorageFunctions", 2);
        try {
            // test basic storage functions of scheduler...
            JobDetail job = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j1").storeDurably().build();
            Assert.assertFalse("Unexpected existence of job named 'j1'.", sched.checkExists(JobKey.jobKey("j1")));
            sched.addJob(job, false);
            Assert.assertTrue("Unexpected non-existence of job named 'j1'.", sched.checkExists(JobKey.jobKey("j1")));
            JobDetail nonDurableJob = JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJob.class).withIdentity("j2").build();
            try {
                sched.addJob(nonDurableJob, false);
                Assert.fail("Storage of non-durable job should not have succeeded.");
            } catch (SchedulerException expected) {
                Assert.assertFalse("Unexpected existence of job named 'j2'.", sched.checkExists(JobKey.jobKey("j2")));
            }
            sched.addJob(nonDurableJob, false, true);
            Assert.assertTrue("Unexpected non-existence of job named 'j2'.", sched.checkExists(JobKey.jobKey("j2")));
        } finally {
            sched.shutdown(true);
        }
    }

    @Test
    public void testShutdownWithSleepReturnsAfterAllThreadsAreStopped() throws Exception {
        Map<Thread, StackTraceElement[]> allThreadsStart = Thread.getAllStackTraces();
        int threadPoolSize = 5;
        Scheduler scheduler = createScheduler("testShutdownWithSleepReturnsAfterAllThreadsAreStopped", threadPoolSize);
        Thread.sleep(500L);
        Map<Thread, StackTraceElement[]> allThreadsRunning = Thread.getAllStackTraces();
        scheduler.shutdown(true);
        Thread.sleep(200L);
        Map<Thread, StackTraceElement[]> allThreadsEnd = Thread.getAllStackTraces();
        Set<Thread> endingThreads = new HashSet<Thread>(allThreadsEnd.keySet());
        // remove all pre-existing threads from the set
        for (Thread t : allThreadsStart.keySet()) {
            allThreadsEnd.remove(t);
        }
        // remove threads that are known artifacts of the test
        for (Thread t : endingThreads) {
            if ((t.getName().contains("derby")) && (t.getThreadGroup().getName().contains("derby"))) {
                allThreadsEnd.remove(t);
            }
            if (((t.getThreadGroup()) != null) && (t.getThreadGroup().getName().equals("system"))) {
                allThreadsEnd.remove(t);
            }
            if (((t.getThreadGroup()) != null) && (t.getThreadGroup().getName().equals("main"))) {
                allThreadsEnd.remove(t);
            }
        }
        if ((allThreadsEnd.size()) > 0) {
            // log the additional threads
            for (Thread t : allThreadsEnd.keySet()) {
                System.out.println(((((((("*** Found additional thread: " + (t.getName())) + " (of type ") + (t.getClass().getName())) + ")  in group: ") + (t.getThreadGroup().getName())) + " with parent group: ") + ((t.getThreadGroup().getParent()) == null ? "-none-" : t.getThreadGroup().getParent().getName())));
            }
            // log all threads that were running before shutdown
            for (Thread t : allThreadsRunning.keySet()) {
                System.out.println(((((("- Test runtime thread: " + (t.getName())) + " (of type ") + (t.getClass().getName())) + ")  in group: ") + ((t.getThreadGroup()) == null ? "-none-" : ((t.getThreadGroup().getName()) + " with parent group: ") + ((t.getThreadGroup().getParent()) == null ? "-none-" : t.getThreadGroup().getParent().getName()))));
            }
        }
        Assert.assertTrue("Found unexpected new threads (see console output for listing)", ((allThreadsEnd.size()) == 0));
    }

    @Test
    public void testAbilityToFireImmediatelyWhenStartedBefore() throws Exception {
        List<Long> jobExecTimestamps = Collections.synchronizedList(new ArrayList<Long>());
        CyclicBarrier barrier = new CyclicBarrier(2);
        Scheduler sched = createScheduler("testAbilityToFireImmediatelyWhenStartedBefore", 5);
        sched.getContext().put(AbstractSchedulerTest.BARRIER, barrier);
        sched.getContext().put(AbstractSchedulerTest.DATE_STAMPS, jobExecTimestamps);
        sched.start();
        Thread.yield();
        JobDetail job1 = JobBuilder.newJob(AbstractSchedulerTest.TestJobWithSync.class).withIdentity("job1").build();
        Trigger trigger1 = TriggerBuilder.newTrigger().forJob(job1).build();
        long sTime = System.currentTimeMillis();
        sched.scheduleJob(job1, trigger1);
        barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        sched.shutdown(true);
        long fTime = jobExecTimestamps.get(0);
        Assert.assertTrue("Immediate trigger did not fire within a reasonable amount of time.", ((fTime - sTime) < 7000L));// This is dangerously subjective!  but what else to do?

    }

    @Test
    public void testAbilityToFireImmediatelyWhenStartedBeforeWithTriggerJob() throws Exception {
        List<Long> jobExecTimestamps = Collections.synchronizedList(new ArrayList<Long>());
        CyclicBarrier barrier = new CyclicBarrier(2);
        Scheduler sched = createScheduler("testAbilityToFireImmediatelyWhenStartedBeforeWithTriggerJob", 5);
        sched.getContext().put(AbstractSchedulerTest.BARRIER, barrier);
        sched.getContext().put(AbstractSchedulerTest.DATE_STAMPS, jobExecTimestamps);
        sched.start();
        Thread.yield();
        JobDetail job1 = JobBuilder.newJob(AbstractSchedulerTest.TestJobWithSync.class).withIdentity("job1").storeDurably().build();
        sched.addJob(job1, false);
        long sTime = System.currentTimeMillis();
        sched.triggerJob(job1.getKey());
        barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        sched.shutdown(true);
        long fTime = jobExecTimestamps.get(0);
        Assert.assertTrue("Immediate trigger did not fire within a reasonable amount of time.", ((fTime - sTime) < 7000L));// This is dangerously subjective!  but what else to do?

    }

    @Test
    public void testAbilityToFireImmediatelyWhenStartedAfter() throws Exception {
        List<Long> jobExecTimestamps = Collections.synchronizedList(new ArrayList<Long>());
        CyclicBarrier barrier = new CyclicBarrier(2);
        Scheduler sched = createScheduler("testAbilityToFireImmediatelyWhenStartedAfter", 5);
        sched.getContext().put(AbstractSchedulerTest.BARRIER, barrier);
        sched.getContext().put(AbstractSchedulerTest.DATE_STAMPS, jobExecTimestamps);
        JobDetail job1 = JobBuilder.newJob(AbstractSchedulerTest.TestJobWithSync.class).withIdentity("job1").build();
        Trigger trigger1 = TriggerBuilder.newTrigger().forJob(job1).build();
        long sTime = System.currentTimeMillis();
        sched.scheduleJob(job1, trigger1);
        sched.start();
        barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        sched.shutdown(true);
        long fTime = jobExecTimestamps.get(0);
        Assert.assertTrue("Immediate trigger did not fire within a reasonable amount of time.", ((fTime - sTime) < 7000L));// This is dangerously subjective!  but what else to do?

    }

    @Test
    public void testScheduleMultipleTriggersForAJob() throws SchedulerException {
        JobDetail job = JobBuilder.newJob(AbstractSchedulerTest.TestJob.class).withIdentity("job1", "group1").build();
        Trigger trigger1 = TriggerBuilder.newTrigger().withIdentity("trigger1", "group1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();
        Trigger trigger2 = TriggerBuilder.newTrigger().withIdentity("trigger2", "group1").startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();
        Set<Trigger> triggersForJob = new HashSet<Trigger>();
        triggersForJob.add(trigger1);
        triggersForJob.add(trigger2);
        Scheduler sched = createScheduler("testScheduleMultipleTriggersForAJob", 5);
        sched.scheduleJob(job, triggersForJob, true);
        List<? extends Trigger> triggersOfJob = sched.getTriggersOfJob(job.getKey());
        Assert.assertEquals(2, triggersOfJob.size());
        Assert.assertTrue(triggersOfJob.contains(trigger1));
        Assert.assertTrue(triggersOfJob.contains(trigger2));
        sched.shutdown(true);
    }

    @Test
    public void testShutdownWithoutWaitIsUnclean() throws Exception {
        CyclicBarrier barrier = new CyclicBarrier(2);
        Scheduler scheduler = createScheduler("testShutdownWithoutWaitIsUnclean", 8);
        try {
            scheduler.getContext().put(AbstractSchedulerTest.BARRIER, barrier);
            scheduler.start();
            scheduler.addJob(JobBuilder.newJob().ofType(AbstractSchedulerTest.UncleanShutdownJob.class).withIdentity("job").storeDurably().build(), false);
            scheduler.scheduleJob(TriggerBuilder.newTrigger().forJob("job").startNow().build());
            while (scheduler.getCurrentlyExecutingJobs().isEmpty()) {
                Thread.sleep(50);
            } 
        } finally {
            scheduler.shutdown(false);
        }
        barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        Thread jobThread = ((Thread) (scheduler.getContext().get(AbstractSchedulerTest.JOB_THREAD)));
        jobThread.join(TimeUnit.SECONDS.toMillis(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS));
    }

    public static class UncleanShutdownJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            try {
                SchedulerContext schedulerContext = context.getScheduler().getContext();
                schedulerContext.put(AbstractSchedulerTest.JOB_THREAD, Thread.currentThread());
                CyclicBarrier barrier = ((CyclicBarrier) (schedulerContext.get(AbstractSchedulerTest.BARRIER)));
                barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new AssertionError(("Await on barrier was interrupted: " + (e.toString())));
            }
        }
    }

    @Test
    public void testShutdownWithWaitIsClean() throws Exception {
        final AtomicBoolean shutdown = new AtomicBoolean(false);
        List<Long> jobExecTimestamps = Collections.synchronizedList(new ArrayList<Long>());
        CyclicBarrier barrier = new CyclicBarrier(2);
        final Scheduler scheduler = createScheduler("testShutdownWithWaitIsClean", 8);
        try {
            scheduler.getContext().put(AbstractSchedulerTest.BARRIER, barrier);
            scheduler.getContext().put(AbstractSchedulerTest.DATE_STAMPS, jobExecTimestamps);
            scheduler.start();
            scheduler.addJob(JobBuilder.newJob().ofType(AbstractSchedulerTest.TestJobWithSync.class).withIdentity("job").storeDurably().build(), false);
            scheduler.scheduleJob(TriggerBuilder.newTrigger().forJob("job").startNow().build());
            while (scheduler.getCurrentlyExecutingJobs().isEmpty()) {
                Thread.sleep(50);
            } 
        } finally {
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        scheduler.shutdown(true);
                        shutdown.set(true);
                    } catch (SchedulerException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            };
            t.start();
            Thread.sleep(1000);
            Assert.assertFalse(shutdown.get());
            barrier.await(AbstractSchedulerTest.TEST_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            t.join();
        }
    }
}

