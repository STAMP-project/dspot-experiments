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


import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Run tests related to {@link org.apache.hadoop.hbase.filter.TimestampsFilter} using HBase client APIs.
 * Sets up the HBase mini cluster once at start. Each creates a table
 * named for the method and does its stuff against that.
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestMultipleTimestamps {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMultipleTimestamps.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMultipleTimestamps.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testReseeksWithOneColumnMiltipleTimestamp() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        Integer[] putRows = new Integer[]{ 1, 3, 5, 7 };
        Integer[] putColumns = new Integer[]{ 1, 3, 5 };
        Long[] putTimestamps = new Long[]{ 1L, 2L, 3L, 4L, 5L };
        Integer[] scanRows = new Integer[]{ 3, 5 };
        Integer[] scanColumns = new Integer[]{ 3 };
        Long[] scanTimestamps = new Long[]{ 3L, 4L };
        int scanMaxVersions = 2;
        put(ht, FAMILY, putRows, putColumns, putTimestamps);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        ResultScanner scanner = scan(ht, FAMILY, scanRows, scanColumns, scanTimestamps, scanMaxVersions);
        Cell[] kvs;
        kvs = scanner.next().rawCells();
        Assert.assertEquals(2, kvs.length);
        checkOneCell(kvs[0], FAMILY, 3, 3, 4);
        checkOneCell(kvs[1], FAMILY, 3, 3, 3);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(2, kvs.length);
        checkOneCell(kvs[0], FAMILY, 5, 3, 4);
        checkOneCell(kvs[1], FAMILY, 5, 3, 3);
        ht.close();
    }

    @Test
    public void testReseeksWithMultipleColumnOneTimestamp() throws IOException {
        TestMultipleTimestamps.LOG.info(name.getMethodName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        Integer[] putRows = new Integer[]{ 1, 3, 5, 7 };
        Integer[] putColumns = new Integer[]{ 1, 3, 5 };
        Long[] putTimestamps = new Long[]{ 1L, 2L, 3L, 4L, 5L };
        Integer[] scanRows = new Integer[]{ 3, 5 };
        Integer[] scanColumns = new Integer[]{ 3, 4 };
        Long[] scanTimestamps = new Long[]{ 3L };
        int scanMaxVersions = 2;
        put(ht, FAMILY, putRows, putColumns, putTimestamps);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        ResultScanner scanner = scan(ht, FAMILY, scanRows, scanColumns, scanTimestamps, scanMaxVersions);
        Cell[] kvs;
        kvs = scanner.next().rawCells();
        Assert.assertEquals(1, kvs.length);
        checkOneCell(kvs[0], FAMILY, 3, 3, 3);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(1, kvs.length);
        checkOneCell(kvs[0], FAMILY, 5, 3, 3);
        ht.close();
    }

    @Test
    public void testReseeksWithMultipleColumnMultipleTimestamp() throws IOException {
        TestMultipleTimestamps.LOG.info(name.getMethodName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        Integer[] putRows = new Integer[]{ 1, 3, 5, 7 };
        Integer[] putColumns = new Integer[]{ 1, 3, 5 };
        Long[] putTimestamps = new Long[]{ 1L, 2L, 3L, 4L, 5L };
        Integer[] scanRows = new Integer[]{ 5, 7 };
        Integer[] scanColumns = new Integer[]{ 3, 4, 5 };
        Long[] scanTimestamps = new Long[]{ 2L, 3L };
        int scanMaxVersions = 2;
        put(ht, FAMILY, putRows, putColumns, putTimestamps);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        Scan scan = new Scan();
        scan.setMaxVersions(10);
        ResultScanner scanner = ht.getScanner(scan);
        while (true) {
            Result r = scanner.next();
            if (r == null)
                break;

            TestMultipleTimestamps.LOG.info(("r=" + r));
        } 
        scanner = scan(ht, FAMILY, scanRows, scanColumns, scanTimestamps, scanMaxVersions);
        Cell[] kvs;
        // This looks like wrong answer.  Should be 2.  Even then we are returning wrong result,
        // timestamps that are 3 whereas should be 2 since min is inclusive.
        kvs = scanner.next().rawCells();
        Assert.assertEquals(4, kvs.length);
        checkOneCell(kvs[0], FAMILY, 5, 3, 3);
        checkOneCell(kvs[1], FAMILY, 5, 3, 2);
        checkOneCell(kvs[2], FAMILY, 5, 5, 3);
        checkOneCell(kvs[3], FAMILY, 5, 5, 2);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(4, kvs.length);
        checkOneCell(kvs[0], FAMILY, 7, 3, 3);
        checkOneCell(kvs[1], FAMILY, 7, 3, 2);
        checkOneCell(kvs[2], FAMILY, 7, 5, 3);
        checkOneCell(kvs[3], FAMILY, 7, 5, 2);
        ht.close();
    }

    @Test
    public void testReseeksWithMultipleFiles() throws IOException {
        TestMultipleTimestamps.LOG.info(name.getMethodName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        Integer[] putRows1 = new Integer[]{ 1, 2, 3 };
        Integer[] putColumns1 = new Integer[]{ 2, 5, 6 };
        Long[] putTimestamps1 = new Long[]{ 1L, 2L, 5L };
        Integer[] putRows2 = new Integer[]{ 6, 7 };
        Integer[] putColumns2 = new Integer[]{ 3, 6 };
        Long[] putTimestamps2 = new Long[]{ 4L, 5L };
        Integer[] putRows3 = new Integer[]{ 2, 3, 5 };
        Integer[] putColumns3 = new Integer[]{ 1, 2, 3 };
        Long[] putTimestamps3 = new Long[]{ 4L, 8L };
        Integer[] scanRows = new Integer[]{ 3, 5, 7 };
        Integer[] scanColumns = new Integer[]{ 3, 4, 5 };
        Long[] scanTimestamps = new Long[]{ 2L, 4L };
        int scanMaxVersions = 5;
        put(ht, FAMILY, putRows1, putColumns1, putTimestamps1);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        put(ht, FAMILY, putRows2, putColumns2, putTimestamps2);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        put(ht, FAMILY, putRows3, putColumns3, putTimestamps3);
        ResultScanner scanner = scan(ht, FAMILY, scanRows, scanColumns, scanTimestamps, scanMaxVersions);
        Cell[] kvs;
        kvs = scanner.next().rawCells();
        Assert.assertEquals(2, kvs.length);
        checkOneCell(kvs[0], FAMILY, 3, 3, 4);
        checkOneCell(kvs[1], FAMILY, 3, 5, 2);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(1, kvs.length);
        checkOneCell(kvs[0], FAMILY, 5, 3, 4);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(1, kvs.length);
        checkOneCell(kvs[0], FAMILY, 6, 3, 4);
        kvs = scanner.next().rawCells();
        Assert.assertEquals(1, kvs.length);
        checkOneCell(kvs[0], FAMILY, 7, 3, 4);
        ht.close();
    }

    @Test
    public void testWithVersionDeletes() throws Exception {
        // first test from memstore (without flushing).
        testWithVersionDeletes(false);
        // run same test against HFiles (by forcing a flush).
        testWithVersionDeletes(true);
    }

    @Test
    public void testWithMultipleVersionDeletes() throws IOException {
        TestMultipleTimestamps.LOG.info(name.getMethodName());
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        // For row:0, col:0: insert versions 1 through 5.
        putNVersions(ht, FAMILY, 0, 0, 1, 5);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        // delete all versions before 4.
        deleteAllVersionsBefore(ht, FAMILY, 0, 0, 4);
        // request a bunch of versions including the deleted version. We should
        // only get back entries for the versions that exist.
        Cell[] kvs = getNVersions(ht, FAMILY, 0, 0, Arrays.asList(2L, 3L));
        Assert.assertEquals(0, kvs.length);
        ht.close();
    }

    @Test
    public void testWithColumnDeletes() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        // For row:0, col:0: insert versions 1 through 5.
        putNVersions(ht, FAMILY, 0, 0, 1, 5);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        // delete all versions before 4.
        deleteColumn(ht, FAMILY, 0, 0);
        // request a bunch of versions including the deleted version. We should
        // only get back entries for the versions that exist.
        Cell[] kvs = getNVersions(ht, FAMILY, 0, 0, Arrays.asList(2L, 3L));
        Assert.assertEquals(0, kvs.length);
        ht.close();
    }

    @Test
    public void testWithFamilyDeletes() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestMultipleTimestamps.TEST_UTIL.createTable(tableName, FAMILIES, Integer.MAX_VALUE);
        // For row:0, col:0: insert versions 1 through 5.
        putNVersions(ht, FAMILY, 0, 0, 1, 5);
        TestMultipleTimestamps.TEST_UTIL.flush(tableName);
        // delete all versions before 4.
        deleteFamily(ht, FAMILY, 0);
        // request a bunch of versions including the deleted version. We should
        // only get back entries for the versions that exist.
        Cell[] kvs = getNVersions(ht, FAMILY, 0, 0, Arrays.asList(2L, 3L));
        Assert.assertEquals(0, kvs.length);
        ht.close();
    }
}

