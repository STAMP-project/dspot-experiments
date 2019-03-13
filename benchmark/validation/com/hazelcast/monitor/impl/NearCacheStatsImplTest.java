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


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.FileNotFoundException;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NearCacheStatsImplTest extends HazelcastTestSupport {
    private NearCacheStatsImpl nearCacheStats;

    @Test
    public void testDefaultConstructor() {
        NearCacheStatsImplTest.assertNearCacheStats(nearCacheStats, 1, 200, 300, 400, false);
    }

    @Test
    public void testCopyConstructor() {
        NearCacheStatsImpl copy = new NearCacheStatsImpl(nearCacheStats);
        NearCacheStatsImplTest.assertNearCacheStats(copy, 1, 200, 300, 400, false);
    }

    @Test
    public void testSerialization() {
        NearCacheStatsImpl deserialized = NearCacheStatsImplTest.serializeAndDeserializeNearCacheStats(nearCacheStats);
        NearCacheStatsImplTest.assertNearCacheStats(deserialized, 1, 200, 300, 400, false);
    }

    @Test
    public void testSerialization_withPersistenceFailure() {
        Throwable throwable = new FileNotFoundException("expected exception");
        nearCacheStats.addPersistenceFailure(throwable);
        NearCacheStatsImpl deserialized = NearCacheStatsImplTest.serializeAndDeserializeNearCacheStats(nearCacheStats);
        NearCacheStatsImplTest.assertNearCacheStats(deserialized, 2, 0, 0, 0, true);
        String lastPersistenceFailure = deserialized.getLastPersistenceFailure();
        HazelcastTestSupport.assertContains(lastPersistenceFailure, throwable.getClass().getSimpleName());
        HazelcastTestSupport.assertContains(lastPersistenceFailure, "expected exception");
    }

    @Test
    public void testGetRatio_NaN() {
        NearCacheStatsImpl nearCacheStats = new NearCacheStatsImpl();
        Assert.assertEquals(Double.NaN, nearCacheStats.getRatio(), 1.0E-4);
    }

    @Test
    public void testGetRatio_POSITIVE_INFINITY() {
        NearCacheStatsImpl nearCacheStats = new NearCacheStatsImpl();
        nearCacheStats.setHits(1);
        Assert.assertEquals(Double.POSITIVE_INFINITY, nearCacheStats.getRatio(), 1.0E-4);
    }

    @Test
    public void testGetRatio_100() {
        NearCacheStatsImpl nearCacheStats = new NearCacheStatsImpl();
        nearCacheStats.setHits(1);
        nearCacheStats.setMisses(1);
        Assert.assertEquals(100.0, nearCacheStats.getRatio(), 1.0E-4);
    }

    @Test
    public void testConcurrentModification() {
        int incThreads = 40;
        int decThreads = 10;
        int countPerThread = 500;
        CountDownLatch startLatch = new CountDownLatch(1);
        NearCacheStatsImpl nearCacheStats = new NearCacheStatsImpl();
        Thread[] threads = new Thread[incThreads + decThreads];
        for (int i = 0; i < incThreads; i++) {
            threads[i] = new NearCacheStatsImplTest.StatsModifierThread(startLatch, nearCacheStats, true, countPerThread);
            threads[i].start();
        }
        for (int i = incThreads; i < (incThreads + decThreads); i++) {
            threads[i] = new NearCacheStatsImplTest.StatsModifierThread(startLatch, nearCacheStats, false, countPerThread);
            threads[i].start();
        }
        startLatch.countDown();
        HazelcastTestSupport.assertJoinable(threads);
        System.out.println(nearCacheStats);
        int incCount = incThreads * countPerThread;
        int decCount = decThreads * countPerThread;
        int totalCount = incCount - decCount;
        Assert.assertEquals(totalCount, nearCacheStats.getOwnedEntryCount());
        Assert.assertEquals((totalCount * 23), nearCacheStats.getOwnedEntryMemoryCost());
        Assert.assertEquals(incCount, nearCacheStats.getHits());
        Assert.assertEquals(decCount, nearCacheStats.getMisses());
        Assert.assertEquals(incCount, nearCacheStats.getEvictions());
        Assert.assertEquals(incCount, nearCacheStats.getExpirations());
    }

    private static class StatsModifierThread extends Thread {
        private final CountDownLatch startLatch;

        private final NearCacheStatsImpl nearCacheStats;

        private final boolean increment;

        private final int count;

        private StatsModifierThread(CountDownLatch startLatch, NearCacheStatsImpl nearCacheStats, boolean increment, int count) {
            this.startLatch = startLatch;
            this.nearCacheStats = nearCacheStats;
            this.increment = increment;
            this.count = count;
        }

        @Override
        public void run() {
            HazelcastTestSupport.assertOpenEventually(startLatch);
            for (int i = 0; i < (count); i++) {
                if (increment) {
                    nearCacheStats.incrementOwnedEntryCount();
                    nearCacheStats.incrementOwnedEntryMemoryCost(23);
                    nearCacheStats.incrementHits();
                    nearCacheStats.incrementEvictions();
                    nearCacheStats.incrementExpirations();
                } else {
                    nearCacheStats.decrementOwnedEntryCount();
                    nearCacheStats.decrementOwnedEntryMemoryCost(23);
                    nearCacheStats.incrementMisses();
                }
            }
        }
    }
}

