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
package org.apache.hadoop.hbase.procedure;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.errorhandling.ForeignExceptionDispatcher;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Test Procedure coordinator operation.
 * <p>
 * This only works correctly when we do <i>class level parallelization</i> of tests. If we do method
 * level serialization this class will likely throw all kinds of errors.
 */
@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureCoordinator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureCoordinator.class);

    // general test constants
    private static final long WAKE_FREQUENCY = 1000;

    private static final long TIMEOUT = 100000;

    private static final long POOL_KEEP_ALIVE = 1;

    private static final String nodeName = "node";

    private static final String procName = "some op";

    private static final byte[] procData = new byte[0];

    private static final List<String> expected = Lists.newArrayList("remote1", "remote2");

    // setup the mocks
    private final ProcedureCoordinatorRpcs controller = Mockito.mock(ProcedureCoordinatorRpcs.class);

    private final Procedure task = Mockito.mock(Procedure.class);

    private final ForeignExceptionDispatcher monitor = Mockito.mock(ForeignExceptionDispatcher.class);

    // handle to the coordinator for each test
    private ProcedureCoordinator coordinator;

    /**
     * Currently we can only handle one procedure at a time.  This makes sure we handle that and
     * reject submitting more.
     */
    @Test
    public void testThreadPoolSize() throws Exception {
        ProcedureCoordinator coordinator = buildNewCoordinator();
        Procedure proc = new Procedure(coordinator, monitor, TestProcedureCoordinator.WAKE_FREQUENCY, TestProcedureCoordinator.TIMEOUT, TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, TestProcedureCoordinator.expected);
        Procedure procSpy = Mockito.spy(proc);
        Procedure proc2 = new Procedure(coordinator, monitor, TestProcedureCoordinator.WAKE_FREQUENCY, TestProcedureCoordinator.TIMEOUT, ((TestProcedureCoordinator.procName) + "2"), TestProcedureCoordinator.procData, TestProcedureCoordinator.expected);
        Procedure procSpy2 = Mockito.spy(proc2);
        Mockito.when(coordinator.createProcedure(ArgumentMatchers.any(), ArgumentMatchers.eq(TestProcedureCoordinator.procName), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class))).thenReturn(procSpy, procSpy2);
        coordinator.startProcedure(procSpy.getErrorMonitor(), TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, TestProcedureCoordinator.expected);
        // null here means second procedure failed to start.
        Assert.assertNull("Coordinator successfully ran two tasks at once with a single thread pool.", coordinator.startProcedure(proc2.getErrorMonitor(), "another op", TestProcedureCoordinator.procData, TestProcedureCoordinator.expected));
    }

    /**
     * Check handling a connection failure correctly if we get it during the acquiring phase
     */
    @Test
    public void testUnreachableControllerDuringPrepare() throws Exception {
        coordinator = buildNewCoordinator();
        // setup the proc
        List<String> expected = Arrays.asList("cohort");
        Procedure proc = new Procedure(coordinator, TestProcedureCoordinator.WAKE_FREQUENCY, TestProcedureCoordinator.TIMEOUT, TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, expected);
        final Procedure procSpy = Mockito.spy(proc);
        Mockito.when(coordinator.createProcedure(ArgumentMatchers.any(), ArgumentMatchers.eq(TestProcedureCoordinator.procName), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class))).thenReturn(procSpy);
        // use the passed controller responses
        IOException cause = new IOException("Failed to reach comms during acquire");
        Mockito.doThrow(cause).when(controller).sendGlobalBarrierAcquire(ArgumentMatchers.eq(procSpy), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class));
        // run the operation
        proc = coordinator.startProcedure(proc.getErrorMonitor(), TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, expected);
        // and wait for it to finish
        while (!(proc.completedLatch.await(TestProcedureCoordinator.WAKE_FREQUENCY, TimeUnit.MILLISECONDS)));
        Mockito.verify(procSpy, Mockito.atLeastOnce()).receive(ArgumentMatchers.any());
        Mockito.verify(coordinator, Mockito.times(1)).rpcConnectionFailure(ArgumentMatchers.anyString(), ArgumentMatchers.eq(cause));
        Mockito.verify(controller, Mockito.times(1)).sendGlobalBarrierAcquire(procSpy, TestProcedureCoordinator.procData, expected);
        Mockito.verify(controller, Mockito.never()).sendGlobalBarrierReached(ArgumentMatchers.any(), ArgumentMatchers.anyListOf(String.class));
    }

    /**
     * Check handling a connection failure correctly if we get it during the barrier phase
     */
    @Test
    public void testUnreachableControllerDuringCommit() throws Exception {
        coordinator = buildNewCoordinator();
        // setup the task and spy on it
        List<String> expected = Arrays.asList("cohort");
        final Procedure spy = Mockito.spy(new Procedure(coordinator, TestProcedureCoordinator.WAKE_FREQUENCY, TestProcedureCoordinator.TIMEOUT, TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, expected));
        Mockito.when(coordinator.createProcedure(ArgumentMatchers.any(), ArgumentMatchers.eq(TestProcedureCoordinator.procName), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class))).thenReturn(spy);
        // use the passed controller responses
        IOException cause = new IOException("Failed to reach controller during prepare");
        Mockito.doAnswer(new TestProcedureCoordinator.AcquireBarrierAnswer(TestProcedureCoordinator.procName, new String[]{ "cohort" })).when(controller).sendGlobalBarrierAcquire(ArgumentMatchers.eq(spy), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class));
        Mockito.doThrow(cause).when(controller).sendGlobalBarrierReached(ArgumentMatchers.eq(spy), ArgumentMatchers.anyListOf(String.class));
        // run the operation
        Procedure task = coordinator.startProcedure(spy.getErrorMonitor(), TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, expected);
        // and wait for it to finish
        while (!(task.completedLatch.await(TestProcedureCoordinator.WAKE_FREQUENCY, TimeUnit.MILLISECONDS)));
        Mockito.verify(spy, Mockito.atLeastOnce()).receive(ArgumentMatchers.any());
        Mockito.verify(coordinator, Mockito.times(1)).rpcConnectionFailure(ArgumentMatchers.anyString(), ArgumentMatchers.eq(cause));
        Mockito.verify(controller, Mockito.times(1)).sendGlobalBarrierAcquire(ArgumentMatchers.eq(spy), ArgumentMatchers.eq(TestProcedureCoordinator.procData), ArgumentMatchers.anyListOf(String.class));
        Mockito.verify(controller, Mockito.times(1)).sendGlobalBarrierReached(ArgumentMatchers.any(), ArgumentMatchers.anyListOf(String.class));
    }

    @Test
    public void testNoCohort() throws Exception {
        runSimpleProcedure();
    }

    @Test
    public void testSingleCohortOrchestration() throws Exception {
        runSimpleProcedure("one");
    }

    @Test
    public void testMultipleCohortOrchestration() throws Exception {
        runSimpleProcedure("one", "two", "three", "four");
    }

    /**
     * Test that if nodes join the barrier early we still correctly handle the progress
     */
    @Test
    public void testEarlyJoiningBarrier() throws Exception {
        final String[] cohort = new String[]{ "one", "two", "three", "four" };
        coordinator = buildNewCoordinator();
        final ProcedureCoordinator ref = coordinator;
        Procedure task = new Procedure(coordinator, monitor, TestProcedureCoordinator.WAKE_FREQUENCY, TestProcedureCoordinator.TIMEOUT, TestProcedureCoordinator.procName, TestProcedureCoordinator.procData, Arrays.asList(cohort));
        final Procedure spy = Mockito.spy(task);
        TestProcedureCoordinator.AcquireBarrierAnswer prepare = new TestProcedureCoordinator.AcquireBarrierAnswer(TestProcedureCoordinator.procName, cohort) {
            @Override
            public void doWork() {
                // then do some fun where we commit before all nodes have prepared
                // "one" commits before anyone else is done
                ref.memberAcquiredBarrier(this.opName, this.cohort[0]);
                ref.memberFinishedBarrier(this.opName, this.cohort[0], new byte[0]);
                // but "two" takes a while
                ref.memberAcquiredBarrier(this.opName, this.cohort[1]);
                // "three"jumps ahead
                ref.memberAcquiredBarrier(this.opName, this.cohort[2]);
                ref.memberFinishedBarrier(this.opName, this.cohort[2], new byte[0]);
                // and "four" takes a while
                ref.memberAcquiredBarrier(this.opName, this.cohort[3]);
            }
        };
        TestProcedureCoordinator.BarrierAnswer commit = new TestProcedureCoordinator.BarrierAnswer(TestProcedureCoordinator.procName, cohort) {
            @Override
            public void doWork() {
                ref.memberFinishedBarrier(opName, this.cohort[1], new byte[0]);
                ref.memberFinishedBarrier(opName, this.cohort[3], new byte[0]);
            }
        };
        runCoordinatedOperation(spy, prepare, commit, cohort);
    }

    private abstract static class OperationAnswer implements Answer<Void> {
        private boolean ran = false;

        public void ensureRan() {
            Assert.assertTrue("Prepare mocking didn't actually run!", ran);
        }

        @Override
        public final Void answer(InvocationOnMock invocation) throws Throwable {
            this.ran = true;
            doWork();
            return null;
        }

        protected abstract void doWork() throws Throwable;
    }

    /**
     * Just tell the current coordinator that each of the nodes has prepared
     */
    private class AcquireBarrierAnswer extends TestProcedureCoordinator.OperationAnswer {
        protected final String[] cohort;

        protected final String opName;

        public AcquireBarrierAnswer(String opName, String... cohort) {
            this.cohort = cohort;
            this.opName = opName;
        }

        @Override
        public void doWork() {
            if ((cohort) == null)
                return;

            for (String member : cohort) {
                TestProcedureCoordinator.this.coordinator.memberAcquiredBarrier(opName, member);
            }
        }
    }

    /**
     * Just tell the current coordinator that each of the nodes has committed
     */
    private class BarrierAnswer extends TestProcedureCoordinator.OperationAnswer {
        protected final String[] cohort;

        protected final String opName;

        public BarrierAnswer(String opName, String... cohort) {
            this.cohort = cohort;
            this.opName = opName;
        }

        @Override
        public void doWork() {
            if ((cohort) == null)
                return;

            for (String member : cohort) {
                TestProcedureCoordinator.this.coordinator.memberFinishedBarrier(opName, member, new byte[0]);
            }
        }
    }
}

