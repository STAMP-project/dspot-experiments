/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.cache.stats;


import EvictionConfig.MaxSizePolicy;
import GroupProperty.PARTITION_COUNT;
import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.CacheTestSupport;
import com.hazelcast.cache.ICache;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CacheStatsTest extends CacheTestSupport {
    protected TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();

    @Test
    public void testGettingStatistics() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        Assert.assertNotNull(stats);
    }

    @Test
    public void testStatisticsDisabled() {
        long now = System.currentTimeMillis();
        CacheConfig cacheConfig = createCacheConfig();
        cacheConfig.setStatisticsEnabled(false);
        ICache<Integer, String> cache = createCache(cacheConfig);
        CacheStatistics stats = cache.getLocalCacheStatistics();
        Assert.assertTrue(((stats.getCreationTime()) >= now));
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.get(i);
        }
        for (int i = 0; i < 10; i++) {
            cache.remove(i);
        }
        Assert.assertEquals(0, stats.getCacheHits());
        Assert.assertEquals(0, stats.getCacheHitPercentage(), 0.0F);
        Assert.assertEquals(0, stats.getCacheMisses());
        Assert.assertEquals(0, stats.getCacheMissPercentage(), 0.0F);
        Assert.assertEquals(0, stats.getCacheGets());
        Assert.assertEquals(0, stats.getAverageGetTime(), 0.0F);
        Assert.assertEquals(0, stats.getCachePuts());
        Assert.assertEquals(0, stats.getAveragePutTime(), 0.0F);
        Assert.assertEquals(0, stats.getCacheRemovals());
        Assert.assertEquals(0, stats.getAverageRemoveTime(), 0.0F);
        Assert.assertEquals(0, stats.getLastAccessTime());
        Assert.assertEquals(0, stats.getLastUpdateTime());
    }

    @Test
    public void testCreationTime() {
        long now = System.currentTimeMillis();
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        Assert.assertTrue(((stats.getCreationTime()) >= now));
    }

    @Test
    public void testPutStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCachePuts());
    }

    @Test
    public void testPutStat_whenPutAsync() throws Exception {
        ICache<Integer, String> cache = createCache();
        final CacheStatistics stats = cache.getLocalCacheStatistics();
        final long ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.putAsync(i, ("Value-" + i)).get();
        }
        HazelcastTestSupport.assertEqualsEventually(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return stats.getCachePuts();
            }
        }, ENTRY_COUNT);
    }

    @Test
    public void testPutStat_whenPutIfAbsent_andKeysDoNotExist() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.putIfAbsent(i, ("Value-" + i));
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCachePuts());
    }

    @Test
    public void testPutStat_whenPutIfAbsent_andKeysExist() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.putIfAbsent(i, ("NewValue-" + i));
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCachePuts());
    }

    @Test
    public void testAveragePutTimeStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        long start = System.nanoTime();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        long end = System.nanoTime();
        float avgPutTime = TimeUnit.NANOSECONDS.toMicros((end - start));
        Assert.assertTrue(((stats.getAveragePutTime()) > 0));
        Assert.assertTrue(((stats.getAveragePutTime()) < avgPutTime));
    }

    @Test
    public void testGetStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < (2 * ENTRY_COUNT); i++) {
            cache.get(i);
        }
        Assert.assertEquals((2 * ENTRY_COUNT), stats.getCacheGets());
    }

    @Test
    public void testGetStat_whenGetAsync() throws Exception {
        ICache<Integer, String> cache = createCache();
        final CacheStatistics stats = cache.getLocalCacheStatistics();
        final long ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < (2 * ENTRY_COUNT); i++) {
            cache.getAsync(i).get();
        }
        HazelcastTestSupport.assertEqualsEventually(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return stats.getCacheGets();
            }
        }, (2 * ENTRY_COUNT));
    }

    @Test
    public void testAverageGetTimeStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        long start = System.nanoTime();
        for (int i = 0; i < (2 * ENTRY_COUNT); i++) {
            cache.get(i);
        }
        long end = System.nanoTime();
        float avgGetTime = TimeUnit.NANOSECONDS.toMicros((end - start));
        Assert.assertTrue(((stats.getAverageGetTime()) > 0));
        Assert.assertTrue(((stats.getAverageGetTime()) < avgGetTime));
    }

    @Test
    public void testRemoveStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < (2 * ENTRY_COUNT); i++) {
            cache.remove(i);
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCacheRemovals());
    }

    @Test
    public void testRemoveStat_whenRemoveAsync() throws Exception {
        ICache<Integer, String> cache = createCache();
        final CacheStatistics stats = cache.getLocalCacheStatistics();
        final long ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < (2 * ENTRY_COUNT); i++) {
            cache.removeAsync(i).get();
        }
        HazelcastTestSupport.assertEqualsEventually(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return stats.getCacheRemovals();
            }
        }, ENTRY_COUNT);
    }

    @Test
    public void testAverageRemoveTimeStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        long start = System.nanoTime();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.remove(i);
        }
        long end = System.nanoTime();
        float avgRemoveTime = TimeUnit.NANOSECONDS.toMicros((end - start));
        Assert.assertTrue(((stats.getAverageRemoveTime()) > 0));
        Assert.assertTrue(((stats.getAverageRemoveTime()) < avgRemoveTime));
        float currentAverageRemoveTime = stats.getAverageRemoveTime();
        HazelcastTestSupport.sleepAtLeastMillis(1);
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.remove(i);
        }
        // Latest removes has no effect since keys are already removed at previous loop
        Assert.assertEquals(currentAverageRemoveTime, stats.getAverageRemoveTime(), 0.0F);
    }

    @Test
    public void testHitStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        final int GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.get(i);
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCacheHits());
    }

    @Test
    public void testHitStat_whenGetAsync() throws Exception {
        ICache<Integer, String> cache = createCache();
        final CacheStatistics stats = cache.getLocalCacheStatistics();
        final long ENTRY_COUNT = 100;
        final long GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.getAsync(i).get();
        }
        HazelcastTestSupport.assertEqualsEventually(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return stats.getCacheHits();
            }
        }, ENTRY_COUNT);
    }

    @Test
    public void testHitPercentageStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        final int GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.get(i);
        }
        float expectedHitPercentage = (((float) (ENTRY_COUNT)) / GET_COUNT) * 100.0F;
        Assert.assertEquals(expectedHitPercentage, stats.getCacheHitPercentage(), 0.0F);
    }

    @Test
    public void testMissStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        final int GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.get(i);
        }
        Assert.assertEquals((GET_COUNT - ENTRY_COUNT), stats.getCacheMisses());
    }

    @Test
    public void testMissStat_whenGetAsync() throws Exception {
        ICache<Integer, String> cache = createCache();
        final CacheStatistics stats = cache.getLocalCacheStatistics();
        final long ENTRY_COUNT = 100;
        final long GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.getAsync(i).get();
        }
        HazelcastTestSupport.assertEqualsEventually(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return stats.getCacheMisses();
            }
        }, (GET_COUNT - ENTRY_COUNT));
    }

    @Test
    public void testMissStat_whenPutIfAbsent_andKeysDoNotExist() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.putIfAbsent(i, ("Value-" + i));
        }
        Assert.assertEquals(ENTRY_COUNT, stats.getCacheMisses());
    }

    @Test
    public void testMissStat_whenPutIfAbsent_andKeysAlreadyExist() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.putIfAbsent(i, ("NewValue-" + i));
        }
        Assert.assertEquals(0, stats.getCacheMisses());
    }

    @Test
    public void testMissPercentageStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        final int GET_COUNT = 3 * ENTRY_COUNT;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        for (int i = 0; i < GET_COUNT; i++) {
            cache.get(i);
        }
        float expectedMissPercentage = (((float) (GET_COUNT - ENTRY_COUNT)) / GET_COUNT) * 100.0F;
        Assert.assertEquals(expectedMissPercentage, stats.getCacheMissPercentage(), 0.0F);
    }

    @Test
    public void testLastAccessTimeStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        long start;
        long end;
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        Assert.assertEquals(0, stats.getLastAccessTime());
        start = System.currentTimeMillis();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.get(i);
        }
        end = System.currentTimeMillis();
        // Hits effect last access time
        Assert.assertTrue(((stats.getLastAccessTime()) >= start));
        Assert.assertTrue(((stats.getLastAccessTime()) <= end));
        long currentLastAccessTime = stats.getLastAccessTime();
        HazelcastTestSupport.sleepAtLeastMillis(1);
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.remove(i);
        }
        // Removes has no effect on last access time
        Assert.assertEquals(currentLastAccessTime, stats.getLastAccessTime());
        HazelcastTestSupport.sleepAtLeastMillis(1);
        start = System.currentTimeMillis();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.get(i);
        }
        end = System.currentTimeMillis();
        // Misses also effect last access time
        Assert.assertTrue(((stats.getLastAccessTime()) >= start));
        Assert.assertTrue(((stats.getLastAccessTime()) <= end));
    }

    @Test
    public void testLastUpdateTimeStat() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        final int ENTRY_COUNT = 100;
        long start;
        long end;
        start = System.currentTimeMillis();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.put(i, ("Value-" + i));
        }
        end = System.currentTimeMillis();
        Assert.assertTrue(((stats.getLastUpdateTime()) >= start));
        Assert.assertTrue(((stats.getLastUpdateTime()) <= end));
        start = System.currentTimeMillis();
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.remove(i);
        }
        end = System.currentTimeMillis();
        Assert.assertTrue(((stats.getLastUpdateTime()) >= start));
        Assert.assertTrue(((stats.getLastUpdateTime()) <= end));
        long currentLastUpdateTime = stats.getLastUpdateTime();
        HazelcastTestSupport.sleepAtLeastMillis(1);
        for (int i = 0; i < ENTRY_COUNT; i++) {
            cache.remove(i);
        }
        // Latest removes has no effect since keys are already removed at previous loop
        Assert.assertEquals(currentLastUpdateTime, stats.getLastUpdateTime());
    }

    @Test
    public void testOwnedEntryCountWhenThereIsNoBackup() {
        doTestForOwnedEntryCount(false, false);
    }

    @Test
    public void testOwnedEntryCountWhenThereAreBackupsOnStaticCluster() {
        doTestForOwnedEntryCount(true, false);
    }

    @Test
    public void testOwnedEntryCountWhenThereAreBackupsOnDynamicCluster() {
        doTestForOwnedEntryCount(true, true);
    }

    @Test
    public void testExpirations() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        // TODO Produce a case that triggers expirations and verify the expiration count
        // At the moment, we are just verifying that this stats is supported
        stats.getCacheEvictions();
    }

    @Test
    public void testEvictions() {
        int partitionCount = 2;
        int maxEntryCount = 2;
        // with given parameters, the eviction checker expects 6 entries per partition to kick-in
        // see EntryCountCacheEvictionChecker#calculateMaxPartitionSize for actual calculation
        int calculatedMaxEntriesPerPartition = 6;
        factory.terminateAll();
        // configure members with 2 partitions, cache with eviction on max size 2
        CacheSimpleConfig cacheConfig = new CacheSimpleConfig();
        cacheConfig.setName("*").setBackupCount(1).setStatisticsEnabled(true).setEvictionConfig(new com.hazelcast.config.EvictionConfig(maxEntryCount, MaxSizePolicy.ENTRY_COUNT, EvictionPolicy.LFU));
        Config config = new Config();
        config.setProperty(PARTITION_COUNT.getName(), Integer.toString(partitionCount));
        config.addCacheConfig(cacheConfig);
        HazelcastInstance hz1 = getHazelcastInstance(config);
        HazelcastInstance hz2 = getHazelcastInstance(config);
        ICache<String, String> cache1 = hz1.getCacheManager().getCache("cache1");
        ICache<String, String> cache2 = hz2.getCacheManager().getCache("cache1");
        // put 5 entries in a single partition
        while ((cache1.size()) < calculatedMaxEntriesPerPartition) {
            String key = HazelcastTestSupport.generateKeyForPartition(hz1, 0);
            cache1.put(key, HazelcastTestSupport.randomString());
        } 
        String key = HazelcastTestSupport.generateKeyForPartition(hz1, 0);
        // this put must trigger eviction
        cache1.put(key, "foo");
        // number of evictions on primary and backup must be 1
        Assert.assertEquals(1, ((cache1.getLocalCacheStatistics().getCacheEvictions()) + (cache2.getLocalCacheStatistics().getCacheEvictions())));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testNearCacheStats_availableWhenEnabled() {
        ICache<Integer, String> cache = createCache();
        CacheStatistics stats = cache.getLocalCacheStatistics();
        stats.getNearCacheStatistics();
    }
}

