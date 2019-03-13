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


import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.wal.WALProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


/**
 * Testcase for HBASE-20973
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureRollbackAIOOB {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureRollbackAIOOB.class);

    private static final HBaseCommonTestingUtility UTIL = new HBaseCommonTestingUtility();

    public static final class ParentProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        private final CountDownLatch latch = new CountDownLatch(1);

        private boolean scheduled;

        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            latch.await();
            if (scheduled) {
                return null;
            }
            scheduled = true;
            return new Procedure[]{ new TestProcedureRollbackAIOOB.SubProcedure() };
        }
    }

    public static final class SubProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        @Override
        protected Procedure[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            setFailure("Inject error", new RuntimeException("Inject error"));
            return null;
        }
    }

    private WALProcedureStore procStore;

    private ProcedureExecutor<Void> procExec;

    @Rule
    public final TestName name = new TestName();

    @Test
    public void testArrayIndexOutOfBounds() {
        TestProcedureRollbackAIOOB.ParentProcedure proc = new TestProcedureRollbackAIOOB.ParentProcedure();
        long procId = procExec.submitProcedure(proc);
        long noopProcId = -1L;
        // make sure that the sub procedure will have a new BitSetNode
        for (int i = 0; i < ((Long.SIZE) - 2); i++) {
            noopProcId = procExec.submitProcedure(new ProcedureTestingUtility.NoopProcedure());
        }
        final long lastNoopProcId = noopProcId;
        TestProcedureRollbackAIOOB.UTIL.waitFor(30000, () -> procExec.isFinished(lastNoopProcId));
        proc.latch.countDown();
        TestProcedureRollbackAIOOB.UTIL.waitFor(10000, () -> procExec.isFinished(procId));
    }
}

