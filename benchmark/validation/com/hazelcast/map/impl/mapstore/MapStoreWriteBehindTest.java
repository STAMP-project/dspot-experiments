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


import GroupProperty.MAP_REPLICA_SCHEDULED_TASK_DELAY_SECONDS;
import GroupProperty.PARTITION_COUNT;
import InitialLoadMode.EAGER;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.core.TransactionalMap;
import com.hazelcast.map.impl.mapstore.writebehind.TestMapUsingMapStoreBuilder;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.NightlyTest;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.transaction.TransactionContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.map.impl.mapstore.EventBasedMapStore.STORE_EVENTS.DELETE;
import static com.hazelcast.map.impl.mapstore.EventBasedMapStore.STORE_EVENTS.LOAD;
import static com.hazelcast.map.impl.mapstore.EventBasedMapStore.STORE_EVENTS.LOAD_ALL_KEYS;
import static com.hazelcast.map.impl.mapstore.EventBasedMapStore.STORE_EVENTS.STORE;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapStoreWriteBehindTest extends AbstractMapStoreTest {
    @Test(timeout = 120000)
    public void testOneMemberWriteBehindWithMaxIdle() {
        final EventBasedMapStore testMapStore = new EventBasedMapStore();
        Config config = newConfig(testMapStore, 5, EAGER);
        config.setProperty(PARTITION_COUNT.getName(), "1");
        config.getMapConfig("default").setMaxIdleSeconds(10);
        HazelcastInstance instance = createHazelcastInstance(config);
        final IMap<Integer, String> map = instance.getMap("default");
        final int total = 10;
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(LOAD_ALL_KEYS, testMapStore.getEvents().poll());
            }
        });
        for (int i = 0; i < total; i++) {
            map.put(i, ("value" + i));
        }
        HazelcastTestSupport.sleepSeconds(11);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, map.size());
            }
        });
        Assert.assertEquals(total, testMapStore.getStore().size());
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteBehindWithEvictions() throws Exception {
        final String mapName = "testOneMemberWriteBehindWithEvictions";
        final EventBasedMapStore testMapStore = new EventBasedMapStore();
        testMapStore.loadAllLatch = new CountDownLatch(1);
        final Config config = newConfig(testMapStore, 2, EAGER);
        final HazelcastInstance instance = createHazelcastInstance(config);
        final IMap<Integer, String> map = instance.getMap(mapName);
        // check if load all called.
        Assert.assertTrue("map store loadAllKeys must be called", testMapStore.loadAllLatch.await(10, TimeUnit.SECONDS));
        // map population count.
        final int populationCount = 100;
        // latch for store & storeAll events.
        testMapStore.storeLatch = new CountDownLatch(populationCount);
        // populate map.
        for (int i = 0; i < populationCount; i++) {
            map.put(i, ("value" + i));
        }
        // wait for all store ops.
        HazelcastTestSupport.assertOpenEventually(testMapStore.storeLatch);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, writeBehindQueueSize(instance, mapName));
            }
        });
        // init before eviction
        testMapStore.storeLatch = new CountDownLatch(populationCount);
        // evict.
        for (int i = 0; i < populationCount; i++) {
            map.evict(i);
        }
        // expect no store op
        Assert.assertEquals(populationCount, testMapStore.storeLatch.getCount());
        // check store size
        Assert.assertEquals(populationCount, testMapStore.getStore().size());
        // check map size
        Assert.assertEquals(0, map.size());
        // re-populate map
        for (int i = 0; i < populationCount; i++) {
            map.put(i, ("value" + i));
        }
        // evict again
        for (int i = 0; i < populationCount; i++) {
            map.evict(i);
        }
        // wait for all store ops
        testMapStore.storeLatch.await(10, TimeUnit.SECONDS);
        // check store size
        Assert.assertEquals(populationCount, testMapStore.getStore().size());
        // check map size
        Assert.assertEquals(0, map.size());
        // re-populate map
        for (int i = 0; i < populationCount; i++) {
            map.put(i, ("value" + i));
        }
        testMapStore.deleteLatch = new CountDownLatch(populationCount);
        // clear map
        for (int i = 0; i < populationCount; i++) {
            map.remove(i);
        }
        testMapStore.deleteLatch.await(10, TimeUnit.SECONDS);
        // check map size
        Assert.assertEquals(0, map.size());
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteBehind() throws Exception {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 5);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        testMapStore.insert("1", "value1");
        IMap<String, String> map = instance.getMap("default");
        Assert.assertEquals(0, map.size());
        Assert.assertEquals("value1", map.get("1"));
        Assert.assertEquals("value1", map.put("1", "value2"));
        Assert.assertEquals("value2", map.get("1"));
        // store should have the old data as we will write-behind
        Assert.assertEquals("value1", testMapStore.getStore().get("1"));
        Assert.assertEquals(1, map.size());
        map.flush();
        Assert.assertTrue(map.evict("1"));
        Assert.assertEquals("value2", testMapStore.getStore().get("1"));
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(1, testMapStore.getStore().size());
        Assert.assertEquals("value2", map.get("1"));
        Assert.assertEquals(1, map.size());
        map.remove("1");
        // store should have the old data as we will delete-behind
        Assert.assertEquals(1, testMapStore.getStore().size());
        Assert.assertEquals(0, map.size());
        testMapStore.assertAwait(100);
        Assert.assertEquals(0, testMapStore.getStore().size());
    }

    @Test(timeout = 120000)
    public void testWriteBehindUpdateSameKey() throws Exception {
        final MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(2, 0, 0);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 5);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        nodeFactory.newHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap("map");
        map.put("key", "value");
        Thread.sleep(2000);
        map.put("key", "value2");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals("value2", testMapStore.getStore().get("key"));
            }
        });
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteBehindFlush() {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        testMapStore.setLoadAllKeys(false);
        int writeDelaySeconds = 10;
        Config config = newConfig(testMapStore, writeDelaySeconds);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map = instance.getMap("default");
        Assert.assertEquals(0, map.size());
        long timeBeforePut = System.nanoTime();
        Assert.assertEquals("Map produced a value out of thin air", null, map.put("1", "value1"));
        Assert.assertEquals("Map did not return a previously stored value", "value1", map.get("1"));
        String mapStoreValue = ((String) (testMapStore.getStore().get("1")));
        if (mapStoreValue != null) {
            assertMapStoreDidNotFlushValueTooSoon(testMapStore, writeDelaySeconds, timeBeforePut);
            Assert.assertEquals("value1", mapStoreValue);
        }
        Assert.assertEquals(1, map.size());
        map.flush();
        Assert.assertEquals("value1", testMapStore.getStore().get("1"));
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteBehind2() {
        final EventBasedMapStore testMapStore = new EventBasedMapStore();
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 1, EAGER);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap("default");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Object event = testMapStore.getEvents().poll();
                Assert.assertEquals(LOAD_ALL_KEYS, event);
            }
        });
        map.put("1", "value1");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(LOAD, testMapStore.getEvents().poll());
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(STORE, testMapStore.getEvents().poll());
            }
        });
        map.remove("1");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(DELETE, testMapStore.getEvents().poll());
            }
        });
    }

    // issue #2747: when MapStore configured with write behind, distributed objects' destroy method does not work
    @Test(timeout = 120000)
    @Category(NightlyTest.class)
    public void testWriteBehindDestroy() {
        final int writeDelaySeconds = 5;
        String mapName = HazelcastTestSupport.randomMapName();
        final MapStore<String, String> store = new MapStoreTest.SimpleMapStore<String, String>();
        Config config = newConfig(mapName, store, writeDelaySeconds);
        HazelcastInstance hzInstance = createHazelcastInstance(config);
        IMap<String, String> map = hzInstance.getMap(mapName);
        map.put("key", "value");
        map.destroy();
        HazelcastTestSupport.sleepSeconds((2 * writeDelaySeconds));
        Assert.assertNotEquals("value", store.load("key"));
    }

    @Test(timeout = 120000)
    public void testKeysWithPredicateShouldLoadMapStore() {
        EventBasedMapStore<String, Integer> testMapStore = new EventBasedMapStore<String, Integer>().insert("key1", 17).insert("key2", 23).insert("key3", 47);
        HazelcastInstance instance = createHazelcastInstance(newConfig(testMapStore, 0));
        final IMap map = instance.getMap("default");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Set result = map.keySet();
                HazelcastTestSupport.assertContains(result, "key1");
                HazelcastTestSupport.assertContains(result, "key2");
                HazelcastTestSupport.assertContains(result, "key3");
            }
        });
    }

    @Test(timeout = 120000)
    public void testIssue1085WriteBehindBackup() {
        Config config = getConfig();
        String name = "testIssue1085WriteBehindBackup";
        MapConfig writeBehindBackup = config.getMapConfig(name);
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setWriteDelaySeconds(5);
        int size = 1000;
        MapStoreTest.MapStoreWithStoreCount mapStore = new MapStoreTest.MapStoreWithStoreCount(size, 120);
        mapStoreConfig.setImplementation(mapStore);
        writeBehindBackup.setMapStoreConfig(mapStoreConfig);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map = instance.getMap(name);
        for (int i = 0; i < size; i++) {
            map.put(i, i);
        }
        instance2.getLifecycleService().shutdown();
        mapStore.awaitStores();
    }

    @Test(timeout = 120000)
    public void testIssue1085WriteBehindBackupWithLongRunnigMapStore() {
        final String name = HazelcastTestSupport.randomMapName("testIssue1085WriteBehindBackup");
        final int expectedStoreCount = 3;
        final int nodeCount = 3;
        Config config = getConfig();
        config.setProperty(MAP_REPLICA_SCHEDULED_TASK_DELAY_SECONDS.getName(), "30");
        MapConfig writeBehindBackupConfig = config.getMapConfig(name);
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setWriteDelaySeconds(5);
        final MapStoreTest.MapStoreWithStoreCount mapStore = new MapStoreTest.MapStoreWithStoreCount(expectedStoreCount, 300, 50);
        mapStoreConfig.setImplementation(mapStore);
        writeBehindBackupConfig.setMapStoreConfig(mapStoreConfig);
        // create nodes.
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        HazelcastInstance node1 = factory.newHazelcastInstance(config);
        HazelcastInstance node2 = factory.newHazelcastInstance(config);
        HazelcastInstance node3 = factory.newHazelcastInstance(config);
        // create corresponding keys.
        final String keyOwnedByNode1 = HazelcastTestSupport.generateKeyOwnedBy(node1);
        final String keyOwnedByNode2 = HazelcastTestSupport.generateKeyOwnedBy(node2);
        final String keyOwnedByNode3 = HazelcastTestSupport.generateKeyOwnedBy(node3);
        // put one key value pair per node.
        final IMap<String, Integer> map = node1.getMap(name);
        map.put(keyOwnedByNode1, 1);
        map.put(keyOwnedByNode2, 2);
        map.put(keyOwnedByNode3, 3);
        // shutdown node2.
        node2.getLifecycleService().shutdown();
        // wait store ops. finish.
        mapStore.awaitStores();
        // we should see at least expected store count.
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                int storeOperationCount = mapStore.count.intValue();
                Assert.assertTrue(((("expected: " + expectedStoreCount) + ", actual: ") + storeOperationCount), (expectedStoreCount <= storeOperationCount));
            }
        });
    }

    @Test(timeout = 120000)
    public void testMapDelete_whenLoadFails() {
        final MapStoreWriteBehindTest.FailingLoadMapStore mapStore = new MapStoreWriteBehindTest.FailingLoadMapStore();
        final IMap<Object, Object> map = TestMapUsingMapStoreBuilder.create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).build();
        try {
            map.delete(1);
        } catch (IllegalStateException e) {
            Assert.fail();
        }
    }

    @Test(timeout = 120000, expected = IllegalStateException.class)
    public void testMapRemove_whenMapStoreLoadFails() {
        final MapStoreWriteBehindTest.FailingLoadMapStore mapStore = new MapStoreWriteBehindTest.FailingLoadMapStore();
        final IMap<Object, Object> map = TestMapUsingMapStoreBuilder.create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).build();
        map.remove(1);
    }

    @Test(timeout = 120000)
    public void testIssue1085WriteBehindBackupTransactional() {
        final String name = HazelcastTestSupport.randomMapName();
        final int size = 1000;
        MapStoreTest.MapStoreWithStoreCount mapStore = new MapStoreTest.MapStoreWithStoreCount(size, 120);
        Config config = newConfig(name, mapStore, 5);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        TransactionContext context = instance.newTransactionContext();
        context.beginTransaction();
        TransactionalMap<Object, Object> tmap = context.getMap(name);
        for (int i = 0; i < size; i++) {
            tmap.put(i, i);
        }
        context.commitTransaction();
        instance2.getLifecycleService().shutdown();
        mapStore.awaitStores();
    }

    @Test(timeout = 120000)
    public void testWriteBehindSameSecondSameKey() {
        final MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(100, 0, 0);// In some cases 2 store operation may happened

        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 2);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<Object, Object> map = instance.getMap("testWriteBehindSameSecondSameKey");
        final int size1 = 20;
        final int size2 = 10;
        for (int i = 0; i < size1; i++) {
            map.put("key", ("value" + i));
        }
        for (int i = 0; i < size2; i++) {
            map.put(("key" + i), ("value" + i));
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(("value" + (size1 - 1)), testMapStore.getStore().get("key"));
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(("value" + (size2 - 1)), testMapStore.getStore().get(("key" + (size2 - 1))));
            }
        });
    }

    @Test(timeout = 120000)
    public void testWriteBehindWriteRemoveOrderOfSameKey() {
        final String mapName = HazelcastTestSupport.randomMapName("_testWriteBehindWriteRemoveOrderOfSameKey_");
        final int iterationCount = 5;
        final int delaySeconds = 1;
        final int putOps = 3;
        final int removeOps = 2;
        final int expectedStoreSizeEventually = 1;
        final MapStoreWriteBehindTest.RecordingMapStore store = new MapStoreWriteBehindTest.RecordingMapStore((iterationCount * putOps), (iterationCount * removeOps));
        final Config config = newConfig(mapName, store, delaySeconds);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<String, String> map = node.getMap(mapName);
        String key = "key";
        for (int i = 0; i < iterationCount; i++) {
            String value = "value" + i;
            map.put(key, value);
            map.remove(key);
            map.put(key, value);
            map.remove(key);
            map.put(key, value);
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(expectedStoreSizeEventually, store.getStore().size());
            }
        });
        Assert.assertEquals(("value" + (iterationCount - 1)), map.get(key));
    }

    @Test(timeout = 120000)
    public void mapStore_setOnIMapDoesNotRemoveKeyFromWriteBehindDeleteQueue() {
        MapStoreConfig mapStoreConfig = new MapStoreConfig().setEnabled(true).setImplementation(new MapStoreTest.SimpleMapStore<String, String>()).setWriteDelaySeconds(Integer.MAX_VALUE);
        Config config = getConfig();
        config.getMapConfig("map").setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<String, String> map = instance.getMap("map");
        map.put("foo", "bar");
        map.remove("foo");
        map.set("foo", "bar");
        Assert.assertEquals("bar", map.get("foo"));
    }

    @Test(timeout = 120000)
    public void testDelete_thenPutIfAbsent_withWriteBehindEnabled() {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        Config config = newConfig(testMapStore, 100);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<Integer, Integer> map = instance.getMap("default");
        map.put(1, 1);
        map.delete(1);
        final Object putIfAbsent = map.putIfAbsent(1, 2);
        Assert.assertNull(putIfAbsent);
    }

    public static class RecordingMapStore implements MapStore<String, String> {
        private static final boolean DEBUG = false;

        private final CountDownLatch expectedStore;

        private final CountDownLatch expectedRemove;

        private final ConcurrentHashMap<String, String> store;

        RecordingMapStore(int expectedStore, int expectedRemove) {
            this.expectedStore = new CountDownLatch(expectedStore);
            this.expectedRemove = new CountDownLatch(expectedRemove);
            this.store = new ConcurrentHashMap<String, String>();
        }

        public ConcurrentHashMap<String, String> getStore() {
            return store;
        }

        @Override
        public String load(String key) {
            log((("load(" + key) + ") called."));
            return store.get(key);
        }

        @Override
        public Map<String, String> loadAll(Collection<String> keys) {
            if (MapStoreWriteBehindTest.RecordingMapStore.DEBUG) {
                List<String> keysList = new ArrayList<String>(keys);
                Collections.sort(keysList);
                log((("loadAll(" + keysList) + ") called."));
            }
            Map<String, String> result = new HashMap<String, String>();
            for (String key : keys) {
                String value = store.get(key);
                if (value != null) {
                    result.put(key, value);
                }
            }
            return result;
        }

        @Override
        public Set<String> loadAllKeys() {
            log("loadAllKeys() called.");
            Set<String> result = new HashSet<String>(store.keySet());
            log(("loadAllKeys result = " + result));
            return result;
        }

        @Override
        public void store(String key, String value) {
            log((("store(" + key) + ") called."));
            String valuePrev = store.put(key, value);
            expectedStore.countDown();
            if (valuePrev != null) {
                log(("- Unexpected Update (operations reordered?): " + key));
            }
        }

        @Override
        public void storeAll(Map<String, String> map) {
            if (MapStoreWriteBehindTest.RecordingMapStore.DEBUG) {
                TreeSet<String> setSorted = new TreeSet<String>(map.keySet());
                log((("storeAll(" + setSorted) + ") called."));
            }
            store.putAll(map);
            final int size = map.keySet().size();
            for (int i = 0; i < size; i++) {
                expectedStore.countDown();
            }
        }

        @Override
        public void delete(String key) {
            log((("delete(" + key) + ") called."));
            String valuePrev = store.remove(key);
            expectedRemove.countDown();
            if (valuePrev == null) {
                log(("- Unnecessary delete (operations reordered?): " + key));
            }
        }

        @Override
        public void deleteAll(Collection<String> keys) {
            if (MapStoreWriteBehindTest.RecordingMapStore.DEBUG) {
                List<String> keysList = new ArrayList<String>(keys);
                Collections.sort(keysList);
                log((("deleteAll(" + keysList) + ") called."));
            }
            for (String key : keys) {
                String valuePrev = store.remove(key);
                expectedRemove.countDown();
                if (valuePrev == null) {
                    log(("- Unnecessary delete (operations reordered?): " + key));
                }
            }
        }

        private void log(String msg) {
            if (MapStoreWriteBehindTest.RecordingMapStore.DEBUG) {
                System.out.println(msg);
            }
        }
    }

    public static class FailAwareMapStore implements MapStore {
        final Map<Object, Object> db = new ConcurrentHashMap<Object, Object>();

        final AtomicLong deletes = new AtomicLong();

        final AtomicLong deleteAlls = new AtomicLong();

        final AtomicLong stores = new AtomicLong();

        final AtomicLong storeAlls = new AtomicLong();

        final AtomicLong loads = new AtomicLong();

        final AtomicLong loadAlls = new AtomicLong();

        final AtomicLong loadAllKeys = new AtomicLong();

        final AtomicBoolean storeFail = new AtomicBoolean(false);

        final AtomicBoolean loadFail = new AtomicBoolean(false);

        final List<BlockingQueue<Object>> listeners = new CopyOnWriteArrayList<BlockingQueue<Object>>();

        public void addListener(BlockingQueue<Object> obj) {
            listeners.add(obj);
        }

        public void notifyListeners() {
            for (BlockingQueue<Object> listener : listeners) {
                listener.offer(new Object());
            }
        }

        @Override
        public void delete(Object key) {
            try {
                if (storeFail.get()) {
                    throw new RuntimeException();
                } else {
                    db.remove(key);
                }
            } finally {
                deletes.incrementAndGet();
                notifyListeners();
            }
        }

        public void setFail(boolean shouldFail, boolean loadFail) {
            this.storeFail.set(shouldFail);
            this.loadFail.set(loadFail);
        }

        @Override
        public void store(Object key, Object value) {
            try {
                if (storeFail.get()) {
                    throw new RuntimeException();
                } else {
                    db.put(key, value);
                }
            } finally {
                stores.incrementAndGet();
                notifyListeners();
            }
        }

        @Override
        public Set loadAllKeys() {
            try {
                return db.keySet();
            } finally {
                loadAllKeys.incrementAndGet();
            }
        }

        @Override
        public Object load(Object key) {
            try {
                if (loadFail.get()) {
                    throw new RuntimeException();
                } else {
                    return db.get(key);
                }
            } finally {
                loads.incrementAndGet();
            }
        }

        @Override
        public void storeAll(Map map) {
            try {
                if (storeFail.get()) {
                    throw new RuntimeException();
                } else {
                    db.putAll(map);
                }
            } finally {
                storeAlls.incrementAndGet();
                notifyListeners();
            }
        }

        @Override
        public Map loadAll(Collection keys) {
            try {
                if (loadFail.get()) {
                    throw new RuntimeException();
                } else {
                    Map<Object, Object> results = new HashMap<Object, Object>();
                    for (Object key : keys) {
                        Object value = db.get(key);
                        if (value != null) {
                            results.put(key, value);
                        }
                    }
                    return results;
                }
            } finally {
                loadAlls.incrementAndGet();
                notifyListeners();
            }
        }

        @Override
        public void deleteAll(Collection keys) {
            try {
                if (storeFail.get()) {
                    throw new RuntimeException();
                } else {
                    for (Object key : keys) {
                        db.remove(key);
                    }
                }
            } finally {
                deleteAlls.incrementAndGet();
                notifyListeners();
            }
        }
    }

    class FailingLoadMapStore extends MapStoreAdapter<Object, Object> {
        @Override
        public Object load(Object key) {
            throw new IllegalStateException();
        }
    }
}

