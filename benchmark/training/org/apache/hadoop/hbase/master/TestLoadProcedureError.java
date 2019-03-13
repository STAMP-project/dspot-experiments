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
package org.apache.hadoop.hbase.master;


import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.TableProcedureInterface;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.NoopProcedure;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static TableOperationType.READ;


/**
 * Testcase for HBASE-21490.
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestLoadProcedureError {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLoadProcedureError.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName NAME = TableName.valueOf("Load");

    private static volatile CountDownLatch ARRIVE;

    private static volatile boolean FINISH_PROC;

    private static volatile boolean FAIL_LOAD;

    public static final class TestProcedure extends NoopProcedure<MasterProcedureEnv> implements TableProcedureInterface {
        @Override
        protected Procedure<MasterProcedureEnv>[] execute(MasterProcedureEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if ((TestLoadProcedureError.ARRIVE) != null) {
                TestLoadProcedureError.ARRIVE.countDown();
                TestLoadProcedureError.ARRIVE = null;
            }
            if (TestLoadProcedureError.FINISH_PROC) {
                return null;
            }
            setTimeout(1000);
            setState(ProcedureState.WAITING_TIMEOUT);
            throw new ProcedureSuspendedException();
        }

        @Override
        protected synchronized boolean setTimeoutFailure(MasterProcedureEnv env) {
            setState(ProcedureState.RUNNABLE);
            env.getProcedureScheduler().addBack(this);
            return false;
        }

        @Override
        protected void afterReplay(MasterProcedureEnv env) {
            if (TestLoadProcedureError.FAIL_LOAD) {
                throw new RuntimeException("Inject error");
            }
        }

        @Override
        public TableName getTableName() {
            return TestLoadProcedureError.NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return READ;
        }
    }

    @Test
    public void testLoadError() throws Exception {
        ProcedureExecutor<MasterProcedureEnv> procExec = TestLoadProcedureError.UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor();
        TestLoadProcedureError.ARRIVE = new CountDownLatch(1);
        long procId = procExec.submitProcedure(new TestLoadProcedureError.TestProcedure());
        TestLoadProcedureError.ARRIVE.await();
        TestLoadProcedureError.FAIL_LOAD = true;
        // do not persist the store tracker
        TestLoadProcedureError.UTIL.getMiniHBaseCluster().getMaster().getWalProcedureStore().stop(true);
        TestLoadProcedureError.UTIL.getMiniHBaseCluster().getMaster().abort("for testing");
        waitNoMaster();
        // restart twice, and should fail twice, as we will throw an exception in the afterReplay above
        // in order to reproduce the problem in HBASE-21490 stably, here we will wait until a master is
        // fully done, before starting the new master, otherwise the new master may start too early and
        // call recoverLease on the proc wal files and cause we fail to persist the store tracker when
        // shutting down
        TestLoadProcedureError.UTIL.getMiniHBaseCluster().startMaster();
        waitNoMaster();
        TestLoadProcedureError.UTIL.getMiniHBaseCluster().startMaster();
        waitNoMaster();
        TestLoadProcedureError.FAIL_LOAD = false;
        HMaster master = TestLoadProcedureError.UTIL.getMiniHBaseCluster().startMaster().getMaster();
        waitFor(30000, () -> (master.isActiveMaster()) && (master.isInitialized()));
        // assert the procedure is still there and not finished yet
        TestLoadProcedureError.TestProcedure proc = ((TestLoadProcedureError.TestProcedure) (master.getMasterProcedureExecutor().getProcedure(procId)));
        Assert.assertFalse(isFinished());
        TestLoadProcedureError.FINISH_PROC = true;
        waitFor(30000, () -> proc.isFinished());
    }
}

