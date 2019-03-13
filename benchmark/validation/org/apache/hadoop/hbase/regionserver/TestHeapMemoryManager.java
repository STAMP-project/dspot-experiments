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


import DefaultHeapMemoryTuner.DEFAULT_MAX_STEP_VALUE;
import DefaultHeapMemoryTuner.NUM_PERIODS_TO_IGNORE;
import FlushLifeCycleTracker.DUMMY;
import HConstants.HFILE_BLOCK_CACHE_SIZE_KEY;
import HeapMemoryManager.BLOCK_CACHE_SIZE_MAX_RANGE_KEY;
import HeapMemoryManager.BLOCK_CACHE_SIZE_MIN_RANGE_KEY;
import HeapMemoryManager.HBASE_RS_HEAP_MEMORY_TUNER_CLASS;
import HeapMemoryManager.HBASE_RS_HEAP_MEMORY_TUNER_PERIOD;
import HeapMemoryManager.MEMSTORE_SIZE_MAX_RANGE_KEY;
import HeapMemoryManager.MEMSTORE_SIZE_MIN_RANGE_KEY;
import MemorySizeUtil.MEMSTORE_SIZE_KEY;
import MemorySizeUtil.MEMSTORE_SIZE_LOWER_LIMIT_KEY;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.CoordinatedStateManager;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.Server;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.ClusterConnection;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.io.hfile.BlockCache;
import org.apache.hadoop.hbase.io.hfile.BlockCacheKey;
import org.apache.hadoop.hbase.io.hfile.CacheStats;
import org.apache.hadoop.hbase.io.hfile.Cacheable;
import org.apache.hadoop.hbase.io.hfile.CachedBlock;
import org.apache.hadoop.hbase.io.hfile.ResizableBlockCache;
import org.apache.hadoop.hbase.regionserver.HeapMemoryManager.TunerContext;
import org.apache.hadoop.hbase.regionserver.HeapMemoryManager.TunerResult;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.zookeeper.ZKWatcher;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static DefaultHeapMemoryTuner.DEFAULT_MAX_STEP_VALUE;
import static DefaultHeapMemoryTuner.DEFAULT_MIN_STEP_VALUE;
import static FlushType.ABOVE_OFFHEAP_HIGHER_MARK;
import static FlushType.ABOVE_ONHEAP_HIGHER_MARK;
import static FlushType.ABOVE_ONHEAP_LOWER_MARK;
import static FlushType.NORMAL;


@Category({ RegionServerTests.class, SmallTests.class })
public class TestHeapMemoryManager {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHeapMemoryManager.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private long maxHeapSize = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();

