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
package com.hazelcast.map.impl.mapstore;


import com.hazelcast.config.Config;
import com.hazelcast.config.MapStoreConfig.InitialLoadMode;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParametersRunnerFactory;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.SlowTest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Test if a node joining a cluster which is loading data works.
 * <p/>
 * Thanks Lukas Blunschi (@lukasblu) for contributing this test originally.
 */
@RunWith(Parameterized.class)
@Parameterized.UseParametersRunnerFactory(HazelcastParametersRunnerFactory.class)
@Category(SlowTest.class)
public class MapStoreDataLoadingContinuesWhenNodeJoins extends HazelcastTestSupport {
    private static final String MAP_NAME = "testMap";

    private static final boolean SIMULATE_SECOND_NODE = true;

    private static final int WRITE_DELAY_SECONDS = 5;

    private static final int PRELOAD_SIZE = 1000;

    private static final int MS_PER_LOAD = 300;

    private static final int NODE_COUNT = 2;

    private static final ILogger LOGGER = Logger.getLogger(MapStoreDataLoadingContinuesWhenNodeJoins.class);

    @Parameterized.Parameter
    public InitialLoadMode initialLoadMode;

    private final AtomicReferenceArray<HazelcastInstance> instances = new AtomicReferenceArray<HazelcastInstance>(MapStoreDataLoadingContinuesWhenNodeJoins.NODE_COUNT);

