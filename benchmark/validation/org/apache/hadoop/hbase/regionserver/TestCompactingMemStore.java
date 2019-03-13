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


import AdaptiveMemStoreCompactionStrategy.ADAPTIVE_COMPACTION_THRESHOLD_KEY;
import CompactingMemStore.COMPACTING_MEMSTORE_TYPE_KEY;
import CompactingMemStore.IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY;
import KeyValue.LOWESTKEY;
import KeyValue.Type.Delete;
import MemStoreCompactionStrategy.COMPACTING_MEMSTORE_THRESHOLD_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.MemoryCompactionPolicy;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.exceptions.IllegalArgumentIOException;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CellArrayImmutableSegment.DEEP_OVERHEAD_CAM;
import static MutableSegment.DEEP_OVERHEAD;


/**
 * compacted memstore test case
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestCompactingMemStore extends TestDefaultMemStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactingMemStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactingMemStore.class);

    protected static ChunkCreator chunkCreator;

    protected HRegion region;

    protected RegionServicesForStores regionServicesForStores;

    protected HStore store;

    /**
     * A simple test which verifies the 3 possible states when scanning across snapshot.
     *
     * @throws IOException
     * 		
     * @throws CloneNotSupportedException
     * 		
     */
    @Override
    @Test
    public void testScanAcrossSnapshot2() throws IOException, CloneNotSupportedException {
        // we are going to the scanning across snapshot with two kvs
        // kv1 should always be returned before kv2
        final byte[] one = Bytes.toBytes(1);
        final byte[] two = Bytes.toBytes(2);
        final byte[] f = Bytes.toBytes("f");
        final byte[] q = Bytes.toBytes("q");
        final byte[] v = Bytes.toBytes(3);
        final KeyValue kv1 = new KeyValue(one, f, q, 10, v);
        final KeyValue kv2 = new KeyValue(two, f, q, 10, v);
        // use case 1: both kvs in kvset
        this.memstore.add(kv1.clone(), null);
        this.memstore.add(kv2.clone(), null);
        verifyScanAcrossSnapshot2(kv1, kv2);
        // use case 2: both kvs in snapshot
        this.memstore.snapshot();
        verifyScanAcrossSnapshot2(kv1, kv2);
        // use case 3: first in snapshot second in kvset
        this.memstore = new CompactingMemStore(HBaseConfiguration.create(), CellComparator.getInstance(), store, regionServicesForStores, MemoryCompactionPolicy.EAGER);
        this.memstore.add(kv1.clone(), null);
        // As compaction is starting in the background the repetition
        // of the k1 might be removed BUT the scanners created earlier
        // should look on the OLD MutableCellSetSegment, so this should be OK...
        this.memstore.snapshot();
        this.memstore.add(kv2.clone(), null);
        verifyScanAcrossSnapshot2(kv1, kv2);
    }

    /**
     * Test memstore snapshots
     *
     * @throws IOException
     * 		
     */
    @Override
    @Test
    public void testSnapshotting() throws IOException {
        final int snapshotCount = 5;
        // Add some rows, run a snapshot. Do it a few times.
        for (int i = 0; i < snapshotCount; i++) {
            addRows(this.memstore);
            runSnapshot(this.memstore, true);
            Assert.assertEquals("History not being cleared", 0, this.memstore.getSnapshot().getCellsCount());
        }
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
    @Override
    @Test
    public void testGetNextRow() throws Exception {
        addRows(this.memstore);
        // Add more versions to make it a little more interesting.
        Thread.sleep(1);
        addRows(this.memstore);
        Cell closestToEmpty = ((CompactingMemStore) (this.memstore)).getNextRow(LOWESTKEY);
        Assert.assertTrue(((CellComparator.getInstance().compareRows(closestToEmpty, new KeyValue(Bytes.toBytes(0), System.currentTimeMillis()))) == 0));
        for (int i = 0; i < (TestDefaultMemStore.ROW_COUNT); i++) {
            Cell nr = getNextRow(new KeyValue(Bytes.toBytes(i), System.currentTimeMillis()));
            if ((i + 1) == (TestDefaultMemStore.ROW_COUNT)) {
                Assert.assertNull(nr);
            } else {
                Assert.assertTrue(((CellComparator.getInstance().compareRows(nr, new KeyValue(Bytes.toBytes((i + 1)), System.currentTimeMillis()))) == 0));
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
                    Assert.assertTrue("Row name", ((CellComparator.getInstance().compareRows(left, row1, 0, row1.length)) == 0));
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

    @Override
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
        // Pushing to pipeline
        flushInMemory();
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // Creating a snapshot
        memstore.snapshot();
        Assert.assertEquals(3, memstore.getSnapshot().getCellsCount());
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf4, val), null);
        memstore.add(new KeyValue(row, fam, qf5, val), null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
    }

    // //////////////////////////////////
    // Test for periodic memstore flushes
    // based on time of oldest edit
    // //////////////////////////////////
    /**
     * Add keyvalues with a fixed memstoreTs, and checks that memstore size is decreased
     * as older keyvalues are deleted from the memstore.
     *
     * @throws Exception
     * 		
     */
    @Override
    @Test
    public void testUpsertMemstoreSize() throws Exception {
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

    /**
     * Tests that the timeOfOldestEdit is updated correctly for the
     * various edit operations in memstore.
     *
     * @throws Exception
     * 		
     */
    @Override
    @Test
    public void testUpdateToTimeOfOldestEdit() throws Exception {
        try {
            TestCompactingMemStore.EnvironmentEdgeForMemstoreTest edge = new TestCompactingMemStore.EnvironmentEdgeForMemstoreTest();
            EnvironmentEdgeManager.injectEdge(edge);
            long t = memstore.timeOfOldestEdit();
            Assert.assertEquals(Long.MAX_VALUE, t);
            // test the case that the timeOfOldestEdit is updated after a KV add
            memstore.add(KeyValueTestUtil.create("r", "f", "q", 100, "v"), null);
            t = memstore.timeOfOldestEdit();
            Assert.assertTrue((t == 1234));
            // The method will also assert
            // the value is reset to Long.MAX_VALUE
            t = runSnapshot(memstore, true);
            // test the case that the timeOfOldestEdit is updated after a KV delete
            memstore.add(KeyValueTestUtil.create("r", "f", "q", 100, Delete, "v"), null);
            t = memstore.timeOfOldestEdit();
            Assert.assertTrue((t == 1234));
            t = runSnapshot(memstore, true);
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

    @Test
    public void testPuttingBackChunksAfterFlushing() throws IOException {
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
        MemStoreSnapshot snapshot = memstore.snapshot();
        Assert.assertEquals(3, memstore.getSnapshot().getCellsCount());
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf4, val), null);
        memstore.add(new KeyValue(row, fam, qf5, val), null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        // close the scanners
        for (KeyValueScanner scanner : snapshot.getScanners()) {
            scanner.close();
        }
        memstore.clearSnapshot(snapshot.getId());
        int chunkCount = TestCompactingMemStore.chunkCreator.getPoolSize();
        Assert.assertTrue((chunkCount > 0));
    }

    @Test
    public void testPuttingBackChunksWithOpeningScanner() throws IOException {
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier1");
        byte[] qf2 = Bytes.toBytes("testqualifier2");
        byte[] qf3 = Bytes.toBytes("testqualifier3");
        byte[] qf4 = Bytes.toBytes("testqualifier4");
        byte[] qf5 = Bytes.toBytes("testqualifier5");
        byte[] qf6 = Bytes.toBytes("testqualifier6");
        byte[] qf7 = Bytes.toBytes("testqualifier7");
        byte[] val = Bytes.toBytes("testval");
        // Setting up memstore
        memstore.add(new KeyValue(row, fam, qf1, val), null);
        memstore.add(new KeyValue(row, fam, qf2, val), null);
        memstore.add(new KeyValue(row, fam, qf3, val), null);
        // Creating a snapshot
        MemStoreSnapshot snapshot = memstore.snapshot();
        Assert.assertEquals(3, memstore.getSnapshot().getCellsCount());
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf4, val), null);
        memstore.add(new KeyValue(row, fam, qf5, val), null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        // opening scanner before clear the snapshot
        List<KeyValueScanner> scanners = memstore.getScanners(0);
        // Shouldn't putting back the chunks to pool,since some scanners are opening
        // based on their data
        // close the scanners
        for (KeyValueScanner scanner : snapshot.getScanners()) {
            scanner.close();
        }
        memstore.clearSnapshot(snapshot.getId());
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) == 0));
        // Chunks will be put back to pool after close scanners;
        for (KeyValueScanner scanner : scanners) {
            scanner.close();
        }
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) > 0));
        // clear chunks
        TestCompactingMemStore.chunkCreator.clearChunksInPool();
        // Creating another snapshot
        snapshot = memstore.snapshot();
        // Adding more value
        memstore.add(new KeyValue(row, fam, qf6, val), null);
        memstore.add(new KeyValue(row, fam, qf7, val), null);
        // opening scanners
        scanners = memstore.getScanners(0);
        // close scanners before clear the snapshot
        for (KeyValueScanner scanner : scanners) {
            scanner.close();
        }
        // Since no opening scanner, the chunks of snapshot should be put back to
        // pool
        // close the scanners
        for (KeyValueScanner scanner : snapshot.getScanners()) {
            scanner.close();
        }
        memstore.clearSnapshot(snapshot.getId());
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) > 0));
    }

    @Test
    public void testPuttingBackChunksWithOpeningPipelineScanner() throws IOException {
        // set memstore to do data compaction and not to use the speculative scan
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.EAGER;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        byte[] row = Bytes.toBytes("testrow");
        byte[] fam = Bytes.toBytes("testfamily");
        byte[] qf1 = Bytes.toBytes("testqualifier1");
        byte[] qf2 = Bytes.toBytes("testqualifier2");
        byte[] qf3 = Bytes.toBytes("testqualifier3");
        byte[] val = Bytes.toBytes("testval");
        // Setting up memstore
        memstore.add(new KeyValue(row, fam, qf1, 1, val), null);
        memstore.add(new KeyValue(row, fam, qf2, 1, val), null);
        memstore.add(new KeyValue(row, fam, qf3, 1, val), null);
        // Creating a pipeline
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).disableCompaction();
        flushInMemory();
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf1, 2, val), null);
        memstore.add(new KeyValue(row, fam, qf2, 2, val), null);
        Assert.assertEquals(2, memstore.getActive().getCellsCount());
        // pipeline bucket 2
        flushInMemory();
        // opening scanner before force flushing
        List<KeyValueScanner> scanners = memstore.getScanners(0);
        // Shouldn't putting back the chunks to pool,since some scanners are opening
        // based on their data
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).enableCompaction();
        // trigger compaction
        flushInMemory();
        // Adding value to "new" memstore
        Assert.assertEquals(0, memstore.getActive().getCellsCount());
        memstore.add(new KeyValue(row, fam, qf3, 3, val), null);
        memstore.add(new KeyValue(row, fam, qf2, 3, val), null);
        memstore.add(new KeyValue(row, fam, qf1, 3, val), null);
        Assert.assertEquals(3, memstore.getActive().getCellsCount());
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) == 0));
        // Chunks will be put back to pool after close scanners;
        for (KeyValueScanner scanner : scanners) {
            scanner.close();
        }
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) > 0));
        // clear chunks
        TestCompactingMemStore.chunkCreator.clearChunksInPool();
        // Creating another snapshot
        MemStoreSnapshot snapshot = memstore.snapshot();
        // close the scanners
        for (KeyValueScanner scanner : snapshot.getScanners()) {
            scanner.close();
        }
        memstore.clearSnapshot(snapshot.getId());
        snapshot = memstore.snapshot();
        // Adding more value
        memstore.add(new KeyValue(row, fam, qf2, 4, val), null);
        memstore.add(new KeyValue(row, fam, qf3, 4, val), null);
        // opening scanners
        scanners = memstore.getScanners(0);
        // close scanners before clear the snapshot
        for (KeyValueScanner scanner : scanners) {
            scanner.close();
        }
        // Since no opening scanner, the chunks of snapshot should be put back to
        // pool
        // close the scanners
        for (KeyValueScanner scanner : snapshot.getScanners()) {
            scanner.close();
        }
        memstore.clearSnapshot(snapshot.getId());
        Assert.assertTrue(((TestCompactingMemStore.chunkCreator.getPoolSize()) > 0));
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Compaction tests
    // ////////////////////////////////////////////////////////////////////////////
    @Test
    public void testCompaction1Bucket() throws IOException {
        // set memstore to do basic structure flattening, the "eager" option is tested in
        // TestCompactingToCellFlatMapMemStore
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        String[] keys1 = new String[]{ "A", "A", "B", "C" };// A1, A2, B3, C4

        // test 1 bucket
        int totalCellsLen = addRowsByKeys(memstore, keys1);
        int oneCellOnCSLMHeapSize = 120;
        int oneCellOnCAHeapSize = 88;
        long totalHeapSize = (DEEP_OVERHEAD) + (4 * oneCellOnCSLMHeapSize);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and compact

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // There is no compaction, as the compacting memstore type is basic.
        // totalCellsLen remains the same
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CAM)) + (4 * oneCellOnCAHeapSize);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        MemStoreSize mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss);
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(4, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
    }

    @Test
    public void testCompaction2Buckets() throws IOException {
        // set memstore to do basic structure flattening, the "eager" option is tested in
        // TestCompactingToCellFlatMapMemStore
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_THRESHOLD_KEY, String.valueOf(1));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        String[] keys1 = new String[]{ "A", "A", "B", "C" };
        String[] keys2 = new String[]{ "A", "B", "D" };
        int totalCellsLen1 = addRowsByKeys(memstore, keys1);
        int oneCellOnCSLMHeapSize = 120;
        int oneCellOnCAHeapSize = 88;
        long totalHeapSize = (DEEP_OVERHEAD) + (4 * oneCellOnCSLMHeapSize);
        Assert.assertEquals(totalCellsLen1, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and compact

        int counter = 0;
        for (Segment s : memstore.getSegments()) {
            counter += s.getCellsCount();
        }
        Assert.assertEquals(4, counter);
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // There is no compaction, as the compacting memstore type is basic.
        // totalCellsLen remains the same
        Assert.assertEquals(totalCellsLen1, regionServicesForStores.getMemStoreSize());
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CAM)) + (4 * oneCellOnCAHeapSize);
        Assert.assertEquals(totalHeapSize, heapSize());
        int totalCellsLen2 = addRowsByKeys(memstore, keys2);
        totalHeapSize += 3 * oneCellOnCSLMHeapSize;
        Assert.assertEquals((totalCellsLen1 + totalCellsLen2), regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        MemStoreSize mss = memstore.getFlushableSize();
        flushInMemory();// push keys to pipeline and compact

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        Assert.assertEquals((totalCellsLen1 + totalCellsLen2), regionServicesForStores.getMemStoreSize());
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CAM)) + (7 * oneCellOnCAHeapSize);
        Assert.assertEquals(totalHeapSize, heapSize());
        mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss.getDataSize(), mss.getHeapSize(), mss.getOffHeapSize(), mss.getCellsCount());
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(7, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
    }

    @Test
    public void testCompaction3Buckets() throws IOException {
        // set memstore to do data compaction and not to use the speculative scan
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.EAGER;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        String[] keys1 = new String[]{ "A", "A", "B", "C" };
        String[] keys2 = new String[]{ "A", "B", "D" };
        String[] keys3 = new String[]{ "D", "B", "B" };
        int totalCellsLen1 = addRowsByKeys(memstore, keys1);// Adding 4 cells.

        int oneCellOnCSLMHeapSize = 120;
        int oneCellOnCAHeapSize = 88;
        Assert.assertEquals(totalCellsLen1, region.getMemStoreDataSize());
        long totalHeapSize = (DEEP_OVERHEAD) + (4 * oneCellOnCSLMHeapSize);
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and compact

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // One cell is duplicated and the compaction will remove it. All cells of same time so adjusting
        // totalCellsLen
        totalCellsLen1 = (totalCellsLen1 * 3) / 4;
        Assert.assertEquals(totalCellsLen1, regionServicesForStores.getMemStoreSize());
        // In memory flush to make a CellArrayMap instead of CSLM. See the overhead diff.
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CAM)) + (3 * oneCellOnCAHeapSize);
        Assert.assertEquals(totalHeapSize, heapSize());
        int totalCellsLen2 = addRowsByKeys(memstore, keys2);// Adding 3 more cells.

        long totalHeapSize2 = totalHeapSize + (3 * oneCellOnCSLMHeapSize);
        Assert.assertEquals((totalCellsLen1 + totalCellsLen2), regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize2, heapSize());
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).disableCompaction();
        MemStoreSize mss = memstore.getFlushableSize();
        flushInMemory();// push keys to pipeline without compaction

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // No change in the cells data size. ie. memstore size. as there is no compaction.
        Assert.assertEquals((totalCellsLen1 + totalCellsLen2), regionServicesForStores.getMemStoreSize());
        Assert.assertEquals((totalHeapSize2 + (DEEP_OVERHEAD_CAM)), heapSize());
        int totalCellsLen3 = addRowsByKeys(memstore, keys3);// 3 more cells added

        Assert.assertEquals(((totalCellsLen1 + totalCellsLen2) + totalCellsLen3), regionServicesForStores.getMemStoreSize());
        long totalHeapSize3 = (totalHeapSize2 + (DEEP_OVERHEAD_CAM)) + (3 * oneCellOnCSLMHeapSize);
        Assert.assertEquals(totalHeapSize3, heapSize());
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).enableCompaction();
        mss = memstore.getFlushableSize();
        flushInMemory();// push keys to pipeline and compact

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // active flushed to pipeline and all 3 segments compacted. Will get rid of duplicated cells.
        // Out of total 10, only 4 cells are unique
        totalCellsLen2 = totalCellsLen2 / 3;// 2 out of 3 cells are duplicated

        totalCellsLen3 = 0;// All duplicated cells.

        Assert.assertEquals(((totalCellsLen1 + totalCellsLen2) + totalCellsLen3), regionServicesForStores.getMemStoreSize());
        // Only 4 unique cells left
        Assert.assertEquals((((4 * oneCellOnCAHeapSize) + (DEEP_OVERHEAD)) + (DEEP_OVERHEAD_CAM)), heapSize());
        mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss.getDataSize(), mss.getHeapSize(), mss.getOffHeapSize(), mss.getCellsCount());
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(4, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
    }

    @Test
    public void testMagicCompaction3Buckets() throws IOException {
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.ADAPTIVE;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        memstore.getConfiguration().setDouble(ADAPTIVE_COMPACTION_THRESHOLD_KEY, 0.45);
        memstore.getConfiguration().setInt(AdaptiveMemStoreCompactionStrategy.COMPACTING_MEMSTORE_THRESHOLD_KEY, 2);
        memstore.getConfiguration().setInt(IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY, 1);
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        String[] keys1 = new String[]{ "A", "B", "D" };
        String[] keys2 = new String[]{ "A" };
        String[] keys3 = new String[]{ "A", "A", "B", "C" };
        String[] keys4 = new String[]{ "D", "B", "B" };
        int totalCellsLen1 = addRowsByKeys(memstore, keys1);// Adding 3 cells.

        int oneCellOnCSLMHeapSize = 120;
        Assert.assertEquals(totalCellsLen1, region.getMemStoreDataSize());
        long totalHeapSize = (DEEP_OVERHEAD) + (3 * oneCellOnCSLMHeapSize);
        Assert.assertEquals(totalHeapSize, memstore.heapSize());
        flushInMemory();// push keys to pipeline - flatten

        Assert.assertEquals(3, getImmutableSegments().getNumOfCells());
        Assert.assertEquals(1.0, getImmutableSegments().getEstimatedUniquesFrac(), 0);
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        addRowsByKeys(memstore, keys2);// Adding 1 more cell - flatten.

        flushInMemory();// push keys to pipeline without compaction

        Assert.assertEquals(4, getImmutableSegments().getNumOfCells());
        Assert.assertEquals(1.0, getImmutableSegments().getEstimatedUniquesFrac(), 0);
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        addRowsByKeys(memstore, keys3);// Adding 4 more cells - merge.

        flushInMemory();// push keys to pipeline without compaction

        Assert.assertEquals(8, getImmutableSegments().getNumOfCells());
        Assert.assertEquals((4.0 / 8.0), getImmutableSegments().getEstimatedUniquesFrac(), 0);
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        addRowsByKeys(memstore, keys4);// 3 more cells added - compact (or not)

        flushInMemory();// push keys to pipeline and compact

        int numCells = getImmutableSegments().getNumOfCells();
        Assert.assertTrue(((4 == numCells) || (11 == numCells)));
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        MemStoreSize mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss);
        ImmutableSegment s = memstore.getSnapshot();
        numCells = s.getCellsCount();
        Assert.assertTrue(((4 == numCells) || (11 == numCells)));
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
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

    protected static class MyCompactingMemStore extends CompactingMemStore {
        public MyCompactingMemStore(Configuration conf, CellComparator c, HStore store, RegionServicesForStores regionServices, MemoryCompactionPolicy compactionPolicy) throws IOException {
            super(conf, c, store, regionServices, compactionPolicy);
        }

        void disableCompaction() {
            allowCompaction.set(false);
        }

        void enableCompaction() {
            allowCompaction.set(true);
        }

        void initiateType(MemoryCompactionPolicy compactionType, Configuration conf) throws IllegalArgumentIOException {
            compactor.initiateCompactionStrategy(compactionType, conf, "CF_TEST");
        }
    }
}

