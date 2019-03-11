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
package org.apache.flink.runtime.rest.handler.job.savepoints;


import HttpResponseStatus.BAD_REQUEST;
import QueueStatus.Id.COMPLETED;
import SavepointHandlers.SavepointStatusHandler;
import SavepointHandlers.SavepointTriggerHandler;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.messages.TriggerId;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointInfo;
import org.apache.flink.runtime.webmonitor.RestfulGateway;
import org.apache.flink.runtime.webmonitor.TestingRestfulGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for {@link SavepointHandlers}.
 */
public class SavepointHandlersTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10);

    private static final JobID JOB_ID = new JobID();

    private static final String COMPLETED_SAVEPOINT_EXTERNAL_POINTER = "/tmp/savepoint-0d2fb9-8d5e0106041a";

    private static final String DEFAULT_REQUESTED_SAVEPOINT_TARGET_DIRECTORY = "/tmp";

    private SavepointTriggerHandler savepointTriggerHandler;

    private SavepointStatusHandler savepointStatusHandler;

    private GatewayRetriever<RestfulGateway> leaderRetriever;

    @Test
    public void testSavepointCompletedSuccessfully() throws Exception {
        final TestingRestfulGateway testingRestfulGateway = new TestingRestfulGateway.Builder().setTriggerSavepointFunction((JobID jobId,String targetDirectory) -> CompletableFuture.completedFuture(SavepointHandlersTest.COMPLETED_SAVEPOINT_EXTERNAL_POINTER)).build();
        final TriggerId triggerId = savepointTriggerHandler.handleRequest(SavepointHandlersTest.triggerSavepointRequest(), testingRestfulGateway).get().getTriggerId();
        AsynchronousOperationResult<SavepointInfo> savepointResponseBody;
        savepointResponseBody = savepointStatusHandler.handleRequest(SavepointHandlersTest.savepointStatusRequest(triggerId), testingRestfulGateway).get();
        Assert.assertThat(savepointResponseBody.queueStatus().getId(), Matchers.equalTo(COMPLETED));
        Assert.assertThat(savepointResponseBody.resource(), Matchers.notNullValue());
        Assert.assertThat(savepointResponseBody.resource().getLocation(), Matchers.equalTo(SavepointHandlersTest.COMPLETED_SAVEPOINT_EXTERNAL_POINTER));
    }

    @Test
    public void testTriggerSavepointWithDefaultDirectory() throws Exception {
        final CompletableFuture<String> targetDirectoryFuture = new CompletableFuture<>();
        final TestingRestfulGateway testingRestfulGateway = new TestingRestfulGateway.Builder().setTriggerSavepointFunction((JobID jobId,String targetDirectory) -> {
            targetDirectoryFuture.complete(targetDirectory);
            return CompletableFuture.completedFuture(SavepointHandlersTest.COMPLETED_SAVEPOINT_EXTERNAL_POINTER);
        }).build();
        final String defaultSavepointDir = "/other/dir";
        final SavepointHandlers savepointHandlers = new SavepointHandlers(defaultSavepointDir);
        final SavepointHandlers.SavepointTriggerHandler savepointTriggerHandler = savepointHandlers.new SavepointTriggerHandler(leaderRetriever, SavepointHandlersTest.TIMEOUT, Collections.emptyMap());
        savepointTriggerHandler.handleRequest(SavepointHandlersTest.triggerSavepointRequestWithDefaultDirectory(), testingRestfulGateway).get();
        Assert.assertThat(targetDirectoryFuture.get(), Matchers.equalTo(defaultSavepointDir));
    }

    @Test
    public void testTriggerSavepointNoDirectory() throws Exception {
        TestingRestfulGateway testingRestfulGateway = new TestingRestfulGateway.Builder().setTriggerSavepointFunction((JobID jobId,String directory) -> CompletableFuture.completedFuture(SavepointHandlersTest.COMPLETED_SAVEPOINT_EXTERNAL_POINTER)).build();
        try {
            savepointTriggerHandler.handleRequest(SavepointHandlersTest.triggerSavepointRequestWithDefaultDirectory(), testingRestfulGateway).get();
            Assert.fail("Expected exception not thrown.");
        } catch (RestHandlerException rhe) {
            Assert.assertThat(rhe.getMessage(), Matchers.equalTo(("Config key [state.savepoints.dir] is not set. " + "Property [target-directory] must be provided.")));
            Assert.assertThat(rhe.getHttpResponseStatus(), Matchers.equalTo(BAD_REQUEST));
        }
    }

    @Test
    public void testSavepointCompletedWithException() throws Exception {
        TestingRestfulGateway testingRestfulGateway = new TestingRestfulGateway.Builder().setTriggerSavepointFunction((JobID jobId,String directory) -> FutureUtils.completedExceptionally(new RuntimeException("expected"))).build();
        final TriggerId triggerId = savepointTriggerHandler.handleRequest(SavepointHandlersTest.triggerSavepointRequest(), testingRestfulGateway).get().getTriggerId();
        final AsynchronousOperationResult<SavepointInfo> savepointResponseBody = savepointStatusHandler.handleRequest(SavepointHandlersTest.savepointStatusRequest(triggerId), testingRestfulGateway).get();
        Assert.assertThat(savepointResponseBody.queueStatus().getId(), Matchers.equalTo(COMPLETED));
        Assert.assertThat(savepointResponseBody.resource(), Matchers.notNullValue());
        Assert.assertThat(savepointResponseBody.resource().getFailureCause(), Matchers.notNullValue());
        final Throwable savepointError = savepointResponseBody.resource().getFailureCause().deserializeError(ClassLoader.getSystemClassLoader());
        Assert.assertThat(savepointError.getMessage(), Matchers.equalTo("expected"));
        Assert.assertThat(savepointError, Matchers.instanceOf(RuntimeException.class));
    }
}

