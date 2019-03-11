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
package org.apache.flink.runtime.executiongraph;


import DistributionPattern.POINTWISE;
import ExecutionState.FINISHED;
import JobStatus.CANCELED;
import JobStatus.CANCELLING;
import JobStatus.CREATED;
import JobStatus.FAILED;
import JobStatus.FAILING;
import JobStatus.RESTARTING;
import JobStatus.RUNNING;
import JobStatus.SUSPENDED;
import ResultPartitionType.PIPELINED_BOUNDED;
import ScheduleMode.EAGER;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.execution.SuppressRestartsException;
import org.apache.flink.runtime.executiongraph.restart.FixedDelayRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.InfiniteDelayRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.runtime.executiongraph.utils.NotCancelAckingTaskGateway;
import org.apache.flink.runtime.executiongraph.utils.SimpleAckingTaskManagerGateway;
import org.apache.flink.runtime.executiongraph.utils.SimpleSlotProvider;
import org.apache.flink.runtime.instance.Instance;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException;
import org.apache.flink.runtime.jobmanager.scheduler.Scheduler;
import org.apache.flink.runtime.jobmanager.scheduler.SlotSharingGroup;
import org.apache.flink.runtime.jobmanager.slots.ActorTaskManagerGateway;
import org.apache.flink.runtime.jobmanager.slots.TaskManagerGateway;
import org.apache.flink.runtime.jobmaster.slotpool.SlotProvider;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;


/**
 * Tests the restart behaviour of the {@link ExecutionGraph}.
 */
