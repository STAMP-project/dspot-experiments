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
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEndToEndProvider;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheFactory;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RandomPicker;
import java.util.ArrayList;
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
public class QueryCacheMemoryLeakTest extends HazelcastTestSupport {
    private static final int STRESS_TEST_RUN_SECONDS = 5;

    private static final int STRESS_TEST_THREAD_COUNT = 5;

    @Test
    public void event_service_is_empty_after_queryCache_destroy() throws InterruptedException {
        final String mapName = "test";
        final String queryCacheName = "cqc";
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config config = getConfig();
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        HazelcastInstance node3 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(3, node1, node2, node3);
        final IMap<Integer, Integer> map = node1.getMap(mapName);
        final AtomicBoolean stop = new AtomicBoolean(false);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (QueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT); i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (!(stop.get())) {
                        QueryCache queryCache = map.getQueryCache(queryCacheName, INSTANCE, true);
                        queryCache.destroy();
                    } 
                }
            };
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        HazelcastTestSupport.sleepSeconds(QueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        map.destroy();
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node3);
    }

    @Test
    public void stress_user_listener_removal_upon_query_cache_destroy() throws InterruptedException {
        final String[] mapNames = new String[]{ "mapA", "mapB", "mapC", "mapD" };
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config config = getConfig();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        HazelcastInstance node3 = factory.newHazelcastInstance(config);
        final AtomicBoolean stop = new AtomicBoolean(false);
        ArrayList<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < (QueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT); i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    while (!(stop.get())) {
                        String name = mapNames[RandomPicker.getInt(0, 4)];
                        final IMap<Integer, Integer> map = node1.getMap(name);
                        int key = RandomPicker.getInt(0, Integer.MAX_VALUE);
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
        HazelcastTestSupport.sleepSeconds(QueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        for (Thread thread : threads) {
            thread.join();
        }
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node3);
    }

    @Test
    public void removes_user_listener_upon_query_cache_destroy() {
        String name = "mapA";
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        Config config = getConfig();
        final HazelcastInstance node1 = factory.newHazelcastInstance(config);
        final HazelcastInstance node2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.assertClusterSizeEventually(2, node1, node2);
        final IMap<Integer, Integer> map = node1.getMap(name);
        int key = RandomPicker.getInt(0, Integer.MAX_VALUE);
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
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node1);
        QueryCacheMemoryLeakTest.assertNoUserListenerLeft(node2);
    }

    @Test
    public void removes_internal_query_caches_upon_map_destroy() {
        Config config = getConfig();
        HazelcastInstance node = createHazelcastInstance(config);
        String mapName = "test";
        IMap<Integer, Integer> map = node.getMap(mapName);
        QueryCacheMemoryLeakTest.populateMap(map);
        for (int j = 0; j < 10; j++) {
            map.getQueryCache((j + "-test-QC"), INSTANCE, true);
        }
        map.destroy();
        SubscriberContext subscriberContext = QueryCacheMemoryLeakTest.getSubscriberContext(node);
        QueryCacheEndToEndProvider provider = subscriberContext.getEndToEndQueryCacheProvider();
        QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        Assert.assertEquals(0, provider.getQueryCacheCount(mapName));
        Assert.assertEquals(0, queryCacheFactory.getQueryCacheCount());
    }

    @Test
    public void no_query_cache_left_after_creating_and_destroying_same_map_concurrently() throws Exception {
        Config config = getConfig();
        final HazelcastInstance node = createHazelcastInstance(config);
        final String mapName = "test";
        ExecutorService pool = Executors.newFixedThreadPool(QueryCacheMemoryLeakTest.STRESS_TEST_THREAD_COUNT);
        final AtomicBoolean stop = new AtomicBoolean();
        for (int i = 0; i < 1000; i++) {
            Runnable runnable = new Runnable() {
                public void run() {
                    while (!(stop.get())) {
                        IMap<Integer, Integer> map = node.getMap(mapName);
                        try {
                            QueryCacheMemoryLeakTest.populateMap(map);
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
        HazelcastTestSupport.sleepSeconds(QueryCacheMemoryLeakTest.STRESS_TEST_RUN_SECONDS);
        stop.set(true);
        pool.shutdown();
        pool.awaitTermination(120, TimeUnit.SECONDS);
        SubscriberContext subscriberContext = QueryCacheMemoryLeakTest.getSubscriberContext(node);
        final QueryCacheEndToEndProvider provider = subscriberContext.getEndToEndQueryCacheProvider();
        final QueryCacheFactory queryCacheFactory = subscriberContext.getQueryCacheFactory();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, provider.getQueryCacheCount(mapName));
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, queryCacheFactory.getQueryCacheCount());
            }
        });
        QueryCacheMemoryLeakTest.assertNoListenerLeftOnEventService(node);
        QueryCacheMemoryLeakTest.assertNoRegisteredListenerLeft(node, mapName);
        QueryCacheMemoryLeakTest.assertNoAccumulatorInfoSupplierLeft(node, mapName);
        QueryCacheMemoryLeakTest.assertNoPartitionAccumulatorRegistryLeft(node, mapName);
    }
}

