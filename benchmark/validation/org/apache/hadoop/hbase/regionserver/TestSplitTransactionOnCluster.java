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


import Consistency.STRONG;
import Consistency.TIMELINE;
import Coprocessor.PRIORITY_USER;
import HConstants.CATALOG_FAMILY;
import NoLimitThroughputController.INSTANCE;
import RegionInfo.COMPARATOR;
import RegionState.State.CLOSING;
import State.OPEN;
import State.SPLIT;
import TransitionCode.READY_TO_SPLIT;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.CompactionState;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.DoNotRetryRegionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.TestReplicasClient;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessor;
import org.apache.hadoop.hbase.coprocessor.MasterCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.MasterObserver;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.MasterRpcServices;
import org.apache.hadoop.hbase.master.assignment.AssignmentManager;
import org.apache.hadoop.hbase.master.assignment.RegionStateNode;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.procedure2.ProcedureTestingUtility;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionContext;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.RegionServerStatusProtos.ReportRegionStateTransitionResponse;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.HBaseFsck;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.util.RetryCounter;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.client.TestReplicasClient.SlowMeCopro.getPrimaryCdl;


/**
 * The below tests are testing split region against a running cluster
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestSplitTransactionOnCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitTransactionOnCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSplitTransactionOnCluster.class);

    private Admin admin = null;

    private MiniHBaseCluster cluster = null;

    private static final int NB_SERVERS = 3;

    static final HBaseTestingUtility TESTING_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRITStateForRollback() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final HMaster master = cluster.getMaster();
        try {
            // Create table then get the single region for our new table.
            Table t = createTableAndWait(tableName, Bytes.toBytes("cf"));
            final List<HRegion> regions = cluster.getRegions(tableName);
            final RegionInfo hri = getAndCheckSingleTableRegion(regions);
            insertData(tableName, admin, t);
            t.close();
            // Turn off balancer so it doesn't cut in and mess up our placements.
            this.admin.balancerSwitch(false, true);
            // Turn off the meta scanner so it don't remove parent on us.
            master.setCatalogJanitorEnabled(false);
            // find a splittable region
            final HRegion region = findSplittableRegion(regions);
            Assert.assertTrue("not able to find a splittable region", (region != null));
            // install master co-processor to fail splits
            master.getMasterCoprocessorHost().load(TestSplitTransactionOnCluster.FailingSplitMasterObserver.class, PRIORITY_USER, master.getConfiguration());
            // split async
            this.admin.splitRegionAsync(region.getRegionInfo().getRegionName(), new byte[]{ 42 });
            // we have to wait until the SPLITTING state is seen by the master
            TestSplitTransactionOnCluster.FailingSplitMasterObserver observer = master.getMasterCoprocessorHost().findCoprocessor(TestSplitTransactionOnCluster.FailingSplitMasterObserver.class);
            Assert.assertNotNull(observer);
            observer.latch.await();
            TestSplitTransactionOnCluster.LOG.info("Waiting for region to come out of RIT");
            while (!(cluster.getMaster().getAssignmentManager().getRegionStates().isRegionOnline(hri))) {
                Threads.sleep(100);
            } 
            Assert.assertTrue(cluster.getMaster().getAssignmentManager().getRegionStates().isRegionOnline(hri));
        } finally {
            admin.balancerSwitch(true, false);
            master.setCatalogJanitorEnabled(true);
            abortAndWaitForMaster();
            TestSplitTransactionOnCluster.TESTING_UTIL.deleteTable(tableName);
        }
    }

    @Test
    public void testSplitFailedCompactionAndSplit() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table then get the single region for our new table.
        byte[] cf = Bytes.toBytes("cf");
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(cf)).build();
        admin.createTable(htd);
        for (int i = 0; (cluster.getRegions(tableName).isEmpty()) && (i < 100); i++) {
            Thread.sleep(100);
        }
        Assert.assertEquals(1, cluster.getRegions(tableName).size());
        HRegion region = cluster.getRegions(tableName).get(0);
        HStore store = region.getStore(cf);
        int regionServerIndex = cluster.getServerWith(region.getRegionInfo().getRegionName());
        HRegionServer regionServer = cluster.getRegionServer(regionServerIndex);
        Table t = TestSplitTransactionOnCluster.TESTING_UTIL.getConnection().getTable(tableName);
        // insert data
        insertData(tableName, admin, t);
        insertData(tableName, admin, t);
        int fileNum = store.getStorefiles().size();
        // 0, Compaction Request
        store.triggerMajorCompaction();
        Optional<CompactionContext> cc = store.requestCompaction();
        Assert.assertTrue(cc.isPresent());
        // 1, A timeout split
        // 1.1 close region
        Assert.assertEquals(2, region.close(false).get(cf).size());
        // 1.2 rollback and Region initialize again
        region.initialize();
        // 2, Run Compaction cc
        Assert.assertFalse(region.compact(cc.get(), store, INSTANCE));
        Assert.assertTrue((fileNum > (store.getStorefiles().size())));
        // 3, Split
        requestSplitRegion(regionServer, region, Bytes.toBytes("row3"));
        Assert.assertEquals(2, cluster.getRegions(tableName).size());
    }

    public static class FailingSplitMasterObserver implements MasterCoprocessor , MasterObserver {
        volatile CountDownLatch latch;

        @Override
        public void start(CoprocessorEnvironment e) throws IOException {
            latch = new CountDownLatch(1);
        }

        @Override
        public Optional<MasterObserver> getMasterObserver() {
            return Optional.of(this);
        }

        @Override
        public void preSplitRegionBeforeMETAAction(final ObserverContext<MasterCoprocessorEnvironment> ctx, final byte[] splitKey, final List<Mutation> metaEntries) throws IOException {
            latch.countDown();
            throw new IOException("Causing rollback of region split");
        }
    }

    @Test
    public void testSplitRollbackOnRegionClosing() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table then get the single region for our new table.
        Table t = createTableAndWait(tableName, CATALOG_FAMILY);
        List<HRegion> regions = cluster.getRegions(tableName);
        RegionInfo hri = getAndCheckSingleTableRegion(regions);
        int tableRegionIndex = ensureTableRegionNotOnSameServerAsMeta(admin, hri);
        RegionStates regionStates = cluster.getMaster().getAssignmentManager().getRegionStates();
        // Turn off balancer so it doesn't cut in and mess up our placements.
        this.admin.balancerSwitch(false, true);
        // Turn off the meta scanner so it don't remove parent on us.
        cluster.getMaster().setCatalogJanitorEnabled(false);
        try {
            // Add a bit of load up into the table so splittable.
            TestSplitTransactionOnCluster.TESTING_UTIL.loadTable(t, CATALOG_FAMILY, false);
            // Get region pre-split.
            HRegionServer server = cluster.getRegionServer(tableRegionIndex);
            printOutRegions(server, "Initial regions: ");
            int regionCount = cluster.getRegions(hri.getTable()).size();
            regionStates.updateRegionState(hri, CLOSING);
            // Now try splitting.... should fail.  And each should successfully
            // rollback.
            // We don't roll back here anymore. Instead we fail-fast on construction of the
            // split transaction. Catch the exception instead.
            try {
                this.admin.splitRegionAsync(hri.getRegionName(), null);
                Assert.fail();
            } catch (DoNotRetryRegionException e) {
                // Expected
            }
            // Wait around a while and assert count of regions remains constant.
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                Assert.assertEquals(regionCount, cluster.getRegions(hri.getTable()).size());
            }
            regionStates.updateRegionState(hri, OPEN);
            // Now try splitting and it should work.
            split(hri, server, regionCount);
            // Get daughters
            checkAndGetDaughters(tableName);
            // OK, so split happened after we cleared the blocking node.
        } finally {
            admin.balancerSwitch(true, false);
            cluster.getMaster().setCatalogJanitorEnabled(true);
            t.close();
        }
    }

    /**
     * Test that if daughter split on us, we won't do the shutdown handler fixup
     * just because we can't find the immediate daughter of an offlined parent.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testShutdownFixupWhenDaughterHasSplit() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table then get the single region for our new table.
        Table t = createTableAndWait(tableName, CATALOG_FAMILY);
        List<HRegion> regions = cluster.getRegions(tableName);
        RegionInfo hri = getAndCheckSingleTableRegion(regions);
        int tableRegionIndex = ensureTableRegionNotOnSameServerAsMeta(admin, hri);
        // Turn off balancer so it doesn't cut in and mess up our placements.
        this.admin.balancerSwitch(false, true);
        // Turn off the meta scanner so it don't remove parent on us.
        cluster.getMaster().setCatalogJanitorEnabled(false);
        try {
            // Add a bit of load up into the table so splittable.
            TestSplitTransactionOnCluster.TESTING_UTIL.loadTable(t, CATALOG_FAMILY);
            // Get region pre-split.
            HRegionServer server = cluster.getRegionServer(tableRegionIndex);
            printOutRegions(server, "Initial regions: ");
            int regionCount = cluster.getRegions(hri.getTable()).size();
            // Now split.
            split(hri, server, regionCount);
            // Get daughters
            List<HRegion> daughters = checkAndGetDaughters(tableName);
            // Now split one of the daughters.
            regionCount = cluster.getRegions(hri.getTable()).size();
            RegionInfo daughter = daughters.get(0).getRegionInfo();
            TestSplitTransactionOnCluster.LOG.info(("Daughter we are going to split: " + daughter));
            // Compact first to ensure we have cleaned up references -- else the split
            // will fail.
            this.admin.compactRegion(daughter.getRegionName());
            RetryCounter retrier = new RetryCounter(30, 1, TimeUnit.SECONDS);
            while (((CompactionState.NONE) != (admin.getCompactionStateForRegion(daughter.getRegionName()))) && (retrier.shouldRetry())) {
                retrier.sleepUntilNextRetry();
            } 
            daughters = cluster.getRegions(tableName);
            HRegion daughterRegion = null;
            for (HRegion r : daughters) {
                if ((COMPARATOR.compare(r.getRegionInfo(), daughter)) == 0) {
                    daughterRegion = r;
                    // Archiving the compacted references file
                    r.getStores().get(0).closeAndArchiveCompactedFiles();
                    TestSplitTransactionOnCluster.LOG.info(("Found matching HRI: " + daughterRegion));
                    break;
                }
            }
            Assert.assertTrue((daughterRegion != null));
            for (int i = 0; i < 100; i++) {
                if (!(daughterRegion.hasReferences()))
                    break;

                Threads.sleep(100);
            }
            Assert.assertFalse("Waiting for reference to be compacted", daughterRegion.hasReferences());
            TestSplitTransactionOnCluster.LOG.info(("Daughter hri before split (has been compacted): " + daughter));
            split(daughter, server, regionCount);
            // Get list of daughters
            daughters = cluster.getRegions(tableName);
            for (HRegion d : daughters) {
                TestSplitTransactionOnCluster.LOG.info(("Regions before crash: " + d));
            }
            // Now crash the server
            cluster.abortRegionServer(tableRegionIndex);
            waitUntilRegionServerDead();
            awaitDaughters(tableName, daughters.size());
            // Assert daughters are online and ONLY the original daughters -- that
            // fixup didn't insert one during server shutdown recover.
            regions = cluster.getRegions(tableName);
            for (HRegion d : daughters) {
                TestSplitTransactionOnCluster.LOG.info(("Regions after crash: " + d));
            }
            if ((daughters.size()) != (regions.size())) {
                TestSplitTransactionOnCluster.LOG.info(((("Daughters=" + (daughters.size())) + ", regions=") + (regions.size())));
            }
            Assert.assertEquals(daughters.size(), regions.size());
            for (HRegion r : regions) {
                TestSplitTransactionOnCluster.LOG.info(((("Regions post crash " + r) + ", contains=") + (daughters.contains(r))));
                Assert.assertTrue(("Missing region post crash " + r), daughters.contains(r));
            }
        } finally {
            TestSplitTransactionOnCluster.LOG.info("EXITING");
            admin.balancerSwitch(true, false);
            cluster.getMaster().setCatalogJanitorEnabled(true);
            t.close();
        }
    }

    @Test
    public void testSplitShouldNotThrowNPEEvenARegionHasEmptySplitFiles() throws Exception {
        TableName userTableName = TableName.valueOf(name.getMethodName());
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(userTableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of("col")).build();
        admin.createTable(htd);
        Table table = TestSplitTransactionOnCluster.TESTING_UTIL.getConnection().getTable(userTableName);
        try {
            for (int i = 0; i <= 5; i++) {
                String row = "row" + i;
                Put p = new Put(Bytes.toBytes(row));
                String val = "Val" + i;
                p.addColumn(Bytes.toBytes("col"), Bytes.toBytes("ql"), Bytes.toBytes(val));
                table.put(p);
                admin.flush(userTableName);
                Delete d = new Delete(Bytes.toBytes(row));
                // Do a normal delete
                table.delete(d);
                admin.flush(userTableName);
            }
            admin.majorCompact(userTableName);
            List<RegionInfo> regionsOfTable = cluster.getMaster().getAssignmentManager().getRegionStates().getRegionsOfTable(userTableName);
            Assert.assertEquals(1, regionsOfTable.size());
            RegionInfo hRegionInfo = regionsOfTable.get(0);
            Put p = new Put(Bytes.toBytes("row6"));
            p.addColumn(Bytes.toBytes("col"), Bytes.toBytes("ql"), Bytes.toBytes("val"));
            table.put(p);
            p = new Put(Bytes.toBytes("row7"));
            p.addColumn(Bytes.toBytes("col"), Bytes.toBytes("ql"), Bytes.toBytes("val"));
            table.put(p);
            p = new Put(Bytes.toBytes("row8"));
            p.addColumn(Bytes.toBytes("col"), Bytes.toBytes("ql"), Bytes.toBytes("val"));
            table.put(p);
            admin.flush(userTableName);
            admin.splitRegionAsync(hRegionInfo.getRegionName(), Bytes.toBytes("row7"));
            regionsOfTable = cluster.getMaster().getAssignmentManager().getRegionStates().getRegionsOfTable(userTableName);
            while ((regionsOfTable.size()) != 2) {
                Thread.sleep(1000);
                regionsOfTable = cluster.getMaster().getAssignmentManager().getRegionStates().getRegionsOfTable(userTableName);
                TestSplitTransactionOnCluster.LOG.debug(((("waiting 2 regions to be available, got " + (regionsOfTable.size())) + ": ") + regionsOfTable));
            } 
            Assert.assertEquals(2, regionsOfTable.size());
            Scan s = new Scan();
            ResultScanner scanner = table.getScanner(s);
            int mainTableCount = 0;
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
                mainTableCount++;
            }
            Assert.assertEquals(3, mainTableCount);
        } finally {
            table.close();
        }
    }

    /**
     * Verifies HBASE-5806.  Here the case is that splitting is completed but before the
     * CJ could remove the parent region the master is killed and restarted.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws NodeExistsException
     * 		
     * @throws KeeperException
     * 		
     */
    @Test
    public void testMasterRestartAtRegionSplitPendingCatalogJanitor() throws IOException, InterruptedException, ServiceException, KeeperException, NodeExistsException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table then get the single region for our new table.
        Table t = createTableAndWait(tableName, CATALOG_FAMILY);
        List<HRegion> regions = cluster.getRegions(tableName);
        RegionInfo hri = getAndCheckSingleTableRegion(regions);
        int tableRegionIndex = ensureTableRegionNotOnSameServerAsMeta(admin, hri);
        // Turn off balancer so it doesn't cut in and mess up our placements.
        this.admin.balancerSwitch(false, true);
        // Turn off the meta scanner so it don't remove parent on us.
        cluster.getMaster().setCatalogJanitorEnabled(false);
        try {
            // Add a bit of load up into the table so splittable.
            TestSplitTransactionOnCluster.TESTING_UTIL.loadTable(t, CATALOG_FAMILY, false);
            // Get region pre-split.
            HRegionServer server = cluster.getRegionServer(tableRegionIndex);
            printOutRegions(server, "Initial regions: ");
            // Call split.
            this.admin.splitRegionAsync(hri.getRegionName(), null);
            List<HRegion> daughters = checkAndGetDaughters(tableName);
            // Before cleanup, get a new master.
            HMaster master = abortAndWaitForMaster();
            // Now call compact on the daughters and clean up any references.
            for (HRegion daughter : daughters) {
                daughter.compact(true);
                RetryCounter retrier = new RetryCounter(30, 1, TimeUnit.SECONDS);
                while (((CompactionState.NONE) != (admin.getCompactionStateForRegion(daughter.getRegionInfo().getRegionName()))) && (retrier.shouldRetry())) {
                    retrier.sleepUntilNextRetry();
                } 
                daughter.getStores().get(0).closeAndArchiveCompactedFiles();
                Assert.assertFalse(daughter.hasReferences());
            }
            // BUT calling compact on the daughters is not enough. The CatalogJanitor looks
            // in the filesystem, and the filesystem content is not same as what the Region
            // is reading from. Compacted-away files are picked up later by the compacted
            // file discharger process. It runs infrequently. Make it run so CatalogJanitor
            // doens't find any references.
            for (RegionServerThread rst : cluster.getRegionServerThreads()) {
                boolean oldSetting = rst.getRegionServer().compactedFileDischarger.setUseExecutor(false);
                rst.getRegionServer().compactedFileDischarger.run();
                rst.getRegionServer().compactedFileDischarger.setUseExecutor(oldSetting);
            }
            cluster.getMaster().setCatalogJanitorEnabled(true);
            TestSplitTransactionOnCluster.LOG.info("Starting run of CatalogJanitor");
            cluster.getMaster().getCatalogJanitor().run();
            ProcedureTestingUtility.waitAllProcedures(cluster.getMaster().getMasterProcedureExecutor());
            RegionStates regionStates = master.getAssignmentManager().getRegionStates();
            ServerName regionServerOfRegion = regionStates.getRegionServerOfRegion(hri);
            Assert.assertEquals(null, regionServerOfRegion);
        } finally {
            TestSplitTransactionOnCluster.TESTING_UTIL.getAdmin().balancerSwitch(true, false);
            cluster.getMaster().setCatalogJanitorEnabled(true);
            t.close();
        }
    }

    @Test
    public void testSplitWithRegionReplicas() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = TestSplitTransactionOnCluster.TESTING_UTIL.createTableDescriptor(name.getMethodName());
        htd.setRegionReplication(2);
        htd.addCoprocessor(TestReplicasClient.SlowMeCopro.class.getName());
        // Create table then get the single region for our new table.
        Table t = TestSplitTransactionOnCluster.TESTING_UTIL.createTable(htd, new byte[][]{ Bytes.toBytes("cf") }, null);
        List<HRegion> oldRegions;
        do {
            oldRegions = cluster.getRegions(tableName);
            Thread.sleep(10);
        } while ((oldRegions.size()) != 2 );
        for (HRegion h : oldRegions)
            TestSplitTransactionOnCluster.LOG.debug(("OLDREGION " + (h.getRegionInfo())));

        try {
            int regionServerIndex = cluster.getServerWith(oldRegions.get(0).getRegionInfo().getRegionName());
            HRegionServer regionServer = cluster.getRegionServer(regionServerIndex);
            insertData(tableName, admin, t);
            // Turn off balancer so it doesn't cut in and mess up our placements.
            admin.balancerSwitch(false, true);
            // Turn off the meta scanner so it don't remove parent on us.
            cluster.getMaster().setCatalogJanitorEnabled(false);
            boolean tableExists = MetaTableAccessor.tableExists(regionServer.getConnection(), tableName);
            Assert.assertEquals("The specified table should be present.", true, tableExists);
            final HRegion region = findSplittableRegion(oldRegions);
            regionServerIndex = cluster.getServerWith(region.getRegionInfo().getRegionName());
            regionServer = cluster.getRegionServer(regionServerIndex);
            Assert.assertTrue("not able to find a splittable region", (region != null));
            try {
                requestSplitRegion(regionServer, region, Bytes.toBytes("row2"));
            } catch (IOException e) {
                e.printStackTrace();
                Assert.fail(("Split execution should have succeeded with no exceptions thrown " + e));
            }
            // TESTING_UTIL.waitUntilAllRegionsAssigned(tableName);
            List<HRegion> newRegions;
            do {
                newRegions = cluster.getRegions(tableName);
                for (HRegion h : newRegions)
                    TestSplitTransactionOnCluster.LOG.debug(("NEWREGION " + (h.getRegionInfo())));

                Thread.sleep(1000);
            } while (((newRegions.contains(oldRegions.get(0))) || (newRegions.contains(oldRegions.get(1)))) || ((newRegions.size()) != 4) );
            tableExists = MetaTableAccessor.tableExists(regionServer.getConnection(), tableName);
            Assert.assertEquals("The specified table should be present.", true, tableExists);
            // exists works on stale and we see the put after the flush
            byte[] b1 = Bytes.toBytes("row1");
            Get g = new Get(b1);
            g.setConsistency(STRONG);
            // The following GET will make a trip to the meta to get the new location of the 1st daughter
            // In the process it will also get the location of the replica of the daughter (initially
            // pointing to the parent's replica)
            Result r = t.get(g);
            Assert.assertFalse(r.isStale());
            TestSplitTransactionOnCluster.LOG.info("exists stale after flush done");
            getPrimaryCdl().set(new CountDownLatch(1));
            g = new Get(b1);
            g.setConsistency(TIMELINE);
            // This will succeed because in the previous GET we get the location of the replica
            r = t.get(g);
            Assert.assertTrue(r.isStale());
            getPrimaryCdl().get().countDown();
        } finally {
            getPrimaryCdl().get().countDown();
            admin.balancerSwitch(true, false);
            cluster.getMaster().setCatalogJanitorEnabled(true);
            t.close();
        }
    }

    /**
     * If a table has regions that have no store files in a region, they should split successfully
     * into two regions with no store files.
     */
    @Test
    public void testSplitRegionWithNoStoreFiles() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create table then get the single region for our new table.
        createTableAndWait(tableName, CATALOG_FAMILY);
        List<HRegion> regions = cluster.getRegions(tableName);
        RegionInfo hri = getAndCheckSingleTableRegion(regions);
        ensureTableRegionNotOnSameServerAsMeta(admin, hri);
        int regionServerIndex = cluster.getServerWith(regions.get(0).getRegionInfo().getRegionName());
        HRegionServer regionServer = cluster.getRegionServer(regionServerIndex);
        // Turn off balancer so it doesn't cut in and mess up our placements.
        this.admin.balancerSwitch(false, true);
        // Turn off the meta scanner so it don't remove parent on us.
        cluster.getMaster().setCatalogJanitorEnabled(false);
        try {
            // Precondition: we created a table with no data, no store files.
            printOutRegions(regionServer, "Initial regions: ");
            Configuration conf = cluster.getConfiguration();
            HBaseFsck.debugLsr(conf, new Path("/"));
            Path rootDir = FSUtils.getRootDir(conf);
            FileSystem fs = TestSplitTransactionOnCluster.TESTING_UTIL.getDFSCluster().getFileSystem();
            Map<String, Path> storefiles = FSUtils.getTableStoreFilePathMap(null, fs, rootDir, tableName);
            Assert.assertEquals(("Expected nothing but found " + (storefiles.toString())), 0, storefiles.size());
            // find a splittable region.  Refresh the regions list
            regions = cluster.getRegions(tableName);
            final HRegion region = findSplittableRegion(regions);
            Assert.assertTrue("not able to find a splittable region", (region != null));
            // Now split.
            try {
                requestSplitRegion(regionServer, region, Bytes.toBytes("row2"));
            } catch (IOException e) {
                Assert.fail("Split execution should have succeeded with no exceptions thrown");
            }
            // Postcondition: split the table with no store files into two regions, but still have no
            // store files
            List<HRegion> daughters = cluster.getRegions(tableName);
            Assert.assertEquals(2, daughters.size());
            // check dirs
            HBaseFsck.debugLsr(conf, new Path("/"));
            Map<String, Path> storefilesAfter = FSUtils.getTableStoreFilePathMap(null, fs, rootDir, tableName);
            Assert.assertEquals(("Expected nothing but found " + (storefilesAfter.toString())), 0, storefilesAfter.size());
            hri = region.getRegionInfo();// split parent

            AssignmentManager am = cluster.getMaster().getAssignmentManager();
            RegionStates regionStates = am.getRegionStates();
            long start = EnvironmentEdgeManager.currentTime();
            while (!(regionStates.isRegionInState(hri, SPLIT))) {
                TestSplitTransactionOnCluster.LOG.debug(("Waiting for SPLIT state on: " + hri));
                Assert.assertFalse("Timed out in waiting split parent to be in state SPLIT", (((EnvironmentEdgeManager.currentTime()) - start) > 60000));
                Thread.sleep(500);
            } 
            Assert.assertTrue(regionStates.isRegionInState(daughters.get(0).getRegionInfo(), OPEN));
            Assert.assertTrue(regionStates.isRegionInState(daughters.get(1).getRegionInfo(), OPEN));
            // We should not be able to assign it again
            try {
                am.assign(hri);
            } catch (DoNotRetryIOException e) {
                // Expected
            }
            Assert.assertFalse("Split region can't be assigned", regionStates.isRegionInTransition(hri));
            Assert.assertTrue(regionStates.isRegionInState(hri, SPLIT));
            // We should not be able to unassign it either
            try {
                am.unassign(hri);
                Assert.fail("Should have thrown exception");
            } catch (DoNotRetryIOException e) {
                // Expected
            }
            Assert.assertFalse("Split region can't be unassigned", regionStates.isRegionInTransition(hri));
            Assert.assertTrue(regionStates.isRegionInState(hri, SPLIT));
        } finally {
            admin.balancerSwitch(true, false);
            cluster.getMaster().setCatalogJanitorEnabled(true);
        }
    }

    @Test
    public void testStoreFileReferenceCreationWhenSplitPolicySaysToSkipRangeCheck() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            byte[] cf = Bytes.toBytes("f");
            byte[] cf1 = Bytes.toBytes("i_f");
            TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(cf)).setColumnFamily(ColumnFamilyDescriptorBuilder.of(cf1)).setRegionSplitPolicyClassName(TestSplitTransactionOnCluster.CustomSplitPolicy.class.getName()).build();
            admin.createTable(htd);
            List<HRegion> regions = awaitTableRegions(tableName);
            HRegion region = regions.get(0);
            for (int i = 3; i < 9; i++) {
                Put p = new Put(Bytes.toBytes(("row" + i)));
                p.addColumn(cf, Bytes.toBytes("q"), Bytes.toBytes(("value" + i)));
                p.addColumn(cf1, Bytes.toBytes("q"), Bytes.toBytes(("value" + i)));
                region.put(p);
            }
            region.flush(true);
            HStore store = region.getStore(cf);
            Collection<HStoreFile> storefiles = store.getStorefiles();
            Assert.assertEquals(1, storefiles.size());
            Assert.assertFalse(region.hasReferences());
            Path referencePath = region.getRegionFileSystem().splitStoreFile(region.getRegionInfo(), "f", storefiles.iterator().next(), Bytes.toBytes("row1"), false, region.getSplitPolicy());
            Assert.assertNull(referencePath);
            referencePath = region.getRegionFileSystem().splitStoreFile(region.getRegionInfo(), "i_f", storefiles.iterator().next(), Bytes.toBytes("row1"), false, region.getSplitPolicy());
            Assert.assertNotNull(referencePath);
        } finally {
            TestSplitTransactionOnCluster.TESTING_UTIL.deleteTable(tableName);
        }
    }

    // Make it public so that JVMClusterUtil can access it.
    public static class MyMaster extends HMaster {
        public MyMaster(Configuration conf) throws IOException, InterruptedException, KeeperException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestSplitTransactionOnCluster.MyMasterRpcServices(this);
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
            if (((TestSplitTransactionOnCluster.MyMasterRpcServices.enabled.get()) && (req.getTransition(0).getTransitionCode().equals(READY_TO_SPLIT))) && (!(resp.hasErrorMessage()))) {
                RegionStates regionStates = myMaster.getAssignmentManager().getRegionStates();
                for (RegionStateNode regionState : regionStates.getRegionsInTransition()) {
                    /* TODO!!!!
                    // Find the merging_new region and remove it
                    if (regionState.isSplittingNew()) {
                    regionStates.deleteRegion(regionState.getRegion());
                    }
                     */
                }
            }
            return resp;
        }
    }

    static class CustomSplitPolicy extends IncreasingToUpperBoundRegionSplitPolicy {
        @Override
        protected boolean shouldSplit() {
            return true;
        }

        @Override
        public boolean skipStoreFileRangeCheck(String familyName) {
            if (familyName.startsWith("i_")) {
                return true;
            } else {
                return false;
            }
        }
    }
}

