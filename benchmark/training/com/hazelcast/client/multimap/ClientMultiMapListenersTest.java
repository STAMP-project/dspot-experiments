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
package com.hazelcast.client.multimap;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.MultiMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMultiMapListenersTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server;

    private HazelcastInstance client;

    @Test(expected = UnsupportedOperationException.class)
    public void testAddLocalEntryListener_whenNull() {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        mm.addLocalEntryListener(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddLocalEntryListener() {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener myEntryListener = new ClientMultiMapListenersTest.CountDownValueNotNullListener(1);
        mm.addLocalEntryListener(myEntryListener);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddListener_whenListenerNull() throws InterruptedException {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        mm.addEntryListener(null, true);
    }

    @Test
    public void testRemoveListener() throws InterruptedException {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener(1);
        final String id = mm.addEntryListener(listener, true);
        Assert.assertTrue(mm.removeEntryListener(id));
    }

    @Test
    public void testRemoveListener_whenNotExist() throws InterruptedException {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        Assert.assertFalse(mm.removeEntryListener("NOT_THERE"));
    }

    @Test
    public void testListenerEntryAddEvent() throws InterruptedException {
        final int maxKeys = 12;
        final int maxItems = 3;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener((maxKeys * maxItems));
        mm.addEntryListener(listener, true);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                mm.put(i, j);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerEntryAddEvent_whenValueNotIncluded() throws InterruptedException {
        final int maxKeys = 21;
        final int maxItems = 3;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener((maxKeys * maxItems));
        mm.addEntryListener(listener, false);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                mm.put(i, j);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerEntryRemoveEvent() throws InterruptedException {
        final int maxKeys = 25;
        final int maxItems = 3;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener((maxKeys * maxItems));
        mm.addEntryListener(listener, true);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                mm.put(i, j);
                mm.remove(i);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerEntryRemoveEvent_whenValueNotIncluded() throws InterruptedException {
        final int maxKeys = 31;
        final int maxItems = 3;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener((maxKeys * maxItems));
        mm.addEntryListener(listener, false);
        for (int i = 0; i < maxKeys; i++) {
            for (int j = 0; j < maxKeys; j++) {
                mm.put(i, j);
                mm.remove(i);
            }
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryAddEvent() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 42;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener(maxItems);
        mm.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerOnKey_whenOtherKeysAdded() throws InterruptedException {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        final List<EntryEvent> events = new ArrayList<EntryEvent>();
        mm.addEntryListener(new EntryAdapter() {
            @Override
            public void entryAdded(EntryEvent event) {
                events.add(event);
            }
        }, "key", true);
        mm.put("key2", "value");
        mm.put("key", "value");
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(1, events.size());
                Assert.assertEquals("key", events.get(0).getKey());
            }
        });
    }

    @Test
    public void testListenerOnKeyEntryAddEvent_whenValueNotIncluded() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 72;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener(maxItems);
        mm.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemoveEvent() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 88;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener(maxItems);
        mm.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
            mm.remove(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemoveEvent_whenValueNotIncluded() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 62;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener(maxItems);
        mm.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
            mm.remove(key, i);
        }
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemove_WithOneRemove() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 98;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNotNullListener(maxItems, 1);
        final String id = mm.addEntryListener(listener, key, true);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
        }
        mm.remove(key);
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListenerOnKeyEntryRemove_WithOneRemoveWhenValueNotIncluded() throws InterruptedException {
        final Object key = "key";
        final int maxItems = 56;
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener(maxItems, 1);
        final String id = mm.addEntryListener(listener, key, false);
        for (int i = 0; i < maxItems; i++) {
            mm.put(key, i);
        }
        mm.remove(key);
        HazelcastTestSupport.assertOpenEventually(listener.removeLatch);
    }

    @Test
    public void testListeners_clearAll() {
        final MultiMap mm = client.getMultiMap(HazelcastTestSupport.randomString());
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener(1);
        mm.addEntryListener(listener, false);
        mm.put("key", "value");
        mm.clear();
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
        HazelcastTestSupport.assertOpenEventually(listener.clearLatch);
    }

    @Test
    public void testListeners_clearAllFromNode() {
        final String name = HazelcastTestSupport.randomString();
        final MultiMap mm = client.getMultiMap(name);
        ClientMultiMapListenersTest.MyEntryListener listener = new ClientMultiMapListenersTest.CountDownValueNullListener(1);
        mm.addEntryListener(listener, false);
        mm.put("key", "value");
        server.getMultiMap(name).clear();
        HazelcastTestSupport.assertOpenEventually(listener.addLatch);
        HazelcastTestSupport.assertOpenEventually(listener.clearLatch);
    }

    abstract static class MyEntryListener extends EntryAdapter {
        public final CountDownLatch addLatch;

        public final CountDownLatch removeLatch;

        public final CountDownLatch updateLatch;

        public final CountDownLatch evictLatch;

        public final CountDownLatch clearLatch;

        public MyEntryListener(int latchCount) {
            addLatch = new CountDownLatch(latchCount);
            removeLatch = new CountDownLatch(latchCount);
            updateLatch = new CountDownLatch(1);
            evictLatch = new CountDownLatch(1);
            clearLatch = new CountDownLatch(1);
        }

        public MyEntryListener(int addlatchCount, int removeLatchCount) {
            addLatch = new CountDownLatch(addlatchCount);
            removeLatch = new CountDownLatch(removeLatchCount);
            updateLatch = new CountDownLatch(1);
            evictLatch = new CountDownLatch(1);
            clearLatch = new CountDownLatch(1);
        }
    }

    static class CountDownValueNotNullListener extends ClientMultiMapListenersTest.MyEntryListener {
        public CountDownValueNotNullListener(int latchCount) {
            super(latchCount);
        }

        public CountDownValueNotNullListener(int addlatchCount, int removeLatchCount) {
            super(addlatchCount, removeLatchCount);
        }

        public void entryAdded(EntryEvent event) {
            if ((event.getValue()) != null) {
                addLatch.countDown();
            }
        }

        public void entryRemoved(EntryEvent event) {
            if ((event.getOldValue()) != null) {
                removeLatch.countDown();
            }
        }

        public void entryUpdated(EntryEvent event) {
            if ((event.getValue()) != null) {
                updateLatch.countDown();
            }
        }

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

    static class CountDownValueNullListener extends ClientMultiMapListenersTest.MyEntryListener {
        public CountDownValueNullListener(int latchCount) {
            super(latchCount);
        }

        public CountDownValueNullListener(int addlatchCount, int removeLatchCount) {
            super(addlatchCount, removeLatchCount);
        }

        public void entryAdded(EntryEvent event) {
            if ((event.getValue()) == null) {
                addLatch.countDown();
            }
        }

        public void entryRemoved(EntryEvent event) {
            if ((event.getOldValue()) == null) {
                removeLatch.countDown();
            }
        }

        public void entryUpdated(EntryEvent event) {
            if ((event.getValue()) == null) {
                updateLatch.countDown();
            }
        }

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
}