public class ExecutionGraphRestartTest extends TestLogger {
    private static final int NUM_TASKS = 31;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);

    private TestingComponentMainThreadExecutorServiceAdapter mainThreadExecutor = TestingComponentMainThreadExecutorServiceAdapter.forMainThread();

    // ------------------------------------------------------------------------
    @Test
    public void testNoManualRestart() throws Exception {
        NoRestartStrategy restartStrategy = new NoRestartStrategy();
        Tuple2<ExecutionGraph, Instance> executionGraphInstanceTuple = createExecutionGraph(restartStrategy);
        ExecutionGraph eg = executionGraphInstanceTuple.f0;
        eg.getAllExecutionVertices().iterator().next().fail(new Exception("Test Exception"));
        completeCanceling(eg);
        Assert.assertEquals(FAILED, eg.getState());
        // This should not restart the graph.
        eg.restart(eg.getGlobalModVersion());
        Assert.assertEquals(FAILED, eg.getState());
    }

    @Test
    public void testRestartAutomatically() throws Exception {
        Tuple2<ExecutionGraph, Instance> executionGraphInstanceTuple = createExecutionGraph(TestRestartStrategy.directExecuting());
        ExecutionGraph eg = executionGraphInstanceTuple.f0;
        restartAfterFailure(eg, new FiniteDuration(2, TimeUnit.MINUTES), true);
    }

    @Test
    public void testCancelWhileRestarting() throws Exception {
        // We want to manually control the restart and delay
        RestartStrategy restartStrategy = new InfiniteDelayRestartStrategy();
        Tuple2<ExecutionGraph, Instance> executionGraphInstanceTuple = createExecutionGraph(restartStrategy);
        ExecutionGraph executionGraph = executionGraphInstanceTuple.f0;
        Instance instance = executionGraphInstanceTuple.f1;
        // Kill the instance and wait for the job to restart
        instance.markDead();
        Assert.assertEquals(RESTARTING, executionGraph.getState());
        Assert.assertEquals(RESTARTING, executionGraph.getState());
        // Canceling needs to abort the restart
        executionGraph.cancel();
        Assert.assertEquals(CANCELED, executionGraph.getState());
        // The restart has been aborted
        executionGraph.restart(executionGraph.getGlobalModVersion());
        Assert.assertEquals(CANCELED, executionGraph.getState());
    }

    @Test
    public void testFailWhileRestarting() throws Exception {
        Scheduler scheduler = new Scheduler(TestingUtils.defaultExecutionContext());
        Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(new ExecutionGraphTestUtils.SimpleActorGateway(TestingUtils.directExecutionContext())), ExecutionGraphRestartTest.NUM_TASKS);
        scheduler.newInstanceAvailable(instance);
        // Blocking program
        ExecutionGraph executionGraph = // We want to manually control the restart and delay
        new ExecutionGraph(TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new JobID(), "TestJob", new Configuration(), new org.apache.flink.util.SerializedValue(new ExecutionConfig()), AkkaUtils.getDefaultTimeout(), new InfiniteDelayRestartStrategy(), scheduler);
        executionGraph.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        JobVertex jobVertex = new JobVertex("NoOpInvokable");
        jobVertex.setInvokableClass(NoOpInvokable.class);
        jobVertex.setParallelism(ExecutionGraphRestartTest.NUM_TASKS);
        JobGraph jobGraph = new JobGraph("TestJob", jobVertex);
        executionGraph.attachJobGraph(jobGraph.getVerticesSortedTopologicallyFromSources());
        Assert.assertEquals(CREATED, executionGraph.getState());
        executionGraph.scheduleForExecution();
        Assert.assertEquals(RUNNING, executionGraph.getState());
        // Kill the instance and wait for the job to restart
        instance.markDead();
        Assert.assertEquals(RESTARTING, executionGraph.getState());
        // If we fail when being in RESTARTING, then we should try to restart again
        final long globalModVersion = executionGraph.getGlobalModVersion();
        final Exception testException = new Exception("Test exception");
        executionGraph.failGlobal(testException);
        Assert.assertNotEquals(globalModVersion, executionGraph.getGlobalModVersion());
        Assert.assertEquals(RESTARTING, executionGraph.getState());
        Assert.assertEquals(testException, executionGraph.getFailureCause());// we should have updated the failure cause

        // but it should fail when sending a SuppressRestartsException
        executionGraph.failGlobal(new SuppressRestartsException(new Exception("Suppress restart exception")));
        Assert.assertEquals(FAILED, executionGraph.getState());
        // The restart has been aborted
        executionGraph.restart(executionGraph.getGlobalModVersion());
        Assert.assertEquals(FAILED, executionGraph.getState());
    }

    @Test
    public void testCancelWhileFailing() throws Exception {
        final RestartStrategy restartStrategy = new InfiniteDelayRestartStrategy();
        final ExecutionGraph graph = createExecutionGraph(restartStrategy).f0;
        Assert.assertEquals(RUNNING, graph.getState());
        // switch all tasks to running
        for (ExecutionVertex vertex : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            vertex.getCurrentExecutionAttempt().switchToRunning();
        }
        graph.failGlobal(new Exception("test"));
        Assert.assertEquals(FAILING, graph.getState());
        graph.cancel();
        Assert.assertEquals(CANCELLING, graph.getState());
        // let all tasks finish cancelling
        completeCanceling(graph);
        Assert.assertEquals(CANCELED, graph.getState());
    }

    @Test
    public void testFailWhileCanceling() throws Exception {
        final RestartStrategy restartStrategy = new NoRestartStrategy();
        final ExecutionGraph graph = createExecutionGraph(restartStrategy).f0;
        Assert.assertEquals(RUNNING, graph.getState());
        switchAllTasksToRunning(graph);
        graph.cancel();
        Assert.assertEquals(CANCELLING, graph.getState());
        graph.failGlobal(new Exception("test"));
        Assert.assertEquals(FAILING, graph.getState());
        // let all tasks finish cancelling
        completeCanceling(graph);
        Assert.assertEquals(FAILED, graph.getState());
    }

    @Test
    public void testNoRestartOnSuppressException() throws Exception {
        final ExecutionGraph eg = createExecutionGraph(new FixedDelayRestartStrategy(Integer.MAX_VALUE, 0)).f0;
        // Fail with unrecoverable Exception
        eg.getAllExecutionVertices().iterator().next().fail(new SuppressRestartsException(new Exception("Test Exception")));
        Assert.assertEquals(FAILING, eg.getState());
        completeCanceling(eg);
        eg.waitUntilTerminal();
        Assert.assertEquals(FAILED, eg.getState());
        RestartStrategy restartStrategy = eg.getRestartStrategy();
        Assert.assertTrue((restartStrategy instanceof FixedDelayRestartStrategy));
        Assert.assertEquals(0, getCurrentRestartAttempt());
    }

    /**
     * Tests that a failing execution does not affect a restarted job. This is important if a
     * callback handler fails an execution after it has already reached a final state and the job
     * has been restarted.
     */
    @Test
    public void testFailingExecutionAfterRestart() throws Exception {
        Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(new ExecutionGraphTestUtils.SimpleActorGateway(TestingUtils.directExecutionContext())), 2);
        Scheduler scheduler = new Scheduler(TestingUtils.defaultExecutionContext());
        scheduler.newInstanceAvailable(instance);
        TestRestartStrategy restartStrategy = TestRestartStrategy.directExecuting();
        JobVertex sender = ExecutionGraphTestUtils.createJobVertex("Task1", 1, NoOpInvokable.class);
        JobVertex receiver = ExecutionGraphTestUtils.createJobVertex("Task2", 1, NoOpInvokable.class);
        JobGraph jobGraph = new JobGraph("Pointwise job", sender, receiver);
        ExecutionGraph eg = ExecutionGraphRestartTest.newExecutionGraph(restartStrategy, scheduler);
        eg.start(mainThreadExecutor);
        eg.attachJobGraph(jobGraph.getVerticesSortedTopologicallyFromSources());
        Assert.assertEquals(CREATED, eg.getState());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        Iterator<ExecutionVertex> executionVertices = eg.getAllExecutionVertices().iterator();
        Execution finishedExecution = executionVertices.next().getCurrentExecutionAttempt();
        Execution failedExecution = executionVertices.next().getCurrentExecutionAttempt();
        finishedExecution.markFinished();
        failedExecution.fail(new Exception("Test Exception"));
        failedExecution.completeCancelling();
        Assert.assertEquals(RUNNING, eg.getState());
        // At this point all resources have been assigned
        for (ExecutionVertex vertex : eg.getAllExecutionVertices()) {
            Assert.assertNotNull("No assigned resource (test instability).", vertex.getCurrentAssignedResource());
            vertex.getCurrentExecutionAttempt().switchToRunning();
        }
        // fail old finished execution, this should not affect the execution
        finishedExecution.fail(new Exception("This should have no effect"));
        for (ExecutionVertex vertex : eg.getAllExecutionVertices()) {
            vertex.getCurrentExecutionAttempt().markFinished();
        }
        // the state of the finished execution should have not changed since it is terminal
        Assert.assertEquals(FINISHED, finishedExecution.getState());
        Assert.assertEquals(JobStatus.FINISHED, eg.getState());
    }

    /**
     * Tests that a graph is not restarted after cancellation via a call to
     * {@link ExecutionGraph#failGlobal(Throwable)}. This can happen when a slot is
     * released concurrently with cancellation.
     */
    @Test
    public void testFailExecutionAfterCancel() throws Exception {
        Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(new ExecutionGraphTestUtils.SimpleActorGateway(TestingUtils.directExecutionContext())), 2);
        Scheduler scheduler = new Scheduler(TestingUtils.defaultExecutionContext());
        scheduler.newInstanceAvailable(instance);
        JobVertex vertex = ExecutionGraphTestUtils.createJobVertex("Test Vertex", 1, NoOpInvokable.class);
        ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JobGraph jobGraph = new JobGraph("Test Job", vertex);
        jobGraph.setExecutionConfig(executionConfig);
        ExecutionGraph eg = ExecutionGraphRestartTest.newExecutionGraph(new InfiniteDelayRestartStrategy(), scheduler);
        eg.attachJobGraph(jobGraph.getVerticesSortedTopologicallyFromSources());
        Assert.assertEquals(CREATED, eg.getState());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        // Fail right after cancel (for example with concurrent slot release)
        eg.cancel();
        for (ExecutionVertex v : eg.getAllExecutionVertices()) {
            v.getCurrentExecutionAttempt().fail(new Exception("Test Exception"));
        }
        Assert.assertEquals(CANCELED, eg.getTerminationFuture().get());
        Execution execution = eg.getAllExecutionVertices().iterator().next().getCurrentExecutionAttempt();
        execution.completeCancelling();
        Assert.assertEquals(CANCELED, eg.getState());
    }

    /**
     * Tests that it is possible to fail a graph via a call to
     * {@link ExecutionGraph#failGlobal(Throwable)} after cancellation.
     */
    @Test
    public void testFailExecutionGraphAfterCancel() throws Exception {
        Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(new ExecutionGraphTestUtils.SimpleActorGateway(TestingUtils.directExecutionContext())), 2);
        Scheduler scheduler = new Scheduler(TestingUtils.defaultExecutionContext());
        scheduler.newInstanceAvailable(instance);
        JobVertex vertex = ExecutionGraphTestUtils.createJobVertex("Test Vertex", 1, NoOpInvokable.class);
        ExecutionConfig executionConfig = new ExecutionConfig();
        executionConfig.setRestartStrategy(RestartStrategies.fixedDelayRestart(Integer.MAX_VALUE, Integer.MAX_VALUE));
        JobGraph jobGraph = new JobGraph("Test Job", vertex);
        jobGraph.setExecutionConfig(executionConfig);
        ExecutionGraph eg = ExecutionGraphRestartTest.newExecutionGraph(new InfiniteDelayRestartStrategy(), scheduler);
        eg.attachJobGraph(jobGraph.getVerticesSortedTopologicallyFromSources());
        Assert.assertEquals(CREATED, eg.getState());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        // Fail right after cancel (for example with concurrent slot release)
        eg.cancel();
        Assert.assertEquals(CANCELLING, eg.getState());
        eg.failGlobal(new Exception("Test Exception"));
        Assert.assertEquals(FAILING, eg.getState());
        Execution execution = eg.getAllExecutionVertices().iterator().next().getCurrentExecutionAttempt();
        execution.completeCancelling();
        Assert.assertEquals(RESTARTING, eg.getState());
    }

    /**
     * Tests that a suspend call while restarting a job, will abort the restarting.
     */
    @Test
    public void testSuspendWhileRestarting() throws Exception {
        Instance instance = ExecutionGraphTestUtils.getInstance(new ActorTaskManagerGateway(new ExecutionGraphTestUtils.SimpleActorGateway(TestingUtils.directExecutionContext())), ExecutionGraphRestartTest.NUM_TASKS);
        Scheduler scheduler = new Scheduler(TestingUtils.defaultExecutionContext());
        scheduler.newInstanceAvailable(instance);
        JobVertex sender = new JobVertex("Task");
        sender.setInvokableClass(NoOpInvokable.class);
        sender.setParallelism(ExecutionGraphRestartTest.NUM_TASKS);
        JobGraph jobGraph = new JobGraph("Pointwise job", sender);
        TestRestartStrategy controllableRestartStrategy = TestRestartStrategy.manuallyTriggered();
        ExecutionGraph eg = new ExecutionGraph(TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new JobID(), "Test job", new Configuration(), new org.apache.flink.util.SerializedValue(new ExecutionConfig()), AkkaUtils.getDefaultTimeout(), controllableRestartStrategy, scheduler);
        eg.start(mainThreadExecutor);
        eg.attachJobGraph(jobGraph.getVerticesSortedTopologicallyFromSources());
        Assert.assertEquals(CREATED, eg.getState());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        instance.markDead();
        Assert.assertEquals(1, controllableRestartStrategy.getNumberOfQueuedActions());
        Assert.assertEquals(RESTARTING, eg.getState());
        eg.suspend(new Exception("Test exception"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        controllableRestartStrategy.triggerAll().join();
        Assert.assertEquals(SUSPENDED, eg.getState());
    }

    @Test
    public void testLocalFailAndRestart() throws Exception {
        final int parallelism = 10;
        SimpleAckingTaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
        final TestRestartStrategy triggeredRestartStrategy = TestRestartStrategy.manuallyTriggered();
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), taskManagerGateway, triggeredRestartStrategy, ExecutionGraphTestUtils.createNoOpVertex(parallelism));
        eg.start(mainThreadExecutor);
        eg.setScheduleMode(EAGER);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchToRunning(eg);
        final ExecutionJobVertex vertex = eg.getVerticesTopologically().iterator().next();
        final Execution first = vertex.getTaskVertices()[0].getCurrentExecutionAttempt();
        final Execution last = vertex.getTaskVertices()[((vertex.getParallelism()) - 1)].getCurrentExecutionAttempt();
        // Have two executions fail
        first.fail(new Exception("intended test failure 1"));
        last.fail(new Exception("intended test failure 2"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        // Now trigger the restart
        Assert.assertEquals(1, triggeredRestartStrategy.getNumberOfQueuedActions());
        triggeredRestartStrategy.triggerAll().join();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphTestUtils.switchToRunning(eg);
        ExecutionGraphTestUtils.finishAllVertices(eg);
        eg.waitUntilTerminal();
        Assert.assertEquals(JobStatus.FINISHED, eg.getState());
    }

    @Test
    public void testGlobalFailAndRestarts() throws Exception {
        final int parallelism = 10;
        final JobID jid = new JobID();
        final JobVertex vertex = ExecutionGraphTestUtils.createNoOpVertex(parallelism);
        final NotCancelAckingTaskGateway taskManagerGateway = new NotCancelAckingTaskGateway();
        final SlotProvider slots = new SimpleSlotProvider(jid, parallelism, taskManagerGateway);
        final TestRestartStrategy restartStrategy = TestRestartStrategy.manuallyTriggered();
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(jid, slots, restartStrategy, vertex);
        eg.start(mainThreadExecutor);
        eg.setScheduleMode(EAGER);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchToRunning(eg);
        // fail into 'RESTARTING'
        eg.failGlobal(new Exception("intended test failure 1"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        Assert.assertEquals(RESTARTING, eg.getState());
        eg.failGlobal(new Exception("intended test failure 2"));
        Assert.assertEquals(RESTARTING, eg.getState());
        restartStrategy.triggerAll().join();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphTestUtils.switchToRunning(eg);
        ExecutionGraphTestUtils.finishAllVertices(eg);
        eg.waitUntilTerminal();
        Assert.assertEquals(JobStatus.FINISHED, eg.getState());
        if ((eg.getNumberOfFullRestarts()) > 2) {
            Assert.fail(("Too many restarts: " + (eg.getNumberOfFullRestarts())));
        }
    }

    @Test
    public void testRestartWithEagerSchedulingAndSlotSharing() throws Exception {
        // this test is inconclusive if not used with a proper multi-threaded executor
        Assert.assertTrue("test assumptions violated", ((((ThreadPoolExecutor) (executor)).getCorePoolSize()) > 1));
        SimpleAckingTaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
        final int parallelism = 20;
        final Scheduler scheduler = createSchedulerWithInstances(parallelism, taskManagerGateway);
        final SlotSharingGroup sharingGroup = new SlotSharingGroup();
        final JobVertex source = new JobVertex("source");
        source.setInvokableClass(NoOpInvokable.class);
        source.setParallelism(parallelism);
        source.setSlotSharingGroup(sharingGroup);
        final JobVertex sink = new JobVertex("sink");
        sink.setInvokableClass(NoOpInvokable.class);
        sink.setParallelism(parallelism);
        sink.setSlotSharingGroup(sharingGroup);
        sink.connectNewDataSetAsInput(source, POINTWISE, PIPELINED_BOUNDED);
        TestRestartStrategy restartStrategy = TestRestartStrategy.directExecuting();
        final ExecutionGraph eg = ExecutionGraphTestUtils.createExecutionGraph(new JobID(), scheduler, restartStrategy, executor, source, sink);
        eg.start(mainThreadExecutor);
        eg.setScheduleMode(EAGER);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchToRunning(eg);
        // fail into 'RESTARTING'
        eg.getAllExecutionVertices().iterator().next().getCurrentExecutionAttempt().fail(new Exception("intended test failure"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        Assert.assertEquals(RUNNING, eg.getState());
        // clean termination
        ExecutionGraphTestUtils.switchToRunning(eg);
        ExecutionGraphTestUtils.finishAllVertices(eg);
        Assert.assertEquals(JobStatus.FINISHED, eg.getState());
    }

    @Test
    public void testRestartWithSlotSharingAndNotEnoughResources() throws Exception {
        // this test is inconclusive if not used with a proper multi-threaded executor
        Assert.assertTrue("test assumptions violated", ((((ThreadPoolExecutor) (executor)).getCorePoolSize()) > 1));
        final int numRestarts = 10;
        final int parallelism = 20;
        TaskManagerGateway taskManagerGateway = new SimpleAckingTaskManagerGateway();
        final Scheduler scheduler = createSchedulerWithInstances((parallelism - 1), taskManagerGateway);
        final SlotSharingGroup sharingGroup = new SlotSharingGroup();
        final JobVertex source = new JobVertex("source");
        source.setInvokableClass(NoOpInvokable.class);
        source.setParallelism(parallelism);
        source.setSlotSharingGroup(sharingGroup);
        final JobVertex sink = new JobVertex("sink");
        sink.setInvokableClass(NoOpInvokable.class);
        sink.setParallelism(parallelism);
        sink.setSlotSharingGroup(sharingGroup);
        sink.connectNewDataSetAsInput(source, POINTWISE, PIPELINED_BOUNDED);
        TestRestartStrategy restartStrategy = new TestRestartStrategy(numRestarts, false);
        final ExecutionGraph eg = ExecutionGraphTestUtils.createExecutionGraph(new JobID(), scheduler, restartStrategy, executor, source, sink);
        eg.start(mainThreadExecutor);
        eg.setScheduleMode(EAGER);
        eg.scheduleForExecution();
        // wait until no more changes happen
        while ((eg.getNumberOfFullRestarts()) < numRestarts) {
            Thread.sleep(1);
        } 
        Assert.assertEquals(FAILED, eg.getState());
        final Throwable t = eg.getFailureCause();
        if (!(t instanceof NoResourceAvailableException)) {
            ExceptionUtils.rethrowException(t, t.getMessage());
        }
    }

    /**
     * Tests that the {@link ExecutionGraph} can handle failures while
     * being in the RESTARTING state.
     */
    @Test
    public void testFailureWhileRestarting() throws Exception {
        final TestRestartStrategy restartStrategy = TestRestartStrategy.manuallyTriggered();
        final ExecutionGraph executionGraph = createSimpleExecutionGraph(restartStrategy, new TestingSlotProvider(( ignored) -> new CompletableFuture<>()));
        executionGraph.start(mainThreadExecutor);
        executionGraph.setQueuedSchedulingAllowed(true);
        executionGraph.scheduleForExecution();
        Assert.assertThat(executionGraph.getState(), Matchers.is(RUNNING));
        executionGraph.failGlobal(new FlinkException("Test exception"));
        restartStrategy.triggerAll().join();
        executionGraph.failGlobal(new FlinkException("Concurrent exception"));
        restartStrategy.triggerAll().join();
        Assert.assertEquals(RUNNING, executionGraph.getState());
    }
}

