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
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.TimeConstants;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapLoaderFailoverTest extends HazelcastTestSupport {
    private static final int MAP_STORE_ENTRY_COUNT = 10000;

    private static final int BATCH_SIZE = 100;

    private static final int NODE_COUNT = 3;

    private TestHazelcastInstanceFactory nodeFactory;

    private CountingMapLoader mapLoader;

    @Test(timeout = TimeConstants.MINUTE)
    public void testDoesntLoadAgain_whenLoaderNodeGoesDown() {
        Config cfg = newConfig("default", InitialLoadMode.LAZY);
        HazelcastInstance[] nodes = nodeFactory.newInstances(cfg, 3);
        HazelcastInstance hz3 = nodes[2];
        String mapName = HazelcastTestSupport.generateKeyOwnedBy(hz3);
        IMap<Object, Object> map = nodes[0].getMap(mapName);
        map.size();
        assertSizeAndLoadCount(map);
        hz3.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(nodeFactory.getAllHazelcastInstances());
        assertSizeAndLoadCount(map);
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
    }

    @Test(timeout = TimeConstants.MINUTE)
    public void testLoads_whenInitialLoaderNodeRemoved() {
        Config cfg = newConfig("default", InitialLoadMode.LAZY);
        HazelcastInstance[] nodes = nodeFactory.newInstances(cfg, 3);
        HazelcastInstance hz3 = nodes[2];
        String mapName = HazelcastTestSupport.generateKeyOwnedBy(hz3);
        IMap<Object, Object> map = nodes[0].getMap(mapName);
        hz3.getLifecycleService().terminate();
        // trigger loading
        map.size();
        Assert.assertEquals(1, mapLoader.getLoadAllKeysInvocations());
        assertSizeAndLoadCount(map);
    }

    // FIXES https://github.com/hazelcast/hazelcast/issues/6056
    @Test(timeout = TimeConstants.MINUTE)
    public void testLoadsAll_whenInitialLoaderNodeRemovedAfterLoading() {
        Config cfg = newConfig("default", InitialLoadMode.LAZY);
        HazelcastInstance[] nodes = nodeFactory.newInstances(cfg, 3);
        HazelcastInstance hz3 = nodes[2];
        String mapName = HazelcastTestSupport.generateKeyOwnedBy(hz3);
        IMap<Object, Object> map = nodes[0].getMap(mapName);
        map.size();
        assertSizeAndLoadCount(map);
        hz3.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, nodes[0]);
        map.loadAll(true);
        HazelcastTestSupport.assertSizeEventually(MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT, map);
        Assert.assertEquals(2, mapLoader.getLoadAllKeysInvocations());
        Assert.assertEquals((2 * (MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT)), mapLoader.getLoadedValueCount());
    }

    @Test(timeout = TimeConstants.MINUTE)
    public void testLoadsAll_whenInitialLoaderNodeRemovedWhileLoading() throws Exception {
        PausingMapLoader<Integer, Integer> pausingLoader = new PausingMapLoader<Integer, Integer>(mapLoader, 5000);
        Config cfg = newConfig("default", InitialLoadMode.LAZY, 1, pausingLoader);
        HazelcastInstance[] nodes = nodeFactory.newInstances(cfg, 3);
        HazelcastInstance hz3 = nodes[2];
        String mapName = HazelcastTestSupport.generateKeyOwnedBy(hz3);
        IMap<Object, Object> map = nodes[0].getMap(mapName);
        // trigger loading and pause half way through
        Future<Object> asyncVal = map.getAsync(1);
        pausingLoader.awaitPause();
        hz3.getLifecycleService().terminate();
        HazelcastTestSupport.assertClusterSizeEventually(2, nodes[0]);
        pausingLoader.resume();
        // workaround for a known MapLoader issue documented in #12384
        // 
        // in short, there is an edge case in which the get operation is
        // processed before loading the partition holding the given key
        // restarts on the previously replica node, after the owner node
        // died during the load process
        // for the details, see the issue
        // 
        // we do this workaround since the goal of the test is to verify
        // that loadAll() eventually loads all records even if a node
        // dies in the middle of loading
        Object getResult = asyncVal.get();
        if (getResult == null) {
            getResult = map.get(1);
        }
        Assert.assertEquals(1, getResult);
        HazelcastTestSupport.assertSizeEventually(MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT, map);
        Assert.assertTrue(((mapLoader.getLoadedValueCount()) >= (MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT)));
        Assert.assertEquals(2, mapLoader.getLoadAllKeysInvocations());
    }

    // FIXES https://github.com/hazelcast/hazelcast/issues/7959
    @Test(timeout = TimeConstants.MINUTE)
    public void testLoadsAll_whenInitialLoaderNodeRemovedWhileLoadingAndNoBackups() {
        PausingMapLoader<Integer, Integer> pausingLoader = new PausingMapLoader<Integer, Integer>(mapLoader, 5000);
        Config cfg = newConfig("default", InitialLoadMode.LAZY, 0, pausingLoader);
        HazelcastInstance[] nodes = nodeFactory.newInstances(cfg, 3);
        HazelcastInstance hz3 = nodes[2];
        String mapName = HazelcastTestSupport.generateKeyOwnedBy(hz3);
        IMap<Object, Object> map = nodes[0].getMap(mapName);
        // trigger loading and pause half way through
        map.putAsync(1, 2);
        pausingLoader.awaitPause();
        hz3.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(nodeFactory.getAllHazelcastInstances());
        pausingLoader.resume();
        int size = map.size();
        Assert.assertEquals(MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT, size);
        Assert.assertTrue(((mapLoader.getLoadedValueCount()) >= (MapLoaderFailoverTest.MAP_STORE_ENTRY_COUNT)));
        Assert.assertEquals(2, mapLoader.getLoadAllKeysInvocations());
    }
}

