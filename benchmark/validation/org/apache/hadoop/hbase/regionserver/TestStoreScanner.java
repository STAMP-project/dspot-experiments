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


import KeyValue.Type;
import KeyValue.Type.Delete;
import KeyValue.Type.DeleteColumn;
import KeyValue.Type.DeleteFamily;
import KeyValue.Type.Put;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.ColumnCountGetFilter;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdge;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManagerTestHelper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ScanType.COMPACT_DROP_DELETES;
import static ScanType.COMPACT_RETAIN_DELETES;


// Can't be small as it plays with EnvironmentEdgeManager
@Category({ RegionServerTests.class, MediumTests.class })
public class TestStoreScanner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestStoreScanner.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestStoreScanner.class);

    @Rule
    public TestName name = new TestName();

    private static final String CF_STR = "cf";

    private static final byte[] CF = Bytes.toBytes(TestStoreScanner.CF_STR);

    static Configuration CONF = HBaseConfiguration.create();

    private ScanInfo scanInfo = new ScanInfo(TestStoreScanner.CONF, TestStoreScanner.CF, 0, Integer.MAX_VALUE, Long.MAX_VALUE, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, CellComparator.getInstance(), false);

    /**
     * From here on down, we have a bunch of defines and specific CELL_GRID of Cells. The
     * CELL_GRID then has a Scanner that can fake out 'block' transitions. All this elaborate
     * setup is for tests that ensure we don't overread, and that the
     * {@link StoreScanner#optimize(org.apache.hadoop.hbase.regionserver.querymatcher.ScanQueryMatcher.MatchCode,
     * Cell)} is not overly enthusiastic.
     */
    private static final byte[] ZERO = new byte[]{ '0' };

    private static final byte[] ZERO_POINT_ZERO = new byte[]{ '0', '.', '0' };

    private static final byte[] ONE = new byte[]{ '1' };

    private static final byte[] TWO = new byte[]{ '2' };

    private static final byte[] TWO_POINT_TWO = new byte[]{ '2', '.', '2' };

    private static final byte[] THREE = new byte[]{ '3' };

    private static final byte[] FOUR = new byte[]{ '4' };

    private static final byte[] FIVE = new byte[]{ '5' };

    private static final byte[] VALUE = new byte[]{ 'v' };

    private static final int CELL_GRID_BLOCK2_BOUNDARY = 4;

    private static final int CELL_GRID_BLOCK3_BOUNDARY = 11;

    private static final int CELL_GRID_BLOCK4_BOUNDARY = 15;

    private static final int CELL_GRID_BLOCK5_BOUNDARY = 19;

    /**
     * Five rows by four columns distinguished by column qualifier (column qualifier is one of the
     * four rows... ONE, TWO, etc.). Exceptions are a weird row after TWO; it is TWO_POINT_TWO.
     * And then row FOUR has five columns finishing w/ row FIVE having a single column.
     * We will use this to test scan does the right thing as it
     * we do Gets, StoreScanner#optimize, and what we do on (faked) block boundaries.
     */
    private static final Cell[] CELL_GRID = new Cell[]{ CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.THREE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.FOUR, 1L, Put.getCode(), TestStoreScanner.VALUE), // Offset 4 CELL_GRID_BLOCK2_BOUNDARY
    CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.THREE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.FOUR, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO_POINT_TWO, TestStoreScanner.CF, TestStoreScanner.ZERO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO_POINT_TWO, TestStoreScanner.CF, TestStoreScanner.ZERO_POINT_ZERO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO_POINT_TWO, TestStoreScanner.CF, TestStoreScanner.FIVE, 1L, Put.getCode(), TestStoreScanner.VALUE), // Offset 11! CELL_GRID_BLOCK3_BOUNDARY
    CellUtil.createCell(TestStoreScanner.THREE, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.THREE, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.THREE, TestStoreScanner.CF, TestStoreScanner.THREE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.THREE, TestStoreScanner.CF, TestStoreScanner.FOUR, 1L, Put.getCode(), TestStoreScanner.VALUE), // Offset 15 CELL_GRID_BLOCK4_BOUNDARY
    CellUtil.createCell(TestStoreScanner.FOUR, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.FOUR, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.FOUR, TestStoreScanner.CF, TestStoreScanner.THREE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.FOUR, TestStoreScanner.CF, TestStoreScanner.FOUR, 1L, Put.getCode(), TestStoreScanner.VALUE), // Offset 19 CELL_GRID_BLOCK5_BOUNDARY
    CellUtil.createCell(TestStoreScanner.FOUR, TestStoreScanner.CF, TestStoreScanner.FIVE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.FIVE, TestStoreScanner.CF, TestStoreScanner.ZERO, 1L, Put.getCode(), TestStoreScanner.VALUE) };

    private static class KeyValueHeapWithCount extends KeyValueHeap {
        final AtomicInteger count;

        public KeyValueHeapWithCount(List<? extends KeyValueScanner> scanners, CellComparator comparator, AtomicInteger count) throws IOException {
            super(scanners, comparator);
            this.count = count;
        }

        @Override
        public Cell peek() {
            this.count.incrementAndGet();
            return super.peek();
        }
    }

    /**
     * A StoreScanner for our CELL_GRID above. Fakes the block transitions. Does counts of
     * calls to optimize and counts of when optimize actually did an optimize.
     */
    private static class CellGridStoreScanner extends StoreScanner {
        // Count of how often optimize is called and of how often it does an optimize.
        AtomicInteger count;

        final AtomicInteger optimization = new AtomicInteger(0);

        CellGridStoreScanner(final Scan scan, ScanInfo scanInfo) throws IOException {
            super(scan, scanInfo, scan.getFamilyMap().get(TestStoreScanner.CF), Arrays.<KeyValueScanner>asList(new KeyValueScanner[]{ new KeyValueScanFixture(CellComparator.getInstance(), TestStoreScanner.CELL_GRID) }));
        }

        @Override
        protected void resetKVHeap(List<? extends KeyValueScanner> scanners, CellComparator comparator) throws IOException {
            if ((count) == null) {
                count = new AtomicInteger(0);
            }
            heap = newKVHeap(scanners, comparator);
        }

        @Override
        protected KeyValueHeap newKVHeap(List<? extends KeyValueScanner> scanners, CellComparator comparator) throws IOException {
            return new TestStoreScanner.KeyValueHeapWithCount(scanners, comparator, count);
        }

        @Override
        protected boolean trySkipToNextRow(Cell cell) throws IOException {
            boolean optimized = super.trySkipToNextRow(cell);
            TestStoreScanner.LOG.info(((((("Cell=" + cell) + ", nextIndex=") + (CellUtil.toString(getNextIndexedKey(), false))) + ", optimized=") + optimized));
            if (optimized) {
                optimization.incrementAndGet();
            }
            return optimized;
        }

        @Override
        protected boolean trySkipToNextColumn(Cell cell) throws IOException {
            boolean optimized = super.trySkipToNextColumn(cell);
            TestStoreScanner.LOG.info(((((("Cell=" + cell) + ", nextIndex=") + (CellUtil.toString(getNextIndexedKey(), false))) + ", optimized=") + optimized));
            if (optimized) {
                optimization.incrementAndGet();
            }
            return optimized;
        }

        @Override
        public Cell getNextIndexedKey() {
            // Fake block boundaries by having index of next block change as we go through scan.
            return (count.get()) > (TestStoreScanner.CELL_GRID_BLOCK4_BOUNDARY) ? PrivateCellUtil.createFirstOnRow(TestStoreScanner.CELL_GRID[TestStoreScanner.CELL_GRID_BLOCK5_BOUNDARY]) : (count.get()) > (TestStoreScanner.CELL_GRID_BLOCK3_BOUNDARY) ? PrivateCellUtil.createFirstOnRow(TestStoreScanner.CELL_GRID[TestStoreScanner.CELL_GRID_BLOCK4_BOUNDARY]) : (count.get()) > (TestStoreScanner.CELL_GRID_BLOCK2_BOUNDARY) ? PrivateCellUtil.createFirstOnRow(TestStoreScanner.CELL_GRID[TestStoreScanner.CELL_GRID_BLOCK3_BOUNDARY]) : PrivateCellUtil.createFirstOnRow(TestStoreScanner.CELL_GRID[TestStoreScanner.CELL_GRID_BLOCK2_BOUNDARY]);
        }
    }

    private static final int CELL_WITH_VERSIONS_BLOCK2_BOUNDARY = 4;

    private static final Cell[] CELL_WITH_VERSIONS = new Cell[]{ CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.ONE, 2L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.TWO, 2L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.ONE, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE), // Offset 4 CELL_WITH_VERSIONS_BLOCK2_BOUNDARY
    CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.ONE, 1L, Put.getCode(), TestStoreScanner.VALUE), CellUtil.createCell(TestStoreScanner.TWO, TestStoreScanner.CF, TestStoreScanner.TWO, 1L, Put.getCode(), TestStoreScanner.VALUE) };

    private static class CellWithVersionsStoreScanner extends StoreScanner {
        // Count of how often optimize is called and of how often it does an optimize.
        final AtomicInteger optimization = new AtomicInteger(0);

        CellWithVersionsStoreScanner(final Scan scan, ScanInfo scanInfo) throws IOException {
            super(scan, scanInfo, scan.getFamilyMap().get(TestStoreScanner.CF), Arrays.<KeyValueScanner>asList(new KeyValueScanner[]{ new KeyValueScanFixture(CellComparator.getInstance(), TestStoreScanner.CELL_WITH_VERSIONS) }));
        }

        @Override
        protected boolean trySkipToNextColumn(Cell cell) throws IOException {
            boolean optimized = super.trySkipToNextColumn(cell);
            TestStoreScanner.LOG.info(((((("Cell=" + cell) + ", nextIndex=") + (CellUtil.toString(getNextIndexedKey(), false))) + ", optimized=") + optimized));
            if (optimized) {
                optimization.incrementAndGet();
            }
            return optimized;
        }

        @Override
        public Cell getNextIndexedKey() {
            // Fake block boundaries by having index of next block change as we go through scan.
            return PrivateCellUtil.createFirstOnRow(TestStoreScanner.CELL_WITH_VERSIONS[TestStoreScanner.CELL_WITH_VERSIONS_BLOCK2_BOUNDARY]);
        }
    }

    private static class CellWithVersionsNoOptimizeStoreScanner extends StoreScanner {
        // Count of how often optimize is called and of how often it does an optimize.
        final AtomicInteger optimization = new AtomicInteger(0);

        CellWithVersionsNoOptimizeStoreScanner(Scan scan, ScanInfo scanInfo) throws IOException {
            super(scan, scanInfo, scan.getFamilyMap().get(TestStoreScanner.CF), Arrays.<KeyValueScanner>asList(new KeyValueScanner[]{ new KeyValueScanFixture(CellComparator.getInstance(), TestStoreScanner.CELL_WITH_VERSIONS) }));
        }

        @Override
        protected boolean trySkipToNextColumn(Cell cell) throws IOException {
            boolean optimized = super.trySkipToNextColumn(cell);
            TestStoreScanner.LOG.info(((((("Cell=" + cell) + ", nextIndex=") + (CellUtil.toString(getNextIndexedKey(), false))) + ", optimized=") + optimized));
            if (optimized) {
                optimization.incrementAndGet();
            }
            return optimized;
        }

        @Override
        public Cell getNextIndexedKey() {
            return null;
        }
    }

    @Test
    public void testWithColumnCountGetFilter() throws Exception {
        Get get = new Get(TestStoreScanner.ONE);
        get.readAllVersions();
        get.addFamily(TestStoreScanner.CF);
        get.setFilter(new ColumnCountGetFilter(2));
        try (TestStoreScanner.CellWithVersionsNoOptimizeStoreScanner scannerNoOptimize = new TestStoreScanner.CellWithVersionsNoOptimizeStoreScanner(new Scan(get), this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            while (scannerNoOptimize.next(results)) {
                continue;
            } 
            Assert.assertEquals(2, results.size());
            Assert.assertTrue(CellUtil.matchingColumn(results.get(0), TestStoreScanner.CELL_WITH_VERSIONS[0]));
            Assert.assertTrue(CellUtil.matchingColumn(results.get(1), TestStoreScanner.CELL_WITH_VERSIONS[2]));
            Assert.assertTrue("Optimize should do some optimizations", ((scannerNoOptimize.optimization.get()) == 0));
        }
        get.setFilter(new ColumnCountGetFilter(2));
        try (TestStoreScanner.CellWithVersionsStoreScanner scanner = new TestStoreScanner.CellWithVersionsStoreScanner(new Scan(get), this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            while (scanner.next(results)) {
                continue;
            } 
            Assert.assertEquals(2, results.size());
            Assert.assertTrue(CellUtil.matchingColumn(results.get(0), TestStoreScanner.CELL_WITH_VERSIONS[0]));
            Assert.assertTrue(CellUtil.matchingColumn(results.get(1), TestStoreScanner.CELL_WITH_VERSIONS[2]));
            Assert.assertTrue("Optimize should do some optimizations", ((scanner.optimization.get()) > 0));
        }
    }

    @Test
    public void testFullRowGetDoesNotOverreadWhenRowInsideOneBlock() throws IOException {
        // Do a Get against row two. Row two is inside a block that starts with row TWO but ends with
        // row TWO_POINT_TWO. We should read one block only.
        Get get = new Get(TestStoreScanner.TWO);
        Scan scan = new Scan(get);
        try (TestStoreScanner.CellGridStoreScanner scanner = new TestStoreScanner.CellGridStoreScanner(scan, this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            while (scanner.next(results)) {
                continue;
            } 
            // Should be four results of column 1 (though there are 5 rows in the CELL_GRID -- the
            // TWO_POINT_TWO row does not have a a column ONE.
            Assert.assertEquals(4, results.size());
            // We should have gone the optimize route 5 times totally... an INCLUDE for the four cells
            // in the row plus the DONE on the end.
            Assert.assertEquals(5, scanner.count.get());
            // For a full row Get, there should be no opportunity for scanner optimization.
            Assert.assertEquals(0, scanner.optimization.get());
        }
    }

    @Test
    public void testFullRowSpansBlocks() throws IOException {
        // Do a Get against row FOUR. It spans two blocks.
        Get get = new Get(TestStoreScanner.FOUR);
        Scan scan = new Scan(get);
        try (TestStoreScanner.CellGridStoreScanner scanner = new TestStoreScanner.CellGridStoreScanner(scan, this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            while (scanner.next(results)) {
                continue;
            } 
            // Should be four results of column 1 (though there are 5 rows in the CELL_GRID -- the
            // TWO_POINT_TWO row does not have a a column ONE.
            Assert.assertEquals(5, results.size());
            // We should have gone the optimize route 6 times totally... an INCLUDE for the five cells
            // in the row plus the DONE on the end.
            Assert.assertEquals(6, scanner.count.get());
            // For a full row Get, there should be no opportunity for scanner optimization.
            Assert.assertEquals(0, scanner.optimization.get());
        }
    }

    /**
     * Test optimize in StoreScanner. Test that we skip to the next 'block' when we it makes sense
     * reading the block 'index'.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOptimize() throws IOException {
        Scan scan = new Scan();
        // A scan that just gets the first qualifier on each row of the CELL_GRID
        scan.addColumn(TestStoreScanner.CF, TestStoreScanner.ONE);
        try (TestStoreScanner.CellGridStoreScanner scanner = new TestStoreScanner.CellGridStoreScanner(scan, this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            while (scanner.next(results)) {
                continue;
            } 
            // Should be four results of column 1 (though there are 5 rows in the CELL_GRID -- the
            // TWO_POINT_TWO row does not have a a column ONE.
            Assert.assertEquals(4, results.size());
            for (Cell cell : results) {
                Assert.assertTrue(Bytes.equals(TestStoreScanner.ONE, 0, TestStoreScanner.ONE.length, cell.getQualifierArray(), cell.getQualifierOffset(), cell.getQualifierLength()));
            }
            Assert.assertTrue("Optimize should do some optimizations", ((scanner.optimization.get()) > 0));
        }
    }

    /**
     * Ensure the optimize Scan method in StoreScanner does not get in the way of a Get doing minimum
     * work... seeking to start of block and then SKIPPING until we find the wanted Cell.
     * This 'simple' scenario mimics case of all Cells fitting inside a single HFileBlock.
     * See HBASE-15392. This test is a little cryptic. Takes a bit of staring to figure what it up to.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOptimizeAndGet() throws IOException {
        // First test a Get of two columns in the row R2. Every Get is a Scan. Get columns named
        // R2 and R3.
        Get get = new Get(TestStoreScanner.TWO);
        get.addColumn(TestStoreScanner.CF, TestStoreScanner.TWO);
        get.addColumn(TestStoreScanner.CF, TestStoreScanner.THREE);
        Scan scan = new Scan(get);
        try (TestStoreScanner.CellGridStoreScanner scanner = new TestStoreScanner.CellGridStoreScanner(scan, this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            // For a Get there should be no more next's after the first call.
            Assert.assertEquals(false, scanner.next(results));
            // Should be one result only.
            Assert.assertEquals(2, results.size());
            // And we should have gone through optimize twice only.
            Assert.assertEquals("First qcode is SEEK_NEXT_COL and second INCLUDE_AND_SEEK_NEXT_ROW", 3, scanner.count.get());
        }
    }

    /**
     * Ensure that optimize does not cause the Get to do more seeking than required. Optimize
     * (see HBASE-15392) was causing us to seek all Cells in a block when a Get Scan if the next block
     * index/start key was a different row to the current one. A bug. We'd call next too often
     * because we had to exhaust all Cells in the current row making us load the next block just to
     * discard what we read there. This test is a little cryptic. Takes a bit of staring to figure
     * what it up to.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testOptimizeAndGetWithFakedNextBlockIndexStart() throws IOException {
        // First test a Get of second column in the row R2. Every Get is a Scan. Second column has a
        // qualifier of R2.
        Get get = new Get(TestStoreScanner.THREE);
        get.addColumn(TestStoreScanner.CF, TestStoreScanner.TWO);
        Scan scan = new Scan(get);
        try (TestStoreScanner.CellGridStoreScanner scanner = new TestStoreScanner.CellGridStoreScanner(scan, this.scanInfo)) {
            List<Cell> results = new ArrayList<>();
            // For a Get there should be no more next's after the first call.
            Assert.assertEquals(false, scanner.next(results));
            // Should be one result only.
            Assert.assertEquals(1, results.size());
            // And we should have gone through optimize twice only.
            Assert.assertEquals("First qcode is SEEK_NEXT_COL and second INCLUDE_AND_SEEK_NEXT_ROW", 2, scanner.count.get());
        }
    }

    @Test
    public void testScanTimeRange() throws IOException {
        String r1 = "R1";
        // returns only 1 of these 2 even though same timestamp
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create(r1, TestStoreScanner.CF_STR, "a", 1, Put, "dont-care"), KeyValueTestUtil.create(r1, TestStoreScanner.CF_STR, "a", 2, Put, "dont-care"), KeyValueTestUtil.create(r1, TestStoreScanner.CF_STR, "a", 3, Put, "dont-care"), KeyValueTestUtil.create(r1, TestStoreScanner.CF_STR, "a", 4, Put, "dont-care"), KeyValueTestUtil.create(r1, TestStoreScanner.CF_STR, "a", 5, Put, "dont-care") };
        List<KeyValueScanner> scanners = Arrays.<KeyValueScanner>asList(new KeyValueScanner[]{ new KeyValueScanFixture(CellComparator.getInstance(), kvs) });
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes(r1));
        scanSpec.setTimeRange(0, 6);
        scanSpec.readAllVersions();
        List<Cell> results = null;
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            results = new ArrayList();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(5, results.size());
            Assert.assertEquals(kvs[((kvs.length) - 1)], results.get(0));
        }
        // Scan limited TimeRange
        scanSpec = new Scan().withStartRow(Bytes.toBytes(r1));
        scanSpec.setTimeRange(1, 3);
        scanSpec.readAllVersions();
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            results = new ArrayList();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(2, results.size());
        }
        // Another range.
        scanSpec = new Scan().withStartRow(Bytes.toBytes(r1));
        scanSpec.setTimeRange(5, 10);
        scanSpec.readAllVersions();
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            results = new ArrayList();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
        }
        // See how TimeRange and Versions interact.
        // Another range.
        scanSpec = new Scan().withStartRow(Bytes.toBytes(r1));
        scanSpec.setTimeRange(0, 10);
        scanSpec.readVersions(3);
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            results = new ArrayList();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(3, results.size());
        }
    }

    @Test
    public void testScanSameTimestamp() throws IOException {
        // returns only 1 of these 2 even though same timestamp
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 1, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 1, Put, "dont-care") };
        List<KeyValueScanner> scanners = Arrays.asList(new KeyValueScanner[]{ new KeyValueScanFixture(CellComparator.getInstance(), kvs) });
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes("R1"));
        // this only uses maxVersions (default=1) and TimeRange (default=all)
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[0], results.get(0));
        }
    }

    /* Test test shows exactly how the matcher's return codes confuses the StoreScanner
    and prevent it from doing the right thing.  Seeking once, then nexting twice
    should return R1, then R2, but in this case it doesnt.
    TODO this comment makes no sense above. Appears to do the right thing.
    @throws IOException
     */
    @Test
    public void testWontNextToNext() throws IOException {
        // build the scan file:
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 2, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 1, Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "a", 1, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes("R1"));
        // this only uses maxVersions (default=1) and TimeRange (default=all)
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            scan.next(results);
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[0], results.get(0));
            // should be ok...
            // now scan _next_ again.
            results.clear();
            scan.next(results);
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[2], results.get(0));
            results.clear();
            scan.next(results);
            Assert.assertEquals(0, results.size());
        }
    }

    @Test
    public void testDeleteVersionSameTimestamp() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 1, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 1, Delete, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes("R1"));
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertFalse(scan.next(results));
            Assert.assertEquals(0, results.size());
        }
    }

    /* Test the case where there is a delete row 'in front of' the next row, the scanner
    will move to the next row.
     */
    @Test
    public void testDeletedRowThenGoodRow() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 1, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 1, Delete, "dont-care"), KeyValueTestUtil.create("R2", "cf", "a", 20, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes("R1"));
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(0, results.size());
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[2], results.get(0));
            Assert.assertEquals(false, scan.next(results));
        }
    }

    @Test
    public void testDeleteVersionMaskingMultiplePuts() throws IOException {
        long now = System.currentTimeMillis();
        KeyValue[] kvs1 = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", now, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", now, Delete, "dont-care") };
        KeyValue[] kvs2 = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", (now - 500), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", (now - 100), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", now, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs1, kvs2);
        try (StoreScanner scan = new StoreScanner(new Scan().withStartRow(Bytes.toBytes("R1")), scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            // the two put at ts=now will be masked by the 1 delete, and
            // since the scan default returns 1 version we'll return the newest
            // key, which is kvs[2], now-100.
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs2[1], results.get(0));
        }
    }

    @Test
    public void testDeleteVersionsMixedAndMultipleVersionReturn() throws IOException {
        long now = System.currentTimeMillis();
        KeyValue[] kvs1 = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", now, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", now, Delete, "dont-care") };
        KeyValue[] kvs2 = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", (now - 500), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", (now + 500), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", now, Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "z", now, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs1, kvs2);
        Scan scanSpec = new Scan().withStartRow(Bytes.toBytes("R1")).readVersions(2);
        try (StoreScanner scan = new StoreScanner(scanSpec, scanInfo, getCols("a"), scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(kvs2[1], results.get(0));
            Assert.assertEquals(kvs2[0], results.get(1));
        }
    }

    @Test
    public void testWildCardOneVersionScan() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 2, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "b", 1, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 1, DeleteColumn, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        try (StoreScanner scan = new StoreScanner(new Scan().withStartRow(Bytes.toBytes("R1")), scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(kvs[0], results.get(0));
            Assert.assertEquals(kvs[1], results.get(1));
        }
    }

    @Test
    public void testWildCardScannerUnderDeletes() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 2, Put, "dont-care")// inc
        , // orphaned delete column.
        KeyValueTestUtil.create("R1", "cf", "a", 1, DeleteColumn, "dont-care"), // column b
        KeyValueTestUtil.create("R1", "cf", "b", 2, Put, "dont-care")// inc
        , KeyValueTestUtil.create("R1", "cf", "b", 1, Put, "dont-care")// inc
        , // column c
        KeyValueTestUtil.create("R1", "cf", "c", 10, Delete, "dont-care"), KeyValueTestUtil.create("R1", "cf", "c", 10, Put, "dont-care")// no
        , KeyValueTestUtil.create("R1", "cf", "c", 9, Put, "dont-care")// inc
        , // column d
        KeyValueTestUtil.create("R1", "cf", "d", 11, Put, "dont-care")// inc
        , KeyValueTestUtil.create("R1", "cf", "d", 10, DeleteColumn, "dont-care"), KeyValueTestUtil.create("R1", "cf", "d", 9, Put, "dont-care")// no
        , KeyValueTestUtil.create("R1", "cf", "d", 8, Put, "dont-care")// no
         };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        try (StoreScanner scan = new StoreScanner(new Scan().readVersions(2), scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(5, results.size());
            Assert.assertEquals(kvs[0], results.get(0));
            Assert.assertEquals(kvs[2], results.get(1));
            Assert.assertEquals(kvs[3], results.get(2));
            Assert.assertEquals(kvs[6], results.get(3));
            Assert.assertEquals(kvs[7], results.get(4));
        }
    }

    @Test
    public void testDeleteFamily() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 100, DeleteFamily, "dont-care"), KeyValueTestUtil.create("R1", "cf", "b", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "c", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "d", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "e", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "e", 11, DeleteColumn, "dont-care"), KeyValueTestUtil.create("R1", "cf", "f", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "g", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "g", 11, Delete, "dont-care"), KeyValueTestUtil.create("R1", "cf", "h", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "i", 11, Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "a", 11, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        try (StoreScanner scan = new StoreScanner(new Scan().readAllVersions(), scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(0, results.size());
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[((kvs.length) - 1)], results.get(0));
            Assert.assertEquals(false, scan.next(results));
        }
    }

    @Test
    public void testDeleteColumn() throws IOException {
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 10, DeleteColumn, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 9, Delete, "dont-care"), KeyValueTestUtil.create("R1", "cf", "a", 8, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "b", 5, Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        try (StoreScanner scan = new StoreScanner(new Scan(), scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[3], results.get(0));
        }
    }

    private static final KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "b", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "c", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "d", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "e", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "f", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "g", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "h", 11, Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "i", 11, Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "a", 11, Put, "dont-care") };

    @Test
    public void testSkipColumn() throws IOException {
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(TestStoreScanner.kvs);
        try (StoreScanner scan = new StoreScanner(new Scan(), scanInfo, getCols("a", "d"), scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(TestStoreScanner.kvs[0], results.get(0));
            Assert.assertEquals(TestStoreScanner.kvs[3], results.get(1));
            results.clear();
            Assert.assertEquals(true, scan.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(TestStoreScanner.kvs[((TestStoreScanner.kvs.length) - 1)], results.get(0));
            results.clear();
            Assert.assertEquals(false, scan.next(results));
        }
    }

    /* Test expiration of KeyValues in combination with a configured TTL for
    a column family (as should be triggered in a major compaction).
     */
    @Test
    public void testWildCardTtlScan() throws IOException {
        long now = System.currentTimeMillis();
        KeyValue[] kvs = new KeyValue[]{ KeyValueTestUtil.create("R1", "cf", "a", (now - 1000), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "b", (now - 10), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "c", (now - 200), Put, "dont-care"), KeyValueTestUtil.create("R1", "cf", "d", (now - 10000), Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "a", now, Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "b", (now - 10), Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "c", (now - 200), Put, "dont-care"), KeyValueTestUtil.create("R2", "cf", "c", (now - 1000), Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        Scan scan = new Scan();
        scan.readVersions(1);
        ScanInfo scanInfo = new ScanInfo(TestStoreScanner.CONF, TestStoreScanner.CF, 0, 1, 500, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, CellComparator.getInstance(), false);
        try (StoreScanner scanner = new StoreScanner(scan, scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scanner.next(results));
            Assert.assertEquals(2, results.size());
            Assert.assertEquals(kvs[1], results.get(0));
            Assert.assertEquals(kvs[2], results.get(1));
            results.clear();
            Assert.assertEquals(true, scanner.next(results));
            Assert.assertEquals(3, results.size());
            Assert.assertEquals(kvs[4], results.get(0));
            Assert.assertEquals(kvs[5], results.get(1));
            Assert.assertEquals(kvs[6], results.get(2));
            results.clear();
            Assert.assertEquals(false, scanner.next(results));
        }
    }

    @Test
    public void testScannerReseekDoesntNPE() throws Exception {
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(TestStoreScanner.kvs);
        try (StoreScanner scan = new StoreScanner(new Scan(), scanInfo, getCols("a", "d"), scanners)) {
            // Previously a updateReaders twice in a row would cause an NPE. In test this would also
            // normally cause an NPE because scan.store is null. So as long as we get through these
            // two calls we are good and the bug was quashed.
            scan.updateReaders(Collections.emptyList(), Collections.emptyList());
            scan.updateReaders(Collections.emptyList(), Collections.emptyList());
            scan.peek();
        }
    }

    /**
     * Ensure that expired delete family markers don't override valid puts
     */
    @Test
    public void testExpiredDeleteFamily() throws Exception {
        long now = System.currentTimeMillis();
        KeyValue[] kvs = new KeyValue[]{ new KeyValue(Bytes.toBytes("R1"), Bytes.toBytes("cf"), null, (now - 1000), Type.DeleteFamily), KeyValueTestUtil.create("R1", "cf", "a", (now - 10), Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        Scan scan = new Scan();
        scan.readVersions(1);
        // scanner with ttl equal to 500
        ScanInfo scanInfo = new ScanInfo(TestStoreScanner.CONF, TestStoreScanner.CF, 0, 1, 500, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, CellComparator.getInstance(), false);
        try (StoreScanner scanner = new StoreScanner(scan, scanInfo, null, scanners)) {
            List<Cell> results = new ArrayList<>();
            Assert.assertEquals(true, scanner.next(results));
            Assert.assertEquals(1, results.size());
            Assert.assertEquals(kvs[1], results.get(0));
            results.clear();
            Assert.assertEquals(false, scanner.next(results));
        }
    }

    @Test
    public void testDeleteMarkerLongevity() throws Exception {
        try {
            final long now = System.currentTimeMillis();
            EnvironmentEdgeManagerTestHelper.injectEdge(new EnvironmentEdge() {
                @Override
                public long currentTime() {
                    return now;
                }
            });
            KeyValue[] kvs = new KeyValue[]{ new KeyValue(Bytes.toBytes("R1"), Bytes.toBytes("cf"), null, (now - 100), Type.DeleteFamily)// live
            , new KeyValue(Bytes.toBytes("R1"), Bytes.toBytes("cf"), null, (now - 1000), Type.DeleteFamily)// expired
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 50), Put, "v3")// live
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 55), Delete, "dontcare")// live
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 55), Put, "deleted-version v2")// deleted
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 60), Put, "v1")// live
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 65), Put, "v0")// max-version reached
            , KeyValueTestUtil.create("R1", "cf", "a", (now - 100), DeleteColumn, "dont-care")// max-version
            , KeyValueTestUtil.create("R1", "cf", "b", (now - 600), DeleteColumn, "dont-care")// expired
            , KeyValueTestUtil.create("R1", "cf", "b", (now - 70), Put, "v2")// live
            , KeyValueTestUtil.create("R1", "cf", "b", (now - 750), Put, "v1")// expired
            , KeyValueTestUtil.create("R1", "cf", "c", (now - 500), Delete, "dontcare")// expired
            , KeyValueTestUtil.create("R1", "cf", "c", (now - 600), Put, "v1")// expired
            , KeyValueTestUtil.create("R1", "cf", "c", (now - 1000), Delete, "dontcare")// expired
            , KeyValueTestUtil.create("R1", "cf", "d", (now - 60), Put, "expired put")// live
            , KeyValueTestUtil.create("R1", "cf", "d", (now - 100), Delete, "not-expired delete")// live
             };
            List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
            ScanInfo scanInfo = /* minVersions */
            /* maxVersions */
            /* ttl */
            /* keepDeletedCells */
            /* block size */
            /* timeToPurgeDeletes */
            new ScanInfo(TestStoreScanner.CONF, Bytes.toBytes("cf"), 0, 2, 500, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 200, CellComparator.getInstance(), false);
            try (StoreScanner scanner = new StoreScanner(scanInfo, OptionalInt.of(2), COMPACT_DROP_DELETES, scanners)) {
                List<Cell> results = new ArrayList<>();
                results = new ArrayList();
                Assert.assertEquals(true, scanner.next(results));
                Assert.assertEquals(kvs[0], results.get(0));
                Assert.assertEquals(kvs[2], results.get(1));
                Assert.assertEquals(kvs[3], results.get(2));
                Assert.assertEquals(kvs[5], results.get(3));
                Assert.assertEquals(kvs[9], results.get(4));
                Assert.assertEquals(kvs[14], results.get(5));
                Assert.assertEquals(kvs[15], results.get(6));
                Assert.assertEquals(7, results.size());
            }
        } finally {
            EnvironmentEdgeManagerTestHelper.reset();
        }
    }

    @Test
    public void testPreadNotEnabledForCompactionStoreScanners() throws Exception {
        long now = System.currentTimeMillis();
        KeyValue[] kvs = new KeyValue[]{ new KeyValue(Bytes.toBytes("R1"), Bytes.toBytes("cf"), null, (now - 1000), Type.DeleteFamily), KeyValueTestUtil.create("R1", "cf", "a", (now - 10), Put, "dont-care") };
        List<KeyValueScanner> scanners = KeyValueScanFixture.scanFixture(kvs);
        ScanInfo scanInfo = new ScanInfo(TestStoreScanner.CONF, TestStoreScanner.CF, 0, 1, 500, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, CellComparator.getInstance(), false);
        try (StoreScanner storeScanner = new StoreScanner(scanInfo, OptionalInt.empty(), COMPACT_RETAIN_DELETES, scanners)) {
            Assert.assertFalse(storeScanner.isScanUsePread());
        }
    }
}

