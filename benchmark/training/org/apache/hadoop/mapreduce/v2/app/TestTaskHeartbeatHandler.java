/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.mapreduce.v2.app;


import MRJobConfig.TASK_EXIT_TIMEOUT;
import MRJobConfig.TASK_EXIT_TIMEOUT_DEFAULT;
import MRJobConfig.TASK_PROGRESS_REPORT_INTERVAL;
import MRJobConfig.TASK_STUCK_TIMEOUT_MS;
import MRJobConfig.TASK_TIMEOUT;
import MRJobConfig.TASK_TIMEOUT_CHECK_INTERVAL_MS;
import TaskHeartbeatHandler.ReportTime;
import TaskType.MAP;
import com.google.common.base.Supplier;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestTaskHeartbeatHandler {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testTaskTimeout() throws InterruptedException {
        EventHandler mockHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        TaskHeartbeatHandler hb = new TaskHeartbeatHandler(mockHandler, clock, 1);
        Configuration conf = new Configuration();
        conf.setInt(TASK_TIMEOUT, 10);// 10 ms

        // set TASK_PROGRESS_REPORT_INTERVAL to a value smaller than TASK_TIMEOUT
        // so that TASK_TIMEOUT is not overridden
        conf.setLong(TASK_PROGRESS_REPORT_INTERVAL, 5);
        conf.setInt(TASK_TIMEOUT_CHECK_INTERVAL_MS, 10);// 10 ms

        hb.init(conf);
        hb.start();
        try {
            ApplicationId appId = ApplicationId.newInstance(0L, 5);
            JobId jobId = MRBuilderUtils.newJobId(appId, 4);
            TaskId tid = MRBuilderUtils.newTaskId(jobId, 3, MAP);
            TaskAttemptId taid = MRBuilderUtils.newTaskAttemptId(tid, 2);
            hb.register(taid);
            // Task heartbeat once to avoid stuck
            hb.progressing(taid);
            Thread.sleep(100);
            // Events only happen when the task is canceled
            Mockito.verify(mockHandler, Mockito.times(2)).handle(ArgumentMatchers.any(Event.class));
        } finally {
            hb.stop();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testTaskStuck() throws InterruptedException {
        EventHandler mockHandler = Mockito.mock(EventHandler.class);
        Clock clock = SystemClock.getInstance();
        TaskHeartbeatHandler hb = new TaskHeartbeatHandler(mockHandler, clock, 1);
        Configuration conf = new Configuration();
        conf.setLong(TASK_STUCK_TIMEOUT_MS, 10);// 10ms

        conf.setInt(TASK_TIMEOUT, 1000);// 1000 ms

        // set TASK_PROGRESS_REPORT_INTERVAL to a value smaller than TASK_TIMEOUT
        // so that TASK_TIMEOUT is not overridden
        conf.setLong(TASK_PROGRESS_REPORT_INTERVAL, 5);
        conf.setInt(TASK_TIMEOUT_CHECK_INTERVAL_MS, 10);// 10 ms

        hb.init(conf);
        hb.start();
        try {
            ApplicationId appId = ApplicationId.newInstance(0L, 5);
            JobId jobId = MRBuilderUtils.newJobId(appId, 4);
            TaskId tid = MRBuilderUtils.newTaskId(jobId, 3, MAP);
            TaskAttemptId taid = MRBuilderUtils.newTaskAttemptId(tid, 2);
            hb.register(taid);
            ConcurrentMap<TaskAttemptId, TaskHeartbeatHandler.ReportTime> runningAttempts = hb.getRunningAttempts();
            for (Map.Entry<TaskAttemptId, TaskHeartbeatHandler.ReportTime> entry : runningAttempts.entrySet()) {
                Assert.assertFalse(entry.getValue().isReported());
            }
            Thread.sleep(100);
            // Events only happen when the task is canceled
            Mockito.verify(mockHandler, Mockito.times(2)).handle(ArgumentMatchers.any(Event.class));
        } finally {
            hb.stop();
        }
    }

    /**
     * Test if the final heartbeat timeout is set correctly when task progress
     * report interval is set bigger than the task timeout in the configuration.
     */
    @Test
    public void testTaskTimeoutConfigSmallerThanTaskProgressReportInterval() {
        TestTaskHeartbeatHandler.testTaskTimeoutWrtProgressReportInterval(1000L, 5000L);
    }

    /**
     * Test if the final heartbeat timeout is set correctly when task progress
     * report interval is set smaller than the task timeout in the configuration.
     */
    @Test
    public void testTaskTimeoutConfigBiggerThanTaskProgressReportInterval() {
        TestTaskHeartbeatHandler.testTaskTimeoutWrtProgressReportInterval(5000L, 1000L);
    }

    /**
     * Test if the final heartbeat timeout is set correctly when task progress
     * report interval is not set in the configuration.
     */
    @Test
    public void testTaskTimeoutConfigWithoutTaskProgressReportInterval() {
        final long taskTimeoutConfiged = 2000L;
        final Configuration conf = new Configuration();
        conf.setLong(TASK_TIMEOUT, taskTimeoutConfiged);
        final long expectedTimeout = taskTimeoutConfiged;
        TestTaskHeartbeatHandler.verifyTaskTimeoutConfig(conf, expectedTimeout);
    }

    @Test
    public void testTaskUnregistered() throws Exception {
        EventHandler mockHandler = Mockito.mock(EventHandler.class);
        ControlledClock clock = new ControlledClock();
        clock.setTime(0);
        final TaskHeartbeatHandler hb = new TaskHeartbeatHandler(mockHandler, clock, 1);
        Configuration conf = new Configuration();
        conf.setInt(TASK_TIMEOUT_CHECK_INTERVAL_MS, 1);
        hb.init(conf);
        hb.start();
        try {
            ApplicationId appId = ApplicationId.newInstance(0L, 5);
            JobId jobId = MRBuilderUtils.newJobId(appId, 4);
            TaskId tid = MRBuilderUtils.newTaskId(jobId, 3, MAP);
            final TaskAttemptId taid = MRBuilderUtils.newTaskAttemptId(tid, 2);
            Assert.assertFalse(hb.hasRecentlyUnregistered(taid));
            hb.register(taid);
            Assert.assertFalse(hb.hasRecentlyUnregistered(taid));
            hb.unregister(taid);
            Assert.assertTrue(hb.hasRecentlyUnregistered(taid));
            long unregisterTimeout = conf.getLong(TASK_EXIT_TIMEOUT, TASK_EXIT_TIMEOUT_DEFAULT);
            clock.setTime((unregisterTimeout + 1));
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    return !(hb.hasRecentlyUnregistered(taid));
                }
            }, 10, 10000);
        } finally {
            hb.stop();
        }
    }
}

