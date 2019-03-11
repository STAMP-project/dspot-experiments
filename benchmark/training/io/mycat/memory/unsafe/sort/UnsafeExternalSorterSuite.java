/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mycat.memory.unsafe.sort;


import Platform.BYTE_ARRAY_OFFSET;
import Platform.LONG_ARRAY_OFFSET;
import UnsafeExternalSorter.SpillableIterator;
import io.mycat.memory.unsafe.Platform;
import io.mycat.memory.unsafe.memory.TestMemoryManager;
import io.mycat.memory.unsafe.memory.mm.DataNodeMemoryManager;
import io.mycat.memory.unsafe.storage.DataNodeDiskManager;
import io.mycat.memory.unsafe.storage.DataNodeFileManager;
import io.mycat.memory.unsafe.storage.SerializerManager;
import io.mycat.memory.unsafe.utils.MycatPropertyConf;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static PrefixComparators.LONG;


public class UnsafeExternalSorterSuite {
    private static final Logger logger = LoggerFactory.getLogger(UnsafeExternalSorterSuite.class);

    static final TestMemoryManager memoryManager = new TestMemoryManager(new MycatPropertyConf().set("mycat.memory.offHeap.enabled", "false"));

    static final DataNodeMemoryManager DATA_NODE_MEMORY_MANAGER = new DataNodeMemoryManager(UnsafeExternalSorterSuite.memoryManager, 0);

    static final SerializerManager serializerManager = new SerializerManager();

    static final MycatPropertyConf conf = new MycatPropertyConf();

    static final DataNodeDiskManager blockManager = new DataNodeDiskManager(UnsafeExternalSorterSuite.conf, true, UnsafeExternalSorterSuite.serializerManager);

    static DataNodeFileManager diskBlockManager;

    // Use integer comparison for comparing prefixes (which are partition ids, in this case)
    final PrefixComparator prefixComparator = LONG;

    // Since the key fits within the 8-byte prefix, we don't need to do any record comparison, so
    // use a dummy comparator
    final RecordComparator recordComparator = new RecordComparator() {
        @Override
        public int compare(Object leftBaseObject, long leftBaseOffset, Object rightBaseObject, long rightBaseOffset) {
            return 0;
        }
    };

    static File tempDir;

    static {
        try {
            UnsafeExternalSorterSuite.diskBlockManager = UnsafeExternalSorterSuite.blockManager.diskBlockManager();
        } catch (IOException e) {
            UnsafeExternalSorterSuite.logger.error(e.getMessage());
        }
    }

    private final long pageSizeBytes = new MycatPropertyConf().getSizeAsBytes("mycat.buffer.pageSize", "4m");

    @Test
    public void testSortingOnlyByPrefix() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        UnsafeExternalSorterSuite.insertNumber(sorter, 5);
        UnsafeExternalSorterSuite.insertNumber(sorter, 1);
        UnsafeExternalSorterSuite.insertNumber(sorter, 3);
        sorter.spill();
        UnsafeExternalSorterSuite.insertNumber(sorter, 4);
        sorter.spill();
        UnsafeExternalSorterSuite.insertNumber(sorter, 2);
        UnsafeSorterIterator iter = sorter.getSortedIterator();
        for (int i = 1; i <= 5; i++) {
            iter.loadNext();
            Assert.assertEquals(i, iter.getKeyPrefix());
            Assert.assertEquals(4, iter.getRecordLength());
            Assert.assertEquals(i, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
        }
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void testSortingEmptyArrays() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        sorter.insertRecord(null, 0, 0, 0);
        sorter.insertRecord(null, 0, 0, 0);
        sorter.spill();
        sorter.insertRecord(null, 0, 0, 0);
        sorter.spill();
        sorter.insertRecord(null, 0, 0, 0);
        sorter.insertRecord(null, 0, 0, 0);
        UnsafeSorterIterator iter = sorter.getSortedIterator();
        for (int i = 1; i <= 5; i++) {
            iter.loadNext();
            Assert.assertEquals(0, iter.getKeyPrefix());
            Assert.assertEquals(0, iter.getRecordLength());
        }
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void testSortTimeMetric() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        long prevSortTime = sorter.getSortTimeNanos();
        Assert.assertEquals(prevSortTime, 0);
        sorter.insertRecord(null, 0, 0, 0);
        sorter.spill();
        Assert.assertThat(sorter.getSortTimeNanos(), Matchers.greaterThan(prevSortTime));
        prevSortTime = sorter.getSortTimeNanos();
        sorter.spill();// no sort needed

        Assert.assertEquals(sorter.getSortTimeNanos(), prevSortTime);
        sorter.insertRecord(null, 0, 0, 0);
        UnsafeSorterIterator iter = sorter.getSortedIterator();
        Assert.assertThat(sorter.getSortTimeNanos(), Matchers.greaterThan(prevSortTime));
    }

