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


import ExecutionState.CANCELED;
import ExecutionState.DEPLOYING;
import JobStatus.CANCELLING;
import JobStatus.CREATED;
import JobStatus.FAILED;
import JobStatus.FAILING;
import JobStatus.RESTARTING;
import JobStatus.RUNNING;
import JobStatus.SUSPENDED;
import org.apache.flink.runtime.executiongraph.restart.InfiniteDelayRestartStrategy;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Validates that suspending out of various states works correctly.
 */
public class ExecutionGraphSuspendTest extends TestLogger {
    /**
     * Going into SUSPENDED out of CREATED should immediately cancel everything and
     * not send out RPC calls.
     */
    @Test
    public void testSuspendedOutOfCreated() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        Assert.assertEquals(CREATED, eg.getState());
        // suspend
        eg.suspend(new Exception("suspend"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        ExecutionGraphSuspendTest.validateAllVerticesInState(eg, CANCELED);
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, 0);
        ExecutionGraphSuspendTest.ensureCannotLeaveSuspendedState(eg, gateway);
    }

    /**
     * Going into SUSPENDED out of DEPLOYING vertices should cancel all vertices once with RPC calls.
     */
    @Test
    public void testSuspendedOutOfDeploying() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphSuspendTest.validateAllVerticesInState(eg, DEPLOYING);
        // suspend
        eg.suspend(new Exception("suspend"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        ExecutionGraphSuspendTest.ensureCannotLeaveSuspendedState(eg, gateway);
    }

    /**
     * Going into SUSPENDED out of RUNNING vertices should cancel all vertices once with RPC calls.
     */
    @Test
    public void testSuspendedOutOfRunning() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphSuspendTest.validateAllVerticesInState(eg, ExecutionState.RUNNING);
        // suspend
        eg.suspend(new Exception("suspend"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        ExecutionGraphSuspendTest.ensureCannotLeaveSuspendedState(eg, gateway);
    }

    /**
     * Suspending from FAILING goes to SUSPENDED and sends no additional RPC calls.
     */
    @Test
    public void testSuspendedOutOfFailing() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        eg.failGlobal(new Exception("fail global"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        // suspend
        eg.suspend(new Exception("suspend"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        ExecutionGraphSuspendTest.ensureCannotLeaveSuspendedState(eg, gateway);
    }

    /**
     * Suspending from FAILED should do nothing.
     */
    @Test
    public void testSuspendedOutOfFailed() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        eg.failGlobal(new Exception("fail global"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        Assert.assertEquals(FAILED, eg.getState());
        // suspend
        eg.suspend(new Exception("suspend"));
        // still in failed state
        Assert.assertEquals(FAILED, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
    }

    /**
     * Suspending from CANCELING goes to SUSPENDED and sends no additional RPC calls.
     */
    @Test
    public void testSuspendedOutOfCanceling() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        eg.cancel();
        Assert.assertEquals(CANCELLING, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        // suspend
        eg.suspend(new Exception("suspend"));
        Assert.assertEquals(SUSPENDED, eg.getState());
        ExecutionGraphSuspendTest.ensureCannotLeaveSuspendedState(eg, gateway);
    }

    /**
     * Suspending from CANCELLED should do nothing.
     */
    @Test
    public void testSuspendedOutOfCanceled() throws Exception {
        final InteractionsCountingTaskManagerGateway gateway = new InteractionsCountingTaskManagerGateway();
        final int parallelism = 10;
        final ExecutionGraph eg = ExecutionGraphSuspendTest.createExecutionGraph(gateway, parallelism);
        eg.scheduleForExecution();
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        eg.cancel();
        Assert.assertEquals(CANCELLING, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        Assert.assertEquals(JobStatus.CANCELED, eg.getTerminationFuture().get());
        // suspend
        eg.suspend(new Exception("suspend"));
        // still in failed state
        Assert.assertEquals(JobStatus.CANCELED, eg.getState());
        ExecutionGraphSuspendTest.validateCancelRpcCalls(gateway, parallelism);
    }

    /**
     * Tests that we can suspend a job when in state RESTARTING.
     */
    @Test
    public void testSuspendWhileRestarting() throws Exception {
        final ExecutionGraph eg = ExecutionGraphTestUtils.createSimpleTestGraph(new InfiniteDelayRestartStrategy(10));
        eg.start(TestingComponentMainThreadExecutorServiceAdapter.forMainThread());
        eg.scheduleForExecution();
        Assert.assertEquals(RUNNING, eg.getState());
        ExecutionGraphTestUtils.switchAllVerticesToRunning(eg);
        eg.failGlobal(new Exception("test"));
        Assert.assertEquals(FAILING, eg.getState());
        ExecutionGraphTestUtils.completeCancellingForAllVertices(eg);
        Assert.assertEquals(RESTARTING, eg.getState());
        final Exception exception = new Exception("Suspended");
        eg.suspend(exception);
        Assert.assertEquals(SUSPENDED, eg.getState());
        Assert.assertEquals(exception, eg.getFailureCause());
    }
}

