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


import AdminProtos.AdminService.BlockingInterface;
import WALEdit.BULK_LOAD;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.MultithreadedTestUtil;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ClientServiceCallable;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RpcRetryingCaller;
import org.apache.hadoop.hbase.client.RpcRetryingCallerFactory;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.SecureBulkLoadClient;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.regionserver.wal.TestWALActionsListener;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos.CompactRegionRequest;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKey;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests bulk loading of HFiles and shows the atomicity or lack of atomicity of
 * the region server's bullkLoad functionality.
 */
@RunWith(Parameterized.class)
@Category({ RegionServerTests.class, LargeTests.class })
public class TestHRegionServerBulkLoad {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHRegionServerBulkLoad.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHRegionServerBulkLoad.class);

    protected static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final Configuration conf = TestHRegionServerBulkLoad.UTIL.getConfiguration();

    protected static final byte[] QUAL = Bytes.toBytes("qual");

    protected static final int NUM_CFS = 10;

    private int sleepDuration;

    public static int BLOCKSIZE = 64 * 1024;

    public static Algorithm COMPRESSION = Algorithm.NONE;

    protected static final byte[][] families = new byte[TestHRegionServerBulkLoad.NUM_CFS][];

    static {
        for (int i = 0; i < (TestHRegionServerBulkLoad.NUM_CFS); i++) {
            TestHRegionServerBulkLoad.families[i] = Bytes.toBytes(TestHRegionServerBulkLoad.family(i));
        }
    }

    public TestHRegionServerBulkLoad(int duration) {
        this.sleepDuration = duration;
    }

    /**
     * Thread that does full scans of the table looking for any partially
     * completed rows.
     *
     * Each iteration of this loads 10 hdfs files, which occupies 5 file open file
     * handles. So every 10 iterations (500 file handles) it does a region
     * compaction to reduce the number of open file handles.
     */
    public static class AtomicHFileLoader extends MultithreadedTestUtil.RepeatingTestThread {
        final AtomicLong numBulkLoads = new AtomicLong();

        final AtomicLong numCompactions = new AtomicLong();

        private TableName tableName;

        public AtomicHFileLoader(TableName tableName, MultithreadedTestUtil.TestContext ctx, byte[][] targetFamilies) throws IOException {
            super(ctx);
            this.tableName = tableName;
        }

        @Override
        public void doAnAction() throws Exception {
            long iteration = numBulkLoads.getAndIncrement();
            Path dir = TestHRegionServerBulkLoad.UTIL.getDataTestDirOnTestFS(String.format("bulkLoad_%08d", iteration));
            // create HFiles for different column families
            FileSystem fs = TestHRegionServerBulkLoad.UTIL.getTestFileSystem();
            byte[] val = Bytes.toBytes(String.format("%010d", iteration));
            final List<Pair<byte[], String>> famPaths = new ArrayList<>(TestHRegionServerBulkLoad.NUM_CFS);
            for (int i = 0; i < (TestHRegionServerBulkLoad.NUM_CFS); i++) {
                Path hfile = new Path(dir, TestHRegionServerBulkLoad.family(i));
                byte[] fam = Bytes.toBytes(TestHRegionServerBulkLoad.family(i));
                TestHRegionServerBulkLoad.createHFile(fs, hfile, fam, TestHRegionServerBulkLoad.QUAL, val, 1000);
                famPaths.add(new Pair(fam, hfile.toString()));
            }
            // bulk load HFiles
            final ClusterConnection conn = ((ClusterConnection) (TestHRegionServerBulkLoad.UTIL.getConnection()));
            Table table = conn.getTable(tableName);
            final String bulkToken = new SecureBulkLoadClient(TestHRegionServerBulkLoad.UTIL.getConfiguration(), table).prepareBulkLoad(conn);
            ClientServiceCallable<Void> callable = new ClientServiceCallable<Void>(conn, tableName, Bytes.toBytes("aaa"), newController(), HConstants.PRIORITY_UNSET) {
                @Override
                public Void rpcCall() throws Exception {
                    TestHRegionServerBulkLoad.LOG.debug(((("Going to connect to server " + (getLocation())) + " for row ") + (Bytes.toStringBinary(getRow()))));
                    SecureBulkLoadClient secureClient = null;
                    byte[] regionName = getLocation().getRegionInfo().getRegionName();
                    try (Table table = conn.getTable(getTableName())) {
                        secureClient = new SecureBulkLoadClient(TestHRegionServerBulkLoad.UTIL.getConfiguration(), table);
                        secureClient.secureBulkLoadHFiles(getStub(), famPaths, regionName, true, null, bulkToken);
                    }
                    return null;
                }
            };
            RpcRetryingCallerFactory factory = new RpcRetryingCallerFactory(TestHRegionServerBulkLoad.conf);
            RpcRetryingCaller<Void> caller = factory.<Void>newCaller();
            caller.callWithRetries(callable, Integer.MAX_VALUE);
            // Periodically do compaction to reduce the number of open file handles.
            if (((numBulkLoads.get()) % 5) == 0) {
                // 5 * 50 = 250 open file handles!
                callable = new ClientServiceCallable<Void>(conn, tableName, Bytes.toBytes("aaa"), newController(), HConstants.PRIORITY_UNSET) {
                    @Override
                    protected Void rpcCall() throws Exception {
                        TestHRegionServerBulkLoad.LOG.debug(((("compacting " + (getLocation())) + " for row ") + (Bytes.toStringBinary(getRow()))));
                        AdminProtos.AdminService.BlockingInterface server = conn.getAdmin(getLocation().getServerName());
                        CompactRegionRequest request = RequestConverter.buildCompactRegionRequest(getLocation().getRegionInfo().getRegionName(), true, null);
                        server.compactRegion(null, request);
                        numCompactions.incrementAndGet();
                        return null;
                    }
                };
                caller.callWithRetries(callable, Integer.MAX_VALUE);
            }
        }
    }

    public static class MyObserver implements RegionCoprocessor , RegionObserver {
        static int sleepDuration;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            try {
                Thread.sleep(TestHRegionServerBulkLoad.MyObserver.sleepDuration);
            } catch (InterruptedException ie) {
                IOException ioe = new InterruptedIOException();
                ioe.initCause(ie);
                throw ioe;
            }
            return scanner;
        }
    }

    /**
     * Thread that does full scans of the table looking for any partially
     * completed rows.
     */
    public static class AtomicScanReader extends MultithreadedTestUtil.RepeatingTestThread {
        byte[][] targetFamilies;

        Table table;

        AtomicLong numScans = new AtomicLong();

        AtomicLong numRowsScanned = new AtomicLong();

        TableName TABLE_NAME;

        public AtomicScanReader(TableName TABLE_NAME, MultithreadedTestUtil.TestContext ctx, byte[][] targetFamilies) throws IOException {
            super(ctx);
            this.TABLE_NAME = TABLE_NAME;
            this.targetFamilies = targetFamilies;
            table = TestHRegionServerBulkLoad.UTIL.getConnection().getTable(TABLE_NAME);
        }

        @Override
        public void doAnAction() throws Exception {
            Scan s = new Scan();
            for (byte[] family : targetFamilies) {
                s.addFamily(family);
            }
            ResultScanner scanner = table.getScanner(s);
            for (Result res : scanner) {
                byte[] lastRow = null;
                byte[] lastFam = null;
                byte[] lastQual = null;
                byte[] gotValue = null;
                for (byte[] family : targetFamilies) {
                    byte[] qualifier = TestHRegionServerBulkLoad.QUAL;
                    byte[] thisValue = res.getValue(family, qualifier);
                    if (((gotValue != null) && (thisValue != null)) && (!(Bytes.equals(gotValue, thisValue)))) {
                        StringBuilder msg = new StringBuilder();
                        msg.append("Failed on scan ").append(numScans).append(" after scanning ").append(numRowsScanned).append(" rows!\n");
                        msg.append((((((((("Current  was " + (Bytes.toString(res.getRow()))) + "/") + (Bytes.toString(family))) + ":") + (Bytes.toString(qualifier))) + " = ") + (Bytes.toString(thisValue))) + "\n"));
                        msg.append(((((((("Previous  was " + (Bytes.toString(lastRow))) + "/") + (Bytes.toString(lastFam))) + ":") + (Bytes.toString(lastQual))) + " = ") + (Bytes.toString(gotValue))));
                        throw new RuntimeException(msg.toString());
                    }
                    lastFam = family;
                    lastQual = qualifier;
                    lastRow = res.getRow();
                    gotValue = thisValue;
                }
                numRowsScanned.getAndIncrement();
            }
            numScans.getAndIncrement();
        }
    }

    /**
     * Atomic bulk load.
     */
    @Test
    public void testAtomicBulkLoad() throws Exception {
        TableName TABLE_NAME = TableName.valueOf("atomicBulkLoad");
        int millisToRun = 30000;
        int numScanners = 50;
        // Set createWALDir to true and use default values for other options.
        TestHRegionServerBulkLoad.UTIL.startMiniCluster(StartMiniClusterOption.builder().createWALDir(true).build());
        try {
            WAL log = TestHRegionServerBulkLoad.UTIL.getHBaseCluster().getRegionServer(0).getWAL(null);
            TestHRegionServerBulkLoad.FindBulkHBaseListener listener = new TestHRegionServerBulkLoad.FindBulkHBaseListener();
            log.registerWALActionsListener(listener);
            runAtomicBulkloadTest(TABLE_NAME, millisToRun, numScanners);
            Assert.assertThat(listener.isFound(), Is.is(true));
        } finally {
            TestHRegionServerBulkLoad.UTIL.shutdownMiniCluster();
        }
    }

    static class FindBulkHBaseListener extends TestWALActionsListener.DummyWALActionsListener {
        private boolean found = false;

        @Override
        public void visitLogEntryBeforeWrite(WALKey logKey, WALEdit logEdit) {
            for (Cell cell : logEdit.getCells()) {
                KeyValue kv = KeyValueUtil.ensureKeyValue(cell);
                for (Map.Entry entry : kv.toStringMap().entrySet()) {
                    if (entry.getValue().equals(Bytes.toString(BULK_LOAD))) {
                        found = true;
                    }
                }
            }
        }

        public boolean isFound() {
            return found;
        }
    }
}

