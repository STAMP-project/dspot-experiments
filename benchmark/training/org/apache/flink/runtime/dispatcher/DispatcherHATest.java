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


import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServicesBuilder;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmanager.SubmittedJobGraph;
import org.apache.flink.runtime.jobmanager.SubmittedJobGraphStore;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.metrics.groups.JobManagerMetricGroup;
import org.apache.flink.runtime.resourcemanager.ResourceManagerGateway;
import org.apache.flink.runtime.rpc.FatalErrorHandler;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.testutils.InMemorySubmittedJobGraphStore;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.runtime.webmonitor.retriever.GatewayRetriever;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the HA behaviour of the {@link Dispatcher}.
 */
public class DispatcherHATest extends TestLogger {
    private static final DispatcherId NULL_FENCING_TOKEN = DispatcherId.fromUuid(new UUID(0L, 0L));

    private static final Time timeout = Time.seconds(10L);

    private static TestingRpcService rpcService;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    /**
     * Tests that interleaved granting and revoking of the leadership won't interfere
     * with the job recovery and the resulting internal state of the Dispatcher.
     */
    @Test
    public void testGrantingRevokingLeadership() throws Exception {
        final TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServices();
        final JobGraph nonEmptyJobGraph = DispatcherHATest.createNonEmptyJobGraph();
        final SubmittedJobGraph submittedJobGraph = new SubmittedJobGraph(nonEmptyJobGraph);
        final OneShotLatch enterGetJobIdsLatch = new OneShotLatch();
        final OneShotLatch proceedGetJobIdsLatch = new OneShotLatch();
        highAvailabilityServices.setSubmittedJobGraphStore(new DispatcherHATest.BlockingSubmittedJobGraphStore(submittedJobGraph, enterGetJobIdsLatch, proceedGetJobIdsLatch));
        final TestingLeaderElectionService dispatcherLeaderElectionService = new TestingLeaderElectionService();
        highAvailabilityServices.setDispatcherLeaderElectionService(dispatcherLeaderElectionService);
        final BlockingQueue<DispatcherId> fencingTokens = new ArrayBlockingQueue<>(2);
        final DispatcherHATest.HATestingDispatcher dispatcher = createDispatcherWithObservableFencingTokens(highAvailabilityServices, fencingTokens);
        start();
        try {
            // wait until the election service has been started
            dispatcherLeaderElectionService.getStartFuture().get();
            final UUID leaderId = UUID.randomUUID();
            dispatcherLeaderElectionService.isLeader(leaderId);
            dispatcherLeaderElectionService.notLeader();
            final DispatcherId firstFencingToken = fencingTokens.take();
            Assert.assertThat(firstFencingToken, Matchers.equalTo(DispatcherHATest.NULL_FENCING_TOKEN));
            enterGetJobIdsLatch.await();
            proceedGetJobIdsLatch.trigger();
            Assert.assertThat(dispatcher.getNumberJobs(DispatcherHATest.timeout).get(), Matchers.is(0));
        } finally {
            RpcUtils.terminateRpcEndpoint(dispatcher, DispatcherHATest.timeout);
        }
    }

    /**
     * Tests that all JobManagerRunner are terminated if the leadership of the
     * Dispatcher is revoked.
     */
    @Test
    public void testRevokeLeadershipTerminatesJobManagerRunners() throws Exception {
        final TestingLeaderElectionService leaderElectionService = new TestingLeaderElectionService();
        final TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServicesBuilder().setDispatcherLeaderElectionService(leaderElectionService).build();
        final ArrayBlockingQueue<DispatcherId> fencingTokens = new ArrayBlockingQueue<>(2);
        final DispatcherHATest.HATestingDispatcher dispatcher = createDispatcherWithObservableFencingTokens(highAvailabilityServices, fencingTokens);
        start();
        try {
            // grant leadership and submit a single job
            final DispatcherId expectedDispatcherId = DispatcherId.generate();
            leaderElectionService.isLeader(expectedDispatcherId.toUUID()).get();
            Assert.assertThat(fencingTokens.take(), Matchers.is(Matchers.equalTo(expectedDispatcherId)));
            final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
            final CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(DispatcherHATest.createNonEmptyJobGraph(), DispatcherHATest.timeout);
            submissionFuture.get();
            Assert.assertThat(dispatcher.getNumberJobs(DispatcherHATest.timeout).get(), Matchers.is(1));
            // revoke the leadership --> this should stop all running JobManagerRunners
            leaderElectionService.notLeader();
            Assert.assertThat(fencingTokens.take(), Matchers.is(Matchers.equalTo(DispatcherHATest.NULL_FENCING_TOKEN)));
            Assert.assertThat(dispatcher.getNumberJobs(DispatcherHATest.timeout).get(), Matchers.is(0));
        } finally {
            RpcUtils.terminateRpcEndpoint(dispatcher, DispatcherHATest.timeout);
        }
    }

