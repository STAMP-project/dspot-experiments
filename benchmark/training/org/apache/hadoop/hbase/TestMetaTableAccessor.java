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
package org.apache.hadoop.hbase;


import HConstants.CATALOG_FAMILY;
import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import HConstants.SEQNUM_QUALIFIER;
import HConstants.SERVER_QUALIFIER;
import HConstants.STARTCODE_QUALIFIER;
import MetaTableAccessor.Visitor;
import RSRpcServices.REGION_SERVER_RPC_SCHEDULER_FACTORY_CLASS;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.ipc.CallRunner;
import org.apache.hadoop.hbase.ipc.DelegatingRpcScheduler;
import org.apache.hadoop.hbase.ipc.PriorityFunction;
import org.apache.hadoop.hbase.ipc.RpcScheduler;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.SimpleRpcSchedulerFactory;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.ManualEnvironmentEdge;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.zookeeper.MetaTableLocator;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HConstants.QOS_THRESHOLD;
import static HConstants.SEQNUM_QUALIFIER_STR;
import static HConstants.SERVER_QUALIFIER_STR;
import static HConstants.STARTCODE_QUALIFIER_STR;
import static MetaTableAccessor.META_REPLICA_ID_DELIMITER;


/**
 * Test {@link org.apache.hadoop.hbase.MetaTableAccessor}.
 */
@Category({ MiscTests.class, MediumTests.class })
@SuppressWarnings("deprecation")
public class TestMetaTableAccessor {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMetaTableAccessor.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMetaTableAccessor.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static Connection connection;

    private Random random = new Random();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testIsMetaWhenAllHealthy() throws InterruptedException {
        HMaster m = TestMetaTableAccessor.UTIL.getMiniHBaseCluster().getMaster();
        Assert.assertTrue(m.waitForMetaOnline());
    }

    @Test
    public void testIsMetaWhenMetaGoesOffline() throws InterruptedException {
        HMaster m = TestMetaTableAccessor.UTIL.getMiniHBaseCluster().getMaster();
        int index = TestMetaTableAccessor.UTIL.getMiniHBaseCluster().getServerWithMeta();
        HRegionServer rsWithMeta = TestMetaTableAccessor.UTIL.getMiniHBaseCluster().getRegionServer(index);
        rsWithMeta.abort("TESTING");
        Assert.assertTrue(m.waitForMetaOnline());
    }

