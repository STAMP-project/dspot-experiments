/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.impl;


import Trigger.TriggerState.NORMAL;
import Trigger.TriggerState.PAUSED;
import java.lang.management.ManagementFactory;
import java.util.Collections;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.TriggerKey;
import org.quartz.impl.calendar.BaseCalendar;
import org.quartz.impl.matchers.GroupMatcher;


/**
 * RemoteMBeanSchedulerTest
 */
public class RemoteMBeanSchedulerTest {
    public static final String TRIGGER_KEY = "trigger1";

    public static final String GROUP_KEY = "group1";

    public static final String JOB_KEY = "job1";

    public static final String CALENDAR_KEY = "calendar1";

    private Scheduler scheduler;

    private RemoteMBeanScheduler remoteScheduler;

    @Test
    public void testJMXAttributesAccess() throws Exception {
        MatcherAssert.assertThat(remoteScheduler.getCalendarNames(), CoreMatchers.equalTo(scheduler.getCalendarNames()));
        MatcherAssert.assertThat(remoteScheduler.getJobGroupNames(), CoreMatchers.equalTo(scheduler.getJobGroupNames()));
        MatcherAssert.assertThat(remoteScheduler.getPausedTriggerGroups(), CoreMatchers.equalTo(scheduler.getPausedTriggerGroups()));
        MatcherAssert.assertThat(remoteScheduler.getSchedulerInstanceId(), CoreMatchers.equalTo(scheduler.getSchedulerInstanceId()));
        MatcherAssert.assertThat(remoteScheduler.getSchedulerName(), CoreMatchers.equalTo(scheduler.getSchedulerName()));
        MatcherAssert.assertThat(remoteScheduler.getTriggerGroupNames(), CoreMatchers.equalTo(scheduler.getTriggerGroupNames()));
    }

