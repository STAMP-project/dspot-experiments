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
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapStoreEvictionTest extends HazelcastTestSupport {
    private static final int MAP_STORE_ENTRY_COUNT = 1000;

    private static final int NODE_COUNT = 2;

    private static final int MAX_SIZE_PER_NODE = (MapStoreEvictionTest.MAP_STORE_ENTRY_COUNT) / 4;

    private static final int MAX_SIZE_PER_CLUSTER = (MapStoreEvictionTest.MAX_SIZE_PER_NODE) * (MapStoreEvictionTest.NODE_COUNT);

    private CountingMapLoader loader;

    private TestHazelcastInstanceFactory nodeFactory;

    @Test(timeout = 2 * (TimeConstants.MINUTE))
    public void testLoadsAll_whenEvictionDisabled() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config cfg = newConfig(mapName, false, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        HazelcastTestSupport.assertSizeEventually(MapStoreEvictionTest.MAP_STORE_ENTRY_COUNT, map);
        Assert.assertEquals(MapStoreEvictionTest.MAP_STORE_ENTRY_COUNT, loader.getLoadedValueCount());
        assertLoaderIsClosedEventually();
    }

    @Test(timeout = 2 * (TimeConstants.MINUTE))
    public void testLoadsLessThanMaxSize_whenEvictionEnabled() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config cfg = newConfig(mapName, true, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        Assert.assertFalse(map.isEmpty());
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (map.size())));
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (loader.getLoadedValueCount())));
    }

    @Test(timeout = 2 * (TimeConstants.MINUTE))
    public void testLoadsLessThanMaxSize_AfterContainsKey_whenEvictionEnabled() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config cfg = newConfig(mapName, true, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        for (int i = 0; i < (MapStoreEvictionTest.MAP_STORE_ENTRY_COUNT); i++) {
            map.containsKey(i);
        }
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (map.size())));
    }

    @Test(timeout = 2 * (TimeConstants.MINUTE))
    public void testLoadsLessThanMaxSize_AfterGet_whenEvictionEnabled() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config cfg = newConfig(mapName, true, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        for (int i = 0; i < (MapStoreEvictionTest.MAP_STORE_ENTRY_COUNT); i++) {
            map.get(i);
        }
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (map.size())));
    }

    @Test(timeout = 2 * (TimeConstants.MINUTE))
    public void testLoadsLessThanMaxSize_whenEvictionEnabledAndReloaded() throws Exception {
        final String mapName = HazelcastTestSupport.randomMapName();
        Config cfg = newConfig(mapName, true, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        map.evictAll();
        loader.reset();
        map.loadAll(true);
        Assert.assertFalse("Map is not empty", map.isEmpty());
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (map.size())));
        Assert.assertTrue(((MapStoreEvictionTest.MAX_SIZE_PER_CLUSTER) >= (loader.getLoadedValueCount())));
        assertLoaderIsClosedEventually();
    }
}

