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


import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.tool.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestScannerWithBulkload {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerWithBulkload.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testBulkLoad() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        long l = System.currentTimeMillis();
        Admin admin = TestScannerWithBulkload.TEST_UTIL.getAdmin();
        TestScannerWithBulkload.createTable(admin, tableName);
        Scan scan = createScan();
        final Table table = init(admin, l, scan, tableName);
        // use bulkload
        final Path hfilePath = writeToHFile(l, "/temp/testBulkLoad/", "/temp/testBulkLoad/col/file", false);
        Configuration conf = TestScannerWithBulkload.TEST_UTIL.getConfiguration();
        conf.setBoolean("hbase.mapreduce.bulkload.assign.sequenceNumbers", true);
        final LoadIncrementalHFiles bulkload = new LoadIncrementalHFiles(conf);
        try (RegionLocator locator = TestScannerWithBulkload.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            bulkload.doBulkLoad(hfilePath, admin, table, locator);
        }
        ResultScanner scanner = table.getScanner(scan);
        Result result = scanner.next();
        result = scanAfterBulkLoad(scanner, result, "version2");
        Put put0 = new Put(Bytes.toBytes("row1"));
        put0.add(new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("col"), Bytes.toBytes("q"), l, Bytes.toBytes("version3")));
        table.put(put0);
        admin.flush(tableName);
        scanner = table.getScanner(scan);
        result = scanner.next();
        while (result != null) {
            List<Cell> cells = result.getColumnCells(Bytes.toBytes("col"), Bytes.toBytes("q"));
            for (Cell _c : cells) {
                if (Bytes.toString(_c.getRowArray(), _c.getRowOffset(), _c.getRowLength()).equals("row1")) {
                    System.out.println(Bytes.toString(_c.getRowArray(), _c.getRowOffset(), _c.getRowLength()));
                    System.out.println(Bytes.toString(_c.getQualifierArray(), _c.getQualifierOffset(), _c.getQualifierLength()));
                    System.out.println(Bytes.toString(_c.getValueArray(), _c.getValueOffset(), _c.getValueLength()));
                    Assert.assertEquals("version3", Bytes.toString(_c.getValueArray(), _c.getValueOffset(), _c.getValueLength()));
                }
            }
            result = scanner.next();
        } 
        scanner.close();
        table.close();
    }

    @Test
    public void testBulkLoadWithParallelScan() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final long l = System.currentTimeMillis();
        final Admin admin = TestScannerWithBulkload.TEST_UTIL.getAdmin();
        TestScannerWithBulkload.createTable(admin, tableName);
        Scan scan = createScan();
        scan.setCaching(1);
        final Table table = init(admin, l, scan, tableName);
        // use bulkload
        final Path hfilePath = writeToHFile(l, "/temp/testBulkLoadWithParallelScan/", "/temp/testBulkLoadWithParallelScan/col/file", false);
        Configuration conf = TestScannerWithBulkload.TEST_UTIL.getConfiguration();
        conf.setBoolean("hbase.mapreduce.bulkload.assign.sequenceNumbers", true);
        final LoadIncrementalHFiles bulkload = new LoadIncrementalHFiles(conf);
        ResultScanner scanner = table.getScanner(scan);
        Result result = scanner.next();
        // Create a scanner and then do bulk load
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                try {
                    Put put1 = new Put(Bytes.toBytes("row5"));
                    put1.add(new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes("row5"), Bytes.toBytes("col"), Bytes.toBytes("q"), l, Bytes.toBytes("version0")));
                    table.put(put1);
                    try (RegionLocator locator = TestScannerWithBulkload.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
                        bulkload.doBulkLoad(hfilePath, admin, table, locator);
                    }
                    latch.countDown();
                } catch (TableNotFoundException e) {
                } catch (IOException e) {
                }
            }
        }.start();
        latch.await();
        // By the time we do next() the bulk loaded files are also added to the kv
        // scanner
        scanAfterBulkLoad(scanner, result, "version1");
        scanner.close();
        table.close();
    }

    @Test
    public void testBulkLoadNativeHFile() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        long l = System.currentTimeMillis();
        Admin admin = TestScannerWithBulkload.TEST_UTIL.getAdmin();
        TestScannerWithBulkload.createTable(admin, tableName);
        Scan scan = createScan();
        final Table table = init(admin, l, scan, tableName);
        // use bulkload
        final Path hfilePath = writeToHFile(l, "/temp/testBulkLoadNativeHFile/", "/temp/testBulkLoadNativeHFile/col/file", true);
        Configuration conf = TestScannerWithBulkload.TEST_UTIL.getConfiguration();
        conf.setBoolean("hbase.mapreduce.bulkload.assign.sequenceNumbers", true);
        final LoadIncrementalHFiles bulkload = new LoadIncrementalHFiles(conf);
        try (RegionLocator locator = TestScannerWithBulkload.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            bulkload.doBulkLoad(hfilePath, admin, table, locator);
        }
        ResultScanner scanner = table.getScanner(scan);
        Result result = scanner.next();
        // We had 'version0', 'version1' for 'row1,col:q' in the table.
        // Bulk load added 'version2'  scanner should be able to see 'version2'
        result = scanAfterBulkLoad(scanner, result, "version2");
        Put put0 = new Put(Bytes.toBytes("row1"));
        put0.add(new org.apache.hadoop.hbase.KeyValue(Bytes.toBytes("row1"), Bytes.toBytes("col"), Bytes.toBytes("q"), l, Bytes.toBytes("version3")));
        table.put(put0);
        admin.flush(tableName);
        scanner = table.getScanner(scan);
        result = scanner.next();
        while (result != null) {
            List<Cell> cells = result.getColumnCells(Bytes.toBytes("col"), Bytes.toBytes("q"));
            for (Cell _c : cells) {
                if (Bytes.toString(_c.getRowArray(), _c.getRowOffset(), _c.getRowLength()).equals("row1")) {
                    System.out.println(Bytes.toString(_c.getRowArray(), _c.getRowOffset(), _c.getRowLength()));
                    System.out.println(Bytes.toString(_c.getQualifierArray(), _c.getQualifierOffset(), _c.getQualifierLength()));
                    System.out.println(Bytes.toString(_c.getValueArray(), _c.getValueOffset(), _c.getValueLength()));
                    Assert.assertEquals("version3", Bytes.toString(_c.getValueArray(), _c.getValueOffset(), _c.getValueLength()));
                }
            }
            result = scanner.next();
        } 
        scanner.close();
        table.close();
    }
}