    /**
     * Does {@link MetaTableAccessor#getRegion(Connection, byte[])} and a write
     * against hbase:meta while its hosted server is restarted to prove our retrying
     * works.
     */
    @Test
    public void testRetrying() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestMetaTableAccessor.LOG.info(("Started " + tableName));
        Table t = TestMetaTableAccessor.UTIL.createMultiRegionTable(tableName, CATALOG_FAMILY);
        int regionCount = -1;
        try (RegionLocator r = TestMetaTableAccessor.UTIL.getConnection().getRegionLocator(tableName)) {
            regionCount = r.getStartKeys().length;
        }
        // Test it works getting a region from just made user table.
        final List<RegionInfo> regions = TestMetaTableAccessor.testGettingTableRegions(TestMetaTableAccessor.connection, tableName, regionCount);
        TestMetaTableAccessor.MetaTask reader = new TestMetaTableAccessor.MetaTask(TestMetaTableAccessor.connection, "reader") {
            @Override
            void metaTask() throws Throwable {
                TestMetaTableAccessor.testGetRegion(connection, regions.get(0));
                TestMetaTableAccessor.LOG.info(("Read " + (regions.get(0).getEncodedName())));
            }
        };
        TestMetaTableAccessor.MetaTask writer = new TestMetaTableAccessor.MetaTask(TestMetaTableAccessor.connection, "writer") {
            @Override
            void metaTask() throws Throwable {
                MetaTableAccessor.addRegionToMeta(connection, regions.get(0));
                TestMetaTableAccessor.LOG.info(("Wrote " + (regions.get(0).getEncodedName())));
            }
        };
        reader.start();
        writer.start();
        // We're gonna check how it takes. If it takes too long, we will consider
        // it as a fail. We can't put that in the @Test tag as we want to close
        // the threads nicely
        final long timeOut = 180000;
        long startTime = System.currentTimeMillis();
        try {
            // Make sure reader and writer are working.
            Assert.assertTrue(reader.isProgressing());
            Assert.assertTrue(writer.isProgressing());
            // Kill server hosting meta -- twice  . See if our reader/writer ride over the
            // meta moves.  They'll need to retry.
            for (int i = 0; i < 2; i++) {
                TestMetaTableAccessor.LOG.info(("Restart=" + i));
                TestMetaTableAccessor.UTIL.ensureSomeRegionServersAvailable(2);
                int index = -1;
                do {
                    index = TestMetaTableAccessor.UTIL.getMiniHBaseCluster().getServerWithMeta();
                } while ((index == (-1)) && ((startTime + timeOut) < (System.currentTimeMillis())) );
                if (index != (-1)) {
                    TestMetaTableAccessor.UTIL.getMiniHBaseCluster().abortRegionServer(index);
                    TestMetaTableAccessor.UTIL.getMiniHBaseCluster().waitOnRegionServer(index);
                }
            }
            Assert.assertTrue(("reader: " + (reader.toString())), reader.isProgressing());
            Assert.assertTrue(("writer: " + (writer.toString())), writer.isProgressing());
        } catch (IOException e) {
            throw e;
        } finally {
            reader.stop = true;
            writer.stop = true;
            reader.join();
            writer.join();
            t.close();
        }
        long exeTime = (System.currentTimeMillis()) - startTime;
        Assert.assertTrue((("Timeout: test took " + (exeTime / 1000)) + " sec"), (exeTime < timeOut));
    }

    /**
     * Thread that runs a MetaTableAccessor task until asked stop.
     */
    abstract static class MetaTask extends Thread {
        boolean stop = false;

        int count = 0;

        Throwable t = null;

        final Connection connection;

        MetaTask(final Connection connection, final String name) {
            super(name);
            this.connection = connection;
        }

        @Override
        public void run() {
            try {
                while (!(this.stop)) {
                    TestMetaTableAccessor.LOG.info(((("Before " + (this.getName())) + ", count=") + (this.count)));
                    metaTask();
                    this.count += 1;
                    TestMetaTableAccessor.LOG.info(((("After " + (this.getName())) + ", count=") + (this.count)));
                    Thread.sleep(100);
                } 
            } catch (Throwable t) {
                TestMetaTableAccessor.LOG.info(((this.getName()) + " failed"), t);
                this.t = t;
            }
        }

        boolean isProgressing() throws InterruptedException {
            int currentCount = this.count;
            while (currentCount == (this.count)) {
                if (!(isAlive()))
                    return false;

                if ((this.t) != null)
                    return false;

                Thread.sleep(10);
            } 
            return true;
        }

        @Override
        public String toString() {
            return (("count=" + (this.count)) + ", t=") + ((this.t) == null ? "null" : this.t.toString());
        }

        abstract void metaTask() throws Throwable;
    }

    @Test
    public void testGetRegionsFromMetaTable() throws IOException, InterruptedException {
        List<RegionInfo> regions = MetaTableLocator.getMetaRegions(getZooKeeperWatcher());
        Assert.assertTrue(((regions.size()) >= 1));
        Assert.assertTrue(((MetaTableLocator.getMetaRegionsAndLocations(getZooKeeperWatcher()).size()) >= 1));
    }

    @Test
    public void testTableExists() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Assert.assertFalse(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, tableName));
        TestMetaTableAccessor.UTIL.createTable(tableName, CATALOG_FAMILY);
        Assert.assertTrue(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, tableName));
        Admin admin = TestMetaTableAccessor.UTIL.getAdmin();
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        Assert.assertFalse(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, tableName));
        Assert.assertTrue(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, META_TABLE_NAME));
        TestMetaTableAccessor.UTIL.createTable(tableName, CATALOG_FAMILY);
        Assert.assertTrue(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, tableName));
        admin.disableTable(tableName);
        admin.deleteTable(tableName);
        Assert.assertFalse(MetaTableAccessor.tableExists(TestMetaTableAccessor.connection, tableName));
    }

    @Test
    public void testGetRegion() throws IOException, InterruptedException {
        final String name = this.name.getMethodName();
        TestMetaTableAccessor.LOG.info(("Started " + name));
        // Test get on non-existent region.
        Pair<RegionInfo, ServerName> pair = MetaTableAccessor.getRegion(TestMetaTableAccessor.connection, Bytes.toBytes("nonexistent-region"));
        Assert.assertNull(pair);
        TestMetaTableAccessor.LOG.info(("Finished " + name));
    }

    // Test for the optimization made in HBASE-3650
    @Test
    public void testScanMetaForTable() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestMetaTableAccessor.LOG.info(("Started " + tableName));
        /**
         * Create 2 tables
         * - testScanMetaForTable
         * - testScanMetaForTablf
         */
        TestMetaTableAccessor.UTIL.createTable(tableName, CATALOG_FAMILY);
        // name that is +1 greater than the first one (e+1=f)
        TableName greaterName = TableName.valueOf("testScanMetaForTablf");
        TestMetaTableAccessor.UTIL.createTable(greaterName, CATALOG_FAMILY);
        // Now make sure we only get the regions from 1 of the tables at a time
        Assert.assertEquals(1, MetaTableAccessor.getTableRegions(TestMetaTableAccessor.connection, tableName).size());
        Assert.assertEquals(1, MetaTableAccessor.getTableRegions(TestMetaTableAccessor.connection, greaterName).size());
    }

    @Test
    public void testParseReplicaIdFromServerColumn() {
        String column1 = SERVER_QUALIFIER_STR;
        Assert.assertEquals(0, MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column1)));
        String column2 = column1 + (META_REPLICA_ID_DELIMITER);
        Assert.assertEquals((-1), MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column2)));
        String column3 = column2 + "00";
        Assert.assertEquals((-1), MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column3)));
        String column4 = column3 + "2A";
        Assert.assertEquals(42, MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column4)));
        String column5 = column4 + "2A";
        Assert.assertEquals((-1), MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column5)));
        String column6 = STARTCODE_QUALIFIER_STR;
        Assert.assertEquals((-1), MetaTableAccessor.parseReplicaIdFromServerColumn(Bytes.toBytes(column6)));
    }

    @Test
    public void testMetaReaderGetColumnMethods() {
        Assert.assertArrayEquals(SERVER_QUALIFIER, MetaTableAccessor.getServerColumn(0));
        Assert.assertArrayEquals(Bytes.toBytes((((SERVER_QUALIFIER_STR) + (META_REPLICA_ID_DELIMITER)) + "002A")), MetaTableAccessor.getServerColumn(42));
        Assert.assertArrayEquals(STARTCODE_QUALIFIER, MetaTableAccessor.getStartCodeColumn(0));
        Assert.assertArrayEquals(Bytes.toBytes((((STARTCODE_QUALIFIER_STR) + (META_REPLICA_ID_DELIMITER)) + "002A")), MetaTableAccessor.getStartCodeColumn(42));
        Assert.assertArrayEquals(SEQNUM_QUALIFIER, MetaTableAccessor.getSeqNumColumn(0));
        Assert.assertArrayEquals(Bytes.toBytes((((SEQNUM_QUALIFIER_STR) + (META_REPLICA_ID_DELIMITER)) + "002A")), MetaTableAccessor.getSeqNumColumn(42));
    }

    @Test
    public void testMetaLocationsForRegionReplicas() throws IOException {
        ServerName serverName0 = ServerName.valueOf("foo", 60010, random.nextLong());
        ServerName serverName1 = ServerName.valueOf("bar", 60010, random.nextLong());
        ServerName serverName100 = ServerName.valueOf("baz", 60010, random.nextLong());
        long regionId = System.currentTimeMillis();
        RegionInfo primary = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo replica1 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(1).build();
        RegionInfo replica100 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(100).build();
        long seqNum0 = random.nextLong();
        long seqNum1 = random.nextLong();
        long seqNum100 = random.nextLong();
        try (Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection)) {
            MetaTableAccessor.updateRegionLocation(TestMetaTableAccessor.connection, primary, serverName0, seqNum0, EnvironmentEdgeManager.currentTime());
            // assert that the server, startcode and seqNum columns are there for the primary region
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName0, seqNum0, 0, true);
            // add replica = 1
            MetaTableAccessor.updateRegionLocation(TestMetaTableAccessor.connection, replica1, serverName1, seqNum1, EnvironmentEdgeManager.currentTime());
            // check whether the primary is still there
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName0, seqNum0, 0, true);
            // now check for replica 1
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName1, seqNum1, 1, true);
            // add replica = 1
            MetaTableAccessor.updateRegionLocation(TestMetaTableAccessor.connection, replica100, serverName100, seqNum100, EnvironmentEdgeManager.currentTime());
            // check whether the primary is still there
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName0, seqNum0, 0, true);
            // check whether the replica 1 is still there
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName1, seqNum1, 1, true);
            // now check for replica 1
            TestMetaTableAccessor.assertMetaLocation(meta, primary.getRegionName(), serverName100, seqNum100, 100, true);
        }
    }

    @Test
    public void testMetaLocationForRegionReplicasIsRemovedAtTableDeletion() throws IOException {
        long regionId = System.currentTimeMillis();
        RegionInfo primary = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection);
        try {
            List<RegionInfo> regionInfos = Lists.newArrayList(primary);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 3);
            MetaTableAccessor.removeRegionReplicasFromMeta(Sets.newHashSet(primary.getRegionName()), 1, 2, TestMetaTableAccessor.connection);
            Get get = new Get(primary.getRegionName());
            Result result = meta.get(get);
            for (int replicaId = 0; replicaId < 3; replicaId++) {
                Cell serverCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(replicaId));
                Cell startCodeCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getStartCodeColumn(replicaId));
                Cell stateCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getRegionStateColumn(replicaId));
                Cell snCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerNameColumn(replicaId));
                if (replicaId == 0) {
                    Assert.assertNotNull(stateCell);
                } else {
                    Assert.assertNull(serverCell);
                    Assert.assertNull(startCodeCell);
                    Assert.assertNull(stateCell);
                    Assert.assertNull(snCell);
                }
            }
        } finally {
            meta.close();
        }
    }

    @Test
    public void testMetaLocationForRegionReplicasIsAddedAtTableCreation() throws IOException {
        long regionId = System.currentTimeMillis();
        RegionInfo primary = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection);
        try {
            List<RegionInfo> regionInfos = Lists.newArrayList(primary);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 3);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, primary.getRegionName(), 1);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, primary.getRegionName(), 2);
        } finally {
            meta.close();
        }
    }

    @Test
    public void testMetaLocationForRegionReplicasIsAddedAtRegionSplit() throws IOException {
        long regionId = System.currentTimeMillis();
        ServerName serverName0 = ServerName.valueOf("foo", 60010, random.nextLong());
        RegionInfo parent = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo splitA = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(Bytes.toBytes("a")).setSplit(false).setRegionId((regionId + 1)).setReplicaId(0).build();
        RegionInfo splitB = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("a")).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId((regionId + 1)).setReplicaId(0).build();
        try (Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection)) {
            List<RegionInfo> regionInfos = Lists.newArrayList(parent);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 3);
            MetaTableAccessor.splitRegion(TestMetaTableAccessor.connection, parent, (-1L), splitA, splitB, serverName0, 3);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, splitA.getRegionName(), 1);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, splitA.getRegionName(), 2);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, splitB.getRegionName(), 1);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, splitB.getRegionName(), 2);
        }
    }

    @Test
    public void testMetaLocationForRegionReplicasIsAddedAtRegionMerge() throws IOException {
        long regionId = System.currentTimeMillis();
        ServerName serverName0 = ServerName.valueOf("foo", 60010, random.nextLong());
        RegionInfo parentA = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("a")).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo parentB = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(Bytes.toBytes("a")).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo merged = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId((regionId + 1)).setReplicaId(0).build();
        try (Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection)) {
            List<RegionInfo> regionInfos = Lists.newArrayList(parentA, parentB);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 3);
            MetaTableAccessor.mergeRegions(TestMetaTableAccessor.connection, merged, parentA, (-1L), parentB, (-1L), serverName0, 3);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, merged.getRegionName(), 1);
            TestMetaTableAccessor.assertEmptyMetaLocation(meta, merged.getRegionName(), 2);
        }
    }

    @Test
    public void testMetaScanner() throws Exception {
        TestMetaTableAccessor.LOG.info(("Starting " + (name.getMethodName())));
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] FAMILY = Bytes.toBytes("family");
        final byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("region_a"), Bytes.toBytes("region_b") };
        TestMetaTableAccessor.UTIL.createTable(tableName, FAMILY, SPLIT_KEYS);
        Table table = TestMetaTableAccessor.connection.getTable(tableName);
        // Make sure all the regions are deployed
        TestMetaTableAccessor.UTIL.countRows(table);
        MetaTableAccessor.Visitor visitor = Mockito.mock(Visitor.class);
        Mockito.doReturn(true).when(visitor).visit(((Result) (ArgumentMatchers.anyObject())));
        // Scanning the entire table should give us three rows
        MetaTableAccessor.scanMetaForTableRegions(TestMetaTableAccessor.connection, visitor, tableName);
        Mockito.verify(visitor, Mockito.times(3)).visit(((Result) (ArgumentMatchers.anyObject())));
        // Scanning the table with a specified empty start row should also
        // give us three hbase:meta rows
        Mockito.reset(visitor);
        Mockito.doReturn(true).when(visitor).visit(((Result) (ArgumentMatchers.anyObject())));
        MetaTableAccessor.scanMeta(TestMetaTableAccessor.connection, visitor, tableName, null, 1000);
        Mockito.verify(visitor, Mockito.times(3)).visit(((Result) (ArgumentMatchers.anyObject())));
        // Scanning the table starting in the middle should give us two rows:
        // region_a and region_b
        Mockito.reset(visitor);
        Mockito.doReturn(true).when(visitor).visit(((Result) (ArgumentMatchers.anyObject())));
        MetaTableAccessor.scanMeta(TestMetaTableAccessor.connection, visitor, tableName, Bytes.toBytes("region_ac"), 1000);
        Mockito.verify(visitor, Mockito.times(2)).visit(((Result) (ArgumentMatchers.anyObject())));
        // Scanning with a limit of 1 should only give us one row
        Mockito.reset(visitor);
        Mockito.doReturn(true).when(visitor).visit(((Result) (ArgumentMatchers.anyObject())));
        MetaTableAccessor.scanMeta(TestMetaTableAccessor.connection, visitor, tableName, Bytes.toBytes("region_ac"), 1);
        Mockito.verify(visitor, Mockito.times(1)).visit(((Result) (ArgumentMatchers.anyObject())));
        table.close();
    }

    /**
     * Tests whether maximum of masters system time versus RSs local system time is used
     */
    @Test
    public void testMastersSystemTimeIsUsedInUpdateLocations() throws IOException {
        long regionId = System.currentTimeMillis();
        RegionInfo regionInfo = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        ServerName sn = ServerName.valueOf("bar", 0, 0);
        try (Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection)) {
            List<RegionInfo> regionInfos = Lists.newArrayList(regionInfo);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 1);
            long masterSystemTime = (EnvironmentEdgeManager.currentTime()) + 123456789;
            MetaTableAccessor.updateRegionLocation(TestMetaTableAccessor.connection, regionInfo, sn, 1, masterSystemTime);
            Get get = new Get(regionInfo.getRegionName());
            Result result = meta.get(get);
            Cell serverCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(0));
            Cell startCodeCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getStartCodeColumn(0));
            Cell seqNumCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getSeqNumColumn(0));
            Assert.assertNotNull(serverCell);
            Assert.assertNotNull(startCodeCell);
            Assert.assertNotNull(seqNumCell);
            Assert.assertTrue(((serverCell.getValueLength()) > 0));
            Assert.assertTrue(((startCodeCell.getValueLength()) > 0));
            Assert.assertTrue(((seqNumCell.getValueLength()) > 0));
            Assert.assertEquals(masterSystemTime, serverCell.getTimestamp());
            Assert.assertEquals(masterSystemTime, startCodeCell.getTimestamp());
            Assert.assertEquals(masterSystemTime, seqNumCell.getTimestamp());
        }
    }

    @Test
    public void testMastersSystemTimeIsUsedInMergeRegions() throws IOException {
        long regionId = System.currentTimeMillis();
        RegionInfo regionInfoA = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(new byte[]{ 'a' }).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo regionInfoB = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(new byte[]{ 'a' }).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo mergedRegionInfo = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        ServerName sn = ServerName.valueOf("bar", 0, 0);
        try (Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection)) {
            List<RegionInfo> regionInfos = Lists.newArrayList(regionInfoA, regionInfoB);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 1);
            // write the serverName column with a big current time, but set the masters time as even
            // bigger. When region merge deletes the rows for regionA and regionB, the serverName columns
            // should not be seen by the following get
            long serverNameTime = (EnvironmentEdgeManager.currentTime()) + 100000000;
            long masterSystemTime = (EnvironmentEdgeManager.currentTime()) + 123456789;
            // write the serverName columns
            MetaTableAccessor.updateRegionLocation(TestMetaTableAccessor.connection, regionInfoA, sn, 1, serverNameTime);
            // assert that we have the serverName column with expected ts
            Get get = new Get(mergedRegionInfo.getRegionName());
            Result result = meta.get(get);
            Cell serverCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(0));
            Assert.assertNotNull(serverCell);
            Assert.assertEquals(serverNameTime, serverCell.getTimestamp());
            ManualEnvironmentEdge edge = new ManualEnvironmentEdge();
            edge.setValue(masterSystemTime);
            EnvironmentEdgeManager.injectEdge(edge);
            try {
                // now merge the regions, effectively deleting the rows for region a and b.
                MetaTableAccessor.mergeRegions(TestMetaTableAccessor.connection, mergedRegionInfo, regionInfoA, (-1L), regionInfoB, (-1L), sn, 1);
            } finally {
                EnvironmentEdgeManager.reset();
            }
            result = meta.get(get);
            serverCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(0));
            Cell startCodeCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getStartCodeColumn(0));
            Cell seqNumCell = result.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getSeqNumColumn(0));
            Assert.assertNull(serverCell);
            Assert.assertNull(startCodeCell);
            Assert.assertNull(seqNumCell);
        }
    }

    public static class SpyingRpcSchedulerFactory extends SimpleRpcSchedulerFactory {
        @Override
        public RpcScheduler create(Configuration conf, PriorityFunction priority, Abortable server) {
            final RpcScheduler delegate = super.create(conf, priority, server);
            return new TestMetaTableAccessor.SpyingRpcScheduler(delegate);
        }
    }

    public static class SpyingRpcScheduler extends DelegatingRpcScheduler {
        long numPriorityCalls = 0;

        public SpyingRpcScheduler(RpcScheduler delegate) {
            super(delegate);
        }

        @Override
        public boolean dispatch(CallRunner task) throws IOException, InterruptedException {
            int priority = task.getRpcCall().getPriority();
            if (priority > (QOS_THRESHOLD)) {
                (numPriorityCalls)++;
            }
            return super.dispatch(task);
        }
    }

    @Test
    public void testMetaUpdatesGoToPriorityQueue() throws Exception {
        // This test has to be end-to-end, and do the verification from the server side
        Configuration c = TestMetaTableAccessor.UTIL.getConfiguration();
        c.set(REGION_SERVER_RPC_SCHEDULER_FACTORY_CLASS, TestMetaTableAccessor.SpyingRpcSchedulerFactory.class.getName());
        // restart so that new config takes place
        TestMetaTableAccessor.afterClass();
        TestMetaTableAccessor.beforeClass();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Admin admin = TestMetaTableAccessor.connection.getAdmin();RegionLocator rl = TestMetaTableAccessor.connection.getRegionLocator(tableName)) {
            // create a table and prepare for a manual split
            TestMetaTableAccessor.UTIL.createTable(tableName, "cf1");
            HRegionLocation loc = rl.getAllRegionLocations().get(0);
            RegionInfo parent = loc.getRegionInfo();
            long rid = 1000;
            byte[] splitKey = Bytes.toBytes("a");
            RegionInfo splitA = RegionInfoBuilder.newBuilder(parent.getTable()).setStartKey(parent.getStartKey()).setEndKey(splitKey).setSplit(false).setRegionId(rid).build();
            RegionInfo splitB = RegionInfoBuilder.newBuilder(parent.getTable()).setStartKey(splitKey).setEndKey(parent.getEndKey()).setSplit(false).setRegionId(rid).build();
            // find the meta server
            MiniHBaseCluster cluster = TestMetaTableAccessor.UTIL.getMiniHBaseCluster();
            int rsIndex = cluster.getServerWithMeta();
            HRegionServer rs;
            if (rsIndex >= 0) {
                rs = cluster.getRegionServer(rsIndex);
            } else {
                // it is in master
                rs = cluster.getMaster();
            }
            TestMetaTableAccessor.SpyingRpcScheduler scheduler = ((TestMetaTableAccessor.SpyingRpcScheduler) (rs.getRpcServer().getScheduler()));
            long prevCalls = scheduler.numPriorityCalls;
            MetaTableAccessor.splitRegion(TestMetaTableAccessor.connection, parent, (-1L), splitA, splitB, loc.getServerName(), 1);
            Assert.assertTrue((prevCalls < (scheduler.numPriorityCalls)));
        }
    }

    @Test
    public void testEmptyMetaDaughterLocationDuringSplit() throws IOException {
        long regionId = System.currentTimeMillis();
        ServerName serverName0 = ServerName.valueOf("foo", 60010, random.nextLong());
        RegionInfo parent = RegionInfoBuilder.newBuilder(TableName.valueOf("table_foo")).setStartKey(EMPTY_START_ROW).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId(regionId).setReplicaId(0).build();
        RegionInfo splitA = RegionInfoBuilder.newBuilder(TableName.valueOf("table_foo")).setStartKey(EMPTY_START_ROW).setEndKey(Bytes.toBytes("a")).setSplit(false).setRegionId((regionId + 1)).setReplicaId(0).build();
        RegionInfo splitB = RegionInfoBuilder.newBuilder(TableName.valueOf("table_foo")).setStartKey(Bytes.toBytes("a")).setEndKey(EMPTY_END_ROW).setSplit(false).setRegionId((regionId + 1)).setReplicaId(0).build();
        Table meta = MetaTableAccessor.getMetaHTable(TestMetaTableAccessor.connection);
        try {
            List<RegionInfo> regionInfos = Lists.newArrayList(parent);
            MetaTableAccessor.addRegionsToMeta(TestMetaTableAccessor.connection, regionInfos, 3);
            MetaTableAccessor.splitRegion(TestMetaTableAccessor.connection, parent, (-1L), splitA, splitB, serverName0, 3);
            Get get1 = new Get(splitA.getRegionName());
            Result resultA = meta.get(get1);
            Cell serverCellA = resultA.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(splitA.getReplicaId()));
            Cell startCodeCellA = resultA.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getStartCodeColumn(splitA.getReplicaId()));
            Assert.assertNull(serverCellA);
            Assert.assertNull(startCodeCellA);
            Get get2 = new Get(splitA.getRegionName());
            Result resultB = meta.get(get2);
            Cell serverCellB = resultB.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getServerColumn(splitB.getReplicaId()));
            Cell startCodeCellB = resultB.getColumnLatestCell(CATALOG_FAMILY, MetaTableAccessor.getStartCodeColumn(splitB.getReplicaId()));
            Assert.assertNull(serverCellB);
            Assert.assertNull(startCodeCellB);
        } finally {
            if (meta != null) {
                meta.close();
            }
        }
    }
}

