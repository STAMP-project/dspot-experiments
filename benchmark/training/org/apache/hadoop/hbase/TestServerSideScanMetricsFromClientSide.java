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


import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(MediumTests.class)
public class TestServerSideScanMetricsFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestServerSideScanMetricsFromClientSide.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Table TABLE = null;

    /**
     * Table configuration
     */
    private static TableName TABLE_NAME = TableName.valueOf("testTable");

    private static int NUM_ROWS = 10;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[][] ROWS = HTestConst.makeNAscii(TestServerSideScanMetricsFromClientSide.ROW, TestServerSideScanMetricsFromClientSide.NUM_ROWS);

    // Should keep this value below 10 to keep generation of expected kv's simple. If above 10 then
    // table/row/cf1/... will be followed by table/row/cf10/... instead of table/row/cf2/... which
    // breaks the simple generation of expected kv's
    private static int NUM_FAMILIES = 1;

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES = HTestConst.makeNAscii(TestServerSideScanMetricsFromClientSide.FAMILY, TestServerSideScanMetricsFromClientSide.NUM_FAMILIES);

    private static int NUM_QUALIFIERS = 1;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[][] QUALIFIERS = HTestConst.makeNAscii(TestServerSideScanMetricsFromClientSide.QUALIFIER, TestServerSideScanMetricsFromClientSide.NUM_QUALIFIERS);

    private static int VALUE_SIZE = 10;

    private static byte[] VALUE = Bytes.createMaxByteArray(TestServerSideScanMetricsFromClientSide.VALUE_SIZE);

    private static int NUM_COLS = (TestServerSideScanMetricsFromClientSide.NUM_FAMILIES) * (TestServerSideScanMetricsFromClientSide.NUM_QUALIFIERS);

    // Approximation of how large the heap size of cells in our table. Should be accessed through
    // getCellHeapSize().
    private static long CELL_HEAP_SIZE = -1;

    @Test
    public void testRowsSeenMetricWithSync() throws Exception {
        testRowsSeenMetric(false);
    }

    @Test
    public void testRowsSeenMetricWithAsync() throws Exception {
        testRowsSeenMetric(true);
    }

    @Test
    public void testRowsFilteredMetric() throws Exception {
        // Base scan configuration
        Scan baseScan;
        baseScan = new Scan();
        baseScan.setScanMetricsEnabled(true);
        // Test case where scan uses default values
        testRowsFilteredMetric(baseScan);
        // Test case where at most one Result is retrieved per RPC
        baseScan.setCaching(1);
        testRowsFilteredMetric(baseScan);
        // Test case where size limit is very restrictive and partial results will be returned from
        // server
        baseScan.setMaxResultSize(1);
        testRowsFilteredMetric(baseScan);
        // Test a case where max result size limits response from server to only a few cells (not all
        // cells from the row)
        baseScan.setCaching(TestServerSideScanMetricsFromClientSide.NUM_ROWS);
        baseScan.setMaxResultSize(((getCellHeapSize()) * ((TestServerSideScanMetricsFromClientSide.NUM_COLS) - 1)));
        testRowsSeenMetric(baseScan);
    }
}

