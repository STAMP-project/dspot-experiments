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


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IFunction;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientQueryCacheTest extends HazelcastTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Integer> TRUE_PREDICATE = TruePredicate.INSTANCE;

    private static TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testQueryCache_whenIncludeValueEnabled() {
        boolean includeValue = true;
        testQueryCache(includeValue);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testQueryCache_whenIncludeValueDisabled() {
        boolean includeValue = false;
        testQueryCache(includeValue);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testQueryCache_whenInitialPopulationEnabled() {
        boolean enableInitialPopulation = true;
        int numberOfElementsToBePutToIMap = 1000;
        int expectedSizeOfQueryCache = numberOfElementsToBePutToIMap;
        testWithInitialPopulation(enableInitialPopulation, expectedSizeOfQueryCache, numberOfElementsToBePutToIMap);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void testQueryCache_whenInitialPopulationDisabled() {
        boolean enableInitialPopulation = false;
        int numberOfElementsToBePutToIMap = 1000;
        int expectedSizeOfQueryCache = 0;
        testWithInitialPopulation(enableInitialPopulation, expectedSizeOfQueryCache, numberOfElementsToBePutToIMap);
    }

    @Test
    public void testQueryCache_withLocalListener() {
        String mapName = randomString();
        String queryCacheName = randomString();
        HazelcastInstance client = ClientQueryCacheTest.factory.newHazelcastClient();
        IMap<Integer, Integer> map = client.getMap(mapName);
        for (int i = 0; i < 30; i++) {
            map.put(i, i);
        }
        final AtomicInteger countAddEvent = new AtomicInteger();
        final AtomicInteger countRemoveEvent = new AtomicInteger();
        final QueryCache<Integer, Integer> queryCache = map.getQueryCache(queryCacheName, new EntryAdapter() {
            @Override
            public void entryAdded(EntryEvent event) {
                countAddEvent.incrementAndGet();
            }

            @Override
            public void entryRemoved(EntryEvent event) {
                countRemoveEvent.incrementAndGet();
            }
        }, new SqlPredicate("this > 20"), true);
        for (int i = 0; i < 30; i++) {
            map.remove(i);
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(0, queryCache.size());
            }
        });
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals("Count of add events wrong!", 9, countAddEvent.get());
            }
        });
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals("Count of remove events wrong!", 9, countRemoveEvent.get());
            }
        });
    }

    @Test
    public void testQueryCacheCleared_afterCalling_IMap_evictAll() {
        String cacheName = randomString();
        final IMap<Integer, Integer> map = getMap();
        QueryCache<Integer, Integer> queryCache = map.getQueryCache(cacheName, ClientQueryCacheTest.TRUE_PREDICATE, false);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        IFunction evictAll = new IFunction() {
            @Override
            public Object apply(Object ignored) {
                map.evictAll();
                return null;
            }
        };
        assertQueryCacheSizeEventually(0, evictAll, queryCache);
    }

    @Test
    public void testQueryCacheCleared_afterCalling_IMap_clear() {
        String cacheName = randomString();
        final IMap<Integer, Integer> map = getMap();
        QueryCache<Integer, Integer> queryCache = map.getQueryCache(cacheName, ClientQueryCacheTest.TRUE_PREDICATE, false);
        for (int i = 0; i < 1000; i++) {
            map.put(i, i);
        }
        IFunction clear = new IFunction() {
            @Override
            public Object apply(Object ignored) {
                map.clear();
                return null;
            }
        };
        assertQueryCacheSizeEventually(0, clear, queryCache);
    }

    @Test
    public void testDestroy_emptiesQueryCache() {
        int entryCount = 1000;
        final CountDownLatch numberOfAddEvents = new CountDownLatch(entryCount);
        String cacheName = randomString();
        IMap<Integer, Integer> map = getMap();
        QueryCache<Integer, Integer> queryCache = map.getQueryCache(cacheName, new com.hazelcast.map.listener.EntryAddedListener<Integer, Integer>() {
            @Override
            public void entryAdded(EntryEvent<Integer, Integer> event) {
                numberOfAddEvents.countDown();
            }
        }, ClientQueryCacheTest.TRUE_PREDICATE, false);
        for (int i = 0; i < entryCount; i++) {
            map.put(i, i);
        }
        assertOpenEventually(numberOfAddEvents);
        queryCache.destroy();
        Assert.assertEquals(0, queryCache.size());
    }
}

