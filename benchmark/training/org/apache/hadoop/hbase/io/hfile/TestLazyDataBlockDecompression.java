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


import CacheConfig.CACHE_BLOCKS_ON_WRITE_KEY;
import CacheConfig.CACHE_BLOOM_BLOCKS_ON_WRITE_KEY;
import CacheConfig.CACHE_DATA_BLOCKS_COMPRESSED_KEY;
import CacheConfig.CACHE_INDEX_BLOCKS_ON_WRITE_KEY;
import Compression.Algorithm.GZ;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A kind of integration test at the intersection of {@link HFileBlock}, {@link CacheConfig},
 * and {@link LruBlockCache}.
 */
@Category({ IOTests.class, SmallTests.class })
@RunWith(Parameterized.class)
public class TestLazyDataBlockDecompression {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestLazyDataBlockDecompression.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestLazyDataBlockDecompression.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private FileSystem fs;

    @Parameterized.Parameter(0)
    public boolean cacheOnWrite;

    @Test
    public void testCompressionIncreasesEffectiveBlockCacheSize() throws Exception {
        // enough room for 2 uncompressed block
        int maxSize = ((int) ((HConstants.DEFAULT_BLOCKSIZE) * 2.1));
        Path hfilePath = new Path(getDataTestDir(), "testCompressionIncreasesEffectiveBlockcacheSize");
        HFileContext context = new HFileContextBuilder().withCompression(GZ).build();
        TestLazyDataBlockDecompression.LOG.info(("context=" + context));
        // setup cache with lazy-decompression disabled.
        Configuration lazyCompressDisabled = HBaseConfiguration.create(TestLazyDataBlockDecompression.TEST_UTIL.getConfiguration());
        lazyCompressDisabled.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressDisabled.setBoolean(CACHE_BLOOM_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressDisabled.setBoolean(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressDisabled.setBoolean(CACHE_DATA_BLOCKS_COMPRESSED_KEY, false);
        CacheConfig cc = new CacheConfig(lazyCompressDisabled, new LruBlockCache(maxSize, HConstants.DEFAULT_BLOCKSIZE, false, lazyCompressDisabled));
        Assert.assertFalse(cc.shouldCacheDataCompressed());
        Assert.assertTrue(((cc.getBlockCache().get()) instanceof LruBlockCache));
        LruBlockCache disabledBlockCache = ((LruBlockCache) (cc.getBlockCache().get()));
        TestLazyDataBlockDecompression.LOG.info(("disabledBlockCache=" + disabledBlockCache));
        Assert.assertEquals("test inconsistency detected.", maxSize, disabledBlockCache.getMaxSize());
        Assert.assertTrue("eviction thread spawned unintentionally.", ((disabledBlockCache.getEvictionThread()) == null));
        Assert.assertEquals("freshly created blockcache contains blocks.", 0, disabledBlockCache.getBlockCount());
        // 2000 kv's is ~3.6 full unencoded data blocks.
        // Requires a conf and CacheConfig but should not be specific to this instance's cache settings
        TestLazyDataBlockDecompression.writeHFile(lazyCompressDisabled, cc, fs, hfilePath, context, 2000);
        // populate the cache
        TestLazyDataBlockDecompression.cacheBlocks(lazyCompressDisabled, cc, fs, hfilePath, context);
        long disabledBlockCount = disabledBlockCache.getBlockCount();
        Assert.assertTrue(("blockcache should contain blocks. disabledBlockCount=" + disabledBlockCount), (disabledBlockCount > 0));
        long disabledEvictedCount = disabledBlockCache.getStats().getEvictedCount();
        for (Map.Entry<BlockCacheKey, LruCachedBlock> e : disabledBlockCache.getMapForTests().entrySet()) {
            HFileBlock block = ((HFileBlock) (e.getValue().getBuffer()));
            Assert.assertTrue(("found a packed block, block=" + block), block.isUnpacked());
        }
        // count blocks with lazy decompression
        Configuration lazyCompressEnabled = HBaseConfiguration.create(TestLazyDataBlockDecompression.TEST_UTIL.getConfiguration());
        lazyCompressEnabled.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressEnabled.setBoolean(CACHE_BLOOM_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressEnabled.setBoolean(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, cacheOnWrite);
        lazyCompressEnabled.setBoolean(CACHE_DATA_BLOCKS_COMPRESSED_KEY, true);
        cc = new CacheConfig(lazyCompressEnabled, new LruBlockCache(maxSize, HConstants.DEFAULT_BLOCKSIZE, false, lazyCompressEnabled));
        Assert.assertTrue("test improperly configured.", cc.shouldCacheDataCompressed());
        Assert.assertTrue(((cc.getBlockCache().get()) instanceof LruBlockCache));
        LruBlockCache enabledBlockCache = ((LruBlockCache) (cc.getBlockCache().get()));
        TestLazyDataBlockDecompression.LOG.info(("enabledBlockCache=" + enabledBlockCache));
        Assert.assertEquals("test inconsistency detected", maxSize, enabledBlockCache.getMaxSize());
        Assert.assertTrue("eviction thread spawned unintentionally.", ((enabledBlockCache.getEvictionThread()) == null));
        Assert.assertEquals("freshly created blockcache contains blocks.", 0, enabledBlockCache.getBlockCount());
        TestLazyDataBlockDecompression.cacheBlocks(lazyCompressEnabled, cc, fs, hfilePath, context);
        long enabledBlockCount = enabledBlockCache.getBlockCount();
        Assert.assertTrue(("blockcache should contain blocks. enabledBlockCount=" + enabledBlockCount), (enabledBlockCount > 0));
        long enabledEvictedCount = enabledBlockCache.getStats().getEvictedCount();
        int candidatesFound = 0;
        for (Map.Entry<BlockCacheKey, LruCachedBlock> e : enabledBlockCache.getMapForTests().entrySet()) {
            candidatesFound++;
            HFileBlock block = ((HFileBlock) (e.getValue().getBuffer()));
            if (cc.shouldCacheCompressed(block.getBlockType().getCategory())) {
                Assert.assertFalse(((("found an unpacked block, block=" + block) + ", block buffer capacity=") + (block.getBufferWithoutHeader().capacity())), block.isUnpacked());
            }
        }
        Assert.assertTrue("did not find any candidates for compressed caching. Invalid test.", (candidatesFound > 0));
        TestLazyDataBlockDecompression.LOG.info(((("disabledBlockCount=" + disabledBlockCount) + ", enabledBlockCount=") + enabledBlockCount));
        Assert.assertTrue((((("enabling compressed data blocks should increase the effective cache size. " + "disabledBlockCount=") + disabledBlockCount) + ", enabledBlockCount=") + enabledBlockCount), (disabledBlockCount < enabledBlockCount));
        TestLazyDataBlockDecompression.LOG.info(((("disabledEvictedCount=" + disabledEvictedCount) + ", enabledEvictedCount=") + enabledEvictedCount));
        Assert.assertTrue((((("enabling compressed data blocks should reduce the number of evictions. " + "disabledEvictedCount=") + disabledEvictedCount) + ", enabledEvictedCount=") + enabledEvictedCount), (enabledEvictedCount < disabledEvictedCount));
    }
}

