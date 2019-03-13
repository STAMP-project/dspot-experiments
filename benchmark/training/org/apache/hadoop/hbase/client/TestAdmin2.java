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


import HConstants.CATALOG_FAMILY;
import HConstants.DEFAULT_HBASE_RPC_TIMEOUT;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import HConstants.ZOOKEEPER_CLIENT_PORT;
import Option.LIVE_SERVERS;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotDisabledException;
import org.apache.hadoop.hbase.TableNotEnabledException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.UnknownRegionException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.constraint.ConstraintException;
import org.apache.hadoop.hbase.ipc.HBaseRpcController;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.assignment.AssignmentManager;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.AbstractFSWALProvider;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to test HBaseAdmin.
 * Spins up the minicluster once at test start and then takes it down afterward.
 * Add any testing of HBaseAdmin functionality here.
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestAdmin2 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAdmin2.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAdmin2.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Admin admin;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testCreateBadTables() throws IOException {
        String msg = null;
        try {
            this.admin.createTable(new HTableDescriptor(TableName.META_TABLE_NAME));
        } catch (TableExistsException e) {
            msg = e.toString();
        }
        Assert.assertTrue(("Unexcepted exception message " + msg), (((msg != null) && (msg.startsWith(TableExistsException.class.getName()))) && (msg.contains(META_TABLE_NAME.getNameAsString()))));
        // Now try and do concurrent creation with a bunch of threads.
        final HTableDescriptor threadDesc = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        threadDesc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        int count = 10;
        Thread[] threads = new Thread[count];
        final AtomicInteger successes = new AtomicInteger(0);
        final AtomicInteger failures = new AtomicInteger(0);
        final Admin localAdmin = this.admin;
        for (int i = 0; i < count; i++) {
            threads[i] = new Thread(Integer.toString(i)) {
                @Override
                public void run() {
                    try {
                        localAdmin.createTable(threadDesc);
                        successes.incrementAndGet();
                    } catch (TableExistsException e) {
                        failures.incrementAndGet();
                    } catch (IOException e) {
                        throw new RuntimeException(("Failed threaded create" + (getName())), e);
                    }
                }
            };
        }
        for (int i = 0; i < count; i++) {
            threads[i].start();
        }
        for (int i = 0; i < count; i++) {
            while (threads[i].isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // continue
                }
            } 
        }
        // All threads are now dead.  Count up how many tables were created and
        // how many failed w/ appropriate exception.
        Assert.assertEquals(1, successes.get());
        Assert.assertEquals((count - 1), failures.get());
    }

    /**
     * Test for hadoop-1581 'HBASE: Unopenable tablename bug'.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTableNameClash() throws Exception {
        final String name = this.name.getMethodName();
        HTableDescriptor htd1 = new HTableDescriptor(TableName.valueOf((name + "SOMEUPPERCASE")));
        HTableDescriptor htd2 = new HTableDescriptor(TableName.valueOf(name));
        htd1.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        htd2.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(htd1);
        admin.createTable(htd2);
        // Before fix, below would fail throwing a NoServerForRegionException.
        TestAdmin2.TEST_UTIL.getConnection().getTable(htd2.getTableName()).close();
    }

    /**
     * *
     * HMaster.createTable used to be kind of synchronous call
     * Thus creating of table with lots of regions can cause RPC timeout
     * After the fix to make createTable truly async, RPC timeout shouldn't be an
     * issue anymore
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCreateTableRPCTimeOut() throws Exception {
        final String name = this.name.getMethodName();
        int oldTimeout = TestAdmin2.TEST_UTIL.getConfiguration().getInt(HBASE_RPC_TIMEOUT_KEY, DEFAULT_HBASE_RPC_TIMEOUT);
        TestAdmin2.TEST_UTIL.getConfiguration().setInt(HBASE_RPC_TIMEOUT_KEY, 1500);
        try {
            int expectedRegions = 100;
            // Use 80 bit numbers to make sure we aren't limited
            byte[] startKey = new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
            byte[] endKey = new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
            Admin hbaseadmin = TestAdmin2.TEST_UTIL.getHBaseAdmin();
            HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name));
            htd.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
            hbaseadmin.createTable(htd, startKey, endKey, expectedRegions);
        } finally {
            TestAdmin2.TEST_UTIL.getConfiguration().setInt(HBASE_RPC_TIMEOUT_KEY, oldTimeout);
        }
    }

    /**
     * Test read only tables
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testReadOnlyTable() throws Exception {
        final TableName name = TableName.valueOf(this.name.getMethodName());
        Table table = TestAdmin2.TEST_UTIL.createTable(name, CATALOG_FAMILY);
        byte[] value = Bytes.toBytes("somedata");
        // This used to use an empty row... That must have been a bug
        Put put = new Put(value);
        put.addColumn(CATALOG_FAMILY, CATALOG_FAMILY, value);
        table.put(put);
        table.close();
    }

    /**
     * Test that user table names can contain '-' and '.' so long as they do not
     * start with same. HBASE-771
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTableNames() throws IOException {
        byte[][] illegalNames = new byte[][]{ Bytes.toBytes("-bad"), Bytes.toBytes(".bad") };
        for (byte[] illegalName : illegalNames) {
            try {
                new HTableDescriptor(TableName.valueOf(illegalName));
                throw new IOException((("Did not detect '" + (Bytes.toString(illegalName))) + "' as an illegal user table name"));
            } catch (IllegalArgumentException e) {
                // expected
            }
        }
        byte[] legalName = Bytes.toBytes("g-oo.d");
        try {
            new HTableDescriptor(TableName.valueOf(legalName));
        } catch (IllegalArgumentException e) {
            throw new IOException(((("Legal user table name: '" + (Bytes.toString(legalName))) + "' caused IllegalArgumentException: ") + (e.getMessage())));
        }
    }

    /**
     * For HADOOP-2579
     *
     * @throws IOException
     * 		
     */
    @Test(expected = TableExistsException.class)
    public void testTableExistsExceptionWithATable() throws IOException {
        final TableName name = TableName.valueOf(this.name.getMethodName());
        TestAdmin2.TEST_UTIL.createTable(name, CATALOG_FAMILY).close();
        TestAdmin2.TEST_UTIL.createTable(name, CATALOG_FAMILY);
    }

    /**
     * Can't disable a table if the table isn't in enabled state
     *
     * @throws IOException
     * 		
     */
    @Test(expected = TableNotEnabledException.class)
    public void testTableNotEnabledExceptionWithATable() throws IOException {
        final TableName name = TableName.valueOf(this.name.getMethodName());
        TestAdmin2.TEST_UTIL.createTable(name, CATALOG_FAMILY).close();
        this.admin.disableTable(name);
        this.admin.disableTable(name);
    }

    /**
     * Can't enable a table if the table isn't in disabled state
     *
     * @throws IOException
     * 		
     */
    @Test(expected = TableNotDisabledException.class)
    public void testTableNotDisabledExceptionWithATable() throws IOException {
        final TableName name = TableName.valueOf(this.name.getMethodName());
        Table t = TestAdmin2.TEST_UTIL.createTable(name, CATALOG_FAMILY);
        try {
            this.admin.enableTable(name);
        } finally {
            t.close();
        }
    }

    /**
     * For HADOOP-2579
     *
     * @throws IOException
     * 		
     */
    @Test(expected = TableNotFoundException.class)
    public void testTableNotFoundExceptionWithoutAnyTables() throws IOException {
        TableName tableName = TableName.valueOf("testTableNotFoundExceptionWithoutAnyTables");
        Table ht = TestAdmin2.TEST_UTIL.getConnection().getTable(tableName);
        ht.get(new Get(Bytes.toBytes("e")));
    }

    @Test
    public void testShouldUnassignTheRegion() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        createTableWithDefaultConf(tableName);
        RegionInfo info = null;
        HRegionServer rs = TestAdmin2.TEST_UTIL.getRSForFirstRegionInTable(tableName);
        List<RegionInfo> onlineRegions = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices());
        for (RegionInfo regionInfo : onlineRegions) {
            if (!(regionInfo.getTable().isSystemTable())) {
                info = regionInfo;
                admin.unassign(regionInfo.getRegionName(), true);
            }
        }
        boolean isInList = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices()).contains(info);
        long timeout = (System.currentTimeMillis()) + 10000;
        while (((System.currentTimeMillis()) < timeout) && isInList) {
            Thread.sleep(100);
            isInList = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices()).contains(info);
        } 
        Assert.assertFalse("The region should not be present in online regions list.", isInList);
    }

    @Test
    public void testCloseRegionIfInvalidRegionNameIsPassed() throws Exception {
        final String name = this.name.getMethodName();
        byte[] tableName = Bytes.toBytes(name);
        createTableWithDefaultConf(tableName);
        RegionInfo info = null;
        HRegionServer rs = TestAdmin2.TEST_UTIL.getRSForFirstRegionInTable(TableName.valueOf(tableName));
        List<RegionInfo> onlineRegions = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices());
        for (RegionInfo regionInfo : onlineRegions) {
            if (!(regionInfo.isMetaRegion())) {
                if (regionInfo.getRegionNameAsString().contains(name)) {
                    info = regionInfo;
                    try {
                        admin.unassign(Bytes.toBytes("sample"), true);
                    } catch (UnknownRegionException nsre) {
                        // expected, ignore it
                    }
                }
            }
        }
        onlineRegions = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices());
        Assert.assertTrue("The region should be present in online regions list.", onlineRegions.contains(info));
    }

    @Test
    public void testCloseRegionThatFetchesTheHRIFromMeta() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        createTableWithDefaultConf(tableName);
        RegionInfo info = null;
        HRegionServer rs = TestAdmin2.TEST_UTIL.getRSForFirstRegionInTable(tableName);
        List<RegionInfo> onlineRegions = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices());
        for (RegionInfo regionInfo : onlineRegions) {
            if (!(regionInfo.isMetaRegion())) {
                if (regionInfo.getRegionNameAsString().contains("TestHBACloseRegion2")) {
                    info = regionInfo;
                    admin.unassign(regionInfo.getRegionName(), true);
                }
            }
        }
        boolean isInList = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices()).contains(info);
        long timeout = (System.currentTimeMillis()) + 10000;
        while (((System.currentTimeMillis()) < timeout) && isInList) {
            Thread.sleep(100);
            isInList = ProtobufUtil.getOnlineRegions(rs.getRSRpcServices()).contains(info);
        } 
        Assert.assertFalse("The region should not be present in online regions list.", isInList);
    }

    /**
     * For HBASE-2556
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testGetTableRegions() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        int expectedRegions = 10;
        // Use 80 bit numbers to make sure we aren't limited
        byte[] startKey = new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        byte[] endKey = new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, startKey, endKey, expectedRegions);
        List<RegionInfo> RegionInfos = admin.getRegions(tableName);
        Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (RegionInfos.size())), expectedRegions, RegionInfos.size());
    }

    @Test
    public void testMoveToPreviouslyAssignedRS() throws IOException, InterruptedException {
        MiniHBaseCluster cluster = TestAdmin2.TEST_UTIL.getHBaseCluster();
        HMaster master = cluster.getMaster();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Admin localAdmin = createTable(tableName);
        List<RegionInfo> tableRegions = localAdmin.getRegions(tableName);
        RegionInfo hri = tableRegions.get(0);
        AssignmentManager am = master.getAssignmentManager();
        ServerName server = am.getRegionStates().getRegionServerOfRegion(hri);
        localAdmin.move(hri.getEncodedNameAsBytes(), Bytes.toBytes(server.getServerName()));
        Assert.assertEquals("Current region server and region server before move should be same.", server, am.getRegionStates().getRegionServerOfRegion(hri));
    }

    @Test
    public void testWALRollWriting() throws Exception {
        setUpforLogRolling();
        String className = this.getClass().getName();
        StringBuilder v = new StringBuilder(className);
        while ((v.length()) < 1000) {
            v.append(className);
        } 
        byte[] value = Bytes.toBytes(v.toString());
        HRegionServer regionServer = startAndWriteData(TableName.valueOf(name.getMethodName()), value);
        TestAdmin2.LOG.info((("after writing there are " + (AbstractFSWALProvider.getNumRolledLogFiles(regionServer.getWAL(null)))) + " log files"));
        // flush all regions
        for (HRegion r : regionServer.getOnlineRegionsLocalContext()) {
            r.flush(true);
        }
        admin.rollWALWriter(regionServer.getServerName());
        int count = AbstractFSWALProvider.getNumRolledLogFiles(regionServer.getWAL(null));
        TestAdmin2.LOG.info((("after flushing all regions and rolling logs there are " + count) + " log files"));
        Assert.assertTrue(("actual count: " + count), (count <= 2));
    }

    /**
     * Check that we have an exception if the cluster is not there.
     */
    @Test
    public void testCheckHBaseAvailableWithoutCluster() {
        Configuration conf = new Configuration(TestAdmin2.TEST_UTIL.getConfiguration());
        // Change the ZK address to go to something not used.
        conf.setInt(ZOOKEEPER_CLIENT_PORT, ((conf.getInt(ZOOKEEPER_CLIENT_PORT, 9999)) + 10));
        long start = System.currentTimeMillis();
        try {
            HBaseAdmin.available(conf);
            Assert.assertTrue(false);
        } catch (ZooKeeperConnectionException ignored) {
        } catch (IOException ignored) {
        }
        long end = System.currentTimeMillis();
        TestAdmin2.LOG.info(((("It took " + (end - start)) + " ms to find out that") + " HBase was not available"));
    }

    @Test
    public void testDisableCatalogTable() throws Exception {
        try {
            this.admin.disableTable(META_TABLE_NAME);
            Assert.fail("Expected to throw ConstraintException");
        } catch (ConstraintException e) {
        }
        // Before the fix for HBASE-6146, the below table creation was failing as the hbase:meta table
        // actually getting disabled by the disableTable() call.
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(Bytes.toBytes(name.getMethodName())));
        HColumnDescriptor hcd = new HColumnDescriptor(Bytes.toBytes("cf1"));
        htd.addFamily(hcd);
        TestAdmin2.TEST_UTIL.getHBaseAdmin().createTable(htd);
    }

    @Test
    public void testIsEnabledOrDisabledOnUnknownTable() throws Exception {
        try {
            admin.isTableEnabled(TableName.valueOf(name.getMethodName()));
            Assert.fail("Test should fail if isTableEnabled called on unknown table.");
        } catch (IOException e) {
        }
        try {
            admin.isTableDisabled(TableName.valueOf(name.getMethodName()));
            Assert.fail("Test should fail if isTableDisabled called on unknown table.");
        } catch (IOException e) {
        }
    }

    @Test
    public void testGetRegion() throws Exception {
        // We use actual HBaseAdmin instance instead of going via Admin interface in
        // here because makes use of an internal HBA method (TODO: Fix.).
        HBaseAdmin rawAdmin = TestAdmin2.TEST_UTIL.getHBaseAdmin();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin2.LOG.info(("Started " + tableName));
        Table t = TestAdmin2.TEST_UTIL.createMultiRegionTable(tableName, CATALOG_FAMILY);
        try (RegionLocator locator = TestAdmin2.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            HRegionLocation regionLocation = locator.getRegionLocation(Bytes.toBytes("mmm"));
            RegionInfo region = regionLocation.getRegionInfo();
            byte[] regionName = region.getRegionName();
            Pair<RegionInfo, ServerName> pair = rawAdmin.getRegion(regionName);
            Assert.assertTrue(Bytes.equals(regionName, pair.getFirst().getRegionName()));
            pair = rawAdmin.getRegion(region.getEncodedNameAsBytes());
            Assert.assertTrue(Bytes.equals(regionName, pair.getFirst().getRegionName()));
        }
    }

    @Test
    public void testBalancer() throws Exception {
        boolean initialState = admin.isBalancerEnabled();
        // Start the balancer, wait for it.
        boolean prevState = admin.setBalancerRunning((!initialState), true);
        // The previous state should be the original state we observed
        Assert.assertEquals(initialState, prevState);
        // Current state should be opposite of the original
        Assert.assertEquals((!initialState), admin.isBalancerEnabled());
        // Reset it back to what it was
        prevState = admin.setBalancerRunning(initialState, true);
        // The previous state should be the opposite of the initial state
        Assert.assertEquals((!initialState), prevState);
        // Current state should be the original state again
        Assert.assertEquals(initialState, admin.isBalancerEnabled());
    }

    @Test
    public void testRegionNormalizer() throws Exception {
        boolean initialState = admin.isNormalizerEnabled();
        // flip state
        boolean prevState = admin.setNormalizerRunning((!initialState));
        // The previous state should be the original state we observed
        Assert.assertEquals(initialState, prevState);
        // Current state should be opposite of the original
        Assert.assertEquals((!initialState), admin.isNormalizerEnabled());
        // Reset it back to what it was
        prevState = admin.setNormalizerRunning(initialState);
        // The previous state should be the opposite of the initial state
        Assert.assertEquals((!initialState), prevState);
        // Current state should be the original state again
        Assert.assertEquals(initialState, admin.isNormalizerEnabled());
    }

    @Test
    public void testAbortProcedureFail() throws Exception {
        Random randomGenerator = new Random();
        long procId = randomGenerator.nextLong();
        boolean abortResult = admin.abortProcedure(procId, true);
        Assert.assertFalse(abortResult);
    }

    @Test
    public void testGetProcedures() throws Exception {
        String procList = admin.getProcedures();
        Assert.assertTrue(procList.startsWith("["));
    }

    @Test
    public void testGetLocks() throws Exception {
        String lockList = admin.getLocks();
        Assert.assertTrue(lockList.startsWith("["));
    }

    @Test
    public void testDecommissionRegionServers() throws Exception {
        List<ServerName> decommissionedRegionServers = admin.listDecommissionedRegionServers();
        Assert.assertTrue(decommissionedRegionServers.isEmpty());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin2.TEST_UTIL.createMultiRegionTable(tableName, Bytes.toBytes("f"), 6);
        ArrayList<ServerName> clusterRegionServers = new ArrayList(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet());
        Assert.assertEquals(3, clusterRegionServers.size());
        HashMap<ServerName, List<RegionInfo>> serversToDecommssion = new HashMap<>();
        // Get a server that has meta online. We will decommission two of the servers,
        // leaving one online.
        int i;
        for (i = 0; i < (clusterRegionServers.size()); i++) {
            List<RegionInfo> regionsOnServer = admin.getRegions(clusterRegionServers.get(i));
            if (admin.getRegions(clusterRegionServers.get(i)).stream().anyMatch(( p) -> p.isMetaRegion())) {
                serversToDecommssion.put(clusterRegionServers.get(i), regionsOnServer);
                break;
            }
        }
        clusterRegionServers.remove(i);
        // Get another server to decommission.
        serversToDecommssion.put(clusterRegionServers.get(0), admin.getRegions(clusterRegionServers.get(0)));
        ServerName remainingServer = clusterRegionServers.get(1);
        // Decommission
        admin.decommissionRegionServers(new ArrayList<ServerName>(serversToDecommssion.keySet()), true);
        Assert.assertEquals(2, admin.listDecommissionedRegionServers().size());
        // Verify the regions have been off the decommissioned servers, all on the one
        // remaining server.
        for (ServerName server : serversToDecommssion.keySet()) {
            for (RegionInfo region : serversToDecommssion.get(server)) {
                TestAdmin2.TEST_UTIL.assertRegionOnServer(region, remainingServer, 10000);
            }
        }
        // Recommission and load the regions.
        for (ServerName server : serversToDecommssion.keySet()) {
            List<byte[]> encodedRegionNames = serversToDecommssion.get(server).stream().map(( region) -> region.getEncodedNameAsBytes()).collect(Collectors.toList());
            admin.recommissionRegionServer(server, encodedRegionNames);
        }
        Assert.assertTrue(admin.listDecommissionedRegionServers().isEmpty());
        // Verify the regions have been moved to the recommissioned servers
        for (ServerName server : serversToDecommssion.keySet()) {
            for (RegionInfo region : serversToDecommssion.get(server)) {
                TestAdmin2.TEST_UTIL.assertRegionOnServer(region, server, 10000);
            }
        }
    }

    /**
     * TestCase for HBASE-21355
     */
    @Test
    public void testGetRegionInfo() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestAdmin2.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"));
        for (int i = 0; i < 100; i++) {
            table.put(new Put(Bytes.toBytes(i)).addColumn(Bytes.toBytes("f"), Bytes.toBytes("q"), Bytes.toBytes(i)));
        }
        admin.flush(tableName);
        HRegionServer rs = TestAdmin2.TEST_UTIL.getRSForFirstRegionInTable(table.getName());
        List<HRegion> regions = rs.getRegions(tableName);
        Assert.assertEquals(1, regions.size());
        HRegion region = regions.get(0);
        byte[] regionName = region.getRegionInfo().getRegionName();
        HStore store = region.getStore(Bytes.toBytes("f"));
        long expectedStoreFilesSize = store.getStorefilesSize();
        Assert.assertNotNull(store);
        Assert.assertEquals(expectedStoreFilesSize, store.getSize());
        ClusterConnection conn = ((ClusterConnection) (admin.getConnection()));
        HBaseRpcController controller = conn.getRpcControllerFactory().newController();
        for (int i = 0; i < 10; i++) {
            RegionInfo ri = ProtobufUtil.getRegionInfo(controller, conn.getAdmin(rs.getServerName()), regionName);
            Assert.assertEquals(region.getRegionInfo(), ri);
            // Make sure that the store size is still the actual file system's store size.
            Assert.assertEquals(expectedStoreFilesSize, store.getSize());
        }
    }

    @Test
    public void testTableSplitFollowedByModify() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin2.TEST_UTIL.createTable(tableName, Bytes.toBytes("f"));
        // get the original table region count
        List<RegionInfo> regions = admin.getRegions(tableName);
        int originalCount = regions.size();
        Assert.assertEquals(1, originalCount);
        // split the table and wait until region count increases
        admin.split(tableName, Bytes.toBytes(3));
        TestAdmin2.TEST_UTIL.waitFor(30000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (admin.getRegions(tableName).size()) > originalCount;
            }
        });
        // do some table modification
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(admin.getDescriptor(tableName)).setMaxFileSize(11111111).build();
        admin.modifyTable(tableDesc);
        Assert.assertEquals(11111111, admin.getDescriptor(tableName).getMaxFileSize());
    }

    @Test
    public void testTableMergeFollowedByModify() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin2.TEST_UTIL.createTable(tableName, new byte[][]{ Bytes.toBytes("f") }, new byte[][]{ Bytes.toBytes(3) });
        // assert we have at least 2 regions in the table
        List<RegionInfo> regions = admin.getRegions(tableName);
        int originalCount = regions.size();
        Assert.assertTrue((originalCount >= 2));
        byte[] nameOfRegionA = regions.get(0).getEncodedNameAsBytes();
        byte[] nameOfRegionB = regions.get(1).getEncodedNameAsBytes();
        // merge the table regions and wait until region count decreases
        admin.mergeRegionsAsync(nameOfRegionA, nameOfRegionB, true);
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (admin.getRegions(tableName).size()) < originalCount;
            }
        });
        // do some table modification
        TableDescriptor tableDesc = TableDescriptorBuilder.newBuilder(admin.getDescriptor(tableName)).setMaxFileSize(11111111).build();
        admin.modifyTable(tableDesc);
        Assert.assertEquals(11111111, admin.getDescriptor(tableName).getMaxFileSize());
    }
}