    @Test
    public void spillingOccursInResponseToMemoryPressure() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        // This should be enough records to completely fill up a data page:
        final int numRecords = ((int) ((pageSizeBytes) / (4 + 4)));
        for (int i = 0; i < numRecords; i++) {
            UnsafeExternalSorterSuite.insertNumber(sorter, (numRecords - i));
        }
        Assert.assertEquals(1, sorter.getNumberOfAllocatedPages());
        UnsafeExternalSorterSuite.memoryManager.markExecutionAsOutOfMemoryOnce();
        // The insertion of this record should trigger a spill:
        UnsafeExternalSorterSuite.insertNumber(sorter, 0);
        // Ensure that spill files were created
        // assertThat(tempDir.listFiles().length, greaterThanOrEqualTo(1));
        // Read back the sorted data:
        UnsafeSorterIterator iter = sorter.getSortedIterator();
        int i = 0;
        while (iter.hasNext()) {
            iter.loadNext();
            Assert.assertEquals(i, iter.getKeyPrefix());
            Assert.assertEquals(4, iter.getRecordLength());
            Assert.assertEquals(i, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
            i++;
        } 
        Assert.assertEquals((numRecords + 1), i);
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void testFillingPage() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        byte[] record = new byte[16];
        while ((sorter.getNumberOfAllocatedPages()) < 2) {
            sorter.insertRecord(record, BYTE_ARRAY_OFFSET, record.length, 0);
        } 
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void sortingRecordsThatExceedPageSize() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        final int[] largeRecord = new int[((int) (pageSizeBytes)) + 16];
        Arrays.fill(largeRecord, 456);
        final int[] smallRecord = new int[100];
        Arrays.fill(smallRecord, 123);
        UnsafeExternalSorterSuite.insertRecord(sorter, largeRecord, 456);
        sorter.spill();
        UnsafeExternalSorterSuite.insertRecord(sorter, smallRecord, 123);
        sorter.spill();
        UnsafeExternalSorterSuite.insertRecord(sorter, smallRecord, 123);
        UnsafeExternalSorterSuite.insertRecord(sorter, largeRecord, 456);
        UnsafeSorterIterator iter = sorter.getSortedIterator();
        // Small record
        Assert.assertTrue(iter.hasNext());
        iter.loadNext();
        Assert.assertEquals(123, iter.getKeyPrefix());
        Assert.assertEquals(((smallRecord.length) * 4), iter.getRecordLength());
        Assert.assertEquals(123, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
        // Small record
        Assert.assertTrue(iter.hasNext());
        iter.loadNext();
        Assert.assertEquals(123, iter.getKeyPrefix());
        Assert.assertEquals(((smallRecord.length) * 4), iter.getRecordLength());
        Assert.assertEquals(123, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
        // Large record
        Assert.assertTrue(iter.hasNext());
        iter.loadNext();
        Assert.assertEquals(456, iter.getKeyPrefix());
        Assert.assertEquals(((largeRecord.length) * 4), iter.getRecordLength());
        Assert.assertEquals(456, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
        // Large record
        Assert.assertTrue(iter.hasNext());
        iter.loadNext();
        Assert.assertEquals(456, iter.getKeyPrefix());
        Assert.assertEquals(((largeRecord.length) * 4), iter.getRecordLength());
        Assert.assertEquals(456, Platform.getInt(iter.getBaseObject(), iter.getBaseOffset()));
        Assert.assertFalse(iter.hasNext());
        // sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void forcedSpillingWithReadIterator() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        long[] record = new long[100];
        int recordSize = (record.length) * 8;
        int n = (((int) (pageSizeBytes)) / recordSize) * 3;
        for (int i = 0; i < n; i++) {
            record[0] = ((long) (i));
            sorter.insertRecord(record, LONG_ARRAY_OFFSET, recordSize, 0);
        }
        Assert.assertTrue(((sorter.getNumberOfAllocatedPages()) >= 2));
        UnsafeExternalSorter.SpillableIterator iter = ((UnsafeExternalSorter.SpillableIterator) (sorter.getSortedIterator()));
        int lastv = 0;
        for (int i = 0; i < (n / 3); i++) {
            iter.hasNext();
            iter.loadNext();
            Assert.assertTrue(((Platform.getLong(iter.getBaseObject(), iter.getBaseOffset())) == i));
            lastv = i;
        }
        Assert.assertTrue(((iter.spill()) > 0));
        Assert.assertEquals(0, iter.spill());
        Assert.assertTrue(((Platform.getLong(iter.getBaseObject(), iter.getBaseOffset())) == lastv));
        for (int i = n / 3; i < n; i++) {
            iter.hasNext();
            iter.loadNext();
            Assert.assertEquals(i, Platform.getLong(iter.getBaseObject(), iter.getBaseOffset()));
        }
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void forcedSpillingWithNotReadIterator() throws Exception {
        final UnsafeExternalSorter sorter = newSorter();
        long[] record = new long[100];
        int recordSize = (record.length) * 8;
        int n = (((int) (pageSizeBytes)) / recordSize) * 3;
        for (int i = 0; i < n; i++) {
            record[0] = ((long) (i));
            sorter.insertRecord(record, LONG_ARRAY_OFFSET, recordSize, 0);
        }
        Assert.assertTrue(((sorter.getNumberOfAllocatedPages()) >= 2));
        UnsafeExternalSorter.SpillableIterator iter = ((UnsafeExternalSorter.SpillableIterator) (sorter.getSortedIterator()));
        Assert.assertTrue(((iter.spill()) > 0));
        Assert.assertEquals(0, iter.spill());
        for (int i = 0; i < n; i++) {
            iter.hasNext();
            iter.loadNext();
            Assert.assertEquals(i, Platform.getLong(iter.getBaseObject(), iter.getBaseOffset()));
        }
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void forcedSpillingWithoutComparator() throws Exception {
        final UnsafeExternalSorter sorter = /* initialSize */
        UnsafeExternalSorter.create(UnsafeExternalSorterSuite.DATA_NODE_MEMORY_MANAGER, UnsafeExternalSorterSuite.blockManager, UnsafeExternalSorterSuite.serializerManager, null, null, 1024, pageSizeBytes, shouldUseRadixSort(), true);
        long[] record = new long[100];
        int recordSize = (record.length) * 8;
        int n = (((int) (pageSizeBytes)) / recordSize) * 3;
        int batch = n / 4;
        for (int i = 0; i < n; i++) {
            record[0] = ((long) (i));
            sorter.insertRecord(record, LONG_ARRAY_OFFSET, recordSize, 0);
            if ((i % batch) == (batch - 1)) {
                sorter.spill();
            }
        }
        UnsafeSorterIterator iter = sorter.getIterator();
        for (int i = 0; i < n; i++) {
            iter.hasNext();
            iter.loadNext();
            Assert.assertEquals(i, Platform.getLong(iter.getBaseObject(), iter.getBaseOffset()));
        }
        sorter.cleanupResources();
        assertSpillFilesWereCleanedUp();
    }

    @Test
    public void testPeakMemoryUsed() throws Exception {
        final long recordLengthBytes = 8;
        final long pageSizeBytes = 256;
        final long numRecordsPerPage = pageSizeBytes / recordLengthBytes;
        final UnsafeExternalSorter sorter = UnsafeExternalSorter.create(UnsafeExternalSorterSuite.DATA_NODE_MEMORY_MANAGER, UnsafeExternalSorterSuite.blockManager, UnsafeExternalSorterSuite.serializerManager, recordComparator, prefixComparator, 1024, pageSizeBytes, shouldUseRadixSort(), true);
        // Peak memory should be monotonically increasing. More specifically, every time
        // we allocate a new page it should increase by exactly the size of the page.
        long previousPeakMemory = sorter.getPeakMemoryUsedBytes();
        long newPeakMemory;
        try {
            for (int i = 0; i < (numRecordsPerPage * 10); i++) {
                UnsafeExternalSorterSuite.insertNumber(sorter, i);
                newPeakMemory = sorter.getPeakMemoryUsedBytes();
                if ((i % numRecordsPerPage) == 0) {
                    // We allocated a new page for this record, so peak memory should change
                    Assert.assertEquals((previousPeakMemory + pageSizeBytes), newPeakMemory);
                } else {
                    Assert.assertEquals(previousPeakMemory, newPeakMemory);
                }
                previousPeakMemory = newPeakMemory;
            }
            // Spilling should not change peak memory
            sorter.spill();
            newPeakMemory = sorter.getPeakMemoryUsedBytes();
            Assert.assertEquals(previousPeakMemory, newPeakMemory);
            for (int i = 0; i < numRecordsPerPage; i++) {
                UnsafeExternalSorterSuite.insertNumber(sorter, i);
            }
            newPeakMemory = sorter.getPeakMemoryUsedBytes();
            Assert.assertEquals(previousPeakMemory, newPeakMemory);
        } finally {
            sorter.cleanupResources();
            assertSpillFilesWereCleanedUp();
        }
    }
}

