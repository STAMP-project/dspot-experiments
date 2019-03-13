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


import Consistency.TIMELINE;
import HConstants.EMPTY_START_ROW;
import HConstants.HBASE_CLIENT_INSTANCE_ID;
import HConstants.REPLICATION_SCOPE_GLOBAL;
import HConstants.ZOOKEEPER_ZNODE_PARENT;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.replication.ReplicationAdmin;
import org.apache.hadoop.hbase.coprocessor.CoreCoprocessor;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.regionserver.TestHRegionServerBulkLoad;
import org.apache.hadoop.hbase.replication.ReplicationPeerConfig;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.zookeeper.MiniZooKeeperCluster;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MediumTests.class, ClientTests.class })
public class TestReplicaWithCluster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicaWithCluster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicaWithCluster.class);

    private static final int NB_SERVERS = 3;

    private static final byte[] row = Bytes.toBytes(TestReplicaWithCluster.class.getName());

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    // second minicluster used in testing of replication
    private static HBaseTestingUtility HTU2;

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    private static final int REFRESH_PERIOD = 1000;

    private static final int META_SCAN_TIMEOUT_IN_MILLISEC = 200;

    /**
     * This copro is used to synchronize the tests.
     */
    public static class SlowMeCopro implements RegionCoprocessor , RegionObserver {
        static final AtomicLong sleepTime = new AtomicLong(0);

        static final AtomicReference<CountDownLatch> cdl = new AtomicReference<>(new CountDownLatch(0));

        public SlowMeCopro() {
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e, final Get get, final List<Cell> results) throws IOException {
            if ((e.getEnvironment().getRegion().getRegionInfo().getReplicaId()) == 0) {
                CountDownLatch latch = TestReplicaWithCluster.SlowMeCopro.cdl.get();
                try {
                    if ((TestReplicaWithCluster.SlowMeCopro.sleepTime.get()) > 0) {
                        TestReplicaWithCluster.LOG.info((("Sleeping for " + (TestReplicaWithCluster.SlowMeCopro.sleepTime.get())) + " ms"));
                        Thread.sleep(TestReplicaWithCluster.SlowMeCopro.sleepTime.get());
                    } else
                        if ((latch.getCount()) > 0) {
                            TestReplicaWithCluster.LOG.info("Waiting for the counterCountDownLatch");
                            latch.await(2, TimeUnit.MINUTES);// To help the tests to finish.

                            if ((latch.getCount()) > 0) {
                                throw new RuntimeException("Can't wait more");
                            }
                        }

                } catch (InterruptedException e1) {
                    TestReplicaWithCluster.LOG.error(e1.toString(), e1);
                }
            } else {
                TestReplicaWithCluster.LOG.info("We're not the primary replicas.");
            }
        }
    }

    /**
     * This copro is used to simulate region server down exception for Get and Scan
     */
    @CoreCoprocessor
    public static class RegionServerStoppedCopro implements RegionCoprocessor , RegionObserver {
        public RegionServerStoppedCopro() {
        }

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e, final Get get, final List<Cell> results) throws IOException {
            int replicaId = e.getEnvironment().getRegion().getRegionInfo().getReplicaId();
            // Fail for the primary replica and replica 1
            if ((e.getEnvironment().getRegion().getRegionInfo().getReplicaId()) <= 1) {
                TestReplicaWithCluster.LOG.info(("Throw Region Server Stopped Exceptoin for replica id " + replicaId));
                throw new org.apache.hadoop.hbase.regionserver.RegionServerStoppedException((("Server " + (e.getEnvironment().getServerName())) + " not running"));
            } else {
                TestReplicaWithCluster.LOG.info(("We're replica region " + replicaId));
            }
        }

        @Override
        public void preScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan) throws IOException {
            int replicaId = e.getEnvironment().getRegion().getRegionInfo().getReplicaId();
            // Fail for the primary replica and replica 1
            if ((e.getEnvironment().getRegion().getRegionInfo().getReplicaId()) <= 1) {
                TestReplicaWithCluster.LOG.info(("Throw Region Server Stopped Exceptoin for replica id " + replicaId));
                throw new org.apache.hadoop.hbase.regionserver.RegionServerStoppedException((("Server " + (e.getEnvironment().getServerName())) + " not running"));
            } else {
                TestReplicaWithCluster.LOG.info(("We're replica region " + replicaId));
            }
        }
    }

    /**
     * This copro is used to slow down the primary meta region scan a bit
     */
    public static class RegionServerHostingPrimayMetaRegionSlowOrStopCopro implements RegionCoprocessor , RegionObserver {
        static boolean slowDownPrimaryMetaScan = false;

        static boolean throwException = false;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void preGetOp(final ObserverContext<RegionCoprocessorEnvironment> e, final Get get, final List<Cell> results) throws IOException {
            int replicaId = e.getEnvironment().getRegion().getRegionInfo().getReplicaId();
            // Fail for the primary replica, but not for meta
            if (TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.throwException) {
                if ((!(e.getEnvironment().getRegion().getRegionInfo().isMetaRegion())) && (replicaId == 0)) {
                    TestReplicaWithCluster.LOG.info(("Get, throw Region Server Stopped Exceptoin for region " + (e.getEnvironment().getRegion().getRegionInfo())));
                    throw new org.apache.hadoop.hbase.regionserver.RegionServerStoppedException((("Server " + (e.getEnvironment().getServerName())) + " not running"));
                }
            } else {
                TestReplicaWithCluster.LOG.info(("Get, We're replica region " + replicaId));
            }
        }

        @Override
        public void preScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan) throws IOException {
            int replicaId = e.getEnvironment().getRegion().getRegionInfo().getReplicaId();
            // Slow down with the primary meta region scan
            if ((e.getEnvironment().getRegion().getRegionInfo().isMetaRegion()) && (replicaId == 0)) {
                if (TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.slowDownPrimaryMetaScan) {
                    TestReplicaWithCluster.LOG.info("Scan with primary meta region, slow down a bit");
                    try {
                        Thread.sleep(((TestReplicaWithCluster.META_SCAN_TIMEOUT_IN_MILLISEC) - 50));
                    } catch (InterruptedException ie) {
                        // Ingore
                    }
                }
                // Fail for the primary replica
                if (TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.throwException) {
                    TestReplicaWithCluster.LOG.info(("Scan, throw Region Server Stopped Exceptoin for replica " + (e.getEnvironment().getRegion().getRegionInfo())));
                    throw new org.apache.hadoop.hbase.regionserver.RegionServerStoppedException((("Server " + (e.getEnvironment().getServerName())) + " not running"));
                } else {
                    TestReplicaWithCluster.LOG.info(("Scan, We're replica region " + replicaId));
                }
            } else {
                TestReplicaWithCluster.LOG.info(("Scan, We're replica region " + replicaId));
            }
        }
    }

    @Test
    public void testCreateDeleteTable() throws IOException {
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testCreateDeleteTable");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        hdt.addCoprocessor(TestReplicaWithCluster.SlowMeCopro.class.getName());
        Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
        Put p = new Put(TestReplicaWithCluster.row);
        p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
        table.put(p);
        Get g = new Get(TestReplicaWithCluster.row);
        Result r = table.get(g);
        Assert.assertFalse(r.isStale());
        try {
            // But if we ask for stale we will get it
            TestReplicaWithCluster.SlowMeCopro.cdl.set(new CountDownLatch(1));
            g = new Get(TestReplicaWithCluster.row);
            g.setConsistency(TIMELINE);
            r = table.get(g);
            Assert.assertTrue(r.isStale());
            TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
        } finally {
            TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
            TestReplicaWithCluster.SlowMeCopro.sleepTime.set(0);
        }
        TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
        TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
    }

    @Test
    public void testChangeTable() throws Exception {
        TableDescriptor td = TableDescriptorBuilder.newBuilder(TableName.valueOf("testChangeTable")).setRegionReplication(TestReplicaWithCluster.NB_SERVERS).setCoprocessor(TestReplicaWithCluster.SlowMeCopro.class.getName()).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestReplicaWithCluster.f)).build();
        TestReplicaWithCluster.HTU.getAdmin().createTable(td);
        Table table = TestReplicaWithCluster.HTU.getConnection().getTable(td.getTableName());
        // basic test: it should work.
        Put p = new Put(TestReplicaWithCluster.row);
        p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
        table.put(p);
        Get g = new Get(TestReplicaWithCluster.row);
        Result r = table.get(g);
        Assert.assertFalse(r.isStale());
        // Add a CF, it should work.
        TableDescriptor bHdt = TestReplicaWithCluster.HTU.getAdmin().getDescriptor(td.getTableName());
        td = TableDescriptorBuilder.newBuilder(td).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestReplicaWithCluster.row)).build();
        TestReplicaWithCluster.HTU.getAdmin().disableTable(td.getTableName());
        TestReplicaWithCluster.HTU.getAdmin().modifyTable(td);
        TestReplicaWithCluster.HTU.getAdmin().enableTable(td.getTableName());
        TableDescriptor nHdt = TestReplicaWithCluster.HTU.getAdmin().getDescriptor(td.getTableName());
        Assert.assertEquals(("fams=" + (Arrays.toString(nHdt.getColumnFamilies()))), ((bHdt.getColumnFamilyCount()) + 1), nHdt.getColumnFamilyCount());
        p = new Put(TestReplicaWithCluster.row);
        p.addColumn(TestReplicaWithCluster.row, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
        table.put(p);
        g = new Get(TestReplicaWithCluster.row);
        r = table.get(g);
        Assert.assertFalse(r.isStale());
        try {
            TestReplicaWithCluster.SlowMeCopro.cdl.set(new CountDownLatch(1));
            g = new Get(TestReplicaWithCluster.row);
            g.setConsistency(TIMELINE);
            r = table.get(g);
            Assert.assertTrue(r.isStale());
        } finally {
            TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
            TestReplicaWithCluster.SlowMeCopro.sleepTime.set(0);
        }
        Admin admin = TestReplicaWithCluster.HTU.getAdmin();
        nHdt = admin.getDescriptor(td.getTableName());
        Assert.assertEquals(("fams=" + (Arrays.toString(nHdt.getColumnFamilies()))), ((bHdt.getColumnFamilyCount()) + 1), nHdt.getColumnFamilyCount());
        admin.disableTable(td.getTableName());
        admin.deleteTable(td.getTableName());
        admin.close();
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testReplicaAndReplication() throws Exception {
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testReplicaAndReplication");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        HColumnDescriptor fam = new HColumnDescriptor(TestReplicaWithCluster.row);
        fam.setScope(REPLICATION_SCOPE_GLOBAL);
        hdt.addFamily(fam);
        hdt.addCoprocessor(TestReplicaWithCluster.SlowMeCopro.class.getName());
        TestReplicaWithCluster.HTU.getAdmin().createTable(hdt, HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE);
        Configuration conf2 = HBaseConfiguration.create(TestReplicaWithCluster.HTU.getConfiguration());
        conf2.set(HBASE_CLIENT_INSTANCE_ID, String.valueOf((-1)));
        conf2.set(ZOOKEEPER_ZNODE_PARENT, "/2");
        MiniZooKeeperCluster miniZK = getZkCluster();
        TestReplicaWithCluster.HTU2 = new HBaseTestingUtility(conf2);
        TestReplicaWithCluster.HTU2.setZkCluster(miniZK);
        TestReplicaWithCluster.HTU2.startMiniCluster(TestReplicaWithCluster.NB_SERVERS);
        TestReplicaWithCluster.LOG.info("Setup second Zk");
        TestReplicaWithCluster.HTU2.getAdmin().createTable(hdt, HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE);
        ReplicationAdmin admin = new ReplicationAdmin(TestReplicaWithCluster.HTU.getConfiguration());
        ReplicationPeerConfig rpc = new ReplicationPeerConfig();
        rpc.setClusterKey(TestReplicaWithCluster.HTU2.getClusterKey());
        admin.addPeer("2", rpc, null);
        admin.close();
        Put p = new Put(TestReplicaWithCluster.row);
        p.addColumn(TestReplicaWithCluster.row, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
        final Table table = TestReplicaWithCluster.HTU.getConnection().getTable(hdt.getTableName());
        table.put(p);
        TestReplicaWithCluster.HTU.getAdmin().flush(table.getName());
        TestReplicaWithCluster.LOG.info("Put & flush done on the first cluster. Now doing a get on the same cluster.");
        Waiter.waitFor(TestReplicaWithCluster.HTU.getConfiguration(), 1000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                try {
                    SlowMeCopro.cdl.set(new CountDownLatch(1));
                    Get g = new Get(TestReplicaWithCluster.row);
                    g.setConsistency(Consistency.TIMELINE);
                    Result r = table.get(g);
                    Assert.assertTrue(r.isStale());
                    return !(r.isEmpty());
                } finally {
                    SlowMeCopro.cdl.get().countDown();
                    SlowMeCopro.sleepTime.set(0);
                }
            }
        });
        table.close();
        TestReplicaWithCluster.LOG.info("stale get on the first cluster done. Now for the second.");
        final Table table2 = TestReplicaWithCluster.HTU.getConnection().getTable(hdt.getTableName());
        Waiter.waitFor(TestReplicaWithCluster.HTU.getConfiguration(), 1000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                try {
                    SlowMeCopro.cdl.set(new CountDownLatch(1));
                    Get g = new Get(TestReplicaWithCluster.row);
                    g.setConsistency(Consistency.TIMELINE);
                    Result r = table2.get(g);
                    Assert.assertTrue(r.isStale());
                    return !(r.isEmpty());
                } finally {
                    SlowMeCopro.cdl.get().countDown();
                    SlowMeCopro.sleepTime.set(0);
                }
            }
        });
        table2.close();
        TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
        TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        TestReplicaWithCluster.HTU2.getAdmin().disableTable(hdt.getTableName());
        TestReplicaWithCluster.HTU2.deleteTable(hdt.getTableName());
        // We shutdown HTU2 minicluster later, in afterClass(), as shutting down
        // the minicluster has negative impact of deleting all HConnections in JVM.
    }

    @Test
    public void testBulkLoad() throws IOException {
        // Create table then get the single region for our new table.
        TestReplicaWithCluster.LOG.debug("Creating test table");
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testBulkLoad");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        hdt.addCoprocessor(TestReplicaWithCluster.SlowMeCopro.class.getName());
        Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
        // create hfiles to load.
        TestReplicaWithCluster.LOG.debug("Creating test data");
        Path dir = TestReplicaWithCluster.HTU.getDataTestDirOnTestFS("testBulkLoad");
        final int numRows = 10;
        final byte[] qual = Bytes.toBytes("qual");
        final byte[] val = Bytes.toBytes("val");
        final List<Pair<byte[], String>> famPaths = new ArrayList<>();
        for (HColumnDescriptor col : hdt.getColumnFamilies()) {
            Path hfile = new Path(dir, col.getNameAsString());
            TestHRegionServerBulkLoad.createHFile(TestReplicaWithCluster.HTU.getTestFileSystem(), hfile, col.getName(), qual, val, numRows);
            famPaths.add(new Pair(col.getName(), hfile.toString()));
        }
        // bulk load HFiles
        TestReplicaWithCluster.LOG.debug("Loading test data");
        final ClusterConnection conn = ((ClusterConnection) (TestReplicaWithCluster.HTU.getAdmin().getConnection()));
        table = conn.getTable(hdt.getTableName());
        final String bulkToken = new SecureBulkLoadClient(TestReplicaWithCluster.HTU.getConfiguration(), table).prepareBulkLoad(conn);
        ClientServiceCallable<Void> callable = new ClientServiceCallable<Void>(conn, hdt.getTableName(), TestHRegionServerBulkLoad.rowkey(0), newController(), HConstants.PRIORITY_UNSET) {
            @Override
            protected Void rpcCall() throws Exception {
                TestReplicaWithCluster.LOG.debug(((("Going to connect to server " + (getLocation())) + " for row ") + (Bytes.toStringBinary(getRow()))));
                SecureBulkLoadClient secureClient = null;
                byte[] regionName = getLocation().getRegionInfo().getRegionName();
                try (Table table = conn.getTable(getTableName())) {
                    secureClient = new SecureBulkLoadClient(TestReplicaWithCluster.HTU.getConfiguration(), table);
                    secureClient.secureBulkLoadHFiles(getStub(), famPaths, regionName, true, null, bulkToken);
                }
                return null;
            }
        };
        RpcRetryingCallerFactory factory = new RpcRetryingCallerFactory(TestReplicaWithCluster.HTU.getConfiguration());
        RpcRetryingCaller<Void> caller = factory.newCaller();
        caller.callWithRetries(callable, 10000);
        // verify we can read them from the primary
        TestReplicaWithCluster.LOG.debug("Verifying data load");
        for (int i = 0; i < numRows; i++) {
            byte[] row = TestHRegionServerBulkLoad.rowkey(i);
            Get g = new Get(row);
            Result r = table.get(g);
            Assert.assertFalse(r.isStale());
        }
        // verify we can read them from the replica
        TestReplicaWithCluster.LOG.debug("Verifying replica queries");
        try {
            TestReplicaWithCluster.SlowMeCopro.cdl.set(new CountDownLatch(1));
            for (int i = 0; i < numRows; i++) {
                byte[] row = TestHRegionServerBulkLoad.rowkey(i);
                Get g = new Get(row);
                g.setConsistency(TIMELINE);
                Result r = table.get(g);
                Assert.assertTrue(r.isStale());
            }
            TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
        } finally {
            TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
            TestReplicaWithCluster.SlowMeCopro.sleepTime.set(0);
        }
        TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
        TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
    }

    @Test
    public void testReplicaGetWithPrimaryDown() throws IOException {
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testCreateDeleteTable");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        hdt.addCoprocessor(TestReplicaWithCluster.RegionServerStoppedCopro.class.getName());
        try {
            Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
            Put p = new Put(TestReplicaWithCluster.row);
            p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
            table.put(p);
            // Flush so it can be picked by the replica refresher thread
            TestReplicaWithCluster.HTU.flush(table.getName());
            // Sleep for some time until data is picked up by replicas
            try {
                Thread.sleep((2 * (TestReplicaWithCluster.REFRESH_PERIOD)));
            } catch (InterruptedException e1) {
                TestReplicaWithCluster.LOG.error(e1.toString(), e1);
            }
            // But if we ask for stale we will get it
            Get g = new Get(TestReplicaWithCluster.row);
            g.setConsistency(TIMELINE);
            Result r = table.get(g);
            Assert.assertTrue(r.isStale());
        } finally {
            TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
            TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        }
    }

    @Test
    public void testReplicaScanWithPrimaryDown() throws IOException {
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testCreateDeleteTable");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        hdt.addCoprocessor(TestReplicaWithCluster.RegionServerStoppedCopro.class.getName());
        try {
            Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
            Put p = new Put(TestReplicaWithCluster.row);
            p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
            table.put(p);
            // Flush so it can be picked by the replica refresher thread
            TestReplicaWithCluster.HTU.flush(table.getName());
            // Sleep for some time until data is picked up by replicas
            try {
                Thread.sleep((2 * (TestReplicaWithCluster.REFRESH_PERIOD)));
            } catch (InterruptedException e1) {
                TestReplicaWithCluster.LOG.error(e1.toString(), e1);
            }
            // But if we ask for stale we will get it
            // Instantiating the Scan class
            Scan scan = new Scan();
            // Scanning the required columns
            scan.addFamily(TestReplicaWithCluster.f);
            scan.setConsistency(TIMELINE);
            // Getting the scan result
            ResultScanner scanner = table.getScanner(scan);
            Result r = scanner.next();
            Assert.assertTrue(r.isStale());
        } finally {
            TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
            TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        }
    }

    @Test
    public void testReplicaGetWithRpcClientImpl() throws IOException {
        TestReplicaWithCluster.HTU.getConfiguration().setBoolean("hbase.ipc.client.specificThreadForWriting", true);
        TestReplicaWithCluster.HTU.getConfiguration().set("hbase.rpc.client.impl", "org.apache.hadoop.hbase.ipc.RpcClientImpl");
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testReplicaGetWithRpcClientImpl");
        hdt.setRegionReplication(TestReplicaWithCluster.NB_SERVERS);
        hdt.addCoprocessor(TestReplicaWithCluster.SlowMeCopro.class.getName());
        try {
            Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
            Put p = new Put(TestReplicaWithCluster.row);
            p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
            table.put(p);
            // Flush so it can be picked by the replica refresher thread
            TestReplicaWithCluster.HTU.flush(table.getName());
            // Sleep for some time until data is picked up by replicas
            try {
                Thread.sleep((2 * (TestReplicaWithCluster.REFRESH_PERIOD)));
            } catch (InterruptedException e1) {
                TestReplicaWithCluster.LOG.error(e1.toString(), e1);
            }
            try {
                // Create the new connection so new config can kick in
                Connection connection = ConnectionFactory.createConnection(TestReplicaWithCluster.HTU.getConfiguration());
                Table t = connection.getTable(hdt.getTableName());
                // But if we ask for stale we will get it
                TestReplicaWithCluster.SlowMeCopro.cdl.set(new CountDownLatch(1));
                Get g = new Get(TestReplicaWithCluster.row);
                g.setConsistency(TIMELINE);
                Result r = t.get(g);
                Assert.assertTrue(r.isStale());
                TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
            } finally {
                TestReplicaWithCluster.SlowMeCopro.cdl.get().countDown();
                TestReplicaWithCluster.SlowMeCopro.sleepTime.set(0);
            }
        } finally {
            TestReplicaWithCluster.HTU.getConfiguration().unset("hbase.ipc.client.specificThreadForWriting");
            TestReplicaWithCluster.HTU.getConfiguration().unset("hbase.rpc.client.impl");
            TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
            TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        }
    }

    // This test is to test when hbase.client.metaReplicaCallTimeout.scan is configured, meta table
    // scan will always get the result from primary meta region as long as the result is returned
    // within configured hbase.client.metaReplicaCallTimeout.scan from primary meta region.
    @Test
    public void testGetRegionLocationFromPrimaryMetaRegion() throws IOException, InterruptedException {
        TestReplicaWithCluster.HTU.getAdmin().setBalancerRunning(false, true);
        setUseMetaReplicas(true);
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testGetRegionLocationFromPrimaryMetaRegion");
        hdt.setRegionReplication(2);
        try {
            TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
            TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.slowDownPrimaryMetaScan = true;
            // Get user table location, always get it from the primary meta replica
            RegionLocations url = ((ClusterConnection) (TestReplicaWithCluster.HTU.getConnection())).locateRegion(hdt.getTableName(), TestReplicaWithCluster.row, false, false);
        } finally {
            TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.slowDownPrimaryMetaScan = false;
            setUseMetaReplicas(false);
            TestReplicaWithCluster.HTU.getAdmin().setBalancerRunning(true, true);
            TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
            TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        }
    }

    // This test is to simulate the case that the meta region and the primary user region
    // are down, hbase client is able to access user replica regions and return stale data.
    // Meta replica is enabled to show the case that the meta replica region could be out of sync
    // with the primary meta region.
    @Test
    public void testReplicaGetWithPrimaryAndMetaDown() throws IOException, InterruptedException {
        TestReplicaWithCluster.HTU.getAdmin().setBalancerRunning(false, true);
        setUseMetaReplicas(true);
        // Create table then get the single region for our new table.
        HTableDescriptor hdt = TestReplicaWithCluster.HTU.createTableDescriptor("testReplicaGetWithPrimaryAndMetaDown");
        hdt.setRegionReplication(2);
        try {
            Table table = TestReplicaWithCluster.HTU.createTable(hdt, new byte[][]{ TestReplicaWithCluster.f }, null);
            // Get Meta location
            RegionLocations mrl = ((ClusterConnection) (TestReplicaWithCluster.HTU.getConnection())).locateRegion(META_TABLE_NAME, EMPTY_START_ROW, false, false);
            // Get user table location
            RegionLocations url = ((ClusterConnection) (TestReplicaWithCluster.HTU.getConnection())).locateRegion(hdt.getTableName(), TestReplicaWithCluster.row, false, false);
            // Make sure that user primary region is co-hosted with the meta region
            if (!(url.getDefaultRegionLocation().getServerName().equals(mrl.getDefaultRegionLocation().getServerName()))) {
                TestReplicaWithCluster.HTU.moveRegionAndWait(url.getDefaultRegionLocation().getRegionInfo(), mrl.getDefaultRegionLocation().getServerName());
            }
            // Make sure that the user replica region is not hosted by the same region server with
            // primary
            if (url.getRegionLocation(1).getServerName().equals(mrl.getDefaultRegionLocation().getServerName())) {
                TestReplicaWithCluster.HTU.moveRegionAndWait(url.getRegionLocation(1).getRegionInfo(), url.getDefaultRegionLocation().getServerName());
            }
            // Wait until the meta table is updated with new location info
            while (true) {
                mrl = ((ClusterConnection) (TestReplicaWithCluster.HTU.getConnection())).locateRegion(META_TABLE_NAME, EMPTY_START_ROW, false, false);
                // Get user table location
                url = ((ClusterConnection) (TestReplicaWithCluster.HTU.getConnection())).locateRegion(hdt.getTableName(), TestReplicaWithCluster.row, false, true);
                TestReplicaWithCluster.LOG.info(("meta locations " + mrl));
                TestReplicaWithCluster.LOG.info(("table locations " + url));
                ServerName a = url.getDefaultRegionLocation().getServerName();
                ServerName b = mrl.getDefaultRegionLocation().getServerName();
                if (a.equals(b)) {
                    break;
                } else {
                    TestReplicaWithCluster.LOG.info("Waiting for new region info to be updated in meta table");
                    Thread.sleep(100);
                }
            } 
            Put p = new Put(TestReplicaWithCluster.row);
            p.addColumn(TestReplicaWithCluster.f, TestReplicaWithCluster.row, TestReplicaWithCluster.row);
            table.put(p);
            // Flush so it can be picked by the replica refresher thread
            TestReplicaWithCluster.HTU.flush(table.getName());
            // Sleep for some time until data is picked up by replicas
            try {
                Thread.sleep((2 * (TestReplicaWithCluster.REFRESH_PERIOD)));
            } catch (InterruptedException e1) {
                TestReplicaWithCluster.LOG.error(e1.toString(), e1);
            }
            // Simulating the RS down
            TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.throwException = true;
            // The first Get is supposed to succeed
            Get g = new Get(TestReplicaWithCluster.row);
            g.setConsistency(TIMELINE);
            Result r = table.get(g);
            Assert.assertTrue(r.isStale());
            // The second Get will succeed as well
            r = table.get(g);
            Assert.assertTrue(r.isStale());
        } finally {
            setUseMetaReplicas(false);
            TestReplicaWithCluster.RegionServerHostingPrimayMetaRegionSlowOrStopCopro.throwException = false;
            TestReplicaWithCluster.HTU.getAdmin().setBalancerRunning(true, true);
            TestReplicaWithCluster.HTU.getAdmin().disableTable(hdt.getTableName());
            TestReplicaWithCluster.HTU.deleteTable(hdt.getTableName());
        }
    }
}

