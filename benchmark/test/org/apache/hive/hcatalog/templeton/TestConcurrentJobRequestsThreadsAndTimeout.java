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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.templeton;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/* Test submission of concurrent job requests with the controlled number of concurrent
Requests and job request execution time outs. Verify that we get appropriate exceptions
and exception message.
 */
public class TestConcurrentJobRequestsThreadsAndTimeout extends ConcurrentJobRequestsTestBase {
    private static AppConfig config;

    private static QueueStatusBean statusBean;

    private static String statusTooManyRequestsExceptionMessage;

    private static String listTooManyRequestsExceptionMessage;

    private static String submitTooManyRequestsExceptionMessage;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void ConcurrentJobsStatusTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentJobsStatus(6, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, statusJobHelper.getDelayedResonseAnswer(4, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            verifyTooManyRequestsException(jobRunnable.exception, this.statusTooManyRequestsExceptionMessage);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentListJobsTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentListJobs(6, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, listJobHelper.getDelayedResonseAnswer(4, new ArrayList<JobItemBean>()));
            verifyTooManyRequestsException(jobRunnable.exception, this.listTooManyRequestsExceptionMessage);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentSubmitJobsTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = SubmitConcurrentJobs(6, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(4, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1000");
            verifyTooManyRequestsException(jobRunnable.exception, this.submitTooManyRequestsExceptionMessage);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentJobsStatusTimeOutException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, statusJobHelper.getDelayedResonseAnswer(6, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof TimeoutException));
            String expectedMessage = "Status job request got timed out. Please wait for some time before " + ("retrying the operation. Please refer to the config " + "templeton.job.status.timeout to configure job request time out.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Verify that new job requests should succeed with no issues. */
            jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, statusJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentListJobsTimeOutException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, listJobHelper.getDelayedResonseAnswer(6, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof TimeoutException));
            String expectedMessage = "List job request got timed out. Please wait for some time before " + ("retrying the operation. Please refer to the config " + "templeton.job.list.timeout to configure job request time out.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Verify that new job requests should succeed with no issues. */
            jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, listJobHelper.getDelayedResonseAnswer(1, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentSubmitJobsTimeOutException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = SubmitConcurrentJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(6, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1000");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof QueueException));
            String expectedMessage = "Submit job request got timed out. Please wait for some time before " + ("retrying the operation. Please refer to the config " + "templeton.job.submit.timeout to configure job request time out.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* For submit operation, tasks are not cancelled. Verify that new job request
            should fail with TooManyRequestsException.
             */
            jobRunnable = SubmitConcurrentJobs(1, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(0, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1000");
            verifyTooManyRequestsException(jobRunnable.exception, this.submitTooManyRequestsExceptionMessage);
            /* Sleep until all threads with clean up tasks are completed. */
            Thread.sleep(2000);
            /* Now, tasks would have passed. Verify that new job requests should succeed with no issues. */
            jobRunnable = SubmitConcurrentJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(0, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1000");
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentStatusJobsVerifyExceptions() {
        try {
            /* Trigger kill threads and verify we get InterruptedException and expected Message. */
            int timeoutTaskDelay = 4;
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, true, false, statusJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof InterruptedException));
            String expectedMessage = "Status job request got interrupted. Please wait for some time before " + "retrying the operation.";
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Interrupt all thread and verify we get InterruptedException and expected Message. */
            jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, true, statusJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof InterruptedException));
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Raise custom exception like IOException and verify expected Message. */
            jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, statusJobHelper.getIOExceptionAnswer());
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception.getCause()) instanceof IOException));
            /* Now new job requests should succeed as status operation has no cancel threads. */
            jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, statusJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentListJobsVerifyExceptions() {
        try {
            /* Trigger kill threads and verify we get InterruptedException and expected Message. */
            int timeoutTaskDelay = 4;
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, true, false, listJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof InterruptedException));
            String expectedMessage = "List job request got interrupted. Please wait for some time before " + "retrying the operation.";
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Interrupt all thread and verify we get InterruptedException and expected Message. */
            jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, true, listJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof InterruptedException));
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Raise custom exception like IOException and verify expected Message. */
            jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, listJobHelper.getIOExceptionAnswer());
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception.getCause()) instanceof IOException));
            /* Now new job requests should succeed as list operation has no cancel threads. */
            jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, listJobHelper.getDelayedResonseAnswer(0, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentSubmitJobsVerifyExceptions() {
        try {
            int timeoutTaskDelay = 4;
            /* Raise custom exception like IOException and verify expected Message.
            This should not invoke cancel operation.
             */
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = SubmitConcurrentJobs(1, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getIOExceptionAnswer(), killJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1002");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof QueueException));
            Assert.assertTrue(jobRunnable.exception.getMessage().contains("IOException raised manually."));
            /* Raise custom exception like IOException and verify expected Message.
            This should not invoke cancel operation.
             */
            jobRunnable = SubmitConcurrentJobs(1, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getOutOfMemoryErrorAnswer(), killJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1003");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof QueueException));
            Assert.assertTrue(jobRunnable.exception.getMessage().contains("OutOfMemoryError raised manually."));
            /* Trigger kill threads and verify that we get InterruptedException and expected
            Message. This should raise 3 kill operations and ensure that retries keep the time out
            occupied for 4 sec.
             */
            jobRunnable = SubmitConcurrentJobs(3, TestConcurrentJobRequestsThreadsAndTimeout.config, true, false, submitJobHelper.getDelayedResonseAnswer(2, 0), killJobHelper.getDelayedResonseAnswer(timeoutTaskDelay, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1000");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof QueueException));
            String expectedMessage = "Submit job request got interrupted. Please wait for some time " + "before retrying the operation.";
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Interrupt all threads and verify we get InterruptedException and expected
            Message. Also raise 2 kill operations and ensure that retries keep the time out
            occupied for 4 sec.
             */
            jobRunnable = SubmitConcurrentJobs(2, TestConcurrentJobRequestsThreadsAndTimeout.config, false, true, submitJobHelper.getDelayedResonseAnswer(2, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1001");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof QueueException));
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* For submit operation, tasks are not cancelled. Verify that new job request
            should fail with TooManyRequestsException.
             */
            jobRunnable = SubmitConcurrentJobs(1, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(0, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1002");
            verifyTooManyRequestsException(jobRunnable.exception, this.submitTooManyRequestsExceptionMessage);
            /* Sleep until all threads with clean up tasks are completed. 2 seconds completing task
            and 1 sec grace period.
             */
            Thread.sleep((((timeoutTaskDelay + 2) + 1) * 1000));
            /* Now new job requests should succeed as all cancel threads would have completed. */
            jobRunnable = SubmitConcurrentJobs(5, TestConcurrentJobRequestsThreadsAndTimeout.config, false, false, submitJobHelper.getDelayedResonseAnswer(0, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreadsAndTimeout.statusBean), "job_1004");
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}

