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


import InitialLoadMode.LAZY;
import com.hazelcast.config.Config;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLoaderMultiNodeTest extends HazelcastTestSupport {
    private static final int MAP_STORE_ENTRY_COUNT = 10000;

    private static final int BATCH_SIZE = 100;

    private static final int NODE_COUNT = 3;

    private final String mapName = getClass().getSimpleName();

    private TestHazelcastInstanceFactory nodeFactory;

    private CountingMapLoader mapLoader;

    @Test
    public void testLoads_whenMapLazyAndCheckingSize() {
        Config cfg = newConfig(mapName, InitialLoadMode.LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testLoadsAll_whenMapCreatedInEager() {
        Config cfg = newConfig(mapName, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testLoadsNothing_whenMapCreatedLazy() {
        Config cfg = newConfig(mapName, LAZY);
        getMap(mapName, cfg);
        Assert.assertEquals(0, mapLoader.getLoadedValueCount());
    }

    @Test
    public void testLoadsMap_whenLazyAndValueRetrieved() {
        Config cfg = newConfig(mapName, LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        Assert.assertEquals(1, map.get(1));
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testLoadsAll_whenLazyModeAndLoadAll() {
        Config cfg = newConfig(mapName, InitialLoadMode.LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        map.loadAll(true);
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testDoesNotLoadAgain_whenLoadedAndNodeAdded() {
        Config cfg = newConfig(mapName, InitialLoadMode.EAGER);
        IMap<Object, Object> map = getMap(mapName, cfg);
        nodeFactory.newHazelcastInstance(cfg);
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testDoesNotLoadAgain_whenLoadedLazyAndNodeAdded() {
        Config cfg = newConfig(mapName, InitialLoadMode.LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        map.loadAll(true);
        nodeFactory.newHazelcastInstance(cfg);
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
        assertSizeAndLoadCount(map);
    }

    @Test
    public void testLoadAgain_whenLoadedAllCalledMultipleTimes() {
        Config cfg = newConfig(mapName, InitialLoadMode.LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        map.loadAll(true);
        map.loadAll(true);
        Assert.assertEquals(2, mapLoader.getLoadAllKeysInvocations());
        HazelcastTestSupport.assertSizeEventually(MapLoaderMultiNodeTest.MAP_STORE_ENTRY_COUNT, map);
        Assert.assertEquals((2 * (MapLoaderMultiNodeTest.MAP_STORE_ENTRY_COUNT)), mapLoader.getLoadedValueCount());
    }

    @Test
    public void testLoadsOnce_whenSizeCheckedTwice() {
        mapLoader = new CountingMapLoader(MapLoaderMultiNodeTest.MAP_STORE_ENTRY_COUNT, true);
        Config cfg = newConfig(mapName, InitialLoadMode.LAZY);
        IMap<Object, Object> map = getMap(mapName, cfg);
        map.size();
        map.size();
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
        assertSizeAndLoadCount(map);
    }
}

