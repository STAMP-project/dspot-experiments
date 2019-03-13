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


import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotDisabledException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestRestoreSnapshotProcedure extends TestTableDDLProcedureBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRestoreSnapshotProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRestoreSnapshotProcedure.class);

    protected final TableName snapshotTableName = TableName.valueOf("testRestoreSnapshot");

    protected final byte[] CF1 = Bytes.toBytes("cf1");

    protected final byte[] CF2 = Bytes.toBytes("cf2");

    protected final byte[] CF3 = Bytes.toBytes("cf3");

    protected final byte[] CF4 = Bytes.toBytes("cf4");

    protected final int rowCountCF1 = 10;

    protected final int rowCountCF2 = 40;

    protected final int rowCountCF3 = 40;

    protected final int rowCountCF4 = 40;

    protected final int rowCountCF1addition = 10;

    private SnapshotDescription snapshot = null;

    private HTableDescriptor snapshotHTD = null;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRestoreSnapshot() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        long procId = ProcedureTestingUtility.submitAndWait(procExec, new RestoreSnapshotProcedure(procExec.getEnvironment(), snapshotHTD, snapshot));
        ProcedureTestingUtility.assertProcNotFailed(procExec.getResult(procId));
        validateSnapshotRestore();
    }

    @Test
    public void testRestoreSnapshotToDifferentTable() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        final TableName restoredTableName = TableName.valueOf(name.getMethodName());
        final HTableDescriptor newHTD = TestRestoreSnapshotProcedure.createHTableDescriptor(restoredTableName, CF1, CF2);
        long procId = ProcedureTestingUtility.submitAndWait(procExec, new RestoreSnapshotProcedure(procExec.getEnvironment(), newHTD, snapshot));
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestRestoreSnapshotProcedure.LOG.debug(("Restore snapshot failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof TableNotFoundException));
    }

    @Test
    public void testRestoreSnapshotToEnabledTable() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        try {
            TestTableDDLProcedureBase.UTIL.getAdmin().enableTable(snapshotTableName);
            long procId = ProcedureTestingUtility.submitAndWait(procExec, new RestoreSnapshotProcedure(procExec.getEnvironment(), snapshotHTD, snapshot));
            Procedure<?> result = procExec.getResult(procId);
            Assert.assertTrue(result.isFailed());
            TestRestoreSnapshotProcedure.LOG.debug(("Restore snapshot failed with exception: " + (result.getException())));
            Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof TableNotDisabledException));
        } finally {
            TestTableDDLProcedureBase.UTIL.getAdmin().disableTable(snapshotTableName);
        }
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the Restore snapshot procedure && kill the executor
        long procId = procExec.submitProcedure(new RestoreSnapshotProcedure(procExec.getEnvironment(), snapshotHTD, snapshot));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        resetProcExecutorTestingKillFlag();
        validateSnapshotRestore();
    }
}

