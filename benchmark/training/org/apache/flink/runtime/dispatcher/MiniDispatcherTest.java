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


import ClusterEntrypoint.ExecutionMode.DETACHED;
import ClusterEntrypoint.ExecutionMode.NORMAL;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.blob.BlobServer;
import org.apache.flink.runtime.executiongraph.ArchivedExecutionGraph;
import org.apache.flink.runtime.heartbeat.HeartbeatServices;
import org.apache.flink.runtime.highavailability.TestingHighAvailabilityServices;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.leaderelection.TestingLeaderElectionService;
import org.apache.flink.runtime.resourcemanager.utils.TestingResourceManagerGateway;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.util.TestingFatalErrorHandler;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link MiniDispatcher}.
 */
public class MiniDispatcherTest extends TestLogger {
    private static final Time timeout = Time.seconds(10L);

    @ClassRule
    public static TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static JobGraph jobGraph;

    private static ArchivedExecutionGraph archivedExecutionGraph;

    private static TestingRpcService rpcService;

    private static Configuration configuration;

    private static BlobServer blobServer;

    private final TestingResourceManagerGateway resourceManagerGateway = new TestingResourceManagerGateway();

    private final HeartbeatServices heartbeatServices = new HeartbeatServices(1000L, 1000L);

    private final ArchivedExecutionGraphStore archivedExecutionGraphStore = new MemoryArchivedExecutionGraphStore();

    private CompletableFuture<JobGraph> jobGraphFuture;

    private CompletableFuture<ArchivedExecutionGraph> resultFuture;

    private TestingLeaderElectionService dispatcherLeaderElectionService;

    private TestingHighAvailabilityServices highAvailabilityServices;

    private TestingFatalErrorHandler testingFatalErrorHandler;

    private TestingJobManagerRunnerFactory testingJobManagerRunnerFactory;

    /**
     * Tests that the {@link MiniDispatcher} recovers the single job with which it
     * was started.
     */
    @Test
    public void testSingleJobRecovery() throws Exception {
        final MiniDispatcher miniDispatcher = createMiniDispatcher(DETACHED);
        miniDispatcher.start();
        try {
            // wait until the Dispatcher is the leader
            dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
            final JobGraph actualJobGraph = jobGraphFuture.get();
            Assert.assertThat(actualJobGraph.getJobID(), Matchers.is(MiniDispatcherTest.jobGraph.getJobID()));
        } finally {
            RpcUtils.terminateRpcEndpoint(miniDispatcher, MiniDispatcherTest.timeout);
        }
    }

    /**
     * Tests that in detached mode, the {@link MiniDispatcher} will complete the future that
     * signals job termination.
     */
    @Test
    public void testTerminationAfterJobCompletion() throws Exception {
        final MiniDispatcher miniDispatcher = createMiniDispatcher(DETACHED);
        miniDispatcher.start();
        try {
            // wait until the Dispatcher is the leader
            dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
            // wait until we have submitted the job
            jobGraphFuture.get();
            resultFuture.complete(MiniDispatcherTest.archivedExecutionGraph);
            // wait until we terminate
            miniDispatcher.getJobTerminationFuture().get();
        } finally {
            RpcUtils.terminateRpcEndpoint(miniDispatcher, MiniDispatcherTest.timeout);
        }
    }

    /**
     * Tests that the {@link MiniDispatcher} only terminates in {@link ClusterEntrypoint.ExecutionMode#NORMAL}
     * after it has served the {@link org.apache.flink.runtime.jobmaster.JobResult} once.
     */
    @Test
    public void testJobResultRetrieval() throws Exception {
        final MiniDispatcher miniDispatcher = createMiniDispatcher(NORMAL);
        miniDispatcher.start();
        try {
            // wait until the Dispatcher is the leader
            dispatcherLeaderElectionService.isLeader(UUID.randomUUID()).get();
            // wait until we have submitted the job
            jobGraphFuture.get();
            resultFuture.complete(MiniDispatcherTest.archivedExecutionGraph);
            Assert.assertFalse(miniDispatcher.getTerminationFuture().isDone());
            final DispatcherGateway dispatcherGateway = miniDispatcher.getSelfGateway(DispatcherGateway.class);
            final CompletableFuture<JobResult> jobResultFuture = dispatcherGateway.requestJobResult(MiniDispatcherTest.jobGraph.getJobID(), MiniDispatcherTest.timeout);
            final JobResult jobResult = jobResultFuture.get();
            Assert.assertThat(jobResult.getJobId(), Matchers.is(MiniDispatcherTest.jobGraph.getJobID()));
        } finally {
            RpcUtils.terminateRpcEndpoint(miniDispatcher, MiniDispatcherTest.timeout);
        }
    }
}

