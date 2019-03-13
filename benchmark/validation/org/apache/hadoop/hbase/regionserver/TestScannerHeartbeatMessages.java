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


import ScanRequest.Builder;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTestConst;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.regionserver.HRegion.RegionScannerImpl;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.ScanResponse;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hbase.thirdparty.com.google.protobuf.RpcController;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static ReturnCode.INCLUDE;
import static ReturnCode.SKIP;


/**
 * Here we test to make sure that scans return the expected Results when the server is sending the
 * Client heartbeat messages. Heartbeat messages are essentially keep-alive messages (they prevent
 * the scanner on the client side from timing out). A heartbeat message is sent from the server to
 * the client when the server has exceeded the time limit during the processing of the scan. When
 * the time limit is reached, the server will return to the Client whatever Results it has
 * accumulated (potentially empty).
 */
@Category(MediumTests.class)
public class TestScannerHeartbeatMessages {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerHeartbeatMessages.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static Table TABLE = null;

    /**
     * Table configuration
     */
    private static TableName TABLE_NAME = TableName.valueOf("testScannerHeartbeatMessagesTable");

    private static int NUM_ROWS = 5;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[][] ROWS = HTestConst.makeNAscii(TestScannerHeartbeatMessages.ROW, TestScannerHeartbeatMessages.NUM_ROWS);

    private static int NUM_FAMILIES = 4;

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES = HTestConst.makeNAscii(TestScannerHeartbeatMessages.FAMILY, TestScannerHeartbeatMessages.NUM_FAMILIES);

    private static int NUM_QUALIFIERS = 3;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[][] QUALIFIERS = HTestConst.makeNAscii(TestScannerHeartbeatMessages.QUALIFIER, TestScannerHeartbeatMessages.NUM_QUALIFIERS);

    private static int VALUE_SIZE = 128;

    private static byte[] VALUE = Bytes.createMaxByteArray(TestScannerHeartbeatMessages.VALUE_SIZE);

    // The time limit should be based on the rpc timeout at client, or the client will regards
    // the request as timeout before server return a heartbeat.
    private static int SERVER_TIMEOUT = 60000;

    // Time, in milliseconds, that the client will wait for a response from the server before timing
    // out. This value is used server side to determine when it is necessary to send a heartbeat
    // message to the client. Time limit will be 500 ms.
    private static int CLIENT_TIMEOUT = 1000;

    // In this test, we sleep after reading each row. So we should make sure after we get some number
    // of rows and sleep same times we must reach time limit, and do not timeout after next sleeping.
    private static int DEFAULT_ROW_SLEEP_TIME = 300;

    // Similar with row sleep time.
    private static int DEFAULT_CF_SLEEP_TIME = 300;

