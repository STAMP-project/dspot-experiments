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
package com.liferay.portal.scheduler.quartz.internal;


import NewEnv.Type;
import SchedulerEngine.DESTINATION_NAME;
import SchedulerEngine.JOB_STATE;
import SchedulerEngine.MESSAGE;
import SchedulerEngine.STORAGE_TYPE;
import StorageType.MEMORY;
import StorageType.PERSISTED;
import StringPool.BLANK;
import TimeUnit.SECOND;
import TriggerState.NORMAL;
import TriggerState.PAUSED;
import TriggerState.UNSCHEDULED;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.messaging.Message;
import com.liferay.portal.kernel.messaging.MessageListener;
import com.liferay.portal.kernel.scheduler.JobState;
import com.liferay.portal.kernel.scheduler.JobStateSerializeUtil;
import com.liferay.portal.kernel.scheduler.SchedulerException;
import com.liferay.portal.kernel.scheduler.StorageType;
import com.liferay.portal.kernel.scheduler.Trigger;
import com.liferay.portal.kernel.scheduler.TriggerState;
import com.liferay.portal.kernel.scheduler.messaging.SchedulerResponse;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.NewEnv;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.scheduler.quartz.internal.job.MessageSenderJob;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.Calendar;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerMetaData;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.spi.JobFactory;


/**
 *
 *
 * @author Tina Tian
 */
