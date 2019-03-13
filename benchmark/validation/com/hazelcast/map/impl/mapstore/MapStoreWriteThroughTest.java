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


import EvictionPolicy.LRU;
import GroupProperty.PARTITION_COUNT;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.EntryAdapter;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.SampleTestObjects;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapStoreWriteThroughTest extends AbstractMapStoreTest {
    @Test(timeout = 120000)
    public void testOneMemberWriteThroughWithIndex() throws Exception {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        testMapStore.insert("1", "value1");
        IMap<String, String> map = instance.getMap("default");
        Assert.assertEquals(0, map.size());
        Assert.assertTrue(map.tryLock("1", 1, TimeUnit.SECONDS));
        Assert.assertEquals("value1", map.get("1"));
        map.unlock("1");
        Assert.assertEquals("value1", map.put("1", "value2"));
        Assert.assertEquals("value2", map.get("1"));
        Assert.assertEquals("value2", testMapStore.getStore().get("1"));
        Assert.assertEquals(1, map.size());
        Assert.assertTrue(map.evict("1"));
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(1, testMapStore.getStore().size());
        Assert.assertEquals("value2", map.get("1"));
        Assert.assertEquals(1, map.size());
        map.remove("1");
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(0, testMapStore.getStore().size());
        testMapStore.assertAwait(1);
        Assert.assertEquals(1, testMapStore.getInitCount());
        Assert.assertEquals("default", testMapStore.getMapName());
        Assert.assertEquals(HazelcastTestSupport.getNode(instance), HazelcastTestSupport.getNode(testMapStore.getHazelcastInstance()));
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteThroughWithLRU() {
        final int size = 10000;
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore((size * 2), 1, 1);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 0);
        config.setProperty(PARTITION_COUNT.getName(), "1");
        MaxSizeConfig maxSizeConfig = new MaxSizeConfig();
        maxSizeConfig.setSize(size);
        MapConfig mapConfig = config.getMapConfig("default");
        mapConfig.setEvictionPolicy(LRU);
        mapConfig.setMaxSizeConfig(maxSizeConfig);
        mapConfig.setMinEvictionCheckMillis(0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<Integer, SampleTestObjects.Employee> map = instance.getMap("default");
        final CountDownLatch countDownLatch = new CountDownLatch(size);
        map.addEntryListener(new EntryAdapter() {
            @Override
            public void entryEvicted(EntryEvent event) {
                countDownLatch.countDown();
            }
        }, false);
        for (int i = 0; i < (size * 2); i++) {
            // trigger eviction
            if ((i == ((size * 2) - 1)) || (i == size)) {
                HazelcastTestSupport.sleepMillis(1001);
            }
            map.put(i, new SampleTestObjects.Employee("joe", i, true, 100.0));
        }
        Assert.assertEquals(testMapStore.getStore().size(), (size * 2));
        HazelcastTestSupport.assertOpenEventually(countDownLatch);
        final String msgFailure = String.format("map size: %d put count: %d", map.size(), size);
        Assert.assertTrue(msgFailure, ((map.size()) > (size / 2)));
        Assert.assertTrue(msgFailure, ((map.size()) <= size));
        Assert.assertEquals(testMapStore.getStore().size(), (size * 2));
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteThrough() throws Exception {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 0);
        HazelcastInstance instance = createHazelcastInstance(config);
        SampleTestObjects.Employee employee = new SampleTestObjects.Employee("joe", 25, true, 100.0);
        SampleTestObjects.Employee newEmployee = new SampleTestObjects.Employee("ali", 26, true, 1000);
        testMapStore.insert("1", employee);
        testMapStore.insert("2", employee);
        testMapStore.insert("3", employee);
        testMapStore.insert("4", employee);
        testMapStore.insert("5", employee);
        testMapStore.insert("6", employee);
        testMapStore.insert("7", employee);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("default");
        map.addIndex("name", false);
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(employee, map.get("1"));
        Assert.assertEquals(employee, testMapStore.getStore().get("1"));
        Assert.assertEquals(1, map.size());
        Assert.assertEquals(employee, map.put("2", newEmployee));
        Assert.assertEquals(newEmployee, testMapStore.getStore().get("2"));
        Assert.assertEquals(2, map.size());
        map.remove("1");
        map.put("1", employee, 1, TimeUnit.SECONDS);
        map.put("1", employee);
        Thread.sleep(2000);
        Assert.assertEquals(employee, testMapStore.getStore().get("1"));
        Assert.assertEquals(employee, map.get("1"));
        map.evict("2");
        Assert.assertEquals(newEmployee, map.get("2"));
        Assert.assertEquals(employee, map.get("3"));
        Assert.assertEquals(employee, map.put("3", newEmployee));
        Assert.assertEquals(newEmployee, map.get("3"));
        Assert.assertEquals(employee, map.remove("4"));
        Assert.assertEquals(employee, map.get("5"));
        Assert.assertEquals(employee, map.remove("5"));
        Assert.assertEquals(employee, map.putIfAbsent("6", newEmployee));
        Assert.assertEquals(employee, map.get("6"));
        Assert.assertEquals(employee, testMapStore.getStore().get("6"));
        Assert.assertTrue(map.containsKey("7"));
        Assert.assertEquals(employee, map.get("7"));
        Assert.assertNull(map.get("8"));
        Assert.assertFalse(map.containsKey("8"));
        Assert.assertNull(map.putIfAbsent("8", employee));
        Assert.assertEquals(employee, map.get("8"));
        Assert.assertEquals(employee, testMapStore.getStore().get("8"));
    }

    @Test(timeout = 120000)
    public void testTwoMemberWriteThrough() throws Exception {
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(1, 1, 1);
        testMapStore.setLoadAllKeys(false);
        Config config = newConfig(testMapStore, 0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        nodeFactory.newHazelcastInstance(config);
        SampleTestObjects.Employee employee = new SampleTestObjects.Employee("joe", 25, true, 100.0);
        SampleTestObjects.Employee employee2 = new SampleTestObjects.Employee("jay", 35, false, 100.0);
        testMapStore.insert("1", employee);
        IMap<String, SampleTestObjects.Employee> map = instance.getMap("default");
        map.addIndex("name", false);
        Assert.assertEquals(0, map.size());
        Assert.assertEquals(employee, map.get("1"));
        Assert.assertEquals(employee, testMapStore.getStore().get("1"));
        Assert.assertEquals(1, map.size());
        map.put("2", employee2);
        Assert.assertEquals(employee2, testMapStore.getStore().get("2"));
        Assert.assertEquals(2, testMapStore.getStore().size());
        Assert.assertEquals(2, map.size());
        map.remove("2");
        Assert.assertEquals(1, testMapStore.getStore().size());
        Assert.assertEquals(1, map.size());
        testMapStore.assertAwait(10);
        Assert.assertEquals(5, testMapStore.callCount.get());
    }

    @Test(timeout = 300000)
    public void testTwoMemberWriteThrough2() throws Exception {
        int items = 1000;
        MapStoreTest.TestMapStore testMapStore = new MapStoreTest.TestMapStore(items, 0, 0);
        Config config = newConfig(testMapStore, 0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance h1 = nodeFactory.newHazelcastInstance(config);
        HazelcastInstance h2 = nodeFactory.newHazelcastInstance(config);
        IMap<Integer, String> map1 = h1.getMap("default");
        IMap<Integer, String> map2 = h2.getMap("default");
        for (int i = 0; i < items; i++) {
            map1.put(i, ("value" + i));
        }
        Assert.assertTrue("store operations could not be done wisely ", testMapStore.latchStore.await(30, TimeUnit.SECONDS));
        Assert.assertEquals(items, testMapStore.getStore().size());
        Assert.assertEquals(items, map1.size());
        Assert.assertEquals(items, map2.size());
        testMapStore.assertAwait(10);
        // N put-load N put-store call and 1 loadAllKeys
        Assert.assertEquals(((items * 2) + 1), testMapStore.callCount.get());
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteThroughFailingStore() {
        MapStoreWriteBehindTest.FailAwareMapStore testMapStore = new MapStoreWriteBehindTest.FailAwareMapStore();
        testMapStore.setFail(true, true);
        Config config = newConfig(testMapStore, 0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map = instance.getMap("default");
        Assert.assertEquals(0, map.size());
        try {
            map.get("1");
            Assert.fail("should have thrown exception");
        } catch (Exception e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(1, testMapStore.loads.get());
        try {
            map.get("1");
            Assert.fail("should have thrown exception");
        } catch (Exception e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(2, testMapStore.loads.get());
        try {
            map.put("1", "value");
            Assert.fail("should have thrown exception");
        } catch (Exception e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(0, testMapStore.stores.get());
        Assert.assertEquals(0, map.size());
    }

    @Test(timeout = 120000)
    public void testOneMemberWriteThroughFailingStore2() {
        MapStoreWriteBehindTest.FailAwareMapStore testMapStore = new MapStoreWriteBehindTest.FailAwareMapStore();
        testMapStore.setFail(true, false);
        Config config = newConfig(testMapStore, 0);
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        HazelcastInstance instance = nodeFactory.newHazelcastInstance(config);
        IMap<String, String> map = instance.getMap("default");
        Assert.assertEquals(0, map.size());
        try {
            map.put("1", "value");
            Assert.fail("should have thrown exception");
        } catch (Exception e) {
            HazelcastTestSupport.ignore(e);
        }
        Assert.assertEquals(0, map.size());
    }
}

