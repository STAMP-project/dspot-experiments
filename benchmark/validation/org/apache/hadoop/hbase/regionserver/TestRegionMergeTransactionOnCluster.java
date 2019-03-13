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
package org.apache.hadoop.hbase.regionserver;


import HConstants.CATALOG_FAMILY;
import HConstants.MERGEA_QUALIFIER;
import HConstants.MERGEB_QUALIFIER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.UnknownRegionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.DoNotRetryRegionException;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionReplicaUtil;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.exceptions.MergeRegionException;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.MasterRpcServices;
import org.apache.hadoop.hbase.master.RegionState;
import org.apache.hadoop.hbase.master.assignment.AssignmentManager;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.RegionStateTransition.TransitionCode;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionResponse;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.util.PairOfSameType;
import org.apache.hadoop.util.StringUtils;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.zookeeper.KeeperException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, LargeTests.class })
public class TestRegionMergeTransactionOnCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionMergeTransactionOnCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionMergeTransactionOnCluster.class);

    @Rule
    public TestName name = new TestName();

    private static final int NB_SERVERS = 3;

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    private static final byte[] QUALIFIER = Bytes.toBytes("q");

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static final int INITIAL_REGION_NUM = 10;

    private static final int ROWSIZE = 200;

    private static byte[][] ROWS = TestRegionMergeTransactionOnCluster.makeN(TestRegionMergeTransactionOnCluster.ROW, TestRegionMergeTransactionOnCluster.ROWSIZE);

    private static int waitTime = 60 * 1000;

    static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static HMaster MASTER;

    private static Admin ADMIN;

    @Test
    public void testWholesomeMerge() throws Exception {
        TestRegionMergeTransactionOnCluster.LOG.info(("Starting " + (name.getMethodName())));
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table and load data.
        Table table = createTableAndLoadData(TestRegionMergeTransactionOnCluster.MASTER, tableName);
        // Merge 1st and 2nd region
        mergeRegionsAndVerifyRegionNum(TestRegionMergeTransactionOnCluster.MASTER, tableName, 0, 1, ((TestRegionMergeTransactionOnCluster.INITIAL_REGION_NUM) - 1));
        // Merge 2nd and 3th region
        PairOfSameType<RegionInfo> mergedRegions = mergeRegionsAndVerifyRegionNum(TestRegionMergeTransactionOnCluster.MASTER, tableName, 1, 2, ((TestRegionMergeTransactionOnCluster.INITIAL_REGION_NUM) - 2));
        verifyRowCount(table, TestRegionMergeTransactionOnCluster.ROWSIZE);
        // Randomly choose one of the two merged regions
        RegionInfo hri = (RandomUtils.nextBoolean()) ? mergedRegions.getFirst() : mergedRegions.getSecond();
        MiniHBaseCluster cluster = TestRegionMergeTransactionOnCluster.TEST_UTIL.getHBaseCluster();
        AssignmentManager am = cluster.getMaster().getAssignmentManager();
        RegionStates regionStates = am.getRegionStates();
        // We should not be able to assign it again
        am.assign(hri);
        Assert.assertFalse("Merged region can't be assigned", regionStates.isRegionInTransition(hri));
        // We should not be able to unassign it either
        am.unassign(hri);
        Assert.assertFalse("Merged region can't be unassigned", regionStates.isRegionInTransition(hri));
        table.close();
    }

    /**
     * Not really restarting the master. Simulate it by clear of new region
     * state since it is not persisted, will be lost after master restarts.
     */
    @Test
    public void testMergeAndRestartingMaster() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table and load data.
        Table table = createTableAndLoadData(TestRegionMergeTransactionOnCluster.MASTER, tableName);
        try {
            TestRegionMergeTransactionOnCluster.MyMasterRpcServices.enabled.set(true);
            // Merge 1st and 2nd region
            mergeRegionsAndVerifyRegionNum(TestRegionMergeTransactionOnCluster.MASTER, tableName, 0, 1, ((TestRegionMergeTransactionOnCluster.INITIAL_REGION_NUM) - 1));
        } finally {
            TestRegionMergeTransactionOnCluster.MyMasterRpcServices.enabled.set(false);
        }
        table.close();
    }

    @Test
    public void testCleanMergeReference() throws Exception {
        TestRegionMergeTransactionOnCluster.LOG.info(("Starting " + (name.getMethodName())));
        TestRegionMergeTransactionOnCluster.ADMIN.enableCatalogJanitor(false);
        try {
            final TableName tableName = TableName.valueOf(name.getMethodName());
            // Create table and load data.
            Table table = createTableAndLoadData(TestRegionMergeTransactionOnCluster.MASTER, tableName);
            // Merge 1st and 2nd region
            mergeRegionsAndVerifyRegionNum(TestRegionMergeTransactionOnCluster.MASTER, tableName, 0, 1, ((TestRegionMergeTransactionOnCluster.INITIAL_REGION_NUM) - 1));
            verifyRowCount(table, TestRegionMergeTransactionOnCluster.ROWSIZE);
            table.close();
            List<Pair<RegionInfo, ServerName>> tableRegions = MetaTableAccessor.getTableRegionsAndLocations(TestRegionMergeTransactionOnCluster.MASTER.getConnection(), tableName);
            RegionInfo mergedRegionInfo = tableRegions.get(0).getFirst();
            TableDescriptor tableDescriptor = TestRegionMergeTransactionOnCluster.MASTER.getTableDescriptors().get(tableName);
            Result mergedRegionResult = MetaTableAccessor.getRegionResult(TestRegionMergeTransactionOnCluster.MASTER.getConnection(), mergedRegionInfo.getRegionName());
            // contains merge reference in META
            Assert.assertTrue(((mergedRegionResult.getValue(CATALOG_FAMILY, MERGEA_QUALIFIER)) != null));
            Assert.assertTrue(((mergedRegionResult.getValue(CATALOG_FAMILY, MERGEB_QUALIFIER)) != null));
            // merging regions' directory are in the file system all the same
            PairOfSameType<RegionInfo> p = MetaTableAccessor.getMergeRegions(mergedRegionResult);
            RegionInfo regionA = p.getFirst();
            RegionInfo regionB = p.getSecond();
            FileSystem fs = TestRegionMergeTransactionOnCluster.MASTER.getMasterFileSystem().getFileSystem();
            Path rootDir = TestRegionMergeTransactionOnCluster.MASTER.getMasterFileSystem().getRootDir();
            Path tabledir = FSUtils.getTableDir(rootDir, mergedRegionInfo.getTable());
            Path regionAdir = new Path(tabledir, regionA.getEncodedName());
            Path regionBdir = new Path(tabledir, regionB.getEncodedName());
            Assert.assertTrue(fs.exists(regionAdir));
            Assert.assertTrue(fs.exists(regionBdir));
            ColumnFamilyDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            HRegionFileSystem hrfs = new HRegionFileSystem(TestRegionMergeTransactionOnCluster.TEST_UTIL.getConfiguration(), fs, tabledir, mergedRegionInfo);
            int count = 0;
            for (ColumnFamilyDescriptor colFamily : columnFamilies) {
                count += hrfs.getStoreFiles(colFamily.getName()).size();
            }
            TestRegionMergeTransactionOnCluster.ADMIN.compactRegion(mergedRegionInfo.getRegionName());
            // clean up the merged region store files
            // wait until merged region have reference file
            long timeout = (System.currentTimeMillis()) + (TestRegionMergeTransactionOnCluster.waitTime);
            int newcount = 0;
            while ((System.currentTimeMillis()) < timeout) {
                for (ColumnFamilyDescriptor colFamily : columnFamilies) {
                    newcount += hrfs.getStoreFiles(colFamily.getName()).size();
                }
                if (newcount > count) {
                    break;
                }
                Thread.sleep(50);
            } 
            Assert.assertTrue((newcount > count));
            List<RegionServerThread> regionServerThreads = TestRegionMergeTransactionOnCluster.TEST_UTIL.getHBaseCluster().getRegionServerThreads();
            for (RegionServerThread rs : regionServerThreads) {
                CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(100, null, rs.getRegionServer(), false);
                cleaner.chore();
                Thread.sleep(1000);
            }
            while ((System.currentTimeMillis()) < timeout) {
                int newcount1 = 0;
                for (ColumnFamilyDescriptor colFamily : columnFamilies) {
                    newcount1 += hrfs.getStoreFiles(colFamily.getName()).size();
                }
                if (newcount1 <= 1) {
                    break;
                }
                Thread.sleep(50);
            } 
            // run CatalogJanitor to clean merge references in hbase:meta and archive the
            // files of merging regions
            int cleaned = 0;
            while (cleaned == 0) {
                cleaned = TestRegionMergeTransactionOnCluster.ADMIN.runCatalogScan();
                TestRegionMergeTransactionOnCluster.LOG.debug(("catalog janitor returned " + cleaned));
                Thread.sleep(50);
                // Cleanup is async so wait till all procedures are done running.
                ProcedureTestingUtility.waitNoProcedureRunning(TestRegionMergeTransactionOnCluster.TEST_UTIL.getMiniHBaseCluster().getMaster().getMasterProcedureExecutor());
            } 
            Assert.assertFalse(regionAdir.toString(), fs.exists(regionAdir));
            Assert.assertFalse(regionBdir.toString(), fs.exists(regionBdir));
            Assert.assertTrue((cleaned > 0));
            mergedRegionResult = MetaTableAccessor.getRegionResult(TestRegionMergeTransactionOnCluster.TEST_UTIL.getConnection(), mergedRegionInfo.getRegionName());
            Assert.assertFalse(((mergedRegionResult.getValue(CATALOG_FAMILY, MERGEA_QUALIFIER)) != null));
            Assert.assertFalse(((mergedRegionResult.getValue(CATALOG_FAMILY, MERGEB_QUALIFIER)) != null));
        } finally {
            TestRegionMergeTransactionOnCluster.ADMIN.enableCatalogJanitor(true);
        }
    }

    /**
     * This test tests 1, merging region not online;
     * 2, merging same two regions; 3, merging unknown regions.
     * They are in one test case so that we don't have to create
     * many tables, and these tests are simple.
     */
    @Test
    public void testMerge() throws Exception {
        TestRegionMergeTransactionOnCluster.LOG.info(("Starting " + (name.getMethodName())));
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final Admin admin = TestRegionMergeTransactionOnCluster.TEST_UTIL.getAdmin();
        final int syncWaitTimeout = 10 * 60000;// 10min

        try {
            // Create table and load data.
            Table table = createTableAndLoadData(TestRegionMergeTransactionOnCluster.MASTER, tableName);
            AssignmentManager am = TestRegionMergeTransactionOnCluster.MASTER.getAssignmentManager();
            List<RegionInfo> regions = am.getRegionStates().getRegionsOfTable(tableName);
            // Fake offline one region
            RegionInfo a = regions.get(0);
            RegionInfo b = regions.get(1);
            am.unassign(b);
            am.offlineRegion(b);
            try {
                // Merge offline region. Region a is offline here
                admin.mergeRegionsAsync(a.getEncodedNameAsBytes(), b.getEncodedNameAsBytes(), false).get(syncWaitTimeout, TimeUnit.MILLISECONDS);
                Assert.fail("Offline regions should not be able to merge");
            } catch (DoNotRetryRegionException ie) {
                System.out.println(ie);
                Assert.assertTrue((ie instanceof MergeRegionException));
            }
            try {
                // Merge the same region: b and b.
                admin.mergeRegionsAsync(b.getEncodedNameAsBytes(), b.getEncodedNameAsBytes(), true);
                Assert.fail("A region should not be able to merge with itself, even forcifully");
            } catch (IOException ie) {
                Assert.assertTrue("Exception should mention regions not online", ((StringUtils.stringifyException(ie).contains("region to itself")) && (ie instanceof MergeRegionException)));
            }
            try {
                // Merge unknown regions
                admin.mergeRegionsAsync(Bytes.toBytes("-f1"), Bytes.toBytes("-f2"), true);
                Assert.fail("Unknown region could not be merged");
            } catch (IOException ie) {
                Assert.assertTrue("UnknownRegionException should be thrown", (ie instanceof UnknownRegionException));
            }
            table.close();
        } finally {
            TestRegionMergeTransactionOnCluster.TEST_UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testMergeWithReplicas() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table and load data.
        createTableAndLoadData(TestRegionMergeTransactionOnCluster.MASTER, tableName, 5, 2);
        List<Pair<RegionInfo, ServerName>> initialRegionToServers = MetaTableAccessor.getTableRegionsAndLocations(TestRegionMergeTransactionOnCluster.TEST_UTIL.getConnection(), tableName);
        // Merge 1st and 2nd region
        PairOfSameType<RegionInfo> mergedRegions = mergeRegionsAndVerifyRegionNum(TestRegionMergeTransactionOnCluster.MASTER, tableName, 0, 2, ((5 * 2) - 2));
        List<Pair<RegionInfo, ServerName>> currentRegionToServers = MetaTableAccessor.getTableRegionsAndLocations(TestRegionMergeTransactionOnCluster.TEST_UTIL.getConnection(), tableName);
        List<RegionInfo> initialRegions = new ArrayList<>();
        for (Pair<RegionInfo, ServerName> p : initialRegionToServers) {
            initialRegions.add(p.getFirst());
        }
        List<RegionInfo> currentRegions = new ArrayList<>();
        for (Pair<RegionInfo, ServerName> p : currentRegionToServers) {
            currentRegions.add(p.getFirst());
        }
        Assert.assertTrue(initialRegions.contains(mergedRegions.getFirst()));// this is the first region

        Assert.assertTrue(initialRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(mergedRegions.getFirst(), 1)));// this is the replica of the first region

        Assert.assertTrue(initialRegions.contains(mergedRegions.getSecond()));// this is the second region

        Assert.assertTrue(initialRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(mergedRegions.getSecond(), 1)));// this is the replica of the second region

        Assert.assertTrue((!(initialRegions.contains(currentRegions.get(0)))));// this is the new region

        Assert.assertTrue((!(initialRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(currentRegions.get(0), 1)))));// replica of the new region

        Assert.assertTrue(currentRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(currentRegions.get(0), 1)));// replica of the new region

        Assert.assertTrue((!(currentRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(mergedRegions.getFirst(), 1)))));// replica of the merged region

        Assert.assertTrue((!(currentRegions.contains(RegionReplicaUtil.getRegionInfoForReplica(mergedRegions.getSecond(), 1)))));// replica of the merged region

    }

    // Make it public so that JVMClusterUtil can access it.
    public static class MyMaster extends HMaster {
        public MyMaster(Configuration conf) throws IOException, InterruptedException, KeeperException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestRegionMergeTransactionOnCluster.MyMasterRpcServices(this);
        }
    }

    static class MyMasterRpcServices extends MasterRpcServices {
        static AtomicBoolean enabled = new AtomicBoolean(false);

        private HMaster myMaster;

        public MyMasterRpcServices(HMaster master) throws IOException {
            super(master);
            myMaster = master;
        }

        @Override
        public ReportRegionStateTransitionResponse reportRegionStateTransition(RpcController c, ReportRegionStateTransitionRequest req) throws ServiceException {
            ReportRegionStateTransitionResponse resp = super.reportRegionStateTransition(c, req);
            if (((TestRegionMergeTransactionOnCluster.MyMasterRpcServices.enabled.get()) && ((req.getTransition(0).getTransitionCode()) == (TransitionCode.READY_TO_MERGE))) && (!(resp.hasErrorMessage()))) {
                RegionStates regionStates = myMaster.getAssignmentManager().getRegionStates();
                for (RegionState regionState : regionStates.getRegionsStateInTransition()) {
                    // Find the merging_new region and remove it
                    if (regionState.isMergingNew()) {
                        regionStates.deleteRegion(regionState.getRegion());
                    }
                }
            }
            return resp;
        }
    }
}

