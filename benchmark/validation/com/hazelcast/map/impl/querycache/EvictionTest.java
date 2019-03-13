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
package com.hazelcast.map.impl.querycache;


import TruePredicate.INSTANCE;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class EvictionTest extends HazelcastTestSupport {
    @Test
    public void testMaxSizeEvictionWorks() {
        int maxSize = 100;
        int populationCount = 500;
        String mapName = HazelcastTestSupport.randomString();
        String cacheName = HazelcastTestSupport.randomString();
        Config config = getConfig(maxSize, mapName, cacheName);
        HazelcastInstance node = createHazelcastInstance(config);
        IMap<Integer, Integer> map = AbstractQueryCacheTestSupport.getMap(node, mapName);
        final CountDownLatch entryCountingLatch = new CountDownLatch(populationCount);
        QueryCache<Integer, Integer> cache = map.getQueryCache(cacheName, INSTANCE, true);
        String listener = cache.addEntryListener(new EntryAddedListener() {
            @Override
            public void entryAdded(EntryEvent event) {
                entryCountingLatch.countDown();
            }
        }, false);
        for (int i = 0; i < populationCount; i++) {
            map.put(i, i);
        }
        HazelcastTestSupport.assertOpenEventually(("Cache size is " + (cache.size())), entryCountingLatch);
        // expecting at most populationCount - maxSize - 5 entries
        // 5 states an error margin since eviction does not sweep precise number of entries.
        assertQueryCacheEvicted(maxSize, 5, cache);
        Assert.assertTrue(cache.removeEntryListener(listener));
    }
}

