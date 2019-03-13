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
package com.hazelcast.client.map;


import MapStoreConfig.InitialLoadMode.EAGER;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.map.impl.mapstore.AbstractMapStoreTest;
import com.hazelcast.map.impl.mapstore.MapLoaderTest;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.TestCollectionUtils;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMapLoadAllTest extends AbstractMapStoreTest {
    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    @Test
    public void testGetMap_issue_3031() throws Exception {
        final int itemCount = 1000;
        final String mapName = randomMapName();
        final AtomicBoolean breakMe = new AtomicBoolean(false);
        final Config config = createNewConfig(mapName, new ClientMapLoadAllTest.BrokenLoadSimpleStore(breakMe));
        final HazelcastInstance server = hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        try {
            final IMap<Object, Object> map = server.getMap(mapName);
            ClientMapLoadAllTest.populateMap(map, itemCount);
            map.clear();
        } catch (Exception e) {
            Assert.fail();
        }
        breakMe.set(true);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        client.getMap(mapName);
    }

    @Test
    public void testLoadAll_givenKeys() throws Exception {
        final String mapName = randomMapName();
        final Config config = createNewConfig(mapName);
        hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        final IMap<Object, Object> map = client.getMap(mapName);
        ClientMapLoadAllTest.populateMap(map, 1000);
        map.evictAll();
        final Set keysToLoad = ClientMapLoadAllTest.selectKeysToLoad(10, 910);
        map.loadAll(keysToLoad, true);
        Assert.assertEquals(900, map.size());
        ClientMapLoadAllTest.assertRangeLoaded(map, 10, 910);
    }

    @Test
    public void givenSpecificKeysWereReloaded_whenLoadAllIsCalled_thenAllEntriesAreLoadedFromTheStore() {
        String name = randomString();
        int keysInMapStore = 10000;
        Config config = getConfig();
        MapConfig mapConfig = config.getMapConfig(name);
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setEnabled(true);
        mapStoreConfig.setImplementation(new MapLoaderTest.DummyMapLoader(keysInMapStore));
        mapStoreConfig.setInitialLoadMode(EAGER);
        mapConfig.setMapStoreConfig(mapStoreConfig);
        hazelcastFactory.newHazelcastInstance(config);
        HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        IMap<Integer, Integer> map = client.getMap(name);
        // load specific keys
        map.loadAll(TestCollectionUtils.setOfValuesBetween(0, keysInMapStore), true);
        // remove everything
        map.clear();
        // assert loadAll with load all entries provided by the mapLoader
        map.loadAll(true);
        Assert.assertEquals(keysInMapStore, map.size());
    }

    @Test
    public void testLoadAll_allKeys() throws Exception {
        final String mapName = randomMapName();
        final Config config = createNewConfig(mapName);
        hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        hazelcastFactory.newHazelcastInstance(config);
        final HazelcastInstance client = hazelcastFactory.newHazelcastClient(getClientConfig());
        final IMap<Object, Object> map = client.getMap(mapName);
        ClientMapLoadAllTest.populateMap(map, 1000);
        map.evictAll();
        map.loadAll(true);
        Assert.assertEquals(1000, map.size());
    }

    private static class SimpleStore implements MapStore {
        private ConcurrentMap store = new ConcurrentHashMap();

        @Override
        public void store(Object key, Object value) {
            store.put(key, value);
        }

        @Override
        public void storeAll(Map map) {
            final Set<Map.Entry> entrySet = map.entrySet();
            for (Map.Entry entry : entrySet) {
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                store(key, value);
            }
        }

        @Override
        public void delete(Object key) {
        }

        @Override
        public void deleteAll(Collection keys) {
        }

        @Override
        public Object load(Object key) {
            return store.get(key);
        }

        @Override
        public Map loadAll(Collection keys) {
            final Map map = new HashMap();
            for (Object key : keys) {
                final Object value = load(key);
                map.put(key, value);
            }
            return map;
        }

        @Override
        public Set loadAllKeys() {
            return store.keySet();
        }
    }

    private static class BrokenLoadSimpleStore extends ClientMapLoadAllTest.SimpleStore {
        private final AtomicBoolean breakMe;

        private BrokenLoadSimpleStore(AtomicBoolean breakMe) {
            this.breakMe = breakMe;
        }

        @Override
        public Object load(Object key) {
            testClientRequest();
            return null;
        }

        @Override
        public Map loadAll(Collection keys) {
            testClientRequest();
            return null;
        }

        private boolean testClientRequest() {
            if (breakMe.get()) {
                throw new ClassCastException();
            }
            return false;
        }
    }
}

