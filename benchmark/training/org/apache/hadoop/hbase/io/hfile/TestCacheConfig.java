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


import BlockCacheFactory.BUCKET_CACHE_BUCKETS_KEY;
import BlockCategory.BLOOM;
import BlockCategory.DATA;
import BlockCategory.INDEX;
import BlockCategory.META;
import CacheConfig.CACHE_BLOCKS_ON_WRITE_KEY;
import CacheConfig.CACHE_BLOOM_BLOCKS_ON_WRITE_KEY;
import CacheConfig.CACHE_DATA_BLOCKS_COMPRESSED_KEY;
import CacheConfig.CACHE_DATA_ON_READ_KEY;
import CacheConfig.CACHE_INDEX_BLOCKS_ON_WRITE_KEY;
import HConstants.BUCKET_CACHE_IOENGINE_KEY;
import HConstants.BUCKET_CACHE_SIZE_KEY;
import HConstants.HFILE_BLOCK_CACHE_SIZE_KEY;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.nio.ByteBuffer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.io.hfile.Cacheable.MemoryType;
import org.apache.hadoop.hbase.io.util.MemorySizeUtil;
import org.apache.hadoop.hbase.nio.ByteBuff;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static BlockType.DATA;
import static BlockType.INTERMEDIATE_INDEX;
import static BlockType.ROOT_INDEX;
import static CacheConfig.DEFAULT_IN_MEMORY;


/**
 * Tests that {@link CacheConfig} does as expected.
 */
