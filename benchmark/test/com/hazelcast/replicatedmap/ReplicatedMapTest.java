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
package com.hazelcast.replicatedmap;


import InMemoryFormat.BINARY;
import InMemoryFormat.OBJECT;
import com.hazelcast.config.Config;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ReplicatedMap;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicatedMapTest extends ReplicatedMapAbstractTest {
    @Test
    public void testEmptyMapIsEmpty() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = instance.getReplicatedMap(HazelcastTestSupport.randomName());
        Assert.assertTrue("map should be empty", map.isEmpty());
    }

    @Test
    public void testNonEmptyMapIsNotEmpty() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = instance.getReplicatedMap(HazelcastTestSupport.randomName());
        map.put(1, 1);
        Assert.assertFalse("map should not be empty", map.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTtlThrowsException() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = instance.getReplicatedMap(HazelcastTestSupport.randomName());
        map.put(1, 1, (-1), TimeUnit.DAYS);
    }

    @Test
    public void testAddObject() {
        testAdd(buildConfig(OBJECT));
    }

    @Test
    public void testAddObjectSyncFillUp() {
        Config config = buildConfig(OBJECT);
        config.getReplicatedMapConfig("default").setAsyncFillup(false);
        testFillUp(config);
    }

    @Test
    public void testAddObjectAsyncFillUp() {
        Config config = buildConfig(OBJECT);
        config.getReplicatedMapConfig("default").setAsyncFillup(true);
        testFillUp(config);
    }

    @Test
    public void testAddBinary() {
        testAdd(buildConfig(BINARY));
    }

    @Test
    public void testAddBinarySyncFillUp() {
        Config config = buildConfig(BINARY);
        config.getReplicatedMapConfig("default").setAsyncFillup(false);
        testFillUp(config);
    }

    @Test
    public void testAddBinaryAsyncFillUp() {
        Config config = buildConfig(BINARY);
        config.getReplicatedMapConfig("default").setAsyncFillup(true);
        testFillUp(config);
    }

    @Test
    public void testPutAllObject() {
        testPutAll(buildConfig(OBJECT));
    }

    @Test
    public void testPutAllBinary() {
        testPutAll(buildConfig(BINARY));
    }

    @Test
    public void testClearObject() {
        testClear(buildConfig(OBJECT));
    }

    @Test
    public void testClearBinary() {
        testClear(buildConfig(BINARY));
    }

    @Test
    public void testAddTtlObject() {
        testAddTtl(buildConfig(OBJECT));
    }

    @Test
    public void testAddTtlBinary() {
        testAddTtl(buildConfig(BINARY));
    }

    @Test
    public void testUpdateObject() {
        testUpdate(buildConfig(OBJECT));
    }

    @Test
    public void testUpdateBinary() {
        testUpdate(buildConfig(BINARY));
    }

    @Test
    public void testUpdateTtlObject() {
        testUpdateTtl(buildConfig(OBJECT));
    }

    @Test
    public void testUpdateTtlBinary() {
        testUpdateTtl(buildConfig(BINARY));
    }

    @Test
    public void testRemoveObject() {
        testRemove(buildConfig(OBJECT));
    }

    @Test
    public void testRemoveBinary() {
        testRemove(buildConfig(BINARY));
    }

    @Test
    public void testContainsKey_returnsFalse_onRemovedKeys() {
        HazelcastInstance node = createHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        map.put(1, Integer.MAX_VALUE);
        map.remove(1);
        Assert.assertFalse(map.containsKey(1));
    }

    @Test
    public void testContainsKey_returnsFalse_onNonexistentKeys() {
        HazelcastInstance node = createHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        Assert.assertFalse(map.containsKey(1));
    }

    @Test
    public void testContainsKey_returnsTrue_onExistingKeys() {
        HazelcastInstance node = createHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        map.put(1, Integer.MAX_VALUE);
        Assert.assertTrue(map.containsKey(1));
    }

    @Test
    public void testKeySet_notIncludes_removedKeys() {
        HazelcastInstance node = createHazelcastInstance();
        final ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        map.put(1, Integer.MAX_VALUE);
        map.put(2, Integer.MIN_VALUE);
        map.remove(1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Set<Integer> keys = new HashSet<Integer>(map.keySet());
                Assert.assertFalse(keys.contains(1));
            }
        }, 20);
    }

    @Test
    public void testEntrySet_notIncludes_removedKeys() {
        HazelcastInstance node = createHazelcastInstance();
        final ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        map.put(1, Integer.MAX_VALUE);
        map.put(2, Integer.MIN_VALUE);
        map.remove(1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Set<Map.Entry<Integer, Integer>> entries = map.entrySet();
                for (Map.Entry<Integer, Integer> entry : entries) {
                    if (entry.getKey().equals(1)) {
                        Assert.fail(String.format("We do not expect an entry which's key equals to %d in entry set", 1));
                    }
                }
            }
        }, 20);
    }

    @Test
    public void testSizeObject() {
        testSize(buildConfig(OBJECT));
    }

    @Test
    public void testSizeBinary() {
        testSize(buildConfig(BINARY));
    }

    @Test
    public void testContainsKeyObject() {
        testContainsKey(buildConfig(OBJECT));
    }

    @Test
    public void testContainsKeyBinary() {
        testContainsKey(buildConfig(BINARY));
    }

    @Test
    public void testContainsValue_returnsFalse_onNonexistentValue() {
        HazelcastInstance node = createHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        Assert.assertFalse(map.containsValue(1));
    }

    @Test
    public void testContainsValueObject() {
        testContainsValue(buildConfig(OBJECT));
    }

    @Test
    public void testContainsValueBinary() {
        testContainsValue(buildConfig(BINARY));
    }

    @Test
    public void testValuesWithComparator() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = instance.getReplicatedMap(HazelcastTestSupport.randomName());
        for (int i = 0; i < 100; i++) {
            map.put(i, i);
        }
        Collection<Integer> values = map.values(new ReplicatedMapTest.DescendingComparator());
        int v = 100;
        for (Integer value : values) {
            Assert.assertEquals((--v), ((int) (value)));
        }
    }

    @Test
    public void testValuesObject() {
        testValues(buildConfig(OBJECT));
    }

    @Test
    public void testValuesBinary() {
        testValues(buildConfig(BINARY));
    }

    @Test
    public void testKeySetObject() {
        testKeySet(buildConfig(OBJECT));
    }

    @Test
    public void testKeySetBinary() {
        testKeySet(buildConfig(BINARY));
    }

    @Test
    public void testEntrySetObject() {
        testEntrySet(buildConfig(OBJECT));
    }

    @Test
    public void testEntrySetBinary() {
        testEntrySet(buildConfig(BINARY));
    }

    @Test
    public void testAddListenerObject() {
        testAddEntryListener(buildConfig(OBJECT));
    }

    @Test
    public void testAddListenerBinary() {
        testAddEntryListener(buildConfig(BINARY));
    }

    @Test
    public void testEvictionObject() {
        testEviction(buildConfig(OBJECT));
    }

    @Test
    public void testEvictionBinary() {
        testEviction(buildConfig(BINARY));
    }

    private class SimpleEntryListener extends EntryAdapter<String, String> {
        CountDownLatch addLatch;

        CountDownLatch evictLatch;

        SimpleEntryListener(int addCount, int evictCount) {
            addLatch = new CountDownLatch(addCount);
            evictLatch = new CountDownLatch(evictCount);
        }

        @Override
        public void entryAdded(EntryEvent event) {
            addLatch.countDown();
        }

        @Override
        public void entryEvicted(EntryEvent event) {
            evictLatch.countDown();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void putNullKey() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Object, Object> map1 = instance1.getReplicatedMap("default");
        map1.put(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullKey() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Object, Object> map1 = instance1.getReplicatedMap("default");
        map1.remove(null);
    }

    @Test
    public void removeEmptyListener() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Object, Object> map1 = instance1.getReplicatedMap("default");
        Assert.assertFalse(map1.removeEntryListener("2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void removeNullListener() {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        ReplicatedMap<Object, Object> map1 = instance1.getReplicatedMap("default");
        map1.removeEntryListener(null);
    }

    @Test
    public void testSizeAfterRemove() {
        HazelcastInstance node = createHazelcastInstance();
        ReplicatedMap<Integer, Integer> map = node.getReplicatedMap("default");
        map.put(1, Integer.MAX_VALUE);
        map.remove(1);
        Assert.assertTrue(((map.size()) == 0));
    }

    @Test
    public void testDestroy() {
        HazelcastInstance instance = createHazelcastInstance();
        ReplicatedMap<Object, Object> replicatedMap = instance.getReplicatedMap(HazelcastTestSupport.randomName());
        replicatedMap.put(1, 1);
        replicatedMap.destroy();
        Collection<DistributedObject> objects = instance.getDistributedObjects();
        Assert.assertEquals(0, objects.size());
    }

    class DescendingComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.equals(o2) ? 0 : o1 > o2 ? -1 : 1;
        }
    }
}

