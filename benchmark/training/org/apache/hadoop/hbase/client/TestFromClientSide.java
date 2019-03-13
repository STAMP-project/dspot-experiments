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


import Bytes.SIZEOF_INT;
import CompareOperator.EQUAL;
import CompareOperator.GREATER;
import CompareOperator.GREATER_OR_EQUAL;
import CompareOperator.LESS;
import CompareOperator.LESS_OR_EQUAL;
import CompareOperator.NOT_EQUAL;
import ConnectionConfiguration.MAX_KEYVALUE_SIZE_KEY;
import Durability.SKIP_WAL;
import HConstants.CATALOG_FAMILY;
import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import HConstants.HBASE_CLIENT_IPC_POOL_SIZE;
import HConstants.HBASE_CLIENT_IPC_POOL_TYPE;
import HConstants.HBASE_CLIENT_PAUSE;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import HRegion.HBASE_MAX_CELL_SIZE_KEY;
import KeepDeletedCells.TRUE;
import MultiRowMutationService.BlockingInterface;
import MutateRowsRequest.Builder;
import MutationType.PUT;
import Option.LIVE_SERVERS;
import TableName.META_TABLE_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellScanner;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.metrics.ScanMetrics;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.InclusiveStopFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.io.TimeRange;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcChannel;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.MutationProto;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos.MultiRowMutationService;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos.MutateRowsRequest;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ConnectionUtils.MAX_BYTE_ARRAY;
import static org.apache.hadoop.hbase.client.HConnectionTestingUtility.SleepAtFirstRpcCall.SLEEP_TIME_CONF_KEY;


/**
 * Run tests that use the HBase clients; {@link Table}.
 * Sets up the HBase mini cluster once at start and runs through all client tests.
 * Each creates a table named for the method and does its stuff against that.
 */
