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


import Durability.SKIP_WAL;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ RegionServerTests.class, LargeTests.class })
public class TestRegionServerMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionServerMetrics.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionServerMetrics.class);

    @Rule
    public TestName testName = new TestName();

    private static MetricsAssertHelper metricsHelper;

    private static MiniHBaseCluster cluster;

    private static HRegionServer rs;

    private static Configuration conf;

    private static HBaseTestingUtility TEST_UTIL;

    private static Connection connection;

    private static MetricsRegionServer metricsRegionServer;

    private static MetricsRegionServerSource serverSource;

    private static final int NUM_SCAN_NEXT = 30;

    private static int numScanNext = 0;

    private static byte[] cf = Bytes.toBytes("cf");

    private static byte[] row = Bytes.toBytes("row");

    private static byte[] qualifier = Bytes.toBytes("qual");

    private static byte[] val = Bytes.toBytes("val");

    private static Admin admin;

    private static boolean TABLES_ON_MASTER;

    TableName tableName;

    Table table;

    @Test
    public void testRegionCount() throws Exception {
        TestRegionServerMetrics.metricsHelper.assertGauge("regionCount", (TestRegionServerMetrics.TABLES_ON_MASTER ? 1 : 2), TestRegionServerMetrics.serverSource);
    }

    @Test
    public void testLocalFiles() throws Exception {
        assertGauge("percentFilesLocal", 0);
        assertGauge("percentFilesLocalSecondaryRegions", 0);
    }

    @Test
    public void testRequestCount() throws Exception {
        // Do a first put to be sure that the connection is established, meta is there and so on.
        doNPuts(1, false);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        long requests = TestRegionServerMetrics.metricsHelper.getCounter("totalRequestCount", TestRegionServerMetrics.serverSource);
        long rowActionRequests = TestRegionServerMetrics.metricsHelper.getCounter("totalRowActionRequestCount", TestRegionServerMetrics.serverSource);
        long readRequests = TestRegionServerMetrics.metricsHelper.getCounter("readRequestCount", TestRegionServerMetrics.serverSource);
        long writeRequests = TestRegionServerMetrics.metricsHelper.getCounter("writeRequestCount", TestRegionServerMetrics.serverSource);
        doNPuts(30, false);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertCounter("totalRequestCount", (requests + 30));
        assertCounter("totalRowActionRequestCount", (rowActionRequests + 30));
        assertCounter("readRequestCount", readRequests);
        assertCounter("writeRequestCount", (writeRequests + 30));
        doNGets(10, false);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertCounter("totalRequestCount", (requests + 40));
        assertCounter("totalRowActionRequestCount", (rowActionRequests + 40));
        assertCounter("readRequestCount", (readRequests + 10));
        assertCounter("writeRequestCount", (writeRequests + 30));
        assertRegionMetrics("getCount", 10);
        assertRegionMetrics("putCount", 31);
        doNGets(10, true);// true = batch

        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("totalRequestCount", (requests + 41));
            assertCounter("totalRowActionRequestCount", (rowActionRequests + 50));
            assertCounter("readRequestCount", (readRequests + 20));
        }
        assertCounter("writeRequestCount", (writeRequests + 30));
        doNPuts(30, true);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("totalRequestCount", (requests + 42));
            assertCounter("totalRowActionRequestCount", (rowActionRequests + 80));
            assertCounter("readRequestCount", (readRequests + 20));
        }
        assertCounter("writeRequestCount", (writeRequests + 60));
        doScan(10, false);// test after batch put so we have enough lines

        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("totalRequestCount", (requests + 52));
            assertCounter("totalRowActionRequestCount", (rowActionRequests + 90));
            assertCounter("readRequestCount", (readRequests + 30));
        }
        assertCounter("writeRequestCount", (writeRequests + 60));
        TestRegionServerMetrics.numScanNext += 10;
        doScan(10, true);// true = caching

        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("totalRequestCount", (requests + 53));
            assertCounter("totalRowActionRequestCount", (rowActionRequests + 100));
            assertCounter("readRequestCount", (readRequests + 40));
        }
        assertCounter("writeRequestCount", (writeRequests + 60));
        TestRegionServerMetrics.numScanNext += 1;
    }

    @Test
    public void testGet() throws Exception {
        // Do a first put to be sure that the connection is established, meta is there and so on.
        doNPuts(1, false);
        doNGets(10, false);
        assertRegionMetrics("getCount", 10);
        TestRegionServerMetrics.metricsHelper.assertCounterGt("Get_num_ops", 10, TestRegionServerMetrics.serverSource);
    }

    @Test
    public void testMutationsWithoutWal() throws Exception {
        Put p = new Put(TestRegionServerMetrics.row).addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, TestRegionServerMetrics.val).setDurability(SKIP_WAL);
        table.put(p);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertGauge("mutationsWithoutWALCount", 1);
        long minLength = (((TestRegionServerMetrics.row.length) + (TestRegionServerMetrics.cf.length)) + (TestRegionServerMetrics.qualifier.length)) + (TestRegionServerMetrics.val.length);
        TestRegionServerMetrics.metricsHelper.assertGaugeGt("mutationsWithoutWALSize", minLength, TestRegionServerMetrics.serverSource);
    }

    @Test
    public void testStoreCount() throws Exception {
        // Force a hfile.
        doNPuts(1, false);
        TestRegionServerMetrics.TEST_UTIL.getAdmin().flush(tableName);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertGauge("storeCount", (TestRegionServerMetrics.TABLES_ON_MASTER ? 1 : 5));
        assertGauge("storeFileCount", 1);
    }

    @Test
    public void testStoreFileAge() throws Exception {
        // Force a hfile.
        doNPuts(1, false);
        TestRegionServerMetrics.TEST_UTIL.getAdmin().flush(tableName);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        Assert.assertTrue(((TestRegionServerMetrics.metricsHelper.getGaugeLong("maxStoreFileAge", TestRegionServerMetrics.serverSource)) > 0));
        Assert.assertTrue(((TestRegionServerMetrics.metricsHelper.getGaugeLong("minStoreFileAge", TestRegionServerMetrics.serverSource)) > 0));
        Assert.assertTrue(((TestRegionServerMetrics.metricsHelper.getGaugeLong("avgStoreFileAge", TestRegionServerMetrics.serverSource)) > 0));
    }

    @Test
    public void testCheckAndPutCount() throws Exception {
        byte[] valOne = Bytes.toBytes("Value");
        byte[] valTwo = Bytes.toBytes("ValueTwo");
        byte[] valThree = Bytes.toBytes("ValueThree");
        Put p = new Put(TestRegionServerMetrics.row);
        p.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, valOne);
        table.put(p);
        Put pTwo = new Put(TestRegionServerMetrics.row);
        pTwo.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, valTwo);
        table.checkAndMutate(TestRegionServerMetrics.row, TestRegionServerMetrics.cf).qualifier(TestRegionServerMetrics.qualifier).ifEquals(valOne).thenPut(pTwo);
        Put pThree = new Put(TestRegionServerMetrics.row);
        pThree.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, valThree);
        table.checkAndMutate(TestRegionServerMetrics.row, TestRegionServerMetrics.cf).qualifier(TestRegionServerMetrics.qualifier).ifEquals(valOne).thenPut(pThree);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertCounter("checkMutateFailedCount", 1);
        assertCounter("checkMutatePassedCount", 1);
    }

    @Test
    public void testIncrement() throws Exception {
        Put p = new Put(TestRegionServerMetrics.row).addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, Bytes.toBytes(0L));
        table.put(p);
        for (int count = 0; count < 13; count++) {
            Increment inc = new Increment(TestRegionServerMetrics.row);
            inc.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, 100);
            table.increment(inc);
        }
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertCounter("incrementNumOps", 13);
    }

    @Test
    public void testAppend() throws Exception {
        doNPuts(1, false);
        for (int count = 0; count < 73; count++) {
            Append append = new Append(TestRegionServerMetrics.row);
            append.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, Bytes.toBytes(",Test"));
            table.append(append);
        }
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        assertCounter("appendNumOps", 73);
    }

    @Test
    public void testScanSize() throws Exception {
        doNPuts(100, true);// batch put

        Scan s = new Scan();
        s.setBatch(1);
        s.setCaching(1);
        ResultScanner resultScanners = table.getScanner(s);
        for (int nextCount = 0; nextCount < (TestRegionServerMetrics.NUM_SCAN_NEXT); nextCount++) {
            Result result = resultScanners.next();
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.size());
        }
        TestRegionServerMetrics.numScanNext += TestRegionServerMetrics.NUM_SCAN_NEXT;
        assertRegionMetrics("scanCount", TestRegionServerMetrics.NUM_SCAN_NEXT);
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("ScanSize_num_ops", TestRegionServerMetrics.numScanNext);
        }
    }

    @Test
    public void testScanTime() throws Exception {
        doNPuts(100, true);
        Scan s = new Scan();
        s.setBatch(1);
        s.setCaching(1);
        ResultScanner resultScanners = table.getScanner(s);
        for (int nextCount = 0; nextCount < (TestRegionServerMetrics.NUM_SCAN_NEXT); nextCount++) {
            Result result = resultScanners.next();
            Assert.assertNotNull(result);
            Assert.assertEquals(1, result.size());
        }
        TestRegionServerMetrics.numScanNext += TestRegionServerMetrics.NUM_SCAN_NEXT;
        assertRegionMetrics("scanCount", TestRegionServerMetrics.NUM_SCAN_NEXT);
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("ScanTime_num_ops", TestRegionServerMetrics.numScanNext);
        }
    }

    @Test
    public void testScanSizeForSmallScan() throws Exception {
        doNPuts(100, true);
        Scan s = new Scan();
        s.setSmall(true);
        s.setCaching(1);
        ResultScanner resultScanners = table.getScanner(s);
        for (int nextCount = 0; nextCount < (TestRegionServerMetrics.NUM_SCAN_NEXT); nextCount++) {
            Result result = resultScanners.next();
            Assert.assertNotNull(result);
            if (TestRegionServerMetrics.TABLES_ON_MASTER) {
                Assert.assertEquals(1, result.size());
            }
        }
        TestRegionServerMetrics.numScanNext += TestRegionServerMetrics.NUM_SCAN_NEXT;
        assertRegionMetrics("scanCount", TestRegionServerMetrics.NUM_SCAN_NEXT);
        if (TestRegionServerMetrics.TABLES_ON_MASTER) {
            assertCounter("ScanSize_num_ops", TestRegionServerMetrics.numScanNext);
        }
    }

    @Test
    public void testMobMetrics() throws IOException, InterruptedException {
        TableName tableName = TableName.valueOf("testMobMetricsLocal");
        int numHfiles = 5;
        HTableDescriptor htd = new HTableDescriptor(tableName);
        HColumnDescriptor hcd = new HColumnDescriptor(TestRegionServerMetrics.cf);
        hcd.setMobEnabled(true);
        hcd.setMobThreshold(0);
        htd.addFamily(hcd);
        byte[] val = Bytes.toBytes("mobdata");
        try {
            Table table = TestRegionServerMetrics.TEST_UTIL.createTable(htd, new byte[0][0], TestRegionServerMetrics.conf);
            HRegion region = TestRegionServerMetrics.rs.getRegions(tableName).get(0);
            for (int insertCount = 0; insertCount < numHfiles; insertCount++) {
                Put p = new Put(Bytes.toBytes(insertCount));
                p.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, val);
                table.put(p);
                TestRegionServerMetrics.admin.flush(tableName);
            }
            TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
            assertCounter("mobFlushCount", numHfiles);
            Scan scan = new Scan(Bytes.toBytes(0), Bytes.toBytes(numHfiles));
            ResultScanner scanner = table.getScanner(scan);
            scanner.next(100);
            (TestRegionServerMetrics.numScanNext)++;// this is an ugly construct

            scanner.close();
            TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
            assertCounter("mobScanCellsCount", numHfiles);
            TestRegionServerMetrics.setMobThreshold(region, TestRegionServerMetrics.cf, 100);
            // metrics are reset by the region initialization
            region.initialize();
            region.compact(true);
            TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
            assertCounter("cellsCountCompactedFromMob", numHfiles);
            assertCounter("cellsCountCompactedToMob", 0);
            scanner = table.getScanner(scan);
            scanner.next(100);
            (TestRegionServerMetrics.numScanNext)++;// this is an ugly construct

            TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
            assertCounter("mobScanCellsCount", 0);
            for (int insertCount = numHfiles; insertCount < (2 * numHfiles); insertCount++) {
                Put p = new Put(Bytes.toBytes(insertCount));
                p.addColumn(TestRegionServerMetrics.cf, TestRegionServerMetrics.qualifier, val);
                table.put(p);
                TestRegionServerMetrics.admin.flush(tableName);
            }
            TestRegionServerMetrics.setMobThreshold(region, TestRegionServerMetrics.cf, 0);
            // closing the region forces the compaction.discharger to archive the compacted hfiles
            region.close();
            // metrics are reset by the region initialization
            region.initialize();
            region.compact(true);
            TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
            // metrics are reset by the region initialization
            assertCounter("cellsCountCompactedFromMob", 0);
            assertCounter("cellsCountCompactedToMob", (2 * numHfiles));
        } finally {
            TestRegionServerMetrics.admin.disableTable(tableName);
            TestRegionServerMetrics.admin.deleteTable(tableName);
        }
    }

    @Test
    public void testAverageRegionSize() throws Exception {
        // Force a hfile.
        doNPuts(1, false);
        TestRegionServerMetrics.TEST_UTIL.getAdmin().flush(tableName);
        TestRegionServerMetrics.metricsRegionServer.getRegionServerWrapper().forceRecompute();
        Assert.assertTrue(((TestRegionServerMetrics.metricsHelper.getGaugeDouble("averageRegionSize", TestRegionServerMetrics.serverSource)) > 0.0));
    }
}

