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


import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class WriteBehindMapStoreWithEvictionsTest extends HazelcastTestSupport {
    @Test
    public void testWriteBehind_callEvictBeforePersisting() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        final int numberOfItems = 1000;
        populateMap(map, numberOfItems);
        evictMap(map, numberOfItems);
        assertFinalValueEqualsForEachEntry(map, numberOfItems);
    }

    @Test
    public void testWriteBehind_callEvictBeforePersisting_onSameKey() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(3).withPartitionCount(1).build();
        final int numberOfUpdates = 1000;
        final int key = 0;
        continuouslyUpdateKey(map, numberOfUpdates, key);
        map.evict(0);
        final int expectedLastValue = numberOfUpdates - 1;
        assertFinalValueEquals(expectedLastValue, map.get(0));
    }

    @Test
    public void testWriteBehind_callEvictBeforePersisting_onSameKey_thenCallRemove() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        final int numberOfUpdates = 1000;
        final int key = 0;
        continuouslyUpdateKey(map, numberOfUpdates, key);
        map.evict(0);
        final Object previousValue = map.remove(0);
        final int expectedLastValue = numberOfUpdates - 1;
        assertFinalValueEquals(expectedLastValue, ((Integer) (previousValue)));
    }

    @Test
    public void testWriteBehind_callEvictBeforePersisting_onSameKey_thenCallRemoveMultipleTimes() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        final int numberOfUpdates = 1000;
        final int key = 0;
        continuouslyUpdateKey(map, numberOfUpdates, key);
        map.evict(0);
        map.remove(0);
        final Object previousValue = map.remove(0);
        Assert.assertNull(null, previousValue);
    }

    @Test
    public void evict_then_loadAll_onSameKey() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        map.put(1, 100);
        final Map<Integer, Integer> fill = new HashMap<Integer, Integer>();
        fill.put(1, (-1));
        mapStore.storeAll(fill);
        map.evict(1);
        final Set<Integer> loadKeys = new HashSet<Integer>();
        loadKeys.add(1);
        map.loadAll(loadKeys, true);
        Assert.assertEquals(100, map.get(1).intValue());
    }

    @Test
    public void evictAll_then_loadAll_onSameKey() throws Exception {
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(1).build();
        map.put(1, 100);
        final Map<Integer, Integer> fill = new HashMap<Integer, Integer>();
        fill.put(1, (-1));
        mapStore.storeAll(fill);
        map.evictAll();
        final Set<Integer> loadKeys = new HashSet<Integer>();
        loadKeys.add(1);
        map.loadAll(loadKeys, true);
        Assert.assertEquals(100, map.get(1).intValue());
    }

    @Test
    public void testWriteBehindFlushPersistsAllRecords_afterShutdownAll() throws Exception {
        int nodeCount = 2;
        final MapStoreWithCounter<Integer, Integer> mapStore = new MapStoreWithCounter<Integer, Integer>();
        final TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(nodeCount);
        final IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(mapStore).withNodeCount(nodeCount).withNodeFactory(factory).withBackupCount(0).withWriteDelaySeconds(100).withPartitionCount(100).build();
        final int numberOfItems = 1000;
        // add some expiration logic by setting a 10 seconds TTL to puts
        populateMap(map, numberOfItems, 10);
        factory.shutdownAll();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (int i = 0; i < 1000; i++) {
                    Assert.assertEquals(Integer.valueOf(i), mapStore.store.get(i));
                }
            }
        });
    }

    @Test
    public void testWriteBehind_shouldNotMakeDuplicateStoreOperationForAKey_uponEviction() throws Exception {
        final AtomicInteger storeCount = new AtomicInteger(0);
        MapStore<Integer, Integer> store = createSlowMapStore(storeCount);
        IMap<Integer, Integer> map = TestMapUsingMapStoreBuilder.<Integer, Integer>create().withMapStore(store).withNodeCount(1).withNodeFactory(createHazelcastInstanceFactory(1)).withBackupCount(0).withWriteDelaySeconds(1).build();
        map.put(1, 1);
        map.evict(1);
        // give some time to process write-behind
        HazelcastTestSupport.sleepSeconds(2);
        assertStoreCount(1, storeCount);
    }

    @Test
    public void testTransientlyPutKeysAreNotReachable_afterEviction() throws Exception {
        int numberOfItems = 1000;
        IMap<Integer, Integer> map = createMapBackedByWriteBehindStore();
        // 1. these puts are used to create write-behind-queues on partitions
        for (int i = -1; i > (-numberOfItems); i--) {
            map.put(i, i);
        }
        // 2. put transient entries
        for (int i = 0; i < numberOfItems; i++) {
            map.putTransient(i, i, 10, TimeUnit.SECONDS);
        }
        // 3. evict all transient entries
        for (int i = 0; i < numberOfItems; i++) {
            map.evict(i);
        }
        // 4. expecting all transiently put entries are not reachable
        assertEntriesRemoved(map, numberOfItems);
    }
}

