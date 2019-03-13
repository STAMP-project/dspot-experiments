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
package org.apache.hadoop.hbase.master.procedure;


import java.util.concurrent.Semaphore;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureSuspendedException;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility.NoopProcedure;
import org.apache.hadoop.hbase.procedure2.ProcedureYieldException;
import org.apache.hadoop.hbase.procedure2.store.wal.WALProcedureStore;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import static LockState.LOCK_ACQUIRED;
import static LockState.LOCK_EVENT_WAIT;
import static TableOperationType.EDIT;
import static TableOperationType.READ;


@Category({ MasterTests.class, LargeTests.class })
public class TestSchedulerQueueDeadLock {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSchedulerQueueDeadLock.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("deadlock");

    private static final class TestEnv {
        private final MasterProcedureScheduler scheduler;

        public TestEnv(MasterProcedureScheduler scheduler) {
            this.scheduler = scheduler;
        }

        public MasterProcedureScheduler getScheduler() {
            return scheduler;
        }
    }

    public static class TableSharedProcedure extends NoopProcedure<TestSchedulerQueueDeadLock.TestEnv> implements TableProcedureInterface {
        private final Semaphore latch = new Semaphore(0);

        @Override
        protected Procedure<TestSchedulerQueueDeadLock.TestEnv>[] execute(TestSchedulerQueueDeadLock.TestEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            latch.acquire();
            return null;
        }

        @Override
        protected LockState acquireLock(TestSchedulerQueueDeadLock.TestEnv env) {
            if (env.getScheduler().waitTableSharedLock(this, getTableName())) {
                return LOCK_EVENT_WAIT;
            }
            return LOCK_ACQUIRED;
        }

        @Override
        protected void releaseLock(TestSchedulerQueueDeadLock.TestEnv env) {
            env.getScheduler().wakeTableSharedLock(this, getTableName());
        }

        @Override
        protected boolean holdLock(TestSchedulerQueueDeadLock.TestEnv env) {
            return true;
        }

        @Override
        public TableName getTableName() {
            return TestSchedulerQueueDeadLock.TABLE_NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return READ;
        }
    }

    public static class TableExclusiveProcedure extends NoopProcedure<TestSchedulerQueueDeadLock.TestEnv> implements TableProcedureInterface {
        private final Semaphore latch = new Semaphore(0);

        @Override
        protected Procedure<TestSchedulerQueueDeadLock.TestEnv>[] execute(TestSchedulerQueueDeadLock.TestEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            latch.acquire();
            return null;
        }

        @Override
        protected LockState acquireLock(TestSchedulerQueueDeadLock.TestEnv env) {
            if (env.getScheduler().waitTableExclusiveLock(this, getTableName())) {
                return LockState.LOCK_EVENT_WAIT;
            }
            return LockState.LOCK_ACQUIRED;
        }

        @Override
        protected void releaseLock(TestSchedulerQueueDeadLock.TestEnv env) {
            env.getScheduler().wakeTableExclusiveLock(this, getTableName());
        }

        @Override
        protected boolean holdLock(TestSchedulerQueueDeadLock.TestEnv env) {
            return true;
        }

        @Override
        public TableName getTableName() {
            return TestSchedulerQueueDeadLock.TABLE_NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return EDIT;
        }
    }

    private WALProcedureStore procStore;

    private ProcedureExecutor<TestSchedulerQueueDeadLock.TestEnv> procExec;

    @Rule
    public final TestName name = new TestName();

    public static final class TableSharedProcedureWithId extends TestSchedulerQueueDeadLock.TableSharedProcedure {
        @Override
        protected void setProcId(long procId) {
            // this is a hack to make this procedure be loaded after the procedure below as we will sort
            // the procedures by id when loading.
            super.setProcId(2L);
        }
    }

    public static final class TableExclusiveProcedureWithId extends TestSchedulerQueueDeadLock.TableExclusiveProcedure {
        @Override
        protected void setProcId(long procId) {
            // this is a hack to make this procedure be loaded before the procedure above as we will
            // sort the procedures by id when loading.
            super.setProcId(1L);
        }
    }

    @Test
    public void testTableProcedureDeadLockAfterRestarting() throws Exception {
        // let the shared procedure run first, but let it have a greater procId so when loading it will
        // be loaded at last.
        long procId1 = procExec.submitProcedure(new TestSchedulerQueueDeadLock.TableSharedProcedureWithId());
        long procId2 = procExec.submitProcedure(new TestSchedulerQueueDeadLock.TableExclusiveProcedureWithId());
        procExec.startWorkers();
        waitFor(10000, () -> ((org.apache.hadoop.hbase.master.procedure.TableSharedProcedure) (procExec.getProcedure(procId1))).latch.hasQueuedThreads());
        ProcedureTestingUtility.restart(procExec);
        ((TestSchedulerQueueDeadLock.TableSharedProcedure) (procExec.getProcedure(procId1))).latch.release();
        ((TestSchedulerQueueDeadLock.TableExclusiveProcedure) (procExec.getProcedure(procId2))).latch.release();
        waitFor(10000, () -> procExec.isFinished(procId1));
        waitFor(10000, () -> procExec.isFinished(procId2));
    }

    public static final class TableShardParentProcedure extends NoopProcedure<TestSchedulerQueueDeadLock.TestEnv> implements TableProcedureInterface {
        private boolean scheduled;

        @Override
        protected Procedure<TestSchedulerQueueDeadLock.TestEnv>[] execute(TestSchedulerQueueDeadLock.TestEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if (!(scheduled)) {
                scheduled = true;
                return new Procedure[]{ new TestSchedulerQueueDeadLock.TableSharedProcedure() };
            }
            return null;
        }

        @Override
        protected LockState acquireLock(TestSchedulerQueueDeadLock.TestEnv env) {
            if (env.getScheduler().waitTableSharedLock(this, getTableName())) {
                return LockState.LOCK_EVENT_WAIT;
            }
            return LockState.LOCK_ACQUIRED;
        }

        @Override
        protected void releaseLock(TestSchedulerQueueDeadLock.TestEnv env) {
            env.getScheduler().wakeTableSharedLock(this, getTableName());
        }

        @Override
        protected boolean holdLock(TestSchedulerQueueDeadLock.TestEnv env) {
            return true;
        }

        @Override
        public TableName getTableName() {
            return TestSchedulerQueueDeadLock.TABLE_NAME;
        }

        @Override
        public TableOperationType getTableOperationType() {
            return TableOperationType.READ;
        }
    }

    @Test
    public void testTableProcedureSubProcedureDeadLock() throws Exception {
        // the shared procedure will also schedule a shared procedure, but after the exclusive procedure
        long procId1 = procExec.submitProcedure(new TestSchedulerQueueDeadLock.TableShardParentProcedure());
        long procId2 = procExec.submitProcedure(new TestSchedulerQueueDeadLock.TableExclusiveProcedure());
        procExec.startWorkers();
        waitFor(10000, () -> procExec.getProcedures().stream().anyMatch(( p) -> p instanceof org.apache.hadoop.hbase.master.procedure.TableSharedProcedure));
        procExec.getProcedures().stream().filter(( p) -> p instanceof org.apache.hadoop.hbase.master.procedure.TableSharedProcedure).map(( p) -> ((org.apache.hadoop.hbase.master.procedure.TableSharedProcedure) (p))).forEach(( p) -> p.latch.release());
        ((TestSchedulerQueueDeadLock.TableExclusiveProcedure) (procExec.getProcedure(procId2))).latch.release();
        waitFor(10000, () -> procExec.isFinished(procId1));
        waitFor(10000, () -> procExec.isFinished(procId2));
    }
}

