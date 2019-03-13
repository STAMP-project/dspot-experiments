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


import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.config.QueryCacheConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.mapreduce.helpers.Employee;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.SqlPredicate;
import com.hazelcast.query.TruePredicate;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientQueryCacheListenerTest extends HazelcastTestSupport {
    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Employee> TRUE_PREDICATE = TruePredicate.INSTANCE;

    @SuppressWarnings("unchecked")
    private static final Predicate<Integer, Integer> INTEGER_TRUE_PREDICATE = TruePredicate.INSTANCE;

    private static TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void shouldReceiveEvent_whenListening_withPredicate() {
        String mapName = randomString();
        String cacheName = randomString();
        HazelcastInstance instance = ClientQueryCacheListenerTest.factory.newHazelcastClient();
        IMap<Integer, Employee> map = instance.getMap(mapName);
        final QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, ClientQueryCacheListenerTest.TRUE_PREDICATE, true);
        final ClientQueryCacheListenerTest.QueryCacheAdditionListener listener = new ClientQueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(listener, new SqlPredicate("id > 100"), true);
        final int putCount = 111;
        for (int i = 0; i < putCount; i++) {
            map.put(i, new Employee(i));
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(10, listener.getAddedEventCount());
            }
        });
    }

    @Test
    public void shouldReceiveEvent_whenListeningKey_withPredicate() {
        String mapName = randomString();
        String cacheName = randomString();
        HazelcastInstance instance = ClientQueryCacheListenerTest.factory.newHazelcastClient();
        IMap<Integer, Employee> map = instance.getMap(mapName);
        QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, ClientQueryCacheListenerTest.TRUE_PREDICATE, true);
        int keyToListen = 109;
        final ClientQueryCacheListenerTest.QueryCacheAdditionListener listener = new ClientQueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(listener, new SqlPredicate("id > 100"), keyToListen, true);
        int putCount = 111;
        for (int i = 0; i < putCount; i++) {
            map.put(i, new Employee(i));
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(1, listener.getAddedEventCount());
            }
        });
    }

    @Test
    public void shouldReceiveEvent_whenListeningKey_withMultipleListener() {
        String mapName = randomString();
        String cacheName = randomString();
        HazelcastInstance instance = ClientQueryCacheListenerTest.factory.newHazelcastClient();
        IMap<Integer, Employee> map = instance.getMap(mapName);
        final QueryCache<Integer, Employee> cache = map.getQueryCache(cacheName, ClientQueryCacheListenerTest.TRUE_PREDICATE, true);
        int keyToListen = 109;
        final ClientQueryCacheListenerTest.QueryCacheAdditionListener addListener = new ClientQueryCacheListenerTest.QueryCacheAdditionListener();
        cache.addEntryListener(addListener, new SqlPredicate("id > 100"), keyToListen, true);
        final ClientQueryCacheListenerTest.QueryCacheRemovalListener removeListener = new ClientQueryCacheListenerTest.QueryCacheRemovalListener();
        cache.addEntryListener(removeListener, new SqlPredicate("id > 100"), keyToListen, true);
        // populate map before construction of query cache
        int count = 111;
        for (int i = 0; i < count; i++) {
            map.put(i, new Employee(i));
        }
        for (int i = 0; i < count; i++) {
            map.remove(i);
        }
        for (int i = 0; i < count; i++) {
            map.put(i, new Employee(i));
        }
        for (int i = 0; i < count; i++) {
            map.remove(i);
        }
        assertTrueEventually(new AssertTask() {
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
    public void shouldReceiveValue_whenIncludeValue_enabled() {
        boolean includeValue = true;
        testIncludeValue(includeValue);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void shouldNotReceiveValue_whenIncludeValue_disabled() {
        boolean includeValue = false;
        testIncludeValue(includeValue);
    }

    @Test
    public void listenerShouldBeRegistered_whenConfiguredProgrammatically() {
        final int valueCount = 100;
        String mapName = randomString();
        String qcName = randomString();
        final ClientQueryCacheListenerTest.QueryCacheAdditionListener listener = new ClientQueryCacheListenerTest.QueryCacheAdditionListener();
        QueryCacheConfig queryCacheConfig = new QueryCacheConfig(qcName).setPredicateConfig(new com.hazelcast.config.PredicateConfig(ClientQueryCacheListenerTest.TRUE_PREDICATE)).addEntryListenerConfig(new EntryListenerConfig(listener, true, true));
        ClientConfig config = new ClientConfig().addQueryCacheConfig(mapName, queryCacheConfig);
        HazelcastInstance instance = ClientQueryCacheListenerTest.factory.newHazelcastClient(config);
        IMap<Integer, Employee> map = instance.getMap(mapName);
        // trigger creation of the query cache
        map.getQueryCache(qcName);
        for (int i = 0; i < valueCount; i++) {
            map.put(i, new Employee(i));
        }
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(valueCount, listener.getAddedEventCount());
            }
        });
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