    /**
     * Test the case that the time limit for the scan is reached after each full row of cells is
     * fetched.
     */
    @Test
    public void testHeartbeatBetweenRows() throws Exception {
        testImportanceOfHeartbeats(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Configure the scan so that it can read the entire table in a single RPC. We want to test
                // the case where a scan stops on the server side due to a time limit
                Scan scan = new Scan();
                scan.setMaxResultSize(Long.MAX_VALUE);
                scan.setCaching(Integer.MAX_VALUE);
                testEquivalenceOfScanWithHeartbeats(scan, TestScannerHeartbeatMessages.DEFAULT_ROW_SLEEP_TIME, (-1), false);
                return null;
            }
        });
    }

    /**
     * Test the case that the time limit for scans is reached in between column families
     */
    @Test
    public void testHeartbeatBetweenColumnFamilies() throws Exception {
        testImportanceOfHeartbeats(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                // Configure the scan so that it can read the entire table in a single RPC. We want to test
                // the case where a scan stops on the server side due to a time limit
                Scan baseScan = new Scan();
                baseScan.setMaxResultSize(Long.MAX_VALUE);
                baseScan.setCaching(Integer.MAX_VALUE);
                // Copy the scan before each test. When a scan object is used by a scanner, some of its
                // fields may be changed such as start row
                Scan scanCopy = new Scan(baseScan);
                testEquivalenceOfScanWithHeartbeats(scanCopy, (-1), TestScannerHeartbeatMessages.DEFAULT_CF_SLEEP_TIME, false);
                scanCopy = new Scan(baseScan);
                testEquivalenceOfScanWithHeartbeats(scanCopy, (-1), TestScannerHeartbeatMessages.DEFAULT_CF_SLEEP_TIME, true);
                return null;
            }
        });
    }

    public static class SparseCellFilter extends FilterBase {
        @Override
        public ReturnCode filterCell(final Cell v) throws IOException {
            try {
                Thread.sleep((((TestScannerHeartbeatMessages.CLIENT_TIMEOUT) / 2) + 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Bytes.equals(CellUtil.cloneRow(v), TestScannerHeartbeatMessages.ROWS[((TestScannerHeartbeatMessages.NUM_ROWS) - 1)]) ? INCLUDE : SKIP;
        }

        public static Filter parseFrom(final byte[] pbBytes) {
            return new TestScannerHeartbeatMessages.SparseCellFilter();
        }
    }

    public static class SparseRowFilter extends FilterBase {
        @Override
        public boolean filterRowKey(Cell cell) throws IOException {
            try {
                Thread.sleep((((TestScannerHeartbeatMessages.CLIENT_TIMEOUT) / 2) - 100));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return !(Bytes.equals(CellUtil.cloneRow(cell), TestScannerHeartbeatMessages.ROWS[((TestScannerHeartbeatMessages.NUM_ROWS) - 1)]));
        }

        public static Filter parseFrom(final byte[] pbBytes) {
            return new TestScannerHeartbeatMessages.SparseRowFilter();
        }
    }

    /**
     * Test the case that there is a filter which filters most of cells
     */
    @Test
    public void testHeartbeatWithSparseCellFilter() throws Exception {
        testImportanceOfHeartbeats(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Scan scan = new Scan();
                scan.setMaxResultSize(Long.MAX_VALUE);
                scan.setCaching(Integer.MAX_VALUE);
                scan.setFilter(new TestScannerHeartbeatMessages.SparseCellFilter());
                ResultScanner scanner = TestScannerHeartbeatMessages.TABLE.getScanner(scan);
                int num = 0;
                while ((scanner.next()) != null) {
                    num++;
                } 
                Assert.assertEquals(1, num);
                scanner.close();
                scan = new Scan();
                scan.setMaxResultSize(Long.MAX_VALUE);
                scan.setCaching(Integer.MAX_VALUE);
                scan.setFilter(new TestScannerHeartbeatMessages.SparseCellFilter());
                scan.setAllowPartialResults(true);
                scanner = TestScannerHeartbeatMessages.TABLE.getScanner(scan);
                num = 0;
                while ((scanner.next()) != null) {
                    num++;
                } 
                Assert.assertEquals(((TestScannerHeartbeatMessages.NUM_FAMILIES) * (TestScannerHeartbeatMessages.NUM_QUALIFIERS)), num);
                scanner.close();
                return null;
            }
        });
    }

    /**
     * Test the case that there is a filter which filters most of rows
     */
    @Test
    public void testHeartbeatWithSparseRowFilter() throws Exception {
        testImportanceOfHeartbeats(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Scan scan = new Scan();
                scan.setMaxResultSize(Long.MAX_VALUE);
                scan.setCaching(Integer.MAX_VALUE);
                scan.setFilter(new TestScannerHeartbeatMessages.SparseRowFilter());
                ResultScanner scanner = TestScannerHeartbeatMessages.TABLE.getScanner(scan);
                int num = 0;
                while ((scanner.next()) != null) {
                    num++;
                } 
                Assert.assertEquals(1, num);
                scanner.close();
                return null;
            }
        });
    }

    /**
     * Custom HRegionServer instance that instantiates {@link HeartbeatRPCServices} in place of
     * {@link RSRpcServices} to allow us to toggle support for heartbeat messages
     */
    private static class HeartbeatHRegionServer extends HRegionServer {
        public HeartbeatHRegionServer(Configuration conf) throws IOException, InterruptedException {
            super(conf);
        }

        @Override
        protected RSRpcServices createRpcServices() throws IOException {
            return new TestScannerHeartbeatMessages.HeartbeatRPCServices(this);
        }
    }

    /**
     * Custom RSRpcServices instance that allows heartbeat support to be toggled
     */
    private static class HeartbeatRPCServices extends RSRpcServices {
        private static volatile boolean heartbeatsEnabled = true;

        public HeartbeatRPCServices(HRegionServer rs) throws IOException {
            super(rs);
        }

        @Override
        public ScanResponse scan(RpcController controller, ScanRequest request) throws ServiceException {
            ScanRequest.Builder builder = ScanRequest.newBuilder(request);
            builder.setClientHandlesHeartbeats(TestScannerHeartbeatMessages.HeartbeatRPCServices.heartbeatsEnabled);
            return super.scan(controller, builder.build());
        }
    }

    /**
     * Custom HRegion class that instantiates {@link RegionScanner}s with configurable sleep times
     * between fetches of row Results and/or column family cells. Useful for emulating an instance
     * where the server is taking a long time to process a client's scan request
     */
    private static class HeartbeatHRegion extends HRegion {
        // Row sleeps occur AFTER each row worth of cells is retrieved.
        private static volatile int rowSleepTime = TestScannerHeartbeatMessages.DEFAULT_ROW_SLEEP_TIME;

        private static volatile boolean sleepBetweenRows = false;

        // The sleep for column families can be initiated before or after we fetch the cells for the
        // column family. If the sleep occurs BEFORE then the time limits will be reached inside
        // StoreScanner while we are fetching individual cells. If the sleep occurs AFTER then the time
        // limit will be reached inside RegionScanner after all the cells for a column family have been
        // retrieved.
        private static volatile boolean sleepBeforeColumnFamily = false;

        private static volatile int columnFamilySleepTime = TestScannerHeartbeatMessages.DEFAULT_CF_SLEEP_TIME;

        private static volatile boolean sleepBetweenColumnFamilies = false;

        public HeartbeatHRegion(Path tableDir, WAL wal, FileSystem fs, Configuration confParam, RegionInfo regionInfo, TableDescriptor htd, RegionServerServices rsServices) {
            super(tableDir, wal, fs, confParam, regionInfo, htd, rsServices);
        }

        public HeartbeatHRegion(HRegionFileSystem fs, WAL wal, Configuration confParam, TableDescriptor htd, RegionServerServices rsServices) {
            super(fs, wal, confParam, htd, rsServices);
        }

        private static void columnFamilySleep() {
            if (TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBetweenColumnFamilies) {
                Threads.sleepWithoutInterrupt(TestScannerHeartbeatMessages.HeartbeatHRegion.columnFamilySleepTime);
            }
        }

        private static void rowSleep() {
            if (TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBetweenRows) {
                Threads.sleepWithoutInterrupt(TestScannerHeartbeatMessages.HeartbeatHRegion.rowSleepTime);
            }
        }

        // Instantiate the custom heartbeat region scanners
        @Override
        protected RegionScannerImpl instantiateRegionScanner(Scan scan, List<KeyValueScanner> additionalScanners, long nonceGroup, long nonce) throws IOException {
            if (scan.isReversed()) {
                if ((scan.getFilter()) != null) {
                    scan.getFilter().setReversed(true);
                }
                return new TestScannerHeartbeatMessages.HeartbeatReversedRegionScanner(scan, additionalScanners, this);
            }
            return new TestScannerHeartbeatMessages.HeartbeatRegionScanner(scan, additionalScanners, this);
        }
    }

    /**
     * Custom ReversedRegionScanner that can be configured to sleep between retrievals of row Results
     * and/or column family cells
     */
    private static class HeartbeatReversedRegionScanner extends ReversedRegionScannerImpl {
        HeartbeatReversedRegionScanner(Scan scan, List<KeyValueScanner> additionalScanners, HRegion region) throws IOException {
            super(scan, additionalScanners, region);
        }

        @Override
        public boolean nextRaw(List<Cell> outResults, ScannerContext context) throws IOException {
            boolean moreRows = super.nextRaw(outResults, context);
            TestScannerHeartbeatMessages.HeartbeatHRegion.rowSleep();
            return moreRows;
        }

        @Override
        protected void initializeKVHeap(List<KeyValueScanner> scanners, List<KeyValueScanner> joinedScanners, HRegion region) throws IOException {
            this.storeHeap = new TestScannerHeartbeatMessages.HeartbeatReversedKVHeap(scanners, ((CellComparatorImpl) (region.getCellComparator())));
            if (!(joinedScanners.isEmpty())) {
                this.joinedHeap = new TestScannerHeartbeatMessages.HeartbeatReversedKVHeap(joinedScanners, ((CellComparatorImpl) (region.getCellComparator())));
            }
        }
    }

    /**
     * Custom RegionScanner that can be configured to sleep between retrievals of row Results and/or
     * column family cells
     */
    private static class HeartbeatRegionScanner extends RegionScannerImpl {
        HeartbeatRegionScanner(Scan scan, List<KeyValueScanner> additionalScanners, HRegion region) throws IOException {
            region.super(scan, additionalScanners, region);
        }

        @Override
        public boolean nextRaw(List<Cell> outResults, ScannerContext context) throws IOException {
            boolean moreRows = super.nextRaw(outResults, context);
            TestScannerHeartbeatMessages.HeartbeatHRegion.rowSleep();
            return moreRows;
        }

        @Override
        protected void initializeKVHeap(List<KeyValueScanner> scanners, List<KeyValueScanner> joinedScanners, HRegion region) throws IOException {
            this.storeHeap = new TestScannerHeartbeatMessages.HeartbeatKVHeap(scanners, region.getCellComparator());
            if (!(joinedScanners.isEmpty())) {
                this.joinedHeap = new TestScannerHeartbeatMessages.HeartbeatKVHeap(joinedScanners, region.getCellComparator());
            }
        }
    }

    /**
     * Custom KV Heap that can be configured to sleep/wait in between retrievals of column family
     * cells. Useful for testing
     */
    private static final class HeartbeatKVHeap extends KeyValueHeap {
        public HeartbeatKVHeap(List<? extends KeyValueScanner> scanners, CellComparator comparator) throws IOException {
            super(scanners, comparator);
        }

        HeartbeatKVHeap(List<? extends KeyValueScanner> scanners, KVScannerComparator comparator) throws IOException {
            super(scanners, comparator);
        }

        @Override
        public boolean next(List<Cell> result, ScannerContext context) throws IOException {
            if (TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBeforeColumnFamily)
                TestScannerHeartbeatMessages.HeartbeatHRegion.columnFamilySleep();

            boolean moreRows = super.next(result, context);
            if (!(TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBeforeColumnFamily))
                TestScannerHeartbeatMessages.HeartbeatHRegion.columnFamilySleep();

            return moreRows;
        }
    }

    /**
     * Custom reversed KV Heap that can be configured to sleep in between retrievals of column family
     * cells.
     */
    private static final class HeartbeatReversedKVHeap extends ReversedKeyValueHeap {
        public HeartbeatReversedKVHeap(List<? extends KeyValueScanner> scanners, CellComparatorImpl comparator) throws IOException {
            super(scanners, comparator);
        }

        @Override
        public boolean next(List<Cell> result, ScannerContext context) throws IOException {
            if (TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBeforeColumnFamily)
                TestScannerHeartbeatMessages.HeartbeatHRegion.columnFamilySleep();

            boolean moreRows = super.next(result, context);
            if (!(TestScannerHeartbeatMessages.HeartbeatHRegion.sleepBeforeColumnFamily))
                TestScannerHeartbeatMessages.HeartbeatHRegion.columnFamilySleep();

            return moreRows;
        }
    }
}

