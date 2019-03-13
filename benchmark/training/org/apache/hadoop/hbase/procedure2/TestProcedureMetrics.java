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
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, SmallTests.class })
public class TestProcedureMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureMetrics.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private TestProcedureMetrics.TestProcEnv procEnv;

    private static ProcedureExecutor<TestProcedureMetrics.TestProcEnv> procExecutor;

    private ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    private static int beginCount = 0;

    private static int successCount = 0;

    private static int failedCount = 0;

    @Test
    public void testMetricForSimpleProcedure() throws Exception {
        // procedure that executes successfully
        TestProcedureMetrics.ProcedureMetrics proc = new TestProcedureMetrics.ProcedureMetrics(true);
        long id = ProcedureTestingUtility.submitAndWait(TestProcedureMetrics.procExecutor, proc);
        Assert.assertNotEquals("ProcId zero!", 0, id);
        (TestProcedureMetrics.beginCount)++;
        (TestProcedureMetrics.successCount)++;
        ProcedureTestingUtility.waitProcedure(TestProcedureMetrics.procExecutor, proc);
        Assert.assertEquals("beginCount doesn't match!", TestProcedureMetrics.beginCount, proc.beginCount);
        Assert.assertEquals("successCount doesn't match!", TestProcedureMetrics.successCount, proc.successCount);
        Assert.assertEquals("failedCont doesn't match!", TestProcedureMetrics.failedCount, proc.failedCount);
    }

    @Test
    public void testMetricsForFailedProcedure() throws Exception {
        // procedure that fails
        TestProcedureMetrics.ProcedureMetrics proc = new TestProcedureMetrics.ProcedureMetrics(false);
        long id = ProcedureTestingUtility.submitAndWait(TestProcedureMetrics.procExecutor, proc);
        Assert.assertNotEquals("ProcId zero!", 0, id);
        (TestProcedureMetrics.beginCount)++;
        (TestProcedureMetrics.failedCount)++;
        ProcedureTestingUtility.waitProcedure(TestProcedureMetrics.procExecutor, proc);
        Assert.assertEquals("beginCount doesn't match!", TestProcedureMetrics.beginCount, proc.beginCount);
        Assert.assertEquals("successCount doesn't match!", TestProcedureMetrics.successCount, proc.successCount);
        Assert.assertEquals("failedCont doesn't match!", TestProcedureMetrics.failedCount, proc.failedCount);
    }

    @Test
    public void testMetricForYieldProcedure() throws Exception {
        // procedure that yields
        TestProcedureMetrics.ProcedureMetrics proc = new TestProcedureMetrics.ProcedureMetrics(true, true);
        long id = ProcedureTestingUtility.submitAndWait(TestProcedureMetrics.procExecutor, proc);
        Assert.assertNotEquals("ProcId zero!", 0, id);
        (TestProcedureMetrics.beginCount)++;
        (TestProcedureMetrics.successCount)++;
        ProcedureTestingUtility.waitProcedure(TestProcedureMetrics.procExecutor, proc);
        Assert.assertEquals("beginCount doesn't match!", TestProcedureMetrics.beginCount, proc.beginCount);
        Assert.assertEquals("successCount doesn't match!", TestProcedureMetrics.successCount, proc.successCount);
        Assert.assertEquals("failedCont doesn't match!", TestProcedureMetrics.failedCount, proc.failedCount);
    }

    @Test
    public void testMetricForFailedYiledProcedure() {
        // procedure that yields and fails
        TestProcedureMetrics.ProcedureMetrics proc = new TestProcedureMetrics.ProcedureMetrics(false, true);
        long id = ProcedureTestingUtility.submitAndWait(TestProcedureMetrics.procExecutor, proc);
        Assert.assertNotEquals("ProcId zero!", 0, id);
        (TestProcedureMetrics.beginCount)++;
        (TestProcedureMetrics.failedCount)++;
        ProcedureTestingUtility.waitProcedure(TestProcedureMetrics.procExecutor, proc);
        Assert.assertEquals("beginCount doesn't match!", TestProcedureMetrics.beginCount, proc.beginCount);
        Assert.assertEquals("successCount doesn't match!", TestProcedureMetrics.successCount, proc.successCount);
        Assert.assertEquals("failedCont doesn't match!", TestProcedureMetrics.failedCount, proc.failedCount);
    }

    @Test
    public void testMetricForProcedureWithChildren() throws Exception {
        // Procedure that yileds with one of the sub-procedures that fail
        int subProcCount = 10;
        int failChildIndex = 2;
        int yiledChildIndex = -1;
        TestProcedureMetrics.ProcedureMetrics[] subprocs = new TestProcedureMetrics.ProcedureMetrics[subProcCount];
        for (int i = 0; i < subProcCount; ++i) {
            subprocs[i] = new TestProcedureMetrics.ProcedureMetrics((failChildIndex != i), (yiledChildIndex == i), 3);
        }
        TestProcedureMetrics.ProcedureMetrics proc = new TestProcedureMetrics.ProcedureMetrics(true, true, 3, subprocs);
        long id = ProcedureTestingUtility.submitAndWait(TestProcedureMetrics.procExecutor, proc);
        Assert.assertNotEquals("ProcId zero!", 0, id);
        TestProcedureMetrics.beginCount += subProcCount + 1;
        TestProcedureMetrics.successCount += subProcCount - (failChildIndex + 1);
        if (failChildIndex >= 0) {
            TestProcedureMetrics.failedCount += subProcCount + 1;
        } else {
            (TestProcedureMetrics.successCount)++;
        }
        ProcedureTestingUtility.waitProcedure(TestProcedureMetrics.procExecutor, proc);
        Assert.assertEquals("beginCount doesn't match!", TestProcedureMetrics.beginCount, proc.beginCount);
        Assert.assertEquals("successCount doesn't match!", TestProcedureMetrics.successCount, proc.successCount);
        Assert.assertEquals("failedCont doesn't match!", TestProcedureMetrics.failedCount, proc.failedCount);
    }

    private static class TestProcEnv {
        public boolean toggleKillBeforeStoreUpdate = false;

        public boolean triggerRollbackOnChild = false;
    }

    public static class ProcedureMetrics extends SequentialProcedure<TestProcedureMetrics.TestProcEnv> {
        public static long beginCount = 0;

        public static long successCount = 0;

        public static long failedCount = 0;

        private boolean success;

        private boolean yield;

        private int yieldCount;

        private int yieldNum;

        private TestProcedureMetrics.ProcedureMetrics[] subprocs = null;

        public ProcedureMetrics() {
            this(true);
        }

        public ProcedureMetrics(boolean success) {
            this(success, true);
        }

        public ProcedureMetrics(boolean success, boolean yield) {
            this(success, yield, 1);
        }

        public ProcedureMetrics(boolean success, boolean yield, int yieldCount) {
            this(success, yield, yieldCount, null);
        }

        public ProcedureMetrics(boolean success, TestProcedureMetrics.ProcedureMetrics[] subprocs) {
            this(success, false, 1, subprocs);
        }

        public ProcedureMetrics(boolean success, boolean yield, int yieldCount, TestProcedureMetrics.ProcedureMetrics[] subprocs) {
            this.success = success;
            this.yield = yield;
            this.yieldCount = yieldCount;
            this.subprocs = subprocs;
            yieldNum = 0;
        }

        @Override
        protected void updateMetricsOnSubmit(TestProcedureMetrics.TestProcEnv env) {
            (TestProcedureMetrics.ProcedureMetrics.beginCount)++;
        }

        @Override
        protected Procedure[] execute(TestProcedureMetrics.TestProcEnv env) throws InterruptedException, ProcedureSuspendedException, ProcedureYieldException {
            if (this.yield) {
                if ((yieldNum) < (yieldCount)) {
                    (yieldNum)++;
                    throw new ProcedureYieldException();
                }
            }
            if (!(this.success)) {
                setFailure("Failed", new InterruptedException("Failed"));
                return null;
            }
            return subprocs;
        }

        @Override
        protected void rollback(TestProcedureMetrics.TestProcEnv env) throws IOException, InterruptedException {
        }

        @Override
        protected boolean abort(TestProcedureMetrics.TestProcEnv env) {
            return false;
        }

        @Override
        protected void updateMetricsOnFinish(final TestProcedureMetrics.TestProcEnv env, final long time, boolean success) {
            if (success) {
                (TestProcedureMetrics.ProcedureMetrics.successCount)++;
            } else {
                (TestProcedureMetrics.ProcedureMetrics.failedCount)++;
            }
        }
    }
}

