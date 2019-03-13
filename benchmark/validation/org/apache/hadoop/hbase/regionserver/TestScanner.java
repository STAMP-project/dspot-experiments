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


import Bytes.BYTES_COMPARATOR;
import HConstants.CATALOG_FAMILY;
import HConstants.REGIONINFO_QUALIFIER;
import HConstants.SERVER_QUALIFIER;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.UnknownScannerException;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.InclusiveStopFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
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
 * Test of a long-lived scanner validating as we go.
 */
@Category({ RegionServerTests.class, SmallTests.class })
public class TestScanner {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScanner.class);

    @Rule
    public TestName name = new TestName();

    private static final Logger LOG = LoggerFactory.getLogger(TestScanner.class);

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static final byte[] FIRST_ROW = HConstants.EMPTY_START_ROW;

    private static final byte[][] COLS = new byte[][]{ HConstants.CATALOG_FAMILY };

    private static final byte[][] EXPLICIT_COLS = // TODO ryan
    // HConstants.STARTCODE_QUALIFIER
    new byte[][]{ HConstants.REGIONINFO_QUALIFIER, HConstants.SERVER_QUALIFIER }// TODO ryan
    // HConstants.STARTCODE_QUALIFIER
    ;

    static final HTableDescriptor TESTTABLEDESC = new HTableDescriptor(TableName.valueOf("testscanner"));

    static {
        TestScanner.TESTTABLEDESC.addFamily(// Ten is an arbitrary number.  Keep versions to help debugging.
        setMaxVersions(10).setBlockCacheEnabled(false).setBlocksize((8 * 1024)));
    }

    /**
     * HRegionInfo for root region
     */
    public static final HRegionInfo REGION_INFO = new HRegionInfo(TestScanner.TESTTABLEDESC.getTableName(), HConstants.EMPTY_BYTE_ARRAY, HConstants.EMPTY_BYTE_ARRAY);

    private static final byte[] ROW_KEY = TestScanner.REGION_INFO.getRegionName();

    private static final long START_CODE = Long.MAX_VALUE;

    private HRegion region;

    private byte[] firstRowBytes;

    private byte[] secondRowBytes;

    private byte[] thirdRowBytes;

    private final byte[] col1;

    public TestScanner() {
        super();
        firstRowBytes = HBaseTestingUtility.START_KEY_BYTES;
        secondRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        // Increment the least significant character so we get to next row.
        (secondRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)])++;
        thirdRowBytes = HBaseTestingUtility.START_KEY_BYTES.clone();
        thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)] = ((byte) ((thirdRowBytes[((HBaseTestingUtility.START_KEY_BYTES.length) - 1)]) + 2));
        col1 = Bytes.toBytes("column1");
    }

    /**
     * Test basic stop row filter works.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testStopRow() throws Exception {
        byte[] startrow = Bytes.toBytes("bbb");
        byte[] stoprow = Bytes.toBytes("ccc");
        try {
            this.region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
            HBaseTestCase.addContent(this.region, CATALOG_FAMILY);
            List<Cell> results = new ArrayList<>();
            // Do simple test of getting one row only first.
            Scan scan = new Scan(Bytes.toBytes("abc"), Bytes.toBytes("abd"));
            scan.addFamily(CATALOG_FAMILY);
            InternalScanner s = region.getScanner(scan);
            int count = 0;
            while (s.next(results)) {
                count++;
            } 
            s.close();
            Assert.assertEquals(0, count);
            // Now do something a bit more imvolved.
            scan = new Scan(startrow, stoprow);
            scan.addFamily(CATALOG_FAMILY);
            s = region.getScanner(scan);
            count = 0;
            Cell kv = null;
            results = new ArrayList();
            for (boolean first = true; s.next(results);) {
                kv = results.get(0);
                if (first) {
                    Assert.assertTrue(CellUtil.matchingRows(kv, startrow));
                    first = false;
                }
                count++;
            }
            Assert.assertTrue(((BYTES_COMPARATOR.compare(stoprow, CellUtil.cloneRow(kv))) > 0));
            // We got something back.
            Assert.assertTrue((count > 10));
            s.close();
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    @Test
    public void testFilters() throws IOException {
        try {
            this.region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
            HBaseTestCase.addContent(this.region, CATALOG_FAMILY);
            byte[] prefix = Bytes.toBytes("ab");
            Filter newFilter = new PrefixFilter(prefix);
            Scan scan = new Scan();
            scan.setFilter(newFilter);
            rowPrefixFilter(scan);
            byte[] stopRow = Bytes.toBytes("bbc");
            newFilter = new org.apache.hadoop.hbase.filter.WhileMatchFilter(new InclusiveStopFilter(stopRow));
            scan = new Scan();
            scan.setFilter(newFilter);
            rowInclusiveStopFilter(scan, stopRow);
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    /**
     * Test that closing a scanner while a client is using it doesn't throw
     * NPEs but instead a UnknownScannerException. HBASE-2503
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRaceBetweenClientAndTimeout() throws Exception {
        try {
            this.region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
            HBaseTestCase.addContent(this.region, CATALOG_FAMILY);
            Scan scan = new Scan();
            InternalScanner s = region.getScanner(scan);
            List<Cell> results = new ArrayList<>();
            try {
                s.next(results);
                s.close();
                s.next(results);
                Assert.fail("We don't want anything more, we should be failing");
            } catch (UnknownScannerException ex) {
                // ok!
                return;
            }
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    /**
     * The test!
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testScanner() throws IOException {
        try {
            region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
            Table table = new RegionAsTable(region);
            // Write information to the meta table
            Put put = new Put(TestScanner.ROW_KEY, System.currentTimeMillis());
            put.addColumn(CATALOG_FAMILY, REGIONINFO_QUALIFIER, TestScanner.REGION_INFO.toByteArray());
            table.put(put);
            // What we just committed is in the memstore. Verify that we can get
            // it back both with scanning and get
            scan(false, null);
            getRegionInfo(table);
            // Close and re-open
            close();
            region = HRegion.openHRegion(region, null);
            table = new RegionAsTable(region);
            // Verify we can get the data back now that it is on disk.
            scan(false, null);
            getRegionInfo(table);
            // Store some new information
            String address = ((HConstants.LOCALHOST_IP) + ":") + (HBaseTestingUtility.randomFreePort());
            put = new Put(TestScanner.ROW_KEY, System.currentTimeMillis());
            put.addColumn(CATALOG_FAMILY, SERVER_QUALIFIER, Bytes.toBytes(address));
            // put.add(HConstants.COL_STARTCODE, Bytes.toBytes(START_CODE));
            table.put(put);
            // Validate that we can still get the HRegionInfo, even though it is in
            // an older row on disk and there is a newer row in the memstore
            scan(true, address.toString());
            getRegionInfo(table);
            // flush cache
            this.region.flush(true);
            // Validate again
            scan(true, address.toString());
            getRegionInfo(table);
            // Close and reopen
            close();
            region = HRegion.openHRegion(region, null);
            table = new RegionAsTable(region);
            // Validate again
            scan(true, address.toString());
            getRegionInfo(table);
            // Now update the information again
            address = "bar.foo.com:4321";
            put = new Put(TestScanner.ROW_KEY, System.currentTimeMillis());
            put.addColumn(CATALOG_FAMILY, SERVER_QUALIFIER, Bytes.toBytes(address));
            table.put(put);
            // Validate again
            scan(true, address.toString());
            getRegionInfo(table);
            // flush cache
            region.flush(true);
            // Validate again
            scan(true, address.toString());
            getRegionInfo(table);
            // Close and reopen
            close();
            this.region = HRegion.openHRegion(region, null);
            table = new RegionAsTable(this.region);
            // Validate again
            scan(true, address.toString());
            getRegionInfo(table);
        } finally {
            // clean up
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    /**
     * Tests to do a sync flush during the middle of a scan. This is testing the StoreScanner
     * update readers code essentially.  This is not highly concurrent, since its all 1 thread.
     * HBase-910.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScanAndSyncFlush() throws Exception {
        this.region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
        Table hri = new RegionAsTable(region);
        try {
            TestScanner.LOG.info(("Added: " + (HBaseTestCase.addContent(hri, Bytes.toString(CATALOG_FAMILY), Bytes.toString(REGIONINFO_QUALIFIER)))));
            int count = count(hri, (-1), false);
            Assert.assertEquals(count, count(hri, 100, false));// do a sync flush.

        } catch (Exception e) {
            TestScanner.LOG.error("Failed", e);
            throw e;
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    /**
     * Tests to do a concurrent flush (using a 2nd thread) while scanning.  This tests both
     * the StoreScanner update readers and the transition from memstore -> snapshot -> store file.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testScanAndRealConcurrentFlush() throws Exception {
        this.region = TestScanner.TEST_UTIL.createLocalHRegion(TestScanner.TESTTABLEDESC, null, null);
        Table hri = new RegionAsTable(region);
        try {
            TestScanner.LOG.info(("Added: " + (HBaseTestCase.addContent(hri, Bytes.toString(CATALOG_FAMILY), Bytes.toString(REGIONINFO_QUALIFIER)))));
            int count = count(hri, (-1), false);
            Assert.assertEquals(count, count(hri, 100, true));// do a true concurrent background thread flush

        } catch (Exception e) {
            TestScanner.LOG.error("Failed", e);
            throw e;
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }

    /**
     * Make sure scanner returns correct result when we run a major compaction
     * with deletes.
     *
     * @throws Exception
     * 		
     */
    @Test
    @SuppressWarnings("deprecation")
    public void testScanAndConcurrentMajorCompact() throws Exception {
        HTableDescriptor htd = TestScanner.TEST_UTIL.createTableDescriptor(name.getMethodName());
        this.region = TestScanner.TEST_UTIL.createLocalHRegion(htd, null, null);
        Table hri = new RegionAsTable(region);
        try {
            HBaseTestCase.addContent(hri, Bytes.toString(HBaseTestingUtility.fam1), Bytes.toString(col1), firstRowBytes, secondRowBytes);
            HBaseTestCase.addContent(hri, Bytes.toString(HBaseTestingUtility.fam2), Bytes.toString(col1), firstRowBytes, secondRowBytes);
            Delete dc = new Delete(firstRowBytes);
            /* delete column1 of firstRow */
            dc.addColumns(HBaseTestingUtility.fam1, col1);
            region.delete(dc);
            region.flush(true);
            HBaseTestCase.addContent(hri, Bytes.toString(HBaseTestingUtility.fam1), Bytes.toString(col1), secondRowBytes, thirdRowBytes);
            HBaseTestCase.addContent(hri, Bytes.toString(HBaseTestingUtility.fam2), Bytes.toString(col1), secondRowBytes, thirdRowBytes);
            region.flush(true);
            InternalScanner s = region.getScanner(new Scan());
            // run a major compact, column1 of firstRow will be cleaned.
            region.compact(true);
            List<Cell> results = new ArrayList<>();
            s.next(results);
            // make sure returns column2 of firstRow
            Assert.assertTrue(("result is not correct, keyValues : " + results), ((results.size()) == 1));
            Assert.assertTrue(CellUtil.matchingRows(results.get(0), firstRowBytes));
            Assert.assertTrue(CellUtil.matchingFamily(results.get(0), HBaseTestingUtility.fam2));
            results = new ArrayList();
            s.next(results);
            // get secondRow
            Assert.assertTrue(((results.size()) == 2));
            Assert.assertTrue(CellUtil.matchingRows(results.get(0), secondRowBytes));
            Assert.assertTrue(CellUtil.matchingFamily(results.get(0), HBaseTestingUtility.fam1));
            Assert.assertTrue(CellUtil.matchingFamily(results.get(1), HBaseTestingUtility.fam2));
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
        }
    }
}

