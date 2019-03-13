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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureExecution {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureExecution.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureExecution.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static final Procedure<?> NULL_PROC = null;

    private ProcedureExecutor<Void> procExecutor;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    private static class TestProcedureException extends IOException {
        private static final long serialVersionUID = 8798565784658913798L;

        public TestProcedureException(String msg) {
            super(msg);
        }
    }

    public static class TestSequentialProcedure extends SequentialProcedure<Void> {
        private final Procedure<Void>[] subProcs;

        private final List<String> state;

        private final Exception failure;

        private final String name;

        public TestSequentialProcedure() {
            throw new UnsupportedOperationException("recovery should not be triggered here");
        }

        public TestSequentialProcedure(String name, List<String> state, Procedure... subProcs) {
            this.state = state;
            this.subProcs = subProcs;
            this.name = name;
            this.failure = null;
        }

        public TestSequentialProcedure(String name, List<String> state, Exception failure) {
            this.state = state;
            this.subProcs = null;
            this.name = name;
            this.failure = failure;
        }

        @Override
        protected Procedure<Void>[] execute(Void env) {
            state.add(((name) + "-execute"));
            if ((failure) != null) {
                setFailure(new RemoteProcedureException(((name) + "-failure"), failure));
                return null;
            }
            return subProcs;
        }

        @Override
        protected void rollback(Void env) {
            state.add(((name) + "-rollback"));
        }

        @Override
        protected boolean abort(Void env) {
            state.add(((name) + "-abort"));
            return true;
        }
    }

    @Test
    public void testBadSubprocList() {
        List<String> state = new ArrayList<>();
        Procedure<Void> subProc2 = new TestProcedureExecution.TestSequentialProcedure("subProc2", state);
        Procedure<Void> subProc1 = new TestProcedureExecution.TestSequentialProcedure("subProc1", state, subProc2, TestProcedureExecution.NULL_PROC);
        Procedure<Void> rootProc = new TestProcedureExecution.TestSequentialProcedure("rootProc", state, subProc1);
        long rootId = ProcedureTestingUtility.submitAndWait(procExecutor, rootProc);
        // subProc1 has a "null" subprocedure which is catched as InvalidArgument
        // failed state with 2 execute and 2 rollback
        TestProcedureExecution.LOG.info(Objects.toString(state));
        Procedure<?> result = procExecutor.getResult(rootId);
        Assert.assertTrue(state.toString(), result.isFailed());
        ProcedureTestingUtility.assertIsIllegalArgumentException(result);
        Assert.assertEquals(state.toString(), 4, state.size());
        Assert.assertEquals("rootProc-execute", state.get(0));
        Assert.assertEquals("subProc1-execute", state.get(1));
        Assert.assertEquals("subProc1-rollback", state.get(2));
        Assert.assertEquals("rootProc-rollback", state.get(3));
    }

    @Test
    public void testSingleSequentialProc() {
        List<String> state = new ArrayList<>();
        Procedure<Void> subProc2 = new TestProcedureExecution.TestSequentialProcedure("subProc2", state);
        Procedure<Void> subProc1 = new TestProcedureExecution.TestSequentialProcedure("subProc1", state, subProc2);
        Procedure<Void> rootProc = new TestProcedureExecution.TestSequentialProcedure("rootProc", state, subProc1);
        long rootId = ProcedureTestingUtility.submitAndWait(procExecutor, rootProc);
        // successful state, with 3 execute
        TestProcedureExecution.LOG.info(Objects.toString(state));
        Procedure<?> result = procExecutor.getResult(rootId);
        ProcedureTestingUtility.assertProcNotFailed(result);
        Assert.assertEquals(state.toString(), 3, state.size());
    }

    @Test
    public void testSingleSequentialProcRollback() {
        List<String> state = new ArrayList<>();
        Procedure<Void> subProc2 = new TestProcedureExecution.TestSequentialProcedure("subProc2", state, new TestProcedureExecution.TestProcedureException("fail test"));
        Procedure<Void> subProc1 = new TestProcedureExecution.TestSequentialProcedure("subProc1", state, subProc2);
        Procedure<Void> rootProc = new TestProcedureExecution.TestSequentialProcedure("rootProc", state, subProc1);
        long rootId = ProcedureTestingUtility.submitAndWait(procExecutor, rootProc);
        // the 3rd proc fail, rollback after 2 successful execution
        TestProcedureExecution.LOG.info(Objects.toString(state));
        Procedure<?> result = procExecutor.getResult(rootId);
        Assert.assertTrue(state.toString(), result.isFailed());
        TestProcedureExecution.LOG.info(result.getException().getMessage());
        Throwable cause = ProcedureTestingUtility.getExceptionCause(result);
        Assert.assertTrue(("expected TestProcedureException, got " + cause), (cause instanceof TestProcedureExecution.TestProcedureException));
        Assert.assertEquals(state.toString(), 6, state.size());
        Assert.assertEquals("rootProc-execute", state.get(0));
        Assert.assertEquals("subProc1-execute", state.get(1));
        Assert.assertEquals("subProc2-execute", state.get(2));
        Assert.assertEquals("subProc2-rollback", state.get(3));
        Assert.assertEquals("subProc1-rollback", state.get(4));
        Assert.assertEquals("rootProc-rollback", state.get(5));
    }

    public static class TestFaultyRollback extends SequentialProcedure<Void> {
        private int retries = 0;

        public TestFaultyRollback() {
        }

        @Override
        protected Procedure<Void>[] execute(Void env) {
            setFailure("faulty-rollback-test", new TestProcedureExecution.TestProcedureException("test faulty rollback"));
            return null;
        }

        @Override
        protected void rollback(Void env) throws IOException {
            if ((++(retries)) < 3) {
                TestProcedureExecution.LOG.info(("inject rollback failure " + (retries)));
                throw new IOException(("injected failure number " + (retries)));
            }
            TestProcedureExecution.LOG.info(("execute non faulty rollback step retries=" + (retries)));
        }

        @Override
        protected boolean abort(Void env) {
            return false;
        }
    }

    @Test
    public void testRollbackRetriableFailure() {
        long procId = ProcedureTestingUtility.submitAndWait(procExecutor, new TestProcedureExecution.TestFaultyRollback());
        Procedure<?> result = procExecutor.getResult(procId);
        Assert.assertTrue("expected a failure", result.isFailed());
        TestProcedureExecution.LOG.info(result.getException().getMessage());
        Throwable cause = ProcedureTestingUtility.getExceptionCause(result);
        Assert.assertTrue(("expected TestProcedureException, got " + cause), (cause instanceof TestProcedureExecution.TestProcedureException));
    }

    public static class TestWaitingProcedure extends SequentialProcedure<Void> {
        private final List<String> state;

        private final boolean hasChild;

        private final String name;

        public TestWaitingProcedure() {
            throw new UnsupportedOperationException("recovery should not be triggered here");
        }

        public TestWaitingProcedure(String name, List<String> state, boolean hasChild) {
            this.hasChild = hasChild;
            this.state = state;
            this.name = name;
        }

        @Override
        protected Procedure<Void>[] execute(Void env) {
            state.add(((name) + "-execute"));
            setState(ProcedureState.WAITING_TIMEOUT);
            return hasChild ? new Procedure[]{ new TestProcedureExecution.TestWaitingProcedure.TestWaitChild(name, state) } : null;
        }

        @Override
        protected void rollback(Void env) {
            state.add(((name) + "-rollback"));
        }

        @Override
        protected boolean abort(Void env) {
            state.add(((name) + "-abort"));
            return true;
        }

        public static class TestWaitChild extends SequentialProcedure<Void> {
            private final List<String> state;

            private final String name;

            public TestWaitChild() {
                throw new UnsupportedOperationException("recovery should not be triggered here");
            }

            public TestWaitChild(String name, List<String> state) {
                this.name = name;
                this.state = state;
            }

            @Override
            protected Procedure<Void>[] execute(Void env) {
                state.add(((name) + "-child-execute"));
                return null;
            }

            @Override
            protected void rollback(Void env) {
                throw new UnsupportedOperationException("should not rollback a successful child procedure");
            }

            @Override
            protected boolean abort(Void env) {
                state.add(((name) + "-child-abort"));
                return true;
            }
        }
    }

    @Test
    public void testAbortTimeout() {
        final int PROC_TIMEOUT_MSEC = 2500;
        List<String> state = new ArrayList<>();
        Procedure<Void> proc = new TestProcedureExecution.TestWaitingProcedure("wproc", state, false);
        proc.setTimeout(PROC_TIMEOUT_MSEC);
        long startTime = EnvironmentEdgeManager.currentTime();
        long rootId = ProcedureTestingUtility.submitAndWait(procExecutor, proc);
        long execTime = (EnvironmentEdgeManager.currentTime()) - startTime;
        TestProcedureExecution.LOG.info(Objects.toString(state));
        Assert.assertTrue(("we didn't wait enough execTime=" + execTime), (execTime >= PROC_TIMEOUT_MSEC));
        Procedure<?> result = procExecutor.getResult(rootId);
        Assert.assertTrue(state.toString(), result.isFailed());
        ProcedureTestingUtility.assertIsTimeoutException(result);
        Assert.assertEquals(state.toString(), 2, state.size());
        Assert.assertEquals("wproc-execute", state.get(0));
        Assert.assertEquals("wproc-rollback", state.get(1));
    }

    @Test
    public void testAbortTimeoutWithChildren() {
        List<String> state = new ArrayList<>();
        Procedure<Void> proc = new TestProcedureExecution.TestWaitingProcedure("wproc", state, true);
        proc.setTimeout(2500);
        long rootId = ProcedureTestingUtility.submitAndWait(procExecutor, proc);
        TestProcedureExecution.LOG.info(Objects.toString(state));
        Procedure<?> result = procExecutor.getResult(rootId);
        Assert.assertTrue(state.toString(), result.isFailed());
        ProcedureTestingUtility.assertIsTimeoutException(result);
        Assert.assertEquals(state.toString(), 3, state.size());
        Assert.assertEquals("wproc-execute", state.get(0));
        Assert.assertEquals("wproc-child-execute", state.get(1));
        Assert.assertEquals("wproc-rollback", state.get(2));
    }
}

