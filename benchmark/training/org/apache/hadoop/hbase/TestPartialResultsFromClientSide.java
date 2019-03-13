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
package org.apache.hadoop.hbase;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.FirstKeyValueMatchingQualifiersFilter;
import org.apache.hadoop.hbase.filter.RandomRowFilter;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * These tests are focused on testing how partial results appear to a client. Partial results are
 * {@link Result}s that contain only a portion of a row's complete list of cells. Partial results
 * are formed when the server breaches its maximum result size when trying to service a client's RPC
 * request. It is the responsibility of the scanner on the client side to recognize when partial
 * results have been returned and to take action to form the complete results.
 * <p>
 * Unless the flag {@link Scan#setAllowPartialResults(boolean)} has been set to true, the caller of
 * {@link ResultScanner#next()} should never see partial results.
 */
@Category(MediumTests.class)
public class TestPartialResultsFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPartialResultsFromClientSide.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestPartialResultsFromClientSide.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int MINICLUSTER_SIZE = 5;

    private static Table TABLE = null;

    /**
     * Table configuration
     */
    private static TableName TABLE_NAME = TableName.valueOf("testTable");

    private static int NUM_ROWS = 5;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[][] ROWS = HTestConst.makeNAscii(TestPartialResultsFromClientSide.ROW, TestPartialResultsFromClientSide.NUM_ROWS);

    // Should keep this value below 10 to keep generation of expected kv's simple. If above 10 then
    // table/row/cf1/... will be followed by table/row/cf10/... instead of table/row/cf2/... which
    // breaks the simple generation of expected kv's
    private static int NUM_FAMILIES = 10;

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[][] FAMILIES = HTestConst.makeNAscii(TestPartialResultsFromClientSide.FAMILY, TestPartialResultsFromClientSide.NUM_FAMILIES);

    private static int NUM_QUALIFIERS = 10;

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static byte[][] QUALIFIERS = HTestConst.makeNAscii(TestPartialResultsFromClientSide.QUALIFIER, TestPartialResultsFromClientSide.NUM_QUALIFIERS);

    private static int VALUE_SIZE = 1024;

    private static byte[] VALUE = Bytes.createMaxByteArray(TestPartialResultsFromClientSide.VALUE_SIZE);

    private static int NUM_COLS = (TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS);

    // Approximation of how large the heap size of cells in our table. Should be accessed through
    // getCellHeapSize().
    private static long CELL_HEAP_SIZE = -1;

    private static long timeout = 10000;

    @Rule
    public TestName name = new TestName();

    /**
     * Ensure that the expected key values appear in a result returned from a scanner that is
     * combining partial results into complete results
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExpectedValuesOfPartialResults() throws Exception {
        testExpectedValuesOfPartialResults(false);
        testExpectedValuesOfPartialResults(true);
    }

    /**
     * Ensure that we only see Results marked as partial when the allowPartial flag is set
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAllowPartialResults() throws Exception {
        Scan scan = new Scan();
        scan.setAllowPartialResults(true);
        scan.setMaxResultSize(1);
        ResultScanner scanner = TestPartialResultsFromClientSide.TABLE.getScanner(scan);
        Result result = scanner.next();
        Assert.assertTrue((result != null));
        Assert.assertTrue(result.mayHaveMoreCellsInRow());
        Assert.assertTrue(((result.rawCells()) != null));
        Assert.assertTrue(((result.rawCells().length) == 1));
        scanner.close();
        scan.setAllowPartialResults(false);
        scanner = TestPartialResultsFromClientSide.TABLE.getScanner(scan);
        result = scanner.next();
        Assert.assertTrue((result != null));
        Assert.assertTrue((!(result.mayHaveMoreCellsInRow())));
        Assert.assertTrue(((result.rawCells()) != null));
        Assert.assertTrue(((result.rawCells().length) == (TestPartialResultsFromClientSide.NUM_COLS)));
        scanner.close();
    }

    /**
     * Ensure that the results returned from a scanner that retrieves all results in a single RPC call
     * matches the results that are returned from a scanner that must incrementally combine partial
     * results into complete results. A variety of scan configurations can be tested
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEquivalenceOfScanResults() throws Exception {
        Scan oneShotScan = new Scan();
        oneShotScan.setMaxResultSize(Long.MAX_VALUE);
        Scan partialScan = new Scan(oneShotScan);
        partialScan.setMaxResultSize(1);
        testEquivalenceOfScanResults(TestPartialResultsFromClientSide.TABLE, oneShotScan, partialScan);
    }

    /**
     * Order of cells in partial results matches the ordering of cells from complete results
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOrderingOfCellsInPartialResults() throws Exception {
        Scan scan = new Scan();
        for (int col = 1; col <= (TestPartialResultsFromClientSide.NUM_COLS); col++) {
            scan.setMaxResultSize(getResultSizeForNumberOfCells(col));
            testOrderingOfCellsInPartialResults(scan);
            // Test again with a reversed scanner
            scan.setReversed(true);
            testOrderingOfCellsInPartialResults(scan);
        }
    }

    /**
     * Setting the max result size allows us to control how many cells we expect to see on each call
     * to next on the scanner. Test a variety of different sizes for correctness
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testExpectedNumberOfCellsPerPartialResult() throws Exception {
        Scan scan = new Scan();
        testExpectedNumberOfCellsPerPartialResult(scan);
        scan.setReversed(true);
        testExpectedNumberOfCellsPerPartialResult(scan);
    }

    /**
     * Test various combinations of batching and partial results for correctness
     */
    @Test
    public void testPartialResultsAndBatch() throws Exception {
        for (int batch = 1; batch <= ((TestPartialResultsFromClientSide.NUM_COLS) / 4); batch++) {
            for (int cellsPerPartial = 1; cellsPerPartial <= ((TestPartialResultsFromClientSide.NUM_COLS) / 4); cellsPerPartial++) {
                testPartialResultsAndBatch(batch, cellsPerPartial);
            }
        }
    }

    /**
     * Test the method {@link Result#createCompleteResult(Iterable)}
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartialResultsReassembly() throws Exception {
        Scan scan = new Scan();
        testPartialResultsReassembly(scan);
        scan.setReversed(true);
        testPartialResultsReassembly(scan);
    }

    /**
     * When reconstructing the complete result from its partials we ensure that the row of each
     * partial result is the same. If one of the rows differs, an exception is thrown.
     */
    @Test
    public void testExceptionThrownOnMismatchedPartialResults() throws IOException {
        Assert.assertTrue(((TestPartialResultsFromClientSide.NUM_ROWS) >= 2));
        ArrayList<Result> partials = new ArrayList<>();
        Scan scan = new Scan();
        scan.setMaxResultSize(Long.MAX_VALUE);
        ResultScanner scanner = TestPartialResultsFromClientSide.TABLE.getScanner(scan);
        Result r1 = scanner.next();
        partials.add(r1);
        Result r2 = scanner.next();
        partials.add(r2);
        Assert.assertFalse(Bytes.equals(r1.getRow(), r2.getRow()));
        try {
            Result.createCompleteResult(partials);
            Assert.fail(("r1 and r2 are from different rows. It should not be possible to combine them into" + " a single result"));
        } catch (IOException e) {
        }
        scanner.close();
    }

    /**
     * When a scan has a filter where {@link org.apache.hadoop.hbase.filter.Filter#hasFilterRow()} is
     * true, the scanner should not return partial results. The scanner cannot return partial results
     * because the entire row needs to be read for the include/exclude decision to be made
     */
    @Test
    public void testNoPartialResultsWhenRowFilterPresent() throws Exception {
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.setAllowPartialResults(true);
        // If a filter hasFilter() is true then partial results should not be returned else filter
        // application server side would break.
        scan.setFilter(new RandomRowFilter(1.0F));
        ResultScanner scanner = TestPartialResultsFromClientSide.TABLE.getScanner(scan);
        Result r = null;
        while ((r = scanner.next()) != null) {
            Assert.assertFalse(r.mayHaveMoreCellsInRow());
        } 
        scanner.close();
    }

    /**
     * Examine the interaction between the maxResultSize and caching. If the caching limit is reached
     * before the maxResultSize limit, we should not see partial results. On the other hand, if the
     * maxResultSize limit is reached before the caching limit, it is likely that partial results will
     * be seen.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartialResultsAndCaching() throws Exception {
        for (int caching = 1; caching <= (TestPartialResultsFromClientSide.NUM_ROWS); caching++) {
            for (int maxResultRows = 0; maxResultRows <= (TestPartialResultsFromClientSide.NUM_ROWS); maxResultRows++) {
                testPartialResultsAndCaching(maxResultRows, caching);
            }
        }
    }

    @Test
    public void testReadPointAndPartialResults() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        int numRows = 5;
        int numFamilies = 5;
        int numQualifiers = 5;
        byte[][] rows = HTestConst.makeNAscii(Bytes.toBytes("testRow"), numRows);
        byte[][] families = HTestConst.makeNAscii(Bytes.toBytes("testFamily"), numFamilies);
        byte[][] qualifiers = HTestConst.makeNAscii(Bytes.toBytes("testQualifier"), numQualifiers);
        byte[] value = Bytes.createMaxByteArray(100);
        Table tmpTable = TestPartialResultsFromClientSide.createTestTable(tableName, rows, families, qualifiers, value);
        // Open scanner before deletes
        ResultScanner scanner = tmpTable.getScanner(new Scan().setMaxResultSize(1).setAllowPartialResults(true));
        // now the openScanner will also fetch data and will be executed lazily, i.e, only openScanner
        // when you call next, so here we need to make a next call to open scanner. The maxResultSize
        // limit can make sure that we will not fetch all the data at once, so the test sill works.
        int scannerCount = scanner.next().rawCells().length;
        Delete delete1 = new Delete(rows[0]);
        delete1.addColumn(families[0], qualifiers[0], 0);
        tmpTable.delete(delete1);
        Delete delete2 = new Delete(rows[1]);
        delete2.addColumn(families[1], qualifiers[1], 1);
        tmpTable.delete(delete2);
        // Should see all cells because scanner was opened prior to deletes
        scannerCount += countCellsFromScanner(scanner);
        int expectedCount = (numRows * numFamilies) * numQualifiers;
        Assert.assertTrue(((("scannerCount: " + scannerCount) + " expectedCount: ") + expectedCount), (scannerCount == expectedCount));
        // Minus 2 for the two cells that were deleted
        scanner = tmpTable.getScanner(new Scan().setMaxResultSize(1).setAllowPartialResults(true));
        scannerCount = countCellsFromScanner(scanner);
        expectedCount = ((numRows * numFamilies) * numQualifiers) - 2;
        Assert.assertTrue(((("scannerCount: " + scannerCount) + " expectedCount: ") + expectedCount), (scannerCount == expectedCount));
        scanner = tmpTable.getScanner(new Scan().setMaxResultSize(1).setAllowPartialResults(true));
        scannerCount = scanner.next().rawCells().length;
        // Put in 2 new rows. The timestamps differ from the deleted rows
        Put put1 = new Put(rows[0]);
        put1.add(new KeyValue(rows[0], families[0], qualifiers[0], 1, value));
        tmpTable.put(put1);
        Put put2 = new Put(rows[1]);
        put2.add(new KeyValue(rows[1], families[1], qualifiers[1], 2, value));
        tmpTable.put(put2);
        // Scanner opened prior to puts. Cell count shouldn't have changed
        scannerCount += countCellsFromScanner(scanner);
        expectedCount = ((numRows * numFamilies) * numQualifiers) - 2;
        Assert.assertTrue(((("scannerCount: " + scannerCount) + " expectedCount: ") + expectedCount), (scannerCount == expectedCount));
        // Now the scanner should see the cells that were added by puts
        scanner = tmpTable.getScanner(new Scan().setMaxResultSize(1).setAllowPartialResults(true));
        scannerCount = countCellsFromScanner(scanner);
        expectedCount = (numRows * numFamilies) * numQualifiers;
        Assert.assertTrue(((("scannerCount: " + scannerCount) + " expectedCount: ") + expectedCount), (scannerCount == expectedCount));
        TestPartialResultsFromClientSide.TEST_UTIL.deleteTable(tableName);
    }

    /**
     * Test partial Result re-assembly in the presence of different filters. The Results from the
     * partial scanner should match the Results returned from a scanner that receives all of the
     * results in one RPC to the server. The partial scanner is tested with a variety of different
     * result sizes (all of which are less than the size necessary to fetch an entire row)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testPartialResultsWithColumnFilter() throws Exception {
        testPartialResultsWithColumnFilter(new FirstKeyOnlyFilter());
        testPartialResultsWithColumnFilter(new org.apache.hadoop.hbase.filter.ColumnPrefixFilter(Bytes.toBytes("testQualifier5")));
        testPartialResultsWithColumnFilter(new org.apache.hadoop.hbase.filter.ColumnRangeFilter(Bytes.toBytes("testQualifer1"), true, Bytes.toBytes("testQualifier7"), true));
        Set<byte[]> qualifiers = new LinkedHashSet<>();
        qualifiers.add(Bytes.toBytes("testQualifier5"));
        testPartialResultsWithColumnFilter(new FirstKeyValueMatchingQualifiersFilter(qualifiers));
    }

    @Test
    public void testPartialResultWhenRegionMove() throws IOException {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        moveRegion(table, 1);
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.setAllowPartialResults(true);
        ResultScanner scanner = table.getScanner(scan);
        for (int i = 0; i < (((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)) - 1); i++) {
            scanner.next();
        }
        Result result1 = scanner.next();
        Assert.assertEquals(1, result1.rawCells().length);
        Cell c1 = result1.rawCells()[0];
        assertCell(c1, TestPartialResultsFromClientSide.ROWS[0], TestPartialResultsFromClientSide.FAMILIES[((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)], TestPartialResultsFromClientSide.QUALIFIERS[((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 1)]);
        Assert.assertFalse(result1.mayHaveMoreCellsInRow());
        moveRegion(table, 2);
        Result result2 = scanner.next();
        Assert.assertEquals(1, result2.rawCells().length);
        Cell c2 = result2.rawCells()[0];
        assertCell(c2, TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertTrue(result2.mayHaveMoreCellsInRow());
        moveRegion(table, 3);
        Result result3 = scanner.next();
        Assert.assertEquals(1, result3.rawCells().length);
        Cell c3 = result3.rawCells()[0];
        assertCell(c3, TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[1]);
        Assert.assertTrue(result3.mayHaveMoreCellsInRow());
    }

    @Test
    public void testReversedPartialResultWhenRegionMove() throws IOException {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        moveRegion(table, 1);
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.setAllowPartialResults(true);
        scan.setReversed(true);
        ResultScanner scanner = table.getScanner(scan);
        for (int i = 0; i < (((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)) - 1); i++) {
            scanner.next();
        }
        Result result1 = scanner.next();
        Assert.assertEquals(1, result1.rawCells().length);
        Cell c1 = result1.rawCells()[0];
        assertCell(c1, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 1)], TestPartialResultsFromClientSide.FAMILIES[((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)], TestPartialResultsFromClientSide.QUALIFIERS[((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 1)]);
        Assert.assertFalse(result1.mayHaveMoreCellsInRow());
        moveRegion(table, 2);
        Result result2 = scanner.next();
        Assert.assertEquals(1, result2.rawCells().length);
        Cell c2 = result2.rawCells()[0];
        assertCell(c2, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 2)], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertTrue(result2.mayHaveMoreCellsInRow());
        moveRegion(table, 3);
        Result result3 = scanner.next();
        Assert.assertEquals(1, result3.rawCells().length);
        Cell c3 = result3.rawCells()[0];
        assertCell(c3, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 2)], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[1]);
        Assert.assertTrue(result3.mayHaveMoreCellsInRow());
    }

    @Test
    public void testCompleteResultWhenRegionMove() throws IOException {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        moveRegion(table, 1);
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.setCaching(1);
        ResultScanner scanner = table.getScanner(scan);
        Result result1 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result1.rawCells().length);
        Cell c1 = result1.rawCells()[0];
        assertCell(c1, TestPartialResultsFromClientSide.ROWS[0], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result1.mayHaveMoreCellsInRow());
        moveRegion(table, 2);
        Result result2 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result2.rawCells().length);
        Cell c2 = result2.rawCells()[0];
        assertCell(c2, TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result2.mayHaveMoreCellsInRow());
        moveRegion(table, 3);
        Result result3 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result3.rawCells().length);
        Cell c3 = result3.rawCells()[0];
        assertCell(c3, TestPartialResultsFromClientSide.ROWS[2], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result3.mayHaveMoreCellsInRow());
    }

    @Test
    public void testReversedCompleteResultWhenRegionMove() throws IOException {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        moveRegion(table, 1);
        Scan scan = new Scan();
        scan.setMaxResultSize(1);
        scan.setCaching(1);
        scan.setReversed(true);
        ResultScanner scanner = table.getScanner(scan);
        Result result1 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result1.rawCells().length);
        Cell c1 = result1.rawCells()[0];
        assertCell(c1, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 1)], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result1.mayHaveMoreCellsInRow());
        moveRegion(table, 2);
        Result result2 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result2.rawCells().length);
        Cell c2 = result2.rawCells()[0];
        assertCell(c2, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 2)], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result2.mayHaveMoreCellsInRow());
        moveRegion(table, 3);
        Result result3 = scanner.next();
        Assert.assertEquals(((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)), result3.rawCells().length);
        Cell c3 = result3.rawCells()[0];
        assertCell(c3, TestPartialResultsFromClientSide.ROWS[((TestPartialResultsFromClientSide.NUM_ROWS) - 3)], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        Assert.assertFalse(result3.mayHaveMoreCellsInRow());
    }

    @Test
    public void testBatchingResultWhenRegionMove() throws IOException {
        // If user setBatch(5) and rpc returns 3+5+5+5+3 cells,
        // we should return 5+5+5+5+1 to user.
        // setBatch doesn't mean setAllowPartialResult(true)
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        Put put = new Put(TestPartialResultsFromClientSide.ROWS[1]);
        put.addColumn(TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[1], new byte[(TestPartialResultsFromClientSide.VALUE_SIZE) * 10]);
        table.put(put);
        Delete delete = new Delete(TestPartialResultsFromClientSide.ROWS[1]);
        delete.addColumn(TestPartialResultsFromClientSide.FAMILIES[((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)], TestPartialResultsFromClientSide.QUALIFIERS[((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 1)]);
        table.delete(delete);
        moveRegion(table, 1);
        Scan scan = new Scan();
        scan.setCaching(1);
        scan.setBatch(5);
        scan.setMaxResultSize(((TestPartialResultsFromClientSide.VALUE_SIZE) * 6));
        ResultScanner scanner = table.getScanner(scan);
        for (int i = 0; i < ((((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)) / 5) - 1); i++) {
            Assert.assertTrue(scanner.next().mayHaveMoreCellsInRow());
        }
        Result result1 = scanner.next();
        Assert.assertEquals(5, result1.rawCells().length);
        assertCell(result1.rawCells()[0], TestPartialResultsFromClientSide.ROWS[0], TestPartialResultsFromClientSide.FAMILIES[((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)], TestPartialResultsFromClientSide.QUALIFIERS[((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 5)]);
        assertCell(result1.rawCells()[4], TestPartialResultsFromClientSide.ROWS[0], TestPartialResultsFromClientSide.FAMILIES[((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)], TestPartialResultsFromClientSide.QUALIFIERS[((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 1)]);
        Assert.assertFalse(result1.mayHaveMoreCellsInRow());
        moveRegion(table, 2);
        Result result2 = scanner.next();
        Assert.assertEquals(5, result2.rawCells().length);
        assertCell(result2.rawCells()[0], TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[0]);
        assertCell(result2.rawCells()[4], TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[4]);
        Assert.assertTrue(result2.mayHaveMoreCellsInRow());
        moveRegion(table, 3);
        Result result3 = scanner.next();
        Assert.assertEquals(5, result3.rawCells().length);
        assertCell(result3.rawCells()[0], TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[5]);
        assertCell(result3.rawCells()[4], TestPartialResultsFromClientSide.ROWS[1], TestPartialResultsFromClientSide.FAMILIES[0], TestPartialResultsFromClientSide.QUALIFIERS[9]);
        Assert.assertTrue(result3.mayHaveMoreCellsInRow());
        for (int i = 0; i < ((((TestPartialResultsFromClientSide.NUM_FAMILIES) * (TestPartialResultsFromClientSide.NUM_QUALIFIERS)) / 5) - 3); i++) {
            Result result = scanner.next();
            Assert.assertEquals(5, result.rawCells().length);
            Assert.assertTrue(result.mayHaveMoreCellsInRow());
        }
        Result result = scanner.next();
        Assert.assertEquals(4, result.rawCells().length);
        Assert.assertFalse(result.mayHaveMoreCellsInRow());
        for (int i = 2; i < (TestPartialResultsFromClientSide.NUM_ROWS); i++) {
            for (int j = 0; j < (TestPartialResultsFromClientSide.NUM_FAMILIES); j++) {
                for (int k = 0; k < (TestPartialResultsFromClientSide.NUM_QUALIFIERS); k += 5) {
                    result = scanner.next();
                    assertCell(result.rawCells()[0], TestPartialResultsFromClientSide.ROWS[i], TestPartialResultsFromClientSide.FAMILIES[j], TestPartialResultsFromClientSide.QUALIFIERS[k]);
                    Assert.assertEquals(5, result.rawCells().length);
                    if ((j == ((TestPartialResultsFromClientSide.NUM_FAMILIES) - 1)) && (k == ((TestPartialResultsFromClientSide.NUM_QUALIFIERS) - 5))) {
                        Assert.assertFalse(result.mayHaveMoreCellsInRow());
                    } else {
                        Assert.assertTrue(result.mayHaveMoreCellsInRow());
                    }
                }
            }
        }
        Assert.assertNull(scanner.next());
    }

    @Test
    public void testDontThrowUnknowScannerExceptionToClient() throws Exception {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        Scan scan = new Scan();
        scan.setCaching(1);
        ResultScanner scanner = table.getScanner(scan);
        scanner.next();
        Thread.sleep(((TestPartialResultsFromClientSide.timeout) * 2));
        int count = 1;
        while ((scanner.next()) != null) {
            count++;
        } 
        Assert.assertEquals(TestPartialResultsFromClientSide.NUM_ROWS, count);
        scanner.close();
    }

    @Test
    public void testMayHaveMoreCellsInRowReturnsTrueAndSetBatch() throws IOException {
        Table table = TestPartialResultsFromClientSide.createTestTable(TableName.valueOf(name.getMethodName()), TestPartialResultsFromClientSide.ROWS, TestPartialResultsFromClientSide.FAMILIES, TestPartialResultsFromClientSide.QUALIFIERS, TestPartialResultsFromClientSide.VALUE);
        Scan scan = new Scan();
        scan.setBatch(1);
        scan.setFilter(new FirstKeyOnlyFilter());
        ResultScanner scanner = table.getScanner(scan);
        Result result;
        while ((result = scanner.next()) != null) {
            Assert.assertTrue(((result.rawCells()) != null));
            Assert.assertEquals(1, result.rawCells().length);
        } 
    }
}

