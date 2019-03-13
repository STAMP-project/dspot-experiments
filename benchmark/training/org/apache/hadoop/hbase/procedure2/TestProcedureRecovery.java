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


import Int32Value.Builder;
import StateMachineProcedure.Flow;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hbase.thirdparty.com.google.protobuf.Int32Value;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Flow.HAS_MORE_STATE;
import static Flow.NO_MORE_STATE;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureRecovery {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureRecovery.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureRecovery.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static TestProcedureRecovery.TestProcEnv procEnv;

    private static ProcedureExecutor<TestProcedureRecovery.TestProcEnv> procExecutor;

    private static ProcedureStore procStore;

    private static int procSleepInterval;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    public static class TestSingleStepProcedure extends SequentialProcedure<TestProcedureRecovery.TestProcEnv> {
        private int step = 0;

        public TestSingleStepProcedure() {
        }

        @Override
        protected Procedure[] execute(TestProcedureRecovery.TestProcEnv env) throws InterruptedException {
            env.waitOnLatch();
            TestProcedureRecovery.LOG.debug(((("execute procedure " + (this)) + " step=") + (step)));
            (step)++;
            setResult(Bytes.toBytes(step));
            return null;
        }

        @Override
        protected void rollback(TestProcedureRecovery.TestProcEnv env) {
        }

        @Override
        protected boolean abort(TestProcedureRecovery.TestProcEnv env) {
            return true;
        }
    }

    public static class BaseTestStepProcedure extends SequentialProcedure<TestProcedureRecovery.TestProcEnv> {
        private AtomicBoolean abort = new AtomicBoolean(false);

        private int step = 0;

        @Override
        protected Procedure[] execute(TestProcedureRecovery.TestProcEnv env) throws InterruptedException {
            env.waitOnLatch();
            TestProcedureRecovery.LOG.debug(((("execute procedure " + (this)) + " step=") + (step)));
            ProcedureTestingUtility.toggleKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor);
            (step)++;
            Threads.sleepWithoutInterrupt(TestProcedureRecovery.procSleepInterval);
            if (isAborted()) {
                setFailure(new RemoteProcedureException(getClass().getName(), new ProcedureAbortedException(((("got an abort at " + (getClass().getName())) + " step=") + (step)))));
                return null;
            }
            return null;
        }

        @Override
        protected void rollback(TestProcedureRecovery.TestProcEnv env) {
            TestProcedureRecovery.LOG.debug(((("rollback procedure " + (this)) + " step=") + (step)));
            ProcedureTestingUtility.toggleKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor);
            (step)++;
        }

        @Override
        protected boolean abort(TestProcedureRecovery.TestProcEnv env) {
            abort.set(true);
            return true;
        }

        private boolean isAborted() {
            boolean aborted = abort.get();
            TestProcedureRecovery.BaseTestStepProcedure proc = this;
            while ((hasParent()) && (!aborted)) {
                proc = ((TestProcedureRecovery.BaseTestStepProcedure) (TestProcedureRecovery.procExecutor.getProcedure(getParentProcId())));
                aborted = proc.isAborted();
            } 
            return aborted;
        }
    }

    public static class TestMultiStepProcedure extends TestProcedureRecovery.BaseTestStepProcedure {
        public TestMultiStepProcedure() {
        }

        @Override
        public Procedure[] execute(TestProcedureRecovery.TestProcEnv env) throws InterruptedException {
            super.execute(env);
            return isFailed() ? null : new Procedure[]{ new TestProcedureRecovery.TestMultiStepProcedure.Step1Procedure() };
        }

        public static class Step1Procedure extends TestProcedureRecovery.BaseTestStepProcedure {
            public Step1Procedure() {
            }

            @Override
            protected Procedure[] execute(TestProcedureRecovery.TestProcEnv env) throws InterruptedException {
                super.execute(env);
                return isFailed() ? null : new Procedure[]{ new TestProcedureRecovery.TestMultiStepProcedure.Step2Procedure() };
            }
        }

        public static class Step2Procedure extends TestProcedureRecovery.BaseTestStepProcedure {
            public Step2Procedure() {
            }
        }
    }

    @Test
    public void testNoopLoad() throws Exception {
        restart();
    }

    @Test
    public void testSingleStepProcRecovery() throws Exception {
        Procedure proc = new TestProcedureRecovery.TestSingleStepProcedure();
        TestProcedureRecovery.procExecutor.testing.killBeforeStoreUpdate = true;
        long procId = ProcedureTestingUtility.submitAndWait(TestProcedureRecovery.procExecutor, proc);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        TestProcedureRecovery.procExecutor.testing.killBeforeStoreUpdate = false;
        // Restart and verify that the procedures restart
        long restartTs = EnvironmentEdgeManager.currentTime();
        restart();
        waitProcedure(procId);
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        Assert.assertTrue(((result.getLastUpdate()) > restartTs));
        ProcedureTestingUtility.assertProcNotFailed(result);
        Assert.assertEquals(1, Bytes.toInt(result.getResult()));
        long resultTs = result.getLastUpdate();
        // Verify that after another restart the result is still there
        restart();
        result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertProcNotFailed(result);
        Assert.assertEquals(resultTs, result.getLastUpdate());
        Assert.assertEquals(1, Bytes.toInt(result.getResult()));
    }

    @Test
    public void testMultiStepProcRecovery() throws Exception {
        // Step 0 - kill
        Procedure proc = new TestProcedureRecovery.TestMultiStepProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestProcedureRecovery.procExecutor, proc);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 0 exec && Step 1 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 1 exec && step 2 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 2 exec
        restart();
        waitProcedure(procId);
        Assert.assertTrue(TestProcedureRecovery.procExecutor.isRunning());
        // The procedure is completed
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertProcNotFailed(result);
    }

    @Test
    public void testMultiStepRollbackRecovery() throws Exception {
        // Step 0 - kill
        Procedure proc = new TestProcedureRecovery.TestMultiStepProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestProcedureRecovery.procExecutor, proc);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 0 exec && Step 1 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 1 exec && step 2 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 2 exec - rollback - kill
        TestProcedureRecovery.procSleepInterval = 2500;
        restart();
        Assert.assertTrue(TestProcedureRecovery.procExecutor.abort(procId));
        waitProcedure(procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // rollback - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // rollback - complete
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Restart the executor and get the result
        restart();
        waitProcedure(procId);
        // The procedure is completed
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertIsAbortException(result);
    }

    public static class TestStateMachineProcedure extends StateMachineProcedure<TestProcedureRecovery.TestProcEnv, TestProcedureRecovery.TestStateMachineProcedure.State> {
        enum State {

            STATE_1,
            STATE_2,
            STATE_3,
            DONE;}

        public TestStateMachineProcedure() {
        }

        public TestStateMachineProcedure(final boolean testSubmitChildProc) {
            this.submitChildProc = testSubmitChildProc;
        }

        private AtomicBoolean aborted = new AtomicBoolean(false);

        private int iResult = 0;

        private boolean submitChildProc = false;

        @Override
        protected Flow executeFromState(TestProcedureRecovery.TestProcEnv env, TestProcedureRecovery.TestStateMachineProcedure.State state) {
            switch (state) {
                case STATE_1 :
                    TestProcedureRecovery.LOG.info(("execute step 1 " + (this)));
                    setNextState(TestProcedureRecovery.TestStateMachineProcedure.State.STATE_2);
                    iResult += 3;
                    break;
                case STATE_2 :
                    TestProcedureRecovery.LOG.info(("execute step 2 " + (this)));
                    if (submitChildProc) {
                        addChildProcedure(new TestProcedureRecovery.TestStateMachineProcedure(), new TestProcedureRecovery.TestStateMachineProcedure());
                        setNextState(TestProcedureRecovery.TestStateMachineProcedure.State.DONE);
                    } else {
                        setNextState(TestProcedureRecovery.TestStateMachineProcedure.State.STATE_3);
                    }
                    iResult += 5;
                    break;
                case STATE_3 :
                    TestProcedureRecovery.LOG.info(("execute step 3 " + (this)));
                    Threads.sleepWithoutInterrupt(TestProcedureRecovery.procSleepInterval);
                    if (aborted.get()) {
                        TestProcedureRecovery.LOG.info(("aborted step 3 " + (this)));
                        setAbortFailure("test", "aborted");
                        break;
                    }
                    setNextState(TestProcedureRecovery.TestStateMachineProcedure.State.DONE);
                    iResult += 7;
                    break;
                case DONE :
                    if (submitChildProc) {
                        addChildProcedure(new TestProcedureRecovery.TestStateMachineProcedure());
                    }
                    iResult += 11;
                    setResult(Bytes.toBytes(iResult));
                    return NO_MORE_STATE;
                default :
                    throw new UnsupportedOperationException();
            }
            return HAS_MORE_STATE;
        }

        @Override
        protected void rollbackState(TestProcedureRecovery.TestProcEnv env, final TestProcedureRecovery.TestStateMachineProcedure.State state) {
            switch (state) {
                case STATE_1 :
                    TestProcedureRecovery.LOG.info(("rollback step 1 " + (this)));
                    break;
                case STATE_2 :
                    TestProcedureRecovery.LOG.info(("rollback step 2 " + (this)));
                    break;
                case STATE_3 :
                    TestProcedureRecovery.LOG.info(("rollback step 3 " + (this)));
                    break;
                default :
                    throw new UnsupportedOperationException();
            }
        }

        @Override
        protected TestProcedureRecovery.TestStateMachineProcedure.State getState(final int stateId) {
            return TestProcedureRecovery.TestStateMachineProcedure.State.values()[stateId];
        }

        @Override
        protected int getStateId(final TestProcedureRecovery.TestStateMachineProcedure.State state) {
            return state.ordinal();
        }

        @Override
        protected TestProcedureRecovery.TestStateMachineProcedure.State getInitialState() {
            return TestProcedureRecovery.TestStateMachineProcedure.State.STATE_1;
        }

        @Override
        protected boolean abort(TestProcedureRecovery.TestProcEnv env) {
            aborted.set(true);
            return true;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
            super.serializeStateData(serializer);
            Int32Value.Builder builder = Int32Value.newBuilder().setValue(iResult);
            serializer.serialize(builder.build());
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
            super.deserializeStateData(serializer);
            Int32Value value = serializer.deserialize(Int32Value.class);
            iResult = value.getValue();
        }
    }

    @Test
    public void testStateMachineMultipleLevel() throws Exception {
        long procId = TestProcedureRecovery.procExecutor.submitProcedure(new TestProcedureRecovery.TestStateMachineProcedure(true));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(TestProcedureRecovery.procExecutor, procId);
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertProcNotFailed(result);
        Assert.assertEquals(19, Bytes.toInt(result.getResult()));
        Assert.assertEquals(4, TestProcedureRecovery.procExecutor.getLastProcId());
    }

    @Test
    public void testStateMachineRecovery() throws Exception {
        ProcedureTestingUtility.setToggleKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor, true);
        ProcedureTestingUtility.setKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor, true);
        // Step 1 - kill
        Procedure proc = new TestProcedureRecovery.TestStateMachineProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestProcedureRecovery.procExecutor, proc);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 1 exec && Step 2 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 2 exec && step 3 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 3 exec
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        restart();
        waitProcedure(procId);
        Assert.assertTrue(TestProcedureRecovery.procExecutor.isRunning());
        // The procedure is completed
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertProcNotFailed(result);
        Assert.assertEquals(26, Bytes.toInt(result.getResult()));
    }

    @Test
    public void testStateMachineRollbackRecovery() throws Exception {
        ProcedureTestingUtility.setToggleKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor, true);
        ProcedureTestingUtility.setKillBeforeStoreUpdate(TestProcedureRecovery.procExecutor, true);
        // Step 1 - kill
        Procedure proc = new TestProcedureRecovery.TestStateMachineProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestProcedureRecovery.procExecutor, proc);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 1 exec && Step 2 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 2 exec && step 3 - kill
        restart();
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Step 3 exec - rollback step 3 - kill
        TestProcedureRecovery.procSleepInterval = 2500;
        restart();
        Assert.assertTrue(TestProcedureRecovery.procExecutor.abort(procId));
        waitProcedure(procId);
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        // Rollback step 3 - rollback step 2 - kill
        restart();
        waitProcedure(procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        // Rollback step 2 - step 1 - kill
        restart();
        waitProcedure(procId);
        Assert.assertFalse(TestProcedureRecovery.procExecutor.isRunning());
        ProcedureTestingUtility.assertProcNotYetCompleted(TestProcedureRecovery.procExecutor, procId);
        // Rollback step 1 - complete
        restart();
        waitProcedure(procId);
        Assert.assertTrue(TestProcedureRecovery.procExecutor.isRunning());
        // The procedure is completed
        Procedure<?> result = TestProcedureRecovery.procExecutor.getResult(procId);
        ProcedureTestingUtility.assertIsAbortException(result);
    }

    private static class TestProcEnv {
        private CountDownLatch latch = null;

        /**
         * set/unset a latch. every procedure execute() step will wait on the latch if any.
         */
        public void setWaitLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        public void waitOnLatch() throws InterruptedException {
            if ((latch) != null) {
                latch.await();
            }
        }
    }
}

