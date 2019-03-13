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
package org.apache.hadoop.hbase.client;


import CellComparatorImpl.COMPARATOR;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.BlockCacheKey;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.CachedBlock;
import org.apache.hadoop.hbase.regionserver.DelegatingInternalScanner;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.regionserver.ScanType;
import org.apache.hadoop.hbase.regionserver.ScannerContext;
import org.apache.hadoop.hbase.regionserver.Store;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionLifeCycleTracker;
import org.apache.hadoop.hbase.regionserver.compactions.CompactionRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;


@Category({ LargeTests.class, ClientTests.class })
public class TestAvoidCellReferencesIntoShippedBlocks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAvoidCellReferencesIntoShippedBlocks.class);

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    static byte[][] ROWS = new byte[2][];

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] ROW1 = Bytes.toBytes("testRow1");

    private static byte[] ROW2 = Bytes.toBytes("testRow2");

    private static byte[] ROW3 = Bytes.toBytes("testRow3");

    private static byte[] ROW4 = Bytes.toBytes("testRow4");

    private static byte[] ROW5 = Bytes.toBytes("testRow5");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES_1 = new byte[1][0];

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[] QUALIFIER1 = Bytes.toBytes("testQualifier1");

    private static byte[] data = new byte[1000];

    protected static int SLAVES = 1;

    private CountDownLatch latch = new CountDownLatch(1);

    private static CountDownLatch compactReadLatch = new CountDownLatch(1);

    private static AtomicBoolean doScan = new AtomicBoolean(false);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testHBase16372InCompactionWritePath() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create a table with block size as 1024
        final Table table = TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.createTable(tableName, TestAvoidCellReferencesIntoShippedBlocks.FAMILIES_1, 1, 1024, TestAvoidCellReferencesIntoShippedBlocks.CompactorRegionObserver.class.getName());
        try {
            // get the block cache and region
            RegionLocator locator = TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.getConnection().getRegionLocator(tableName);
            String regionName = locator.getAllRegionLocations().get(0).getRegion().getEncodedName();
            HRegion region = ((HRegion) (TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.getRSForFirstRegionInTable(tableName).getRegion(regionName)));
            HStore store = region.getStores().iterator().next();
            CacheConfig cacheConf = store.getCacheConfig();
            cacheConf.setCacheDataOnWrite(true);
            cacheConf.setEvictOnClose(true);
            final BlockCache cache = cacheConf.getBlockCache().get();
            // insert data. 5 Rows are added
            Put put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW1);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            // data was in memstore so don't expect any changes
            region.flush(true);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW1);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW2);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW2);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            // data was in memstore so don't expect any changes
            region.flush(true);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW3);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW3);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW4);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            // data was in memstore so don't expect any changes
            region.flush(true);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW4);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW5);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW5);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            // data was in memstore so don't expect any changes
            region.flush(true);
            // Load cache
            Scan s = new Scan();
            s.setMaxResultSize(1000);
            int count;
            try (ResultScanner scanner = table.getScanner(s)) {
                count = Iterables.size(scanner);
            }
            Assert.assertEquals("Count all the rows ", 6, count);
            // all the cache is loaded
            // trigger a major compaction
            TestAvoidCellReferencesIntoShippedBlocks.ScannerThread scannerThread = new TestAvoidCellReferencesIntoShippedBlocks.ScannerThread(table, cache);
            scannerThread.start();
            region.compact(true);
            s = new Scan();
            s.setMaxResultSize(1000);
            try (ResultScanner scanner = table.getScanner(s)) {
                count = Iterables.size(scanner);
            }
            Assert.assertEquals("Count all the rows ", 6, count);
        } finally {
            table.close();
        }
    }

    private static class ScannerThread extends Thread {
        private final Table table;

        private final BlockCache cache;

        public ScannerThread(Table table, BlockCache cache) {
            this.table = table;
            this.cache = cache;
        }

        @Override
        public void run() {
            Scan s = new Scan().withStartRow(TestAvoidCellReferencesIntoShippedBlocks.ROW4).withStopRow(TestAvoidCellReferencesIntoShippedBlocks.ROW5).setCaching(1);
            try {
                while (!(TestAvoidCellReferencesIntoShippedBlocks.doScan.get())) {
                    try {
                        // Sleep till you start scan
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                    }
                } 
                List<BlockCacheKey> cacheList = new ArrayList<>();
                Iterator<CachedBlock> iterator = cache.iterator();
                // evict all the blocks
                while (iterator.hasNext()) {
                    CachedBlock next = iterator.next();
                    BlockCacheKey cacheKey = new BlockCacheKey(next.getFilename(), next.getOffset());
                    cacheList.add(cacheKey);
                    // evict what ever is available
                    cache.evictBlock(cacheKey);
                } 
                try (ResultScanner scanner = table.getScanner(s)) {
                    while ((scanner.next()) != null) {
                    } 
                }
                TestAvoidCellReferencesIntoShippedBlocks.compactReadLatch.countDown();
            } catch (IOException e) {
            }
        }
    }

    public static class CompactorRegionObserver implements RegionCoprocessor , RegionObserver {
        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public InternalScanner preCompact(ObserverContext<RegionCoprocessorEnvironment> c, Store store, InternalScanner scanner, ScanType scanType, CompactionLifeCycleTracker tracker, CompactionRequest request) throws IOException {
            return new TestAvoidCellReferencesIntoShippedBlocks.CompactorInternalScanner(scanner);
        }
    }

    private static final class CompactorInternalScanner extends DelegatingInternalScanner {
        public CompactorInternalScanner(InternalScanner scanner) {
            super(scanner);
        }

        @Override
        public boolean next(List<Cell> result, ScannerContext scannerContext) throws IOException {
            boolean next = scanner.next(result, scannerContext);
            for (Cell cell : result) {
                if ((COMPARATOR.compareRows(cell, TestAvoidCellReferencesIntoShippedBlocks.ROW2, 0, TestAvoidCellReferencesIntoShippedBlocks.ROW2.length)) == 0) {
                    try {
                        // hold the compaction
                        // set doscan to true
                        TestAvoidCellReferencesIntoShippedBlocks.doScan.compareAndSet(false, true);
                        TestAvoidCellReferencesIntoShippedBlocks.compactReadLatch.await();
                    } catch (InterruptedException e) {
                    }
                }
            }
            return next;
        }
    }

    @Test
    public void testHBASE16372InReadPath() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        // Create a table with block size as 1024
        final Table table = TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.createTable(tableName, TestAvoidCellReferencesIntoShippedBlocks.FAMILIES_1, 1, 1024, null);
        try {
            // get the block cache and region
            RegionLocator locator = TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.getConnection().getRegionLocator(tableName);
            String regionName = locator.getAllRegionLocations().get(0).getRegion().getEncodedName();
            HRegion region = ((HRegion) (TestAvoidCellReferencesIntoShippedBlocks.TEST_UTIL.getRSForFirstRegionInTable(tableName).getRegion(regionName)));
            HStore store = region.getStores().iterator().next();
            CacheConfig cacheConf = store.getCacheConfig();
            cacheConf.setCacheDataOnWrite(true);
            cacheConf.setEvictOnClose(true);
            final BlockCache cache = cacheConf.getBlockCache().get();
            // insert data. 5 Rows are added
            Put put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW1);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW1);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW2);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW2);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW3);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW3);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW4);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW4);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW5);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            put = new Put(TestAvoidCellReferencesIntoShippedBlocks.ROW5);
            put.addColumn(TestAvoidCellReferencesIntoShippedBlocks.FAMILY, TestAvoidCellReferencesIntoShippedBlocks.QUALIFIER1, TestAvoidCellReferencesIntoShippedBlocks.data);
            table.put(put);
            // data was in memstore so don't expect any changes
            region.flush(true);
            // Load cache
            Scan s = new Scan();
            s.setMaxResultSize(1000);
            int count;
            try (ResultScanner scanner = table.getScanner(s)) {
                count = Iterables.size(scanner);
            }
            Assert.assertEquals("Count all the rows ", 6, count);
            // Scan from cache
            s = new Scan();
            // Start a scan from row3
            s.setCaching(1);
            s.withStartRow(TestAvoidCellReferencesIntoShippedBlocks.ROW1);
            // set partial as true so that the scan can send partial columns also
            s.setAllowPartialResults(true);
            s.setMaxResultSize(1000);
            try (ResultScanner scanner = table.getScanner(s)) {
                Thread evictorThread = new Thread() {
                    @Override
                    public void run() {
                        List<BlockCacheKey> cacheList = new ArrayList<>();
                        Iterator<CachedBlock> iterator = cache.iterator();
                        // evict all the blocks
                        while (iterator.hasNext()) {
                            CachedBlock next = iterator.next();
                            BlockCacheKey cacheKey = new BlockCacheKey(next.getFilename(), next.getOffset());
                            cacheList.add(cacheKey);
                            cache.evictBlock(cacheKey);
                        } 
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e1) {
                        }
                        iterator = cache.iterator();
                        int refBlockCount = 0;
                        while (iterator.hasNext()) {
                            iterator.next();
                            refBlockCount++;
                        } 
                        Assert.assertEquals("One block should be there ", 1, refBlockCount);
                        // Rescan to prepopulate the data
                        // cache this row.
                        Scan s1 = new Scan();
                        // This scan will start from ROW1 and it will populate the cache with a
                        // row that is lower than ROW3.
                        s1.withStartRow(TestAvoidCellReferencesIntoShippedBlocks.ROW3);
                        s1.withStopRow(TestAvoidCellReferencesIntoShippedBlocks.ROW5);
                        s1.setCaching(1);
                        ResultScanner scanner;
                        try {
                            scanner = table.getScanner(s1);
                            int count = Iterables.size(scanner);
                            Assert.assertEquals("Count the rows", 2, count);
                            int newBlockRefCount = 0;
                            List<BlockCacheKey> newCacheList = new ArrayList<>();
                            while (true) {
                                newBlockRefCount = 0;
                                newCacheList.clear();
                                iterator = cache.iterator();
                                while (iterator.hasNext()) {
                                    CachedBlock next = iterator.next();
                                    BlockCacheKey cacheKey = new BlockCacheKey(next.getFilename(), next.getOffset());
                                    newCacheList.add(cacheKey);
                                } 
                                for (BlockCacheKey key : cacheList) {
                                    if (newCacheList.contains(key)) {
                                        newBlockRefCount++;
                                    }
                                }
                                if (newBlockRefCount == 6) {
                                    break;
                                }
                            } 
                            latch.countDown();
                        } catch (IOException e) {
                        }
                    }
                };
                count = 0;
                while ((scanner.next()) != null) {
                    count++;
                    if (count == 2) {
                        evictorThread.start();
                        latch.await();
                    }
                } 
            }
            Assert.assertEquals("Count should give all rows ", 10, count);
        } finally {
            table.close();
        }
    }
}

