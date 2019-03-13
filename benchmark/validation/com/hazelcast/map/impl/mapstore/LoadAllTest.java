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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LoadAllTest extends AbstractMapStoreTest {
    @Test(expected = NullPointerException.class)
    public void load_givenKeys_null() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Object, Object> map = node.getMap(mapName);
        map.loadAll(null, true);
    }

    @Test
    public void load_givenKeys_withEmptySet() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Object, Object> map = node.getMap(mapName);
        map.loadAll(Collections.emptySet(), true);
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void load_givenKeys() {
        // SETUP
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Integer, Integer> map = node.getMap(mapName);
        LoadAllTest.populateMap(map, 1000);
        // GIVEN
        map.evictAll();
        Assert.assertEquals(0, map.size());
        // WHEN
        final Set<Integer> keysToLoad = LoadAllTest.selectKeysToLoad(100, 910);
        map.loadAll(keysToLoad, true);
        // THEN
        Assert.assertEquals(810, map.size());
        LoadAllTest.assertRangeLoaded(map, 100, 910);
    }

    @Test
    public void load_allKeys() {
        // SETUP
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Integer, Integer> map = node.getMap(mapName);
        final int itemCount = 1000;
        LoadAllTest.populateMap(map, itemCount);
        // GIVEN
        map.evictAll();
        Assert.assertEquals(0, map.size());
        // WHEN
        map.loadAll(true);
        // THEN
        Assert.assertEquals(itemCount, map.size());
    }

    @Test
    public void testAllItemsLoaded_whenLoadingAllOnMultipleInstances() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNewConfig(mapName);
        HazelcastInstance[] nodes = createHazelcastInstanceFactory(3).newInstances(config);
        HazelcastInstance node = nodes[0];
        IMap<Integer, Integer> map = node.getMap(mapName);
        int itemCount = 1000;
        LoadAllTest.populateMap(map, itemCount);
        map.evictAll();
        map.loadAll(true);
        Assert.assertEquals(itemCount, map.size());
    }

    @Test
    public void testItemsNotOverwritten_whenLoadingWithoutReplacing() {
        String mapName = HazelcastTestSupport.randomMapName();
        Config config = createNewConfig(mapName);
        HazelcastInstance[] nodes = createHazelcastInstanceFactory(3).newInstances(config);
        HazelcastInstance node = nodes[0];
        IMap<Integer, Integer> map = node.getMap(mapName);
        int itemCount = 100;
        LoadAllTest.populateMap(map, itemCount);
        map.evictAll();
        map.putTransient(0, (-1), 0, TimeUnit.SECONDS);
        map.loadAll(false);
        Assert.assertEquals(itemCount, map.size());
        Assert.assertEquals((-1), map.get(0).intValue());
    }

    @Test
    public void load_allKeys_preserveExistingKeys_firesEvent() {
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Integer, Integer> map = node.getMap(mapName);
        final int itemCount = 1000;
        LoadAllTest.populateMap(map, itemCount);
        LoadAllTest.evictRange(map, 0, 700);
        final CountDownLatch loadEventCounter = new CountDownLatch(700);
        LoadAllTest.addLoadedListener(map, loadEventCounter);
        map.loadAll(false);
        HazelcastTestSupport.assertOpenEventually(loadEventCounter);
        Assert.assertEquals(itemCount, map.size());
    }

    @Test
    public void load_allKeys_firesEvent() {
        final int itemCount = 1000;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final HazelcastInstance node = createHazelcastInstance(config);
        final IMap<Integer, Integer> map = node.getMap(mapName);
        final CountDownLatch loadedCounter = new CountDownLatch(itemCount);
        LoadAllTest.populateMap(map, itemCount);
        map.evictAll();
        LoadAllTest.addLoadedListener(map, loadedCounter);
        map.loadAll(true);
        HazelcastTestSupport.assertOpenEventually(loadedCounter);
        Assert.assertEquals(itemCount, map.size());
    }

    @Test
    public void load_givenKeys_withBackupNodes() {
        final int itemCount = 10000;
        final int rangeStart = 1000;
        // select an ordinary value
        final int rangeEnd = 9001;
        final String mapName = HazelcastTestSupport.randomMapName();
        final Config config = createNewConfig(mapName);
        final TestHazelcastInstanceFactory instanceFactory = createHazelcastInstanceFactory(2);
        final HazelcastInstance node1 = instanceFactory.newHazelcastInstance(config);
        final HazelcastInstance node2 = instanceFactory.newHazelcastInstance(config);
        final IMap<Integer, Integer> map1 = node1.getMap(mapName);
        final IMap<Integer, Integer> map2 = node2.getMap(mapName);
        LoadAllTest.populateMap(map1, itemCount);
        map1.evictAll();
        final Set<Integer> keysToLoad = LoadAllTest.selectKeysToLoad(rangeStart, rangeEnd);
        map1.loadAll(keysToLoad, true);
        Assert.assertEquals((rangeEnd - rangeStart), map1.size());
        LoadAllTest.assertRangeLoaded(map2, rangeStart, rangeEnd);
    }

    private static class SimpleStore implements MapStore<Integer, Integer> {
        private ConcurrentMap<Integer, Integer> store = new ConcurrentHashMap<Integer, Integer>();

        @Override
        public void store(Integer key, Integer value) {
            store.put(key, value);
        }

        @Override
        public void storeAll(Map<Integer, Integer> map) {
            final Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();
            for (Map.Entry<Integer, Integer> entry : entrySet) {
                store(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void delete(Integer key) {
        }

        @Override
        public void deleteAll(Collection<Integer> keys) {
        }

        @Override
        public Integer load(Integer key) {
            return store.get(key);
        }

        @Override
        public Map<Integer, Integer> loadAll(Collection<Integer> keys) {
            final Map<Integer, Integer> map = new HashMap<Integer, Integer>();
            for (Integer key : keys) {
                map.put(key, load(key));
            }
            return map;
        }

        @Override
        public Set<Integer> loadAllKeys() {
            return store.keySet();
        }
    }
}

