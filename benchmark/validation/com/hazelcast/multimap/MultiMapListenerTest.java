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
package com.hazelcast.multimap;


import MultiMapConfig.ValueCollectionType.LIST;
import com.hazelcast.config.Config;
import com.hazelcast.config.EntryListenerConfig;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.MultiMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MultiMapListenerTest extends HazelcastTestSupport {
    @Test(expected = IllegalArgumentException.class)
    public void testAddLocalEntryListener_whenNull() {
        MultiMap<String, String> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        multiMap.addLocalEntryListener(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_whenListenerNull() {
        MultiMap<String, String> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        multiMap.addEntryListener(null, true);
    }

    @Test
    public void testRemoveListener() {
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener(1);
        String id = multiMap.addEntryListener(listener, true);
        Assert.assertTrue(multiMap.removeEntryListener(id));
    }

    @Test
    public void testRemoveListener_whenNotExist() {
        MultiMap<String, String> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        Assert.assertFalse(multiMap.removeEntryListener("NOT_THERE"));
    }

    @Test
    public void testListenerEntryAddEvent() {
        int maxKeys = 12;
        int maxItems = 3;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener((maxKeys * maxItems));
        multiMap.addEntryListener(listener, true);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                multiMap.put(i, j);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerEntryAddEvent_whenValueNotIncluded() {
        int maxKeys = 21;
        int maxItems = 3;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener((maxKeys * maxItems));
        multiMap.addEntryListener(listener, false);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                multiMap.put(i, j);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerEntryRemoveEvent() {
        int maxKeys = 25;
        int maxItems = 3;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener((maxKeys * maxItems));
        multiMap.addEntryListener(listener, true);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                multiMap.put(i, j);
                multiMap.remove(i);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerEntryRemoveEvent_whenValueNotIncluded() {
        int maxKeys = 31;
        int maxItems = 3;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener((maxKeys * maxItems));
        multiMap.addEntryListener(listener, false);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                multiMap.put(i, j);
                multiMap.remove(i);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryAddEvent() {
        Object key = "key";
        int maxItems = 42;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener(maxItems);
        multiMap.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerOnKeyEntryAddEvent_whenValueNotIncluded() {
        Object key = "key";
        int maxItems = 72;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener(maxItems);
        multiMap.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemoveEvent() {
        Object key = "key";
        int maxItems = 88;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener(maxItems);
        multiMap.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
            multiMap.remove(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemoveEvent_whenValueNotIncluded() {
        Object key = "key";
        int maxItems = 62;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener(maxItems);
        multiMap.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
            multiMap.remove(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemove_WithOneRemove() {
        Object key = "key";
        int maxItems = 98;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNotNullListener(maxItems, 1);
        multiMap.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
        }
        multiMap.remove(key);
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemove_WithOneRemoveWhenValueNotIncluded() {
        Object key = "key";
        int maxItems = 56;
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener(maxItems, 1);
        multiMap.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            multiMap.put(key, i);
        }
        multiMap.remove(key);
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListeners_clearAll() {
        MultiMap<Object, Object> multiMap = createHazelcastInstance().getMultiMap(HazelcastTestSupport.randomString());
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener(1);
        multiMap.addEntryListener(listener, false);
        multiMap.put("key", "value");
        multiMap.clear();
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
        HazelcastTestSupport.assertOpenEventually(listener.clearLatch);
    }

    @Test
    public void testListeners_clearAllFromNode() {
        String name = HazelcastTestSupport.randomString();
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<Object, Object> multiMap = instance.getMultiMap(name);
        MultiMapListenerTest.MyEntryListener listener = new MultiMapListenerTest.CountDownValueNullListener(1);
        multiMap.addEntryListener(listener, false);
        multiMap.put("key", "value");
        multiMap.clear();
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
        HazelcastTestSupport.assertOpenEventually(listener.clearLatch);
    }

    @Test
    public void testConfigListenerRegistration() {
        String name = "default";
        final CountDownLatch latch = new CountDownLatch(1);
        EntryListenerConfig entryListenerConfig = new EntryListenerConfig().setImplementation(new EntryAdapter() {
            public void entryAdded(EntryEvent event) {
                latch.countDown();
            }
        });
        Config config = new Config();
        config.getMultiMapConfig(name).addEntryListenerConfig(entryListenerConfig);
        HazelcastInstance hz = createHazelcastInstance(config);
        hz.getMultiMap(name).put(1, 1);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void testMultiMapEntryListener() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapEntryListener");
        final Set<String> expectedValues = new CopyOnWriteArraySet<String>();
        expectedValues.add("hello");
        expectedValues.add("world");
        expectedValues.add("again");
        final CountDownLatch latchAdded = new CountDownLatch(3);
        final CountDownLatch latchRemoved = new CountDownLatch(1);
        final CountDownLatch latchCleared = new CountDownLatch(1);
        multiMap.addEntryListener(new EntryAdapter<String, String>() {
            public void entryAdded(EntryEvent<String, String> event) {
                String key = event.getKey();
                String value = event.getValue();
                if ("2".equals(key)) {
                    Assert.assertEquals("again", value);
                } else {
                    Assert.assertEquals("1", key);
                }
                HazelcastTestSupport.assertContains(expectedValues, value);
                expectedValues.remove(value);
                latchAdded.countDown();
            }

            public void entryRemoved(EntryEvent<String, String> event) {
                Assert.assertEquals("2", event.getKey());
                Assert.assertEquals("again", event.getOldValue());
                latchRemoved.countDown();
            }

            public void entryUpdated(EntryEvent<String, String> event) {
                throw new AssertionError("MultiMap cannot get update event!");
            }

            public void entryEvicted(EntryEvent<String, String> event) {
                entryRemoved(event);
            }

            @Override
            public void mapEvicted(MapEvent event) {
            }

            @Override
            public void mapCleared(MapEvent event) {
                latchCleared.countDown();
            }
        }, true);
        multiMap.put("1", "hello");
        multiMap.put("1", "world");
        multiMap.put("2", "again");
        Collection<String> values = multiMap.get("1");
        Assert.assertEquals(2, values.size());
        HazelcastTestSupport.assertContains(values, "hello");
        HazelcastTestSupport.assertContains(values, "world");
        Assert.assertEquals(1, multiMap.get("2").size());
        Assert.assertEquals(3, multiMap.size());
        multiMap.remove("2");
        Assert.assertEquals(2, multiMap.size());
        multiMap.clear();
        HazelcastTestSupport.assertOpenEventually(latchAdded);
        HazelcastTestSupport.assertOpenEventually(latchRemoved);
        HazelcastTestSupport.assertOpenEventually(latchCleared);
    }

    @Test
    public void testListeners_local() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = factory.newInstances(config);
        HazelcastInstance localInstance = instances[0];
        MultiMap<String, String> localMultiMap = localInstance.getMultiMap(name);
        MultiMapListenerTest.KeyCollectingListener<String> listener = new MultiMapListenerTest.KeyCollectingListener<String>();
        localMultiMap.addLocalEntryListener(listener);
        localMultiMap.put("key1", "val1");
        localMultiMap.put("key2", "val2");
        localMultiMap.put("key3", "val3");
        localMultiMap.put("key4", "val4");
        localMultiMap.put("key5", "val5");
        localMultiMap.put("key6", "val6");
        localMultiMap.put("key7", "val7");
        // we want at least one key to be guaranteed to trigger the local listener
        localMultiMap.put(HazelcastTestSupport.generateKeyOwnedBy(localInstance), "val8");
        // see if the local listener was called for all local entries
        MultiMapListenerTest.assertContainsAllEventually(listener.keys, localMultiMap.localKeySet());
        // remove something -> this should remove the key from the listener
        String keyToRemove = listener.keys.iterator().next();
        System.out.println(("Local key set: " + (localMultiMap.localKeySet())));
        System.out.println(("Removing " + keyToRemove));
        localMultiMap.remove(keyToRemove);
        System.out.println(("Local key set: " + (localMultiMap.localKeySet())));
        MultiMapListenerTest.assertContainsAllEventually(localMultiMap.localKeySet(), listener.keys);
        localInstance.getMultiMap(name).clear();
        HazelcastTestSupport.assertSizeEventually(0, listener.keys);
    }

    @Test
    public void testListeners_distributed() {
        String name = HazelcastTestSupport.randomMapName();
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = factory.newInstances(config);
        MultiMap<String, String> multiMap = instances[0].getMultiMap(name);
        MultiMapListenerTest.KeyCollectingListener<String> listener = new MultiMapListenerTest.KeyCollectingListener<String>();
        String id2 = multiMap.addEntryListener(listener, true);
        MultiMapListenerTest.getMultiMap(instances, name).put("key3", "val3");
        MultiMapListenerTest.getMultiMap(instances, name).put("key3", "val33");
        MultiMapListenerTest.getMultiMap(instances, name).put("key4", "val4");
        MultiMapListenerTest.getMultiMap(instances, name).remove("key3", "val33");
        // awaitEventCount() acts as a barrier.
        // without this barrier assertSize(-eventually) could pass just after receiving the very first
        // event when inserting the first entry ("key3", "val3"). Events triggered by the other
        // entries could be re-ordered with sub-sequent map.clear()
        listener.awaitEventCount(4);
        Assert.assertEquals(1, listener.size());
        MultiMapListenerTest.getMultiMap(instances, name).clear();
        // it should fire the mapCleared event and listener will remove everything
        HazelcastTestSupport.assertSizeEventually(0, listener.keys);
        multiMap.removeEntryListener(id2);
        multiMap.addEntryListener(listener, "key7", true);
        MultiMapListenerTest.getMultiMap(instances, name).put("key2", "val2");
        MultiMapListenerTest.getMultiMap(instances, name).put("key3", "val3");
        MultiMapListenerTest.getMultiMap(instances, name).put("key7", "val7");
        HazelcastTestSupport.assertSizeEventually(1, listener.keys);
    }

    private abstract static class MyEntryListener extends EntryAdapter<Object, Object> {
        final CountDownLatch addLatch;

        final CountDownLatch removeLatch;

        final CountDownLatch updateLatch;

        final CountDownLatch evictLatch;

        final CountDownLatch clearLatch;

        MyEntryListener(int latchCount) {
            addLatch = new CountDownLatch(latchCount);
            removeLatch = new CountDownLatch(latchCount);
            updateLatch = new CountDownLatch(1);
            evictLatch = new CountDownLatch(1);
            clearLatch = new CountDownLatch(1);
        }

        MyEntryListener(int addLatchCount, int removeLatchCount) {
            addLatch = new CountDownLatch(addLatchCount);
            removeLatch = new CountDownLatch(removeLatchCount);
            updateLatch = new CountDownLatch(1);
            evictLatch = new CountDownLatch(1);
            clearLatch = new CountDownLatch(1);
        }
    }

    private static class CountDownValueNotNullListener extends MultiMapListenerTest.MyEntryListener {
        CountDownValueNotNullListener(int latchCount) {
            super(latchCount);
        }

        CountDownValueNotNullListener(int addLatchCount, int removeLatchCount) {
            super(addLatchCount, removeLatchCount);
        }

        @Override
        public void entryAdded(EntryEvent event) {
            if ((event.getValue()) != null) {
                addLatch.countDown();
            }
        }

        @Override
        public void entryRemoved(EntryEvent event) {
            if ((event.getOldValue()) != null) {
                removeLatch.countDown();
            }
        }

        @Override
        public void entryUpdated(EntryEvent event) {
            if ((event.getValue()) != null) {
                updateLatch.countDown();
            }
        }

        @Override
        public void entryEvicted(EntryEvent event) {
            if ((event.getValue()) != null) {
                evictLatch.countDown();
            }
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }

        @Override
        public void mapCleared(MapEvent event) {
            clearLatch.countDown();
        }
    }

    private static class CountDownValueNullListener extends MultiMapListenerTest.MyEntryListener {
        CountDownValueNullListener(int latchCount) {
            super(latchCount);
        }

        CountDownValueNullListener(int addLatchCount, int removeLatchCount) {
            super(addLatchCount, removeLatchCount);
        }

        @Override
        public void entryAdded(EntryEvent event) {
            if ((event.getValue()) == null) {
                addLatch.countDown();
            }
        }

        @Override
        public void entryRemoved(EntryEvent event) {
            if ((event.getOldValue()) == null) {
                removeLatch.countDown();
            }
        }

        @Override
        public void entryUpdated(EntryEvent event) {
            if ((event.getValue()) == null) {
                updateLatch.countDown();
            }
        }

        @Override
        public void entryEvicted(EntryEvent event) {
            if ((event.getValue()) == null) {
                evictLatch.countDown();
            }
        }

        @Override
        public void mapEvicted(MapEvent event) {
        }

        @Override
        public void mapCleared(MapEvent event) {
            clearLatch.countDown();
        }
    }

    private static class KeyCollectingListener<V> extends EntryAdapter<String, V> {
        private final Set<String> keys = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

        private final AtomicInteger eventCount = new AtomicInteger();

        public void entryAdded(EntryEvent<String, V> event) {
            keys.add(event.getKey());
            eventCount.incrementAndGet();
        }

        public void entryRemoved(EntryEvent<String, V> event) {
            keys.remove(event.getKey());
            eventCount.incrementAndGet();
        }

        @Override
        public void mapCleared(MapEvent event) {
            keys.clear();
            eventCount.incrementAndGet();
        }

        private int size() {
            return keys.size();
        }

        @SuppressWarnings("SameParameterValue")
        private void awaitEventCount(int expectedEventCount) {
            HazelcastTestSupport.assertEqualsEventually(expectedEventCount, eventCount);
        }
    }
}

