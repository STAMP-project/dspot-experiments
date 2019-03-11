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
package org.apache.flink.runtime.dispatcher;


import DefaultJobManagerRunnerFactory.INSTANCE;
import RunningJobsRegistry.JobSchedulingStatus.RUNNING;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.executiongraph.ErrorInfo;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.RunningJobsRegistry;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobmanager.SubmittedJobGraph;
import org.apache.flink.runtime.jobmaster.JobManagerRunner;
import org.apache.flink.runtime.jobmaster.JobManagerSharedServices;
import org.apache.flink.runtime.jobmaster.JobNotFinishedException;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.jobmaster.factories.JobManagerJobMetricGroupFactory;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.messages.FlinkJobNotFoundException;
import org.apache.flink.runtime.rest.handler.legacy.utils.ArchivedExecutionGraphBuilder;
import org.apache.flink.runtime.rpc.FatalErrorHandler;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.testutils.InMemorySubmittedJobGraphStore;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.apache.flink.util.function.ThrowingRunnable;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;


/**
 * Test for the {@link Dispatcher} component.
 */
public class DispatcherTest extends TestLogger {
    private static RpcService rpcService;

    private static final Time TIMEOUT = Time.seconds(10L);

    private static final JobID TEST_JOB_ID = new JobID();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Rule
    public TestName name = new TestName();

    private JobGraph jobGraph;

    private TestingFatalErrorHandler fatalErrorHandler;

    private FaultySubmittedJobGraphStore submittedJobGraphStore;

    private TestingLeaderElectionService dispatcherLeaderElectionService;

    private TestingLeaderElectionService jobMasterLeaderElectionService;

    private RunningJobsRegistry runningJobsRegistry;

    private CountDownLatch createdJobManagerRunnerLatch;

    private Configuration configuration;

    private BlobServer blobServer;

    /**
     * Instance under test.
     */
    private TestingDispatcher dispatcher;

    private TestingHighAvailabilityServices haServices;

    private HeartbeatServices heartbeatServices;

