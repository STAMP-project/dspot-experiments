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
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.procedure2.Procedure;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestCloneSnapshotProcedure extends TestTableDDLProcedureBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCloneSnapshotProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCloneSnapshotProcedure.class);

    protected final byte[] CF = Bytes.toBytes("cf1");

    private static SnapshotDescription snapshot = null;

    @Test
    public void testCloneSnapshot() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        final TableName clonedTableName = TableName.valueOf("testCloneSnapshot2");
        final TableDescriptor htd = TestCloneSnapshotProcedure.createTableDescriptor(clonedTableName, CF);
        // take the snapshot
        SnapshotProtos.SnapshotDescription snapshotDesc = getSnapshot();
        long procId = ProcedureTestingUtility.submitAndWait(procExec, new CloneSnapshotProcedure(procExec.getEnvironment(), htd, snapshotDesc));
        ProcedureTestingUtility.assertProcNotFailed(procExec.getResult(procId));
        MasterProcedureTestingUtility.validateTableIsEnabled(TestTableDDLProcedureBase.UTIL.getHBaseCluster().getMaster(), clonedTableName);
    }

    @Test
    public void testCloneSnapshotToSameTable() throws Exception {
        // take the snapshot
        SnapshotProtos.SnapshotDescription snapshotDesc = getSnapshot();
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        final TableName clonedTableName = TableName.valueOf(snapshotDesc.getTable());
        final TableDescriptor htd = TestCloneSnapshotProcedure.createTableDescriptor(clonedTableName, CF);
        long procId = ProcedureTestingUtility.submitAndWait(procExec, new CloneSnapshotProcedure(procExec.getEnvironment(), htd, snapshotDesc));
        Procedure<?> result = procExec.getResult(procId);
        Assert.assertTrue(result.isFailed());
        TestCloneSnapshotProcedure.LOG.debug(("Clone snapshot failed with exception: " + (result.getException())));
        Assert.assertTrue(((ProcedureTestingUtility.getExceptionCause(result)) instanceof TableExistsException));
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        final TableName clonedTableName = TableName.valueOf("testRecoveryAndDoubleExecution");
        final TableDescriptor htd = TestCloneSnapshotProcedure.createTableDescriptor(clonedTableName, CF);
        // take the snapshot
        SnapshotProtos.SnapshotDescription snapshotDesc = getSnapshot();
        // Here if you enable this then we will enter an infinite loop, as we will fail either after
        // TRSP.openRegion or after OpenRegionProcedure.execute, so we can never finish the TRSP...
        ProcedureTestingUtility.setKillIfHasParent(procExec, false);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the Clone snapshot procedure && kill the executor
        long procId = procExec.submitProcedure(new CloneSnapshotProcedure(procExec.getEnvironment(), htd, snapshotDesc));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        MasterProcedureTestingUtility.validateTableIsEnabled(TestTableDDLProcedureBase.UTIL.getHBaseCluster().getMaster(), clonedTableName);
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        final TableName clonedTableName = TableName.valueOf("testRollbackAndDoubleExecution");
        final TableDescriptor htd = TestCloneSnapshotProcedure.createTableDescriptor(clonedTableName, CF);
        // take the snapshot
        SnapshotProtos.SnapshotDescription snapshotDesc = getSnapshot();
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Start the Clone snapshot procedure && kill the executor
        long procId = procExec.submitProcedure(new CloneSnapshotProcedure(procExec.getEnvironment(), htd, snapshotDesc));
        int lastStep = 2;// failing before CLONE_SNAPSHOT_WRITE_FS_LAYOUT

        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep);
        MasterProcedureTestingUtility.validateTableDeletion(TestTableDDLProcedureBase.UTIL.getHBaseCluster().getMaster(), clonedTableName);
    }
}

