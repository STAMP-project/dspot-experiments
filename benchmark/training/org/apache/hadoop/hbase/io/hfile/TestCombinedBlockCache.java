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


import BlockType.DATA;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.io.hfile.CombinedBlockCache.CombinedCacheStats;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class })
public class TestCombinedBlockCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCombinedBlockCache.class);

    @Test
    public void testCombinedCacheStats() {
        CacheStats lruCacheStats = new CacheStats("lruCacheStats", 2);
        CacheStats bucketCacheStats = new CacheStats("bucketCacheStats", 2);
        CombinedCacheStats stats = new CombinedCacheStats(lruCacheStats, bucketCacheStats);
        double delta = 0.01;
        // period 1:
        // lru cache: 1 hit caching, 1 miss caching
        // bucket cache: 2 hit non-caching,1 miss non-caching/primary,1 fail insert
        lruCacheStats.hit(true, true, DATA);
        lruCacheStats.miss(true, false, DATA);
        bucketCacheStats.hit(false, true, DATA);
        bucketCacheStats.hit(false, true, DATA);
        bucketCacheStats.miss(false, true, DATA);
        Assert.assertEquals(5, stats.getRequestCount());
        Assert.assertEquals(2, stats.getRequestCachingCount());
        Assert.assertEquals(2, stats.getMissCount());
        Assert.assertEquals(1, stats.getPrimaryMissCount());
        Assert.assertEquals(1, stats.getMissCachingCount());
        Assert.assertEquals(3, stats.getHitCount());
        Assert.assertEquals(3, stats.getPrimaryHitCount());
        Assert.assertEquals(1, stats.getHitCachingCount());
        Assert.assertEquals(0.6, stats.getHitRatio(), delta);
        Assert.assertEquals(0.5, stats.getHitCachingRatio(), delta);
        Assert.assertEquals(0.4, stats.getMissRatio(), delta);
        Assert.assertEquals(0.5, stats.getMissCachingRatio(), delta);
        // lru cache: 2 evicted, 1 evict
        // bucket cache: 1 evict
        lruCacheStats.evicted(1000, true);
        lruCacheStats.evicted(1000, false);
        lruCacheStats.evict();
        bucketCacheStats.evict();
        Assert.assertEquals(2, stats.getEvictionCount());
        Assert.assertEquals(2, stats.getEvictedCount());
        Assert.assertEquals(1, stats.getPrimaryEvictedCount());
        Assert.assertEquals(1.0, stats.evictedPerEviction(), delta);
        // lru cache:  1 fail insert
        lruCacheStats.failInsert();
        Assert.assertEquals(1, stats.getFailedInserts());
        // rollMetricsPeriod
        stats.rollMetricsPeriod();
        Assert.assertEquals(3, stats.getSumHitCountsPastNPeriods());
        Assert.assertEquals(5, stats.getSumRequestCountsPastNPeriods());
        Assert.assertEquals(1, stats.getSumHitCachingCountsPastNPeriods());
        Assert.assertEquals(2, stats.getSumRequestCachingCountsPastNPeriods());
        Assert.assertEquals(0.6, stats.getHitRatioPastNPeriods(), delta);
        Assert.assertEquals(0.5, stats.getHitCachingRatioPastNPeriods(), delta);
        // period 2:
        // lru cache: 3 hit caching
        lruCacheStats.hit(true, true, DATA);
        lruCacheStats.hit(true, true, DATA);
        lruCacheStats.hit(true, true, DATA);
        stats.rollMetricsPeriod();
        Assert.assertEquals(6, stats.getSumHitCountsPastNPeriods());
        Assert.assertEquals(8, stats.getSumRequestCountsPastNPeriods());
        Assert.assertEquals(4, stats.getSumHitCachingCountsPastNPeriods());
        Assert.assertEquals(5, stats.getSumRequestCachingCountsPastNPeriods());
        Assert.assertEquals(0.75, stats.getHitRatioPastNPeriods(), delta);
        Assert.assertEquals(0.8, stats.getHitCachingRatioPastNPeriods(), delta);
    }
}