@NewEnv(type = Type.CLASSLOADER)
public class QuartzSchedulerEngineTest {
    @Test
    public void testDelete1() throws Exception {
        // Delete by group name
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
        _quartzSchedulerEngine.delete(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertTrue(schedulerResponses.toString(), schedulerResponses.isEmpty());
        // Delete by job name and group name
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        Assert.assertNotNull(schedulerResponse);
        _quartzSchedulerEngine.delete(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        Assert.assertNull(schedulerResponse);
    }

    @Test
    public void testDelete2() throws Exception {
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs();
        String testJobName = (QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "memory";
        Assert.assertEquals(schedulerResponses.toString(), (2 * (QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER)), schedulerResponses.size());
        Trigger trigger = _quartzTriggerFactory.createTrigger(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
        _quartzSchedulerEngine.schedule(trigger, BLANK, QuartzSchedulerEngineTest._TEST_DESTINATION_NAME, new Message(), MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs();
        Assert.assertEquals(schedulerResponses.toString(), ((2 * (QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER)) + 1), schedulerResponses.size());
        _quartzSchedulerEngine.delete(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs();
        Assert.assertEquals(schedulerResponses.toString(), (2 * (QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER)), schedulerResponses.size());
    }

    @Test
    public void testInitJobState() throws Exception {
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
        QuartzSchedulerEngineTest.MockScheduler mockScheduler = ReflectionTestUtil.getFieldValue(_quartzSchedulerEngine, "_persistedScheduler");
        mockScheduler.addJob(((QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "persisted"), QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED, null);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), ((QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER) + 1), schedulerResponses.size());
        _quartzSchedulerEngine.initJobState();
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
    }

    @Test
    public void testPauseAndResume1() throws Exception {
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, NORMAL);
        }
        _quartzSchedulerEngine.pause(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, PAUSED);
        }
        _quartzSchedulerEngine.resume(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, NORMAL);
        }
    }

    @Test
    public void testPauseAndResume2() throws Exception {
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
        _quartzSchedulerEngine.pause(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, PAUSED);
        _quartzSchedulerEngine.resume(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
    }

    @Test
    public void testSchedule1() throws Exception {
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
        Trigger trigger = _quartzTriggerFactory.createTrigger(((QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "memory"), QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
        _quartzSchedulerEngine.schedule(trigger, BLANK, QuartzSchedulerEngineTest._TEST_DESTINATION_NAME, new Message(), MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), ((QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER) + 1), schedulerResponses.size());
    }

    @Test
    public void testSchedule2() throws Exception {
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
        Trigger trigger = _quartzTriggerFactory.createTrigger(((QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "memory"), QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
        _quartzSchedulerEngine.schedule(trigger, BLANK, QuartzSchedulerEngineTest._TEST_DESTINATION_NAME, new Message(), MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), ((QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER) + 1), schedulerResponses.size());
    }

    @Test
    public void testSuppressError() throws Exception {
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Message message = schedulerResponse.getMessage();
        JobState jobState = ((JobState) (message.get(JOB_STATE)));
        Assert.assertNotNull(jobState.getExceptions());
        _quartzSchedulerEngine.suppressError(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        message = schedulerResponse.getMessage();
        jobState = ((JobState) (message.get(JOB_STATE)));
        Assert.assertNull(jobState.getExceptions());
    }

    @Test
    public void testUnschedule1() throws Exception {
        // Unschedule memory job
        List<SchedulerResponse> schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertEquals(schedulerResponses.toString(), QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER, schedulerResponses.size());
        _quartzSchedulerEngine.unschedule(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, UNSCHEDULED);
        }
        // Unschedule persisted job
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, NORMAL);
        }
        _quartzSchedulerEngine.unschedule(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        schedulerResponses = _quartzSchedulerEngine.getScheduledJobs(QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        for (SchedulerResponse schedulerResponse : schedulerResponses) {
            assertTriggerState(schedulerResponse, UNSCHEDULED);
        }
    }

    @Test
    public void testUnschedule2() throws Exception {
        // Unschedule memory job
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        assertTriggerState(schedulerResponse, NORMAL);
        _quartzSchedulerEngine.unschedule(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        assertTriggerState(schedulerResponse, UNSCHEDULED);
        // Unschedule persisted job
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, NORMAL);
        _quartzSchedulerEngine.unschedule(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._PERSISTED_TEST_GROUP_NAME, PERSISTED);
        assertTriggerState(schedulerResponse, UNSCHEDULED);
    }

    @Test
    public void testUnschedule3() throws Exception {
        String testJobName = (QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "memory";
        Trigger trigger = _quartzTriggerFactory.createTrigger(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
        _quartzSchedulerEngine.schedule(trigger, BLANK, QuartzSchedulerEngineTest._TEST_DESTINATION_NAME, new Message(), MEMORY);
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        assertTriggerState(schedulerResponse, NORMAL);
        _quartzSchedulerEngine.unschedule(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(testJobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        assertTriggerState(schedulerResponse, UNSCHEDULED);
    }

    @Test
    public void testUpdate1() throws Exception {
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Trigger trigger = schedulerResponse.getTrigger();
        CalendarIntervalTrigger calendarIntervalTrigger = ((CalendarIntervalTrigger) (trigger.getWrappedTrigger()));
        Assert.assertEquals(QuartzSchedulerEngineTest._DEFAULT_INTERVAL, calendarIntervalTrigger.getRepeatInterval());
        Trigger newTrigger = _quartzTriggerFactory.createTrigger(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, ((QuartzSchedulerEngineTest._DEFAULT_INTERVAL) * 2), SECOND);
        _quartzSchedulerEngine.update(newTrigger, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        trigger = schedulerResponse.getTrigger();
        calendarIntervalTrigger = ((CalendarIntervalTrigger) (trigger.getWrappedTrigger()));
        Assert.assertEquals(((QuartzSchedulerEngineTest._DEFAULT_INTERVAL) * 2), calendarIntervalTrigger.getRepeatInterval());
    }

    @Test
    public void testUpdate2() throws Exception {
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Trigger trigger = schedulerResponse.getTrigger();
        CalendarIntervalTrigger calendarIntervalTrigger = ((CalendarIntervalTrigger) (trigger.getWrappedTrigger()));
        Assert.assertEquals(QuartzSchedulerEngineTest._DEFAULT_INTERVAL, calendarIntervalTrigger.getRepeatInterval());
        String cronExpression = "0 0 12 * * ?";
        Trigger newTrigger = _quartzTriggerFactory.createTrigger(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, null, null, cronExpression);
        _quartzSchedulerEngine.update(newTrigger, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        trigger = schedulerResponse.getTrigger();
        CronTrigger cronTrigger = ((CronTrigger) (trigger.getWrappedTrigger()));
        Assert.assertEquals(cronExpression, cronTrigger.getCronExpression());
    }

    @Test
    public void testUpdate3() throws SchedulerException {
        QuartzSchedulerEngineTest.MockScheduler mockScheduler = ReflectionTestUtil.getFieldValue(_quartzSchedulerEngine, "_memoryScheduler");
        String jobName = (QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + "memory";
        mockScheduler.addJob(jobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY, null);
        SchedulerResponse schedulerResponse = _quartzSchedulerEngine.getScheduledJob(jobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertNull(schedulerResponse.getTrigger());
        Trigger trigger = _quartzTriggerFactory.createTrigger(jobName, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, new Date(), null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
        _quartzSchedulerEngine.update(trigger, MEMORY);
        schedulerResponse = _quartzSchedulerEngine.getScheduledJob(QuartzSchedulerEngineTest._TEST_JOB_NAME_0, QuartzSchedulerEngineTest._MEMORY_TEST_GROUP_NAME, MEMORY);
        Assert.assertNotNull(schedulerResponse.getTrigger());
    }

    public static class TestMessageListener implements MessageListener {
        @Override
        public void receive(Message message) {
        }
    }

    private static final int _DEFAULT_INTERVAL = 10;

    private static final int _DEFAULT_JOB_NUMBER = 3;

    private static final String _MEMORY_TEST_GROUP_NAME = "memory.test.group";

    private static final String _PERSISTED_TEST_GROUP_NAME = "persisted.test.group";

    private static final String _TEST_DESTINATION_NAME = "liferay/test";

    private static final String _TEST_JOB_NAME_0 = "test.job.0";

    private static final String _TEST_JOB_NAME_PREFIX = "test.job.";

    private JSONFactory _jsonFactory;

    private QuartzSchedulerEngine _quartzSchedulerEngine;

    private final QuartzTriggerFactory _quartzTriggerFactory = new QuartzTriggerFactory();

    private class MockScheduler implements Scheduler {
        public MockScheduler(StorageType storageType, String defaultGroupName) {
            for (int i = 0; i < (QuartzSchedulerEngineTest._DEFAULT_JOB_NUMBER); i++) {
                Trigger trigger = _quartzTriggerFactory.createTrigger(((QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + i), defaultGroupName, null, null, QuartzSchedulerEngineTest._DEFAULT_INTERVAL, SECOND);
                addJob(((QuartzSchedulerEngineTest._TEST_JOB_NAME_PREFIX) + i), defaultGroupName, storageType, ((org.quartz.Trigger) (trigger.getWrappedTrigger())));
            }
        }

        @Override
        public void addCalendar(String name, Calendar calendar, boolean replace, boolean updateTriggers) {
        }

        @Override
        public void addJob(JobDetail jobDetail, boolean replace) {
            _jobs.put(jobDetail.getKey(), new Tuple(jobDetail, null, TriggerState.UNSCHEDULED));
        }

        public final void addJob(String jobName, String groupName, StorageType storageType, org.quartz.Trigger trigger) {
            JobKey jobKey = new JobKey(jobName, groupName);
            JobBuilder jobBuilder = JobBuilder.newJob(MessageSenderJob.class);
            jobBuilder = jobBuilder.withIdentity(jobKey);
            JobDetail jobDetail = jobBuilder.build();
            JobDataMap jobDataMap = jobDetail.getJobDataMap();
            jobDataMap.put(MESSAGE, _jsonFactory.serialize(new Message()));
            jobDataMap.put(DESTINATION_NAME, QuartzSchedulerEngineTest._TEST_DESTINATION_NAME);
            jobDataMap.put(STORAGE_TYPE, storageType.toString());
            JobState jobState = new JobState(TriggerState.NORMAL);
            jobState.addException(new Exception(), new Date());
            jobDataMap.put(JOB_STATE, JobStateSerializeUtil.serialize(jobState));
            _jobs.put(jobKey, new Tuple(jobDetail, trigger, TriggerState.NORMAL));
        }

        @Override
        public boolean checkExists(JobKey jobKey) {
            return false;
        }

        @Override
        public boolean checkExists(TriggerKey triggerKey) {
            return false;
        }

        @Override
        public void clear() {
        }

        @Override
        public boolean deleteCalendar(String name) {
            return false;
        }

        @Override
        public boolean deleteJob(JobKey jobKey) {
            _jobs.remove(jobKey);
            return true;
        }

        @Override
        public boolean deleteJobs(List<JobKey> jobKeys) {
            return false;
        }

        @Override
        public Calendar getCalendar(String name) {
            return null;
        }

        @Override
        public List<String> getCalendarNames() {
            return Collections.emptyList();
        }

        @Override
        public SchedulerContext getContext() {
            return null;
        }

        @Override
        public List<JobExecutionContext> getCurrentlyExecutingJobs() {
            return Collections.emptyList();
        }

        @Override
        public JobDetail getJobDetail(JobKey jobKey) {
            Tuple tuple = _jobs.get(jobKey);
            if (tuple == null) {
                return null;
            }
            return ((JobDetail) (tuple.getObject(0)));
        }

        @Override
        public List<String> getJobGroupNames() {
            List<String> groupNames = new ArrayList<>();
            for (JobKey jobKey : _jobs.keySet()) {
                if (!(groupNames.contains(jobKey.getGroup()))) {
                    groupNames.add(jobKey.getGroup());
                }
            }
            return groupNames;
        }

        @Override
        public Set<JobKey> getJobKeys(GroupMatcher<JobKey> groupMatcher) {
            String groupName = groupMatcher.getCompareToValue();
            Set<JobKey> jobKeys = new HashSet<>();
            for (JobKey jobKey : _jobs.keySet()) {
                if (Objects.equals(jobKey.getGroup(), groupName)) {
                    jobKeys.add(jobKey);
                }
            }
            return jobKeys;
        }

        @Override
        public ListenerManager getListenerManager() {
            return null;
        }

        @Override
        public SchedulerMetaData getMetaData() {
            return null;
        }

        @Override
        public Set<String> getPausedTriggerGroups() {
            return null;
        }

        @Override
        public String getSchedulerInstanceId() {
            return null;
        }

        @Override
        public String getSchedulerName() {
            return null;
        }

        @Override
        public Trigger getTrigger(TriggerKey triggerKey) {
            Tuple tuple = _jobs.get(new JobKey(triggerKey.getName(), triggerKey.getGroup()));
            if (tuple == null) {
                return null;
            }
            return ((org.quartz.Trigger) (tuple.getObject(1)));
        }

        @Override
        public List<String> getTriggerGroupNames() {
            return Collections.emptyList();
        }

        @Override
        public Set<TriggerKey> getTriggerKeys(GroupMatcher<TriggerKey> groupMatcher) {
            return null;
        }

        @Override
        public List<? extends org.quartz.Trigger> getTriggersOfJob(JobKey jobKey) {
            return Collections.emptyList();
        }

        @Override
        public TriggerState getTriggerState(TriggerKey triggerKey) {
            return null;
        }

        @Override
        public boolean interrupt(JobKey jobKey) {
            return false;
        }

        @Override
        public boolean interrupt(String fireInstanceId) {
            return false;
        }

        @Override
        public boolean isInStandbyMode() {
            return false;
        }

        @Override
        public boolean isShutdown() {
            if (!(_ready)) {
                return true;
            }
            return false;
        }

        @Override
        public boolean isStarted() {
            return _ready;
        }

        @Override
        public void pauseAll() {
        }

        @Override
        public void pauseJob(JobKey jobKey) {
            Tuple tuple = _jobs.get(jobKey);
            if (tuple == null) {
                return;
            }
            _jobs.put(jobKey, new Tuple(tuple.getObject(0), tuple.getObject(1), TriggerState.PAUSED));
        }

        @Override
        public void pauseJobs(GroupMatcher<JobKey> groupMatcher) {
            String groupName = groupMatcher.getCompareToValue();
            for (JobKey jobKey : _jobs.keySet()) {
                if (Objects.equals(jobKey.getGroup(), groupName)) {
                    pauseJob(jobKey);
                }
            }
        }

        @Override
        public void pauseTrigger(TriggerKey triggerKey) {
        }

        @Override
        public void pauseTriggers(GroupMatcher<TriggerKey> groupMatcher) {
        }

        @Override
        public Date rescheduleJob(TriggerKey triggerKey, org.quartz.Trigger trigger) {
            JobKey jobKey = new JobKey(triggerKey.getName(), triggerKey.getGroup());
            Tuple tuple = _jobs.get(jobKey);
            if (tuple == null) {
                return null;
            }
            _jobs.put(jobKey, new Tuple(tuple.getObject(0), trigger, tuple.getObject(2)));
            return null;
        }

        @Override
        public void resumeAll() {
        }

        @Override
        public void resumeJob(JobKey jobKey) {
            Tuple tuple = _jobs.get(jobKey);
            if (tuple == null) {
                return;
            }
            _jobs.put(jobKey, new Tuple(tuple.getObject(0), tuple.getObject(1), TriggerState.NORMAL));
        }

        @Override
        public void resumeJobs(GroupMatcher<JobKey> groupMatcher) {
            String groupName = groupMatcher.getCompareToValue();
            for (JobKey jobKey : _jobs.keySet()) {
                if (Objects.equals(jobKey.getGroup(), groupName)) {
                    resumeJob(jobKey);
                }
            }
        }

        @Override
        public void resumeTrigger(TriggerKey triggerKey) {
        }

        @Override
        public void resumeTriggers(GroupMatcher<TriggerKey> groupMatcher) {
        }

        @Override
        public Date scheduleJob(JobDetail jobDetail, org.quartz.Trigger trigger) {
            _jobs.put(jobDetail.getKey(), new Tuple(jobDetail, trigger, TriggerState.NORMAL));
            return null;
        }

        @Override
        public Date scheduleJob(org.quartz.Trigger trigger) {
            return null;
        }

        @Override
        public void scheduleJobs(Map<JobDetail, List<org.quartz.Trigger>> map, boolean replace) {
        }

        @Override
        public void setJobFactory(JobFactory jobFactory) {
        }

        @Override
        public void shutdown() {
            _ready = false;
        }

        @Override
        public void shutdown(boolean waitForJobsToComplete) {
            _ready = false;
        }

        @Override
        public void standby() {
        }

        @Override
        public void start() {
            _ready = true;
        }

        @Override
        public void startDelayed(int seconds) {
        }

        @Override
        public void triggerJob(JobKey jobKey) {
        }

        @Override
        public void triggerJob(JobKey jobKey, JobDataMap jobDataMap) {
        }

        @Override
        public boolean unscheduleJob(TriggerKey triggerKey) {
            _jobs.remove(new JobKey(triggerKey.getName(), triggerKey.getGroup()));
            return true;
        }

        @Override
        public boolean unscheduleJobs(List<TriggerKey> list) {
            return false;
        }

        private final Map<JobKey, Tuple> _jobs = new HashMap<>();

        private boolean _ready;
    }
}

