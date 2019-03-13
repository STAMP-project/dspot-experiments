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
package com.hazelcast.monitor.impl;


import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LocalCacheStatsImplTest {
    @Test
    public void testDefaultConstructor() {
        LocalCacheStatsImpl localCacheStats = new LocalCacheStatsImpl();
        Assert.assertEquals(0, localCacheStats.getCreationTime());
        Assert.assertEquals(0, localCacheStats.getLastUpdateTime());
        Assert.assertEquals(0, localCacheStats.getLastAccessTime());
        Assert.assertEquals(0, localCacheStats.getOwnedEntryCount());
        Assert.assertEquals(0, localCacheStats.getCacheHits());
        Assert.assertEquals(0, localCacheStats.getCacheHitPercentage(), 1.0E-4);
        Assert.assertEquals(0, localCacheStats.getCacheMisses());
        Assert.assertEquals(0, localCacheStats.getCacheMissPercentage(), 1.0E-4);
        Assert.assertEquals(0, localCacheStats.getCachePuts());
        Assert.assertEquals(0, localCacheStats.getCacheGets());
        Assert.assertEquals(0, localCacheStats.getCacheRemovals());
        Assert.assertEquals(0, localCacheStats.getCacheEvictions());
        Assert.assertEquals(0, localCacheStats.getAverageGetTime(), 1.0E-4);
        Assert.assertEquals(0, localCacheStats.getAveragePutTime(), 1.0E-4);
        Assert.assertEquals(0, localCacheStats.getAverageRemoveTime(), 1.0E-4);
        Assert.assertEquals(0, localCacheStats.getCreationTime());
        Assert.assertNotNull(localCacheStats.toString());
    }

    @Test
    public void testSerialization() {
        CacheStatistics cacheStatistics = new CacheStatistics() {
            @Override
            public long getCreationTime() {
                return 1986;
            }

            @Override
            public long getLastUpdateTime() {
                return 2014;
            }

            @Override
            public long getLastAccessTime() {
                return 2015;
            }

            @Override
            public long getOwnedEntryCount() {
                return 1000;
            }

            @Override
            public long getCacheHits() {
                return 127;
            }

            @Override
            public float getCacheHitPercentage() {
                return 12.5F;
            }

            @Override
            public long getCacheMisses() {
                return 5;
            }

            @Override
            public float getCacheMissPercentage() {
                return 11.4F;
            }

            @Override
            public long getCacheGets() {
                return 6;
            }

            @Override
            public long getCachePuts() {
                return 7;
            }

            @Override
            public long getCacheRemovals() {
                return 8;
            }

            @Override
            public long getCacheEvictions() {
                return 9;
            }

            @Override
            public float getAverageGetTime() {
                return 23.42F;
            }

            @Override
            public float getAveragePutTime() {
                return 42.23F;
            }

            @Override
            public float getAverageRemoveTime() {
                return 127.45F;
            }

            @Override
            public NearCacheStats getNearCacheStatistics() {
                return null;
            }
        };
        LocalCacheStatsImpl localCacheStats = new LocalCacheStatsImpl(cacheStatistics);
        JsonObject serialized = localCacheStats.toJson();
        LocalCacheStatsImpl deserialized = new LocalCacheStatsImpl();
        deserialized.fromJson(serialized);
        Assert.assertEquals(1986, deserialized.getCreationTime());
        Assert.assertEquals(2014, deserialized.getLastUpdateTime());
        Assert.assertEquals(2015, deserialized.getLastAccessTime());
        Assert.assertEquals(1000, deserialized.getOwnedEntryCount());
        Assert.assertEquals(127, deserialized.getCacheHits());
        Assert.assertEquals(12.5F, deserialized.getCacheHitPercentage(), 1.0E-4);
        Assert.assertEquals(5, deserialized.getCacheMisses());
        Assert.assertEquals(11.4F, deserialized.getCacheMissPercentage(), 1.0E-4);
        Assert.assertEquals(6, deserialized.getCacheGets());
        Assert.assertEquals(7, deserialized.getCachePuts());
        Assert.assertEquals(8, deserialized.getCacheRemovals());
        Assert.assertEquals(9, deserialized.getCacheEvictions());
        Assert.assertEquals(23.42F, deserialized.getAverageGetTime(), 1.0E-4);
        Assert.assertEquals(42.23F, deserialized.getAveragePutTime(), 1.0E-4);
        Assert.assertEquals(127.45F, deserialized.getAverageRemoveTime(), 1.0E-4);
        Assert.assertTrue(((deserialized.getCreationTime()) > 0));
        Assert.assertNotNull(deserialized.toString());
    }
}

