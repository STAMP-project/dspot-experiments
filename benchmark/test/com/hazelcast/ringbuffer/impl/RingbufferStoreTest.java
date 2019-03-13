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
package com.hazelcast.ringbuffer.impl;


import OverflowPolicy.OVERWRITE;
import com.hazelcast.config.Config;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.RingbufferStoreConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.RingbufferStore;
import com.hazelcast.core.RingbufferStoreFactory;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.ringbuffer.Ringbuffer;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ConcurrencyUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RingbufferStoreTest extends HazelcastTestSupport {
    @Test
    public void testRingbufferStore() throws Exception {
        final int numItems = 2000;
        final RingbufferStoreTest.TestRingbufferStore<Integer> rbStore = new RingbufferStoreTest.TestRingbufferStore<Integer>(2000, 0, 2000);
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setStoreImplementation(rbStore);
        final Config config = RingbufferStoreTest.getConfig("testRingbufferStore", RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        // add items to the ring buffer and the store and shut down
        final HazelcastInstance instance = factory.newHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = instance.getRingbuffer("testRingbufferStore");
        for (int i = 0; i < numItems; i++) {
            ringbuffer.add(i);
        }
        instance.shutdown();
        // now get a new ring buffer and read the items from the store
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer2 = instance2.getRingbuffer("testRingbufferStore");
        // the actual ring buffer is empty but we can still load items from it
        Assert.assertEquals(0, ringbuffer2.size());
        Assert.assertEquals(RingbufferConfig.DEFAULT_CAPACITY, ringbuffer2.remainingCapacity());
        Assert.assertEquals(numItems, rbStore.store.size());
        for (int i = 0; i < numItems; i++) {
            Assert.assertEquals(i, ringbuffer2.readOne(i));
        }
        rbStore.assertAwait(3);
    }

    @Test
    public void testRingbufferStoreAllAndReadFromMemory() throws Exception {
        final int numItems = 200;
        final RingbufferStoreTest.WriteOnlyRingbufferStore<Integer> rbStore = new RingbufferStoreTest.WriteOnlyRingbufferStore<Integer>();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setStoreImplementation(rbStore);
        final Config config = RingbufferStoreTest.getConfig("testRingbufferStore", RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance = factory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(instance, instance2);
        // add items to both ring buffers (master and backup) and shut down the master
        final Ringbuffer<Object> ringbuffer = instance.getRingbuffer("testRingbufferStore");
        final ArrayList<Integer> items = new ArrayList<Integer>();
        for (int i = 0; i < numItems; i++) {
            items.add(i);
        }
        ringbuffer.addAllAsync(items, OVERWRITE).get();
        TestUtil.terminateInstance(instance);
        // now read items from the backup
        final Ringbuffer<Object> ringbuffer2 = instance2.getRingbuffer("testRingbufferStore");
        Assert.assertEquals(numItems, ringbuffer2.size());
        Assert.assertEquals(numItems, rbStore.store.size());
        // assert that the backup has all items in memory, without loading from the store
        for (int i = 0; i < numItems; i++) {
            Assert.assertEquals(i, ringbuffer2.readOne(i));
        }
    }

    @Test
    public void testRingbufferStoreMoreThanCapacity() throws Exception {
        final int capacity = 1000;
        final RingbufferStoreTest.TestRingbufferStore<Integer> rbStore = new RingbufferStoreTest.TestRingbufferStore<Integer>((capacity * 2), 0, 0);
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setStoreImplementation(rbStore);
        final Config config = RingbufferStoreTest.getConfig("testRingbufferStore", capacity, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance instance = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = instance.getRingbuffer("testRingbufferStore");
        for (int i = 0; i < (capacity * 2); i++) {
            ringbuffer.add(i);
        }
        Assert.assertEquals(capacity, ringbuffer.size());
        Assert.assertEquals((capacity * 2), rbStore.store.size());
        for (int i = 0; i < (capacity * 2); i++) {
            Assert.assertEquals(i, ringbuffer.readOne(i));
        }
        rbStore.assertAwait(3);
    }

    @Test
    public void testStoreId_whenNodeDown() throws InterruptedException {
        final RingbufferStoreTest.IdCheckerRingbufferStore rbStore = new RingbufferStoreTest.IdCheckerRingbufferStore();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setStoreImplementation(rbStore);
        final Config config = RingbufferStoreTest.getConfig("default", RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        final String name = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        final Ringbuffer<Object> ringbuffer = instance2.getRingbuffer(name);
        final HashMap<Long, String> addedItems = new HashMap<Long, String>();
        for (int i = 0; i < 3; i++) {
            final String item = HazelcastTestSupport.randomString();
            addedItems.put(ringbuffer.add(item), item);
        }
        instance1.shutdown();
        final String item = HazelcastTestSupport.randomString();
        addedItems.put(ringbuffer.add(item), item);
        for (Map.Entry<Long, String> e : addedItems.entrySet()) {
            Assert.assertEquals("The ring buffer returned a different object than the one which was stored", e.getValue(), ringbuffer.readOne(e.getKey()));
        }
    }

    @Test
    public void testStoreId_writeToMasterAndReadFromBackup() throws InterruptedException {
        final RingbufferStoreTest.IdCheckerRingbufferStore rbStore = new RingbufferStoreTest.IdCheckerRingbufferStore();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setStoreImplementation(rbStore);
        final Config config = RingbufferStoreTest.getConfig("default", RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        final HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        final HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        HazelcastTestSupport.warmUpPartitions(instance1, instance2);
        final String name = HazelcastTestSupport.generateKeyOwnedBy(instance1);
        final Ringbuffer<Object> masterRB = instance1.getRingbuffer(name);
        final HashMap<Long, Integer> addedItems = new HashMap<Long, Integer>();
        for (int i = 0; i < 100; i++) {
            addedItems.put(masterRB.add(i), i);
        }
        TestUtil.terminateInstance(instance1);
        final Ringbuffer<Object> backupRB = instance2.getRingbuffer(name);
        for (Map.Entry<Long, Integer> e : addedItems.entrySet()) {
            Assert.assertEquals("The ring buffer returned a different object than the one which was stored", e.getValue(), backupRB.readOne(e.getKey()));
        }
    }

    @Test
    public void testRingbufferStoreFactory() {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreTest.SimpleRingbufferStoreFactory rbStoreFactory = new RingbufferStoreTest.SimpleRingbufferStoreFactory();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(true).setFactoryImplementation(rbStoreFactory);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance instance = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = instance.getRingbuffer(ringbufferName);
        ringbuffer.add(1);
        Assert.assertEquals(1, rbStoreFactory.stores.size());
        final RingbufferStoreTest.TestRingbufferStore ringbufferStore = ((RingbufferStoreTest.TestRingbufferStore) (rbStoreFactory.stores.get(ringbufferName)));
        int size = ringbufferStore.store.size();
        Assert.assertEquals(("Ring buffer store size should be 1 but found " + size), 1, size);
    }

    @Test
    public void testRingbufferStoreFactoryIsNotInitialized_whenDisabledInRingbufferStoreConfig() {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreTest.SimpleRingbufferStoreFactory rbStoreFactory = new RingbufferStoreTest.SimpleRingbufferStoreFactory();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setEnabled(false).setFactoryImplementation(rbStoreFactory);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance instance = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = instance.getRingbuffer(ringbufferName);
        ringbuffer.add(1);
        Assert.assertEquals(("Expected that the RingbufferStore would not be initialized since we disabled it" + " in the RingbufferStoreConfig, but found initialized"), 0, rbStoreFactory.stores.size());
    }

    @Test
    public void testRingbufferStore_withBinaryModeOn() throws InterruptedException {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setStoreImplementation(new RingbufferStoreTest.TestRingbufferStore<com.hazelcast.nio.serialization.Data>()).setEnabled(true);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.BINARY, rbStoreConfig);
        final HazelcastInstance node = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = node.getRingbuffer(ringbufferName);
        ringbuffer.add(1);
        ringbuffer.add(2);
        final long lastSequence = ringbuffer.add(3);
        Assert.assertEquals(3, ringbuffer.readOne(lastSequence));
    }

    @Test(expected = HazelcastException.class)
    public void testRingbufferStore_addThrowsException() {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setStoreImplementation(new RingbufferStoreTest.ExceptionThrowingRingbufferStore()).setEnabled(true);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance node = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = node.getRingbuffer(ringbufferName);
        ringbuffer.add(1);
    }

    @Test(expected = ExecutionException.class)
    public void testRingbufferStore_addAllThrowsException() throws Exception {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setStoreImplementation(new RingbufferStoreTest.ExceptionThrowingRingbufferStore()).setEnabled(true);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance node = createHazelcastInstance(config);
        final Ringbuffer<Object> ringbuffer = node.getRingbuffer(ringbufferName);
        ringbuffer.addAllAsync(Arrays.asList(1, 2), OVERWRITE).get();
    }

    @Test(expected = HazelcastException.class)
    public void testRingbufferStore_getLargestSequenceThrowsException() {
        final String ringbufferName = HazelcastTestSupport.randomString();
        final RingbufferStoreConfig rbStoreConfig = new RingbufferStoreConfig().setStoreImplementation(new RingbufferStoreTest.ExceptionThrowingRingbufferStore(true)).setEnabled(true);
        final Config config = RingbufferStoreTest.getConfig(ringbufferName, RingbufferConfig.DEFAULT_CAPACITY, InMemoryFormat.OBJECT, rbStoreConfig);
        final HazelcastInstance node = createHazelcastInstance(config);
        node.getRingbuffer(ringbufferName).size();
    }

    static class SimpleRingbufferStoreFactory implements RingbufferStoreFactory<Integer> {
        private final ConcurrentMap<String, RingbufferStore> stores = new ConcurrentHashMap<String, RingbufferStore>();

        @Override
        @SuppressWarnings("unchecked")
        public RingbufferStore<Integer> newRingbufferStore(String name, Properties properties) {
            return ConcurrencyUtil.getOrPutIfAbsent(stores, name, new com.hazelcast.util.ConstructorFunction<String, RingbufferStore>() {
                @Override
                public RingbufferStore<Integer> createNew(String arg) {
                    return new RingbufferStoreTest.TestRingbufferStore<Integer>();
                }
            });
        }
    }

    static class IdCheckerRingbufferStore<T> implements RingbufferStore<T> {
        long lastKey = -1;

        final Map<Long, T> store = new LinkedHashMap<Long, T>();

        @Override
        public void store(final long sequence, final T value) {
            if ((lastKey) >= sequence) {
                throw new RuntimeException((("key[" + sequence) + "] is already stored"));
            }
            lastKey = sequence;
            store.put(sequence, value);
        }

        @Override
        public void storeAll(long firstItemSequence, T[] items) {
            throw new UnsupportedOperationException();
        }

        @Override
        public T load(final long sequence) {
            return store.get(sequence);
        }

        @Override
        public long getLargestSequence() {
            return lastKey;
        }
    }

    static class ExceptionThrowingRingbufferStore<T> implements RingbufferStore<T> {
        private final boolean getLargestSequenceThrowsException;

        public ExceptionThrowingRingbufferStore() {
            this(false);
        }

        ExceptionThrowingRingbufferStore(boolean getLargestSequenceThrowsException) {
            this.getLargestSequenceThrowsException = getLargestSequenceThrowsException;
        }

        @Override
        public void store(long sequence, T data) {
            throw new RuntimeException();
        }

        @Override
        public void storeAll(long firstItemSequence, T[] items) {
            throw new RuntimeException();
        }

        @Override
        public T load(long sequence) {
            throw new RuntimeException();
        }

        @Override
        public long getLargestSequence() {
            if (getLargestSequenceThrowsException) {
                throw new RuntimeException();
            }
            return -1;
        }
    }

    static class TestRingbufferStore<T> implements RingbufferStore<T> {
        final Map<Long, T> store = new LinkedHashMap<Long, T>();

        final AtomicInteger callCount = new AtomicInteger();

        final AtomicInteger destroyCount = new AtomicInteger();

        final CountDownLatch latchStore;

        final CountDownLatch latchStoreAll;

        final CountDownLatch latchLoad;

        public TestRingbufferStore() {
            this(0, 0, 0);
        }

        TestRingbufferStore(int expectedStore, int expectedStoreAll, int expectedLoad) {
            latchStore = new CountDownLatch(expectedStore);
            latchStoreAll = new CountDownLatch(expectedStoreAll);
            latchLoad = new CountDownLatch(expectedLoad);
        }

        public void destroy() {
            destroyCount.incrementAndGet();
        }

        void assertAwait(int seconds) throws Exception {
            Assert.assertTrue(("Store remaining: " + (latchStore.getCount())), latchStore.await(seconds, TimeUnit.SECONDS));
            Assert.assertTrue(("Store-all remaining: " + (latchStoreAll.getCount())), latchStoreAll.await(seconds, TimeUnit.SECONDS));
            Assert.assertTrue(("Load remaining: " + (latchLoad.getCount())), latchLoad.await(seconds, TimeUnit.SECONDS));
        }

        Map<Long, T> getStore() {
            return store;
        }

        @Override
        public void store(long sequence, T data) {
            store.put(sequence, data);
            callCount.incrementAndGet();
            latchStore.countDown();
        }

        @Override
        public void storeAll(long firstItemSequence, T[] items) {
            for (int i = 0; i < (items.length); i++) {
                store.put((firstItemSequence + i), items[i]);
            }
            callCount.incrementAndGet();
            latchStoreAll.countDown();
        }

        @Override
        public T load(long sequence) {
            callCount.incrementAndGet();
            latchLoad.countDown();
            return store.get(sequence);
        }

        @Override
        public long getLargestSequence() {
            final Set<Long> coll = store.keySet();
            return coll.isEmpty() ? -1 : Collections.max(coll);
        }
    }

    static class WriteOnlyRingbufferStore<T> implements RingbufferStore<T> {
        final Map<Long, T> store = new LinkedHashMap<Long, T>();

        public WriteOnlyRingbufferStore() {
        }

        Map<Long, T> getStore() {
            return store;
        }

        @Override
        public void store(long sequence, T data) {
            store.put(sequence, data);
        }

        @Override
        public void storeAll(long firstItemSequence, T[] items) {
            for (int i = 0; i < (items.length); i++) {
                store.put((firstItemSequence + i), items[i]);
            }
        }

        @Override
        public T load(long sequence) {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLargestSequence() {
            final Set<Long> coll = store.keySet();
            return coll.isEmpty() ? -1 : Collections.max(coll);
        }
    }
}

