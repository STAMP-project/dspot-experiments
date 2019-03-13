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
package org.apache.hadoop.hbase.tool;


import HBaseMarkers.FATAL;
import HConstants.DEFAULT_HBASE_CLIENT_RETRIES_NUMBER;
import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import LoadIncrementalHFiles.RETRY_ON_IO_EXCEPTION;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ClientServiceCallable;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.TestHRegionServerBulkLoad;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hbase.thirdparty.com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static LoadIncrementalHFiles.TMP_DIR;


/**
 * Test cases for the atomic load error handling of the bulk load functionality.
 */
@Category({ MiscTests.class, LargeTests.class })
public class TestLoadIncrementalHFilesSplitRecovery {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLoadIncrementalHFilesSplitRecovery.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHRegionServerBulkLoad.class);

    static HBaseTestingUtility util;

    // used by secure subclass
    static boolean useSecure = false;

    static final int NUM_CFS = 10;

    static final byte[] QUAL = Bytes.toBytes("qual");

    static final int ROWCOUNT = 100;

    private static final byte[][] families = new byte[TestLoadIncrementalHFilesSplitRecovery.NUM_CFS][];

    @Rule
    public TestName name = new TestName();

    static {
        for (int i = 0; i < (TestLoadIncrementalHFilesSplitRecovery.NUM_CFS); i++) {
            TestLoadIncrementalHFilesSplitRecovery.families[i] = Bytes.toBytes(TestLoadIncrementalHFilesSplitRecovery.family(i));
        }
    }

    /**
     * Test that shows that exception thrown from the RS side will result in an exception on the
     * LIHFile client.
     */
    @Test(expected = IOException.class)
    public void testBulkLoadPhaseFailure() throws Exception {
        final TableName table = TableName.valueOf(name.getMethodName());
        final AtomicInteger attmptedCalls = new AtomicInteger();
        final AtomicInteger failedCalls = new AtomicInteger();
        TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, 2);
        try (Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration())) {
            setupTable(connection, table, 10);
            LoadIncrementalHFiles lih = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
                @Override
                protected List<LoadQueueItem> tryAtomicRegionLoad(ClientServiceCallable<byte[]> serviceCallable, TableName tableName, final byte[] first, Collection<LoadQueueItem> lqis) throws IOException {
                    int i = attmptedCalls.incrementAndGet();
                    if (i == 1) {
                        Connection errConn;
                        try {
                            errConn = getMockedConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration());
                            serviceCallable = this.buildClientServiceCallable(errConn, table, first, lqis, true);
                        } catch (Exception e) {
                            TestLoadIncrementalHFilesSplitRecovery.LOG.error(FATAL, "mocking cruft, should never happen", e);
                            throw new RuntimeException("mocking cruft, should never happen");
                        }
                        failedCalls.incrementAndGet();
                        return super.tryAtomicRegionLoad(serviceCallable, tableName, first, lqis);
                    }
                    return super.tryAtomicRegionLoad(serviceCallable, tableName, first, lqis);
                }
            };
            try {
                // create HFiles for different column families
                Path dir = buildBulkFiles(table, 1);
                try (Table t = connection.getTable(table);RegionLocator locator = connection.getRegionLocator(table);Admin admin = connection.getAdmin()) {
                    lih.doBulkLoad(dir, admin, t, locator);
                }
            } finally {
                TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, DEFAULT_HBASE_CLIENT_RETRIES_NUMBER);
            }
            Assert.fail("doBulkLoad should have thrown an exception");
        }
    }

    /**
     * Test that shows that exception thrown from the RS side will result in the expected number of
     * retries set by ${@link HConstants#HBASE_CLIENT_RETRIES_NUMBER} when
     * ${@link LoadIncrementalHFiles#RETRY_ON_IO_EXCEPTION} is set
     */
    @Test
    public void testRetryOnIOException() throws Exception {
        final TableName table = TableName.valueOf(name.getMethodName());
        final AtomicInteger calls = new AtomicInteger(0);
        final Connection conn = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration());
        TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().setInt(HBASE_CLIENT_RETRIES_NUMBER, 2);
        TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().setBoolean(RETRY_ON_IO_EXCEPTION, true);
        final LoadIncrementalHFiles lih = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
            @Override
            protected List<LoadQueueItem> tryAtomicRegionLoad(ClientServiceCallable<byte[]> serverCallable, TableName tableName, final byte[] first, Collection<LoadQueueItem> lqis) throws IOException {
                if ((calls.get()) < (TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().getInt(HBASE_CLIENT_RETRIES_NUMBER, DEFAULT_HBASE_CLIENT_RETRIES_NUMBER))) {
                    ClientServiceCallable<byte[]> newServerCallable = new ClientServiceCallable<byte[]>(conn, tableName, first, newController(), HConstants.PRIORITY_UNSET) {
                        @Override
                        public byte[] rpcCall() throws Exception {
                            throw new IOException("Error calling something on RegionServer");
                        }
                    };
                    calls.getAndIncrement();
                    return super.tryAtomicRegionLoad(newServerCallable, tableName, first, lqis);
                } else {
                    return super.tryAtomicRegionLoad(serverCallable, tableName, first, lqis);
                }
            }
        };
        setupTable(conn, table, 10);
        Path dir = buildBulkFiles(table, 1);
        lih.doBulkLoad(dir, conn.getAdmin(), conn.getTable(table), conn.getRegionLocator(table));
        Assert.assertEquals(calls.get(), 2);
        TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration().setBoolean(RETRY_ON_IO_EXCEPTION, false);
    }

    /**
     * This test exercises the path where there is a split after initial validation but before the
     * atomic bulk load call. We cannot use presplitting to test this path, so we actually inject a
     * split just before the atomic region load.
     */
    @Test
    public void testSplitWhileBulkLoadPhase() throws Exception {
        final TableName table = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration())) {
            setupTable(connection, table, 10);
            populateTable(connection, table, 1);
            assertExpectedTable(table, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 1);
            // Now let's cause trouble. This will occur after checks and cause bulk
            // files to fail when attempt to atomically import. This is recoverable.
            final AtomicInteger attemptedCalls = new AtomicInteger();
            LoadIncrementalHFiles lih2 = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
                @Override
                protected void bulkLoadPhase(final Table htable, final Connection conn, ExecutorService pool, Deque<LoadQueueItem> queue, final Multimap<ByteBuffer, LoadQueueItem> regionGroups, boolean copyFile, Map<LoadQueueItem, ByteBuffer> item2RegionMap) throws IOException {
                    int i = attemptedCalls.incrementAndGet();
                    if (i == 1) {
                        // On first attempt force a split.
                        forceSplit(table);
                    }
                    super.bulkLoadPhase(htable, conn, pool, queue, regionGroups, copyFile, item2RegionMap);
                }
            };
            // create HFiles for different column families
            try (Table t = connection.getTable(table);RegionLocator locator = connection.getRegionLocator(table);Admin admin = connection.getAdmin()) {
                Path bulk = buildBulkFiles(table, 2);
                lih2.doBulkLoad(bulk, admin, t, locator);
            }
            // check that data was loaded
            // The three expected attempts are 1) failure because need to split, 2)
            // load of split top 3) load of split bottom
            Assert.assertEquals(3, attemptedCalls.get());
            assertExpectedTable(table, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 2);
        }
    }

    /**
     * This test splits a table and attempts to bulk load. The bulk import files should be split
     * before atomically importing.
     */
    @Test
    public void testGroupOrSplitPresplit() throws Exception {
        final TableName table = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration())) {
            setupTable(connection, table, 10);
            populateTable(connection, table, 1);
            assertExpectedTable(connection, table, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 1);
            forceSplit(table);
            final AtomicInteger countedLqis = new AtomicInteger();
            LoadIncrementalHFiles lih = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
                @Override
                protected Pair<List<LoadQueueItem>, String> groupOrSplit(Multimap<ByteBuffer, LoadQueueItem> regionGroups, final LoadQueueItem item, final Table htable, final Pair<byte[][], byte[][]> startEndKeys) throws IOException {
                    Pair<List<LoadQueueItem>, String> lqis = super.groupOrSplit(regionGroups, item, htable, startEndKeys);
                    if ((lqis != null) && ((lqis.getFirst()) != null)) {
                        countedLqis.addAndGet(lqis.getFirst().size());
                    }
                    return lqis;
                }
            };
            // create HFiles for different column families
            Path bulk = buildBulkFiles(table, 2);
            try (Table t = connection.getTable(table);RegionLocator locator = connection.getRegionLocator(table);Admin admin = connection.getAdmin()) {
                lih.doBulkLoad(bulk, admin, t, locator);
            }
            assertExpectedTable(connection, table, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 2);
            Assert.assertEquals(20, countedLqis.get());
        }
    }

    /**
     * This test creates a table with many small regions. The bulk load files would be splitted
     * multiple times before all of them can be loaded successfully.
     */
    @Test
    public void testSplitTmpFileCleanUp() throws Exception {
        final TableName table = TableName.valueOf(name.getMethodName());
        byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("row_00000010"), Bytes.toBytes("row_00000020"), Bytes.toBytes("row_00000030"), Bytes.toBytes("row_00000040"), Bytes.toBytes("row_00000050") };
        try (Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration())) {
            setupTableWithSplitkeys(table, 10, SPLIT_KEYS);
            LoadIncrementalHFiles lih = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration());
            // create HFiles
            Path bulk = buildBulkFiles(table, 2);
            try (Table t = connection.getTable(table);RegionLocator locator = connection.getRegionLocator(table);Admin admin = connection.getAdmin()) {
                lih.doBulkLoad(bulk, admin, t, locator);
            }
            // family path
            Path tmpPath = new Path(bulk, TestLoadIncrementalHFilesSplitRecovery.family(0));
            // TMP_DIR under family path
            tmpPath = new Path(tmpPath, TMP_DIR);
            FileSystem fs = bulk.getFileSystem(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration());
            // HFiles have been splitted, there is TMP_DIR
            Assert.assertTrue(fs.exists(tmpPath));
            // TMP_DIR should have been cleaned-up
            Assert.assertNull(((TMP_DIR) + " should be empty."), FSUtils.listStatus(fs, tmpPath));
            assertExpectedTable(connection, table, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 2);
        }
    }

    /**
     * This simulates an remote exception which should cause LIHF to exit with an exception.
     */
    @Test(expected = IOException.class)
    public void testGroupOrSplitFailure() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try (Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration())) {
            setupTable(connection, tableName, 10);
            LoadIncrementalHFiles lih = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
                int i = 0;

                @Override
                protected Pair<List<LoadQueueItem>, String> groupOrSplit(Multimap<ByteBuffer, LoadQueueItem> regionGroups, final LoadQueueItem item, final Table table, final Pair<byte[][], byte[][]> startEndKeys) throws IOException {
                    (i)++;
                    if ((i) == 5) {
                        throw new IOException("failure");
                    }
                    return super.groupOrSplit(regionGroups, item, table, startEndKeys);
                }
            };
            // create HFiles for different column families
            Path dir = buildBulkFiles(tableName, 1);
            try (Table t = connection.getTable(tableName);RegionLocator locator = connection.getRegionLocator(tableName);Admin admin = connection.getAdmin()) {
                lih.doBulkLoad(dir, admin, t, locator);
            }
        }
        Assert.fail("doBulkLoad should have thrown an exception");
    }

    @Test
    public void testGroupOrSplitWhenRegionHoleExistsInMeta() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] SPLIT_KEYS = new byte[][]{ Bytes.toBytes("row_00000100") };
        // Share connection. We were failing to find the table with our new reverse scan because it
        // looks for first region, not any region -- that is how it works now. The below removes first
        // region in test. Was reliant on the Connection caching having first region.
        Connection connection = ConnectionFactory.createConnection(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration());
        Table table = connection.getTable(tableName);
        setupTableWithSplitkeys(tableName, 10, SPLIT_KEYS);
        Path dir = buildBulkFiles(tableName, 2);
        final AtomicInteger countedLqis = new AtomicInteger();
        LoadIncrementalHFiles loader = new LoadIncrementalHFiles(TestLoadIncrementalHFilesSplitRecovery.util.getConfiguration()) {
            @Override
            protected Pair<List<LoadQueueItem>, String> groupOrSplit(Multimap<ByteBuffer, LoadQueueItem> regionGroups, final LoadQueueItem item, final Table htable, final Pair<byte[][], byte[][]> startEndKeys) throws IOException {
                Pair<List<LoadQueueItem>, String> lqis = super.groupOrSplit(regionGroups, item, htable, startEndKeys);
                if ((lqis != null) && ((lqis.getFirst()) != null)) {
                    countedLqis.addAndGet(lqis.getFirst().size());
                }
                return lqis;
            }
        };
        // do bulkload when there is no region hole in hbase:meta.
        try (Table t = connection.getTable(tableName);RegionLocator locator = connection.getRegionLocator(tableName);Admin admin = connection.getAdmin()) {
            loader.doBulkLoad(dir, admin, t, locator);
        } catch (Exception e) {
            TestLoadIncrementalHFilesSplitRecovery.LOG.error("exeception=", e);
        }
        // check if all the data are loaded into the table.
        this.assertExpectedTable(tableName, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 2);
        dir = buildBulkFiles(tableName, 3);
        // Mess it up by leaving a hole in the hbase:meta
        List<RegionInfo> regionInfos = MetaTableAccessor.getTableRegions(connection, tableName);
        for (RegionInfo regionInfo : regionInfos) {
            if (Bytes.equals(regionInfo.getStartKey(), EMPTY_BYTE_ARRAY)) {
                MetaTableAccessor.deleteRegion(connection, regionInfo);
                break;
            }
        }
        try (Table t = connection.getTable(tableName);RegionLocator locator = connection.getRegionLocator(tableName);Admin admin = connection.getAdmin()) {
            loader.doBulkLoad(dir, admin, t, locator);
        } catch (Exception e) {
            TestLoadIncrementalHFilesSplitRecovery.LOG.error("exception=", e);
            Assert.assertTrue("IOException expected", (e instanceof IOException));
        }
        table.close();
        // Make sure at least the one region that still exists can be found.
        regionInfos = MetaTableAccessor.getTableRegions(connection, tableName);
        Assert.assertTrue(((regionInfos.size()) >= 1));
        this.assertExpectedTable(connection, tableName, TestLoadIncrementalHFilesSplitRecovery.ROWCOUNT, 2);
        connection.close();
    }
}

