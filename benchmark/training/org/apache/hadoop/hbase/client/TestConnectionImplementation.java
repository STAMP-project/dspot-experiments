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


import Bytes.BYTES_COMPARATOR;
import ConnectionImplementation.ServerErrorTracker;
import HConstants.HBASE_CLIENT_INSTANCE_ID;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import HConstants.LATEST_TIMESTAMP;
import HConstants.ZOOKEEPER_CLIENT_PORT;
import HConstants.ZOOKEEPER_QUORUM;
import RpcClient.IDLE_TIME;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.exceptions.ClientExceptionsUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.exceptions.RegionMovedException;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.Region;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.ManualEnvironmentEdge;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ReturnCode.INCLUDE;


/**
 * This class is for testing HBaseConnectionManager features
 */
@Category({ LargeTests.class })
public class TestConnectionImplementation {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestConnectionImplementation.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestConnectionImplementation.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("test");

    private static final TableName TABLE_NAME1 = TableName.valueOf("test1");

    private static final TableName TABLE_NAME2 = TableName.valueOf("test2");

    private static final TableName TABLE_NAME3 = TableName.valueOf("test3");

    private static final byte[] FAM_NAM = Bytes.toBytes("f");

    private static final byte[] ROW = Bytes.toBytes("bbb");

    private static final byte[] ROW_X = Bytes.toBytes("xxx");

