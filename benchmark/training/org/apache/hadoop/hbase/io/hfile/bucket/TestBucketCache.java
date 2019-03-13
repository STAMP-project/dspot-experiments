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
package org.apache.hadoop.hbase.io.hfile.bucket;


import BucketCache.ACCEPT_FACTOR_CONFIG_NAME;
import BucketCache.BucketEntry;
import BucketCache.DEFAULT_MIN_FACTOR;
import BucketCache.DEFAULT_SINGLE_FACTOR;
import BucketCache.EXTRA_FREE_FACTOR_CONFIG_NAME;
import BucketCache.MEMORY_FACTOR_CONFIG_NAME;
import BucketCache.MIN_FACTOR_CONFIG_NAME;
import BucketCache.MULTI_FACTOR_CONFIG_NAME;
import BucketCache.SINGLE_FACTOR_CONFIG_NAME;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.io.hfile.BlockCacheKey;
import org.apache.hadoop.hbase.io.hfile.BlockType;
import org.apache.hadoop.hbase.io.hfile.CacheTestUtils;
import org.apache.hadoop.hbase.io.hfile.Cacheable;
import org.apache.hadoop.hbase.io.hfile.HFileBlock;
import org.apache.hadoop.hbase.io.hfile.HFileContext;
import org.apache.hadoop.hbase.io.hfile.HFileContextBuilder;
import org.apache.hadoop.hbase.io.hfile.bucket.BucketAllocator.BucketSizeInfo;
import org.apache.hadoop.hbase.io.hfile.bucket.BucketAllocator.IndexStatistics;
import org.apache.hadoop.hbase.testclassification.IOTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hbase.thirdparty.com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BucketCache.ACCEPT_FACTOR_CONFIG_NAME;
import static BucketCache.DEFAULT_WRITER_QUEUE_ITEMS;
import static BucketCache.DEFAULT_WRITER_THREADS;
import static BucketCache.EXTRA_FREE_FACTOR_CONFIG_NAME;
import static BucketCache.MEMORY_FACTOR_CONFIG_NAME;
import static BucketCache.MIN_FACTOR_CONFIG_NAME;
import static BucketCache.MULTI_FACTOR_CONFIG_NAME;
import static BucketCache.SINGLE_FACTOR_CONFIG_NAME;


/**
 * Basic test of BucketCache.Puts and gets.
 * <p>
 * Tests will ensure that blocks' data correctness under several threads concurrency
 */
