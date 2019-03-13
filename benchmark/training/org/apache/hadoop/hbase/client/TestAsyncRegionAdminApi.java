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
package org.apache.hadoop.hbase.client;


import CompactType.MOB;
import CompactionState.MAJOR;
import CompactionState.MINOR;
import CompactionState.NONE;
import JVMClusterUtil.RegionServerThread;
import RegionState.State.OPEN;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.ServerManager;
import org.apache.hadoop.hbase.master.assignment.AssignmentManager;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.Threads;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Class to test asynchronous region admin operations.
 *
 * @see TestAsyncRegionAdminApi2 This test and it used to be joined it was taking longer than our
ten minute timeout so they were split.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncRegionAdminApi extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncRegionAdminApi.class);

    @Test
    public void testAssignRegionAndUnassignRegion() throws Exception {
        createTableWithDefaultConf(tableName);
        // assign region.
        HMaster master = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster();
        AssignmentManager am = master.getAssignmentManager();
        RegionInfo hri = am.getRegionStates().getRegionsOfTable(tableName).get(0);
        // assert region on server
        RegionStates regionStates = am.getRegionStates();
        ServerName serverName = regionStates.getRegionServerOfRegion(hri);
        TestAsyncAdminBase.TEST_UTIL.assertRegionOnServer(hri, serverName, 200);
        Assert.assertTrue(regionStates.getRegionState(hri).isOpened());
        // Region is assigned now. Let's assign it again.
        // Master should not abort, and region should stay assigned.
        try {
            admin.assign(hri.getRegionName()).get();
            Assert.fail("Should fail when assigning an already onlined region");
        } catch (ExecutionException e) {
            // Expected
            Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(DoNotRetryRegionException.class));
        }
        Assert.assertFalse(am.getRegionStates().getRegionStateNode(hri).isInTransition());
        Assert.assertTrue(regionStates.getRegionState(hri).isOpened());
        // unassign region
        admin.unassign(hri.getRegionName(), true).get();
        Assert.assertFalse(am.getRegionStates().getRegionStateNode(hri).isInTransition());
        Assert.assertTrue(regionStates.getRegionState(hri).isClosed());
    }

    @Test
    public void testGetRegionByStateOfTable() throws Exception {
        RegionInfo hri = createTableAndGetOneRegion(tableName);
        RegionStates regionStates = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates();
        Assert.assertTrue(regionStates.getRegionByStateOfTable(tableName).get(OPEN).stream().anyMatch(( r) -> (RegionInfo.COMPARATOR.compare(r, hri)) == 0));
        Assert.assertFalse(regionStates.getRegionByStateOfTable(TableName.valueOf("I_am_the_phantom")).get(OPEN).stream().anyMatch(( r) -> (RegionInfo.COMPARATOR.compare(r, hri)) == 0));
    }

    @Test
    public void testMoveRegion() throws Exception {
        admin.balancerSwitch(false).join();
        RegionInfo hri = createTableAndGetOneRegion(tableName);
        RawAsyncHBaseAdmin rawAdmin = ((RawAsyncHBaseAdmin) (TestAsyncAdminBase.ASYNC_CONN.getAdmin()));
        ServerName serverName = rawAdmin.getRegionLocation(hri.getRegionName()).get().getServerName();
        HMaster master = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster();
        ServerManager serverManager = master.getServerManager();
        ServerName destServerName = null;
        List<JVMClusterUtil.RegionServerThread> regionServers = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads();
        for (JVMClusterUtil.RegionServerThread regionServer : regionServers) {
            HRegionServer destServer = regionServer.getRegionServer();
            destServerName = destServer.getServerName();
            if ((!(destServerName.equals(serverName))) && (serverManager.isServerOnline(destServerName))) {
                break;
            }
        }
        Assert.assertTrue(((destServerName != null) && (!(destServerName.equals(serverName)))));
        admin.move(hri.getRegionName(), destServerName).get();
        long timeoutTime = (System.currentTimeMillis()) + 30000;
        while (true) {
            ServerName sn = rawAdmin.getRegionLocation(hri.getRegionName()).get().getServerName();
            if ((sn != null) && (sn.equals(destServerName))) {
                break;
            }
            long now = System.currentTimeMillis();
            if (now > timeoutTime) {
                Assert.fail(("Failed to move the region in time: " + hri));
            }
            Thread.sleep(100);
        } 
        admin.balancerSwitch(true).join();
    }

    @Test
    public void testGetOnlineRegions() throws Exception {
        createTableAndGetOneRegion(tableName);
        AtomicInteger regionServerCount = new AtomicInteger(0);
        TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().stream().map(( rsThread) -> rsThread.getRegionServer()).forEach(( rs) -> {
            ServerName serverName = rs.getServerName();
            try {
                assertEquals(admin.getRegions(serverName).get().size(), rs.getRegions().size());
            } catch ( e) {
                fail(("admin.getOnlineRegions() method throws a exception: " + (e.getMessage())));
            }
            regionServerCount.incrementAndGet();
        });
        Assert.assertEquals(2, regionServerCount.get());
    }

    @Test
    public void testFlushTableAndRegion() throws Exception {
        RegionInfo hri = createTableAndGetOneRegion(tableName);
        ServerName serverName = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster().getAssignmentManager().getRegionStates().getRegionServerOfRegion(hri);
        HRegionServer regionServer = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().stream().map(( rsThread) -> rsThread.getRegionServer()).filter(( rs) -> rs.getServerName().equals(serverName)).findFirst().get();
        // write a put into the specific region
        TestAsyncAdminBase.ASYNC_CONN.getTable(tableName).put(new Put(hri.getStartKey()).addColumn(TestAsyncAdminBase.FAMILY, TestAsyncAdminBase.FAMILY_0, Bytes.toBytes("value-1"))).join();
        Assert.assertTrue(((regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize()) > 0));
        // flush region and wait flush operation finished.
        TestAsyncAdminBase.LOG.info(("flushing region: " + (Bytes.toStringBinary(hri.getRegionName()))));
        admin.flushRegion(hri.getRegionName()).get();
        TestAsyncAdminBase.LOG.info(("blocking until flush is complete: " + (Bytes.toStringBinary(hri.getRegionName()))));
        Threads.sleepWithoutInterrupt(500);
        while ((regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize()) > 0) {
            Threads.sleep(50);
        } 
        // check the memstore.
        Assert.assertEquals(regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize(), 0);
        // write another put into the specific region
        TestAsyncAdminBase.ASYNC_CONN.getTable(tableName).put(new Put(hri.getStartKey()).addColumn(TestAsyncAdminBase.FAMILY, TestAsyncAdminBase.FAMILY_0, Bytes.toBytes("value-2"))).join();
        Assert.assertTrue(((regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize()) > 0));
        admin.flush(tableName).get();
        Threads.sleepWithoutInterrupt(500);
        while ((regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize()) > 0) {
            Threads.sleep(50);
        } 
        // check the memstore.
        Assert.assertEquals(regionServer.getOnlineRegion(hri.getRegionName()).getMemStoreDataSize(), 0);
    }

    @Test
    public void testCompactMob() throws Exception {
        ColumnFamilyDescriptor columnDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes("mob")).setMobEnabled(true).setMobThreshold(0).build();
        TableDescriptor tableDescriptor = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(columnDescriptor).build();
        admin.createTable(tableDescriptor).get();
        byte[][] families = new byte[][]{ Bytes.toBytes("mob") };
        TestAsyncRegionAdminApi.loadData(tableName, families, 3000, 8);
        admin.majorCompact(tableName, MOB).get();
        CompactionState state = admin.getCompactionState(tableName, MOB).get();
        Assert.assertNotEquals(NONE, state);
        waitUntilMobCompactionFinished(tableName);
    }

    @Test
    public void testCompactRegionServer() throws Exception {
        byte[][] families = new byte[][]{ Bytes.toBytes("f1"), Bytes.toBytes("f2"), Bytes.toBytes("f3") };
        createTableWithDefaultConf(tableName, null, families);
        TestAsyncRegionAdminApi.loadData(tableName, families, 3000, 8);
        List<HRegionServer> rsList = TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().stream().map(( rsThread) -> rsThread.getRegionServer()).collect(Collectors.toList());
        List<Region> regions = new ArrayList<>();
        rsList.forEach(( rs) -> regions.addAll(rs.getRegions(tableName)));
        Assert.assertEquals(1, regions.size());
        int countBefore = TestAsyncRegionAdminApi.countStoreFilesInFamilies(regions, families);
        Assert.assertTrue((countBefore > 0));
        // Minor compaction for all region servers.
        for (HRegionServer rs : rsList)
            admin.compactRegionServer(rs.getServerName()).get();

        Thread.sleep(5000);
        int countAfterMinorCompaction = TestAsyncRegionAdminApi.countStoreFilesInFamilies(regions, families);
        Assert.assertTrue((countAfterMinorCompaction < countBefore));
        // Major compaction for all region servers.
        for (HRegionServer rs : rsList)
            admin.majorCompactRegionServer(rs.getServerName()).get();

        Thread.sleep(5000);
        int countAfterMajorCompaction = TestAsyncRegionAdminApi.countStoreFilesInFamilies(regions, families);
        Assert.assertEquals(3, countAfterMajorCompaction);
    }

    @Test
    public void testCompactionSwitchStates() throws Exception {
        // Create a table with regions
        byte[] family = Bytes.toBytes("family");
        byte[][] families = new byte[][]{ family, Bytes.add(family, Bytes.toBytes("2")), Bytes.add(family, Bytes.toBytes("3")) };
        createTableWithDefaultConf(tableName, null, families);
        TestAsyncRegionAdminApi.loadData(tableName, families, 3000, 8);
        List<Region> regions = new ArrayList<>();
        TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getLiveRegionServerThreads().forEach(( rsThread) -> regions.addAll(rsThread.getRegionServer().getRegions(tableName)));
        CompletableFuture<Map<ServerName, Boolean>> listCompletableFuture = admin.compactionSwitch(true, new ArrayList());
        Map<ServerName, Boolean> pairs = listCompletableFuture.get();
        for (Map.Entry<ServerName, Boolean> p : pairs.entrySet()) {
            Assert.assertEquals("Default compaction state, expected=enabled actual=disabled", true, p.getValue());
        }
        CompletableFuture<Map<ServerName, Boolean>> listCompletableFuture1 = admin.compactionSwitch(false, new ArrayList());
        Map<ServerName, Boolean> pairs1 = listCompletableFuture1.get();
        for (Map.Entry<ServerName, Boolean> p : pairs1.entrySet()) {
            Assert.assertEquals("Last compaction state, expected=enabled actual=disabled", true, p.getValue());
        }
        CompletableFuture<Map<ServerName, Boolean>> listCompletableFuture2 = admin.compactionSwitch(true, new ArrayList());
        Map<ServerName, Boolean> pairs2 = listCompletableFuture2.get();
        for (Map.Entry<ServerName, Boolean> p : pairs2.entrySet()) {
            Assert.assertEquals("Last compaction state, expected=disabled actual=enabled", false, p.getValue());
        }
    }

    @Test
    public void testCompact() throws Exception {
        compactionTest(TableName.valueOf("testCompact1"), 8, MAJOR, false);
        compactionTest(TableName.valueOf("testCompact2"), 15, MINOR, false);
        compactionTest(TableName.valueOf("testCompact3"), 8, MAJOR, true);
        compactionTest(TableName.valueOf("testCompact4"), 15, MINOR, true);
    }
}

