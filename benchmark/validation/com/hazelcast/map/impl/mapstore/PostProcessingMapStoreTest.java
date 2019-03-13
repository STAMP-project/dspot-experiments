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
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapStoreConfig;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapStore;
import com.hazelcast.core.PostProcessingMapStore;
import com.hazelcast.map.EntryBackupProcessor;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PostProcessingMapStoreTest extends HazelcastTestSupport {
    private TestHazelcastInstanceFactory factory;

    @Test
    public void testProcessedValueCarriedToTheBackup() {
        String name = HazelcastTestSupport.randomString();
        Config config = new Config();
        MapConfig mapConfig = config.getMapConfig(name);
        mapConfig.setReadBackupData(true);
        MapStoreConfig mapStoreConfig = new MapStoreConfig();
        mapStoreConfig.setEnabled(true).setClassName(PostProcessingMapStoreTest.IncrementerPostProcessingMapStore.class.getName());
        mapConfig.setMapStoreConfig(mapStoreConfig);
        HazelcastInstance instance1 = factory.newHazelcastInstance(config);
        HazelcastInstance instance2 = factory.newHazelcastInstance(config);
        IMap<Integer, PostProcessingMapStoreTest.SampleObject> map1 = instance1.getMap(name);
        IMap<Integer, PostProcessingMapStoreTest.SampleObject> map2 = instance2.getMap(name);
        for (int i = 0; i < 100; i++) {
            map1.put(i, new PostProcessingMapStoreTest.SampleObject(i));
        }
        for (int i = 0; i < 100; i++) {
            PostProcessingMapStoreTest.SampleObject o = map1.get(i);
            Assert.assertEquals((i + 1), o.version);
        }
        for (int i = 0; i < 100; i++) {
            PostProcessingMapStoreTest.SampleObject o = map2.get(i);
            Assert.assertEquals((i + 1), o.version);
        }
    }

    @Test
    public void testEntryListenerIncludesTheProcessedValue_onPut() {
        IMap<Integer, PostProcessingMapStoreTest.SampleObject> map = createInstanceAndGetMap();
        int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        map.addEntryListener(new com.hazelcast.map.listener.EntryAddedListener<Integer, PostProcessingMapStoreTest.SampleObject>() {
            @Override
            public void entryAdded(EntryEvent<Integer, PostProcessingMapStoreTest.SampleObject> event) {
                Assert.assertEquals(((event.getKey()) + 1), event.getValue().version);
                latch.countDown();
            }
        }, true);
        for (int i = 0; i < count; i++) {
            map.put(i, new PostProcessingMapStoreTest.SampleObject(i));
        }
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void testEntryListenerIncludesTheProcessedValue_onPutAll() {
        IMap<Integer, PostProcessingMapStoreTest.SampleObject> map = createInstanceAndGetMap();
        int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        map.addEntryListener(new com.hazelcast.map.listener.EntryAddedListener<Integer, PostProcessingMapStoreTest.SampleObject>() {
            @Override
            public void entryAdded(EntryEvent<Integer, PostProcessingMapStoreTest.SampleObject> event) {
                Assert.assertEquals(((event.getKey()) + 1), event.getValue().version);
                latch.countDown();
            }
        }, true);
        Map<Integer, PostProcessingMapStoreTest.SampleObject> localMap = new HashMap<Integer, PostProcessingMapStoreTest.SampleObject>();
        for (int i = 0; i < count; i++) {
            localMap.put(i, new PostProcessingMapStoreTest.SampleObject(i));
        }
        map.putAll(localMap);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void testEntryListenerIncludesTheProcessedValue_onEntryProcessor() {
        IMap<Integer, PostProcessingMapStoreTest.SampleObject> map = createInstanceAndGetMap();
        int count = 10;
        final CountDownLatch latch = new CountDownLatch(count);
        map.addEntryListener(new com.hazelcast.map.listener.EntryUpdatedListener<Integer, PostProcessingMapStoreTest.SampleObject>() {
            @Override
            public void entryUpdated(EntryEvent<Integer, PostProcessingMapStoreTest.SampleObject> event) {
                // value is incremented three times :
                // +1 -> post processing map store
                // +1 -> entry processor
                // +1 -> post processing map store
                Assert.assertEquals(((event.getKey()) + 3), event.getValue().version);
                latch.countDown();
            }
        }, true);
        for (int i = 0; i < count; i++) {
            map.put(i, new PostProcessingMapStoreTest.SampleObject(i));
            map.executeOnKey(i, new com.hazelcast.map.EntryProcessor<Integer, PostProcessingMapStoreTest.SampleObject>() {
                @Override
                public Object process(Map.Entry<Integer, PostProcessingMapStoreTest.SampleObject> entry) {
                    PostProcessingMapStoreTest.SampleObject value = entry.getValue();
                    (value.version)++;
                    entry.setValue(value);
                    return null;
                }

                @Override
                public EntryBackupProcessor<Integer, PostProcessingMapStoreTest.SampleObject> getBackupProcessor() {
                    return null;
                }
            });
        }
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    public static class IncrementerPostProcessingMapStore implements MapStore<Integer, PostProcessingMapStoreTest.SampleObject> , PostProcessingMapStore {
        Map<Integer, PostProcessingMapStoreTest.SampleObject> map = new ConcurrentHashMap<Integer, PostProcessingMapStoreTest.SampleObject>();

        @Override
        public void store(Integer key, PostProcessingMapStoreTest.SampleObject value) {
            (value.version)++;
            map.put(key, value);
        }

        @Override
        public void storeAll(Map<Integer, PostProcessingMapStoreTest.SampleObject> map) {
            for (Map.Entry<Integer, PostProcessingMapStoreTest.SampleObject> entry : map.entrySet()) {
                store(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void delete(Integer key) {
            map.remove(key);
        }

        @Override
        public void deleteAll(Collection<Integer> keys) {
            for (Integer key : keys) {
                map.remove(key);
            }
        }

        @Override
        public PostProcessingMapStoreTest.SampleObject load(Integer key) {
            return map.get(key);
        }

        @Override
        public Map<Integer, PostProcessingMapStoreTest.SampleObject> loadAll(Collection<Integer> keys) {
            HashMap<Integer, PostProcessingMapStoreTest.SampleObject> temp = new HashMap<Integer, PostProcessingMapStoreTest.SampleObject>();
            for (Integer key : keys) {
                temp.put(key, map.get(key));
            }
            return temp;
        }

        @Override
        public Set<Integer> loadAllKeys() {
            return map.keySet();
        }
    }

    public static class SampleObject implements Serializable {
        public int version;

        SampleObject(int version) {
            this.version = version;
        }
    }
}

