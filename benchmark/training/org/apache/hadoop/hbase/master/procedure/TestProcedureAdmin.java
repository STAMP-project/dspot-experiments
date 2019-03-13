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


import java.util.List;
import java.util.Random;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestProcedureAdmin {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestProcedureAdmin.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestProcedureAdmin.class);

    @Rule
    public TestName name = new TestName();

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void testAbortProcedureSuccess() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f");
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Submit an abortable procedure
        long procId = procExec.submitProcedure(new DisableTableProcedure(procExec.getEnvironment(), tableName, false));
        // Wait for one step to complete
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        boolean abortResult = procExec.abort(procId, true);
        Assert.assertTrue(abortResult);
        MasterProcedureTestingUtility.testRestartWithAbort(procExec, procId);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        // Validate the disable table procedure was aborted successfully
        MasterProcedureTestingUtility.validateTableIsEnabled(TestProcedureAdmin.UTIL.getHBaseCluster().getMaster(), tableName);
    }

    @Test
    public void testAbortProcedureFailure() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f");
        TestProcedureAdmin.UTIL.getAdmin().disableTable(tableName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Submit an un-abortable procedure
        long procId = procExec.submitProcedure(new DeleteTableProcedure(procExec.getEnvironment(), tableName));
        // Wait for a couple of steps to complete (first step "prepare" is abortable)
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        for (int i = 0; i < 2; ++i) {
            ProcedureTestingUtility.assertProcNotYetCompleted(procExec, procId);
            ProcedureTestingUtility.restart(procExec);
            ProcedureTestingUtility.waitProcedure(procExec, procId);
        }
        boolean abortResult = procExec.abort(procId, true);
        Assert.assertFalse(abortResult);
        MasterProcedureTestingUtility.testRestartWithAbort(procExec, procId);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        // Validate the delete table procedure was not aborted
        MasterProcedureTestingUtility.validateTableDeletion(TestProcedureAdmin.UTIL.getHBaseCluster().getMaster(), tableName);
    }

    @Test
    public void testAbortProcedureInterruptedNotAllowed() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f");
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Submit a procedure
        long procId = procExec.submitProcedure(new DisableTableProcedure(procExec.getEnvironment(), tableName, true));
        // Wait for one step to complete
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        // Set the mayInterruptIfRunning flag to false
        boolean abortResult = procExec.abort(procId, false);
        Assert.assertFalse(abortResult);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, false);
        ProcedureTestingUtility.restart(procExec);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        // Validate the delete table procedure was not aborted
        MasterProcedureTestingUtility.validateTableIsDisabled(TestProcedureAdmin.UTIL.getHBaseCluster().getMaster(), tableName);
    }

    @Test
    public void testAbortNonExistProcedure() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        Random randomGenerator = new Random();
        long procId;
        // Generate a non-existing procedure
        do {
            procId = randomGenerator.nextLong();
        } while ((procExec.getResult(procId)) != null );
        boolean abortResult = procExec.abort(procId, true);
        Assert.assertFalse(abortResult);
    }

    @Test
    public void testGetProcedure() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        MasterProcedureTestingUtility.createTable(procExec, tableName, null, "f");
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        long procId = procExec.submitProcedure(new DisableTableProcedure(procExec.getEnvironment(), tableName, false));
        // Wait for one step to complete
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        List<Procedure<MasterProcedureEnv>> procedures = procExec.getProcedures();
        Assert.assertTrue(((procedures.size()) >= 1));
        boolean found = false;
        for (Procedure<?> proc : procedures) {
            if ((proc.getProcId()) == procId) {
                Assert.assertTrue(proc.isRunnable());
                found = true;
            } else {
                Assert.assertTrue(proc.isSuccess());
            }
        }
        Assert.assertTrue(found);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, false);
        ProcedureTestingUtility.restart(procExec);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        procedures = procExec.getProcedures();
        for (Procedure proc : procedures) {
            Assert.assertTrue(proc.isSuccess());
        }
    }
}