    @Test(timeout = 600000)
    public void testNoDeadLockDuringJoin() throws Exception {
        // create shared hazelcast config
        final Config config = createConfigWithDelayingMapStore();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(MapStoreDataLoadingContinuesWhenNodeJoins.NODE_COUNT);
        final CountDownLatch node1Started = new CountDownLatch(1);
        final CountDownLatch node1FinishedLoading = new CountDownLatch(1);
        final AtomicBoolean thread1FinishedFirst = new AtomicBoolean();
        // thread 1: start a single member and load the data
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance hcInstance = factory.newHazelcastInstance(config);
                instances.set(0, hcInstance);
                node1Started.countDown();
                // get map and trigger loading the data
                IMap<String, String> map = hcInstance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                map.size();
                node1FinishedLoading.countDown();
            }
        }, "Thread 1");
        thread1.start();
        node1Started.await();
        // thread 2: a second member joins the cluster
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance hcInstance = factory.newHazelcastInstance(config);
                try {
                    hcInstance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                    final int loadTimeMillis = (MapStoreDataLoadingContinuesWhenNodeJoins.MS_PER_LOAD) * (MapStoreDataLoadingContinuesWhenNodeJoins.PRELOAD_SIZE);
                    thread1FinishedFirst.set(node1FinishedLoading.await(loadTimeMillis, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    HazelcastTestSupport.ignore(e);
                }
            }
        }, "Thread 2");
        thread2.start();
        // join threads
        thread1.join();
        thread2.join();
        // assert correct shutdown order
        Assert.assertTrue("Thread 2 was shutdown before thread 1.", thread1FinishedFirst.get());
    }

    @Test(timeout = 600000)
    public void testLoadingFinishes_whenMemberJoinsWhileLoading() throws Exception {
        Assume.assumeThat("With LAZY InMemoryModel this test may fail due to a known issue reported in OS #11544 and #12384", initialLoadMode, Matchers.not(InitialLoadMode.LAZY));
        final Config config = createConfigWithDelayingMapStore();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final CountDownLatch node1MapLoadingAboutToStart = new CountDownLatch(1);
        final CountDownLatch node1FinishedLoading = new CountDownLatch(1);
        final AtomicInteger mapSizeOnNode2 = new AtomicInteger();
        // thread 1: start a single node and trigger loading the data
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance instance = factory.newHazelcastInstance(config);
                instances.set(0, instance);
                // get map and trigger loading the data
                IMap<String, String> map = instance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                node1MapLoadingAboutToStart.countDown();
                MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info("Getting the size of the map on node1 -> load is triggered");
                int sizeOnNode1 = map.size();
                MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info("Map loading has been completed by now");
                MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info(("Map size on node 1: " + sizeOnNode1));
                node1FinishedLoading.countDown();
            }
        }, "Thread 1");
        thread1.start();
        node1MapLoadingAboutToStart.await();
        // thread 2: second member joins the cluster while loading is in progress
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance instance = factory.newHazelcastInstance(config);
                instances.set(1, instance);
                try {
                    MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info(("Getting the map " + (MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME)));
                    IMap map = instance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                    final int loadTimeMillis = (MapStoreDataLoadingContinuesWhenNodeJoins.MS_PER_LOAD) * (MapStoreDataLoadingContinuesWhenNodeJoins.PRELOAD_SIZE);
                    boolean node1FinishedLoadingInTime = node1FinishedLoading.await(loadTimeMillis, TimeUnit.MILLISECONDS);
                    // if node1 doesn't finish in time (unlikely because of the 5min timeout), we may execute GetSizeOperation
                    // again on a not fully loaded map -> map size may not match to the expected value
                    MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info(("Node1 finished loading in time: " + node1FinishedLoadingInTime));
                    MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info("Getting the size of the map on node2");
                    mapSizeOnNode2.set(map.size());
                    MapStoreDataLoadingContinuesWhenNodeJoins.LOGGER.info(("Map size on node 2: " + (mapSizeOnNode2.get())));
                } catch (InterruptedException e) {
                    HazelcastTestSupport.ignore(e);
                }
            }
        }, "Thread 2");
        thread2.start();
        // join threads
        thread1.join();
        thread2.join();
        Assert.assertEquals(MapStoreDataLoadingContinuesWhenNodeJoins.PRELOAD_SIZE, mapSizeOnNode2.get());
    }

    @Test(timeout = 600000)
    public void testDataLoadedCorrectly() throws Exception {
        // create shared hazelcast config
        final Config config = createConfigWithDelayingMapStore();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final CountDownLatch node1Started = new CountDownLatch(1);
        final CountDownLatch node1FinishedLoading = new CountDownLatch(1);
        // thread 1:
        // start a single node and load the data
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance instance = factory.newHazelcastInstance(config);
                instances.set(0, instance);
                node1Started.countDown();
                // get map
                // this will trigger loading the data
                final IMap<String, String> map = instance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                map.size();
                node1FinishedLoading.countDown();
                HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                    @Override
                    public void run() throws Exception {
                        Assert.assertEquals(MapStoreDataLoadingContinuesWhenNodeJoins.PRELOAD_SIZE, map.size());
                    }
                }, 5);
                // -------------------------------------------------- {20s}
            }
        }, "Thread 1");
        thread1.start();
        // wait 10s after starting first thread
        node1Started.await();
        // thread 2:
        // simulate a second member which joins the cluster
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance instance = factory.newHazelcastInstance(config);
                instances.set(1, instance);
                try {
                    // get map
                    instance.getMap(MapStoreDataLoadingContinuesWhenNodeJoins.MAP_NAME);
                    final int loadTimeMillis = (MapStoreDataLoadingContinuesWhenNodeJoins.MS_PER_LOAD) * (MapStoreDataLoadingContinuesWhenNodeJoins.PRELOAD_SIZE);
                    node1FinishedLoading.await(loadTimeMillis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    HazelcastTestSupport.ignore(e);
                }
            }
        }, "Thread 2");
        if (MapStoreDataLoadingContinuesWhenNodeJoins.SIMULATE_SECOND_NODE) {
            thread2.start();
        }
        // join threads
        thread1.join();
        if (MapStoreDataLoadingContinuesWhenNodeJoins.SIMULATE_SECOND_NODE) {
            thread2.join();
        }
    }

    public class InMemoryMapStore implements MapStore<String, String> {
        private final ConcurrentHashMap<String, String> store = new ConcurrentHashMap<String, String>();

        private final AtomicInteger countLoadAllKeys = new AtomicInteger(0);

        private final CountDownLatch halfOfKeysAreLoaded;

        private final int msPerLoad;

        private final boolean sleepBeforeLoadAllKeys;

        InMemoryMapStore(CountDownLatch halfOfKeysAreLoaded, int msPerLoad, boolean sleepBeforeLoadAllKeys) {
            this.halfOfKeysAreLoaded = halfOfKeysAreLoaded;
            this.msPerLoad = msPerLoad;
            this.sleepBeforeLoadAllKeys = sleepBeforeLoadAllKeys;
        }

        void preload(int size) {
            for (int i = 0; i < size; i++) {
                store.put(("k" + i), ("v" + i));
            }
        }

        @Override
        public String load(String key) {
            if ((msPerLoad) > 0) {
                sleep(msPerLoad, false);
            }
            return store.get(key);
        }

        @Override
        public Map<String, String> loadAll(Collection<String> keys) {
            List<String> keysList = new ArrayList<String>(keys);
            int size = keys.size();
            Collections.sort(keysList);
            Map<String, String> result = new HashMap<String, String>();
            int count = 0;
            for (String key : keys) {
                if ((msPerLoad) > 0) {
                    sleep(msPerLoad, false);
                }
                String value = store.get(key);
                if (value != null) {
                    result.put(key, value);
                }
                if (count > (size / 2)) {
                    halfOfKeysAreLoaded.countDown();
                }
                count += 1;
            }
            return result;
        }

        @Override
        public Set<String> loadAllKeys() {
            // sleep 5s to highlight asynchronous behavior
            if (sleepBeforeLoadAllKeys) {
                sleep(5000, true);
            }
            countLoadAllKeys.incrementAndGet();
            Set<String> result = new HashSet<String>(store.keySet());
            List<String> resultList = new ArrayList<String>(result);
            Collections.sort(resultList);
            return result;
        }

        @Override
        public void store(String key, String value) {
            store.put(key, value);
        }

        @Override
        public void storeAll(Map<String, String> map) {
            store.putAll(map);
        }

        @Override
        public void delete(String key) {
            store.remove(key);
        }

        @Override
        public void deleteAll(Collection<String> keys) {
            List<String> keysList = new ArrayList<String>(keys);
            Collections.sort(keysList);
            for (String key : keys) {
                store.remove(key);
            }
        }
    }
}