    @Test
    public void testSchedulerMetaData() throws Exception {
        SchedulerMetaData remoteSchedulerMetaData = remoteScheduler.getMetaData();
        SchedulerMetaData metaData = scheduler.getMetaData();
        MatcherAssert.assertThat(remoteSchedulerMetaData.getSchedulerName(), CoreMatchers.equalTo(metaData.getSchedulerName()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getSchedulerInstanceId(), CoreMatchers.equalTo(metaData.getSchedulerInstanceId()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.isInStandbyMode(), CoreMatchers.is(metaData.isInStandbyMode()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getSchedulerClass(), CoreMatchers.equalTo(((Class) (RemoteMBeanSchedulerTest.TestRemoteScheduler.class))));
        MatcherAssert.assertThat(remoteSchedulerMetaData.isSchedulerRemote(), CoreMatchers.is(true));
        MatcherAssert.assertThat(remoteSchedulerMetaData.isStarted(), CoreMatchers.is(false));// information not available through JMX

        MatcherAssert.assertThat(remoteSchedulerMetaData.isInStandbyMode(), CoreMatchers.is(metaData.isInStandbyMode()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.isShutdown(), CoreMatchers.is(metaData.isShutdown()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getRunningSince(), CoreMatchers.nullValue());// Information not available through JMX

        MatcherAssert.assertThat(remoteSchedulerMetaData.getNumberOfJobsExecuted(), CoreMatchers.is(metaData.getNumberOfJobsExecuted()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getJobStoreClass(), CoreMatchers.equalTo(((Class) (metaData.getJobStoreClass()))));
        MatcherAssert.assertThat(remoteSchedulerMetaData.isJobStoreSupportsPersistence(), CoreMatchers.is(false));// Information not available through JMX

        MatcherAssert.assertThat(remoteSchedulerMetaData.isJobStoreClustered(), CoreMatchers.is(false));// Information not available through JMX

        MatcherAssert.assertThat(remoteSchedulerMetaData.getThreadPoolClass(), CoreMatchers.equalTo(((Class) (metaData.getThreadPoolClass()))));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getThreadPoolSize(), CoreMatchers.is(metaData.getThreadPoolSize()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getVersion(), CoreMatchers.equalTo(metaData.getVersion()));
        MatcherAssert.assertThat(remoteSchedulerMetaData.getJobStoreClass(), CoreMatchers.equalTo(((Class) (metaData.getJobStoreClass()))));
    }

    @Test
    public void testCalendarOperations() throws Exception {
        try {
            remoteScheduler.addCalendar("testCal", new BaseCalendar(), true, true);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.getCalendar("test");
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        remoteScheduler.deleteCalendar(RemoteMBeanSchedulerTest.CALENDAR_KEY);
        MatcherAssert.assertThat(scheduler.getCalendar(RemoteMBeanSchedulerTest.CALENDAR_KEY), CoreMatchers.nullValue());
    }

    @Test
    public void testTriggerOperations() throws Exception {
        TriggerKey triggerKey = new TriggerKey(RemoteMBeanSchedulerTest.TRIGGER_KEY, RemoteMBeanSchedulerTest.GROUP_KEY);
        GroupMatcher<TriggerKey> groupMatcher = GroupMatcher.triggerGroupEquals(RemoteMBeanSchedulerTest.GROUP_KEY);
        try {
            remoteScheduler.getTrigger(triggerKey);
            Assert.fail("Method had a different return type in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.getTriggersOfJob(new JobKey(RemoteMBeanSchedulerTest.JOB_KEY, RemoteMBeanSchedulerTest.GROUP_KEY));
            Assert.fail("Method had a different return type in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.checkExists(triggerKey);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        MatcherAssert.assertThat(remoteScheduler.getTriggerState(triggerKey), CoreMatchers.is(scheduler.getTriggerState(triggerKey)));
        try {
            remoteScheduler.getTriggerKeys(groupMatcher);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        remoteScheduler.pauseTrigger(triggerKey);
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(PAUSED));
        remoteScheduler.resumeTrigger(triggerKey);
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(NORMAL));
        remoteScheduler.pauseTriggers(groupMatcher);
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(PAUSED));
        remoteScheduler.resumeTriggers(groupMatcher);
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(NORMAL));
        remoteScheduler.pauseAll();
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(PAUSED));
        remoteScheduler.resumeAll();
        MatcherAssert.assertThat(scheduler.getTriggerState(triggerKey), CoreMatchers.is(NORMAL));
    }

    @Test
    public void testJobOperations() throws Exception {
        JobKey job2 = new JobKey("job2", RemoteMBeanSchedulerTest.GROUP_KEY);
        JobDetail job2Detail = JobBuilder.newJob(RemoteMBeanSchedulerTest.HelloJob.class).withIdentity(job2).storeDurably().build();
        remoteScheduler.addJob(job2Detail, false);
        MatcherAssert.assertThat(remoteScheduler.getJobDetail(job2), CoreMatchers.equalTo(job2Detail));
        try {
            remoteScheduler.checkExists(job2);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        remoteScheduler.pauseJob(job2);
        remoteScheduler.resumeJob(job2);
        GroupMatcher<JobKey> matcher = GroupMatcher.jobGroupEquals(RemoteMBeanSchedulerTest.GROUP_KEY);
        remoteScheduler.pauseJobs(matcher);
        remoteScheduler.resumeJobs(matcher);
        MatcherAssert.assertThat(remoteScheduler.getJobKeys(matcher).size(), CoreMatchers.is(2));
        MatcherAssert.assertThat(remoteScheduler.interrupt(job2), CoreMatchers.is(false));
        try {
            remoteScheduler.triggerJob(job2);
            Assert.fail("Method had different parameters in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.scheduleJob(null, null);
            Assert.fail("Method had different parameters in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.scheduleJob(null);
            Assert.fail("Method had different parameters in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.scheduleJobs(null, false);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        MatcherAssert.assertThat(remoteScheduler.unscheduleJob(TriggerKey.triggerKey(RemoteMBeanSchedulerTest.TRIGGER_KEY, RemoteMBeanSchedulerTest.GROUP_KEY)), CoreMatchers.is(true));
        try {
            remoteScheduler.unscheduleJobs(null);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.rescheduleJob(null, null);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        MatcherAssert.assertThat(remoteScheduler.deleteJob(job2), CoreMatchers.is(true));
        try {
            remoteScheduler.deleteJobs(Collections.singletonList(JobKey.jobKey(RemoteMBeanSchedulerTest.JOB_KEY, RemoteMBeanSchedulerTest.GROUP_KEY)));
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
    }

    @Test
    public void testLifecycleOperations() throws SchedulerException {
        try {
            remoteScheduler.startDelayed(60);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        remoteScheduler.start();
        MatcherAssert.assertThat(remoteScheduler.isStarted(), CoreMatchers.is(true));
        MatcherAssert.assertThat(scheduler.isStarted(), CoreMatchers.is(true));
        remoteScheduler.standby();
        MatcherAssert.assertThat(remoteScheduler.isInStandbyMode(), CoreMatchers.is(true));
        MatcherAssert.assertThat(scheduler.isInStandbyMode(), CoreMatchers.is(true));
        try {
            remoteScheduler.shutdown(true);
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        remoteScheduler.shutdown();
        try {
            remoteScheduler.isShutdown();
            Assert.fail("Shutting down a scheduler un-registers it in JMX");
        } catch (SchedulerException e) {
            // expected
        }
        MatcherAssert.assertThat(scheduler.isShutdown(), CoreMatchers.is(true));
    }

    @Test
    public void testJMXOperations() throws Exception {
        remoteScheduler.clear();
        MatcherAssert.assertThat(remoteScheduler.getJobGroupNames().isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void testUnsupportedMethods() {
        try {
            remoteScheduler.getListenerManager();
            Assert.fail("Operation should not be supported");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.setJobFactory(null);
            Assert.fail("Operation should not be supported");
        } catch (SchedulerException e) {
            // expected
        }
    }

    @Test
    public void testListBrokenAttributes() throws Exception {
        try {
            remoteScheduler.getContext();
            Assert.fail("Method was not exposed in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
        try {
            remoteScheduler.getCurrentlyExecutingJobs();
            Assert.fail("Method had a different return type in MBean API");
        } catch (SchedulerException e) {
            // expected
        }
    }

    public static class HelloJob implements Job {
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("hello world!");
        }
    }

    public static class TestRemoteScheduler extends RemoteMBeanScheduler {
        private MBeanServer mBeanServer;

        private ObjectName objectName;

        public TestRemoteScheduler(String objectName) throws MalformedObjectNameException, SchedulerException {
            this.objectName = new ObjectName(objectName);
            initialize();
        }

        @Override
        public void initialize() throws SchedulerException {
            mBeanServer = ManagementFactory.getPlatformMBeanServer();
        }

        @Override
        protected Object getAttribute(String attribute) throws SchedulerException {
            try {
                return mBeanServer.getAttribute(objectName, attribute);
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }

        @Override
        protected AttributeList getAttributes(String[] attributes) throws SchedulerException {
            try {
                return mBeanServer.getAttributes(objectName, attributes);
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }

        @Override
        protected Object invoke(String operationName, Object[] params, String[] signature) throws SchedulerException {
            try {
                return mBeanServer.invoke(objectName, operationName, params, signature);
            } catch (Exception e) {
                throw new SchedulerException(e);
            }
        }
    }
}