@RunWith(Parameterized.class)
@Category({ IOTests.class, MediumTests.class })
public class TestBucketCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBucketCache.class);

    private static final Random RAND = new Random();

    @Parameterized.Parameter(0)
    public int constructedBlockSize;

    @Parameterized.Parameter(1)
    public int[] constructedBlockSizes;

    BucketCache cache;

    final int CACHE_SIZE = 1000000;

    final int NUM_BLOCKS = 100;

    final int BLOCK_SIZE = (CACHE_SIZE) / (NUM_BLOCKS);

    final int NUM_THREADS = 100;

    final int NUM_QUERIES = 10000;

    final long capacitySize = (32 * 1024) * 1024;

    final int writeThreads = DEFAULT_WRITER_THREADS;

    final int writerQLen = DEFAULT_WRITER_QUEUE_ITEMS;

    String ioEngineName = "offheap";

    String persistencePath = null;

    private static class MockedBucketCache extends BucketCache {
        public MockedBucketCache(String ioEngineName, long capacity, int blockSize, int[] bucketSizes, int writerThreads, int writerQLen, String persistencePath) throws FileNotFoundException, IOException {
            super(ioEngineName, capacity, blockSize, bucketSizes, writerThreads, writerQLen, persistencePath);
            super.wait_when_cache = true;
        }

        @Override
        public void cacheBlock(BlockCacheKey cacheKey, Cacheable buf, boolean inMemory) {
            super.cacheBlock(cacheKey, buf, inMemory);
        }

        @Override
        public void cacheBlock(BlockCacheKey cacheKey, Cacheable buf) {
            super.cacheBlock(cacheKey, buf);
        }
    }

    @Test
    public void testBucketAllocator() throws BucketAllocatorException {
        BucketAllocator mAllocator = cache.getAllocator();
        /* Test the allocator first */
        final List<Integer> BLOCKSIZES = Arrays.asList((4 * 1024), (8 * 1024), (64 * 1024), (96 * 1024));
        boolean full = false;
        ArrayList<Long> allocations = new ArrayList<>();
        // Fill the allocated extents by choosing a random blocksize. Continues selecting blocks until
        // the cache is completely filled.
        List<Integer> tmp = new ArrayList<>(BLOCKSIZES);
        while (!full) {
            Integer blockSize = null;
            try {
                blockSize = TestBucketCache.randFrom(tmp);
                allocations.add(mAllocator.allocateBlock(blockSize));
            } catch (CacheFullException cfe) {
                tmp.remove(blockSize);
                if (tmp.isEmpty())
                    full = true;

            }
        } 
        for (Integer blockSize : BLOCKSIZES) {
            BucketSizeInfo bucketSizeInfo = mAllocator.roundUpToBucketSizeInfo(blockSize);
            IndexStatistics indexStatistics = bucketSizeInfo.statistics();
            Assert.assertEquals(("unexpected freeCount for " + bucketSizeInfo), 0, indexStatistics.freeCount());
        }
        for (long offset : allocations) {
            Assert.assertEquals(mAllocator.sizeOfAllocation(offset), mAllocator.freeBlock(offset));
        }
        Assert.assertEquals(0, mAllocator.getUsedSize());
    }

    @Test
    public void testCacheSimple() throws Exception {
        CacheTestUtils.testCacheSimple(cache, BLOCK_SIZE, NUM_QUERIES);
    }

    @Test
    public void testCacheMultiThreadedSingleKey() throws Exception {
        CacheTestUtils.hammerSingleKey(cache, BLOCK_SIZE, (2 * (NUM_THREADS)), (2 * (NUM_QUERIES)));
    }

    @Test
    public void testHeapSizeChanges() throws Exception {
        cache.stopWriterThreads();
        CacheTestUtils.testHeapSizeChanges(cache, BLOCK_SIZE);
    }

    @Test
    public void testMemoryLeak() throws Exception {
        final BlockCacheKey cacheKey = new BlockCacheKey("dummy", 1L);
        cacheAndWaitUntilFlushedToBucket(cache, cacheKey, new CacheTestUtils.ByteArrayCacheable(new byte[10]));
        long lockId = cache.backingMap.get(cacheKey).offset();
        ReentrantReadWriteLock lock = cache.offsetLock.getLock(lockId);
        lock.writeLock().lock();
        Thread evictThread = new Thread("evict-block") {
            @Override
            public void run() {
                cache.evictBlock(cacheKey);
            }
        };
        evictThread.start();
        cache.offsetLock.waitForWaiters(lockId, 1);
        cache.blockEvicted(cacheKey, cache.backingMap.remove(cacheKey), true);
        cacheAndWaitUntilFlushedToBucket(cache, cacheKey, new CacheTestUtils.ByteArrayCacheable(new byte[10]));
        lock.writeLock().unlock();
        evictThread.join();
        Assert.assertEquals(1L, cache.getBlockCount());
        Assert.assertTrue(((cache.getCurrentSize()) > 0L));
        Assert.assertTrue("We should have a block!", cache.iterator().hasNext());
    }

    @Test
    public void testRetrieveFromFile() throws Exception {
        HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
        Path testDir = getDataTestDir();
        TEST_UTIL.getTestFileSystem().mkdirs(testDir);
        String ioEngineName = ("file:" + testDir) + "/bucket.cache";
        String persistencePath = testDir + "/bucket.persistence";
        BucketCache bucketCache = new BucketCache(ioEngineName, capacitySize, constructedBlockSize, constructedBlockSizes, writeThreads, writerQLen, persistencePath);
        long usedSize = bucketCache.getAllocator().getUsedSize();
        Assert.assertEquals(0, usedSize);
        CacheTestUtils.HFileBlockPair[] blocks = CacheTestUtils.generateHFileBlocks(constructedBlockSize, 1);
        // Add blocks
        for (CacheTestUtils.HFileBlockPair block : blocks) {
            bucketCache.cacheBlock(block.getBlockName(), block.getBlock());
        }
        for (CacheTestUtils.HFileBlockPair block : blocks) {
            cacheAndWaitUntilFlushedToBucket(bucketCache, block.getBlockName(), block.getBlock());
        }
        usedSize = bucketCache.getAllocator().getUsedSize();
        Assert.assertNotEquals(0, usedSize);
        // persist cache to file
        bucketCache.shutdown();
        Assert.assertTrue(new File(persistencePath).exists());
        // restore cache from file
        bucketCache = new BucketCache(ioEngineName, capacitySize, constructedBlockSize, constructedBlockSizes, writeThreads, writerQLen, persistencePath);
        Assert.assertFalse(new File(persistencePath).exists());
        Assert.assertEquals(usedSize, bucketCache.getAllocator().getUsedSize());
        // persist cache to file
        bucketCache.shutdown();
        Assert.assertTrue(new File(persistencePath).exists());
        // reconfig buckets sizes, the biggest bucket is small than constructedBlockSize (8k or 16k)
        // so it can't restore cache from file
        int[] smallBucketSizes = new int[]{ (2 * 1024) + 1024, (4 * 1024) + 1024 };
        bucketCache = new BucketCache(ioEngineName, capacitySize, constructedBlockSize, smallBucketSizes, writeThreads, writerQLen, persistencePath);
        Assert.assertFalse(new File(persistencePath).exists());
        Assert.assertEquals(0, bucketCache.getAllocator().getUsedSize());
        Assert.assertEquals(0, bucketCache.backingMap.size());
        cleanupTestDir();
    }

    @Test
    public void testBucketAllocatorLargeBuckets() throws BucketAllocatorException {
        long availableSpace = ((20 * 1024L) * 1024) * 1024;
        int[] bucketSizes = new int[]{ 1024, 1024 * 1024, (1024 * 1024) * 1024 };
        BucketAllocator allocator = new BucketAllocator(availableSpace, bucketSizes);
        Assert.assertTrue(((allocator.getBuckets().length) > 0));
    }

    @Test
    public void testGetPartitionSize() throws IOException {
        // Test default values
        validateGetPartitionSize(cache, DEFAULT_SINGLE_FACTOR, DEFAULT_MIN_FACTOR);
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MIN_FACTOR_CONFIG_NAME, 0.5F);
        conf.setFloat(SINGLE_FACTOR_CONFIG_NAME, 0.1F);
        conf.setFloat(MULTI_FACTOR_CONFIG_NAME, 0.7F);
        conf.setFloat(MEMORY_FACTOR_CONFIG_NAME, 0.2F);
        BucketCache cache = new BucketCache(ioEngineName, capacitySize, constructedBlockSize, constructedBlockSizes, writeThreads, writerQLen, persistencePath, 100, conf);
        validateGetPartitionSize(cache, 0.1F, 0.5F);
        validateGetPartitionSize(cache, 0.7F, 0.5F);
        validateGetPartitionSize(cache, 0.2F, 0.5F);
    }

    @Test
    public void testValidBucketCacheConfigs() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(ACCEPT_FACTOR_CONFIG_NAME, 0.9F);
        conf.setFloat(MIN_FACTOR_CONFIG_NAME, 0.5F);
        conf.setFloat(EXTRA_FREE_FACTOR_CONFIG_NAME, 0.5F);
        conf.setFloat(SINGLE_FACTOR_CONFIG_NAME, 0.1F);
        conf.setFloat(MULTI_FACTOR_CONFIG_NAME, 0.7F);
        conf.setFloat(MEMORY_FACTOR_CONFIG_NAME, 0.2F);
        BucketCache cache = new BucketCache(ioEngineName, capacitySize, constructedBlockSize, constructedBlockSizes, writeThreads, writerQLen, persistencePath, 100, conf);
        Assert.assertEquals(((ACCEPT_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.9F, cache.getAcceptableFactor(), 0);
        Assert.assertEquals(((MIN_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.5F, cache.getMinFactor(), 0);
        Assert.assertEquals(((EXTRA_FREE_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.5F, cache.getExtraFreeFactor(), 0);
        Assert.assertEquals(((SINGLE_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.1F, cache.getSingleFactor(), 0);
        Assert.assertEquals(((MULTI_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.7F, cache.getMultiFactor(), 0);
        Assert.assertEquals(((MEMORY_FACTOR_CONFIG_NAME) + " failed to propagate."), 0.2F, cache.getMemoryFactor(), 0);
    }

    @Test
    public void testInvalidAcceptFactorConfig() throws IOException {
        float[] configValues = new float[]{ -1.0F, 0.2F, 0.86F, 1.05F };
        boolean[] expectedOutcomes = new boolean[]{ false, false, true, false };
        Map<String, float[]> configMappings = ImmutableMap.of(ACCEPT_FACTOR_CONFIG_NAME, configValues);
        Configuration conf = HBaseConfiguration.create();
        checkConfigValues(conf, configMappings, expectedOutcomes);
    }

    @Test
    public void testInvalidMinFactorConfig() throws IOException {
        float[] configValues = new float[]{ -1.0F, 0.0F, 0.96F, 1.05F };
        // throws due to <0, in expected range, minFactor > acceptableFactor, > 1.0
        boolean[] expectedOutcomes = new boolean[]{ false, true, false, false };
        Map<String, float[]> configMappings = ImmutableMap.of(MIN_FACTOR_CONFIG_NAME, configValues);
        Configuration conf = HBaseConfiguration.create();
        checkConfigValues(conf, configMappings, expectedOutcomes);
    }

    @Test
    public void testInvalidExtraFreeFactorConfig() throws IOException {
        float[] configValues = new float[]{ -1.0F, 0.0F, 0.2F, 1.05F };
        // throws due to <0, in expected range, in expected range, config can be > 1.0
        boolean[] expectedOutcomes = new boolean[]{ false, true, true, true };
        Map<String, float[]> configMappings = ImmutableMap.of(EXTRA_FREE_FACTOR_CONFIG_NAME, configValues);
        Configuration conf = HBaseConfiguration.create();
        checkConfigValues(conf, configMappings, expectedOutcomes);
    }

    @Test
    public void testInvalidCacheSplitFactorConfig() throws IOException {
        float[] singleFactorConfigValues = new float[]{ 0.2F, 0.0F, -0.2F, 1.0F };
        float[] multiFactorConfigValues = new float[]{ 0.4F, 0.0F, 1.0F, 0.05F };
        float[] memoryFactorConfigValues = new float[]{ 0.4F, 0.0F, 0.2F, 0.5F };
        // All configs add up to 1.0 and are between 0 and 1.0, configs don't add to 1.0, configs can't be negative, configs don't add to 1.0
        boolean[] expectedOutcomes = new boolean[]{ true, false, false, false };
        Map<String, float[]> configMappings = ImmutableMap.of(SINGLE_FACTOR_CONFIG_NAME, singleFactorConfigValues, MULTI_FACTOR_CONFIG_NAME, multiFactorConfigValues, MEMORY_FACTOR_CONFIG_NAME, memoryFactorConfigValues);
        Configuration conf = HBaseConfiguration.create();
        checkConfigValues(conf, configMappings, expectedOutcomes);
    }

    @Test
    public void testOffsetProducesPositiveOutput() {
        // This number is picked because it produces negative output if the values isn't ensured to be positive.
        // See HBASE-18757 for more information.
        long testValue = 549888460800L;
        BucketCache.BucketEntry bucketEntry = new BucketCache.BucketEntry(testValue, 10, 10L, true);
        Assert.assertEquals(testValue, bucketEntry.offset());
    }

    @Test
    public void testCacheBlockNextBlockMetadataMissing() throws Exception {
        int size = 100;
        int length = (HConstants.HFILEBLOCK_HEADER_SIZE) + size;
        byte[] byteArr = new byte[length];
        ByteBuffer buf = ByteBuffer.wrap(byteArr, 0, size);
        HFileContext meta = new HFileContextBuilder().build();
        HFileBlock blockWithNextBlockMetadata = new HFileBlock(BlockType.DATA, size, size, (-1), buf, HFileBlock.FILL_HEADER, (-1), 52, (-1), meta);
        HFileBlock blockWithoutNextBlockMetadata = new HFileBlock(BlockType.DATA, size, size, (-1), buf, HFileBlock.FILL_HEADER, (-1), (-1), (-1), meta);
        BlockCacheKey key = new BlockCacheKey("key1", 0);
        ByteBuffer actualBuffer = ByteBuffer.allocate(length);
        ByteBuffer block1Buffer = ByteBuffer.allocate(length);
        ByteBuffer block2Buffer = ByteBuffer.allocate(length);
        blockWithNextBlockMetadata.serialize(block1Buffer, true);
        blockWithoutNextBlockMetadata.serialize(block2Buffer, true);
        // Add blockWithNextBlockMetadata, expect blockWithNextBlockMetadata back.
        CacheTestUtils.getBlockAndAssertEquals(cache, key, blockWithNextBlockMetadata, actualBuffer, block1Buffer);
        waitUntilFlushedToBucket(cache, key);
        // Add blockWithoutNextBlockMetada, expect blockWithNextBlockMetadata back.
        CacheTestUtils.getBlockAndAssertEquals(cache, key, blockWithoutNextBlockMetadata, actualBuffer, block1Buffer);
        // Clear and add blockWithoutNextBlockMetadata
        cache.evictBlock(key);
        Assert.assertNull(cache.getBlock(key, false, false, false));
        CacheTestUtils.getBlockAndAssertEquals(cache, key, blockWithoutNextBlockMetadata, actualBuffer, block2Buffer);
        waitUntilFlushedToBucket(cache, key);
        // Add blockWithNextBlockMetadata, expect blockWithNextBlockMetadata to replace.
        CacheTestUtils.getBlockAndAssertEquals(cache, key, blockWithNextBlockMetadata, actualBuffer, block1Buffer);
    }
}

