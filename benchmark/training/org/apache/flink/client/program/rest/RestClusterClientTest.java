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
package org.apache.flink.client.program.rest;


import JobAccumulatorsInfo.UserTaskAccumulator;
import JobManagerOptions.ADDRESS;
import RestOptions.PORT;
import RestOptions.RETRY_DELAY;
import RestOptions.RETRY_MAX_ATTEMPTS;
import RpcUtils.INF_TIMEOUT;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import org.apache.commons.cli.CommandLine;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.client.cli.DefaultCLI;
import org.apache.flink.client.deployment.StandaloneClusterDescriptor;
import org.apache.flink.client.program.ProgramInvocationException;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.dispatcher.DispatcherGateway;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.messages.webmonitor.JobDetails;
import org.apache.flink.runtime.messages.webmonitor.MultipleJobsDetails;
import org.apache.flink.runtime.rest.HttpMethodWrapper;
import org.apache.flink.runtime.rest.RestServerEndpointConfiguration;
import org.apache.flink.runtime.rest.handler.AbstractRestHandler;
import org.apache.flink.runtime.rest.handler.HandlerRequest;
import org.apache.flink.runtime.rest.handler.RestHandlerException;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationInfo;
import org.apache.flink.runtime.rest.handler.async.AsynchronousOperationResult;
import org.apache.flink.runtime.rest.handler.async.TriggerResponse;
import org.apache.flink.runtime.rest.messages.AccumulatorsIncludeSerializedValueQueryParameter;
import org.apache.flink.runtime.rest.messages.EmptyMessageParameters;
import org.apache.flink.runtime.rest.messages.EmptyRequestBody;
import org.apache.flink.runtime.rest.messages.EmptyResponseBody;
import org.apache.flink.runtime.rest.messages.JobAccumulatorsHeaders;
import org.apache.flink.runtime.rest.messages.JobAccumulatorsInfo;
import org.apache.flink.runtime.rest.messages.JobAccumulatorsMessageParameters;
import org.apache.flink.runtime.rest.messages.JobMessageParameters;
import org.apache.flink.runtime.rest.messages.JobTerminationHeaders;
import org.apache.flink.runtime.rest.messages.JobTerminationMessageParameters;
import org.apache.flink.runtime.rest.messages.JobsOverviewHeaders;
import org.apache.flink.runtime.rest.messages.MessageHeaders;
import org.apache.flink.runtime.rest.messages.MessageParameters;
import org.apache.flink.runtime.rest.messages.RequestBody;
import org.apache.flink.runtime.rest.messages.ResponseBody;
import org.apache.flink.runtime.rest.messages.TerminationModeQueryParameter;
import org.apache.flink.runtime.rest.messages.TriggerId;
import org.apache.flink.runtime.rest.messages.TriggerIdPathParameter;
import org.apache.flink.runtime.rest.messages.job.JobExecutionResultHeaders;
import org.apache.flink.runtime.rest.messages.job.JobExecutionResultResponseBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitHeaders;
import org.apache.flink.runtime.rest.messages.job.JobSubmitRequestBody;
import org.apache.flink.runtime.rest.messages.job.JobSubmitResponseBody;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalRequest;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalStatusHeaders;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalStatusMessageParameters;
import org.apache.flink.runtime.rest.messages.job.savepoints.SavepointDisposalTriggerHeaders;
import org.apache.flink.runtime.rest.util.RestClientException;
import org.apache.flink.runtime.webmonitor.TestingDispatcherGateway;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.shaded.netty4.io.netty.handler.codec.http.HttpResponseStatus;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.OptionalFailure;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.SerializedThrowable;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link RestClusterClient}.
 *
 * <p>These tests verify that the client uses the appropriate headers for each
 * request, properly constructs the request bodies/parameters and processes the responses correctly.
 */
public class RestClusterClientTest extends TestLogger {
    private final DispatcherGateway mockRestfulGateway = new TestingDispatcherGateway.Builder().build();

