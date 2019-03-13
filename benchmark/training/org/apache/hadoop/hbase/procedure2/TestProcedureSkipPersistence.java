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
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.ProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureSkipPersistence {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureSkipPersistence.class);

    private ProcedureExecutor<TestProcedureSkipPersistence.ProcEnv> procExecutor;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    private static volatile int STEP = 0;

    public class ProcEnv {
        public ProcedureExecutor<TestProcedureSkipPersistence.ProcEnv> getProcedureExecutor() {
            return procExecutor;
        }
    }

    public static class TestProcedure extends Procedure<TestProcedureSkipPersistence.ProcEnv> {
        // need to override this method, otherwise we will persist the release lock operation and the
        // test will fail.
        @Override
        protected boolean holdLock(TestProcedureSkipPersistence.ProcEnv env) {
            return true;
        }

        @Override
        protected Procedure<TestProcedureSkipPersistence.ProcEnv>[] execute(TestProcedureSkipPersistence.ProcEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if ((TestProcedureSkipPersistence.STEP) == 0) {
                TestProcedureSkipPersistence.STEP = 1;
                setTimeout(((60 * 60) * 1000));
                setState(ProcedureProtos.ProcedureState.WAITING_TIMEOUT);
                skipPersistence();
                throw new ProcedureSuspendedException();
            } else
                if ((TestProcedureSkipPersistence.STEP) == 1) {
                    TestProcedureSkipPersistence.STEP = 2;
                    if (hasTimeout()) {
                        setFailure("Should not persist the timeout value", new IOException("Should not persist the timeout value"));
                        return null;
                    }
                    setTimeout((2 * 1000));
                    setState(ProcedureProtos.ProcedureState.WAITING_TIMEOUT);
                    // used to confirm that we reset the persist flag before execution
                    throw new ProcedureSuspendedException();
                } else {
                    if (!(hasTimeout())) {
                        setFailure("Should have persisted the timeout value", new IOException("Should have persisted the timeout value"));
                    }
                    return null;
                }

        }

        @Override
        protected synchronized boolean setTimeoutFailure(TestProcedureSkipPersistence.ProcEnv env) {
            setState(ProcedureProtos.ProcedureState.RUNNABLE);
            env.getProcedureExecutor().getProcedureScheduler().addFront(this);
            return false;
        }

        @Override
        protected void rollback(TestProcedureSkipPersistence.ProcEnv env) throws IOException, InterruptedException {
            throw new UnsupportedOperationException();
        }

        @Override
        protected boolean abort(TestProcedureSkipPersistence.ProcEnv env) {
            return false;
        }

        @Override
        protected void serializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }

        @Override
        protected void deserializeStateData(ProcedureStateSerializer serializer) throws IOException {
        }
    }

    @Test
    public void test() throws Exception {
        TestProcedureSkipPersistence.TestProcedure proc = new TestProcedureSkipPersistence.TestProcedure();
        long procId = procExecutor.submitProcedure(proc);
        htu.waitFor(30000, () -> (proc.isWaiting()) && ((procExecutor.getActiveExecutorCount()) == 0));
        ProcedureTestingUtility.restart(procExecutor);
        htu.waitFor(30000, () -> {
            Procedure<?> p = procExecutor.getProcedure(procId);
            return ((p.isWaiting()) || (p.isFinished())) && ((procExecutor.getActiveExecutorCount()) == 0);
        });
        Assert.assertFalse(procExecutor.isFinished(procId));
        ProcedureTestingUtility.restart(procExecutor);
        htu.waitFor(30000, () -> procExecutor.isFinished(procId));
        Procedure<TestProcedureSkipPersistence.ProcEnv> p = procExecutor.getResult(procId);
        Assert.assertTrue(p.isSuccess());
    }
}

