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
package org.apache.hadoop.hbase.master.assignment;


import java.io.IOException;
import java.util.List;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureEnv;
import org.apache.hadoop.hbase.master.procedure.MasterProcedureTestingUtility;
import org.apache.hadoop.hbase.procedure2.ProcedureExecutor;
import org.apache.hadoop.hbase.procedure2.ProcedureMetrics;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.regionserver.HRegion;
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
public class TestSplitTableRegionProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitTableRegionProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSplitTableRegionProcedure.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static String ColumnFamilyName1 = "cf1";

    private static String ColumnFamilyName2 = "cf2";

    private static final int startRowNum = 11;

    private static final int rowCount = 60;

    private AssignmentManager am;

    private ProcedureMetrics splitProcMetrics;

    private ProcedureMetrics assignProcMetrics;

    private ProcedureMetrics unassignProcMetrics;

    private long splitSubmittedCount = 0;

    private long splitFailedCount = 0;

    private long assignSubmittedCount = 0;

    private long assignFailedCount = 0;

    private long unassignSubmittedCount = 0;

    private long unassignFailedCount = 0;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSplitTableRegion() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 2);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        verify(tableName, splitRowNum);
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((assignSubmittedCount) + 2), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((unassignSubmittedCount) + 1), unassignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(unassignFailedCount, unassignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testSplitTableRegionNoStoreFile() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 2);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        Assert.assertTrue(((TestSplitTableRegionProcedure.UTIL.getMiniHBaseCluster().getRegions(tableName).size()) == 2));
        Assert.assertTrue(((TestSplitTableRegionProcedure.UTIL.countRows(tableName)) == 0));
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testSplitTableRegionUnevenDaughter() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        // Split to two daughters with one of them only has 1 row
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 4);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        verify(tableName, splitRowNum);
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testSplitTableRegionEmptyDaughter() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        // Split to two daughters with one of them only has 1 row
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + (TestSplitTableRegionProcedure.rowCount);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        // Make sure one daughter has 0 rows.
        List<HRegion> daughters = TestSplitTableRegionProcedure.UTIL.getMiniHBaseCluster().getRegions(tableName);
        Assert.assertTrue(((daughters.size()) == 2));
        Assert.assertTrue(((TestSplitTableRegionProcedure.UTIL.countRows(tableName)) == (TestSplitTableRegionProcedure.rowCount)));
        Assert.assertTrue((((TestSplitTableRegionProcedure.UTIL.countRows(daughters.get(0))) == 0) || ((TestSplitTableRegionProcedure.UTIL.countRows(daughters.get(1))) == 0)));
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testSplitTableRegionDeletedRowsDaughter() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        // Split to two daughters with one of them only has 1 row
        int splitRowNum = TestSplitTableRegionProcedure.rowCount;
        deleteData(tableName, splitRowNum);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Wait the completion
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        TestSplitTableRegionProcedure.UTIL.getAdmin().majorCompact(tableName);
        // waiting for the major compaction to complete
        TestSplitTableRegionProcedure.UTIL.waitFor(6000, new Waiter.Predicate<IOException>() {
            @Override
            public boolean evaluate() throws IOException {
                return (TestSplitTableRegionProcedure.UTIL.getAdmin().getCompactionState(tableName)) == CompactionState.NONE;
            }
        });
        // Make sure one daughter has 0 rows.
        List<HRegion> daughters = TestSplitTableRegionProcedure.UTIL.getMiniHBaseCluster().getRegions(tableName);
        Assert.assertTrue(((daughters.size()) == 2));
        final int currentRowCount = splitRowNum - (TestSplitTableRegionProcedure.startRowNum);
        Assert.assertTrue(((TestSplitTableRegionProcedure.UTIL.countRows(tableName)) == currentRowCount));
        Assert.assertTrue((((TestSplitTableRegionProcedure.UTIL.countRows(daughters.get(0))) == 0) || ((TestSplitTableRegionProcedure.UTIL.countRows(daughters.get(1))) == 0)));
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testInvalidSplitKey() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table with null split key
        try {
            long procId1 = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], null));
            ProcedureTestingUtility.waitProcedure(procExec, procId1);
            Assert.fail("unexpected procedure start with invalid split-key");
        } catch (DoNotRetryIOException e) {
            TestSplitTableRegionProcedure.LOG.debug(("Expected Split procedure construction failure: " + (e.getMessage())));
        }
        Assert.assertEquals(splitSubmittedCount, splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 2);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Failing before SPLIT_TABLE_REGION_UPDATE_META we should trigger the
        // rollback
        // NOTE: the 7 (number of SPLIT_TABLE_REGION_UPDATE_META step) is
        // hardcoded, so you have to look at this test at least once when you add a new step.
        int lastStep = 7;
        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep, true);
        // check that we have only 1 region
        Assert.assertEquals(1, TestSplitTableRegionProcedure.UTIL.getAdmin().getRegions(tableName).size());
        TestSplitTableRegionProcedure.UTIL.waitUntilAllRegionsAssigned(tableName);
        List<HRegion> newRegions = TestSplitTableRegionProcedure.UTIL.getMiniHBaseCluster().getRegions(tableName);
        Assert.assertEquals(1, newRegions.size());
        verifyData(newRegions.get(0), TestSplitTableRegionProcedure.startRowNum, TestSplitTableRegionProcedure.rowCount, Bytes.toBytes(TestSplitTableRegionProcedure.ColumnFamilyName1), Bytes.toBytes(TestSplitTableRegionProcedure.ColumnFamilyName2));
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(((splitFailedCount) + 1), splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 2);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillIfHasParent(procExec, false);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        verify(tableName, splitRowNum);
        Assert.assertEquals(((splitSubmittedCount) + 1), splitProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(splitFailedCount, splitProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testSplitWithoutPONR() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        RegionInfo[] regions = MasterProcedureTestingUtility.createTable(procExec, tableName, null, TestSplitTableRegionProcedure.ColumnFamilyName1, TestSplitTableRegionProcedure.ColumnFamilyName2);
        insertData(tableName);
        int splitRowNum = (TestSplitTableRegionProcedure.startRowNum) + ((TestSplitTableRegionProcedure.rowCount) / 2);
        byte[] splitKey = Bytes.toBytes(("" + splitRowNum));
        Assert.assertTrue("not able to find a splittable region", (regions != null));
        Assert.assertTrue("not able to find a splittable region", ((regions.length) == 1));
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        // Split region of the table
        long procId = procExec.submitProcedure(new SplitTableRegionProcedure(procExec.getEnvironment(), regions[0], splitKey));
        // Execute until step 7 of split procedure
        // NOTE: the 7 (number after SPLIT_TABLE_REGION_UPDATE_META step)
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId, 7, false);
        // Unset Toggle Kill and make ProcExec work correctly
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, false);
        MasterProcedureTestingUtility.restartMasterProcedureExecutor(procExec);
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        // Even split failed after step 4, it should still works fine
        verify(tableName, splitRowNum);
    }
}

