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


import JobStatus.FAILED;
import JobStatus.FINISHED;
import JobStatus.RUNNING;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.testtasks.NoOpInvokable;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests that the {@link JobVertex#finalizeOnMaster(ClassLoader)} is called properly and
 * only when the execution graph reaches the a successful final state.
 */
public class FinalizeOnMasterTest extends TestLogger {
    @Test
    public void testFinalizeIsCalledUponSuccess() throws Exception {
        final JobID jid = new JobID();
        final JobVertex vertex1 = Mockito.spy(new JobVertex("test vertex 1"));
        vertex1.setInvokableClass(NoOpInvokable.class);
        vertex1.setParallelism(3);
        final JobVertex vertex2 = Mockito.spy(new JobVertex("test vertex 2"));
        vertex2.setInvokableClass(NoOpInvokable.class);
        vertex2.setParallelism(2);
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(jid, vertex1, vertex2);
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        // move all vertices to finished state
        ExecutionGraphTestUtils.finishAllVertices(eg);
        Assert.assertEquals(FINISHED, eg.waitUntilTerminal());
        Mockito.verify(vertex1, Mockito.times(1)).finalizeOnMaster(ArgumentMatchers.any(ClassLoader.class));
        Mockito.verify(vertex2, Mockito.times(1)).finalizeOnMaster(ArgumentMatchers.any(ClassLoader.class));
        Assert.assertEquals(0, eg.getRegisteredExecutions().size());
    }

    @Test
    public void testFinalizeIsNotCalledUponFailure() throws Exception {
        final JobID jid = new JobID();
        final JobVertex vertex = Mockito.spy(new JobVertex("test vertex 1"));
        vertex.setInvokableClass(NoOpInvokable.class);
        vertex.setParallelism(1);
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(jid, vertex);
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        // fail the execution
        final Execution exec = eg.getJobVertex(vertex.getID()).getTaskVertices()[0].getCurrentExecutionAttempt();
        exec.fail(new Exception("test"));
        Assert.assertEquals(FAILED, eg.waitUntilTerminal());
        Mockito.verify(vertex, Mockito.times(0)).finalizeOnMaster(ArgumentMatchers.any(ClassLoader.class));
        Assert.assertEquals(0, eg.getRegisteredExecutions().size());
    }
}

