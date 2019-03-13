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


import ExecutionState.CANCELING;
import ExecutionState.DEPLOYING;
import JobStatus.CANCELED;
import JobStatus.FAILING;
import JobStatus.RESTARTING;
import JobStatus.RUNNING;
import org.apache.flink.runtime.executiongraph.failover.FailoverStrategy;
import org.apache.flink.runtime.executiongraph.failover.FailoverStrategy.Factory;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class GlobalModVersionTest extends TestLogger {
    /**
     * Tests that failures during a global cancellation are not handed to the local
     * failover strategy.
     */
    @Test
    public void testNoLocalFailoverWhileCancelling() throws Exception {
        final FailoverStrategy mockStrategy = Mockito.mock(FailoverStrategy.class);
        final ExecutionGraph graph = createSampleGraph(mockStrategy);
        final ExecutionVertex testVertex = GlobalModVersionTest.getRandomVertex(graph);
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        Assert.assertEquals(1L, graph.getGlobalModVersion());
        // wait until everything is running
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            ExecutionGraphTestUtils.waitUntilExecutionState(exec, DEPLOYING, 1000);
            exec.switchToRunning();
            Assert.assertEquals(ExecutionState.RUNNING, exec.getState());
        }
        // now cancel the job
        graph.cancel();
        Assert.assertEquals(2L, graph.getGlobalModVersion());
        // everything should be cancelling
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            Assert.assertEquals(CANCELING, exec.getState());
        }
        // let a vertex fail
        testVertex.getCurrentExecutionAttempt().fail(new Exception("test exception"));
        // all cancellations are done now
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            exec.completeCancelling();
        }
        Assert.assertEquals(CANCELED, graph.getTerminationFuture().get());
        // no failure notification at all
        Mockito.verify(mockStrategy, Mockito.times(0)).onTaskFailure(ArgumentMatchers.any(Execution.class), ArgumentMatchers.any(Throwable.class));
    }

    /**
     * Tests that failures during a global failover are not handed to the local
     * failover strategy.
     */
    @Test
    public void testNoLocalFailoverWhileFailing() throws Exception {
        final FailoverStrategy mockStrategy = Mockito.mock(FailoverStrategy.class);
        final ExecutionGraph graph = createSampleGraph(mockStrategy);
        final ExecutionVertex testVertex = GlobalModVersionTest.getRandomVertex(graph);
        graph.scheduleForExecution();
        Assert.assertEquals(RUNNING, graph.getState());
        // wait until everything is running
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            ExecutionGraphTestUtils.waitUntilExecutionState(exec, DEPLOYING, 1000);
            exec.switchToRunning();
            Assert.assertEquals(ExecutionState.RUNNING, exec.getState());
        }
        // now send the job into global failover
        graph.failGlobal(new Exception("global failover"));
        Assert.assertEquals(FAILING, graph.getState());
        Assert.assertEquals(2L, graph.getGlobalModVersion());
        // another attempt to fail global should not do anything
        graph.failGlobal(new Exception("should be ignored"));
        Assert.assertEquals(FAILING, graph.getState());
        Assert.assertEquals(2L, graph.getGlobalModVersion());
        // everything should be cancelling
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            Assert.assertEquals(CANCELING, exec.getState());
        }
        // let a vertex fail
        testVertex.getCurrentExecutionAttempt().fail(new Exception("test exception"));
        // all cancellations are done now
        for (ExecutionVertex v : graph.getVerticesTopologically().iterator().next().getTaskVertices()) {
            final Execution exec = v.getCurrentExecutionAttempt();
            exec.completeCancelling();
        }
        Assert.assertEquals(RESTARTING, graph.getState());
        // no failure notification at all
        Mockito.verify(mockStrategy, Mockito.times(0)).onTaskFailure(ArgumentMatchers.any(Execution.class), ArgumentMatchers.any(Throwable.class));
    }

    // ------------------------------------------------------------------------
    private static class CustomStrategy implements Factory {
        private final FailoverStrategy failoverStrategy;

        CustomStrategy(FailoverStrategy failoverStrategy) {
            this.failoverStrategy = failoverStrategy;
        }

        @Override
        public FailoverStrategy create(ExecutionGraph executionGraph) {
            return failoverStrategy;
        }
    }
}