    private static final int RPC_RETRY = 5;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testClusterConnection() throws IOException {
        ThreadPoolExecutor otherPool = new ThreadPoolExecutor(1, 1, 5, TimeUnit.SECONDS, new SynchronousQueue(), Threads.newDaemonThreadFactory("test-hcm"));
        Connection con1 = ConnectionFactory.createConnection(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        Connection con2 = ConnectionFactory.createConnection(TestConnectionImplementation.TEST_UTIL.getConfiguration(), otherPool);
        // make sure the internally created ExecutorService is the one passed
        Assert.assertTrue((otherPool == (getCurrentBatchPool())));
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestConnectionImplementation.TEST_UTIL.createTable(tableName, TestConnectionImplementation.FAM_NAM).close();
        Table table = con1.getTable(tableName, otherPool);
        ExecutorService pool = null;
        if (table instanceof HTable) {
            HTable t = ((HTable) (table));
            // make sure passing a pool to the getTable does not trigger creation of an internal pool
            Assert.assertNull("Internal Thread pool should be null", getCurrentBatchPool());
            // table should use the pool passed
            Assert.assertTrue((otherPool == (t.getPool())));
            t.close();
            t = ((HTable) (con2.getTable(tableName)));
            // table should use the connectin's internal pool
            Assert.assertTrue((otherPool == (t.getPool())));
            t.close();
            t = ((HTable) (con2.getTable(tableName)));
            // try other API too
            Assert.assertTrue((otherPool == (t.getPool())));
            t.close();
            t = ((HTable) (con2.getTable(tableName)));
            // try other API too
            Assert.assertTrue((otherPool == (t.getPool())));
            t.close();
            t = ((HTable) (con1.getTable(tableName)));
            pool = ((ConnectionImplementation) (con1)).getCurrentBatchPool();
            // make sure an internal pool was created
            Assert.assertNotNull("An internal Thread pool should have been created", pool);
            // and that the table is using it
            Assert.assertTrue(((t.getPool()) == pool));
            t.close();
            t = ((HTable) (con1.getTable(tableName)));
            // still using the *same* internal pool
            Assert.assertTrue(((t.getPool()) == pool));
            t.close();
        } else {
            table.close();
        }
        con1.close();
        // if the pool was created on demand it should be closed upon connection close
        if (pool != null) {
            Assert.assertTrue(pool.isShutdown());
        }
        con2.close();
        // if the pool is passed, it is not closed
        Assert.assertFalse(otherPool.isShutdown());
        otherPool.shutdownNow();
    }

    /**
     * Naive test to check that Connection#getAdmin returns a properly constructed HBaseAdmin object
     *
     * @throws IOException
     * 		Unable to construct admin
     */
    @Test
    public void testAdminFactory() throws IOException {
        Connection con1 = ConnectionFactory.createConnection(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        Admin admin = con1.getAdmin();
        Assert.assertTrue(((admin.getConnection()) == con1));
        Assert.assertTrue(((admin.getConfiguration()) == (TestConnectionImplementation.TEST_UTIL.getConfiguration())));
        con1.close();
    }

    /**
     * Test that we can handle connection close: it will trigger a retry, but the calls will finish.
     */
    @Test
    public void testConnectionCloseAllowsInterrupt() throws Exception {
        testConnectionClose(true);
    }

    @Test
    public void testConnectionNotAllowsInterrupt() throws Exception {
        testConnectionClose(false);
    }

    /**
     * Test that connection can become idle without breaking everything.
     */
    @Test
    public void testConnectionIdle() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestConnectionImplementation.TEST_UTIL.createTable(tableName, TestConnectionImplementation.FAM_NAM).close();
        int idleTime = 20000;
        boolean previousBalance = TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(false, true);
        Configuration c2 = new Configuration(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        // We want to work on a separate connection.
        c2.set(HBASE_CLIENT_INSTANCE_ID, String.valueOf((-1)));
        c2.setInt(HBASE_CLIENT_RETRIES_NUMBER, 1);// Don't retry: retry = test failed

        c2.setInt(IDLE_TIME, idleTime);
        Connection connection = ConnectionFactory.createConnection(c2);
        final Table table = connection.getTable(tableName);
        Put put = new Put(TestConnectionImplementation.ROW);
        put.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW, TestConnectionImplementation.ROW);
        table.put(put);
        ManualEnvironmentEdge mee = new ManualEnvironmentEdge();
        mee.setValue(System.currentTimeMillis());
        EnvironmentEdgeManager.injectEdge(mee);
        TestConnectionImplementation.LOG.info("first get");
        table.get(new Get(TestConnectionImplementation.ROW));
        TestConnectionImplementation.LOG.info("first get - changing the time & sleeping");
        mee.incValue((idleTime + 1000));
        Thread.sleep(1500);// we need to wait a little for the connection to be seen as idle.

        // 1500 = sleep time in RpcClient#waitForWork + a margin
        TestConnectionImplementation.LOG.info("second get - connection has been marked idle in the middle");
        // To check that the connection actually became idle would need to read some private
        // fields of RpcClient.
        table.get(new Get(TestConnectionImplementation.ROW));
        mee.incValue((idleTime + 1000));
        TestConnectionImplementation.LOG.info("third get - connection is idle, but the reader doesn't know yet");
        // We're testing here a special case:
        // time limit reached BUT connection not yet reclaimed AND a new call.
        // in this situation, we don't close the connection, instead we use it immediately.
        // If we're very unlucky we can have a race condition in the test: the connection is already
        // under closing when we do the get, so we have an exception, and we don't retry as the
        // retry number is 1. The probability is very very low, and seems acceptable for now. It's
        // a test issue only.
        table.get(new Get(TestConnectionImplementation.ROW));
        TestConnectionImplementation.LOG.info("we're done - time will change back");
        table.close();
        connection.close();
        EnvironmentEdgeManager.reset();
        TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(previousBalance, true);
    }

    /**
     * Test that the connection to the dead server is cut immediately when we receive the
     *  notification.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionCut() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestConnectionImplementation.TEST_UTIL.createTable(tableName, TestConnectionImplementation.FAM_NAM).close();
        boolean previousBalance = TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(false, true);
        Configuration c2 = new Configuration(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        // We want to work on a separate connection.
        c2.set(HBASE_CLIENT_INSTANCE_ID, String.valueOf((-1)));
        // try only once w/o any retry
        c2.setInt(HBASE_CLIENT_RETRIES_NUMBER, 0);
        c2.setInt(HBASE_RPC_TIMEOUT_KEY, (30 * 1000));
        final Connection connection = ConnectionFactory.createConnection(c2);
        final Table table = connection.getTable(tableName);
        Put p = new Put(TestConnectionImplementation.FAM_NAM);
        p.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.FAM_NAM);
        table.put(p);
        final ConnectionImplementation hci = ((ConnectionImplementation) (connection));
        final HRegionLocation loc;
        try (RegionLocator rl = connection.getRegionLocator(tableName)) {
            loc = rl.getRegionLocation(TestConnectionImplementation.FAM_NAM);
        }
        Get get = new Get(TestConnectionImplementation.FAM_NAM);
        Assert.assertNotNull(table.get(get));
        get = new Get(TestConnectionImplementation.FAM_NAM);
        get.setFilter(new TestConnectionImplementation.BlockingFilter());
        // This thread will mark the server as dead while we're waiting during a get.
        Thread t = new Thread() {
            @Override
            public void run() {
                synchronized(TestConnectionImplementation.syncBlockingFilter) {
                    try {
                        TestConnectionImplementation.syncBlockingFilter.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                hci.clusterStatusListener.deadServerHandler.newDead(loc.getServerName());
            }
        };
        t.start();
        try {
            table.get(get);
            Assert.fail();
        } catch (IOException expected) {
            TestConnectionImplementation.LOG.debug(("Received: " + expected));
            Assert.assertFalse((expected instanceof SocketTimeoutException));
            Assert.assertFalse(TestConnectionImplementation.syncBlockingFilter.get());
        } finally {
            TestConnectionImplementation.syncBlockingFilter.set(true);
            t.join();
            TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(previousBalance, true);
        }
        table.close();
        connection.close();
    }

    protected static final AtomicBoolean syncBlockingFilter = new AtomicBoolean(false);

    public static class BlockingFilter extends FilterBase {
        @Override
        public boolean filterRowKey(byte[] buffer, int offset, int length) throws IOException {
            int i = 0;
            while (((i++) < 1000) && (!(TestConnectionImplementation.syncBlockingFilter.get()))) {
                synchronized(TestConnectionImplementation.syncBlockingFilter) {
                    TestConnectionImplementation.syncBlockingFilter.notifyAll();
                }
                Threads.sleep(100);
            } 
            TestConnectionImplementation.syncBlockingFilter.set(true);
            return false;
        }

        @Override
        public ReturnCode filterCell(final Cell ignored) throws IOException {
            return INCLUDE;
        }

        public static Filter parseFrom(final byte[] pbBytes) throws DeserializationException {
            return new TestConnectionImplementation.BlockingFilter();
        }
    }

    /**
     * Test that when we delete a location using the first row of a region
     * that we really delete it.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRegionCaching() throws Exception {
        TestConnectionImplementation.TEST_UTIL.createMultiRegionTable(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.FAM_NAM).close();
        Configuration conf = new Configuration(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        // test with no retry, or client cache will get updated after the first failure
        conf.setInt(HBASE_CLIENT_RETRIES_NUMBER, 0);
        Connection connection = ConnectionFactory.createConnection(conf);
        final Table table = connection.getTable(TestConnectionImplementation.TABLE_NAME);
        TestConnectionImplementation.TEST_UTIL.waitUntilAllRegionsAssigned(table.getName());
        Put put = new Put(TestConnectionImplementation.ROW);
        put.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW, TestConnectionImplementation.ROW);
        table.put(put);
        ConnectionImplementation conn = ((ConnectionImplementation) (connection));
        Assert.assertNotNull(conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW));
        // Here we mess with the cached location making it so the region at TABLE_NAME, ROW is at
        // a location where the port is current port number +1 -- i.e. a non-existent location.
        HRegionLocation loc = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation();
        final int nextPort = (loc.getPort()) + 1;
        conn.updateCachedLocation(loc.getRegionInfo(), loc.getServerName(), ServerName.valueOf("127.0.0.1", nextPort, LATEST_TIMESTAMP), LATEST_TIMESTAMP);
        Assert.assertEquals(conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation().getPort(), nextPort);
        conn.clearRegionCache(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW.clone());
        RegionLocations rl = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW);
        Assert.assertNull(("What is this location?? " + rl), rl);
        // We're now going to move the region and check that it works for the client
        // First a new put to add the location in the cache
        conn.clearRegionCache(TestConnectionImplementation.TABLE_NAME);
        Assert.assertEquals(0, conn.getNumberOfCachedRegionLocations(TestConnectionImplementation.TABLE_NAME));
        Put put2 = new Put(TestConnectionImplementation.ROW);
        put2.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW, TestConnectionImplementation.ROW);
        table.put(put2);
        Assert.assertNotNull(conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW));
        Assert.assertNotNull(conn.getCachedLocation(TableName.valueOf(TestConnectionImplementation.TABLE_NAME.getName()), TestConnectionImplementation.ROW.clone()));
        TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(false, false);
        HMaster master = TestConnectionImplementation.TEST_UTIL.getMiniHBaseCluster().getMaster();
        // We can wait for all regions to be online, that makes log reading easier when debugging
        TestConnectionImplementation.TEST_UTIL.waitUntilNoRegionsInTransition();
        // Now moving the region to the second server
        HRegionLocation toMove = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation();
        byte[] regionName = toMove.getRegionInfo().getRegionName();
        byte[] encodedRegionNameBytes = toMove.getRegionInfo().getEncodedNameAsBytes();
        // Choose the other server.
        int curServerId = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getServerWith(regionName);
        int destServerId = (curServerId == 0) ? 1 : 0;
        HRegionServer curServer = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getRegionServer(curServerId);
        HRegionServer destServer = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getRegionServer(destServerId);
        ServerName destServerName = destServer.getServerName();
        // Check that we are in the expected state
        Assert.assertTrue((curServer != destServer));
        Assert.assertFalse(curServer.getServerName().equals(destServer.getServerName()));
        Assert.assertFalse(((toMove.getPort()) == (destServerName.getPort())));
        Assert.assertNotNull(curServer.getOnlineRegion(regionName));
        Assert.assertNull(destServer.getOnlineRegion(regionName));
        Assert.assertFalse(TestConnectionImplementation.TEST_UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager().hasRegionsInTransition());
        // Moving. It's possible that we don't have all the regions online at this point, so
        // the test must depend only on the region we're looking at.
        TestConnectionImplementation.LOG.info(("Move starting region=" + (toMove.getRegionInfo().getRegionNameAsString())));
        TestConnectionImplementation.TEST_UTIL.getAdmin().move(toMove.getRegionInfo().getEncodedNameAsBytes(), Bytes.toBytes(destServerName.getServerName()));
        while (((((destServer.getOnlineRegion(regionName)) == null) || (destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (master.getAssignmentManager().hasRegionsInTransition())) {
            // wait for the move to be finished
            Thread.sleep(1);
        } 
        TestConnectionImplementation.LOG.info(("Move finished for region=" + (toMove.getRegionInfo().getRegionNameAsString())));
        // Check our new state.
        Assert.assertNull(curServer.getOnlineRegion(regionName));
        Assert.assertNotNull(destServer.getOnlineRegion(regionName));
        Assert.assertFalse(destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
        Assert.assertFalse(curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
        // Cache was NOT updated and points to the wrong server
        Assert.assertFalse(((conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation().getPort()) == (destServerName.getPort())));
        // This part relies on a number of tries equals to 1.
        // We do a put and expect the cache to be updated, even if we don't retry
        TestConnectionImplementation.LOG.info("Put starting");
        Put put3 = new Put(TestConnectionImplementation.ROW);
        put3.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW, TestConnectionImplementation.ROW);
        try {
            table.put(put3);
            Assert.fail("Unreachable point");
        } catch (RetriesExhaustedWithDetailsException e) {
            TestConnectionImplementation.LOG.info(("Put done, exception caught: " + (e.getClass())));
            Assert.assertEquals(1, e.getNumExceptions());
            Assert.assertEquals(1, e.getCauses().size());
            Assert.assertArrayEquals(TestConnectionImplementation.ROW, e.getRow(0).getRow());
            // Check that we unserialized the exception as expected
            Throwable cause = ClientExceptionsUtil.findException(e.getCause(0));
            Assert.assertNotNull(cause);
            Assert.assertTrue((cause instanceof RegionMovedException));
        } catch (RetriesExhaustedException ree) {
            // hbase2 throws RetriesExhaustedException instead of RetriesExhaustedWithDetailsException
            // as hbase1 used to do. Keep an eye on this to see if this changed behavior is an issue.
            TestConnectionImplementation.LOG.info(("Put done, exception caught: " + (ree.getClass())));
            Throwable cause = ClientExceptionsUtil.findException(ree.getCause());
            Assert.assertNotNull(cause);
            Assert.assertTrue((cause instanceof RegionMovedException));
        }
        Assert.assertNotNull("Cached connection is null", conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW));
        Assert.assertEquals(("Previous server was " + (curServer.getServerName().getHostAndPort())), destServerName.getPort(), conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation().getPort());
        Assert.assertFalse(destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
        Assert.assertFalse(curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
        // We move it back to do another test with a scan
        TestConnectionImplementation.LOG.info(("Move starting region=" + (toMove.getRegionInfo().getRegionNameAsString())));
        TestConnectionImplementation.TEST_UTIL.getAdmin().move(toMove.getRegionInfo().getEncodedNameAsBytes(), Bytes.toBytes(curServer.getServerName().getServerName()));
        while (((((curServer.getOnlineRegion(regionName)) == null) || (destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (master.getAssignmentManager().hasRegionsInTransition())) {
            // wait for the move to be finished
            Thread.sleep(1);
        } 
        // Check our new state.
        Assert.assertNotNull(curServer.getOnlineRegion(regionName));
        Assert.assertNull(destServer.getOnlineRegion(regionName));
        TestConnectionImplementation.LOG.info(("Move finished for region=" + (toMove.getRegionInfo().getRegionNameAsString())));
        // Cache was NOT updated and points to the wrong server
        Assert.assertFalse(((conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation().getPort()) == (curServer.getServerName().getPort())));
        Scan sc = new Scan();
        sc.setStopRow(TestConnectionImplementation.ROW);
        sc.setStartRow(TestConnectionImplementation.ROW);
        // The scanner takes the max retries from the connection configuration, not the table as
        // the put.
        TestConnectionImplementation.TEST_UTIL.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, 1);
        try {
            ResultScanner rs = table.getScanner(sc);
            while ((rs.next()) != null) {
            } 
            Assert.fail("Unreachable point");
        } catch (RetriesExhaustedException e) {
            TestConnectionImplementation.LOG.info(("Scan done, expected exception caught: " + (e.getClass())));
        }
        // Cache is updated with the right value.
        Assert.assertNotNull(conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW));
        Assert.assertEquals(("Previous server was " + (destServer.getServerName().getHostAndPort())), curServer.getServerName().getPort(), conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME, TestConnectionImplementation.ROW).getRegionLocation().getPort());
        TestConnectionImplementation.TEST_UTIL.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, TestConnectionImplementation.RPC_RETRY);
        table.close();
        connection.close();
    }

    /**
     * Test that Connection or Pool are not closed when managed externally
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testConnectionManagement() throws Exception {
        Table table0 = TestConnectionImplementation.TEST_UTIL.createTable(TestConnectionImplementation.TABLE_NAME1, TestConnectionImplementation.FAM_NAM);
        Connection conn = ConnectionFactory.createConnection(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        Table table = conn.getTable(TestConnectionImplementation.TABLE_NAME1);
        table.close();
        Assert.assertFalse(conn.isClosed());
        if (table instanceof HTable) {
            Assert.assertFalse(getPool().isShutdown());
        }
        table = conn.getTable(TestConnectionImplementation.TABLE_NAME1);
        table.close();
        if (table instanceof HTable) {
            Assert.assertFalse(getPool().isShutdown());
        }
        conn.close();
        if (table instanceof HTable) {
            Assert.assertTrue(getPool().isShutdown());
        }
        table0.close();
    }

    /**
     * Test that stale cache updates don't override newer cached values.
     */
    @Test
    public void testCacheSeqNums() throws Exception {
        Table table = TestConnectionImplementation.TEST_UTIL.createMultiRegionTable(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.FAM_NAM);
        Put put = new Put(TestConnectionImplementation.ROW);
        put.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW, TestConnectionImplementation.ROW);
        table.put(put);
        ConnectionImplementation conn = ((ConnectionImplementation) (TestConnectionImplementation.TEST_UTIL.getConnection()));
        HRegionLocation location = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.ROW).getRegionLocation();
        Assert.assertNotNull(location);
        ServerName anySource = ServerName.valueOf(location.getHostname(), ((location.getPort()) - 1), 0L);
        // Same server as already in cache reporting - overwrites any value despite seqNum.
        int nextPort = (location.getPort()) + 1;
        conn.updateCachedLocation(location.getRegionInfo(), location.getServerName(), ServerName.valueOf("127.0.0.1", nextPort, 0), ((location.getSeqNum()) - 1));
        location = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.ROW).getRegionLocation();
        Assert.assertEquals(nextPort, location.getPort());
        // No source specified - same.
        nextPort = (location.getPort()) + 1;
        conn.updateCachedLocation(location.getRegionInfo(), location.getServerName(), ServerName.valueOf("127.0.0.1", nextPort, 0), ((location.getSeqNum()) - 1));
        location = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.ROW).getRegionLocation();
        Assert.assertEquals(nextPort, location.getPort());
        // Higher seqNum - overwrites lower seqNum.
        nextPort = (location.getPort()) + 1;
        conn.updateCachedLocation(location.getRegionInfo(), anySource, ServerName.valueOf("127.0.0.1", nextPort, 0), ((location.getSeqNum()) + 1));
        location = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.ROW).getRegionLocation();
        Assert.assertEquals(nextPort, location.getPort());
        // Lower seqNum - does not overwrite higher seqNum.
        nextPort = (location.getPort()) + 1;
        conn.updateCachedLocation(location.getRegionInfo(), anySource, ServerName.valueOf("127.0.0.1", nextPort, 0), ((location.getSeqNum()) - 1));
        location = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME2, TestConnectionImplementation.ROW).getRegionLocation();
        Assert.assertEquals((nextPort - 1), location.getPort());
        table.close();
    }

    @Test
    public void testClosing() throws Exception {
        Configuration configuration = new Configuration(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        configuration.set(HBASE_CLIENT_INSTANCE_ID, String.valueOf(ThreadLocalRandom.current().nextInt()));
        // as connection caching is going away, now we're just testing
        // that closed connection does actually get closed.
        Connection c1 = ConnectionFactory.createConnection(configuration);
        Connection c2 = ConnectionFactory.createConnection(configuration);
        // no caching, different connections
        Assert.assertTrue((c1 != c2));
        // closing independently
        c1.close();
        Assert.assertTrue(c1.isClosed());
        Assert.assertFalse(c2.isClosed());
        c2.close();
        Assert.assertTrue(c2.isClosed());
    }

    /**
     * Trivial test to verify that nobody messes with
     * {@link ConnectionFactory#createConnection(Configuration)}
     */
    @Test
    public void testCreateConnection() throws Exception {
        Configuration configuration = TestConnectionImplementation.TEST_UTIL.getConfiguration();
        Connection c1 = ConnectionFactory.createConnection(configuration);
        Connection c2 = ConnectionFactory.createConnection(configuration);
        // created from the same configuration, yet they are different
        Assert.assertTrue((c1 != c2));
        Assert.assertTrue(((c1.getConfiguration()) == (c2.getConfiguration())));
    }

    /**
     * This test checks that one can connect to the cluster with only the
     *  ZooKeeper quorum set. Other stuff like master address will be read
     *  from ZK by the client.
     */
    @Test
    public void testConnection() throws Exception {
        // We create an empty config and add the ZK address.
        Configuration c = new Configuration();
        c.set(ZOOKEEPER_QUORUM, TestConnectionImplementation.TEST_UTIL.getConfiguration().get(ZOOKEEPER_QUORUM));
        c.set(ZOOKEEPER_CLIENT_PORT, TestConnectionImplementation.TEST_UTIL.getConfiguration().get(ZOOKEEPER_CLIENT_PORT));
        // This should be enough to connect
        ClusterConnection conn = ((ClusterConnection) (ConnectionFactory.createConnection(c)));
        Assert.assertTrue(conn.isMasterRunning());
        conn.close();
    }

    @Test
    public void testMulti() throws Exception {
        Table table = TestConnectionImplementation.TEST_UTIL.createMultiRegionTable(TestConnectionImplementation.TABLE_NAME3, TestConnectionImplementation.FAM_NAM);
        try {
            ConnectionImplementation conn = ((ConnectionImplementation) (TestConnectionImplementation.TEST_UTIL.getConnection()));
            // We're now going to move the region and check that it works for the client
            // First a new put to add the location in the cache
            conn.clearRegionCache(TestConnectionImplementation.TABLE_NAME3);
            Assert.assertEquals(0, conn.getNumberOfCachedRegionLocations(TestConnectionImplementation.TABLE_NAME3));
            TestConnectionImplementation.TEST_UTIL.getAdmin().setBalancerRunning(false, false);
            HMaster master = TestConnectionImplementation.TEST_UTIL.getMiniHBaseCluster().getMaster();
            // We can wait for all regions to be online, that makes log reading easier when debugging
            TestConnectionImplementation.TEST_UTIL.waitUntilNoRegionsInTransition();
            Put put = new Put(TestConnectionImplementation.ROW_X);
            put.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW_X, TestConnectionImplementation.ROW_X);
            table.put(put);
            // Now moving the region to the second server
            HRegionLocation toMove = conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME3, TestConnectionImplementation.ROW_X).getRegionLocation();
            byte[] regionName = toMove.getRegionInfo().getRegionName();
            byte[] encodedRegionNameBytes = toMove.getRegionInfo().getEncodedNameAsBytes();
            // Choose the other server.
            int curServerId = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getServerWith(regionName);
            int destServerId = (curServerId == 0) ? 1 : 0;
            HRegionServer curServer = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getRegionServer(curServerId);
            HRegionServer destServer = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getRegionServer(destServerId);
            ServerName destServerName = destServer.getServerName();
            ServerName metaServerName = TestConnectionImplementation.TEST_UTIL.getHBaseCluster().getServerHoldingMeta();
            // find another row in the cur server that is less than ROW_X
            List<HRegion> regions = curServer.getRegions(TestConnectionImplementation.TABLE_NAME3);
            byte[] otherRow = null;
            for (Region region : regions) {
                if ((!(region.getRegionInfo().getEncodedName().equals(toMove.getRegionInfo().getEncodedName()))) && ((BYTES_COMPARATOR.compare(region.getRegionInfo().getStartKey(), TestConnectionImplementation.ROW_X)) < 0)) {
                    otherRow = region.getRegionInfo().getStartKey();
                    break;
                }
            }
            Assert.assertNotNull(otherRow);
            // If empty row, set it to first row.-f
            if ((otherRow.length) <= 0)
                otherRow = Bytes.toBytes("aaa");

            Put put2 = new Put(otherRow);
            put2.addColumn(TestConnectionImplementation.FAM_NAM, otherRow, otherRow);
            table.put(put2);// cache put2's location

            // Check that we are in the expected state
            Assert.assertTrue((curServer != destServer));
            Assert.assertNotEquals(curServer.getServerName(), destServer.getServerName());
            Assert.assertNotEquals(toMove.getPort(), destServerName.getPort());
            Assert.assertNotNull(curServer.getOnlineRegion(regionName));
            Assert.assertNull(destServer.getOnlineRegion(regionName));
            Assert.assertFalse(TestConnectionImplementation.TEST_UTIL.getMiniHBaseCluster().getMaster().getAssignmentManager().hasRegionsInTransition());
            // Moving. It's possible that we don't have all the regions online at this point, so
            // the test depends only on the region we're looking at.
            TestConnectionImplementation.LOG.info(("Move starting region=" + (toMove.getRegionInfo().getRegionNameAsString())));
            TestConnectionImplementation.TEST_UTIL.getAdmin().move(toMove.getRegionInfo().getEncodedNameAsBytes(), Bytes.toBytes(destServerName.getServerName()));
            while (((((destServer.getOnlineRegion(regionName)) == null) || (destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes))) || (master.getAssignmentManager().hasRegionsInTransition())) {
                // wait for the move to be finished
                Thread.sleep(1);
            } 
            TestConnectionImplementation.LOG.info(("Move finished for region=" + (toMove.getRegionInfo().getRegionNameAsString())));
            // Check our new state.
            Assert.assertNull(curServer.getOnlineRegion(regionName));
            Assert.assertNotNull(destServer.getOnlineRegion(regionName));
            Assert.assertFalse(destServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
            Assert.assertFalse(curServer.getRegionsInTransitionInRS().containsKey(encodedRegionNameBytes));
            // Cache was NOT updated and points to the wrong server
            Assert.assertFalse(((conn.getCachedLocation(TestConnectionImplementation.TABLE_NAME3, TestConnectionImplementation.ROW_X).getRegionLocation().getPort()) == (destServerName.getPort())));
            // Hijack the number of retry to fail after 2 tries
            final int prevNumRetriesVal = setNumTries(conn, 2);
            Put put3 = new Put(TestConnectionImplementation.ROW_X);
            put3.addColumn(TestConnectionImplementation.FAM_NAM, TestConnectionImplementation.ROW_X, TestConnectionImplementation.ROW_X);
            Put put4 = new Put(otherRow);
            put4.addColumn(TestConnectionImplementation.FAM_NAM, otherRow, otherRow);
            // do multi
            ArrayList<Put> actions = Lists.newArrayList(put4, put3);
            table.batch(actions, null);// first should be a valid row,

            // second we get RegionMovedException.
            setNumTries(conn, prevNumRetriesVal);
        } finally {
            table.close();
        }
    }

    @Test
    public void testErrorBackoffTimeCalculation() throws Exception {
        // TODO: This test would seem to presume hardcoded RETRY_BACKOFF which it should not.
        final long ANY_PAUSE = 100;
        ServerName location = ServerName.valueOf("127.0.0.1", 1, 0);
        ServerName diffLocation = ServerName.valueOf("127.0.0.1", 2, 0);
        ManualEnvironmentEdge timeMachine = new ManualEnvironmentEdge();
        EnvironmentEdgeManager.injectEdge(timeMachine);
        try {
            long largeAmountOfTime = ANY_PAUSE * 1000;
            ConnectionImplementation.ServerErrorTracker tracker = new ConnectionImplementation.ServerErrorTracker(largeAmountOfTime, 100);
            // The default backoff is 0.
            Assert.assertEquals(0, tracker.calculateBackoffTime(location, ANY_PAUSE));
            // Check some backoff values from HConstants sequence.
            tracker.reportServerError(location);
            TestConnectionImplementation.assertEqualsWithJitter((ANY_PAUSE * (HConstants.RETRY_BACKOFF[0])), tracker.calculateBackoffTime(location, ANY_PAUSE));
            tracker.reportServerError(location);
            tracker.reportServerError(location);
            tracker.reportServerError(location);
            TestConnectionImplementation.assertEqualsWithJitter((ANY_PAUSE * (HConstants.RETRY_BACKOFF[3])), tracker.calculateBackoffTime(location, ANY_PAUSE));
            // All of this shouldn't affect backoff for different location.
            Assert.assertEquals(0, tracker.calculateBackoffTime(diffLocation, ANY_PAUSE));
            tracker.reportServerError(diffLocation);
            TestConnectionImplementation.assertEqualsWithJitter((ANY_PAUSE * (HConstants.RETRY_BACKOFF[0])), tracker.calculateBackoffTime(diffLocation, ANY_PAUSE));
            // Check with different base.
            TestConnectionImplementation.assertEqualsWithJitter(((ANY_PAUSE * 2) * (HConstants.RETRY_BACKOFF[3])), tracker.calculateBackoffTime(location, (ANY_PAUSE * 2)));
        } finally {
            EnvironmentEdgeManager.reset();
        }
    }

    @Test
    public void testConnectionRideOverClusterRestart() throws IOException, InterruptedException {
        Configuration config = new Configuration(TestConnectionImplementation.TEST_UTIL.getConfiguration());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestConnectionImplementation.TEST_UTIL.createTable(tableName, new byte[][]{ TestConnectionImplementation.FAM_NAM }).close();
        Connection connection = ConnectionFactory.createConnection(config);
        Table table = connection.getTable(tableName);
        // this will cache the meta location and table's region location
        table.get(new Get(Bytes.toBytes("foo")));
        // restart HBase
        TestConnectionImplementation.TEST_UTIL.shutdownMiniHBaseCluster();
        TestConnectionImplementation.TEST_UTIL.restartHBaseCluster(2);
        // this should be able to discover new locations for meta and table's region
        table.get(new Get(Bytes.toBytes("foo")));
        TestConnectionImplementation.TEST_UTIL.deleteTable(tableName);
        table.close();
        connection.close();
    }

    @Test
    public void testLocateRegionsWithRegionReplicas() throws IOException {
        int regionReplication = 3;
        byte[] family = Bytes.toBytes("cf");
        TableName tableName = TableName.valueOf(name.getMethodName());
        // Create a table with region replicas
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName).setRegionReplication(regionReplication).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family));
        TestConnectionImplementation.TEST_UTIL.getAdmin().createTable(builder.build());
        try (ConnectionImplementation con = ((ConnectionImplementation) (ConnectionFactory.createConnection(TestConnectionImplementation.TEST_UTIL.getConfiguration())))) {
            // Get locations of the regions of the table
            List<HRegionLocation> locations = con.locateRegions(tableName, false, false);
            // The size of the returned locations should be 3
            Assert.assertEquals(regionReplication, locations.size());
            // The replicaIds of the returned locations should be 0, 1 and 2
            Set<Integer> expectedReplicaIds = IntStream.range(0, regionReplication).boxed().collect(Collectors.toSet());
            for (HRegionLocation location : locations) {
                Assert.assertTrue(expectedReplicaIds.remove(location.getRegion().getReplicaId()));
            }
        } finally {
            TestConnectionImplementation.TEST_UTIL.deleteTable(tableName);
        }
    }
}

