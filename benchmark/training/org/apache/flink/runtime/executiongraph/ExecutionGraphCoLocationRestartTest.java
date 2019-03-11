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


import ExecutionState.DEPLOYING;
import JobStatus.CREATED;
import JobStatus.FAILING;
import JobStatus.RUNNING;
import java.util.function.Predicate;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobStatus;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.jobmanager.scheduler.SchedulerTestBase;
import org.apache.flink.runtime.jobmanager.scheduler.SlotSharingGroup;
import org.apache.flink.util.FlinkException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Additional {@link ExecutionGraph} restart tests {@link ExecutionGraphRestartTest} which
 * require the usage of a {@link SlotProvider}.
 */
public class ExecutionGraphCoLocationRestartTest extends SchedulerTestBase {
    private static final int NUM_TASKS = 31;

    @Test
    public void testConstraintsAfterRestart() throws Exception {
        final long timeout = 5000L;
        // setting up
        testingSlotProvider.addTaskManager(ExecutionGraphCoLocationRestartTest.NUM_TASKS);
        JobVertex groupVertex = ExecutionGraphTestUtils.createNoOpVertex(ExecutionGraphCoLocationRestartTest.NUM_TASKS);
        JobVertex groupVertex2 = ExecutionGraphTestUtils.createNoOpVertex(ExecutionGraphCoLocationRestartTest.NUM_TASKS);
        SlotSharingGroup sharingGroup = new SlotSharingGroup();
        groupVertex.setSlotSharingGroup(sharingGroup);
        groupVertex2.setSlotSharingGroup(sharingGroup);
        groupVertex.setStrictlyCoLocatedWith(groupVertex2);
        // initiate and schedule job
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(new JobID(), testingSlotProvider, new TestRestartStrategy(1, false), groupVertex, groupVertex2);
        // enable the queued scheduling for the slot pool
        eg.setQueuedSchedulingAllowed(true);
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        Assert.assertEquals(CREATED, eg.getState());
        eg.scheduleForExecution();
        Predicate<AccessExecution> isDeploying = ExecutionGraphTestUtils.isInExecutionState(DEPLOYING);
        ExecutionGraphTestUtils.waitForAllExecutionsPredicate(eg, isDeploying, timeout);
        Assert.assertEquals(RUNNING, eg.getState());
        // sanity checks
        validateConstraints(eg);
        eg.getAllExecutionVertices().iterator().next().fail(new FlinkException("Test exception"));
        Assert.assertEquals(FAILING, eg.getState());
        for (ExecutionVertex vertex : eg.getAllExecutionVertices()) {
            vertex.getCurrentExecutionAttempt().completeCancelling();
        }
        // wait until we have restarted
        ExecutionGraphTestUtils.waitUntilJobStatus(eg, RUNNING, timeout);
        ExecutionGraphTestUtils.waitForAllExecutionsPredicate(eg, isDeploying, timeout);
        // checking execution vertex properties
        validateConstraints(eg);
        ExecutionGraphTestUtils.finishAllVertices(eg);
        Assert.assertThat(eg.getState(), Matchers.is(JobStatus.FINISHED));
    }
}

