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


import TruePredicate.INSTANCE;
import com.hazelcast.client.impl.querycache.ClientQueryCacheContext;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.Collection;
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
public class ClientQueryCacheMemoryLeakTest extends HazelcastTestSupport {
    private static final int STRESS_TEST_RUN_SECONDS = 3;

    private static final int STRESS_TEST_THREAD_COUNT = 4;

    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void stress_user_listener_removal_upon_query_cache_destroy() throws InterruptedException {
        final String[] mapNames = new String[]{ "mapA", "mapB", "mapC", "mapD" };
        Config config = getConfig();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        HazelcastInstance node3 = factory.newHazelcastInstance(config);
        final HazelcastInstance client = factory.newHazelcastClient();
        final AtomicBoolean stop = new AtomicBoolean(false);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (ClientQueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT); i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (!(stop.get())) {
                        String name = mapNames[getInt(0, 4)];
                        final IMap<Integer, Integer> map = client.getMap(name);
                        int key = getInt(0, Integer.MAX_VALUE);
                        map.put(key, 1);
                        QueryCache queryCache = map.getQueryCache(name, INSTANCE, true);
                        queryCache.get(key);
                        queryCache.addEntryListener(new com.hazelcast.map.listener.EntryAddedListener<Integer, Integer>() {
                            @Override
                            public void entryAdded(EntryEvent<Integer, Integer> event) {
                            }
                        }, true);
                        queryCache.destroy();
                        map.destroy();
                    } 
                }
            };
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        sleepSeconds(ClientQueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node3);
        for (String mapName : mapNames) {
            // this is to ensure no user added listener is left on this client side query-cache
            ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(mapName, client);
        }
        Collection<HazelcastInstance> instances = getAllHazelcastInstances();
        for (HazelcastInstance instance : instances) {
            // this is to ensure no publisher side resource is left on server side event service
            ClientQueryCacheMemoryLeakTest.assertServerSideEventServiceCleared(instance);
        }
    }

    @Test
    public void event_service_is_empty_after_queryCache_destroy() {
        String mapName = "test";
        HazelcastInstance node1 = newHazelcastInstance();
        HazelcastInstance node2 = newHazelcastInstance();
        HazelcastInstance client = factory.newHazelcastClient();
        IMap<Integer, Integer> map = client.getMap(mapName);
        QueryCache queryCache = map.getQueryCache(mapName, INSTANCE, true);
        queryCache.destroy();
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
    }

    @Test
    public void event_service_is_empty_after_queryCache_concurrent_destroy() throws InterruptedException {
        final HazelcastInstance node1 = newHazelcastInstance();
        HazelcastInstance node2 = newHazelcastInstance();
        String mapName = "test";
        HazelcastInstance client = factory.newHazelcastClient();
        final IMap<Integer, Integer> map = client.getMap(mapName);
        ClientQueryCacheMemoryLeakTest.populateMap(map);
        final AtomicBoolean stop = new AtomicBoolean(false);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (ClientQueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT); i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (!(stop.get())) {
                        QueryCache queryCache = map.getQueryCache("a", INSTANCE, true);
                        queryCache.addEntryListener(new com.hazelcast.map.listener.EntryAddedListener<Integer, Integer>() {
                            @Override
                            public void entryAdded(EntryEvent<Integer, Integer> event) {
                            }
                        }, true);
                        queryCache.destroy();
                    } 
                }
            };
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        sleepSeconds(ClientQueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
        ClientQueryCacheMemoryLeakTest.assertNoUserListenerLeft(mapName, client);
    }

    @Test
    public void removes_internal_query_caches_upon_map_destroy() {
        factory.newHazelcastInstance();
        HazelcastInstance client = factory.newHazelcastClient();
        String mapName = "test";
        IMap<Integer, Integer> map = client.getMap(mapName);
        ClientQueryCacheMemoryLeakTest.populateMap(map);
        for (int j = 0; j < 10; j++) {
            map.getQueryCache((j + "-test-QC"), INSTANCE, true);
        }
        map.destroy();
        ClientQueryCacheContext queryCacheContext = getQueryCacheContext();
        SubscriberContext subscriberContext = queryCacheContext.getSubscriberContext();
        QueryCacheEndToEndProvider provider = subscriberContext.getEndToEndQueryCacheProvider();
        QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        Assert.assertEquals(0, provider.getQueryCacheCount(mapName));
        Assert.assertEquals(0, queryCacheFactory.getQueryCacheCount());
    }

    @Test
    public void no_query_cache_left_after_creating_and_destroying_same_map_concurrently() throws Exception {
        final HazelcastInstance node = newHazelcastInstance();
        final HazelcastInstance client = factory.newHazelcastClient();
        final String mapName = "test";
        ExecutorService pool = Executors.newFixedThreadPool(ClientQueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT);
        final AtomicBoolean stop = new AtomicBoolean(false);
        for (int i = 0; i < 1000; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    while (!(stop.get())) {
                        IMap<Integer, Integer> map = client.getMap(mapName);
                        try {
                            ClientQueryCacheMemoryLeakTest.populateMap(map);
                            for (int j = 0; j < 10; j++) {
                                map.getQueryCache((j + "-test-QC"), INSTANCE, true);
                            }
                        } finally {
                            map.destroy();
                        }
                    } 
                }
            };
            pool.submit(runnable);
        }
        sleepSeconds(ClientQueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        pool.shutdown();
        pool.awaitTermination(120, TimeUnit.SECONDS);
        SubscriberContext subscriberContext = ClientQueryCacheMemoryLeakTest.getSubscriberContext(client, mapName);
        final QueryCacheEndToEndProvider provider = subscriberContext.getEndToEndQueryCacheProvider();
        final QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, provider.getQueryCacheCount(mapName));
            }
        });
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, queryCacheFactory.getQueryCacheCount());
            }
        });
        ClientQueryCacheMemoryLeakTest.assertNoListenerLeftOnEventService(node);
        ClientQueryCacheMemoryLeakTest.assertNoRegisteredListenerLeft(node, mapName);
        ClientQueryCacheMemoryLeakTest.assertNoAccumulatorInfoSupplierLeft(node, mapName);
        ClientQueryCacheMemoryLeakTest.assertNoPartitionAccumulatorRegistryLeft(node, mapName);
    }
}

