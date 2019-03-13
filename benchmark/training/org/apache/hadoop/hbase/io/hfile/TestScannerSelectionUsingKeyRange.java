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
package org.apache.hadoop.hbase.io.hfile;


import BloomType.NONE;
import BloomType.ROW;
import BloomType.ROWCOL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.InternalScanner;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test the optimization that does not scan files where all key ranges are excluded.
 */
@RunWith(Parameterized.class)
@Category({ IOTests.class, SmallTests.class })
public class TestScannerSelectionUsingKeyRange {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestScannerSelectionUsingKeyRange.class);

    private static final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static TableName TABLE = TableName.valueOf("myTable");

    private static String FAMILY = "myCF";

    private static byte[] FAMILY_BYTES = Bytes.toBytes(TestScannerSelectionUsingKeyRange.FAMILY);

    private static final int NUM_ROWS = 8;

    private static final int NUM_COLS_PER_ROW = 5;

    private static final int NUM_FILES = 2;

    private static final Map<Object, Integer> TYPE_COUNT = new HashMap<>(3);

    static {
        TestScannerSelectionUsingKeyRange.TYPE_COUNT.put(ROWCOL, 0);
        TestScannerSelectionUsingKeyRange.TYPE_COUNT.put(ROW, 0);
        TestScannerSelectionUsingKeyRange.TYPE_COUNT.put(NONE, 0);
    }

    private BloomType bloomType;

    private int expectedCount;

    public TestScannerSelectionUsingKeyRange(Object type, Object count) {
        bloomType = ((BloomType) (type));
        expectedCount = ((Integer) (count));
    }

    @Test
    public void testScannerSelection() throws IOException {
        Configuration conf = TestScannerSelectionUsingKeyRange.TEST_UTIL.getConfiguration();
        conf.setInt("hbase.hstore.compactionThreshold", 10000);
        HColumnDescriptor hcd = new HColumnDescriptor(TestScannerSelectionUsingKeyRange.FAMILY_BYTES).setBlockCacheEnabled(true).setBloomFilterType(bloomType);
        HTableDescriptor htd = new HTableDescriptor(TestScannerSelectionUsingKeyRange.TABLE);
        htd.addFamily(hcd);
        HRegionInfo info = new HRegionInfo(TestScannerSelectionUsingKeyRange.TABLE);
        HRegion region = HBaseTestingUtility.createRegionAndWAL(info, getDataTestDir(), conf, htd);
        for (int iFile = 0; iFile < (TestScannerSelectionUsingKeyRange.NUM_FILES); ++iFile) {
            for (int iRow = 0; iRow < (TestScannerSelectionUsingKeyRange.NUM_ROWS); ++iRow) {
                Put put = new Put(Bytes.toBytes(("row" + iRow)));
                for (int iCol = 0; iCol < (TestScannerSelectionUsingKeyRange.NUM_COLS_PER_ROW); ++iCol) {
                    put.addColumn(TestScannerSelectionUsingKeyRange.FAMILY_BYTES, Bytes.toBytes(("col" + iCol)), Bytes.toBytes(((((("value" + iFile) + "_") + iRow) + "_") + iCol)));
                }
                region.put(put);
            }
            region.flush(true);
        }
        Scan scan = new Scan(Bytes.toBytes("aaa"), Bytes.toBytes("aaz"));
        LruBlockCache cache = ((LruBlockCache) (BlockCacheFactory.createBlockCache(conf)));
        cache.clearCache();
        InternalScanner scanner = region.getScanner(scan);
        List<Cell> results = new ArrayList<>();
        while (scanner.next(results)) {
        } 
        scanner.close();
        Assert.assertEquals(0, results.size());
        Set<String> accessedFiles = cache.getCachedFileNamesForTest();
        Assert.assertEquals(expectedCount, accessedFiles.size());
        HBaseTestingUtility.closeRegionAndWAL(region);
    }
}

