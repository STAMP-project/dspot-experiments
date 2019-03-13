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
import MultiMapConfig.ValueCollectionType.SET;
import com.hazelcast.config.Config;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.core.HazelcastException;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MultiMap;
import com.hazelcast.mapreduce.aggregation.Aggregations;
import com.hazelcast.mapreduce.aggregation.Supplier;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.Clock;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MultiMapTest extends HazelcastTestSupport {
    @Test
    public void testMultiMapPutAndGet() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapPutAndGet");
        multiMap.put("Hello", "World");
        Collection<String> values = multiMap.get("Hello");
        Assert.assertEquals("World", values.iterator().next());
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        values = multiMap.get("Hello");
        Assert.assertEquals(7, values.size());
        Assert.assertFalse(multiMap.remove("Hello", "Unknown"));
        Assert.assertEquals(7, multiMap.get("Hello").size());
        Assert.assertTrue(multiMap.remove("Hello", "Antarctica"));
        Assert.assertEquals(6, multiMap.get("Hello").size());
    }

    @Test
    public void testMultiMapPutGetRemove() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapPutGetRemove");
        multiMap.put("1", "C");
        multiMap.put("2", "x");
        multiMap.put("2", "y");
        multiMap.put("1", "A");
        multiMap.put("1", "B");
        Collection g1 = multiMap.get("1");
        HazelcastTestSupport.assertContains(g1, "A");
        HazelcastTestSupport.assertContains(g1, "B");
        HazelcastTestSupport.assertContains(g1, "C");
        Assert.assertEquals(5, multiMap.size());
        Assert.assertTrue(multiMap.remove("1", "C"));
        Assert.assertEquals(4, multiMap.size());
        Collection g2 = multiMap.get("1");
        HazelcastTestSupport.assertContains(g2, "A");
        HazelcastTestSupport.assertContains(g2, "B");
        Assert.assertFalse(g2.contains("C"));
        Collection r1 = multiMap.remove("2");
        HazelcastTestSupport.assertContains(r1, "x");
        HazelcastTestSupport.assertContains(r1, "y");
        Assert.assertNotNull(multiMap.get("2"));
        Assert.assertTrue(multiMap.get("2").isEmpty());
        Assert.assertEquals(2, multiMap.size());
        Collection r2 = multiMap.remove("1");
        HazelcastTestSupport.assertContains(r2, "A");
        HazelcastTestSupport.assertContains(r2, "B");
        Assert.assertNotNull(multiMap.get("1"));
        Assert.assertTrue(multiMap.get("1").isEmpty());
        Assert.assertEquals(0, multiMap.size());
    }

    @Test
    public void testMultiMapClear() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapClear");
        multiMap.put("Hello", "World");
        Assert.assertEquals(1, multiMap.size());
        multiMap.clear();
        Assert.assertEquals(0, multiMap.size());
    }

    @Test
    public void testMultiMapContainsKey() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapContainsKey");
        multiMap.put("Hello", "World");
        Assert.assertTrue(multiMap.containsKey("Hello"));
    }

    @Test
    public void testMultiMapContainsValue() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapContainsValue");
        multiMap.put("Hello", "World");
        Assert.assertTrue(multiMap.containsValue("World"));
    }

    @Test
    public void testMultiMapContainsEntry() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapContainsEntry");
        multiMap.put("Hello", "World");
        Assert.assertTrue(multiMap.containsEntry("Hello", "World"));
    }

    @Test
    public void testMultiMapDelete() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> map = instance.getMultiMap("testMultiMapContainsEntry");
        map.put("Hello", "World");
        map.delete("Hello");
        Assert.assertFalse(map.containsEntry("Hello", "World"));
    }

    /**
     * Issue 818
     */
    @Test
    public void testMultiMapWithCustomSerializable() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, MultiMapTest.CustomSerializable> multiMap = instance.getMultiMap("testMultiMapWithCustomSerializable");
        multiMap.put("1", new MultiMapTest.CustomSerializable());
        Assert.assertEquals(1, multiMap.size());
        multiMap.remove("1");
        Assert.assertEquals(0, multiMap.size());
    }

    @Test
    public void testContains() {
        MultiMapConfig multiMapConfigWithSet = new MultiMapConfig().setName("testContains.set").setValueCollectionType("SET").setBinary(false);
        MultiMapConfig multiMapConfigWithList = new MultiMapConfig().setName("testContains.list").setValueCollectionType("LIST").setBinary(false);
        Config config = HazelcastTestSupport.smallInstanceConfig().addMultiMapConfig(multiMapConfigWithSet).addMultiMapConfig(multiMapConfigWithList);
        HazelcastInstance instance = createHazelcastInstance(config);
        // MultiMap with ValueCollectionType.SET
        MultiMap<String, ComplexValue> multiMapWithSet = instance.getMultiMap("testContains.set");
        Assert.assertTrue(multiMapWithSet.put("1", new ComplexValue("text", 1)));
        Assert.assertFalse(multiMapWithSet.put("1", new ComplexValue("text", 1)));
        Assert.assertFalse(multiMapWithSet.put("1", new ComplexValue("text", 2)));
        Assert.assertTrue(multiMapWithSet.containsValue(new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithSet.containsValue(new ComplexValue("text", 2)));
        Assert.assertTrue(multiMapWithSet.remove("1", new ComplexValue("text", 3)));
        Assert.assertFalse(multiMapWithSet.remove("1", new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithSet.put("1", new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithSet.containsEntry("1", new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithSet.containsEntry("1", new ComplexValue("text", 2)));
        Assert.assertTrue(multiMapWithSet.remove("1", new ComplexValue("text", 1)));
        // MultiMap with ValueCollectionType.LIST
        MultiMap<String, ComplexValue> multiMapWithList = instance.getMultiMap("testContains.list");
        Assert.assertTrue(multiMapWithList.put("1", new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithList.put("1", new ComplexValue("text", 1)));
        Assert.assertTrue(multiMapWithList.put("1", new ComplexValue("text", 2)));
        Assert.assertEquals(3, multiMapWithList.size());
        Assert.assertTrue(multiMapWithList.remove("1", new ComplexValue("text", 4)));
        Assert.assertEquals(2, multiMapWithList.size());
    }

    @Test
    public void testMultiMapKeySet() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapKeySet");
        multiMap.put("Hello", "World");
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        Set<String> keys = multiMap.keySet();
        Assert.assertEquals(1, keys.size());
    }

    @Test
    public void testMultiMapValues() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapValues");
        multiMap.put("Hello", "World");
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        Collection<String> values = multiMap.values();
        Assert.assertEquals(7, values.size());
    }

    @Test
    public void testMultiMapRemove() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapRemove");
        multiMap.put("Hello", "World");
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        Assert.assertEquals(7, multiMap.size());
        Assert.assertEquals(1, multiMap.keySet().size());
        Collection<String> values = multiMap.remove("Hello");
        Assert.assertEquals(7, values.size());
        Assert.assertEquals(0, multiMap.size());
        Assert.assertEquals(0, multiMap.keySet().size());
        multiMap.put("Hello", "World");
        Assert.assertEquals(1, multiMap.size());
        Assert.assertEquals(1, multiMap.keySet().size());
    }

    @Test
    public void testMultiMapRemoveEntries() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapRemoveEntries");
        multiMap.put("Hello", "World");
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        boolean removed = multiMap.remove("Hello", "World");
        Assert.assertTrue(removed);
        Assert.assertEquals(6, multiMap.size());
    }

    @Test
    public void testMultiMapEntrySet() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<String, String> multiMap = instance.getMultiMap("testMultiMapEntrySet");
        multiMap.put("Hello", "World");
        multiMap.put("Hello", "Europe");
        multiMap.put("Hello", "America");
        multiMap.put("Hello", "Asia");
        multiMap.put("Hello", "Africa");
        multiMap.put("Hello", "Antarctica");
        multiMap.put("Hello", "Australia");
        Set<Map.Entry<String, String>> entries = multiMap.entrySet();
        Assert.assertEquals(7, entries.size());
        int itCount = 0;
        for (Map.Entry<String, String> entry : entries) {
            Assert.assertEquals("Hello", entry.getKey());
            itCount++;
        }
        Assert.assertEquals(7, itCount);
    }

    @Test
    public void testMultiMapValueCount() {
        HazelcastInstance instance = createHazelcastInstance();
        MultiMap<Integer, String> multiMap = instance.getMultiMap("testMultiMapValueCount");
        multiMap.put(1, "World");
        multiMap.put(2, "Africa");
        multiMap.put(1, "America");
        multiMap.put(2, "Antarctica");
        multiMap.put(1, "Asia");
        multiMap.put(1, "Europe");
        multiMap.put(2, "Australia");
        Assert.assertEquals(4, multiMap.valueCount(1));
        Assert.assertEquals(3, multiMap.valueCount(2));
    }

    /**
     * idGen is not set while replicating
     */
    @Test
    public void testIssue5220() {
        String name = HazelcastTestSupport.randomString();
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance1 = factory.newHazelcastInstance();
        MultiMap<Object, Object> multiMap1 = instance1.getMultiMap(name);
        // populate multimap while instance1 is owner
        // records will have ids from 0 to 10
        for (int i = 0; i < 10; i++) {
            multiMap1.put("ping-address", ("instance1-" + i));
        }
        HazelcastInstance instance2 = factory.newHazelcastInstance();
        MultiMap<Object, Object> multiMap2 = instance2.getMultiMap(name);
        // now the second instance is the owner
        // if idGen is not set while replicating
        // these entries will have ids from 0 to 10 too
        for (int i = 0; i < 10; i++) {
            multiMap2.put("ping-address", ("instance2-" + i));
        }
        HazelcastInstance instance3 = factory.newHazelcastInstance();
        MultiMap<Object, Object> multiMap3 = instance3.getMultiMap(name);
        // since remove iterates all items and check equals it will remove correct item from owner-side
        // but for the backup we just sent the recordId. if idGen is not set while replicating
        // we may end up removing instance1's items
        for (int i = 0; i < 10; i++) {
            multiMap2.remove("ping-address", ("instance2-" + i));
        }
        instance2.getLifecycleService().terminate();
        for (int i = 0; i < 10; i++) {
            multiMap1.remove("ping-address", ("instance1-" + i));
        }
        instance1.shutdown();
        Assert.assertEquals(0, multiMap3.size());
    }

    /**
     * ConcurrentModificationExceptions
     */
    @Test
    public void testIssue1882() {
        String name = "mm";
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance instance = factory.newHazelcastInstance(config);
        factory.newHazelcastInstance(config);
        final String key = HazelcastTestSupport.generateKeyOwnedBy(instance);
        final MultiMap<Object, Object> multiMap = instance.getMultiMap(name);
        multiMap.put(key, 1);
        multiMap.put(key, 2);
        final AtomicBoolean isRunning = new AtomicBoolean(true);
        Thread thread = new Thread() {
            @Override
            public void run() {
                int count = 3;
                while (isRunning.get()) {
                    multiMap.put(key, (count++));
                } 
            }
        };
        thread.start();
        for (int i = 0; i < 10; i++) {
            multiMap.get(key);
        }
        isRunning.set(false);
        HazelcastTestSupport.assertJoinable(thread);
    }

    @Test
    public void testPutGetRemoveWhileCollectionTypeSet() {
        String name = "defMM";
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(SET);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = factory.newInstances(config);
        Assert.assertTrue(getMultiMap(instances, name).put("key1", "key1_value1"));
        Assert.assertTrue(getMultiMap(instances, name).put("key1", "key1_value2"));
        Assert.assertTrue(getMultiMap(instances, name).put("key2", "key2_value1"));
        Assert.assertFalse(getMultiMap(instances, name).put("key2", "key2_value1"));
        Assert.assertEquals(getMultiMap(instances, name).valueCount("key1"), 2);
        Assert.assertEquals(getMultiMap(instances, name).valueCount("key2"), 1);
        Assert.assertEquals(getMultiMap(instances, name).size(), 3);
        Collection collection = getMultiMap(instances, name).get("key2");
        Assert.assertEquals(collection.size(), 1);
        Iterator iterator = collection.iterator();
        Object value = iterator.next();
        Assert.assertEquals(value, "key2_value1");
        Assert.assertTrue(getMultiMap(instances, name).remove("key1", "key1_value1"));
        Assert.assertFalse(getMultiMap(instances, name).remove("key1", "key1_value1"));
        Assert.assertTrue(getMultiMap(instances, name).remove("key1", "key1_value2"));
        collection = getMultiMap(instances, name).get("key1");
        Assert.assertEquals(collection.size(), 0);
        collection = getMultiMap(instances, name).remove("key2");
        Assert.assertEquals(collection.size(), 1);
        iterator = collection.iterator();
        value = iterator.next();
        Assert.assertEquals(value, "key2_value1");
    }

    @Test
    public void testContainsKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        Assert.assertFalse(multiMap.containsKey("test"));
        multiMap.put("test", "test");
        Assert.assertTrue(multiMap.containsKey("test"));
        multiMap.remove("test");
        Assert.assertFalse(multiMap.containsKey("test"));
    }

    @Test(expected = NullPointerException.class)
    public void testGet_whenNullKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.get(null);
    }

    @Test(expected = NullPointerException.class)
    public void testPut_whenNullKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.put(null, "someVal");
    }

    @Test(expected = NullPointerException.class)
    public void testPut_whenNullValue() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.put("someVal", null);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsKey_whenNullKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.containsKey(null);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsValue_whenNullKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.containsValue(null);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsEntry_whenNullKey() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.containsEntry(null, "someVal");
    }

    @Test(expected = NullPointerException.class)
    public void testContainsEntry_whenNullValue() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.containsEntry("someVal", null);
    }

    @Test
    public void testPutGetRemoveWhileCollectionTypeList() {
        String name = "defMM";
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = factory.newInstances(config);
        Assert.assertTrue(getMultiMap(instances, name).put("key1", "key1_value1"));
        Assert.assertTrue(getMultiMap(instances, name).put("key1", "key1_value2"));
        Assert.assertTrue(getMultiMap(instances, name).put("key2", "key2_value1"));
        Assert.assertTrue(getMultiMap(instances, name).put("key2", "key2_value1"));
        Assert.assertEquals(getMultiMap(instances, name).valueCount("key1"), 2);
        Assert.assertEquals(getMultiMap(instances, name).valueCount("key2"), 2);
        Assert.assertEquals(getMultiMap(instances, name).size(), 4);
        Collection<Object> collection = getMultiMap(instances, name).get("key1");
        Assert.assertEquals(collection.size(), 2);
        Iterator iterator = collection.iterator();
        Assert.assertEquals(iterator.next(), "key1_value1");
        Assert.assertEquals(iterator.next(), "key1_value2");
        Assert.assertTrue(getMultiMap(instances, name).remove("key1", "key1_value1"));
        Assert.assertFalse(getMultiMap(instances, name).remove("key1", "key1_value1"));
        Assert.assertTrue(getMultiMap(instances, name).remove("key1", "key1_value2"));
        collection = getMultiMap(instances, name).get("key1");
        Assert.assertEquals(collection.size(), 0);
        collection = getMultiMap(instances, name).remove("key2");
        Assert.assertEquals(collection.size(), 2);
        iterator = collection.iterator();
        Assert.assertEquals(iterator.next(), "key2_value1");
        Assert.assertEquals(iterator.next(), "key2_value1");
    }

    /**
     * test localKeySet, keySet, entrySet, values, contains, containsKey and containsValue methods
     */
    @Test
    public void testCollectionInterfaceMethods() {
        String name = "defMM";
        Config config = new Config();
        config.getMultiMapConfig(name).setValueCollectionType(LIST);
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(4);
        HazelcastInstance[] instances = factory.newInstances(config);
        getMultiMap(instances, name).put("key1", "key1_val1");
        getMultiMap(instances, name).put("key1", "key1_val2");
        getMultiMap(instances, name).put("key1", "key1_val3");
        getMultiMap(instances, name).put("key2", "key2_val1");
        getMultiMap(instances, name).put("key2", "key2_val2");
        getMultiMap(instances, name).put("key3", "key3_val1");
        getMultiMap(instances, name).put("key3", "key3_val2");
        getMultiMap(instances, name).put("key3", "key3_val3");
        getMultiMap(instances, name).put("key3", "key3_val4");
        Assert.assertTrue(getMultiMap(instances, name).containsKey("key3"));
        Assert.assertTrue(getMultiMap(instances, name).containsValue("key3_val4"));
        Set<Object> localKeySet = instances[0].getMultiMap(name).localKeySet();
        Set<Object> totalKeySet = new HashSet<Object>(localKeySet);
        localKeySet = instances[1].getMultiMap(name).localKeySet();
        totalKeySet.addAll(localKeySet);
        localKeySet = instances[2].getMultiMap(name).localKeySet();
        totalKeySet.addAll(localKeySet);
        localKeySet = instances[3].getMultiMap(name).localKeySet();
        totalKeySet.addAll(localKeySet);
        Assert.assertEquals(3, totalKeySet.size());
        Set keySet = getMultiMap(instances, name).keySet();
        Assert.assertEquals(keySet.size(), 3);
        for (Object key : keySet) {
            HazelcastTestSupport.assertContains(totalKeySet, key);
        }
        Set<Map.Entry<Object, Object>> entrySet = getMultiMap(instances, name).entrySet();
        Assert.assertEquals(entrySet.size(), 9);
        for (Map.Entry entry : entrySet) {
            String key = ((String) (entry.getKey()));
            String val = ((String) (entry.getValue()));
            Assert.assertTrue(val.startsWith(key));
        }
        Collection values = getMultiMap(instances, name).values();
        Assert.assertEquals(values.size(), 9);
        Assert.assertTrue(getMultiMap(instances, name).containsKey("key2"));
        Assert.assertFalse(getMultiMap(instances, name).containsKey("key4"));
        Assert.assertTrue(getMultiMap(instances, name).containsEntry("key3", "key3_val3"));
        Assert.assertFalse(getMultiMap(instances, name).containsEntry("key3", "key3_val7"));
        Assert.assertFalse(getMultiMap(instances, name).containsEntry("key2", "key3_val3"));
        Assert.assertTrue(getMultiMap(instances, name).containsValue("key2_val2"));
        Assert.assertFalse(getMultiMap(instances, name).containsValue("key2_val4"));
    }

    // it must throw ClassCastException wrapped by HazelcastException
    @Test(expected = HazelcastException.class)
    @SuppressWarnings("deprecation")
    public void testAggregateMultiMap_differentDataTypes() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        multiMap.put(1, "fail");
        multiMap.put(2, 75);
        Integer aggregate = multiMap.aggregate(Supplier.all(), Aggregations.integerAvg());
        Assert.assertEquals(50, aggregate.intValue());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testAggregateMultiMap() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        MultiMap<Object, Object> multiMap = getMultiMap(factory.newInstances(), HazelcastTestSupport.randomString());
        Integer aggregate = multiMap.aggregate(Supplier.all(), Aggregations.integerAvg());
        Assert.assertEquals(0, aggregate.intValue());
        multiMap.put(1, 25);
        multiMap.put(2, 75);
        aggregate = multiMap.aggregate(Supplier.all(), Aggregations.integerAvg());
        Assert.assertEquals(50, aggregate.intValue());
    }

    @SuppressWarnings("unused")
    private static class CustomSerializable implements Serializable {
        private long dummy1 = Clock.currentTimeMillis();

        private String dummy2 = String.valueOf(dummy1);
    }
}

