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


import ProcedureState.SUCCESS;
import ProcedureState.WAITING;
import ProcedureState.WAITING_TIMEOUT;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Exchanger;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.wal.WALProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import static org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.NoopProcedure.getState;


@Category({ MasterTests.class, SmallTests.class })
public class TestForceUpdateProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestForceUpdateProcedure.class);

    private static HBaseCommonTestingUtility UTIL = new HBaseCommonTestingUtility();

    private static WALProcedureStore STORE;

    private static ProcedureExecutor<Void> EXEC;

    private static Exchanger<Boolean> EXCHANGER = new Exchanger<>();

    private static int WAL_COUNT = 5;

    @Rule
    public final TestName name = new TestName();

    public static final class WaitingProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.TRUE);
            setState(WAITING_TIMEOUT);
            setTimeout(Integer.MAX_VALUE);
            throw new ProcedureSuspendedException();
        }
    }

    public static final class ParentProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        @SuppressWarnings("unchecked")
        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            return new Procedure[]{ new ProcedureTestingUtility.NoopProcedure(), new TestForceUpdateProcedure.WaitingProcedure() };
        }
    }

    public static final class ExchangeProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        @SuppressWarnings("unchecked")
        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if (TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.TRUE)) {
                return new Procedure[]{ this };
            } else {
                return null;
            }
        }
    }

    public static final class NoopNoAckProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        @Override
        protected boolean shouldWaitClientAck(Void env) {
            return false;
        }
    }

    @Test
    public void testProcedureStuck() throws IOException, InterruptedException {
        TestForceUpdateProcedure.EXEC.submitProcedure(new TestForceUpdateProcedure.ParentProcedure());
        TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.TRUE);
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> (TestForceUpdateProcedure.EXEC.getActiveExecutorCount()) == 0);
        // The above operations are used to make sure that we have persist the states of the two
        // procedures.
        long procId = TestForceUpdateProcedure.EXEC.submitProcedure(new TestForceUpdateProcedure.ExchangeProcedure());
        Assert.assertEquals(1, TestForceUpdateProcedure.STORE.getActiveLogs().size());
        for (int i = 0; i < ((TestForceUpdateProcedure.WAL_COUNT) - 1); i++) {
            Assert.assertTrue(TestForceUpdateProcedure.STORE.rollWriterForTesting());
            // The WaitinProcedure never gets updated so we can not delete the oldest wal file, so the
            // number of wal files will increase
            Assert.assertEquals((2 + i), TestForceUpdateProcedure.STORE.getActiveLogs().size());
            TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.TRUE);
            Thread.sleep(1000);
        }
        TestForceUpdateProcedure.STORE.rollWriterForTesting();
        // Finish the ExchangeProcedure
        TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.FALSE);
        // Make sure that we can delete several wal files because we force update the state of
        // WaitingProcedure. Notice that the last closed wal files can not be deleted, as when rolling
        // the newest wal file does not have anything in it, and in the closed file we still have the
        // state for the ExchangeProcedure so it can not be deleted
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> (TestForceUpdateProcedure.STORE.getActiveLogs().size()) <= 2);
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> TestForceUpdateProcedure.EXEC.isFinished(procId));
        // Make sure that after the force update we could still load the procedures
        stopStoreAndExecutor();
        createStoreAndExecutor();
        Map<Class<?>, Procedure<Void>> procMap = new HashMap<>();
        TestForceUpdateProcedure.EXEC.getActiveProceduresNoCopy().forEach(( p) -> procMap.put(p.getClass(), p));
        Assert.assertEquals(3, procMap.size());
        TestForceUpdateProcedure.ParentProcedure parentProc = ((TestForceUpdateProcedure.ParentProcedure) (procMap.get(TestForceUpdateProcedure.ParentProcedure.class)));
        Assert.assertEquals(WAITING, parentProc.getState());
        TestForceUpdateProcedure.WaitingProcedure waitingProc = ((TestForceUpdateProcedure.WaitingProcedure) (procMap.get(TestForceUpdateProcedure.WaitingProcedure.class)));
        Assert.assertEquals(WAITING_TIMEOUT, waitingProc.getState());
        ProcedureTestingUtility.NoopProcedure<Void> noopProc = ((ProcedureTestingUtility.NoopProcedure<Void>) (procMap.get(ProcedureTestingUtility.NoopProcedure.class)));
        Assert.assertEquals(SUCCESS, getState());
    }

    @Test
    public void testCompletedProcedure() throws IOException, InterruptedException {
        long procId = TestForceUpdateProcedure.EXEC.submitProcedure(new TestForceUpdateProcedure.ExchangeProcedure());
        TestForceUpdateProcedure.EXCHANGER.exchange(Boolean.FALSE);
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> TestForceUpdateProcedure.EXEC.isFinished(procId));
        for (int i = 0; i < ((TestForceUpdateProcedure.WAL_COUNT) - 1); i++) {
            Assert.assertTrue(TestForceUpdateProcedure.STORE.rollWriterForTesting());
            // The exchange procedure is completed but still not deleted yet so we can not delete the
            // oldest wal file
            long pid = TestForceUpdateProcedure.EXEC.submitProcedure(new TestForceUpdateProcedure.NoopNoAckProcedure());
            Assert.assertEquals((2 + i), TestForceUpdateProcedure.STORE.getActiveLogs().size());
            TestForceUpdateProcedure.UTIL.waitFor(10000, () -> TestForceUpdateProcedure.EXEC.isFinished(pid));
        }
        // Only the exchange procedure can not be deleted
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> (TestForceUpdateProcedure.EXEC.getCompletedSize()) == 1);
        TestForceUpdateProcedure.STORE.rollWriterForTesting();
        TestForceUpdateProcedure.UTIL.waitFor(10000, () -> (TestForceUpdateProcedure.STORE.getActiveLogs().size()) <= 1);
    }
}

