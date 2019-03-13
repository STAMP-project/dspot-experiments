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
package org.apache.flink.runtime.minicluster;


import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ResultPartitionType.PIPELINED;
import RpcServiceSharing.DEDICATED;
import RpcServiceSharing.SHARED;
import ScheduleMode.EAGER;
import ScheduleMode.LAZY_FROM_SOURCES;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.flink.api.common.JobSubmissionResult;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobmanager.Tasks.AgnosticBinaryReceiver;
import org.apache.flink.runtime.jobmanager.Tasks.AgnosticReceiver;
import org.apache.flink.runtime.jobmanager.Tasks.AgnosticTertiaryReceiver;
import org.apache.flink.runtime.jobmanager.Tasks.ExceptionReceiver;
import org.apache.flink.runtime.jobmanager.Tasks.ExceptionSender;
import org.apache.flink.runtime.jobmanager.Tasks.Forwarder;
import org.apache.flink.runtime.jobmanager.Tasks.InstantiationErrorSender;
import org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException;
import org.apache.flink.runtime.jobmanager.scheduler.SlotSharingGroup;
import org.apache.flink.runtime.jobmaster.JobResult;
import org.apache.flink.runtime.jobmaster.TestingAbstractInvokables;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.runtime.testtasks.WaitingNoOpInvokable;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Integration test cases for the {@link MiniCluster}.
 */
