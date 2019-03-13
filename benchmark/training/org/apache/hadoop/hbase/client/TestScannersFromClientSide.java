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


import HConstants.DEFAULT_HBASE_CLIENT_SCANNER_MAX_RESULT_SIZE;
import HConstants.HBASE_CLIENT_SCANNER_MAX_RESULT_SIZE_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTestConst;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.ipc.RpcClient;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A client-side test, mostly testing scanners with various parameters.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestScannersFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannersFromClientSide.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestScannersFromClientSide.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] VALUE = Bytes.toBytes("testValue");

    @Rule
    public TestName name = new TestName();

    /**
     * Test from client side for batch of scan
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScanBatch() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, 8);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);
        Put put;
        Scan scan;
        Delete delete;
        Result result;
        ResultScanner scanner;
        boolean toLog = true;
        List<Cell> kvListExp;
        // table: row, family, c0:0, c1:1, ... , c7:7
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[i], i, TestScannersFromClientSide.VALUE);
            put.add(kv);
        }
        ht.put(put);
        // table: row, family, c0:0, c1:1, ..., c6:2, c6:6 , c7:7
        put = new Put(TestScannersFromClientSide.ROW);
        KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[6], 2, TestScannersFromClientSide.VALUE);
        put.add(kv);
        ht.put(put);
        // delete upto ts: 3
        delete = new Delete(TestScannersFromClientSide.ROW);
        delete.addFamily(TestScannersFromClientSide.FAMILY, 3);
        ht.delete(delete);
        // without batch
        scan = new Scan().withStartRow(TestScannersFromClientSide.ROW);
        scan.setMaxVersions();
        scanner = ht.getScanner(scan);
        // c4:4, c5:5, c6:6, c7:7
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[4], 4, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[5], 5, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[6], 6, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[7], 7, TestScannersFromClientSide.VALUE));
        result = scanner.next();
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing first batch of scan");
        // with batch
        scan = new Scan().withStartRow(TestScannersFromClientSide.ROW);
        scan.setMaxVersions();
        scan.setBatch(2);
        scanner = ht.getScanner(scan);
        // First batch: c4:4, c5:5
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[4], 4, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[5], 5, TestScannersFromClientSide.VALUE));
        result = scanner.next();
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing first batch of scan");
        // Second batch: c6:6, c7:7
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[6], 6, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[7], 7, TestScannersFromClientSide.VALUE));
        result = scanner.next();
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing second batch of scan");
    }

    @Test
    public void testMaxResultSizeIsSetToDefault() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);
        // The max result size we expect the scan to use by default.
        long expectedMaxResultSize = TestScannersFromClientSide.TEST_UTIL.getConfiguration().getLong(HBASE_CLIENT_SCANNER_MAX_RESULT_SIZE_KEY, DEFAULT_HBASE_CLIENT_SCANNER_MAX_RESULT_SIZE);
        int numRows = 5;
        byte[][] ROWS = HTestConst.makeNAscii(TestScannersFromClientSide.ROW, numRows);
        int numQualifiers = 10;
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, numQualifiers);
        // Specify the cell size such that a single row will be larger than the default
        // value of maxResultSize. This means that Scan RPCs should return at most a single
        // result back to the client.
        int cellSize = ((int) (expectedMaxResultSize / (numQualifiers - 1)));
        byte[] cellValue = Bytes.createMaxByteArray(cellSize);
        Put put;
        List<Put> puts = new ArrayList<>();
        for (int row = 0; row < (ROWS.length); row++) {
            put = new Put(ROWS[row]);
            for (int qual = 0; qual < (QUALIFIERS.length); qual++) {
                KeyValue kv = new KeyValue(ROWS[row], TestScannersFromClientSide.FAMILY, QUALIFIERS[qual], cellValue);
                put.add(kv);
            }
            puts.add(put);
        }
        ht.put(puts);
        // Create a scan with the default configuration.
        Scan scan = new Scan();
        ResultScanner scanner = ht.getScanner(scan);
        Assert.assertTrue((scanner instanceof ClientScanner));
        ClientScanner clientScanner = ((ClientScanner) (scanner));
        // Call next to issue a single RPC to the server
        scanner.next();
        // The scanner should have, at most, a single result in its cache. If there more results exists
        // in the cache it means that more than the expected max result size was fetched.
        Assert.assertTrue((("The cache contains: " + (clientScanner.getCacheSize())) + " results"), ((clientScanner.getCacheSize()) <= 1));
    }

    /**
     * Scan on not existing table should throw the exception with correct message
     */
    @Test
    public void testScannerForNotExistingTable() {
        String[] tableNames = new String[]{ "A", "Z", "A:A", "Z:Z" };
        for (String tableName : tableNames) {
            try {
                Table table = TestScannersFromClientSide.TEST_UTIL.getConnection().getTable(TableName.valueOf(tableName));
                testSmallScan(table, true, 1, 5);
                Assert.fail("TableNotFoundException was not thrown");
            } catch (TableNotFoundException e) {
                // We expect that the message for TableNotFoundException would have only the table name only
                // Otherwise that would mean that localeRegionInMeta doesn't work properly
                Assert.assertEquals(e.getMessage(), tableName);
            } catch (Exception e) {
                Assert.fail(("Unexpected exception " + (e.getMessage())));
            }
        }
    }

    @Test
    public void testSmallScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        int numRows = 10;
        byte[][] ROWS = HTestConst.makeNAscii(TestScannersFromClientSide.ROW, numRows);
        int numQualifiers = 10;
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, numQualifiers);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);
        Put put;
        List<Put> puts = new ArrayList<>();
        for (int row = 0; row < (ROWS.length); row++) {
            put = new Put(ROWS[row]);
            for (int qual = 0; qual < (QUALIFIERS.length); qual++) {
                KeyValue kv = new KeyValue(ROWS[row], TestScannersFromClientSide.FAMILY, QUALIFIERS[qual], TestScannersFromClientSide.VALUE);
                put.add(kv);
            }
            puts.add(put);
        }
        ht.put(puts);
        int expectedRows = numRows;
        int expectedCols = numRows * numQualifiers;
        // Test normal and reversed
        testSmallScan(ht, true, expectedRows, expectedCols);
        testSmallScan(ht, false, expectedRows, expectedCols);
    }

    /**
     * Test from client side for get with maxResultPerCF set
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetMaxResults() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = HTestConst.makeNAscii(TestScannersFromClientSide.FAMILY, 3);
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, 20);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        Get get;
        Put put;
        Result result;
        boolean toLog = true;
        List<Cell> kvListExp;
        kvListExp = new ArrayList();
        // Insert one CF for row[0]
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < 10; i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE);
            put.add(kv);
            kvListExp.add(kv);
        }
        ht.put(put);
        get = new Get(TestScannersFromClientSide.ROW);
        result = ht.get(get);
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing without setting maxResults");
        get = new Get(TestScannersFromClientSide.ROW);
        get.setMaxResultsPerColumnFamily(2);
        result = ht.get(get);
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[0], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[1], 1, TestScannersFromClientSide.VALUE));
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing basic setMaxResults");
        // Filters: ColumnRangeFilter
        get = new Get(TestScannersFromClientSide.ROW);
        get.setMaxResultsPerColumnFamily(5);
        get.setFilter(new ColumnRangeFilter(QUALIFIERS[2], true, QUALIFIERS[5], true));
        result = ht.get(get);
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[2], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[3], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[4], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[5], 1, TestScannersFromClientSide.VALUE));
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing single CF with CRF");
        // Insert two more CF for row[0]
        // 20 columns for CF2, 10 columns for CF1
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE);
            put.add(kv);
        }
        ht.put(put);
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < 10; i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE);
            put.add(kv);
        }
        ht.put(put);
        get = new Get(TestScannersFromClientSide.ROW);
        get.setMaxResultsPerColumnFamily(12);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        result = ht.get(get);
        kvListExp = new ArrayList();
        // Exp: CF1:q0, ..., q9, CF2: q0, q1, q10, q11, ..., q19
        for (int i = 0; i < 10; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        for (int i = 0; i < 2; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        for (int i = 10; i < 20; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing multiple CFs");
        // Filters: ColumnRangeFilter and ColumnPrefixFilter
        get = new Get(TestScannersFromClientSide.ROW);
        get.setMaxResultsPerColumnFamily(3);
        get.setFilter(new ColumnRangeFilter(QUALIFIERS[2], true, null, true));
        result = ht.get(get);
        kvListExp = new ArrayList();
        for (int i = 2; i < 5; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        for (int i = 2; i < 5; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        for (int i = 2; i < 5; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing multiple CFs + CRF");
        get = new Get(TestScannersFromClientSide.ROW);
        get.setMaxResultsPerColumnFamily(7);
        get.setFilter(new ColumnPrefixFilter(QUALIFIERS[1]));
        result = ht.get(get);
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[1], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[1], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[1], 1, TestScannersFromClientSide.VALUE));
        for (int i = 10; i < 16; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing multiple CFs + PFF");
    }

    /**
     * Test from client side for scan with maxResultPerCF set
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScanMaxResults() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] ROWS = HTestConst.makeNAscii(TestScannersFromClientSide.ROW, 2);
        byte[][] FAMILIES = HTestConst.makeNAscii(TestScannersFromClientSide.FAMILY, 3);
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, 10);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        Put put;
        Scan scan;
        Result result;
        boolean toLog = true;
        List<Cell> kvListExp;
        List<Cell> kvListScan;
        kvListExp = new ArrayList();
        for (int r = 0; r < (ROWS.length); r++) {
            put = new Put(ROWS[r]);
            for (int c = 0; c < (FAMILIES.length); c++) {
                for (int q = 0; q < (QUALIFIERS.length); q++) {
                    KeyValue kv = new KeyValue(ROWS[r], FAMILIES[c], QUALIFIERS[q], 1, TestScannersFromClientSide.VALUE);
                    put.add(kv);
                    if (q < 4) {
                        kvListExp.add(kv);
                    }
                }
            }
            ht.put(put);
        }
        scan = new Scan();
        scan.setMaxResultsPerColumnFamily(4);
        ResultScanner scanner = ht.getScanner(scan);
        kvListScan = new ArrayList();
        while ((result = scanner.next()) != null) {
            for (Cell kv : result.listCells()) {
                kvListScan.add(kv);
            }
        } 
        result = Result.create(kvListScan);
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing scan with maxResults");
    }

    /**
     * Test from client side for get with rowOffset
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testGetRowOffset() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] FAMILIES = HTestConst.makeNAscii(TestScannersFromClientSide.FAMILY, 3);
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, 20);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, FAMILIES);
        Get get;
        Put put;
        Result result;
        boolean toLog = true;
        List<Cell> kvListExp;
        // Insert one CF for row
        kvListExp = new ArrayList();
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < 10; i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE);
            put.add(kv);
            // skipping first two kvs
            if (i < 2)
                continue;

            kvListExp.add(kv);
        }
        ht.put(put);
        // setting offset to 2
        get = new Get(TestScannersFromClientSide.ROW);
        get.setRowOffsetPerColumnFamily(2);
        result = ht.get(get);
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing basic setRowOffset");
        // setting offset to 20
        get = new Get(TestScannersFromClientSide.ROW);
        get.setRowOffsetPerColumnFamily(20);
        result = ht.get(get);
        kvListExp = new ArrayList();
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing offset > #kvs");
        // offset + maxResultPerCF
        get = new Get(TestScannersFromClientSide.ROW);
        get.setRowOffsetPerColumnFamily(4);
        get.setMaxResultsPerColumnFamily(5);
        result = ht.get(get);
        kvListExp = new ArrayList();
        for (int i = 4; i < 9; i++) {
            kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE));
        }
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing offset + setMaxResultsPerCF");
        // Filters: ColumnRangeFilter
        get = new Get(TestScannersFromClientSide.ROW);
        get.setRowOffsetPerColumnFamily(1);
        get.setFilter(new ColumnRangeFilter(QUALIFIERS[2], true, QUALIFIERS[5], true));
        result = ht.get(get);
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[3], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[4], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[0], QUALIFIERS[5], 1, TestScannersFromClientSide.VALUE));
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing offset with CRF");
        // Insert into two more CFs for row
        // 10 columns for CF2, 10 columns for CF1
        for (int j = 2; j > 0; j--) {
            put = new Put(TestScannersFromClientSide.ROW);
            for (int i = 0; i < 10; i++) {
                KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[j], QUALIFIERS[i], 1, TestScannersFromClientSide.VALUE);
                put.add(kv);
            }
            ht.put(put);
        }
        get = new Get(TestScannersFromClientSide.ROW);
        get.setRowOffsetPerColumnFamily(4);
        get.setMaxResultsPerColumnFamily(2);
        get.addFamily(FAMILIES[1]);
        get.addFamily(FAMILIES[2]);
        result = ht.get(get);
        kvListExp = new ArrayList();
        // Exp: CF1:q4, q5, CF2: q4, q5
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[4], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[1], QUALIFIERS[5], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[4], 1, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, FAMILIES[2], QUALIFIERS[5], 1, TestScannersFromClientSide.VALUE));
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing offset + multiple CFs + maxResults");
    }

    @Test
    public void testScanRawDeleteFamilyVersion() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);
        Configuration conf = new Configuration(TestScannersFromClientSide.TEST_UTIL.getConfiguration());
        conf.set(HConstants.RPC_CODEC_CONF_KEY, "");
        conf.set(RpcClient.DEFAULT_CODEC_CLASS, "");
        try (Connection connection = ConnectionFactory.createConnection(conf);Table table = connection.getTable(tableName)) {
            Delete delete = new Delete(TestScannersFromClientSide.ROW);
            delete.addFamilyVersion(TestScannersFromClientSide.FAMILY, 0L);
            table.delete(delete);
            Scan scan = new Scan(TestScannersFromClientSide.ROW).setRaw(true);
            ResultScanner scanner = table.getScanner(scan);
            int count = 0;
            while ((scanner.next()) != null) {
                count++;
            } 
            Assert.assertEquals(1, count);
        } finally {
            TestScannersFromClientSide.TEST_UTIL.deleteTable(tableName);
        }
    }

    /**
     * Test from client side for scan while the region is reopened
     * on the same region server.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScanOnReopenedRegion() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannersFromClientSide.QUALIFIER, 2);
        Table ht = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);
        Put put;
        Scan scan;
        Result result;
        ResultScanner scanner;
        boolean toLog = false;
        List<Cell> kvListExp;
        // table: row, family, c0:0, c1:1
        put = new Put(TestScannersFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            KeyValue kv = new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[i], i, TestScannersFromClientSide.VALUE);
            put.add(kv);
        }
        ht.put(put);
        scan = new Scan().withStartRow(TestScannersFromClientSide.ROW);
        scanner = ht.getScanner(scan);
        HRegionLocation loc;
        try (RegionLocator locator = TestScannersFromClientSide.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            loc = locator.getRegionLocation(TestScannersFromClientSide.ROW);
        }
        HRegionInfo hri = loc.getRegionInfo();
        MiniHBaseCluster cluster = TestScannersFromClientSide.TEST_UTIL.getMiniHBaseCluster();
        byte[] regionName = hri.getRegionName();
        int i = cluster.getServerWith(regionName);
        HRegionServer rs = cluster.getRegionServer(i);
        TestScannersFromClientSide.LOG.info(("Unassigning " + hri));
        TestScannersFromClientSide.TEST_UTIL.getAdmin().unassign(hri.getRegionName(), true);
        long startTime = EnvironmentEdgeManager.currentTime();
        long timeOut = 10000;
        boolean offline = false;
        while (true) {
            if ((rs.getOnlineRegion(regionName)) == null) {
                offline = true;
                break;
            }
            Assert.assertTrue("Timed out in closing the testing region", ((EnvironmentEdgeManager.currentTime()) < (startTime + timeOut)));
        } 
        Assert.assertTrue(offline);
        TestScannersFromClientSide.LOG.info(("Assigning " + hri));
        TestScannersFromClientSide.TEST_UTIL.getAdmin().assign(hri.getRegionName());
        startTime = EnvironmentEdgeManager.currentTime();
        while (true) {
            rs = cluster.getRegionServer(cluster.getServerWith(regionName));
            if ((rs != null) && ((rs.getOnlineRegion(regionName)) != null)) {
                offline = false;
                break;
            }
            Assert.assertTrue("Timed out in open the testing region", ((EnvironmentEdgeManager.currentTime()) < (startTime + timeOut)));
        } 
        Assert.assertFalse(offline);
        // c0:0, c1:1
        kvListExp = new ArrayList();
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[0], 0, TestScannersFromClientSide.VALUE));
        kvListExp.add(new KeyValue(TestScannersFromClientSide.ROW, TestScannersFromClientSide.FAMILY, QUALIFIERS[1], 1, TestScannersFromClientSide.VALUE));
        result = scanner.next();
        TestScannersFromClientSide.verifyResult(result, kvListExp, toLog, "Testing scan on re-opened region");
    }

    @Test
    public void testAsyncScannerWithSmallData() throws Exception {
        testAsyncScanner(TableName.valueOf(name.getMethodName()), 2, 3, 10, (-1), null);
    }

    @Test
    public void testAsyncScannerWithManyRows() throws Exception {
        testAsyncScanner(TableName.valueOf(name.getMethodName()), 30000, 1, 1, (-1), null);
    }

    @Test
    public void testAsyncScannerWithoutCaching() throws Exception {
        testAsyncScanner(TableName.valueOf(name.getMethodName()), 5, 1, 1, 1, ( b) -> {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch ( ex) {
            }
        });
    }

    @Test
    public void testReadExpiredDataForRawScan() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        long ts = (System.currentTimeMillis()) - 10000;
        byte[] value = Bytes.toBytes("expired");
        try (Table table = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY)) {
            table.put(new Put(TestScannersFromClientSide.ROW).addColumn(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER, ts, value));
            Assert.assertArrayEquals(value, table.get(new Get(TestScannersFromClientSide.ROW)).getValue(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER));
            TestScannersFromClientSide.TEST_UTIL.getAdmin().modifyColumnFamily(tableName, new HColumnDescriptor(TestScannersFromClientSide.FAMILY).setTimeToLive(5));
            try (ResultScanner scanner = table.getScanner(TestScannersFromClientSide.FAMILY)) {
                Assert.assertNull(scanner.next());
            }
            try (ResultScanner scanner = table.getScanner(new Scan().setRaw(true))) {
                Assert.assertArrayEquals(value, scanner.next().getValue(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER));
                Assert.assertNull(scanner.next());
            }
        }
    }

    @Test
    public void testScanWithColumnsAndFilterAndVersion() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        try (Table table = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY, 4)) {
            for (int i = 0; i < 4; i++) {
                Put put = new Put(TestScannersFromClientSide.ROW);
                put.addColumn(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER, TestScannersFromClientSide.VALUE);
                table.put(put);
            }
            Scan scan = new Scan();
            scan.addColumn(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER);
            scan.setFilter(new org.apache.hadoop.hbase.filter.QualifierFilter(CompareOperator.EQUAL, new BinaryComparator(TestScannersFromClientSide.QUALIFIER)));
            scan.readVersions(3);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Result result = scanner.next();
                Assert.assertEquals(3, result.size());
            }
        }
    }

    @Test
    public void testScanWithSameStartRowStopRow() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        try (Table table = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY)) {
            table.put(new Put(TestScannersFromClientSide.ROW).addColumn(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER, TestScannersFromClientSide.VALUE));
            Scan scan = new Scan().withStartRow(TestScannersFromClientSide.ROW).withStopRow(TestScannersFromClientSide.ROW);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Assert.assertNull(scanner.next());
            }
            scan = new Scan().withStartRow(TestScannersFromClientSide.ROW, true).withStopRow(TestScannersFromClientSide.ROW, true);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Result result = scanner.next();
                Assert.assertNotNull(result);
                Assert.assertArrayEquals(TestScannersFromClientSide.ROW, result.getRow());
                Assert.assertArrayEquals(TestScannersFromClientSide.VALUE, result.getValue(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER));
                Assert.assertNull(scanner.next());
            }
            scan = new Scan().withStartRow(TestScannersFromClientSide.ROW, true).withStopRow(TestScannersFromClientSide.ROW, false);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Assert.assertNull(scanner.next());
            }
            scan = new Scan().withStartRow(TestScannersFromClientSide.ROW, false).withStopRow(TestScannersFromClientSide.ROW, false);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Assert.assertNull(scanner.next());
            }
            scan = new Scan().withStartRow(TestScannersFromClientSide.ROW, false).withStopRow(TestScannersFromClientSide.ROW, true);
            try (ResultScanner scanner = table.getScanner(scan)) {
                Assert.assertNull(scanner.next());
            }
        }
    }

    @Test
    public void testReverseScanWithFlush() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        final int BATCH_SIZE = 10;
        final int ROWS_TO_INSERT = 100;
        final byte[] LARGE_VALUE = TestFromClientSide3.generateHugeValue((128 * 1024));
        try (Table table = TestScannersFromClientSide.TEST_UTIL.createTable(tableName, TestScannersFromClientSide.FAMILY);Admin admin = TestScannersFromClientSide.TEST_UTIL.getAdmin()) {
            List<Put> putList = new ArrayList<>();
            for (long i = 0; i < ROWS_TO_INSERT; i++) {
                Put put = new Put(Bytes.toBytes(i));
                put.addColumn(TestScannersFromClientSide.FAMILY, TestScannersFromClientSide.QUALIFIER, LARGE_VALUE);
                putList.add(put);
                if ((putList.size()) >= BATCH_SIZE) {
                    table.put(putList);
                    admin.flush(tableName);
                    putList.clear();
                }
            }
            if (!(putList.isEmpty())) {
                table.put(putList);
                admin.flush(tableName);
                putList.clear();
            }
            Scan scan = new Scan();
            scan.setReversed(true);
            int count = 0;
            try (ResultScanner results = table.getScanner(scan)) {
                for (Result result : results) {
                    count++;
                }
            }
            Assert.assertEquals(((("Expected " + ROWS_TO_INSERT) + " rows in the table but it is ") + count), ROWS_TO_INSERT, count);
        }
    }
}