// This test is marked as a large test though it runs in a short amount of time
// (seconds).  It is large because it depends on being able to reset the global
// blockcache instance which is in a global variable.  Experience has it that
// tests clash on the global variable if this test is run as small sized test.
@Category({ IOTests.class, LargeTests.class })
public class TestCacheConfig {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCacheConfig.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestCacheConfig.class);

    private Configuration conf;

    static class Deserializer implements CacheableDeserializer<Cacheable> {
        private final Cacheable cacheable;

        private int deserializedIdentifier = 0;

        Deserializer(final Cacheable c) {
            deserializedIdentifier = CacheableDeserializerIdManager.registerDeserializer(this);
            this.cacheable = c;
        }

        @Override
        public int getDeserialiserIdentifier() {
            return deserializedIdentifier;
        }

        @Override
        public Cacheable deserialize(ByteBuff b, boolean reuse, MemoryType memType) throws IOException {
            TestCacheConfig.LOG.info(((("Deserialized " + b) + ", reuse=") + reuse));
            return cacheable;
        }

        @Override
        public Cacheable deserialize(ByteBuff b) throws IOException {
            TestCacheConfig.LOG.info(("Deserialized " + b));
            return cacheable;
        }
    }

    static class IndexCacheEntry extends TestCacheConfig.DataCacheEntry {
        private static TestCacheConfig.IndexCacheEntry SINGLETON = new TestCacheConfig.IndexCacheEntry();

        public IndexCacheEntry() {
            super(TestCacheConfig.IndexCacheEntry.SINGLETON);
        }

        @Override
        public BlockType getBlockType() {
            return ROOT_INDEX;
        }
    }

    static class DataCacheEntry implements Cacheable {
        private static final int SIZE = 1;

        private static TestCacheConfig.DataCacheEntry SINGLETON = new TestCacheConfig.DataCacheEntry();

        final CacheableDeserializer<Cacheable> deserializer;

        DataCacheEntry() {
            this(TestCacheConfig.DataCacheEntry.SINGLETON);
        }

        DataCacheEntry(final Cacheable c) {
            this.deserializer = new TestCacheConfig.Deserializer(c);
        }

        @Override
        public String toString() {
            return (("size=" + (TestCacheConfig.DataCacheEntry.SIZE)) + ", type=") + (getBlockType());
        }

        @Override
        public long heapSize() {
            return TestCacheConfig.DataCacheEntry.SIZE;
        }

        @Override
        public int getSerializedLength() {
            return TestCacheConfig.DataCacheEntry.SIZE;
        }

        @Override
        public void serialize(ByteBuffer destination, boolean includeNextBlockMetadata) {
            TestCacheConfig.LOG.info(((("Serialized " + (this)) + " to ") + destination));
        }

        @Override
        public CacheableDeserializer<Cacheable> getDeserializer() {
            return this.deserializer;
        }

        @Override
        public BlockType getBlockType() {
            return DATA;
        }

        @Override
        public MemoryType getMemoryType() {
            return MemoryType.EXCLUSIVE;
        }
    }

    static class MetaCacheEntry extends TestCacheConfig.DataCacheEntry {
        @Override
        public BlockType getBlockType() {
            return INTERMEDIATE_INDEX;
        }
    }

    @Test
    public void testDisableCacheDataBlock() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        CacheConfig cacheConfig = new CacheConfig(conf);
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheCompressed(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheDataCompressed());
        Assert.assertFalse(cacheConfig.shouldCacheDataOnWrite());
        Assert.assertTrue(cacheConfig.shouldCacheDataOnRead());
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(INDEX));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(META));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(BLOOM));
        Assert.assertFalse(cacheConfig.shouldCacheBloomsOnWrite());
        Assert.assertFalse(cacheConfig.shouldCacheIndexesOnWrite());
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, true);
        conf.setBoolean(CACHE_DATA_BLOCKS_COMPRESSED_KEY, true);
        conf.setBoolean(CACHE_BLOOM_BLOCKS_ON_WRITE_KEY, true);
        conf.setBoolean(CACHE_INDEX_BLOCKS_ON_WRITE_KEY, true);
        cacheConfig = new CacheConfig(conf);
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(DATA));
        Assert.assertTrue(cacheConfig.shouldCacheCompressed(DATA));
        Assert.assertTrue(cacheConfig.shouldCacheDataCompressed());
        Assert.assertTrue(cacheConfig.shouldCacheDataOnWrite());
        Assert.assertTrue(cacheConfig.shouldCacheDataOnRead());
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(INDEX));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(META));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(BLOOM));
        Assert.assertTrue(cacheConfig.shouldCacheBloomsOnWrite());
        Assert.assertTrue(cacheConfig.shouldCacheIndexesOnWrite());
        conf.setBoolean(CACHE_DATA_ON_READ_KEY, false);
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, false);
        cacheConfig = new CacheConfig(conf);
        Assert.assertFalse(cacheConfig.shouldCacheBlockOnRead(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheCompressed(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheDataCompressed());
        Assert.assertFalse(cacheConfig.shouldCacheDataOnWrite());
        Assert.assertFalse(cacheConfig.shouldCacheDataOnRead());
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(INDEX));
        Assert.assertFalse(cacheConfig.shouldCacheBlockOnRead(META));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(BLOOM));
        Assert.assertTrue(cacheConfig.shouldCacheBloomsOnWrite());
        Assert.assertTrue(cacheConfig.shouldCacheIndexesOnWrite());
        conf.setBoolean(CACHE_DATA_ON_READ_KEY, true);
        conf.setBoolean(CACHE_BLOCKS_ON_WRITE_KEY, false);
        HColumnDescriptor family = new HColumnDescriptor("testDisableCacheDataBlock");
        family.setBlockCacheEnabled(false);
        cacheConfig = new CacheConfig(conf, family, null);
        Assert.assertFalse(cacheConfig.shouldCacheBlockOnRead(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheCompressed(DATA));
        Assert.assertFalse(cacheConfig.shouldCacheDataCompressed());
        Assert.assertFalse(cacheConfig.shouldCacheDataOnWrite());
        Assert.assertFalse(cacheConfig.shouldCacheDataOnRead());
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(INDEX));
        Assert.assertFalse(cacheConfig.shouldCacheBlockOnRead(META));
        Assert.assertTrue(cacheConfig.shouldCacheBlockOnRead(BLOOM));
        Assert.assertTrue(cacheConfig.shouldCacheBloomsOnWrite());
        Assert.assertTrue(cacheConfig.shouldCacheIndexesOnWrite());
    }

    @Test
    public void testCacheConfigDefaultLRUBlockCache() {
        CacheConfig cc = new CacheConfig(this.conf);
        Assert.assertTrue(((DEFAULT_IN_MEMORY) == (cc.isInMemory())));
        BlockCache blockCache = BlockCacheFactory.createBlockCache(this.conf);
        basicBlockCacheOps(blockCache, cc, false, true);
        Assert.assertTrue((blockCache instanceof LruBlockCache));
    }

    /**
     * Assert that the caches are deployed with CombinedBlockCache and of the appropriate sizes.
     */
    @Test
    public void testOffHeapBucketCacheConfig() {
        this.conf.set(BUCKET_CACHE_IOENGINE_KEY, "offheap");
        doBucketCacheConfigTest();
    }

    @Test
    public void testFileBucketCacheConfig() throws IOException {
        HBaseTestingUtility htu = new HBaseTestingUtility(this.conf);
        try {
            Path p = new Path(getDataTestDir(), "bc.txt");
            FileSystem fs = FileSystem.get(this.conf);
            fs.create(p).close();
            this.conf.set(BUCKET_CACHE_IOENGINE_KEY, ("file:" + p));
            doBucketCacheConfigTest();
        } finally {
            cleanupTestDir();
        }
    }

    /**
     * Assert that when BUCKET_CACHE_COMBINED_KEY is false, the non-default, that we deploy
     * LruBlockCache as L1 with a BucketCache for L2.
     */
    @Test
    public void testBucketCacheConfigL1L2Setup() {
        this.conf.set(BUCKET_CACHE_IOENGINE_KEY, "offheap");
        // Make lru size is smaller than bcSize for sure.  Need this to be true so when eviction
        // from L1 happens, it does not fail because L2 can't take the eviction because block too big.
        this.conf.setFloat(HFILE_BLOCK_CACHE_SIZE_KEY, 0.001F);
        MemoryUsage mu = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        long lruExpectedSize = MemorySizeUtil.getOnHeapCacheSize(this.conf);
        final int bcSize = 100;
        long bcExpectedSize = (100 * 1024) * 1024;// MB.

        Assert.assertTrue((lruExpectedSize < bcExpectedSize));
        this.conf.setInt(BUCKET_CACHE_SIZE_KEY, bcSize);
        CacheConfig cc = new CacheConfig(this.conf);
        BlockCache blockCache = BlockCacheFactory.createBlockCache(this.conf);
        basicBlockCacheOps(blockCache, cc, false, false);
        Assert.assertTrue((blockCache instanceof CombinedBlockCache));
        // TODO: Assert sizes allocated are right and proportions.
        CombinedBlockCache cbc = ((CombinedBlockCache) (blockCache));
        LruBlockCache lbc = cbc.onHeapCache;
        Assert.assertEquals(lruExpectedSize, lbc.getMaxSize());
        BlockCache bc = cbc.l2Cache;
        // getMaxSize comes back in bytes but we specified size in MB
        Assert.assertEquals(bcExpectedSize, getMaxSize());
        // Test the L1+L2 deploy works as we'd expect with blocks evicted from L1 going to L2.
        long initialL1BlockCount = lbc.getBlockCount();
        long initialL2BlockCount = bc.getBlockCount();
        Cacheable c = new TestCacheConfig.DataCacheEntry();
        BlockCacheKey bck = new BlockCacheKey("bck", 0);
        lbc.cacheBlock(bck, c, false);
        Assert.assertEquals((initialL1BlockCount + 1), lbc.getBlockCount());
        Assert.assertEquals(initialL2BlockCount, bc.getBlockCount());
        // Force evictions by putting in a block too big.
        final long justTooBigSize = (lbc.acceptableSize()) + 1;
        lbc.cacheBlock(new BlockCacheKey("bck2", 0), new TestCacheConfig.DataCacheEntry() {
            @Override
            public long heapSize() {
                return justTooBigSize;
            }

            @Override
            public int getSerializedLength() {
                return ((int) (heapSize()));
            }
        });
        // The eviction thread in lrublockcache needs to run.
        while (initialL1BlockCount != (lbc.getBlockCount()))
            Threads.sleep(10);

        Assert.assertEquals(initialL1BlockCount, lbc.getBlockCount());
    }

    @Test
    public void testL2CacheWithInvalidBucketSize() {
        Configuration c = new Configuration(this.conf);
        c.set(BUCKET_CACHE_IOENGINE_KEY, "offheap");
        c.set(BUCKET_CACHE_BUCKETS_KEY, "256,512,1024,2048,4000,4096");
        c.setFloat(BUCKET_CACHE_SIZE_KEY, 1024);
        try {
            BlockCacheFactory.createBlockCache(c);
            Assert.fail("Should throw IllegalArgumentException when passing illegal value for bucket size");
        } catch (IllegalArgumentException e) {
        }
    }
}

