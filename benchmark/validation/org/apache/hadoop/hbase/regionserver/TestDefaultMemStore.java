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


import CellComparatorImpl.COMPARATOR;
import HRegion.MEMSTORE_PERIODIC_FLUSH_INTERVAL;
import KeyValue.LOWESTKEY;
import KeyValue.Type;
import KeyValue.Type.Delete;
import KeyValue.Type.DeleteColumn;
import KeyValue.Type.DeleteFamily;
import MultiVersionConcurrencyControl.WriteEntry;
import RegionInfoBuilder.FIRST_META_REGIONINFO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.FSTableDescriptors;
import org.apache.hadoop.hbase.wal.WALFactory;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HRegion.SYSTEM_CACHE_FLUSH_INTERVAL;


/**
 * memstore test case
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestDefaultMemStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestDefaultMemStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestDefaultMemStore.class);

    @Rule
    public TestName name = new TestName();

    protected AbstractMemStore memstore;

    protected static final int ROW_COUNT = 10;

    protected static final int QUALIFIER_COUNT = TestDefaultMemStore.ROW_COUNT;

    protected static final byte[] FAMILY = Bytes.toBytes("column");

    protected MultiVersionConcurrencyControl mvcc;

    protected AtomicLong startSeqNum = new AtomicLong(0);

    protected ChunkCreator chunkCreator;

    @Test
    public void testPutSameKey() {
        byte[] bytes = Bytes.toBytes(getName());
        KeyValue kv = new KeyValue(bytes, bytes, bytes, bytes);
        this.memstore.add(kv, null);
        byte[] other = Bytes.toBytes("somethingelse");
        KeyValue samekey = new KeyValue(bytes, bytes, bytes, other);
        this.memstore.add(samekey, null);
        Cell found = this.memstore.getActive().first();
        Assert.assertEquals(1, this.memstore.getActive().getCellsCount());
        Assert.assertTrue(Bytes.toString(found.getValueArray()), CellUtil.matchingValue(samekey, found));
    }

    @Test
    public void testPutSameCell() {
        byte[] bytes = Bytes.toBytes(getName());
        KeyValue kv = new KeyValue(bytes, bytes, bytes, bytes);
        MemStoreSizing sizeChangeForFirstCell = new NonThreadSafeMemStoreSizing();
        this.memstore.add(kv, sizeChangeForFirstCell);
        MemStoreSizing sizeChangeForSecondCell = new NonThreadSafeMemStoreSizing();
        this.memstore.add(kv, sizeChangeForSecondCell);
        // make sure memstore size increase won't double-count MSLAB chunk size
        Assert.assertEquals(Segment.getCellLength(kv), sizeChangeForFirstCell.getMemStoreSize().getDataSize());
        Segment segment = this.memstore.getActive();
        MemStoreLAB msLab = segment.getMemStoreLAB();
        if (msLab != null) {
            // make sure memstore size increased even when writing the same cell, if using MSLAB
            Assert.assertEquals(Segment.getCellLength(kv), sizeChangeForSecondCell.getMemStoreSize().getDataSize());
            // make sure chunk size increased even when writing the same cell, if using MSLAB
            if (msLab instanceof MemStoreLABImpl) {
                // since we add the chunkID at the 0th offset of the chunk and the
                // chunkid is an int we need to account for those 4 bytes
                Assert.assertEquals(((2 * (Segment.getCellLength(kv))) + (Bytes.SIZEOF_INT)), getCurrentChunk().getNextFreeOffset());
            }
        } else {
            // make sure no memstore size change w/o MSLAB
            Assert.assertEquals(0, sizeChangeForSecondCell.getMemStoreSize().getDataSize());
            Assert.assertEquals(0, sizeChangeForSecondCell.getMemStoreSize().getHeapSize());
        }
    }

    /**
     * Test memstore snapshot happening while scanning.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testScanAcrossSnapshot() throws IOException {
        int rowCount = addRows(this.memstore);
        List<KeyValueScanner> memstorescanners = this.memstore.getScanners(0);
        Scan scan = new Scan();
        List<Cell> result = new ArrayList<>();
        Configuration conf = HBaseConfiguration.create();
        ScanInfo scanInfo = new ScanInfo(conf, null, 0, 1, HConstants.LATEST_TIMESTAMP, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, this.memstore.getComparator(), false);
        int count = 0;
        try (StoreScanner s = new StoreScanner(scan, scanInfo, null, memstorescanners)) {
            while (s.next(result)) {
                TestDefaultMemStore.LOG.info(Objects.toString(result));
                count++;
                // Row count is same as column count.
                Assert.assertEquals(rowCount, result.size());
                result.clear();
            } 
        }
        Assert.assertEquals(rowCount, count);
        for (KeyValueScanner scanner : memstorescanners) {
            scanner.close();
        }
        memstorescanners = this.memstore.getScanners(mvcc.getReadPoint());
        // Now assert can count same number even if a snapshot mid-scan.
        count = 0;
        try (StoreScanner s = new StoreScanner(scan, scanInfo, null, memstorescanners)) {
            while (s.next(result)) {
                TestDefaultMemStore.LOG.info(Objects.toString(result));
                // Assert the stuff is coming out in right order.
                Assert.assertTrue(CellUtil.matchingRows(result.get(0), Bytes.toBytes(count)));
                count++;
                // Row count is same as column count.
                Assert.assertEquals(rowCount, result.size());
                if (count == 2) {
                    this.memstore.snapshot();
                    TestDefaultMemStore.LOG.info("Snapshotted");
                }
                result.clear();
            } 
        }
        Assert.assertEquals(rowCount, count);
        for (KeyValueScanner scanner : memstorescanners) {
            scanner.close();
        }
        memstorescanners = this.memstore.getScanners(mvcc.getReadPoint());
        // Assert that new values are seen in kvset as we scan.
        long ts = System.currentTimeMillis();
        count = 0;
        int snapshotIndex = 5;
        try (StoreScanner s = new StoreScanner(scan, scanInfo, null, memstorescanners)) {
            while (s.next(result)) {
                TestDefaultMemStore.LOG.info(Objects.toString(result));
                // Assert the stuff is coming out in right order.
                Assert.assertTrue(CellUtil.matchingRows(result.get(0), Bytes.toBytes(count)));
                // Row count is same as column count.
                Assert.assertEquals(((("count=" + count) + ", result=") + result), rowCount, result.size());
                count++;
                if (count == snapshotIndex) {
                    MemStoreSnapshot snapshot = this.memstore.snapshot();
                    this.memstore.clearSnapshot(snapshot.getId());
                    // Added more rows into kvset. But the scanner wont see these rows.
                    addRows(this.memstore, ts);
                    TestDefaultMemStore.LOG.info("Snapshotted, cleared it and then added values (which wont be seen)");
                }
                result.clear();
            } 
        }
        Assert.assertEquals(rowCount, count);
    }

    /**
     * A simple test which verifies the 3 possible states when scanning across snapshot.
     *
     * @throws IOException
     * 		
     * @throws CloneNotSupportedException
     * 		
     */
    @Test
    public void testScanAcrossSnapshot2() throws IOException, CloneNotSupportedException {
        // we are going to the scanning across snapshot with two kvs
        // kv1 should always be returned before kv2
        final byte[] one = Bytes.toBytes(1);
        final byte[] two = Bytes.toBytes(2);
        final byte[] f = Bytes.toBytes("f");
        final byte[] q = Bytes.toBytes("q");
        final byte[] v = Bytes.toBytes(3);
        final KeyValue kv1 = new KeyValue(one, f, q, v);
        final KeyValue kv2 = new KeyValue(two, f, q, v);
        // use case 1: both kvs in kvset
        this.memstore.add(kv1.clone(), null);
        this.memstore.add(kv2.clone(), null);
        verifyScanAcrossSnapshot2(kv1, kv2);
        // use case 2: both kvs in snapshot
        this.memstore.snapshot();
        verifyScanAcrossSnapshot2(kv1, kv2);
        // use case 3: first in snapshot second in kvset
        this.memstore = new DefaultMemStore();
        this.memstore.add(kv1.clone(), null);
        this.memstore.snapshot();
        this.memstore.add(kv2.clone(), null);
        verifyScanAcrossSnapshot2(kv1, kv2);
    }

    @Test
    public void testMemstoreConcurrentControl() throws IOException {
        final byte[] row = Bytes.toBytes(1);
        final byte[] f = Bytes.toBytes("family");
        final byte[] q1 = Bytes.toBytes("q1");
        final byte[] q2 = Bytes.toBytes("q2");
        final byte[] v = Bytes.toBytes("value");
        MultiVersionConcurrencyControl.WriteEntry w = mvcc.begin();
        KeyValue kv1 = new KeyValue(row, f, q1, v);
        kv1.setSequenceId(w.getWriteNumber());
        memstore.add(kv1, null);
        KeyValueScanner s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{  });
        mvcc.completeAndWait(w);
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv1 });
        w = mvcc.begin();
        KeyValue kv2 = new KeyValue(row, f, q2, v);
        kv2.setSequenceId(w.getWriteNumber());
        memstore.add(kv2, null);
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv1 });
        mvcc.completeAndWait(w);
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv1, kv2 });
    }

    /**
     * Regression test for HBASE-2616, HBASE-2670.
     * When we insert a higher-memstoreTS version of a cell but with
     * the same timestamp, we still need to provide consistent reads
     * for the same scanner.
     */
    @Test
    public void testMemstoreEditsVisibilityWithSameKey() throws IOException {
        final byte[] row = Bytes.toBytes(1);
        final byte[] f = Bytes.toBytes("family");
        final byte[] q1 = Bytes.toBytes("q1");
        final byte[] q2 = Bytes.toBytes("q2");
        final byte[] v1 = Bytes.toBytes("value1");
        final byte[] v2 = Bytes.toBytes("value2");
        // INSERT 1: Write both columns val1
        MultiVersionConcurrencyControl.WriteEntry w = mvcc.begin();
        KeyValue kv11 = new KeyValue(row, f, q1, v1);
        kv11.setSequenceId(w.getWriteNumber());
        memstore.add(kv11, null);
        KeyValue kv12 = new KeyValue(row, f, q2, v1);
        kv12.setSequenceId(w.getWriteNumber());
        memstore.add(kv12, null);
        mvcc.completeAndWait(w);
        // BEFORE STARTING INSERT 2, SEE FIRST KVS
        KeyValueScanner s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv11, kv12 });
        // START INSERT 2: Write both columns val2
        w = mvcc.begin();
        KeyValue kv21 = new KeyValue(row, f, q1, v2);
        kv21.setSequenceId(w.getWriteNumber());
        memstore.add(kv21, null);
        KeyValue kv22 = new KeyValue(row, f, q2, v2);
        kv22.setSequenceId(w.getWriteNumber());
        memstore.add(kv22, null);
        // BEFORE COMPLETING INSERT 2, SEE FIRST KVS
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv11, kv12 });
        // COMPLETE INSERT 2
        mvcc.completeAndWait(w);
        // NOW SHOULD SEE NEW KVS IN ADDITION TO OLD KVS.
        // See HBASE-1485 for discussion about what we should do with
        // the duplicate-TS inserts
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv21, kv11, kv22, kv12 });
    }

    /**
     * When we insert a higher-memstoreTS deletion of a cell but with
     * the same timestamp, we still need to provide consistent reads
     * for the same scanner.
     */
    @Test
    public void testMemstoreDeletesVisibilityWithSameKey() throws IOException {
        final byte[] row = Bytes.toBytes(1);
        final byte[] f = Bytes.toBytes("family");
        final byte[] q1 = Bytes.toBytes("q1");
        final byte[] q2 = Bytes.toBytes("q2");
        final byte[] v1 = Bytes.toBytes("value1");
        // INSERT 1: Write both columns val1
        MultiVersionConcurrencyControl.WriteEntry w = mvcc.begin();
        KeyValue kv11 = new KeyValue(row, f, q1, v1);
        kv11.setSequenceId(w.getWriteNumber());
        memstore.add(kv11, null);
        KeyValue kv12 = new KeyValue(row, f, q2, v1);
        kv12.setSequenceId(w.getWriteNumber());
        memstore.add(kv12, null);
        mvcc.completeAndWait(w);
        // BEFORE STARTING INSERT 2, SEE FIRST KVS
        KeyValueScanner s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv11, kv12 });
        // START DELETE: Insert delete for one of the columns
        w = mvcc.begin();
        KeyValue kvDel = new KeyValue(row, f, q2, kv11.getTimestamp(), Type.DeleteColumn);
        kvDel.setSequenceId(w.getWriteNumber());
        memstore.add(kvDel, null);
        // BEFORE COMPLETING DELETE, SEE FIRST KVS
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv11, kv12 });
        // COMPLETE DELETE
        mvcc.completeAndWait(w);
        // NOW WE SHOULD SEE DELETE
        s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
        assertScannerResults(s, new KeyValue[]{ kv11, kvDel, kv12 });
    }

    private static class ReadOwnWritesTester extends Thread {
        static final int NUM_TRIES = 1000;

        final byte[] row;

        final byte[] f = Bytes.toBytes("family");

        final byte[] q1 = Bytes.toBytes("q1");

        final MultiVersionConcurrencyControl mvcc;

        final MemStore memstore;

        AtomicReference<Throwable> caughtException;

        public ReadOwnWritesTester(int id, MemStore memstore, MultiVersionConcurrencyControl mvcc, AtomicReference<Throwable> caughtException) {
            this.mvcc = mvcc;
            this.memstore = memstore;
            this.caughtException = caughtException;
            row = Bytes.toBytes(id);
        }

        @Override
        public void run() {
            try {
                internalRun();
            } catch (Throwable t) {
                caughtException.compareAndSet(null, t);
            }
        }

        private void internalRun() throws IOException {
            for (long i = 0; (i < (TestDefaultMemStore.ReadOwnWritesTester.NUM_TRIES)) && ((caughtException.get()) == null); i++) {
                MultiVersionConcurrencyControl.WriteEntry w = mvcc.begin();
                // Insert the sequence value (i)
                byte[] v = Bytes.toBytes(i);
                KeyValue kv = new KeyValue(row, f, q1, i, v);
                kv.setSequenceId(w.getWriteNumber());
                memstore.add(kv, null);
                mvcc.completeAndWait(w);
                // Assert that we can read back
                KeyValueScanner s = this.memstore.getScanners(mvcc.getReadPoint()).get(0);
                s.seek(kv);
                Cell ret = s.next();
                Assert.assertNotNull("Didnt find own write at all", ret);
                Assert.assertEquals("Didnt read own writes", kv.getTimestamp(), ret.getTimestamp());
            }
        }
    }

    @Test
    public void testReadOwnWritesUnderConcurrency() throws Throwable {
        int NUM_THREADS = 8;
        TestDefaultMemStore.ReadOwnWritesTester[] threads = new TestDefaultMemStore.ReadOwnWritesTester[NUM_THREADS];
        AtomicReference<Throwable> caught = new AtomicReference<>();
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new TestDefaultMemStore.ReadOwnWritesTester(i, memstore, mvcc, caught);
            threads[i].start();
        }
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i].join();
        }
        if ((caught.get()) != null) {
            throw caught.get();
        }
    }

    /**
     * Test memstore snapshots
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSnapshotting() throws IOException {
        final int snapshotCount = 5;
        // Add some rows, run a snapshot. Do it a few times.
        for (int i = 0; i < snapshotCount; i++) {
            addRows(this.memstore);
            runSnapshot(this.memstore);
            Assert.assertEquals("History not being cleared", 0, this.memstore.getSnapshot().getCellsCount());
        }
    }

    @Test
    public void testMultipleVersionsSimple() throws Exception {
        DefaultMemStore m = new DefaultMemStore(new Configuration(), CellComparatorImpl.COMPARATOR);
        byte[] row = Bytes.toBytes("testRow");
        byte[] family = Bytes.toBytes("testFamily");
        byte[] qf = Bytes.toBytes("testQualifier");
        long[] stamps = new long[]{ 1, 2, 3 };
        byte[][] values = new byte[][]{ Bytes.toBytes("value0"), Bytes.toBytes("value1"), Bytes.toBytes("value2") };
        KeyValue key0 = new KeyValue(row, family, qf, stamps[0], values[0]);
        KeyValue key1 = new KeyValue(row, family, qf, stamps[1], values[1]);
        KeyValue key2 = new KeyValue(row, family, qf, stamps[2], values[2]);
        m.add(key0, null);
        m.add(key1, null);
        m.add(key2, null);
        Assert.assertTrue(("Expected memstore to hold 3 values, actually has " + (m.getActive().getCellsCount())), ((m.getActive().getCellsCount()) == 3));
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Get tests
    // ////////////////////////////////////////////////////////////////////////////
    /**
     * Test getNextRow from memstore
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void testGetNextRow() throws Exception {
        addRows(this.memstore);
        // Add more versions to make it a little more interesting.
        Thread.sleep(1);
        addRows(this.memstore);
        Cell closestToEmpty = ((DefaultMemStore) (this.memstore)).getNextRow(LOWESTKEY);
        Assert.assertTrue(((COMPARATOR.compareRows(closestToEmpty, new KeyValue(Bytes.toBytes(0), System.currentTimeMillis()))) == 0));
        for (int i = 0; i < (TestDefaultMemStore.ROW_COUNT); i++) {
            Cell nr = getNextRow(new KeyValue(Bytes.toBytes(i), System.currentTimeMillis()));
            if ((i + 1) == (TestDefaultMemStore.ROW_COUNT)) {
                Assert.assertNull(nr);
            } else {
                Assert.assertTrue(((COMPARATOR.compareRows(nr, new KeyValue(Bytes.toBytes((i + 1)), System.currentTimeMillis()))) == 0));
            }
        }
        // starting from each row, validate results should contain the starting row
        Configuration conf = HBaseConfiguration.create();
        for (int startRowId = 0; startRowId < (TestDefaultMemStore.ROW_COUNT); startRowId++) {
            ScanInfo scanInfo = new ScanInfo(conf, TestDefaultMemStore.FAMILY, 0, 1, Integer.MAX_VALUE, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, this.memstore.getComparator(), false);
            try (InternalScanner scanner = new StoreScanner(new Scan().withStartRow(Bytes.toBytes(startRowId)), scanInfo, null, memstore.getScanners(0))) {
                List<Cell> results = new ArrayList<>();
                for (int i = 0; scanner.next(results); i++) {
                    int rowId = startRowId + i;
                    Cell left = results.get(0);
                    byte[] row1 = Bytes.toBytes(rowId);
                    Assert.assertTrue("Row name", ((COMPARATOR.compareRows(left, row1, 0, row1.length)) == 0));
                    Assert.assertEquals("Count of columns", TestDefaultMemStore.QUALIFIER_COUNT, results.size());
                    List<Cell> row = new ArrayList<>();
                    for (Cell kv : results) {
                        row.add(kv);
                    }
                    isExpectedRowWithoutTimestamps(rowId, row);
                    // Clear out set. Otherwise row results accumulate.
                    results.clear();
                }
            }
        }
    }

    @Test
    public void testGet_memstoreAndSnapShot() throws IOException {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier1");
        byte[] qf2 = Bytes.toBytes("testqualifier2");
        byte[] qf3 = Bytes.toBytes("testqualifier3");
        byte[] qf4 = Bytes.toBytes("testqualifier4");
        byte[] qf5 = Bytes.toBytes("testqualifier5");
        byte[] val = Bytes.toBytes("testval");
        // Setting up memstore
        memstore.add(new KeyValue(row, fam, qf1, val), null);
        memstore.add(new KeyValue(row, fam, qf2, val), null);
        memstore.add(new KeyValue(row, fam, qf3, val), null);
        // Creating a snapshot
        memstore.snapshot();
        Assert.assertEquals(3, memstore.getSnapshot().getCellsCount());
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf4, val), null);
        memstore.add(new KeyValue(row, fam, qf5, val), null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Delete tests
    // ////////////////////////////////////////////////////////////////////////////
    @Test
    public void testGetWithDelete() throws IOException {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier");
        byte[] val = Bytes.toBytes("testval");
        long ts1 = System.nanoTime();
        KeyValue put1 = new KeyValue(row, fam, qf1, ts1, val);
        long ts2 = ts1 + 1;
        KeyValue put2 = new KeyValue(row, fam, qf1, ts2, val);
        long ts3 = ts2 + 1;
        KeyValue put3 = new KeyValue(row, fam, qf1, ts3, val);
        memstore.add(put1, null);
        memstore.add(put2, null);
        memstore.add(put3, null);
        Assert.assertEquals(3, memstore.getActive().getCellsCount());
        KeyValue del2 = new KeyValue(row, fam, qf1, ts2, Type.Delete, val);
        memstore.add(del2, null);
        List<Cell> expected = new ArrayList<>();
        expected.add(put3);
        expected.add(del2);
        expected.add(put2);
        expected.add(put1);
        Assert.assertEquals(4, memstore.getActive().getCellsCount());
        int i = 0;
        for (Cell cell : memstore.getActive().getCellSet()) {
            Assert.assertEquals(expected.get((i++)), cell);
        }
    }

    @Test
    public void testGetWithDeleteColumn() throws IOException {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier");
        byte[] val = Bytes.toBytes("testval");
        long ts1 = System.nanoTime();
        KeyValue put1 = new KeyValue(row, fam, qf1, ts1, val);
        long ts2 = ts1 + 1;
        KeyValue put2 = new KeyValue(row, fam, qf1, ts2, val);
        long ts3 = ts2 + 1;
        KeyValue put3 = new KeyValue(row, fam, qf1, ts3, val);
        memstore.add(put1, null);
        memstore.add(put2, null);
        memstore.add(put3, null);
        Assert.assertEquals(3, memstore.getActive().getCellsCount());
        KeyValue del2 = new KeyValue(row, fam, qf1, ts2, Type.DeleteColumn, val);
        memstore.add(del2, null);
        List<Cell> expected = new ArrayList<>();
        expected.add(put3);
        expected.add(del2);
        expected.add(put2);
        expected.add(put1);
        Assert.assertEquals(4, memstore.getActive().getCellsCount());
        int i = 0;
        for (Cell cell : memstore.getActive().getCellSet()) {
            Assert.assertEquals(expected.get((i++)), cell);
        }
    }

    @Test
    public void testGetWithDeleteFamily() throws IOException {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier1");
        byte[] qf2 = Bytes.toBytes("testqualifier2");
        byte[] qf3 = Bytes.toBytes("testqualifier3");
        byte[] val = Bytes.toBytes("testval");
        long ts = System.nanoTime();
        KeyValue put1 = new KeyValue(row, fam, qf1, ts, val);
        KeyValue put2 = new KeyValue(row, fam, qf2, ts, val);
        KeyValue put3 = new KeyValue(row, fam, qf3, ts, val);
        KeyValue put4 = new KeyValue(row, fam, qf3, (ts + 1), val);
        memstore.add(put1, null);
        memstore.add(put2, null);
        memstore.add(put3, null);
        memstore.add(put4, null);
        KeyValue del = new KeyValue(row, fam, null, ts, Type.DeleteFamily, val);
        memstore.add(del, null);
        List<Cell> expected = new ArrayList<>();
        expected.add(del);
        expected.add(put1);
        expected.add(put2);
        expected.add(put4);
        expected.add(put3);
        Assert.assertEquals(5, memstore.getActive().getCellsCount());
        int i = 0;
        for (Cell cell : memstore.getActive().getCellSet()) {
            Assert.assertEquals(expected.get((i++)), cell);
        }
    }

    @Test
    public void testKeepDeleteInmemstore() {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf = Bytes.toBytes("testqualifier");
        byte[] val = Bytes.toBytes("testval");
        long ts = System.nanoTime();
        memstore.add(new KeyValue(row, fam, qf, ts, val), null);
        KeyValue delete = new KeyValue(row, fam, qf, ts, Type.Delete, val);
        memstore.add(delete, null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        Assert.assertEquals(delete, memstore.getActive().first());
    }

    @Test
    public void testRetainsDeleteVersion() throws IOException {
        // add a put to memstore
        memstore.add(KeyValueTestUtil.create("row1", "fam", "a", 100, "dont-care"), null);
        // now process a specific delete:
        KeyValue delete = KeyValueTestUtil.create("row1", "fam", "a", 100, Delete, "dont-care");
        memstore.add(delete, null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        Assert.assertEquals(delete, memstore.getActive().first());
    }

    @Test
    public void testRetainsDeleteColumn() throws IOException {
        // add a put to memstore
        memstore.add(KeyValueTestUtil.create("row1", "fam", "a", 100, "dont-care"), null);
        // now process a specific delete:
        KeyValue delete = KeyValueTestUtil.create("row1", "fam", "a", 100, DeleteColumn, "dont-care");
        memstore.add(delete, null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        Assert.assertEquals(delete, memstore.getActive().first());
    }

    @Test
    public void testRetainsDeleteFamily() throws IOException {
        // add a put to memstore
        memstore.add(KeyValueTestUtil.create("row1", "fam", "a", 100, "dont-care"), null);
        // now process a specific delete:
        KeyValue delete = KeyValueTestUtil.create("row1", "fam", "a", 100, DeleteFamily, "dont-care");
        memstore.add(delete, null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        Assert.assertEquals(delete, memstore.getActive().first());
    }

    /**
     * Add keyvalues with a fixed memstoreTs, and checks that memstore size is decreased
     * as older keyvalues are deleted from the memstore.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpsertMemstoreSize() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        memstore = new DefaultMemStore(conf, CellComparatorImpl.COMPARATOR);
        MemStoreSize oldSize = memstore.size();
        List<Cell> l = new ArrayList<>();
        KeyValue kv1 = KeyValueTestUtil.create("r", "f", "q", 100, "v");
        KeyValue kv2 = KeyValueTestUtil.create("r", "f", "q", 101, "v");
        KeyValue kv3 = KeyValueTestUtil.create("r", "f", "q", 102, "v");
        kv1.setSequenceId(1);
        kv2.setSequenceId(1);
        kv3.setSequenceId(1);
        l.add(kv1);
        l.add(kv2);
        l.add(kv3);
        this.memstore.upsert(l, 2, null);// readpoint is 2

        MemStoreSize newSize = this.memstore.size();
        assert (newSize.getDataSize()) > (oldSize.getDataSize());
        // The kv1 should be removed.
        assert (memstore.getActive().getCellsCount()) == 2;
        KeyValue kv4 = KeyValueTestUtil.create("r", "f", "q", 104, "v");
        kv4.setSequenceId(1);
        l.clear();
        l.add(kv4);
        this.memstore.upsert(l, 3, null);
        Assert.assertEquals(newSize, this.memstore.size());
        // The kv2 should be removed.
        assert (memstore.getActive().getCellsCount()) == 2;
        // this.memstore = null;
    }

    // //////////////////////////////////
    // Test for periodic memstore flushes
    // based on time of oldest edit
    // //////////////////////////////////
    /**
     * Tests that the timeOfOldestEdit is updated correctly for the
     * various edit operations in memstore.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpdateToTimeOfOldestEdit() throws Exception {
        try {
            TestDefaultMemStore.EnvironmentEdgeForMemstoreTest edge = new TestDefaultMemStore.EnvironmentEdgeForMemstoreTest();
            EnvironmentEdgeManager.injectEdge(edge);
            DefaultMemStore memstore = new DefaultMemStore();
            long t = memstore.timeOfOldestEdit();
            Assert.assertEquals(Long.MAX_VALUE, t);
            // test the case that the timeOfOldestEdit is updated after a KV add
            memstore.add(KeyValueTestUtil.create("r", "f", "q", 100, "v"), null);
            t = memstore.timeOfOldestEdit();
            Assert.assertTrue((t == 1234));
            // snapshot() will reset timeOfOldestEdit. The method will also assert the
            // value is reset to Long.MAX_VALUE
            t = runSnapshot(memstore);
            // test the case that the timeOfOldestEdit is updated after a KV delete
            memstore.add(KeyValueTestUtil.create("r", "f", "q", 100, Delete, "v"), null);
            t = memstore.timeOfOldestEdit();
            Assert.assertTrue((t == 1234));
            t = runSnapshot(memstore);
            // test the case that the timeOfOldestEdit is updated after a KV upsert
            List<Cell> l = new ArrayList<>();
            KeyValue kv1 = KeyValueTestUtil.create("r", "f", "q", 100, "v");
            kv1.setSequenceId(100);
            l.add(kv1);
            memstore.upsert(l, 1000, null);
            t = memstore.timeOfOldestEdit();
            Assert.assertTrue((t == 1234));
        } finally {
            EnvironmentEdgeManager.reset();
        }
    }

    /**
     * Tests the HRegion.shouldFlush method - adds an edit in the memstore
     * and checks that shouldFlush returns true, and another where it disables
     * the periodic flush functionality and tests whether shouldFlush returns
     * false.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testShouldFlush() throws Exception {
        Configuration conf = new Configuration();
        conf.setInt(MEMSTORE_PERIODIC_FLUSH_INTERVAL, 1000);
        checkShouldFlush(conf, true);
        // test disable flush
        conf.setInt(MEMSTORE_PERIODIC_FLUSH_INTERVAL, 0);
        checkShouldFlush(conf, false);
    }

    @Test
    public void testShouldFlushMeta() throws Exception {
        // write an edit in the META and ensure the shouldFlush (that the periodic memstore
        // flusher invokes) returns true after SYSTEM_CACHE_FLUSH_INTERVAL (even though
        // the MEMSTORE_PERIODIC_FLUSH_INTERVAL is set to a higher value)
        Configuration conf = new Configuration();
        conf.setInt(MEMSTORE_PERIODIC_FLUSH_INTERVAL, ((SYSTEM_CACHE_FLUSH_INTERVAL) * 10));
        HBaseTestingUtility hbaseUtility = HBaseTestingUtility.createLocalHTU(conf);
        Path testDir = getDataTestDir();
        TestDefaultMemStore.EnvironmentEdgeForMemstoreTest edge = new TestDefaultMemStore.EnvironmentEdgeForMemstoreTest();
        EnvironmentEdgeManager.injectEdge(edge);
        edge.setCurrentTimeMillis(1234);
        WALFactory wFactory = new WALFactory(conf, "1234");
        HRegion meta = HRegion.createHRegion(FIRST_META_REGIONINFO, testDir, conf, FSTableDescriptors.createMetaTableDescriptor(conf), wFactory.getWAL(FIRST_META_REGIONINFO));
        // parameterized tests add [#] suffix get rid of [ and ].
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(TableName.valueOf(name.getMethodName().replaceAll("[\\[\\]]", "_"))).setColumnFamily(ColumnFamilyDescriptorBuilder.of("foo")).build();
        RegionInfo hri = RegionInfoBuilder.newBuilder(desc.getTableName()).setStartKey(Bytes.toBytes("row_0200")).setEndKey(Bytes.toBytes("row_0300")).build();
        HRegion r = HRegion.createHRegion(hri, testDir, conf, desc, wFactory.getWAL(hri));
        TestDefaultMemStore.addRegionToMETA(meta, r);
        edge.setCurrentTimeMillis((1234 + 100));
        StringBuilder sb = new StringBuilder();
        Assert.assertTrue(((meta.shouldFlush(sb)) == false));
        edge.setCurrentTimeMillis((((edge.currentTime()) + (SYSTEM_CACHE_FLUSH_INTERVAL)) + 1));
        Assert.assertTrue(((meta.shouldFlush(sb)) == true));
    }

    private class EnvironmentEdgeForMemstoreTest implements EnvironmentEdge {
        long t = 1234;

        @Override
        public long currentTime() {
            return t;
        }

        public void setCurrentTimeMillis(long t) {
            this.t = t;
        }
    }
}

