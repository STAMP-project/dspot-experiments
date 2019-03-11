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


import JobStatus.FINISHED;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.curator.framework.CuratorFramework;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.blob.VoidBlobStore;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmanager.SubmittedJobGraph;
import org.apache.flink.runtime.jobmanager.ZooKeeperSubmittedJobGraphStore;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.runtime.rest.handler.legacy.utils.ArchivedExecutionGraphBuilder;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.util.LeaderConnectionInfo;
import org.apache.flink.runtime.util.LeaderRetrievalUtils;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.runtime.util.ZooKeeperUtils;
import org.apache.flink.runtime.zookeeper.ZooKeeperResource;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestName;


/**
 * Test cases for the interaction between ZooKeeper HA and the {@link Dispatcher}.
 */
public class ZooKeeperHADispatcherTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    @Rule
    public final ZooKeeperResource zooKeeperResource = new ZooKeeperResource();

    @ClassRule
    public static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

    private static Configuration configuration;

    private static TestingRpcService rpcService;

    private static BlobServer blobServer;

    @Rule
    public TestName name = new TestName();

    private TestingFatalErrorHandler testingFatalErrorHandler;

    /**
     * Tests that the {@link Dispatcher} releases a locked {@link SubmittedJobGraph} if it
     * lost the leadership.
     */
    @Test
    public void testSubmittedJobGraphRelease() throws Exception {
        final CuratorFramework client = ZooKeeperUtils.startCuratorFramework(ZooKeeperHADispatcherTest.configuration);
        final CuratorFramework otherClient = ZooKeeperUtils.startCuratorFramework(ZooKeeperHADispatcherTest.configuration);
        try (final TestingHighAvailabilityServices testingHighAvailabilityServices = new TestingHighAvailabilityServices()) {
            testingHighAvailabilityServices.setSubmittedJobGraphStore(ZooKeeperUtils.createSubmittedJobGraphs(client, ZooKeeperHADispatcherTest.configuration));
            final ZooKeeperSubmittedJobGraphStore otherSubmittedJobGraphStore = ZooKeeperUtils.createSubmittedJobGraphs(otherClient, ZooKeeperHADispatcherTest.configuration);
            otherSubmittedJobGraphStore.start(NoOpSubmittedJobGraphListener.INSTANCE);
            final TestingLeaderElectionService leaderElectionService = new TestingLeaderElectionService();
            setDispatcherLeaderElectionService(leaderElectionService);
            final TestingDispatcher dispatcher = createDispatcher(testingHighAvailabilityServices, new TestingJobManagerRunnerFactory(new CompletableFuture<>(), new CompletableFuture<>(), CompletableFuture.completedFuture(null)));
            start();
            try {
                final DispatcherId expectedLeaderId = DispatcherId.generate();
                leaderElectionService.isLeader(expectedLeaderId.toUUID()).get();
                final DispatcherGateway dispatcherGateway = getSelfGateway(DispatcherGateway.class);
                final JobGraph nonEmptyJobGraph = DispatcherHATest.createNonEmptyJobGraph();
                final CompletableFuture<Acknowledge> submissionFuture = dispatcherGateway.submitJob(nonEmptyJobGraph, ZooKeeperHADispatcherTest.TIMEOUT);
                submissionFuture.get();
                Collection<JobID> jobIds = otherSubmittedJobGraphStore.getJobIds();
                final JobID jobId = nonEmptyJobGraph.getJobID();
                Assert.assertThat(jobIds, Matchers.contains(jobId));
                leaderElectionService.notLeader();
                // wait for the job to properly terminate
                final CompletableFuture<Void> jobTerminationFuture = dispatcher.getJobTerminationFuture(jobId, ZooKeeperHADispatcherTest.TIMEOUT);
                jobTerminationFuture.get();
                // recover the job
                final SubmittedJobGraph submittedJobGraph = otherSubmittedJobGraphStore.recoverJobGraph(jobId);
                Assert.assertThat(submittedJobGraph, Matchers.is(Matchers.notNullValue()));
                // check that the other submitted job graph store can remove the job graph after the original leader
                // has lost its leadership
                otherSubmittedJobGraphStore.removeJobGraph(jobId);
                jobIds = otherSubmittedJobGraphStore.getJobIds();
                Assert.assertThat(jobIds, Matchers.not(Matchers.contains(jobId)));
            } finally {
                RpcUtils.terminateRpcEndpoint(dispatcher, ZooKeeperHADispatcherTest.TIMEOUT);
                client.close();
                otherClient.close();
            }
        }
    }

    /**
     * Tests that a standby Dispatcher does not interfere with the clean up of a completed
     * job.
     */
    @Test
    public void testStandbyDispatcherJobExecution() throws Exception {
        try (final TestingHighAvailabilityServices haServices1 = new TestingHighAvailabilityServices();final TestingHighAvailabilityServices haServices2 = new TestingHighAvailabilityServices();final CuratorFramework curatorFramework = ZooKeeperUtils.startCuratorFramework(ZooKeeperHADispatcherTest.configuration)) {
            final ZooKeeperSubmittedJobGraphStore submittedJobGraphStore1 = ZooKeeperUtils.createSubmittedJobGraphs(curatorFramework, ZooKeeperHADispatcherTest.configuration);
            haServices1.setSubmittedJobGraphStore(submittedJobGraphStore1);
            final TestingLeaderElectionService leaderElectionService1 = new TestingLeaderElectionService();
            setDispatcherLeaderElectionService(leaderElectionService1);
            final ZooKeeperSubmittedJobGraphStore submittedJobGraphStore2 = ZooKeeperUtils.createSubmittedJobGraphs(curatorFramework, ZooKeeperHADispatcherTest.configuration);
            haServices2.setSubmittedJobGraphStore(submittedJobGraphStore2);
            final TestingLeaderElectionService leaderElectionService2 = new TestingLeaderElectionService();
            setDispatcherLeaderElectionService(leaderElectionService2);
            final CompletableFuture<JobGraph> jobGraphFuture = new CompletableFuture<>();
            final CompletableFuture<ArchivedExecutionGraph> resultFuture = new CompletableFuture<>();
            final TestingDispatcher dispatcher1 = createDispatcher(haServices1, new TestingJobManagerRunnerFactory(jobGraphFuture, resultFuture, CompletableFuture.completedFuture(null)));
            final TestingDispatcher dispatcher2 = createDispatcher(haServices2, new TestingJobManagerRunnerFactory(new CompletableFuture<>(), new CompletableFuture<>(), CompletableFuture.completedFuture(null)));
            try {
                start();
                start();
                leaderElectionService1.isLeader(UUID.randomUUID()).get();
                final DispatcherGateway dispatcherGateway1 = getSelfGateway(DispatcherGateway.class);
                final JobGraph jobGraph = DispatcherHATest.createNonEmptyJobGraph();
                dispatcherGateway1.submitJob(jobGraph, ZooKeeperHADispatcherTest.TIMEOUT).get();
                final CompletableFuture<JobResult> jobResultFuture = dispatcherGateway1.requestJobResult(jobGraph.getJobID(), ZooKeeperHADispatcherTest.TIMEOUT);
                jobGraphFuture.get();
                // complete the job
                resultFuture.complete(new ArchivedExecutionGraphBuilder().setJobID(jobGraph.getJobID()).setState(FINISHED).build());
                final JobResult jobResult = jobResultFuture.get();
                Assert.assertThat(jobResult.isSuccess(), Matchers.is(true));
                // wait for the completion of the job
                dispatcher1.getJobTerminationFuture(jobGraph.getJobID(), ZooKeeperHADispatcherTest.TIMEOUT).get();
                // change leadership
                leaderElectionService1.notLeader();
                leaderElectionService2.isLeader(UUID.randomUUID()).get();
                // Dispatcher 2 should not recover any jobs
                final DispatcherGateway dispatcherGateway2 = getSelfGateway(DispatcherGateway.class);
                Assert.assertThat(dispatcherGateway2.listJobs(ZooKeeperHADispatcherTest.TIMEOUT).get(), Matchers.is(Matchers.empty()));
            } finally {
                RpcUtils.terminateRpcEndpoint(dispatcher1, ZooKeeperHADispatcherTest.TIMEOUT);
                RpcUtils.terminateRpcEndpoint(dispatcher2, ZooKeeperHADispatcherTest.TIMEOUT);
            }
        }
    }

    /**
     * Tests that a standby {@link Dispatcher} can recover all submitted jobs.
     */
    @Test
    public void testStandbyDispatcherJobRecovery() throws Exception {
        try (CuratorFramework curatorFramework = ZooKeeperUtils.startCuratorFramework(ZooKeeperHADispatcherTest.configuration)) {
            HighAvailabilityServices haServices = null;
            Dispatcher dispatcher1 = null;
            Dispatcher dispatcher2 = null;
            try {
                haServices = new org.apache.flink.runtime.highavailability.zookeeper.ZooKeeperHaServices(curatorFramework, getExecutor(), ZooKeeperHADispatcherTest.configuration, new VoidBlobStore());
                final CompletableFuture<JobGraph> jobGraphFuture1 = new CompletableFuture<>();
                dispatcher1 = createDispatcher(haServices, new TestingJobManagerRunnerFactory(jobGraphFuture1, new CompletableFuture(), CompletableFuture.completedFuture(null)));
                final CompletableFuture<JobGraph> jobGraphFuture2 = new CompletableFuture<>();
                dispatcher2 = createDispatcher(haServices, new TestingJobManagerRunnerFactory(jobGraphFuture2, new CompletableFuture(), CompletableFuture.completedFuture(null)));
                dispatcher1.start();
                dispatcher2.start();
                final LeaderConnectionInfo leaderConnectionInfo = LeaderRetrievalUtils.retrieveLeaderConnectionInfo(haServices.getDispatcherLeaderRetriever(), ZooKeeperHADispatcherTest.TIMEOUT);
                final DispatcherGateway dispatcherGateway = ZooKeeperHADispatcherTest.rpcService.connect(leaderConnectionInfo.getAddress(), DispatcherId.fromUuid(leaderConnectionInfo.getLeaderSessionID()), DispatcherGateway.class).get();
                final JobGraph nonEmptyJobGraph = DispatcherHATest.createNonEmptyJobGraph();
                dispatcherGateway.submitJob(nonEmptyJobGraph, ZooKeeperHADispatcherTest.TIMEOUT).get();
                if (dispatcher1.getAddress().equals(leaderConnectionInfo.getAddress())) {
                    dispatcher1.closeAsync();
                    Assert.assertThat(jobGraphFuture2.get().getJobID(), Matchers.is(Matchers.equalTo(nonEmptyJobGraph.getJobID())));
                } else {
                    dispatcher2.closeAsync();
                    Assert.assertThat(jobGraphFuture1.get().getJobID(), Matchers.is(Matchers.equalTo(nonEmptyJobGraph.getJobID())));
                }
            } finally {
                if (dispatcher1 != null) {
                    RpcUtils.terminateRpcEndpoint(dispatcher1, ZooKeeperHADispatcherTest.TIMEOUT);
                }
                if (dispatcher2 != null) {
                    RpcUtils.terminateRpcEndpoint(dispatcher2, ZooKeeperHADispatcherTest.TIMEOUT);
                }
                if (haServices != null) {
                    haServices.close();
                }
            }
        }
    }
}

