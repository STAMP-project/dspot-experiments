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


import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
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
public class TestStateMachineProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStateMachineProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestStateMachineProcedure.class);

    private static final Exception TEST_FAILURE_EXCEPTION = new Exception("test failure") {
        private static final long serialVersionUID = 2147942238987041310L;

        @Override
        public boolean equals(final Object other) {
            if ((this) == other)
                return true;

            if (!(other instanceof Exception))
                return false;

            // we are going to serialize the exception in the test,
            // so the instance comparison will not match
            return getMessage().equals(((Exception) (other)).getMessage());
        }

        @Override
        public int hashCode() {
            return getMessage().hashCode();
        }
    };

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private ProcedureExecutor<TestStateMachineProcedure.TestProcEnv> procExecutor;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    @Test
    public void testAbortStuckProcedure() throws InterruptedException {
        try {
            procExecutor.getEnvironment().loop = true;
            TestStateMachineProcedure.TestSMProcedure proc = new TestStateMachineProcedure.TestSMProcedure();
            long procId = procExecutor.submitProcedure(proc);
            Thread.sleep((1000 + ((int) ((Math.random()) * 4001))));
            proc.abort(procExecutor.getEnvironment());
            ProcedureTestingUtility.waitProcedure(procExecutor, procId);
            Assert.assertEquals(true, isFailed());
        } finally {
            procExecutor.getEnvironment().loop = false;
        }
    }

    @Test
    public void testChildOnLastStep() {
        long procId = procExecutor.submitProcedure(new TestStateMachineProcedure.TestSMProcedure());
        ProcedureTestingUtility.waitProcedure(procExecutor, procId);
        Assert.assertEquals(3, procExecutor.getEnvironment().execCount.get());
        Assert.assertEquals(0, procExecutor.getEnvironment().rollbackCount.get());
        ProcedureTestingUtility.assertProcNotFailed(procExecutor, procId);
    }

    @Test
    public void testChildOnLastStepDoubleExecution() throws Exception {
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExecutor, true);
        long procId = procExecutor.submitProcedure(new TestStateMachineProcedure.TestSMProcedure());
        ProcedureTestingUtility.testRecoveryAndDoubleExecution(procExecutor, procId);
        Assert.assertEquals(6, procExecutor.getEnvironment().execCount.get());
        Assert.assertEquals(0, procExecutor.getEnvironment().rollbackCount.get());
        ProcedureTestingUtility.assertProcNotFailed(procExecutor, procId);
    }

    @Test
    public void testChildOnLastStepWithRollback() {
        procExecutor.getEnvironment().triggerChildRollback = true;
        long procId = procExecutor.submitProcedure(new TestStateMachineProcedure.TestSMProcedure());
        ProcedureTestingUtility.waitProcedure(procExecutor, procId);
        Assert.assertEquals(3, procExecutor.getEnvironment().execCount.get());
        Assert.assertEquals(3, procExecutor.getEnvironment().rollbackCount.get());
        Throwable cause = ProcedureTestingUtility.assertProcFailed(procExecutor, procId);
        Assert.assertEquals(TestStateMachineProcedure.TEST_FAILURE_EXCEPTION, cause);
    }

    @Test
    public void testChildNormalRollbackStateCount() {
        procExecutor.getEnvironment().triggerChildRollback = true;
        TestStateMachineProcedure.TestSMProcedureBadRollback testNormalRollback = new TestStateMachineProcedure.TestSMProcedureBadRollback();
        long procId = procExecutor.submitProcedure(testNormalRollback);
        ProcedureTestingUtility.waitProcedure(procExecutor, procId);
        Assert.assertEquals(0, testNormalRollback.stateCount);
    }

    @Test
    public void testChildBadRollbackStateCount() {
        procExecutor.getEnvironment().triggerChildRollback = true;
        TestStateMachineProcedure.TestSMProcedureBadRollback testBadRollback = new TestStateMachineProcedure.TestSMProcedureBadRollback();
        long procId = procExecutor.submitProcedure(testBadRollback);
        ProcedureTestingUtility.waitProcedure(procExecutor, procId);
        Assert.assertEquals(0, testBadRollback.stateCount);
    }

    @Test
    public void testChildOnLastStepWithRollbackDoubleExecution() throws Exception {
        procExecutor.getEnvironment().triggerChildRollback = true;
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExecutor, true);
        long procId = procExecutor.submitProcedure(new TestStateMachineProcedure.TestSMProcedure());
        ProcedureTestingUtility.testRecoveryAndDoubleExecution(procExecutor, procId, true);
        Assert.assertEquals(6, procExecutor.getEnvironment().execCount.get());
        Assert.assertEquals(6, procExecutor.getEnvironment().rollbackCount.get());
        Throwable cause = ProcedureTestingUtility.assertProcFailed(procExecutor, procId);
        Assert.assertEquals(TestStateMachineProcedure.TEST_FAILURE_EXCEPTION, cause);
    }

    public enum TestSMProcedureState {

        STEP_1,
        STEP_2;}

    public static class TestSMProcedure extends StateMachineProcedure<TestStateMachineProcedure.TestProcEnv, TestStateMachineProcedure.TestSMProcedureState> {
        @Override
        protected Flow executeFromState(TestStateMachineProcedure.TestProcEnv env, TestStateMachineProcedure.TestSMProcedureState state) {
            TestStateMachineProcedure.LOG.info(((("EXEC " + state) + " ") + (this)));
            env.execCount.incrementAndGet();
            switch (state) {
                case STEP_1 :
                    if (!(env.loop)) {
                        setNextState(TestStateMachineProcedure.TestSMProcedureState.STEP_2);
                    }
                    break;
                case STEP_2 :
                    addChildProcedure(new TestStateMachineProcedure.SimpleChildProcedure());
                    return NO_MORE_STATE;
            }
            return HAS_MORE_STATE;
        }

        @Override
        protected boolean isRollbackSupported(TestStateMachineProcedure.TestSMProcedureState state) {
            return true;
        }

        @Override
        protected void rollbackState(TestStateMachineProcedure.TestProcEnv env, TestStateMachineProcedure.TestSMProcedureState state) {
            TestStateMachineProcedure.LOG.info(((("ROLLBACK " + state) + " ") + (this)));
            env.rollbackCount.incrementAndGet();
        }

        @Override
        protected TestStateMachineProcedure.TestSMProcedureState getState(int stateId) {
            return TestStateMachineProcedure.TestSMProcedureState.values()[stateId];
        }

        @Override
        protected int getStateId(TestStateMachineProcedure.TestSMProcedureState state) {
            return state.ordinal();
        }

        @Override
        protected TestStateMachineProcedure.TestSMProcedureState getInitialState() {
            return TestStateMachineProcedure.TestSMProcedureState.STEP_1;
        }
    }

    public static class TestSMProcedureBadRollback extends StateMachineProcedure<TestStateMachineProcedure.TestProcEnv, TestStateMachineProcedure.TestSMProcedureState> {
        @Override
        protected Flow executeFromState(TestStateMachineProcedure.TestProcEnv env, TestStateMachineProcedure.TestSMProcedureState state) {
            TestStateMachineProcedure.LOG.info(((("EXEC " + state) + " ") + (this)));
            env.execCount.incrementAndGet();
            switch (state) {
                case STEP_1 :
                    if (!(env.loop)) {
                        setNextState(TestStateMachineProcedure.TestSMProcedureState.STEP_2);
                    }
                    break;
                case STEP_2 :
                    addChildProcedure(new TestStateMachineProcedure.SimpleChildProcedure());
                    return Flow.NO_MORE_STATE;
            }
            return Flow.HAS_MORE_STATE;
        }

        @Override
        protected void rollbackState(TestStateMachineProcedure.TestProcEnv env, TestStateMachineProcedure.TestSMProcedureState state) {
            TestStateMachineProcedure.LOG.info(((("ROLLBACK " + state) + " ") + (this)));
            env.rollbackCount.incrementAndGet();
        }

        @Override
        protected TestStateMachineProcedure.TestSMProcedureState getState(int stateId) {
            return TestStateMachineProcedure.TestSMProcedureState.values()[stateId];
        }

        @Override
        protected int getStateId(TestStateMachineProcedure.TestSMProcedureState state) {
            return state.ordinal();
        }

        @Override
        protected TestStateMachineProcedure.TestSMProcedureState getInitialState() {
            return TestStateMachineProcedure.TestSMProcedureState.STEP_1;
        }

        @Override
        protected void rollback(final TestStateMachineProcedure.TestProcEnv env) throws IOException, InterruptedException {
            if (isEofState()) {
                (stateCount)--;
            }
            try {
                updateTimestamp();
                rollbackState(env, getCurrentState());
                throw new IOException();
            } catch (IOException e) {
                // do nothing for now
            } finally {
                (stateCount)--;
                updateTimestamp();
            }
        }
    }

    public static class SimpleChildProcedure extends ProcedureTestingUtility.NoopProcedure<TestStateMachineProcedure.TestProcEnv> {
        @Override
        protected Procedure<TestStateMachineProcedure.TestProcEnv>[] execute(TestStateMachineProcedure.TestProcEnv env) {
            TestStateMachineProcedure.LOG.info(("EXEC " + (this)));
            env.execCount.incrementAndGet();
            if (env.triggerChildRollback) {
                setFailure("test-failure", TestStateMachineProcedure.TEST_FAILURE_EXCEPTION);
            }
            return null;
        }

        @Override
        protected void rollback(TestStateMachineProcedure.TestProcEnv env) {
            TestStateMachineProcedure.LOG.info(("ROLLBACK " + (this)));
            env.rollbackCount.incrementAndGet();
        }
    }

    public static class TestProcEnv {
        AtomicInteger execCount = new AtomicInteger(0);

        AtomicInteger rollbackCount = new AtomicInteger(0);

        boolean triggerChildRollback = false;

        boolean loop = false;
    }
}

