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
public class TestChildProcedures {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestChildProcedures.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestChildProcedures.class);

    private static final int PROCEDURE_EXECUTOR_SLOTS = 1;

    private static TestChildProcedures.TestProcEnv procEnv;

    private static ProcedureExecutor<TestChildProcedures.TestProcEnv> procExecutor;

    private static ProcedureStore procStore;

    private HBaseCommonTestingUtility htu;

    private FileSystem fs;

    private Path testDir;

    private Path logDir;

    @Test
    public void testChildLoad() throws Exception {
        TestChildProcedures.procEnv.toggleKillBeforeStoreUpdate = false;
        TestChildProcedures.TestRootProcedure proc = new TestChildProcedures.TestRootProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestChildProcedures.procExecutor, proc);
        ProcedureTestingUtility.restart(TestChildProcedures.procExecutor);
        ProcedureTestingUtility.waitProcedure(TestChildProcedures.procExecutor, proc);
        Assert.assertTrue("expected completed proc", TestChildProcedures.procExecutor.isFinished(procId));
        ProcedureTestingUtility.assertProcNotFailed(TestChildProcedures.procExecutor, procId);
    }

    @Test
    public void testChildLoadWithSteppedRestart() throws Exception {
        TestChildProcedures.procEnv.toggleKillBeforeStoreUpdate = true;
        TestChildProcedures.TestRootProcedure proc = new TestChildProcedures.TestRootProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestChildProcedures.procExecutor, proc);
        int restartCount = 0;
        while (!(TestChildProcedures.procExecutor.isFinished(procId))) {
            ProcedureTestingUtility.restart(TestChildProcedures.procExecutor);
            ProcedureTestingUtility.waitProcedure(TestChildProcedures.procExecutor, proc);
            restartCount++;
        } 
        Assert.assertEquals(3, restartCount);
        Assert.assertTrue("expected completed proc", TestChildProcedures.procExecutor.isFinished(procId));
        ProcedureTestingUtility.assertProcNotFailed(TestChildProcedures.procExecutor, procId);
    }

    /**
     * Test the state setting that happens after store to WAL; in particular the bit where we
     * set the parent runnable again after its children have all completed successfully.
     * See HBASE-20978.
     */
    @Test
    public void testChildLoadWithRestartAfterChildSuccess() throws Exception {
        TestChildProcedures.procEnv.toggleKillAfterStoreUpdate = true;
        TestChildProcedures.TestRootProcedure proc = new TestChildProcedures.TestRootProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestChildProcedures.procExecutor, proc);
        int restartCount = 0;
        while (!(TestChildProcedures.procExecutor.isFinished(procId))) {
            ProcedureTestingUtility.restart(TestChildProcedures.procExecutor);
            ProcedureTestingUtility.waitProcedure(TestChildProcedures.procExecutor, proc);
            restartCount++;
        } 
        Assert.assertEquals(4, restartCount);
        Assert.assertTrue("expected completed proc", TestChildProcedures.procExecutor.isFinished(procId));
        ProcedureTestingUtility.assertProcNotFailed(TestChildProcedures.procExecutor, procId);
    }

    @Test
    public void testChildRollbackLoad() throws Exception {
        TestChildProcedures.procEnv.toggleKillBeforeStoreUpdate = false;
        TestChildProcedures.procEnv.triggerRollbackOnChild = true;
        TestChildProcedures.TestRootProcedure proc = new TestChildProcedures.TestRootProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestChildProcedures.procExecutor, proc);
        ProcedureTestingUtility.restart(TestChildProcedures.procExecutor);
        ProcedureTestingUtility.waitProcedure(TestChildProcedures.procExecutor, proc);
        assertProcFailed(procId);
    }

    @Test
    public void testChildRollbackLoadWithSteppedRestart() throws Exception {
        TestChildProcedures.procEnv.toggleKillBeforeStoreUpdate = true;
        TestChildProcedures.procEnv.triggerRollbackOnChild = true;
        TestChildProcedures.TestRootProcedure proc = new TestChildProcedures.TestRootProcedure();
        long procId = ProcedureTestingUtility.submitAndWait(TestChildProcedures.procExecutor, proc);
        int restartCount = 0;
        while (!(TestChildProcedures.procExecutor.isFinished(procId))) {
            ProcedureTestingUtility.restart(TestChildProcedures.procExecutor);
            ProcedureTestingUtility.waitProcedure(TestChildProcedures.procExecutor, proc);
            restartCount++;
        } 
        Assert.assertEquals(2, restartCount);
        assertProcFailed(procId);
    }

    public static class TestRootProcedure extends SequentialProcedure<TestChildProcedures.TestProcEnv> {
        public TestRootProcedure() {
        }

        @Override
        public Procedure[] execute(TestChildProcedures.TestProcEnv env) {
            if (env.toggleKillBeforeStoreUpdate) {
                ProcedureTestingUtility.toggleKillBeforeStoreUpdate(TestChildProcedures.procExecutor);
            }
            if (env.toggleKillAfterStoreUpdate) {
                ProcedureTestingUtility.toggleKillAfterStoreUpdate(TestChildProcedures.procExecutor);
            }
            return new Procedure[]{ new TestChildProcedures.TestChildProcedure(), new TestChildProcedures.TestChildProcedure() };
        }

        @Override
        public void rollback(TestChildProcedures.TestProcEnv env) {
        }

        @Override
        public boolean abort(TestChildProcedures.TestProcEnv env) {
            return false;
        }
    }

    public static class TestChildProcedure extends SequentialProcedure<TestChildProcedures.TestProcEnv> {
        public TestChildProcedure() {
        }

        @Override
        public Procedure[] execute(TestChildProcedures.TestProcEnv env) {
            if (env.toggleKillBeforeStoreUpdate) {
                ProcedureTestingUtility.toggleKillBeforeStoreUpdate(TestChildProcedures.procExecutor);
            }
            if (env.triggerRollbackOnChild) {
                setFailure("test", new Exception("test"));
            }
            return null;
        }

        @Override
        public void rollback(TestChildProcedures.TestProcEnv env) {
        }

        @Override
        public boolean abort(TestChildProcedures.TestProcEnv env) {
            return false;
        }
    }

    private static class TestProcEnv {
        public boolean toggleKillBeforeStoreUpdate = false;

        public boolean toggleKillAfterStoreUpdate = false;

        public boolean triggerRollbackOnChild = false;
    }
}

