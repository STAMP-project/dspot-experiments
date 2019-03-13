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


import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseCommonTestingUtility;
import org.apache.hadoop.hbase.procedure2.store.NoopProcedureStore;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureExecutor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureExecutor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureExecutor.class);

    private TestProcedureExecutor.TestProcEnv procEnv;

    private NoopProcedureStore procStore;

    private ProcedureExecutor<TestProcedureExecutor.TestProcEnv> procExecutor;

    private HBaseCommonTestingUtility htu;

    @Test
    public void testWorkerStuck() throws Exception {
        // replace the executor
        final Configuration conf = new Configuration(htu.getConfiguration());
        conf.setFloat("hbase.procedure.worker.add.stuck.percentage", 0.5F);
        conf.setInt("hbase.procedure.worker.monitor.interval.msec", 500);
        conf.setInt("hbase.procedure.worker.stuck.threshold.msec", 750);
        final int NUM_THREADS = 2;
        createNewExecutor(conf, NUM_THREADS);
        Semaphore latch1 = new Semaphore(2);
        latch1.acquire(2);
        TestProcedureExecutor.BusyWaitProcedure busyProc1 = new TestProcedureExecutor.BusyWaitProcedure(latch1);
        Semaphore latch2 = new Semaphore(2);
        latch2.acquire(2);
        TestProcedureExecutor.BusyWaitProcedure busyProc2 = new TestProcedureExecutor.BusyWaitProcedure(latch2);
        long busyProcId1 = procExecutor.submitProcedure(busyProc1);
        long busyProcId2 = procExecutor.submitProcedure(busyProc2);
        long otherProcId = procExecutor.submitProcedure(new ProcedureTestingUtility.NoopProcedure());
        // wait until a new worker is being created
        int threads1 = waitThreadCount((NUM_THREADS + 1));
        TestProcedureExecutor.LOG.info(("new threads got created: " + (threads1 - NUM_THREADS)));
        Assert.assertEquals((NUM_THREADS + 1), threads1);
        ProcedureTestingUtility.waitProcedure(procExecutor, otherProcId);
        Assert.assertEquals(true, procExecutor.isFinished(otherProcId));
        ProcedureTestingUtility.assertProcNotFailed(procExecutor, otherProcId);
        Assert.assertEquals(true, procExecutor.isRunning());
        Assert.assertEquals(false, procExecutor.isFinished(busyProcId1));
        Assert.assertEquals(false, procExecutor.isFinished(busyProcId2));
        // terminate the busy procedures
        latch1.release();
        latch2.release();
        TestProcedureExecutor.LOG.info("set keep alive and wait threads being removed");
        procExecutor.setKeepAliveTime(500L, TimeUnit.MILLISECONDS);
        int threads2 = waitThreadCount(NUM_THREADS);
        TestProcedureExecutor.LOG.info(("threads got removed: " + (threads1 - threads2)));
        Assert.assertEquals(NUM_THREADS, threads2);
        // terminate the busy procedures
        latch1.release();
        latch2.release();
        // wait for all procs to complete
        ProcedureTestingUtility.waitProcedure(procExecutor, busyProcId1);
        ProcedureTestingUtility.waitProcedure(procExecutor, busyProcId2);
        ProcedureTestingUtility.assertProcNotFailed(procExecutor, busyProcId1);
        ProcedureTestingUtility.assertProcNotFailed(procExecutor, busyProcId2);
    }

    @Test
    public void testSubmitBatch() throws Exception {
        Procedure[] procs = new Procedure[5];
        for (int i = 0; i < (procs.length); ++i) {
            procs[i] = new ProcedureTestingUtility.NoopProcedure<TestProcedureExecutor.TestProcEnv>();
        }
        // submit procedures
        createNewExecutor(htu.getConfiguration(), 3);
        procExecutor.submitProcedures(procs);
        // wait for procs to be completed
        for (int i = 0; i < (procs.length); ++i) {
            final long procId = procs[i].getProcId();
            ProcedureTestingUtility.waitProcedure(procExecutor, procId);
            ProcedureTestingUtility.assertProcNotFailed(procExecutor, procId);
        }
    }

    public static class BusyWaitProcedure extends ProcedureTestingUtility.NoopProcedure<TestProcedureExecutor.TestProcEnv> {
        private final Semaphore latch;

        public BusyWaitProcedure(final Semaphore latch) {
            this.latch = latch;
        }

        @Override
        protected Procedure[] execute(final TestProcedureExecutor.TestProcEnv env) {
            try {
                TestProcedureExecutor.LOG.info(("worker started " + (this)));
                if (!(latch.tryAcquire(1, 30, TimeUnit.SECONDS))) {
                    throw new Exception("waited too long");
                }
                TestProcedureExecutor.LOG.info(("worker step 2 " + (this)));
                if (!(latch.tryAcquire(1, 30, TimeUnit.SECONDS))) {
                    throw new Exception("waited too long");
                }
            } catch (Exception e) {
                TestProcedureExecutor.LOG.error("got unexpected exception", e);
                setFailure("BusyWaitProcedure", e);
            }
            return null;
        }
    }

    private static class TestProcEnv {}
}

