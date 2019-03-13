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
package com.hazelcast.client.map.impl.querycache;


import EvictionConfig.MaxSizePolicy.ENTRY_COUNT;
import EvictionPolicy.LFU;
import TruePredicate.INSTANCE;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EntryEvictedListener;
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
public class ClientEvictionTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testMaxSizeEvictionWorks() throws Exception {
        final int maxSize = 1000;
        final int populationCount = 5000;
        String mapName = randomString();
        String cacheName = randomString();
        QueryCacheConfig cacheConfig = new QueryCacheConfig(cacheName);
        cacheConfig.getEvictionConfig().setSize(maxSize).setEvictionPolicy(LFU).setMaximumSizePolicy(ENTRY_COUNT);
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addQueryCacheConfig(mapName, cacheConfig);
        HazelcastInstance client = factory.newHazelcastClient(clientConfig);
        IMap<Integer, Integer> map = client.getMap(mapName);
        // expecting at least populationCount - maxSize + 10 evicted entries according to max size.
        // 10 states an error margin since eviction does not sweep precise number of entries.
        int margin = 10;
        final CountDownLatch evictedCount = new CountDownLatch(((populationCount - maxSize) - margin));
        final QueryCache<Integer, Integer> cache = map.getQueryCache(cacheName, INSTANCE, true);
        String listener = cache.addEntryListener(new EntryEvictedListener() {
            @Override
            public void entryEvicted(EntryEvent event) {
                evictedCount.countDown();
            }
        }, false);
        for (int i = 0; i < populationCount; i++) {
            map.put(i, i);
        }
        assertOpenEventually(evictedCount);
        assertQueryCacheEvicted(maxSize, margin, cache);
        Assert.assertTrue(cache.removeEntryListener(listener));
    }
}

