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


import StateMachineProcedure.Flow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
public class TestYieldProcedures {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestYieldProcedures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestYieldProcedures.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static final Procedure NULL_PROC = null;

    private ProcedureExecutor<TestYieldProcedures.TestProcEnv> procExecutor;

    private TestYieldProcedures.TestScheduler procRunnables;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    @Test
    public void testYieldEachExecutionStep() throws Exception {
        final int NUM_STATES = 3;
        TestYieldProcedures.TestStateMachineProcedure[] procs = new TestYieldProcedures.TestStateMachineProcedure[3];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new TestYieldProcedures.TestStateMachineProcedure(true, false);
            procExecutor.submitProcedure(procs[i]);
        }
        ProcedureTestingUtility.waitNoProcedureRunning(procExecutor);
        for (int i = 0; i < (procs.length); ++i) {
            Assert.assertEquals((NUM_STATES * 2), procs[i].getExecutionInfo().size());
            // verify execution
            int index = 0;
            for (int execStep = 0; execStep < NUM_STATES; ++execStep) {
                TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo info = procs[i].getExecutionInfo().get((index++));
                Assert.assertEquals(false, info.isRollback());
                Assert.assertEquals(execStep, info.getStep().ordinal());
            }
            // verify rollback
            for (int execStep = NUM_STATES - 1; execStep >= 0; --execStep) {
                TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo info = procs[i].getExecutionInfo().get((index++));
                Assert.assertEquals(true, info.isRollback());
                Assert.assertEquals(execStep, info.getStep().ordinal());
            }
        }
        // check runnable queue stats
        Assert.assertEquals(0, size());
        Assert.assertEquals(0, procRunnables.addFrontCalls);
        Assert.assertEquals(15, procRunnables.addBackCalls);
        Assert.assertEquals(12, procRunnables.yieldCalls);
        Assert.assertEquals(16, procRunnables.pollCalls);
        Assert.assertEquals(3, procRunnables.completionCalls);
    }

    @Test
    public void testYieldOnInterrupt() throws Exception {
        final int NUM_STATES = 3;
        int count = 0;
        TestYieldProcedures.TestStateMachineProcedure proc = new TestYieldProcedures.TestStateMachineProcedure(true, true);
        ProcedureTestingUtility.submitAndWait(procExecutor, proc);
        // test execute (we execute steps twice, one has the IE the other completes)
        Assert.assertEquals((NUM_STATES * 4), proc.getExecutionInfo().size());
        for (int i = 0; i < NUM_STATES; ++i) {
            TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo info = proc.getExecutionInfo().get((count++));
            Assert.assertEquals(false, info.isRollback());
            Assert.assertEquals(i, info.getStep().ordinal());
            info = proc.getExecutionInfo().get((count++));
            Assert.assertEquals(false, info.isRollback());
            Assert.assertEquals(i, info.getStep().ordinal());
        }
        // test rollback (we execute steps twice, rollback counts both IE and completed)
        for (int i = NUM_STATES - 1; i >= 0; --i) {
            TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo info = proc.getExecutionInfo().get((count++));
            Assert.assertEquals(true, info.isRollback());
            Assert.assertEquals(i, info.getStep().ordinal());
        }
        for (int i = NUM_STATES - 1; i >= 0; --i) {
            TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo info = proc.getExecutionInfo().get((count++));
            Assert.assertEquals(true, info.isRollback());
            Assert.assertEquals(0, info.getStep().ordinal());
        }
        // check runnable queue stats
        Assert.assertEquals(0, size());
        Assert.assertEquals(0, procRunnables.addFrontCalls);
        Assert.assertEquals(11, procRunnables.addBackCalls);
        Assert.assertEquals(10, procRunnables.yieldCalls);
        Assert.assertEquals(12, procRunnables.pollCalls);
        Assert.assertEquals(1, procRunnables.completionCalls);
    }

    @Test
    public void testYieldException() {
        TestYieldProcedures.TestYieldProcedure proc = new TestYieldProcedures.TestYieldProcedure();
        ProcedureTestingUtility.submitAndWait(procExecutor, proc);
        Assert.assertEquals(6, proc.step);
        // check runnable queue stats
        Assert.assertEquals(0, size());
        Assert.assertEquals(0, procRunnables.addFrontCalls);
        Assert.assertEquals(6, procRunnables.addBackCalls);
        Assert.assertEquals(5, procRunnables.yieldCalls);
        Assert.assertEquals(7, procRunnables.pollCalls);
        Assert.assertEquals(1, procRunnables.completionCalls);
    }

    private static class TestProcEnv {
        public final AtomicLong timestamp = new AtomicLong(0);

        public long nextTimestamp() {
            return timestamp.incrementAndGet();
        }
    }

    public static class TestStateMachineProcedure extends StateMachineProcedure<TestYieldProcedures.TestProcEnv, TestYieldProcedures.TestStateMachineProcedure.State> {
        enum State {

            STATE_1,
            STATE_2,
            STATE_3;}

        public static class ExecutionInfo {
            private final boolean rollback;

            private final long timestamp;

            private final TestYieldProcedures.TestStateMachineProcedure.State step;

            public ExecutionInfo(long timestamp, TestYieldProcedures.TestStateMachineProcedure.State step, boolean isRollback) {
                this.timestamp = timestamp;
                this.step = step;
                this.rollback = isRollback;
            }

            public TestYieldProcedures.TestStateMachineProcedure.State getStep() {
                return step;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public boolean isRollback() {
                return rollback;
            }
        }

        private final ArrayList<TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo> executionInfo = new ArrayList<>();

        private final AtomicBoolean aborted = new AtomicBoolean(false);

        private final boolean throwInterruptOnceOnEachStep;

        private final boolean abortOnFinalStep;

        public TestStateMachineProcedure() {
            this(false, false);
        }

        public TestStateMachineProcedure(boolean abortOnFinalStep, boolean throwInterruptOnceOnEachStep) {
            this.abortOnFinalStep = abortOnFinalStep;
            this.throwInterruptOnceOnEachStep = throwInterruptOnceOnEachStep;
        }

        public ArrayList<TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo> getExecutionInfo() {
            return executionInfo;
        }

        @Override
        protected Flow executeFromState(TestYieldProcedures.TestProcEnv env, TestYieldProcedures.TestStateMachineProcedure.State state) throws InterruptedException {
            final long ts = env.nextTimestamp();
            TestYieldProcedures.LOG.info((((((getProcId()) + " execute step ") + state) + " ts=") + ts));
            executionInfo.add(new TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo(ts, state, false));
            Thread.sleep(150);
            if ((throwInterruptOnceOnEachStep) && ((((executionInfo.size()) - 1) % 2) == 0)) {
                TestYieldProcedures.LOG.debug("THROW INTERRUPT");
                throw new InterruptedException("test interrupt");
            }
            switch (state) {
                case STATE_1 :
                    setNextState(TestYieldProcedures.TestStateMachineProcedure.State.STATE_2);
                    break;
                case STATE_2 :
                    setNextState(TestYieldProcedures.TestStateMachineProcedure.State.STATE_3);
                    break;
                case STATE_3 :
                    if (abortOnFinalStep) {
                        setFailure("test", new IOException("Requested abort on final step"));
                    }
                    return NO_MORE_STATE;
                default :
                    throw new UnsupportedOperationException();
            }
            return HAS_MORE_STATE;
        }

        @Override
        protected void rollbackState(TestYieldProcedures.TestProcEnv env, final TestYieldProcedures.TestStateMachineProcedure.State state) throws InterruptedException {
            final long ts = env.nextTimestamp();
            TestYieldProcedures.LOG.debug((((((getProcId()) + " rollback state ") + state) + " ts=") + ts));
            executionInfo.add(new TestYieldProcedures.TestStateMachineProcedure.ExecutionInfo(ts, state, true));
            Thread.sleep(150);
            if ((throwInterruptOnceOnEachStep) && ((((executionInfo.size()) - 1) % 2) == 0)) {
                TestYieldProcedures.LOG.debug("THROW INTERRUPT");
                throw new InterruptedException("test interrupt");
            }
            switch (state) {
                case STATE_1 :
                    break;
                case STATE_2 :
                    break;
                case STATE_3 :
                    break;
                default :
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        protected TestYieldProcedures.TestStateMachineProcedure.State getState(final int stateId) {
            return TestYieldProcedures.TestStateMachineProcedure.State.values()[stateId];
        }

        @Override
        protected int getStateId(final TestYieldProcedures.TestStateMachineProcedure.State state) {
            return state.ordinal();
        }

        @Override
        protected TestYieldProcedures.TestStateMachineProcedure.State getInitialState() {
            return TestYieldProcedures.TestStateMachineProcedure.State.STATE_1;
        }

        @Override
        protected boolean isYieldBeforeExecuteFromState(TestYieldProcedures.TestProcEnv env, TestYieldProcedures.TestStateMachineProcedure.State state) {
            return true;
        }

        @Override
        protected boolean abort(TestYieldProcedures.TestProcEnv env) {
            aborted.set(true);
            return true;
        }
    }

    public static class TestYieldProcedure extends Procedure<TestYieldProcedures.TestProcEnv> {
        private int step = 0;

        public TestYieldProcedure() {
        }

        @Override
        protected Procedure[] execute(final TestYieldProcedures.TestProcEnv env) throws ProcedureYieldException {
            TestYieldProcedures.LOG.info(("execute step " + (step)));
            if (((step)++) < 5) {
                throw new ProcedureYieldException();
            }
            return null;
        }

        @Override
        protected void rollback(TestYieldProcedures.TestProcEnv env) {
        }

        @Override
        protected boolean abort(TestYieldProcedures.TestProcEnv env) {
            return false;
        }

        @Override
        protected boolean isYieldAfterExecutionStep(final TestYieldProcedures.TestProcEnv env) {
            return true;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }
    }

    private static class TestScheduler extends SimpleProcedureScheduler {
        private int completionCalls;

        private int addFrontCalls;

        private int addBackCalls;

        private int yieldCalls;

        private int pollCalls;

        public TestScheduler() {
        }

        @Override
        public void addFront(final Procedure proc) {
            (addFrontCalls)++;
            super.addFront(proc);
        }

        @Override
        public void addBack(final Procedure proc) {
            (addBackCalls)++;
            super.addBack(proc);
        }

        @Override
        public void yield(final Procedure proc) {
            (yieldCalls)++;
            super.yield(proc);
        }

        @Override
        public Procedure poll() {
            (pollCalls)++;
            return super.poll();
        }

        @Override
        public Procedure poll(long timeout, TimeUnit unit) {
            (pollCalls)++;
            return super.poll(timeout, unit);
        }

        @Override
        public void completionCleanup(Procedure proc) {
            (completionCalls)++;
        }
    }
}

