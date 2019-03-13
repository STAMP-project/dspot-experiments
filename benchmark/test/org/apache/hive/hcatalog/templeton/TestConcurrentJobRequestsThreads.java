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


import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static TooManyRequestsException.TOO_MANY_REQUESTS_429;


/* Test submission of concurrent job requests with the controlled number of concurrent
Requests. Verify that we get busy exception and appropriate message.
 */
public class TestConcurrentJobRequestsThreads extends ConcurrentJobRequestsTestBase {
    private static AppConfig config;

    private static QueueStatusBean statusBean;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void ConcurrentJobsStatusTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentJobsStatus(6, TestConcurrentJobRequestsThreads.config, false, false, statusJobHelper.getDelayedResonseAnswer(4, TestConcurrentJobRequestsThreads.statusBean));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof TooManyRequestsException));
            TooManyRequestsException ex = ((TooManyRequestsException) (jobRunnable.exception));
            Assert.assertTrue(((ex.httpCode) == (TOO_MANY_REQUESTS_429)));
            String expectedMessage = "Unable to service the status job request as templeton service is busy " + (("with too many status job requests. Please wait for some time before " + "retrying the operation. Please refer to the config ") + "templeton.parallellism.job.status to configure concurrent requests.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Verify that new job requests have no issues. */
            jobRunnable = ConcurrentJobsStatus(5, TestConcurrentJobRequestsThreads.config, false, false, statusJobHelper.getDelayedResonseAnswer(4, TestConcurrentJobRequestsThreads.statusBean));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentListJobsTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = ConcurrentListJobs(6, TestConcurrentJobRequestsThreads.config, false, false, listJobHelper.getDelayedResonseAnswer(4, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof TooManyRequestsException));
            TooManyRequestsException ex = ((TooManyRequestsException) (jobRunnable.exception));
            Assert.assertTrue(((ex.httpCode) == (TOO_MANY_REQUESTS_429)));
            String expectedMessage = "Unable to service the list job request as templeton service is busy " + (("with too many list job requests. Please wait for some time before " + "retrying the operation. Please refer to the config ") + "templeton.parallellism.job.list to configure concurrent requests.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Verify that new job requests have no issues. */
            jobRunnable = ConcurrentListJobs(5, TestConcurrentJobRequestsThreads.config, false, false, listJobHelper.getDelayedResonseAnswer(4, new ArrayList<JobItemBean>()));
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }

    @Test
    public void ConcurrentSubmitJobsTooManyRequestsException() {
        try {
            ConcurrentJobRequestsTestBase.JobRunnable jobRunnable = SubmitConcurrentJobs(6, TestConcurrentJobRequestsThreads.config, false, false, submitJobHelper.getDelayedResonseAnswer(4, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreads.statusBean), "job_1000");
            Assert.assertTrue(((jobRunnable.exception) != null));
            Assert.assertTrue(((jobRunnable.exception) instanceof TooManyRequestsException));
            TooManyRequestsException ex = ((TooManyRequestsException) (jobRunnable.exception));
            Assert.assertTrue(((ex.httpCode) == (TOO_MANY_REQUESTS_429)));
            String expectedMessage = "Unable to service the submit job request as templeton service is busy " + (("with too many submit job requests. Please wait for some time before " + "retrying the operation. Please refer to the config ") + "templeton.parallellism.job.submit to configure concurrent requests.");
            Assert.assertTrue(jobRunnable.exception.getMessage().contains(expectedMessage));
            /* Verify that new job requests have no issues. */
            jobRunnable = SubmitConcurrentJobs(5, TestConcurrentJobRequestsThreads.config, false, false, submitJobHelper.getDelayedResonseAnswer(4, 0), killJobHelper.getDelayedResonseAnswer(0, TestConcurrentJobRequestsThreads.statusBean), "job_1000");
            Assert.assertTrue(((jobRunnable.exception) == null));
        } catch (Exception e) {
            Assert.assertTrue(false);
        }
    }
}