    /**
     * Tests that a Dispatcher does not remove the JobGraph from the submitted job graph store
     * when losing leadership and recovers it when regaining leadership.
     */
    @Test
    public void testJobRecoveryWhenChangingLeadership() throws Exception {
        final InMemorySubmittedJobGraphStore submittedJobGraphStore = new InMemorySubmittedJobGraphStore();
        final CompletableFuture<JobID> recoveredJobFuture = new CompletableFuture<>();
        submittedJobGraphStore.setRecoverJobGraphFunction(( jobID, jobIDSubmittedJobGraphMap) -> {
            recoveredJobFuture.complete(jobID);
            return jobIDSubmittedJobGraphMap.get(jobID);
        });
        final TestingLeaderElectionService leaderElectionService = new TestingLeaderElectionService();
        final TestingHighAvailabilityServices highAvailabilityServices = new TestingHighAvailabilityServicesBuilder().setSubmittedJobGraphStore(submittedJobGraphStore).setDispatcherLeaderElectionService(leaderElectionService).build();
        final ArrayBlockingQueue<DispatcherId> fencingTokens = new ArrayBlockingQueue<>(2);
        final DispatcherHATest.HATestingDispatcher dispatcher = createDispatcherWithObservableFencingTokens(highAvailabilityServices, fencingTokens);
        start();
        try {
            // grant leadership and submit a single job
            final DispatcherId expectedDispatcherId = DispatcherId.generate();
            leaderElectionService.isLeader(expectedDispatcherId.toUUID()).get();
            Assert.assertThat(fencingTokens.take(), Matchers.is(Matchers.equalTo(expectedDispatcherId)));
            final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
            final JobGraph jobGraph = DispatcherHATest.createNonEmptyJobGraph();
            final CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(jobGraph, DispatcherHATest.timeout);
            submissionFuture.get();
            final JobID jobId = jobGraph.getJobID();
            Assert.assertThat(submittedJobGraphStore.contains(jobId), Matchers.is(true));
            // revoke the leadership --> this should stop all running JobManagerRunners
            leaderElectionService.notLeader();
            Assert.assertThat(fencingTokens.take(), Matchers.is(Matchers.equalTo(DispatcherHATest.NULL_FENCING_TOKEN)));
            Assert.assertThat(submittedJobGraphStore.contains(jobId), Matchers.is(true));
            Assert.assertThat(recoveredJobFuture.isDone(), Matchers.is(false));
            // re-grant leadership
            leaderElectionService.isLeader(DispatcherId.generate().toUUID());
            Assert.assertThat(recoveredJobFuture.get(), Matchers.is(Matchers.equalTo(jobId)));
        } finally {
            RpcUtils.terminateRpcEndpoint(dispatcher, DispatcherHATest.timeout);
        }
    }

    /**
     * Tests that a fatal error is reported if the job recovery fails.
     */
    @Test
    public void testFailingRecoveryIsAFatalError() throws Exception {
        final String exceptionMessage = "Job recovery test failure.";
        final Supplier<Exception> exceptionSupplier = () -> new FlinkException(exceptionMessage);
        final TestingHighAvailabilityServices haServices = new TestingHighAvailabilityServicesBuilder().setSubmittedJobGraphStore(new DispatcherHATest.FailingSubmittedJobGraphStore(exceptionSupplier)).build();
        final DispatcherHATest.HATestingDispatcher dispatcher = createDispatcher(haServices);
        start();
        final Throwable failure = testingFatalErrorHandler.getErrorFuture().get();
        Assert.assertThat(ExceptionUtils.findThrowableWithMessage(failure, exceptionMessage).isPresent(), Matchers.is(true));
        testingFatalErrorHandler.clearError();
    }