@Category({ LargeTests.class, ClientTests.class })
@SuppressWarnings("deprecation")
public class TestFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFromClientSide.class);

    // NOTE: Increment tests were moved to their own class, TestIncrementsFromClientSide.
    private static final Logger LOG = LoggerFactory.getLogger(TestFromClientSide.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static final byte[] INVALID_FAMILY = Bytes.toBytes("invalidTestFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] VALUE = Bytes.toBytes("testValue");

    protected static int SLAVES = 3;

    @Rule
    public TestName name = new TestName();

    /**
     * Test append result when there are duplicate rpc request.
     */
    @Test
    public void testDuplicateAppend() throws Exception {
        HTableDescriptor hdt = TestFromClientSide.TEST_UTIL.createTableDescriptor(name.getMethodName());
        Map<String, String> kvs = new HashMap<>();
        kvs.put(SLEEP_TIME_CONF_KEY, "2000");
        hdt.addCoprocessor(HConnectionTestingUtility.SleepAtFirstRpcCall.class.getName(), null, 1, kvs);
        TestFromClientSide.TEST_UTIL.createTable(hdt, new byte[][]{ TestFromClientSide.ROW }).close();
        Configuration c = new Configuration(TestFromClientSide.TEST_UTIL.getConfiguration());
        c.setInt(HBASE_CLIENT_PAUSE, 50);
        // Client will retry beacuse rpc timeout is small than the sleep time of first rpc call
        c.setInt(HBASE_RPC_TIMEOUT_KEY, 1500);
        Connection connection = ConnectionFactory.createConnection(c);
        Table t = connection.getTable(TableName.valueOf(name.getMethodName()));
        if (t instanceof HTable) {
            HTable table = ((HTable) (t));
            table.setOperationTimeout((3 * 1000));
            try {
                Append append = new Append(TestFromClientSide.ROW);
                append.addColumn(HBaseTestingUtility.fam1, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
                Result result = table.append(append);
                // Verify expected result
                Cell[] cells = result.rawCells();
                Assert.assertEquals(1, cells.length);
                assertKey(cells[0], TestFromClientSide.ROW, HBaseTestingUtility.fam1, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
                // Verify expected result again
                Result readResult = table.get(new Get(TestFromClientSide.ROW));
                cells = readResult.rawCells();
                Assert.assertEquals(1, cells.length);
                assertKey(cells[0], TestFromClientSide.ROW, HBaseTestingUtility.fam1, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            } finally {
                table.close();
                connection.close();
            }
        }
    }

    /**
     * Basic client side validation of HBASE-4536
     */
    @Test
    public void testKeepDeletedCells() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] FAMILY = Bytes.toBytes("family");
        final byte[] C0 = Bytes.toBytes("c0");
        final byte[] T1 = Bytes.toBytes("T1");
        final byte[] T2 = Bytes.toBytes("T2");
        final byte[] T3 = Bytes.toBytes("T3");
        HColumnDescriptor hcd = new HColumnDescriptor(FAMILY).setKeepDeletedCells(TRUE).setMaxVersions(3);
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(hcd);
        TestFromClientSide.TEST_UTIL.getAdmin().createTable(desc);
        Table h = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableName);
        long ts = System.currentTimeMillis();
        Put p = new Put(T1, ts);
        p.addColumn(FAMILY, C0, T1);
        h.put(p);
        p = new Put(T1, (ts + 2));
        p.addColumn(FAMILY, C0, T2);
        h.put(p);
        p = new Put(T1, (ts + 4));
        p.addColumn(FAMILY, C0, T3);
        h.put(p);
        Delete d = new Delete(T1, (ts + 3));
        h.delete(d);
        d = new Delete(T1, (ts + 3));
        d.addColumns(FAMILY, C0, (ts + 3));
        h.delete(d);
        Get g = new Get(T1);
        // does *not* include the delete
        g.setTimeRange(0, (ts + 3));
        Result r = h.get(g);
        Assert.assertArrayEquals(T2, r.getValue(FAMILY, C0));
        Scan s = new Scan(T1);
        s.setTimeRange(0, (ts + 3));
        s.setMaxVersions();
        ResultScanner scanner = h.getScanner(s);
        Cell[] kvs = scanner.next().rawCells();
        Assert.assertArrayEquals(T2, CellUtil.cloneValue(kvs[0]));
        Assert.assertArrayEquals(T1, CellUtil.cloneValue(kvs[1]));
        scanner.close();
        s = new Scan(T1);
        s.setRaw(true);
        s.setMaxVersions();
        scanner = h.getScanner(s);
        kvs = scanner.next().rawCells();
        Assert.assertTrue(PrivateCellUtil.isDeleteFamily(kvs[0]));
        Assert.assertArrayEquals(T3, CellUtil.cloneValue(kvs[1]));
        Assert.assertTrue(CellUtil.isDelete(kvs[2]));
        Assert.assertArrayEquals(T2, CellUtil.cloneValue(kvs[3]));
        Assert.assertArrayEquals(T1, CellUtil.cloneValue(kvs[4]));
        scanner.close();
        h.close();
    }

    /**
     * Basic client side validation of HBASE-10118
     */
    @Test
    public void testPurgeFutureDeletes() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] ROW = Bytes.toBytes("row");
        final byte[] FAMILY = Bytes.toBytes("family");
        final byte[] COLUMN = Bytes.toBytes("column");
        final byte[] VALUE = Bytes.toBytes("value");
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILY);
        // future timestamp
        long ts = (System.currentTimeMillis()) * 2;
        Put put = new Put(ROW, ts);
        put.addColumn(FAMILY, COLUMN, VALUE);
        table.put(put);
        Get get = new Get(ROW);
        Result result = table.get(get);
        Assert.assertArrayEquals(VALUE, result.getValue(FAMILY, COLUMN));
        Delete del = new Delete(ROW);
        del.addColumn(FAMILY, COLUMN, ts);
        table.delete(del);
        get = new Get(ROW);
        result = table.get(get);
        Assert.assertNull(result.getValue(FAMILY, COLUMN));
        // major compaction, purged future deletes
        TestFromClientSide.TEST_UTIL.getAdmin().flush(tableName);
        TestFromClientSide.TEST_UTIL.getAdmin().majorCompact(tableName);
        // waiting for the major compaction to complete
        TestFromClientSide.TEST_UTIL.waitFor(6000, new Waiter.Predicate<IOException>() {
            @Override
            public boolean evaluate() throws IOException {
                return (TestFromClientSide.TEST_UTIL.getAdmin().getCompactionState(tableName)) == CompactionState.NONE;
            }
        });
        put = new Put(ROW, ts);
        put.addColumn(FAMILY, COLUMN, VALUE);
        table.put(put);
        get = new Get(ROW);
        result = table.get(get);
        Assert.assertArrayEquals(VALUE, result.getValue(FAMILY, COLUMN));
        table.close();
    }

    /**
     * Verifies that getConfiguration returns the same Configuration object used
     * to create the HTable instance.
     */
    @Test
    public void testGetConfiguration() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("foo") };
        Configuration conf = TestFromClientSide.TEST_UTIL.getConfiguration();
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        Assert.assertSame(conf, table.getConfiguration());
    }

    /**
     * Test from client side of an involved filter against a multi family that
     * involves deletes.
     */
    @Test
    public void testWeirdCacheBehaviour() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("trans-blob"), Bytes.toBytes("trans-type"), Bytes.toBytes("trans-date"), Bytes.toBytes("trans-tags"), Bytes.toBytes("trans-group") };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        String value = "this is the value";
        String value2 = "this is some other value";
        String keyPrefix1 = getRandomUUID().toString();
        String keyPrefix2 = getRandomUUID().toString();
        String keyPrefix3 = getRandomUUID().toString();
        putRows(ht, 3, value, keyPrefix1);
        putRows(ht, 3, value, keyPrefix2);
        putRows(ht, 3, value, keyPrefix3);
        putRows(ht, 3, value2, keyPrefix1);
        putRows(ht, 3, value2, keyPrefix2);
        putRows(ht, 3, value2, keyPrefix3);
        Table table = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableName);
        System.out.println(("Checking values for key: " + keyPrefix1));
        Assert.assertEquals("Got back incorrect number of rows from scan", 3, getNumberOfRows(keyPrefix1, value2, table));
        System.out.println(("Checking values for key: " + keyPrefix2));
        Assert.assertEquals("Got back incorrect number of rows from scan", 3, getNumberOfRows(keyPrefix2, value2, table));
        System.out.println(("Checking values for key: " + keyPrefix3));
        Assert.assertEquals("Got back incorrect number of rows from scan", 3, getNumberOfRows(keyPrefix3, value2, table));
        deleteColumns(ht, value2, keyPrefix1);
        deleteColumns(ht, value2, keyPrefix2);
        deleteColumns(ht, value2, keyPrefix3);
        System.out.println("Starting important checks.....");
        Assert.assertEquals(("Got back incorrect number of rows from scan: " + keyPrefix1), 0, getNumberOfRows(keyPrefix1, value2, table));
        Assert.assertEquals(("Got back incorrect number of rows from scan: " + keyPrefix2), 0, getNumberOfRows(keyPrefix2, value2, table));
        Assert.assertEquals(("Got back incorrect number of rows from scan: " + keyPrefix3), 0, getNumberOfRows(keyPrefix3, value2, table));
    }

    /**
     * Test filters when multiple regions.  It does counts.  Needs eye-balling of
     * logs to ensure that we're not scanning more regions that we're supposed to.
     * Related to the TestFilterAcrossRegions over in the o.a.h.h.filter package.
     */
    @Test
    public void testFilterAcrossMultipleRegions() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        int rowCount = TestFromClientSide.TEST_UTIL.loadTable(t, TestFromClientSide.FAMILY, false);
        assertRowCount(t, rowCount);
        // Split the table.  Should split on a reasonable key; 'lqj'
        List<HRegionLocation> regions = splitTable(t);
        assertRowCount(t, rowCount);
        // Get end key of first region.
        byte[] endKey = regions.get(0).getRegionInfo().getEndKey();
        // Count rows with a filter that stops us before passed 'endKey'.
        // Should be count of rows in first region.
        int endKeyCount = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(endKey));
        Assert.assertTrue((endKeyCount < rowCount));
        // How do I know I did not got to second region?  Thats tough.  Can't really
        // do that in client-side region test.  I verified by tracing in debugger.
        // I changed the messages that come out when set to DEBUG so should see
        // when scanner is done. Says "Finished with scanning..." with region name.
        // Check that its finished in right region.
        // New test.  Make it so scan goes into next region by one and then two.
        // Make sure count comes out right.
        byte[] key = new byte[]{ endKey[0], endKey[1], ((byte) ((endKey[2]) + 1)) };
        int plusOneCount = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(key));
        Assert.assertEquals((endKeyCount + 1), plusOneCount);
        key = new byte[]{ endKey[0], endKey[1], ((byte) ((endKey[2]) + 2)) };
        int plusTwoCount = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(key));
        Assert.assertEquals((endKeyCount + 2), plusTwoCount);
        // New test.  Make it so I scan one less than endkey.
        key = new byte[]{ endKey[0], endKey[1], ((byte) ((endKey[2]) - 1)) };
        int minusOneCount = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(key));
        Assert.assertEquals((endKeyCount - 1), minusOneCount);
        // For above test... study logs.  Make sure we do "Finished with scanning.."
        // in first region and that we do not fall into the next region.
        key = new byte[]{ 'a', 'a', 'a' };
        int countBBB = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(key, null, EQUAL));
        Assert.assertEquals(1, countBBB);
        int countGreater = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(endKey, null, GREATER_OR_EQUAL));
        // Because started at start of table.
        Assert.assertEquals(0, countGreater);
        countGreater = TestFromClientSide.TEST_UTIL.countRows(t, createScanWithRowFilter(endKey, endKey, GREATER_OR_EQUAL));
        Assert.assertEquals((rowCount - endKeyCount), countGreater);
    }

    @Test
    public void testSuperSimple() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        Scan scan = new Scan();
        scan.addColumn(TestFromClientSide.FAMILY, tableName.toBytes());
        ResultScanner scanner = ht.getScanner(scan);
        Result result = scanner.next();
        Assert.assertTrue("Expected null result", (result == null));
        scanner.close();
    }

    @Test
    public void testMaxKeyValueSize() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Configuration conf = TestFromClientSide.TEST_UTIL.getConfiguration();
        String oldMaxSize = conf.get(MAX_KEYVALUE_SIZE_KEY);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[] value = new byte[(4 * 1024) * 1024];
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value);
        ht.put(put);
        try {
            TestFromClientSide.TEST_UTIL.getConfiguration().setInt(MAX_KEYVALUE_SIZE_KEY, ((2 * 1024) * 1024));
            // Create new table so we pick up the change in Configuration.
            try (Connection connection = ConnectionFactory.createConnection(TestFromClientSide.TEST_UTIL.getConfiguration())) {
                try (Table t = connection.getTable(TableName.valueOf(TestFromClientSide.FAMILY))) {
                    put = new Put(TestFromClientSide.ROW);
                    put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value);
                    t.put(put);
                }
            }
            Assert.fail("Inserting a too large KeyValue worked, should throw exception");
        } catch (Exception e) {
        }
        conf.set(MAX_KEYVALUE_SIZE_KEY, oldMaxSize);
    }

    @Test
    public void testFilters() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 10);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("col0-<d2v1>-<d3v2>"), Bytes.toBytes("col1-<d2v1>-<d3v2>"), Bytes.toBytes("col2-<d2v1>-<d3v2>"), Bytes.toBytes("col3-<d2v1>-<d3v2>"), Bytes.toBytes("col4-<d2v1>-<d3v2>"), Bytes.toBytes("col5-<d2v1>-<d3v2>"), Bytes.toBytes("col6-<d2v1>-<d3v2>"), Bytes.toBytes("col7-<d2v1>-<d3v2>"), Bytes.toBytes("col8-<d2v1>-<d3v2>"), Bytes.toBytes("col9-<d2v1>-<d3v2>") };
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROWS[i]);
            put.setDurability(SKIP_WAL);
            put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i], TestFromClientSide.VALUE);
            ht.put(put);
        }
        Scan scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        Filter filter = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new RegexStringComparator("col[1-5]"));
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int expectedIndex = 1;
        for (Result result : ht.getScanner(scan)) {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[0]), ROWS[expectedIndex]));
            Assert.assertTrue(Bytes.equals(CellUtil.cloneQualifier(result.rawCells()[0]), QUALIFIERS[expectedIndex]));
            expectedIndex++;
        }
        Assert.assertEquals(6, expectedIndex);
        scanner.close();
    }

    @Test
    public void testFilterWithLongCompartor() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 10);
        byte[][] values = new byte[10][];
        for (int i = 0; i < 10; i++) {
            values[i] = Bytes.toBytes((100L * i));
        }
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROWS[i]);
            put.setDurability(SKIP_WAL);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, values[i]);
            ht.put(put);
        }
        Scan scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        Filter filter = new org.apache.hadoop.hbase.filter.SingleColumnValueFilter(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, CompareOperator.GREATER, new LongComparator(500));
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int expectedIndex = 0;
        for (Result result : ht.getScanner(scan)) {
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(((Bytes.toLong(result.getValue(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER))) > 500));
            expectedIndex++;
        }
        Assert.assertEquals(4, expectedIndex);
        scanner.close();
    }

    @Test
    public void testKeyOnlyFilter() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 10);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("col0-<d2v1>-<d3v2>"), Bytes.toBytes("col1-<d2v1>-<d3v2>"), Bytes.toBytes("col2-<d2v1>-<d3v2>"), Bytes.toBytes("col3-<d2v1>-<d3v2>"), Bytes.toBytes("col4-<d2v1>-<d3v2>"), Bytes.toBytes("col5-<d2v1>-<d3v2>"), Bytes.toBytes("col6-<d2v1>-<d3v2>"), Bytes.toBytes("col7-<d2v1>-<d3v2>"), Bytes.toBytes("col8-<d2v1>-<d3v2>"), Bytes.toBytes("col9-<d2v1>-<d3v2>") };
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROWS[i]);
            put.setDurability(SKIP_WAL);
            put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i], TestFromClientSide.VALUE);
            ht.put(put);
        }
        Scan scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        Filter filter = new KeyOnlyFilter(true);
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int count = 0;
        for (Result result : ht.getScanner(scan)) {
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(SIZEOF_INT, result.rawCells()[0].getValueLength());
            Assert.assertEquals(TestFromClientSide.VALUE.length, Bytes.toInt(CellUtil.cloneValue(result.rawCells()[0])));
            count++;
        }
        Assert.assertEquals(10, count);
        scanner.close();
    }

    /**
     * Test simple table and non-existent row cases.
     */
    @Test
    public void testSimpleMissing() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 4);
        // Try to get a row on an empty table
        Get get = new Get(ROWS[0]);
        Result result = ht.get(get);
        assertEmptyResult(result);
        get = new Get(ROWS[0]);
        get.addFamily(TestFromClientSide.FAMILY);
        result = ht.get(get);
        assertEmptyResult(result);
        get = new Get(ROWS[0]);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        result = ht.get(get);
        assertEmptyResult(result);
        Scan scan = new Scan();
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan(ROWS[0]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan(ROWS[0], ROWS[1]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan();
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Insert a row
        Put put = new Put(ROWS[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        // Try to get empty rows around it
        get = new Get(ROWS[1]);
        result = ht.get(get);
        assertEmptyResult(result);
        get = new Get(ROWS[0]);
        get.addFamily(TestFromClientSide.FAMILY);
        result = ht.get(get);
        assertEmptyResult(result);
        get = new Get(ROWS[3]);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to scan empty rows around it
        scan = new Scan(ROWS[3]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan(ROWS[0], ROWS[2]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Make sure we can actually get the row
        get = new Get(ROWS[2]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        get = new Get(ROWS[2]);
        get.addFamily(TestFromClientSide.FAMILY);
        result = ht.get(get);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        get = new Get(ROWS[2]);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        result = ht.get(get);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        // Make sure we can scan the row
        scan = new Scan();
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        scan = new Scan(ROWS[0], ROWS[3]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        scan = new Scan(ROWS[2], ROWS[3]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
    }

    /**
     * Test basic puts, gets, scans, and deletes for a single row
     * in a multiple family table.
     */
    @Test
    public void testSingleRowMultipleFamily() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 3);
        byte[][] FAMILIES = makeNAscii(TestFromClientSide.FAMILY, 10);
        byte[][] QUALIFIERS = makeN(TestFromClientSide.QUALIFIER, 10);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 10);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        Get get;
        Scan scan;
        Delete delete;
        Put put;
        Result result;
        // //////////////////////////////////////////////////////////////////////////
        // Insert one column to one family
        // //////////////////////////////////////////////////////////////////////////
        put = new Put(ROWS[0]);
        put.addColumn(FAMILIES[4], QUALIFIERS[0], VALUES[0]);
        ht.put(put);
        // Get the single column
        getVerifySingleColumn(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0, VALUES, 0);
        // Scan the single column
        scanVerifySingleColumn(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0, VALUES, 0);
        // Get empty results around inserted column
        getVerifySingleEmpty(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0);
        // Scan empty results around inserted column
        scanVerifySingleEmpty(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0);
        // //////////////////////////////////////////////////////////////////////////
        // Flush memstore and run same tests from storefiles
        // //////////////////////////////////////////////////////////////////////////
        TestFromClientSide.TEST_UTIL.flush();
        // Redo get and scan tests from storefile
        getVerifySingleColumn(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0, VALUES, 0);
        scanVerifySingleColumn(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0, VALUES, 0);
        getVerifySingleEmpty(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0);
        scanVerifySingleEmpty(ht, ROWS, 0, FAMILIES, 4, QUALIFIERS, 0);
        // //////////////////////////////////////////////////////////////////////////
        // Now, Test reading from memstore and storefiles at once
        // //////////////////////////////////////////////////////////////////////////
        // Insert multiple columns to two other families
        put = new Put(ROWS[0]);
        put.addColumn(FAMILIES[2], QUALIFIERS[2], VALUES[2]);
        put.addColumn(FAMILIES[2], QUALIFIERS[4], VALUES[4]);
        put.addColumn(FAMILIES[4], QUALIFIERS[4], VALUES[4]);
        put.addColumn(FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        put.addColumn(FAMILIES[6], QUALIFIERS[7], VALUES[7]);
        put.addColumn(FAMILIES[7], QUALIFIERS[7], VALUES[7]);
        put.addColumn(FAMILIES[9], QUALIFIERS[0], VALUES[0]);
        ht.put(put);
        // Get multiple columns across multiple families and get empties around it
        singleRowGetTest(ht, ROWS, FAMILIES, QUALIFIERS, VALUES);
        // Scan multiple columns across multiple families and scan empties around it
        singleRowScanTest(ht, ROWS, FAMILIES, QUALIFIERS, VALUES);
        // //////////////////////////////////////////////////////////////////////////
        // Flush the table again
        // //////////////////////////////////////////////////////////////////////////
        TestFromClientSide.TEST_UTIL.flush();
        // Redo tests again
        singleRowGetTest(ht, ROWS, FAMILIES, QUALIFIERS, VALUES);
        singleRowScanTest(ht, ROWS, FAMILIES, QUALIFIERS, VALUES);
        // Insert more data to memstore
        put = new Put(ROWS[0]);
        put.addColumn(FAMILIES[6], QUALIFIERS[5], VALUES[5]);
        put.addColumn(FAMILIES[6], QUALIFIERS[8], VALUES[8]);
        put.addColumn(FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        put.addColumn(FAMILIES[4], QUALIFIERS[3], VALUES[3]);
        ht.put(put);
        // //////////////////////////////////////////////////////////////////////////
        // Delete a storefile column
        // //////////////////////////////////////////////////////////////////////////
        delete = new Delete(ROWS[0]);
        delete.addColumns(FAMILIES[6], QUALIFIERS[7]);
        ht.delete(delete);
        // Try to get deleted column
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[7]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to scan deleted column
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[7]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Make sure we can still get a column before it and after it
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[8]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[8], VALUES[8]);
        // Make sure we can still scan a column before it and after it
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[8]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[8], VALUES[8]);
        // //////////////////////////////////////////////////////////////////////////
        // Delete a memstore column
        // //////////////////////////////////////////////////////////////////////////
        delete = new Delete(ROWS[0]);
        delete.addColumns(FAMILIES[6], QUALIFIERS[8]);
        ht.delete(delete);
        // Try to get deleted column
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[8]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to scan deleted column
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[8]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Make sure we can still get a column before it and after it
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        // Make sure we can still scan a column before it and after it
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        // //////////////////////////////////////////////////////////////////////////
        // Delete joint storefile/memstore family
        // //////////////////////////////////////////////////////////////////////////
        delete = new Delete(ROWS[0]);
        delete.addFamily(FAMILIES[4]);
        ht.delete(delete);
        // Try to get storefile column in deleted family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[4], QUALIFIERS[4]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to get memstore column in deleted family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[4], QUALIFIERS[3]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to get deleted family
        get = new Get(ROWS[0]);
        get.addFamily(FAMILIES[4]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to scan storefile column in deleted family
        scan = new Scan();
        scan.addColumn(FAMILIES[4], QUALIFIERS[4]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Try to scan memstore column in deleted family
        scan = new Scan();
        scan.addColumn(FAMILIES[4], QUALIFIERS[3]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Try to scan deleted family
        scan = new Scan();
        scan.addFamily(FAMILIES[4]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Make sure we can still get another family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[2], QUALIFIERS[2]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[2], QUALIFIERS[2], VALUES[2]);
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        // Make sure we can still scan another family
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        // //////////////////////////////////////////////////////////////////////////
        // Flush everything and rerun delete tests
        // //////////////////////////////////////////////////////////////////////////
        TestFromClientSide.TEST_UTIL.flush();
        // Try to get storefile column in deleted family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[4], QUALIFIERS[4]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to get memstore column in deleted family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[4], QUALIFIERS[3]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to get deleted family
        get = new Get(ROWS[0]);
        get.addFamily(FAMILIES[4]);
        result = ht.get(get);
        assertEmptyResult(result);
        // Try to scan storefile column in deleted family
        scan = new Scan();
        scan.addColumn(FAMILIES[4], QUALIFIERS[4]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Try to scan memstore column in deleted family
        scan = new Scan();
        scan.addColumn(FAMILIES[4], QUALIFIERS[3]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Try to scan deleted family
        scan = new Scan();
        scan.addFamily(FAMILIES[4]);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Make sure we can still get another family
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[2], QUALIFIERS[2]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[2], QUALIFIERS[2], VALUES[2]);
        get = new Get(ROWS[0]);
        get.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = ht.get(get);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
        // Make sure we can still scan another family
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[6]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[6], VALUES[6]);
        scan = new Scan();
        scan.addColumn(FAMILIES[6], QUALIFIERS[9]);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[0], FAMILIES[6], QUALIFIERS[9], VALUES[9]);
    }

    @Test
    public void testNull() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Null table name (should NOT work)
        try {
            TestFromClientSide.TEST_UTIL.createTable(((TableName) (null)), TestFromClientSide.FAMILY);
            Assert.fail("Creating a table with null name passed, should have failed");
        } catch (Exception e) {
        }
        // Null family (should NOT work)
        try {
            TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ null });
            Assert.fail("Creating a table with a null family passed, should fail");
        } catch (Exception e) {
        }
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Null row (should NOT work)
        try {
            Put put = new Put(((byte[]) (null)));
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            ht.put(put);
            Assert.fail("Inserting a null row worked, should throw exception");
        } catch (Exception e) {
        }
        // Null qualifier (should work)
        {
            Put put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
            ht.put(put);
            getTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            Delete delete = new Delete(TestFromClientSide.ROW);
            delete.addColumns(TestFromClientSide.FAMILY, null);
            ht.delete(delete);
            Get get = new Get(TestFromClientSide.ROW);
            Result result = ht.get(get);
            assertEmptyResult(result);
        }
        // Use a new table
        ht = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(((name.getMethodName()) + "2")), TestFromClientSide.FAMILY);
        // Empty qualifier, byte[0] instead of null (should work)
        try {
            Put put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, EMPTY_BYTE_ARRAY, TestFromClientSide.VALUE);
            ht.put(put);
            getTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            // Flush and try again
            TestFromClientSide.TEST_UTIL.flush();
            getTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
            Delete delete = new Delete(TestFromClientSide.ROW);
            delete.addColumns(TestFromClientSide.FAMILY, EMPTY_BYTE_ARRAY);
            ht.delete(delete);
            Get get = new Get(TestFromClientSide.ROW);
            Result result = ht.get(get);
            assertEmptyResult(result);
        } catch (Exception e) {
            throw new IOException("Using a row with null qualifier threw exception, should ");
        }
        // Null value
        try {
            Put put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, null);
            ht.put(put);
            Get get = new Get(TestFromClientSide.ROW);
            get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
            Result result = ht.get(get);
            assertSingleResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, null);
            Scan scan = new Scan();
            scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
            result = getSingleScanResult(ht, scan);
            assertSingleResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, null);
            Delete delete = new Delete(TestFromClientSide.ROW);
            delete.addColumns(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
            ht.delete(delete);
            get = new Get(TestFromClientSide.ROW);
            result = ht.get(get);
            assertEmptyResult(result);
        } catch (Exception e) {
            throw new IOException("Null values should be allowed, but threw exception");
        }
    }

    @Test
    public void testNullQualifier() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Work for Put
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
        table.put(put);
        // Work for Get, Scan
        getTestNull(table, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
        scanTestNull(table, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
        // Work for Delete
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, null);
        table.delete(delete);
        Get get = new Get(TestFromClientSide.ROW);
        Result result = table.get(get);
        assertEmptyResult(result);
        // Work for Increment/Append
        Increment increment = new Increment(TestFromClientSide.ROW);
        increment.addColumn(TestFromClientSide.FAMILY, null, 1L);
        table.increment(increment);
        getTestNull(table, TestFromClientSide.ROW, TestFromClientSide.FAMILY, 1L);
        table.incrementColumnValue(TestFromClientSide.ROW, TestFromClientSide.FAMILY, null, 1L);
        getTestNull(table, TestFromClientSide.ROW, TestFromClientSide.FAMILY, 2L);
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, null);
        table.delete(delete);
        Append append = new Append(TestFromClientSide.ROW);
        append.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
        table.append(append);
        getTestNull(table, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE);
        // Work for checkAndMutate using thenPut, thenMutate and thenDelete
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, null, Bytes.toBytes("checkAndPut"));
        table.put(put);
        table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).ifEquals(TestFromClientSide.VALUE).thenPut(put);
        RowMutations mutate = new RowMutations(TestFromClientSide.ROW);
        mutate.add(new Put(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, null, Bytes.toBytes("checkAndMutate")));
        table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).ifEquals(Bytes.toBytes("checkAndPut")).thenMutate(mutate);
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, null);
        table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).ifEquals(Bytes.toBytes("checkAndMutate")).thenDelete(delete);
    }

    @Test
    public void testVersions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        long[] STAMPS = makeStamps(20);
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 20);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert 4 versions of same column
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        ht.put(put);
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        // Ensure maxVersions in query is respected
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(2);
        Result result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        Scan scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(2);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        // Flush and redo
        TestFromClientSide.TEST_UTIL.flush();
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        // Ensure maxVersions in query is respected
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(2);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(2);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        // Add some memstore and retest
        // Insert 4 more versions of same column and a dupe
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[8], VALUES[8]);
        ht.put(put);
        // Ensure maxVersions in query is respected
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions();
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 7);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions();
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 7);
        get = new Get(TestFromClientSide.ROW);
        get.setMaxVersions();
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 7);
        scan = new Scan(TestFromClientSide.ROW);
        scan.setMaxVersions();
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 7);
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9]);
        // Ensure maxVersions of table is respected
        TestFromClientSide.TEST_UTIL.flush();
        // Insert 4 more versions of same column and a dupe
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9], VALUES[9]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[11], VALUES[11]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[13], VALUES[13]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[15], VALUES[15]);
        ht.put(put);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8], STAMPS[9], STAMPS[11], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8], VALUES[9], VALUES[11], VALUES[13], VALUES[15] }, 0, 9);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8], STAMPS[9], STAMPS[11], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[7], VALUES[8], VALUES[9], VALUES[11], VALUES[13], VALUES[15] }, 0, 9);
        // Delete a version in the memstore and a version in a storefile
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[11]);
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7]);
        ht.delete(delete);
        // Test that it's gone
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[8], STAMPS[9], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[8], VALUES[9], VALUES[13], VALUES[15] }, 0, 9);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[8], STAMPS[9], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6], VALUES[8], VALUES[9], VALUES[13], VALUES[15] }, 0, 9);
    }

    @Test
    public void testVersionLimits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = makeNAscii(TestFromClientSide.FAMILY, 3);
        int[] LIMITS = new int[]{ 1, 3, 5 };
        long[] STAMPS = makeStamps(10);
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 10);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES, LIMITS);
        // Insert limit + 1 on each family
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
        ht.put(put);
        // Verify we only get the right number out of each
        // Family0
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        Result result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[0]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        Scan scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addFamily(FAMILIES[0]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        // Family1
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[1]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addFamily(FAMILIES[1]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // Family2
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6] }, 0, 4);
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6] }, 0, 4);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6] }, 0, 4);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[4], VALUES[5], VALUES[6] }, 0, 4);
        // Try all families
        get = new Get(TestFromClientSide.ROW);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[0]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        get.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        get.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
        scan = new Scan(TestFromClientSide.ROW);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
        scan = new Scan(TestFromClientSide.ROW);
        scan.setMaxVersions(Integer.MAX_VALUE);
        scan.addFamily(FAMILIES[0]);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
        scan = new Scan(TestFromClientSide.ROW);
        scan.setMaxVersions(Integer.MAX_VALUE);
        scan.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        scan.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        scan.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 9 keys but received " + (result.size())), ((result.size()) == 9));
    }

    @Test
    public void testDeleteFamilyVersion() throws Exception {
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] QUALIFIERS = makeNAscii(TestFromClientSide.QUALIFIER, 1);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 5);
        Put put = new Put(TestFromClientSide.ROW);
        for (int q = 0; q < 1; q++)
            for (int t = 0; t < 5; t++)
                put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[q], ts[t], VALUES[t]);


        ht.put(put);
        admin.flush(tableName);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addFamilyVersion(TestFromClientSide.FAMILY, ts[1]);// delete version '2000'

        delete.addFamilyVersion(TestFromClientSide.FAMILY, ts[3]);// delete version '4000'

        ht.delete(delete);
        admin.flush(tableName);
        for (int i = 0; i < 1; i++) {
            Get get = new Get(TestFromClientSide.ROW);
            get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i]);
            get.setMaxVersions(Integer.MAX_VALUE);
            Result result = ht.get(get);
            // verify version '1000'/'3000'/'5000' remains for all columns
            assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, QUALIFIERS[i], new long[]{ ts[0], ts[2], ts[4] }, new byte[][]{ VALUES[0], VALUES[2], VALUES[4] }, 0, 2);
        }
        ht.close();
        admin.close();
    }

    @Test
    public void testDeleteFamilyVersionWithOtherDeletes() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] QUALIFIERS = makeNAscii(TestFromClientSide.QUALIFIER, 5);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 5);
        Put put = null;
        Result result = null;
        Get get = null;
        Delete delete = null;
        // 1. put on ROW
        put = new Put(TestFromClientSide.ROW);
        for (int q = 0; q < 5; q++)
            for (int t = 0; t < 5; t++)
                put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[q], ts[t], VALUES[t]);


        ht.put(put);
        admin.flush(tableName);
        // 2. put on ROWS[0]
        byte[] ROW2 = Bytes.toBytes("myRowForTest");
        put = new Put(ROW2);
        for (int q = 0; q < 5; q++)
            for (int t = 0; t < 5; t++)
                put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[q], ts[t], VALUES[t]);


        ht.put(put);
        admin.flush(tableName);
        // 3. delete on ROW
        delete = new Delete(TestFromClientSide.ROW);
        // delete version <= 2000 of all columns
        // note: addFamily must be the first since it will mask
        // the subsequent other type deletes!
        delete.addFamily(TestFromClientSide.FAMILY, ts[1]);
        // delete version '4000' of all columns
        delete.addFamilyVersion(TestFromClientSide.FAMILY, ts[3]);
        // delete version <= 3000 of column 0
        delete.addColumns(TestFromClientSide.FAMILY, QUALIFIERS[0], ts[2]);
        // delete version <= 5000 of column 2
        delete.addColumns(TestFromClientSide.FAMILY, QUALIFIERS[2], ts[4]);
        // delete version 5000 of column 4
        delete.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[4], ts[4]);
        ht.delete(delete);
        admin.flush(tableName);
        // 4. delete on ROWS[0]
        delete = new Delete(ROW2);
        delete.addFamilyVersion(TestFromClientSide.FAMILY, ts[1]);// delete version '2000'

        delete.addFamilyVersion(TestFromClientSide.FAMILY, ts[3]);// delete version '4000'

        ht.delete(delete);
        admin.flush(tableName);
        // 5. check ROW
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[0]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, QUALIFIERS[0], new long[]{ ts[4] }, new byte[][]{ VALUES[4] }, 0, 0);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[1]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, QUALIFIERS[1], new long[]{ ts[2], ts[4] }, new byte[][]{ VALUES[2], VALUES[4] }, 0, 1);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertEquals(0, result.size());
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[3]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, QUALIFIERS[3], new long[]{ ts[2], ts[4] }, new byte[][]{ VALUES[2], VALUES[4] }, 0, 1);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[4]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, QUALIFIERS[4], new long[]{ ts[2] }, new byte[][]{ VALUES[2] }, 0, 0);
        // 6. check ROWS[0]
        for (int i = 0; i < 5; i++) {
            get = new Get(ROW2);
            get.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i]);
            get.setMaxVersions(Integer.MAX_VALUE);
            result = ht.get(get);
            // verify version '1000'/'3000'/'5000' remains for all columns
            assertNResult(result, ROW2, TestFromClientSide.FAMILY, QUALIFIERS[i], new long[]{ ts[0], ts[2], ts[4] }, new byte[][]{ VALUES[0], VALUES[2], VALUES[4] }, 0, 2);
        }
        ht.close();
        admin.close();
    }

    @Test
    public void testDeleteWithFailed() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = makeNAscii(TestFromClientSide.FAMILY, 3);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES, 3);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        ht.put(put);
        // delete wrong family
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addFamily(FAMILIES[1], ts[0]);
        ht.delete(delete);
        Get get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[0]);
        get.readAllVersions();
        Result result = ht.get(get);
        Assert.assertTrue(Bytes.equals(result.getValue(FAMILIES[0], TestFromClientSide.QUALIFIER), VALUES[0]));
    }

    @Test
    public void testDeletes() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] ROWS = makeNAscii(TestFromClientSide.ROW, 6);
        byte[][] FAMILIES = makeNAscii(TestFromClientSide.FAMILY, 3);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES, 3);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        ht.put(put);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addFamily(FAMILIES[0], ts[0]);
        ht.delete(delete);
        Get get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[0]);
        get.setMaxVersions(Integer.MAX_VALUE);
        Result result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        Scan scan = new Scan(TestFromClientSide.ROW);
        scan.addFamily(FAMILIES[0]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        // Test delete latest version
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[4], VALUES[4]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        put.addColumn(FAMILIES[0], null, ts[4], VALUES[4]);
        put.addColumn(FAMILIES[0], null, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[0], null, ts[3], VALUES[3]);
        ht.put(put);
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);// ts[4]

        ht.delete(delete);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // Test for HBASE-1847
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(FAMILIES[0], null);
        ht.delete(delete);
        // Cleanup null qualifier
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(FAMILIES[0], null);
        ht.delete(delete);
        // Expected client behavior might be that you can re-put deleted values
        // But alas, this is not to be.  We can't put them back in either case.
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);// 1000

        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[4], VALUES[4]);// 5000

        ht.put(put);
        // It used to be due to the internal implementation of Get, that
        // the Get() call would return ts[4] UNLIKE the Scan below. With
        // the switch to using Scan for Get this is no longer the case.
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(FAMILIES[0]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // The Scanner returns the previous values, the expected-naive-unexpected behavior
        scan = new Scan(TestFromClientSide.ROW);
        scan.addFamily(FAMILIES[0]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // Test deleting an entire family from one row but not the other various ways
        put = new Put(ROWS[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        put = new Put(ROWS[1]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        put = new Put(ROWS[2]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        // Assert that above went in.
        get = new Get(ROWS[2]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(((("Expected 4 key but received " + (result.size())) + ": ") + result), ((result.size()) == 4));
        delete = new Delete(ROWS[0]);
        delete.addFamily(FAMILIES[2]);
        ht.delete(delete);
        delete = new Delete(ROWS[1]);
        delete.addColumns(FAMILIES[1], TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        delete = new Delete(ROWS[2]);
        delete.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        delete.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        delete.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        get = new Get(ROWS[0]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        assertNResult(result, ROWS[0], FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1] }, new byte[][]{ VALUES[0], VALUES[1] }, 0, 1);
        scan = new Scan(ROWS[0]);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        assertNResult(result, ROWS[0], FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1] }, new byte[][]{ VALUES[0], VALUES[1] }, 0, 1);
        get = new Get(ROWS[1]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        scan = new Scan(ROWS[1]);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        get = new Get(ROWS[2]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertEquals(1, result.size());
        assertNResult(result, ROWS[2], FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ ts[2] }, new byte[][]{ VALUES[2] }, 0, 0);
        scan = new Scan(ROWS[2]);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertEquals(1, result.size());
        assertNResult(result, ROWS[2], FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ ts[2] }, new byte[][]{ VALUES[2] }, 0, 0);
        // Test if we delete the family first in one row (HBASE-1541)
        delete = new Delete(ROWS[3]);
        delete.addFamily(FAMILIES[1]);
        ht.delete(delete);
        put = new Put(ROWS[3]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, VALUES[0]);
        ht.put(put);
        put = new Put(ROWS[4]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, VALUES[2]);
        ht.put(put);
        get = new Get(ROWS[3]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 1 key but received " + (result.size())), ((result.size()) == 1));
        get = new Get(ROWS[4]);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        scan = new Scan(ROWS[3]);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        ResultScanner scanner = ht.getScanner(scan);
        result = scanner.next();
        Assert.assertTrue(("Expected 1 key but received " + (result.size())), ((result.size()) == 1));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[0]), ROWS[3]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[0]), VALUES[0]));
        result = scanner.next();
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[0]), ROWS[4]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[1]), ROWS[4]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[0]), VALUES[1]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[1]), VALUES[2]));
        scanner.close();
        // Add test of bulk deleting.
        for (int i = 0; i < 10; i++) {
            byte[] bytes = Bytes.toBytes(i);
            put = new Put(bytes);
            put.setDurability(SKIP_WAL);
            put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, bytes);
            ht.put(put);
        }
        for (int i = 0; i < 10; i++) {
            byte[] bytes = Bytes.toBytes(i);
            get = new Get(bytes);
            get.addFamily(FAMILIES[0]);
            result = ht.get(get);
            Assert.assertTrue(((result.size()) == 1));
        }
        ArrayList<Delete> deletes = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] bytes = Bytes.toBytes(i);
            delete = new Delete(bytes);
            delete.addFamily(FAMILIES[0]);
            deletes.add(delete);
        }
        ht.delete(deletes);
        for (int i = 0; i < 10; i++) {
            byte[] bytes = Bytes.toBytes(i);
            get = new Get(bytes);
            get.addFamily(FAMILIES[0]);
            result = ht.get(get);
            Assert.assertTrue(result.isEmpty());
        }
    }

    /**
     * Test batch operations with combination of valid and invalid args
     */
    @Test
    public void testBatchOperationsWithErrors() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table foo = TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ TestFromClientSide.FAMILY }, 10);
        int NUM_OPS = 100;
        int FAILED_OPS = 50;
        RetriesExhaustedWithDetailsException expectedException = null;
        IllegalArgumentException iae = null;
        // 1.1 Put with no column families (local validation, runtime exception)
        List<Put> puts = new ArrayList<Put>(NUM_OPS);
        for (int i = 0; i != NUM_OPS; i++) {
            Put put = new Put(Bytes.toBytes(i));
            puts.add(put);
        }
        try {
            foo.put(puts);
        } catch (IllegalArgumentException e) {
            iae = e;
        }
        Assert.assertNotNull(iae);
        Assert.assertEquals(NUM_OPS, puts.size());
        // 1.2 Put with invalid column family
        iae = null;
        puts.clear();
        for (int i = 0; i != NUM_OPS; i++) {
            Put put = new Put(Bytes.toBytes(i));
            put.addColumn(((i % 2) == 0 ? TestFromClientSide.FAMILY : TestFromClientSide.INVALID_FAMILY), TestFromClientSide.FAMILY, Bytes.toBytes(i));
            puts.add(put);
        }
        try {
            foo.put(puts);
        } catch (RetriesExhaustedWithDetailsException e) {
            expectedException = e;
        }
        Assert.assertNotNull(expectedException);
        Assert.assertEquals(FAILED_OPS, expectedException.exceptions.size());
        Assert.assertTrue(expectedException.actions.contains(puts.get(1)));
        // 2.1 Get non-existent rows
        List<Get> gets = new ArrayList<>(NUM_OPS);
        for (int i = 0; i < NUM_OPS; i++) {
            Get get = new Get(Bytes.toBytes(i));
            // get.addColumn(FAMILY, FAMILY);
            gets.add(get);
        }
        Result[] getsResult = foo.get(gets);
        Assert.assertNotNull(getsResult);
        Assert.assertEquals(NUM_OPS, getsResult.length);
        Assert.assertNull(getsResult[1].getRow());
        // 2.2 Get with invalid column family
        gets.clear();
        getsResult = null;
        expectedException = null;
        for (int i = 0; i < NUM_OPS; i++) {
            Get get = new Get(Bytes.toBytes(i));
            get.addColumn(((i % 2) == 0 ? TestFromClientSide.FAMILY : TestFromClientSide.INVALID_FAMILY), TestFromClientSide.FAMILY);
            gets.add(get);
        }
        try {
            getsResult = foo.get(gets);
        } catch (RetriesExhaustedWithDetailsException e) {
            expectedException = e;
        }
        Assert.assertNull(getsResult);
        Assert.assertNotNull(expectedException);
        Assert.assertEquals(FAILED_OPS, expectedException.exceptions.size());
        Assert.assertTrue(expectedException.actions.contains(gets.get(1)));
        // 3.1 Delete with invalid column family
        expectedException = null;
        List<Delete> deletes = new ArrayList<>(NUM_OPS);
        for (int i = 0; i < NUM_OPS; i++) {
            Delete delete = new Delete(Bytes.toBytes(i));
            delete.addColumn(((i % 2) == 0 ? TestFromClientSide.FAMILY : TestFromClientSide.INVALID_FAMILY), TestFromClientSide.FAMILY);
            deletes.add(delete);
        }
        try {
            foo.delete(deletes);
        } catch (RetriesExhaustedWithDetailsException e) {
            expectedException = e;
        }
        Assert.assertEquals((NUM_OPS - FAILED_OPS), deletes.size());
        Assert.assertNotNull(expectedException);
        Assert.assertEquals(FAILED_OPS, expectedException.exceptions.size());
        Assert.assertTrue(expectedException.actions.contains(deletes.get(1)));
        // 3.2 Delete non-existent rows
        deletes.clear();
        for (int i = 0; i < NUM_OPS; i++) {
            Delete delete = new Delete(Bytes.toBytes(i));
            deletes.add(delete);
        }
        foo.delete(deletes);
        Assert.assertTrue(deletes.isEmpty());
    }

    // 
    // JIRA Testers
    // 
    /**
     * HBASE-867
     *    If millions of columns in a column family, hbase scanner won't come up
     *
     *    Test will create numRows rows, each with numColsPerRow columns
     *    (1 version each), and attempt to scan them all.
     *
     *    To test at scale, up numColsPerRow to the millions
     *    (have not gotten that to work running as junit though)
     */
    @Test
    public void testJiraTest867() throws Exception {
        int numRows = 10;
        int numColsPerRow = 2000;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] ROWS = makeN(TestFromClientSide.ROW, numRows);
        byte[][] QUALIFIERS = makeN(TestFromClientSide.QUALIFIER, numColsPerRow);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Insert rows
        for (int i = 0; i < numRows; i++) {
            Put put = new Put(ROWS[i]);
            put.setDurability(SKIP_WAL);
            for (int j = 0; j < numColsPerRow; j++) {
                put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[j], QUALIFIERS[j]);
            }
            Assert.assertTrue((((("Put expected to contain " + numColsPerRow) + " columns but ") + "only contains ") + (put.size())), ((put.size()) == numColsPerRow));
            ht.put(put);
        }
        // Get a row
        Get get = new Get(ROWS[(numRows - 1)]);
        Result result = ht.get(get);
        assertNumKeys(result, numColsPerRow);
        Cell[] keys = result.rawCells();
        for (int i = 0; i < (result.size()); i++) {
            assertKey(keys[i], ROWS[(numRows - 1)], TestFromClientSide.FAMILY, QUALIFIERS[i], QUALIFIERS[i]);
        }
        // Scan the rows
        Scan scan = new Scan();
        ResultScanner scanner = ht.getScanner(scan);
        int rowCount = 0;
        while ((result = scanner.next()) != null) {
            assertNumKeys(result, numColsPerRow);
            Cell[] kvs = result.rawCells();
            for (int i = 0; i < numColsPerRow; i++) {
                assertKey(kvs[i], ROWS[rowCount], TestFromClientSide.FAMILY, QUALIFIERS[i], QUALIFIERS[i]);
            }
            rowCount++;
        } 
        scanner.close();
        Assert.assertTrue((((("Expected to scan " + numRows) + " rows but actually scanned ") + rowCount) + " rows"), (rowCount == numRows));
        // flush and try again
        TestFromClientSide.TEST_UTIL.flush();
        // Get a row
        get = new Get(ROWS[(numRows - 1)]);
        result = ht.get(get);
        assertNumKeys(result, numColsPerRow);
        keys = result.rawCells();
        for (int i = 0; i < (result.size()); i++) {
            assertKey(keys[i], ROWS[(numRows - 1)], TestFromClientSide.FAMILY, QUALIFIERS[i], QUALIFIERS[i]);
        }
        // Scan the rows
        scan = new Scan();
        scanner = ht.getScanner(scan);
        rowCount = 0;
        while ((result = scanner.next()) != null) {
            assertNumKeys(result, numColsPerRow);
            Cell[] kvs = result.rawCells();
            for (int i = 0; i < numColsPerRow; i++) {
                assertKey(kvs[i], ROWS[rowCount], TestFromClientSide.FAMILY, QUALIFIERS[i], QUALIFIERS[i]);
            }
            rowCount++;
        } 
        scanner.close();
        Assert.assertTrue((((("Expected to scan " + numRows) + " rows but actually scanned ") + rowCount) + " rows"), (rowCount == numRows));
    }

    /**
     * HBASE-861
     *    get with timestamp will return a value if there is a version with an
     *    earlier timestamp
     */
    @Test
    public void testJiraTest861() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 7);
        long[] STAMPS = makeStamps(7);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert three versions
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        ht.put(put);
        // Get the middle value
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        // Try to get one version before (expect fail)
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1]);
        // Try to get one version after (expect fail)
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5]);
        // Try same from storefile
        TestFromClientSide.TEST_UTIL.flush();
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5]);
        // Insert two more versions surrounding others, into memstore
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
        ht.put(put);
        // Check we can get everything we should and can't get what we shouldn't
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
        // Try same from two storefiles
        TestFromClientSide.TEST_UTIL.flush();
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
    }

    /**
     * HBASE-33
     *    Add a HTable get/obtainScanner method that retrieves all versions of a
     *    particular column and row between two timestamps
     */
    @Test
    public void testJiraTest33() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 7);
        long[] STAMPS = makeStamps(7);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert lots versions
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        ht.put(put);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 2);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 3);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 2);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 3);
        // Try same from storefile
        TestFromClientSide.TEST_UTIL.flush();
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 2);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        getVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 3);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 2);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        scanVersionRangeAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 3);
    }

    /**
     * HBASE-1014
     *    commit(BatchUpdate) method should return timestamp
     */
    @Test
    public void testJiraTest1014() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        long manualStamp = 12345;
        // Insert lots versions
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, manualStamp, TestFromClientSide.VALUE);
        ht.put(put);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, manualStamp, TestFromClientSide.VALUE);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, (manualStamp - 1));
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, (manualStamp + 1));
    }

    /**
     * HBASE-1182
     *    Scan for columns > some timestamp
     */
    @Test
    public void testJiraTest1182() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 7);
        long[] STAMPS = makeStamps(7);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert lots versions
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        ht.put(put);
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 5);
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        // Try same from storefile
        TestFromClientSide.TEST_UTIL.flush();
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 5);
        getVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 2, 5);
        scanVersionRangeAndVerifyGreaterThan(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 4, 5);
    }

    /**
     * HBASE-52
     *    Add a means of scanning over all versions
     */
    @Test
    public void testJiraTest52() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 7);
        long[] STAMPS = makeStamps(7);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert lots versions
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0], VALUES[0]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        ht.put(put);
        getAllVersionsAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanAllVersionsAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        // Try same from storefile
        TestFromClientSide.TEST_UTIL.flush();
        getAllVersionsAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
        scanAllVersionsAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS, VALUES, 0, 5);
    }

    @Test
    public void testDuplicateVersions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        long[] STAMPS = makeStamps(20);
        byte[][] VALUES = makeNAscii(TestFromClientSide.VALUE, 20);
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Insert 4 versions of same column
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        ht.put(put);
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        // Ensure maxVersions in query is respected
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(2);
        Result result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        Scan scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(2);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        // Flush and redo
        TestFromClientSide.TEST_UTIL.flush();
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[4]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[5], VALUES[5]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6]);
        // Ensure maxVersions in query is respected
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(2);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(2);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[4], STAMPS[5] }, new byte[][]{ VALUES[4], VALUES[5] }, 0, 1);
        // Add some memstore and retest
        // Insert 4 more versions of same column and a dupe
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[3], VALUES[3]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[14]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[6], VALUES[6]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[8], VALUES[8]);
        ht.put(put);
        // Ensure maxVersions in query is respected
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(7);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 6);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(7);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 6);
        get = new Get(TestFromClientSide.ROW);
        get.setMaxVersions(7);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 6);
        scan = new Scan(TestFromClientSide.ROW);
        scan.setMaxVersions(7);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8] }, new byte[][]{ VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8] }, 0, 6);
        // Verify we can get each one properly
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[14]);
        getVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[1], VALUES[1]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[2], VALUES[2]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[4], VALUES[14]);
        scanVersionAndVerify(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7], VALUES[7]);
        // Verify we don't accidentally get others
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        getVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[0]);
        scanVersionAndVerifyMissing(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9]);
        // Ensure maxVersions of table is respected
        TestFromClientSide.TEST_UTIL.flush();
        // Insert 4 more versions of same column and a dupe
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[9], VALUES[9]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[11], VALUES[11]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[13], VALUES[13]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[15], VALUES[15]);
        ht.put(put);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8], STAMPS[9], STAMPS[11], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8], VALUES[9], VALUES[11], VALUES[13], VALUES[15] }, 0, 9);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[7], STAMPS[8], STAMPS[9], STAMPS[11], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[7], VALUES[8], VALUES[9], VALUES[11], VALUES[13], VALUES[15] }, 0, 9);
        // Delete a version in the memstore and a version in a storefile
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[11]);
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, STAMPS[7]);
        ht.delete(delete);
        // Test that it's gone
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[8], STAMPS[9], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[8], VALUES[9], VALUES[13], VALUES[15] }, 0, 9);
        scan = new Scan(TestFromClientSide.ROW);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ STAMPS[1], STAMPS[2], STAMPS[3], STAMPS[4], STAMPS[5], STAMPS[6], STAMPS[8], STAMPS[9], STAMPS[13], STAMPS[15] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3], VALUES[14], VALUES[5], VALUES[6], VALUES[8], VALUES[9], VALUES[13], VALUES[15] }, 0, 9);
    }

    @Test
    public void testUpdates() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table hTable = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        // Write a column with values at timestamp 1, 2 and 3
        byte[] row = Bytes.toBytes("row1");
        byte[] qualifier = Bytes.toBytes("myCol");
        Put put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("AAA"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("BBB"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 3L, Bytes.toBytes("EEE"));
        hTable.put(put);
        Get get = new Get(row);
        get.addColumn(TestFromClientSide.FAMILY, qualifier);
        get.setMaxVersions();
        // Check that the column indeed has the right values at timestamps 1 and
        // 2
        Result result = hTable.get(get);
        NavigableMap<Long, byte[]> navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("AAA", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("BBB", Bytes.toString(navigableMap.get(2L)));
        // Update the value at timestamp 1
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("CCC"));
        hTable.put(put);
        // Update the value at timestamp 2
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("DDD"));
        hTable.put(put);
        // Check that the values at timestamp 2 and 1 got updated
        result = hTable.get(get);
        navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("CCC", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("DDD", Bytes.toString(navigableMap.get(2L)));
    }

    @Test
    public void testUpdatesWithMajorCompaction() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table hTable = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        // Write a column with values at timestamp 1, 2 and 3
        byte[] row = Bytes.toBytes("row2");
        byte[] qualifier = Bytes.toBytes("myCol");
        Put put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("AAA"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("BBB"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 3L, Bytes.toBytes("EEE"));
        hTable.put(put);
        Get get = new Get(row);
        get.addColumn(TestFromClientSide.FAMILY, qualifier);
        get.setMaxVersions();
        // Check that the column indeed has the right values at timestamps 1 and
        // 2
        Result result = hTable.get(get);
        NavigableMap<Long, byte[]> navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("AAA", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("BBB", Bytes.toString(navigableMap.get(2L)));
        // Trigger a major compaction
        admin.flush(tableName);
        admin.majorCompact(tableName);
        Thread.sleep(6000);
        // Update the value at timestamp 1
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("CCC"));
        hTable.put(put);
        // Update the value at timestamp 2
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("DDD"));
        hTable.put(put);
        // Trigger a major compaction
        admin.flush(tableName);
        admin.majorCompact(tableName);
        Thread.sleep(6000);
        // Check that the values at timestamp 2 and 1 got updated
        result = hTable.get(get);
        navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("CCC", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("DDD", Bytes.toString(navigableMap.get(2L)));
    }

    @Test
    public void testMajorCompactionBetweenTwoUpdates() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table hTable = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 10);
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        // Write a column with values at timestamp 1, 2 and 3
        byte[] row = Bytes.toBytes("row3");
        byte[] qualifier = Bytes.toBytes("myCol");
        Put put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("AAA"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("BBB"));
        hTable.put(put);
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 3L, Bytes.toBytes("EEE"));
        hTable.put(put);
        Get get = new Get(row);
        get.addColumn(TestFromClientSide.FAMILY, qualifier);
        get.setMaxVersions();
        // Check that the column indeed has the right values at timestamps 1 and
        // 2
        Result result = hTable.get(get);
        NavigableMap<Long, byte[]> navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("AAA", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("BBB", Bytes.toString(navigableMap.get(2L)));
        // Trigger a major compaction
        admin.flush(tableName);
        admin.majorCompact(tableName);
        Thread.sleep(6000);
        // Update the value at timestamp 1
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 1L, Bytes.toBytes("CCC"));
        hTable.put(put);
        // Trigger a major compaction
        admin.flush(tableName);
        admin.majorCompact(tableName);
        Thread.sleep(6000);
        // Update the value at timestamp 2
        put = new Put(row);
        put.addColumn(TestFromClientSide.FAMILY, qualifier, 2L, Bytes.toBytes("DDD"));
        hTable.put(put);
        // Trigger a major compaction
        admin.flush(tableName);
        admin.majorCompact(tableName);
        Thread.sleep(6000);
        // Check that the values at timestamp 2 and 1 got updated
        result = hTable.get(get);
        navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(qualifier);
        Assert.assertEquals("CCC", Bytes.toString(navigableMap.get(1L)));
        Assert.assertEquals("DDD", Bytes.toString(navigableMap.get(2L)));
    }

    @Test
    public void testGet_EmptyTable() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Get get = new Get(TestFromClientSide.ROW);
        get.addFamily(TestFromClientSide.FAMILY);
        Result r = table.get(get);
        Assert.assertTrue(r.isEmpty());
    }

    @Test
    public void testGet_NullQualifier() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        table.put(put);
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
        table.put(put);
        TestFromClientSide.LOG.info("Row put");
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, null);
        Result r = table.get(get);
        Assert.assertEquals(1, r.size());
        get = new Get(TestFromClientSide.ROW);
        get.addFamily(TestFromClientSide.FAMILY);
        r = table.get(get);
        Assert.assertEquals(2, r.size());
    }

    @Test
    public void testGet_NonExistentRow() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        table.put(put);
        TestFromClientSide.LOG.info("Row put");
        Get get = new Get(TestFromClientSide.ROW);
        get.addFamily(TestFromClientSide.FAMILY);
        Result r = table.get(get);
        Assert.assertFalse(r.isEmpty());
        System.out.println("Row retrieved successfully");
        byte[] missingrow = Bytes.toBytes("missingrow");
        get = new Get(missingrow);
        get.addFamily(TestFromClientSide.FAMILY);
        r = table.get(get);
        Assert.assertTrue(r.isEmpty());
        TestFromClientSide.LOG.info("Row missing as it should be");
    }

    @Test
    public void testPut() throws IOException {
        final byte[] CONTENTS_FAMILY = Bytes.toBytes("contents");
        final byte[] SMALL_FAMILY = Bytes.toBytes("smallfam");
        final byte[] row1 = Bytes.toBytes("row1");
        final byte[] row2 = Bytes.toBytes("row2");
        final byte[] value = Bytes.toBytes("abcd");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ CONTENTS_FAMILY, SMALL_FAMILY });
        Put put = new Put(row1);
        put.addColumn(CONTENTS_FAMILY, null, value);
        table.put(put);
        put = new Put(row2);
        put.addColumn(CONTENTS_FAMILY, null, value);
        Assert.assertEquals(1, put.size());
        Assert.assertEquals(1, put.getFamilyCellMap().get(CONTENTS_FAMILY).size());
        // KeyValue v1 expectation.  Cast for now until we go all Cell all the time. TODO
        KeyValue kv = ((KeyValue) (put.getFamilyCellMap().get(CONTENTS_FAMILY).get(0)));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneFamily(kv), CONTENTS_FAMILY));
        // will it return null or an empty byte array?
        Assert.assertTrue(Bytes.equals(CellUtil.cloneQualifier(kv), new byte[0]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(kv), value));
        table.put(put);
        Scan scan = new Scan();
        scan.addColumn(CONTENTS_FAMILY, null);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell key : r.rawCells()) {
                System.out.println((((Bytes.toString(r.getRow())) + ": ") + (key.toString())));
            }
        }
    }

    @Test
    public void testPutNoCF() throws IOException {
        final byte[] BAD_FAM = Bytes.toBytes("BAD_CF");
        final byte[] VAL = Bytes.toBytes(100);
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        boolean caughtNSCFE = false;
        try {
            Put p = new Put(TestFromClientSide.ROW);
            p.addColumn(BAD_FAM, TestFromClientSide.QUALIFIER, VAL);
            table.put(p);
        } catch (Exception e) {
            caughtNSCFE = e instanceof NoSuchColumnFamilyException;
        }
        Assert.assertTrue("Should throw NoSuchColumnFamilyException", caughtNSCFE);
    }

    @Test
    public void testRowsPut() throws IOException {
        final byte[] CONTENTS_FAMILY = Bytes.toBytes("contents");
        final byte[] SMALL_FAMILY = Bytes.toBytes("smallfam");
        final int NB_BATCH_ROWS = 10;
        final byte[] value = Bytes.toBytes("abcd");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ CONTENTS_FAMILY, SMALL_FAMILY });
        ArrayList<Put> rowsUpdate = new ArrayList<Put>();
        for (int i = 0; i < NB_BATCH_ROWS; i++) {
            byte[] row = Bytes.toBytes(("row" + i));
            Put put = new Put(row);
            put.setDurability(SKIP_WAL);
            put.addColumn(CONTENTS_FAMILY, null, value);
            rowsUpdate.add(put);
        }
        table.put(rowsUpdate);
        Scan scan = new Scan();
        scan.addFamily(CONTENTS_FAMILY);
        ResultScanner scanner = table.getScanner(scan);
        int nbRows = 0;
        for (@SuppressWarnings("unused")
        Result row : scanner)
            nbRows++;

        Assert.assertEquals(NB_BATCH_ROWS, nbRows);
    }

    @Test
    public void testRowsPutBufferedManyManyFlushes() throws IOException {
        final byte[] CONTENTS_FAMILY = Bytes.toBytes("contents");
        final byte[] SMALL_FAMILY = Bytes.toBytes("smallfam");
        final byte[] value = Bytes.toBytes("abcd");
        final int NB_BATCH_ROWS = 10;
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ CONTENTS_FAMILY, SMALL_FAMILY });
        ArrayList<Put> rowsUpdate = new ArrayList<Put>();
        for (int i = 0; i < (NB_BATCH_ROWS * 10); i++) {
            byte[] row = Bytes.toBytes(("row" + i));
            Put put = new Put(row);
            put.setDurability(SKIP_WAL);
            put.addColumn(CONTENTS_FAMILY, null, value);
            rowsUpdate.add(put);
        }
        table.put(rowsUpdate);
        Scan scan = new Scan();
        scan.addFamily(CONTENTS_FAMILY);
        ResultScanner scanner = table.getScanner(scan);
        int nbRows = 0;
        for (@SuppressWarnings("unused")
        Result row : scanner)
            nbRows++;

        Assert.assertEquals((NB_BATCH_ROWS * 10), nbRows);
    }

    @Test
    public void testAddKeyValue() throws IOException {
        final byte[] CONTENTS_FAMILY = Bytes.toBytes("contents");
        final byte[] value = Bytes.toBytes("abcd");
        final byte[] row1 = Bytes.toBytes("row1");
        final byte[] row2 = Bytes.toBytes("row2");
        byte[] qualifier = Bytes.toBytes("qf1");
        Put put = new Put(row1);
        // Adding KeyValue with the same row
        KeyValue kv = new KeyValue(row1, CONTENTS_FAMILY, qualifier, value);
        boolean ok = true;
        try {
            put.add(kv);
        } catch (IOException e) {
            ok = false;
        }
        Assert.assertEquals(true, ok);
        // Adding KeyValue with the different row
        kv = new KeyValue(row2, CONTENTS_FAMILY, qualifier, value);
        ok = false;
        try {
            put.add(kv);
        } catch (IOException e) {
            ok = true;
        }
        Assert.assertEquals(true, ok);
    }

    /**
     * test for HBASE-737
     */
    @Test
    public void testHBase737() throws IOException {
        final byte[] FAM1 = Bytes.toBytes("fam1");
        final byte[] FAM2 = Bytes.toBytes("fam2");
        // Open table
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ FAM1, FAM2 });
        // Insert some values
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAM1, Bytes.toBytes("letters"), Bytes.toBytes("abcdefg"));
        table.put(put);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException i) {
            // ignore
        }
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAM1, Bytes.toBytes("numbers"), Bytes.toBytes("123456"));
        table.put(put);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException i) {
            // ignore
        }
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAM2, Bytes.toBytes("letters"), Bytes.toBytes("hijklmnop"));
        table.put(put);
        long[] times = new long[3];
        // First scan the memstore
        Scan scan = new Scan();
        scan.addFamily(FAM1);
        scan.addFamily(FAM2);
        ResultScanner s = table.getScanner(scan);
        try {
            int index = 0;
            Result r = null;
            while ((r = s.next()) != null) {
                for (Cell key : r.rawCells()) {
                    times[(index++)] = key.getTimestamp();
                }
            } 
        } finally {
            s.close();
        }
        for (int i = 0; i < ((times.length) - 1); i++) {
            for (int j = i + 1; j < (times.length); j++) {
                Assert.assertTrue(((times[j]) > (times[i])));
            }
        }
        // Flush data to disk and try again
        TestFromClientSide.TEST_UTIL.flush();
        // Reset times
        for (int i = 0; i < (times.length); i++) {
            times[i] = 0;
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException i) {
            // ignore
        }
        scan = new Scan();
        scan.addFamily(FAM1);
        scan.addFamily(FAM2);
        s = table.getScanner(scan);
        try {
            int index = 0;
            Result r = null;
            while ((r = s.next()) != null) {
                for (Cell key : r.rawCells()) {
                    times[(index++)] = key.getTimestamp();
                }
            } 
        } finally {
            s.close();
        }
        for (int i = 0; i < ((times.length) - 1); i++) {
            for (int j = i + 1; j < (times.length); j++) {
                Assert.assertTrue(((times[j]) > (times[i])));
            }
        }
    }

    @Test
    public void testListTables() throws IOException, InterruptedException {
        final TableName tableName1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName tableName2 = TableName.valueOf(((name.getMethodName()) + "2"));
        final TableName tableName3 = TableName.valueOf(((name.getMethodName()) + "3"));
        TableName[] tables = new TableName[]{ tableName1, tableName2, tableName3 };
        for (int i = 0; i < (tables.length); i++) {
            TestFromClientSide.TEST_UTIL.createTable(tables[i], TestFromClientSide.FAMILY);
        }
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        HTableDescriptor[] ts = admin.listTables();
        HashSet<HTableDescriptor> result = new HashSet<HTableDescriptor>(ts.length);
        Collections.addAll(result, ts);
        int size = result.size();
        Assert.assertTrue((size >= (tables.length)));
        for (int i = 0; (i < (tables.length)) && (i < size); i++) {
            boolean found = false;
            for (int j = 0; j < (ts.length); j++) {
                if (ts[j].getTableName().equals(tables[i])) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(("Not found: " + (tables[i])), found);
        }
    }

    /**
     * simple test that just executes parts of the client
     * API that accept a pre-created Connection instance
     */
    @Test
    public void testUnmanagedHConnection() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestFromClientSide.TEST_UTIL.createTable(tableName, CATALOG_FAMILY);
        Connection conn = ConnectionFactory.createConnection(TestFromClientSide.TEST_UTIL.getConfiguration());
        Table t = conn.getTable(tableName);
        Admin admin = conn.getAdmin();
        Assert.assertTrue(admin.tableExists(tableName));
        Assert.assertTrue(t.get(new Get(TestFromClientSide.ROW)).isEmpty());
        admin.close();
    }

    /**
     * test of that unmanaged HConnections are able to reconnect
     * properly (see HBASE-5058)
     */
    @Test
    public void testUnmanagedHConnectionReconnect() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestFromClientSide.TEST_UTIL.createTable(tableName, CATALOG_FAMILY);
        Connection conn = ConnectionFactory.createConnection(TestFromClientSide.TEST_UTIL.getConfiguration());
        Table t = conn.getTable(tableName);
        try (Admin admin = conn.getAdmin()) {
            Assert.assertTrue(admin.tableExists(tableName));
            Assert.assertTrue(t.get(new Get(TestFromClientSide.ROW)).isEmpty());
        }
        // stop the master
        MiniHBaseCluster cluster = TestFromClientSide.TEST_UTIL.getHBaseCluster();
        cluster.stopMaster(0, false);
        cluster.waitOnMaster(0);
        // start up a new master
        cluster.startMaster();
        Assert.assertTrue(cluster.waitForActiveAndReadyMaster());
        // test that the same unmanaged connection works with a new
        // Admin and can connect to the new master;
        boolean tablesOnMaster = LoadBalancer.isTablesOnMaster(TestFromClientSide.TEST_UTIL.getConfiguration());
        try (Admin admin = conn.getAdmin()) {
            Assert.assertTrue(admin.tableExists(tableName));
            Assert.assertTrue(((admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().size()) == ((TestFromClientSide.SLAVES) + (tablesOnMaster ? 1 : 0))));
        }
    }

    @Test
    public void testMiscHTableStuff() throws IOException {
        final TableName tableAname = TableName.valueOf(((name.getMethodName()) + "A"));
        final TableName tableBname = TableName.valueOf(((name.getMethodName()) + "B"));
        final byte[] attrName = Bytes.toBytes("TESTATTR");
        final byte[] attrValue = Bytes.toBytes("somevalue");
        byte[] value = Bytes.toBytes("value");
        Table a = TestFromClientSide.TEST_UTIL.createTable(tableAname, CATALOG_FAMILY);
        Table b = TestFromClientSide.TEST_UTIL.createTable(tableBname, CATALOG_FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(CATALOG_FAMILY, null, value);
        a.put(put);
        // open a new connection to A and a connection to b
        Table newA = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableAname);
        // copy data from A to B
        Scan scan = new Scan();
        scan.addFamily(CATALOG_FAMILY);
        ResultScanner s = newA.getScanner(scan);
        try {
            for (Result r : s) {
                put = new Put(r.getRow());
                put.setDurability(SKIP_WAL);
                for (Cell kv : r.rawCells()) {
                    put.add(kv);
                }
                b.put(put);
            }
        } finally {
            s.close();
        }
        // Opening a new connection to A will cause the tables to be reloaded
        Table anotherA = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableAname);
        Get get = new Get(TestFromClientSide.ROW);
        get.addFamily(CATALOG_FAMILY);
        anotherA.get(get);
        // We can still access A through newA because it has the table information
        // cached. And if it needs to recalibrate, that will cause the information
        // to be reloaded.
        // Test user metadata
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        // make a modifiable descriptor
        HTableDescriptor desc = new HTableDescriptor(a.getTableDescriptor());
        // offline the table
        admin.disableTable(tableAname);
        // add a user attribute to HTD
        desc.setValue(attrName, attrValue);
        // add a user attribute to HCD
        for (HColumnDescriptor c : desc.getFamilies())
            c.setValue(attrName, attrValue);

        // update metadata for all regions of this table
        admin.modifyTable(tableAname, desc);
        // enable the table
        admin.enableTable(tableAname);
        // Test that attribute changes were applied
        desc = a.getTableDescriptor();
        Assert.assertEquals("wrong table descriptor returned", desc.getTableName(), tableAname);
        // check HTD attribute
        value = desc.getValue(attrName);
        Assert.assertFalse("missing HTD attribute value", (value == null));
        Assert.assertFalse("HTD attribute value is incorrect", ((Bytes.compareTo(value, attrValue)) != 0));
        // check HCD attribute
        for (HColumnDescriptor c : desc.getFamilies()) {
            value = c.getValue(attrName);
            Assert.assertFalse("missing HCD attribute value", (value == null));
            Assert.assertFalse("HCD attribute value is incorrect", ((Bytes.compareTo(value, attrValue)) != 0));
        }
    }

    @Test
    public void testGetClosestRowBefore() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] firstRow = Bytes.toBytes("row111");
        final byte[] secondRow = Bytes.toBytes("row222");
        final byte[] thirdRow = Bytes.toBytes("row333");
        final byte[] forthRow = Bytes.toBytes("row444");
        final byte[] beforeFirstRow = Bytes.toBytes("row");
        final byte[] beforeSecondRow = Bytes.toBytes("row22");
        final byte[] beforeThirdRow = Bytes.toBytes("row33");
        final byte[] beforeForthRow = Bytes.toBytes("row44");
        try (Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ HConstants.CATALOG_FAMILY, Bytes.toBytes("info2") }, 1, 1024);RegionLocator locator = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            // set block size to 64 to making 2 kvs into one block, bypassing the walkForwardInSingleRow
            // in Store.rowAtOrBeforeFromStoreFile
            String regionName = locator.getAllRegionLocations().get(0).getRegionInfo().getEncodedName();
            HRegion region = TestFromClientSide.TEST_UTIL.getRSForFirstRegionInTable(tableName).getRegion(regionName);
            Put put1 = new Put(firstRow);
            Put put2 = new Put(secondRow);
            Put put3 = new Put(thirdRow);
            Put put4 = new Put(forthRow);
            byte[] one = new byte[]{ 1 };
            byte[] two = new byte[]{ 2 };
            byte[] three = new byte[]{ 3 };
            byte[] four = new byte[]{ 4 };
            put1.addColumn(CATALOG_FAMILY, null, one);
            put2.addColumn(CATALOG_FAMILY, null, two);
            put3.addColumn(CATALOG_FAMILY, null, three);
            put4.addColumn(CATALOG_FAMILY, null, four);
            table.put(put1);
            table.put(put2);
            table.put(put3);
            table.put(put4);
            region.flush(true);
            Result result;
            // Test before first that null is returned
            result = getReverseScanResult(table, beforeFirstRow, CATALOG_FAMILY);
            Assert.assertNull(result);
            // Test at first that first is returned
            result = getReverseScanResult(table, firstRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), firstRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), one));
            // Test in between first and second that first is returned
            result = getReverseScanResult(table, beforeSecondRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), firstRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), one));
            // Test at second make sure second is returned
            result = getReverseScanResult(table, secondRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), secondRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), two));
            // Test in second and third, make sure second is returned
            result = getReverseScanResult(table, beforeThirdRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), secondRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), two));
            // Test at third make sure third is returned
            result = getReverseScanResult(table, thirdRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), thirdRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), three));
            // Test in third and forth, make sure third is returned
            result = getReverseScanResult(table, beforeForthRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), thirdRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), three));
            // Test at forth make sure forth is returned
            result = getReverseScanResult(table, forthRow, CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), forthRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), four));
            // Test after forth make sure forth is returned
            result = getReverseScanResult(table, Bytes.add(forthRow, one), CATALOG_FAMILY);
            Assert.assertTrue(result.containsColumn(CATALOG_FAMILY, null));
            Assert.assertTrue(Bytes.equals(result.getRow(), forthRow));
            Assert.assertTrue(Bytes.equals(result.getValue(CATALOG_FAMILY, null), four));
        }
    }

    /**
     * For HBASE-2156
     */
    @Test
    public void testScanVariableReuse() throws Exception {
        Scan scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.ROW);
        Assert.assertTrue(((scan.getFamilyMap().get(TestFromClientSide.FAMILY).size()) == 1));
        scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        Assert.assertTrue(((scan.getFamilyMap().get(TestFromClientSide.FAMILY)) == null));
        Assert.assertTrue(scan.getFamilyMap().containsKey(TestFromClientSide.FAMILY));
    }

    @Test
    public void testMultiRowMutation() throws Exception {
        TestFromClientSide.LOG.info("Starting testMultiRowMutation");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] ROW1 = Bytes.toBytes("testRow1");
        Table t = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        Put p = new Put(TestFromClientSide.ROW);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        MutationProto m1 = ProtobufUtil.toMutation(PUT, p);
        p = new Put(ROW1);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        MutationProto m2 = ProtobufUtil.toMutation(PUT, p);
        MutateRowsRequest.Builder mrmBuilder = MutateRowsRequest.newBuilder();
        mrmBuilder.addMutationRequest(m1);
        mrmBuilder.addMutationRequest(m2);
        MutateRowsRequest mrm = mrmBuilder.build();
        CoprocessorRpcChannel channel = t.coprocessorService(TestFromClientSide.ROW);
        MultiRowMutationService.BlockingInterface service = MultiRowMutationService.newBlockingStub(channel);
        service.mutateRows(null, mrm);
        Get g = new Get(TestFromClientSide.ROW);
        Result r = t.get(g);
        Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide.VALUE, r.getValue(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER)));
        g = new Get(ROW1);
        r = t.get(g);
        Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide.VALUE, r.getValue(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER)));
    }

    @Test
    public void testRowMutation() throws Exception {
        TestFromClientSide.LOG.info("Starting testRowMutation");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b") };
        RowMutations arm = new RowMutations(TestFromClientSide.ROW);
        Put p = new Put(TestFromClientSide.ROW);
        p.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[0], TestFromClientSide.VALUE);
        arm.add(p);
        t.mutateRow(arm);
        Get g = new Get(TestFromClientSide.ROW);
        Result r = t.get(g);
        Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide.VALUE, r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[0])));
        arm = new RowMutations(TestFromClientSide.ROW);
        p = new Put(TestFromClientSide.ROW);
        p.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[1], TestFromClientSide.VALUE);
        arm.add(p);
        Delete d = new Delete(TestFromClientSide.ROW);
        d.addColumns(TestFromClientSide.FAMILY, QUALIFIERS[0]);
        arm.add(d);
        // TODO: Trying mutateRow again.  The batch was failing with a one try only.
        t.mutateRow(arm);
        r = t.get(g);
        Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide.VALUE, r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[1])));
        Assert.assertNull(r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[0]));
        // Test that we get a region level exception
        try {
            arm = new RowMutations(TestFromClientSide.ROW);
            p = new Put(TestFromClientSide.ROW);
            p.addColumn(new byte[]{ 'b', 'o', 'g', 'u', 's' }, QUALIFIERS[0], TestFromClientSide.VALUE);
            arm.add(p);
            t.mutateRow(arm);
            Assert.fail("Expected NoSuchColumnFamilyException");
        } catch (RetriesExhaustedWithDetailsException e) {
            for (Throwable rootCause : e.getCauses()) {
                if (rootCause instanceof NoSuchColumnFamilyException) {
                    return;
                }
            }
            throw e;
        }
    }

    @Test
    public void testBatchAppendWithReturnResultFalse() throws Exception {
        TestFromClientSide.LOG.info("Starting testBatchAppendWithReturnResultFalse");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        Append append1 = new Append(Bytes.toBytes("row1"));
        append1.setReturnResults(false);
        append1.addColumn(TestFromClientSide.FAMILY, Bytes.toBytes("f1"), Bytes.toBytes("value1"));
        Append append2 = new Append(Bytes.toBytes("row1"));
        append2.setReturnResults(false);
        append2.addColumn(TestFromClientSide.FAMILY, Bytes.toBytes("f1"), Bytes.toBytes("value2"));
        List<Append> appends = new ArrayList<>();
        appends.add(append1);
        appends.add(append2);
        Object[] results = new Object[2];
        table.batch(appends, results);
        Assert.assertTrue(((results.length) == 2));
        for (Object r : results) {
            Result result = ((Result) (r));
            Assert.assertTrue(result.isEmpty());
        }
        table.close();
    }

    @Test
    public void testAppend() throws Exception {
        TestFromClientSide.LOG.info("Starting testAppend");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table t = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[] v1 = Bytes.toBytes("42");
        byte[] v2 = Bytes.toBytes("23");
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("b"), Bytes.toBytes("a"), Bytes.toBytes("c") };
        Append a = new Append(TestFromClientSide.ROW);
        a.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[0], v1);
        a.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[1], v2);
        a.setReturnResults(false);
        assertEmptyResult(t.append(a));
        a = new Append(TestFromClientSide.ROW);
        a.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[0], v2);
        a.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[1], v1);
        a.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[2], v2);
        Result r = t.append(a);
        Assert.assertEquals(0, Bytes.compareTo(Bytes.add(v1, v2), r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[0])));
        Assert.assertEquals(0, Bytes.compareTo(Bytes.add(v2, v1), r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[1])));
        // QUALIFIERS[2] previously not exist, verify both value and timestamp are correct
        Assert.assertEquals(0, Bytes.compareTo(v2, r.getValue(TestFromClientSide.FAMILY, QUALIFIERS[2])));
        Assert.assertEquals(r.getColumnLatestCell(TestFromClientSide.FAMILY, QUALIFIERS[0]).getTimestamp(), r.getColumnLatestCell(TestFromClientSide.FAMILY, QUALIFIERS[2]).getTimestamp());
    }

    @Test
    public void testAppendWithoutWAL() throws Exception {
        List<Result> resultsWithWal = doAppend(true);
        List<Result> resultsWithoutWal = doAppend(false);
        Assert.assertEquals(resultsWithWal.size(), resultsWithoutWal.size());
        for (int i = 0; i != (resultsWithWal.size()); ++i) {
            Result resultWithWal = resultsWithWal.get(i);
            Result resultWithoutWal = resultsWithoutWal.get(i);
            Assert.assertEquals(resultWithWal.rawCells().length, resultWithoutWal.rawCells().length);
            for (int j = 0; j != (resultWithWal.rawCells().length); ++j) {
                Cell cellWithWal = resultWithWal.rawCells()[j];
                Cell cellWithoutWal = resultWithoutWal.rawCells()[j];
                Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(cellWithWal), CellUtil.cloneRow(cellWithoutWal)));
                Assert.assertTrue(Bytes.equals(CellUtil.cloneFamily(cellWithWal), CellUtil.cloneFamily(cellWithoutWal)));
                Assert.assertTrue(Bytes.equals(CellUtil.cloneQualifier(cellWithWal), CellUtil.cloneQualifier(cellWithoutWal)));
                Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(cellWithWal), CellUtil.cloneValue(cellWithoutWal)));
            }
        }
    }

    @Test
    public void testClientPoolRoundRobin() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        int poolSize = 3;
        int numVersions = poolSize * 2;
        Configuration conf = TestFromClientSide.TEST_UTIL.getConfiguration();
        conf.set(HBASE_CLIENT_IPC_POOL_TYPE, "round-robin");
        conf.setInt(HBASE_CLIENT_IPC_POOL_SIZE, poolSize);
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ TestFromClientSide.FAMILY }, Integer.MAX_VALUE);
        final long ts = EnvironmentEdgeManager.currentTime();
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions();
        for (int versions = 1; versions <= numVersions; versions++) {
            Put put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, (ts + versions), TestFromClientSide.VALUE);
            table.put(put);
            Result result = table.get(get);
            NavigableMap<Long, byte[]> navigableMap = result.getMap().get(TestFromClientSide.FAMILY).get(TestFromClientSide.QUALIFIER);
            Assert.assertEquals((((("The number of versions of '" + (Bytes.toString(TestFromClientSide.FAMILY))) + ":") + (Bytes.toString(TestFromClientSide.QUALIFIER))) + " did not match"), versions, navigableMap.size());
            for (Map.Entry<Long, byte[]> entry : navigableMap.entrySet()) {
                Assert.assertTrue((("The value at time " + (entry.getKey())) + " did not match what was put"), Bytes.equals(TestFromClientSide.VALUE, entry.getValue()));
            }
        }
    }

    @Test
    public void testCheckAndPut() throws IOException {
        final byte[] anotherrow = Bytes.toBytes("anotherrow");
        final byte[] value2 = Bytes.toBytes("abcd");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put1 = new Put(TestFromClientSide.ROW);
        put1.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        // row doesn't exist, so using non-null value should be considered "not match".
        boolean ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifEquals(TestFromClientSide.VALUE).thenPut(put1);
        Assert.assertFalse(ok);
        // row doesn't exist, so using "ifNotExists" should be considered "match".
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifNotExists().thenPut(put1);
        Assert.assertTrue(ok);
        // row now exists, so using "ifNotExists" should be considered "not match".
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifNotExists().thenPut(put1);
        Assert.assertFalse(ok);
        Put put2 = new Put(TestFromClientSide.ROW);
        put2.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value2);
        // row now exists, use the matching value to check
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifEquals(TestFromClientSide.VALUE).thenPut(put2);
        Assert.assertTrue(ok);
        Put put3 = new Put(anotherrow);
        put3.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        // try to do CheckAndPut on different rows
        try {
            table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifEquals(value2).thenPut(put3);
            Assert.fail("trying to check and modify different rows should have failed.");
        } catch (Exception e) {
        }
    }

    @Test
    public void testCheckAndMutateWithTimeRange() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        final long ts = (System.currentTimeMillis()) / 2;
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts, TestFromClientSide.VALUE);
        boolean ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifNotExists().thenPut(put);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestFromClientSide.VALUE).thenPut(put);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestFromClientSide.VALUE).thenPut(put);
        Assert.assertTrue(ok);
        RowMutations rm = new RowMutations(TestFromClientSide.ROW).add(((Mutation) (put)));
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestFromClientSide.VALUE).thenMutate(rm);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestFromClientSide.VALUE).thenMutate(rm);
        Assert.assertTrue(ok);
        Delete delete = new Delete(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at((ts + 10000))).ifEquals(TestFromClientSide.VALUE).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).timeRange(TimeRange.at(ts)).ifEquals(TestFromClientSide.VALUE).thenDelete(delete);
        Assert.assertTrue(ok);
    }

    @Test
    public void testCheckAndPutWithCompareOp() throws IOException {
        final byte[] value1 = Bytes.toBytes("aaaa");
        final byte[] value2 = Bytes.toBytes("bbbb");
        final byte[] value3 = Bytes.toBytes("cccc");
        final byte[] value4 = Bytes.toBytes("dddd");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put2 = new Put(TestFromClientSide.ROW);
        put2.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value2);
        Put put3 = new Put(TestFromClientSide.ROW);
        put3.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value3);
        // row doesn't exist, so using "ifNotExists" should be considered "match".
        boolean ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifNotExists().thenPut(put2);
        Assert.assertTrue(ok);
        // cell = "bbbb", using "aaaa" to compare only LESS/LESS_OR_EQUAL/NOT_EQUAL
        // turns out "match"
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value1).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value1).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value1).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value1).thenPut(put2);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value1).thenPut(put2);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value1).thenPut(put3);
        Assert.assertTrue(ok);
        // cell = "cccc", using "dddd" to compare only LARGER/LARGER_OR_EQUAL/NOT_EQUAL
        // turns out "match"
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value4).thenPut(put3);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value4).thenPut(put3);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value4).thenPut(put3);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value4).thenPut(put3);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value4).thenPut(put3);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value4).thenPut(put2);
        Assert.assertTrue(ok);
        // cell = "bbbb", using "bbbb" to compare only GREATER_OR_EQUAL/LESS_OR_EQUAL/EQUAL
        // turns out "match"
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value2).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value2).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value2).thenPut(put2);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value2).thenPut(put2);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value2).thenPut(put2);
        Assert.assertTrue(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value2).thenPut(put3);
        Assert.assertTrue(ok);
    }

    @Test
    public void testCheckAndDelete() throws IOException {
        final byte[] value1 = Bytes.toBytes("aaaa");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value1);
        table.put(put);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        boolean ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifEquals(value1).thenDelete(delete);
        Assert.assertTrue(ok);
    }

    @Test
    public void testCheckAndDeleteWithCompareOp() throws IOException {
        final byte[] value1 = Bytes.toBytes("aaaa");
        final byte[] value2 = Bytes.toBytes("bbbb");
        final byte[] value3 = Bytes.toBytes("cccc");
        final byte[] value4 = Bytes.toBytes("dddd");
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put2 = new Put(TestFromClientSide.ROW);
        put2.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value2);
        table.put(put2);
        Put put3 = new Put(TestFromClientSide.ROW);
        put3.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, value3);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        // cell = "bbbb", using "aaaa" to compare only LESS/LESS_OR_EQUAL/NOT_EQUAL
        // turns out "match"
        boolean ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value1).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value1).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value1).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value1).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put2);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value1).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put2);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value1).thenDelete(delete);
        Assert.assertTrue(ok);
        // cell = "cccc", using "dddd" to compare only LARGER/LARGER_OR_EQUAL/NOT_EQUAL
        // turns out "match"
        table.put(put3);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value4).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value4).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value4).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value4).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put3);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value4).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put3);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value4).thenDelete(delete);
        Assert.assertTrue(ok);
        // cell = "bbbb", using "bbbb" to compare only GREATER_OR_EQUAL/LESS_OR_EQUAL/EQUAL
        // turns out "match"
        table.put(put2);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER, value2).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(NOT_EQUAL, value2).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS, value2).thenDelete(delete);
        Assert.assertFalse(ok);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(GREATER_OR_EQUAL, value2).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put2);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(LESS_OR_EQUAL, value2).thenDelete(delete);
        Assert.assertTrue(ok);
        table.put(put2);
        ok = table.checkAndMutate(TestFromClientSide.ROW, TestFromClientSide.FAMILY).qualifier(TestFromClientSide.QUALIFIER).ifMatches(EQUAL, value2).thenDelete(delete);
        Assert.assertTrue(ok);
    }

    /**
     * Test ScanMetrics
     */
    @Test
    @SuppressWarnings("unused")
    public void testScanMetrics() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Set up test table:
        // Create table:
        Table ht = TestFromClientSide.TEST_UTIL.createMultiRegionTable(tableName, TestFromClientSide.FAMILY);
        int numOfRegions = -1;
        try (RegionLocator r = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            numOfRegions = r.getStartKeys().length;
        }
        // Create 3 rows in the table, with rowkeys starting with "zzz*" so that
        // scan are forced to hit all the regions.
        Put put1 = new Put(Bytes.toBytes("zzz1"));
        put1.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        Put put2 = new Put(Bytes.toBytes("zzz2"));
        put2.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        Put put3 = new Put(Bytes.toBytes("zzz3"));
        put3.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(Arrays.asList(put1, put2, put3));
        Scan scan1 = new Scan();
        int numRecords = 0;
        ResultScanner scanner = ht.getScanner(scan1);
        for (Result result : scanner) {
            numRecords++;
        }
        scanner.close();
        TestFromClientSide.LOG.info((("test data has " + numRecords) + " records."));
        // by default, scan metrics collection is turned off
        Assert.assertEquals(null, scanner.getScanMetrics());
        // turn on scan metrics
        Scan scan2 = new Scan();
        scan2.setScanMetricsEnabled(true);
        scan2.setCaching((numRecords + 1));
        scanner = ht.getScanner(scan2);
        for (Result result : scanner.next((numRecords - 1))) {
        }
        scanner.close();
        // closing the scanner will set the metrics.
        Assert.assertNotNull(scanner.getScanMetrics());
        // set caching to 1, because metrics are collected in each roundtrip only
        scan2 = new Scan();
        scan2.setScanMetricsEnabled(true);
        scan2.setCaching(1);
        scanner = ht.getScanner(scan2);
        // per HBASE-5717, this should still collect even if you don't run all the way to
        // the end of the scanner. So this is asking for 2 of the 3 rows we inserted.
        for (Result result : scanner.next((numRecords - 1))) {
        }
        scanner.close();
        ScanMetrics scanMetrics = scanner.getScanMetrics();
        Assert.assertEquals("Did not access all the regions in the table", numOfRegions, scanMetrics.countOfRegions.get());
        // check byte counters
        scan2 = new Scan();
        scan2.setScanMetricsEnabled(true);
        scan2.setCaching(1);
        scanner = ht.getScanner(scan2);
        int numBytes = 0;
        for (Result result : scanner.next(1)) {
            for (Cell cell : result.listCells()) {
                numBytes += PrivateCellUtil.estimatedSerializedSizeOf(cell);
            }
        }
        scanner.close();
        scanMetrics = scanner.getScanMetrics();
        Assert.assertEquals("Did not count the result bytes", numBytes, scanMetrics.countOfBytesInResults.get());
        // check byte counters on a small scan
        scan2 = new Scan();
        scan2.setScanMetricsEnabled(true);
        scan2.setCaching(1);
        scan2.setSmall(true);
        scanner = ht.getScanner(scan2);
        numBytes = 0;
        for (Result result : scanner.next(1)) {
            for (Cell cell : result.listCells()) {
                numBytes += PrivateCellUtil.estimatedSerializedSizeOf(cell);
            }
        }
        scanner.close();
        scanMetrics = scanner.getScanMetrics();
        Assert.assertEquals("Did not count the result bytes", numBytes, scanMetrics.countOfBytesInResults.get());
        // now, test that the metrics are still collected even if you don't call close, but do
        // run past the end of all the records
        /**
         * There seems to be a timing issue here.  Comment out for now. Fix when time.
         * Scan scanWithoutClose = new Scan();
         * scanWithoutClose.setCaching(1);
         * scanWithoutClose.setScanMetricsEnabled(true);
         * ResultScanner scannerWithoutClose = ht.getScanner(scanWithoutClose);
         * for (Result result : scannerWithoutClose.next(numRecords + 1)) {
         * }
         * ScanMetrics scanMetricsWithoutClose = getScanMetrics(scanWithoutClose);
         * assertEquals("Did not access all the regions in the table", numOfRegions,
         * scanMetricsWithoutClose.countOfRegions.get());
         */
        // finally, test that the metrics are collected correctly if you both run past all the records,
        // AND close the scanner
        Scan scanWithClose = new Scan();
        // make sure we can set caching up to the number of a scanned values
        scanWithClose.setCaching(numRecords);
        scanWithClose.setScanMetricsEnabled(true);
        ResultScanner scannerWithClose = ht.getScanner(scanWithClose);
        for (Result result : scannerWithClose.next((numRecords + 1))) {
        }
        scannerWithClose.close();
        ScanMetrics scanMetricsWithClose = scannerWithClose.getScanMetrics();
        Assert.assertEquals("Did not access all the regions in the table", numOfRegions, scanMetricsWithClose.countOfRegions.get());
    }

    /**
     * Tests that cache on write works all the way up from the client-side.
     *
     * Performs inserts, flushes, and compactions, verifying changes in the block
     * cache along the way.
     */
    @Test
    public void testCacheOnWriteEvictOnClose() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] data = Bytes.toBytes("data");
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        try (RegionLocator locator = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            // get the block cache and region
            String regionName = locator.getAllRegionLocations().get(0).getRegionInfo().getEncodedName();
            HRegion region = TestFromClientSide.TEST_UTIL.getRSForFirstRegionInTable(tableName).getRegion(regionName);
            HStore store = region.getStores().iterator().next();
            CacheConfig cacheConf = store.getCacheConfig();
            cacheConf.setCacheDataOnWrite(true);
            cacheConf.setEvictOnClose(true);
            BlockCache cache = cacheConf.getBlockCache().get();
            // establish baseline stats
            long startBlockCount = cache.getBlockCount();
            long startBlockHits = cache.getStats().getHitCount();
            long startBlockMiss = cache.getStats().getMissCount();
            // wait till baseline is stable, (minimal 500 ms)
            for (int i = 0; i < 5; i++) {
                Thread.sleep(100);
                if (((startBlockCount != (cache.getBlockCount())) || (startBlockHits != (cache.getStats().getHitCount()))) || (startBlockMiss != (cache.getStats().getMissCount()))) {
                    startBlockCount = cache.getBlockCount();
                    startBlockHits = cache.getStats().getHitCount();
                    startBlockMiss = cache.getStats().getMissCount();
                    i = -1;
                }
            }
            // insert data
            Put put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, data);
            table.put(put);
            Assert.assertTrue(Bytes.equals(table.get(new Get(TestFromClientSide.ROW)).value(), data));
            // data was in memstore so don't expect any changes
            Assert.assertEquals(startBlockCount, cache.getBlockCount());
            Assert.assertEquals(startBlockHits, cache.getStats().getHitCount());
            Assert.assertEquals(startBlockMiss, cache.getStats().getMissCount());
            // flush the data
            System.out.println("Flushing cache");
            region.flush(true);
            // expect one more block in cache, no change in hits/misses
            long expectedBlockCount = startBlockCount + 1;
            long expectedBlockHits = startBlockHits;
            long expectedBlockMiss = startBlockMiss;
            Assert.assertEquals(expectedBlockCount, cache.getBlockCount());
            Assert.assertEquals(expectedBlockHits, cache.getStats().getHitCount());
            Assert.assertEquals(expectedBlockMiss, cache.getStats().getMissCount());
            // read the data and expect same blocks, one new hit, no misses
            Assert.assertTrue(Bytes.equals(table.get(new Get(TestFromClientSide.ROW)).value(), data));
            Assert.assertEquals(expectedBlockCount, cache.getBlockCount());
            Assert.assertEquals((++expectedBlockHits), cache.getStats().getHitCount());
            Assert.assertEquals(expectedBlockMiss, cache.getStats().getMissCount());
            // insert a second column, read the row, no new blocks, one new hit
            byte[] QUALIFIER2 = Bytes.add(TestFromClientSide.QUALIFIER, TestFromClientSide.QUALIFIER);
            byte[] data2 = Bytes.add(data, data);
            put = new Put(TestFromClientSide.ROW);
            put.addColumn(TestFromClientSide.FAMILY, QUALIFIER2, data2);
            table.put(put);
            Result r = table.get(new Get(TestFromClientSide.ROW));
            Assert.assertTrue(Bytes.equals(r.getValue(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER), data));
            Assert.assertTrue(Bytes.equals(r.getValue(TestFromClientSide.FAMILY, QUALIFIER2), data2));
            Assert.assertEquals(expectedBlockCount, cache.getBlockCount());
            Assert.assertEquals((++expectedBlockHits), cache.getStats().getHitCount());
            Assert.assertEquals(expectedBlockMiss, cache.getStats().getMissCount());
            // flush, one new block
            System.out.println("Flushing cache");
            region.flush(true);
            Assert.assertEquals((++expectedBlockCount), cache.getBlockCount());
            Assert.assertEquals(expectedBlockHits, cache.getStats().getHitCount());
            Assert.assertEquals(expectedBlockMiss, cache.getStats().getMissCount());
            // compact, net minus two blocks, two hits, no misses
            System.out.println("Compacting");
            Assert.assertEquals(2, store.getStorefilesCount());
            store.triggerMajorCompaction();
            region.compact(true);
            store.closeAndArchiveCompactedFiles();
            waitForStoreFileCount(store, 1, 10000);// wait 10 seconds max

            Assert.assertEquals(1, store.getStorefilesCount());
            expectedBlockCount -= 2;// evicted two blocks, cached none

            Assert.assertEquals(expectedBlockCount, cache.getBlockCount());
            expectedBlockHits += 2;
            Assert.assertEquals(expectedBlockMiss, cache.getStats().getMissCount());
            Assert.assertEquals(expectedBlockHits, cache.getStats().getHitCount());
            // read the row, this should be a cache miss because we don't cache data
            // blocks on compaction
            r = table.get(new Get(TestFromClientSide.ROW));
            Assert.assertTrue(Bytes.equals(r.getValue(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER), data));
            Assert.assertTrue(Bytes.equals(r.getValue(TestFromClientSide.FAMILY, QUALIFIER2), data2));
            expectedBlockCount += 1;// cached one data block

            Assert.assertEquals(expectedBlockCount, cache.getBlockCount());
            Assert.assertEquals(expectedBlockHits, cache.getStats().getHitCount());
            Assert.assertEquals((++expectedBlockMiss), cache.getStats().getMissCount());
        }
    }

    /**
     * Tests the non cached version of getRegionLocator by moving a region.
     */
    @Test
    public void testNonCachedGetRegionLocation() throws Exception {
        // Test Initialization.
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] family1 = Bytes.toBytes("f1");
        byte[] family2 = Bytes.toBytes("f2");
        try (Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ family1, family2 }, 10);Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();RegionLocator locator = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            List<HRegionLocation> allRegionLocations = locator.getAllRegionLocations();
            Assert.assertEquals(1, allRegionLocations.size());
            HRegionInfo regionInfo = allRegionLocations.get(0).getRegionInfo();
            ServerName addrBefore = allRegionLocations.get(0).getServerName();
            // Verify region location before move.
            HRegionLocation addrCache = locator.getRegionLocation(regionInfo.getStartKey(), false);
            HRegionLocation addrNoCache = locator.getRegionLocation(regionInfo.getStartKey(), true);
            Assert.assertEquals(addrBefore.getPort(), addrCache.getPort());
            Assert.assertEquals(addrBefore.getPort(), addrNoCache.getPort());
            ServerName addrAfter = null;
            // Now move the region to a different server.
            for (int i = 0; i < (TestFromClientSide.SLAVES); i++) {
                HRegionServer regionServer = TestFromClientSide.TEST_UTIL.getHBaseCluster().getRegionServer(i);
                ServerName addr = regionServer.getServerName();
                if ((addr.getPort()) != (addrBefore.getPort())) {
                    admin.move(regionInfo.getEncodedNameAsBytes(), Bytes.toBytes(addr.toString()));
                    // Wait for the region to move.
                    Thread.sleep(5000);
                    addrAfter = addr;
                    break;
                }
            }
            // Verify the region was moved.
            addrCache = locator.getRegionLocation(regionInfo.getStartKey(), false);
            addrNoCache = locator.getRegionLocation(regionInfo.getStartKey(), true);
            Assert.assertNotNull(addrAfter);
            Assert.assertTrue(((addrAfter.getPort()) != (addrCache.getPort())));
            Assert.assertEquals(addrAfter.getPort(), addrNoCache.getPort());
        }
    }

    /**
     * Tests getRegionsInRange by creating some regions over which a range of
     * keys spans; then changing the key range.
     */
    @Test
    public void testGetRegionsInRange() throws Exception {
        // Test Initialization.
        byte[] startKey = Bytes.toBytes("ddc");
        byte[] endKey = Bytes.toBytes("mmm");
        TableName tableName = TableName.valueOf(name.getMethodName());
        TestFromClientSide.TEST_UTIL.createMultiRegionTable(tableName, new byte[][]{ TestFromClientSide.FAMILY }, 10);
        int numOfRegions = -1;
        try (RegionLocator r = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            numOfRegions = r.getStartKeys().length;
        }
        Assert.assertEquals(26, numOfRegions);
        // Get the regions in this range
        List<HRegionLocation> regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(10, regionsList.size());
        // Change the start key
        startKey = Bytes.toBytes("fff");
        regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(7, regionsList.size());
        // Change the end key
        endKey = Bytes.toBytes("nnn");
        regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(8, regionsList.size());
        // Empty start key
        regionsList = getRegionsInRange(tableName, EMPTY_START_ROW, endKey);
        Assert.assertEquals(13, regionsList.size());
        // Empty end key
        regionsList = getRegionsInRange(tableName, startKey, EMPTY_END_ROW);
        Assert.assertEquals(21, regionsList.size());
        // Both start and end keys empty
        regionsList = getRegionsInRange(tableName, EMPTY_START_ROW, EMPTY_END_ROW);
        Assert.assertEquals(26, regionsList.size());
        // Change the end key to somewhere in the last block
        endKey = Bytes.toBytes("zzz1");
        regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(21, regionsList.size());
        // Change the start key to somewhere in the first block
        startKey = Bytes.toBytes("aac");
        regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(26, regionsList.size());
        // Make start and end key the same
        startKey = endKey = Bytes.toBytes("ccc");
        regionsList = getRegionsInRange(tableName, startKey, endKey);
        Assert.assertEquals(1, regionsList.size());
    }

    @Test
    public void testJira6912() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table foo = TestFromClientSide.TEST_UTIL.createTable(tableName, new byte[][]{ TestFromClientSide.FAMILY }, 10);
        List<Put> puts = new ArrayList<Put>();
        for (int i = 0; i != 100; i++) {
            Put put = new Put(Bytes.toBytes(i));
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.FAMILY, Bytes.toBytes(i));
            puts.add(put);
        }
        foo.put(puts);
        // If i comment this out it works
        TestFromClientSide.TEST_UTIL.flush();
        Scan scan = new Scan();
        scan.setStartRow(Bytes.toBytes(1));
        scan.setStopRow(Bytes.toBytes(3));
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.FAMILY);
        scan.setFilter(new org.apache.hadoop.hbase.filter.RowFilter(CompareOperator.NOT_EQUAL, new org.apache.hadoop.hbase.filter.BinaryComparator(Bytes.toBytes(1))));
        ResultScanner scanner = foo.getScanner(scan);
        Result[] bar = scanner.next(100);
        Assert.assertEquals(1, bar.length);
    }

    @Test
    public void testScan_NullQualifier() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        table.put(put);
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
        table.put(put);
        TestFromClientSide.LOG.info("Row put");
        Scan scan = new Scan();
        scan.addColumn(TestFromClientSide.FAMILY, null);
        ResultScanner scanner = table.getScanner(scan);
        Result[] bar = scanner.next(100);
        Assert.assertEquals(1, bar.length);
        Assert.assertEquals(1, bar[0].size());
        scan = new Scan();
        scan.addFamily(TestFromClientSide.FAMILY);
        scanner = table.getScanner(scan);
        bar = scanner.next(100);
        Assert.assertEquals(1, bar.length);
        Assert.assertEquals(2, bar[0].size());
    }

    @Test
    public void testNegativeTimestamp() throws IOException {
        Table table = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), TestFromClientSide.FAMILY);
        try {
            Put put = new Put(TestFromClientSide.ROW, (-1));
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            table.put(put);
            Assert.fail("Negative timestamps should not have been allowed");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("negative"));
        }
        try {
            Put put = new Put(TestFromClientSide.ROW);
            long ts = -1;
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts, TestFromClientSide.VALUE);
            table.put(put);
            Assert.fail("Negative timestamps should not have been allowed");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("negative"));
        }
        try {
            Delete delete = new Delete(TestFromClientSide.ROW, (-1));
            table.delete(delete);
            Assert.fail("Negative timestamps should not have been allowed");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("negative"));
        }
        try {
            Delete delete = new Delete(TestFromClientSide.ROW);
            delete.addFamily(TestFromClientSide.FAMILY, (-1));
            table.delete(delete);
            Assert.fail("Negative timestamps should not have been allowed");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("negative"));
        }
        try {
            Scan scan = new Scan();
            scan.setTimeRange((-1), 1);
            table.getScanner(scan);
            Assert.fail("Negative timestamps should not have been allowed");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(ex.getMessage().contains("negative"));
        }
        // KeyValue should allow negative timestamps for backwards compat. Otherwise, if the user
        // already has negative timestamps in cluster data, HBase won't be able to handle that
        try {
            new KeyValue(Bytes.toBytes(42), Bytes.toBytes(42), Bytes.toBytes(42), (-1), Bytes.toBytes(42));
        } catch (IllegalArgumentException ex) {
            Assert.fail("KeyValue SHOULD allow negative timestamps");
        }
        table.close();
    }

    @Test
    public void testRawScanRespectsVersions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[] row = Bytes.toBytes("row");
        // put the same row 4 times, with different values
        Put p = new Put(row);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, 10, TestFromClientSide.VALUE);
        table.put(p);
        p = new Put(row);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, 11, ArrayUtils.add(TestFromClientSide.VALUE, ((byte) (2))));
        table.put(p);
        p = new Put(row);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, 12, ArrayUtils.add(TestFromClientSide.VALUE, ((byte) (3))));
        table.put(p);
        p = new Put(row);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, 13, ArrayUtils.add(TestFromClientSide.VALUE, ((byte) (4))));
        table.put(p);
        int versions = 4;
        Scan s = new Scan(row);
        // get all the possible versions
        s.setMaxVersions();
        s.setRaw(true);
        ResultScanner scanner = table.getScanner(s);
        int count = 0;
        for (Result r : scanner) {
            Assert.assertEquals("Found an unexpected number of results for the row!", versions, r.listCells().size());
            count++;
        }
        Assert.assertEquals("Found more than a single row when raw scanning the table with a single row!", 1, count);
        scanner.close();
        // then if we decrease the number of versions, but keep the scan raw, we should see exactly that
        // number of versions
        versions = 2;
        s.setMaxVersions(versions);
        scanner = table.getScanner(s);
        count = 0;
        for (Result r : scanner) {
            Assert.assertEquals("Found an unexpected number of results for the row!", versions, r.listCells().size());
            count++;
        }
        Assert.assertEquals("Found more than a single row when raw scanning the table with a single row!", 1, count);
        scanner.close();
        // finally, if we turn off raw scanning, but max out the number of versions, we should go back
        // to seeing just three
        versions = 3;
        s.setMaxVersions(versions);
        scanner = table.getScanner(s);
        count = 0;
        for (Result r : scanner) {
            Assert.assertEquals("Found an unexpected number of results for the row!", versions, r.listCells().size());
            count++;
        }
        Assert.assertEquals("Found more than a single row when raw scanning the table with a single row!", 1, count);
        scanner.close();
        table.close();
        TestFromClientSide.TEST_UTIL.deleteTable(tableName);
    }

    @Test
    public void testEmptyFilterList() throws Exception {
        // Test Initialization.
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Insert one row each region
        Put put = new Put(Bytes.toBytes("row"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        table.put(put);
        List<Result> scanResults = new LinkedList<>();
        Scan scan = new Scan();
        scan.setFilter(new FilterList());
        try (ResultScanner scanner = table.getScanner(scan)) {
            for (Result r : scanner) {
                scanResults.add(r);
            }
        }
        Assert.assertEquals(1, scanResults.size());
        Get g = new Get(Bytes.toBytes("row"));
        g.setFilter(new FilterList());
        Result getResult = table.get(g);
        Result scanResult = scanResults.get(0);
        Assert.assertEquals(scanResult.rawCells().length, getResult.rawCells().length);
        for (int i = 0; i != (scanResult.rawCells().length); ++i) {
            Cell scanCell = scanResult.rawCells()[i];
            Cell getCell = getResult.rawCells()[i];
            Assert.assertEquals(0, Bytes.compareTo(CellUtil.cloneRow(scanCell), CellUtil.cloneRow(getCell)));
            Assert.assertEquals(0, Bytes.compareTo(CellUtil.cloneFamily(scanCell), CellUtil.cloneFamily(getCell)));
            Assert.assertEquals(0, Bytes.compareTo(CellUtil.cloneQualifier(scanCell), CellUtil.cloneQualifier(getCell)));
            Assert.assertEquals(0, Bytes.compareTo(CellUtil.cloneValue(scanCell), CellUtil.cloneValue(getCell)));
        }
    }

    @Test
    public void testSmallScan() throws Exception {
        // Test Initialization.
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Insert one row each region
        int insertNum = 10;
        for (int i = 0; i < 10; i++) {
            Put put = new Put(Bytes.toBytes(("row" + (String.format("%03d", i)))));
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            table.put(put);
        }
        // normal scan
        ResultScanner scanner = table.getScanner(new Scan());
        int count = 0;
        for (Result r : scanner) {
            Assert.assertTrue((!(r.isEmpty())));
            count++;
        }
        Assert.assertEquals(insertNum, count);
        // small scan
        Scan scan = new Scan(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW);
        scan.setSmall(true);
        scan.setCaching(2);
        scanner = table.getScanner(scan);
        count = 0;
        for (Result r : scanner) {
            Assert.assertTrue((!(r.isEmpty())));
            count++;
        }
        Assert.assertEquals(insertNum, count);
    }

    @Test
    public void testSuperSimpleWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        Put put = new Put(Bytes.toBytes("0-b11111-0000000000000000000"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b11111-0000000000000000002"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b11111-0000000000000000004"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b11111-0000000000000000006"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b11111-0000000000000000008"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b22222-0000000000000000001"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b22222-0000000000000000003"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b22222-0000000000000000005"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b22222-0000000000000000007"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        put = new Put(Bytes.toBytes("0-b22222-0000000000000000009"));
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        Scan scan = new Scan(Bytes.toBytes("0-b11111-9223372036854775807"), Bytes.toBytes("0-b11111-0000000000000000000"));
        scan.setReversed(true);
        ResultScanner scanner = ht.getScanner(scan);
        Result result = scanner.next();
        Assert.assertTrue(Bytes.equals(result.getRow(), Bytes.toBytes("0-b11111-0000000000000000008")));
        scanner.close();
        ht.close();
    }

    @Test
    public void testFiltersWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 10);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("col0-<d2v1>-<d3v2>"), Bytes.toBytes("col1-<d2v1>-<d3v2>"), Bytes.toBytes("col2-<d2v1>-<d3v2>"), Bytes.toBytes("col3-<d2v1>-<d3v2>"), Bytes.toBytes("col4-<d2v1>-<d3v2>"), Bytes.toBytes("col5-<d2v1>-<d3v2>"), Bytes.toBytes("col6-<d2v1>-<d3v2>"), Bytes.toBytes("col7-<d2v1>-<d3v2>"), Bytes.toBytes("col8-<d2v1>-<d3v2>"), Bytes.toBytes("col9-<d2v1>-<d3v2>") };
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROWS[i]);
            put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i], TestFromClientSide.VALUE);
            ht.put(put);
        }
        Scan scan = new Scan();
        scan.setReversed(true);
        scan.addFamily(TestFromClientSide.FAMILY);
        Filter filter = new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new RegexStringComparator("col[1-5]"));
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int expectedIndex = 5;
        for (Result result : scanner) {
            Assert.assertEquals(1, result.size());
            Cell c = result.rawCells()[0];
            Assert.assertTrue(Bytes.equals(c.getRowArray(), c.getRowOffset(), c.getRowLength(), ROWS[expectedIndex], 0, ROWS[expectedIndex].length));
            Assert.assertTrue(Bytes.equals(c.getQualifierArray(), c.getQualifierOffset(), c.getQualifierLength(), QUALIFIERS[expectedIndex], 0, QUALIFIERS[expectedIndex].length));
            expectedIndex--;
        }
        Assert.assertEquals(0, expectedIndex);
        scanner.close();
        ht.close();
    }

    @Test
    public void testKeyOnlyFilterWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 10);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("col0-<d2v1>-<d3v2>"), Bytes.toBytes("col1-<d2v1>-<d3v2>"), Bytes.toBytes("col2-<d2v1>-<d3v2>"), Bytes.toBytes("col3-<d2v1>-<d3v2>"), Bytes.toBytes("col4-<d2v1>-<d3v2>"), Bytes.toBytes("col5-<d2v1>-<d3v2>"), Bytes.toBytes("col6-<d2v1>-<d3v2>"), Bytes.toBytes("col7-<d2v1>-<d3v2>"), Bytes.toBytes("col8-<d2v1>-<d3v2>"), Bytes.toBytes("col9-<d2v1>-<d3v2>") };
        for (int i = 0; i < 10; i++) {
            Put put = new Put(ROWS[i]);
            put.addColumn(TestFromClientSide.FAMILY, QUALIFIERS[i], TestFromClientSide.VALUE);
            ht.put(put);
        }
        Scan scan = new Scan();
        scan.setReversed(true);
        scan.addFamily(TestFromClientSide.FAMILY);
        Filter filter = new KeyOnlyFilter(true);
        scan.setFilter(filter);
        ResultScanner scanner = ht.getScanner(scan);
        int count = 0;
        for (Result result : ht.getScanner(scan)) {
            Assert.assertEquals(1, result.size());
            Assert.assertEquals(SIZEOF_INT, result.rawCells()[0].getValueLength());
            Assert.assertEquals(TestFromClientSide.VALUE.length, Bytes.toInt(CellUtil.cloneValue(result.rawCells()[0])));
            count++;
        }
        Assert.assertEquals(10, count);
        scanner.close();
        ht.close();
    }

    /**
     * Test simple table and non-existent row cases.
     */
    @Test
    public void testSimpleMissingWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        byte[][] ROWS = makeN(TestFromClientSide.ROW, 4);
        // Try to get a row on an empty table
        Scan scan = new Scan();
        scan.setReversed(true);
        Result result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan(ROWS[0]);
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan(ROWS[0], ROWS[1]);
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan();
        scan.setReversed(true);
        scan.addFamily(TestFromClientSide.FAMILY);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        scan = new Scan();
        scan.setReversed(true);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        // Insert a row
        Put put = new Put(ROWS[2]);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        ht.put(put);
        // Make sure we can scan the row
        scan = new Scan();
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        scan = new Scan(ROWS[3], ROWS[0]);
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        scan = new Scan(ROWS[2], ROWS[1]);
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertSingleResult(result, ROWS[2], TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        // Try to scan empty rows around it
        // Introduced MemStore#shouldSeekForReverseScan to fix the following
        scan = new Scan(ROWS[1]);
        scan.setReversed(true);
        result = getSingleScanResult(ht, scan);
        assertNullResult(result);
        ht.close();
    }

    @Test
    public void testNullWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        // Null qualifier (should work)
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, null, TestFromClientSide.VALUE);
        ht.put(put);
        scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE, true);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, null);
        ht.delete(delete);
        // Use a new table
        ht = TestFromClientSide.TEST_UTIL.createTable(TableName.valueOf(((name.getMethodName()) + "2")), TestFromClientSide.FAMILY);
        // Empty qualifier, byte[0] instead of null (should work)
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, EMPTY_BYTE_ARRAY, TestFromClientSide.VALUE);
        ht.put(put);
        scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE, true);
        TestFromClientSide.TEST_UTIL.flush();
        scanTestNull(ht, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.VALUE, true);
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(TestFromClientSide.FAMILY, EMPTY_BYTE_ARRAY);
        ht.delete(delete);
        // Null value
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, null);
        ht.put(put);
        Scan scan = new Scan();
        scan.setReversed(true);
        scan.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        Result result = getSingleScanResult(ht, scan);
        assertSingleResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, null);
        ht.close();
    }

    @Test
    public void testDeletesWithReverseScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] ROWS = makeNAscii(TestFromClientSide.ROW, 6);
        byte[][] FAMILIES = makeNAscii(TestFromClientSide.FAMILY, 3);
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES, 3);
        Put put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        ht.put(put);
        Delete delete = new Delete(TestFromClientSide.ROW);
        delete.addFamily(FAMILIES[0], ts[0]);
        ht.delete(delete);
        Scan scan = new Scan(TestFromClientSide.ROW);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[0]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        Result result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUES[1] }, 0, 0);
        // Test delete latest version
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[4], VALUES[4]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        put.addColumn(FAMILIES[0], null, ts[4], VALUES[4]);
        put.addColumn(FAMILIES[0], null, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[0], null, ts[3], VALUES[3]);
        ht.put(put);
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);// ts[4]

        ht.delete(delete);
        scan = new Scan(TestFromClientSide.ROW);
        scan.setReversed(true);
        scan.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // Test for HBASE-1847
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumn(FAMILIES[0], null);
        ht.delete(delete);
        // Cleanup null qualifier
        delete = new Delete(TestFromClientSide.ROW);
        delete.addColumns(FAMILIES[0], null);
        ht.delete(delete);
        // Expected client behavior might be that you can re-put deleted values
        // But alas, this is not to be. We can't put them back in either case.
        put = new Put(TestFromClientSide.ROW);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[0], TestFromClientSide.QUALIFIER, ts[4], VALUES[4]);
        ht.put(put);
        // The Scanner returns the previous values, the expected-naive-unexpected
        // behavior
        scan = new Scan(TestFromClientSide.ROW);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[0]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        assertNResult(result, TestFromClientSide.ROW, FAMILIES[0], TestFromClientSide.QUALIFIER, new long[]{ ts[1], ts[2], ts[3] }, new byte[][]{ VALUES[1], VALUES[2], VALUES[3] }, 0, 2);
        // Test deleting an entire family from one row but not the other various
        // ways
        put = new Put(ROWS[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        put = new Put(ROWS[1]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        put = new Put(ROWS[2]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[0], VALUES[0]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, ts[1], VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[2], VALUES[2]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, ts[3], VALUES[3]);
        ht.put(put);
        delete = new Delete(ROWS[0]);
        delete.addFamily(FAMILIES[2]);
        ht.delete(delete);
        delete = new Delete(ROWS[1]);
        delete.addColumns(FAMILIES[1], TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        delete = new Delete(ROWS[2]);
        delete.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        delete.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER);
        delete.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        scan = new Scan(ROWS[0]);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        assertNResult(result, ROWS[0], FAMILIES[1], TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1] }, new byte[][]{ VALUES[0], VALUES[1] }, 0, 1);
        scan = new Scan(ROWS[1]);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        scan = new Scan(ROWS[2]);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        result = getSingleScanResult(ht, scan);
        Assert.assertEquals(1, result.size());
        assertNResult(result, ROWS[2], FAMILIES[2], TestFromClientSide.QUALIFIER, new long[]{ ts[2] }, new byte[][]{ VALUES[2] }, 0, 0);
        // Test if we delete the family first in one row (HBASE-1541)
        delete = new Delete(ROWS[3]);
        delete.addFamily(FAMILIES[1]);
        ht.delete(delete);
        put = new Put(ROWS[3]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, VALUES[0]);
        ht.put(put);
        put = new Put(ROWS[4]);
        put.addColumn(FAMILIES[1], TestFromClientSide.QUALIFIER, VALUES[1]);
        put.addColumn(FAMILIES[2], TestFromClientSide.QUALIFIER, VALUES[2]);
        ht.put(put);
        scan = new Scan(ROWS[4]);
        scan.setReversed(true);
        scan.addFamily(FAMILIES[1]);
        scan.addFamily(FAMILIES[2]);
        scan.setMaxVersions(Integer.MAX_VALUE);
        ResultScanner scanner = ht.getScanner(scan);
        result = scanner.next();
        Assert.assertTrue(("Expected 2 keys but received " + (result.size())), ((result.size()) == 2));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[0]), ROWS[4]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[1]), ROWS[4]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[0]), VALUES[1]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[1]), VALUES[2]));
        result = scanner.next();
        Assert.assertTrue(("Expected 1 key but received " + (result.size())), ((result.size()) == 1));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneRow(result.rawCells()[0]), ROWS[3]));
        Assert.assertTrue(Bytes.equals(CellUtil.cloneValue(result.rawCells()[0]), VALUES[0]));
        scanner.close();
        ht.close();
    }

    /**
     * Tests reversed scan under multi regions
     */
    @Test
    public void testReversedScanUnderMultiRegions() throws Exception {
        // Test Initialization.
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] maxByteArray = MAX_BYTE_ARRAY;
        byte[][] splitRows = new byte[][]{ Bytes.toBytes("005"), Bytes.add(Bytes.toBytes("005"), Bytes.multiple(maxByteArray, 16)), Bytes.toBytes("006"), Bytes.add(Bytes.toBytes("006"), Bytes.multiple(maxByteArray, 8)), Bytes.toBytes("007"), Bytes.add(Bytes.toBytes("007"), Bytes.multiple(maxByteArray, 4)), Bytes.toBytes("008"), Bytes.multiple(maxByteArray, 2) };
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, splitRows);
        TestFromClientSide.TEST_UTIL.waitUntilAllRegionsAssigned(table.getName());
        try (RegionLocator l = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            Assert.assertEquals(((splitRows.length) + 1), l.getAllRegionLocations().size());
        }
        // Insert one row each region
        int insertNum = splitRows.length;
        for (int i = 0; i < insertNum; i++) {
            Put put = new Put(splitRows[i]);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            table.put(put);
        }
        // scan forward
        ResultScanner scanner = table.getScanner(new Scan());
        int count = 0;
        for (Result r : scanner) {
            Assert.assertTrue((!(r.isEmpty())));
            count++;
        }
        Assert.assertEquals(insertNum, count);
        // scan backward
        Scan scan = new Scan();
        scan.setReversed(true);
        scanner = table.getScanner(scan);
        count = 0;
        byte[] lastRow = null;
        for (Result r : scanner) {
            Assert.assertTrue((!(r.isEmpty())));
            count++;
            byte[] thisRow = r.getRow();
            if (lastRow != null) {
                Assert.assertTrue(((("Error scan order, last row= " + (Bytes.toString(lastRow))) + ",this row=") + (Bytes.toString(thisRow))), ((Bytes.compareTo(thisRow, lastRow)) < 0));
            }
            lastRow = thisRow;
        }
        Assert.assertEquals(insertNum, count);
        table.close();
    }

    /**
     * Tests reversed scan under multi regions
     */
    @Test
    public void testSmallReversedScanUnderMultiRegions() throws Exception {
        // Test Initialization.
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] splitRows = new byte[][]{ Bytes.toBytes("000"), Bytes.toBytes("002"), Bytes.toBytes("004"), Bytes.toBytes("006"), Bytes.toBytes("008"), Bytes.toBytes("010") };
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, splitRows);
        TestFromClientSide.TEST_UTIL.waitUntilAllRegionsAssigned(table.getName());
        try (RegionLocator l = TestFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            Assert.assertEquals(((splitRows.length) + 1), l.getAllRegionLocations().size());
        }
        for (byte[] splitRow : splitRows) {
            Put put = new Put(splitRow);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            table.put(put);
            byte[] nextRow = Bytes.copy(splitRow);
            (nextRow[((nextRow.length) - 1)])++;
            put = new Put(nextRow);
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
            table.put(put);
        }
        // scan forward
        ResultScanner scanner = table.getScanner(new Scan());
        int count = 0;
        for (Result r : scanner) {
            Assert.assertTrue((!(r.isEmpty())));
            count++;
        }
        Assert.assertEquals(12, count);
        reverseScanTest(table, false);
        reverseScanTest(table, true);
        table.close();
    }

    @Test
    public void testGetStartEndKeysWithRegionReplicas() throws IOException {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        HColumnDescriptor fam = new HColumnDescriptor(TestFromClientSide.FAMILY);
        htd.addFamily(fam);
        byte[][] KEYS = HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE;
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        admin.createTable(htd, KEYS);
        List<HRegionInfo> regions = admin.getTableRegions(htd.getTableName());
        HRegionLocator locator = ((HRegionLocator) (admin.getConnection().getRegionLocator(htd.getTableName())));
        for (int regionReplication = 1; regionReplication < 4; regionReplication++) {
            List<RegionLocations> regionLocations = new ArrayList<>();
            // mock region locations coming from meta with multiple replicas
            for (HRegionInfo region : regions) {
                HRegionLocation[] arr = new HRegionLocation[regionReplication];
                for (int i = 0; i < (arr.length); i++) {
                    arr[i] = new HRegionLocation(RegionReplicaUtil.getRegionInfoForReplica(region, i), null);
                }
                regionLocations.add(new RegionLocations(arr));
            }
            Pair<byte[][], byte[][]> startEndKeys = TestFromClientSide.getStartEndKeys(regionLocations);
            Assert.assertEquals(((KEYS.length) + 1), startEndKeys.getFirst().length);
            for (int i = 0; i < ((KEYS.length) + 1); i++) {
                byte[] startKey = (i == 0) ? HConstants.EMPTY_START_ROW : KEYS[(i - 1)];
                byte[] endKey = (i == (KEYS.length)) ? HConstants.EMPTY_END_ROW : KEYS[i];
                Assert.assertArrayEquals(startKey, startEndKeys.getFirst()[i]);
                Assert.assertArrayEquals(endKey, startEndKeys.getSecond()[i]);
            }
        }
    }

    @Test
    public void testFilterAllRecords() throws IOException {
        Scan scan = new Scan();
        scan.setBatch(1);
        scan.setCaching(1);
        // Filter out any records
        scan.setFilter(new FilterList(new FirstKeyOnlyFilter(), new InclusiveStopFilter(new byte[0])));
        try (Table table = TestFromClientSide.TEST_UTIL.getConnection().getTable(META_TABLE_NAME)) {
            try (ResultScanner s = table.getScanner(scan)) {
                Assert.assertNull(s.next());
            }
        }
    }

    @Test
    public void testRegionCache() throws IOException {
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        HColumnDescriptor fam = new HColumnDescriptor(TestFromClientSide.FAMILY);
        htd.addFamily(fam);
        byte[][] KEYS = HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE;
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        admin.createTable(htd, KEYS);
        HRegionLocator locator = ((HRegionLocator) (admin.getConnection().getRegionLocator(htd.getTableName())));
        List<HRegionLocation> results = locator.getAllRegionLocations();
        int number = ((ConnectionImplementation) (admin.getConnection())).getNumberOfCachedRegionLocations(htd.getTableName());
        Assert.assertEquals(results.size(), number);
        ConnectionImplementation conn = ((ConnectionImplementation) (admin.getConnection()));
        Assert.assertNotNull("Can't get cached location for row aaa", conn.getCachedLocation(htd.getTableName(), Bytes.toBytes("aaa")));
        for (byte[] startKey : HBaseTestingUtility.KEYS_FOR_HBA_CREATE_TABLE) {
            Assert.assertNotNull(("Can't get cached location for row " + (Bytes.toString(startKey))), conn.getCachedLocation(htd.getTableName(), startKey));
        }
    }

    @Test
    public void testCellSizeLimit() throws IOException {
        final TableName tableName = TableName.valueOf("testCellSizeLimit");
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.setConfiguration(HBASE_MAX_CELL_SIZE_KEY, Integer.toString((10 * 1024)));// 10K

        HColumnDescriptor fam = new HColumnDescriptor(TestFromClientSide.FAMILY);
        htd.addFamily(fam);
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        admin.createTable(htd);
        // Will succeed
        try (Table t = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableName)) {
            t.put(new Put(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, Bytes.toBytes(0L)));
            t.increment(new Increment(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, 1L));
        }
        // Will succeed
        try (Table t = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableName)) {
            t.put(new Put(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new byte[9 * 1024]));
        }
        // Will fail
        try (Table t = TestFromClientSide.TEST_UTIL.getConnection().getTable(tableName)) {
            try {
                t.put(new Put(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new byte[10 * 1024]));
                Assert.fail("Oversize cell failed to trigger exception");
            } catch (IOException e) {
                // expected
            }
            try {
                t.append(new Append(TestFromClientSide.ROW).addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new byte[10 * 1024]));
                Assert.fail("Oversize cell failed to trigger exception");
            } catch (IOException e) {
                // expected
            }
        }
    }

    @Test
    public void testDeleteSpecifiedVersionOfSpecifiedColumn() throws Exception {
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 5);
        Put put = new Put(TestFromClientSide.ROW);
        // Put version 1000,2000,3000,4000 of column FAMILY:QUALIFIER
        for (int t = 0; t < 4; t++) {
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[t], VALUES[t]);
        }
        ht.put(put);
        Delete delete = new Delete(TestFromClientSide.ROW);
        // Delete version 3000 of column FAMILY:QUALIFIER
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[2]);
        ht.delete(delete);
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        Result result = ht.get(get);
        // verify version 1000,2000,4000 remains for column FAMILY:QUALIFIER
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1], ts[3] }, new byte[][]{ VALUES[0], VALUES[1], VALUES[3] }, 0, 2);
        delete = new Delete(TestFromClientSide.ROW);
        // Delete a version 5000 of column FAMILY:QUALIFIER which didn't exist
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[4]);
        ht.delete(delete);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        // verify version 1000,2000,4000 remains for column FAMILY:QUALIFIER
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1], ts[3] }, new byte[][]{ VALUES[0], VALUES[1], VALUES[3] }, 0, 2);
        ht.close();
        admin.close();
    }

    @Test
    public void testDeleteLatestVersionOfSpecifiedColumn() throws Exception {
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] VALUES = makeN(TestFromClientSide.VALUE, 5);
        long[] ts = new long[]{ 1000, 2000, 3000, 4000, 5000 };
        Table ht = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 5);
        Put put = new Put(TestFromClientSide.ROW);
        // Put version 1000,2000,3000,4000 of column FAMILY:QUALIFIER
        for (int t = 0; t < 4; t++) {
            put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[t], VALUES[t]);
        }
        ht.put(put);
        Delete delete = new Delete(TestFromClientSide.ROW);
        // Delete latest version of column FAMILY:QUALIFIER
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        Get get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        Result result = ht.get(get);
        // verify version 1000,2000,3000 remains for column FAMILY:QUALIFIER
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[1], ts[2] }, new byte[][]{ VALUES[0], VALUES[1], VALUES[2] }, 0, 2);
        delete = new Delete(TestFromClientSide.ROW);
        // Delete two latest version of column FAMILY:QUALIFIER
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        delete.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        ht.delete(delete);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        // verify version 1000 remains for column FAMILY:QUALIFIER
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[0] }, new byte[][]{ VALUES[0] }, 0, 0);
        put = new Put(TestFromClientSide.ROW);
        // Put a version 5000 of column FAMILY:QUALIFIER
        put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[4], VALUES[4]);
        ht.put(put);
        get = new Get(TestFromClientSide.ROW);
        get.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        get.setMaxVersions(Integer.MAX_VALUE);
        result = ht.get(get);
        // verify version 1000,5000 remains for column FAMILY:QUALIFIER
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[0], ts[4] }, new byte[][]{ VALUES[0], VALUES[4] }, 0, 1);
        ht.close();
        admin.close();
    }

    /**
     * Test for HBASE-17125
     */
    @Test
    public void testReadWithFilter() throws Exception {
        Admin admin = TestFromClientSide.TEST_UTIL.getAdmin();
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY, 3);
        byte[] VALUEA = Bytes.toBytes("value-a");
        byte[] VALUEB = Bytes.toBytes("value-b");
        long[] ts = new long[]{ 1000, 2000, 3000, 4000 };
        Put put = new Put(TestFromClientSide.ROW);
        // Put version 1000,2000,3000,4000 of column FAMILY:QUALIFIER
        for (int t = 0; t <= 3; t++) {
            if (t <= 1) {
                put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[t], VALUEA);
            } else {
                put.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, ts[t], VALUEB);
            }
        }
        table.put(put);
        Scan scan = new Scan().setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(3);
        ResultScanner scanner = table.getScanner(scan);
        Result result = scanner.next();
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        Get get = new Get(TestFromClientSide.ROW).setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(3);
        result = table.get(get);
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        // Test with max versions 1, it should still read ts[1]
        scan = new Scan().setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(1);
        scanner = table.getScanner(scan);
        result = scanner.next();
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        // Test with max versions 1, it should still read ts[1]
        get = new Get(TestFromClientSide.ROW).setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(1);
        result = table.get(get);
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        // Test with max versions 5, it should still read ts[1]
        scan = new Scan().setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(5);
        scanner = table.getScanner(scan);
        result = scanner.next();
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        // Test with max versions 5, it should still read ts[1]
        get = new Get(TestFromClientSide.ROW).setFilter(new org.apache.hadoop.hbase.filter.ValueFilter(CompareOperator.EQUAL, new SubstringComparator("value-a"))).setMaxVersions(5);
        result = table.get(get);
        // ts[0] has gone from user view. Only read ts[2] which value is less or equal to 3
        assertNResult(result, TestFromClientSide.ROW, TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, new long[]{ ts[1] }, new byte[][]{ VALUEA }, 0, 0);
        table.close();
        admin.close();
    }

    @Test
    public void testCellUtilTypeMethods() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestFromClientSide.TEST_UTIL.createTable(tableName, TestFromClientSide.FAMILY);
        final byte[] row = Bytes.toBytes("p");
        Put p = new Put(row);
        p.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER, TestFromClientSide.VALUE);
        table.put(p);
        try (ResultScanner scanner = table.getScanner(new Scan())) {
            Result result = scanner.next();
            Assert.assertNotNull(result);
            CellScanner cs = result.cellScanner();
            Assert.assertTrue(cs.advance());
            Cell c = cs.current();
            Assert.assertTrue(CellUtil.isPut(c));
            Assert.assertFalse(CellUtil.isDelete(c));
            Assert.assertFalse(cs.advance());
            Assert.assertNull(scanner.next());
        }
        Delete d = new Delete(row);
        d.addColumn(TestFromClientSide.FAMILY, TestFromClientSide.QUALIFIER);
        table.delete(d);
        Scan scan = new Scan();
        scan.setRaw(true);
        try (ResultScanner scanner = table.getScanner(scan)) {
            Result result = scanner.next();
            Assert.assertNotNull(result);
            CellScanner cs = result.cellScanner();
            Assert.assertTrue(cs.advance());
            // First cell should be the delete (masking the Put)
            Cell c = cs.current();
            Assert.assertTrue(("Cell should be a Delete: " + c), CellUtil.isDelete(c));
            Assert.assertFalse(("Cell should not be a Put: " + c), CellUtil.isPut(c));
            // Second cell should be the original Put
            Assert.assertTrue(cs.advance());
            c = cs.current();
            Assert.assertFalse(("Cell should not be a Delete: " + c), CellUtil.isDelete(c));
            Assert.assertTrue(("Cell should be a Put: " + c), CellUtil.isPut(c));
            // No more cells in this row
            Assert.assertFalse(cs.advance());
            // No more results in this scan
            Assert.assertNull(scanner.next());
        }
    }

    @Test(expected = DoNotRetryIOException.class)
    public void testCreateTableWithZeroRegionReplicas() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(Bytes.toBytes("cf"))).setRegionReplication(0).build();
        TestFromClientSide.TEST_UTIL.getAdmin().createTable(desc);
    }

    @Test(expected = DoNotRetryIOException.class)
    public void testModifyTableWithZeroRegionReplicas() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(Bytes.toBytes("cf"))).build();
        TestFromClientSide.TEST_UTIL.getAdmin().createTable(desc);
        TableDescriptor newDesc = TableDescriptorBuilder.newBuilder(desc).setRegionReplication(0).build();
        TestFromClientSide.TEST_UTIL.getAdmin().modifyTable(newDesc);
    }
}

