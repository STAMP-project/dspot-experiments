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


import HConstants.EMPTY_BYTE_ARRAY;
import HConstants.EMPTY_START_ROW;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.CellComparatorImpl;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeepDeletedCells;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.io.encoding.DataBlockEncoding;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BloomType.NONE;


/**
 * Test cases against ReversibleKeyValueScanner
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestReversibleScanners {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReversibleScanners.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReversibleScanners.class);

    HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILYNAME = Bytes.toBytes("testCf");

    private static long TS = System.currentTimeMillis();

    private static int MAXMVCC = 7;

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static final int ROWSIZE = 200;

    private static byte[][] ROWS = TestReversibleScanners.makeN(TestReversibleScanners.ROW, TestReversibleScanners.ROWSIZE);

    private static byte[] QUAL = Bytes.toBytes("testQual");

    private static final int QUALSIZE = 5;

    private static byte[][] QUALS = TestReversibleScanners.makeN(TestReversibleScanners.QUAL, TestReversibleScanners.QUALSIZE);

    private static byte[] VALUE = Bytes.toBytes("testValue");

    private static final int VALUESIZE = 3;

    private static byte[][] VALUES = TestReversibleScanners.makeN(TestReversibleScanners.VALUE, TestReversibleScanners.VALUESIZE);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testReversibleStoreFileScanner() throws IOException {
        FileSystem fs = TEST_UTIL.getTestFileSystem();
        Path hfilePath = new Path(new Path(getDataTestDir("testReversibleStoreFileScanner"), "regionname"), "familyname");
        CacheConfig cacheConf = new CacheConfig(TEST_UTIL.getConfiguration());
        for (DataBlockEncoding encoding : DataBlockEncoding.values()) {
            HFileContextBuilder hcBuilder = new HFileContextBuilder();
            hcBuilder.withBlockSize((2 * 1024));
            hcBuilder.withDataBlockEncoding(encoding);
            HFileContext hFileContext = hcBuilder.build();
            StoreFileWriter writer = new StoreFileWriter.Builder(TEST_UTIL.getConfiguration(), cacheConf, fs).withOutputDir(hfilePath).withFileContext(hFileContext).build();
            TestReversibleScanners.writeStoreFile(writer);
            HStoreFile sf = new HStoreFile(fs, writer.getPath(), TEST_UTIL.getConfiguration(), cacheConf, NONE, true);
            List<StoreFileScanner> scanners = StoreFileScanner.getScannersForStoreFiles(Collections.singletonList(sf), false, true, false, false, Long.MAX_VALUE);
            StoreFileScanner scanner = scanners.get(0);
            seekTestOfReversibleKeyValueScanner(scanner);
            for (int readPoint = 0; readPoint < (TestReversibleScanners.MAXMVCC); readPoint++) {
                TestReversibleScanners.LOG.info(("Setting read point to " + readPoint));
                scanners = StoreFileScanner.getScannersForStoreFiles(Collections.singletonList(sf), false, true, false, false, readPoint);
                seekTestOfReversibleKeyValueScannerWithMVCC(scanners, readPoint);
            }
        }
    }

    @Test
    public void testReversibleMemstoreScanner() throws IOException {
        MemStore memstore = new DefaultMemStore();
        TestReversibleScanners.writeMemstore(memstore);
        List<KeyValueScanner> scanners = memstore.getScanners(Long.MAX_VALUE);
        seekTestOfReversibleKeyValueScanner(scanners.get(0));
        for (int readPoint = 0; readPoint < (TestReversibleScanners.MAXMVCC); readPoint++) {
            TestReversibleScanners.LOG.info(("Setting read point to " + readPoint));
            scanners = memstore.getScanners(readPoint);
            seekTestOfReversibleKeyValueScannerWithMVCC(scanners, readPoint);
        }
    }

    @Test
    public void testReversibleKeyValueHeap() throws IOException {
        // write data to one memstore and two store files
        FileSystem fs = TEST_UTIL.getTestFileSystem();
        Path hfilePath = new Path(new Path(getDataTestDir("testReversibleKeyValueHeap"), "regionname"), "familyname");
        CacheConfig cacheConf = new CacheConfig(TEST_UTIL.getConfiguration());
        HFileContextBuilder hcBuilder = new HFileContextBuilder();
        hcBuilder.withBlockSize((2 * 1024));
        HFileContext hFileContext = hcBuilder.build();
        StoreFileWriter writer1 = new StoreFileWriter.Builder(TEST_UTIL.getConfiguration(), cacheConf, fs).withOutputDir(hfilePath).withFileContext(hFileContext).build();
        StoreFileWriter writer2 = new StoreFileWriter.Builder(TEST_UTIL.getConfiguration(), cacheConf, fs).withOutputDir(hfilePath).withFileContext(hFileContext).build();
        MemStore memstore = new DefaultMemStore();
        TestReversibleScanners.writeMemstoreAndStoreFiles(memstore, new StoreFileWriter[]{ writer1, writer2 });
        HStoreFile sf1 = new HStoreFile(fs, writer1.getPath(), TEST_UTIL.getConfiguration(), cacheConf, NONE, true);
        HStoreFile sf2 = new HStoreFile(fs, writer2.getPath(), TEST_UTIL.getConfiguration(), cacheConf, NONE, true);
        /**
         * Test without MVCC
         */
        int startRowNum = (TestReversibleScanners.ROWSIZE) / 2;
        ReversedKeyValueHeap kvHeap = getReversibleKeyValueHeap(memstore, sf1, sf2, TestReversibleScanners.ROWS[startRowNum], TestReversibleScanners.MAXMVCC);
        internalTestSeekAndNextForReversibleKeyValueHeap(kvHeap, startRowNum);
        startRowNum = (TestReversibleScanners.ROWSIZE) - 1;
        kvHeap = getReversibleKeyValueHeap(memstore, sf1, sf2, EMPTY_START_ROW, TestReversibleScanners.MAXMVCC);
        internalTestSeekAndNextForReversibleKeyValueHeap(kvHeap, startRowNum);
        /**
         * Test with MVCC
         */
        for (int readPoint = 0; readPoint < (TestReversibleScanners.MAXMVCC); readPoint++) {
            TestReversibleScanners.LOG.info(("Setting read point to " + readPoint));
            startRowNum = (TestReversibleScanners.ROWSIZE) - 1;
            kvHeap = getReversibleKeyValueHeap(memstore, sf1, sf2, EMPTY_START_ROW, readPoint);
            for (int i = startRowNum; i >= 0; i--) {
                if ((i - 2) < 0)
                    break;

                i = i - 2;
                kvHeap.seekToPreviousRow(KeyValueUtil.createFirstOnRow(TestReversibleScanners.ROWS[(i + 1)]));
                Pair<Integer, Integer> nextReadableNum = getNextReadableNumWithBackwardScan(i, 0, readPoint);
                if (nextReadableNum == null)
                    break;

                KeyValue expecedKey = TestReversibleScanners.makeKV(nextReadableNum.getFirst(), nextReadableNum.getSecond());
                Assert.assertEquals(expecedKey, kvHeap.peek());
                i = nextReadableNum.getFirst();
                int qualNum = nextReadableNum.getSecond();
                if ((qualNum + 1) < (TestReversibleScanners.QUALSIZE)) {
                    kvHeap.backwardSeek(TestReversibleScanners.makeKV(i, (qualNum + 1)));
                    nextReadableNum = getNextReadableNumWithBackwardScan(i, (qualNum + 1), readPoint);
                    if (nextReadableNum == null)
                        break;

                    expecedKey = TestReversibleScanners.makeKV(nextReadableNum.getFirst(), nextReadableNum.getSecond());
                    Assert.assertEquals(expecedKey, kvHeap.peek());
                    i = nextReadableNum.getFirst();
                    qualNum = nextReadableNum.getSecond();
                }
                kvHeap.next();
                if ((qualNum + 1) >= (TestReversibleScanners.QUALSIZE)) {
                    nextReadableNum = getNextReadableNumWithBackwardScan((i - 1), 0, readPoint);
                } else {
                    nextReadableNum = getNextReadableNumWithBackwardScan(i, (qualNum + 1), readPoint);
                }
                if (nextReadableNum == null)
                    break;

                expecedKey = TestReversibleScanners.makeKV(nextReadableNum.getFirst(), nextReadableNum.getSecond());
                Assert.assertEquals(expecedKey, kvHeap.peek());
                i = nextReadableNum.getFirst();
            }
        }
    }

    @Test
    public void testReversibleStoreScanner() throws IOException {
        // write data to one memstore and two store files
        FileSystem fs = TEST_UTIL.getTestFileSystem();
        Path hfilePath = new Path(new Path(getDataTestDir("testReversibleStoreScanner"), "regionname"), "familyname");
        CacheConfig cacheConf = new CacheConfig(TEST_UTIL.getConfiguration());
        HFileContextBuilder hcBuilder = new HFileContextBuilder();
        hcBuilder.withBlockSize((2 * 1024));
        HFileContext hFileContext = hcBuilder.build();
        StoreFileWriter writer1 = new StoreFileWriter.Builder(TEST_UTIL.getConfiguration(), cacheConf, fs).withOutputDir(hfilePath).withFileContext(hFileContext).build();
        StoreFileWriter writer2 = new StoreFileWriter.Builder(TEST_UTIL.getConfiguration(), cacheConf, fs).withOutputDir(hfilePath).withFileContext(hFileContext).build();
        MemStore memstore = new DefaultMemStore();
        TestReversibleScanners.writeMemstoreAndStoreFiles(memstore, new StoreFileWriter[]{ writer1, writer2 });
        HStoreFile sf1 = new HStoreFile(fs, writer1.getPath(), TEST_UTIL.getConfiguration(), cacheConf, NONE, true);
        HStoreFile sf2 = new HStoreFile(fs, writer2.getPath(), TEST_UTIL.getConfiguration(), cacheConf, NONE, true);
        ScanInfo scanInfo = new ScanInfo(TEST_UTIL.getConfiguration(), TestReversibleScanners.FAMILYNAME, 0, Integer.MAX_VALUE, Long.MAX_VALUE, KeepDeletedCells.FALSE, HConstants.DEFAULT_BLOCKSIZE, 0, CellComparatorImpl.COMPARATOR, false);
        // Case 1.Test a full reversed scan
        Scan scan = new Scan();
        scan.setReversed(true);
        StoreScanner storeScanner = getReversibleStoreScanner(memstore, sf1, sf2, scan, scanInfo, TestReversibleScanners.MAXMVCC);
        verifyCountAndOrder(storeScanner, ((TestReversibleScanners.QUALSIZE) * (TestReversibleScanners.ROWSIZE)), TestReversibleScanners.ROWSIZE, false);
        // Case 2.Test reversed scan with a specified start row
        int startRowNum = (TestReversibleScanners.ROWSIZE) / 2;
        byte[] startRow = TestReversibleScanners.ROWS[startRowNum];
        scan.withStartRow(startRow);
        storeScanner = getReversibleStoreScanner(memstore, sf1, sf2, scan, scanInfo, TestReversibleScanners.MAXMVCC);
        verifyCountAndOrder(storeScanner, ((TestReversibleScanners.QUALSIZE) * (startRowNum + 1)), (startRowNum + 1), false);
        // Case 3.Test reversed scan with a specified start row and specified
        // qualifiers
        Assert.assertTrue(((TestReversibleScanners.QUALSIZE) > 2));
        scan.addColumn(TestReversibleScanners.FAMILYNAME, TestReversibleScanners.QUALS[0]);
        scan.addColumn(TestReversibleScanners.FAMILYNAME, TestReversibleScanners.QUALS[2]);
        storeScanner = getReversibleStoreScanner(memstore, sf1, sf2, scan, scanInfo, TestReversibleScanners.MAXMVCC);
        verifyCountAndOrder(storeScanner, (2 * (startRowNum + 1)), (startRowNum + 1), false);
        // Case 4.Test reversed scan with mvcc based on case 3
        for (int readPoint = 0; readPoint < (TestReversibleScanners.MAXMVCC); readPoint++) {
            TestReversibleScanners.LOG.info(("Setting read point to " + readPoint));
            storeScanner = getReversibleStoreScanner(memstore, sf1, sf2, scan, scanInfo, readPoint);
            int expectedRowCount = 0;
            int expectedKVCount = 0;
            for (int i = startRowNum; i >= 0; i--) {
                int kvCount = 0;
                if ((TestReversibleScanners.makeMVCC(i, 0)) <= readPoint) {
                    kvCount++;
                }
                if ((TestReversibleScanners.makeMVCC(i, 2)) <= readPoint) {
                    kvCount++;
                }
                if (kvCount > 0) {
                    expectedRowCount++;
                    expectedKVCount += kvCount;
                }
            }
            verifyCountAndOrder(storeScanner, expectedKVCount, expectedRowCount, false);
        }
    }

    @Test
    public void testReversibleRegionScanner() throws IOException {
        byte[] FAMILYNAME2 = Bytes.toBytes("testCf2");
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName())).addFamily(new HColumnDescriptor(TestReversibleScanners.FAMILYNAME)).addFamily(new HColumnDescriptor(FAMILYNAME2));
        HRegion region = TEST_UTIL.createLocalHRegion(htd, null, null);
        TestReversibleScanners.loadDataToRegion(region, FAMILYNAME2);
        // verify row count with forward scan
        Scan scan = new Scan();
        InternalScanner scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((TestReversibleScanners.ROWSIZE) * (TestReversibleScanners.QUALSIZE)) * 2), TestReversibleScanners.ROWSIZE, true);
        // Case1:Full reversed scan
        scan.setReversed(true);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((TestReversibleScanners.ROWSIZE) * (TestReversibleScanners.QUALSIZE)) * 2), TestReversibleScanners.ROWSIZE, false);
        // Case2:Full reversed scan with one family
        scan = new Scan();
        scan.setReversed(true);
        scan.addFamily(TestReversibleScanners.FAMILYNAME);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, ((TestReversibleScanners.ROWSIZE) * (TestReversibleScanners.QUALSIZE)), TestReversibleScanners.ROWSIZE, false);
        // Case3:Specify qualifiers + One family
        byte[][] specifiedQualifiers = new byte[][]{ TestReversibleScanners.QUALS[1], TestReversibleScanners.QUALS[2] };
        for (byte[] specifiedQualifier : specifiedQualifiers)
            scan.addColumn(TestReversibleScanners.FAMILYNAME, specifiedQualifier);

        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, ((TestReversibleScanners.ROWSIZE) * 2), TestReversibleScanners.ROWSIZE, false);
        // Case4:Specify qualifiers + Two families
        for (byte[] specifiedQualifier : specifiedQualifiers)
            scan.addColumn(FAMILYNAME2, specifiedQualifier);

        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((TestReversibleScanners.ROWSIZE) * 2) * 2), TestReversibleScanners.ROWSIZE, false);
        // Case5: Case4 + specify start row
        int startRowNum = ((TestReversibleScanners.ROWSIZE) * 3) / 4;
        scan.withStartRow(TestReversibleScanners.ROWS[startRowNum]);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((startRowNum + 1) * 2) * 2), (startRowNum + 1), false);
        // Case6: Case4 + specify stop row
        int stopRowNum = (TestReversibleScanners.ROWSIZE) / 4;
        scan.withStartRow(EMPTY_BYTE_ARRAY);
        scan.withStopRow(TestReversibleScanners.ROWS[stopRowNum]);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((((TestReversibleScanners.ROWSIZE) - stopRowNum) - 1) * 2) * 2), (((TestReversibleScanners.ROWSIZE) - stopRowNum) - 1), false);
        // Case7: Case4 + specify start row + specify stop row
        scan.withStartRow(TestReversibleScanners.ROWS[startRowNum]);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, (((startRowNum - stopRowNum) * 2) * 2), (startRowNum - stopRowNum), false);
        // Case8: Case7 + SingleColumnValueFilter
        int valueNum = startRowNum % (TestReversibleScanners.VALUESIZE);
        Filter filter = new SingleColumnValueFilter(TestReversibleScanners.FAMILYNAME, specifiedQualifiers[0], CompareOp.EQUAL, TestReversibleScanners.VALUES[valueNum]);
        scan.setFilter(filter);
        scanner = region.getScanner(scan);
        int unfilteredRowNum = ((startRowNum - stopRowNum) / (TestReversibleScanners.VALUESIZE)) + ((stopRowNum / (TestReversibleScanners.VALUESIZE)) == valueNum ? 0 : 1);
        verifyCountAndOrder(scanner, ((unfilteredRowNum * 2) * 2), unfilteredRowNum, false);
        // Case9: Case7 + PageFilter
        int pageSize = 10;
        filter = new PageFilter(pageSize);
        scan.setFilter(filter);
        scanner = region.getScanner(scan);
        int expectedRowNum = pageSize;
        verifyCountAndOrder(scanner, ((expectedRowNum * 2) * 2), expectedRowNum, false);
        // Case10: Case7 + FilterList+MUST_PASS_ONE
        SingleColumnValueFilter scvFilter1 = new SingleColumnValueFilter(TestReversibleScanners.FAMILYNAME, specifiedQualifiers[0], CompareOp.EQUAL, TestReversibleScanners.VALUES[0]);
        SingleColumnValueFilter scvFilter2 = new SingleColumnValueFilter(TestReversibleScanners.FAMILYNAME, specifiedQualifiers[0], CompareOp.EQUAL, TestReversibleScanners.VALUES[1]);
        expectedRowNum = 0;
        for (int i = startRowNum; i > stopRowNum; i--) {
            if (((i % (TestReversibleScanners.VALUESIZE)) == 0) || ((i % (TestReversibleScanners.VALUESIZE)) == 1)) {
                expectedRowNum++;
            }
        }
        filter = new org.apache.hadoop.hbase.filter.FilterList(Operator.MUST_PASS_ONE, scvFilter1, scvFilter2);
        scan.setFilter(filter);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, ((expectedRowNum * 2) * 2), expectedRowNum, false);
        // Case10: Case7 + FilterList+MUST_PASS_ALL
        filter = new org.apache.hadoop.hbase.filter.FilterList(Operator.MUST_PASS_ALL, scvFilter1, scvFilter2);
        expectedRowNum = 0;
        scan.setFilter(filter);
        scanner = region.getScanner(scan);
        verifyCountAndOrder(scanner, ((expectedRowNum * 2) * 2), expectedRowNum, false);
    }
}

