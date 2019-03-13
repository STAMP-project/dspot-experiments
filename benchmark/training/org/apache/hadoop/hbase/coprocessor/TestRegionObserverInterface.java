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
package org.apache.hadoop.hbase.coprocessor;


import Durability.SKIP_WAL;
import JVMClusterUtil.RegionServerThread;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.Coprocessor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Append;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.RowMutations;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FilterAllFilter;
import org.apache.hadoop.hbase.regionserver.FlushLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.ScannerContext;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.StoreFile;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ CoprocessorTests.class, MediumTests.class })
public class TestRegionObserverInterface {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionObserverInterface.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionObserverInterface.class);

    public static final TableName TEST_TABLE = TableName.valueOf("TestTable");

    public static final byte[] A = Bytes.toBytes("a");

    public static final byte[] B = Bytes.toBytes("b");

    public static final byte[] C = Bytes.toBytes("c");

    public static final byte[] ROW = Bytes.toBytes("testrow");

    private static HBaseTestingUtility util = new HBaseTestingUtility();

    private static MiniHBaseCluster cluster = null;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testRegionObserver() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        // recreate table every time in order to reset the status of the
        // coprocessor.
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try {
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadDelete", "hadPostStartRegionOperation", "hadPostCloseRegionOperation", "hadPostBatchMutateIndispensably" }, tableName, new Boolean[]{ false, false, false, false, false, false, false, false });
            Put put = new Put(TestRegionObserverInterface.ROW);
            put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            put.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            put.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            table.put(put);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadPreBatchMutate", "hadPostBatchMutate", "hadDelete", "hadPostStartRegionOperation", "hadPostCloseRegionOperation", "hadPostBatchMutateIndispensably" }, TestRegionObserverInterface.TEST_TABLE, new Boolean[]{ false, false, true, true, true, true, false, true, true, true });
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "getCtPreOpen", "getCtPostOpen", "getCtPreClose", "getCtPostClose" }, tableName, new Integer[]{ 1, 1, 0, 0 });
            Get get = new Get(TestRegionObserverInterface.ROW);
            get.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            get.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            get.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            table.get(get);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadDelete", "hadPrePreparedDeleteTS" }, tableName, new Boolean[]{ true, true, true, true, false, false });
            Delete delete = new Delete(TestRegionObserverInterface.ROW);
            delete.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            delete.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            delete.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            table.delete(delete);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadPreBatchMutate", "hadPostBatchMutate", "hadDelete", "hadPrePreparedDeleteTS" }, tableName, new Boolean[]{ true, true, true, true, true, true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "getCtPreOpen", "getCtPostOpen", "getCtPreClose", "getCtPostClose" }, tableName, new Integer[]{ 1, 1, 1, 1 });
    }

    @Test
    public void testRowMutation() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try {
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadDeleted" }, tableName, new Boolean[]{ false, false, false, false, false });
            Put put = new Put(TestRegionObserverInterface.ROW);
            put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            put.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            put.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            Delete delete = new Delete(TestRegionObserverInterface.ROW);
            delete.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            delete.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            delete.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            RowMutations arm = new RowMutations(TestRegionObserverInterface.ROW);
            arm.add(put);
            arm.add(delete);
            table.mutateRow(arm);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadDeleted" }, tableName, new Boolean[]{ false, false, true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    @Test
    public void testIncrementHook() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try {
            Increment inc = new Increment(Bytes.toBytes(0));
            inc.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, 1);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreIncrement", "hadPostIncrement", "hadPreIncrementAfterRowLock" }, tableName, new Boolean[]{ false, false, false });
            table.increment(inc);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreIncrement", "hadPostIncrement", "hadPreIncrementAfterRowLock" }, tableName, new Boolean[]{ true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    @Test
    public void testCheckAndPutHooks() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        try (Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C })) {
            Put p = new Put(Bytes.toBytes(0));
            p.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            table.put(p);
            p = new Put(Bytes.toBytes(0));
            p.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreCheckAndPut", "hadPreCheckAndPutAfterRowLock", "hadPostCheckAndPut" }, tableName, new Boolean[]{ false, false, false });
            table.checkAndMutate(Bytes.toBytes(0), TestRegionObserverInterface.A).qualifier(TestRegionObserverInterface.A).ifEquals(TestRegionObserverInterface.A).thenPut(p);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreCheckAndPut", "hadPreCheckAndPutAfterRowLock", "hadPostCheckAndPut" }, tableName, new Boolean[]{ true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
        }
    }

    @Test
    public void testCheckAndDeleteHooks() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try {
            Put p = new Put(Bytes.toBytes(0));
            p.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            table.put(p);
            Delete d = new Delete(Bytes.toBytes(0));
            table.delete(d);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreCheckAndDelete", "hadPreCheckAndDeleteAfterRowLock", "hadPostCheckAndDelete" }, tableName, new Boolean[]{ false, false, false });
            table.checkAndMutate(Bytes.toBytes(0), TestRegionObserverInterface.A).qualifier(TestRegionObserverInterface.A).ifEquals(TestRegionObserverInterface.A).thenDelete(d);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreCheckAndDelete", "hadPreCheckAndDeleteAfterRowLock", "hadPostCheckAndDelete" }, tableName, new Boolean[]{ true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    @Test
    public void testAppendHook() throws IOException {
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try {
            Append app = new Append(Bytes.toBytes(0));
            app.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreAppend", "hadPostAppend", "hadPreAppendAfterRowLock" }, tableName, new Boolean[]{ false, false, false });
            table.append(app);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreAppend", "hadPostAppend", "hadPreAppendAfterRowLock" }, tableName, new Boolean[]{ true, true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    // HBase-3583
    @Test
    public void testHBase3583() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        TestRegionObserverInterface.util.waitUntilAllRegionsAssigned(tableName);
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "wasScannerNextCalled", "wasScannerCloseCalled" }, tableName, new Boolean[]{ false, false, false, false });
        Table table = TestRegionObserverInterface.util.getConnection().getTable(tableName);
        Put put = new Put(TestRegionObserverInterface.ROW);
        put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
        table.put(put);
        Get get = new Get(TestRegionObserverInterface.ROW);
        get.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A);
        table.get(get);
        // verify that scannerNext and scannerClose upcalls won't be invoked
        // when we perform get().
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "wasScannerNextCalled", "wasScannerCloseCalled" }, tableName, new Boolean[]{ true, true, false, false });
        Scan s = new Scan();
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            }
        } finally {
            scanner.close();
        }
        // now scanner hooks should be invoked.
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "wasScannerNextCalled", "wasScannerCloseCalled" }, tableName, new Boolean[]{ true, true });
        TestRegionObserverInterface.util.deleteTable(tableName);
        table.close();
    }

    @Test
    public void testHBASE14489() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A });
        Put put = new Put(TestRegionObserverInterface.ROW);
        put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
        table.put(put);
        Scan s = new Scan();
        s.setFilter(new FilterAllFilter());
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            }
        } finally {
            scanner.close();
        }
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "wasScannerFilterRowCalled" }, tableName, new Boolean[]{ true });
        TestRegionObserverInterface.util.deleteTable(tableName);
        table.close();
    }

    // HBase-3758
    @Test
    public void testHBase3758() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadDeleted", "wasScannerOpenCalled" }, tableName, new Boolean[]{ false, false });
        Table table = TestRegionObserverInterface.util.getConnection().getTable(tableName);
        Put put = new Put(TestRegionObserverInterface.ROW);
        put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
        table.put(put);
        Delete delete = new Delete(TestRegionObserverInterface.ROW);
        table.delete(delete);
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadDeleted", "wasScannerOpenCalled" }, tableName, new Boolean[]{ true, false });
        Scan s = new Scan();
        ResultScanner scanner = table.getScanner(s);
        try {
            for (Result rr = scanner.next(); rr != null; rr = scanner.next()) {
            }
        } finally {
            scanner.close();
        }
        // now scanner hooks should be invoked.
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "wasScannerOpenCalled" }, tableName, new Boolean[]{ true });
        TestRegionObserverInterface.util.deleteTable(tableName);
        table.close();
    }

    /* Overrides compaction to only output rows with keys that are even numbers */
    public static class EvenOnlyCompactor implements RegionCoprocessor , RegionObserver {
        long lastCompaction;

        long lastFlush;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) {
            return new InternalScanner() {
                @Override
                public boolean next(List<Cell> results, ScannerContext scannerContext) throws IOException {
                    List<Cell> internalResults = new ArrayList<>();
                    boolean hasMore;
                    do {
                        hasMore = scanner.next(internalResults, scannerContext);
                        if (!(internalResults.isEmpty())) {
                            long row = Bytes.toLong(CellUtil.cloneValue(internalResults.get(0)));
                            if ((row % 2) == 0) {
                                // return this row
                                break;
                            }
                            // clear and continue
                            internalResults.clear();
                        }
                    } while (hasMore );
                    if (!(internalResults.isEmpty())) {
                        results.addAll(internalResults);
                    }
                    return hasMore;
                }

                @Override
                public void close() throws IOException {
                    scanner.close();
                }
            };
        }

        @Override
        public void postCompact(ObserverContext<RegionCoprocessorEnvironment> e, Store store, StoreFile resultFile, CompactionLifeCycleTracker tracker, CompactionRequest request) {
            lastCompaction = EnvironmentEdgeManager.currentTime();
        }

        @Override
        public void postFlush(ObserverContext<RegionCoprocessorEnvironment> e, FlushLifeCycleTracker tracker) {
            lastFlush = EnvironmentEdgeManager.currentTime();
        }
    }

    /**
     * Tests overriding compaction handling via coprocessor hooks
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCompactionOverride() throws Exception {
        final TableName compactTable = TableName.valueOf(name.getMethodName());
        Admin admin = TestRegionObserverInterface.util.getAdmin();
        if (admin.tableExists(compactTable)) {
            admin.disableTable(compactTable);
            admin.deleteTable(compactTable);
        }
        HTableDescriptor htd = new HTableDescriptor(compactTable);
        htd.addFamily(new HColumnDescriptor(TestRegionObserverInterface.A));
        htd.addCoprocessor(TestRegionObserverInterface.EvenOnlyCompactor.class.getName());
        admin.createTable(htd);
        Table table = TestRegionObserverInterface.util.getConnection().getTable(compactTable);
        for (long i = 1; i <= 10; i++) {
            byte[] iBytes = Bytes.toBytes(i);
            Put put = new Put(iBytes);
            put.setDurability(SKIP_WAL);
            put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, iBytes);
            table.put(put);
        }
        HRegion firstRegion = TestRegionObserverInterface.cluster.getRegions(compactTable).get(0);
        Coprocessor cp = firstRegion.getCoprocessorHost().findCoprocessor(TestRegionObserverInterface.EvenOnlyCompactor.class);
        Assert.assertNotNull("EvenOnlyCompactor coprocessor should be loaded", cp);
        TestRegionObserverInterface.EvenOnlyCompactor compactor = ((TestRegionObserverInterface.EvenOnlyCompactor) (cp));
        // force a compaction
        long ts = System.currentTimeMillis();
        admin.flush(compactTable);
        // wait for flush
        for (int i = 0; i < 10; i++) {
            if ((compactor.lastFlush) >= ts) {
                break;
            }
            Thread.sleep(1000);
        }
        Assert.assertTrue("Flush didn't complete", ((compactor.lastFlush) >= ts));
        TestRegionObserverInterface.LOG.debug("Flush complete");
        ts = compactor.lastFlush;
        admin.majorCompact(compactTable);
        // wait for compaction
        for (int i = 0; i < 30; i++) {
            if ((compactor.lastCompaction) >= ts) {
                break;
            }
            Thread.sleep(1000);
        }
        TestRegionObserverInterface.LOG.debug(("Last compaction was at " + (compactor.lastCompaction)));
        Assert.assertTrue("Compaction didn't complete", ((compactor.lastCompaction) >= ts));
        // only even rows should remain
        ResultScanner scanner = table.getScanner(new Scan());
        try {
            for (long i = 2; i <= 10; i += 2) {
                Result r = scanner.next();
                Assert.assertNotNull(r);
                Assert.assertFalse(r.isEmpty());
                byte[] iBytes = Bytes.toBytes(i);
                Assert.assertArrayEquals(("Row should be " + i), r.getRow(), iBytes);
                Assert.assertArrayEquals(("Value should be " + i), r.getValue(TestRegionObserverInterface.A, TestRegionObserverInterface.A), iBytes);
            }
        } finally {
            scanner.close();
        }
        table.close();
    }

    @Test
    public void bulkLoadHFileTest() throws Exception {
        final String testName = ((TestRegionObserverInterface.class.getName()) + ".") + (name.getMethodName());
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Configuration conf = TestRegionObserverInterface.util.getConfiguration();
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try (RegionLocator locator = TestRegionObserverInterface.util.getConnection().getRegionLocator(tableName)) {
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreBulkLoadHFile", "hadPostBulkLoadHFile" }, tableName, new Boolean[]{ false, false });
            FileSystem fs = TestRegionObserverInterface.util.getTestFileSystem();
            final Path dir = TestRegionObserverInterface.util.getDataTestDirOnTestFS(testName).makeQualified(fs);
            Path familyDir = new Path(dir, Bytes.toString(TestRegionObserverInterface.A));
            TestRegionObserverInterface.createHFile(TestRegionObserverInterface.util.getConfiguration(), fs, new Path(familyDir, Bytes.toString(TestRegionObserverInterface.A)), TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            // Bulk load
            new org.apache.hadoop.hbase.tool.LoadIncrementalHFiles(conf).doBulkLoad(dir, TestRegionObserverInterface.util.getAdmin(), table, locator);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreBulkLoadHFile", "hadPostBulkLoadHFile" }, tableName, new Boolean[]{ true, true });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    @Test
    public void testRecovery() throws Exception {
        TestRegionObserverInterface.LOG.info((((TestRegionObserverInterface.class.getName()) + ".") + (name.getMethodName())));
        final TableName tableName = TableName.valueOf((((TestRegionObserverInterface.TEST_TABLE.getNameAsString()) + ".") + (name.getMethodName())));
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try (RegionLocator locator = TestRegionObserverInterface.util.getConnection().getRegionLocator(tableName)) {
            JVMClusterUtil.RegionServerThread rs1 = TestRegionObserverInterface.cluster.startRegionServer();
            ServerName sn2 = rs1.getRegionServer().getServerName();
            String regEN = locator.getAllRegionLocations().get(0).getRegionInfo().getEncodedName();
            TestRegionObserverInterface.util.getAdmin().move(Bytes.toBytes(regEN), Bytes.toBytes(sn2.getServerName()));
            while (!(sn2.equals(locator.getAllRegionLocations().get(0).getServerName()))) {
                Thread.sleep(100);
            } 
            Put put = new Put(TestRegionObserverInterface.ROW);
            put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            put.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            put.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            table.put(put);
            // put two times
            table.put(put);
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "hadPreGet", "hadPostGet", "hadPrePut", "hadPostPut", "hadPreBatchMutate", "hadPostBatchMutate", "hadDelete" }, tableName, new Boolean[]{ false, false, true, true, true, true, false });
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "getCtPreReplayWALs", "getCtPostReplayWALs", "getCtPreWALRestore", "getCtPostWALRestore", "getCtPrePut", "getCtPostPut" }, tableName, new Integer[]{ 0, 0, 0, 0, 2, 2 });
            TestRegionObserverInterface.cluster.killRegionServer(rs1.getRegionServer().getServerName());
            Threads.sleep(1000);// Let the kill soak in.

            TestRegionObserverInterface.util.waitUntilAllRegionsAssigned(tableName);
            TestRegionObserverInterface.LOG.info("All regions assigned");
            verifyMethodResult(SimpleRegionObserver.class, new String[]{ "getCtPreReplayWALs", "getCtPostReplayWALs", "getCtPreWALRestore", "getCtPostWALRestore", "getCtPrePut", "getCtPostPut" }, tableName, new Integer[]{ 1, 1, 2, 2, 0, 0 });
        } finally {
            TestRegionObserverInterface.util.deleteTable(tableName);
            table.close();
        }
    }

    @Test
    public void testPreWALRestoreSkip() throws Exception {
        TestRegionObserverInterface.LOG.info((((TestRegionObserverInterface.class.getName()) + ".") + (name.getMethodName())));
        TableName tableName = TableName.valueOf(SimpleRegionObserver.TABLE_SKIPPED);
        Table table = TestRegionObserverInterface.util.createTable(tableName, new byte[][]{ TestRegionObserverInterface.A, TestRegionObserverInterface.B, TestRegionObserverInterface.C });
        try (RegionLocator locator = TestRegionObserverInterface.util.getConnection().getRegionLocator(tableName)) {
            JVMClusterUtil.RegionServerThread rs1 = TestRegionObserverInterface.cluster.startRegionServer();
            ServerName sn2 = rs1.getRegionServer().getServerName();
            String regEN = locator.getAllRegionLocations().get(0).getRegionInfo().getEncodedName();
            TestRegionObserverInterface.util.getAdmin().move(Bytes.toBytes(regEN), Bytes.toBytes(sn2.getServerName()));
            while (!(sn2.equals(locator.getAllRegionLocations().get(0).getServerName()))) {
                Thread.sleep(100);
            } 
            Put put = new Put(TestRegionObserverInterface.ROW);
            put.addColumn(TestRegionObserverInterface.A, TestRegionObserverInterface.A, TestRegionObserverInterface.A);
            put.addColumn(TestRegionObserverInterface.B, TestRegionObserverInterface.B, TestRegionObserverInterface.B);
            put.addColumn(TestRegionObserverInterface.C, TestRegionObserverInterface.C, TestRegionObserverInterface.C);
            table.put(put);
            TestRegionObserverInterface.cluster.killRegionServer(rs1.getRegionServer().getServerName());
            Threads.sleep(20000);// just to be sure that the kill has fully started.

            TestRegionObserverInterface.util.waitUntilAllRegionsAssigned(tableName);
        }
        verifyMethodResult(SimpleRegionObserver.class, new String[]{ "getCtPreWALRestore", "getCtPostWALRestore" }, tableName, new Integer[]{ 0, 0 });
        TestRegionObserverInterface.util.deleteTable(tableName);
        table.close();
    }
}

