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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.BlockCacheFactory;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BloomType.NONE;
import static BloomType.ROW;
import static BloomType.ROWCOL;


@Category({ RegionServerTests.class, MediumTests.class })
public class TestBlocksRead {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBlocksRead.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestBlocksRead.class);

    @Rule
    public TestName testName = new TestName();

    static final BloomType[] BLOOM_TYPE = new BloomType[]{ ROWCOL, ROW, NONE };

    HRegion region = null;

    private static HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private final String DIR = getDataTestDir("TestBlocksRead").toString();

    private Configuration conf = TestBlocksRead.TEST_UTIL.getConfiguration();

    /**
     * Test # of blocks read for some simple seek cases.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlocksRead() throws Exception {
        byte[] TABLE = Bytes.toBytes("testBlocksRead");
        String FAMILY = "cf1";
        Cell[] kvs;
        this.region = initHRegion(TABLE, testName.getMethodName(), conf, FAMILY);
        try {
            putData(FAMILY, "row", "col1", 1);
            putData(FAMILY, "row", "col2", 2);
            putData(FAMILY, "row", "col3", 3);
            putData(FAMILY, "row", "col4", 4);
            putData(FAMILY, "row", "col5", 5);
            putData(FAMILY, "row", "col6", 6);
            putData(FAMILY, "row", "col7", 7);
            region.flush(true);
            // Expected block reads: 1
            // The top block has the KV we are
            // interested. So only 1 seek is needed.
            kvs = getData(FAMILY, "row", "col1", 1);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 1);
            // Expected block reads: 2
            // The top block and next block has the KVs we are
            // interested. So only 2 seek is needed.
            kvs = getData(FAMILY, "row", Arrays.asList("col1", "col2"), 2);
            Assert.assertEquals(2, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 1);
            TestBlocksRead.verifyData(kvs[1], "row", "col2", 2);
            // Expected block reads: 3
            // The first 2 seeks is to find out col2. [HBASE-4443]
            // One additional seek for col3
            // So 3 seeks are needed.
            kvs = getData(FAMILY, "row", Arrays.asList("col2", "col3"), 2);
            Assert.assertEquals(2, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col2", 2);
            TestBlocksRead.verifyData(kvs[1], "row", "col3", 3);
            // Expected block reads: 1. [HBASE-4443]&[HBASE-7845]
            kvs = getData(FAMILY, "row", Arrays.asList("col5"), 1);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col5", 5);
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }

    /**
     * Test # of blocks read (targeted at some of the cases Lazy Seek optimizes).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testLazySeekBlocksRead() throws Exception {
        byte[] TABLE = Bytes.toBytes("testLazySeekBlocksRead");
        String FAMILY = "cf1";
        Cell[] kvs;
        this.region = initHRegion(TABLE, testName.getMethodName(), conf, FAMILY);
        try {
            // File 1
            putData(FAMILY, "row", "col1", 1);
            putData(FAMILY, "row", "col2", 2);
            region.flush(true);
            // File 2
            putData(FAMILY, "row", "col1", 3);
            putData(FAMILY, "row", "col2", 4);
            region.flush(true);
            // Expected blocks read: 1.
            // File 2's top block is also the KV we are
            // interested. So only 1 seek is needed.
            kvs = getData(FAMILY, "row", Arrays.asList("col1"), 1);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 3);
            // Expected blocks read: 2
            // File 2's top block has the "col1" KV we are
            // interested. We also need "col2" which is in a block
            // of its own. So, we need that block as well.
            kvs = getData(FAMILY, "row", Arrays.asList("col1", "col2"), 2);
            Assert.assertEquals(2, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 3);
            TestBlocksRead.verifyData(kvs[1], "row", "col2", 4);
            // File 3: Add another column
            putData(FAMILY, "row", "col3", 5);
            region.flush(true);
            // Expected blocks read: 1
            // File 3's top block has the "col3" KV we are
            // interested. So only 1 seek is needed.
            kvs = getData(FAMILY, "row", "col3", 1);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col3", 5);
            // Get a column from older file.
            // For ROWCOL Bloom filter: Expected blocks read: 1.
            // For ROW Bloom filter: Expected blocks read: 2.
            // For NONE Bloom filter: Expected blocks read: 2.
            kvs = getData(FAMILY, "row", Arrays.asList("col1"), 1, 2, 2);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 3);
            // File 4: Delete the entire row.
            deleteFamily(FAMILY, "row", 6);
            region.flush(true);
            // For ROWCOL Bloom filter: Expected blocks read: 2.
            // For ROW Bloom filter: Expected blocks read: 3.
            // For NONE Bloom filter: Expected blocks read: 3.
            kvs = getData(FAMILY, "row", "col1", 2, 3, 3);
            Assert.assertEquals(0, kvs.length);
            kvs = getData(FAMILY, "row", "col2", 2, 3, 3);
            Assert.assertEquals(0, kvs.length);
            kvs = getData(FAMILY, "row", "col3", 2);
            Assert.assertEquals(0, kvs.length);
            kvs = getData(FAMILY, "row", Arrays.asList("col1", "col2", "col3"), 4);
            Assert.assertEquals(0, kvs.length);
            // File 5: Delete
            deleteFamily(FAMILY, "row", 10);
            region.flush(true);
            // File 6: some more puts, but with timestamps older than the
            // previous delete.
            putData(FAMILY, "row", "col1", 7);
            putData(FAMILY, "row", "col2", 8);
            putData(FAMILY, "row", "col3", 9);
            region.flush(true);
            // Baseline expected blocks read: 6. [HBASE-4532]
            kvs = getData(FAMILY, "row", Arrays.asList("col1", "col2", "col3"), 6, 7, 7);
            Assert.assertEquals(0, kvs.length);
            // File 7: Put back new data
            putData(FAMILY, "row", "col1", 11);
            putData(FAMILY, "row", "col2", 12);
            putData(FAMILY, "row", "col3", 13);
            region.flush(true);
            // Expected blocks read: 8. [HBASE-4585, HBASE-13109]
            kvs = getData(FAMILY, "row", Arrays.asList("col1", "col2", "col3"), 8, 9, 9);
            Assert.assertEquals(3, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col1", 11);
            TestBlocksRead.verifyData(kvs[1], "row", "col2", 12);
            TestBlocksRead.verifyData(kvs[2], "row", "col3", 13);
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }

    /**
     * Test # of blocks read to ensure disabling cache-fill on Scan works.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBlocksStoredWhenCachingDisabled() throws Exception {
        byte[] TABLE = Bytes.toBytes("testBlocksReadWhenCachingDisabled");
        String FAMILY = "cf1";
        BlockCache blockCache = BlockCacheFactory.createBlockCache(conf);
        this.region = initHRegion(TABLE, testName.getMethodName(), conf, FAMILY, blockCache);
        try {
            putData(FAMILY, "row", "col1", 1);
            putData(FAMILY, "row", "col2", 2);
            region.flush(true);
            // Execute a scan with caching turned off
            // Expected blocks stored: 0
            long blocksStart = blockCache.getBlockCount();
            Scan scan = new Scan();
            scan.setCacheBlocks(false);
            RegionScanner rs = region.getScanner(scan);
            List<Cell> result = new ArrayList<>(2);
            rs.next(result);
            Assert.assertEquals((2 * (TestBlocksRead.BLOOM_TYPE.length)), result.size());
            rs.close();
            long blocksEnd = blockCache.getBlockCount();
            Assert.assertEquals(blocksStart, blocksEnd);
            // Execute with caching turned on
            // Expected blocks stored: 2
            blocksStart = blocksEnd;
            scan.setCacheBlocks(true);
            rs = region.getScanner(scan);
            result = new ArrayList(2);
            rs.next(result);
            Assert.assertEquals((2 * (TestBlocksRead.BLOOM_TYPE.length)), result.size());
            rs.close();
            blocksEnd = blockCache.getBlockCount();
            Assert.assertEquals((2 * (TestBlocksRead.BLOOM_TYPE.length)), (blocksEnd - blocksStart));
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }

    @Test
    public void testLazySeekBlocksReadWithDelete() throws Exception {
        byte[] TABLE = Bytes.toBytes("testLazySeekBlocksReadWithDelete");
        String FAMILY = "cf1";
        Cell[] kvs;
        this.region = initHRegion(TABLE, testName.getMethodName(), conf, FAMILY);
        try {
            deleteFamily(FAMILY, "row", 200);
            for (int i = 0; i < 100; i++) {
                putData(FAMILY, "row", ("col" + i), i);
            }
            putData(FAMILY, "row", "col99", 201);
            region.flush(true);
            kvs = getData(FAMILY, "row", Arrays.asList("col0"), 2);
            Assert.assertEquals(0, kvs.length);
            kvs = getData(FAMILY, "row", Arrays.asList("col99"), 2);
            Assert.assertEquals(1, kvs.length);
            TestBlocksRead.verifyData(kvs[0], "row", "col99", 201);
        } finally {
            HBaseTestingUtility.closeRegionAndWAL(this.region);
            this.region = null;
        }
    }
}

