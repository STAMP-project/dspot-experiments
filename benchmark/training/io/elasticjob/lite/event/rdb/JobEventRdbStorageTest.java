/**
 * Copyright 1999-2015 dangdang.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */
package io.elasticjob.lite.event.rdb;


import JobExecutionEvent.ExecutionSource;
import io.elasticjob.lite.context.ExecutionType;
import io.elasticjob.lite.event.type.JobExecutionEvent;
import io.elasticjob.lite.event.type.JobStatusTraceEvent;
import io.elasticjob.lite.event.type.JobStatusTraceEvent.Source;
import io.elasticjob.lite.event.type.JobStatusTraceEvent.State;
import java.sql.SQLException;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class JobEventRdbStorageTest {
    private JobEventRdbStorage storage;

    @Test
    public void assertAddJobExecutionEvent() throws SQLException {
        TestCase.assertTrue(storage.addJobExecutionEvent(new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0)));
    }

    @Test
    public void assertAddJobStatusTraceEvent() throws SQLException {
        TestCase.assertTrue(storage.addJobStatusTraceEvent(new JobStatusTraceEvent("test_job", "fake_task_id", "fake_slave_id", Source.LITE_EXECUTOR, ExecutionType.READY, "0", State.TASK_RUNNING, "message is empty.")));
    }

    @Test
    public void assertAddJobStatusTraceEventWhenFailoverWithTaskStagingState() throws SQLException {
        JobStatusTraceEvent jobStatusTraceEvent = new JobStatusTraceEvent("test_job", "fake_failover_task_id", "fake_slave_id", Source.LITE_EXECUTOR, ExecutionType.FAILOVER, "0", State.TASK_STAGING, "message is empty.");
        jobStatusTraceEvent.setOriginalTaskId("original_fake_failover_task_id");
        Assert.assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), CoreMatchers.is(0));
        storage.addJobStatusTraceEvent(jobStatusTraceEvent);
        Assert.assertThat(storage.getJobStatusTraceEvents("fake_failover_task_id").size(), CoreMatchers.is(1));
    }

    @Test
    public void assertAddJobStatusTraceEventWhenFailoverWithTaskFailedState() throws SQLException {
        JobStatusTraceEvent stagingJobStatusTraceEvent = new JobStatusTraceEvent("test_job", "fake_failed_failover_task_id", "fake_slave_id", Source.LITE_EXECUTOR, ExecutionType.FAILOVER, "0", State.TASK_STAGING, "message is empty.");
        stagingJobStatusTraceEvent.setOriginalTaskId("original_fake_failed_failover_task_id");
        storage.addJobStatusTraceEvent(stagingJobStatusTraceEvent);
        JobStatusTraceEvent failedJobStatusTraceEvent = new JobStatusTraceEvent("test_job", "fake_failed_failover_task_id", "fake_slave_id", Source.LITE_EXECUTOR, ExecutionType.FAILOVER, "0", State.TASK_FAILED, "message is empty.");
        storage.addJobStatusTraceEvent(failedJobStatusTraceEvent);
        List<JobStatusTraceEvent> jobStatusTraceEvents = storage.getJobStatusTraceEvents("fake_failed_failover_task_id");
        Assert.assertThat(jobStatusTraceEvents.size(), CoreMatchers.is(2));
        for (JobStatusTraceEvent jobStatusTraceEvent : jobStatusTraceEvents) {
            Assert.assertThat(jobStatusTraceEvent.getOriginalTaskId(), CoreMatchers.is("original_fake_failed_failover_task_id"));
        }
    }

    @Test
    public void assertUpdateJobExecutionEventWhenSuccess() throws SQLException {
        JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0);
        TestCase.assertTrue(storage.addJobExecutionEvent(startEvent));
        JobExecutionEvent successEvent = startEvent.executionSuccess();
        TestCase.assertTrue(storage.addJobExecutionEvent(successEvent));
    }

    @Test
    public void assertUpdateJobExecutionEventWhenFailure() throws SQLException {
        JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0);
        TestCase.assertTrue(storage.addJobExecutionEvent(startEvent));
        JobExecutionEvent failureEvent = startEvent.executionFailure(new RuntimeException("failure"));
        TestCase.assertTrue(storage.addJobExecutionEvent(failureEvent));
        Assert.assertThat(failureEvent.getFailureCause(), CoreMatchers.startsWith("java.lang.RuntimeException: failure"));
        TestCase.assertTrue((null != (failureEvent.getCompleteTime())));
    }

    @Test
    public void assertUpdateJobExecutionEventWhenSuccessAndConflict() throws SQLException {
        JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0);
        JobExecutionEvent successEvent = startEvent.executionSuccess();
        TestCase.assertTrue(storage.addJobExecutionEvent(successEvent));
        Assert.assertFalse(storage.addJobExecutionEvent(startEvent));
    }

    @Test
    public void assertUpdateJobExecutionEventWhenFailureAndConflict() throws SQLException {
        JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0);
        JobExecutionEvent failureEvent = startEvent.executionFailure(new RuntimeException("failure"));
        TestCase.assertTrue(storage.addJobExecutionEvent(failureEvent));
        Assert.assertThat(failureEvent.getFailureCause(), CoreMatchers.startsWith("java.lang.RuntimeException: failure"));
        Assert.assertFalse(storage.addJobExecutionEvent(startEvent));
    }

    @Test
    public void assertUpdateJobExecutionEventWhenFailureAndMessageExceed() throws SQLException {
        JobExecutionEvent startEvent = new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0);
        TestCase.assertTrue(storage.addJobExecutionEvent(startEvent));
        StringBuilder failureMsg = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            failureMsg.append(i);
        }
        JobExecutionEvent failEvent = startEvent.executionFailure(new RuntimeException(("failure" + (failureMsg.toString()))));
        TestCase.assertTrue(storage.addJobExecutionEvent(failEvent));
        Assert.assertThat(failEvent.getFailureCause(), CoreMatchers.startsWith("java.lang.RuntimeException: failure"));
    }

    @Test
    public void assertFindJobExecutionEvent() throws SQLException {
        storage.addJobExecutionEvent(new JobExecutionEvent("fake_task_id", "test_job", ExecutionSource.NORMAL_TRIGGER, 0));
    }
}

