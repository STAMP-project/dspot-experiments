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


import com.google.common.collect.Lists;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.MapLoader;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLoaderUnexpectedLoadedItemsTest extends HazelcastTestSupport {
    private static final boolean LOAD_ALL_KEYS = false;

    private static final boolean LOAD_PROVIDED_KEYS = true;

    private static final Integer[] KEYS_TO_LOAD = new Integer[]{ 0, 1, 2, 3, 4, 5 };

    @Test
    public void loadAllAbortsIfItemFromOtherPartitionIsLoaded() {
        String mapName = HazelcastTestSupport.randomString();
        MapLoaderUnexpectedLoadedItemsTest.DummyMapLoader mapLoader = new MapLoaderUnexpectedLoadedItemsTest.DummyMapLoader(MapLoaderUnexpectedLoadedItemsTest.LOAD_ALL_KEYS);
        Config config = getConfig(mapName, mapLoader);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<?, ?> map = instance.getMap(mapName);
        map.clear();
        map.loadAll(true);
        Assert.assertThat(map.size(), Matchers.lessThan(MapLoaderUnexpectedLoadedItemsTest.KEYS_TO_LOAD.length));
        instance.shutdown();
    }

    @Test
    public void loadAllLoadsAllIfProvidedKeysLoadedFromStore() {
        String mapName = HazelcastTestSupport.randomString();
        MapLoaderUnexpectedLoadedItemsTest.DummyMapLoader mapLoader = new MapLoaderUnexpectedLoadedItemsTest.DummyMapLoader(MapLoaderUnexpectedLoadedItemsTest.LOAD_PROVIDED_KEYS);
        Config config = getConfig(mapName, mapLoader);
        HazelcastInstance instance = createHazelcastInstance(config);
        IMap<?, ?> map = instance.getMap(mapName);
        map.clear();
        map.loadAll(true);
        Assert.assertThat(map.size(), Matchers.equalTo(MapLoaderUnexpectedLoadedItemsTest.KEYS_TO_LOAD.length));
        instance.shutdown();
    }

    public static class DummyMapLoader implements MapLoader<Integer, String> {
        private final boolean useProvidedKeys;

        private DummyMapLoader(boolean useProvidedKeys) {
            this.useProvidedKeys = useProvidedKeys;
        }

        public String load(Integer key) {
            return key.toString();
        }

        public Map<Integer, String> loadAll(Collection<Integer> keys) {
            Map<Integer, String> map = new HashMap<Integer, String>();
            for (Integer key : getKeysToLoad(keys)) {
                map.put(key, load(key));
            }
            return map;
        }

        private Iterable<Integer> getKeysToLoad(Collection<Integer> keys) {
            if (useProvidedKeys) {
                return keys;
            }
            return loadAllKeys();
        }

        public Iterable<Integer> loadAllKeys() {
            return Lists.newArrayList(MapLoaderUnexpectedLoadedItemsTest.KEYS_TO_LOAD);
        }
    }
}

