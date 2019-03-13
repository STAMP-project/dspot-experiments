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
package org.apache.flink.runtime.rest.handler.job;


import HttpResponseStatus.NOT_FOUND;
import JobStatus.RUNNING;
import QueueStatus.Id.COMPLETED;
import QueueStatus.Id.IN_PROGRESS;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.handler.legacy.utils.ArchivedExecutionGraphBuilder;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.JobMessageParameters;
import org.apache.flink.runtime.rest.messages.job.JobExecutionResultResponseBody;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link JobExecutionResultHandler}.
 */
public class JobExecutionResultHandlerTest extends TestLogger {
    private static final JobID TEST_JOB_ID = new JobID();

    private JobExecutionResultHandler jobExecutionResultHandler;

    private HandlerRequest<EmptyRequestBody, JobMessageParameters> testRequest;

    @Test
    public void testResultInProgress() throws Exception {
        final TestingRestfulGateway testingRestfulGateway = TestingRestfulGateway.newBuilder().setRequestJobStatusFunction(( jobId) -> CompletableFuture.completedFuture(RUNNING)).build();
        final JobExecutionResultResponseBody responseBody = jobExecutionResultHandler.handleRequest(testRequest, testingRestfulGateway).get();
        Assert.assertThat(responseBody.getStatus().getId(), Matchers.equalTo(IN_PROGRESS));
    }

    @Test
    public void testCompletedResult() throws Exception {
        final JobStatus jobStatus = JobStatus.FINISHED;
        final ArchivedExecutionGraph executionGraph = new ArchivedExecutionGraphBuilder().setJobID(JobExecutionResultHandlerTest.TEST_JOB_ID).setState(jobStatus).build();
        final TestingRestfulGateway testingRestfulGateway = TestingRestfulGateway.newBuilder().setRequestJobStatusFunction(( jobId) -> {
            Assert.assertThat(jobId, Matchers.equalTo(JobExecutionResultHandlerTest.TEST_JOB_ID));
            return CompletableFuture.completedFuture(jobStatus);
        }).setRequestJobResultFunction(( jobId) -> {
            Assert.assertThat(jobId, Matchers.equalTo(JobExecutionResultHandlerTest.TEST_JOB_ID));
            return CompletableFuture.completedFuture(JobResult.createFrom(executionGraph));
        }).build();
        final JobExecutionResultResponseBody responseBody = jobExecutionResultHandler.handleRequest(testRequest, testingRestfulGateway).get();
        Assert.assertThat(responseBody.getStatus().getId(), Matchers.equalTo(COMPLETED));
        Assert.assertThat(responseBody.getJobExecutionResult(), Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void testPropagateFlinkJobNotFoundExceptionAsRestHandlerException() throws Exception {
        final TestingRestfulGateway testingRestfulGateway = TestingRestfulGateway.newBuilder().setRequestJobStatusFunction(( jobId) -> FutureUtils.completedExceptionally(new org.apache.flink.runtime.messages.FlinkJobNotFoundException(jobId))).build();
        try {
            jobExecutionResultHandler.handleRequest(testRequest, testingRestfulGateway).get();
            Assert.fail("Expected exception not thrown");
        } catch (final ExecutionException e) {
            final Throwable cause = ExceptionUtils.stripCompletionException(e.getCause());
            Assert.assertThat(cause, Matchers.instanceOf(RestHandlerException.class));
            Assert.assertThat(getHttpResponseStatus(), Matchers.equalTo(NOT_FOUND));
        }
    }
}

