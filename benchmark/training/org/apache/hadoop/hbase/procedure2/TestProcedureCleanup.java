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


import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureCleanup {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureCleanup.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureCleanup.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 2;

    private static WALProcedureStore procStore;

    private static ProcedureExecutor<Void> procExecutor;

    private static HBaseCommonTestingUtility htu;

    private static FileSystem fs;

    private static Path testDir;

    private static Path logDir;

    @Rule
    public final TestName name = new TestName();

    @Test
    public void testProcedureShouldNotCleanOnLoad() throws Exception {
        createProcExecutor();
        final TestProcedureCleanup.RootProcedure proc = new TestProcedureCleanup.RootProcedure();
        long rootProc = TestProcedureCleanup.procExecutor.submitProcedure(proc);
        TestProcedureCleanup.LOG.info(("Begin to execute " + rootProc));
        // wait until the child procedure arrival
        TestProcedureCleanup.htu.waitFor(10000, () -> (TestProcedureCleanup.procExecutor.getProcedures().size()) >= 2);
        TestProcedureCleanup.SuspendProcedure suspendProcedure = ((TestProcedureCleanup.SuspendProcedure) (TestProcedureCleanup.procExecutor.getProcedures().get(1)));
        // wait until the suspendProcedure executed
        suspendProcedure.latch.countDown();
        Thread.sleep(100);
        // roll the procedure log
        TestProcedureCleanup.LOG.info("Begin to roll log ");
        TestProcedureCleanup.procStore.rollWriterForTesting();
        TestProcedureCleanup.LOG.info("finish to roll log ");
        Thread.sleep(500);
        TestProcedureCleanup.LOG.info("begin to restart1 ");
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true);
        TestProcedureCleanup.LOG.info("finish to restart1 ");
        Assert.assertTrue(((TestProcedureCleanup.procExecutor.getProcedure(rootProc)) != null));
        Thread.sleep(500);
        TestProcedureCleanup.LOG.info("begin to restart2 ");
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true);
        TestProcedureCleanup.LOG.info("finish to restart2 ");
        Assert.assertTrue(((TestProcedureCleanup.procExecutor.getProcedure(rootProc)) != null));
    }

    @Test
    public void testProcedureUpdatedShouldClean() throws Exception {
        createProcExecutor();
        TestProcedureCleanup.SuspendProcedure suspendProcedure = new TestProcedureCleanup.SuspendProcedure();
        long suspendProc = TestProcedureCleanup.procExecutor.submitProcedure(suspendProcedure);
        TestProcedureCleanup.LOG.info(("Begin to execute " + suspendProc));
        suspendProcedure.latch.countDown();
        Thread.sleep(500);
        TestProcedureCleanup.LOG.info("begin to restart1 ");
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true);
        TestProcedureCleanup.LOG.info("finish to restart1 ");
        TestProcedureCleanup.htu.waitFor(10000, () -> (TestProcedureCleanup.procExecutor.getProcedure(suspendProc)) != null);
        // Wait until the suspendProc executed after restart
        suspendProcedure = ((TestProcedureCleanup.SuspendProcedure) (TestProcedureCleanup.procExecutor.getProcedure(suspendProc)));
        suspendProcedure.latch.countDown();
        Thread.sleep(500);
        // Should be 1 log since the suspendProcedure is updated in the new log
        Assert.assertTrue(((TestProcedureCleanup.procStore.getActiveLogs().size()) == 1));
        // restart procExecutor
        TestProcedureCleanup.LOG.info("begin to restart2");
        // Restart the executor but do not start the workers.
        // Otherwise, the suspendProcedure will soon be executed and the oldest log
        // will be cleaned, leaving only the newest log.
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true, false);
        TestProcedureCleanup.LOG.info("finish to restart2");
        // There should be two active logs
        Assert.assertTrue(((TestProcedureCleanup.procStore.getActiveLogs().size()) == 2));
        TestProcedureCleanup.procExecutor.startWorkers();
    }

    @Test
    public void testProcedureDeletedShouldClean() throws Exception {
        createProcExecutor();
        TestProcedureCleanup.WaitProcedure waitProcedure = new TestProcedureCleanup.WaitProcedure();
        long waitProce = TestProcedureCleanup.procExecutor.submitProcedure(waitProcedure);
        TestProcedureCleanup.LOG.info(("Begin to execute " + waitProce));
        Thread.sleep(500);
        TestProcedureCleanup.LOG.info("begin to restart1 ");
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true);
        TestProcedureCleanup.LOG.info("finish to restart1 ");
        TestProcedureCleanup.htu.waitFor(10000, () -> (TestProcedureCleanup.procExecutor.getProcedure(waitProce)) != null);
        // Wait until the suspendProc executed after restart
        waitProcedure = ((TestProcedureCleanup.WaitProcedure) (TestProcedureCleanup.procExecutor.getProcedure(waitProce)));
        waitProcedure.latch.countDown();
        Thread.sleep(500);
        // Should be 1 log since the suspendProcedure is updated in the new log
        Assert.assertTrue(((TestProcedureCleanup.procStore.getActiveLogs().size()) == 1));
        // restart procExecutor
        TestProcedureCleanup.LOG.info("begin to restart2");
        // Restart the executor but do not start the workers.
        // Otherwise, the suspendProcedure will soon be executed and the oldest log
        // will be cleaned, leaving only the newest log.
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, true, false);
        TestProcedureCleanup.LOG.info("finish to restart2");
        // There should be two active logs
        Assert.assertTrue(((TestProcedureCleanup.procStore.getActiveLogs().size()) == 2));
        TestProcedureCleanup.procExecutor.startWorkers();
    }

    public static final class ExchangeProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        private final Exchanger<Boolean> exchanger = new Exchanger<>();

        @SuppressWarnings("unchecked")
        @Override
        protected Procedure<Void>[] execute(Void env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if (exchanger.exchange(Boolean.TRUE)) {
                return new Procedure[]{ this };
            } else {
                return null;
            }
        }
    }

    @Test
    public void testResetDeleteWhenBuildingHoldingCleanupTracker() throws Exception {
        createProcExecutor();
        TestProcedureCleanup.ExchangeProcedure proc1 = new TestProcedureCleanup.ExchangeProcedure();
        TestProcedureCleanup.ExchangeProcedure proc2 = new TestProcedureCleanup.ExchangeProcedure();
        TestProcedureCleanup.procExecutor.submitProcedure(proc1);
        long procId2 = TestProcedureCleanup.procExecutor.submitProcedure(proc2);
        Thread.sleep(500);
        TestProcedureCleanup.procStore.rollWriterForTesting();
        proc1.exchanger.exchange(Boolean.TRUE);
        Thread.sleep(500);
        FileStatus[] walFiles = TestProcedureCleanup.fs.listStatus(TestProcedureCleanup.logDir);
        Arrays.sort(walFiles, ( f1, f2) -> f1.getPath().getName().compareTo(f2.getPath().getName()));
        // corrupt the first proc wal file, so we will have a partial tracker for it after restarting
        corrupt(walFiles[0]);
        ProcedureTestingUtility.restart(TestProcedureCleanup.procExecutor, false, true);
        // also update proc2, which means that all the procedures in the first proc wal have been
        // updated and it should be deleted.
        proc2 = ((TestProcedureCleanup.ExchangeProcedure) (TestProcedureCleanup.procExecutor.getProcedure(procId2)));
        proc2.exchanger.exchange(Boolean.TRUE);
        TestProcedureCleanup.htu.waitFor(10000, () -> !(TestProcedureCleanup.fs.exists(walFiles[0].getPath())));
    }

    public static class WaitProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        public WaitProcedure() {
            super();
        }

        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        protected Procedure<Void>[] execute(Void env) throws ProcedureSuspendedException {
            // Always wait here
            TestProcedureCleanup.LOG.info("wait here");
            try {
                latch.await();
            } catch (Throwable t) {
            }
            TestProcedureCleanup.LOG.info("finished");
            return null;
        }
    }

    public static class SuspendProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        public SuspendProcedure() {
            super();
        }

        private CountDownLatch latch = new CountDownLatch(1);

        @Override
        protected Procedure<Void>[] execute(Void env) throws ProcedureSuspendedException {
            // Always suspend the procedure
            TestProcedureCleanup.LOG.info("suspend here");
            latch.countDown();
            throw new ProcedureSuspendedException();
        }
    }

    public static class RootProcedure extends ProcedureTestingUtility.NoopProcedure<Void> {
        private boolean childSpwaned = false;

        public RootProcedure() {
            super();
        }

        @Override
        protected Procedure<Void>[] execute(Void env) throws ProcedureSuspendedException {
            if (!(childSpwaned)) {
                childSpwaned = true;
                return new Procedure[]{ new TestProcedureCleanup.SuspendProcedure() };
            } else {
                return null;
            }
        }
    }
}