public class MiniClusterITCase extends TestLogger {
    @Test
    public void runJobWithSingleRpcService() throws Exception {
        final int numOfTMs = 3;
        final int slotsPerTM = 7;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(numOfTMs).setNumSlotsPerTaskManager(slotsPerTM).setRpcServiceSharing(SHARED).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            miniCluster.executeJobBlocking(MiniClusterITCase.getSimpleJob((numOfTMs * slotsPerTM)));
        }
    }

    @Test
    public void runJobWithMultipleRpcServices() throws Exception {
        final int numOfTMs = 3;
        final int slotsPerTM = 7;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(numOfTMs).setNumSlotsPerTaskManager(slotsPerTM).setRpcServiceSharing(DEDICATED).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            miniCluster.executeJobBlocking(MiniClusterITCase.getSimpleJob((numOfTMs * slotsPerTM)));
        }
    }

    @Test
    public void testHandleStreamingJobsWhenNotEnoughSlot() throws Exception {
        try {
            setupAndRunHandleJobsWhenNotEnoughSlots(EAGER);
            Assert.fail("Job should fail.");
        } catch (JobExecutionException e) {
            Assert.assertTrue(findThrowableWithMessage(e, "Job execution failed.").isPresent());
            Assert.assertTrue(findThrowable(e, NoResourceAvailableException.class).isPresent());
            Assert.assertTrue(findThrowableWithMessage(e, "Slots required: 2, slots allocated: 1").isPresent());
        }
    }

    @Test
    public void testHandleBatchJobsWhenNotEnoughSlot() throws Exception {
        try {
            setupAndRunHandleJobsWhenNotEnoughSlots(LAZY_FROM_SOURCES);
            Assert.fail("Job should fail.");
        } catch (JobExecutionException e) {
            Assert.assertTrue(findThrowableWithMessage(e, "Job execution failed.").isPresent());
            Assert.assertTrue(findThrowable(e, NoResourceAvailableException.class).isPresent());
            Assert.assertTrue(findThrowableWithMessage(e, "Could not allocate enough slots").isPresent());
        }
    }

    @Test
    public void testForwardJob() throws Exception {
        final int parallelism = 31;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(TestingAbstractInvokables.Receiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            miniCluster.executeJobBlocking(jobGraph);
        }
    }

    @Test
    public void testBipartiteJob() throws Exception {
        final int parallelism = 31;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(AgnosticReceiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Bipartite Job", sender, receiver);
            miniCluster.executeJobBlocking(jobGraph);
        }
    }

    @Test
    public void testTwoInputJobFailingEdgeMismatch() throws Exception {
        final int parallelism = 1;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager((6 * parallelism)).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender1 = new JobVertex("Sender1");
            sender1.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender1.setParallelism(parallelism);
            final JobVertex sender2 = new JobVertex("Sender2");
            sender2.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender2.setParallelism((2 * parallelism));
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(AgnosticTertiaryReceiver.class);
            receiver.setParallelism((3 * parallelism));
            receiver.connectNewDataSetAsInput(sender1, POINTWISE, PIPELINED);
            receiver.connectNewDataSetAsInput(sender2, ALL_TO_ALL, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Bipartite Job", sender1, receiver, sender2);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, ArrayIndexOutOfBoundsException.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "2").isPresent());
            }
        }
    }

    @Test
    public void testTwoInputJob() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager((6 * parallelism)).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender1 = new JobVertex("Sender1");
            sender1.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender1.setParallelism(parallelism);
            final JobVertex sender2 = new JobVertex("Sender2");
            sender2.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender2.setParallelism((2 * parallelism));
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(AgnosticBinaryReceiver.class);
            receiver.setParallelism((3 * parallelism));
            receiver.connectNewDataSetAsInput(sender1, POINTWISE, PIPELINED);
            receiver.connectNewDataSetAsInput(sender2, ALL_TO_ALL, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Bipartite Job", sender1, receiver, sender2);
            miniCluster.executeJobBlocking(jobGraph);
        }
    }

    @Test
    public void testSchedulingAllAtOnce() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender.setParallelism(parallelism);
            final JobVertex forwarder = new JobVertex("Forwarder");
            forwarder.setInvokableClass(Forwarder.class);
            forwarder.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(AgnosticReceiver.class);
            receiver.setParallelism(parallelism);
            final SlotSharingGroup sharingGroup = new SlotSharingGroup(sender.getID(), receiver.getID());
            sender.setSlotSharingGroup(sharingGroup);
            forwarder.setSlotSharingGroup(sharingGroup);
            receiver.setSlotSharingGroup(sharingGroup);
            forwarder.connectNewDataSetAsInput(sender, ALL_TO_ALL, PIPELINED);
            receiver.connectNewDataSetAsInput(forwarder, ALL_TO_ALL, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Forwarding Job", sender, forwarder, receiver);
            jobGraph.setScheduleMode(EAGER);
            miniCluster.executeJobBlocking(jobGraph);
        }
    }

    @Test
    public void testJobWithAFailingSenderVertex() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(ExceptionSender.class);
            sender.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(TestingAbstractInvokables.Receiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, Exception.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "Test exception").isPresent());
            }
        }
    }

    @Test
    public void testJobWithAnOccasionallyFailingSenderVertex() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(SometimesExceptionSender.class);
            sender.setParallelism(parallelism);
            // set failing senders
            SometimesExceptionSender.configFailingSenders(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(TestingAbstractInvokables.Receiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, Exception.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "Test exception").isPresent());
            }
        }
    }

    @Test
    public void testJobWithAFailingReceiverVertex() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(TestingAbstractInvokables.Sender.class);
            sender.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(ExceptionReceiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, Exception.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "Test exception").isPresent());
            }
        }
    }

    @Test
    public void testJobWithAllVerticesFailingDuringInstantiation() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(InstantiationErrorSender.class);
            sender.setParallelism(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(TestingAbstractInvokables.Receiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, Exception.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "Test exception in constructor").isPresent());
            }
        }
    }

    @Test
    public void testJobWithSomeVerticesFailingDuringInstantiation() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex sender = new JobVertex("Sender");
            sender.setInvokableClass(SometimesInstantiationErrorSender.class);
            sender.setParallelism(parallelism);
            // set failing senders
            SometimesInstantiationErrorSender.configFailingSenders(parallelism);
            final JobVertex receiver = new JobVertex("Receiver");
            receiver.setInvokableClass(TestingAbstractInvokables.Receiver.class);
            receiver.setParallelism(parallelism);
            receiver.connectNewDataSetAsInput(sender, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("Pointwise Job", sender, receiver);
            try {
                miniCluster.executeJobBlocking(jobGraph);
                Assert.fail("Job should fail.");
            } catch (JobExecutionException e) {
                Assert.assertTrue(findThrowable(e, Exception.class).isPresent());
                Assert.assertTrue(findThrowableWithMessage(e, "Test exception in constructor").isPresent());
            }
        }
    }

    @Test
    public void testCallFinalizeOnMasterBeforeJobCompletes() throws Exception {
        final int parallelism = 11;
        final MiniClusterConfiguration cfg = new MiniClusterConfiguration.Builder().setNumTaskManagers(1).setNumSlotsPerTaskManager(parallelism).setConfiguration(getDefaultConfiguration()).build();
        try (final MiniCluster miniCluster = new MiniCluster(cfg)) {
            miniCluster.start();
            final JobVertex source = new JobVertex("Source");
            source.setInvokableClass(WaitingNoOpInvokable.class);
            source.setParallelism(parallelism);
            final MiniClusterITCase.WaitOnFinalizeJobVertex sink = new MiniClusterITCase.WaitOnFinalizeJobVertex("Sink", 20L);
            setInvokableClass(NoOpInvokable.class);
            setParallelism(parallelism);
            sink.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
            final JobGraph jobGraph = new JobGraph("SubtaskInFinalStateRaceCondition", source, sink);
            final CompletableFuture<JobSubmissionResult> submissionFuture = miniCluster.submitJob(jobGraph);
            final CompletableFuture<JobResult> jobResultFuture = submissionFuture.thenCompose((JobSubmissionResult ignored) -> miniCluster.requestJobResult(jobGraph.getJobID()));
            jobResultFuture.get().toJobExecutionResult(getClass().getClassLoader());
            Assert.assertTrue(sink.finalizedOnMaster.get());
        }
    }

    private static class WaitOnFinalizeJobVertex extends JobVertex {
        private static final long serialVersionUID = -1179547322468530299L;

        private final AtomicBoolean finalizedOnMaster = new AtomicBoolean(false);

        private final long waitingTime;

        WaitOnFinalizeJobVertex(String name, long waitingTime) {
            super(name);
            this.waitingTime = waitingTime;
        }

        @Override
        public void finalizeOnMaster(ClassLoader loader) throws Exception {
            Thread.sleep(waitingTime);
            finalizedOnMaster.set(true);
        }
    }
}

