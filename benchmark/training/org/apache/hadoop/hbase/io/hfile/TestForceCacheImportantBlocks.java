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


import HFile.DATABLOCK_READ_COUNT;
import HFile.FORMAT_VERSION_KEY;
import HFileBlockIndex.MAX_CHUNK_SIZE_KEY;
import java.io.IOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.io.compress.Compression.Algorithm;
import org.apache.hadoop.hbase.regionserver.BloomType;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Make sure we always cache important block types, such as index blocks, as
 * long as we have a block cache, even though block caching might be disabled
 * for the column family.
 *
 * <p>TODO: This test writes a lot of data and only tests the most basic of metrics.  Cache stats
 * need to reveal more about what is being cached whether DATA or INDEX blocks and then we could
 * do more verification in this test.
 */
@Category({ IOTests.class, MediumTests.class })
@RunWith(Parameterized.class)
public class TestForceCacheImportantBlocks {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestForceCacheImportantBlocks.class);

    private final HBaseTestingUtility TEST_UTIL = HBaseTestingUtility.createLocalHTU();

    private static final String TABLE = "myTable";

    private static final String CF = "myCF";

    private static final byte[] CF_BYTES = Bytes.toBytes(TestForceCacheImportantBlocks.CF);

    private static final int MAX_VERSIONS = 3;

    private static final int NUM_HFILES = 5;

    private static final int ROWS_PER_HFILE = 100;

    private static final int NUM_ROWS = (TestForceCacheImportantBlocks.NUM_HFILES) * (TestForceCacheImportantBlocks.ROWS_PER_HFILE);

    private static final int NUM_COLS_PER_ROW = 50;

    private static final int NUM_TIMESTAMPS_PER_COL = 50;

    /**
     * Extremely small block size, so that we can get some index blocks
     */
    private static final int BLOCK_SIZE = 256;

    private static final Algorithm COMPRESSION_ALGORITHM = Algorithm.GZ;

    private static final BloomType BLOOM_TYPE = BloomType.ROW;

    // Currently unused.
    @SuppressWarnings("unused")
    private final int hfileVersion;

    private final boolean cfCacheEnabled;

    public TestForceCacheImportantBlocks(int hfileVersion, boolean cfCacheEnabled) {
        this.hfileVersion = hfileVersion;
        this.cfCacheEnabled = cfCacheEnabled;
        TEST_UTIL.getConfiguration().setInt(FORMAT_VERSION_KEY, hfileVersion);
    }

    @Test
    public void testCacheBlocks() throws IOException {
        // Set index block size to be the same as normal block size.
        TEST_UTIL.getConfiguration().setInt(MAX_CHUNK_SIZE_KEY, TestForceCacheImportantBlocks.BLOCK_SIZE);
        BlockCache blockCache = BlockCacheFactory.createBlockCache(TEST_UTIL.getConfiguration());
        ColumnFamilyDescriptor cfd = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(TestForceCacheImportantBlocks.CF)).setMaxVersions(TestForceCacheImportantBlocks.MAX_VERSIONS).setCompressionType(TestForceCacheImportantBlocks.COMPRESSION_ALGORITHM).setBloomFilterType(TestForceCacheImportantBlocks.BLOOM_TYPE).setBlocksize(TestForceCacheImportantBlocks.BLOCK_SIZE).setBlockCacheEnabled(cfCacheEnabled).build();
        HRegion region = TEST_UTIL.createTestRegion(TestForceCacheImportantBlocks.TABLE, cfd, blockCache);
        CacheStats stats = blockCache.getStats();
        writeTestData(region);
        Assert.assertEquals(0, stats.getHitCount());
        Assert.assertEquals(0, DATABLOCK_READ_COUNT.sum());
        // Do a single get, take count of caches.  If we are NOT caching DATA blocks, the miss
        // count should go up.  Otherwise, all should be cached and the miss count should not rise.
        region.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(("row" + 0))));
        Assert.assertTrue(((stats.getHitCount()) > 0));
        Assert.assertTrue(((DATABLOCK_READ_COUNT.sum()) > 0));
        long missCount = stats.getMissCount();
        region.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(("row" + 0))));
        if (this.cfCacheEnabled)
            Assert.assertEquals(missCount, stats.getMissCount());
        else
            Assert.assertTrue(((stats.getMissCount()) > missCount));

    }
}