    @Test
    public void testAutoTunerShouldBeOffWhenMaxMinRangesForMemstoreIsNotGiven() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_KEY, 0.02F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.03F);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        HeapMemoryManager manager = new HeapMemoryManager(new TestHeapMemoryManager.BlockCacheStub(0), new TestHeapMemoryManager.MemstoreFlusherStub(0), new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        Assert.assertFalse(manager.isTunerOn());
    }

    @Test
    public void testAutoTunerShouldBeOffWhenMaxMinRangesForBlockCacheIsNotGiven() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(HFILE_BLOCK_CACHE_SIZE_KEY, 0.02F);
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.03F);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        HeapMemoryManager manager = new HeapMemoryManager(new TestHeapMemoryManager.BlockCacheStub(0), new TestHeapMemoryManager.MemstoreFlusherStub(0), new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        Assert.assertFalse(manager.isTunerOn());
    }

    @Test
    public void testWhenMemstoreAndBlockCacheMaxMinChecksFails() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(0);
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.06F);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(0);
        try {
            new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
            Assert.fail();
        } catch (RuntimeException e) {
        }
        conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.2F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        try {
            new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
            Assert.fail();
        } catch (RuntimeException e) {
        }
    }

    @Test
    public void testWhenClusterIsWriteHeavyWithEmptyMemstore() throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty block cache and memstore
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(0);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        memStoreFlusher.flushType = ABOVE_ONHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        // No changes should be made by tuner as we already have lot of empty space
        Assert.assertEquals(oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testHeapMemoryManagerWhenOffheapFlushesHappenUnderReadHeavyCase() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_LOWER_LIMIT_KEY, 0.7F);
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf, true);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty memstore and but nearly filled block cache
        blockCache.setTestBlockSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        regionServerAccounting.setTestMemstoreSize(0);
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        float maxStepValue = DEFAULT_MIN_STEP_VALUE;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // do some offheap flushes also. So there should be decrease in memstore but
        // not as that when we don't have offheap flushes
        memStoreFlusher.flushType = ABOVE_OFFHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldBlockCacheSize, blockCache.maxSize);
        oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        oldBlockCacheSize = blockCache.maxSize;
        // Do some more evictions before the next run of HeapMemoryTuner
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testHeapMemoryManagerWithOffheapMemstoreAndMixedWorkload() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_LOWER_LIMIT_KEY, 0.7F);
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf, true);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty memstore and but nearly filled block cache
        blockCache.setTestBlockSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        regionServerAccounting.setTestMemstoreSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        float maxStepValue = DEFAULT_MIN_STEP_VALUE;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // do some offheap flushes also. So there should be decrease in memstore but
        // not as that when we don't have offheap flushes
        memStoreFlusher.flushType = ABOVE_OFFHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldBlockCacheSize, blockCache.maxSize);
        oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        oldBlockCacheSize = blockCache.maxSize;
        // change memstore size
        // regionServerAccounting.setTestMemstoreSize((long)(maxHeapSize * 0.4 * 0.8));
        // The memstore size would have decreased. Now again do some flushes and ensure the
        // flushes are due to onheap overhead. This should once again call for increase in
        // memstore size but that increase should be to the safe size
        memStoreFlusher.flushType = ABOVE_ONHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testWhenClusterIsReadHeavyWithEmptyBlockCache() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty block cache and memstore
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(0);
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        // No changes should be made by tuner as we already have lot of empty space
        Assert.assertEquals(oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testWhenClusterIsWriteHeavy() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty block cache and but nearly filled memstore
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(DEFAULT_MAX_STEP_VALUE, oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-(DEFAULT_MAX_STEP_VALUE)), oldBlockCacheSize, blockCache.maxSize);
        oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        oldBlockCacheSize = blockCache.maxSize;
        // Do some more flushes before the next run of HeapMemoryTuner
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(DEFAULT_MAX_STEP_VALUE, oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-(DEFAULT_MAX_STEP_VALUE)), oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testWhenClusterIsWriteHeavyWithOffheapMemstore() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty block cache and but nearly filled memstore
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        // this should not change anything with onheap memstore
        memStoreFlusher.flushType = ABOVE_OFFHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        // No changes should be made by tuner as we already have lot of empty space
        Assert.assertEquals(oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testWhenClusterIsReadHeavy() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_LOWER_LIMIT_KEY, 0.7F);
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Empty memstore and but nearly filled block cache
        blockCache.setTestBlockSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        regionServerAccounting.setTestMemstoreSize(0);
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), new TestHeapMemoryManager.RegionServerAccountingStub(conf));
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        long oldMemstoreLowerMarkSize = (7 * oldMemstoreHeapSize) / 10;
        long maxTuneSize = oldMemstoreHeapSize - ((oldMemstoreLowerMarkSize + oldMemstoreHeapSize) / 2);
        float maxStepValue = (maxTuneSize * 1.0F) / oldMemstoreHeapSize;
        maxStepValue = (maxStepValue > (DEFAULT_MAX_STEP_VALUE)) ? DEFAULT_MAX_STEP_VALUE : maxStepValue;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldBlockCacheSize, blockCache.maxSize);
        oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        oldBlockCacheSize = blockCache.maxSize;
        oldMemstoreLowerMarkSize = (7 * oldMemstoreHeapSize) / 10;
        maxTuneSize = oldMemstoreHeapSize - ((oldMemstoreLowerMarkSize + oldMemstoreHeapSize) / 2);
        maxStepValue = (maxTuneSize * 1.0F) / oldMemstoreHeapSize;
        maxStepValue = (maxStepValue > (DEFAULT_MAX_STEP_VALUE)) ? DEFAULT_MAX_STEP_VALUE : maxStepValue;
        // Do some more evictions before the next run of HeapMemoryTuner
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-maxStepValue), oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(maxStepValue, oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testWhenClusterIsHavingMoreWritesThanReads() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        // Both memstore and block cache are nearly filled
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        blockCache.setTestBlockSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        // No changes should happen as there is undefined increase in flushes and evictions
        Assert.assertEquals(oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
        // Do some more flushes before the next run of HeapMemoryTuner
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta(DEFAULT_MAX_STEP_VALUE, oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-(DEFAULT_MAX_STEP_VALUE)), oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testBlockedFlushesIncreaseMemstoreInSteadyState() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        TestHeapMemoryManager.RegionServerAccountingStub regionServerAccounting = new TestHeapMemoryManager.RegionServerAccountingStub(conf);
        // Both memstore and block cache are nearly filled
        blockCache.setTestBlockSize(0);
        regionServerAccounting.setTestMemstoreSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        blockCache.setTestBlockSize(((long) (((maxHeapSize) * 0.4) * 0.8)));
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), regionServerAccounting);
        long oldMemstoreHeapSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        memStoreFlusher.flushType = ABOVE_ONHEAP_LOWER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        memStoreFlusher.requestFlush(null, false, DUMMY);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        // No changes should happen as there is undefined increase in flushes and evictions
        Assert.assertEquals(oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
        // Flushes that block updates
        memStoreFlusher.flushType = ABOVE_ONHEAP_HIGHER_MARK;
        memStoreFlusher.requestFlush(null, false, DUMMY);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        blockCache.evictBlock(null);
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        assertHeapSpaceDelta(DEFAULT_MAX_STEP_VALUE, oldMemstoreHeapSize, memStoreFlusher.memstoreSize);
        assertHeapSpaceDelta((-(DEFAULT_MAX_STEP_VALUE)), oldBlockCacheSize, blockCache.maxSize);
    }

    @Test
    public void testPluggingInHeapMemoryTuner() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.78F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.05F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.75F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.02F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        conf.setClass(HBASE_RS_HEAP_MEMORY_TUNER_CLASS, TestHeapMemoryManager.CustomHeapMemoryTuner.class, HeapMemoryTuner.class);
        // Let the system start with default values for memstore heap and block cache size.
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), new TestHeapMemoryManager.RegionServerAccountingStub(conf));
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        // Now we wants to be in write mode. Set bigger memstore size from CustomHeapMemoryTuner
        TestHeapMemoryManager.CustomHeapMemoryTuner.memstoreSize = 0.78F;
        TestHeapMemoryManager.CustomHeapMemoryTuner.blockCacheSize = 0.02F;
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpace(0.78F, memStoreFlusher.memstoreSize);// Memstore

        assertHeapSpace(0.02F, blockCache.maxSize);// BlockCache

        // Now we wants to be in read mode. Set bigger memstore size from CustomHeapMemoryTuner
        TestHeapMemoryManager.CustomHeapMemoryTuner.blockCacheSize = 0.75F;
        TestHeapMemoryManager.CustomHeapMemoryTuner.memstoreSize = 0.05F;
        // Allow the tuner to run once and do necessary memory up
        waitForTune(memStoreFlusher, memStoreFlusher.memstoreSize);
        assertHeapSpace(0.75F, blockCache.maxSize);// BlockCache

        assertHeapSpace(0.05F, memStoreFlusher.memstoreSize);// Memstore

    }

    @Test
    public void testWhenSizeGivenByHeapTunerGoesOutsideRange() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        conf.setClass(HBASE_RS_HEAP_MEMORY_TUNER_CLASS, TestHeapMemoryManager.CustomHeapMemoryTuner.class, HeapMemoryTuner.class);
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), new TestHeapMemoryManager.RegionServerAccountingStub(conf));
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        TestHeapMemoryManager.CustomHeapMemoryTuner.memstoreSize = 0.78F;
        TestHeapMemoryManager.CustomHeapMemoryTuner.blockCacheSize = 0.02F;
        Thread.sleep(1500);// Allow the tuner to run once and do necessary memory up

        // Even if the tuner says to set the memstore to 78%, HBase makes it as 70% as that is the
        // upper bound. Same with block cache as 10% is the lower bound.
        assertHeapSpace(0.7F, memStoreFlusher.memstoreSize);
        assertHeapSpace(0.1F, blockCache.maxSize);
    }

    @Test
    public void testWhenCombinedHeapSizesFromTunerGoesOutSideMaxLimit() throws Exception {
        TestHeapMemoryManager.BlockCacheStub blockCache = new TestHeapMemoryManager.BlockCacheStub(((long) ((maxHeapSize) * 0.4)));
        TestHeapMemoryManager.MemstoreFlusherStub memStoreFlusher = new TestHeapMemoryManager.MemstoreFlusherStub(((long) ((maxHeapSize) * 0.4)));
        Configuration conf = HBaseConfiguration.create();
        conf.setFloat(MEMSTORE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(MEMSTORE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setFloat(BLOCK_CACHE_SIZE_MAX_RANGE_KEY, 0.7F);
        conf.setFloat(BLOCK_CACHE_SIZE_MIN_RANGE_KEY, 0.1F);
        conf.setLong(HBASE_RS_HEAP_MEMORY_TUNER_PERIOD, 1000);
        conf.setInt(NUM_PERIODS_TO_IGNORE, 0);
        conf.setClass(HBASE_RS_HEAP_MEMORY_TUNER_CLASS, TestHeapMemoryManager.CustomHeapMemoryTuner.class, HeapMemoryTuner.class);
        HeapMemoryManager heapMemoryManager = new HeapMemoryManager(blockCache, memStoreFlusher, new TestHeapMemoryManager.RegionServerStub(conf), new TestHeapMemoryManager.RegionServerAccountingStub(conf));
        long oldMemstoreSize = memStoreFlusher.memstoreSize;
        long oldBlockCacheSize = blockCache.maxSize;
        final ChoreService choreService = new ChoreService("TEST_SERVER_NAME");
        heapMemoryManager.start(choreService);
        TestHeapMemoryManager.CustomHeapMemoryTuner.memstoreSize = 0.7F;
        TestHeapMemoryManager.CustomHeapMemoryTuner.blockCacheSize = 0.3F;
        // Allow the tuner to run once and do necessary memory up
        Thread.sleep(1500);
        Assert.assertEquals(oldMemstoreSize, memStoreFlusher.memstoreSize);
        Assert.assertEquals(oldBlockCacheSize, blockCache.maxSize);
    }

    private static class BlockCacheStub implements ResizableBlockCache {
        CacheStats stats = new CacheStats("test");

        long maxSize = 0;

        private long testBlockSize = 0;

        public BlockCacheStub(long size) {
            this.maxSize = size;
        }

        @Override
        public void cacheBlock(BlockCacheKey cacheKey, Cacheable buf, boolean inMemory) {
        }

        @Override
        public void cacheBlock(BlockCacheKey cacheKey, Cacheable buf) {
        }

        @Override
        public Cacheable getBlock(BlockCacheKey cacheKey, boolean caching, boolean repeat, boolean updateCacheMetrics) {
            return null;
        }

        @Override
        public boolean evictBlock(BlockCacheKey cacheKey) {
            stats.evicted(0, (cacheKey != null ? cacheKey.isPrimary() : true));
            return false;
        }

        @Override
        public int evictBlocksByHfileName(String hfileName) {
            stats.evicted(0, true);// Just assuming only one block for file here.

            return 0;
        }

        @Override
        public CacheStats getStats() {
            return this.stats;
        }

        @Override
        public void shutdown() {
        }

        @Override
        public long size() {
            return 0;
        }

        @Override
        public long getMaxSize() {
            return 0;
        }

        @Override
        public long getFreeSize() {
            return 0;
        }

        @Override
        public long getCurrentSize() {
            return this.testBlockSize;
        }

        @Override
        public long getCurrentDataSize() {
            return 0;
        }

        @Override
        public long getBlockCount() {
            return 0;
        }

        @Override
        public long getDataBlockCount() {
            return 0;
        }

        @Override
        public void setMaxSize(long size) {
            this.maxSize = size;
        }

        @Override
        public Iterator<CachedBlock> iterator() {
            return null;
        }

        @Override
        public BlockCache[] getBlockCaches() {
            return null;
        }

        @Override
        public void returnBlock(BlockCacheKey cacheKey, Cacheable buf) {
        }

        public void setTestBlockSize(long testBlockSize) {
            this.testBlockSize = testBlockSize;
        }
    }

    private static class MemstoreFlusherStub implements FlushRequester {
        long memstoreSize;

        FlushRequestListener listener;

        FlushType flushType = NORMAL;

        public MemstoreFlusherStub(long memstoreSize) {
            this.memstoreSize = memstoreSize;
        }

        @Override
        public boolean requestFlush(HRegion region, boolean forceFlushAllStores, FlushLifeCycleTracker tracker) {
            this.listener.flushRequested(flushType, region);
            return true;
        }

        @Override
        public boolean requestDelayedFlush(HRegion region, long delay, boolean forceFlushAllStores) {
            return true;
        }

        @Override
        public void registerFlushRequestListener(FlushRequestListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean unregisterFlushRequestListener(FlushRequestListener listener) {
            return false;
        }

        @Override
        public void setGlobalMemStoreLimit(long globalMemStoreSize) {
            this.memstoreSize = globalMemStoreSize;
        }
    }

    private static class RegionServerStub implements Server {
        private Configuration conf;

        private boolean stopped = false;

        public RegionServerStub(Configuration conf) {
            this.conf = conf;
        }

        @Override
        public void abort(String why, Throwable e) {
        }

        @Override
        public boolean isAborted() {
            return false;
        }

        @Override
        public void stop(String why) {
            this.stopped = true;
        }

        @Override
        public boolean isStopped() {
            return this.stopped;
        }

        @Override
        public Configuration getConfiguration() {
            return this.conf;
        }

        @Override
        public ZKWatcher getZooKeeper() {
            return null;
        }

        @Override
        public CoordinatedStateManager getCoordinatedStateManager() {
            return null;
        }

        @Override
        public ClusterConnection getConnection() {
            return null;
        }

        @Override
        public ServerName getServerName() {
            return ServerName.valueOf("server1", 4000, 12345);
        }

        @Override
        public ChoreService getChoreService() {
            return null;
        }

        @Override
        public ClusterConnection getClusterConnection() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public FileSystem getFileSystem() {
            return null;
        }

        @Override
        public boolean isStopping() {
            return false;
        }

        @Override
        public Connection createConnection(Configuration conf) throws IOException {
            return null;
        }
    }

    static class CustomHeapMemoryTuner implements HeapMemoryTuner {
        static float blockCacheSize = 0.4F;

        static float memstoreSize = 0.4F;

        @Override
        public Configuration getConf() {
            return null;
        }

        @Override
        public void setConf(Configuration arg0) {
        }

        @Override
        public TunerResult tune(TunerContext context) {
            TunerResult result = new TunerResult(true);
            result.setBlockCacheSize(TestHeapMemoryManager.CustomHeapMemoryTuner.blockCacheSize);
            result.setMemStoreSize(TestHeapMemoryManager.CustomHeapMemoryTuner.memstoreSize);
            return result;
        }
    }

    private static class RegionServerAccountingStub extends RegionServerAccounting {
        boolean offheap;

        public RegionServerAccountingStub(Configuration conf) {
            super(conf);
        }

        public RegionServerAccountingStub(Configuration conf, boolean offheap) {
            super(conf);
            this.offheap = offheap;
        }

        private long testMemstoreSize = 0;

        @Override
        public long getGlobalMemStoreDataSize() {
            return testMemstoreSize;
        }

        @Override
        public long getGlobalMemStoreHeapSize() {
            return testMemstoreSize;
        }

        @Override
        public boolean isOffheap() {
            return offheap;
        }

        public void setTestMemstoreSize(long testMemstoreSize) {
            this.testMemstoreSize = testMemstoreSize;
        }
    }
}