    private GatewayRetriever<DispatcherGateway> mockGatewayRetriever;

    private RestServerEndpointConfiguration restServerEndpointConfiguration;

    private volatile RestClusterClientTest.FailHttpRequestPredicate failHttpRequest = RestClusterClientTest.FailHttpRequestPredicate.never();

    private ExecutorService executor;

    private JobGraph jobGraph;

    private JobID jobId;

    private static final Configuration restConfig;

    static {
        final Configuration config = new Configuration();
        config.setString(ADDRESS, "localhost");
        config.setInteger(RETRY_MAX_ATTEMPTS, 10);
        config.setLong(RETRY_DELAY, 0);
        config.setInteger(PORT, 0);
        restConfig = config;
    }

    @Test
    public void testJobSubmitCancelStop() throws Exception {
        RestClusterClientTest.TestJobSubmitHandler submitHandler = new RestClusterClientTest.TestJobSubmitHandler();
        RestClusterClientTest.TestJobTerminationHandler terminationHandler = new RestClusterClientTest.TestJobTerminationHandler();
        RestClusterClientTest.TestJobExecutionResultHandler testJobExecutionResultHandler = new RestClusterClientTest.TestJobExecutionResultHandler(JobExecutionResultResponseBody.created(new org.apache.flink.runtime.jobmaster.JobResult.Builder().applicationStatus(ApplicationStatus.SUCCEEDED).jobId(jobId).netRuntime(Long.MAX_VALUE).build()));
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(submitHandler, terminationHandler, testJobExecutionResultHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                Assert.assertFalse(submitHandler.jobSubmitted);
                restClusterClient.submitJob(jobGraph, ClassLoader.getSystemClassLoader());
                Assert.assertTrue(submitHandler.jobSubmitted);
                Assert.assertFalse(terminationHandler.jobCanceled);
                restClusterClient.cancel(jobId);
                Assert.assertTrue(terminationHandler.jobCanceled);
                Assert.assertFalse(terminationHandler.jobStopped);
                restClusterClient.stop(jobId);
                Assert.assertTrue(terminationHandler.jobStopped);
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    /**
     * Tests that we can submit a jobGraph in detached mode.
     */
    @Test
    public void testDetachedJobSubmission() throws Exception {
        final RestClusterClientTest.TestJobSubmitHandler testJobSubmitHandler = new RestClusterClientTest.TestJobSubmitHandler();
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(testJobSubmitHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                restClusterClient.setDetached(true);
                final JobSubmissionResult jobSubmissionResult = restClusterClient.submitJob(jobGraph, ClassLoader.getSystemClassLoader());
                // if the detached mode didn't work, then we would not reach this point because the execution result
                // retrieval would have failed.
                Assert.assertThat(jobSubmissionResult, Matchers.is(Matchers.not(Matchers.instanceOf(JobExecutionResult.class))));
                Assert.assertThat(jobSubmissionResult.getJobID(), Matchers.is(jobId));
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    private class TestJobSubmitHandler extends RestClusterClientTest.TestHandler<JobSubmitRequestBody, JobSubmitResponseBody, EmptyMessageParameters> {
        private volatile boolean jobSubmitted = false;

        private TestJobSubmitHandler() {
            super(JobSubmitHeaders.getInstance());
        }

        @Override
        protected CompletableFuture<JobSubmitResponseBody> handleRequest(@Nonnull
        HandlerRequest<JobSubmitRequestBody, EmptyMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            jobSubmitted = true;
            return CompletableFuture.completedFuture(new JobSubmitResponseBody("/url"));
        }
    }

    private class TestJobTerminationHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, EmptyResponseBody, JobTerminationMessageParameters> {
        private volatile boolean jobCanceled = false;

        private volatile boolean jobStopped = false;

        private TestJobTerminationHandler() {
            super(JobTerminationHeaders.getInstance());
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, JobTerminationMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            switch (request.getQueryParameter(TerminationModeQueryParameter.class).get(0)) {
                case CANCEL :
                    jobCanceled = true;
                    break;
                case STOP :
                    jobStopped = true;
                    break;
            }
            return CompletableFuture.completedFuture(EmptyResponseBody.getInstance());
        }
    }

    private class TestJobExecutionResultHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, JobExecutionResultResponseBody, JobMessageParameters> {
        private final Iterator<Object> jobExecutionResults;

        private Object lastJobExecutionResult;

        private TestJobExecutionResultHandler(final Object... jobExecutionResults) {
            super(JobExecutionResultHeaders.getInstance());
            checkArgument(Arrays.stream(jobExecutionResults).allMatch(( object) -> (object instanceof JobExecutionResultResponseBody) || (object instanceof RestHandlerException)));
            this.jobExecutionResults = Arrays.asList(jobExecutionResults).iterator();
        }

        @Override
        protected CompletableFuture<JobExecutionResultResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, JobMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            if (jobExecutionResults.hasNext()) {
                lastJobExecutionResult = jobExecutionResults.next();
            }
            checkState(((lastJobExecutionResult) != null));
            if ((lastJobExecutionResult) instanceof JobExecutionResultResponseBody) {
                return CompletableFuture.completedFuture(((JobExecutionResultResponseBody) (lastJobExecutionResult)));
            } else
                if ((lastJobExecutionResult) instanceof RestHandlerException) {
                    return FutureUtils.completedExceptionally(((RestHandlerException) (lastJobExecutionResult)));
                } else {
                    throw new AssertionError();
                }

        }
    }

    @Test
    public void testSubmitJobAndWaitForExecutionResult() throws Exception {
        final RestClusterClientTest.TestJobExecutionResultHandler testJobExecutionResultHandler = new RestClusterClientTest.TestJobExecutionResultHandler(new RestHandlerException("should trigger retry", HttpResponseStatus.SERVICE_UNAVAILABLE), JobExecutionResultResponseBody.inProgress(), JobExecutionResultResponseBody.created(new org.apache.flink.runtime.jobmaster.JobResult.Builder().applicationStatus(ApplicationStatus.SUCCEEDED).jobId(jobId).netRuntime(Long.MAX_VALUE).accumulatorResults(Collections.singletonMap("testName", new SerializedValue(OptionalFailure.of(1.0)))).build()), JobExecutionResultResponseBody.created(new org.apache.flink.runtime.jobmaster.JobResult.Builder().applicationStatus(ApplicationStatus.FAILED).jobId(jobId).netRuntime(Long.MAX_VALUE).serializedThrowable(new SerializedThrowable(new RuntimeException("expected"))).build()));
        // fail first HTTP polling attempt, which should not be a problem because of the retries
        final AtomicBoolean firstPollFailed = new AtomicBoolean();
        failHttpRequest = ( messageHeaders, messageParameters, requestBody) -> (messageHeaders instanceof JobExecutionResultHeaders) && (!(firstPollFailed.getAndSet(true)));
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(testJobExecutionResultHandler, new RestClusterClientTest.TestJobSubmitHandler())) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                JobExecutionResult jobExecutionResult;
                jobExecutionResult = ((JobExecutionResult) (restClusterClient.submitJob(jobGraph, ClassLoader.getSystemClassLoader())));
                Assert.assertThat(jobExecutionResult.getJobID(), Matchers.equalTo(jobId));
                Assert.assertThat(jobExecutionResult.getNetRuntime(), Matchers.equalTo(Long.MAX_VALUE));
                Assert.assertThat(jobExecutionResult.getAllAccumulatorResults(), Matchers.equalTo(Collections.singletonMap("testName", 1.0)));
                try {
                    restClusterClient.submitJob(jobGraph, ClassLoader.getSystemClassLoader());
                    Assert.fail("Expected exception not thrown.");
                } catch (final ProgramInvocationException e) {
                    final Optional<RuntimeException> cause = ExceptionUtils.findThrowable(e, RuntimeException.class);
                    Assert.assertThat(cause.isPresent(), Matchers.is(true));
                    Assert.assertThat(cause.get().getMessage(), Matchers.equalTo("expected"));
                }
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    @Test
    public void testDisposeSavepoint() throws Exception {
        final String savepointPath = "foobar";
        final String exceptionMessage = "Test exception.";
        final FlinkException testException = new FlinkException(exceptionMessage);
        final RestClusterClientTest.TestSavepointDisposalHandlers testSavepointDisposalHandlers = new RestClusterClientTest.TestSavepointDisposalHandlers(savepointPath);
        final RestClusterClientTest.TestSavepointDisposalHandlers.TestSavepointDisposalTriggerHandler testSavepointDisposalTriggerHandler = testSavepointDisposalHandlers.new TestSavepointDisposalTriggerHandler();
        final RestClusterClientTest.TestSavepointDisposalHandlers.TestSavepointDisposalStatusHandler testSavepointDisposalStatusHandler = testSavepointDisposalHandlers.new TestSavepointDisposalStatusHandler(OptionalFailure.of(AsynchronousOperationInfo.complete()), OptionalFailure.of(AsynchronousOperationInfo.completeExceptional(new SerializedThrowable(testException))), OptionalFailure.ofFailure(testException));
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(testSavepointDisposalStatusHandler, testSavepointDisposalTriggerHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                {
                    final CompletableFuture<Acknowledge> disposeSavepointFuture = restClusterClient.disposeSavepoint(savepointPath);
                    Assert.assertThat(disposeSavepointFuture.get(), Matchers.is(Acknowledge.get()));
                }
                {
                    final CompletableFuture<Acknowledge> disposeSavepointFuture = restClusterClient.disposeSavepoint(savepointPath);
                    try {
                        disposeSavepointFuture.get();
                        Assert.fail("Expected an exception");
                    } catch (ExecutionException ee) {
                        Assert.assertThat(ExceptionUtils.findThrowableWithMessage(ee, exceptionMessage).isPresent(), Matchers.is(true));
                    }
                }
                {
                    try {
                        restClusterClient.disposeSavepoint(savepointPath).get();
                        Assert.fail("Expected an exception.");
                    } catch (ExecutionException ee) {
                        Assert.assertThat(ExceptionUtils.findThrowable(ee, RestClientException.class).isPresent(), Matchers.is(true));
                    }
                }
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    private class TestSavepointDisposalHandlers {
        private final TriggerId triggerId = new TriggerId();

        private final String savepointPath;

        private TestSavepointDisposalHandlers(String savepointPath) {
            this.savepointPath = Preconditions.checkNotNull(savepointPath);
        }

        private class TestSavepointDisposalTriggerHandler extends RestClusterClientTest.TestHandler<SavepointDisposalRequest, TriggerResponse, EmptyMessageParameters> {
            private TestSavepointDisposalTriggerHandler() {
                super(SavepointDisposalTriggerHeaders.getInstance());
            }

            @Override
            protected CompletableFuture<TriggerResponse> handleRequest(@Nonnull
            HandlerRequest<SavepointDisposalRequest, EmptyMessageParameters> request, @Nonnull
            DispatcherGateway gateway) {
                Assert.assertThat(request.getRequestBody().getSavepointPath(), Matchers.is(savepointPath));
                return CompletableFuture.completedFuture(new TriggerResponse(triggerId));
            }
        }

        private class TestSavepointDisposalStatusHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, AsynchronousOperationResult<AsynchronousOperationInfo>, SavepointDisposalStatusMessageParameters> {
            private final Queue<OptionalFailure<AsynchronousOperationInfo>> responses;

            private TestSavepointDisposalStatusHandler(OptionalFailure<AsynchronousOperationInfo>... responses) {
                super(SavepointDisposalStatusHeaders.getInstance());
                this.responses = new java.util.ArrayDeque(Arrays.asList(responses));
            }

            @Override
            protected CompletableFuture<AsynchronousOperationResult<AsynchronousOperationInfo>> handleRequest(@Nonnull
            HandlerRequest<EmptyRequestBody, SavepointDisposalStatusMessageParameters> request, @Nonnull
            DispatcherGateway gateway) throws RestHandlerException {
                final TriggerId actualTriggerId = request.getPathParameter(TriggerIdPathParameter.class);
                if (actualTriggerId.equals(triggerId)) {
                    final OptionalFailure<AsynchronousOperationInfo> nextResponse = responses.poll();
                    if (nextResponse != null) {
                        if (nextResponse.isFailure()) {
                            throw new RestHandlerException("Failure", HttpResponseStatus.BAD_REQUEST, nextResponse.getFailureCause());
                        } else {
                            return CompletableFuture.completedFuture(AsynchronousOperationResult.completed(nextResponse.getUnchecked()));
                        }
                    } else {
                        throw new AssertionError();
                    }
                } else {
                    throw new AssertionError();
                }
            }
        }
    }

    @Test
    public void testListJobs() throws Exception {
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(new RestClusterClientTest.TestListJobsHandler())) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                CompletableFuture<Collection<JobStatusMessage>> jobDetailsFuture = restClusterClient.listJobs();
                Collection<JobStatusMessage> jobDetails = jobDetailsFuture.get();
                Iterator<JobStatusMessage> jobDetailsIterator = jobDetails.iterator();
                JobStatusMessage job1 = jobDetailsIterator.next();
                JobStatusMessage job2 = jobDetailsIterator.next();
                Assert.assertNotEquals("The job status should not be equal.", job1.getJobState(), job2.getJobState());
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    @Test
    public void testGetAccumulators() throws Exception {
        RestClusterClientTest.TestAccumulatorHandler accumulatorHandler = new RestClusterClientTest.TestAccumulatorHandler();
        try (TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(accumulatorHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                JobID id = new JobID();
                {
                    Map<String, OptionalFailure<Object>> accumulators = restClusterClient.getAccumulators(id);
                    Assert.assertNotNull(accumulators);
                    Assert.assertEquals(1, accumulators.size());
                    Assert.assertEquals(true, accumulators.containsKey("testKey"));
                    Assert.assertEquals("testValue", accumulators.get("testKey").get().toString());
                }
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    /**
     * Tests that command line options override the configuration settings.
     */
    @Test
    public void testRESTManualConfigurationOverride() throws Exception {
        final String configuredHostname = "localhost";
        final int configuredPort = 1234;
        final Configuration configuration = new Configuration();
        configuration.setString(ADDRESS, configuredHostname);
        configuration.setInteger(JobManagerOptions.PORT, configuredPort);
        configuration.setString(RestOptions.ADDRESS, configuredHostname);
        configuration.setInteger(PORT, configuredPort);
        final DefaultCLI defaultCLI = new DefaultCLI(configuration);
        final String manualHostname = "123.123.123.123";
        final int manualPort = 4321;
        final String[] args = new String[]{ "-m", (manualHostname + ':') + manualPort };
        CommandLine commandLine = defaultCLI.parseCommandLineOptions(args, false);
        final StandaloneClusterDescriptor clusterDescriptor = defaultCLI.createClusterDescriptor(commandLine);
        final RestClusterClient<?> clusterClient = clusterDescriptor.retrieve(defaultCLI.getClusterId(commandLine));
        URL webMonitorBaseUrl = clusterClient.getWebMonitorBaseUrl().get();
        Assert.assertThat(webMonitorBaseUrl.getHost(), Matchers.equalTo(manualHostname));
        Assert.assertThat(webMonitorBaseUrl.getPort(), Matchers.equalTo(manualPort));
    }

    /**
     * Tests that the send operation is being retried.
     */
    @Test
    public void testRetriableSendOperationIfConnectionErrorOrServiceUnavailable() throws Exception {
        final RestClusterClientTest.PingRestHandler pingRestHandler = new RestClusterClientTest.PingRestHandler(FutureUtils.completedExceptionally(new RestHandlerException("test exception", HttpResponseStatus.SERVICE_UNAVAILABLE)), CompletableFuture.completedFuture(EmptyResponseBody.getInstance()));
        try (final TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(pingRestHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                final AtomicBoolean firstPollFailed = new AtomicBoolean();
                failHttpRequest = ( messageHeaders, messageParameters, requestBody) -> (messageHeaders instanceof RestClusterClientTest.PingRestHandlerHeaders) && (!(firstPollFailed.getAndSet(true)));
                restClusterClient.sendRequest(RestClusterClientTest.PingRestHandlerHeaders.INSTANCE).get();
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    @Test
    public void testJobSubmissionFailureThrowsProgramInvocationException() throws Exception {
        try (final TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(new RestClusterClientTest.SubmissionFailingHandler())) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                restClusterClient.submitJob(jobGraph, ClassLoader.getSystemClassLoader());
            } catch (final ProgramInvocationException expected) {
                // expected
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    private final class SubmissionFailingHandler extends RestClusterClientTest.TestHandler<JobSubmitRequestBody, JobSubmitResponseBody, EmptyMessageParameters> {
        private SubmissionFailingHandler() {
            super(JobSubmitHeaders.getInstance());
        }

        @Override
        protected CompletableFuture<JobSubmitResponseBody> handleRequest(@Nonnull
        HandlerRequest<JobSubmitRequestBody, EmptyMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            throw new RestHandlerException("expected", HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tests that the send operation is not being retried when receiving a NOT_FOUND return code.
     */
    @Test
    public void testSendIsNotRetriableIfHttpNotFound() throws Exception {
        final String exceptionMessage = "test exception";
        final RestClusterClientTest.PingRestHandler pingRestHandler = new RestClusterClientTest.PingRestHandler(FutureUtils.completedExceptionally(new RestHandlerException(exceptionMessage, HttpResponseStatus.NOT_FOUND)));
        try (final TestRestServerEndpoint restServerEndpoint = createRestServerEndpoint(pingRestHandler)) {
            RestClusterClient<?> restClusterClient = createRestClusterClient(getServerAddress().getPort());
            try {
                restClusterClient.sendRequest(RestClusterClientTest.PingRestHandlerHeaders.INSTANCE).get();
                Assert.fail("The rest request should have failed.");
            } catch (Exception e) {
                Assert.assertThat(ExceptionUtils.findThrowableWithMessage(e, exceptionMessage).isPresent(), Matchers.is(true));
            } finally {
                restClusterClient.shutdown();
            }
        }
    }

    private class PingRestHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        private final Queue<CompletableFuture<EmptyResponseBody>> responseQueue;

        private PingRestHandler(CompletableFuture<EmptyResponseBody>... responses) {
            super(RestClusterClientTest.PingRestHandlerHeaders.INSTANCE);
            responseQueue = new java.util.ArrayDeque(Arrays.asList(responses));
        }

        @Override
        protected CompletableFuture<EmptyResponseBody> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            final CompletableFuture<EmptyResponseBody> result = responseQueue.poll();
            if (result != null) {
                return result;
            } else {
                return CompletableFuture.completedFuture(EmptyResponseBody.getInstance());
            }
        }
    }

    private static final class PingRestHandlerHeaders implements MessageHeaders<EmptyRequestBody, EmptyResponseBody, EmptyMessageParameters> {
        static final RestClusterClientTest.PingRestHandlerHeaders INSTANCE = new RestClusterClientTest.PingRestHandlerHeaders();

        @Override
        public Class<EmptyResponseBody> getResponseClass() {
            return EmptyResponseBody.class;
        }

        @Override
        public HttpResponseStatus getResponseStatusCode() {
            return HttpResponseStatus.OK;
        }

        @Override
        public String getDescription() {
            return "foobar";
        }

        @Override
        public Class<EmptyRequestBody> getRequestClass() {
            return EmptyRequestBody.class;
        }

        @Override
        public EmptyMessageParameters getUnresolvedMessageParameters() {
            return EmptyMessageParameters.getInstance();
        }

        @Override
        public HttpMethodWrapper getHttpMethod() {
            return HttpMethodWrapper.GET;
        }

        @Override
        public String getTargetRestEndpointURL() {
            return "/foobar";
        }
    }

    private class TestAccumulatorHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, JobAccumulatorsInfo, JobAccumulatorsMessageParameters> {
        public TestAccumulatorHandler() {
            super(JobAccumulatorsHeaders.getInstance());
        }

        @Override
        protected CompletableFuture<JobAccumulatorsInfo> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, JobAccumulatorsMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            JobAccumulatorsInfo accumulatorsInfo;
            List<Boolean> queryParams = request.getQueryParameter(AccumulatorsIncludeSerializedValueQueryParameter.class);
            final boolean includeSerializedValue;
            if (!(queryParams.isEmpty())) {
                includeSerializedValue = queryParams.get(0);
            } else {
                includeSerializedValue = false;
            }
            List<JobAccumulatorsInfo.UserTaskAccumulator> userTaskAccumulators = new ArrayList<>(1);
            userTaskAccumulators.add(new JobAccumulatorsInfo.UserTaskAccumulator("testName", "testType", "testValue"));
            if (includeSerializedValue) {
                Map<String, SerializedValue<OptionalFailure<Object>>> serializedUserTaskAccumulators = new HashMap<>(1);
                try {
                    serializedUserTaskAccumulators.put("testKey", new SerializedValue(OptionalFailure.of("testValue")));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                accumulatorsInfo = new JobAccumulatorsInfo(Collections.emptyList(), userTaskAccumulators, serializedUserTaskAccumulators);
            } else {
                accumulatorsInfo = new JobAccumulatorsInfo(Collections.emptyList(), userTaskAccumulators, Collections.emptyMap());
            }
            return CompletableFuture.completedFuture(accumulatorsInfo);
        }
    }

    private class TestListJobsHandler extends RestClusterClientTest.TestHandler<EmptyRequestBody, MultipleJobsDetails, EmptyMessageParameters> {
        private TestListJobsHandler() {
            super(JobsOverviewHeaders.getInstance());
        }

        @Override
        protected CompletableFuture<MultipleJobsDetails> handleRequest(@Nonnull
        HandlerRequest<EmptyRequestBody, EmptyMessageParameters> request, @Nonnull
        DispatcherGateway gateway) throws RestHandlerException {
            JobDetails running = new JobDetails(new JobID(), "job1", 0, 0, 0, JobStatus.RUNNING, 0, new int[9], 0);
            JobDetails finished = new JobDetails(new JobID(), "job2", 0, 0, 0, JobStatus.FINISHED, 0, new int[9], 0);
            return CompletableFuture.completedFuture(new MultipleJobsDetails(Arrays.asList(running, finished)));
        }
    }

    private abstract class TestHandler<R extends RequestBody, P extends ResponseBody, M extends MessageParameters> extends AbstractRestHandler<DispatcherGateway, R, P, M> {
        private TestHandler(MessageHeaders<R, P, M> headers) {
            super(mockGatewayRetriever, INF_TIMEOUT, Collections.emptyMap(), headers);
        }
    }

    @FunctionalInterface
    private interface FailHttpRequestPredicate {
        boolean test(MessageHeaders<?, ?, ?> messageHeaders, MessageParameters messageParameters, RequestBody requestBody);

        static RestClusterClientTest.FailHttpRequestPredicate never() {
            return ( messageHeaders, messageParameters, requestBody) -> false;
        }
    }
}