    private static class HATestingDispatcher extends TestingDispatcher {
        @Nonnull
        private final Queue<DispatcherId> fencingTokens;

        HATestingDispatcher(RpcService rpcService, String endpointId, Configuration configuration, HighAvailabilityServices highAvailabilityServices, GatewayRetriever<ResourceManagerGateway> resourceManagerGatewayRetriever, BlobServer blobServer, HeartbeatServices heartbeatServices, JobManagerMetricGroup jobManagerMetricGroup, @Nullable
        String metricQueryServicePath, ArchivedExecutionGraphStore archivedExecutionGraphStore, JobManagerRunnerFactory jobManagerRunnerFactory, FatalErrorHandler fatalErrorHandler, @Nonnull
        Queue<DispatcherId> fencingTokens) throws Exception {
            super(rpcService, endpointId, configuration, highAvailabilityServices, resourceManagerGatewayRetriever, blobServer, heartbeatServices, jobManagerMetricGroup, metricQueryServicePath, archivedExecutionGraphStore, jobManagerRunnerFactory, fatalErrorHandler);
            this.fencingTokens = fencingTokens;
        }

        @Override
        protected void setFencingToken(@Nullable
        DispatcherId newFencingToken) {
            super.setFencingToken(newFencingToken);
            final DispatcherId fencingToken;
            if (newFencingToken == null) {
                fencingToken = DispatcherHATest.NULL_FENCING_TOKEN;
            } else {
                fencingToken = newFencingToken;
            }
            fencingTokens.offer(fencingToken);
        }
    }

    private static class BlockingSubmittedJobGraphStore implements SubmittedJobGraphStore {
        @Nonnull
        private final SubmittedJobGraph submittedJobGraph;

        @Nonnull
        private final OneShotLatch enterGetJobIdsLatch;

        @Nonnull
        private final OneShotLatch proceedGetJobIdsLatch;

        private BlockingSubmittedJobGraphStore(@Nonnull
        SubmittedJobGraph submittedJobGraph, @Nonnull
        OneShotLatch enterGetJobIdsLatch, @Nonnull
        OneShotLatch proceedGetJobIdsLatch) {
            this.submittedJobGraph = submittedJobGraph;
            this.enterGetJobIdsLatch = enterGetJobIdsLatch;
            this.proceedGetJobIdsLatch = proceedGetJobIdsLatch;
        }

        @Override
        public void start(SubmittedJobGraphListener jobGraphListener) {
        }

        @Override
        public void stop() {
        }

        @Nullable
        @Override
        public SubmittedJobGraph recoverJobGraph(JobID jobId) {
            Preconditions.checkArgument(jobId.equals(submittedJobGraph.getJobId()));
            return submittedJobGraph;
        }

        @Override
        public void putJobGraph(SubmittedJobGraph jobGraph) {
            throw new UnsupportedOperationException("Should not be called.");
        }

        @Override
        public void removeJobGraph(JobID jobId) {
            throw new UnsupportedOperationException("Should not be called.");
        }

        @Override
        public void releaseJobGraph(JobID jobId) {
        }

        @Override
        public Collection<JobID> getJobIds() throws Exception {
            enterGetJobIdsLatch.trigger();
            proceedGetJobIdsLatch.await();
            return Collections.singleton(submittedJobGraph.getJobId());
        }
    }

    private static class FailingSubmittedJobGraphStore implements SubmittedJobGraphStore {
        private final JobID jobId = new JobID();

        private final Supplier<Exception> exceptionSupplier;

        private FailingSubmittedJobGraphStore(Supplier<Exception> exceptionSupplier) {
            this.exceptionSupplier = exceptionSupplier;
        }

        @Override
        public void start(SubmittedJobGraphListener jobGraphListener) {
        }

        @Override
        public void stop() {
        }

        @Nullable
        @Override
        public SubmittedJobGraph recoverJobGraph(JobID jobId) throws Exception {
            throw exceptionSupplier.get();
        }

        @Override
        public void putJobGraph(SubmittedJobGraph jobGraph) {
        }

        @Override
        public void removeJobGraph(JobID jobId) {
        }

        @Override
        public void releaseJobGraph(JobID jobId) {
        }

        @Override
        public Collection<JobID> getJobIds() {
            return Collections.singleton(jobId);
        }
    }
}

