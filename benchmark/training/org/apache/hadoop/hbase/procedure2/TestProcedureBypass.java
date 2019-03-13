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
package org.apache.hadoop.hbase.procedure2;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Flow.HAS_MORE_STATE;
import static Flow.NO_MORE_STATE;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureBypass {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureBypass.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureBypass.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static TestProcedureBypass.TestProcEnv procEnv;

    private static ProcedureStore procStore;

    private static ProcedureExecutor<TestProcedureBypass.TestProcEnv> procExecutor;

    private static HBaseCommonTestingUtility htu;

    private static FileSystem fs;

    private static Path testDir;

    private static Path logDir;

    private static class TestProcEnv {}

    @Test
    public void testBypassSuspendProcedure() throws Exception {
        final TestProcedureBypass.SuspendProcedure proc = new TestProcedureBypass.SuspendProcedure();
        long id = TestProcedureBypass.procExecutor.submitProcedure(proc);
        Thread.sleep(500);
        // bypass the procedure
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(id, 30000, false, false));
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    @Test
    public void testStuckProcedure() throws Exception {
        final TestProcedureBypass.StuckProcedure proc = new TestProcedureBypass.StuckProcedure();
        long id = TestProcedureBypass.procExecutor.submitProcedure(proc);
        Thread.sleep(500);
        // bypass the procedure
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(id, 1000, true, false));
        // Since the procedure is stuck there, we need to restart the executor to recovery.
        ProcedureTestingUtility.restart(TestProcedureBypass.procExecutor);
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    @Test
    public void testBypassingProcedureWithParent() throws Exception {
        final TestProcedureBypass.RootProcedure proc = new TestProcedureBypass.RootProcedure();
        long rootId = TestProcedureBypass.procExecutor.submitProcedure(proc);
        TestProcedureBypass.htu.waitFor(5000, () -> (TestProcedureBypass.procExecutor.getProcedures().stream().filter(( p) -> (p.getParentProcId()) == rootId).collect(Collectors.toList()).size()) > 0);
        TestProcedureBypass.SuspendProcedure suspendProcedure = ((TestProcedureBypass.SuspendProcedure) (TestProcedureBypass.procExecutor.getProcedures().stream().filter(( p) -> (p.getParentProcId()) == rootId).collect(Collectors.toList()).get(0)));
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(getProcId(), 1000, false, false));
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    @Test
    public void testBypassingStuckStateMachineProcedure() throws Exception {
        final TestProcedureBypass.StuckStateMachineProcedure proc = new TestProcedureBypass.StuckStateMachineProcedure(TestProcedureBypass.procEnv, TestProcedureBypass.StuckStateMachineState.START);
        long id = TestProcedureBypass.procExecutor.submitProcedure(proc);
        Thread.sleep(500);
        // bypass the procedure
        Assert.assertFalse(TestProcedureBypass.procExecutor.bypassProcedure(id, 1000, false, false));
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(id, 1000, true, false));
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    @Test
    public void testBypassingProcedureWithParentRecursive() throws Exception {
        final TestProcedureBypass.RootProcedure proc = new TestProcedureBypass.RootProcedure();
        long rootId = TestProcedureBypass.procExecutor.submitProcedure(proc);
        TestProcedureBypass.htu.waitFor(5000, () -> (TestProcedureBypass.procExecutor.getProcedures().stream().filter(( p) -> (p.getParentProcId()) == rootId).collect(Collectors.toList()).size()) > 0);
        TestProcedureBypass.SuspendProcedure suspendProcedure = ((TestProcedureBypass.SuspendProcedure) (TestProcedureBypass.procExecutor.getProcedures().stream().filter(( p) -> (p.getParentProcId()) == rootId).collect(Collectors.toList()).get(0)));
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(rootId, 1000, false, true));
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    @Test
    public void testBypassingWaitingTimeoutProcedures() throws Exception {
        final TestProcedureBypass.WaitingTimeoutProcedure proc = new TestProcedureBypass.WaitingTimeoutProcedure();
        long id = TestProcedureBypass.procExecutor.submitProcedure(proc);
        Thread.sleep(500);
        // bypass the procedure
        Assert.assertTrue(TestProcedureBypass.procExecutor.bypassProcedure(id, 1000, true, false));
        TestProcedureBypass.htu.waitFor(5000, () -> (proc.isSuccess()) && (proc.isBypass()));
        TestProcedureBypass.LOG.info("{} finished", proc);
    }

    public static class SuspendProcedure extends ProcedureTestingUtility.NoopProcedure<TestProcedureBypass.TestProcEnv> {
        public SuspendProcedure() {
            super();
        }

        @Override
        protected Procedure[] execute(final TestProcedureBypass.TestProcEnv env) throws ProcedureSuspendedException {
            // Always suspend the procedure
            throw new ProcedureSuspendedException();
        }
    }

    public static class StuckProcedure extends ProcedureTestingUtility.NoopProcedure<TestProcedureBypass.TestProcEnv> {
        public StuckProcedure() {
            super();
        }

        @Override
        protected Procedure[] execute(final TestProcedureBypass.TestProcEnv env) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (Throwable t) {
                TestProcedureBypass.LOG.debug("Sleep is interrupted.", t);
            }
            return null;
        }
    }

    public static class RootProcedure extends ProcedureTestingUtility.NoopProcedure<TestProcedureBypass.TestProcEnv> {
        private boolean childSpwaned = false;

        public RootProcedure() {
            super();
        }

        @Override
        protected Procedure[] execute(final TestProcedureBypass.TestProcEnv env) throws ProcedureSuspendedException {
            if (!(childSpwaned)) {
                childSpwaned = true;
                return new Procedure[]{ new TestProcedureBypass.SuspendProcedure() };
            } else {
                return null;
            }
        }
    }

    public static class WaitingTimeoutProcedure extends ProcedureTestingUtility.NoopProcedure<TestProcedureBypass.TestProcEnv> {
        public WaitingTimeoutProcedure() {
            super();
        }

        @Override
        protected Procedure[] execute(final TestProcedureBypass.TestProcEnv env) throws ProcedureSuspendedException {
            // Always suspend the procedure
            setTimeout(50000);
            setState(ProcedureProtos.ProcedureState.WAITING_TIMEOUT);
            skipPersistence();
            throw new ProcedureSuspendedException();
        }

        @Override
        protected synchronized boolean setTimeoutFailure(TestProcedureBypass.TestProcEnv env) {
            setState(ProcedureProtos.ProcedureState.RUNNABLE);
            TestProcedureBypass.procExecutor.getScheduler().addFront(this);
            return false;// 'false' means that this procedure handled the timeout

        }
    }

    public enum StuckStateMachineState {

        START,
        THEN,
        END;}

    public static class StuckStateMachineProcedure extends ProcedureTestingUtility.NoopStateMachineProcedure<TestProcedureBypass.TestProcEnv, TestProcedureBypass.StuckStateMachineState> {
        private AtomicBoolean stop = new AtomicBoolean(false);

        public StuckStateMachineProcedure() {
            super();
        }

        public StuckStateMachineProcedure(TestProcedureBypass.TestProcEnv env, TestProcedureBypass.StuckStateMachineState initialState) {
            super(env, initialState);
        }

        @Override
        protected Flow executeFromState(TestProcedureBypass.TestProcEnv env, TestProcedureBypass.StuckStateMachineState tState) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            switch (tState) {
                case START :
                    TestProcedureBypass.LOG.info("PHASE 1: START");
                    setNextState(TestProcedureBypass.StuckStateMachineState.THEN);
                    return HAS_MORE_STATE;
                case THEN :
                    if (stop.get()) {
                        setNextState(TestProcedureBypass.StuckStateMachineState.END);
                    }
                    return HAS_MORE_STATE;
                case END :
                    return NO_MORE_STATE;
                default :
                    throw new UnsupportedOperationException(("unhandled state=" + tState));
            }
        }

        @Override
        protected TestProcedureBypass.StuckStateMachineState getState(int stateId) {
            return TestProcedureBypass.StuckStateMachineState.values()[stateId];
        }

        @Override
        protected int getStateId(TestProcedureBypass.StuckStateMachineState tState) {
            return tState.ordinal();
        }
    }
}