    /**
     * Tests that we can submit a job to the Dispatcher which then spawns a
     * new JobManagerRunner.
     */
    @Test
    public void testJobSubmission() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        CompletableFuture<UUID> leaderFuture = dispatcherLeaderElectionService.isLeader(UUID.randomUUID());
        // wait for the leader to be elected
        leaderFuture.get();
        DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        CompletableFuture<Acknowledge> acknowledgeFuture = dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT);
        acknowledgeFuture.get();
        Assert.assertTrue("jobManagerRunner was not started", dispatcherLeaderElectionService.getStartFuture().isDone());
    }

    /**
     * Tests that the dispatcher takes part in the leader election.
     */
    @Test
    public void testLeaderElection() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        CompletableFuture<Void> jobIdsFuture = new CompletableFuture<>();
        submittedJobGraphStore.setJobIdsFunction((Collection<JobID> jobIds) -> {
            jobIdsFuture.complete(null);
            return jobIds;
        });
        electDispatcher();
        // wait that we asked the SubmittedJobGraphStore for the stored jobs
        jobIdsFuture.get(DispatcherTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
    }

    /**
     * Test callbacks from
     * {@link org.apache.flink.runtime.jobmanager.SubmittedJobGraphStore.SubmittedJobGraphListener}.
     */
    @Test
    public void testSubmittedJobGraphListener() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT).get();
        jobMasterLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final SubmittedJobGraph submittedJobGraph = submittedJobGraphStore.recoverJobGraph(DispatcherTest.TEST_JOB_ID);
        // pretend that other Dispatcher has removed job from submittedJobGraphStore
        submittedJobGraphStore.removeJobGraph(DispatcherTest.TEST_JOB_ID);
        dispatcher.onRemovedJobGraph(DispatcherTest.TEST_JOB_ID);
        Assert.assertThat(dispatcherGateway.listJobs(DispatcherTest.TIMEOUT).get(), Matchers.empty());
        // pretend that other Dispatcher has added a job to submittedJobGraphStore
        runningJobsRegistry.clearJob(DispatcherTest.TEST_JOB_ID);
        submittedJobGraphStore.putJobGraph(submittedJobGraph);
        dispatcher.onAddedJobGraph(DispatcherTest.TEST_JOB_ID);
        createdJobManagerRunnerLatch.await();
        Assert.assertThat(dispatcherGateway.listJobs(DispatcherTest.TIMEOUT).get(), Matchers.hasSize(1));
    }

    @Test
    public void testOnAddedJobGraphRecoveryFailure() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        final FlinkException expectedFailure = new FlinkException("Expected failure");
        submittedJobGraphStore.setRecoveryFailure(expectedFailure);
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        submittedJobGraphStore.putJobGraph(new SubmittedJobGraph(jobGraph));
        dispatcher.onAddedJobGraph(DispatcherTest.TEST_JOB_ID);
        final CompletableFuture<Throwable> errorFuture = fatalErrorHandler.getErrorFuture();
        final Throwable throwable = errorFuture.get();
        Assert.assertThat(ExceptionUtils.findThrowable(throwable, expectedFailure::equals).isPresent(), Is.is(true));
        fatalErrorHandler.clearError();
    }

    @Test
    public void testOnAddedJobGraphWithFinishedJob() throws Throwable {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        submittedJobGraphStore.putJobGraph(new SubmittedJobGraph(jobGraph));
        runningJobsRegistry.setJobFinished(DispatcherTest.TEST_JOB_ID);
        dispatcher.onAddedJobGraph(DispatcherTest.TEST_JOB_ID);
        // wait until the recovery is over
        dispatcher.getRecoverOperationFuture(DispatcherTest.TIMEOUT).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        // check that we did not start executing the added JobGraph
        Assert.assertThat(dispatcherGateway.listJobs(DispatcherTest.TIMEOUT).get(), Is.is(Matchers.empty()));
    }

    /**
     * Test that {@link JobResult} is cached when the job finishes.
     */
    @Test
    public void testCacheJobExecutionResult() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        final JobID failedJobId = new JobID();
        final JobStatus expectedState = JobStatus.FAILED;
        final ArchivedExecutionGraph failedExecutionGraph = new ArchivedExecutionGraphBuilder().setJobID(failedJobId).setState(expectedState).setFailureCause(new ErrorInfo(new RuntimeException("expected"), 1L)).build();
        dispatcher.completeJobExecution(failedExecutionGraph);
        Assert.assertThat(dispatcherGateway.requestJobStatus(failedJobId, DispatcherTest.TIMEOUT).get(), Matchers.equalTo(expectedState));
        Assert.assertThat(dispatcherGateway.requestJob(failedJobId, DispatcherTest.TIMEOUT).get(), Matchers.equalTo(failedExecutionGraph));
    }

    @Test
    public void testThrowExceptionIfJobExecutionResultNotFound() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        try {
            dispatcherGateway.requestJob(new JobID(), DispatcherTest.TIMEOUT).get();
        } catch (ExecutionException e) {
            final Throwable throwable = ExceptionUtils.stripExecutionException(e);
            Assert.assertThat(throwable, Matchers.instanceOf(FlinkJobNotFoundException.class));
        }
    }

    /**
     * Tests that a reelected Dispatcher can recover jobs.
     */
    @Test
    public void testJobRecovery() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        // elect the initial dispatcher as the leader
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        // submit the job to the current leader
        dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT).get();
        // check that the job has been persisted
        Assert.assertThat(submittedJobGraphStore.getJobIds(), Matchers.contains(jobGraph.getJobID()));
        jobMasterLeaderElectionService.isLeader(UUID.randomUUID()).get();
        Assert.assertThat(runningJobsRegistry.getJobSchedulingStatus(jobGraph.getJobID()), Is.is(RUNNING));
        // revoke the leadership which will stop all currently running jobs
        dispatcherLeaderElectionService.notLeader();
        // re-grant the leadership, this should trigger the job recovery
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        // wait until we have recovered the job
        createdJobManagerRunnerLatch.await();
        // check whether the job has been recovered
        final Collection<JobID> jobIds = dispatcherGateway.listJobs(DispatcherTest.TIMEOUT).get();
        Assert.assertThat(jobIds, Matchers.hasSize(1));
        Assert.assertThat(jobIds, Matchers.contains(jobGraph.getJobID()));
    }

    /**
     * Tests that we can dispose a savepoint.
     */
    @Test
    public void testSavepointDisposal() throws Exception {
        final URI externalPointer = createTestingSavepoint();
        final Path savepointPath = Paths.get(externalPointer);
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        Assert.assertThat(Files.exists(savepointPath), Is.is(true));
        dispatcherGateway.disposeSavepoint(externalPointer.toString(), DispatcherTest.TIMEOUT).get();
        Assert.assertThat(Files.exists(savepointPath), Is.is(false));
    }

    /**
     * Tests that we wait until the JobMaster has gained leader ship before sending requests
     * to it. See FLINK-8887.
     */
    @Test
    public void testWaitingForJobMasterLeadership() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT).get();
        final CompletableFuture<JobStatus> jobStatusFuture = dispatcherGateway.requestJobStatus(jobGraph.getJobID(), DispatcherTest.TIMEOUT);
        Assert.assertThat(jobStatusFuture.isDone(), Is.is(false));
        try {
            jobStatusFuture.get(10, TimeUnit.MILLISECONDS);
            Assert.fail("Should not complete.");
        } catch (TimeoutException ignored) {
            // ignored
        }
        jobMasterLeaderElectionService.isLeader(UUID.randomUUID()).get();
        Assert.assertThat(jobStatusFuture.get(), Matchers.notNullValue());
    }

    /**
     * Tests that the {@link Dispatcher} terminates if it cannot recover jobs ids from
     * the {@link SubmittedJobGraphStore}. See FLINK-8943.
     */
    @Test
    public void testFatalErrorAfterJobIdRecoveryFailure() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        final FlinkException testException = new FlinkException("Test exception");
        submittedJobGraphStore.setJobIdsFunction((Collection<JobID> jobIds) -> {
            throw testException;
        });
        electDispatcher();
        // we expect that a fatal error occurred
        final Throwable error = fatalErrorHandler.getErrorFuture().get(DispatcherTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        Assert.assertThat(ExceptionUtils.findThrowableWithMessage(error, testException.getMessage()).isPresent(), Is.is(true));
        fatalErrorHandler.clearError();
    }

    /**
     * Tests that the {@link Dispatcher} terminates if it cannot recover jobs from
     * the {@link SubmittedJobGraphStore}. See FLINK-8943.
     */
    @Test
    public void testFatalErrorAfterJobRecoveryFailure() throws Exception {
        final FlinkException testException = new FlinkException("Test exception");
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcher.waitUntilStarted();
        final SubmittedJobGraph submittedJobGraph = new SubmittedJobGraph(jobGraph);
        submittedJobGraphStore.putJobGraph(submittedJobGraph);
        submittedJobGraphStore.setRecoverJobGraphFunction((JobID jobId,Map<JobID, SubmittedJobGraph> submittedJobs) -> {
            throw testException;
        });
        electDispatcher();
        // we expect that a fatal error occurred
        final Throwable error = fatalErrorHandler.getErrorFuture().get(DispatcherTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        Assert.assertThat(ExceptionUtils.findThrowableWithMessage(error, testException.getMessage()).isPresent(), Is.is(true));
        fatalErrorHandler.clearError();
    }

    /**
     * Tests that the {@link Dispatcher} fails fatally if the job submission of a recovered job fails.
     * See FLINK-9097.
     */
    @Test
    public void testJobSubmissionErrorAfterJobRecovery() throws Exception {
        final FlinkException testException = new FlinkException("Test exception");
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcher.waitUntilStarted();
        final JobGraph failingJobGraph = createFailingJobGraph(testException);
        final SubmittedJobGraph submittedJobGraph = new SubmittedJobGraph(failingJobGraph);
        submittedJobGraphStore.putJobGraph(submittedJobGraph);
        electDispatcher();
        final Throwable error = fatalErrorHandler.getErrorFuture().get(DispatcherTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
        Assert.assertThat(ExceptionUtils.findThrowableWithMessage(error, testException.getMessage()).isPresent(), Is.is(true));
        fatalErrorHandler.clearError();
    }

    /**
     * Tests that a blocking {@link JobManagerRunner} creation, e.g. due to blocking FileSystem access,
     * does not block the {@link Dispatcher}.
     *
     * <p>See FLINK-10314
     */
    @Test
    public void testBlockingJobManagerRunner() throws Exception {
        final OneShotLatch jobManagerRunnerCreationLatch = new OneShotLatch();
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.BlockingJobManagerRunnerFactory(jobManagerRunnerCreationLatch::await));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        final CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT);
        Assert.assertThat(submissionFuture.isDone(), Is.is(false));
        final CompletableFuture<Collection<String>> metricQueryServicePathsFuture = dispatcherGateway.requestMetricQueryServicePaths(Time.seconds(5L));
        Assert.assertThat(metricQueryServicePathsFuture.get(), Is.is(Matchers.empty()));
        Assert.assertThat(submissionFuture.isDone(), Is.is(false));
        jobManagerRunnerCreationLatch.trigger();
        submissionFuture.get();
    }

    /**
     * Tests that a failing {@link JobManagerRunner} will be properly cleaned up.
     */
    @Test
    public void testFailingJobManagerRunnerCleanup() throws Exception {
        final FlinkException testException = new FlinkException("Test exception.");
        final ArrayBlockingQueue<Optional<Exception>> queue = new ArrayBlockingQueue<>(2);
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.BlockingJobManagerRunnerFactory(() -> {
            final Optional<Exception> take = queue.take();
            final Exception exception = take.orElse(null);
            if (exception != null) {
                throw exception;
            }
        }));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT);
        Assert.assertThat(submissionFuture.isDone(), Is.is(false));
        queue.offer(Optional.of(testException));
        try {
            submissionFuture.get();
            Assert.fail("Should fail because we could not instantiate the JobManagerRunner.");
        } catch (Exception e) {
            Assert.assertThat(ExceptionUtils.findThrowable(e, ( t) -> t.equals(testException)).isPresent(), Is.is(true));
        }
        submissionFuture = dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT);
        queue.offer(Optional.empty());
        submissionFuture.get();
    }

    @Test
    public void testPersistedJobGraphWhenDispatcherIsShutDown() throws Exception {
        final InMemorySubmittedJobGraphStore submittedJobGraphStore = new InMemorySubmittedJobGraphStore();
        haServices.setSubmittedJobGraphStore(submittedJobGraphStore);
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, INSTANCE);
        // grant leadership and submit a single job
        final DispatcherId expectedDispatcherId = DispatcherId.generate();
        dispatcherLeaderElectionService.isLeader(expectedDispatcherId.toUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        final CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT);
        submissionFuture.get();
        Assert.assertThat(dispatcher.getNumberJobs(DispatcherTest.TIMEOUT).get(), Matchers.is(1));
        close();
        Assert.assertThat(submittedJobGraphStore.contains(jobGraph.getJobID()), Matchers.is(true));
    }

    /**
     * Tests that a submitted job is suspended if the Dispatcher loses leadership.
     */
    @Test
    public void testJobSuspensionWhenDispatcherLosesLeadership() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, new DispatcherTest.ExpectedJobIdJobManagerRunnerFactory(DispatcherTest.TEST_JOB_ID, createdJobManagerRunnerLatch));
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        dispatcherGateway.submitJob(jobGraph, DispatcherTest.TIMEOUT).get();
        final CompletableFuture<JobResult> jobResultFuture = dispatcherGateway.requestJobResult(jobGraph.getJobID(), DispatcherTest.TIMEOUT);
        Assert.assertThat(jobResultFuture.isDone(), Is.is(false));
        dispatcherLeaderElectionService.notLeader();
        try {
            jobResultFuture.get();
            Assert.fail("Expected the job result to throw an exception.");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.findThrowable(ee, JobNotFinishedException.class).isPresent(), Is.is(true));
        }
    }

    @Test
    public void testShutDownClusterShouldTerminateDispatcher() throws Exception {
        dispatcher = createAndStartDispatcher(heartbeatServices, haServices, INSTANCE);
        dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
        final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
        dispatcherGateway.shutDownCluster().get();
        getTerminationFuture().get();
    }

    private final class BlockingJobManagerRunnerFactory extends TestingJobManagerRunnerFactory {
        @Nonnull
        private final ThrowingRunnable<Exception> jobManagerRunnerCreationLatch;

        BlockingJobManagerRunnerFactory(@Nonnull
        ThrowingRunnable<Exception> jobManagerRunnerCreationLatch) {
            super(new CompletableFuture<>(), new CompletableFuture<>(), CompletableFuture.completedFuture(null));
            this.jobManagerRunnerCreationLatch = jobManagerRunnerCreationLatch;
        }

        @Override
        public JobManagerRunner createJobManagerRunner(JobGraph jobGraph, Configuration configuration, RpcService rpcService, HighAvailabilityServices highAvailabilityServices, HeartbeatServices heartbeatServices, JobManagerSharedServices jobManagerSharedServices, JobManagerJobMetricGroupFactory jobManagerJobMetricGroupFactory, FatalErrorHandler fatalErrorHandler) throws Exception {
            jobManagerRunnerCreationLatch.run();
            return super.createJobManagerRunner(jobGraph, configuration, rpcService, highAvailabilityServices, heartbeatServices, jobManagerSharedServices, jobManagerJobMetricGroupFactory, fatalErrorHandler);
        }
    }

    private static class FailingJobVertex extends JobVertex {
        private static final long serialVersionUID = 3218428829168840760L;

        private final Exception failure;

        private FailingJobVertex(String name, Exception failure) {
            super(name);
            this.failure = failure;
        }

        @Override
        public void initializeOnMaster(ClassLoader loader) throws Exception {
            throw failure;
        }
    }

    private static final class ExpectedJobIdJobManagerRunnerFactory implements JobManagerRunnerFactory {
        private final JobID expectedJobId;

        private final CountDownLatch createdJobManagerRunnerLatch;

        private ExpectedJobIdJobManagerRunnerFactory(JobID expectedJobId, CountDownLatch createdJobManagerRunnerLatch) {
            this.expectedJobId = expectedJobId;
            this.createdJobManagerRunnerLatch = createdJobManagerRunnerLatch;
        }

        @Override
        public JobManagerRunner createJobManagerRunner(JobGraph jobGraph, Configuration configuration, RpcService rpcService, HighAvailabilityServices highAvailabilityServices, HeartbeatServices heartbeatServices, JobManagerSharedServices jobManagerSharedServices, JobManagerJobMetricGroupFactory jobManagerJobMetricGroupFactory, FatalErrorHandler fatalErrorHandler) throws Exception {
            Assert.assertEquals(expectedJobId, jobGraph.getJobID());
            createdJobManagerRunnerLatch.countDown();
            return INSTANCE.createJobManagerRunner(jobGraph, configuration, rpcService, highAvailabilityServices, heartbeatServices, jobManagerSharedServices, jobManagerJobMetricGroupFactory, fatalErrorHandler);
        }
    }
}

