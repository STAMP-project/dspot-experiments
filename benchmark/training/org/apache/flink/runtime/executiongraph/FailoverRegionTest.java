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


import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ExecutionState.DEPLOYING;
import JobStatus.CANCELLING;
import JobStatus.FAILED;
import JobStatus.RUNNING;
import LocationPreferenceConstraint.ALL;
import ResultPartitionType.BLOCKING;
import ResultPartitionType.PIPELINED;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.JobException;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.executiongraph.failover.FailoverStrategy;
import org.apache.flink.runtime.executiongraph.failover.FailoverStrategy.Factory;
import org.apache.flink.runtime.executiongraph.failover.RestartPipelinedRegionStrategy;
import org.apache.flink.runtime.executiongraph.restart.InfiniteDelayRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.executiongraph.restart.RestartStrategy;
import org.apache.flink.runtime.executiongraph.utils.SimpleSlotProvider;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobgraph.tasks.AbstractInvokable;
import org.apache.flink.runtime.jobmaster.slotpool.SlotProvider;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


public class FailoverRegionTest extends TestLogger {
    /**
     * Tests that a job only has one failover region and can recover from task failure successfully
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSingleRegionFailover() throws Exception {
        RestartStrategy restartStrategy = new InfiniteDelayRestartStrategy(10);
        ExecutionGraph eg = FailoverRegionTest.createSingleRegionExecutionGraph(restartStrategy);
        RestartPipelinedRegionStrategy strategy = ((RestartPipelinedRegionStrategy) (eg.getFailoverStrategy()));
        ExecutionVertex ev = eg.getAllExecutionVertices().iterator().next();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev).getState());
        ev.getCurrentExecutionAttempt().fail(new Exception("Test Exception"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev).getState());
        for (ExecutionVertex evs : eg.getAllExecutionVertices()) {
            evs.getCurrentExecutionAttempt().completeCancelling();
        }
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev).getState());
    }

    /**
     * Tests that a job has server failover regions and one region failover does not influence others
     *
     * <pre>
     *     (a1) ---> (b1) -+-> (c1) ---+-> (d1)
     *                     X          /
     *     (a2) ---> (b2) -+-> (c2) -+
     *
     *           ^         ^         ^
     *           |         |         |
     *     (pipelined) (blocking) (pipelined)
     *
     * </pre>
     */
    @Test
    public void testMultiRegionsFailover() throws Exception {
        final JobID jobId = new JobID();
        final String jobName = "Test Job Sample Name";
        final SlotProvider slotProvider = new SimpleSlotProvider(jobId, 20);
        JobVertex v1 = new JobVertex("vertex1");
        JobVertex v2 = new JobVertex("vertex2");
        JobVertex v3 = new JobVertex("vertex3");
        JobVertex v4 = new JobVertex("vertex4");
        v1.setParallelism(2);
        v2.setParallelism(2);
        v3.setParallelism(2);
        v4.setParallelism(1);
        v1.setInvokableClass(AbstractInvokable.class);
        v2.setInvokableClass(AbstractInvokable.class);
        v3.setInvokableClass(AbstractInvokable.class);
        v4.setInvokableClass(AbstractInvokable.class);
        v2.connectNewDataSetAsInput(v1, POINTWISE, PIPELINED);
        v3.connectNewDataSetAsInput(v2, ALL_TO_ALL, BLOCKING);
        v4.connectNewDataSetAsInput(v3, ALL_TO_ALL, PIPELINED);
        List<JobVertex> ordered = Arrays.asList(v1, v2, v3, v4);
        ExecutionGraph eg = new ExecutionGraph(new DummyJobInformation(jobId, jobName), TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), AkkaUtils.getDefaultTimeout(), new InfiniteDelayRestartStrategy(10), new FailoverRegionTest.FailoverPipelinedRegionWithDirectExecutor(), slotProvider);
        eg.attachJobGraph(ordered);
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        RestartPipelinedRegionStrategy strategy = ((RestartPipelinedRegionStrategy) (eg.getFailoverStrategy()));
        // the following two vertices are in the same failover region
        ExecutionVertex ev11 = eg.getJobVertex(v1.getID()).getTaskVertices()[0];
        ExecutionVertex ev21 = eg.getJobVertex(v2.getID()).getTaskVertices()[0];
        // the following two vertices are in the same failover region
        ExecutionVertex ev12 = eg.getJobVertex(v1.getID()).getTaskVertices()[1];
        ExecutionVertex ev22 = eg.getJobVertex(v2.getID()).getTaskVertices()[1];
        // the following vertices are in one failover region
        ExecutionVertex ev31 = eg.getJobVertex(v3.getID()).getTaskVertices()[0];
        ExecutionVertex ev32 = eg.getJobVertex(v3.getID()).getTaskVertices()[1];
        ExecutionVertex ev4 = eg.getJobVertex(v3.getID()).getTaskVertices()[0];
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        ev21.scheduleForExecution(slotProvider, true, ALL, Collections.emptySet());
        ev21.getCurrentExecutionAttempt().fail(new Exception("New fail"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev22).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev31).getState());
        ev11.getCurrentExecutionAttempt().completeCancelling();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev22).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev31).getState());
        ev11.getCurrentExecutionAttempt().markFinished();
        ev21.getCurrentExecutionAttempt().markFinished();
        ev22.scheduleForExecution(slotProvider, true, ALL, Collections.emptySet());
        ev22.getCurrentExecutionAttempt().markFinished();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev22).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev31).getState());
        ExecutionGraphTestUtils.waitUntilExecutionState(ev31.getCurrentExecutionAttempt(), DEPLOYING, 2000);
        ExecutionGraphTestUtils.waitUntilExecutionState(ev32.getCurrentExecutionAttempt(), DEPLOYING, 2000);
        ev31.getCurrentExecutionAttempt().fail(new Exception("New fail"));
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev22).getState());
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev31).getState());
        ev32.getCurrentExecutionAttempt().completeCancelling();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev22).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev31).getState());
    }

    /**
     * Tests that when a task fail, and restart strategy doesn't support restarting, the job will go to failed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testNoManualRestart() throws Exception {
        NoRestartStrategy restartStrategy = new NoRestartStrategy();
        ExecutionGraph eg = FailoverRegionTest.createSingleRegionExecutionGraph(restartStrategy);
        ExecutionVertex ev = eg.getAllExecutionVertices().iterator().next();
        ev.fail(new Exception("Test Exception"));
        for (ExecutionVertex evs : eg.getAllExecutionVertices()) {
            evs.getCurrentExecutionAttempt().completeCancelling();
        }
        Assert.assertEquals(FAILED, eg.getState());
    }

    /**
     * Tests that two failover regions failover at the same time, they will not influence each other
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMultiRegionFailoverAtSameTime() throws Exception {
        final JobID jobId = new JobID();
        final String jobName = "Test Job Sample Name";
        final SimpleSlotProvider slotProvider = new SimpleSlotProvider(jobId, 16);
        JobVertex v1 = new JobVertex("vertex1");
        JobVertex v2 = new JobVertex("vertex2");
        JobVertex v3 = new JobVertex("vertex3");
        JobVertex v4 = new JobVertex("vertex4");
        v1.setParallelism(2);
        v2.setParallelism(2);
        v3.setParallelism(2);
        v4.setParallelism(2);
        v1.setInvokableClass(AbstractInvokable.class);
        v2.setInvokableClass(AbstractInvokable.class);
        v3.setInvokableClass(AbstractInvokable.class);
        v4.setInvokableClass(AbstractInvokable.class);
        v2.connectNewDataSetAsInput(v1, ALL_TO_ALL, PIPELINED);
        v4.connectNewDataSetAsInput(v2, ALL_TO_ALL, BLOCKING);
        v4.connectNewDataSetAsInput(v3, ALL_TO_ALL, PIPELINED);
        List<JobVertex> ordered = Arrays.asList(v1, v2, v3, v4);
        ExecutionGraph eg = new ExecutionGraph(new DummyJobInformation(jobId, jobName), TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), AkkaUtils.getDefaultTimeout(), new InfiniteDelayRestartStrategy(10), new RestartPipelinedRegionStrategy.Factory(), slotProvider);
        try {
            eg.attachJobGraph(ordered);
        } catch (JobException e) {
            e.printStackTrace();
            Assert.fail(("Job failed with exception: " + (e.getMessage())));
        }
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        eg.scheduleForExecution();
        RestartPipelinedRegionStrategy strategy = ((RestartPipelinedRegionStrategy) (eg.getFailoverStrategy()));
        ExecutionVertex ev11 = eg.getJobVertex(v1.getID()).getTaskVertices()[0];
        ExecutionVertex ev12 = eg.getJobVertex(v1.getID()).getTaskVertices()[1];
        ExecutionVertex ev31 = eg.getJobVertex(v3.getID()).getTaskVertices()[0];
        ExecutionVertex ev32 = eg.getJobVertex(v3.getID()).getTaskVertices()[1];
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev31).getState());
        ev11.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        ev31.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev11).getState());
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev31).getState());
        ev32.getCurrentExecutionAttempt().completeCancelling();
        ExecutionGraphTestUtils.waitUntilFailoverRegionState(strategy.getFailoverRegion(ev31), RUNNING, 1000);
        ev12.getCurrentExecutionAttempt().completeCancelling();
        ExecutionGraphTestUtils.waitUntilFailoverRegionState(strategy.getFailoverRegion(ev11), RUNNING, 1000);
    }

    /**
     * Tests that a new failure comes while the failover region is in CANCELLING
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailWhileCancelling() throws Exception {
        RestartStrategy restartStrategy = new InfiniteDelayRestartStrategy();
        ExecutionGraph eg = FailoverRegionTest.createSingleRegionExecutionGraph(restartStrategy);
        RestartPipelinedRegionStrategy strategy = ((RestartPipelinedRegionStrategy) (eg.getFailoverStrategy()));
        Iterator<ExecutionVertex> iter = eg.getAllExecutionVertices().iterator();
        ExecutionVertex ev1 = iter.next();
        ev1.getCurrentExecutionAttempt().switchToRunning();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev1).getState());
        ev1.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev1).getState());
        ExecutionVertex ev2 = iter.next();
        ev2.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        Assert.assertEquals(RUNNING, eg.getState());
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev1).getState());
    }

    /**
     * Tests that a new failure comes while the failover region is restarting
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFailWhileRestarting() throws Exception {
        RestartStrategy restartStrategy = new InfiniteDelayRestartStrategy();
        ExecutionGraph eg = FailoverRegionTest.createSingleRegionExecutionGraph(restartStrategy);
        RestartPipelinedRegionStrategy strategy = ((RestartPipelinedRegionStrategy) (eg.getFailoverStrategy()));
        Iterator<ExecutionVertex> iter = eg.getAllExecutionVertices().iterator();
        ExecutionVertex ev1 = iter.next();
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev1).getState());
        ev1.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev1).getState());
        for (ExecutionVertex evs : eg.getAllExecutionVertices()) {
            evs.getCurrentExecutionAttempt().completeCancelling();
        }
        Assert.assertEquals(RUNNING, strategy.getFailoverRegion(ev1).getState());
        ev1.getCurrentExecutionAttempt().fail(new Exception("new fail"));
        Assert.assertEquals(CANCELLING, strategy.getFailoverRegion(ev1).getState());
    }

    // ------------------------------------------------------------------------
    /**
     * A factory to create a RestartPipelinedRegionStrategy that uses a
     * direct (synchronous) executor for easier testing.
     */
    private static class FailoverPipelinedRegionWithDirectExecutor implements Factory {
        @Override
        public FailoverStrategy create(ExecutionGraph executionGraph) {
            return new RestartPipelinedRegionStrategy(executionGraph);
        }
    }
}

