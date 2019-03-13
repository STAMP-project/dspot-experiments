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


import CompactingMemStore.COMPACTING_MEMSTORE_TYPE_KEY;
import CompactingMemStore.IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY;
import CompactingMemStore.IndexType.CHUNK_MAP;
import KeyValue.LOWESTKEY;
import MemStoreCompactionStrategy.COMPACTING_MEMSTORE_THRESHOLD_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MemoryCompactionPolicy;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.ClassSize;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CellChunkImmutableSegment.DEEP_OVERHEAD_CCM;
import static ChunkCreator.SIZEOF_CHUNK_HEADER;
import static MemStoreLAB.CHUNK_SIZE_DEFAULT;
import static MemStoreLAB.MAX_ALLOC_DEFAULT;
import static MutableSegment.DEEP_OVERHEAD;


/**
 * compacted memstore test case
 */
@Category({ RegionServerTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestCompactingToCellFlatMapMemStore extends TestCompactingMemStore {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactingToCellFlatMapMemStore.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCompactingToCellFlatMapMemStore.class);

    public final boolean toCellChunkMap;

    Configuration conf;

    // ////////////////////////////////////////////////////////////////////////////
    // Helpers
    // ////////////////////////////////////////////////////////////////////////////
    public TestCompactingToCellFlatMapMemStore(String type) {
        if (type == "CHUNK_MAP") {
            toCellChunkMap = true;
        } else {
            toCellChunkMap = false;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////
    // Merging tests
    // ////////////////////////////////////////////////////////////////////////////
    @Test
    public void testMerging() throws IOException {
        if (toCellChunkMap) {
            // set memstore to flat into CellChunkMap
            ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        }
        String[] keys1 = new String[]{ "A", "A", "B", "C", "F", "H" };
        String[] keys2 = new String[]{ "A", "B", "D", "G", "I", "J" };
        String[] keys3 = new String[]{ "D", "B", "B", "E" };
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        addRowsByKeysDataSize(memstore, keys1);
        flushInMemory();// push keys to pipeline should not compact

        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        addRowsByKeysDataSize(memstore, keys2);// also should only flatten

        int counter2 = 0;
        for (Segment s : memstore.getSegments()) {
            counter2 += s.getCellsCount();
        }
        Assert.assertEquals(12, counter2);
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).disableCompaction();
        flushInMemory();// push keys to pipeline without flattening

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        int counter3 = 0;
        for (Segment s : memstore.getSegments()) {
            counter3 += s.getCellsCount();
        }
        Assert.assertEquals(12, counter3);
        addRowsByKeysDataSize(memstore, keys3);
        int counter4 = 0;
        for (Segment s : memstore.getSegments()) {
            counter4 += s.getCellsCount();
        }
        Assert.assertEquals(16, counter4);
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).enableCompaction();
        flushInMemory();// push keys to pipeline and compact

        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        int counter = 0;
        for (Segment s : memstore.getSegments()) {
            counter += s.getCellsCount();
        }
        Assert.assertEquals(16, counter);
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        ImmutableSegment s = memstore.getSnapshot();
        memstore.clearSnapshot(snapshot.getId());
    }

    @Test
    public void testTimeRangeAfterCompaction() throws IOException {
        if (toCellChunkMap) {
            // set memstore to flat into CellChunkMap
            ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        }
        testTimeRange(true);
    }

    @Test
    public void testTimeRangeAfterMerge() throws IOException {
        if (toCellChunkMap) {
            // set memstore to flat into CellChunkMap
            ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        }
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        testTimeRange(false);
    }

    @Test
    public void testCountOfCellsAfterFlatteningByScan() throws IOException {
        String[] keys1 = new String[]{ "A", "B", "C" };// A, B, C

        addRowsByKeysWith50Cols(memstore, keys1);
        // this should only flatten as there are no duplicates
        flushInMemory();
        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        List<KeyValueScanner> scanners = memstore.getScanners(Long.MAX_VALUE);
        // seek
        int count = 0;
        for (int i = 0; i < (scanners.size()); i++) {
            scanners.get(i).seek(LOWESTKEY);
            while ((scanners.get(i).next()) != null) {
                count++;
            } 
        }
        Assert.assertEquals("the count should be ", 150, count);
        for (int i = 0; i < (scanners.size()); i++) {
            scanners.get(i).close();
        }
    }

    @Test
    public void testCountOfCellsAfterFlatteningByIterator() throws IOException {
        String[] keys1 = new String[]{ "A", "B", "C" };// A, B, C

        addRowsByKeysWith50Cols(memstore, keys1);
        // this should only flatten as there are no duplicates
        flushInMemory();
        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        // Just doing the cnt operation here
        MemStoreSegmentsIterator itr = new MemStoreMergerSegmentsIterator(getImmutableSegments().getStoreSegments(), CellComparatorImpl.COMPARATOR, 10);
        int cnt = 0;
        try {
            while ((itr.next()) != null) {
                cnt++;
            } 
        } finally {
            itr.close();
        }
        Assert.assertEquals("the count should be ", 150, cnt);
    }

    @Override
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

    @Override
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
    public void testFlatteningToCellChunkMap() throws IOException {
        // set memstore to flat into CellChunkMap
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        int numOfCells = 8;
        String[] keys1 = new String[]{ "A", "A", "B", "C", "D", "D", "E", "F" };// A1, A2, B3, C4, D5, D6, E7, F8

        // make one cell
        byte[] row = Bytes.toBytes(keys1[0]);
        byte[] val = Bytes.toBytes(((keys1[0]) + 0));
        KeyValue kv = new KeyValue(row, Bytes.toBytes("testfamily"), Bytes.toBytes("testqualifier"), System.currentTimeMillis(), val);
        // test 1 bucket
        int totalCellsLen = addRowsByKeys(memstore, keys1);
        long oneCellOnCSLMHeapSize = ClassSize.align((((ClassSize.CONCURRENT_SKIPLISTMAP_ENTRY) + (KeyValue.FIXED_OVERHEAD)) + (kv.getSerializedSize())));
        long totalHeapSize = (numOfCells * oneCellOnCSLMHeapSize) + (DEEP_OVERHEAD);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and flatten

        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        long oneCellOnCCMHeapSize = (ClassSize.CELL_CHUNK_MAP_ENTRY) + (ClassSize.align(kv.getSerializedSize()));
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CCM)) + (numOfCells * oneCellOnCCMHeapSize);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        MemStoreSize mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss);
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(numOfCells, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
    }

    /**
     * CellChunkMap Segment index requires all cell data to be written in the MSLAB Chunks.
     * Even though MSLAB is enabled, cells bigger than maxAlloc
     * (even if smaller than the size of a chunk) are not written in the MSLAB Chunks.
     * If such cells are found in the process of flattening into CellChunkMap
     * (in-memory-flush) they need to be copied into MSLAB.
     * testFlatteningToBigCellChunkMap checks that the process of flattening into
     * CellChunkMap succeeds, even when such big cells are allocated.
     */
    @Test
    public void testFlatteningToBigCellChunkMap() throws IOException {
        if ((toCellChunkMap) == false) {
            return;
        }
        // set memstore to flat into CellChunkMap
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        int numOfCells = 4;
        char[] chars = new char[MAX_ALLOC_DEFAULT];
        for (int i = 0; i < (chars.length); i++) {
            chars[i] = 'A';
        }
        String bigVal = new String(chars);
        String[] keys1 = new String[]{ "A", "B", "C", "D" };
        // make one cell
        byte[] row = Bytes.toBytes(keys1[0]);
        byte[] val = Bytes.toBytes(bigVal);
        KeyValue kv = new KeyValue(row, Bytes.toBytes("testfamily"), Bytes.toBytes("testqualifier"), System.currentTimeMillis(), val);
        // test 1 bucket
        int totalCellsLen = addRowsByKeys(memstore, keys1, val);
        long oneCellOnCSLMHeapSize = ClassSize.align(((ClassSize.CONCURRENT_SKIPLISTMAP_ENTRY) + (kv.heapSize())));
        long totalHeapSize = (numOfCells * oneCellOnCSLMHeapSize) + (DEEP_OVERHEAD);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and flatten

        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // One cell is duplicated, but it shouldn't be compacted because we are in BASIC mode.
        // totalCellsLen should remain the same
        long oneCellOnCCMHeapSize = (ClassSize.CELL_CHUNK_MAP_ENTRY) + (ClassSize.align(kv.getSerializedSize()));
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CCM)) + (numOfCells * oneCellOnCCMHeapSize);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        MemStoreSize mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss);
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(numOfCells, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
    }

    /**
     * CellChunkMap Segment index requires all cell data to be written in the MSLAB Chunks.
     * Even though MSLAB is enabled, cells bigger than the size of a chunk are not
     * written in the MSLAB Chunks.
     * If such cells are found in the process of flattening into CellChunkMap
     * (in-memory-flush) they need to be copied into MSLAB.
     * testFlatteningToJumboCellChunkMap checks that the process of flattening
     * into CellChunkMap succeeds, even when such big cells are allocated.
     */
    @Test
    public void testFlatteningToJumboCellChunkMap() throws IOException {
        if ((toCellChunkMap) == false) {
            return;
        }
        // set memstore to flat into CellChunkMap
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        int numOfCells = 1;
        char[] chars = new char[CHUNK_SIZE_DEFAULT];
        for (int i = 0; i < (chars.length); i++) {
            chars[i] = 'A';
        }
        String bigVal = new String(chars);
        String[] keys1 = new String[]{ "A" };
        // make one cell
        byte[] row = Bytes.toBytes(keys1[0]);
        byte[] val = Bytes.toBytes(bigVal);
        KeyValue kv = new KeyValue(row, Bytes.toBytes("testfamily"), Bytes.toBytes("testqualifier"), System.currentTimeMillis(), val);
        // test 1 bucket
        int totalCellsLen = addRowsByKeys(memstore, keys1, val);
        long oneCellOnCSLMHeapSize = ClassSize.align(((ClassSize.CONCURRENT_SKIPLISTMAP_ENTRY) + (kv.heapSize())));
        long totalHeapSize = (numOfCells * oneCellOnCSLMHeapSize) + (DEEP_OVERHEAD);
        Assert.assertEquals(totalCellsLen, regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        flushInMemory();// push keys to pipeline and flatten

        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        Assert.assertEquals(0, memstore.getSnapshot().getCellsCount());
        // One cell is duplicated, but it shouldn't be compacted because we are in BASIC mode.
        // totalCellsLen should remain the same
        long oneCellOnCCMHeapSize = ((long) (ClassSize.CELL_CHUNK_MAP_ENTRY)) + (ClassSize.align(kv.getSerializedSize()));
        totalHeapSize = ((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CCM)) + (numOfCells * oneCellOnCCMHeapSize);
        Assert.assertEquals((totalCellsLen + (SIZEOF_CHUNK_HEADER)), regionServicesForStores.getMemStoreSize());
        Assert.assertEquals(totalHeapSize, heapSize());
        MemStoreSize mss = memstore.getFlushableSize();
        MemStoreSnapshot snapshot = memstore.snapshot();// push keys to snapshot

        // simulate flusher
        region.decrMemStoreSize(mss);
        ImmutableSegment s = memstore.getSnapshot();
        Assert.assertEquals(numOfCells, s.getCellsCount());
        Assert.assertEquals(0, regionServicesForStores.getMemStoreSize());
        memstore.clearSnapshot(snapshot.getId());
        // Allocating two big cells (too big for being copied into a regular chunk).
        String[] keys2 = new String[]{ "C", "D" };
        addRowsByKeys(memstore, keys2, val);
        while (isMemStoreFlushingInMemory()) {
            Threads.sleep(10);
        } 
        // The in-memory flush size is bigger than the size of a single cell,
        // but smaller than the size of two cells.
        // Therefore, the two created cells are flattened together.
        totalHeapSize = (((DEEP_OVERHEAD) + (DEEP_OVERHEAD_CCM)) + (1 * oneCellOnCSLMHeapSize)) + (1 * oneCellOnCCMHeapSize);
        Assert.assertEquals(totalHeapSize, heapSize());
    }

    /**
     * CellChunkMap Segment index requires all cell data to be written in the MSLAB Chunks.
     * Even though MSLAB is enabled, cells bigger than the size of a chunk are not
     * written in the MSLAB Chunks.
     * If such cells are found in the process of a merge they need to be copied into MSLAB.
     * testForceCopyOfBigCellIntoImmutableSegment checks that the
     * ImmutableMemStoreLAB's forceCopyOfBigCellInto does what it's supposed to do.
     */
    @Test
    public void testForceCopyOfBigCellIntoImmutableSegment() throws IOException {
        if ((toCellChunkMap) == false) {
            return;
        }
        // set memstore to flat into CellChunkMap
        MemoryCompactionPolicy compactionType = MemoryCompactionPolicy.BASIC;
        memstore.getConfiguration().setInt(COMPACTING_MEMSTORE_THRESHOLD_KEY, 4);
        memstore.getConfiguration().setDouble(IN_MEMORY_FLUSH_THRESHOLD_FACTOR_KEY, 0.014);
        memstore.getConfiguration().set(COMPACTING_MEMSTORE_TYPE_KEY, String.valueOf(compactionType));
        ((TestCompactingMemStore.MyCompactingMemStore) (memstore)).initiateType(compactionType, memstore.getConfiguration());
        ((CompactingMemStore) (memstore)).setIndexType(CHUNK_MAP);
        char[] chars = new char[CHUNK_SIZE_DEFAULT];
        for (int i = 0; i < (chars.length); i++) {
            chars[i] = 'A';
        }
        String bigVal = new String(chars);
        byte[] val = Bytes.toBytes(bigVal);
        // We need to add two cells, three times, in order to guarantee a merge
        List<String[]> keysList = new ArrayList<>();
        keysList.add(new String[]{ "A", "B" });
        keysList.add(new String[]{ "C", "D" });
        keysList.add(new String[]{ "E", "F" });
        keysList.add(new String[]{ "G", "H" });
        // Measuring the size of a single kv
        KeyValue kv = new KeyValue(Bytes.toBytes("A"), Bytes.toBytes("testfamily"), Bytes.toBytes("testqualifier"), System.currentTimeMillis(), val);
        long oneCellOnCCMHeapSize = ((long) (ClassSize.CELL_CHUNK_MAP_ENTRY)) + (ClassSize.align(kv.getSerializedSize()));
        long oneCellOnCSLMHeapSize = ClassSize.align(((ClassSize.CONCURRENT_SKIPLISTMAP_ENTRY) + (kv.heapSize())));
        long totalHeapSize = DEEP_OVERHEAD;
        for (int i = 0; i < (keysList.size()); i++) {
            addRowsByKeys(memstore, keysList.get(i), val);
            while (isMemStoreFlushingInMemory()) {
                Threads.sleep(10);
            } 
            if (i == 0) {
                totalHeapSize += ((DEEP_OVERHEAD_CCM) + oneCellOnCCMHeapSize) + oneCellOnCSLMHeapSize;
            } else {
                // The in-memory flush size is bigger than the size of a single cell,
                // but smaller than the size of two cells.
                // Therefore, the two created cells are flattened in a seperate segment.
                totalHeapSize += 2 * ((DEEP_OVERHEAD_CCM) + oneCellOnCCMHeapSize);
            }
            if (i == 2) {
                // Four out of the five segments are merged into one
                totalHeapSize -= 4 * (DEEP_OVERHEAD_CCM);
                totalHeapSize = ClassSize.align(totalHeapSize);
            }
            Assert.assertEquals(("i=" + i), totalHeapSize, heapSize());
        }
    }
}

