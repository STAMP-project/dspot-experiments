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


import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
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
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMergeTableRegionsProcedure {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMergeTableRegionsProcedure.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMergeTableRegionsProcedure.class);

    @Rule
    public final TestName name = new TestName();

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final int initialRegionCount = 4;

    private static final byte[] FAMILY = Bytes.toBytes("FAMILY");

    private static final Configuration conf = TestMergeTableRegionsProcedure.UTIL.getConfiguration();

    private static Admin admin;

    private AssignmentManager am;

    private ProcedureMetrics mergeProcMetrics;

    private ProcedureMetrics assignProcMetrics;

    private ProcedureMetrics unassignProcMetrics;

    private long mergeSubmittedCount = 0;

    private long mergeFailedCount = 0;

    private long assignSubmittedCount = 0;

    private long assignFailedCount = 0;

    private long unassignSubmittedCount = 0;

    private long unassignFailedCount = 0;

    /**
     * This tests two region merges
     */
    @Test
    public void testMergeTwoRegions() throws Exception {
        final TableName tableName = TableName.valueOf(this.name.getMethodName());
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        List<RegionInfo> tableRegions = createTable(tableName);
        RegionInfo[] regionsToMerge = new RegionInfo[2];
        regionsToMerge[0] = tableRegions.get(0);
        regionsToMerge[1] = tableRegions.get(1);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        MergeTableRegionsProcedure proc = new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge, true);
        long procId = procExec.submitProcedure(proc);
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        assertRegionCount(tableName, ((TestMergeTableRegionsProcedure.initialRegionCount) - 1));
        Assert.assertEquals(((mergeSubmittedCount) + 1), mergeProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(mergeFailedCount, mergeProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((assignSubmittedCount) + 1), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((unassignSubmittedCount) + 2), unassignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(unassignFailedCount, unassignProcMetrics.getFailedCounter().getCount());
        Pair<RegionInfo, RegionInfo> pair = MetaTableAccessor.getRegionsFromMergeQualifier(TestMergeTableRegionsProcedure.UTIL.getConnection(), proc.getMergedRegion().getRegionName());
        Assert.assertTrue((((pair.getFirst()) != null) && ((pair.getSecond()) != null)));
        // Can I purge the merged regions from hbase:meta? Check that all went
        // well by looking at the merged row up in hbase:meta. It should have no
        // more mention of the merged regions; they are purged as last step in
        // the merged regions cleanup.
        TestMergeTableRegionsProcedure.UTIL.getHBaseCluster().getMaster().setCatalogJanitorEnabled(true);
        TestMergeTableRegionsProcedure.UTIL.getHBaseCluster().getMaster().getCatalogJanitor().triggerNow();
        while (((pair != null) && ((pair.getFirst()) != null)) && ((pair.getSecond()) != null)) {
            pair = MetaTableAccessor.getRegionsFromMergeQualifier(TestMergeTableRegionsProcedure.UTIL.getConnection(), proc.getMergedRegion().getRegionName());
        } 
    }

    /**
     * This tests two concurrent region merges
     */
    @Test
    public void testMergeRegionsConcurrently() throws Exception {
        final TableName tableName = TableName.valueOf("testMergeRegionsConcurrently");
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        List<RegionInfo> tableRegions = createTable(tableName);
        RegionInfo[] regionsToMerge1 = new RegionInfo[2];
        RegionInfo[] regionsToMerge2 = new RegionInfo[2];
        regionsToMerge1[0] = tableRegions.get(0);
        regionsToMerge1[1] = tableRegions.get(1);
        regionsToMerge2[0] = tableRegions.get(2);
        regionsToMerge2[1] = tableRegions.get(3);
        // collect AM metrics before test
        collectAssignmentManagerMetrics();
        long procId1 = procExec.submitProcedure(new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge1, true));
        long procId2 = procExec.submitProcedure(new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge2, true));
        ProcedureTestingUtility.waitProcedure(procExec, procId1);
        ProcedureTestingUtility.waitProcedure(procExec, procId2);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId1);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId2);
        assertRegionCount(tableName, ((TestMergeTableRegionsProcedure.initialRegionCount) - 2));
        Assert.assertEquals(((mergeSubmittedCount) + 2), mergeProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(mergeFailedCount, mergeProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((assignSubmittedCount) + 2), assignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(assignFailedCount, assignProcMetrics.getFailedCounter().getCount());
        Assert.assertEquals(((unassignSubmittedCount) + 4), unassignProcMetrics.getSubmittedCounter().getCount());
        Assert.assertEquals(unassignFailedCount, unassignProcMetrics.getFailedCounter().getCount());
    }

    @Test
    public void testRecoveryAndDoubleExecution() throws Exception {
        final TableName tableName = TableName.valueOf("testRecoveryAndDoubleExecution");
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        List<RegionInfo> tableRegions = createTable(tableName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillIfHasParent(procExec, false);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        RegionInfo[] regionsToMerge = new RegionInfo[2];
        regionsToMerge[0] = tableRegions.get(0);
        regionsToMerge[1] = tableRegions.get(1);
        long procId = procExec.submitProcedure(new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge, true));
        // Restart the executor and execute the step twice
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId);
        ProcedureTestingUtility.assertProcNotFailed(procExec, procId);
        assertRegionCount(tableName, ((TestMergeTableRegionsProcedure.initialRegionCount) - 1));
    }

    @Test
    public void testRollbackAndDoubleExecution() throws Exception {
        final TableName tableName = TableName.valueOf("testRollbackAndDoubleExecution");
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        List<RegionInfo> tableRegions = createTable(tableName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        RegionInfo[] regionsToMerge = new RegionInfo[2];
        regionsToMerge[0] = tableRegions.get(0);
        regionsToMerge[1] = tableRegions.get(1);
        long procId = procExec.submitProcedure(new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge, true));
        // Failing before MERGE_TABLE_REGIONS_UPDATE_META we should trigger the rollback
        // NOTE: the 8 (number of MERGE_TABLE_REGIONS_UPDATE_META step) is
        // hardcoded, so you have to look at this test at least once when you add a new step.
        int lastStep = 8;
        MasterProcedureTestingUtility.testRollbackAndDoubleExecution(procExec, procId, lastStep, true);
        Assert.assertEquals(TestMergeTableRegionsProcedure.initialRegionCount, TestMergeTableRegionsProcedure.UTIL.getAdmin().getRegions(tableName).size());
        TestMergeTableRegionsProcedure.UTIL.waitUntilAllRegionsAssigned(tableName);
        List<HRegion> regions = TestMergeTableRegionsProcedure.UTIL.getMiniHBaseCluster().getRegions(tableName);
        Assert.assertEquals(TestMergeTableRegionsProcedure.initialRegionCount, regions.size());
    }

    @Test
    public void testMergeWithoutPONR() throws Exception {
        final TableName tableName = TableName.valueOf("testMergeWithoutPONR");
        final ProcedureExecutor<MasterProcedureEnv> procExec = getMasterProcedureExecutor();
        List<RegionInfo> tableRegions = createTable(tableName);
        ProcedureTestingUtility.waitNoProcedureRunning(procExec);
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, true);
        RegionInfo[] regionsToMerge = new RegionInfo[2];
        regionsToMerge[0] = tableRegions.get(0);
        regionsToMerge[1] = tableRegions.get(1);
        long procId = procExec.submitProcedure(new MergeTableRegionsProcedure(procExec.getEnvironment(), regionsToMerge, true));
        // Execute until step 9 of split procedure
        // NOTE: step 9 is after step MERGE_TABLE_REGIONS_UPDATE_META
        MasterProcedureTestingUtility.testRecoveryAndDoubleExecution(procExec, procId, 9, false);
        // Unset Toggle Kill and make ProcExec work correctly
        ProcedureTestingUtility.setKillAndToggleBeforeStoreUpdate(procExec, false);
        MasterProcedureTestingUtility.restartMasterProcedureExecutor(procExec);
        ProcedureTestingUtility.waitProcedure(procExec, procId);
        assertRegionCount(tableName, ((TestMergeTableRegionsProcedure.initialRegionCount) - 1));
    }
}

