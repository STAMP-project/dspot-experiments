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
package com.hazelcast.client.map.impl.nearcache;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.monitor.NearCacheStats;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NearCacheStatsStressTest extends HazelcastTestSupport {
    private static final int KEY_SPACE = 1000;

    private final TestHazelcastFactory factory = new TestHazelcastFactory();

    private final AtomicBoolean stop = new AtomicBoolean(false);

    private InternalSerializationService ss;

    private NearCache<Object, Object> nearCache;

    @Test
    public void stress_stats_by_doing_put_and_remove() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        pool.execute(new NearCacheStatsStressTest.Put());
        pool.execute(new NearCacheStatsStressTest.Remove());
        sleepSeconds(3);
        stop.set(true);
        pool.shutdown();
        if (pool.awaitTermination(10, TimeUnit.SECONDS)) {
            NearCacheStats nearCacheStats = nearCache.getNearCacheStats();
            long ownedEntryCount = nearCacheStats.getOwnedEntryCount();
            long memoryCost = nearCacheStats.getOwnedEntryMemoryCost();
            int size = nearCache.size();
            Assert.assertTrue(((("ownedEntryCount=" + ownedEntryCount) + ", size=") + size), (ownedEntryCount >= 0));
            Assert.assertTrue(((("memoryCost=" + memoryCost) + ", size=") + size), (memoryCost >= 0));
            Assert.assertEquals(((("ownedEntryCount=" + ownedEntryCount) + ", size=") + size), size, ownedEntryCount);
        } else {
            Assert.fail("pool.awaitTermination reached timeout before termination");
        }
    }

    class Put implements Runnable {
        @Override
        public void run() {
            while (!(stop.get())) {
                Object key = getInt(NearCacheStatsStressTest.KEY_SPACE);
                Data keyData = ss.toData(key);
                long reservationId = nearCache.tryReserveForUpdate(key, keyData);
                if (reservationId != (NOT_RESERVED)) {
                    nearCache.tryPublishReserved(key, keyData, reservationId, false);
                }
            } 
        }
    }

    class Remove implements Runnable {
        @Override
        public void run() {
            while (!(stop.get())) {
                Object key = getInt(NearCacheStatsStressTest.KEY_SPACE);
                nearCache.invalidate(key);
            } 
        }
    }
}

