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
package org.apache.hadoop.hbase.master;


import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import RegionInfo.COMPARATOR;
import TableName.META_TABLE_NAME;
import TableState.State.ENABLED;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.PleaseHoldException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.UnknownRegionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.HBaseFsck;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.util.StringUtils;
import org.apache.hbase.thirdparty.com.google.common.base.Joiner;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMaster.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestMaster.class);

    private static final TableName TABLENAME = TableName.valueOf("TestMaster");

    private static final byte[] FAMILYNAME = Bytes.toBytes("fam");

    private static Admin admin;

    @Rule
    public TestName name = new TestName();

    @Test
    @SuppressWarnings("deprecation")
    public void testMasterOpsWhileSplitting() throws Exception {
        MiniHBaseCluster cluster = TestMaster.TEST_UTIL.getHBaseCluster();
        HMaster m = cluster.getMaster();
        try (Table ht = TestMaster.TEST_UTIL.createTable(TestMaster.TABLENAME, TestMaster.FAMILYNAME)) {
            Assert.assertTrue(m.getTableStateManager().isTableState(TestMaster.TABLENAME, ENABLED));
            TestMaster.TEST_UTIL.loadTable(ht, TestMaster.FAMILYNAME, false);
        }
        List<Pair<RegionInfo, ServerName>> tableRegions = MetaTableAccessor.getTableRegionsAndLocations(m.getConnection(), TestMaster.TABLENAME);
        TestMaster.LOG.info(("Regions after load: " + (Joiner.on(',').join(tableRegions))));
        Assert.assertEquals(1, tableRegions.size());
        Assert.assertArrayEquals(EMPTY_START_ROW, tableRegions.get(0).getFirst().getStartKey());
        Assert.assertArrayEquals(EMPTY_END_ROW, tableRegions.get(0).getFirst().getEndKey());
        // Now trigger a split and stop when the split is in progress
        TestMaster.LOG.info("Splitting table");
        TestMaster.TEST_UTIL.getAdmin().split(TestMaster.TABLENAME);
        TestMaster.LOG.info("Making sure we can call getTableRegions while opening");
        while ((tableRegions.size()) < 3) {
            tableRegions = MetaTableAccessor.getTableRegionsAndLocations(m.getConnection(), TestMaster.TABLENAME, false);
            Thread.sleep(100);
        } 
        TestMaster.LOG.info(("Regions: " + (Joiner.on(',').join(tableRegions))));
        // We have three regions because one is split-in-progress
        Assert.assertEquals(3, tableRegions.size());
        TestMaster.LOG.info("Making sure we can call getTableRegionClosest while opening");
        Pair<RegionInfo, ServerName> pair = getTableRegionForRow(m, TestMaster.TABLENAME, Bytes.toBytes("cde"));
        TestMaster.LOG.info(("Result is: " + pair));
        Pair<RegionInfo, ServerName> tableRegionFromName = MetaTableAccessor.getRegion(m.getConnection(), pair.getFirst().getRegionName());
        Assert.assertTrue(((COMPARATOR.compare(tableRegionFromName.getFirst(), pair.getFirst())) == 0));
    }

    @Test
    public void testMoveRegionWhenNotInitialized() {
        MiniHBaseCluster cluster = TestMaster.TEST_UTIL.getHBaseCluster();
        HMaster m = cluster.getMaster();
        try {
            m.setInitialized(false);// fake it, set back later

            RegionInfo meta = RegionInfoBuilder.FIRST_META_REGIONINFO;
            m.move(meta.getEncodedNameAsBytes(), null);
            Assert.fail("Region should not be moved since master is not initialized");
        } catch (IOException ioe) {
            Assert.assertTrue((ioe instanceof PleaseHoldException));
        } finally {
            m.setInitialized(true);
        }
    }

    @Test
    public void testMoveThrowsUnknownRegionException() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor("value");
        htd.addFamily(hcd);
        TestMaster.admin.createTable(htd, null);
        try {
            RegionInfo hri = RegionInfoBuilder.newBuilder(tableName).setStartKey(Bytes.toBytes("A")).setEndKey(Bytes.toBytes("Z")).build();
            TestMaster.admin.move(hri.getEncodedNameAsBytes(), null);
            Assert.fail("Region should not be moved since it is fake");
        } catch (IOException ioe) {
            Assert.assertTrue((ioe instanceof UnknownRegionException));
        } finally {
            TestMaster.TEST_UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testMoveThrowsPleaseHoldException() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HMaster master = TestMaster.TEST_UTIL.getMiniHBaseCluster().getMaster();
        HTableDescriptor htd = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor("value");
        htd.addFamily(hcd);
        TestMaster.admin.createTable(htd, null);
        try {
            List<RegionInfo> tableRegions = TestMaster.admin.getRegions(tableName);
            master.setInitialized(false);// fake it, set back later

            TestMaster.admin.move(tableRegions.get(0).getEncodedNameAsBytes(), null);
            Assert.fail("Region should not be moved since master is not initialized");
        } catch (IOException ioe) {
            Assert.assertTrue(StringUtils.stringifyException(ioe).contains("PleaseHoldException"));
        } finally {
            master.setInitialized(true);
            TestMaster.TEST_UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testFlushedSequenceIdPersistLoad() throws Exception {
        Configuration conf = TestMaster.TEST_UTIL.getConfiguration();
        int msgInterval = conf.getInt("hbase.regionserver.msginterval", 100);
        // insert some data into META
        TableName tableName = TableName.valueOf("testFlushSeqId");
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(Bytes.toBytes("cf")));
        Table table = TestMaster.TEST_UTIL.createTable(desc, null);
        // flush META region
        TestMaster.TEST_UTIL.flush(META_TABLE_NAME);
        // wait for regionserver report
        Threads.sleep((msgInterval * 2));
        // record flush seqid before cluster shutdown
        Map<byte[], Long> regionMapBefore = TestMaster.TEST_UTIL.getHBaseCluster().getMaster().getServerManager().getFlushedSequenceIdByRegion();
        // restart hbase cluster which will cause flushed sequence id persist and reload
        TestMaster.TEST_UTIL.getMiniHBaseCluster().shutdown();
        TestMaster.TEST_UTIL.restartHBaseCluster(2);
        TestMaster.TEST_UTIL.waitUntilNoRegionsInTransition();
        // check equality after reloading flushed sequence id map
        Map<byte[], Long> regionMapAfter = TestMaster.TEST_UTIL.getHBaseCluster().getMaster().getServerManager().getFlushedSequenceIdByRegion();
        Assert.assertTrue(regionMapBefore.equals(regionMapAfter));
    }

    @Test
    public void testBlockingHbkc1WithLockFile() throws IOException {
        // This is how the patch to the lock file is created inside in HBaseFsck. Too hard to use its
        // actual method without disturbing HBaseFsck... Do the below mimic instead.
        Path hbckLockPath = new Path(HBaseFsck.getTmpDir(TestMaster.TEST_UTIL.getConfiguration()), HBaseFsck.HBCK_LOCK_FILE);
        FileSystem fs = TestMaster.TEST_UTIL.getTestFileSystem();
        Assert.assertTrue(fs.exists(hbckLockPath));
        TestMaster.TEST_UTIL.getMiniHBaseCluster().killMaster(TestMaster.TEST_UTIL.getMiniHBaseCluster().getMaster().getServerName());
        Assert.assertTrue(fs.exists(hbckLockPath));
        TestMaster.TEST_UTIL.getMiniHBaseCluster().startMaster();
        waitFor(30000, () -> ((TEST_UTIL.getMiniHBaseCluster().getMaster()) != null) && (TEST_UTIL.getMiniHBaseCluster().getMaster().isInitialized()));
        Assert.assertTrue(fs.exists(hbckLockPath));
        // Start a second Master. Should be fine.
        TestMaster.TEST_UTIL.getMiniHBaseCluster().startMaster();
        Assert.assertTrue(fs.exists(hbckLockPath));
        fs.delete(hbckLockPath, true);
        Assert.assertFalse(fs.exists(hbckLockPath));
        // Kill all Masters.
        TestMaster.TEST_UTIL.getMiniHBaseCluster().getLiveMasterThreads().stream().map(( sn) -> sn.getMaster().getServerName()).forEach(( sn) -> {
            try {
                TEST_UTIL.getMiniHBaseCluster().killMaster(sn);
            } catch ( e) {
                e.printStackTrace();
            }
        });
        // Start a new one.
        TestMaster.TEST_UTIL.getMiniHBaseCluster().startMaster();
        waitFor(30000, () -> ((TEST_UTIL.getMiniHBaseCluster().getMaster()) != null) && (TEST_UTIL.getMiniHBaseCluster().getMaster().isInitialized()));
        // Assert lock gets put in place again.
        Assert.assertTrue(fs.exists(hbckLockPath));
    }
}

