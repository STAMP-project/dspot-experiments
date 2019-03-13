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
package org.apache.hadoop.hbase.regionserver.compactions;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.CompactedHFilesDischarger;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.regionserver.RegionServerServices;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, RegionServerTests.class })
public class TestCompactedHFilesDischarger {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompactedHFilesDischarger.class);

    private final HBaseTestingUtility testUtil = new HBaseTestingUtility();

    private HRegion region;

    private static final byte[] fam = Bytes.toBytes("cf_1");

    private static final byte[] qual1 = Bytes.toBytes("qf_1");

    private static final byte[] val = Bytes.toBytes("val");

    private static CountDownLatch latch = new CountDownLatch(3);

    private static AtomicInteger counter = new AtomicInteger(0);

    private static AtomicInteger scanCompletedCounter = new AtomicInteger(0);

    private RegionServerServices rss;

    @Test
    public void testCompactedHFilesCleaner() throws Exception {
        // Create the cleaner object
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(1000, ((Stoppable) (null)), rss, false);
        // Add some data to the region and do some flushes
        for (int i = 1; i < 10; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 11; i < 20; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 21; i < 30; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        HStore store = region.getStore(TestCompactedHFilesDischarger.fam);
        Assert.assertEquals(3, store.getStorefilesCount());
        Collection<HStoreFile> storefiles = store.getStorefiles();
        Collection<HStoreFile> compactedfiles = store.getStoreEngine().getStoreFileManager().getCompactedfiles();
        // None of the files should be in compacted state.
        for (HStoreFile file : storefiles) {
            Assert.assertFalse(file.isCompactedAway());
        }
        // Try to run the cleaner without compaction. there should not be any change
        cleaner.chore();
        storefiles = store.getStorefiles();
        // None of the files should be in compacted state.
        for (HStoreFile file : storefiles) {
            Assert.assertFalse(file.isCompactedAway());
        }
        // now do some compaction
        region.compact(true);
        // Still the flushed files should be present until the cleaner runs. But the state of it should
        // be in COMPACTED state
        Assert.assertEquals(1, store.getStorefilesCount());
        Assert.assertEquals(3, getStoreEngine().getStoreFileManager().getCompactedfiles().size());
        // Run the cleaner
        cleaner.chore();
        Assert.assertEquals(1, store.getStorefilesCount());
        storefiles = store.getStorefiles();
        for (HStoreFile file : storefiles) {
            // Should not be in compacted state
            Assert.assertFalse(file.isCompactedAway());
        }
        compactedfiles = getStoreEngine().getStoreFileManager().getCompactedfiles();
        Assert.assertTrue(compactedfiles.isEmpty());
    }

    @Test
    public void testCleanerWithParallelScannersAfterCompaction() throws Exception {
        // Create the cleaner object
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(1000, ((Stoppable) (null)), rss, false);
        // Add some data to the region and do some flushes
        for (int i = 1; i < 10; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 11; i < 20; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 21; i < 30; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        HStore store = region.getStore(TestCompactedHFilesDischarger.fam);
        Assert.assertEquals(3, store.getStorefilesCount());
        Collection<HStoreFile> storefiles = store.getStorefiles();
        Collection<HStoreFile> compactedfiles = store.getStoreEngine().getStoreFileManager().getCompactedfiles();
        // None of the files should be in compacted state.
        for (HStoreFile file : storefiles) {
            Assert.assertFalse(file.isCompactedAway());
        }
        // Do compaction
        region.compact(true);
        startScannerThreads();
        storefiles = store.getStorefiles();
        int usedReaderCount = 0;
        int unusedReaderCount = 0;
        for (HStoreFile file : storefiles) {
            if ((getRefCount()) == 3) {
                usedReaderCount++;
            }
        }
        compactedfiles = getStoreEngine().getStoreFileManager().getCompactedfiles();
        for (HStoreFile file : compactedfiles) {
            Assert.assertEquals("Refcount should be 3", 0, getRefCount());
            unusedReaderCount++;
        }
        // Though there are files we are not using them for reads
        Assert.assertEquals("unused reader count should be 3", 3, unusedReaderCount);
        Assert.assertEquals("used reader count should be 1", 1, usedReaderCount);
        // now run the cleaner
        cleaner.chore();
        countDown();
        Assert.assertEquals(1, store.getStorefilesCount());
        storefiles = store.getStorefiles();
        for (HStoreFile file : storefiles) {
            // Should not be in compacted state
            Assert.assertFalse(file.isCompactedAway());
        }
        compactedfiles = getStoreEngine().getStoreFileManager().getCompactedfiles();
        Assert.assertTrue(compactedfiles.isEmpty());
    }

    @Test
    public void testCleanerWithParallelScanners() throws Exception {
        // Create the cleaner object
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(1000, ((Stoppable) (null)), rss, false);
        // Add some data to the region and do some flushes
        for (int i = 1; i < 10; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 11; i < 20; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        for (int i = 21; i < 30; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            p.addColumn(TestCompactedHFilesDischarger.fam, TestCompactedHFilesDischarger.qual1, TestCompactedHFilesDischarger.val);
            region.put(p);
        }
        // flush them
        region.flush(true);
        HStore store = region.getStore(TestCompactedHFilesDischarger.fam);
        Assert.assertEquals(3, store.getStorefilesCount());
        Collection<HStoreFile> storefiles = store.getStorefiles();
        Collection<HStoreFile> compactedfiles = store.getStoreEngine().getStoreFileManager().getCompactedfiles();
        // None of the files should be in compacted state.
        for (HStoreFile file : storefiles) {
            Assert.assertFalse(file.isCompactedAway());
        }
        startScannerThreads();
        // Do compaction
        region.compact(true);
        storefiles = store.getStorefiles();
        int usedReaderCount = 0;
        int unusedReaderCount = 0;
        for (HStoreFile file : storefiles) {
            if ((file.getRefCount()) == 0) {
                unusedReaderCount++;
            }
        }
        compactedfiles = store.getStoreEngine().getStoreFileManager().getCompactedfiles();
        for (HStoreFile file : compactedfiles) {
            Assert.assertEquals("Refcount should be 3", 3, getRefCount());
            usedReaderCount++;
        }
        // The newly compacted file will not be used by any scanner
        Assert.assertEquals("unused reader count should be 1", 1, unusedReaderCount);
        Assert.assertEquals("used reader count should be 3", 3, usedReaderCount);
        // now run the cleaner
        cleaner.chore();
        countDown();
        // No change in the number of store files as none of the compacted files could be cleaned up
        Assert.assertEquals(1, store.getStorefilesCount());
        Assert.assertEquals(3, getStoreEngine().getStoreFileManager().getCompactedfiles().size());
        while ((TestCompactedHFilesDischarger.scanCompletedCounter.get()) != 3) {
            Thread.sleep(100);
        } 
        // reset
        TestCompactedHFilesDischarger.latch = new CountDownLatch(3);
        TestCompactedHFilesDischarger.scanCompletedCounter.set(0);
        TestCompactedHFilesDischarger.counter.set(0);
        // Try creating a new scanner and it should use only the new file created after compaction
        startScannerThreads();
        storefiles = store.getStorefiles();
        usedReaderCount = 0;
        unusedReaderCount = 0;
        for (HStoreFile file : storefiles) {
            if ((file.getRefCount()) == 3) {
                usedReaderCount++;
            }
        }
        compactedfiles = getStoreEngine().getStoreFileManager().getCompactedfiles();
        for (HStoreFile file : compactedfiles) {
            Assert.assertEquals("Refcount should be 0", 0, file.getRefCount());
            unusedReaderCount++;
        }
        // Though there are files we are not using them for reads
        Assert.assertEquals("unused reader count should be 3", 3, unusedReaderCount);
        Assert.assertEquals("used reader count should be 1", 1, usedReaderCount);
        countDown();
        while ((TestCompactedHFilesDischarger.scanCompletedCounter.get()) != 3) {
            Thread.sleep(100);
        } 
        // Run the cleaner again
        cleaner.chore();
        // Now the cleaner should be able to clear it up because there are no active readers
        Assert.assertEquals(1, store.getStorefilesCount());
        storefiles = store.getStorefiles();
        for (HStoreFile file : storefiles) {
            // Should not be in compacted state
            Assert.assertFalse(file.isCompactedAway());
        }
        compactedfiles = getStoreEngine().getStoreFileManager().getCompactedfiles();
        Assert.assertTrue(compactedfiles.isEmpty());
    }

    @Test
    public void testStoreFileMissing() throws Exception {
        // Write 3 records and create 3 store files.
        write("row1");
        region.flush(true);
        write("row2");
        region.flush(true);
        write("row3");
        region.flush(true);
        Scan scan = new Scan();
        scan.setCaching(1);
        RegionScanner scanner = region.getScanner(scan);
        List<Cell> res = new ArrayList<Cell>();
        // Read first item
        scanner.next(res);
        Assert.assertEquals("row1", Bytes.toString(CellUtil.cloneRow(res.get(0))));
        res.clear();
        // Create a new file in between scan nexts
        write("row4");
        region.flush(true);
        // Compact the table
        region.compact(true);
        // Create the cleaner object
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(1000, ((Stoppable) (null)), rss, false);
        cleaner.chore();
        // This issues scan next
        scanner.next(res);
        Assert.assertEquals("row2", Bytes.toString(CellUtil.cloneRow(res.get(0))));
        scanner.close();
    }

    private static class ScanThread extends Thread {
        private final HRegion region;

        public ScanThread(HRegion region) {
            this.region = region;
        }

        @Override
        public void run() {
            try {
                initiateScan(region);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void initiateScan(HRegion region) throws IOException {
            Scan scan = new Scan();
            scan.setCaching(1);
            RegionScanner resScanner = null;
            try {
                resScanner = region.getScanner(scan);
                List<Cell> results = new ArrayList<>();
                boolean next = resScanner.next(results);
                try {
                    TestCompactedHFilesDischarger.counter.incrementAndGet();
                    TestCompactedHFilesDischarger.latch.await();
                } catch (InterruptedException e) {
                }
                while (next) {
                    next = resScanner.next(results);
                } 
            } finally {
                TestCompactedHFilesDischarger.scanCompletedCounter.incrementAndGet();
                resScanner.close();
            }
        }
    }
}

