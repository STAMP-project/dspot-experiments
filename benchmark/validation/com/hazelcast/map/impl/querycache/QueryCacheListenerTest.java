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


import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.mapreduce.helpers.Employee;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category({ QuickTest.class, ParallelTest.class })
public class QueryCacheListenerTest extends AbstractQueryCacheTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Employee> TRUE_PREDICATE = TruePredicate.INSTANCE;

    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Employee> SQL_PREDICATE_GT = new SqlPredicate("id > 100");

    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Employee> SQL_PREDICATE_LT = new SqlPredicate("id < 100");

    @Parameterized.Parameter
    public String useNaturalFilteringStrategy;

    @Test
    public void listen_withPredicate_afterQueryCacheCreation() {
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.TRUE_PREDICATE, useNaturalFilteringStrategy);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        final QueryCacheListenerTest.QueryCacheAdditionListener listener = new QueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(listener, QueryCacheListenerTest.SQL_PREDICATE_GT, true);
        final int count = 111;
        populateMap(map, count);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(10, listener.getAddedEventCount());
            }
        });
    }

    @Test
    public void listenKey_withPredicate_afterQueryCacheCreation() {
        int keyToListen = 109;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.TRUE_PREDICATE, useNaturalFilteringStrategy);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        final QueryCacheListenerTest.QueryCacheAdditionListener listener = new QueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(listener, QueryCacheListenerTest.SQL_PREDICATE_GT, keyToListen, true);
        final int count = 111;
        populateMap(map, count);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.getAddedEventCount());
            }
        });
    }

    @Test
    public void listenKey_withMultipleListeners_afterQueryCacheCreation() {
        int keyToListen = 109;
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.TRUE_PREDICATE, useNaturalFilteringStrategy);
        final QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        final QueryCacheListenerTest.QueryCacheAdditionListener addListener = new QueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(addListener, QueryCacheListenerTest.SQL_PREDICATE_GT, keyToListen, true);
        final QueryCacheListenerTest.QueryCacheRemovalListener removeListener = new QueryCacheListenerTest.QueryCacheRemovalListener();
        cache.addEntryListener(removeListener, QueryCacheListenerTest.SQL_PREDICATE_GT, keyToListen, true);
        final int count = 111;
        populateMap(map, count);
        removeEntriesFromMap(map, 0, count);
        populateMap(map, count);
        removeEntriesFromMap(map, 0, count);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int cacheSize = cache.size();
                String message = "Cache size is=" + cacheSize;
                Assert.assertEquals(message, 0, cacheSize);
                Assert.assertEquals(message, 2, addListener.getAddedEventCount());
                Assert.assertEquals(message, 2, removeListener.getRemovedEventCount());
            }
        });
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void listenerShouldReceiveValues_whenValueCaching_enabled() {
        boolean includeValue = true;
        testValueCaching(includeValue);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void listenerShouldNotReceiveValues_whenValueCaching_disabled() {
        boolean includeValue = false;
        testValueCaching(includeValue);
    }

    @Test
    public void listenerShouldReceive_CLEAR_ALL_Event_whenIMapCleared() {
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.TRUE_PREDICATE, useNaturalFilteringStrategy);
        int entryCount = 1000;
        final AtomicInteger clearAllEventCount = new AtomicInteger();
        final QueryCache<Integer, Employee> queryCache = map.getQueryCache(cacheName, new EntryAdapter() {
            @Override
            public void mapCleared(MapEvent e) {
                clearAllEventCount.incrementAndGet();
            }
        }, QueryCacheListenerTest.TRUE_PREDICATE, false);
        populateMap(map, entryCount);
        assertQueryCacheSizeEventually(entryCount, queryCache);
        map.clear();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                // expecting at least 1 event
                Assert.assertTrue(((clearAllEventCount.get()) >= 1));
                Assert.assertEquals(0, queryCache.size());
            }
        });
    }

    @Test
    public void listenKey_withPredicate_whenNoLongerMatching() {
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.SQL_PREDICATE_LT, useNaturalFilteringStrategy);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        Employee employee = new Employee(0);
        map.put(0, employee);
        final QueryCacheListenerTest.QueryCacheRemovalListener listener = new QueryCacheListenerTest.QueryCacheRemovalListener();
        cache.addEntryListener(listener, true);
        employee = new Employee(200);
        map.put(0, employee);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.getRemovedEventCount());
            }
        });
    }

    @Test
    public void listenKey_withPredicate_whenMatching() {
        IMap<Integer, Employee> map = getIMapWithDefaultConfig(QueryCacheListenerTest.SQL_PREDICATE_LT, useNaturalFilteringStrategy);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName);
        Employee employee = new Employee(200);
        map.put(0, employee);
        final QueryCacheListenerTest.QueryCacheAdditionListener listener = new QueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(listener, true);
        employee = new Employee(0);
        map.put(0, employee);
        HazelcastTestSupport.sleepAtLeastSeconds(5);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.getAddedEventCount());
            }
        });
    }

    @Test
    public void listenerShouldBeRegistered_whenConfiguredProgrammatically() {
        MapConfig mapConfig = new MapConfig(mapName);
        final QueryCacheListenerTest.QueryCacheAdditionListener listener = new QueryCacheListenerTest.QueryCacheAdditionListener();
        QueryCacheConfig queryCacheConfig = new QueryCacheConfig(cacheName).setPredicateConfig(new com.hazelcast.config.PredicateConfig(QueryCacheListenerTest.TRUE_PREDICATE)).addEntryListenerConfig(new EntryListenerConfig(listener, true, true));
        mapConfig.addQueryCacheConfig(queryCacheConfig);
        Config config = new Config();
        config.addMapConfig(mapConfig);
        IMap<Integer, Employee> map = getIMap(config);
        // trigger creation of the query cache
        map.getQueryCache(cacheName);
        populateMap(map, 100);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(100, listener.getAddedEventCount());
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(100, listener.getAddedEventCount());
            }
        }, 5);
    }

    private class TestIncludeValueListener implements EntryAddedListener {
        volatile boolean hasValue = false;

        @Override
        public void entryAdded(EntryEvent event) {
            Object value = event.getValue();
            hasValue = value != null;
        }
    }

    private class QueryCacheAdditionListener implements EntryAddedListener {
        private final AtomicInteger addedEventCount = new AtomicInteger(0);

        QueryCacheAdditionListener() {
        }

        @Override
        public void entryAdded(EntryEvent event) {
            addedEventCount.incrementAndGet();
        }

        public int getAddedEventCount() {
            return addedEventCount.get();
        }
    }

    private class QueryCacheRemovalListener implements EntryRemovedListener {
        private final AtomicInteger removedEventCount = new AtomicInteger(0);

        QueryCacheRemovalListener() {
        }

        @Override
        public void entryRemoved(EntryEvent event) {
            removedEventCount.incrementAndGet();
        }

        public int getRemovedEventCount() {
            return removedEventCount.get();
        }
    }
}

