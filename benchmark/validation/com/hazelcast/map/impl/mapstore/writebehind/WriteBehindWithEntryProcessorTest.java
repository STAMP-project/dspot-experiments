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
package com.hazelcast.map.impl.mapstore.writebehind;


import InMemoryFormat.OBJECT;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.MapStoreAdapter;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.map.impl.mapstore.MapStoreTest;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.CollectionUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class WriteBehindWithEntryProcessorTest extends HazelcastTestSupport {
    @Test
    public void testAllPartialUpdatesStored_whenInMemoryFormatIsObject() {
        CountDownLatch pauseStoreOp = new CountDownLatch(1);
        WriteBehindWithEntryProcessorTest.JournalingMapStore<Integer, SampleTestObjects.Employee> mapStore = new WriteBehindWithEntryProcessorTest.JournalingMapStore<Integer, SampleTestObjects.Employee>(pauseStoreOp);
        IMap<Integer, SampleTestObjects.Employee> map = TestMapUsingMapStoreBuilder.<Integer, SampleTestObjects.Employee>create().withMapStore(mapStore).withNodeFactory(createHazelcastInstanceFactory(1)).withWriteDelaySeconds(1).withWriteCoalescing(false).withInMemoryFormat(OBJECT).build();
        Double[] salaries = new Double[]{ 73.0, 111.0, -23.0, 99.0, 12.0, 77.0, 33.0 };
        for (Double salary : salaries) {
            updateSalary(map, 1, salary);
        }
        pauseStoreOp.countDown();
        assertStoreOperationsCompleted(salaries.length, mapStore);
        Assert.assertArrayEquals("Map store should contain all partial updates on the object", salaries, getStoredSalaries(mapStore));
    }

    private static class JournalingMapStore<K, V> extends MapStoreAdapter<K, V> {
        private final Queue<V> queue = new ConcurrentLinkedQueue<V>();

        private final CountDownLatch pauseStoreOp;

        JournalingMapStore(CountDownLatch pauseStoreOp) {
            this.pauseStoreOp = pauseStoreOp;
        }

        @Override
        public void store(K key, V value) {
            pause();
            queue.add(value);
        }

        private void pause() {
            try {
                pauseStoreOp.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public Iterator<V> iterator() {
            return queue.iterator();
        }
    }

    @Test
    public void updates_on_same_key_when_in_memory_format_is_object() {
        long customerId = 0L;
        int numberOfSubscriptions = 1000;
        MapStore<Long, WriteBehindWithEntryProcessorTest.Customer> mapStore = new WriteBehindWithEntryProcessorTest.CustomerDataStore(customerId);
        IMap<Long, WriteBehindWithEntryProcessorTest.Customer> map = createMap(mapStore);
        // 1 store op
        addCustomer(customerId, map);
        // + 1000 store op
        addSubscriptions(map, customerId, numberOfSubscriptions);
        // + 500 store op
        removeSubscriptions(map, customerId, (numberOfSubscriptions / 2));
        assertStoreOperationCount(mapStore, ((1 + numberOfSubscriptions) + (numberOfSubscriptions / 2)));
        assertFinalSubscriptionCountInStore(mapStore, (numberOfSubscriptions / 2));
    }

    @Test
    public void testCoalescingMode_doesNotCauseSerialization_whenInMemoryFormatIsObject() {
        MapStore<Integer, WriteBehindWithEntryProcessorTest.TestObject> mapStore = new MapStoreTest.SimpleMapStore<Integer, WriteBehindWithEntryProcessorTest.TestObject>();
        IMap<Integer, WriteBehindWithEntryProcessorTest.TestObject> map = TestMapUsingMapStoreBuilder.<Integer, WriteBehindWithEntryProcessorTest.TestObject>create().withMapStore(mapStore).withNodeFactory(createHazelcastInstanceFactory(1)).withWriteDelaySeconds(1).withWriteCoalescing(true).withInMemoryFormat(OBJECT).build();
        final WriteBehindWithEntryProcessorTest.TestObject testObject = new WriteBehindWithEntryProcessorTest.TestObject();
        map.executeOnKey(1, new com.hazelcast.map.EntryProcessor<Integer, WriteBehindWithEntryProcessorTest.TestObject>() {
            @Override
            public Object process(Map.Entry<Integer, WriteBehindWithEntryProcessorTest.TestObject> entry) {
                entry.setValue(testObject);
                return null;
            }

            @Override
            public EntryBackupProcessor<Integer, WriteBehindWithEntryProcessorTest.TestObject> getBackupProcessor() {
                return null;
            }
        });
        Assert.assertEquals(0, testObject.serializedCount);
        Assert.assertEquals(0, testObject.deserializedCount);
    }

    private static class TestObject implements DataSerializable {
        int serializedCount = 0;

        int deserializedCount = 0;

        public TestObject() {
        }

        @Override
        public void writeData(ObjectDataOutput out) throws IOException {
            out.writeInt((++(serializedCount)));
            out.writeInt(deserializedCount);
        }

        @Override
        public void readData(ObjectDataInput in) throws IOException {
            serializedCount = in.readInt();
            deserializedCount = (in.readInt()) + 1;
        }
    }

    private static class CustomerDataStore extends MapStoreAdapter<Long, WriteBehindWithEntryProcessorTest.Customer> {
        private AtomicInteger storeCallCount;

        private final Map<Long, List<WriteBehindWithEntryProcessorTest.Subscription>> store;

        private final long customerId;

        private CustomerDataStore(long customerId) {
            this.store = new ConcurrentHashMap<Long, List<WriteBehindWithEntryProcessorTest.Subscription>>();
            this.storeCallCount = new AtomicInteger(0);
            this.customerId = customerId;
        }

        @Override
        public void store(Long key, WriteBehindWithEntryProcessorTest.Customer customer) {
            storeCallCount.incrementAndGet();
            List<WriteBehindWithEntryProcessorTest.Subscription> subscriptions = customer.getSubscriptions();
            if (CollectionUtil.isEmpty(subscriptions)) {
                return;
            }
            store.put(key, subscriptions);
        }

        int subscriptionCount() {
            final List<WriteBehindWithEntryProcessorTest.Subscription> list = store.get(customerId);
            return list == null ? 0 : list.size();
        }

        int getStoreCallCount() {
            return storeCallCount.get();
        }
    }

    private static class Customer implements Serializable {
        private List<WriteBehindWithEntryProcessorTest.Subscription> subscriptions;

        private Customer() {
        }

        void addSubscription(WriteBehindWithEntryProcessorTest.Subscription subscription) {
            if ((subscriptions) == null) {
                subscriptions = new ArrayList<WriteBehindWithEntryProcessorTest.Subscription>();
            }
            subscriptions.add(subscription);
        }

        void removeSubscription(long productId) {
            if (((subscriptions) == null) || (subscriptions.isEmpty())) {
                return;
            }
            final Iterator<WriteBehindWithEntryProcessorTest.Subscription> iterator = subscriptions.iterator();
            while (iterator.hasNext()) {
                final WriteBehindWithEntryProcessorTest.Subscription next = iterator.next();
                if ((next.getProductId()) == productId) {
                    iterator.remove();
                    break;
                }
            } 
        }

        List<WriteBehindWithEntryProcessorTest.Subscription> getSubscriptions() {
            return subscriptions;
        }
    }

    private static class Subscription implements Serializable {
        private long productId;

        private Subscription(long productId) {
            this.productId = productId;
        }

        long getProductId() {
            return productId;
        }

        @Override
        public String toString() {
            return (("Subscription{" + "productId=") + (productId)) + '}';
        }
    }
}

